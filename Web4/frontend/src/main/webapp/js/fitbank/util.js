include("lib.prototype");
include("lib.split");

/**
 * Namespace Util - Define funciones utilitarias.
 */
var Util = {

    //Selectores para encontrar los elementos candidatos a verificar si su valor 
    //ha cambiado, pueden agregarse mas filtros
    selectors: [
        "input:not([readonly='']), select:not([readonly='']), textarea:not([readonly=''])",
        "[type!='hidden']",
        ".record, [class*='control']"
    ],

    getCaret: function(elemento) {
        var caret = '';
        return caret;
    },

    applySelectors: function() {
        var target = new Element("input");
        var first = true;

        this.selectors.each(function(selector) {
            if (first) {
                target = c.form;
                first = false;
            }

            var selected = Prototype.Selector.select(selector, target);
            target = new Element("input");
            selected.each(function(el) {
                if(el.type === "select-one") {
                    var tmpInput = new Element("input", {
                        id: el.id,
                        class: el.className
                    });

                    tmpInput.value = el.options[el.selectedIndex] && el.options[el.selectedIndex].value || "";
                    target.appendChild(tmpInput);
                } else if(el.type === "checkbox") {
                    var tmpInput = new Element("input", {
                        id: el.id,
                        class: el.className
                    });

                    tmpInput.value = el.value;
                    target.appendChild(tmpInput);
                } else {
                    target.appendChild(el.clone(true));
                }
            });
        });

        return target.childElements();
    },

    getOriginalElements: function() {
        var elements = {};
        var targetElements = Util.applySelectors();

        targetElements.each(function(e) {
            elements[e.id] = e.value;
        });

        return elements;
    },

    checkForChanges: function(originalElements) {
        if (!originalElements || !c.form || !c.formulario) {
            return false;
        }

        var changes = false;
        var currentElements = {};

        var targetElements = Util.applySelectors();

        targetElements.each(function(e) {
            if(e.type !== "select-one") {
                currentElements[e.id] = e.value;
            } else {
                currentElements[e.id] = e.options[e.selectedIndex].text;
            }
        });

        if($H(currentElements).size() !== $H(originalElements).size()) {
            changes = true;
        } else {
            $H(originalElements).each(function(pair) {
                var check = pair.value !== currentElements[pair.key];
                changes = changes || check;
            });
        }

        return changes;
    },

    /**
     * Función que obtiene el valor del nombre de la clase css.
     *
     * @param className
     *            Valor del nombre de la clase css.
     */
    getStyleClass: function(className) {
        for ( var s = 0; s < document.styleSheets.length; s++) {
            if (document.styleSheets[s].rules) {
                for ( var r = 0; r < document.styleSheets[s].rules.length; r++) {
                    if (document.styleSheets[s].rules[r].selectorText == '.' + className) {
                        return document.styleSheets[s].rules[r];
                    }
                }
            } else if (document.styleSheets[s].cssRules) {
                for ( var r = 0; r < document.styleSheets[s].cssRules.length; r++) {
                    if (document.styleSheets[s].cssRules[r].selectorText == '.' + className) {
                        return document.styleSheets[s].cssRules[r];
                    }
                }
            }
        }
        return null;
    },

    encodeHTML: function(string) {
        return string.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(
                />/g, "&gt;");
    },

    getContentWindow: function(iframe) {
        iframe = $(iframe);
        return iframe.contentWindow || iframe;
    },

    /**
     * Inicializa una imagen para que use un oculto.
     */
    initHtmlElement: function(imageId) {
        var elemento = $(imageId);
        var oculto = $(imageId + "_oculto");

        oculto.widget = elemento;
        elemento.oculto = oculto;
        elemento.widget = elemento;

        oculto.hide = Element.hide.curry(elemento);
        oculto.show = Element.show.curry(elemento);
    },

    /**
     * Inicializa un elemento para que use un oculto.
     */
    initHiddenInput: function(elemento, suffix) {
        elemento = $(elemento);

        elemento.sync = function(oculto, e) {
            elemento.changeValue(oculto.value, e && e.options || e);
        };

        elemento.setValueOculto = function (oculto) {
            oculto.changeValue(elemento.value);
        };

        Util._initHiddenInput(elemento, suffix);
    },

    /**
     * Inicializa un checkbox para que use un oculto.
     */
    initCheckBox: function(elemento) {
        elemento = $(elemento);

        elemento.valueOn = elemento.getAttribute("value-on");
        elemento.valueOff = elemento.getAttribute("value-off");

        elemento.hide = Element.hide.curry(elemento.next("label"));
        elemento.show = Element.show.curry(elemento.next("label"));

        elemento.sync = function(oculto, e) {
            elemento.setChecked(oculto.value == elemento.valueOn, e && e.options || e);
        };

        elemento.fixValue = function() {
            var value = elemento.checked ? elemento.valueOn : elemento.valueOff;
            elemento.value = value;
            return value;
        };

        elemento.setValueOculto = function (oculto) {
            oculto.value = elemento.fixValue();
        };

        Util._initHiddenInput(elemento, "checkbox");
    },

    /**
     * Inicializa un combobox para que use un oculto.
     */
    initComboBox: function(elemento) {
        elemento = $(elemento);

        elemento.on("keyup", function(e) {
            elemento.fireDOMEvent("change", {
                generated: false
            });
        });

        elemento.sync = function(oculto, e) {
            for (var i = 0 ; i < elemento.options.length ; i++) {
                if (elemento.options[i].value == oculto.value) {
                    elemento.selectedIndex = i;
                    break;
                }
            }
        };

        elemento.setValueOculto = function (oculto) {
            oculto.value = elemento.options[elemento.selectedIndex].value;
        };

        Util._initHiddenInput(elemento, "combobox");
    },

    /**
     * Inicializa un elemento para que use un oculto.
     * Se sincronizan los cambios efectuados por evento onChange
     */
    _initHiddenInput: function(elemento, suffix) {
        var name = elemento.name;

        elemento.name = name + "_" + (suffix || "widget");

        var oculto = new Element("input", {
            type: "hidden",
            name: name,
            registro: elemento.registro
        });

        elemento.insert( {
            before: oculto
        });

        oculto.oculto = oculto;
        elemento.oculto = oculto;

        oculto.widget = elemento;
        elemento.widget = elemento;

        oculto.hide = Element.hide.curry(elemento);
        oculto.show = Element.show.curry(elemento);

        if (suffix) {
            oculto[suffix] = elemento;
            elemento[suffix] = elemento;
        }

        if (elemento.sync) {
            oculto.on("change", elemento.sync.curry(oculto));
            oculto.on("widget:init", elemento.sync.curry(oculto, {
                load: true
            }));
        }

        if (elemento.setValueOculto) {
            elemento.on("change", elemento.setValueOculto.curry(oculto));
            elemento.setValueOculto(oculto);
        }

        return oculto;
    },

    /**
     * Inicializa una tabla para que tenga scroll.
     */
    initTableScroll: function(elemento, rows, horizontal) {
        elemento = $(elemento);

        if (elemento.visible()) {
            Tabs.removeListener(elemento.scrollHandler);
            Util._initTableScroll(elemento, rows, horizontal);
            c.resize(elemento);
        } else if (!elemento.scrollHandler) {
            elemento.scrollHandler = Util.initTableScroll.curry(elemento, rows,
                    horizontal);
            Tabs.addListener(elemento.scrollHandler);
        }
    },

    _initTableScroll: function(elemento, rows, horizontal) {
        var div = elemento.down("div") || elemento;
        var table = elemento.down("table");
        var thead = table.down("thead");
        var tbody = table.down("tbody");
        var tfoot = table.down("tfoot");
        var tr = tbody && tbody.down("tr");
        var isFlotable = elemento.hasClassName("flotable");
        var borders = 2;
        var scrollbarWidth = 18;

        if (!tbody || !tr) {
            return;
        }

        if (!horizontal) {
            var resize = function(td) {
                var w = parseInt(td.getStyle("width"));
                td.setStyle("width:" + w + "px");
            };

            table.select("th, td").each(resize);

            if (isFlotable) {
                var totalHeight = ((thead.getHeight() + 4) + (rows * (tr.getHeight() + borders)));

                div.setStyle({
                    display: "block",
                    overflowY: "auto",
                    overflowX: "auto",
                    height: totalHeight + "px"
                })

                thead && thead.setStyle({
                    display: "block"
                });

                tbody.setStyle({
                    display: "block",
                });

                tfoot && tfoot.setStyle({
                    display: "block"
                });
            } else {
                div.setStyle({
                    display: "block"
                });

                var bodyHeight = (rows * (tr.getHeight() + borders));
                var tableWidth = parseInt(table.getStyle("width")) + scrollbarWidth;
                table.setStyle({
                    tableLayout: "fixed",
                    width: tableWidth + "px"
                });

                var setStyle = function(tr) {
                    tr.setStyle({
                        display: "block",
                        position: "relative"
                    });
                };

                thead && thead.select("tr").each(setStyle);
                tfoot && tfoot.select("tr").each(setStyle);

                tbody.setStyle({
                    display: "block",
                    overflow: "auto",
                    width: "100%",
                    height: bodyHeight + "px"
                });
            }
        } else if (horizontal) {
            // TODO implementar scroll horizontal

        } else {
            var height = rows * tr.getHeight()
                    + (thead ? thead.getHeight() : 0)
                    + (tfoot ? tfoot.getHeight() : 0);

            elemento.setStyle({
                overflowY: "auto",
                overflowX: "hidden",
                paddingRight: "20px",
                height: height + "px"
            });
        }
    },

    initDeleteRecord: function(name) {
        c.$N(name).each(function(elemento) {
            if (elemento.widget) {
                elemento = elemento.widget;
            }

            elemento.setDisabled(true);

            elemento.on("change", function(e) {
                var tr = elemento.up("td").up("tr");
                if (elemento.checked) {
                    tr.addClassName("delete-record");
                } else {
                    tr.removeClassName("delete-record");
                }
                c.formulario.evalFormulas(elemento);
                c.calcular();
            });
        });
    },

    generarIdUnicoTemporal: function() {
        // IMPORTANTE: Mantener sincronizado con Servicios.java
        var id = "_id_";

        id += Math.round(Math.random() * 999);
        id += "_";
        id += Math.round(Math.random() * 999);
        id += "_";
        id += Math.round(Math.random() * 999);

        return id;
    },

    generarIdUnicoPermanente: function() {
        // IMPORTANTE: Mantener sincronizado con Servicios.java
        return "ID_" + Util.generarIdUnicoTemporal();
    },

    eliminarReferenciasCirculares: function(obj) {
        var seen = {};

        function isPrimitive(obj) {
            var t = typeof obj;
            return !obj || t == 'string' || t == 'number' || t == 'boolean';
        }

        function deCycle(obj) {
            var deCycled;

            if (!isPrimitive(obj)) {
                seen[obj] = true;
            }

            if (obj instanceof Array) {
                deCycled = [];

                for (var i = 0; i < obj.length; i += 1) {
                    if (!seen[obj[i]]) {
                        deCycled[i] = deCycle(obj[i]);
                    }
                }

            } else if (typeof obj == 'object' && obj) {
                deCycled = {};

                for (var k in obj) {
                    if (obj.hasOwnProperty(k) && !seen[obj[k]]) {
                        try {
                             deCycled[k] = deCycle(obj[k]);
                        } catch(e) {
                            // no hacer nada
                        }
                    }
                }
            } else {
                deCycled = obj;
            }

            return deCycled;
        }

        return deCycle(obj);
    },

    clean: function(obj) {
        if (!obj) {
            return;
        }

        try {
            obj.initialize = null;
            obj.constructor = null;
            delete obj.initialize;
            delete obj.constructor;
        } catch(e) {
            // no hacer nada
        }

        return obj;
    },

    isError: function(codigo) {
        // Esto debe ser igual a ManejoExcepcion.isError()
        return codigo && !/0|.*-0|ok-.*/i.test(codigo);
    },

    /**
     * Obtiene las direcciones IP de las interfaces de red locales que consumen
     * el servicio webm usando RTC como proveedor (no soportado en todos los browsers)
     * 
     * @param {function} callback Output a ejecutar con la(s) IP's como parametro
     * @param {HTMLElement} elemento (opcional) que contiene el valor de la IP a mostrar
     * @returns Ejecucion del callback, enviando la(s) IP's como parametro
     */
    getLocalIPsByRTC: function (callback, element) {
        // NOTE: window.RTCPeerConnection is "not a constructor" in FF22/23
        var RTCPeerConnection = /*window.RTCPeerConnection ||*/ window.webkitRTCPeerConnection || window.mozRTCPeerConnection;

        if (RTCPeerConnection)
            (function () {
                var rtc = new RTCPeerConnection({iceServers: []});
                if (1 || window.mozRTCPeerConnection) {      // FF [and now Chrome!] needs a channel/stream to proceed
                    rtc.createDataChannel('', {reliable: false});
                }
                ;

                rtc.onicecandidate = function (evt) {
                    // convert the candidate to SDP so we can run it through our general parser
                    // see https://twitter.com/lancestout/status/525796175425720320 for details
                    if (evt.candidate)
                        grepSDP("a=" + evt.candidate.candidate);
                };
                rtc.createOffer(function (offerDesc) {
                    grepSDP(offerDesc.sdp);
                    rtc.setLocalDescription(offerDesc);
                }, function (e) {
                    console.warn("offer failed", e);
                });

                var addrs = Object.create(null);
                addrs["0.0.0.0"] = false;
                function updateDisplay(newAddr) {
                    if (newAddr in addrs)
                        return;
                    else
                        addrs[newAddr] = true;
                    var displayAddrs = Object.keys(addrs).filter(function (k) {
                        return addrs[k];
                    });

                    callback(displayAddrs.join(",") || "127.0.0.1");
                }

                function grepSDP(sdp) {
                    var hosts = [];
                    sdp.split('\r\n').forEach(function (line) { // c.f. http://tools.ietf.org/html/rfc4566#page-39
                        if (~line.indexOf("a=candidate")) {     // http://tools.ietf.org/html/rfc4566#section-5.13
                            var parts = line.split(' '), // http://tools.ietf.org/html/rfc5245#section-15.1
                                    addr = parts[4],
                                    type = parts[7];
                            if (type === 'host')
                                updateDisplay(addr);
                        } else if (~line.indexOf("c=")) {       // http://tools.ietf.org/html/rfc4566#section-5.7
                            var parts = line.split(' '),
                                    addr = parts[2];
                            updateDisplay(addr);
                        }
                    });
                }
            })();
        else {
            //In Chrome and Firefox your IP should display automatically, by the power of WebRTCskull.
            if (element) {
                element.innerHTML = "<code>ifconfig | grep inet | grep -v inet6 | cut -d\" \" -f2 | tail -n1</code>";
                element.changeValue(element.innerHTML);
            }
        }
    }
};

