package com.fitbank.serializador.html;

public class Script extends Tag {

    private static final long serialVersionUID = 1L;

    public Script() {
        super("script");

        getAtributos().put("type", "text/javascript");

        getHijos().add(new Texto(" "));
    }

    public Script(String src) {
        this();

        getAtributos().put("src", src);
    }

    public void setContenido(String contenido) {
        getHijos().get(0).setValor(contenido);
    }

}
