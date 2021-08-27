UpperCaseFormatter.addMethods( {

    transform: function(value, partial) {
        return value.toUpperCase && value.toUpperCase() || value;
    }

});
