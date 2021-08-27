include("lib.prototype");

/**
 * @remote
 */
FormulaDisabler.addMethods({

    calculos: function(elemento) {
        elemento.setDisabled(this.formulaJS(elemento.registro));
    }

});
