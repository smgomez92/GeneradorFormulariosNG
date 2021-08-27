package com.fitbank.webpages.assistants;

public class Password extends PlainText {

    private static final long serialVersionUID = 1L;

    @Override
    public String format(String valorSinFormato) {
        return valorSinFormato;
    }

    @Override
    public String unformat(String valorFormateado) {
        return valorFormateado;
    }

    @Override
    public String getType() {
        return "password";
    }

}
