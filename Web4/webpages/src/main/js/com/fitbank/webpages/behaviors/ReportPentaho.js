include("lib.prototype");

ReportPentaho.getParameters = function(values, registro, defaultValues) {
    var newValues = $H(defaultValues);

    values.each(function(val) {
        var valor = val.value;
        var elemento = c.$(val.value);
        if (elemento && elemento.length > 0) {
            if (Object.isArray(elemento)) {
                if (elemento.length <= registro) {
                    return;
                }
                elemento = elemento[registro];
            }

            valor = elemento.value;
        } else {
            valor = val.value;
        }

        newValues.set(val.key, valor);
    });

    return newValues;
};

/**
 * @remote
 */
ReportPentaho.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var label = $(elemento.getAttribute("labelid"));

        if (label) {
            elemento = label;
            elemento.href = "#";
            elemento.onclick = "return false;";
        }

        elemento.on('click', (function() {
            if (!this.fireAlways && (elemento.readOnly || elemento.disabled)) {
                return;
            }

            var name = this.name;
            if (c.$(this.name, elemento.registro || 0) != null) {
                name = c.$V(this.name, elemento.registro || 0);
            } else if (c.$(this.name, 0) != null) {
                name = c.$V(this.name, 0);
            }

            var type = this.type;
            if (c.$(this.type, elemento.registro || 0) != null) {
                type = c.$V(this.type, elemento.registro || 0);
            } else if (c.$(this.type, 0) != null) {
                type = c.$V(this.type, 0);
            }

            var folderName = this.folderName;
            if (c.$(this.folderName, elemento.registro || 0) != null) {
                folderName = c.$V(this.folderName, elemento.registro || 0);
            } else if (c.$(this.folderName, 0) != null) {
                folderName = c.$V(this.folderName, 0);
            }

            var parameters = this._getParameters(this.parameters, elemento.registro);

            this.mostrar(name, type, folderName, parameters);
        }).bind(this));
    },

    /**
     * Funcion que genera un reporte invocando WS de Pentaho
     *
     * @parameter (String) name Nombre de reporte a ser bajado, la extension denota el tipo de reporte
     */
    mostrar: function(name, type, folderName, parameters) {
        var randomId = 'reporte' + Math.floor(Math.random() * 10001);
        window.open('about:blank', randomId, '');

        var rParams = $H({
            name: name,
            type: type,
            folderName: folderName
        });

        Enlace.submit(c, {
            target: randomId,
            action: "proc/rep_pentaho/",
            params: rParams.merge(parameters),
            skipFiles: true
        });
    },

    _getParameters: ReportPentaho.getParameters
});
