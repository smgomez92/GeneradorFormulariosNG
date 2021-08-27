package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;
import com.fitbank.webpages.widgets.RadioButton;

public class RadioButtonValidator extends Validator {

    private static final String RADIO_BUTTON_UNICO = "RADIO_BUTTON_UNICO";

    private static final String RADIO_BUTTON_SELECCIONADO =
            "RADIO_BUTTON_SELECCIONADO";

    private static final String RADIO_BUTTON_NO_SELECCIONADO =
            "RADIO_BUTTON_NO_SELECCIONADO";

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage, WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        validate(messages, IterableWebElement.get(webPage, RadioButton.class));

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Container container, WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        validate(messages, IterableWebElement.get(container, RadioButton.class));

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        return Collections.emptyList();
    }

    private void validate(Collection<ValidationMessage> messages,
            Iterable<RadioButton> radioButtons) {
        Map<String, List<RadioButton>> counter =
                new HashMap<String, List<RadioButton>>();

        for (RadioButton radioButton : radioButtons) {
            String name = radioButton.getName();

            if (!counter.containsKey(name)) {
                counter.put(name, new LinkedList<RadioButton>());
            }

            counter.get(name).add(radioButton);
        }

        for (String name : counter.keySet()) {
            List<RadioButton> list = counter.get(name);

            if (list.size() == 1) {
                messages.add(new ValidationMessage(this, RADIO_BUTTON_UNICO,
                        list.get(0), list.get(0),
                        ValidationMessage.Severity.ERROR));
            } else {
                boolean seleccionado = false;
                for (RadioButton radioButton : list) {
                    if (seleccionado
                            && radioButton.getSeleccionadoInicialmente()) {
                        messages.add(new ValidationMessage(this,
                                RADIO_BUTTON_SELECCIONADO, list.get(0),
                                list.get(0), ValidationMessage.Severity.ERROR));
                    } else if (radioButton.getSeleccionadoInicialmente()) {
                        seleccionado = true;
                    }
                }
                if (!seleccionado) {
                    messages.add(new ValidationMessage(this,
                            RADIO_BUTTON_NO_SELECCIONADO, list.get(0), list.get(
                            0), ValidationMessage.Severity.ERROR));
                }
            }
        }
    }

}