/**
 * Extensiones para elementos de html
 */
Element.addMethods({

    ensureVisible: function(element) {
        element = $(element);
        var parent = element.parentNode;
        if (parent) {
            var parentPosition = parent.positionedOffset().top;
            var minScroll = parentPosition + parent.scrollTop;
            var maxScroll = parentPosition + parent.scrollTop
                    + parent.getHeight();
            var position = element.positionedOffset().top;

            if (position < minScroll) {
                parent.scrollTop = position - parentPosition;
            } else if (position + element.getHeight() > maxScroll) {
                parent.scrollTop = position + element.getHeight()
                        - parent.getHeight() - parentPosition;
            }
        }
    },

    ensureInside: function(element, parent) {
        element = $(element);
        parent = ($(parent) || document.viewport);

        var d = element.getDimensions();
        var vd = parent.getDimensions();

        var p = element.viewportOffset();
        var diff = vd.height - p.top - d.height;
        if (diff < 0) {
            element.style.top = (parseInt("0" + element.style.top, 10) + diff) + "px";
        }

        p = element.viewportOffset();
        diff = p.top;
        if (diff < 0) {
            element.style.top = (parseInt("0" + element.style.top, 10) - diff) + "px";
        }

        p = element.viewportOffset();
        diff = vd.width - p.left - d.width;
        if (diff < 0) {
            element.style.left = (parseInt("0" + element.style.left, 10) + diff) + "px";
        }

        p = element.viewportOffset();
        diff = p.left;
        if (diff < 0) {
            element.style.left = (parseInt("0" + element.style.left, 10) - diff) + "px";
        }
    },

    center: function(element, parent) {
        parent = parent || element.getOffsetParent();

        var top = (parent.getHeight() - element.getHeight()) / 2;
        var left = (parent.getWidth() - element.getWidth()) / 2;

        if (top < 0) {
            top = 0;
        }
        if (left < 0) {
            left = 0;
        }

        element.absolutize();
        element.setStyle( {
            top: top + "px",
            left: left + "px"
        });
    },

    relativize2: function(element) {
        element = $(element);

        var pos = element.positionedOffset();
        element.relativize();
        var newPos = element.positionedOffset();

        var dx = pos[0] - newPos[0];
        var dy = pos[1] - newPos[1];

        element.moveMargin(dx, dy);

        element.nextSiblings().each(function(sibling) {
            if (element.visible()) {
                sibling.moveMargin(dx, dy);
            }
        });
    },

    moveMargin: function(element, dx, dy) {
        if (dx != 0) {
            element.style.marginLeft = (parseInt(element.style.marginLeft) + dx) + "px";
        }
        if (dy != 0) {
            element.style.marginTop = (parseInt(element.style.marginTop) + dy) + "px";
        }
    },

    fireDOMEvent: function(element, event, options) {
        element = $(element);

        options = Object.extend({
            generated: true
        }, options);

        if (document.createEventObject){
            // dispatch para IE
            var evt = document.createEventObject();
            evt.options = options;
            return element.fireEvent('on' + event, evt)
        } else{
            // dispatch para firefox + others
            var evt = document.createEvent("HTMLEvents");
            evt.initEvent(event, true, true); // event type,bubbling,cancelable
            evt.options = options;
            return !element.dispatchEvent(evt);
        }
    },

    getOriginalDimensions: function(element) {
        element = $(element);

        var currentW;
        var currentH;

        if (element.tagName == "IMG") {
            currentW = element.width;
            currentH = element.height;
        } else {
            currentW = element.getWidth();
            currentH = element.getHeight();
        }

        element.removeAttribute("width");
        element.removeAttribute("height");
        var h = element.getHeight();
        var w = element.getWidth();

        if (element.tagName == "IMG") {
            element.width = currentW;
            element.height = currentH;
        } else {
            element.setStyle({
                width: currentW + "px",
                height: currentH + "px"
            });
        }

        return {width: w, height: h};
    },

    hide: function(element) {
        element = $(element);
        element.style.display = 'none';

        var nextElement = element.next();
        if (nextElement && nextElement.hasClassName("asistente-icono")) {
            nextElement.style.display = 'none';
        }

        return element;
    },

    show: function(element) {
        element = $(element);
        element.style.display = '';

        var nextElement = element.next();
        if (nextElement && nextElement.hasClassName("asistente-icono")) {
            nextElement.style.display = '';
        }

        return element;
    }
});

