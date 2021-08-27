package com.fitbank.propiedades;

/**
 * Clase PropiedadRegEx.
 * 
 * @author FitBank
 * @version 2.0
 */
public abstract class PropiedadRegEx extends Propiedad<String> {

    private static final long serialVersionUID = 1L;

    private String regEx = ".*";

    /**
     * Crea un nuevo objeto PropiedadRegEx.
     * 
     * @param valorPorDefecto
     *            Valor por defecto de la propiedad
     */
    public PropiedadRegEx(String valorPorDefecto) {
        super(valorPorDefecto);
    }

    /**
     * Crea un nuevo objeto PropiedadRegEx.
     * 
     * @param valorPorDefecto
     *            Valor por defecto de la propiedad
     * @param regEx
     *            Expresion regular
     */
    public PropiedadRegEx(String valorPorDefecto, String regEx) {
        super(valorPorDefecto);

        this.regEx = regEx;
    }

    @Override
    public String valorValido(Object o) {
        return !(o instanceof String) || ((String) o).matches(regEx)
                ? VALOR_VALIDO : o + " no coincide con " + regEx;
    }

}
