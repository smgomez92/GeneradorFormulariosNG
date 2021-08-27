package com.fitbank.serializador.html;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class CDATA extends Tag {

    private static final long serialVersionUID = 1L;

    public CDATA(String valor) {
        super(null);

        setValor(valor);
    }

    @Override
    public Node getNode(Document document) {
        return document.createCDATASection(getValor());
    }

}
