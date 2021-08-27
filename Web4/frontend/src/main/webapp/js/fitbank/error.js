include("lib.prototype");
include("lib.onload");

var Error = {

    init: function() {
        document.disableContextMenu();

        Error.consultar();
    },

    consultar: function() {
        new Ajax.Request("proc/registro", {
            parameters: {
                secuencia: document.location.hash.substring(1),
                numero: 1
            },
            onComplete: function(transport) {
                var registros = transport.responseJSON.registros;
                Plantilla.aplicar(registros[0]);
            }
        });
    }

};

addOnLoad(Error.init);
