include("fitbank.ui.notificaciones");

include("lib.prototype");

include("fitbank.proc.clases");

include("fitbank.ui.barra");

/**
 * Namespace NotificacionesComentarios - Define funciones para manejar las NotificacionesComentarios.
 */
var NotificacionesComentarios = {
    /**
     * @private
     */
    timeout: null,
    
    /**
     * @private
     */
    boton: null,

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
    intervalo: 0,

    init: function() {
        NotificacionesComentarios.elemento = Barra.agregarBoton( {
            nombre: "notificacioncomentarios",
            titulo: "Notificaciones Comentarios",
            onclick: function() {
                NotificacionesComentarios.consultar(c, {
                    params: {}, 
                    pushNotification: true
                });
            }
        }).hide();

        NotificacionesComentarios.elemento.img = NotificacionesComentarios.elemento.down("img");
        NotificacionesComentarios.lista = new Element("div");
        Element.insert(document.body, NotificacionesComentarios.lista);
        NotificacionesComentarios.lista.setOpacity(0);
        NotificacionesComentarios.lista.hide();

        NotificacionesComentarios.ventana = new Ventana( {
            titulo: "Notificaciones Comentarios",
            destruirAlCerrar: false,
            centrada: true
        });

        NotificacionesComentarios.notificar();
    },

    /**
     * @private
     */
    animar: function(animar) {
        if (animar) {
            NotificacionesComentarios.lista.show();
            NotificacionesComentarios.opacidad += 0.05;
        } else {
            NotificacionesComentarios.opacidad -= 0.05;
        }

        NotificacionesComentarios.lista.setOpacity(NotificacionesComentarios.opacidad);

        if (NotificacionesComentarios.opacidad < 1 && NotificacionesComentarios.opacidad > 0) {
            NotificacionesComentarios.timeout = setTimeout(function() {
                NotificacionesComentarios.animar(animar);
            }, 1);
        } else if (NotificacionesComentarios.opacidad <= 0) {
            NotificacionesComentarios.lista.hide();
            clearTimeout(NotificacionesComentarios.timeout);
        } else {
            clearTimeout(NotificacionesComentarios.timeout);
        }

        if (NotificacionesComentarios.opacidad >= 1) {
            NotificacionesComentarios.timeout = setTimeout(function() {
                NotificacionesComentarios.animar();
            }, 5000);
        }
    },

    iniciarParapadeo: function() {
        if (!NotificacionesComentarios.interval) {
            NotificacionesComentarios.interval = setInterval(function() {
                if (NotificacionesComentarios.elemento.img.src.endsWith("img/barra/notificacioncomentarios.png")) {
                    NotificacionesComentarios.elemento.img.src = "img/barra/notificacioncomentariosalert.png";
                } else {
                    NotificacionesComentarios.elemento.img.src = "img/barra/notificacioncomentarios.png";
                }
            }, 600);
        }
    },

    finalizarParapadeo: function(notified) {
        clearInterval(NotificacionesComentarios.interval);
        NotificacionesComentarios.interval = null;

        if (notified) {
            NotificacionesComentarios.elemento.img.src = "img/barra/notificacioncomentarios.png";
        }
    },

    /**
     * @private
     */
    consultar: function(contexto, opciones) {
        if (Parametros['fitbank.notificacionescomentarios.ENABLED'] === "true") {
            new Ajax.Request('proc/notifcomentarios', {
                parameters: $H({
                    notificacion: GeneralRequestTypes.CONSULTA,
                    _contexto: 'notificaciones' ,
                    _contextoPadre: contexto.id || "",
                    _controlConFoco: contexto.formulario.controlConFoco,
                    _registroActivo: contexto.formulario.registroActivo
                }).merge(opciones.params).toQueryString() + "&" + Form.serialize(contexto.form),
                onSuccess: function (response) {
                    var notified = NotificacionesComentarios.notificar(response, opciones.pushNotification);

                    if (opciones.pushNotification) {
                        NotificacionesComentarios.ventana.ver();
                        NotificacionesComentarios.finalizarParapadeo(notified);
                    }
                },
                onException: rethrow
            });
        }
    },

    notificar: function(response, pushNotification) {
        var notificacion = response && response.responseJSON || null;
        var notified = false;
        if (notificacion && notificacion.items.length > 0) {
            if (NotificacionesComentarios.nuevas != notificacion.items.length) {
                NotificacionesComentarios.animar(true);
                NotificacionesComentarios.nuevas = notificacion.items.length;
            }

            NotificacionesComentarios.elemento.img.src = "img/barra/notificacioncomentarios.png";
            NotificacionesComentarios.elemento.img.alt = "Hay comentarios";
            NotificacionesComentarios.ventana.setContenido(NotificacionesComentarios.crearForm(notificacion));
            if (!pushNotification) {
                if (Parametros['fitbank.notificaciones.AUTOMATIC_SHOW'] == "true") {
                    NotificacionesComentarios.elemento.fireDOMEvent('click');
                } else {
                    NotificacionesComentarios.iniciarParapadeo();
                }
            }

            notified = true;
        } else {
            NotificacionesComentarios.nuevas = 0;
            NotificacionesComentarios.elemento.img.src = "img/barra/sinnotificacioncomentarios.png";
            NotificacionesComentarios.elemento.img.alt = "No hay comentarios";
            NotificacionesComentarios.ventana.setContenido("No tiene comentarios existentes");
            NotificacionesComentarios.ventana.esconder();
            NotificacionesComentarios.finalizarParapadeo();
            notified = false;
        }

        NotificacionesComentarios.elemento.show();
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

        tr.insert(new Element("td").update("Identificación"));
        tr.insert(new Element("td").update("Cuenta"));
        tr.insert(new Element("td").update("Mensaje"));

        table.insert(new Element("thead").insert(tr));

        var tbody = new Element("tbody");

        notificacion.items.each(function(item) {
            tbody.insert(NotificacionesComentarios.crearItem(item));
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

        tr.insert(new Element("td").update(item.identificacion));
        tr.insert(new Element("td").update(item.ccuenta));
        tr.insert(new Element("td").update(item.mensaje));

        return tr;
    }
};
