include("lib.prototype");

include("fitbank.validar");

/**
 * @remote
 */
Formatter.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(function(e) {
            if (!e.formatters) {
                e.formatters = $A();
            }
            e.formatters.push(this);
            this.changeValues.bind(this).defer(e, false);
        }, this);
    },

    changeValues: function(elemento, partial) {
        try {
            this.transform(elemento.value, partial);

            elemento.dateValue = null;
            elemento.intValue = Number.NaN;
            elemento.floatValue = Number.NaN;

            elemento.objectValue = this.toObject(elemento, partial);

            if (elemento.objectValue && elemento.objectValue.constructor == Date) {
                elemento.dateValue = elemento.objectValue;
                elemento.intValue = elemento.dateValue.getTime();
                elemento.floatValue = elemento.intValue / 1000;
            } else if (Object.isNumber(elemento.objectValue)) {
                elemento.floatValue = elemento.objectValue;
                elemento.intValue = Math.round(elemento.floatValue);
            }

            Validar.ok(elemento, this.constructor.simpleClassName);
        } catch(e) {
            Validar.error(elemento, e, this.constructor.simpleClassName);
        }
    },

    /**
     * Funcion a ser sobreescrita por formateadores que se ejecuta al modificar
     * un campo
     *
     * @param value Valor a ser convertido
     * @param partial Indica si se debe tomar como valor completo o parcial
     */
    transform: function(value, partial) {
        return value;
    },

    /**
     * Funcion a ser sobreescrita por formateadores
     *
     * @param elemento Elemento que contiene el valor
     */
    toObject: function(elemento) {
        return elemento.value;
    },

    getLabel: function() {
        return this.simpleClassName;
    }

});
