package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.LinkedList;

import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.formulas.Formula;
import com.fitbank.webpages.formulas.FormulaParser;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.ValidationMessage.Severity;
import com.fitbank.webpages.util.Validator;

/**
 * Valida que una fórmula sea válida
 *
 * @author FitBank CI
 */
public class FormulasValidator extends Validator {

    public static final String ELEMENTO_NO_ENCONTRADO = "ELEMENTO_NO_ENCONTRADO";

    @Override
    public Collection<ValidationMessage> validate(Widget widget,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        if (widget instanceof FormElement) {
            FormElement formElement = (FormElement) widget;

            if (formElement.getRelleno().startsWith("=")) {
                Formula formula = FormulaParser.parse(formElement);

                for (String element : formula.getElements()) {
                    if (fullWebPage.findFormElement(element) == null) {
                        messages.add(new ValidationMessage(this,
                                ELEMENTO_NO_ENCONTRADO, "Elemento: " + element,
                                widget, widget, Severity.ERROR));
                    }
                }
            }
        }

        return messages;
    }

}
