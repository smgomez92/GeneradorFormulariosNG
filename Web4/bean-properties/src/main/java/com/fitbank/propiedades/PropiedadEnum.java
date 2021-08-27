package com.fitbank.propiedades;

import java.util.HashMap;

import com.fitbank.enums.UtilEnums;

/**
 * Clase PropiedadEnum.
 * 
 * @author FitBank
 * @version 2.0
 */
public class PropiedadEnum<T extends Enum<T>> extends PropiedadCombo<T> {

    private static final long serialVersionUID = 2L;

    private Class<T> clase;

    @SuppressWarnings("unchecked")
    public PropiedadEnum(T valorPorDefecto) {
        super(valorPorDefecto, new HashMap<String, T>());

        this.clase = (Class<T>) valorPorDefecto.getClass();

        T[] enums = clase.getEnumConstants();
        for (T element : enums) {
            valores.put(UtilEnums.getDescription(element), element);
        }
    }

    @Override
    public void setValorString(String o) {
        T valor = UtilEnums.getEnumValue(clase, o);
        if (valor != null) {
            setValor(valor);
        }
    }

    @Override
    public String getValorString() {
        return UtilEnums.getValue(getValor());
    }

}
