package com.fitbank.serializador.html;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Texto extends Tag {

    private static final long serialVersionUID = 1L;

    public Texto(String valor) {
        super(null);

        setValor(valor);
    }

    @Override
    public Node getNode(Document document) {
        return document.createTextNode(getValor());
    }

}