/**
 * Estos metodos sirven para elementos de tipo Form.Element aunque hay que
 * registrarlos en Element directamente
 */
Element.addMethods({

    /**
     * Cambia el valor de un elemento de un formulario.
     *
     * @param element Elemento donde se cambia el valor
     * @param newValue Nuevo valor del elemento
     * @param options Opciones donde se especifica si el evento es parcial
     */
    changeValue: function(element, value, options) {
        var originalValue = element.value;
        element._formatValue(value, options);

        if (originalValue != element.value) {
            element.fireDOMEvent("change", options);
            return true;
        } else {
            return false;
        }
    },

    _processValue: function(element, options) {
        element = $(element);
        var originalValue = element.value;
        var cambios = false;

        if (element.value != "") {
            Validar.ok(element, "required");
        } else {
            Validar.ok(element, "empty");
        }

        element._formatValue(element.value, options);

        if (originalValue != element.value) {
            if (!options.partial) {
                element.fireDOMEvent("change", options);
            }

            cambios = true;
        }

        c.formulario.evalFormulas(element);

        return cambios;
    },

    _formatValue: function(element, value, options) {
        var newValue = value;
        var className = null;
        options = options || {};

        try {
            // Formatear valor con todos los formateadores
            (element.formatters || $A()).each(function(formatter) {
                className = formatter.constructor.simpleClassName;
                newValue = formatter.transform(newValue, options.partial);
                Validar.ok(element, className);
            });
        } catch(e) {
            Validar.error(element, e, className);
            return element.value;
        }

        newValue = typeof newValue != "undefined" && newValue != null && newValue.toString() || "";

        if (element.value != newValue) {
            var pos = element.value.length - element.getCaretPosition();
            //Hack temporal. Ver Incidencia #8266, bug Chrome #152537
            element.value = "";
            element.value = newValue;
            element.setCaretPosition(element.value.length - pos, false);
        }

        (element.formatters || $A()).each(function(formatter) {
            formatter.changeValues(element, options.partial);
        });
    },

    /**
     * Obtiene el objectValue o value adecuadamente
     *
     * @param element Elemento de donde obtener el objectValue
     */
    getObjectValue: function(element) {
        element = $(element);
        return typeof element.objectValue != "undefined" ? element.objectValue : element.value;
    },

    /**
     * Obtiene el objectValue o value adecuadamente
     *
     * @param element Elemento de donde obtener el objectValue
     */
    getWidget: function(element) {
        element = $(element);
        return typeof element.widget != "undefined" ? element.widget : element;
    },

    getCaretPosition: function(element) {
        element = $(element);

        if (!element.focused) {
            return -1;
        }

        if (typeof document.selection != "undefined") {
            // IE
            var sel = document.selection.createRange();
            sel.moveStart('character', -element.value.length);

            return sel.text.length;
        } else if (element.type == "text" && (typeof element.selectionStart != "undefined")) {
            // Otros browsers
            return element.selectionStart;
        }

        return element.value.length;
    },

    setCaretPosition: function(element, pos, focus) {
        element = $(element);

        if (!element.focused && !focus) {
            return;
        } else {
            element.focus();
        }

        if (typeof element.createTextRange != "undefined") {
            // IE
            var range = element.createTextRange();
            range.collapse(true);
            range.moveEnd('character', pos);
            range.moveStart('character', pos);
            range.select();
        } else if (element.type == "text" && (typeof element.setSelectionRange != "undefined")) {
            // Otros browsers
            element.setSelectionRange(pos, pos);
        }
    },

    setChecked: function(elemento, checked, options) {
        if (!Object.isElement(elemento)) {
            elemento = c.$(elemento);
        }

        if (elemento.widget) {
            elemento = elemento.widget;
        }

        elemento.checked = checked;
        elemento.fireDOMEvent("change", options);
    },

    setDisabled: function(elemento, disabled) {
        if (!Object.isElement(elemento)) {
            elemento = c.$(elemento);
        }

        if (elemento.widget) {
            elemento = elemento.widget;
        }

        if (elemento.type == "text"  || elemento.type == "password"
              || elemento.type == "hidden") {
            elemento.readOnly = disabled;
        } else {
            elemento.disabled = disabled;
        }

        if (elemento.hasDatePicker) {
            datePickerController[disabled ? "disable" : "enable"](elemento.id);
        }

        elemento.tabIndex = disabled ? -1 : elemento.originalTabIndex;
    }
});

