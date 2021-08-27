include("lib.prototype");

/**
 * @remote
 */
LongText.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$W(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        elemento.setStyle({
           overflow: "hidden" 
        });
        elemento.imagen = new Element("img", {
            'class': "asistente-icono",
            src: "img/asistentes/textolargo.png",
            width: 16,
            height: 16
        });
        elemento.insert( {
            after: elemento.imagen
        });
        elemento.on('dblclick', this.ver.bind(this, elemento));
        elemento.imagen.on("click", Form.Element.activate.curry(elemento));
        elemento.imagen.on("click", this.ver.bind(this, elemento));

        elemento.assistant = this;
    },

    ver: function(elemento) {
        var areatexto = new Element("textarea", {
            'class': elemento.className
        }).setStyle({
            width: "700px",
            height: "350px"
        }).update(elemento.value);

        if (elemento.readOnly || elemento.disabled) {
            areatexto.disable();
        }

        areatexto.on("change", function() {
            areatexto.value = areatexto.value.substring(0, elemento.maxLength);
            elemento.changeValue(areatexto.value);
        });
        areatexto.on("keypress", function() {
            if (areatexto.value.length > elemento.maxLength) {
		areatexto.value = areatexto.value.substring(0, elemento.maxLength);
            }
        });
        new Ventana({
            titulo: "Editar texto",
            contenido: areatexto
        }).ver();
        
        Form.Element.focus.defer(areatexto);
    }

});
