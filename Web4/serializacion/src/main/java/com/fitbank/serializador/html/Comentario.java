package com.fitbank.serializador.html;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Comentario extends Tag {

    private static final long serialVersionUID = 1L;

    public Comentario(String valor) {
        super(null);

        setValor(valor);
    }

    @Override
    public Node getNode(Document document) {
        return document.createComment(getValor());
    }

}
