PropiedadSimple.addMethods({

    getInput: function(object, owner, propertyName) {
        var input = new Element('input', {
            type: 'text',
            value: object
        });

        var getList = owner.constructor.getGeneratorProperties().autocomplete[propertyName];

        if (getList) {
            (function() {
                var autocomplete = new Element("div", {
                    className: "autocomplete"
                }).hide();

                input.insert( {
                    after: autocomplete
                });

                new Autocompleter.Local(input, autocomplete,
                    getList.bind(owner)(), {
                        minChars : 0
                    });
            }).defer();
        }

        return input;
    }

});
