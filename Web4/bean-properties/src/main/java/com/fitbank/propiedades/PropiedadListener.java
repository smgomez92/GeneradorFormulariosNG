package com.fitbank.propiedades;

import java.io.Serializable;

public interface PropiedadListener<VALOR> extends Serializable {

    public void onChange(Propiedad<VALOR> propiedad);

}
