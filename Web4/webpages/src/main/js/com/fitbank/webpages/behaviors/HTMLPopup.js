include("lib.prototype");
include("fitbank.ui.ventana");
include("fitbank.util");

/**
 * @remote
 */
HTMLPopup.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var iframeHeight = this.height;
        var iframeWidth = this.width;

        if (!this.height || !this.width) {
            iframeHeight = 200;
            iframeWidth = 200;
        }

        var iframe = new Element("iframe" ,{
            name : "HTMLIframe",
            height: iframeHeight + "px",
            width: iframeWidth + "px",
            style: "background-color: white; border: 1px"
        });

        var boton = new Element("input" , {
            type: "button",
            value: this.label
        });

        elemento.hide().insert({
            after: boton
        });

        boton.on('click', (function() {  
            var ventana = new Ventana({
                titulo: "Contenido HTML",
                contenido: iframe
            });

            Util.getContentWindow(iframe).document.body.innerHTML = "";
            Util.getContentWindow(iframe).document.write(elemento.value);

            Element.fireDOMEvent.defer(elemento, "click");   

            ventana.ver();
        }).bind(this));
    }

});

