include("lib.prototype");
include("lib.simpledateformat");

/**
 * Clase DateFormatter - Soporta un subset de formateo de SimpleDateFormat, en
 * específico los caracteres: y, M, d, H, h, m, s, S. Además solo acepta unos
 * pocos caracteres extraños: " ", "-", "/", ":", ".". Para más información ver:
 * 
 * http://java.sun.com/j2se/1.5.0/docs/api/java/text/SimpleDateFormat.html
 */

Object.extend(DateFormatter, {

    TRANSPORT_FORMAT: /\d{4}-\d{2}-\d{2} \d+:\d+:\d+(.\d+)?/,

    REGEX: /y+|M+|d+|H+|h+|m+|s+|S+|\W+/,

    NUMBER_REGEX: /\d+/,

    CURRENT_YEAR: new Date().getFullYear(),

    formatNumber: function(options, value, partes, token, nextToken, partial,
            length) {
        options = Object.extend({
            min: 0,
            max: Number.MAX_VALUE,
            result: null,
            fixIncomplete: function(numberString, token) {
                return parseInt(numberString, 10).toPaddedString(token.length);
            },
            validate: null
        }, options || {});

        if (!Object.isNumber(length)) {
            if (options.max != Number.MAX_VALUE) {
                length = options.max.toString().length;
            } else {
                length = token.length;
            }
        }

        var numberString = DateFormatter.NUMBER_REGEX.exec(value.substring(0, length));
        var newValue = value.replace(numberString, "");
        numberString = numberString && numberString[0];
        var number = numberString && parseInt(numberString, 10) || 0;

        if (!numberString) {
            throw new Error("Fecha inválida");
        }

        if (options.validate && numberString.length >= length) {
            var message = options.validate.bind(this)(number);
            if (message !== true && message || message === false) {
                throw new Error(message || "Fecha inválida");
            }
        }

        if (number < options.min && value.length >= length) {
            if (DateFormatter.NUMBER_REGEX.test(newValue)) {
                return DateFormatter.formatNumber.bind(this)(options, value,
                        partes, token, nextToken, partial, length - 1);
            } else {
                throw new Error("Valor " + value + " menor al valor mínimo permitido.");
            }
        }

        if (number > options.max) {
            if (length > 1 && nextToken) {
                return DateFormatter.formatNumber.bind(this)(options, value,
                        partes, token, nextToken, partial, length - 1);
            } else {
                throw new Error("Valor " + value + " mayor al valor máximo permitido.");
            }
        }

        if (newValue || numberString.length >= token.length || !partial) {
            if (number == 0) {
                numberString = "";
                for (var a = 0; a < token.length; a++) {
                    numberString += "0";
                }
            } else {
                numberString = options.fixIncomplete.bind(this)(numberString, token);
            }
            
            number = parseInt(numberString, 10);
        }

        partes.push(numberString);

        this[options.result] = number;

        if (newValue && !newValue.startsWith(nextToken)) {
            partes.push(nextToken);
        }

        return newValue;
    }

});

