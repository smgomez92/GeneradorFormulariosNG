include("lib.prototype");
include("lib.datepicker.datepicker");

//Cargar parámetros globales para calendarios
addOnLoad(function() {
    var keyboardControl = Parametros["fitbank.ui.calendar.KEYBOARD_CONTROL"];
    var mousewheelControl = Parametros["fitbank.ui.calendar.MOUSE_WHEEL_CONTROL"];
    var deriveLocale = Parametros["fitbank.ui.calendar.DERIVE_LOCALE"];

    keyboardControl = keyboardControl == "true" ? true : false;
    mousewheelControl = mousewheelControl == "true" ? true : false;
    deriveLocale = deriveLocale == "true" ? true : false;

    datePickerController.setGlobalOptions({
        "buttontabindex" : keyboardControl,
        "mousewheel" : mousewheelControl,
        "derivelocale" : deriveLocale
    });

});

Calendar.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        if (c && c.language) {
            include("lib.datepicker.lang." + c.language.toLowerCase());
        } else {
            include("lib.datepicker.lang.es");
        }

        c && c.$N(this.elementName).each(this.init, this);
    },

    init: function(elemento) {

        if (c && c.language) {
            elemento.writeAttribute({ "lang": c.language.toLowerCase() });
        } else {
            elemento.writeAttribute({ "lang": "es" });
        }

        var elementFormatMap = {};
        elementFormatMap[elemento.id] = this.convertFormat(
            this.dateFormatter.formatString);

        var options = {
            formElements: elementFormatMap,
            showWeeks: this.showWeeks,
            statusFormat: this.statusFormat,
            rangeLow: this.resolveDate(this.minDate),
            rangeHigh: this.resolveDate(this.maxDate),
            disabledDays: this.toNumberArray(this.disabledDays),
            fillGrid: this.fillGrid,
            constrainSelection: false,
            noDrag: !this.draggable,
            finalopacity: 100,
            callbackFunctions: {
                datereturned: [Element.fireDOMEvent.curry(elemento, 'change')],
                //Callbacks agregados a la librería para fitbank
                show: [
                    function(args) {
                        elemento.repositionTimer = setInterval(function() {
                            datePickerController.reposition(elemento.id);
                        }, 500);
                    }
                ],
                hide: [
                    function(args) {
                        clearInterval(elemento.repositionTimer);
                    }
                ],
                startdrag: [
                    function(args) {
                        clearInterval(elemento.repositionTimer);
                    }
                ]
            }
        };

        if (!this.showIcon) {
            var contenedor = new Element('div', {
                id: elemento.id + '-button'
            });

            contenedor.setStyle({
                visibility: 'hidden',
                width: '0px',
                height: '0px',
                margin: '0px',
                padding: '0px',
                overflow: 'hidden'
            });

            elemento.insert({
                after: contenedor
            });

            options.positioned = elemento.id + '-button';
        }

        datePickerController.createDatePicker(options);

        elemento.on('dblclick', function(e) {
            if (!(elemento.hasAttribute('readonly')
                || elemento.hasAttribute('disabled'))) {
                datePickerController.show(elemento.id);
            }
        });

        elemento.assistant = this;
        elemento.hasDatePicker = true;
    },

    /**
     * Convierte la cadena de formato enviada desde el webpages-ide al formato
     * que entiende el widget del calendario.
     */
    convertFormat: function(format) {
        //TODO: Solo se reemplaza la primera ocurrencia de cada token de formato.
        //De ser necesario, crear un lazo para reemplazar todas las ocurrencias.
        var tokens = $H({
            'E': function(str) {
                var regex = /E+/;
                var match = regex.exec(str);
                var len = match && match[0].length || 0;

                if (len == 0) {
                    return str;
                } else if (len < 3) {
                    return str.replace(/E+/, '%D');
                } else if (len > 3) {
                    return str.replace(/E+/, '%l');
                }

                return str;
            },

            'd': function(str) {
                var regex = /([^%d]|^)d+/;
                var match = regex.exec(str);
                var len = match && match[0].length || 0;
                //match contiene el caracter separador también, por ejemplo
                //:dd. Quitar el primer caracter(es) para poder hacer un conteo
                //correcto de los tokens de formato.
                var formatPos = match && match[0].indexOf('d') || 0;
                len -= formatPos;

                if (len == 0) {
                    return str;
                } else if (len == 1) {
                    return str.replace(regex, '$1%j');
                } else if (len > 1) {
                    return str.replace(regex, '$1%d');
                }

                return str;
            },

            'M': function(str) {
                var regex = /([^%M]|^)M+/;
                var match = regex.exec(str);
                var len = match && match[0].length || 0;
                var formatPos = match && match[0].indexOf('M') || 0;
                len -= formatPos;

                if (len == 0) {
                    return str;
                } else if (len < 3) {
                    return str.replace(regex, '$1%m');
                } else if (len == 3) {
                    return str.replace(regex, '$1%M');
                } else if (len > 3) {
                    return str.replace(regex, '$1%F');
                }

                return str;
            },

            'y': function(str) {
                var regex = /([^%y]|^)y+/;
                var match = regex.exec(str);
                var len = match && match[0].length || 0;
                var formatPos = match && match[0].indexOf('y') || 0;
                len -= formatPos;

                if (len == 0) {
                    return str;
                } else if (len < 3) {
                    return str.replace(regex, '$1%y');
                } else if (len >= 3) {
                    return str.replace(regex, '$1%Y');
                }

                return str;
            }
        });

        tokens.values().each(function(transformer) {
            format = transformer(format);
        });

        return format;
    },

    /**
     * Usado para la propiedad disabledDays del calendario. Convierte la lista
     * de días ingresados por el usuario (lun, mar, mié, etc) en un arreglo
     * numérico que representa los días a deshabilitar. Cada posición del arreglo
     * representa un día de la semana, iniciando por lunes (índice 0).
     * Un uno dentro del arreglo indica que el día correspondiente debe deshabilitarse.
     */
    toNumberArray: function(strArray) {
        var arrDays = [0, 0, 0, 0, 0, 0, 0];

        if (!Object.isArray(strArray)) {
            return arrDays;
        }

        var convertFunctions = [
            function(strDay, dayArray) {
                if (/lun/i.test(strDay)) {
                    dayArray[0] = 1;
                }
            },
            function(strDay, dayArray) {
                if (/mar/i.test(strDay)) {
                    dayArray[1] = 1;
                }
            },
            function(strDay, dayArray) {
                if (/mi[eé]/i.test(strDay)) {
                    dayArray[2] = 1;
                }
            },
            function(strDay, dayArray) {
                if (/jue/i.test(strDay)) {
                    dayArray[3] = 1;
                }
            },
            function(strDay, dayArray) {
                if (/vie/i.test(strDay)) {
                    dayArray[4] = 1;
                }
            },
            function(strDay, dayArray) {
                if (/s[aá]b/i.test(strDay)) {
                    dayArray[5] = 1;
                }
            },
            function(strDay, dayArray) {
                if (/dom/i.test(strDay)) {
                    dayArray[6] = 1;
                }
            }
        ];

        for (var i = 0; i < strArray.length; i++) {
            for (var j = 0; j < convertFunctions.length; j++) {
                convertFunctions[j](strArray[i], arrDays);
            }
        }

        return arrDays;
    },

    /**
     * Para las propiedades maxDate y minDate se permite usar variables.
     * Este método convierte el valor de la variable (que se espera que sea un Date)
     * al formato que espera el calendario.
     */
    resolveDate: function(dateStr) {
        if (!dateStr) {
            return '';
        }

        if (dateStr.startsWith('$')) {
            var varName = dateStr.substr(1);
            var value = c[varName];

            if (Object.isDate(value)) {
                return datePickerController.dateToYYYYMMDDStr(value);
            }
        }

        return dateStr;
    }

});
