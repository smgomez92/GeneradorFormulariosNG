package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.fitbank.js.LiteralJS;
import com.fitbank.util.Clonador;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.ListOfValues;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;
import com.fitbank.webpages.widgets.Input;

/**
 * Agrega un formateador de fecha para ciertas formulas si es necesario.
 *
 * @author FitBank HB, CI
 */
public class ArregloNullJSLOV extends Validator {

    public static final String NULL_PREQUERY = "NULL_PREQUERY";

    public static final String NULL_CALLBACK = "NULL_CALLBACK";

    public static final String INCORRECT_PREQUERY = "INCORRECT_PREQUERY";

    public static final String INCORRECT_CALLBACK = "INCORRECT_CALLBACK";

    private static final ListOfValues LOV_EXAMPLE = new ListOfValues();

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        if (!(widget instanceof Input)) {
            return Collections.emptyList();
        }

        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        final Input input = (Input) widget;

        if (input.getAssistant() instanceof ListOfValues) {
            final ListOfValues lov = (ListOfValues) input.getAssistant();

            if (lov.getPreQuery() == null) {
                messages.add(new ValidationMessage(this, NULL_PREQUERY, input,
                        lov, true) {

                    @Override
                    public void fix() {
                        lov.setPreQuery(LOV_EXAMPLE.getPreQuery());
                    }

                });
            }

            if (lov.getCallback() == null) {
                messages.add(new ValidationMessage(this, NULL_CALLBACK, input,
                        input, true) {

                    @Override
                    public void fix() {
                        lov.setCallback(LOV_EXAMPLE.getCallback());
                    }

                });
            }

            if (notEquals(lov.getPreQuery(), LOV_EXAMPLE.getPreQuery())) {
                messages.add(new ValidationMessage(this, INCORRECT_PREQUERY, input,
                        lov, true) {

                    @Override
                    public void fix() {
                        LiteralJS literalJS = Clonador.clonar(LOV_EXAMPLE.
                                getPreQuery());
                        literalJS.setValor(lov.getPreQuery().getValor());
                        lov.setPreQuery(literalJS);
                    }

                });
            }

            if (notEquals(lov.getCallback(), LOV_EXAMPLE.getCallback())) {
                messages.add(new ValidationMessage(this, INCORRECT_CALLBACK, input,
                        input, true) {

                    @Override
                    public void fix() {
                        LiteralJS literalJS = Clonador.clonar(LOV_EXAMPLE.
                                getCallback());
                        literalJS.setValor(lov.getCallback().getValor());
                        lov.setCallback(literalJS);
                    }

                });
            }
        }

        return messages;
    }

    private boolean notEquals(LiteralJS a, LiteralJS b) {
        if (a == null || b == null) {
            return false;
        }

        if (a.getClass() != b.getClass()) {
            return true;
        }

        LiteralJS aClone = Clonador.clonar(a);
        LiteralJS bClone = Clonador.clonar(b);

        aClone.setValor("");
        bClone.setValor("");

        return !aClone.toJS().equals(bClone.toJS());
    }

}
