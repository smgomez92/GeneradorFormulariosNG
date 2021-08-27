include("lib.prototype");

NumberFormatter.addMethods({

    initialize: function($super, parametros) {
        $super(parametros);

        var format = this.format;

        if (format.indexOf(";") == -1) {
            format = format + ";-" + format;
        }

        var parts = format.split(";");

        c.$N(this.elementName).each((function(elemento) {
            if (!elemento.maxLength
                    || elemento.maxLength == 0
                    || elemento.maxLength > 16
                    || elemento.maxLength == -1) {
                //Longitud maxima que permite js para procesos
                elemento.maxLength = "16";
            }

            this.maxLength = elemento.maxLength;
        }).bind(this));

        this._positive = this._readFormat({ stringFormat: parts[0] });
        this._negative = this._readFormat({ stringFormat: parts[1] });
    },

    _readFormat: function(format) {
        var parts = format.stringFormat.split(/[#0]/);

        format.prefix = parts[0];
        format.suffix = parts[parts.length - 1];
        format.minimumIntegers = format.stringFormat.match(/(0*)(?:\.|$)/)[1].length;
        format.minimumFraction = (format.stringFormat.match(/\.(0*)/) || [ "", "" ])[1].length;
        format.fraction = (format.stringFormat.match(/\.([0#]*)/) || [ "", "" ])[1].length;
        format.integers = this.maxLength ? this.maxLength - (format.fraction ? format.fraction + 1 : 0) : Number.MAX_VALUE;
        format.group = format.stringFormat.match(/,([0#]+)(?:\.|$)/)
        if (format.group) {
            format.group = format.group[1].length;
        } else {
            // Infinito
            format.group = 1 / 0;
        }

        return format;
    },

    transform: function(value, partial) {
        if (typeof value == "number") {
            value = value.toString();
        }

        if (!value) {
            return value;
        }

        if (this.like) {
            // Permitir un formato más sencillo para caso que sea criterio LIKE
            return value.replace(/[^\-\d\.\%\_]/g, "").substring(0, this.maxLength);
        }

        var n = this._toFloat(value);

        //Only positive values
        if (this.positivesOnly && n < 0) {
            n *= -1;
        }

        var format = n >= 0 ? this._positive : this._negative;

        // Decimales originales incluyendo el punto
        var original = /\.[\d]*/.exec(this._clean(value));
        original = original && original[0] || "";

        // Variable que contiene el resultado
        var newValue = "";

        // Procesar decimales
        var decimals = this._transformDec(format, partial, original);

        // Procesar enteros
        var integers = Math.floor(Math.abs(n));
        //Agregar accarreo del redondeo de decimales
        integers += decimals[0];

        if (partial) {
            integers = integers.toString();
        } else {
            integers = integers.toPaddedString(format.minimumIntegers);
        }

        if (integers.length > format.integers) {
            integers = integers.substring(0, format.integers);
        }

        // Agregar comas
        while (integers.length > format.group) {
            var x = integers.length - format.group;
            newValue = "," + integers.substring(x, integers.length) + newValue;
            integers = integers.substring(0, x);
        }

        newValue = integers + newValue;

        // Agregar parte decimal
        newValue += decimals[1];

        if (newValue) {
            newValue = format.prefix + newValue + format.suffix;
        }

        return newValue;
    },

    _transformDec: function(format, partial, original) {
        if (format.fraction == 0) {
            return [0, ""];
        }

        if (original.length <= 1 && partial) {
            return [0, original];
        }

        var decimals = original.length > 1 ? original.substring(1) : "";
        var carry = 0;

        // Redondear decimales
        if (decimals.length > format.fraction) {
            var dec10 = Math.pow(10, format.fraction);
            decimals = parseFloat("0." + decimals, 10);
            decimals = Math.round(decimals * dec10) / dec10;

            if (decimals >= 1) {
                carry = decimals;
            }

            decimals = decimals.toString().substring(2);
        }

        // Completar con 0 los decimales obligatorios
        if (!partial || decimals.length < original.length - 1) {
            var max = Math.max(format.minimumFraction, original.length - 1);
            max = Math.min(max, format.fraction);
            while (decimals.length < max) {
                decimals += "0";
            }
        }

        return [carry, "." + decimals];
    },

    /**
     * Obtiene siempre un numero positivo como String.
     *
     * @param value Valor que se debe limpiar.
     */
    _clean: function(value) {
        return value && value.replace(/[^\d\.]/g, "") || "";
    },

    /**
     * Convierte a float el valor indicado.
     *
     * @param value Valor que se debe convertir.
     */
    _toFloat: function(value) {
        var res = parseFloat(this._clean(value), 10) || 0.0;

        // TODO manejar mejor nÃºmeros negativos
        if (value.startsWith(this._negative.prefix) || value.indexOf("-") != -1) {
            res = -res;
        }

        return  res;
    },

    toObject: function(elemento) {
        if (this.criterion) {
            return elemento.value;
        }

        var value = elemento.value;

        if (typeof value == "number") {
            value = value.toString();
        }

        return this._toFloat(value);
    }

});
