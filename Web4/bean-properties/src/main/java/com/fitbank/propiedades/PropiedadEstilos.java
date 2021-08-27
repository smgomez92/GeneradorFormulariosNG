package com.fitbank.propiedades;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Clase que representa una propiedad que tiene estilos.
 * 
 * @author FitBank
 * @version 2.0
 */
public class PropiedadEstilos extends PropiedadComboLibre {

    private static final long serialVersionUID = 2L;

    /**
     * Constructor con tag y titulo.
     */
    public PropiedadEstilos() {
        super("", new LinkedList<String>());
    }

    /**
     * Setea los estilos en esta propiedad.
     * 
     * @param estilos Lista de estilos que puede tomar esta propiedad.
     */
    public void setEstilos(Collection<String> estilos) {
        valores = new LinkedHashMap<String, String>();

        for (String estilo : estilos) {
            valores.put(estilo, estilo);
        }
    }

    @Override
    public void setValorString(String o) {
        setValor(o);
    }

    @Override
    public String getValorString() {
        return getValor();
    }

}
