include('lib.prototype');

// ////////////////////////////////
// Usadas por formularios

include('fitbank.escaneo');
include('fitbank.formulas');
include('fitbank.reporte');
include('fitbank.huella');

/**
 * Clase Formulario - Clase que se usa para contener informacion sobre el
 * formulario.
 */
var Formulario = Class.create( {

    /**
     * Listas valores cargadas.
     */
    listaValores: null,

    /**
     * Control que tiene foco.
     */
    controlConFoco: null,

    /**
     * Registro activo en el formulario.
     */
    registroActivo: null,

    /**
     * Variables locales seteadas por el formulario.
     */
    vars: null,

    /**
     * Contiene el subsistema del formulario
     */
    subsistema: null,

    /**
     * Contiene la transaccion del formulario
     */
    transaccion: null,

    /**
     * Contiene otras funciones de calculos a ser ejecutadas
     */
    otrosCalculos: null,

    /**
     * Indica si hace una consulta al formulario
     */
    consultando: false,

    /**
     * Contiene las formulas ordenadas por los campos dependientes.
     */
    formulasMap: null,

    /**
     * Contiene las formulas ordenadas por los campos dependientes.
     */
    formulasWithElements: null,

    /**
     * Marca usada para debug en firebug y otros.
     */
    _jsMark: "\n//@ sourceURL=fitbank/proc/formulario/",

    initialize: function(parametros) {
        this.listaValores = $H();
        this.vars = {};
        this.otrosCalculos = $A();
        this.formulas = $A();
        this.formulasMap = $H();
        this.formulasWithElements = $A();
        this.ie = Prototype.Browser.IE;

        Object.extend(this, parametros);

        this.formulas.each(function(formula) {
            if(this.ie == true) {
                formula.eval = tryEval.curry(formula.javaScript);
            } else {
                formula.eval = tryEval.curry(formula.javaScript + this._jsMark +
                    "formulas/" + formula.elementName + ".js");
            }

            if (formula.elements.size) {
                this.formulasWithElements.push(formula);

                formula.elements.each(function(elementName) {
                    if (!this.formulasMap.get(elementName)) {
                        this.formulasMap.set(elementName, $A());
                    }
                    this.formulasMap.get(elementName).push(formula);
                }, this);
            }
        }, this);

        if (typeof this.jsInicial == "string") {
            if(this.ie == true) {
                this.jsInicial = tryEval.curry(this.jsInicial);
            } else {
                this.jsInicial = tryEval.curry(this.jsInicial + this._jsMark + "jsInicial.js");
            }
        }

        if (typeof this.jsInicialWebPage == "string") {
            if(this.ie == true) {
                this.jsInicialWebPage = tryEval.curry(this.jsInicialWebPage);
            } else {
                this.jsInicialWebPage = tryEval.curry(this.jsInicialWebPage
                    + this._jsMark + "jsInicialWebPage.js");
            }
        }

        if (typeof this.calculos == "string") {
            if(this.ie == true) {
                this.calculos = tryEval.curry(this.calculos);
            } else {
                this.calculos = tryEval.curry(this.calculos + this._jsMark + "calculos.js");
            }
        }
    },

    evalFormulas: function(elemento) {
        if (elemento && elemento.oculto) {
            elemento = elemento.oculto;
        }

        if (elemento && elemento.name) {
            var f = this.formulasMap.get(elemento.name);
            f && f.invoke("eval");
        } else {
            this.formulas.invoke("eval");
        }
    },

    evalFormulasWithElements: function() {
        this.formulasWithElements.invoke("eval");
    },

    calcular: function() {
        try {
            this.calculos();
            this.otrosCalculos.invoke("call", this.formulario);
        } catch (e) {
            // Ya se proceso con el tryCatch
        }
    },

    /**
     * Contiene el javascript inicial. Se sobre-escribe al momento de cargar un
     * formulario.
     */
    jsInicial: function() {
    },

    /**
     * Contiene el javascript inicial definido en el webPage. Se sobre-escribe
     * al momento de cargar un formulario.
     */
    jsInicialWebPage: function() {
    },

    /**
     * Contiene las formulas. Se sobre-escribe al momento de cargar
     * un formulario.
     */
    formulas: null,

    /**
     * Contiene los calculos. Se sobre-escribe al momento de cargar un
     * formulario.
     */
    calculos: function() {
    },

    /**
     * Contiene instrucciones a ejecutar antes de efectuar una consulta. Se
     * sobre-escribe al momento de cargar un formulario.
     */
    preConsultar: function() {
        return true;
    },

    /**
     * Contiene instrucciones a ejecutar antes de efectuar un mantenimiento. Se
     * sobre-escribe al momento de cargar un formulario.
     */
    preMantener: function() {
        return true;
    },

    /**
     * Contiene instrucciones a ejecutar despues de efectuar una consulta. Se
     * sobre-escribe al momento de cargar un formulario.
     */
    posConsultar: function() {
        return true;
    },
    
    /**
     * Contiene instrucciones a ejecutar despues de efectuar un mantenimiento. Se
     * sobre-escribe al momento de cargar un formulario.
     */
    posMantener: function() {
        return true;
    }

});
