include("lib.prototype");

/**
 * @remote
 */
FormulaHider.addMethods({

    calculos: function(elemento) {
        if(this.formulaJS(elemento.registro)) {
            elemento.hide();
        } else {
            elemento.show();
        }
    }

});
