include("lib.jquery");
include("lib.jquerynoconflicts");
include("lib.lodash");
include("lib.backbone");
include("lib.joint");

FlowBpm.anchoRectangulo = 90;
            
FlowBpm.altoRectangulo = 75;

FlowBpm.radioCirculo = 50;  

FlowBpm.anchoPagina = 0;

FlowBpm.altoPagina = 0;

FlowBpm.saltoEnY = 140;

FlowBpm.saltoEnX = 160;

FlowBpm.posColorActual=0;

FlowBpm.coloresProcesoActual = ['#d1d5ea', '#afb6d8'];

FlowBpm.procesoActualId = 1;


FlowBpm.crearCirculo = function(graph, codigo, radio, posX, posY, texto, colorRelleno, colorTexto) {
    var circulo = new joint.shapes.basic.Circle({
    id: codigo, 
    position: { x: posX-(radio/2), y: posY },
    size: { width: radio, height: radio },
    attrs: { circle: { fill: colorRelleno }, text: { text: texto, fill: colorTexto, 'font-size': 10, 'font-weight': 'bold' } }
    });
    graph.addCells(circulo);
};

FlowBpm.crearRectangulo = function(graph, codigo, posX, posY, texto, colorRelleno, colorBordes, colorTexto, negrita, grosorBorde) {

    var wraptext = joint.util.breakText(texto, {
        width: 120
    });

    var rect = new joint.shapes.basic.Rect({
        id: codigo,
        position: { x: posX-(FlowBpm.anchoRectangulo/2), y: posY },
        size: { width: FlowBpm.anchoRectangulo, height: FlowBpm.altoRectangulo},
        attrs: { rect: { fill: colorRelleno, stroke: colorBordes, 'stroke-width': grosorBorde}, text: { text: wraptext, fill: colorTexto, 'font-size': 8, 'font-weight': negrita, 
        'font-variant': 'small-caps', 
        'text-transform': 'capitalize'} }
    });
    graph.addCells(rect);
    return rect;
};

FlowBpm.crearInicio = function(graph, codigo) {
    FlowBpm.crearCirculo(graph, codigo, FlowBpm.radioCirculo, FlowBpm.anchoPagina/2, 2, 'Inicio', 'green', 'white');
};

FlowBpm.crearFin = function(graph, codigo, posX, posY) {
    FlowBpm.crearCirculo(graph, codigo, FlowBpm.radioCirculo, posX, posY, 'Fin', 'red', 'white');
};

FlowBpm.crearPendienteAutorizacion = function(graph, codigo, posX, posY) {

    var wraptext = joint.util.breakText("Requiere Autorización", {
            width: 80
    });
    var image = new joint.shapes.basic.Image({
        id: codigo,
        position : {
            x : posX,
            y : posY
        },
        size : {
            width : 35,
            height : 35
        },
        attrs : {
            image : {
                "xlink:href" : "./img/bpm/aut_user.png",
                width : 16,
                height : 16
            }, text: { text: wraptext, 'font-size': 12
                      }
        }
    });

    graph.addCell(image); 
};

FlowBpm.crearLinkIr = function(graph, codigo, posX, posY) {

    var image = new joint.shapes.basic.Image({
        id: codigo,
        position : {
            x : posX,
            y : posY
        },
        size : {
            width : 20,
            height : 25
        },
        attrs : {
            image : {
                "xlink:href" : "./img/bpm/link.png",
                width : 9,
                height : 9
            }
        }
    });

    graph.addCell(image); 
};

FlowBpm.crearRegresar = function(graph, codigo, posX, posY) {

    var wraptext = joint.util.breakText("Regresar", {
            width: 80
    });
    var image = new joint.shapes.basic.Image({
        id: codigo,
        position : {
            x : posX,
            y : posY
        },
        size : {
            width : 35,
            height : 35
        },
        attrs : {
            image : {
                "xlink:href" : "./img/bpm/back.png",
                width : 16,
                height : 16
            }, text: { text: wraptext, 'font-size': 13
                      }
        }
    });

    graph.addCell(image); 
};

FlowBpm.cambiarColor = function(object, atributo, color) {
   object.attr(atributo, color);

};

