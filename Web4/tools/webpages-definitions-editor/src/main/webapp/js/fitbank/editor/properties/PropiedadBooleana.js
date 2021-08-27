PropiedadBooleana.addMethods({

    getInput: function(object) {
        var input = new Element('input', {
            type: 'checkbox',
            checked: object
        });

        return input;
    }

});