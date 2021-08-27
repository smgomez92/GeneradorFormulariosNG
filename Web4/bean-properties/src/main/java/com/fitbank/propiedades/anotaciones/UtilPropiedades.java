package com.fitbank.propiedades.anotaciones;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaBean;

import com.fitbank.js.LiteralJS;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadComboObjetos;
import com.fitbank.propiedades.PropiedadEnum;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadLista;
import com.fitbank.propiedades.PropiedadListaString;
import com.fitbank.propiedades.PropiedadListener;
import com.fitbank.propiedades.PropiedadMapa;
import com.fitbank.propiedades.PropiedadNumerica;
import com.fitbank.propiedades.PropiedadObjeto;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.util.Debug;
import com.fitbank.util.Editable;
import com.fitbank.util.MultiplePropertyResourceBundle;
import com.fitbank.util.Pair;
import com.fitbank.util.Servicios;

public class UtilPropiedades {

    private static final ResourceBundle DESCRIPTIONS =
            new MultiplePropertyResourceBundle(
            "descriptions");

    /**
     * Obtiene una colección de propiedades dado un objeto.
     * 
     * @param object
     *            Objeto del que se van a obtener las propiedades
     * 
     * @return Collection con las propiedades
     */
    public static Collection<Propiedad<?>> getPropiedades(Object object) {
        return getMapaPropiedades(object).values();
    }

    /**
     * Obtiene un mapa de propiedades dado un objeto.
     *
     * @param object
     *            Objeto del que se van a obtener las propiedades
     *
     * @return Map que apunta de String a cada propiedad
     */
    public static Map<String, Propiedad<?>> getMapaPropiedades(Object object) {
        Map<String, Propiedad<?>> propiedades =
                new LinkedHashMap<String, Propiedad<?>>();
        List<Pair<String, Editable>> editables =
                new LinkedList<Pair<String, Editable>>();

        DynaBean db = new WrapDynaBean(object);
        DynaBean example = db;
        try {
            example = db.getDynaClass().newInstance();
        } catch (IllegalAccessException e) {
            Debug.warn("No se pudo crear ejemplo, usando objeto", e);
        } catch (InstantiationException e) {
            Debug.warn("No se pudo crear ejemplo, usando objeto", e);
        }

        for (DynaProperty dp : db.getDynaClass().getDynaProperties()) {
            Pair<Propiedad<?>, Editable> p = getPropiedadEditable(object, db,
                    example, dp);
            if (p != null) {
                propiedades.put(dp.getName(), p.getFirst());
                editables.add(new Pair<String, Editable>(dp.getName(), p.
                        getSecond()));
            }
        }

        Collections.sort(editables, new Comparator<Pair<String, Editable>>() {

            public int compare(Pair<String, Editable> o1,
                    Pair<String, Editable> o2) {
                return new Integer(o1.getSecond().weight()).compareTo(o2.
                        getSecond().weight());
            }

        });

        Map<String, Propiedad<?>> ordenadas =
                new LinkedHashMap<String, Propiedad<?>>();

        for (Pair<String, Editable> p : editables) {
            ordenadas.put(p.getFirst(), propiedades.get(p.getFirst()));
        }

        return ordenadas;
    }

    /**
     * Obtiene una propiedad suelta para un valor.
     *
     * @param object Objeto del que se quiere obtener una propiedad
     * @param valor Valor que se quiere obtener
     * @param nombre Nombre de la propiedad del objeto
     *
     * @return Propiedad
     */
    public static Propiedad<?> getPropiedad(Object object, Object value,
            String nombre, String... hints) {
        Propiedad propiedad = getPropiedad(value.getClass(), value, hints);

        propiedad.setNombre(nombre);
        propiedad.setDescripcion(getDescription(object.getClass(), nombre));

        return propiedad;
    }

    /**
     * Obtiene una propiedad suelta para un valor.
     *
     * @param object
     *            Objeto del que se quiere obtener una propiedad
     * @param nombre
     *            Nombre de la propiedad del objeto
     *
     * @return Propiedad
     */
    public static Propiedad<?> getPropiedad(Object object, String nombre) {
        DynaBean db = new WrapDynaBean(object);
        DynaBean example = db;
        try {
            example = db.getDynaClass().newInstance();
        } catch (IllegalAccessException e) {
            Debug.warn("No se pudo crear ejemplo, usando objeto", e);
        } catch (InstantiationException e) {
            Debug.warn("No se pudo crear ejemplo, usando objeto", e);
        }

        DynaProperty dp = db.getDynaClass().getDynaProperty(nombre);

        return getPropiedadEditable(object, db, example, dp).getFirst();
    }

