package com.fitbank.propiedades;

import com.fitbank.util.AbstractBoundedList;
import java.util.List;

public class ListaLimitada<TIPO> extends AbstractBoundedList<TIPO> {

    private static final long serialVersionUID = 1L;

    private PropiedadLista<TIPO> limites;

    public ListaLimitada(List<TIPO> original, PropiedadLista<TIPO> limites) {
        super(original);

        this.limites = limites;

        testMax(original.size());
        testMin(original.size());
    }

    @Override
    protected void testMax(int cuantos) {
        if (cuantos > limites.getMax()) {
            throw new IndexOutOfBoundsException("Indice fuera del rango: "
                    + cuantos + " > max=" + limites.getMax());
        }
    }

    @Override
    protected void testMin(int cuantos) {
        if (cuantos < limites.getMin()) {
            throw new IndexOutOfBoundsException("Indice fuera del rango: "
                    + cuantos + " < min=" + limites.getMin());
        }
    }

    @Override
    protected TIPO getRelleno() {
        return limites.getRelleno();
    }

}
