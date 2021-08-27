include("lib.prototype");
include("lib.onload");

include("lib.scriptaculous.effects");
include("lib.scriptaculous.dragdrop");
include("lib.scriptaculous.controls");

include("fitbank.proc.clases");
include("fitbank.proc.properties");
include("fitbank.ui.ventana");

include("fitbank.editor.FormGenerator");
include("fitbank.editor.GeneratorProperties");
include("fitbank.editor.PrimitiveWrapper");
include("fitbank.editor.Wizard");

include("fitbank.editor.properties.Propiedad");
include("fitbank.editor.properties.PropiedadBooleana");
include("fitbank.editor.properties.PropiedadCombo");
include("fitbank.editor.properties.PropiedadLista");
include("fitbank.editor.properties.PropiedadMapa");
include("fitbank.editor.properties.PropiedadNumerica");
include("fitbank.editor.properties.PropiedadObjeto");
include("fitbank.editor.properties.PropiedadSimple");

include("fitbank.editor.classes.DataSource");
include("fitbank.editor.classes.Dependency");
include("fitbank.editor.classes.Group");
include("fitbank.editor.classes.Field");
include("fitbank.editor.classes.Reference");
include("fitbank.editor.classes.Widget");

/**
 * Namespace Editor - Contiene funciones generales del editor.
 */
