include("lib.prototype");

/**
 * @remote
 */
ClearValues.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var handler = (function() {
            this.elements.each(function(e) {
                c.$(e, elemento.registro).changeValue("");
            }, this);
        }).bind(this);
        
        this.addEventHandler(elemento, handler);
    }
});