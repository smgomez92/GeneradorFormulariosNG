include("lib.prototype");

include("fitbank.ui.ventana");

/**
 * Namespace Wizard - funciones para crear y manejar WizardData
 */
var Wizard = {

    ventana: null,

    show: function() {
        if (Editor.webPageDefinition.groups) {
            if (!confirm("Este WebPage tiene contenido que se van a sobreescribir, quiere continuar?")) {
                return;
            }
        }

        if (!Editor.webPageDefinition.wizardData) {
            Editor.webPageDefinition.wizardData = new WizardData();
        }

        Wizard.data = Editor.webPageDefinition.wizardData;

        if (!Wizard.ventana) {
            Wizard.ventana = new Ventana({
                titulo: "Wizard",
                contenido: Wizard._generarContenido(),
                w: 775,
                h: 400
            });
        }

        if (Wizard.data.tableName) {
            Wizard.loadTable(true);
            Wizard._generateTableContent(Wizard.criteria);
            Wizard._generateTableContent(Wizard.fields);
        }

        Wizard.ventana.ver();
    },

    _generarContenido: function() {
        var div = new Element("div", {
            className: "wizard"
        });

        div.insert(new Element("label", {
            'for': "wizard_table"
        }).update("Tabla:"));

        Wizard.table = new Element("input", {
            name: "wizard_table",
            value: Wizard.data.tableName
        });
        div.insert(Wizard.table);

        var loadTable = new Element("button").update("Cargar");
        div.insert(loadTable);
        loadTable.on("click", Wizard.loadTable);

        Wizard.criteria = new Element("fieldset");
        div.insert(Wizard.criteria);

        Wizard.criteria.clase = WizardCriterion;
        Wizard.criteria.data = Wizard.data.criteria;
        Wizard.criteria.insert(new Element("legend").update("Criterios"));

        Wizard._generateList(Wizard.criteria);
        Wizard._generateButtons(Wizard.criteria);
        Wizard._generateTable(Wizard.criteria);

        Wizard.fields = new Element("fieldset");
        div.insert(Wizard.fields);

        Wizard.fields.clase = WizardField;
        Wizard.fields.data = Wizard.data.fields;
        Wizard.fields.insert(new Element("legend").update("Campos"));

        Wizard._generateList(Wizard.fields);
        Wizard._generateButtons(Wizard.fields);
        Wizard._generateTable(Wizard.fields);

        var ok = new Element("button").update("Ok");
        div.insert(ok);
        ok.on("click", function() {
            new Ajax.Request("proc/" + EditorRequestTypes.GENERATE_WIZARD, {
                parameters: {
                    _contexto: Entorno.contexto.id,
                    _subs: Editor.webPageDefinition.subsystem,
                    _trans: Editor.webPageDefinition.transaction,
                    json: Object.toJSON(Editor.webPageDefinition)
                },
                onSuccess: function(transport) {
                    Editor.webPageDefinition = eval(transport.responseText);
                    FormGenerator.initObject(Editor.webPageDefinition);
                    Editor.refresh();
                    Wizard.ventana.cerrar();
                    Wizard.ventana = null;
                },
                onFailure: function(transport) {
                    Estatus.mensaje("Error al generar el webpage.");
                    Editor.refresh();
                },
                onException: Entorno.contexto.onError.bind(Entorno.contexto)
            });
        });

        return div;
    },

    loadTable: function(ignore) {
        if (ignore !== true) {
            if (Wizard.data.tableName && !confirm("Se borraran todos los campos. Seguro desea cambiar de tabla?")) {
                return;
            }
            Wizard.data.criteria.clear();
            Wizard.data.fields.clear();
        }

        Wizard.data.tableName = Wizard.table.value;

        Wizard.criteria.list.update("");
        Wizard.criteria.table.down("tbody").update("");
        Wizard.fields.list.update("");
        Wizard.fields.table.down("tbody").update("");

        var table = Schema.get(Wizard.table.value);

        $H(table.fields).keys().each(function(key) {
            Wizard.criteria.list.insert(new Element("option", {
                value: key
            }).update(key));
            Wizard.fields.list.insert(new Element("option", {
                value: key
            }).update(key));
        });
    },

    _generateList: function(contenedor) {
        contenedor.list = new Element("select", {
            multiple: true
        });
        contenedor.insert(contenedor.list);
    },

    _generateButtons: function(contenedor) {
        var buttons = new Element("div");
        contenedor.insert(buttons);

        var addSelected = new Element("button").update(">");
        buttons.insert(addSelected);

        addSelected.on("click", function() {
            contenedor.list.select("option").each(function(option) {
                if (option.selected) {
                    Wizard._addField(contenedor, option.value);
                }
            });
        });

        var addAll = new Element("button").update(">>");
        buttons.insert(addAll);

        addAll.on("click", function() {
            contenedor.list.select("option").each(function(option) {
                Wizard._addField(contenedor, option.value);
            });
        });
    },

    _generateTable: function(contenedor) {
        var div =  new Element("div", {
            className: "wizard-table"
        });
        contenedor.insert(div);

        contenedor.table = new Element("table");
        div.insert(contenedor.table);

        var thead = new Element("thead");
        contenedor.table.insert(thead);

        var tr = new Element("tr");
        thead.insert(tr);

        tr.insert(new Element("th").setStyle({ width: "10px" }).update(""));
        tr.insert(new Element("th").update("Nombre"));
        tr.insert(new Element("th").update("Título"));
        tr.insert(new Element("th").update("Descripción"));
        tr.insert(new Element("th").update(""));
        tr.insert(new Element("th").update(""));
        tr.insert(new Element("th").update(""));

        var tbody = new Element("tbody");
        contenedor.table.insert(tbody);
    },

    _generateTableContent: function(contenedor) {
        var tbody = contenedor.table.down("tbody");
        tbody.update("");
        contenedor.data.each(function(field) {
            tbody.insert(Wizard._generateRow(contenedor, field));
        });
    },

    _generateRow: function(contenedor, wizardField) {
        var tr = new Element("tr");

        tr.insert(new Element("th").update("&nbsp;"));

        td = tr.insertCell(1);
        var name = new Element("input", {
            value: wizardField.name
        });
        td.insert(name);
        name.on("change", function() {
            wizardField.name = name.value;
        });

        td = tr.insertCell(2);
        var title = new Element("input", {
            value: wizardField.title
        });
        td.insert(title);
        title.on("change", function() {
            wizardField.title = title.value;
        });

        td = tr.insertCell(3);
        var desc = new Element("input", {
            type: "checkbox",
            checked: wizardField.showDescription
        });
        if ($H(Schema.get(Wizard.data.tableName).fields).get(wizardField.name).descriptionKey) {
            td.insert(desc);
            desc.on("change", function() {
                wizardField.showDescription = desc.checked;
            });
        }

        var up = tr.insertCell(4).insert(new Element("div", {
            className: "editor-up",
            title: "Subir"
        }));
        if (contenedor.data.indexOf(wizardField) == 0) {
            up.down("div").hide();
        }
        up.on("click", function() {
            var pos = contenedor.data.indexOf(wizardField);
            var field = contenedor.data.splice(pos, 1)[0];
            var other = contenedor.data.splice(pos - 1);

            contenedor.data.push(field);
            other.each(function(o) {
                contenedor.data.push(o);
            });

            Wizard._generateTableContent(contenedor);
        });

        var down = tr.insertCell(5).insert(new Element("div", {
            className: "editor-down",
            title: "Bajar"
        }));
        if (contenedor.data.indexOf(wizardField) == contenedor.data.length - 1) {
            down.down("div").hide();
        }
        down.on("click", function() {
            var pos = contenedor.data.indexOf(wizardField);
            var field = contenedor.data.splice(pos, 1)[0];
            var other = contenedor.data.splice(pos + 1);

            contenedor.data.push(field);
            other.each(function(o) {
                contenedor.data.push(o);
            });

            Wizard._generateTableContent(contenedor);
        });

        var del = tr.insertCell(6).insert(new Element("div", {
            className: "editor-delete inline",
            title: "Borrar"
        }));
        del.on("click", function() {
            contenedor.data.splice(contenedor.data.indexOf(wizardField), 1);
            Wizard._generateTableContent(contenedor);
        });

        return tr;
    },

    _addField: function(contenedor, fieldName) {
        var field = new contenedor.clase();
        contenedor.data.push(field);
        field.name = fieldName;
        Wizard._generateTableContent(contenedor);
    }

};

