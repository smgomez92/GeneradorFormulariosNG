package com.fitbank.propiedades;

import java.lang.reflect.Type;
import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.SerializableXml;

/**
 * Clase que crea un objeto SerializableXml desde una Propiedad.
 * 
 * @author FitBank
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class PropiedadSerializable implements SerializableXml {

    private static final long serialVersionUID = 1L;

    private String nombre;

    private Propiedad p;

    public PropiedadSerializable(String nombre, Propiedad p) {
        this.nombre = nombre;
        this.p = p;
    }

    public Node getNode(Document document) {
        Element e = document.createElement("attribute");

        e.setAttribute("nom", nombre);
        e.appendChild(document.createTextNode(p.getValorString()));

        return e;
    }

    public Collection<SerializableXml> getChildren() {
        return null;
    }

    public Object parsear(Node node, Type type) throws ExcepcionParser {
        return null;
    }

    public void setValorXml(String tag, Object valor) throws ExcepcionParser {
    }

}
