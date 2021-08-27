package com.fitbank.propiedades;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;

import com.fitbank.util.Clonador;
import com.fitbank.util.Servicios;

/**
 * Clase PropiedadLista.
 * 
 * @author FitBank
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class PropiedadLista<TIPO> extends Propiedad<Collection<TIPO>> {

    private static final long serialVersionUID = 2L;

    public static final int ILIMITADO = Integer.MAX_VALUE;

    private int min = 0;

    private int max = ILIMITADO;

    private TIPO relleno = null;

    private Class<?> itemsClass;

    private Iterable<Class<?>> itemsSubClasses = null;

    private static <T> T[] crearArray(T relleno, int capacidad) {
        List<T> l = new LinkedList<T>();

        while (l.size() < capacidad) {
            l.add(Clonador.clonar(relleno));
        }

        return (T[]) l.toArray();
    }

    /**
     * Crea un nuevo objeto PropiedadLista con tamaño variable.
     * 
     * @param min Minimo numero de elementos en la lista
     * @param max Maximo numero de elementos en la lista
     * @param relleno Relleno al momento de inicializar
     */
    public PropiedadLista(int min, TIPO relleno) {
        this(crearArray(relleno, min), min, ILIMITADO, relleno,
                relleno.getClass());
    }

    /**
     * Crea un nuevo objeto PropiedadLista con tamaño variable.
     * 
     * @param min Minimo numero de elementos en la lista
     * @param max Maximo numero de elementos en la lista
     * @param relleno Relleno al momento de inicializar
     */
    public PropiedadLista(int min, int max, TIPO relleno) {
        this(crearArray(relleno, min), min, max, relleno, relleno.getClass());
    }

    /**
     * Crea un nuevo objeto PropiedadLista con un tamaño fijo.
     *
     * @param valorPorDefecto Valor por defecto de la propiedad
     */
    public PropiedadLista(Collection<TIPO> valorPorDefecto, TIPO relleno) {
        this((TIPO[]) valorPorDefecto.toArray(), 0, ILIMITADO, relleno, relleno.
                getClass());
    }

    /**
     * Crea un nuevo objeto PropiedadLista con un tamaño fijo.
     *
     * @param valorPorDefecto Valor por defecto de la propiedad
     * @param claseRelleno Clase de relleno
     */
    public PropiedadLista(Collection<TIPO> valorPorDefecto,
            Class<?> claseRelleno) {
        this((TIPO[]) valorPorDefecto.toArray(), 0, ILIMITADO, null,
                claseRelleno);
    }

    /**
     * Crea un nuevo objeto PropiedadLista con un tamaño fijo.
     * 
     * @param valorPorDefecto Valor por defecto de la propiedad
     */
    public PropiedadLista(TIPO[] valorPorDefecto, TIPO relleno) {
        this(valorPorDefecto, valorPorDefecto.length,
                valorPorDefecto.length, relleno, relleno.getClass());
    }

    /**
     * Crea un nuevo objeto PropiedadLista.
     * 
     * @param valorPorDefecto Valor por defecto de la propiedad
     * @param min Minimo numero de elementos en la lista
     * @param max Maximo numero de elementos en la lista
     * @param relleno Relleno
     * @param claseRelleno Clase de relleno
     */
    protected PropiedadLista(TIPO[] valorPorDefecto, int min, int max,
            TIPO relleno, Class<?> claseRelleno) {
        super(new LinkedList<TIPO>());

        this.min = min;
        this.max = max;

        if (min > max) {
            throw new IndexOutOfBoundsException("min=" + min + " > max=" + max);
        }

        this.relleno = relleno;

        setObject(valorPorDefecto, false);
        setObject(valorPorDefecto, true);

        if (claseRelleno != null) {
            this.itemsClass = claseRelleno;
        } else if (relleno != null) {
            this.itemsClass = relleno.getClass();
        } else if (getList().size() > 0) {
            this.itemsClass = getList().get(0).getClass();
        } else {
            throw new Error("No se especifico tipo de la lista");
        }
        itemsSubClasses = Servicios.loadClasses(itemsClass);
    }

    @Override
    public String getValorString() {
        return Arrays.toString(getValor().toArray());
    }

    /**
     * Obtiene un valor de la lista de esta propiedad.
     * 
     * @param cual Posicion
     * 
     * @return Object con el valor
     */
    public TIPO getValor(int cual) {
        return getList().get(cual);
    }

    /**
     * Cambia el valor de esta propiedad.
     * 
     * @param o Objecto con el nuevo valor
     */
    protected void setObject(Object o, boolean setValorPorDefecto) {
        String validacion = valorValido(o);

        if (validacion == VALOR_VALIDO) {
            if (o instanceof List) {
                if (setValorPorDefecto) {
                    setValorPorDefecto((List<TIPO>) o);
                } else {
                    setValor((List<TIPO>) o);
                }
            } else if (o instanceof Collection) {
                List<TIPO> lista = new LinkedList<TIPO>(
                        (Collection<? extends TIPO>) o);
                if (setValorPorDefecto) {
                    setValorPorDefecto(lista);
                } else {
                    setValor(lista);
                }
            } else if (o.getClass().isArray()) {
                List<TIPO> lista = new ArrayList<TIPO>();
                Collections.addAll(lista, (TIPO[]) o);

                if (setValorPorDefecto) {
                    setValorPorDefecto(lista);
                } else {
                    setValor(lista);
                }
            }
        } else {
            if (o != null && o.getClass().isArray()) {
                o = Arrays.toString((Object[]) o) + ", length:"
                        + ((Object[]) o).length;
            } else if (o instanceof Collection) {
                o = o + ", size:" + ((Collection) o).size();
            }
            throw new IllegalArgumentException("Valor inválido: " + o + " -> "
                    + this.toString() + ", " + validacion);
        }
    }

    @Override
    public void setValor(Collection<TIPO> o) {
        if (!(o instanceof List)) {
            o = new LinkedList<TIPO>(o);
        }
        super.setValor(new ListaLimitada((List<TIPO>) o, this));
    }

    /**
     * Cambia el valor de esta propiedad.
     * 
     * @param indice Posicion en la que se quiere añadir
     * @param o Objecto con el nuevo valor
     */
    public void setValor(int indice, TIPO o) {
        getList().set(indice, o);
    }

    /**
     * Define si el valor es valido.
     * 
     * @param o Valor a revisar
     * 
     * @return true si el valor es valido
     */
    @Override
    public String valorValido(Object o) {
        if (o instanceof Collection) {
            return valorValido(((Collection) o).toArray());
        } else if (o.getClass().isArray()) {
            return ((Object[]) o).length <= getMax()
                    && ((Object[]) o).length >= getMin() ? VALOR_VALIDO
                    : String.valueOf(o) + " no esta dentro de los "
                    + "limites permitidos: " + getMin() + "-"
                    + getMax();
        } else {
            return o + " no es Collection ni Array";
        }
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public TIPO getRelleno() {
        return relleno;
    }

    public void setRelleno(TIPO relleno) {
        this.relleno = relleno;
    }

    @Override
    public void setValorString(String o) {
        setObject(o, false);
    }

    /**
     * Agrega un elemento de clase TIPO a la lista
     * 
     * @param t Elemento a ser agregado
     * 
     * @return true si la lista fue cambiada, si no false
     */
    public boolean add(TIPO t) {
        return getValor().add(t);
    }

    /**
     * Clona la lista y cambia el valor de esta propiedad.
     * 
     * @param lista Lista de TIPO con los valores.
     */
    public void clonar(List<TIPO> lista) {
        if (lista instanceof ListaLimitada) {
            setValor(Clonador.clonar(((ListaLimitada) lista).getOriginal()));
        } else {
            setValor(Clonador.clonar(lista));
        }
    }

    /**
     * Reajusta la propiedad hasta que contenga el valor mínimo de elementos.
     */
    public void resetear() {
        ((ListaLimitada<TIPO>) getValor()).contract(getMin());
        ((ListaLimitada<TIPO>) getValor()).expand(getMin());
        for (int i = 0; i < getMin(); i++) {
            setValor(i, getRelleno());
        }
    }

    /**
     * Obtiene el valor de esta propiedad como un list.
     * 
     * @return El valor como List<TIPO>
     */
    public List<TIPO> getList() {
        return (List<TIPO>) getValor();
    }

    public Class<?> getItemsClass() {
        return itemsClass;
    }

    public Collection<Class<?>> getItemsSubClasses() {
        return IteratorUtils.toList(itemsSubClasses.iterator());
    }

}