String.prototype.hashCode = function(){
	var hash = 0;

    for (var i = 0; i < this.length; i++) {
        hash = 31 * hash + this.charCodeAt(i);
        hash = hash & hash;
    }

    return hash;
};

/**
 * Obtiene un array de elementos dado su name. Los parametros pueden ser así:
 *
 * $N(name) => Obtiene un array de elementos buscando en toda la página
 * $N(name, index) => Obtiene el elemento indicado del array
 * $N(base, name) => Obtiene un elemento con el name indicado dentro de la base
 * $N(base, name, index) => Obtiene el elemento indicado del array
 *
 * @param a Ver arriba.
 * @param b Ver arriba.
 * @param c Ver arriba.
 */
var $N = function(a, b, c) {
    var base = null;
    var index = null;
    var name = null;

    if (typeof c == "number") {
        base = a;
        name = b;
        index = c;
    } else if (typeof b == "number") {
        base = document;
        name = a;
        index = b;
    } else if (a && (a.elements || a.select)) {
        base = a;
        name = b;
    } else {
        base = document;
        name = a;
    }

    if (!name) {
        return typeof index == "number" ? null : [];
    }

    var elements = null;

    if (base.getElementsByName) {
        // Más rápido
        elements = $A(base.getElementsByName(name));

    } else if (base.elements) {
        // Optimizado
        elements = $A();
        var els = base.elements;
        for (var i = 0; i < els.length; i++) {
            if (els[i].name == name) {
                elements.push(els[i]);
            }
        }

    } else {
        // Mas lento
        elements = base.select("*[name=" + name + "]");
    }

    return typeof index == "number" ? elements[index] : elements;
};

