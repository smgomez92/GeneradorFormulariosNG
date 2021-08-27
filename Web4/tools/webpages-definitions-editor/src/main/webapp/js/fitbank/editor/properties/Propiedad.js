Propiedad.addMethods({

    getLabel: function() {
        return new Element('label').update(this.descripcion);
    },

    getListItem: function(list) {
        return null;
    },

    getInput: function(object, owner, propertyName) {
        return null;
    },

    getValue: function(value) {
        return value;
    }

});