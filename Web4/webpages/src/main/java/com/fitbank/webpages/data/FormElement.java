package com.fitbank.webpages.data;

import java.util.Collection;

import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.JSBehavior;

/**
 * Esta interfaz sirve para acceder a cualquier objeto que represente un
 * elemento en un formulario html. Tiene dos formas de uso. Se puede usar como
 * un elemento de un solo valor. Tambien se puede usar como un elemento de
 * multiples valores. Se tiene en ambos casos dos tipos de <code>value</code>.
 * El <code>value</code> inicial que es con el que el elemento se inicializa y
 * el <code>value</code> actual que es el que tiene en un determinado momento.
 * De igual forma se aplica para el caso en que se tengan valores múltiples.
 * 
 * @author FitBank
 * @version 2.0
 */
public interface FormElement extends Queryable {

    /**
     * Obtiene la información sobre los valores de este elemento.
     * 
     * @return Un objeto FieldData
     */
    public FieldData getFieldData();

    /**
     * Obtiene la propiedad <code>value</code> actual del elemento del
     * formulario html en la fila actual.
     * 
     * 
     * @return Un String con el valor
     */
    public String getValueFilaActual();

    /**
     * Obtiene la propiedad <code>value</code> de consulta del elemento del
     * formulario html en la fila actual.
     * 
     * 
     * @return Un String con el valor
     */
    public String getValueConsultaFilaActual();

    /**
     * Obtiene el valor que se usará para rellenar en la clonación.
     * 
     * @return String con el relleno
     */
    public String getRelleno();

    /**
     * Obtiene la propiedad <code>name</code> actual del elemento del formulario
     * html o un valor por default.
     *
     * @return String con el nombre
     */
    public String getNameOrDefault();

    /**
     * Obtiene la propiedad <code>name</code> actual del elemento del formulario
     * html.
     *
     * @return String con el nombre
     */
    public String getName();

    /**
     * Obtiene un ID unico dependiendo de la fila actual del elemento y el name.
     *
     * @return String con el nombre
     */
    public String getHTMLId();

    /**
     * Cambia la propiedad <code>name</code> actual del elemento del formulario
     * html.
     * 
     * @param name
     *            Nuevo nombre
     */
    public void setName(String name);

    /**
     * Actualiza las properties valoresIniciales y valoresActuales.
     */
    public void actualizarPropiedadesValores();

    /**
     * Coloca el Assistant
     */
    public Assistant getAssistant();

    /**
     * Coloca el Assistant
     */
    public void setAssistant(Assistant assistant);

    /**
     * Define si es visible
     */
    public boolean getVisible();

    /**
     * Oculto o muestra el campo
     */
    public void setVisible(boolean visible);

    /**
     * Define si es limpiable
     */
    public boolean getLimpiable();

    /**
     * Setea la propiedad limpiable
     */
    public void setLimpiable(boolean limpiable);

    /**
     * Obtiene una coleccion de comportamientos de js.
     */
    public Collection<JSBehavior> getBehaviors();

}