/**
 * Obtiene un array de valores de un elemento dado su name.
 *
 * @param i, j, k se pasan directo a $N
 */
var $V = function(i, j, k) {
    var n = $N(i, j, k);
    return Object.isArray(n) ? n.invoke("getObjectValue"): n.getObjectValue();
};

/**
 * Obtiene un array de widgets visibles de un elemento dado su name.
 *
 * @param i, j, k se pasan directo a $N
 */
var $W = function(i, j, k) {
    var n = $N(i, j, k);
    return Object.isArray(n) ? n.invoke("getWidget") : n && n.getWidget();
};

var trim = function(s) {
    Logger.trace("Usar mejor s.strip en vez de trim");
    return s.strip();
};

var recargar = function(modulo) {
    modularjs.loading[modulo] = false;
    modularjs.loaded[modulo] = false;
    include(modulo);
};

var rethrow = function(transport, e) {
    console && console.log("Exception", arguments);
    Estatus.mensaje(e && e.message || Mensajes['fitbank.validar.ERROR'],
            e && e.stack, "error");
    throw e;
};

var deepCopy = function(object) {
    if (!object || typeof object != "object") {
        return object;
    } else if (Object.isArray(object)) {
        var copy = $A();

        object.each(function(value) {
            copy.push(deepCopy(value));
        });

        return copy;
    } else {
        var copy = new object.constructor();

        $H(object).each(function(pair) {
            copy[pair.key] = deepCopy(pair.value);
        });

        return copy;
    }
};

