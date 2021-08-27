include("lib.prototype");

/**
 * @remote
 */
Report.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var label = $(elemento.getAttribute("labelid"));

        if (label) {
            elemento = label;
            elemento.href = "#";
            elemento.onclick = "return false;";
        }

        elemento.on('click', (function() {
            if (!this.fireAlways && (elemento.readOnly || elemento.disabled)) {
                return;
            }

            Reporte.mostrar(this.name, (this.directDownload ? "1" : "0"), this.downloadName);
        }).bind(this));
    }

});
