package com.fitbank.propiedades;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Clase PropiedadCombo.
 * 
 * @author FitBank
 * @version 2.0
 */
public abstract class PropiedadCombo<T> extends Propiedad<T> {

    private static final long serialVersionUID = 1L;

    protected Map<String, T> valores = new LinkedHashMap<String, T>();

    /**
     * Crea un nuevo objeto PropiedadMultiple.
     * 
     * @param valorPorDefecto Valor por defecto de la propiedad
     * @param valores Valores que puede tomar la propiedad
     */
    public PropiedadCombo(T valorPorDefecto, Map<String, T> valores) {
        super(valorPorDefecto);
        setValores(valores);
    }

    @Override
    public String valorValido(Object o) {
        return valores.containsValue((T) o) ? VALOR_VALIDO : o
                + " no se encuentra dentro de los valores validos";
    }

    /**
     * Cambia los valores que puede tomar esta propiedad.
     * 
     * @param valores Valores
     */
    public final void setValores(Map<String, T> valores) {
        this.valores = valores;
    }

    /**
     * Obtiene la lista de valores que puede tomar la propiedad.
     * 
     * @return String[] con los valores
     */
    public Map<String, T> getEtiquetas() {
        Map<String, T> ret = new LinkedHashMap<String, T>();

        for (String key : valores.keySet()) {
            ret.put(key + " (" + valores.get(key) + ")", valores.get(key));
        }

        return ret;
    }

    public String getEtiquetaSeleccionada() {
        int cual = 0;

        for (T valor : valores.values()) {
            if (valor.equals(getValor())) {
                return getEtiqueta(cual);
            }
            cual++;
        }

        throw new Error("Valor inválido: " + getValor() + ", " + this);
    }

    public String getEtiqueta(int cual) {
        String key = new ArrayList<String>(valores.keySet()).get(cual);
        return key + " (" + valores.get(key) + ")";
    }

    public T getValor(int cual) {
        return new ArrayList<T>(valores.values()).get(cual);
    }

}
