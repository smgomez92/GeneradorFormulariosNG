include("lib.prototype");

include("fitbank.util");
include("fitbank.validar");

/**
 * Namespace Formulas - Contiene las funciones de las formulas de los formularios.
 */
var Formulas = {

    DATE0: new Date(0, 0, 0, 0, 0, 0, 0),

    execute: function(name, func) {
        c.$N(name).each(function (element) {
            try {
                element.changeValue(func.call(element, element.registro), {
                    formula: true
                });
                Validar.ok(element, "formulas");
            } catch (e) {
                Validar.error(element, e, "formulas");
            }
        });
    },

    resolve: function(record, value) {
        if (Object.isElement(value)) {
            return value.getObjectValue();
        } else if (Object.isArray(value)) {
            if (value.length > record) {
                return Formulas.resolve(record, value[record]);
            } else {
                return Formulas.resolve(record, value[0]);
            }
        } else {
            return value;
        }
    },

    noDate: function(d) {
        return !d || d.constructor != Date || d.getTime() == Formulas.DATE0.getTime();
    },

    IF: function(a, b, c) {
        return a ? b : c;
    },

    NOT: function(a) {
        return !a;
    },

    EMPTY: function(e) {
        return e[e.length > this.record ? this.record : 0].value == '';
    },

    SUM: function(values) {
        var res = 0.0;

        values.each(function (e) {
            res += this.resolve(e);
        }, this);

        return res;
    },

    POWER: function(number, power) {
        number = this.resolve(number);
        power = this.resolve(power);
        return Math.pow(number, power);
    },

    ROUND: function(number, digits) {
        number = this.resolve(number);
        return Math.round(number, digits);
    },
    
    CEIL: function(number) {
        number = this.resolve(number);
        return Math.ceil(number);
    },
    
    FLOOR: function(number) {
        number = this.resolve(number);
        return Math.floor(number);
    },

    MAX: function(a, b) {
        if (Object.isArray(a)) {
            var max = -Number.MAX_VALUE;

            a.each(function (e) {
                max = Math.max(max, this.resolve(e));
            }, this);

            return max;
        } else {
            a = this.resolve(a);
            b = this.resolve(b);

            return Math.max(a, b);
        }
    },

    AGE: function(d) {
        d = this.resolve(d);

        if (Formulas.noDate(d)) {
            return "";
        }

        var today = new Date();
        var y = today.getFullYear() - d.getFullYear();
        var m = today.getMonth() - d.getMonth();
        var day = today.getDate() - d.getDate();

        if ((today.getMonth() == d.getMonth() && day < 0) || m < 0) {
            y -= 1;
        }

        return y;
    },

    FULLAGE: function(d) {
        d = this.resolve(d);

        if (Formulas.noDate(d)) {
            return "";
        }

        var today = new Date();
        var y = today.getFullYear() - d.getFullYear();
        var m = today.getMonth() - d.getMonth();
        var day = today.getDate() - d.getDate();

        if ((today.getMonth() == d.getMonth() && day < 0) || m < 0) {
            y -= 1;
            m += 12;
        }

        if (y < 0) {
            throw new Error("Fecha de nacimiento inválida");
        }

        if (day < 0) {
            m -= 1;
        }

        return y + " años " + m + " meses";
    },

    NOW: function() {
        return new Date();
    },

    TODAY: function() {
        var res = new Date();

        // TODO revisar si es que hay que usar setUTCXXX en vez de setXXX
        res.setHours(0);
        res.setMinutes(0);
        res.setSeconds(0);
        res.setMilliseconds(0);

        return res;
    },

    DATEDIF: function(d1, d2, format) {
        d1 = this.resolve(d1);
        d2 = this.resolve(d2);

        if (Formulas.noDate(d1) || Formulas.noDate(d2)) {
            return Number.NaN;
        }

        format = format.toLowerCase();

        if (format == "d") {
            return (d2 - d1) / (1000 * 60 * 60 * 24);

        } else if (format == "m") {
            var months = (d2.getFullYear() - d1.getFullYear()) * 12;
            months += d2.getMonth() - d1.getMonth();
            months -= (d2.getDate() < d1.getDate()) ? 1 : 0;
            return months;

        } else if (format == "y") {
            return d2.getFullYear() - d1.getFullYear();

        } else if (format == "yd" || format == "ym") {
            return Formulas.DATEDIF(d1, new Date(d1.getFullYear(),
                    d2.getMonth(), d2.getDate()), format[1]);

        } else if (format == "md") {
            return Formulas.DATEDIF(d1, new Date(d1.getFullYear(),
                    d1.getMonth(), d2.getDate()), format[1]);
        }

        return Number.NaN;
    },

    YEAR: function(d) {
        d = this.resolve(d);
        return !Formulas.noDate(d) && d.getFullYear() || 0;
    },

    MONTH: function(d) {
        d = this.resolve(d);
        return !Formulas.noDate(d) && d.getMonth() + 1 || 1;
    },

    DAY: function(d) {
        d = this.resolve(d);
        return !Formulas.noDate(d) && d.getDate() || 1;
    },

    HOUR: function(d) {
        d = this.resolve(d);
        return !Formulas.noDate(d) && d.getHours() || 0;
    },

    MINUTE: function(d) {
        d = this.resolve(d);
        return !Formulas.noDate(d) && d.getMinutes() || 0;
    },

    SECOND: function(d) {
        d = this.resolve(d);
        return !Formulas.noDate(d) && d.getSeconds() || 0;
    },

    DATE: function(y, m, d) {
        return new Date(y, m - 1, d);
    },

    TIME: function(h, m, s) {
        return new Date(0, 0, 0, h, m, s);
    },

    CONCATENATE: function() {
        return $A(arguments).collect(this.resolve.bind(this)).join("");
    },

    TEXT: function(value, format) {
        value = this.resolve(value);

        if (value == "" || Formulas.noDate(value)) {
            return "";
        }

        if (format.match("[#0]")) {
            return new NumberFormatter({
                format: format
            }).transform(value);
        } else {
            return new SimpleDateFormat(format, c.language.toLowerCase()).format(value);
        }
    }

};
