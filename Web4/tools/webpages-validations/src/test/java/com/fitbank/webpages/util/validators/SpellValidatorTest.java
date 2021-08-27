package com.fitbank.webpages.util.validators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.inet.jortho.SpellChecker;
import com.inet.jortho.SpellCheckerOptions;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Prueba para validador de ortografía.
 * @author Fitbank RB
 */
public class SpellValidatorTest {
    private static SpellValidator validador = new SpellValidator();

    private static final String CASO1 = "Año Comericial";

    private static final String CASO2 = "INGRESO Y MANTENIMIENTO DE "
            + "REPRESENTANTE LEGAL DE PERSONAS JURIDICAS";

    private static final String CASO3 = "salud SAlud Roberto umberto SRI";

    private static final String CASO4 = "movimiento mobimiento receta reseta "
            + "roseta resetear";

    private static final String CASO5 = "Validar.error(c.$('fecha'), "
            + "'Mobimiento no permitido');";

    @BeforeClass
    public static void inicializar() {
        SpellValidator.loadDictionary("es");
        SpellValidator.setCurrentLang("es");
        SpellCheckerOptions opciones = SpellChecker.getOptions();
        opciones.setIgnoreCapitalization(true);
        opciones.setIgnoreAllCapsWords(false);
        opciones.setCaseSensitive(false);
    }

    @Test
    public void validarCaso1() {
        Set<String> errores = new HashSet<String>();
        validador.checkText(CASO1, errores, false, null);
        assertTrue(errores.size() == 1);
        assertTrue(errores.contains("Comericial"));
    }

    @Test
    public void validarCaso2() {
        Set<String> errores = new HashSet<String>();
        validador.checkText(CASO2, errores, false, null);
        assertTrue(errores.size() == 1);
        assertTrue(errores.contains("JURIDICAS"));
    }

    @Test
    public void validarCaso3() {
        Set<String> errores = new HashSet<String>();
        SpellChecker.getOptions().setCaseSensitive(true);
        validador.checkText(CASO3, errores, false, null);
        assertTrue(errores.size() == 3);
        assertTrue(errores.containsAll(Arrays.asList("SRI", "umberto", "SAlud")));
        SpellChecker.getOptions().setCaseSensitive(false);
    }

    @Test
    public void validarCaso4() {
        Set<String> errores = new HashSet<String>();
        validador.checkText(CASO4, errores, false, null);
        assertTrue(errores.size() == 3);
        assertTrue(errores.containsAll(Arrays.asList("resetear", "reseta",
                "mobimiento")));
    }

    @Test
    public void validarCaso5() {
        Set<String> errores = new HashSet<String>();
        validador.checkJS(CASO5, errores, false, null);
        assertTrue(errores.size() == 1);
        assertTrue(errores.contains("Mobimiento"));
    }
}
