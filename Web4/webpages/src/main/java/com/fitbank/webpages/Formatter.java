package com.fitbank.webpages;

public abstract class Formatter extends AbstractJSBehaivor {

    private static final long serialVersionUID = 1L;

    private boolean doInit = true;

    public String format(String valorSinFormato) {
        return valorSinFormato;
    }

    public String unformat(String valorFormateado) {
        return valorFormateado;
    }

    public boolean getDoInit() {
        return doInit;
    }

    public void setDoInit(boolean doInit) {
        this.doInit = doInit;
    }

}
