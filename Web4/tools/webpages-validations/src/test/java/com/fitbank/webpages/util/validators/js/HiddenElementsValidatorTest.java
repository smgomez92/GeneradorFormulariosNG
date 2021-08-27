package com.fitbank.webpages.util.validators.js;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Prueba para HiddenElements Validator.
 * @author Fitbank RB
 * @see HiddenElementsValidator
 */
public class HiddenElementsValidatorTest {
    
    public static final String CASO1 = "c.$('F4efectivar', p).checkbox.checked=false;";
    
    public static final String CASO2 = "c.$N('f3202_w1_telefonica', i)"
            + ".combobox.setDisabled(false);";
    
    public static final String CASO3 = "c.$('f2_titrec', 0).widget.hide();"
            + "c.$N('f2_titrec', 0).widget.hide();";
    
    //Esto no debería haber... en caso de encontrar, corregir.
    public static final String CASO4 = "c.$V('F4efectivar', p).checkbox.checked=false;";
    
    public static final String CORRECTO1 = "c.$('F4efectivar', p).checked=false;";
    
    public static final String CORRECTO2 = "c.$('f3202_w1_telefonica', i)"
            + ".setDisabled(false);";
    
    public static final String CORRECTO3 = "c.$('f2_titrec', 0).hide();"
            + "c.$('f2_titrec', 0).hide();";
    
    public static final String CORRECTO4 = "c.$('F4efectivar', p).checked=false;";
    
    private HiddenElementsValidator validador = new HiddenElementsValidator();
    
    @Test
    public void validarExpresiones() {
        assertTrue(validador.hasError(CASO1, true));
        assertTrue(validador.hasError(CASO2, true));
        assertTrue(validador.hasError(CASO3, true));
        assertTrue(validador.hasError(CASO4, true));
    }
    
    @Test
    public void arreglarCaso1() {
        assertEquals2(CORRECTO1, validador.fixJS(CASO1, true));
    }
    
    @Test
    public void arreglarCaso2() {
        assertEquals2(CORRECTO2, validador.fixJS(CASO2, true));
    }
    
    @Test
    public void arreglarCaso3() {
        assertEquals2(CORRECTO3, validador.fixJS(CASO3, true));
    }
    
    @Test
    public void arreglarCaso4() {
        assertEquals2(CORRECTO4, validador.fixJS(CASO4, true));
    }
    
    private void assertEquals2(String expected, String actual) {
        assertEquals(expected.replaceAll("\\s", ""),
                actual.replaceAll("\\s", ""));
    }
}
