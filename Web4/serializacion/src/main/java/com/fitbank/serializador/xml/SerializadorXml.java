package com.fitbank.serializador.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.sun.org.apache.xml.internal.serialize.Method;

import com.fitbank.serializador.Serializador;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 *
 * @author FitBank
 * @version 2.0
 */
public class SerializadorXml extends Serializador<SerializableXml<?>> {

    @Override
    public void serializar(SerializableXml<?> s, OutputStream os)
            throws IOException {
        serializar(s, os, false);

    }

    public void serializar(SerializableXml<?> s, OutputStream os, boolean html)
            throws IOException {
        Writer w = new OutputStreamWriter(os, "UTF-8");
        Document d = new DocumentImpl();

        Node root = serialize(s, d);

        d.appendChild(root);
        OutputFormat of = new OutputFormat();
        if (html) {
            of.setMethod(Method.HTML);
            of.setPreserveEmptyAttributes(true);
            of.setPreserveSpace(true);
        } else {
            of.setIndenting(true);
            of.setIndent(2);
        }
        XMLSerializer ser = new XMLSerializer(w, of);
        ser.serialize(d);
    }

    public void serializarNg(SerializableXml<?> s, OutputStream os, boolean html)
            throws IOException {
        Document d = new DocumentImpl();
        Node root = serialize(s, d);
        d.appendChild(root);
        DOMImplementationLS domImplLS = (DOMImplementationLS) d.getImplementation();
        LSSerializer serializer = domImplLS.createLSSerializer();
        LSOutput lso = domImplLS.createLSOutput();
        lso.setByteStream(os);
        serializer.write(d, lso);

    }

    private Node serialize(SerializableXml<?> s, Document d) {
        Node node = s.getNode(d);

        if (s.getChildren() != null) {
            for (SerializableXml<?> sx : s.getChildren()) {
                node.appendChild(serialize(sx, d));
            }
        }

        return node;
    }

    @Override
    public void serializarNg(SerializableXml<?> s, OutputStream os) throws IOException {
        serializarNg(s, os, false);
    }

}
