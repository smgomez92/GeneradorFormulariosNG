include("lib.prototype");

/**
 * @remote
 */
CopyValue.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var onchange = (function() {
            if (!this.fireAlways && (elemento.readOnly || elemento.disabled)) {
                return;
            }
            
            var to = c.$N(this.to, elemento.registro);
            
            if (!to) {
                to = c.$N(this.to, 0);
            }

            var fromRecord = elemento.registro;

            if (!c.$N(this.from, elemento.registro)) {
                fromRecord = 0;
            }

            to.changeValue(c.$V(this.from, fromRecord));
        }).bind(this);

        this.addEventHandler(elemento, Function.defer.bind(onchange));
    }

});
