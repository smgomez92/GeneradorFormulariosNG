package com.fitbank.webpages.behaviors;

import com.fitbank.util.Editable;
import com.fitbank.webpages.AbstractJSBehaivor;

/**
 * Copia un valor a otro campo.
 *
 * @author FitBank CI, AV
 */
public class CopyValue extends AbstractJSBehaivor {

    @Editable(weight = 1)
    private String from = "";

    @Editable(weight = 2)
    private String to = "";

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return String.format("%s (%s => %s)", super.toString(), from, to);
    }

}
