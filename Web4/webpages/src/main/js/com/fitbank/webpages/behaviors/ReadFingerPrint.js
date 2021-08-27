include("lib.prototype");

/**
 * @remote
 */
ReadFingerPrint.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(element) {
        element.on('click', (function() {
            if (!this.fireAlways && (element.readOnly || element.disabled)) {
                return;
            }

            var resultElement = c.$(this.resultElementName);
            var qualityElement = c.$(this.qualityElementName);

            var callback = function(json) {
                element.disabled = false;

                Validar.ok(element, this.constructor.simpleClassName);

                resultElement && resultElement.changeValue(json.result);
                qualityElement && qualityElement.changeValue(json.quality);
            };

            var callbackError = function(json) {
                var msg = json.message || Mensajes["com.fitbank.webpages.behaviors.ReadFingerPrint.ERROR_CALIDAD"];
                element.disabled = false;

                Validar.error(element, msg, this.constructor.simpleClassName);

                resultElement && resultElement.changeValue("");
                qualityElement && qualityElement.changeValue("");

                return msg;
            };

            element.disabled = true;
            Huella.capturar(c.$(this.idElementName).value, callback.bind(this),
                    callbackError.bind(this));
        }).bind(this));
    }

});
