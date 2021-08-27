IPFormatter.addMethods( {

    transform: function(value, partial) {
        if (!value) {
            return value;
        }

        var ret = "";
        var values = value.replace(/[^\d\.]/g, "").split(".");

        for ( var i = 0; i < values.length; i++) {
            if (i > 0) {
                ret += ".";
            }
            if (values[i] == "" && i == values.length - 1) {
                continue;
            }
            var n = parseInt(values[i], 10);
            if (isNaN(n) || (n > 255 && i < values.length - 1)) {
                throw new Error("IP4 inválido");
            } else if (n > 255 && i == values.length - 1) {
                var a = Math.floor(n / 10);
                ret += a + "." + (n - a * 10);
            } else if (values[i] == "00") {
                ret += "0.0";
            } else {
                ret += n;
            }
        }

        var partesIP = ret.split(".");

        if (partesIP.length > 4 || (!partial && partesIP.length < 4)) {
            throw new Error("IP4 inválido");
        }

        partesIP.each(function(p) {
            p = p && parseInt(p, 10);
            if (!partial && (!Object.isNumber(p) || p > 255 || p < 0)) {
                throw new Error("IP4 inválido");
            }
        });

        return ret;
    }

});
