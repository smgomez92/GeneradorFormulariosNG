include("lib.prototype");

include("fitbank.ui.ventana");

/**
 * @remote
 */
Popup.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var contenido = c.$$(".tab-" + this.tab)[0];

        if (!contenido) return;

        elemento.on('click', (function() {
            if (!this.fireAlways && (elemento.readOnly || elemento.disabled)) {
                return;
            }

            contenido.style.display = "inline";

            elemento.ventana = new Ventana({
                titulo: this.titulo,
                contenido: contenido,
                elementoPadre: c.form,
                destruirAlCerrar: false
            });

            elemento.ventana.ver();
        }).bind(this));
    }

});
