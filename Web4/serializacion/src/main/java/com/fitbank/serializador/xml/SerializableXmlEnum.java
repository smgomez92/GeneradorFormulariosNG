package com.fitbank.serializador.xml;

import java.lang.reflect.Type;
import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SerializableXmlEnum<T> implements SerializableXml<T> {

    private static final long serialVersionUID = 1L;

    private final Enum<?> val;

    private final XML xml;

    public SerializableXmlEnum(String nombre, Enum<?> val) {
        this(UtilXML.getXml(nombre), val);
    }

    public SerializableXmlEnum(XML xml, Enum<?> val) {
        this.xml = xml;
        this.val = val;
    }

    public Node getNode(Document document) {
        Element e = document.createElement(xml.nombre());

        if (val != null) {
            e.appendChild(document.createTextNode(val.name()));
        }

        return e;
    }

    public Collection<SerializableXml<?>> getChildren() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public T parsear(Node node, Type type) throws ExcepcionParser {
        if (node.getNodeValue() == null) {
            return null;
        } else {
            return (T) Enum.valueOf(val.getClass(), node.getNodeValue());
        }
    }

    public void setValorXml(String tag, Object valor) throws ExcepcionParser {
        // Método deprecado
    }

}
