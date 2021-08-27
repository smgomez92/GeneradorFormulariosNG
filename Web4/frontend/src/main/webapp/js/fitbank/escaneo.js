include("lib.prototype");
include("lib.shiftzoom.shiftzoom");

include("fitbank.ui.ventana");

/**
 * Namespace Escaneo - Contiene funciones para manejar el escaneo.
 */
var Escaneo = {
    
    webCamInterval: null,

    BASE: "http://localhost:1089/",

    escanear : function(trabajoEscaneo, opciones) {
        trabajoEscaneo = new ScanningJob(trabajoEscaneo);
        
        trabajoEscaneo.query = $H({
            json: Object.toJSON(Util.clean(trabajoEscaneo))
        });
        trabajoEscaneo.pagina = 0;

        // Opciones por default
        trabajoEscaneo.opciones = {
            crossDomain: true,
            paginar: false,
            elemento: null,
            onComplete: function() {},
            onCancel: function() {}
        }

        if (Object.isFunction(opciones)) {
            trabajoEscaneo.opciones.onComplete = opciones;
        } else if (Object.isElement(opciones)) {
            trabajoEscaneo.opciones.elemento = opciones;
        } else {
            Object.extend(trabajoEscaneo.opciones, opciones || {});
        }

        var contenido = new Element("div");

        var ventana = new Ventana({
            titulo: "Escanear",
            contenido: contenido,
            w: 640,
            h: 480
        });

        // Crear imagen (con contenedor)
        contenido.insert(Escaneo._crearImagen(trabajoEscaneo));

        // Compara Tipo Normal o Photo
        if (trabajoEscaneo.scannerType == ScannerType.PHOTO) {
            contenido.insert(Escaneo._crearControlesWebCam(trabajoEscaneo, ventana));
        } else {
            contenido.insert(Escaneo._crearControles(trabajoEscaneo, ventana));
        }

        ventana.ver();
        
        Escaneo._hacerPedido(trabajoEscaneo);
    },
    
    solo_escanear : function(trabajoEscaneo, opciones) {
        trabajoEscaneo = new ScanningJob(trabajoEscaneo);
        
        trabajoEscaneo.query = $H({
            json: Object.toJSON(Util.clean(trabajoEscaneo))
        });
        trabajoEscaneo.pagina = 0;

        // Opciones por default
        trabajoEscaneo.opciones = {
            crossDomain: true,
            paginar: false,
            elemento: null,
            onComplete: function() {},
            onCancel: function() {}
        }

        if (Object.isFunction(opciones)) {
            trabajoEscaneo.opciones.onComplete = opciones;
        } else if (Object.isElement(opciones)) {
            trabajoEscaneo.opciones.elemento = opciones;
        } else {
            Object.extend(trabajoEscaneo.opciones, opciones || {});
        }

        trabajoEscaneo.progreso = new Element("img", {
            src: "img/blanco.png",
            width: 56,
            height: 21
        });


        var contenido = new Element("div");

        var ventana = new Ventana({
            titulo: "Escanear",
            contenido: contenido,
            w: 65,
            h: 25
        });

        contenido.insert(trabajoEscaneo.progreso, ventana);
        ventana.ver();
        Escaneo._soloHacerPedido(trabajoEscaneo, ventana);
    },

    _crearImagen: function(trabajoEscaneo) {
        var w = trabajoEscaneo.w * trabajoEscaneo.resolution;
        var h = trabajoEscaneo.w * trabajoEscaneo.resolution;

        if (w > 600) {
            h = h * 600 / w;
            w = 600;
        }

        if (h > 350) {
            w = w * 350 / h;
            h = 350;
        }

        var divImagen = new Element("div", {
            className: "escaneo-imagen-container"
        }).setStyle({
            width: (w + 40) + "px",
            height: (h + 40) + "px"
        });

        var imagen = new Element("img", {
            src: "img/blanco.png",
            width: w,
            height: h
        });
        divImagen.insert(imagen);
        trabajoEscaneo.imagenId = imagen.identify();

        return divImagen;
    },
    
    _crearControlesWebCam: function(trabajoEscaneo, ventana) {
        var controles = new Element("div", {
           className: "escaneo-botones"
        });

        var tomarFoto = new Element("button").update("Capturar");
        var cancelar = new Element("button").update("Cancelar");
        //Check sirve para  envair peticiones/s al scanner-server
        var chkWebCamInterval = new Element("input", {
           type: "checkbox",
           value: "0"
        });
        //Boton de guardar
        trabajoEscaneo.guardar = new Element("button", {
            title: "Guardar esta pagina y cerrar la ventana."
        }).update("Terminar");

        controles.insert(tomarFoto);
        controles.insert(trabajoEscaneo.guardar);
        controles.insert(cancelar);
        
        controles.insert(chkWebCamInterval);
        
        
        tomarFoto.on("click", function() {
            if (chkWebCamInterval.checked) {
                clearTimeout(Escaneo.webCamInterval.timeout);
                chkWebCamInterval.checked = false;
            } else {
               Escaneo._hacerPedido(trabajoEscaneo);
            }
        });

       cancelar.on("click", function() {
            if (Escaneo.webCamInterval) {
                clearTimeout(Escaneo.webCamInterval.timeout);
            }

           ventana.cerrar();
           trabajoEscaneo.opciones.onCancel(trabajoEscaneo);
       });

       trabajoEscaneo.guardar.on("click", function() {
           if (Escaneo.webCamInterval) {
               clearTimeout(Escaneo.webCamInterval.timeout);
           }

           Escaneo._guardar(trabajoEscaneo);
           ventana.cerrar();
        });

       chkWebCamInterval.on("change", function() {
           
           if (chkWebCamInterval.checked) {
               Escaneo.webCamInterval = function() {
                    Escaneo._hacerPedido(trabajoEscaneo);
                    Escaneo.webCamInterval.timeout = setTimeout(Escaneo.webCamInterval, 1500);
               }

               Escaneo.webCamInterval();
           } else {
               clearTimeout(Escaneo.webCamInterval.timeout);
           }

       });

       return controles;
    },
    
    _crearControles: function(trabajoEscaneo, ventana) {
        var controles = new Element("div", {
            className: "escaneo-botones"
        });

        var label = new Element("span").update("Pagina 1: ");
        controles.insert(label);

        ///////////////////////////////////////////////////////
        // Boton de escanear
        var escanear = new Element("button").update("Re-escanear");
        controles.insert(escanear);

        escanear.on("click", function() {
            Escaneo._hacerPedido(trabajoEscaneo);
        });

        ///////////////////////////////////////////////////////
        // Pagina actual
        trabajoEscaneo.siguiente = new Element("button", {
            title: "Guardar esta pagina y escanear la siguiente pagina."
        }).update("Siguiente");
        controles.insert(trabajoEscaneo.siguiente);
        trabajoEscaneo.siguiente.disabled = true;
        if (!trabajoEscaneo.opciones.paginar) {
            trabajoEscaneo.siguiente.hide();
        }

        trabajoEscaneo.siguiente.on("click", function() {
            Escaneo._guardar(trabajoEscaneo);
            label.update("Pagina " + (++trabajoEscaneo.pagina + 1) + ": ");
            Escaneo._hacerPedido(trabajoEscaneo);
        });

        ///////////////////////////////////////////////////////
        // Boton de guardar
        trabajoEscaneo.guardar = new Element("button", {
            title: "Guardar esta pagina y cerrar la ventana."
        }).update("Terminar");
        controles.insert(trabajoEscaneo.guardar);
        trabajoEscaneo.guardar.disabled = true;

        trabajoEscaneo.guardar.on("click", function() {
            Escaneo._guardar(trabajoEscaneo);
            ventana.cerrar();
        });

        ///////////////////////////////////////////////////////
        // Boton de cancelar
        var cancelar = new Element("button").update("Cancelar");
        controles.insert(cancelar);

        if (trabajoEscaneo.opciones.paginar) {
            cancelar.hide();
        }

        cancelar.on("click", function() {
            ventana.cerrar();
            trabajoEscaneo.opciones.onCancel(trabajoEscaneo);
        });

        ///////////////////////////////////////////////////////
        // Imagen de progreso
        trabajoEscaneo.progreso = new Element("img", {
            src: "img/blanco.png",
            width: 56,
            height: 21
        });
        controles.insert(trabajoEscaneo.progreso);

        return controles;
    },
    
    _hacerPedido: function(trabajoEscaneo) {
        var cb = Escaneo._previsualizar.curry(trabajoEscaneo);
        var url = Escaneo.BASE + 'preview/default/imagen.jpg';
        
        if (trabajoEscaneo.scannerType == ScannerType.NORMAL) {
            trabajoEscaneo.progreso.src = "img/progreso2.gif";
        }

        if (trabajoEscaneo.opciones.crossDomain) {
            var unique = Math.random();
            trabajoEscaneo.query.set("callback", "Escaneo['" + unique + "']");
            var script = new Element("script", {
                src: url + '/data.js?' + trabajoEscaneo.query.toQueryString()
            });
            Escaneo[unique] = function(json) {
                cb({
                    responseJSON: json
                });

                script.remove();
            }
            $$("head")[0].insert(script);
        } else {
            new Ajax.Request(url + '/data.json', {
                parameters: trabajoEscaneo.query,
                onComplete: cb
            });
        }
    },
    
    _soloHacerPedido: function(trabajoEscaneo, ventana) {
        var cb = Escaneo._finalizarescaneo.curry(trabajoEscaneo, ventana);
        var url = Escaneo.BASE + 'preview/default/imagen.jpg';
        trabajoEscaneo.progreso.src = "img/progreso2.gif";
        
        if (trabajoEscaneo.opciones.crossDomain) {
            var unique = Math.random();
            trabajoEscaneo.query.set("callback", "Escaneo['" + unique + "']");
            var script = new Element("script", {
                src: url + '/data.js?' + trabajoEscaneo.query.toQueryString()
            });
            Escaneo[unique] = function(json) {
                cb({
                    responseJSON: json
                });

                script.remove();
            }
            $$("head")[0].insert(script);
        } else {
            new Ajax.Request(url + '/data.json', {
                parameters: trabajoEscaneo.query,
                onComplete: cb
            });
        }
    },
    
    _finalizarescaneo: function(trabajoEscaneo, ventana, response) {
        trabajoEscaneo.data = response.responseJSON.data;
        trabajoEscaneo.progreso.src = "img/blanco.png";
        trabajoEscaneo.opciones.onComplete(trabajoEscaneo);

        if (trabajoEscaneo.opciones.elemento && trabajoEscaneo.data != '') {
            trabajoEscaneo.opciones.elemento.changeValue(trabajoEscaneo.data);
            Estatus.mensaje("ESCANEO REALIZADO CORRECTAMENTE, DEBE GUARDAR PARA FINALIZAR LA TRANSACCION", null, 'warning');
        }
        ventana.cerrar();
    },
    
    _previsualizar: function(trabajoEscaneo, response) {
        trabajoEscaneo.data = response.responseJSON.data;
        trabajoEscaneo.guardar.disabled = false;
        
        if (trabajoEscaneo.siguiente) {
            trabajoEscaneo.siguiente.disabled = false;
            trabajoEscaneo.progreso.src = "img/blanco.png";
        }
       
        Escaneo._verImagen(trabajoEscaneo);
    },

    _verImagen: function(trabajoEscaneo) {
        var imagen = $(trabajoEscaneo.imagenId);
        var url = "data:image/jpeg;base64," + trabajoEscaneo.data;
        
        if (imagen.parentNode.hasClassName("escaneo-imagen-container")) {
            imagen.src = url;
            
            if (trabajoEscaneo.scannerType == ScannerType.NORMAL) {
                imagen.onload = function() {
                    shiftzoom.add(imagen, {
                        buttons: false
                    });
                    imagen.onload = null;
                };
            }
        } else {
            shiftzoom.source(imagen, url, true);
        }
    },

    _guardar: function(trabajoEscaneo) {
        trabajoEscaneo.opciones.onComplete(trabajoEscaneo);

        if (trabajoEscaneo.opciones.elemento) {
            trabajoEscaneo.opciones.elemento.changeValue(trabajoEscaneo.data);
            Estatus.mensaje("ESCANEO REALIZADO CORRECTAMENTE, DEBE GUARDAR PARA FINALIZAR LA TRANSACCION", null, null); 
        }
    }

};