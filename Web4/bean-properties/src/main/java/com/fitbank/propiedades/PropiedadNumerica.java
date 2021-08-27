package com.fitbank.propiedades;

/**
 * Clase PropiedadNumerica.
 * 
 * @author FitBank
 * @version 2.0
 */
public class PropiedadNumerica<T extends Number> extends Propiedad<T> {

    private static final long serialVersionUID = 2L;

    private T min;

    private T max;

    /**
     * Crea un nuevo objeto PropiedadNumerica. Usa el tipo de "valor" como tipo
     * de dato.
     * 
     * @param valorPorDefecto Valor de la propiedad
     */
    public PropiedadNumerica(T valorPorDefecto) {
        this(valorPorDefecto, null, null);
    }

    /**
     * Crea un nuevo objeto PropiedadNumerica.
     * 
     * @param valorPorDefecto Valor por defecto de la propiedad
     * @param min Minimo numero que puede contener esta propiedad
     * @param max Maximo numero que puede contener esta propiedad
     */
    public PropiedadNumerica(T valorPorDefecto, T min, T max) {
        super(valorPorDefecto);

        if (min != null) {
            this.min = min;
        } else {
            this.min = getNumber(valorPorDefecto, Integer.MIN_VALUE);
        }

        if (max != null) {
            this.max = max;
        } else {
            this.max = getNumber(valorPorDefecto, Integer.MAX_VALUE);
        }
    }

    @Override
    public String valorValido(Object o) {
        Double doubleValue = o == null ? Double.NaN : ((Number) o).doubleValue();

        return o instanceof Number && o != null
                && (min == null || doubleValue >= min.doubleValue())
                && (max == null || doubleValue <= max.doubleValue())
                ? VALOR_VALIDO
                : "No es numero o no está en los rangos permitidos: " + min
                + "-" + max;
    }

    public T getNumber() {
        return getNumber(getValor());
    }

    public T getNumber(Object o) {
        return getNumber(getValorPorDefecto(), o);
    }

    @SuppressWarnings("unchecked")
    private T getNumber(Number prueba, Object o) {
        if (prueba instanceof Integer) {
            return (T) Integer.valueOf(o.toString());
        } else if (getValorPorDefecto() instanceof Long) {
            return (T) Long.valueOf(o.toString());
        } else if (getValorPorDefecto() instanceof Byte) {
            return (T) Byte.valueOf(o.toString());
        } else if (getValorPorDefecto() instanceof Double) {
            return (T) Double.valueOf(o.toString());
        } else if (getValorPorDefecto() instanceof Float) {
            return (T) Float.valueOf(o.toString());
        }

        return null;
    }

    @Override
    public void setValorString(String o) {
        setValor(getNumber(getValorPorDefecto(), o));
    }

    @Override
    public String getValorString() {
        return getValor().toString();
    }

}
