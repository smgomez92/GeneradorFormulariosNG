include("lib.prototype");

include("fitbank.ui.ventana");

Link.getValues = function(values, registro, defaultValues) {
    var newValues = $H(defaultValues);

    values.each(function(val) {
        var elemento = c.$(val.value);
        if (!elemento) {
            return;
        }
        if (Object.isArray(elemento)) {
            if (elemento.length <= registro) {
                return;
            }
            elemento = elemento[registro];
        }
        newValues.set(val.key, elemento.getObjectValue());
    });

    return newValues;
};

/**
 * @remote
 */
Link.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        this.nameMap = $H();
        this.values.each(function(par) {
            this.nameMap.set(par.key, par.value);
        }, this);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var label = $(elemento.getAttribute("labelid"));

        if (label) {
            elemento = label;
        }

        var manejador = (function() {
            if (!this.fireAlways && (elemento.readOnly || elemento.disabled)) {
                return;
            }

            var navSize = c.navegacion.size();
            var previousNav = navSize > 0 ? c.navegacion[navSize - 1] : null;
            var newValues = this._getValues(this.values, elemento.registro);
            var options = {
                subsistema: this.subsystem,
                transaccion: this.transaction,
                campos: newValues,
                nameMap: this.nameMap,
                objetoJS: eval(this.jsObject),
                consulta: this.query,
                registro: this.goToRecord && elemento.registro || 0,
                posLink: this.posLink
            };

            var message = this.preLink && this.preLink(options);
            if (message || message === false) {
                this._restoreNav && this._restoreNav(previousNav);
                Estatus.mensaje(message || this.message ||
                    Mensajes[this.constructor.className + '.ERROR_PRELINK'],
                null, "error");
                return;
            }

            c.cargar(options);
        }).bind(this);

        elemento.on('click', manejador.defer.bind(manejador));
    },

    _getValues: Link.getValues

});

