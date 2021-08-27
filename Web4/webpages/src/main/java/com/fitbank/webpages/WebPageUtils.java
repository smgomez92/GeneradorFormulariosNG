package com.fitbank.webpages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.fitbank.util.Servicios;
import com.fitbank.webpages.data.FormElement;

/**
 * Clase que contiene métodos que se usan sobre las formas.
 * 
 * @author FitBank
 * @version 2.0
 */
public class WebPageUtils {

    private WebPageUtils() {
    }

    // ////////////////////////////////////////////////////////
    // Metodos de elementos
    // ////////////////////////////////////////////////////////
    public static Collection<Class<? extends Widget>> getWidgetSubClasses() {
        List<Class<? extends Widget>> clases =
                new ArrayList<Class<? extends Widget>>();

        for (Widget widget : Servicios.load(Widget.class)) {
            clases.add(widget.getClass());
        }

        Collections.sort(clases, new Comparator<Class<?>>() {

            public int compare(Class<?> o1, Class<?> o2) {
                return WebPageUtils.getDescription(o1).compareTo(
                        WebPageUtils.getDescription(o2));
            }

        });

        return clases;
    }

    /**
     * Método utilizado para leer los valores de un FormElement sin formato.
     *
     * @param (FormElement) dato
     * @param (List <String>) valores
     */
    public static void normalize(final FormElement formElement) {
        normalize(formElement, formElement.getFieldData().getValues());
        normalize(formElement, formElement.getFieldData().getValuesConsulta());
    }

    /**
     * Formatea un valor
     *
     * @param formElement FormElement que tiene el valor
     * @param valor Valor a formatear
     *
     * @return String con el valor formateado
     */
    public static String normalize(FormElement formElement, String valor) {
        return unformat(formElement, format(formElement, valor));
    }

    /**
     * Método utilizado para leer los valores de un FormElement sin formato.
     *
     * @param (FormElement) dato
     * @param (List <String>) valores
     */
    public static void normalize(final FormElement formElement, List<String> valores) {
        CollectionUtils.transform(valores, new Transformer() {

            public Object transform(Object input) {
                return normalize(formElement, (String) input);
            }

        });
    }

    /**
     * Formatea un valor
     *
     * @param formElement FormElement que tiene el valor
     * @param valor Valor a formatear
     *
     * @return String con el valor formateado
     */
    public static String unformat(FormElement formElement, String valor) {
        valor = formElement.getAssistant().unformat(valor);

        for (JSBehavior jsBehavior : formElement.getBehaviors()) {
            if (jsBehavior instanceof Formatter) {
                valor = ((Formatter) jsBehavior).unformat(valor);
            }
        }

        return valor;
    }

    /**
    * Método utilizado para leer los valores de un FormElement sin formato.
    *
    * @param (FormElement) dato
    * @param (List <String>) valores
    */
    public static void unformat(FormElement formElement, List<String> valores) {
        List<String> sinFormato = new LinkedList<String>();

        int n = 0;
        for (String valor : valores) {
            String actual = formElement.getFieldData().getValue(n);
            if (normalize(formElement, actual).equals(valor)) {
                sinFormato.add(actual);
            } else {
                sinFormato.add(unformat(formElement, valor));
            }
            n++;
        }

        formElement.getFieldData().setValues(sinFormato);
    }


    /**
     * Formatea un valor
     *
     * @param formElement FormElement que tiene el valor
     * @param valor Valor a formatear
     *
     * @return String con el valor formateado
     */
    public static String format(FormElement formElement, String valor) {
        String relleno = formElement.getRelleno();
        if (relleno.startsWith("=") && valor.equals(relleno)) {
            return "";
        }

        for (JSBehavior jsBehavior : formElement.getBehaviors()) {
            if (jsBehavior instanceof Formatter) {
                valor = ((Formatter) jsBehavior).format(valor);
            }
        }

        return formElement.getAssistant().format(valor);
    }

    /**
     * Devuelve todos los valores formateados.
     * 
     * @param formElement FormElement del que se saca los valores
     * 
     * @return Colleccion de String
     */
    public static Collection<String> format(final FormElement formElement) {
        List<String> values = formElement.getFieldData().getValues();
        return CollectionUtils.collect(values, new Transformer() {

            public Object transform(Object input) {
                return format(formElement, (String) input);
            }

        });
    }

    /**
     * Metodo que resuelve valores ingresados con operaciones aritméticas
     * 
     * @param value
     *            Valor en String
     * @param absoluto
     *            condición para resultado
     * 
     * @return valor entero
     */
    public static int resolverValor(Object value, boolean absoluto) {
        int valor = 0;
        value = value == null ? "0" : value;
        // TODO: Agregar que resuelva signos aritmeticos incluidos en value
        valor = Integer.parseInt(value.toString(), 10);
        if (absoluto) {
            valor = Math.abs(valor);
        }
        return valor;
    }

    public static String getDescription(Class<?> clase) {
        // FIXME: leer de archivo de descripciones
        return clase.getSimpleName();
    }

}
