include("lib.prototype");
include("lib.onload");

include("fitbank.util");

var Registro = {

    init: function() {
        var filtros = $("filtros").down("fieldset");
        filtros.select("input, select").each(function(el) {
            el.on("change", function() {
                filtros.addClassName("change");
                Registro.consultar();
            });
        });

        Registro.consultar();
    },

    consultar: function() {
        clearTimeout(Registro.timeout);

        new Ajax.Request("proc/registro", {
            parameters: $("filtros").serialize(),
            onSuccess: function(transport) {
                //Si el registro está deshabilitado, ir a deshabilitado.html
                if (transport.responseJSON.disabled) {
                    window.location.href = "deshabilitado.html";
                    return;
                }

                var filtros = $("filtros").down("fieldset");
                var debug = $("debug");
                var mode = $("mode");
                var timeout = 1000 * (parseFloat($F("actualizar")) || 1);

                try {
                    (function() { filtros.removeClassName("change"); }).delay(0.5);
                    debug.checked = transport.responseJSON.debug;
                    mode.setValue(transport.responseJSON.mode);
                    $("registros").update("");
                    $("mensajes").update("");
                    var registros = transport.responseJSON.registros;
                    registros.each(Registro.addRegistro);
                } catch (e) {
                    Registro.error(e);
                    timeout *= 10;
                }
                Registro.timeout = setTimeout(Registro.consultar, timeout);
            },
            onError: Registro.error
        });
    },

    error: function() {
        $("registros").update("");
        $("mensajes").update(
            "Error recibiendo datos, recargue la página por favor.")
        .addClassName("error");
    },

    enlace: function(registro, nombre) {
        return new Element("td").update(new Element("a", {
            target: "_blank",
            href: "proc/registro/" + registro.nombreBase + "-" + nombre
        }).update("ver"));
    },

    addRegistro: function(registro) {
        var tr = new Element("tr");

        tr.insert(new Element("th").update(registro.secuencia));
        tr.insert(new Element("td").update(registro.fecha));
        tr.insert(new Element("td").update(registro.usuario));
        tr.insert(new Element("td").update(registro.cia));
        tr.insert(new Element("td").update(registro.contexto));
        tr.insert(new Element("td").update(registro.tran));
        tr.insert(new Element("td").update(registro.tipo));
        tr.insert(new Element("td", {
            className: registro.estado.toLowerCase()
        }).update(registro.estado));
        tr.insert(new Element("td").update(registro.thread));
        if (Util.isError(registro.codigoError)){
            tr.insert(new Element("td").update(new Element("a", {
                href: "error.html#" + registro.secuencia
            }).update(registro.codigoError)));
        } else {
            tr.insert(new Element("td").update("OK"));
        }

        tr.insert(Registro.enlace(registro, "request.txt"));
        tr.insert(Registro.enlace(registro, "entrada.xml"));
        tr.insert(Registro.enlace(registro, "salida.xml"));
        tr.insert(Registro.enlace(registro, "response.txt"));

        $("registros").insert(tr);
    }
};

addOnLoad(Registro.init);
