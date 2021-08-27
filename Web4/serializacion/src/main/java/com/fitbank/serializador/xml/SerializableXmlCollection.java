package com.fitbank.serializador.xml;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SerializableXmlCollection<T> implements
        SerializableXml<Collection<T>> {

    private static final long serialVersionUID = 1L;

    private final Collection<T> items;

    private final XML xml;

    public SerializableXmlCollection(XML xml, Collection<T> value) {
        this.xml = xml;
        this.items = value;
    }

    @SuppressWarnings("unchecked")
    public Collection<SerializableXml<?>> getChildren() {
        Collection<SerializableXml<?>> children = new LinkedList<SerializableXml<?>>();

        for (T o : items) {
            if (o instanceof SerializableXml<?>) {
                children.add((SerializableXml<T>) o);
            } else {
                children.add(UtilXML.newInstance(UtilXML.getXml(xml
                        .nombreSubitems()), o));
            }
        }

        return children;
    }

    public Node getNode(Document document) {
        Element element = document.createElement(xml.nombre());

        return element;
    }

    @SuppressWarnings("unchecked")
    public Collection<T> parsear(Node node, Type type) throws ExcepcionParser {
        items.clear();

        // Procesar nodos
        NodeList childNodes = node.getChildNodes();
        Type itemType = null;
        if (type instanceof ParameterizedType) {
            itemType = ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);

            if (item.getNodeType() == Node.ELEMENT_NODE) {
                items.add((T) ParserXml.parse(item, itemType));
            }
        }

        return items;
    }

    public void setValorXml(String tag, Object valor) throws ExcepcionParser {
        // Método deprecado
    }

}
