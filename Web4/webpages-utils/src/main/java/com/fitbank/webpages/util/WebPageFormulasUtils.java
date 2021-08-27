package com.fitbank.webpages.util;

import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.formulas.Formula;
import com.fitbank.webpages.formulas.FormulaException;
import com.fitbank.webpages.formulas.FormulaParser;

/**
 * Clase que ayuda a definir formulas en elementos.
 *
 * @author FitBank CI
 */
public class WebPageFormulasUtils {

    public static void process(FormElement formElement, Object context) throws FormulaException {
        if (!formElement.getRelleno().startsWith("=")) {
            return;
        }

        Formula formula = FormulaParser.parse(formElement);

        WebPageEnviroment.addFormula(formula);

        formElement.actualizarPropiedadesValores();
    }

}
