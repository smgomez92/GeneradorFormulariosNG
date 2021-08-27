package com.fitbank.webpages.formulas;

import junit.framework.TestCase;

import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.widgets.Input;

/**
 * Clase para probar formulas.
 *
 * @author FitBank CI
 */
public class FormulaTestCase extends TestCase {

    private WebPage webPage = new WebPage();

    private Container container = new Container();

    private Input x;

    private Input a;

    private Input b;

    private Input c;

    private Input d;

    private Input e;

    private Input f;

    public void setUp() {
        webPage = new WebPage();
        container = new Container();
        webPage.add(container);

        x = getInput("X");
        a = getInput("A");
        b = getInput("B");
        c = getInput("C");
        d = getInput("D");
        e = getInput("E");
        f = getInput("F");
    }

    private Input getInput(String name) {
        Input input = new Input();
        input.setName(name);
        container.add(input);
        return input;
    }

    private Formula testFormula(String input, String js) {
        x.setValueInicial(input);

        Formula formula = FormulaParser.parse(x);
        assertEquals(js, formula.getJavaScript());
        return formula;
    }

    private String wrap(String js) {
        return "Formulas.execute('X', " + FormulaParser.PREV + js + FormulaParser.POST + ");";
    }

    public void testSimple() {
        Formula formula = testFormula("=A", wrap("c.$N('A')"));
        assertEquals(1, formula.getElements().size());
    }

    public void testParenthesis() {
        Formula formula = testFormula("=A * (D + E)", wrap("_(c.$N('A')) * _(_(c.$N('D')) + _(c.$N('E')))"));
        assertEquals(3, formula.getElements().size());
    }

    public void testNumbers() {
        Formula formula = testFormula("=A * 123.45", wrap("_(c.$N('A')) * 123.45"));
        assertEquals(1, formula.getElements().size());
    }

    public void testSimpleContexto() {
        Formula formula = testFormula("=$user", wrap("c.user"));
        assertEquals(0, formula.getElements().size());
    }

    public void testSuma() {
        Formula formula = testFormula("=A + B", wrap("_(c.$N('A')) + _(c.$N('B'))"));
        assertEquals(2, formula.getElements().size());
    }

    public void testFuncion0() {
        Formula formula = testFormula("=DATE()", wrap("Formulas['DATE'].call(__)"));
        assertEquals(0, formula.getElements().size());
    }

    public void testFuncion1() {
        Formula formula = testFormula("=SUM(D)", wrap("Formulas['SUM'].call(__, c.$N('D'))"));
        assertEquals(1, formula.getElements().size());
    }

    public void testFuncion2() {
        Formula formula = testFormula("=SUM(D; E)", wrap("Formulas['SUM'].call(__, c.$N('D'), c.$N('E'))"));
        assertEquals(2, formula.getElements().size());
    }

    public void testFuncion3() {
        Formula formula = testFormula("=SUM(D, E)", wrap("Formulas['SUM'].call(__, c.$N('D'), c.$N('E'))"));
        assertEquals(2, formula.getElements().size());
    }

    public void testComplejo1() {
        Formula formula = testFormula("=$A * (B + C) / SUM(D)", wrap("_(c.A)"
                + " * _(_(c.$N('B')) + _(c.$N('C')))"
                + " / _(Formulas['SUM'].call(__, c.$N('D')))"));
        assertEquals(3, formula.getElements().size());
    }

    public void testComplejo2() {
        Formula formula = testFormula("=$A * (B + C) / SUM(D; E + F)", wrap("_(c.A)"
                + " * _(_(c.$N('B')) + _(c.$N('C')))"
                + " / _(Formulas['SUM'].call(__, c.$N('D'), _(c.$N('E')) + _(c.$N('F'))))"));
        assertEquals(5, formula.getElements().size());
    }

    public void testStringSimple() {
        Formula formula = testFormula("='ABC'", wrap("'ABC'"));
        assertEquals(0, formula.getElements().size());
    }

    /**
     * El test prueba un string asi: ="\"=ABC\#\""
     * Se prueba:
     * 1. Escape de comillas dobles: \"
     * 2. Escapes invalidos que se eliminan (solo el backslash): \# => #
     */
    public void testStringDoble() {
        Formula formula = testFormula("=\"=\\\"ABC\\#\\\"\"", wrap("'=\\\"ABC#\\\"'"));
        assertEquals(0, formula.getElements().size());
    }

}
