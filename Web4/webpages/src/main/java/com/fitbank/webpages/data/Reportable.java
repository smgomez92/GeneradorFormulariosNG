package com.fitbank.webpages.data;

import com.fitbank.enums.Reporte;

/**
 * Interfaz Reportable. Esta interface devuelve o setea el codigo de reporte.
 * 
 * @author FitBank
 * @version 2.0
 */
public interface Reportable {
    /**
     * Obtiene el código de reporte.
     * 
     * @return código de reporte
     */
    public Reporte getCodigoReporte();

    /**
     * Cambia el código de reporte.
     * 
     * @param reporte
     *            código de reporte
     */
    public void setCodigoReporte(Reporte reporte);
}
