package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;

/**
 * Revisa si el formulario realmente requiere join quirk y si no requiere lo
 * quita.
 *
 * @author FitBank CI
 */
public class JoinQuirkValidation extends Validator {

    public static final String WEBPAGE_NO_REQUIERE_JOIN_QUIRK =
            "WEBPAGE_NO_REQUIERE_JOIN_QUIRK";

    @Override
    public Collection<ValidationMessage> validate(final WebPage webPage,
            WebPage fullWebPage) {
        boolean remove = false;

        if (webPage.getReferences().size() <= 1) {
            remove = true;
        } else {
        }

        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        if (remove) {
            messages.add(new ValidationMessage(this,
                    WEBPAGE_NO_REQUIERE_JOIN_QUIRK,
                    webPage, webPage, ValidationMessage.Severity.ERROR, true) {

                @Override
                public void fix() {
                    webPage.setJoinQuirk(false);
                }

            });
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget,
            WebPage fullWebPage) {
        return Collections.EMPTY_LIST;
    }

}
