package com.fitbank.webpages.util.validators.js;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas para el ChangeValueValidator
 * @author Fitbank RB
 * @see ChangeValueValidator
 */
public class ChangeValueValidatorTest {

    private ChangeValueValidator validador = new ChangeValueValidator();

    public static final String CASO1 = "c.$('dsfsfd').value=3;";

    public static final String CASO2 = "c.$('dsfsfd')\n.\nvalue     =    3;";

    public static final String CASO3 = "c.$('f1_fflujocaja')[elemento."
            + "registro].value==''";

    public static final String CASO4 = "if(numMes < 10) {\n"
            + "    c.$('f1_fflujocaja') [0].value = '01-0' + numMes + '-' + "
                + "fecha.getFullYear();\n"
            + "} else {\n"
            + "    c.$('f1_fflujocaja') [0].value = '01-' + numMes + '-' + "
                + "fecha.getFullYear();\n"
            + "}";

    public static final String CASO5 = "c.$('f3200_w3_cpersonabasica').value"
            + "=c.$('f3200_w3_cpersona2').value";

    public static final String CORRECTO1 = "c.$('dsfsfd').changeValue(3);";

    public static final String CORRECTO4 = "if(numMes < 10) {\n"
            + "    c.$('f1_fflujocaja') [0].changeValue('01-0' + numMes + '-' + "
            + "        fecha.getFullYear());\n"
            + "} else {\n"
            + "    c.$('f1_fflujocaja') [0].changeValue('01-' + numMes + '-' + "
            + "        fecha.getFullYear());\n"
            + "}";

    public static final String CORRECTO5 = "c.$('f3200_w3_cpersonabasica')."
            + "changeValue(c.$('f3200_w3_cpersona2').value);";

    @Test
    public void validarExpresiones() {
        assertTrue(validador.hasError(CASO1, false));
        assertTrue(validador.hasError(CASO2, false));
        assertFalse(validador.hasError(CASO3, false));
        assertTrue(validador.hasError(CASO4, false));
        assertTrue(validador.hasError(CASO5, false));
    }

    @Test
    public void arreglarCaso1() {
        assertEquals2(CORRECTO1, validador.fixJS(CASO1, false));
    }

    @Test
    public void arreglarCaso2() {
        assertEquals2(CORRECTO1, validador.fixJS(CASO2, false));
    }

    @Test
    public void arreglarCaso4() {
        assertEquals2(CORRECTO4, validador.fixJS(CASO4, false));
    }

    @Test
    public void arreglarCaso5() {
        assertEquals2(CORRECTO5, validador.fixJS(CASO5, false));
    }

    private void assertEquals2(String expected, String actual) {
        assertEquals(expected.replaceAll("\\s", ""),
                actual.replaceAll("\\s", ""));
    }
}
