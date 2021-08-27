package com.fitbank.web.providers;

import java.io.FileNotFoundException;
import java.util.prefs.Preferences;

import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.util.Debug;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageXml;

public class HardDiskWebPageProvider extends WebPageProvider {

    private static Preferences preferences = Preferences.userNodeForPackage(HardDiskWebPageProvider.class);

    public static String getPath(String subsystem, String transaction) {
        return getPath(subsystem, transaction, "wpx");
    }

    public static String getPath(String subsystem, String transaction,
            String extension) {
        return String.format("%s/%s/%s%s.%s", preferences.get("path", ""),
                subsystem, subsystem, transaction, extension);
    }

    public static void setBasePath(String path) {
        preferences.put("path", path);
    }

    public static String getBasePath() {
        return preferences.get("path", "");
    }
    
     public static void setHTMLPath(String hPath) {
        preferences.put("hPath", hPath);
    }

    public static String getHTMLPath() {
        return preferences.get("hPath", "");
    }
    
    @Override
    public WebPage getWebPage(PedidoWeb pedido, String subsystem,
            String transaction, boolean esSecundario) {
        WebPage webPage = null;
        String uri = getPath(subsystem, transaction);

        try {
            webPage = WebPageXml.parse(uri);
        } catch (FileNotFoundException e) {
            Debug.info("No se encontró el archivo " + uri);
        } catch (ExcepcionParser e) {
            throw new ErrorWeb(e);
        }

        return webPage;
    }

    @Override
    public int getWeight() {
        return 0;
    }
}
