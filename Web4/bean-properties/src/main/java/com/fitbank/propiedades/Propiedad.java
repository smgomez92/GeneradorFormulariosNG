package com.fitbank.propiedades;

import com.fitbank.js.JS;
import com.fitbank.util.Clonador;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase abstracta que representa una propiedad. Ver implementaciones.
 * 
 * @author FitBank
 * @version 2.0
 */
public abstract class Propiedad<VALOR extends Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final static String VALOR_VALIDO = "__VALOR_VALIDO__";

    private VALOR valor;

    private VALOR valorPorDefecto;

    private String nombre = "";

    private String descripcion = "";

    private boolean activa = true;

    private boolean error = false;

    private boolean advertencia = false;

    private List<PropiedadListener<VALOR>> listeners =
            new ArrayList<PropiedadListener<VALOR>>();

    /**
     * Constructor por defecto.
     *
     * @param valorPorDefecto Valor por defecto de la propiedad
     */
    public Propiedad(VALOR valorPorDefecto) {
        setValorPorDefecto(valorPorDefecto);
    }

    //<editor-fold defaultstate="collapsed" desc="Getters y setters">
    public VALOR getValor() {
        return valor != null ? valor : getValorPorDefecto();
    }

    public void setValor(VALOR o) {
        String validacion = valorValido(o);
        if (validacion == VALOR_VALIDO) {
            valor = o;
            notifyChange();
        } else {
            throw new IllegalArgumentException("Valor inválido: " + o
                    + (o != null ? " (" + o.getClass() + ")" : "") + ", "
                    + validacion);
        }
    }

    public final VALOR getValorPorDefecto() {
        return valorPorDefecto;
    }

    public final void setValorPorDefecto(VALOR o) {
        if (esValorPorDefecto()) {
            setValor(o);
        }
        valorPorDefecto = Clonador.clonar(o);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public final String getDescripcion() {
        return descripcion;
    }

    public final void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean getActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public boolean getError() {
        return error;
    }

    public void setError() {
        setError(true);
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean getAdvertencia() {
        return advertencia;
    }

    public void setAdvertencia() {
        setAdvertencia(true);
    }

    public void setAdvertencia(boolean advertencia) {
        this.advertencia = advertencia;
    }
    //</editor-fold>

    /**
     * Define si el valor es valido.
     *
     * @param o Valor a revisar
     *
     * @return Propiedad.VALOR_VALIDO si el valor es valido, en caso contrario
     *         una explicación
     */
    public abstract String valorValido(Object o);

    /**
     * Setea un string.
     *
     * @param (String) o Valor a setear
     */
    public abstract void setValorString(String o);

    /**
     * Devuelve un string.
     *
     * @return Valor como String
     */
    @JS(ignore = true)
    public abstract String getValorString();

    protected void notifyChange() {
        for (PropiedadListener<VALOR> propiedadListener : listeners) {
            propiedadListener.onChange(this);
        }
    }

    /**
     * Revisa si el valor es igual al valor por defecto.
     *
     * @return boolean con el resultado
     */
    public boolean esValorPorDefecto() {
        return getValor() != null && getValor().equals(getValorPorDefecto());
    }

    /**
     * Obtiene el valor de esta propiedad para el xml.
     *
     * @return VALOR con el valor
     */
    public VALOR getValorXml() {
        return getValor();
    }

    /**
     * Agrega un listener a esta propiedad.
     * 
     * @param listener
     */
    public void addPropiedadListerner(PropiedadListener<VALOR> listener) {
        this.listeners.add(listener);
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.getDescripcion() + " -> "
                + this.getValorString();
    }

}
