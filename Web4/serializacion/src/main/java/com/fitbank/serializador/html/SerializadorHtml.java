package com.fitbank.serializador.html;

import java.io.IOException;
import java.io.OutputStream;

import com.fitbank.serializador.Serializador;
import com.fitbank.serializador.xml.SerializadorXml;

/**
 *
 * @author FitBank
 * @version 2.0
 */
public class SerializadorHtml extends Serializador<SerializableHtml> {

    @Override
    public void serializar(SerializableHtml s, OutputStream os)
            throws IOException {
        SerializadorXml xml = new SerializadorXml();
        ConstructorHtml html = new ConstructorHtml();
        s.generateHtml(html);
        xml.serializar(html.getCabeza(), os, true);

    }

    /**
     * Método que serializa el archivo a angular
     *
     * @param s
     * @param os
     * @throws IOException
     */
    @Override
    public void serializarNg(SerializableHtml s, OutputStream os)
            throws IOException {
        SerializadorXml xml = new SerializadorXml();
        ConstructorHtml html = new ConstructorHtml(true);
        s.generateHtmlNg(html);
        xml.serializarNg(html.getCabeza(), os, true);

    }

}
