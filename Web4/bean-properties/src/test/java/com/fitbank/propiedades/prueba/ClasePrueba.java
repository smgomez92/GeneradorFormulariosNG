package com.fitbank.propiedades.prueba;

import java.io.Serializable;

public class ClasePrueba implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;

    public ClasePrueba(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ClasePrueba && this.id == ((ClasePrueba) obj).id;
    }
}
