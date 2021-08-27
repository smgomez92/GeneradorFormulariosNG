package com.fitbank.webpages.formatters;

import com.fitbank.webpages.Formatter;

public class UpperCaseFormatter extends Formatter {

    private static final long serialVersionUID = 1L;

    @Override
    public String format(String valorSinFormato) {
        return valorSinFormato.toUpperCase();
    }

}
