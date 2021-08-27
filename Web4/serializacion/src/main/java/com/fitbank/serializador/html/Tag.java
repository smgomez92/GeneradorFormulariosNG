package com.fitbank.serializador.html;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.SerializableXml;

public class Tag implements SerializableXml<Tag> {

    private static final long serialVersionUID = 1L;

    private String nombre;

    private Map<String, String> atributos = new LinkedHashMap<String, String>();

    private List<Tag> hijos = new LinkedList<Tag>();

    private String valor = null;

    public Tag(String nombre) {
        setNombre(nombre);
    }

    public Tag(String nombre, boolean ng) {
        setNombreNg(nombre);
    }

    public Node getNode(Document document) {
        Element elemento = document.createElement(getNombre());

        for (String nombre : getAtributos().keySet()) {
            elemento.setAttribute(nombre, getAtributos().get(nombre));
        }

        if (getValor() != null) {
            elemento.setNodeValue(getValor());
        }

        return elemento;
    }

    public Collection<SerializableXml<?>> getChildren() {
        List<SerializableXml<?>> children = new LinkedList<SerializableXml<?>>();

        for (Tag tag : getHijos()) {
            children.add(tag);
        }

        return children;
    }

    public Tag parsear(Node node, Type type) throws ExcepcionParser {
        return null;
    }

    public void setValorXml(String tag, Object valor) throws ExcepcionParser {
        // Método deprecado
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public void setNombre(String nombre) {
        if (nombre != null && nombre.matches(".*\\W.*")) {
            throw new Error("Tag contiene caracteres no permitidos: '"
                    + nombre.replaceAll("(\\W)", ">>>\1<<<") + "'");
        }

        this.nombre = nombre;
    }

    public void setNombreNg(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setAtributos(Map<String, String> atributos) {
        this.atributos = atributos;
    }

    public Map<String, String> getAtributos() {
        return atributos;
    }

    public void setHijos(List<Tag> hijos) {
        this.hijos = hijos;
    }

    public List<Tag> getHijos() {
        return hijos;
    }
}
