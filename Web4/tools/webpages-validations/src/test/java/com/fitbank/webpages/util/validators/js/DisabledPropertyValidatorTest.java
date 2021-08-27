package com.fitbank.webpages.util.validators.js;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Prueba para el DisabledPropertyValidator.
 * @author Fitbank RB
 */
public class DisabledPropertyValidatorTest {
    
    private static DisabledPropertyValidator validador = new DisabledPropertyValidator();
    
    public static final String CASO1 = "c.$('deleteP', p).checkbox.disabled = true;";
    
    public static final String CASO2 = "c.$('F1Diafijo').disabled = true;";
    
    public static final String CASO3 = "c.$('crearconyuge').disabled = false;";
    
    public static final String CASO4 = "c.$('F1Diafijo').disabled = "
            + "c.$('botonTXT').disabled = false;";
    
    public static final String CORRECTO1 = "c.$('deleteP', p).checkbox.setDisabled(true);";
    
    public static final String CORRECTO2 = "c.$('F1Diafijo').setDisabled(true);";
    
    public static final String CORRECTO3 = "c.$('crearconyuge').setDisabled(false);";
    
    @Test
    public void validarCasos() {
        assertTrue(validador.hasError(CASO1, false));
        assertTrue(validador.hasError(CASO2, false));
        assertTrue(validador.hasError(CASO3, false));
        assertTrue(validador.hasError(CASO4, false));
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
        //Probar que no daña el script si no puede resolver el error.
        assertEquals2(CASO4, validador.fixJS(CASO4, false));
    }
    
    private void assertEquals2(String expected, String actual) {
        assertEquals(expected.replaceAll("\\s", ""),
                actual.replaceAll("\\s", ""));
    }
}
