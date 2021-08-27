package com.fitbank.webpages;

import java.util.Collection;

import com.fitbank.enums.DataSourceType;
import com.fitbank.serializador.html.SerializableHtml;
import com.fitbank.serializador.xml.XML;
import com.fitbank.webpages.data.FormElement;

/**
 * Interfaz de todos los asistentes del sistema. Un asistente es una mezcla
 * entre una clase de java y una de javascript que ayudan a ingresar datos en
 * un campo.
 *
 * @author FitBank CI
 */
public interface Assistant extends SerializableHtml {

    /**
     * Inicializa el asistente.
     *
     * @param formElement FormElement donde se puso este asistente.
     */
    public void init(FormElement formElement);

    /**
     * Formatea el valor del campo para ser presentado
     *
     * @param unformatedValue Valor sin formato
     *
     * @return String con el valor formateado
     */
    public String format(String unformatedValue);

    /**
     * Quita el formato del campo para enviarlo a la base de datos.
     *
     * @param formatedValue Valor formateado
     *
     * @return String con el valor sin formato
     */
    public String unformat(String formatedValue);

    /**
     * Obtiene la representación en el tipo de dato adecuado.
     *
     * @param unformatedValue Valor sin formato
     *
     * @return String con el valor formateado
     */
    public Object asObject(String value);

    /**
     * Define si se va a leer el valor desde el campo o es un proceso especial
     * el que lo lee.
     *
     * @return true si se lee, o si no false
     */
    public boolean readFromHttpRequest();

    /**
     * Define si este asistente usa un icono
     *
     * @return true si usa, si no usa devuelve false
     */
    public boolean usesIcon();

    /**
     * Define sobre que tipos de DataSource se aplica este asistente.
     *
     * @return una colección de DataSourceType
     */
    public Collection<DataSourceType> applyTo();

    /**
     * Obtiene el nombre del elemento sobre el que se aplica este asistente.
     *
     * @return String con el nombre
     */
    @XML(ignore = true)
    public String getElementName();

    /**
     * Obtiene el tipo de elemento (text, password, etc).
     *
     * @return String con el tipo
     */
    @XML(ignore = true)
    public String getType();

}
