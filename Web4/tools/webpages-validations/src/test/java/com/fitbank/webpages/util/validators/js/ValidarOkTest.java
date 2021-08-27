package com.fitbank.webpages.util.validators.js;

import org.apache.commons.lang.StringEscapeUtils;
import static org.junit.Assert.*;
import org.junit.Test;

public class ValidarOkTest {

    public static final String OTRO1 = "otro1();";

    public static final String OTRO2 = "otro2();";

    public static final String BASE1 = OTRO1
            + "if (true) { Validar.error(c.$('abcde')); }" + OTRO2;

    public static final String BASE2 = OTRO1
            + "if (true) { Validar.error(c.$('abcde'), 'Error'); }" + OTRO2;

    public static final String BASE3 = OTRO1
            + "if (true) { Validar.error(c.$('abcde', 3), 'Error, coma'); }"
            + OTRO2;

    public static final String BASE4 = OTRO1
            + "if (true) { Validar.error(c.$('abcde')[3]); }" + OTRO2;

    public static final String BASE5 = OTRO1
            + "if (true) { Validar.error(c.$('abcde')[3], 'Mensaje'); }" + OTRO2;

    public static final String BASE6 = OTRO1
            + "if (true) { Validar.error(c.$('abcde'), null, 'error'); }" + OTRO2;

    public static final String VALIDAR_OK = "Validar.ok(c.$('abcde'));\n";

    public static final String VALIDAR_OK3 = "Validar.ok(c.$('abcde', 3));\n";

    public static final String VALIDAR_OK4 = "Validar.ok(c.$('abcde')[3]);\n";

    public static final String VALIDAR_OK5 = "Validar.ok(c.$('abcde')[3]);\n";

    public static final String VALIDAR_OK6 = "Validar.ok(c.$('abcde'), 'error');";

    public ValidarOkTest() {
    }

    @Test
    public void testValidarError1() {
        assertEquals2(VALIDAR_OK + BASE1, new ValidarOk().fixJS(BASE1, false));
    }

    @Test
    public void testValidarError2() {
        assertEquals2(VALIDAR_OK + BASE2, new ValidarOk().fixJS(BASE2, false));
    }

    @Test
    public void testValidarError3() {
        assertEquals2(VALIDAR_OK3 + BASE3, new ValidarOk().fixJS(BASE3, false));
    }

    @Test
    public void testValidarError4() {
        assertEquals2(VALIDAR_OK4 + BASE4, new ValidarOk().fixJS(BASE4, false));
    }

    @Test
    public void testValidarError5() {
        assertEquals2(VALIDAR_OK5 + BASE5, new ValidarOk().fixJS(BASE5, false));
    }

    @Test
    public void testValidarError6() {
        assertEquals2(VALIDAR_OK6 + BASE6, new ValidarOk().fixJS(BASE6, false));
    }

    @Test
    public void testFixFunctions() {
        String pre = "x = function() {";
        String pos = "};";

        String original = pre + BASE1 + pos;
        String esperado = pre + VALIDAR_OK + BASE1 + pos;

        assertEquals2(esperado, new ValidarOk().fixJS(original, false));
    }

    @Test
    public void testFixFor() {
        String pre = "for (var i = 0; i < 10; i++) {";
        String pos = "}";

        String original = pre + BASE1 + pos;
        String esperado = pre + VALIDAR_OK + BASE1 + pos;

        assertEquals2(esperado, new ValidarOk().fixJS(original, false));
    }

    @Test
    public void testFixEvents() {
        String pre = "onclick=\"";
        String pos = "\"";

        String original = pre + StringEscapeUtils.escapeHtml(BASE1) + pos;
        String esperado = pre + StringEscapeUtils.escapeHtml(VALIDAR_OK + BASE1)
                + pos;

        assertEquals2(esperado, new ValidarOk().fixEvents(original));
    }

    private void assertEquals2(String expected, String actual) {
        assertEquals(expected.replaceAll("\\s", ""),
                actual.replaceAll("\\s", ""));
    }

}