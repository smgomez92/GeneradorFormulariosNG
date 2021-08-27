/**
 * @remote
 */
PropiedadMapa.addMethods({

    orderMatters: true,

    getInput: function(map) {
        var d = new Element('div');

        this.generateTable(map);

        d.insert(new Element("div", {
            className: "editor-list"
        }).update(Editor.getHtmlControl(map, "_table")));
        d.insert(this.getAddSubItemDiv(map));

        return d;
    },

    generateTable: function(map) {
        var originalTable = Editor.getHtmlControl(map, "_table");
        var table = new Element("table");

        if (Object.isElement(originalTable) && originalTable.parentNode) {
            originalTable.replace(table);
        }

        Editor.setHtmlControl(map, "_table", table);

        map.keys().each(function(key) {
            this.createRow(key, map.get(key));
        }, this);
    },

    createRow: function(key, object) {
        var map = Editor.getOwnerList(object);
        var table = Editor.getHtmlControl(map, "_table");
        var row = table.insertRow(table.rows.length);

        row.insertCell(0).update(key);

        row.insertCell(1).update(object.getLabel && object.getLabel() || "null");
        var edit = row.insertCell(2).insert(this.getEditSubItemDiv(object));

        if (PRIMITIVES.include(this.itemsClass)) {
            edit.style.visibility = "hidden";
        }

        row.insertCell(3).insert(this.getDeleteSubItemDiv(object));
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
     * @param map
     *            Mapa donde se va a crear el subitem.
     * @param itemClass
     *            Interno, indica el tipo de objeto a ser creado
     */
    addSubItem: function(map, itemClass) {
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
                this.addSubItem(map, clase);
            }.bind(this));

            ventana.ver();

            return;
        } else if (!itemClass) {
            itemClass = this.itemsClass;
        }

        var key = prompt("Ingrese el identificador");

        var object;
        if (PRIMITIVES.include(itemClass)) {
            object = wrap(prompt("Ingrese un valor"));
        } else {
            object = new itemClass();
            this.editSubItem(object);
        }

        FormGenerator.initObject(object, Editor.getOwner(map), map);

        map.set(key, object);

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

        var map = Editor.getOwnerList(object);

        var key = null;
        map.each(function(p) {
            if (p.value == object) {
                key = p.key;
            }
        });
        if (key) {
            map.unset(key);
        }

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
    }

});
