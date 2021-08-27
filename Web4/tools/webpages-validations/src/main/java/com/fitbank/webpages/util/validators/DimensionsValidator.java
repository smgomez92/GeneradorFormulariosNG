package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.LinkedList;

import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;
import com.fitbank.webpages.widgets.TabBar;

/**
 * Revisa dimensiones de diferentes elementos
 *
 * @author FitBank CI
 */
public class DimensionsValidator extends Validator {

    public static final int TAB_BAR_WIDTH = 960;

    public static final String TAB_BAR_WIDTH_ERROR = "TAB_BAR_WIDTH_ERROR";

    @Override
    public Collection<ValidationMessage> validate(final Widget widget, WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        if (widget instanceof TabBar) {
            if (widget.getW() != TAB_BAR_WIDTH) {
                messages.add(new ValidationMessage(this, TAB_BAR_WIDTH_ERROR,
                        widget, widget, true) {

                    @Override
                    public void fix() {
                        widget.setW(TAB_BAR_WIDTH);
                    }

                });
            }
        }

        return messages;
    }

}
