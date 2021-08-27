package com.fitbank.webpages.updates;

import java.io.InputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import com.fitbank.util.Debug;

/**
 * Migra WebPages entre diferentes versiones.
 *
 * @author FitBank CI
 */
public class WebPageVersionUpdater {

    private static final TransformerFactory FACTORY = TransformerFactory.
            newInstance();

    private static final String PACKAGE_DIR = WebPageVersionUpdater.class.
            getPackage().getName().replace('.', '/');

    public static Document transformBase(Document document) {
        String name = String.format("%s/Base.xslt", PACKAGE_DIR);
        return transform(name, document);
    }

    public static Document transform(Document document, int n) {
        String name = String.format("%s/Version%s.xslt", PACKAGE_DIR, n);
        return transform(name, document);
    }

    private static Document transform(String name, Document document) {
        try {
            InputStream stream = WebPageVersionUpdater.class.getClassLoader().
                    getResourceAsStream(name);

            if (stream == null) {
                return document;
            }

            Transformer trans = FACTORY.newTransformer(new StreamSource(stream));
            DOMResult result = new DOMResult();
            trans.transform(new DOMSource(document), result);

            return (Document) result.getNode();

        } catch (TransformerException te) {
            Debug.error(te);
            return document;
        }
    }

}
