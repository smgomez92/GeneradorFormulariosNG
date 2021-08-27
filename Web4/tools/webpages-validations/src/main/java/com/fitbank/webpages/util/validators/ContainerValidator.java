package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;
import com.fitbank.webpages.widgets.Label;
import org.parboiled.common.StringUtils;

public class ContainerValidator extends Validator {

    public static final String SIN_CONTAINERS = "SIN_CONTAINERS";

    public static final String SIN_WIDGETS = "SIN_WIDGETS";

    public static final String SIN_HEADER_SEPARATOR = "SIN_HEADER_SEPARATOR";

    public static final String FOOTER_ANTES_DE_HEADER = "FOOTER_ANTES_DE_HEADER";

    private static final String HEADER_SEPARATOR_0 = "HEADER_SEPARATOR_0";

    private static final String FOOTER_SEPARATOR_ULTIMO =
            "FOOTER_SEPARATOR_ULTIMO";

    private static final String HEADER_SEPARATOR_SIZE = "HEADER_SEPARATOR_SIZE";

    public static final String CON_HEADER_SEPARATOR = "CON_HEADER_SEPARATOR";

    public static final String CON_FOOTER_SEPARATOR = "CON_FOOTER_SEPARATOR";

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages = super.validate(webPage, null);

        if (webPage.isEmpty()) {
            messages.add(new ValidationMessage(this, SIN_CONTAINERS, webPage,
                    webPage, ValidationMessage.Severity.ERROR));
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(final Container container,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        if (isEmpty(container)) {
            messages.add(new ValidationMessage(this, SIN_WIDGETS, container,
                    container, ValidationMessage.Severity.ERROR));
        }

        final int indexOfHeaderSeparator = container.indexOfHeaderSeparator();
        final int indexOfFooterSeparator = container.indexOfFooterSeparator();

        switch (container.getType()) {
            case TABLA:
                if (indexOfHeaderSeparator == -1) {
                    messages.add(new ValidationMessage(this,
                            SIN_HEADER_SEPARATOR, container, container,
                            ValidationMessage.Severity.ERROR));
                } else if (indexOfHeaderSeparator == 0) {
                    messages.add(new ValidationMessage(this,
                            HEADER_SEPARATOR_0, container, container,
                            ValidationMessage.Severity.ERROR));
                } else if (indexOfHeaderSeparator == container.size()
                        - 1) {
                    messages.add(new ValidationMessage(this,
                            HEADER_SEPARATOR_SIZE, container, container,
                            ValidationMessage.Severity.ERROR));
                }

                if (indexOfFooterSeparator < indexOfHeaderSeparator) {
                    messages.add(new ValidationMessage(this,
                            FOOTER_ANTES_DE_HEADER, container, container,
                            ValidationMessage.Severity.ERROR));
                } else if (indexOfFooterSeparator == container.size() - 1) {
                    messages.add(new ValidationMessage(this,
                            FOOTER_SEPARATOR_ULTIMO, container, container,
                            ValidationMessage.Severity.ERROR));
                }

                break;

            default:
                if (indexOfHeaderSeparator != -1) {
                    messages.add(new ValidationMessage(this,
                            CON_HEADER_SEPARATOR, container, container, true) {

                        @Override
                        public void fix() {
                            container.remove(indexOfHeaderSeparator);
                        }

                    });
                }

                if (indexOfFooterSeparator != container.size()) {
                    messages.add(new ValidationMessage(this,
                            CON_FOOTER_SEPARATOR, container, container, true) {

                        @Override
                        public void fix() {
                            container.remove(indexOfFooterSeparator);
                        }

                    });
                }

                break;
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget,
            WebPage fullWebPage) {
        return Collections.emptyList();
    }

    private boolean isEmpty(Container container) {
        if (container.isEmpty()) {
            return true;
        }

        for (Widget widget : container) {
            if (!(widget instanceof Label) || StringUtils.isNotEmpty(widget.getTexto())) {
                return false;
            }
        }

        return true;
    }

}