FlowBpm.cambiarColorProcesoActual = function(object) {
    if (FlowBpm.posColorActual >= FlowBpm.coloresProcesoActual.length) {
        FlowBpm.posColorActual = 0;
    } else {
        FlowBpm.posColorActual++;
    }

    FlowBpm.cambiarColor(FlowBpm.rectanguloActual, 'rect/fill', FlowBpm.coloresProcesoActual[FlowBpm.posColorActual]);
};

FlowBpm.crearRectanguloActual = function(graph, codigo, posX, posY, texto) {
   FlowBpm.rectanguloActual = FlowBpm.crearRectangulo(graph, codigo, posX, posY, texto, '#dcd7d7', '#3c4260', 'black', 'bold', 3);
   clearInterval(FlowBpm.procesoActualId);
   FlowBpm.procesoActualId = setInterval("FlowBpm.cambiarColorProcesoActual()", 300); 
};

FlowBpm.crearRectanguloRecorrido = function(graph, codigo, posX, posY, texto) {
   FlowBpm.rectanguloActual = FlowBpm.crearRectangulo(graph, codigo, posX, posY, texto, '#c9f0ad', '#3c4260', 'black', 'none', 2);
};

FlowBpm.crearRectanguloDeshabilitado = function(graph, codigo, posX, posY, texto) {
    FlowBpm.crearRectangulo(graph, codigo, posX, posY, texto, '#ffffff', '#d9d9d9', '#afaeae', 'none', 2);
};

FlowBpm.crearRectanguloAutorizacion = function(graph, codigo, posX, posY, texto) {
    FlowBpm.crearRectangulo(graph, codigo, posX, posY, texto, '#a85252', '#3c4260', 'white', 'bold', 2);
};  

FlowBpm.crearRecNomal = function(graph, codigo, posX, posY, texto) {
    FlowBpm.crearRectangulo(graph, codigo, posX, posY, texto, '#dcd7d7', '#3c4260', 'black', 'bold', 2);
};

FlowBpm.crearEnlace = function(graph, idOrigen, idDestino, texto) {
    var wraptextlink = joint.util.breakText(texto, {
        width: 120
    });        

    var link = new joint.dia.Link({
        source: { id: idOrigen },
        target: { id: idDestino },
        labels: [
        { position: 0.5, attrs: { text: { text: wraptextlink, fill: 'black', 'font-size': 10, 'font-weight': 'bold', 
            'font-variant': 'small-caps', 
            'text-transform': 'capitalize'} } }]
    });

    link.attr({
        '.connection': { stroke: '#364071', 'stroke-width': 2 },
        '.marker-target': { stroke: '#364071', fill: '#364071', d: 'M 10 0 L 0 5 L 10 10 z' }
    });
    graph.addCells(link);
};  

FlowBpm.dibujarNiveles = function(iframe, graph, nivelesInformacion) {
    var nuemeroNiveles = nivelesInformacion.length;
    for (var i=0; i<nuemeroNiveles; i++) {
        var posicionY = i * FlowBpm.saltoEnY;
        var informacionPorNivel = nivelesInformacion[i];
        for (var j=0; j<informacionPorNivel.length; j++) {
            var nivel = informacionPorNivel[j];
            var posicionX;
            if (nivel.posX == FlowBpm.mitad) {
                posicionX = FlowBpm.anchoPagina/2;
            } else {
                if (nivel.posX > FlowBpm.mitad) {
                    posicionX = FlowBpm.anchoPagina/2 + (nivel.posX - FlowBpm.mitad) * FlowBpm.saltoEnX;
                } else {
                    posicionX = FlowBpm.anchoPagina/2 - (FlowBpm.mitad - nivel.posX) * FlowBpm.saltoEnX;
                }
            }
            if (i == 0) {
                FlowBpm.crearInicio(graph, nivel.id);
            } else {
                if (nivel.fin == "1") {
                    FlowBpm.crearFin(graph, nivel.id, posicionX, posicionY);
                } else if (nivel.actual == "1" && nivel.pendiente_autorizacion == "1") {
                    FlowBpm.crearRectanguloActual(graph, nivel.id, posicionX, posicionY, nivel.texto);
                    FlowBpm.crearPendienteAutorizacion(graph, nivel.id+'-imagen-pendienteautorizacion', posicionX + 70, posicionY);
                } else if (nivel.actual == "1" && nivel.agregar_link_ir == "1") {
                    FlowBpm.crearRectanguloActual(graph, nivel.id, posicionX, posicionY, nivel.texto);
                    FlowBpm.crearLinkIr(graph, nivel.id + '-imagen-link-ir', posicionX + 70, posicionY + 20);
                    FlowBpm.metadata_ir = nivel.json_ir;
                } else if (nivel.actual == "1") {
                    FlowBpm.crearRectanguloActual(graph, nivel.id, posicionX, posicionY, nivel.texto);
                } else if (nivel.recorrido == "1") {
                    FlowBpm.crearRectanguloRecorrido(graph, nivel.id, posicionX, posicionY, nivel.texto);
                } else if (nivel.activado == "0") {
                    FlowBpm.crearRectanguloDeshabilitado(graph, nivel.id, posicionX, posicionY, nivel.texto);
                } else if (nivel.autorizacion == "1") {
                    FlowBpm.crearRectanguloAutorizacion(graph, nivel.id, posicionX, posicionY, nivel.texto);
                } else {
                    FlowBpm.crearRecNomal(graph, nivel.id, posicionX, posicionY, nivel.texto);
                }
            }
        }      
    }

};

