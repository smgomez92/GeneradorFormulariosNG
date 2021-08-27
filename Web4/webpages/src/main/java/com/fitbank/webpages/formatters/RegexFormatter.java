package com.fitbank.webpages.formatters;

import com.fitbank.util.Editable;

public class RegexFormatter extends TextFormatter {

    private static final long serialVersionUID = 1L;

    @Editable
    private String partialFormat = "";

    @Editable
    private String message = "";

    public String getPartialFormat() {
        return partialFormat;
    }

    public void setPartialFormat(String partialFormat) {
        this.partialFormat = partialFormat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
