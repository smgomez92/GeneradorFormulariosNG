package com.fitbank.webpages.formatters;

import com.fitbank.util.Editable;
import com.fitbank.webpages.Formatter;

public class TextFormatter extends Formatter {

    private static final long serialVersionUID = 1L;

    @Editable
    private String format = "";

    public String getFormat() {
        return format;
    }

    public void setFormat(String formato) {
        this.format = formato;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getFormat() + ")";
    }

}
