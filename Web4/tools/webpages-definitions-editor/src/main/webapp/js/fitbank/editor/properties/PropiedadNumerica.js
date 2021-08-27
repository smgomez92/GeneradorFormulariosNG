PropiedadNumerica.addMethods({

    getInput: function(object) {
        return this.getTextInput(object);
    },

    getTextInput: function(object) {
        var input = new Element("input", {
            type: "text",
            value: object
        });

        input.on("keyup", (function(event) {
            var newstr = this.value;
            var str = "";

            do {
                str = newstr;
                newstr = str.sub(/[^0-9,.-]/, '');
            } while (str != newstr);

            this.value = str;
        }).bind(input));

        return input;
    },
    
    getValue: function(value) {
        return parseInt(value);
    }

});