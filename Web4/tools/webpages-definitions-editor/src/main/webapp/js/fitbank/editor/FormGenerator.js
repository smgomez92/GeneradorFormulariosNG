/**
 * Namespace FormGenerador - Contiene funciones necesarias para generar
 * formularios de edición de objetos.
 */
var FormGenerator = {

    /**
     * Genera una ventana para editar un objeto usando las propiedades definidas
     * en la clase del objeto.
     *
     * @param object
     *            Objeto a ser editado.
     */
    generateWindow: function(object) {
        var id = Math.random();
        var ventana = new Ventana({
            titulo: "Edit " + object.constructor.simpleClassName,
            contenido: new Element("span"),
            onCerrar: function() {
                FormGenerator.saveState(object);
                if(!Object.isUndefined(object.constructor.getGeneratorProperties().events.close)) {
                    object.constructor.getGeneratorProperties().events.close.bind(object)();
                }
                Editor.tables.unset(id);
                Editor.refresh(true);
            }
        });

        var table = new Element("div", {
            className: "editor-property-table-wrap"
        });
        ventana.contenido.insert(table);

        table.refresh = function() {
            table.update(FormGenerator.generateTable(object));
        };
        table.refresh();
        Editor.tables.set(id, table);

        if(Object.isFunction(object.constructor.getGeneratorProperties().events.init)) {
            object.constructor.getGeneratorProperties().events.init.bind(object)();
        }

        ventana.ver();
    },

    /**
     * Genera una tabla con los editores de las propiedades del objeto.
     *
     * @param object
     *            Objeto a ser editado.
     */
    generateTable: function(object) {
        var table = new Element('table', {
            className: "editor-property-table"
        });

        object.constructor.getProperties().keys().each(
                FormGenerator.getPropertyRow.curry(table, object));

        return table;
    },

    /**
     * Guarda el estado de las propiedades tomando desde el formulario.
     *
     * @param object
     *            Objeto a ser editado.
     */
    saveState: function(object) {
        Editor.getHtmlControls(object).each(function (pair) {
            var input = pair.value.input;
            var property = Editor.getObjectProperty(object, pair.key);

            if (!property) {
                object[pair.key] = null;
            } else if(input.tagName == 'INPUT') {
                if (input.type == 'checkbox') {
                    object[pair.key] = property.getValue(input.checked);
                } else if (input.type == 'text') {
                    object[pair.key] = property.getValue(input.value);
                }
            } else if (input.tagName == 'SELECT') {
                object[pair.key] = property.getValue(input.value);
            }
        });

        // updating labels (only for tables for the moment)
        if(Editor.getOwnerList(object)) {
            var owner = Editor.getOwner(object);
            var ownerList = Editor.getOwnerList(object);
            var ownerProperty = Editor.getOwnerProperty(ownerList);
            Editor.getObjectProperty(owner, ownerProperty).generateTable(ownerList);
        }
    },

    /**
     * Genera una fila para una propiedad en específico.
     *
     * @param table
     *            Tabla donde se insertará la fila
     * @param object
     *            Objeto que va a ser editado
     * @param propertyName
     *            Propiedad del objeto a ser editada
     */
    getPropertyRow: function(table, object, propertyName) {
        var clas = object.constructor;
        var html = Editor.getHtmlControl(object, propertyName);
        var property = Editor.getObjectProperty(object, propertyName);

        html.row = table.insertRow(table.rows.length).addClassName("editor-property-row");

        html.label = property.getLabel();
        html.row.insertCell(0).update(html.label);

        html.input = property.getInput(object[propertyName], object, propertyName);
        html.row.insertCell(1).update(html.input);

        var elementEvents = $H($H(clas.getGeneratorProperties().events).get(propertyName));
        if (elementEvents) {
            elementEvents.each(function(pair) {
                html.input.on(pair.key, pair.value.bind(object));
            });
        }

        html.input.on("change", function(e) {
            FormGenerator.saveState(object);
            Editor.refresh(true);
        });
    },

    /**
     * Agrega las propiedades owner, ownlerList, ownerProperty a un objeto
     * recursivamente.
     *
     * @param object
     *            Objeto al que se agregarán las propiedades.
     * @param owner
     *            Opcional, objeto que contiene a este objeto.
     * @param ownerListOrProperty
     *            Opcional, lista que contiene a este objeto o propiedad del
     *            objeto owner que contiene este objeto.
     */
    initObject: function(object, owner, ownerListOrProperty) {
        if(!object || Editor.getOwner(object) || Editor.getOwnerList(object) ||
                Object.isFunction(object) || Object.isElement(object) ||
                PRIMITIVES.include(object.constructor)) {
            return;
        }

        if (owner) {
            Editor.setOwner(object, owner);
        }

        if (Object.isString(ownerListOrProperty)) {
            // ownerProperty
            Editor.setOwnerProperty(object, ownerListOrProperty);
        } else if (ownerListOrProperty) {
            // ownerList
            Editor.setOwnerList(object, ownerListOrProperty);
            Editor.setOwnerProperty(object, Editor.getOwnerProperty(ownerListOrProperty));
        }

        if (Object.isArray(object)) {
            for (var a = 0; a < object.length; a++) {
                object[a] = wrap(object[a]);
            }

            object.each(function(subItem) {
                FormGenerator.initObject(subItem, Editor.getOwner(object), object);
            });
        } else if(Object.isHash(object)) {
            var keys = object.keys().clone();

            keys.each(function(key) {
                var val = wrap(object.unset(key));

                FormGenerator.initObject(val, Editor.getOwner(object), object);

                object.set(key, val);
            });
        } else if (object.constructor != PrimitiveWrapper) {
            $H(object).each(function(pair) {
                if (pair.key.startsWith("_")) {
                    return;
                }
                FormGenerator.initObject(pair.value, object, pair.key);
            });
        }
    },

    /**
     * Elimina todas las propiedades seteadas en un objeto.
     *
     * @param object
     *            Objeto del que se quitarán las propiedades.
     */
    deinitObject: function(object) {
        Editor.properties.set(object._id, null);
    },

    /**
     * Genera un arbol de propiedades.
     *
     * @param object
     *            Objeto deal que se generara el arbol de propiedades.
     */
    generateTree: function(object) {
        var ul = new Element("ul", {
            className: "editor-property-tree"
        });

        object.constructor.getProperties().each(
                FormGenerator.getPropertyListItem.curry(ul, object));

        return ul;
    },

    /**
     * Genera un item del arbol de propiedades.
     *
     * @param ul
     *            Elemento UL donde se agregará el item.
     * @param object
     *            Objeto deal que se generara el item.
     * @param pair
     *            Par de nombre y propiedad;
     */
    getPropertyListItem: function(ul, object, pair) {
        var value = object[pair.key];

        var li = pair.value.getListItem(value, object, pair.key);

        if (li) {
            ul.insert(li);
        }
    },

    /**
     * Genera un editor en linea para un elemento.
     *
     * @param element
     *            Elemento html a ser editado
     * @param objectId
     *            Id del objeto que va a ser editado
     * @param objectClass
     *            Clase del objeto que va a ser editado
     */
    generateInlineEditor: function(element, objectId, objectClass) {
        element = $(element);

        if (!element) {
            return;
        }

        var object = Editor.getObject(objectId);

        if (!object) {
            return;
        }

        var className = "editor-" + objectClass.simpleClassName.toLowerCase();
        element.addClassName(className);

        var showTimeout;

        if (element.tagName == "INPUT") {
            element.on("focus", function() {
                element.focused = true;
            });
            element.on("blur", function() {
                element.focused = false;
            });
        }

        element.on("mouseenter", function() {
            showTimeout = setTimeout(function() {
                var popup = FormGenerator.generatePopup(object, element);
                popup.addClassName(className + "-popup");
            }, 500);
        });

        element.on("mouseleave", function() {
            clearTimeout(showTimeout);
        });
    },

    generatePopup: function(object, element) {
        var popup = new Element("span", {
            className: "editor-popup"
        }).absolutize();

        c.form.insert(popup);
        popup.clonePosition(element, {
            offsetTop: -20
        });
        popup.setStyle({
           width: "",
           height: ""
        });

        var owner = Editor.getOwner(object);
        var ownerList = Editor.getOwnerList(object);
        var ownerProperty = Editor.getOwnerProperty(ownerList);
        var prop = owner.constructor.getProperties().get(ownerProperty);

        popup.insert(object.getLabel());
        popup.insert(prop.getEditSubItemDiv(object));
        popup.insert(prop.getDeleteSubItemDiv(object));

        popup.hideWithTimeout = function() {
            if (element.focused) return;
            clearTimeout(popup.hideTimeout);
            popup.hideTimeout = setTimeout(function() {
                popup.hideHandler.stop();
                popup.blurHandler.stop();
                popup.remove();
            }, 100);
        }

        popup.hideHandler = element.on("mouseleave", popup.hideWithTimeout);

        if (element.tagName == "INPUT") {
            popup.blurHandler = element.on("blur", popup.hideWithTimeout);
        }

        popup.on("mouseleave", Element.hide.curry(popup));

        popup.on("mouseenter", function() {
            clearTimeout(popup.hideTimeout);
        });

        return popup;
    }

};
