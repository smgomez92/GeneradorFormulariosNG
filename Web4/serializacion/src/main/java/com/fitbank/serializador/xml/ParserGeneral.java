package com.fitbank.serializador.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Clase que ejecuta los Parsers.
 * 
 * @author FitBank
 * @version 2.0
 */
public final class ParserGeneral {

    private ParserGeneral() {
    }

    /**
     * Parsea un archivo xml a patir de un string.
     * 
     * @param contenido
     *            Contiene el string que se quiere parsear.
     * 
     * @return Devuelve un Document parseado.
     * 
     * @throws ExcepcionParser
     *             en caso de error
     */
    public static Document parseStringDoc(String contenido)
            throws ExcepcionParser {
        Document doc = null;

        try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(new StringReader(contenido)));
            doc = parser.getDocument();
        } catch (Exception e) {
            throw new ExcepcionParser("No se pudo parsear el archivo XML: "
                    + e.getLocalizedMessage(), e);
        }

        return doc;
    }

    /**
     * Parsea un archivo xml a partir de un path.
     * 
     * @param uri
     *            Es un string que contiene el path del archivo que se quiere
     *            parsear.
     * 
     * @return Devuelve un Document parseado.
     * 
     * @throws ExcepcionParser
     *             En caso de error al parsear.
     * @throws FileNotFoundException
     *             En caso de que no exista el archivo
     */
    public static Document parseDoc(String uri) throws ExcepcionParser,
            FileNotFoundException {
        return parse(new FileInputStream(uri));
    }

    /**
     * Parsea un archivo xml a partir de un path.
     * 
     * @param uri
     *            Es un string que contiene el path del archivo que se quiere
     *            parsear.
     * 
     * @return Devuelve un Document parseado.
     * 
     * @throws ExcepcionParser
     *             en caso de error.
     */
    public static Document parse(InputStream is) throws ExcepcionParser {
        Document doc = null;

        try {
            DOMParser parser = new DOMParser();
            parser.parse(new InputSource(is));
            doc = parser.getDocument();
        } catch (Exception e) {
            throw new ExcepcionParser("Error al parsear el archivo: "
                    + e.getLocalizedMessage(), e);
        }

        return doc;
    }
}
