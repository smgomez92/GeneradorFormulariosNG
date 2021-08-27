include("lib.prototype");

/**
 * @remote
 */
CopyValues.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var handler = (function() {
            if (!this.fireAlways && (elemento.readOnly || elemento.disabled)) {
                return;
            }
            
            this.fields.each(function(par) {
                var from = c.$N(par.value, elemento.registro) || c.$N(par.value, 0);
                var to = c.$N(par.key, elemento.registro) || c.$N(par.key, 0);
                
                to.changeValue(from.getObjectValue());
            });
            
        }).bind(this);
        
        this.addEventHandler(elemento, handler);
    }

});
