package com.fitbank.serializador.xml;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import com.fitbank.util.Servicios;

public final class UtilXML {

    private UtilXML() {
    }

    @XML
    @SuppressWarnings("unused")
    private final static Object def = null;

    private final static XML defaultXML;

    static {
        try {
            defaultXML = UtilXML.class.getDeclaredField("def").getAnnotation(
                    XML.class);
        } catch (NoSuchFieldException e) {
            throw new Error(e);
        } catch (SecurityException e) {
            throw new Error(e);
        }
    }

    public static XML getXml(final String nombre) {
        return getXml(nombre, null);
    }

    public static XML getXml(final String nombre, final String nombreSubitems) {
        return new XML() {

            public Class<? extends Annotation> annotationType() {
                return defaultXML.annotationType();
            }

            public boolean usarTag() {
                return defaultXML.usarTag();
            }

            public String nombreSubitems() {
                return nombreSubitems == null ? defaultXML.nombreSubitems()
                        : nombreSubitems;
            }

            public String nombre() {
                return nombre == null ? defaultXML.nombre() : nombre;
            }

            public boolean ignore() {
                return defaultXML.ignore();
            }

        };
    }

    public static <T> SerializableXml<T> newInstance(String nombre, T value) {
        if (value == null) {
            return new SerializableXmlNull();
        }

        return newInstance(getXml(nombre), value);
    }

    public static <T> SerializableXml<T> newInstance(XML xml, T value) {
        if (value == null) {
            return new SerializableXmlNull();
        }

        return newInstance(xml, value.getClass(), value);
    }

    @SuppressWarnings("unchecked")
    public static <T> SerializableXml<T> newInstance(XML xml, Type type, T value) {
        Class<?> clase = Servicios.getClassFromType(type);

        if (value instanceof SerializableXml<?>) {
            return (SerializableXml<T>) value;
        } else if (Enum.class.isAssignableFrom(clase)) {
            return new SerializableXmlEnum(xml, (Enum) value);
        } else if (Servicios.isSimpleType(type)) {
            return new SerializableXmlSimple(xml, value == null ? null : String.
                    valueOf(value));
        } else if (Map.class.isAssignableFrom(clase)) {
            return new SerializableXmlMap(xml, (Map) value);
        } else if (Collection.class.isAssignableFrom(clase)) {
            return new SerializableXmlCollection(xml, (Collection) value);
        } else if (value == null) {
            return new SerializableXmlNull();
        } else {
            return new SerializableXmlBean(xml, value);
        }
    }

}
