include("lib.prototype");
include("lib.onload");
include("lib.underscore");

var ManualTecnico = {

    init: function() {
        ManualTecnico.consultar();
    },

    consultar: function() {
        new Ajax.Request("proc/manual_tecnico", {
            parameters: {
                _contexto: document.location.hash.substring(1)
            },
            onComplete: function(transport) {
                this.data = transport.responseJSON;

                this.setTitle();

                this.setBasicSpecifications('#specifications');
                this.setFormularios('#formularios');
                this.setComandos('#comandos');
                this.setTablas('#tablas');
                this.setLovs('#lovs');
                this.setLinks('#links');
                this.setReportes('#reportes');

            }.bind(this)
        });
    },

    tab: function(tabla, regNum, display) {
        var t = $j(tabla).parent().parent();
        if(display) {
            this.tabInfo(t.parent()[0].getElementsByClassName('tab-info')[0], regNum);
        }

        t.parent().find('h1')[0].className = t.parent().find('h1')[0].className + " tab-enabled";

        t.parent().find('h1')[0].addEventListener('click', function() {
            var display = t[0].style.display;
            t[0].style.display = display === "" || display === "none" ? "block" : "none";
        });
    },

    emptyTab: function(tabla) {
        var t = $j(tabla).parent().parent();
        t.find('h1')[0].className = t.find('h1')[0].className + " tab-disabled";
        this.tabInfo(t[0].getElementsByClassName('tab-info')[0], "0");
    },

    tabInfo: function(campo, texto) {
        campo.innerHTML = texto;
    },

    yesNo: function(val) {
        return val ? "Si" : "No";
    },

    setTitle: function() {
        var title = this.data.webPage.title;
        var subsystemTransaction = "Manual Técnico " + this.data.webPage.subsystem + this.data.webPage.transaction;
        $$('#title')[0].getElementsByTagName('h1')[0].innerHTML = subsystemTransaction;
        $$('#title')[0].getElementsByTagName('h2')[0].innerHTML = title;
    },

    setDataTable: function(tabla, dataSet, headers, searching) {
        $j(document).ready(function() {
            $j(tabla).DataTable({
                "info":     false,
                "paging":   false,
                "ordering": true,
                "searching": searching,
                "data": dataSet,
                "columns": headers
            });
        });
    },

    setBasicSpecifications: function(tabla) {

        var headers = [{ "title": "Caracteristica" }, { "title": "Valor" }];
        var dataSet = [
            ['Paginación Multiregistro', this.yesNo(this.data.webPage.paginacion)],
            ['Permite mantenimiento', this.yesNo(this.data.webPage.store)],
            ['Consulta post-mantenimiento', this.yesNo(this.data.webPage.postQuery)],
            ['Requiere consulta antes de mantener', this.yesNo(this.data.webPage.requiresQuery)],
            ['Shift-Limpiar', this.yesNo(this.data.webPage.clean)],
            ['Compatibilidad FitWeb2', this.yesNo(this.data.webPage.legacy)],
            ['JavaScript inicial', this.yesNo(this.data.webPage.initialJS != null)],
            ['Elemento foco Pos-Consulta', this.data.webPage.queryFocus]
        ];

        this.setDataTable(tabla, dataSet, headers, false);
        this.tab(tabla, "", false);
    },

    setFormularios: function(tabla) {
        var attachedFiles = ManualTecnico.data.webPage.attached;

        if(attachedFiles.length > 0) {
            var dataSet = [];
            var headers = [{ "title": "Formulario" },
                           { "title": "Posición" },
                           { "title": "Solo lectura" }];

            for(var i = 0; i < attachedFiles.length; i++) {
                var af = attachedFiles[i];
                dataSet.push([af.subsystem + "-" + af.transaction, af.position, this.yesNo(af.readOnly)]);
            }

            this.setDataTable(tabla, dataSet, headers, true);
            this.tab(tabla, attachedFiles.length, true);
        } else {
            this.emptyTab(tabla);
        }
    },

    setComandos: function(tabla) {
        var comandos = ManualTecnico.data.commands;
        var comNum = 0;

        for(type in comandos) {
            for (com in comandos[type][0]) {
                comNum++
            }
        }

        if(comNum > 0) {
            var dataSet = [];
            var headers = [{ "title": "Tipo" },
                           { "title": "Orden" },
                           { "title": "Nombre" },
                           { "title": "Ejecutado por" },
                           { "title": "Evento" }];

            for(type in comandos) {
                for (com in comandos[type][0]) {
                    c = comandos[type][0][com];
                    dataSet.push([type, c.ORDEN, c.EJECUTADO_POR, c.EVENTO, c.COMANDO]);
                }
            }

            this.setDataTable(tabla, dataSet, headers, true);
            this.tab(tabla, comNum, true);
        } else {
            this.emptyTab(tabla);
        }
    },

    setTablas: function(tabla) {
        var tables = ManualTecnico.data.webPage.references;

        var checkRepeated = [];

        if(tables.length > 0) {
            var dataSet = [];
            var headers = [{ "title": "Nombre" },
                           { "title": "Solo lectura" },
                           { "title": "Especial" },
                           { "title": "Solo escritura" }];

            for(var i = 0; i < tables.length; i++) {

                var t = tables[i];

                if(checkRepeated.indexOf(t.table) === -1) {
                    checkRepeated.push(t.table);
                    dataSet.push([t.table, this.yesNo(t.queryOnly), this.yesNo(t.special), this.yesNo(t.storeOnly)]);
                }

            }

            this.setDataTable(tabla, dataSet, headers, true);
            this.tab(tabla, checkRepeated.length, true);

        } else {
            this.emptyTab(tabla);
        }
    },

    setLovs: function(tabla) {
        var lovs = ManualTecnico.data.lovs;

        if(lovs.length > 0) {
            var dataSet = [];
            var headers =  [{ "title": "Nombre" },
                            { "title": "Referencias" },
                            { "title": "Campos" },
                            { "title": "Callback" },
                            { "title": "Consultar al elegir" }];

            for(var i = 0; i < lovs.length; i++) {
                var l = lovs[i];

                var name = l.elementName + " " + l.subsystem + " " + l.transaction;
                var refs = l.references.map(function(el) { return el.table + " as " + el.alias; }).join("\n");
                var fields = l.fields.map(function(el) { return el.alias + "." + el.field; }).join("\n");
                var callback = this.yesNo(l.callback != null);
                var cae = this.yesNo(l.autoQuery != null);

                dataSet.push([name, refs, fields, callback, cae]);
            }

            this.setDataTable(tabla, dataSet, headers, true);
            this.tab(tabla, lovs.length, true);
        } else {
            this.emptyTab(tabla);
        }
    },

    setLinks: function(tabla) {
        var links = ManualTecnico.data.links;

        if(links.length > 0) {
            var dataSet = [];
            var headers = [{ "title": "Nombre" },
                           { "title": "Subsistema" },
                           { "title": "Transacción" }];

            for(var i = 0; i < links.length; i++) {
                var l = links[i];
                dataSet.push([l.elementName, l.subsystem, l.transaction]);
            }

            this.setDataTable(tabla, dataSet, headers, true);
            this.tab(tabla, links.length, true);
        } else {
            this.emptyTab(tabla);
        }
    },

    setReportes: function(tabla) {
        var reportes = ManualTecnico.data.reports;

        if(reportes.length > 0) {
            var dataSet = [];
            var headers = [{ "title": "Nombre" },
                           { "title": "Elemeneto" }];

            for(var i = 0; i < reportes.length; i++) {
                var r = reportes[i];
                dataSet.push([r.name, r.elementName]);
            }

            this.setDataTable(tabla, dataSet, headers, true);
            this.tab(tabla, reportes.length, true);
        } else {
            this.emptyTab(tabla);
        }
    },
};

addOnLoad(ManualTecnico.init);
