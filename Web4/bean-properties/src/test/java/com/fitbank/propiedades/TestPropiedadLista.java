package com.fitbank.propiedades;

import junit.framework.TestCase;

import com.fitbank.propiedades.prueba.ClasePrueba;

public class TestPropiedadLista extends TestCase {

    public void testDefault() {
        PropiedadLista<ClasePrueba> lista = new PropiedadLista<ClasePrueba>(
                new ClasePrueba[] { new ClasePrueba(1) }, new ClasePrueba(0));

        assertEquals(1, lista.getValor().size());
    }

    public void testItemsClass() {
        PropiedadLista<ClasePrueba> lista = new PropiedadLista<ClasePrueba>(
                new ClasePrueba[] { new ClasePrueba(1) }, new ClasePrueba(0));

        assertEquals(ClasePrueba.class, lista.getItemsClass());
    }

    public void testFijo() {
        PropiedadLista<ClasePrueba> lista = new PropiedadLista<ClasePrueba>(
                5, 5, new ClasePrueba(1));

        assertEquals(5, lista.getValor().size());
        try {
            lista.add(new ClasePrueba(2));
        } catch (IndexOutOfBoundsException e) {
            assertEquals(5, lista.getValor().size());
        }
        lista.getList().set(0, new ClasePrueba(2));
        assertEquals(new ClasePrueba(2), lista.getValor(0));
    }

    public void testFlexible() {
        PropiedadLista<ClasePrueba> lista = new PropiedadLista<ClasePrueba>(
                5, 10, new ClasePrueba(1));

        assertEquals(5, lista.getValor().size());
    }

    public void testIlimitado() {
        PropiedadLista<ClasePrueba> lista = new PropiedadLista<ClasePrueba>(
                5, new ClasePrueba(1));

        assertEquals(5, lista.getValor().size());
    }
}
