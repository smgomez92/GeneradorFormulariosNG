include("lib.prototype");

/**
 * @remote
 */
EmailValidator.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var expression = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9_\.\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
        
        var handler = (function(elemento) {
            if (expression.test(elemento.value) || elemento.value == "") {
                Validar.ok(elemento, this.constructor.simpleClassName);
            } else {
                var message = this.message || Mensajes["com.fitbank.webpages.behaviors.EmailValidator.ERROR"];
                Validar.error(elemento, message, this.constructor.simpleClassName);
            }
        }).bind(this, elemento);
        
        this.addEventHandler(elemento, handler);
    }

});