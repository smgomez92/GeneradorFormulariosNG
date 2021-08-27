package com.fitbank.web;

import com.adobe.xmp.impl.Base64;
import com.fitbank.common.properties.PropertiesHandler;
import com.fitbank.util.MultiplePropertyResourceBundle;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.json.JSONObject;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;

public final class ParametrosWeb {

    private static final MultiplePropertyResourceBundle BUNDLE =
            new MultiplePropertyResourceBundle("parametros");

    private static final Configuration FIT_BUNDLE = 
            PropertiesHandler.getConfig("parametrosweb");

    private static final Configuration FIT_BUNDLE_SECURITY =
            PropertiesHandler.getConfig("security");

    /**
     * No se puede construir.
     */
    private ParametrosWeb() {
    }

    /**
     * Obiene el valor como String
     * 
     * @param clase
     * @param nombre
     * @return Valor
     */
    public static String getValueString(Class<?> clase, String nombre) {
        String property = StringUtils.EMPTY;
        if (clase != null) {
            property = clase.getName() + ".";
        }

        property = property + nombre;
        String value = FIT_BUNDLE.getString(property, "_NOPROPERTY_");
        if ("_NOPROPERTY_".equals(value)) {
            value = BUNDLE.getString(property);
        }

        return value;
    }

    /**
     * Obiene el valor como boolean
     * 
     * @param clase
     * @param nombre
     * @return Valor
     */
    public static boolean getValueBoolean(Class<?> clase, String nombre) {
        String property = StringUtils.EMPTY;
        if (clase != null) {
            property = clase.getName() + ".";
        }

        property = property + nombre;
        String value = FIT_BUNDLE.getString(property, "_NOPROPERTY_");
        if ("_NOPROPERTY_".equals(value)) {
            value = BUNDLE.getString(property);
        }

        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }

    /**
     * Obiene el valor como arreglo de String (separados por coma)
     *
     * @param clase Clase a la que le pertenece la propiedad
     * @param nombre Nombre de la propiedad a buscar
     * @return Valor
     */
    public static List<String> getValueStringList(Class<?> clase, String nombre) {
        String singleValue = ParametrosWeb.getValueString(clase, nombre);
        String[] values = singleValue.split(",");
        return Arrays.asList(values);
    }

    /**
     * Obtiene parámetros genéricos que estarán disponibles en el entorno. Estos
     * parámetros deben estar en el archivo parametros.properties y deben empezar
     * con el prefijo "fitbank".
     * @return Devuelve todos los parámetros encontrados en dicho archivo que
     *  inicien con "fitbank" como un objeto javascript.
     */
    public static String obtenerParametrosJS() {
        JSONObject json = new JSONObject();
        String clasePrefijo = "fitbank";

        Set<String> sKeys = new HashSet<String>(EnumerationUtils.toList(BUNDLE.getKeys()));
        Set<String> sFitKeys = new HashSet<String>(IteratorUtils.toList(FIT_BUNDLE.getKeys()));
        sKeys.addAll(sFitKeys);
        for (String key : sKeys) {
            if (key.startsWith(clasePrefijo)) {
                json.put(key, ParametrosWeb.getValueString(null, key));
            }
        }

        ParametrosWeb.agregarParametrosEspeciales(clasePrefijo, json);

        return "var Parametros = " + json.toString(4) + ";";
    }

    /**
     * Agregar parametros especiales que son compartidos con el backend
     * 
     * @param clasePrefijo Prefijo de propiedades web
     * @param json Json de propiedades actual
     */
    private static void agregarParametrosEspeciales(String clasePrefijo, JSONObject json) {
        //Detectar si se debe encriptar ciertos datos sensibles desde la peticion web
        boolean isWebEncrypt = FIT_BUNDLE_SECURITY.getBoolean("webencrypt", false);
        json.put(clasePrefijo.concat(".").concat("webencrypt"), isWebEncrypt);
        if (isWebEncrypt) {
            String phrase = FIT_BUNDLE_SECURITY.getString("phrase_web", "");
            json.put(clasePrefijo.concat(".").concat("phrase_web"), Base64.encode(phrase));
        }
    }
}