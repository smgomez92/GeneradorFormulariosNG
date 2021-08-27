package com.fitbank.serializador.html;

public class Style extends Tag {

    private static final long serialVersionUID = 1L;

    public Style() {
        super("link");

        getAtributos().put("rel", "stylesheet");
        getAtributos().put("type", "text/css");

        getHijos().add(new Comentario(" "));
    }

    public Style(String href) {
        this();

        getAtributos().put("href", href);
    }

    public void setContenido(String contenido) {
        getHijos().get(0).setValor("\n" + contenido);
    }

}
