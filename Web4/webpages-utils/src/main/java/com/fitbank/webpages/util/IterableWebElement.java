package com.fitbank.webpages.util;

import java.util.Iterator;

import com.fitbank.webpages.WebElement;
import com.fitbank.webpages.Widget;
import java.util.NoSuchElementException;

/**
 * Clase que crea un Iterable que devuelve solo elementos del tipo indicado
 * 
 * @author FitBank
 * @version 2.0
 * @param T
 *            Tipo de elemento a devolver
 */
public class IterableWebElement<T> implements Iterable<T> {

    private Class<T> clase;

    private WebElement<?> webElement;

    /**
     * Constructor privado.
     * 
     * @param (WebElement) webElement
     * @param (Class<T>) clase
     */
    private IterableWebElement(WebElement<?> baseFormas, Class<T> clase) {
        this.webElement = baseFormas;
        this.clase = clase;
    }

    /**
     * Obtiene un iterador que devuelve todos los WebElement que se encuentran
     * en el contenedor y en subcontenedores tambien.
     * 
     * Ej: IterableWebElement.get(contenedor, Widget.class)
     * 
     * @param <T>
     *            Tipo de elementos a ser devueltos.
     * @param webElement
     *            elementoWeb que se va a usar de base para la búsqueda
     * @param clase
     *            Solo se devuelven elementos de esta clase
     * @return IterableWebElement
     */
    public static <T> IterableWebElement<T> get(WebElement<?> webElement,
            Class<T> clase) {
        return new IterableWebElement<T>(webElement, clase);
    }

    public Iterator<T> iterator() {
        return new WebElementIterator<T>(webElement, clase);
    }

    /**
     * Clase que crea un Iterator que devuelve solo elementos del tipo indicado
     * recursivamente. Crearlo con IterableWebElement.get(...).
     *
     * @author FitBank
     * @version 2.0
     * @param T
     *            Tipo de elemento a devolver
     * @see IterableWebElement#get
     */
    private class WebElementIterator<T> implements Iterator<T> {

        private Iterator<WebElement<?>> own;

        private Iterator<T> internal;

        private Class<T> clase;

        private T next;

        private boolean alreadyAsked = false;

        @SuppressWarnings("unchecked")
        protected WebElementIterator(final WebElement webElement, Class<T> clase) {
            this.own = webElement.iterator();
            this.clase = clase;
        }

        @SuppressWarnings("unchecked")
        private boolean buscarSiguiente() {
            if (internal == null) {
                while (own.hasNext()) {
                    WebElement o = own.next();

                    if (clase.isAssignableFrom(o.getClass()) && o instanceof Widget
                            && ((Widget) o).esActivoFilaActual()) {
                        next = (T) o;

                        return true;
                    } else {
                        internal = new WebElementIterator<T>(o, clase);

                        return buscarSiguiente();
                    }
                }

                next = null;
            } else {
                if (internal.hasNext()) {
                    next = internal.next();

                    return true;
                } else {
                    internal = null;

                    return buscarSiguiente();
                }
            }

            return false;
        }

        public boolean hasNext() {
            if (alreadyAsked) {
                return next != null;
            }

            alreadyAsked = true;

            return buscarSiguiente();
        }

        public T next() {
            if (!alreadyAsked && !hasNext()) {
                throw new NoSuchElementException();
            }

            alreadyAsked = false;

            return next;
        }

        /**
         * No se soporta esta operacion por el momento.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
