package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.Calendar;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.formatters.DateFormatter;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;

public class DateFormatterValidator extends Validator {

    private static final String CALENDAR_DATEFORMATTER =
            "CALENDAR_DATEFORMATTER";

    private final static Predicate DATE_FORMATTERS = new Predicate() {

        public boolean evaluate(Object object) {
            return object instanceof DateFormatter;
        }

    };

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        if (widget instanceof FormElement) {
            final FormElement formElement = (FormElement) widget;
            boolean tieneDateFormatter = CollectionUtils.exists(formElement.
                    getBehaviors(), DATE_FORMATTERS);

            if (formElement.getAssistant() instanceof Calendar
                    && tieneDateFormatter) {
                messages.add(new ValidationMessage(this, CALENDAR_DATEFORMATTER,
                        widget, widget, true) {

                    @Override
                    public void fix() {
                        formElement.getBehaviors().removeAll(CollectionUtils.
                                select(formElement.getBehaviors(),
                                DATE_FORMATTERS));
                    }

                });
            }
        }

        return messages;
    }

}
