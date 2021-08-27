include("lib.prototype");
include("lib.onload");
include("lib.css3hacks");

include("fitbank.evento");
include("fitbank.util");

var Plantilla = {

    init: function() {
        var href = document.location.href;
        var referrer = document.referrer;

        var partes = href.split("?");
        if (partes.length > 1) {
            Plantilla.aplicar(partes[1].toQueryParams());
        }

        if (window.opener) {
            var cerrar = new Element("button").update("Cerrar");
            cerrar.on("click", Plantilla.cerrarVentana);
            Element.insert(document.body, cerrar);
        }

        if (document.location.hash == "#cerrar") {
            window.close.bind(window).delay(3);
            return;
        }

        if (history.length > 0 && !referrer.match("clave.html")
                && !href.match("(ingreso|clave|caducidad|navegador|deshabilitado).html")) {
            var regresar = new Element("button").update("Regresar");
            regresar.on("click", Plantilla.regresar);
            Element.insert(document.body, regresar);
        }

        if (referrer.match("error.html")) {
            var reportar = new Element("button", { disabled: true }).update("Reportar");
            reportar.on("click", Plantilla.reportar);
            Element.insert(document.body, reportar);
        }

        if (!referrer.match("ingreso.html") && !href.match("(navegador|deshabilitado).html")) {
            var ingreso = new Element("button").update("Ingresar");
            ingreso.on("click", Plantilla.ingreso);
            Element.insert(document.body, ingreso);
        }

        $$("button")[0].focus();
    },

    aplicar: function(valores) {
        $H(valores).each(function(a) {
            if ($(a.key)){
                $(a.key).update(a.value);
            }
        });
    },

    regresar: function(e) {
        $E(e).cancelar();
        history.back();
    },

    reportar: function(e) {
        // TODO: implementar
        alert("No implementado!");
    },

    ingreso: function(e) {
        document.location.href = "ingreso.html";
    },

    cerrarVentana: function(e) {
        $E(e).cancelar();
        window.close();
    }

}

addOnLoad(Plantilla.init);
