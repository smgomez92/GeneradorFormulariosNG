include("lib.prototype");

/**
 * @remote
 */
RegexFormatter.addMethods({

    transform: function(value, partial) {
        var re = new RegExp("^" + (partial ? this.partialFormat : this.format) + "$", "gi");
        
        if (!re.test(value)) {
            throw new Error(this.message || "Valor inválido");
        }
        
        return value;
    },

    getLabel: function($super) {
        return $super() + " (" + this.format + ")";
    }

});
