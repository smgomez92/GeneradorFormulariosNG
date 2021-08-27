include("lib.prototype");

include("fitbank.evento");
include("fitbank.enlace");
include("fitbank.logger");

include("fitbank.ui.dispensadora");
include("fitbank.ui.menu");
include("fitbank.ui.calculadora");

/**
 * Namespace Teclas - Define funciones para manejo del teclado en general.
 */
var Teclas = {

    ACCIONES: {
        ayuda: {
            nombre: "ayuda",
            titulo: "Ayuda",
            evento: function() {
                c.ayudar();
            }
        },

        nueva: {
            nombre: "nueva",
            titulo: "Nueva Ventana",
            evento: function() {
                var maxWindows = 1 * (Parametros['fitbank.ui.ventana.MAX'] || 5);
                if (Entorno.ventanas.size() >= maxWindows) {
                    new Ventana({
                        titulo: "LÌmite de ventanas",
                        contenido: "Ha alcanzado el m·ximo de ventas abiertas..."
                    }).ver();
                } else {
                    window.open(document.location.href, "", "fullscreen=yes");
                }
            }
        },

        menu: {
            nombre: "menu",
            titulo: "Menu",
            evento: function() {
                c.menuRapido();
            }
        },

        dispensadora: {
            nombre: "dispensadora",
            titulo: "Dispensadora",
            evento: function() {
                Dispensadora.mostrar();
            }
        },

        manualTecnico: {
            nombre: "manual_tecnico",
            titulo: "Manual Tecnico",
            evento: function() {
                c.manualTecnico();
            }
        },

        salir: {
            nombre: "salir",
            titulo: "Alt: Cerrar Sesi√≥n",
            evento: function() {
                if (confirm("Acepta cerrar la sesi√≥n")) {
                    new Ajax.Request("proc/" + GeneralRequestTypes.CADUCAR, {
                        onSuccess: function(m) {
                            window.close();
                        }
                    });
                }
            }
        },

        recargar: {
            nombre: "recargar",
            titulo: "Recargar, Shift: Limpiar",
            evento: function() {
                if (c.revisarCambios()) {
                    return;
                }

                c.recargar();
            },
            shift: function() {
                c.limpiar();
            },
            ctrl: function() {
                c.borrarCache();
            }
        },

        calendario: {
            nombre: "calendario",
            titulo: "Ver calendario",
            evento: function() {
                // FIXME: usar scwShow. Ver Calendar.js en el proyecto
                // formas
            }
        },

        calculadora: {
            nombre: "calculadora",
            titulo: "Ver calculadora",
            evento: function() {
                Calculadora.ver();
            }
        },

        anterior: {
            nombre: "anterior",
            titulo: "Anterior",
            evento: function() {
                c.consultar(-1);
            }
        },

        consultar: {
            nombre: "consultar",
            titulo: "Consultar",
            evento: function() {
                c.consultar();
            }
        },

        siguiente: {
            nombre: "siguiente",
            titulo: "Siguiente",
            evento: function() {
                c.consultar(1);
            }
        },

        guardar: {
            nombre: "guardar",
            titulo: "Guardar",
            evento: function() {
                c.mantener();
            }
        },

        sinevento: {
            nombre: "sinevento",
            titulo: "Sin evento",
            evento: function () {
                console && console.log("No existe un evento definido para esta tecla");
            }
        }
    },

    init: function(elemento) {
        Teclas.elemento = $(elemento);

        Teclas.teclas = {
            f1: Teclas.ACCIONES.ayuda,
            f2: Teclas.ACCIONES.recargar,
            f3: Teclas.ACCIONES.sinevento,
            f4: Teclas.ACCIONES.salir,
            f5: Teclas.ACCIONES.sinevento,
            f6: Teclas.ACCIONES.calculadora,
            f7: Teclas.ACCIONES.consultar,
            f8: Teclas.ACCIONES.anterior,
            f9: Teclas.ACCIONES.siguiente,
            f10: Teclas.ACCIONES.sinevento,
            f11: Teclas.ACCIONES.menu,
            f12: Teclas.ACCIONES.guardar
        };

        Event.on(document, 'keydown', Teclas.procesar);

        $R(1, 12).each(function(i) {
            var nombre = "f" + i;
            var t = Teclas.teclas[nombre];

            if (t) {
                Teclas[nombre] = function(e) {
                    e = $E(e);

                    if (!Entorno.bloqueado() && !Entorno.lv) {
                        var funcion = t.evento;

                        if (e && e.ctrl) {
                            funcion = t.ctrl;
                        } else if (e && e.alt) {
                            funcion = t.alt;
                        } else if (e && e.shift) {
                            funcion = t.shift;
                        }

                        if (funcion) {
                            c.refrescarFoco();
                            funcion.defer();
                        }
                    }
                }

                if (t.nombre === "sinevento") {
                    return;
                }

                var img = new Element("img", {
                    src: "img/teclas/" + t.nombre + ".png",
                    alt: t.titulo
                });

                var span = new Element("span").update(nombre.toUpperCase());

                var tecla = new Element("button", {
                    className: "tecla",
                    title: t.titulo,
                    tabIndex: -1
                }).update(img).insert(span);

                tecla.on("click", function(e) {
                    Teclas[nombre](e);
                });

                Teclas.elemento.insert(tecla);
            }
        });

        var alternarTeclas = new Element("div", {
            'id': "entorno-boton-alternar-teclas"
        });

        alternarTeclas.on("click", Element.toggle.curry(Teclas.elemento));

        Element.insert(document.body, alternarTeclas);
    },

    f: function(f) {
        return function(e) {
            Teclas["f" + f](e);
        }
    },

    procesar: function(e) {
        e = $E(e);

        if (e.esFuncion()) {
            e.cancelar();
        }

        switch (e.tecla) {
        case e.F1:
            return Teclas.f1(e);

        case e.F2:
            return Teclas.f2(e);

        case e.F3:
            return Teclas.f3(e);

        case e.F4:
            return Teclas.f4(e);

        case e.F5:
            return Teclas.f5(e);

        case e.F6:
            return Teclas.f6(e);

        case e.F7:
            return Teclas.f7(e);

        case e.F8:
            return Teclas.f8(e);

        case e.F9:
            return Teclas.f9(e);

        case e.F10:
            return Teclas.f10(e);

        case e.F11:
            return Teclas.f11(e);

        case e.F12:
            return Teclas.f12(e);

        case e.BACKSPACE:
            if (Prototype.Browser.IE) {
                var el = e.elemento;
                var type = el && el.type;
                if(!el || (type != 'text' && type != 'password'
                    && type != 'file' && el.tagName != 'TEXTAREA')) {
                    e.cancelar();
                }
            }
            return false;

        default:
            return false;
        }
    },

    esconder: function(elemento) {
        Teclas.elemento = $(elemento);
        Teclas.elemento.hide();
    },

    mostrar: function(elemento) {
        Teclas.elemento = $(elemento);
        Teclas.elemento.show();
    }
};
