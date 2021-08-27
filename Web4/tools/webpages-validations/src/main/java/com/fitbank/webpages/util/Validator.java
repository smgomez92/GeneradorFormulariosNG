package com.fitbank.webpages.util;

import java.util.Collection;
import java.util.LinkedList;

import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;

/**
 * Interfaz que define un validador de un WebPage.
 * 
 * @author Smart Financial System CI
 */
public abstract class Validator {

    public Collection<ValidationMessage> validate(WebPage webPage,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        for (Container container : webPage) {
            messages.addAll(validate(container, fullWebPage));
        }

        return messages;
    }

    public Collection<ValidationMessage> validate(Container container,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        for (Widget widget : container) {
            messages.addAll(validate(widget, fullWebPage));
        }

        return messages;
    }

    public abstract Collection<ValidationMessage> validate(Widget widget,
            WebPage fullWebPage);

}
