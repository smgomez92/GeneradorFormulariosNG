include("lib.prototype");
include("lib.shiftzoom.shiftzoom");

/**
 * @remote
 */
ImageAssistant.addMethods({

    init: function(elemento) {
        var img = $(elemento.getAttribute("imageid"));

        img.assistant = this;
        elemento.registro = img.registro;

        img.controls = this.createControls(elemento, "image-controls", img);
        img.insert({
            after: img.controls
        });

        if (this.showScannerLink) {
            var scan = new Element("button").update("Adquirir");
            img.controls.insert(scan);

            scan.on("click", Escaneo.escanear.curry(this.scanningJob, elemento));
        }

        elemento.on("change", this.reload.bind(this, elemento, img));

        this.reload(elemento, img);
    },

    createControls: function(elemento, className, image, hide) {
        var imageId = image.identify();

        var page = 0;
        var numberOfPages = 1;

        var divControles = new Element("div", {
            className: className
        });

        var botonAnterior = new Element("button", {
            onclick: "return false;"
        }).update("<");
        divControles.insert(botonAnterior);

        var paginas = new Element("span", {
            className: "image-controls-pages"
        });
        divControles.insert(paginas);

        var botonSiguiente = new Element("button", {
            onclick: "return false;"
        }).update(">");
        divControles.insert(botonSiguiente);

        var refresh = (function(reloadImage) {
            botonAnterior.disabled = page <= 0;
            botonSiguiente.disabled = page >= numberOfPages - 1;
            
            paginas.update((page + 1) + "/" + numberOfPages);

            if (hide && numberOfPages <= 1) {
                divControles.hide();
            } else {
                divControles.show();
            }

            if (reloadImage) {
                this.reload(elemento, $(imageId), page);
            }
        }).bind(this);

        botonAnterior.on("click", function() {
            if (page > 0) {
                page--;
                refresh(true);
            }
        });

        botonSiguiente.on("click", function() {
            if (page < numberOfPages - 1) {
                page++;
                refresh(true);
            }
        });

        new Ajax.Request(this._buildUrl(GeneralRequestTypes.IMG, elemento, "_json"), {
            onSuccess: function(transport) {
                var json = transport.responseJSON;
                numberOfPages = json.numberOfPages;
                refresh();
            }
        });
        
        return divControles;
    },

    reload: function(elemento, img, page) {
        var src =  this._buildUrl(GeneralRequestTypes.IMG, elemento, "", {
            page: page || 0
        });

        if (img.tagName == "IMG") {
            img.src = src;
        } else if (img) {
            shiftzoom.source(img, src, true);
        }
    }

});
