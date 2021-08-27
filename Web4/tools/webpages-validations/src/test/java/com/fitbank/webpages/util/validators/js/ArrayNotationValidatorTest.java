package com.fitbank.webpages.util.validators.js;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Casos de prueba para la clase ArrayNotationValidator
 * @author Fitbank RB
 * @see ArrayNotationValidator
 */
public class ArrayNotationValidatorTest {

    public static final String CASO1 = "c.  $  ('abcde')  [$D ] .hide();";

    public static final String CASO2 = "c . $  ('F4Cantmaxima')  [p ] .show()";

    public static final String CASO3 = "c . $ (map.get(5))  [p ] .value";

    public static final String CASO4 = "c.$('F4Secuenciaf')[fila2]\n"
            + "if(c.$('F2Ccuentadepositada')[this.registro].value != ''){\n"
            + "    if(c.$('F2Check')[this.registro].checkbox.checked==false) {\n"
            + "         camps[this.registro] = c.$('F2Check')[this.registro]."
            + "                  getObjectValue();\n"
            + "    }\n"
            + "}";

    public static final String CASO5 = "c.$$('.xyz')[0].show();";

    public static final String CASO6 = "x == c.  $N  ('F4Cantmaxima')  [p ];";

    public static final String CASO7 = "c.$V(c.$('F4Cantmaxima').value)[camps[$D]] == x;";

    public static final String CASO8 = "c.$V('abcd')[x]++;";

    public static final String CORRECTO1 = "c.$('abcde', $D).hide();";

    public static final String CORRECTO2 = "c.$('F4Cantmaxima', p).show();";

    public static final String CORRECTO3 = "c.$(map.get(5), p).value;";

    public static final String CORRECTO4 = "c.$('F4Secuenciaf', fila2);\n"
            + "if(c.$('F2Ccuentadepositada', this.registro).value != ''){\n"
            + "    if(c.$('F2Check', this.registro).checkbox.checked==false) {\n"
            + "         camps[this.registro] = c.$('F2Check', this.registro)."
            + "                  getObjectValue();\n"
            + "    }\n"
            + "}";

    public static final String CORRECTO6 = "x == c.$N('F4Cantmaxima', p);";

    public static final String CORRECTO7 = "c.$V(c.$('F4Cantmaxima').value, camps[$D]) == x;";

    public static final String CORRECTO8 = "c.$V('abcd', x)++;";

    private static final ArrayNotationValidator validador = new
            ArrayNotationValidator();

    @Test
    public void validarExpresiones() {
        assertTrue(validador.hasError(CASO1, false));
        assertTrue(validador.hasError(CASO2, false));
        assertTrue(validador.hasError(CASO3, false));
        assertTrue(validador.hasError(CASO4, false));
        assertFalse(validador.hasError(CASO5, false));
        assertTrue(validador.hasError(CASO6, false));
        assertTrue(validador.hasError(CASO7, false));
        assertTrue(validador.hasError(CASO8, false));
    }

    @Test
    public void arreglarCaso1() {
        assertEquals2(CORRECTO1, validador.fixJS(CASO1, false));
    }

    @Test
    public void arreglarCaso2() {
        assertEquals2(CORRECTO2, validador.fixJS(CASO2, false));
    }

    @Test
    public void arreglarCaso3() {
        assertEquals2(CORRECTO3, validador.fixJS(CASO3, false));
    }

    @Test
    public void arreglarCaso4() {
        assertEquals2(CORRECTO4, validador.fixJS(CASO4, false));
    }

    @Test
    public void arreglarCaso6() {
        assertEquals2(CORRECTO6, validador.fixJS(CASO6, false));
    }

    @Test
    public void arreglarCaso7() {
        assertEquals2(CORRECTO7, validador.fixJS(CASO7, false));
    }

    @Test
    public void arreglarCaso8() {
        assertEquals2(CORRECTO8, validador.fixJS(CASO8, false));
    }

    private void assertEquals2(String expected, String actual) {
        assertEquals(expected.replaceAll("\\s", ""),
                actual.replaceAll("\\s", ""));
    }
}
