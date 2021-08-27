include("lib.prototype");

include("fitbank.proc.clases");

include("fitbank.ui.barra");

/**
 * Namespace Notificaciones - Define funciones para manejar las notificaciones.
 */
var Notificaciones = {
    /**
     * @private
     */
    timeout: null,

    /**
     * @private
     */
    opacidad: 0,

    /**
     * @private
     */
    ventana: null,

    /**
     * @private
     */
    nuevas: 0,

    /**
     * @private
     */
    init: function() {
        Notificaciones.elemento = Barra.agregarBoton( {
            nombre: "notificacion",
            titulo: "Notificaciones",
            onclick: function() {
                Notificaciones.consultar(true);
            }
        }).hide();

        Notificaciones.elemento.img = Notificaciones.elemento.down("img");
        Notificaciones.lista = new Element("div");
        Element.insert(document.body, Notificaciones.lista);
        Notificaciones.lista.setOpacity(0);
        Notificaciones.lista.hide();

        Notificaciones.ventana = new Ventana( {
            titulo: "Notificaciones",
            destruirAlCerrar: false,
            centrada: true
        });

        Notificaciones.consultar();
    },

    /**
     * @private
     */
    animar: function(animar) {
        if (animar) {
            Notificaciones.lista.show();
            Notificaciones.opacidad += 0.05;
        } else {
            Notificaciones.opacidad -= 0.05;
        }

        Notificaciones.lista.setOpacity(Notificaciones.opacidad);

        if (Notificaciones.opacidad < 1 && Notificaciones.opacidad > 0) {
            Notificaciones.timeout = setTimeout(function() {
                Notificaciones.animar(animar);
            }, 1);
        } else if (Notificaciones.opacidad <= 0) {
            Notificaciones.lista.hide();
            clearTimeout(Notificaciones.timeout);
        } else {
            clearTimeout(Notificaciones.timeout);
        }

        if (Notificaciones.opacidad >= 1) {
            Notificaciones.timeout = setTimeout(function() {
                Notificaciones.animar();
            }, 5000);
        }
    },

    iniciarParapadeo: function() {
        if (!Notificaciones.interval) {
            Notificaciones.interval = setInterval(function() {
                if (Notificaciones.elemento.img.src.endsWith("img/barra/notificacion.png")) {
                    Notificaciones.elemento.img.src = "img/barra/notificacionalert.png";
                } else {
                    Notificaciones.elemento.img.src = "img/barra/notificacion.png";
                }
            }, 600);
        }
    },

    finalizarParapadeo: function(notified) {
        clearInterval(Notificaciones.interval);
        Notificaciones.interval = null;

        if (notified) {
            Notificaciones.elemento.img.src = "img/barra/notificacion.png";
        }
    },

    /**
     * @private
     */
    consultar: function(pushNotification, afterNotify) {
        new Ajax.Request('proc/notif', {
            parameters: $H( {
                _contexto: "notificaciones",
                notificacion: GeneralRequestTypes.CONSULTA
            }).toQueryString(),
            onSuccess: function(response) {
                var notified = Notificaciones.notificar(response, pushNotification, afterNotify);

                if (pushNotification) {
                    Notificaciones.ventana.ver();
                    Notificaciones.finalizarParapadeo(notified);
                }
            },
            onException: rethrow
        });
    },

    /**
     * @private
     */
    notificar: function(response, pushNotification, afterNotify) {
        var notificacion = response && response.responseJSON;
        var notified = false;
        if (notificacion && notificacion.items.length > 0) {
            if (Notificaciones.nuevas != notificacion.items.length) {
                Notificaciones.animar(true);
                Notificaciones.nuevas = notificacion.items.length;
            }
            Notificaciones.elemento.img.src = "img/barra/notificacion.png";
            Notificaciones.elemento.img.alt = "Hay notificaciones";
            Notificaciones.ventana.setContenido(Notificaciones.crearForm(notificacion));
            if (!pushNotification) {
                if (Parametros['fitbank.notificaciones.AUTOMATIC_SHOW'] === "true") {
                    Notificaciones.elemento.fireDOMEvent('click');
                } else {
                    Notificaciones.iniciarParapadeo();
                }
            }

            notified = true;
        } else {
            Notificaciones.nuevas = 0;
            Notificaciones.elemento.img.src = "img/barra/sinnotificacion.png";
            Notificaciones.elemento.img.alt = "No hay notificaciones";
            Notificaciones.ventana.setContenido("No tiene notificaciones pendientes");
            Notificaciones.ventana.esconder();
            Notificaciones.finalizarParapadeo();    
            notified = false;
        }

        if (!afterNotify) {
            Notificaciones.elemento.show();
        }

        return notified;
    },

    /**
     * @private
     */
    crearForm: function(notificacion) {
        var table = new Element("table", {
            className: "tabla notificaciones"
        });

        var tr = new Element("tr");

        tr.insert(new Element("td").update("Subsistema"));
        tr.insert(new Element("td").update("Transaccion"));
        tr.insert(new Element("td").update("Mensaje"));

        table.insert(new Element("thead").insert(tr));

        var tbody = new Element("tbody");

        notificacion.items.each(function(item) {
            tbody.insert(Notificaciones.crearItem(item));
        });

        table.insert(tbody);

        var formContenedor = new Element("form");
        formContenedor.insert(table);
        if (Prototype.Browser.IE) {
            formContenedor.style.display = "inline";
        } else {
            formContenedor.style.display = "table";
        }
        return formContenedor;
    },

    /**
     * Función que crea todos los item que va a contener la tabla de
     * notificaciones
     * 
     * @private
     */
    crearItem: function(item) {
        var tr = new Element("tr");

        tr.insert(new Element("td").update(item.subsistema));
        tr.insert(new Element("td").update(item.transaccion));

        var itemlink = new Element("a", {
            href: "#"
        }).update(item.mensaje);

        tr.insert(new Element("td").update(itemlink));

        itemlink.on("click", function(e) {
            Notificaciones.eliminaItem(item);
            Entorno.contexto.cargar({
                subsistema: item.subsistema,
                transaccion: item.transaccion
            });
            Notificaciones.ventana.esconder();
            Event.stop(e);
        });

        return tr;
    },

    /**
     * Función que elimina la notificacion que se le envíe
     * 
     * @private
     */
    eliminaItem: function(item) {
        new Ajax.Request('proc/notif', {
            parameters: $H( {
                _contexto: "notificaciones",
                notificacion: GeneralRequestTypes.MANTENIMIENTO,
                item: item.registro
            }).toQueryString(),
            onSuccess: function() {
                Notificaciones.consultar(false, true);
            },
            onException: rethrow
        });
    }

};
