package com.fitbank.webpages.util.validators.js;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Casos de prueba para el DisplayNoneValidator.
 * @author Rober
 * @see DisplayNoneValidator
 */
public class DisplayNoneValidatorTest {
    
    private static DisplayNoneValidator validador = new DisplayNoneValidator();
    
    public static final String CASO1 = "c.$('F3Monedalov').setStyle({display:'none'})";
    
    public static final String CASO2 = "c.$('F14Cheque').setStyle({color:'#0000FF', "
            + "display: 'none'});";
    
    public static final String CASO3 = "c.$('X').setStyle({color:'#0000FF', "
            + "textDecoration:'underline', display: 'inline-block'});";
    
    public static final String CASO4 = "c.$('F10boton', p).style.display = 'none'";
    
    public static final String CASO5 = "c.$('XX').style.display='inline';";
    
    public static final String CASO6 = "c.$('XX').style.display = c.$('YY').style.display = 'none';";
    
    public static final String CASO7 = "c.$('xx').style.display = 'block';"
            + "c.$('xx').setStyle({display:'block'})"; 
    
    public static final String CORRECTO1 = "c.$('F3Monedalov').hide();";
    
    public static final String CORRECTO4 = "c.$('F10boton', p).hide();";
    
    public static final String CORRECTO7 = "c.$('xx').show();"
            + "c.$('xx').show();"; 
    
    @Test
    public void validarExpresiones() {
        assertTrue(validador.hasError(CASO1, true));
        assertTrue(validador.hasError(CASO2, true));
        assertFalse(validador.hasError(CASO3, true));
        assertTrue(validador.hasError(CASO4, true));
        assertFalse(validador.hasError(CASO5, true));
        assertTrue(validador.hasError(CASO6, true));
    }
    
    @Test
    public void arreglarCaso1() {
        assertEquals2(CORRECTO1, validador.fixJS(CASO1, true));
    }
    
    @Test
    public void arreglarCaso2() {
        assertEquals2(CASO2, validador.fixJS(CASO2, true));
    }
    
    @Test
    public void arreglarCaso4() {
        assertEquals2(CORRECTO4, validador.fixJS(CASO4, true));
    }
    
    @Test
    public void arreglarCaso6() {
        assertEquals2(CASO6, validador.fixJS(CASO6, true));
    }
    
    @Test
    public void arreglarCaso7() {
        assertEquals2(CORRECTO7, validador.fixJS(CASO7, true));
    }
    
    private void assertEquals2(String expected, String actual) {
        assertEquals(expected.replaceAll("\\s", ""),
                actual.replaceAll("\\s", ""));
    }
}
