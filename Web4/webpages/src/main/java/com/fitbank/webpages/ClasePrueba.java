/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fitbank.webpages;

import com.fitbank.serializador.html.SerializadorHtml;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.util.Debug;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author j_par
 */
public class ClasePrueba {

    public static void main(String[] args) throws ExcepcionParser, IOException {
        WebPage webPage = null;
        try {
            webPage = WebPageXml.parse("D:\\GeneradorNG\\prueba2.wpx");
            String subsistema = webPage.getSubsystem();

            String trx = webPage.getTransaction();
            Debug.info(subsistema + trx);
            WebPageEnviromentNG.addName(trx, subsistema);
            String html = new SerializadorHtml().serializar(webPage);
            html = html.replaceAll("abreCorch--", "[");
            html = html.replaceAll("--cerrCorch--", "]");
            html = html.replaceAll("abreLlave--", "{");
            html = html.replaceAll("--cerrLlave--", "}");
            html = html.replaceAll("asterisco--", "*");
            html = html.replaceAll("abreParent--", "(");
            html = html.replaceAll("--cerrParent--", ")");
            html = html.replaceAll("=\"--reemplazar--\"", "");
            html = html.replaceAll("<html>", "");
            html = html.replaceAll("</html>", "");
            html = html.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
            // File file = new File("D:\\GeneradorNG\\pwa_base_pruebas\\src\\app\\form000001\\form" + subsistema + trx + ".component.html");
            // File file = new File("D:\\santy_1\\santy-pruebas\\src\\app\\app.component.html");
            //C:\Users\santy\OneDrive\Documentos\Fitbank Repository\PWA CAJAS\src\app\form036401\form036401.component.html
            File file = new File("C:\\Users\\santy\\OneDrive\\Documentos\\Fitbank Repository\\PWA CAJAS\\src\\app\\form036401\\form" + subsistema + trx + ".component.html");
            FileWriter fileWriter = new FileWriter(file, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(html);
            bufferedWriter.close();
            fileWriter.close();
            WebPageEnviromentNG.saveFile("","");
            System.out.println("html " + html);
        } catch (FileNotFoundException e) {
            Debug.info("No se encontró el archivo " + "");
        }

    }

}
