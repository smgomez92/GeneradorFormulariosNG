package com.fitbank.webpages.data;

import com.fitbank.enums.Modificable;
import com.fitbank.enums.Requerido;

/**
 * Este interfaz sirve para acceder a datos que pueden ser consultables, es
 * decir que tienen un origen de datos y pueden ser usados con la base de datos.
 * 
 * @author FitBank
 * @version 2.0
 */
public interface Queryable {
    /**
     * Obtiene el origen de datos de este elemento.
     * 
     * @return String con el origen de datos
     */
    public DataSource getDataSource();

    /**
     * Cambia el origen de datos de este elemento.
     * 
     * @param datasource
     *            Datasource de datos
     */
    public void setDataSource(DataSource datasource);

    /**
     * Obtiene la propiedad de Requerido de este elemento.
     * 
     * @return valor de tipo Requerido
     */
    public Requerido getRequerido();

    /**
     * Cambia la propiedad de Requerido de este elemento.
     * 
     * @param requerido
     *            Nuevo estado de requerido
     */
    public void setRequerido(Requerido requerido);

    /**
     * Obtiene la propiedad de Modificable de este elemento.
     * 
     * @return
     */
    public Modificable getModificable();

    /**
     * Cambia la propiedad de Modificable de este elemento.
     * 
     * @param modificable
     *            Nuevo estado de modificable
     */
    public void setModificable(Modificable modificable);

    /**
     * Obtiene el número de veces que se clonaría este campo.
     * 
     * @return entero con el número de veces
     */
    public int getRegistrosConsulta();

    /**
     * Obtiene el número de veces que se clonaría este campo.
     * 
     * @return entero con el número de veces
     */
    public int getRegistrosMantenimiento();

}
