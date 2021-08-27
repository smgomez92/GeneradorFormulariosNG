package com.fitbank.webpages.util.validators.js;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas para el validador de propiedades prohibidas.
 * @author Fitbank RB
 * @see ForbiddenPropertiesValidator
 */
public class ForbiddenPropertiesValidatorTest {
    private ForbiddenPropertiesValidator validador = new ForbiddenPropertiesValidator();
    
    public static final String CASO1 = "c.$('F8Resultado').style.background = 'green';";
    
    public static final String CASO2 = "for(var i=0; i < 20; i++) {"
            + "    c.$('F5numcuenta', i).setStyle({color:'#0000FF', textDecoration:'underline'});"
            + "}";
    
    public static final String CASO3 = "c.$('DF').widget.hide()";
    
    public static final String CASO4 = "c.$('X').checkbox.value";
    
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
        assertTrue(validador.hasError(CASO3, false));
    }
    
    @Test
    public void validarCaso4() {
        assertTrue(validador.hasError(CASO3, false));
    }
}
