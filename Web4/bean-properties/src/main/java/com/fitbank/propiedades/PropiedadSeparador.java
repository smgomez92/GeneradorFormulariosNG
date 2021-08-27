package com.fitbank.propiedades;

/**
 * Propiedad que presenta un separador con o sin título.
 * 
 * @author FitBank
 * @version 2.0
 */
public class PropiedadSeparador extends Propiedad<String> {

    private static final long serialVersionUID = 2L;

    public PropiedadSeparador() {
        super("");
    }

    /**
     * Constructor con título.
     * 
     * @param titulo Es el título del separador
     */
    public PropiedadSeparador(String titulo) {
        super("");

        setDescripcion(titulo);
    }

    @Override
    public boolean esValorPorDefecto() {
        return true;
    }

    @Override
    public String valorValido(Object o) {
        return VALOR_VALIDO;
    }

    @Override
    public void setValorString(String o) {
    }

    @Override
    public String getValorString() {
        return "";
    }

}
