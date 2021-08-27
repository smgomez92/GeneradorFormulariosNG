package com.fitbank.webpages.util.validators.js;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Prueba para FixedLimitNodeValidator
 * @author Fitbank RB
 */
public class FixedLimitForValidatorTest {
    private FixedLimitForValidator validador = new FixedLimitForValidator();

    private static String CASO1 = "for(var i = 0; i<5;i++){}";

    private static String CASO2 = "for(var i = 0; i<5, z!=4, k < 10;i++){}";

    private static String CASO3 = "for(; ;i++){}";

    private static String CASO4 = "for(var i = 0; i < campos.length ;i++){}";


    @Test
    public void validarCaso1() {
        assertTrue(validador.hasError(CASO1, false));
    }

    @Test
    public void validarCaso2() {
        assertTrue(validador.hasError(CASO2, false));
    }

    @Test
    public void validarCaso3() {
        assertFalse(validador.hasError(CASO3, false));
    }

    @Test
    public void validarCaso4() {
        assertFalse(validador.hasError(CASO4, false));
    }
}
