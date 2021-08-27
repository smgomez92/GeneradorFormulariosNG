/**
 * @remote
 */
PropiedadLista.addMethods({

    orderMatters: true,

    getListItem: function(list, object, property, label) {
        label = label || this.getLabel();
        var li = new Element("li").update(label);

        label.insert(this.getAddSubItemDiv(list, "inline"));

        if (list && list.length > 0) {
            var ul = new Element("ul");
            li.insert(ul);
            li.addClassName("subitems");
            li.on("click", function(e) {
                if ($E(e).elemento == li) {
                    li.toggleClassName("show");
                    list._visible = li.hasClassName("show");
                }
            });
            if (list._visible) {
                li.addClassName("show");
            }

            list.each(function(object) {
                var li = this.getListSubItem(object);
                li.identify();
                li.object = object;
                ul.insert(li);
            }, this);

            (function() {
                Sortable.create(ul.identify(), {
                    ghosting: false,
                    constraint: true,
                    onUpdate: function(element) {
                        list.clear();
                        ul.childElements().each(function(li) {
                            list.push(li.object);
                        });
                        Editor.refresh(true);
                    }
                });
            }).defer();
        }

        return li;
    },

    getListSubItem: function(object) {
        var label = new Element("label").update(object && object.getLabel
                && object.getLabel() || "null");
        var li = new Element("li").update(label);

        if (!PRIMITIVES.include(this.itemsClass)) {
            label.insert(this.getEditSubItemDiv(object));
        }

        label.insert(this.getDeleteSubItemDiv(object));

        if (!PRIMITIVES.include(this.itemsClass)) {
            var propName = object.constructor.getGeneratorProperties().propertySubItems;
            if (propName) {
                var prop = object.constructor.getProperties().get(propName);
                li = prop.getListItem(object[propName], object, propName, label);
                li.insert({ top: label });

            } else {
                li.addClassName("subitems");
                li.on("click", function(e) {
                    if ($E(e).elemento == li) {
                        li.toggleClassName("show");
                        object._visible = li.hasClassName("show");
                    }
                });
                li.insert(FormGenerator.generateTree(object));
                if (object._visible) {
                    li.addClassName("show");
                }
            }
        }

        return li;
    },

    getInput: function(list) {
        var d = new Element('div');

        this.generateTable(list);

        d.insert(new Element("div", {
            className: "editor-list"
        }).update(Editor.getHtmlControl(list, "_table")));
        d.insert(this.getAddSubItemDiv(list));

        return d;
    },

    generateTable: function(list) {
        var originalTable = Editor.getHtmlControl(list, "_table");
        var table = new Element("table");

        if (Object.isElement(originalTable) && originalTable.parentNode) {
            originalTable.replace(table);
        }

        Editor.setHtmlControl(list, "_table", table);

        list.each(this.createRow.bind(this));
    },

    createRow: function(object) {
        var list = Editor.getOwnerList(object);
        var table = Editor.getHtmlControl(list, "_table");
        var index = list.indexOf(object);
        var listLength = list.length;
        var row = table.insertRow(index);
        var desc = row.insertCell(0);

        var edit = row.insertCell(1).insert(this.getEditSubItemDiv(object));
        var del = row.insertCell(2).insert(this.getDeleteSubItemDiv(object));

        var up = row.insertCell(3).insert(new Element("div", {
            className: "editor-up",
            title: "Subir"
        }));
        up.on("click", this.subItemUp.bind(this, object, list));

        var down = row.insertCell(4).insert(new Element("div", {
            className: "editor-down",
            title: "Bajar"
        }));
        down.on("click", this.subItemDown.bind(this, object, list));

        desc.update(object.getLabel && object.getLabel() || "null");

        if (!this.orderMatters || index == listLength - 1) {
            down.style.visibility = "hidden";
        } else {
            down.style.visibility = "visible";
        }

        if (!this.orderMatters || index == 0) {
            up.style.visibility = "hidden";
        } else {
            up.style.visibility = "visible";
        }

        if (PRIMITIVES.include(this.itemsClass)) {
            edit.style.visibility = "hidden";
        }
    },

    /**
     * Obtiene un div para agregar un subitem
     * 
     * @param object
     *            Objeto al que se agregará el subitem
     * @param extraClassName
     *            Clase que se agregará en caso de ser necesario
     */
    getAddSubItemDiv: function(object, extraClassName) {
        var add = new Element('div', {
            className: "editor-add",
            title: "Agregar"
        });

        if (Object.isString(extraClassName)) {
            add.addClassName(extraClassName);
        }

        add.on("click", this.addSubItem.bind(this, object, null));

        return add;
    },

    /**
     * Crea un subitem dentro de la lista especificada.
     * 
     * @param list
     *            Lista donde se va a crear el subitem.
     * @param itemClass
     *            Interno, indica el tipo de objeto a ser creado
     */
    addSubItem: function(list, itemClass) {
        if (!itemClass && this.itemsSubClasses.length) {
            var sel = new PropiedadObjeto( {
                instanceSubClasses: this.itemsSubClasses
            }).getSelect( {});

            var ventana = new Ventana( {
                titulo: "Escoja un tipo",
                contenido: sel
            });

            sel.on("change", function() {
                var clase = sel.options[sel.selectedIndex].clase;
                ventana.cerrar();
                this.addSubItem(list, clase);
            }.bind(this));

            ventana.ver();

            return;
        } else if (!itemClass) {
            itemClass = this.itemsClass;
        }

        var object;
        if (PRIMITIVES.include(itemClass)) {
            object = wrap(prompt("Ingrese un valor"));
        } else {
            object = new itemClass();
            this.editSubItem(object);
        }

        FormGenerator.initObject(object, Editor.getOwner(list), list);

        list.push(object);

        Editor.refresh(true);
    },

    /**
     * Obtiene un div para agregar un subitem.
     * 
     * @param object
     *            Objeto al que se agregará el subitem
     */
    getDeleteSubItemDiv: function(object) {
        var del = new Element('div', {
            className: "editor-delete inline",
            title: "Eliminar"
        });

        del.on("click", this.deleteSubItem.bind(this, object));

        return del;
    },

    /**
     * Borra un subitem.
     * 
     * @param object
     *            Subitem a ser movido
     */
    deleteSubItem: function(object) {
        if (!confirm("Realmente quiere borrar este item?")) {
            return;
        }

        var list = Editor.getOwnerList(object);
        var ind = list.indexOf(object);

        list.splice(ind, 1);

        FormGenerator.deinitObject(object);

        Editor.refresh(true);
    },

    /**
     * Obtiene un div para agregar editor.
     * 
     * @param object
     *            Objeto al que se agregará el subitem
     */
    getEditSubItemDiv: function(object) {
        var but = new Element("div", {
            className: "editor-edit inline",
            title: "Editar"
        });

        if (object && object.constructor.getProperties().size()) {
            but.on("click", this.editSubItem.bind(this, object));
        } else {
            but.addClassName("disabled");
            but.setOpacity(0.5);
        }

        return but;
    },

    /**
     * Muestra una ventana de edicion de un subitem.
     * 
     * @param object
     *            Objeto a ser editado.
     */
    editSubItem: function(object) {
        if (object && object.constructor.getProperties().size()) {
            FormGenerator.generateWindow(object);
        }
    },

    /**
     * Mover para arriba un subitem.
     * 
     * @param object
     *            Subitem a ser movido
     */
    subItemUp: function(object) {
        var list = Editor.getOwnerList(object);
        var ind = list.indexOf(object);

        list.splice(ind, 1);
        list.splice(ind - 1, 0, object);

        Editor.refresh(true);
    },

    /**
     * Mover para abajo un subitem.
     * 
     * @param object
     *            Subitem a ser movido
     */
    subItemDown: function(object) {
        var list = Editor.getOwnerList(object);
        var ind = list.indexOf(object);

        list.splice(ind, 1);
        list.splice(ind + 1, 0, object);

        Editor.refresh(true);
    }
});
