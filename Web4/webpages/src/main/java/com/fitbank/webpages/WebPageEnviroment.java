package com.fitbank.webpages;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.fitbank.webpages.formulas.Formula;

/**
 * Clase WebPageEnviroment, la representacion del entorno donde los formularios
 * se despliegan y operan.
 * 
 * @author FitBank
 * @version 2.0
 */
public class WebPageEnviroment {

    private StringBuilder javascriptInicial = new StringBuilder();

    private Collection<Formula> formulas = new LinkedList<Formula>();

    private String remoteURL = "error.html";

    private String firstFocus = "";

    private String contextId = "";

    private boolean debug = false;

    /**
     * Inicaliza en un ThreadLocal una instancia de WebPageEnviroment.
     */
    private static ThreadLocal<WebPageEnviroment> webPageEnviroment =
            new ThreadLocal<WebPageEnviroment>() {

                @Override
                protected WebPageEnviroment initialValue() {
                    return new WebPageEnviroment();
                }

            };

    /**
     * Constructor por defecto.
     */
    private WebPageEnviroment() {
    }

    // ////////////////////////////////////////////////////////
    // Métodos: get / set / add
    // ////////////////////////////////////////////////////////
    /**
     * Obtiene el js inicial
     * 
     * @return JS inicial
     */
    public static String getJavascriptInicial() {
        return webPageEnviroment.get().javascriptInicial.toString();
    }

    /**
     * Agrega un javascript a ser ejecutado una vez que el formulario se cargue.
     *
     * @param javascriptInicial código de js
     */
    public static void addJavascriptInicial(String javascriptInicial) {
        addRawJavascriptInicial(String.format(
                "(function(c){\n%s;\n}).tryCatch(c);\n", javascriptInicial));
    }

    /**
     * Agrega un javascript a ser ejecutado una vez que el formulario se cargue.
     *
     * @param javascriptInicial código de js
     */
    public static void addRawJavascriptInicial(String javascriptInicial) {
        webPageEnviroment.get().javascriptInicial.append(javascriptInicial);
    }

    public static Collection<Formula> getFormulas() {
        return Collections.unmodifiableCollection(
                webPageEnviroment.get().formulas);
    }

    public static void addFormula(Formula formula) {
        webPageEnviroment.get().formulas.add(formula);
    }

    public static String getRemoteURL() {
        return webPageEnviroment.get().remoteURL;
    }

    public static void setRemoteURL(String remoteURL) {
        webPageEnviroment.get().remoteURL = remoteURL;
    }

    public static String getFirstFocus() {
        return webPageEnviroment.get().firstFocus;
    }

    public static void setFirstFocus(String firstFocus) {
        webPageEnviroment.get().firstFocus = firstFocus;
    }

    public static String getContextId() {
        return webPageEnviroment.get().contextId;
    }

    public static void setContextId(String contextId) {
        webPageEnviroment.get().contextId = contextId;
    }

    public static boolean getDebug() {
        return webPageEnviroment.get().debug;
    }

    public static void setDebug(boolean debug) {
        webPageEnviroment.get().debug = debug;
    }

    public static void reset(boolean resetUrl) {
        webPageEnviroment.get().javascriptInicial = new StringBuilder();
        webPageEnviroment.get().formulas.clear();
        if (resetUrl) {
            setRemoteURL("error.html");
        }
        setFirstFocus("");
    }

}
