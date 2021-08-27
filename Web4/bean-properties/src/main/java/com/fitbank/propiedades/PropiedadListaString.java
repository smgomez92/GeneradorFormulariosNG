package com.fitbank.propiedades;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.fitbank.util.Servicios;

/**
 * Propiedad que crea una lista con los valores como strings.
 *
 * @author FitBank CI
 *
 * @param <TIPO> Tipo de valores
 */
public class PropiedadListaString<TIPO> extends PropiedadLista<TIPO> {

    private static final long serialVersionUID = 1L;

    private String separador = ",";

    public PropiedadListaString(TIPO relleno) {
        super(0, ILIMITADO, relleno);
    }

    public PropiedadListaString(TIPO relleno, String separador) {
        super(0, ILIMITADO, relleno);

        this.separador = separador;
    }

    public PropiedadListaString(int min, int max, TIPO relleno) {
        super(min, max, relleno);
    }

    public PropiedadListaString(TIPO[] valorPorDefecto) {
        this(valorPorDefecto, valorPorDefecto.length, valorPorDefecto.length,
                valorPorDefecto[0], ",");
    }

    public PropiedadListaString(TIPO[] valorPorDefecto, int min, int max,
            TIPO relleno, String separador) {
        super(valorPorDefecto, min, max, relleno, relleno.getClass());

        this.separador = separador;
    }

    @Override
    public String getValorString() {
        String v = getValorAsString(getValor());
        return v.equals("") ? getValorAsString(getValorPorDefecto()) : v;
    }

    public String getValorAsString(Collection<TIPO> valor) {
        return StringUtils.join(valor, separador);
    }

    private String[] split(String string, String substring) {
        if (StringUtils.isBlank(string)) {
            return new String[0];
        } else {
            return string.split(Pattern.quote(substring), -1);
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "deprecation" })
    protected void setObject(Object o, boolean setValorPorDefecto) {
        if (o instanceof String) {
            String[] split = split((String) o, separador);
            List valores = new LinkedList();

            if (getRelleno() instanceof String) {
                valores.addAll(Arrays.asList(split));
            } else if (getRelleno() instanceof Integer) {
                for (String element : split) {
                    valores.add(Integer.parseInt(element));
                }
            } else if (getRelleno() instanceof Float) {
                for (String element : split) {
                    valores.add(Float.parseFloat(element));
                }
            } else if (getRelleno() instanceof Double) {
                for (String element : split) {
                    valores.add(Double.parseDouble(element));
                }
            } else {
                throw new RuntimeException(
                        "No se esta tomando en cuenta el caso de "
                        + getRelleno().getClass());
            }

            super.setObject(valores, setValorPorDefecto);
        } else {
            super.setObject(o, setValorPorDefecto);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public String valorValido(Object o) {
        if (o instanceof String) {
            return super.valorValido(split((String) o, separador));
        } else {
            return super.valorValido(o);
        }
    }

    public String getSeparador() {
        return separador;
    }

}
