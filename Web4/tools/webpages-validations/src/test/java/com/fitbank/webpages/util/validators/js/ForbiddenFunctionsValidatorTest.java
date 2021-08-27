package com.fitbank.webpages.util.validators.js;

import com.fitbank.util.Debug;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Casos de prueba para el validador de funciones prohibidas.
 * @author Fitbank RB
 * @see ForbiddenFunctionsValidator
 */
public class ForbiddenFunctionsValidatorTest {
    private static ForbiddenFunctionsValidator validador = new 
            ForbiddenFunctionsValidator();
    
    public static final String CASO1 = 
            "if(c.$('F1Cuota') [i].floatValue == \"\" || c.$('F1Cuota') [i].floatValue == \"0.00\") {"
            + "     alert(\"La cuota no puede ser ni cero ni vacio\");"
            + "     bandera = 1;"
            + "}";
    
    public static final String CASO2 = "if (meta != '') {"
            + "    var metadata = meta.substring(6, largo - 7);"
            + "    c.$('metadata').changeValue(metadata);"
            + "    top.data = eval('[' + c.$('metadata').value + ']');"
            + "    w.data = top.data;"
            + "}";
    
    public static final String CASO3 = "if(c.$('F2CambioF') [p].value == '1') {"
            + "    var FechaActual = stringToFecha(c.$('F2Fcontable') [p].value);"
            + "    if(stringToFecha(c.$('f3212_w1_fvinculacion') [p].value) <= FechaActual) {"
            + "        c.$('f3212_w1_fvinculacion') [p].focus();"
            + "        c.$('f3212_w1_fvinculacion') [p].changeValue('');"
            + "        bandera = 1;"
            + "    }"
            + "}";
    
    @Test
    public void validarCaso1() {
        assertTrue(validador.hasError(CASO1, false));
        Debug.info(validador.getValidationDescription());
    }
    
    @Test
    public void validarCaso2() {
        assertTrue(validador.hasError(CASO2, false));
        Debug.info(validador.getValidationDescription());
    }
    
    @Test
    public void validarCaso3() {
        assertTrue(validador.hasError(CASO3, false));
        Debug.info(validador.getValidationDescription());
    }
}
