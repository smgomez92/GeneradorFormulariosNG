include("lib.prototype");

include("fitbank.escaneo");

Scanner.addMethods({

    init: function(elemento) {
        elemento.on('click', (function() {
            this.procesar(elemento);
        }).bind(this));


        elemento.insert({
            after: this._createLink(elemento)
        });
    },

    ver: function(elemento) {
        if (elemento.readOnly || elemento.disabled) {
            return;
        }

        Escaneo.escanear(this.scanningJob, elemento);
    },
    
    procesar: function(elemento) {
        if (elemento.readOnly || elemento.disabled) {
            return;
        }

        Escaneo.solo_escanear(this.scanningJob, elemento);
    }
});