FlowBpm.agregarToolTipText = function(iframe, nivel, nivelesInformacion) {
    var nuemeroNiveles = nivelesInformacion.length;
    for (var i=0; i<nuemeroNiveles; i++) {
        var posicionY = i * FlowBpm.saltoEnY;
        var informacionPorNivel = nivelesInformacion[i];
        for (var j=0; j<informacionPorNivel.length; j++) {
            var nivel = informacionPorNivel[j];
            if (nivel.tooltiptext) {    
                var script_tag = Util.getContentWindow(iframe).document.createElement('script');
                script_tag.type = "text/javascript";
                text ="var myOpentip = new Opentip($('g[model-id=\"" + nivel.id + "\"]')[0], { target: $('g[model-id=\"" + nivel.id + "\"]')[0], tipJoint: \"bottom\", escapeContent:false }); " +
                " myOpentip.setContent(\"" + nivel.tooltiptext + "\");";
                var script = Util.getContentWindow(iframe).document.createTextNode(text);
                script_tag.appendChild(script);
                Util.getContentWindow(iframe).document.body.appendChild(script_tag);
            }
        }
    }
};

FlowBpm.dibujarEnlaces = function(graph, nivelesInformacion) {
    var nuemeroConectores = nivelesInformacion.length;
    for (var i=0; i<nuemeroConectores; i++) {
        var enlace = nivelesInformacion[i];
        FlowBpm.crearEnlace(graph, enlace.origen, enlace.destino, enlace.nombreconector);
    }

};

FlowBpm.crearSiguienteNivel = function(elemento, iframe, manejador, data) {
        Util.getContentWindow(iframe).document.body.innerHTML = "";
        FlowBpm.show(elemento, iframe, data, manejador, true);
};

FlowBpm.show = function(elemento, iframe, data, manejador, regresar) {
        var diframe = Util.getContentWindow(iframe).document;
        FlowBpm.anchoPagina = iframe.getWidth();

        var element = new Element("div", {
            id : "contenedorgraficaworkflow"
        }).setStyle({
            position : "absolute",
            height : iframe.height,
            width : iframe.width,
            overflow : "auto"
        });

        diframe.body.appendChild(element);

        var graph = new joint.dia.Graph;

        var informacion = data.informacion;
        FlowBpm.espaciado = FlowBpm.saltoEnX + FlowBpm.anchoRectangulo;
        FlowBpm.anchoPagina = FlowBpm.espaciado * informacion.columnas;
        FlowBpm.altoPagina = (FlowBpm.saltoEnY + FlowBpm.altoRectangulo) * (informacion.filas + 1);
        FlowBpm.mitad = informacion.mitad;
    
        var paper = new joint.dia.Paper({
            el: element,
            width: FlowBpm.anchoPagina,
            height: FlowBpm.altoPagina,
            model: graph,
            gridSize: 1, 
            interactive: false
        });
        
        var graficarNiveles = data.niveles;
        var graficarEnlaces = data.conectores;

        FlowBpm.dibujarNiveles(iframe, graph, graficarNiveles);   
        FlowBpm.dibujarEnlaces(graph, graficarEnlaces);
        var idtooltiptext = setInterval(function(){
            clearInterval(idtooltiptext);
        	FlowBpm.agregarToolTipText.defer(FlowBpm.iframe, graph, graficarNiveles);
   		}, 500); 
       
        if (regresar) {
            var posicionY = informacion.filas * FlowBpm.saltoEnY;
            FlowBpm.crearRegresar(graph, "regresar_nivel_principal", 20, posicionY);
        }
    
        paper.on('cell:pointerclick', function (cellView) {
            if (cellView.model.id.indexOf("pendienteautorizacion") >= 0) {
                var metadata_autorizacion = data.metadata_autorizacion;
                FlowBpm.crearSiguienteNivel(elemento, iframe, manejador, metadata_autorizacion);    
            }
            if (cellView.model.id.indexOf("regresar_nivel_principal") >= 0) {
                FlowBpm.showInit(elemento, iframe, manejador);    
            }
            if (cellView.model.id.indexOf("imagen-link-ir") >= 0) {
                FlowBpm.ventana.cerrar();
                var jsonir = FlowBpm.metadata_ir;
                top.auth = true;
                c.cargar(jsonir.subsistema, jsonir.transaccion, jsonir.campos, jsonir.nameMap);
            }
        });   
};

