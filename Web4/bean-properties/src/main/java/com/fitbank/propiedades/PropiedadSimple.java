package com.fitbank.propiedades;

/**
 * Clase PropiedadSimple.
 * 
 * @author FitBank
 * @version 2.0
 */
public class PropiedadSimple extends Propiedad<String> {

    private static final long serialVersionUID = 2L;

    public PropiedadSimple() {
        super("");
    }

    public PropiedadSimple(String valorPorDefecto) {
        super(valorPorDefecto);
    }

    @Override
    public String valorValido(Object o) {
        return o instanceof String ? VALOR_VALIDO : o
                + " no es instancia de String";
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
