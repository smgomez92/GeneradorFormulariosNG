include("lib.prototype");

/**
 * Inicializa el evento si no está inicializado y lo devuelve.
 */
$E = function(e) {
    // Si está extendido, simplemente devolverlo
    if (window.event && window.event.inicializado) {
        return window.event.evento;
    } else if (e && e.inicializado) {
        return e.evento;
    }

    // Si se pasó un objeto tipo Evento usar ese.
    var evento = e && e.evento ? e : e && new Evento(e);
    
    if (evento) {
        evento.evento.evento = evento;
    }

    return evento;
};

/**
 * Clase Evento - Clase que contiene los manejadores de eventos disparados desde
 * los elementos. Se recomienda usar mejor el método $E en lugar de esta clase
 * directamente.
 */
var Evento = Class.create( {

    /**
     * Es el evento en si
     */
    evento: null,

    /**
     * Contiene la tecla si se apalastó una tecla
     */
    tecla: null,

    /**
     * Verdadero si se aplastó alt
     */
    alt: null,

    /**
     * Verdadero si se aplastó ctrl
     */
    ctrl: null,

    /**
     * Verdadero si se aplastó shift
     */
    shift: null,

    /**
     * Contiene el caracter correspondiente a la tecla
     */
    caracter: null,

    /**
     * Contiene el elemento que lanzó el evento
     */
    elemento: null,

    /**
     * Contiene la coordenada en x del elemento que lanzó el evento
     */
    x: null,

    /**
     * Contiene la coordenada en y del elemento que lanzó el evento
     */
    y: null,

    /**
     * @private
     */
    initialize: function(e, debug) {
        // capturar el evento para todos los navegadores
        // capturar tecla del evento
        if (window.event) {
            this.evento = window.event; // IE
            this.tecla = window.event.keyCode;
        } else {
            this.evento = e;
            this.tecla = e.which;
        }

        this.initTeclas();

        switch (this.tecla) {
        case this.SLASH:
            this.caracter = '/';
            break;
        case this.DOT:
            this.caracter = '.';
            break;
        case this.DASH:
            this.caracter = '-';
            break;
        case this.BACKSLASH:
            this.caracter = '\\';
            break;
        default:
            if (!this.esEspecial()) {
                this.caracter = String.fromCharCode(this.tecla);
            } else {
                this.caracter = 13;
            }
            break;
        }

        this.alt = this.evento.altKey;
        this.ctrl = this.evento.ctrlKey;
        this.shift = this.evento.shiftKey;

        // capturar posición del evento
        if (this.evento.layerX && this.evento.layerY) {
            this.x = this.evento.layerX;
            this.y = this.evento.layerY;
        } else {
            this.x = this.evento.offsetX;
            this.y = this.evento.offsetY;
        }

        if (window.scrollX && window.scrollY) {
            this.X = this.evento.clientX + window.scrollX;
            this.Y = this.evento.clientY + window.scrollY;
        } else {
            this.X = this.evento.clientX + document.documentElement.scrollLeft
                    + document.body.scrollLeft;
            this.Y = this.evento.clientY + document.documentElement.scrollTop
                    + document.body.scrollTop;
        }

        // capturar origen del evento
        if (this.evento.srcElement) {
            this.elemento = this.evento.srcElement;
        } else if (this.evento.target) {
            this.elemento = this.evento.target;
        } else {
            Logger.warning("No hay elemento que genere el evento!!!!");
        }

        if (debug) {
            Logger.debug(this.toString());
        }
    },

    toString: function() {
        var string = '';

        string += 'Evento\n';
        string += 'evento:' + this.evento + '\n';
        string += 'tecla:' + this.tecla + '\n';
        string += 'caracter:' + this.caracter + '\n';
        string += 'alt:' + this.alt + '\n';
        string += 'ctrl:' + this.ctrl + '\n';
        string += 'shift:' + this.shift + '\n';
        string += 'elemento:' + this.elemento + '\n';
        string += 'x:' + this.x + '\n';
        string += 'y:' + this.y + '\n';
        string += 'X:' + this.X + '\n';
        string += 'Y:' + this.Y + '\n';

        return string;
    },

    /**
     * Cancela la propagación del evento.
     * 
     * @param (Event)
     *            e Evento pasado por el browser.
     */
    cancelar: function(e) {
        if (!e || !this.esEspecial()) {
            try {
                event.keyCode = 0;
            } catch (e) {
            }

            try {
                event.returnValue = false;
            } catch (e) {
            }

            Event.stop(this.evento);
        }
    },

    /**
     * Verifica si una tecla especial fue presionada.
     * 
     * @return (Boolean) Verdadero si una tecla especial fue presionada
     */
    esEspecial: function() {
        return !this.tecla || (this.tecla == this.TAB)
                || (this.tecla == this.BACKSPACE) || (this.tecla == this.ENTER)
                || (this.tecla == this.FIN) || (this.tecla == this.INICIO)
                || (this.tecla == this.IZQUIERDA)
                || (this.tecla == this.ARRIBA) || (this.tecla == this.DERECHA)
                || (this.tecla == this.ABAJO) || (this.tecla == this.ESC)
                || (this.tecla == this.SUPR);
    },

    /**
     * Verifica si una tecla especial fue presionada.
     * 
     * @return (Boolean) Verdadero si una tecla especial fue presionada
     */
    esIngreso: function() {
        return !this.esEspecial() || (this.tecla == this.BACKSPACE)
                || (this.tecla == this.SUPR);
    },

    esFuncion: function() {
        return !this.tecla || (this.F1 == this.tecla)
                || (this.F2 == this.tecla) || (this.F3 == this.tecla)
                || (this.F4 == this.tecla) || (this.F5 == this.tecla)
                || (this.F6 == this.tecla) || (this.F7 == this.tecla)
                || (this.F8 == this.tecla) || (this.F9 == this.tecla)
                || (this.F10 == this.tecla) || (this.F11 == this.tecla)
                || (this.F12 == this.tecla);
    },

    /**
     * Encuentra la longitud de la selección en el campo donde se disparó el
     * evento.
     * 
     * @return (int) La longitud de la selección
     */
    getSelLength: function() {
        if (document.getSelection) {
            return (document.getSelection() + '').length;
        } else if (this.evento.elemento
                && Object.isNumber(this.evento.elemento.selectionStart)) {
            return this.evento.elemento.selectionEnd
                    - this.evento.elemento.selectionStart;
        } else if (window.getSelection) {
            return (window.getSelection() + '').length;
        } else if (document.selection) {
            return (document.selection.createRange().text + '').length;
        } else {
            return -1;
        }
    },

    /**
     * @private
     */
    initTeclas: function() {
        this.TAB = 9;
        this.BACKSPACE = 8;
        this.ENTER = 13;
        this.SHIFT = 16;
        this.CTRL = 17;
        this.ESC = 27;
        this.ESPACIO = 32;
        this.PGUP = 33;
        this.PGDOWN = 34;
        this.FIN = 35;
        this.INICIO = 36;
        this.IZQUIERDA = 37;
        this.ARRIBA = 38;
        this.DERECHA = 39;
        this.ABAJO = 40;
        this.INS = 45;
        this.SUPR = 46;
        this.PUNTO = 190;
        this.DECIMAL = 110;
        this.F1 = 112;
        this.F2 = 113;
        this.F3 = 114;
        this.F4 = 115;
        this.F5 = 116;
        this.F6 = 117;
        this.F7 = 118;
        this.F8 = 119;
        this.F9 = 120;
        this.F10 = 121;
        this.F11 = 122;
        this.F12 = 123;
        this.DOT = 190;
        this.SLASH = 191;
        if (Prototype.Browser.IE) {
            this.DASH = 189;
        } else {
            this.DASH = 109;
        }
        this.BACKSLASH = 220;
    }

});