var Editor = {

    webPageDefinition: null,

    tables: $H(),

    properties: $H(),

    init: function() {
        Barra.agregarSeparador();

        Barra.agregarBoton( {
            nombre: "wizard",
            titulo: "Wizard",
            onclick: Wizard.show
        });

        Barra.agregarBoton( {
            nombre: "guardar",
            titulo: "Guardar",
            onclick: Editor.save
        });

        Barra.agregarBoton( {
            nombre: "compilar",
            titulo: "Compilar",
            onclick: Editor.compile
        });

        Barra.agregarBoton( {
            nombre: "recargar",
            titulo: "Recargar",
            onclick: Editor.refresh
        });

        var treeWrap = new Element("div", {
            className: "editor-property-tree-wrap"
        }).hide();
        document.body.insert(treeWrap);

        var label = new Element("label");
        treeWrap.insert(label);

        var handle = new Element("img", {
            src: "img/agarradera.png",
            className: "handle"
        });
        label.insert(handle);
        new Draggable(treeWrap.identify(), {
            handle: handle
        });
        treeWrap.setStyle({ position: "absolute" });

        label.insert("WebPageDefinition");

        var edit = new Element("div", {
            className: "editor-edit inline"
        });
        label.insert(edit);

        edit.on("click", function() {
            FormGenerator.generateWindow(Editor.webPageDefinition);
        });

        var refresh = new Element("div", {
            className: "editor-refresh inline"
        });
        label.insert(refresh);
        refresh.on("click", Editor.refresh);

        Editor.treeContainer = new Element("div");
        treeWrap.insert(Editor.treeContainer);
        label.on("click", function(e) {
            if ($E(e).elemento == label) {
                Editor.treeContainer.toggle();
            }
        });

        Entorno.contexto._obtener = function(opciones) {
            Entorno.contexto.reset();
            new Ajax.Request("proc/" + EditorRequestTypes.LOAD_WEBPAGE, {
                parameters: {
                    _contexto: Entorno.contexto.id,
                    _subs: opciones.subsistema,
                    _tran: opciones.transaccion
                },
                evalJS: false,
                evalJSON: false,
                onSuccess: function(transport) {
                    Editor.webPageDefinition = eval(transport.responseText);
                    FormGenerator.initObject(Editor.webPageDefinition);
                    treeWrap.show();
                    Editor.refresh();
                },
                onFailure: function(transport) {
                    Estatus.mensaje("Failed to load WebPageDefinition.");
                    Editor.webPageDefinition = new WebPageDefinition({
                        subsystem: opciones.subsistema,
                        transaction: opciones.transaccion
                    });
                    FormGenerator.initObject(Editor.webPageDefinition);
                    treeWrap.show();
                    Editor.refresh();
                },
                onException: Entorno.contexto.onError.bind(Entorno.contexto)
            });
        };
    },

    /**
     * Guarda el WebPageDefinition
     */
    save: function() {
        new Ajax.Request("proc/" + EditorRequestTypes.SAVE_WEBPAGE, {
            parameters: {
                _contexto: Entorno.contexto.id,
                _subs: Editor.webPageDefinition.subsystem,
                _trans: Editor.webPageDefinition.transaction,
                json: Object.toJSON(Editor.webPageDefinition)
            },
            onSuccess: function(transport) {
                Estatus.mensaje("WebPageDefinition guardado");
            },
            onFailure: Entorno.contexto.onError.bind(Entorno.contexto),
            onException: rethrow
        });
    },

    /**
     * Genera y guarda un WebPage
     */
    compile: function() {
        new Ajax.Request("proc/" + EditorRequestTypes.COMPILE_WEBPAGE, {
            parameters: {
                _contexto: Entorno.contexto.id,
                _subs: Editor.webPageDefinition.subsystem,
                _trans: Editor.webPageDefinition.transaction,
                json: Object.toJSON(Editor.webPageDefinition)
            },
            onSuccess: function(transport) {
                Estatus.mensaje("WebPage guardado");
            },
            onFailure: Entorno.contexto.onError.bind(Entorno.contexto),
            onException: rethrow
        });
    },

    /**
     * Refresca tanto el arbol de propiedades como el WebPage
     */
    refresh: function(mantenerTab) {
        Editor.treeContainer.update(FormGenerator
                .generateTree(Editor.webPageDefinition));

        Editor.tables.values().invoke("refresh");

        new Ajax.Request("proc/" + EditorRequestTypes.PREVIEW_WEBPAGE, {
            parameters: {
                _contexto: Entorno.contexto.id,
                json: Object.toJSON(Editor.webPageDefinition)
            },
            onSuccess: function(transport) {
                Entorno.contexto.mostrarForm(mantenerTab, transport);
            },
            onFailure: Entorno.contexto.onError.bind(Entorno.contexto),
            onException: rethrow
        });
    },

    /**
     * Obtiene una propiedad arbitraria para un objeto cualquiera almacenada en
     * Editor.properties.
     * 
     * @param object
     *            Objeto cualquiera
     * @param property
     *            Propiedad a ser obtenida
     * 
     * @return La propiedad si existe o null
     */
    getProperty: function(object, property) {
        if (!object || !object._id || !Editor.properties.get(object._id)) {
            return null;
        }

        return Editor.properties.get(object._id).get(property);
    },

    /**
     * Cambia una propiedad arbitraria para un objeto cualquiera almacenada en
     * Editor.properties.
     * 
     * @param object
     *            Objeto cualquiera
     * @param property
     *            Propiedad a ser obtenida
     * @param value
     *            Valor que va a tomar la propiedad
     */
    setProperty: function(object, property, value) {
        if (!object) {
            return;
        }

        if (!object._id) {
            object._id = object.id || Util.generarIdUnicoTemporal();
        }

        if (!Editor.properties.get(object._id)) {
            Editor.properties.set(object._id, $H());
            Editor.properties.get(object._id).set("object", object);
        }

        Editor.properties.get(object._id).set(property, value);
    },

    getObject: function(id) {
        return Editor.properties.get(id) && Editor.properties.get(id).get("object");
    },

    getOwner: function(object) {
        return Editor.getProperty(object, "owner");
    },

    setOwner: function(object, owner) {
        Editor.setProperty(object, "owner", owner);
    },

    getOwnerList: function(object) {
        return Editor.getProperty(object, "ownerList");
    },

    setOwnerList: function(object, ownerList) {
        Editor.setProperty(object, "ownerList", ownerList);
    },

    getOwnerProperty: function(object) {
        return Editor.getProperty(object, "ownerProperty");
    },

    setOwnerProperty: function(object, ownerProperty) {
        Editor.setProperty(object, "ownerProperty", ownerProperty);
    },

    getHtmlControls: function(object) {
        if (!Editor.getProperty(object, "html")) {
            Editor.setProperty(object, "html", $H());
        }

        return Editor.getProperty(object, "html");
    },

    getHtmlControl: function(object, property) {
        if (!Editor.getHtmlControls(object).get(property)) {
            Editor.getHtmlControls(object).set(property, {});
        }

        return Editor.getHtmlControls(object).get(property);
    },

    setHtmlControl: function(object, property, element) {
        Editor.getHtmlControls(object).set(property, element);
    },

    getObjectProperty: function(object, property) {
        if (!object) {
            return null;
        }

        if (!property) {
            property = Editor.getOwnerProperty(object);
            object = Editor.getOwner(object);
        }

        return object.constructor.getProperties().get(property);
    },

    /**
     * Cambia una propiedad en un objeto.
     * 
     * @param originalValue
     *            Valor original
     * @param newValue
     *            Nuevo valor
     * @param owner
     *            Opcional, el objeto a quien pertenece originalValue
     * @param ownerProperty
     *            Opcional, la propiedad en el objeto owner a la que pertenece
     *            originalValue
     */
    changeValue: function(originalValue, newValue, owner, ownerProperty) {
        if (originalValue == null && !owner) {
            throw new Error("Sin owner");
        }

        owner = owner || Editor.getOwner(originalValue);
        ownerProperty = ownerProperty
                || Editor.getOwnerProperty(originalValue);

        owner[ownerProperty] = newValue;

        FormGenerator.initObject(newValue, owner, ownerProperty);

        if (originalValue) {
            FormGenerator.deinitObject(originalValue);
        }

        Editor.refresh(true);

        return newValue;
    }

};

function $H(object) {
    var id = null;
    if (object && typeof object._id != "undefined") {
        id = object._id;
        delete object["_id"];
    }
    var hash = new Hash(object);
    if (id) {
        object._id = id;
        hash._id = id;
    }
    return hash;
};

addOnLoad(Editor.init);