FlowBpm.showInit = function(elemento, iframe, manejador) {
        Util.getContentWindow(iframe).document.body.innerHTML = "";
        var data = eval('['+ elemento.value +']')[0]
        FlowBpm.show(elemento, iframe, data, manejador);
};  
    
FlowBpm.addMethods({

	initialize : function(parametros) {
		copyConstructor.bind(this)(parametros);
		this.nameMap = $H();
		FlowBpm.btonflows = {};
		this.values.each(function(par) {
			this.nameMap.set(par.key, par.value);
		}, this);
		c && c.$N(this.elementName).each(this.init, this);
	},
    
	init : function(elemento) {
		var iframeHeight = 900;
		var iframeWidth = 1024;
		
        var boton = new Element("input", {
			type : "button",
			value : this.label,
			id : "btnflowclick",
			height : "10px",
			width : "20px"
		});

		elemento.hide().insert({
			after : boton,
		});

		var manejador = {
			melemento : elemento,
			mboton : boton,
			registro : elemento.registro
		};
        
        boton.hide()
		
		boton.on('click', (function() {
            if (elemento.value &&  eval('['+ elemento.value +']')[0].niveles) {
                
                FlowBpm.iframe = new Element("iframe", {
                name : "HTMLIframe",
                height : iframeHeight + "px",
                width : iframeWidth + "px",
                style : "background-color: white; border: 1px",
                id : "iframeflowId"
                });

                FlowBpm.ventana = new Ventana({
                    titulo : "Flujo BPM",
                    contenido : FlowBpm.iframe,
                    destruirAlCerrar: true
                });

                FlowBpm.metadatair = "";
                FlowBpm.ventana.ver();
                var headJoints = Util.getContentWindow(FlowBpm.iframe).document.head;

                var scriptquery = Util.getContentWindow(FlowBpm.iframe).document.createElement('script');
                scriptquery.setAttribute("src", "js/lib/jquery.js");
                scriptquery.setAttribute("type", "text/javascript");
                headJoints.appendChild(scriptquery);

                var scripttooltip = Util.getContentWindow(FlowBpm.iframe).document.createElement('script');
                scripttooltip.setAttribute("src", "js/lib/opentip-jquery.js");
                scripttooltip.setAttribute("type", "text/javascript");
                headJoints.appendChild(scripttooltip);

                var linkopentip = Util.getContentWindow(FlowBpm.iframe).document.createElement("link");
                linkopentip.setAttribute("rel", "stylesheet");
                linkopentip.setAttribute("type", "text/css");
                linkopentip.setAttribute("href", "css/opentip.css");
                headJoints.appendChild(linkopentip);

                var link = Util.getContentWindow(FlowBpm.iframe).document.createElement("link");
                link.setAttribute("rel", "stylesheet");
                link.setAttribute("type", "text/css");
                link.setAttribute("href", "css/joint.css");
                headJoints.appendChild(link);

                FlowBpm.showInit(elemento, FlowBpm.iframe, manejador);
            }
		}).bind(this));
		FlowBpm.btonflows[elemento.registro] = boton;
	}
});