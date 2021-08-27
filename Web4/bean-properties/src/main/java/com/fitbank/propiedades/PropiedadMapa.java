package com.fitbank.propiedades;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;

import com.fitbank.util.Debug;
import com.fitbank.util.Servicios;

/**
 * Define una propiedad tipo Mapa.
 *
 * @author FitBank CI
 */
public class PropiedadMapa<T, U> extends Propiedad<Map<T, U>> {

    private static final long serialVersionUID = 2L;

    private Class<?> keyClass;

    private Class<?> itemsClass;

    private Iterable<Class<?>> itemsSubClasses = null;

    public PropiedadMapa(Class<?> keyClass, Class<?> itemsClass) {
        super(new HashMap<T, U>());

        this.keyClass = keyClass;
        this.itemsClass = itemsClass;
        this.itemsSubClasses = Servicios.loadClasses(itemsClass);
    }

    @Override
    public String getValorString() {
        return String.valueOf(getValor());
    }

    @Override
    public String valorValido(Object o) {
        if (o instanceof Map) {
            return VALOR_VALIDO;
        } else {
            return "No es un map";
        }
    }

    @Override
    public void setValorString(String o) {
        Debug.error("No se puede expresar como string esta propiedad");
    }

    public Class<?> getKeyClass() {
        return keyClass;
    }

    public Class<?> getItemsClass() {
        return itemsClass;
    }

    public Collection<Class<?>> getItemsSubClasses() {
        return IteratorUtils.toList(itemsSubClasses.iterator());
    }

}
