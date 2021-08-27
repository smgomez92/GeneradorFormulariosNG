include("lib.prototype");

/**
 * @remote
 */
Password.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        // TODO: Hacer pedido y completar esto
        return;
        json = { teclado: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0] };
        elemento.assistant = this;

        for ( var a = 1; a <= 12; a++) {
            switch (a) {
                case 10:
                    this.crearBoton(elemento, "<", -1);
                    break;

                case 12:
                    this.crearBoton(elemento, "#", null);
                    break;

                case 11:
                    this.crearBoton(elemento, json.teclado[0], 0);
                    break;

                default:
                    this.crearBoton(elemento, json.teclado[a], a);
            }
        }
    },

    /**
     * Crea un botón.
     */
    crearBoton: function(elemento, texto, numero) {
        var boton = new Element("div", {
            'class': "boton"
        }).update(texto);

        if (texto == -1) {
            boton.on("click", function() {
                elemento.value = elemento.value.substring(0,
                    elemento.value.length - 1);
            });
        } else if (Object.isNumber(texto)) {
            boton.on("click", function() {
                elemento.value += numero;
            });
        }

        $("keypad").insert(boton);
    }

});
