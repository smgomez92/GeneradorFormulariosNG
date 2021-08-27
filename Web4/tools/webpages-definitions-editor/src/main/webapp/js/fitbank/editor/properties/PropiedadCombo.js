PropiedadCombo.addMethods({

    getInput: function(object) {
        var select = new Element("select");

        $H(this.etiquetas).each(function(pair) {
            var opt = new Option(pair.key, pair.value);

            if (this.etiqueta == pair.key) {
                opt.defaultSelected = true;
                opt.selected = false;
            }

            if (object == pair.value) {
                opt.selected = true;
            }

            select.options.add(opt, null);
        }.bind(this));

        return select;
    }

});