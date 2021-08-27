package com.fitbank.webpages.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;

import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.html.SerializadorHtml;
import com.fitbank.util.Clonador;
import com.fitbank.util.Debug;
import com.fitbank.util.MultiplePropertyResourceBundle;
import com.fitbank.util.Servicios;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebElement;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;

public final class ValidationUtils {

    public static final ResourceBundle DESCRIPTIONS =
            new MultiplePropertyResourceBundle("descriptions");

    public static String STYLE = "";

    static {
        try {
            InputStream stream = ValidationUtils.class.getClassLoader().
                    getResourceAsStream("style.css");
            STYLE = IOUtils.toString(stream);
        } catch (IOException e) {
            Debug.error(e);
        } catch (NullPointerException e) {
            Debug.error(e);
        }
    }

    /**
     * Convert ResourceBundle into a Map object.
     *
     * @param resource a resource bundle to convert.
     * @return Map a map version of the resource bundle.
     */
    public static Map<String, String> toMap(ResourceBundle resource) {
        Map<String, String> map = new HashMap<String, String>();

        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            map.put(key, resource.getString(key));
        }

        return map;
    }

    private ValidationUtils() {
    }

    public static Collection<ValidationMessage> validate(WebElement webElement) {
        return validate(webElement, null);
    }

    public static Collection<ValidationMessage> validate(WebElement webElement,
            WebPageSource webPageSource) {
        if (webElement instanceof WebPage) {
            return validate((WebPage) webElement, webPageSource);
        } else if (webElement instanceof Container) {
            return validate((Container) webElement, webPageSource);
        } else if (webElement instanceof Widget) {
            return validate((Widget) webElement, webPageSource);
        } else {
            return null;
        }
    }

    public static WebPage getFullWebPage(WebPage webPage, WebPageSource webPageSource) {
        return webPageSource.processWebPage(Clonador.clonar(webPage), "0");
    }

    /**
     * Ejecuta todos los validadores encontrados sobre el objeto.
     * 
     * @param webPage WebPage a ser validado recursivamente.
     * @param webPageSource De donde obtener formularios en caso de ser necesario.
     * 
     * @return Coleccion de mensajes de validación
     */
    public static Collection<ValidationMessage> validate(WebPage webPage,
            WebPageSource webPageSource) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        WebPage fullWebPage = getFullWebPage(webPage, webPageSource);

        for (Validator validator : Servicios.load(Validator.class)) {
            try {
                messages.addAll(validator.validate(webPage, fullWebPage));
            } catch (Throwable t) {
                messages.add(getExceptionMessage(validator, webPage, t));
            }
        }

        return messages;
    }

    /**
     * Ejecuta todos los validadores encontrados sobre el objeto.
     * 
     * @param container Container a ser validado recursivamente.
     * @param webPageSource De donde obtener formularios en caso de ser necesario.
     * 
     * @return Coleccion de mensajes de validación
     */
    public static Collection<ValidationMessage> validate(Container container,
            WebPageSource webPageSource) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        WebPage fullWebPage = getFullWebPage(container.getParentWebPage(), webPageSource);

        for (Validator validator : Servicios.load(Validator.class)) {
            try {
                messages.addAll(validator.validate(container, fullWebPage));
            } catch (Throwable t) {
                messages.add(getExceptionMessage(validator, container, t));
            }
        }

        return messages;
    }

    /**
     * Ejecuta todos los validadores encontrados sobre el objeto.
     * 
     * @param widget Widget a ser validado recursivamente.
     * @param webPageSource De donde obtener formularios en caso de ser necesario.
     * 
     * @return Coleccion de mensajes de validación
     */
    public static Collection<ValidationMessage> validate(Widget widget,
            WebPageSource webPageSource) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        WebPage fullWebPage = getFullWebPage(widget.getParentWebPage(), webPageSource);

        for (Validator validator : Servicios.load(Validator.class)) {
            try {
                messages.addAll(validator.validate(widget, fullWebPage));
            } catch (Throwable t) {
                messages.add(getExceptionMessage(validator, widget, t));
            }
        }

        return messages;
    }

    /**
     * Genera un reporte en formato HTML.
     *
     * @param messages Mapa de mensajes
     * @param fixed Indica si ya se arregló
     *
     * @return String con el html
     */
    public static String generateReport(
            Map<String, Collection<ValidationMessage>> messages, boolean fixed) {
        ConstructorHtml html = new ConstructorHtml();

        html.abrir("html");

        html.abrir("head");
        html.agregar("title", "Reporte de validaciones");
        html.agregar("style", STYLE);
        html.cerrar("head");

        html.abrir("body");

        html.agregar("h1", "Reporte de validaciones");

        for (String name : messages.keySet()) {
            Collection<ValidationMessage> messagesList = messages.get(name);

            if (messagesList.isEmpty()) {
                continue;
            }

            html.agregar("h2", name);
            html.setAtributo("class", "web-page-title");

            html.abrir("div");
            html.setAtributo("class", "web-page-messages");
            generateReport(html, messagesList, fixed);
            html.cerrar("div");
        }

        html.cerrar("body");

        html.cerrar("html");

        return new SerializadorHtml().serializar(html);
    }

    private static void generateReport(ConstructorHtml html,
            Collection<ValidationMessage> messages, boolean fixed) {

        for (ValidationMessage message : messages) {
            html.abrir("pre");

            html.setAtributo("class", "validation-message " + message.
                    getSeverity().toString().toLowerCase());

            if (fixed && message.isFixable()) {
                html.extenderAtributo("class", " fixed");
            }

            html.setTexto(message.toString());

            html.cerrar("pre");
        }
    }

    public static ValidationMessage getExceptionMessage(Validator validator,
            WebElement webElement, Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        Debug.error(t);
        t.printStackTrace(pw);

        return new ValidationMessage(new ValidationUtils(), "EXCEPTION",
                validator.getClass().getName() + ": " + sw.toString(),
                webElement, webElement, ValidationMessage.Severity.ERROR);
    }

}
