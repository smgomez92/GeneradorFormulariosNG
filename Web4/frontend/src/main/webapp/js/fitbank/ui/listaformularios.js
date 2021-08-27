include("lib.prototype");

var ListaFormularios = {

    init: function(elemento) {
        this.elemento = $(elemento);

        // ANTERIOR
        this.botonAnterior = new Element("button", {
            className: "boton"
        }).update(new Element("img", {
            src: "img/formularios/anterior.png"
        })).hide();
        this.elemento.insert(this.botonAnterior);

        this.botonAnterior.on("click", (function() {
            c.cargar({
                st: this.navigation.prev.uri,
                campos: this.getValues(this.navigation.values),
                action: "PREV",
                fields: this.navigation.fields
            });
            this.cargar();
        }).bind(this));

        // SIGUIENTE
        this.botonSiguiente = new Element("button", {
            className: "boton"
        }).update(new Element("img", {
            src: "img/formularios/siguiente.png"
        })).hide();
        this.elemento.insert(this.botonSiguiente);

        this.botonSiguiente.on("click", (function() {
            c.cargar({
                st: this.navigation.next.uri,
                campos: this.getValues(this.navigation.values),
                action: "NEXT",
                fields: this.navigation.fields
            });
            this.cargar();
        }).bind(this));
    },

    getValues: function(values) {
        var newValues = $H(values);

        newValues.each(function(value) {
            newValues.set(value.key, value.key);
        });

        return Link.getValues(newValues, 0, values);
    },

    /**
     * Carga la información de navegación.
     *
     * @param navigation Información del navegación
     */
    cargar: function(navigation) {
        this.navigation = navigation || {};

        if (this.navigation.prev) {
            this.botonAnterior.show();
        } else {
            this.botonAnterior.hide();
        }

        if (this.navigation.next) {
            this.botonSiguiente.show();
        } else {
            this.botonSiguiente.hide();
        }
    }

};
