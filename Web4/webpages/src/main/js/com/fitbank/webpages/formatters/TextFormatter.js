TextFormatter.addMethods({

    getLabel: function($super) {
        return $super() + " (" + this.format + ")";
    }

});
