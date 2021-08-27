include("lib.prototype");

File.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var archivo = new Element("input", {
            type: "file",
            disabled: elemento.disabled,
            name: elemento.name + "_" + elemento.registro,
            'class': elemento.className
        });

        archivo.registro = elemento.registro;
        elemento.assistant = this;
        elemento.widget = archivo;
        archivo.oculto = elemento;

        elemento.hide().insert({
            after: archivo
        });

        elemento.on("widget:init", function() {
            archivo.value = "";
        });
        elemento.on("change", function() {
            archivo.value = "";
        });

        archivo.insert({
            after: this._createLink(elemento, archivo)
        });
    },

    _createLink: function(elemento, archivo) {
        if (!this.showLink) {
            return null;
        }

        var link = new Element("a", {
            href: "#",
            onclick: "return false;"
        }).update("Descargar").hide();

        link.on("click", (function() {
            window.open(this._buildUrl(GeneralRequestTypes.FILE, elemento));
        }).bind(this));

        var change = function(e) {
            if (elemento.value) {
                link.show();
            } else {
                link.hide();
            }
        };

        elemento.on("widget:init", change);
        elemento.on("change", change);

        return link;
    },

    _buildUrl: function(type, elemento, extra, parameters) {
        extra = extra || "";

        var downloadNameFinal = this.downloadName;
        if (c.$(this.downloadName, elemento.registro || 0) != null) {
            downloadNameFinal = c.$V(this.downloadName, elemento.registro || 0);
        }

        parameters = $H(Object.extend({
            _contexto: c.id,
            hashCode: elemento.value.hashCode(),
            elementName: elemento.name,
            registro: elemento.registro,
            extension: this.extension,
            downloadName: downloadNameFinal,
            extra: extra
        }, parameters || {})).toQueryString();

        return "proc/" + type + "/?" + parameters;
    }

});