    private static Pair<Propiedad<?>, Editable> getPropiedadEditable(
            Object object, DynaBean db, DynaBean example, DynaProperty dp) {
        Editable editable = null;

        try {
            Method method = PropertyUtils.getPropertyDescriptor(object,
                    dp.getName()).getReadMethod();

            if (method.isAnnotationPresent(Editable.class)) {
                editable = method.getAnnotation(Editable.class);
            }
        } catch (Exception e) {
            // Probar siguiente
        }

        try {
            if (editable == null) {
                Field field =
                        Servicios.getField(object.getClass(), dp.getName());

                if (field != null && field.isAnnotationPresent(Editable.class)) {
                    editable = field.getAnnotation(Editable.class);
                }
            }
        } catch (Exception e1) {
            return null;
        }

        if (editable != null) {
            return new Pair<Propiedad<?>, Editable>(getPropiedad(object, db,
                    example, dp, editable, db.get(dp.getName())), editable);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static Propiedad<?> getPropiedad(Object object, final DynaBean db,
            final DynaBean example, final DynaProperty dp, Editable editable,
            Object valorPorDefecto) {
        Propiedad p = getPropiedad(Servicios.getType(object, dp.getName()),
                example.get(dp.getName()), editable.hints());

        p.setNombre(dp.getName());
        p.setDescripcion(getDescription(object.getClass(), dp));
        p.setValor(valorPorDefecto);

        p.addPropiedadListerner(new PropiedadListener() {

            public void onChange(Propiedad propiedad) {
                if (propiedad instanceof PropiedadLista) {
                    ((Collection) db.get(dp.getName())).clear();
                    ((Collection) db.get(dp.getName())).addAll(((PropiedadLista) propiedad).
                            getList());
                } else {
                    db.set(dp.getName(), propiedad.getValor());
                }
            }

        });

        return p;
    }

    private static String getDescription(Class<?> clase, DynaProperty dp) {
        return getDescription(clase, dp.getName());
    }

    public static String getDescription(Class<?> clase, String property) {
        try {
            String name = clase.getName() + "." + property;
            return DESCRIPTIONS.getString(name);
        } catch (MissingResourceException e) {
            if (clase.getSuperclass() != Object.class) {
                return getDescription(clase.getSuperclass(), property);
            } else {
                return "[" + property + "]";
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Propiedad<?> getPropiedad(Type type, Object valorPorDefecto,
            String... hints) {
        Class<?> clase = Servicios.getClassFromType(type);
        Set<String> hintSet = new HashSet<String>(Arrays.asList(hints));

        if (String.class.isAssignableFrom(clase)) {
            if (hintSet.contains("javascript")) {
                return new PropiedadJavascript((String) valorPorDefecto);
            } else {
                return new PropiedadSimple((String) valorPorDefecto);
            }

        } else if (LiteralJS.class.isAssignableFrom(clase)) {
            return new PropiedadJavascript((LiteralJS) valorPorDefecto);

        } else if (clase.equals(Integer.TYPE) || clase.equals(Double.TYPE)
                || clase.equals(Float.TYPE)
                || Number.class.isAssignableFrom(clase)) {
            return new PropiedadNumerica<Number>((Number) valorPorDefecto);

        } else if (clase.equals(Boolean.TYPE)
                || Boolean.class.isAssignableFrom(clase)) {
            return new PropiedadBooleana((Boolean) valorPorDefecto);

        } else if (Collection.class.isAssignableFrom(clase)) {
            Class<?> itemsClass = Servicios.getGenericType(type);
            Object o = null;
            try {
                o = itemsClass.getConstructor().newInstance();
            } catch (Exception e) {
                // No hacer nada, cualquier cosa PropiedadLista va a quejarse
            }

            if (Servicios.isSimpleType(itemsClass)) {
                if (((Collection) valorPorDefecto).isEmpty()) {
                    return new PropiedadListaString(o);
                } else {
                    return new PropiedadListaString(valorPorDefecto);
                }
            } else if (o == null) {
                return new PropiedadLista((Collection) valorPorDefecto,
                        itemsClass);
            } else {
                return new PropiedadLista((Collection) valorPorDefecto, o);
            }

        } else if (Map.class.isAssignableFrom(clase)) {
            Class<?> keyClass = Servicios.getGenericType(type, 0);
            Class<?> itemsClass = Servicios.getGenericType(type, 1);

            return new PropiedadMapa(keyClass, itemsClass);

        } else if (clase.isEnum()) {
            return new PropiedadEnum((Enum) valorPorDefecto);

        } else if (Class.class.isAssignableFrom(clase)) {
            return new PropiedadComboObjetos<Class<?>>(
                    (Class<?>) valorPorDefecto, getMapaClases(Servicios.
                    getGenericType(type)));

        } else {
            return new PropiedadObjeto(valorPorDefecto, clase);
        }
    }

    private static Map<String, Class<?>> getMapaClases(Type type) {
        Map<String, Class<?>> mapa = new HashMap<String, Class<?>>();

        for (Class<?> clase : Servicios.loadClasses(Servicios.getClassFromType(
                type))) {
            mapa.put(clase.getSimpleName(), clase);
        }

        return mapa;
    }

}
