package com.fitbank.propiedades;

import java.util.Collection;

import org.apache.commons.collections.IteratorUtils;

import com.fitbank.util.Servicios;

/**
 * Propiedad pera un objeto genérico.
 *
 * @author FitBank CI
 *
 * @param <T> Tipo del objeto
 */
public class PropiedadObjeto<T> extends Propiedad<T> {

    private static final long serialVersionUID = 1L;

    private Class<?> instanceClass = Object.class;

    private Iterable<Class<?>> instanceSubClasses = null;

    public PropiedadObjeto(T valorPorDefecto, Class<?> clase) {
        super(valorPorDefecto);

        instanceClass = clase;
        instanceSubClasses = Servicios.loadClasses(instanceClass);
    }

    @Override
    public String getValorString() {
        return String.valueOf(getValor());
    }

    @Override
    public void setValorString(String o) {
        throw new Error("No se puede convertir desde string: " + o);
    }

    @Override
    public String valorValido(Object o) {
        return o == null || instanceClass.isAssignableFrom(o.getClass()) ? Propiedad.VALOR_VALIDO
                : "Objeto no es de la clase requerida: "
                        + (o != null ? o.getClass() : null);
    }

    public Class<?> getInstanceClass() {
        return instanceClass;
    }

    @SuppressWarnings("unchecked")
    public Collection<Class<?>> getInstanceSubClasses() {
        return IteratorUtils.toList(instanceSubClasses.iterator());
    }

}
