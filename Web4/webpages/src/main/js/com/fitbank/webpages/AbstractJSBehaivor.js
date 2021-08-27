include("lib.prototype");

include("fitbank.validar");

/**
 * @remote
 */
AbstractJSBehaivor.addMethods({
    
    addEventHandler: function(elemento, callback) {
        var label = $(elemento.getAttribute("labelid"));

        if (label) {
            elemento = label;
        }
        
        if (elemento.widget) {
            elemento = elemento.widget;
        }
        
        if (elemento.hasClassName('button') || elemento.hasClassName('label')) {
            elemento.on("click", callback);
        } else {
            elemento.on("change", callback);
        }
    }

});