DateFormatter.addMethods({

    tokens: $A(),

    year: 0,

    month: 1,

    date: 0,

    hour: 0,

    minute: 0,

    second: 0,

    millisecond: 0,

    initialize: function($super, parametros) {
        $super(parametros);

        var f = this.formatString;
        while (n = DateFormatter.REGEX.exec(f)) {
            f = f.replace(n[0], "");
            this.tokens.push(n[0]);
        }

        (function() {
            c && c.$N(this.elementName).each(this._createHelper, this);
        }).bind(this).defer();
    },

    _createHelper: function(elemento) {
        var formato = new Element("div", {
            className: "calendar-format"
        }).update(this.formatStringToShow).setStyle({
            "margin-top": elemento.getHeight() + "px"
        });

        Element.insert.defer(elemento, {
            after: formato
        });

        Element.clonePosition.defer(formato, elemento, {
            setWidth: false,
            setHeight: false
        });
    },

    toObject: function(elemento, partial) {
        this.transform(elemento.value, partial);

        if (partial || this.year == null) {
            return null;
        }

        return new Date(this.year, this.month - 1, this.date, this.hour,
                this.minute, this.second, this.millisecond);
    },

    transform: function(value, partial) {
        this.year = this.date = this.hour = this.minute = this.second = this.millisecond = 0;
        this.month = 1;

        if (!value) {
            return "";
        }

        if (value.constructor == Number) {
            value = new Date(value);
        }

        if (!partial && DateFormatter.TRANSPORT_FORMAT.match(value)) {
            value = new Date(value);
        }

        if (value.constructor == Date) {
            if (value.getTime() == new Date(0, 0, 0).getTime()) {
                return "";
            }
            
            value = new SimpleDateFormat(this.formatString).format(value);
        }

        var partes = $A();
        for (var i = 0; i < this.tokens.length; i++) {
            var token = this.tokens[i];
            var nextToken = i < this.tokens.length ? this.tokens[i + 1] : null;

            if (nextToken && this.tokens[nextToken]) {
                nextToken = null;
            }

            if (!value) {
                if (partial) {
                    break;
                } else {
                    throw new Error("Fecha incompleta");
                }
            } else if (this[token]) {
                value = this[token].bind(this)(value, partes, token, nextToken, partial);
            } else if (value.startsWith(token)) {
                partes.push(token);
                value = value.replace(token, "");
            }
        }

        if (value) {
            throw new Error("Fecha inválida");
        }

        value = partes.join("");

        return value;
    },

    "yy": DateFormatter.formatNumber.curry({
        result: "year"
    }),

    "yyyy": DateFormatter.formatNumber.curry({
        result: "year",
        fixIncomplete: function(numberString, token) {
            if (numberString.length <= 2) {
                var number = parseInt(numberString, 10);
                while (number < DateFormatter.CURRENT_YEAR - 90) {
                    number += 100;
                }
                numberString = number + "";
            }

            return parseInt(numberString, 10).toPaddedString(token.length);
        },
        validate: function(number) {
            if (number == 2999) {
                return true;
            } else if (number < 1900) {
                return "Valor " + number + " menor al valor mínimo permitido.";
            } else if (number > DateFormatter.CURRENT_YEAR + 100) {
                return "Valor " + number + " mayor al valor máximo permitido.";
            }

            return true;
        }
    }),

    "M": DateFormatter.formatNumber.curry({
        min: 1,
        max: 12,
        result: "month"
    }),

    "MM": DateFormatter.formatNumber.curry({
        min: 1,
        max: 12,
        result: "month"
    }),

    "d": DateFormatter.formatNumber.curry({
        min: 1,
        max: 31,
        result: "date"
    }),

    "dd": DateFormatter.formatNumber.curry({
        min: 1,
        max: 31,
        result: "date"
    }),

    "h": DateFormatter.formatNumber.curry({
        min: 1,
        max: 12,
        result: "hour"
    }),

    "hh": DateFormatter.formatNumber.curry({
        min: 1,
        max: 12,
        result: "hour"
    }),

    "H": DateFormatter.formatNumber.curry({
        max: 23,
        result: "hour"
    }),

    "HH": DateFormatter.formatNumber.curry({
        max: 23,
        result: "hour"
    }),

    "mm": DateFormatter.formatNumber.curry({
        max: 59,
        result: "minute"
    }),

    "ss": DateFormatter.formatNumber.curry({
        max: 59,
        result: "second"
    }),

    "SS": DateFormatter.formatNumber.curry({
        result: "millisecond"
    }),

    "SSS": DateFormatter.formatNumber.curry({
        result: "millisecond"
    }),

    "SSSS": DateFormatter.formatNumber.curry({
        result: "millisecond"
    })

});
