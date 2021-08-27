include("lib.prototype");

/**
 * @remote
 */
FormulaValidator.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        this.id = this.constructor.simpleClassName + ":" + this.formula;

        c && c.$W(this.elementName).each(function(elemento) {
            c.formulario.otrosCalculos.push(this.calculos.bind(this, elemento));
        }, this);
    },

    calculos: function(elemento) {
        if (!(this.validateEmpty || elemento.value) || this.formulaJS(elemento.registro)) {
            Validar.ok(elemento, this.id);
        } else {
            var customMessage = this.getCustomMessage(this.message, elemento);
            if (customMessage !== null) {
                Validar.error(elemento, customMessage, this.id);
            }
        }
    },

    /**
     * En caso de ser necesario, usar una libreria que ya haga esto.
     * Este metodo devuelve un mensaje que contien campos del formulario,
     * para reemplazarlos con su valor, en la forma: "MENSAJE {campo, indice} AQUI"
     * 
     * @param String message a evaluar
     * @returns String mensaje ya evaluado
     */
    getCustomMessage: function(message, elemento) {
        if (!message) {
            return null;
        }

        var valuesMap = $H();
        message.split("{").each(function(e) {
            if (e.indexOf("}") >= 0) {
                var token = e.split("}")[0].trim();
                var field = token;
                var index = 0;
                if (token.indexOf(",") >= 0) {
                    field = token.split(",")[0].trim();
                    var indexValue = token.split(",")[1].trim();
                    if (indexValue === 'this.registro') {
                        index = elemento.registro;
                    } else {
                        index = Number.parseInt(token.split(",")[1].trim());
                    }
                }

                var valueToken = c.$(field, index) && c.$(field, index).value;
                if (valueToken) {
                    valuesMap.set("{" + token + "}", valueToken);
                }
            }
        });

        //Si no se ha podido parsear almenos un mensaje... no lanzar el error
        if (message.indexOf("{") >= 0 && valuesMap.size() === 0) {
            return null;
        }

        valuesMap.each(function(mapEntry) {
            message = message.replace(mapEntry.key, mapEntry.value);
        });

        return message;
    }

});