package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.LinkedList;

import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.AutoListOfValues;
import com.fitbank.webpages.assistants.ListOfValues;
import com.fitbank.webpages.assistants.SpecialListOfValues;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;

/**
 * Valida varias cosas sobre ListOfValues.
 *
 * @author FitBank CI
 */
public class ListOfValuesValidator extends Validator {

    private static final String LIST_OF_VALUES_DESCRIPTION =
            "LIST_OF_VALUES_DESCRIPTION";

    @Override
    public Collection<ValidationMessage> validate(Widget widget,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        if (widget instanceof FormElement) {
            final FormElement formElement = (FormElement) widget;
            ListOfValues listOfValues =
                    getListOfValues(formElement.getAssistant());

            if (listOfValues == null) {
                return messages;
            }

            if (formElement.getDataSource().esDescripcion()) {
                messages.add(new ValidationMessage(this,
                        LIST_OF_VALUES_DESCRIPTION, widget, widget, true) {

                    @Override
                    public void fix() {
                        formElement.setDataSource(new DataSource());
                    }

                });
            }
        }

        return messages;
    }

    private ListOfValues getListOfValues(Assistant assistant) {
        if (assistant instanceof ListOfValues) {
            return (ListOfValues) assistant;
        } else if (assistant instanceof AutoListOfValues) {
            return ((AutoListOfValues) assistant).generateListOfValues();
        } else if (assistant instanceof SpecialListOfValues) {
            return ((SpecialListOfValues) assistant).generateListOfValues();
        } else {
            return null;
        }
    }

}