var copyConstructor = function(params) {
    if (this.id && this.id.startsWith("_id_")) {
        this.id = Util.generarIdUnicoTemporal();
    }

    $H(this).each(function(pair) {
        this[pair.key] = deepCopy(pair.value);
    }, this);

    if (params) {
        Object.extend(this, params);
    }

    if (this.constructor.className) {
        this.className = this.constructor.className;
        this.simpleClassName = this.constructor.simpleClassName;
    }
};

var caducar = function () {
    window.onbeforeunload = null;
    document.location.href = "caducidad.html#cerrar";
}

Object.extend(Function.prototype, (function() {
    var slice = Array.prototype.slice;

    function tryCatch() {
        var args = slice.call(arguments, 0);
        try {
            return this.apply(this, args);
        } catch (e) {
            Estatus.mensaje(e, e && e.stack, 'error');
            throw e;
        }
    }

    function bindTryCatch() {
        var args = slice.call(arguments, 0);
        var f = this.bind.apply(this, args);
        return f.tryCatch.bind(f);
    }

    return {
        tryCatch: tryCatch,
        bindTryCatch: bindTryCatch,
        tryDefer: tryCatch.defer
    }
})());

var tryEval = function(code) {
    return (function() {
        eval(code);
    }).tryCatch();
};

var tryEvent = function(nameOrId, event, func) {
    var elements = c.$N(nameOrId) || [ $(nameOrId) ];

    elements.each(function(element) {
        if (element.widget) {
            element = element.widget;
        }
        element.on(event, func.bindTryCatch(element));
    });
};

Ajax.Request.addMethods({
    abort: function() {
        this.transport.onreadystatechange = Prototype.emptyFunction;
        this.transport.abort();
        if (Ajax.activeRequestCount > 0) {
            Ajax.activeRequestCount--;
        }
    }
});

document.disableContextMenu = function() {
    if (this.captureEvents) {
        this.captureEvents(Event.MOUSEDOWN);
        this.onmousedown = function(e) {
            if (e.which == 2 || e.which == 3) {
                return false;
            }
        };
    }

    this.oncontextmenu = function() {
        return false;
    };
};
