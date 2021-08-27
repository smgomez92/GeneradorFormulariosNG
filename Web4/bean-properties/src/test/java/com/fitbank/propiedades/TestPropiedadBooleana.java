package com.fitbank.propiedades;

import junit.framework.TestCase;

public class TestPropiedadBooleana extends TestCase {

    public void testDefault() {
        PropiedadBooleana defaultTrue = new PropiedadBooleana(true);
        PropiedadBooleana defaultFalse = new PropiedadBooleana(false);

        assertEquals(Boolean.TRUE, defaultTrue.getValor());
        assertEquals(Boolean.FALSE, defaultFalse.getValor());
    }

}
