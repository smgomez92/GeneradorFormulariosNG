package com.fitbank.web;

import com.fitbank.util.IterableEnumeration;
import com.fitbank.util.MultiplePropertyResourceBundle;

import net.sf.json.JSONObject;

/**
 * Clase para leer mensajes del archivo de properties messages.properties
 * 
 * @author FitBank
 */
public final class MensajesWeb {

    private static final MultiplePropertyResourceBundle MESSAGES =
            new MultiplePropertyResourceBundle("messages");

    private MensajesWeb() {
    }

    /**
     * Obiene el valor
     * 
     * @param String
     *            nombre de la propiedad
     * 
     * @return Valor
     */
    public static String getValueString(Class<?> clase, String nombre) {
        return getValue(clase.getName() + "." + nombre);
    }

    private static String getValue(String string) {
        return MESSAGES.getString(string);
    }

    public static String getMensajesJSON() {
        JSONObject json = new JSONObject();

        for (String key : IterableEnumeration.get(MESSAGES.getKeys())) {
            json.put(key, MESSAGES.getString(key));
        }

        return "var Mensajes = " + json.toString(4) + ";";
    }

}
