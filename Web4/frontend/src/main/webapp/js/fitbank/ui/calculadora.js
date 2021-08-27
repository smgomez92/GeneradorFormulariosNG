include("lib.calculator.jquery-plugin");
include("lib.calculator.jquery-calculator");
include("lib.calculator.jquery-calculator-es");

/**
 * Namespace Calculadora - Define funciones para manejo de la calculadora.
 */
var Calculadora = {

    init: function() {
        window.$calc = jQuery.calculator
    },

    ver: function() {
        var el = new Element("div");

        var ventana = new Ventana({
            titulo: "Calculadora",
            contenido: el,
            verFondo: false
        });

        var value = c.$V(c.formulario.controlConFoco, c.formulario.registroActivo);

        jQuery(el).calculator({

            value: isNaN(value) ? 0 : value,

            calculatorClass: 'noborder',

            decimalChar: '.',

            backspaceText: '⇤',

            clearText: 'C',

            layout: [
                '_ MCM+MR_ _%',
                '_ CABSSR_ _/',
                '_ _7_8_9_ _+',
                '_ _4_5_6_ _-',
                '_ _1_2_3_ _*',
                '_ _0+-_._ _=',
                '_   ' + $calc.USE
            ],

            onClose: function(value, inst) {
                ventana.cerrar();
                var curr = c.$N(c.formulario.controlConFoco, c.formulario.registroActivo || 0);
                if (curr && curr.tabIndex !== -1 && value) {
                    curr.changeValue(value);
                }
            }

        });

        ventana.ver();
    }

};

Calculadora.init();
