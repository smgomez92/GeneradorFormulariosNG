include("lib.prototype");
include("lib.onload");

include("fitbank.util");

include("fitbank.proc.mensajes");

/**
 * Namespace Clave - Contiene las funciones para la página de cambio de clave.
 */
var Clave = {

    /**
     * Inicializa el formulario de cambio de clave.
     * 
     * @private
     */
    init: function() {
        document.disableContextMenu();

        Clave.formulario = $("clave");

        if (!Clave.formulario) {
            return;
        }

        $("instrucciones").update(Mensajes["fitbank.clave.INSTRUCCIONES"]);
        
        Clave.formulario.action = "proc/clave";
        Clave.formulario.method = "POST";
        Clave.formulario.onsubmit = Clave.cambiar;
        Clave.formulario.focusFirstElement();
        Clave.progreso = new Element("img", {
            className: "ingreso-progreso",
            src: "img/progreso.gif"
        });
        Clave.formulario.insert( {
            after: Clave.progreso
        });
        Clave.formulario.insert(new Element("input", {
            type: "hidden",
            name: "_contexto",
            value: "sig"
        }));

        Clave.progreso.hide();

        new Ajax.Request("proc/names", {
            parameters: {
                _contexto: "sig",
                cambio: true
            },
            onComplete: Clave.initNames
        });
    },

    /**
     * Función que inicializa las teclas.
     */
    initNames: function(transport) {
        var json = transport.responseJSON;

        Clave.clave = Clave.formulario.select("input[type=password]")[0];
        Clave.clave2 = Clave.formulario.select("input[type=password]")[1];

        var labels = Clave.formulario.select("label");

        Clave.clave.name = json.nameClave;
        labels[0].setAttribute("for", json.nameClave);

        Clave.clave2.name = json.nameClave2;
        labels[1].setAttribute("for", json.nameClave2);
    },

    /**
     * Ejecuta el proceso de Clave.
     * 
     * @private
     */
    cambiar: function() {
        (function() {
            Clave.formulario.hide();
            Clave.progreso.show();
            Clave.clave.value = "";
            Clave.clave2.value = "";
        }).defer();

        return true;
    }

};

addOnLoad(Clave.init);
