package com.fitbank.webpages.definition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.ParserXml;
import com.fitbank.serializador.xml.SerializadorXml;
import com.fitbank.serializador.xml.UtilXML;
import com.fitbank.util.Debug;

/**
 * Clase que convierte un WebPageDefinition en xml y viceversa.
 *
 * @author FitBank CI
 */
public final class WebPageDefinitionXml {

    private WebPageDefinitionXml() {
    }

    public static void save(WebPageDefinition webPageDefinition, String path) {
        save(webPageDefinition, new File(path));
    }

    public static void save(WebPageDefinition webPageDefinition, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            save(webPageDefinition, fos);
            fos.close();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static void save(WebPageDefinition webPageDefinition, OutputStream os) {
        try {
            new SerializadorXml().serializar(UtilXML.newInstance("wpd",
                    webPageDefinition), os);
        } catch (IOException e) {
            Debug.error(e);
        }
    }

    public static WebPageDefinition load(String path) throws ExcepcionParser {
        return load(new File(path));
    }

    public static WebPageDefinition load(File archivo) throws ExcepcionParser {
        try {
            return load(new FileInputStream(archivo));
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }
    }

    public static WebPageDefinition load(InputStream is) throws ExcepcionParser {
        return ParserXml.parse(is, WebPageDefinition.class);
    }

}
