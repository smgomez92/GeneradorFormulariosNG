package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;
import com.fitbank.webpages.widgets.RadioButton;

public class NameValidator extends Validator {

    private static final String NAME_RADIO_BUTTON = "NAME_RADIO BUTTON";

    private static final String NAME_DUPLICADO = "NAME_DUPLICADO";

    private static final String NAME_CON_ESPACIOS = "NAME_CON_ESPACIOS";

    private static final String NAME_CON_CHAR_ESPECIAL =
            "NAME_CON_CHAR_ESPECIAL";

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage, WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        validate(messages, IterableWebElement.get(fullWebPage, FormElement.class));

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Container container, WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        validate(messages, IterableWebElement.get(container, FormElement.class));

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        return Collections.emptyList();
    }

    private void validate(Collection<ValidationMessage> messages,
            Iterable<FormElement> formElements) {
        Set<String> names = new HashSet<String>();
        Set<String> namesRadioButtons = new HashSet<String>();

        for (FormElement formElement : formElements) {
            String name = formElement.getName();
            
            if (StringUtils.isBlank(name)) {
                return;
            }

            if (name.indexOf(" ") >= 0) {
                messages.add(new ValidationMessage(this, NAME_CON_ESPACIOS,
                        (Widget) formElement, formElement,
                        ValidationMessage.Severity.ERROR));
            } else if (!name.matches("(\\w[\\w\\n_]*)")) {
                messages.add(new ValidationMessage(this,
                        NAME_CON_CHAR_ESPECIAL, (Widget) formElement,
                        formElement, ValidationMessage.Severity.ERROR));
            }
            
            if (formElement instanceof RadioButton) {
                if (names.contains(name)) {
                    messages.add(new ValidationMessage(this, NAME_RADIO_BUTTON,
                            (Widget) formElement, formElement,
                            ValidationMessage.Severity.ERROR));
                }
                
                namesRadioButtons.add(name);
                
            } else {
                if (namesRadioButtons.contains(name)) {
                    messages.add(new ValidationMessage(this, NAME_RADIO_BUTTON,
                            (Widget) formElement, formElement,
                            ValidationMessage.Severity.ERROR));
                } else if (names.contains(name)) {
                    messages.add(new ValidationMessage(this, NAME_DUPLICADO,
                            "Otro input ya tiene el nombre '" + name + "' en "
                            + "este formulario o en un adjunto.",
                            (Widget) formElement, formElement,
                            ValidationMessage.Severity.ERROR));
                }
                
                names.add(name);
            }
        }
    }

}
