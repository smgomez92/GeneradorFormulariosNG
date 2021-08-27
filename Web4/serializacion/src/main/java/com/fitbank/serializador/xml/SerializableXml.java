package com.fitbank.serializador.xml;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collection;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Interface que obtiene los nodos, hijos de un XML Setea el valor XML.
 * 
 * @author FitBank
 * @version 2.0
 */
public interface SerializableXml<T> extends Serializable {

    public Node getNode(Document document);

    public Collection<SerializableXml<?>> getChildren();

    /**
     * Solo usado por WebPage, Container y Widget, pero en un futuro deberían
     * implementar mejor el método parsear.
     *
     * @param tag Tag que se encontró
     * @param valor Valor del tag
     * @throws ExcepcionParser En caso de que no se pueda parsear el xml
     *
     * @deprecated Implementar el método pasear
     */
    @Deprecated
    public void setValorXml(String tag, Object valor) throws ExcepcionParser;

    public T parsear(Node node, Type type) throws ExcepcionParser;

}
