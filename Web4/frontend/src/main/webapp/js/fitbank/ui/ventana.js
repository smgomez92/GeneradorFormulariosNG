include("lib.prototype");
include("lib.onload");

include("fitbank.evento");
include("fitbank.util");

/**
 * Clase Ventana - Representa una ventana con cualquier tipo de contenido.
 */
var Ventana = Class.create({

    /**
     * TÃ­tulo de la ventana.
     */
    titulo: null,

    /**
     * Contenedor del título de la ventana.
     */
    divTitulo: null,

    /**
     * Elemento dentro del que va a crear la ventana si se lo especifica.
     */
    elementoPadre: null,

    /**
     * Elemento junto al que va a crear la ventana si se lo especifica.
     */
    elemento: null,

    /**
     * PosiciÃ³n en x de la ventana.
     */
    x: null,

    /**
     * PosiciÃ³n en y de la ventana.
     */
    y: null,

    /**
     * Ancho de la ventana.
     */
    w: null,

    /**
     * Alto de la ventana.
     */
    h: null,

    /**
     * CondiciÃ³n de centrado de la ventana.
     */
    centrada: true,

    /**
     * Contenido de la ventana.
     */
    contenido: null,

    /**
     * Ver fondo oscuro.
     */
    verFondo: true,

    /**
     * Indica si se debe destruir al cerrar.
     */
    destruirAlCerrar: true,

    /**
     * @private
     */
    table: null,

    /**
     * @private
     */
    barra: null,

    /**
     * @private
     */
    onVer: function() {
    },

    /**
     * @private
     */
    onCerrar: function() {
    },

    /**
     * @private
     */
    initialize: function(parametros) {
        this.verFondo = Parametros["fitbank.ui.ventana.fondo.ENABLED"] == "true";
        Object.extend(this, parametros);

        if (!this.elementoPadre) {
            this.elementoPadre = document.body;
        } else {
            this.elementoPadre = $(this.elementoPadre);
        }

        if (this.elemento) {
            this.elemento = $(this.elemento);
            this.centrada = false;
        }

        this.crear();
        
        this.insertar();
    },

    /**
     * @private
     */
    crear: function() {
        this.div = new Element('div', {
            className: "ventana"
        }).hide();

        var x = function(n) {
            return n + (Object.isNumber(n) ? 'px' : '');
        };

        if (this.x) {
            this.div.style.left = x(this.x);
        }

        if (this.y) {
            this.div.style.top = x(this.y);
        }

        if (this.titulo) {
            var divTitulo = new Element("div", {
                className: "titulo"
            });
            this.div.insert(divTitulo);

            var cerrar = new Element('img', {
                src: "img/cerrar.gif"
            });
            divTitulo.update(cerrar).insert(
                    new Element("span").update(this.titulo));

            cerrar.on('click', this.cerrar.bind(this));

            if (Parametros['fitbank.ui.ventana.MAKE_MOVABLE'] == "true") {
                divTitulo.on('mousedown', (function(e) {
                    e = $E(e);

                    var initX = e.X;
                    var initY = e.Y;

                    var divx = this.div.style.left;
                    var divy = this.div.style.top;

                    var divLeft = divx.substr(0, divx.indexOf('px')) * 1;
                    var divTop = divy.substr(0, divy.indexOf('px')) * 1;

                    var divNewLeft = initX - divLeft;
                    var divNewTop = initY - divTop;

                    Ventana.moveHandler = Event.on(document.body, 'mousemove', (function(e) {
                        e = $E(e);

                        this.div.style.left = (e.X - divNewLeft) + 'px';
                        this.div.style.top = (e.Y - divNewTop) + 'px';
                    }).bind(this));
                }).bind(this));
            }

            divTitulo.on("dblclick", function(e) {
                this.divContenido.toggle();
            }.bind(this));

            this.divTitulo = divTitulo;
        }

        this.divContenido = new Element("div", {
            className: "contenido"
        });
        this.div.insert(this.divContenido);

        if (this.w) {
            this.divContenido.style.width = x(this.w);
        }

        if (this.h) {
            this.divContenido.style.height = x(this.h);
        }

        if (this.contenido) {
            this.setContenido(this.contenido);
        }
    },

    /**
     * Inserta la ventana en el documento en la posicion necesitada.
     * 
     * @private
     */
    setContenido: function(contenido) {
        if (typeof contenido == "string") {
            contenido = new Element("span").update(contenido);
        }
        this.contenido = contenido;
        this.divContenido.update(this.contenido);
    },

    /**
     * Inserta la ventana en el documento en la posicion necesitada.
     * 
     * @private
     */
    insertar: function() {
        if (this.elemento) {
            Element.insert(this.elemento, {
                after: this.div
            });
        } else {
            Element.insert(this.elementoPadre, this.div);
        }
    },

    /**
     * Muestra la ventana.
     */
    ver: function() {
        this.insertar();

        if (this.verFondo) {
            Ventana.fondo.show();
        }
        Ventana.abiertas++;
        this.div.show();

        var maxTamano = document.viewport.getHeight() - 10;
        if (this.div.getHeight() > maxTamano) {
            this.div.setStyle({
                height: maxTamano + "px"
            });

            this.divContenido.setStyle({
                height: (maxTamano - this.divTitulo.getHeight() - 20) + "px"
            });
        }

        if (this.centrada) {
            this.div.style.position = "absolute";
            if (this.contenido.getWidth() && !this.w) {
                this.div.setStyle({
                    width: (this.contenido.getWidth() + 20) + "px"
                });
            }
            this.div.center();
        } else {
            var parent = this.div.getOffsetParent();
            this.div.style.position = "absolute";
            this.div.setStyle( {
                top: (parent.getHeight() - this.div.getHeight() - 65) + "px",
                left: "0%"
            });
        }

        this.visible = true;
        this.onVer();
    },

    /**
     * Esconde la ventana.
     */
    esconder: function() {
        if (this.visible) {
            Ventana.abiertas--;
            if (!Ventana.abiertas) {
                Ventana.fondo.hide();
            }
            this.div.hide();
            this.visible = false;
        }
    },

    /**
     * Cierra la ventana.
     */
    cerrar: function() {
        if (this.onCerrar() !== false) {
            this.esconder();
            if (this.destruirAlCerrar) {
                this.destruir();
            }
        }
    },

    /**
     * Destruye la ventana.
     */
    destruir: function() {
        this.div.remove();
    }

});

addOnLoad(function() {
    Ventana.fondo = new Element('div', {
        className: "ventana-fondo"
    });
    document.body.appendChild(Ventana.fondo);
    Ventana.fondo.setOpacity(0.8);
    Ventana.fondo.hide();

    Ventana.abiertas = 0;

    Event.on(document.body, 'mouseup', function(e) {
        if (Ventana.moveHandler) {
            Ventana.moveHandler.stop();
        }
    });
});
