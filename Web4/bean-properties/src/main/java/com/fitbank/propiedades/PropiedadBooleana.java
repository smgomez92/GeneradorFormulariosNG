package com.fitbank.propiedades;

import java.util.HashMap;

/**
 * Clase que representa una propiedad booleana.
 * 
 * @author FitBank
 * @version 2.0
 */
public class PropiedadBooleana extends PropiedadCombo<Boolean> {

    private static final long serialVersionUID = 2L;

    /**
     * Contructor de una PropiedadBooleana.
     * 
     * @param descripcion
     *            Descripcion de la propiedad
     * @param valorPorDefecto
     *            Valor por defecto de la propiedad
     */
    public PropiedadBooleana(boolean valorPorDefecto) {
        super(valorPorDefecto, new HashMap<String, Boolean>());

        valores.put("Verdadero", true);
        valores.put("Falso", false);
    }

    @Override
    public void setValorString(String o) {
        setValor(o.equals("1"));
    }

    @Override
    public String getValorString() {
        return getValor() ? "1" : "0";
    }

}
