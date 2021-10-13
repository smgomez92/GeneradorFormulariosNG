/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fitbank.webpages;

import com.fitbank.serializador.html.SerializadorHtml;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.util.Debug;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author santy
 */
public class TransformarNG {

    public void process(String wpxPath) throws IOException, ExcepcionParser {
        WebPage webPage = null;
        webPage = WebPageXml.parse(wpxPath);
        String subsistema = webPage.getSubsystem();
        String trx = webPage.getTransaction();
        Debug.info(subsistema + trx);
        WebPageEnviromentNG.clear();
        WebPageEnviromentNG.addName(trx, subsistema);

        String html = new SerializadorHtml().serializarNg(webPage);
        html = html.replaceAll("abreCorch--", "[");
        html = html.replaceAll("--cerrCorch--", "]");
        html = html.replaceAll("abreLlave--", "{");
        html = html.replaceAll("--cerrLlave--", "}");
        html = html.replaceAll("asterisco--", "*");
        html = html.replaceAll("abreParent--", "(");
        html = html.replaceAll("--cerrParent--", ")");
        html = html.replaceAll("=\"--reemplazar--\"", "");
//        html = html.replaceAll("pickerdesde=\"#pickerdesde\"", "#pickerdesde");
        html = html.replaceAll("marca--", "#");
        html = html.replaceAll("=\"#picker\"", "");
        html = html.replaceAll("<html>", "");
        html = html.replaceAll("</html>", "");
        html = html.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
        Debug.info(WebPageEnviromentNG.getPath() + "\\form" + subsistema + trx + "\\form" + subsistema + trx + ".component.html");
        File file = new File(WebPageEnviromentNG.getPath() + "\\form" + subsistema + trx + "\\form" + subsistema + trx + ".component.html");
        try (FileWriter fileWriter = new FileWriter(file, false); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(html);
        }
        WebPageEnviromentNG.saveFile(subsistema, trx);
        Debug.info("Transformación exitosa");

    }

    /**
     * Crea el componente angular
     *
     * @param pathNg
     * @param form
     */
    public void createComponent(String pathNg, String form) {
        String unidad;
        Process p;
        String systemInfo = getSoInfo().toLowerCase(), cmd;
        //un prefijo para el sistema operativo windows
        final String PRE_COMMAND = "cmd /c ";
        try {
            unidad = pathNg.substring(0, 2);
            if (systemInfo.contains("windows")) {
                cmd = PRE_COMMAND + unidad + " && cd \"" + pathNg + "\" && ng g c " + form + " --module=app --entry-component=true";
            } else {
                cmd = "cd \"" + pathNg + "\" && ng g c " + form + " --module=app --entry-component=true";
            }

            Debug.info("Ejecutando " + cmd);
            p = Runtime.getRuntime().exec(cmd);
            p.waitFor();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line = "";
                while ((line = reader.readLine()) != null) {
                    Debug.info(line);
                }
            }

        } catch (IOException | InterruptedException e) {
            Debug.error("Error: " + e);
        }
    }

    private String getSoInfo() {
        String sSistemaOperativo = System.getProperty("os.name");
        Debug.info("Ejecutando en sistema operativo " + sSistemaOperativo);
        return sSistemaOperativo;
    }

    public String formatHtml(String html) {
        Document doc;
        doc = Jsoup.parse(html);
        System.out.println(doc.body().html());
        return doc.body().html();
    }
}
