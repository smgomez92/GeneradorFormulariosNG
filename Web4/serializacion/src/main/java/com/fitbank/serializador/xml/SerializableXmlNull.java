package com.fitbank.serializador.xml;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author cesar
 */
public class SerializableXmlNull<T> implements SerializableXml<T> {

    public Node getNode(Document document) {
        return document.createElement("null");
    }

    public Collection<SerializableXml<?>> getChildren() {
        return Collections.EMPTY_LIST;
    }

    public void setValorXml(String tag, Object valor) throws ExcepcionParser {
    }

    public T parsear(Node node, Type type) throws ExcepcionParser {
        return null;
    }

}
