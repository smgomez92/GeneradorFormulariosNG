package com.fitbank.serializador.xml;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Serializable de xml de un mapa
 *
 * @author FitBank CI
 */
public class SerializableXmlMap<T, U> implements SerializableXml<Map<T, U>> {

    private static final long serialVersionUID = 1L;

    private final Map<T, U> items;

    private final XML xml;

    public SerializableXmlMap(XML xml, Map<T, U> value) {
        this.xml = xml;
        this.items = value;
    }

    @SuppressWarnings("unchecked")
    public Collection<SerializableXml<?>> getChildren() {
        Collection<SerializableXml<?>> children =
                new LinkedList<SerializableXml<?>>();

        for (Map.Entry<T, U> entry : items.entrySet()) {
            children.add(new Entry(entry));
        }

        return children;
    }

    public Node getNode(Document document) {
        Element element = document.createElement(xml.nombre());

        return element;
    }

    @SuppressWarnings("unchecked")
    public Map<T, U> parsear(Node node, Type type) throws ExcepcionParser {
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
                Entry<T, U> entry = (Entry<T, U>) new Entry().parsear(item, itemType);
                items.put((T) entry.getKey(), (U) entry.getValue());
            }
        }

        return items;
    }

    public void setValorXml(String tag, Object valor) throws ExcepcionParser {
        // Método deprecado
    }

    public static class Entry<T, U> implements SerializableXml<Entry<T, U>> {

        private T key;

        private U value;

        public Entry() {
        }

        public Entry(T key, U value) {
            this.key = key;
            this.value = value;
        }

        public Entry(Map.Entry<T, U> entry) {
            this.key = entry.getKey();
            this.value = entry.getValue();
        }

        public T getKey() {
            return key;
        }

        public void setKey(T key) {
            this.key = key;
        }

        public U getValue() {
            return value;
        }

        public void setValue(U value) {
            this.value = value;
        }

        public Node getNode(Document document) {
            return document.createElement("item");
        }

        public Collection<SerializableXml<?>> getChildren() {
            return Arrays.asList(UtilXML.newInstance("key", key),
                    UtilXML.newInstance("value", value));
        }

        public void setValorXml(String tag, Object valor) throws ExcepcionParser {
        }

        public Entry<T, U> parsear(Node node, Type type) throws ExcepcionParser {
            NodeList childNodes = node.getChildNodes();
            Entry<T, U> entry = new Entry<T, U>();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);

                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    if (item.getNodeName().equals("key")) {
                        // FIXME asumiendo String pero puede ser cualquier cosa
                        entry.setKey((T) ParserXml.parse(item, String.class));
                    } else if (item.getNodeName().equals("value")) {
                        entry.setValue((U) ParserXml.parse(item, type));
                    }
                }
            }

            return entry;
        }

    }

}
