include("lib.prototype");
include("lib.shiftzoom.shiftzoom");

include("fitbank.ui.ventana");

/**
 * @remote
 */
ImagePopup.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {
        var imgForm = $(elemento.getAttribute("imageid"));

        if (imgForm.controls) {
            var fullScreen = new Element("button").update("^");
            imgForm.controls.insert(fullScreen);
            fullScreen.on("click", this.show.bind(this, elemento, imgForm));
        }

        imgForm.on('click', this.show.bind(this, elemento, imgForm));
    },

    show: function(elemento, originalImage) {
        var dimensions = originalImage.getOriginalDimensions();

        if (!this.fireAlways && (dimensions.width <= 1 && dimensions.width <= 1)) {
            return;
        }

        if (!this.height || !this.width) {
            dimensions = originalImage.getOriginalDimensions();
        } else {
            dimensions = {
                width: this.width,
                height: this.height
            };
        }

        // Ajustar que la imagen no se salga de la pantalla
        var hPantalla = document.viewport.getHeight();
        var wPantalla = document.viewport.getWidth();

        var aImg = dimensions.height / dimensions.width;
        var aPan = hPantalla / wPantalla;

        if(dimensions.height > hPantalla || dimensions.width > wPantalla){
            if (aImg > aPan) {
                dimensions.height = hPantalla - 50;
                dimensions.width = dimensions.height / aImg;
            } else {
                dimensions.width = wPantalla - 50;
                dimensions.height = dimensions.width * aImg;
            }
        }

        var div = new Element("div");

        var divImagen = new Element("div", {
            className: "imagen-container"
        }).setStyle({
            width: dimensions.width + "px",
            height: dimensions.height + "px"
        });
        div.insert(divImagen);

        var imagen = new Element("img", {
            src: originalImage.src,
            width: dimensions.width,
            height: dimensions.height
        });
        divImagen.insert(imagen);

        if (originalImage.assistant) {
            div.insert(originalImage.assistant.createControls(elemento,
                    "image-popup-buttons", imagen), true);
        }

        imagen.onload = function() {
            shiftzoom.add(imagen, {
                buttons: false
            });
            imagen.onload = null;
        };

        new Ventana({
            titulo: "Imagen",
            contenido: div,
            w: "auto"
        }).ver();
    }

});
    
