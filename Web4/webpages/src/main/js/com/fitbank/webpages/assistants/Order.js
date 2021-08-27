include("lib.prototype");

/**
 * @remote
 */
Order.addMethods({

    /**
     * Imagen que se muestra inicialmente en el elemento.
     */
    imagenNada: "img/asistentes/orden.png",

    /**
     * Imagen que se muestra en el elemento con orden ascendente.
     */
    imagenAsc: "img/asistentes/orden-asc.png",

    /**
     * Imagen que se muestra en el elemento con orden descendente.
     */
    imagenDesc: "img/asistentes/orden-desc.png",

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        elemento.setStyle({
            overflow: "hidden"
        });
        elemento.imagen = new Element("img", {
            'class': "asistente-icono",
            src: this.imagenNada,
            width: 16,
            height: 16
        });
        elemento.insert( {
            after: elemento.imagen
        });
        elemento.on('dblclick', this.cambiar.bind(this, elemento));
        elemento.on('keyup', function(e) {
            elemento.value = elemento.value.replace(/^(\-?\d{0,2}).*$/, '$1');
        });
        elemento.on('change', (function(e) {
            this.ordenar();
        }).bind(this));
        elemento.imagen.on("click", Form.Element.activate.curry(elemento));
        elemento.imagen.on("click", this.cambiar.bind(this, elemento));

        if (!c.vars.orden) {
            c.vars.orden = $H();
        }

        if (!c.vars.orden.get(this.aliasPrincipal)) {
            c.vars.orden.set(this.aliasPrincipal, $A());
        }

        c.vars.orden.get(this.aliasPrincipal).push(elemento);

        this.actualizarImagen(elemento);
        this.ordenar();

        elemento.assistant = this;
    },

    cambiar: function(elemento) {
        if (elemento.readOnly || elemento.disabled) {
            return;
        }

        if (Math.abs(elemento.value) != 1) {
            c.vars.orden.get(this.aliasPrincipal).each(function(e) {
                e.value = e.value * 2;
            });
            if (elemento.value != 0) {
                elemento.value = Math.abs(elemento.value) / elemento.value;
            }
        }

        elemento.value = (parseInt(elemento.value) + 2) % 3 - 1

        this.actualizarImagen(elemento);
        this.ordenar();
    },

    actualizarImagen: function(elemento) {
        if (elemento.value > 0) {
            elemento.imagen.src = this.imagenAsc;
        } else if (elemento.value < 0) {
            elemento.imagen.src = this.imagenDesc;
        } else {
            elemento.imagen.src = this.imagenNada;
        }
    },

    ordenar: function() {
        var pos = 1;

        c.vars.orden.get(this.aliasPrincipal).sortBy(function(e) {
            return Math.abs(e.value);
        }).each(function(e) {
            if (e.value != 0) {
                e.value = (Math.abs(e.value) / e.value) * pos++;
            }
        });
    }

});
