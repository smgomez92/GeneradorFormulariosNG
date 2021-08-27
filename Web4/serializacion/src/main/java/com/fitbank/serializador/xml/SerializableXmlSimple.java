package com.fitbank.serializador.xml;

import java.lang.reflect.Type;
import java.util.Collection;

import org.apache.commons.beanutils.ConvertUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author FitBank
 * @version 2.0
 */
public class SerializableXmlSimple<T> implements SerializableXml<T> {

    private static final long serialVersionUID = 1L;

    private final String val;

    private final XML xml;

    public SerializableXmlSimple(String nombre, String val) {
        this(UtilXML.getXml(nombre), val);
    }

    public SerializableXmlSimple(XML xml, String val) {
        this.xml = xml;
        this.val = val;
    }

    public Node getNode(Document document) {
        Element e = document.createElement(xml.nombre());

        if (val != null) {
            e.appendChild(document.createTextNode(val));
        }

        return e;
    }

    public Collection<SerializableXml<?>> getChildren() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public T parsear(Node node, Type type) throws ExcepcionParser {
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            if (node.getNodeValue() == null) {
                return null;
            } else {
                return (T) ConvertUtils.convert(node.getNodeValue(),
                        (Class<?>) type);
            }
        } else if (node.getNodeType() == Node.ELEMENT_NODE
                && node.getNodeValue() == null) {
            if (node.getFirstChild() == null
                    || node.getFirstChild().getNodeValue() == null) {
                return null;
            } else {
                return (T) ConvertUtils.convert(node.getFirstChild()
                        .getNodeValue(), (Class<?>) type);
            }
        } else {
            return null;
        }
    }

    public void setValorXml(String tag, Object valor) throws ExcepcionParser {
        // Método deprecado
    }

}
