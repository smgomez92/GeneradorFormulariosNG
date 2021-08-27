package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.fitbank.webpages.JSBehavior;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.Calendar;
import com.fitbank.webpages.formatters.DateFormatter;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;
import com.fitbank.webpages.widgets.Input;

/**
 * Agrega un formateador de fecha para ciertas formulas si es necesario.
 *
 * @author FitBank CI, HB
 */
public class FormulaDateFormatter extends Validator {

    public static final String DATE_BEHAVIOR = "DATE_BEHAVIOR";

    public static final String FORMULAS_REGEX = "=\\$(accountingDate)";

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        if (!(widget instanceof Input)) {
            return Collections.emptyList();
        }

        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        final Input input = (Input) widget;

        if (input.getValueInicial().matches(FORMULAS_REGEX)) {
            for (JSBehavior behavior : input.getBehaviors()) {
                if (behavior instanceof DateFormatter) {
                    return Collections.EMPTY_LIST;
                }
            }

            if (input.getAssistant() instanceof Calendar) {
                return Collections.EMPTY_LIST;
            }

            messages.add(new ValidationMessage(this, DATE_BEHAVIOR, input, input,
                    true) {

                @Override
                public void fix() {
                    DateFormatter df = new DateFormatter();
                    df.setFormat(DateFormatter.DateFormat.DATE);
                    input.getBehaviors().add(df);
                }

            });
        }

        return messages;
    }

}
