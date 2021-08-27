include("lib.prototype");
include("lib.onload");
include("lib.css3hacks");

include("fitbank.util");

try {
    include("fitbank.proc.clases");
    include("fitbank.proc.mensajes");
    include("fitbank.proc.parametros");
} catch (e) {
    addOnLoad(function() {
        include("fitbank.ui.ventana");
        new Ventana({
            titulo: "Prueba",
            contenido: "Error fatal al cargar los javascripts: " + e
        }).ver();
    });
}

include("fitbank.contexto");
include("fitbank.evento");
include("fitbank.logger");
include("fitbank.validar");

include("fitbank.ui.barra");
include("fitbank.ui.estatus");
include("fitbank.ui.listaformularios");
include("fitbank.ui.menu");
include("fitbank.ui.notificaciones");
include("fitbank.ui.notificacionescomentarios");
include("fitbank.ui.teclas");
include("fitbank.ui.tooltips");

/**
 * Variable c - Usado en los formularios para acceder a elementos en el contexto
 * actual. FIXME: debe setearse antes de ejecutar calculos, js inicial, o
 * cualquier evento de los elementos.
 */
var c = null;

/**
 * Namespace Entorno - Contiene funciones para manejar el entorno.
 */
var Entorno = {

    /**
     * @private
     */
    activo: false,

    /**
     * @private
     */
    init: function() {
        if (window.opener && window.opener.Entorno) {
            Entorno.ventanas = window.opener.Entorno.ventanas;
        } else {
            Entorno.ventanas = $H();
        }
        Entorno.id = Math.random();
        Entorno.ventanas.set(Entorno.id, window);
        Event.on(window, "unload", function() {
            Entorno.ventanas.unset(Entorno.id);
        });

        Estatus.init("entorno-estatus-contenido");
        ListaFormularios.init("entorno-lista-formularios");
        Entorno.contexto = new Contexto("entorno-formulario");
        c = Entorno.contexto;

        Barra.init("entorno-barra-botones");
        Entorno.initInformacion.tryCatch();

        if (Parametros['fitbank.notificaciones.ENABLED'] === "true") {
            Notificaciones.init.tryCatch();
        }

        if (Parametros['fitbank.notificacionescomentarios.ENABLED'] === "true") {
            NotificacionesComentarios.init.tryCatch();
        }

        Menu.init.tryCatch();

        Element.insert(document.body, new Element("iframe", {
            name: "entorno-iframe-ajax"
        }).hide());
    },

    initInformacion: function() {
        Entorno.consultarInformacion($("entorno-informacion"));
    },

    consultarInformacion: function(elemento) {
        Estatus.enviarLog();
        var notificationDelay = 1 * (Parametros['fitbank.entorno.INFO_DELAY'] || 300000);

        new Ajax.Request("proc/" + GeneralRequestTypes.INF, {
            parameters: {
                _user: Entorno.contexto.user,
                _sessionId: Entorno.contexto.sessionId
            },
            onSuccess: function(response) {
                var inf = response.responseJSON;
                if (inf && inf.nouser) {
                    setTimeout(Entorno.consultarInformacion.curry(elemento), notificationDelay);
                    return;
                }

                if (!inf || !inf.valores) {
                    console.log('Sin respuesta del servidor');
                    return Entorno.respuestaCaducar(response);
                }

                elemento.update("");

                var currentColumn = 1;
                var maxColumns = 4;

                var div = new Element("div");
                var table = new Element("table");
                var tbody = new Element("tbody");
                var tr1 = new Element("tr");
                var tr2 = new Element("tr");

                tbody.insert(tr1);
                tbody.insert(tr2);
                table.insert(tbody);
                div.insert(table);
                elemento.insert(div);

                inf.valores.each(function(valor) {
                    var td1 = new Element("td");
                    var td2 = new Element("td");

                    if (currentColumn <= maxColumns) {
                        tr1.insert(td1);
                        tr1.insert(td2);
                    } else {                        
                        tr2.insert(td1);
                        tr2.insert(td2);
                    }

                    td1.insert(new Element("label").update(valor.nombre));
                    td2.insert(new Element("input", {
                        value: valor.valor,
                        readOnly: "readonly",
                        tabIndex: -1,
                        style: "width: " + valor.longitud + "px"
                    }));

                    currentColumn++;
                });

                var divVersion = $('entorno-barra-version');
                divVersion.update("Versi&oacute;n: " + inf.version);
                inf.version && divVersion.show() || divVersion.hide();

                setTimeout(Entorno.consultarInformacion.curry(elemento), notificationDelay);
            },
            onFailure: function() {
                Entorno.respuestaCaducar();
                window.onbeforeunload = null;
                document.location.href = "error.html";
            },
            onError: function(response) {
                Entorno.respuestaCaducar(response);
            },
            onException: function(e) {
                Entorno.respuestaCaducar({
                    mensajeUsuario: e.message,
                    stackTrace: e.stackTrace
                });
            }
        });
    },

    /**
     * @private
     */
    activar: function() {
        var pt = $("entorno-pt");
        var tran = $("entorno-transaccion");

        if (Entorno.activo) {
            pt.activate();
            return;
        }
        Entorno.activo = true;

        Teclas.init("entorno-teclas");

        var enter = function(e) {
            e = $E(e);

            if (e.tecla == e.ENTER) {
                Entorno.contexto.cargar({
                    st: pt.value
                });
                e.elemento.blur();
            }
        };

        var img = new Element("img", {
            className: "entono-pt-activar",
            src: "img/activar.png",
            alt: "activar",
            title: "activar"
        });

        pt.insert( {
            after: img
        });
        tran.show();

        img.on("click", function() {
            Entorno.contexto.cargar({
                st: pt.value
            });
        });

        pt.on("keypress", enter);
        pt.on("keyup", function(e) {
            e = $E(e);

            if (pt.value.length >= 2 && e.tecla != e.BACKSPACE) {
                var value = pt.value.replace(/[^\d]/g, "");
                pt.value = value.substring(0, 2) + "-" + value.substring(2);
            } else if (pt.value.length == 2 && e.tecla == e.BACKSPACE) {
                pt.value = pt.value.substring(0, 1);
            }

            if (pt.value.length > 7) {
                pt.value = pt.value.substring(0, 7);
            }
        });

        if (document.location.hash) {
            Entorno.contexto.cargar({
                st: document.location.hash.split("/")[1]
            });
        } else {
            pt.activate();
        }
    },

    /**
     * Determina si hay procesos activos y está bloqueado el estatus.
     */
    bloqueado: function() {
        return !Estatus.activo()
         || (Entorno.contexto && Entorno.contexto.confirmandoRecargar());
    },

    /**
     * FunciÃ³n que se encarga de cerrar la sesiÃ³n
     * 
     * @param
     */
    caducar: function(e) {
        if (Entorno.ventanas.size() > 1) {
            return;
        }

        new Ventana({
            titulo: "Caducar",
            contenido: "Caducando la sesion, por favor espere..."
        }).ver();

        Estatus.enviarLog(true);

        new Ajax.Request("proc/" + GeneralRequestTypes.CADUCAR, {
            asynchronous: false,
            onComplete: function(response) {
                Logger.log(response.responseText);
            }
        });
    },

    respuestaCaducar: function(e) {
        var res = e && e.responseJSON || e && e.mensajeUsuario || {
                    mensajeUsuario: "La sesi&oacute;n web se ha perdido",
                    stackTrace: "Su sesi&oacute;n web se ha perdido, posiblemente por" 
                                 + " limpieza de la cach&eacute; del explorador o" 
                                 + " se ha perdido la conexi&oacute;n con el servidor."
                };
        alert("Error:\n\n" + (res.mensajeUsuario || res.mensaje) + "\n\nStackTrace:\n\n" + res.stackTrace);
        caducar();
    },

    cambiarCursor: function(esperando) {
        if (esperando) {
            document.body.addClassName('bloqueado');
        } else {
            document.body.removeClassName('bloqueado');
        }
    }

};

document.onhelp = function() {
    event.cancelBubble = true;
    event.returnValue = false;
};

//Corrección temporal para el bug de chrome #45465 y #974:
//Backspace hace que se vaya a la página anterior.
document.observe('keydown', function (event) {
    var stop = false;
    if (event.keyCode === Event.KEY_BACKSPACE) {
        var d = Event.element(event);
        var tag = d.tagName && d.tagName.toUpperCase();
        if (tag == 'BODY') {
            stop = true;
        } else {
            var type = d.type && d.type.toUpperCase();
            if ((tag === 'INPUT' && (type === 'TEXT' || type === 'PASSWORD'))
                || tag === 'TEXTAREA') {
                stop = d.readOnly || d.disabled;
            } else {
                stop = true;
            }
        }

        if (stop) {
            Event.stop(event);
        }
    }
});

addOnLoad(Entorno.init);
