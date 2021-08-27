package com.fitbank.propiedades;

import java.util.Map;

/**
 * Propiedad que contiene objetos y se presenta un combo para elegirlos.
 *
 * @author FitBank CI
 *
 * @param <T> Tipo de los objetos
 */
public class PropiedadComboObjetos<T> extends PropiedadCombo<T> {

    private static final long serialVersionUID = 1L;

    public PropiedadComboObjetos(T valorPorDefecto, Map<String, T> valores) {
        super(valorPorDefecto, valores);
    }

    @Override
    public String getValorString() {
        return null;
    }

    @Override
    public void setValorString(String o) {
    }

}
