include("lib.prototype");

/**
 * Clase que crea el globo de texto y guarda las referencias a los manejadores
 * de eventos agregados al objeto para poder quitarlos después.
 *
 * @author Fitbank RB
 */
var ManejadorTooltip = Class.create({

    /**
     * Elemento al que se asocia el tooltip.
     */
    _elemento: null,

    /**
     * El tooltip en sí: la referencia al globo de texto
     */
    _globo: null,

    /**
     * Referencia a la div que tiene el mensaje
     */
    _divTexto: null,

    /**
     * Manejadores de eventos
     */
    _manejadores: $A(),

    /**
     * Guarda el temporizador para actualizar la posición del elemento
     */
    _temporizador: null,

    initialize: function(elemento, mensaje) {
        this._elemento = elemento;

        this._globo = new Element('div', {
            className: 'globo-tooltip'
        }).hide();
        elemento.insert({
            before: this._globo
        });

        this._divTexto = new Element('div', {
            className: 'tooltip-superior fondoGlobo'
        });
        this._globo.insert(this._divTexto);
        this._mensaje = mensaje;

        var divInferior = new Element('div', {
            className: 'tooltip-inferior fondoGlobo'
        });
        this._globo.insert(divInferior);

        this._manejadores.push(elemento.on("focus", this.mostrar.bind(this)));
        this._manejadores.push(elemento.on("blur", this.esconder.bind(this)));
    },

    _updateMensaje: function() {
        var mensaje = (this._mensaje || "") + (Validar.getMessage(this._elemento) || "");

        if (mensaje) {
            this._divTexto.update(mensaje);
        } else {
            this._divTexto.update(Mensajes["fitbank.ui.tooltips.MENSAJE_PREDETERMINADO"]);
        }
    },

    mostrar: function() {
        if (Tooltip._visible == this._elemento) {
            return;
        }

        if (Tooltip._visible && Tooltip._visible.tooltip) {
            Tooltip._visible.tooltip.esconder();
        }

        Tooltip._visible = this._elemento;

        this._updateMensaje();
        this._posicionar();
        this._globo.show();

        clearInterval(this._temporizador);

        this._temporizador = setInterval((function(){
            if (!this._globo.visible()) {
                clearInterval(this._temporizador);
                this._temporizador = null;
                return;
            }

            this._posicionar();
        }).bind(this), 200);
    },

    esconder: function() {
        if (Tooltip._visible == this._elemento) {
            Tooltip._visible = null;
        }

        clearInterval(this._temporizador);

        this._globo.hide();
    },

    /**
     * Quita los manejadores de eventos del elemento, y remueve el globo del
     * DOM para liberar recursos.
     */
    desechar: function() {
        this.esconder();
        this._manejadores.invoke("stop");
        this._globo.remove();
    },

    _posicionar: function() {
        this._globo.removeClassName('tooltip-derecha');
        this._globo.removeClassName('tooltip-izquierda');

        var despl = this._elemento.measure('left') - this._globo.getWidth();
        if (despl < 0) {
            this._globo.addClassName('tooltip-izquierda');
            despl = this._elemento.getWidth();
        } else {
            this._globo.addClassName('tooltip-derecha');
            despl = -this._globo.getWidth();
        }

        this._globo.clonePosition(this._elemento, {
            setLeft: true,
            setTop: true,
            setWidth: false,
            setHeight: false,
            offsetLeft: despl,
            offsetTop: -17
        });
    }

});

/**
 * Funciones para crear un tooltip asociado a un elemento dentro del formulario.
 * El tooltip es un globo de texto que se muestra junto al elemento asociado
 * cuando se crea y se enfoca el elemento, y desaparece cuando se sale de él.
 *
 * @author: Fitbank RB
 */
var Tooltip = {

    /**
     * Tooltip visible actualmente.
     */
    _visible: null,

    /**
     * Muestra un tooltip sobre el elemento indicado.
     */
    mostrar: function(elemento, mensaje) {
        if (elemento.widget) {
            elemento = elemento.widget;
        }

        if (!elemento || Parametros["fitbank.ui.tooltips.ENABLED"] == "false") {
            return;
        }

        if (!elemento.tooltip) {
            elemento.tooltip = new ManejadorTooltip(elemento, mensaje);
        } else {
            elemento.tooltip._mensaje = mensaje;
        }

        if (elemento.focused) {
            elemento.tooltip.mostrar();
        }
    },

    /**
     * Quita el tooltip asociado a un elemento, deregistrando los manejadores de eventos
     * y quitando el globo de texto del DOM. Se recomienda llamar a esta función en lugar
     * de invocar directamente el método desechar del tooltip.
     */
    quitar: function(elemento) {
        if (elemento.tooltip) {
            elemento.tooltip.desechar();
            elemento.tooltip = null;
        }
    }

};
