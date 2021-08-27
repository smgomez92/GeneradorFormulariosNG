package com.fitbank.serializador.xml;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConvertUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import lombok.extern.slf4j.Slf4j;
import com.fitbank.util.Servicios;
import java.util.LinkedHashSet;

@Slf4j
public final class ParserXml {

    private ParserXml() {
    }

    public static <U> U parse(InputStream is, Class<U> clase)
            throws ExcepcionParser {
        return parse(ParserGeneral.parse(is).getDocumentElement(), clase);
    }

    @SuppressWarnings("unchecked")
    public static <U> U parse(Node node, Class<U> clase) throws ExcepcionParser {
        return (U) parse(node, (Type) clase);
    }

    public static Object parse(Node node, Type clase) throws ExcepcionParser {
        // Obtener clase
        String className = getAtributo(node, "class");
        if (className != null) {
            try {
                clase = Class.forName(className);
            } catch (ClassNotFoundException e) {
                log.warn("", e);
            }
        }

        if (clase == null) {
            throw new ExcepcionParser("Clase " + className
                    + " no se pudo obtener");
        }

        return parseConClase(node, clase);
    }

    @SuppressWarnings("unchecked")
    private static <U> U parseConClase(Node node, Type type)
            throws ExcepcionParser {
        // Obtener instancia
        SerializableXml<U> s;
        U u;
        Class<?> clase = Servicios.getClassFromType(type);
        try {
            if (Set.class.isAssignableFrom(clase)
                    && clase.getPackage().getName().startsWith("java")) {
                u = (U) new LinkedHashSet();
            } else if (Iterable.class.isAssignableFrom(clase)
                    && clase.getPackage().getName().startsWith("java")) {
                u = (U) new LinkedList();
            } else if (Map.class.isAssignableFrom(clase)
                    && clase.getPackage().getName().startsWith("java")) {
                u = (U) new HashMap();
            } else {
                u = (U) clase.getConstructor().newInstance();
            }
        } catch (Exception e) {
            u = (U) ConvertUtils.convert("", clase);
        }

        if (u instanceof SerializableXml) {
            s = (SerializableXml) u;
        } else {
            s = UtilXML.newInstance(node.getNodeName(), u);
        }

        return s.parsear(node, type);
    }

    /**
     * Obtiene un atributo
     *
     * @param node
     * @param nombre
     * @return El tipo de un elemento
     */
    private static String getAtributo(Node node, String nombre) {
        NamedNodeMap attributeNodes = node.getAttributes();

        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node item = attributeNodes.item(i);
                if (item.getNodeName().equalsIgnoreCase(nombre)) {
                    return item.getFirstChild().getNodeValue();
                }
            }
        }

        return null;
    }

}
