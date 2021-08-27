package com.fitbank.serializador.xml;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.slf4j.Slf4j;
import com.fitbank.util.Servicios;

/**
 * Clase que serializa cualquier bean.
 *
 * @author FitBank CI
 */
@Slf4j
public class SerializableXmlBean<T> implements SerializableXml<T> {

    private static final long serialVersionUID = 1L;

    private final static Map<Class<?>, Map<String, String>> CHILDREN =
            new HashMap<Class<?>, Map<String, String>>();

    private final static Map<Class<?>, Map<String, String>> ATTRIBUTES =
            new HashMap<Class<?>, Map<String, String>>();

    private final T o;

    private final WrapDynaBean bean;

    private final DynaBean beanDefault;

    private Map<String, String> children;

    private Map<String, String> attributes;

    private final XML xml;

    protected SerializableXmlBean(XML xml, T o) {
        this.xml = xml;
        this.o = o;
        this.bean = new WrapDynaBean(o);

        try {
            this.beanDefault = this.bean.getDynaClass().newInstance();
        } catch (Exception e) {
            throw new Error(e);
        }

        readProperties(o);
    }

    private synchronized void readProperties(T o) throws Error {
        children = CHILDREN.get(o.getClass());
        attributes = ATTRIBUTES.get(o.getClass());

        if (children != null && attributes != null) {
            return;
        }

        children = new HashMap<String, String>();
        CHILDREN.put(o.getClass(), children);

        attributes = new HashMap<String, String>();
        ATTRIBUTES.put(o.getClass(), attributes);

        for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(o)) {
            String propiedad = pd.getName();

            if ("class".equals(propiedad)) {
                continue;
            }

            XML xmlPropiedad = null;
            Method m = pd.getReadMethod();
            if (m != null) {
                xmlPropiedad = m.getAnnotation(XML.class);
            }

            boolean isTransient = false;
            if (xmlPropiedad == null) {
                try {
                    Field field = Servicios.getField(o.getClass(), propiedad);
                    xmlPropiedad = field.getAnnotation(XML.class);
                    isTransient = Modifier.isTransient(field.getModifiers());
                } catch (NoSuchFieldException e) {
                }
            }

            if (xmlPropiedad == null) {
                xmlPropiedad = UtilXML.getXml(propiedad);
            }

            if (xmlPropiedad.ignore() || isTransient) {
                // No hacer nada
            } else if (xmlPropiedad.usarTag()) {
                children.put(StringUtils.defaultIfEmpty(xmlPropiedad.nombre(),
                        propiedad), propiedad);
            } else {
                Class<?> type = beanDefault.getDynaClass().getDynaProperty(
                        propiedad).getType();

                String nombre = StringUtils.defaultIfEmpty(xmlPropiedad.nombre(),
                        propiedad);

                if (Servicios.isSimpleType(type)) {
                    attributes.put(nombre, propiedad);
                } else {
                    children.put(nombre, propiedad);
                }
            }
        }
    }

    private boolean puedeEscribir(String propiedad) {
        if (propiedad == null) {
            return false;
        }

        try {
            PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                    o, propiedad);
            return descriptor != null && descriptor.getWriteMethod() != null;
        } catch (IllegalAccessException e) {
            throw new Error(e);
        } catch (InvocationTargetException e) {
            throw new Error(e);
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }

    private boolean puedeLeer(String propiedad) {
        try {
            PropertyDescriptor descriptor = PropertyUtils.getPropertyDescriptor(
                    o, propiedad);
            return descriptor != null && descriptor.getReadMethod() != null;
        } catch (IllegalAccessException e) {
            throw new Error(e);
        } catch (InvocationTargetException e) {
            throw new Error(e);
        } catch (NoSuchMethodException e) {
            throw new Error(e);
        }
    }

    @Override
    public Collection<SerializableXml<?>> getChildren() {
        List<SerializableXml<?>> serializables =
                new LinkedList<SerializableXml<?>>();

        for (Entry<String, String> entry : children.entrySet()) {
            String propiedad = entry.getValue();
            if (puedeLeer(propiedad)) {
                Object value = bean.get(propiedad);
                Object defaultValue = beanDefault.get(propiedad);

                if (value == null) {
                    serializables.add(new SerializableXmlSimple<Object>(entry.
                            getKey(), null));
                } else if (!value.equals(defaultValue)) {
                    SerializableXml<?> serializable;
                    serializable = UtilXML.newInstance(UtilXML.getXml(entry.
                            getKey()), value);
                    serializables.add(serializable);
                }
            }
        }

        return serializables;
    }

    @Override
    public Node getNode(Document document) {
        Element elemento = document.createElement(xml.nombre());

        elemento.setAttribute("class", bean.getDynaClass().getName());

        for (Entry<String, String> entry : attributes.entrySet()) {
            String propiedad = entry.getValue();
            if (puedeLeer(propiedad)) {
                Object value = bean.get(propiedad);
                Object defaultValue = beanDefault.get(propiedad);

                if (value == null && defaultValue != null) {
                    elemento.setAttribute(entry.getKey(), "null");
                } else if (value != null && !value.equals(defaultValue)) {
                    if (value instanceof Enum) {
                        elemento.setAttribute(entry.getKey(), ((Enum) value).
                                name());
                    } else {
                        elemento.setAttribute(entry.getKey(), String.valueOf(
                                value));
                    }
                }
            }
        }

        return elemento;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T parsear(Node node, Type type) throws ExcepcionParser {
        processAttributes(node);

        processNodes(node);

        return o;
    }

    private void processAttributes(Node node) throws DOMException {
        // Procesar atributos
        NamedNodeMap attributeNodes = node.getAttributes();

        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node item = attributeNodes.item(i);
                String propiedad = getFieldName(item.getNodeName());

                if (item.getNodeName().equals("class")) {
                    continue;
                }

                if (propiedad == null) {
                    log.error("No existe propiedad para el xml "
                            + node.getNodeName() + " > " + item.getNodeName()
                            + " (" + propiedad + ")");
                    continue;
                }

                Class typePropiedad = bean.getDynaClass().getDynaProperty(
                        propiedad).getType();

                if (puedeEscribir(propiedad)) {
                    if (item.getNodeValue().equals("null")) {
                        bean.set(propiedad, null);
                    } else if (Enum.class.isAssignableFrom(typePropiedad)) {
                        // FIXME: tomar en cuenta BasicEnums tambien!!!
                        bean.set(propiedad, Enum.valueOf(typePropiedad, item.
                                getNodeValue()));
                    } else {
                        Object object = ConvertUtils.convert(item.getNodeValue(),
                                typePropiedad);
                        bean.set(propiedad, object);
                    }
                }
            }
        }
    }

    private void processNodes(Node node) throws ExcepcionParser {
        // Procesar nodos
        NodeList childNodes = node.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);

                if (item.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                String propiedad = getFieldName(item.getNodeName());

                if (propiedad == null) {
                    log.warn("No se pudo leer la propiedad de " + item.
                            getNodeName() + " para la clase "
                            + o.getClass().getName());
                    continue;
                }

                if (bean.get(propiedad) instanceof Map) {
                    Type itemClass = getType(propiedad);
                    Map object = (Map) ParserXml.parse(item, itemClass);
                    for (Object obj : object.keySet()) {
                        ((Map) bean.get(propiedad)).put(obj, object.get(obj));
                    }
                } else if (!item.hasChildNodes() && !item.hasAttributes()
                        && puedeEscribir(propiedad)) {
                    bean.set(propiedad, null);
                } else if (puedeEscribir(propiedad)) {
                    Type itemClass = getType(propiedad);
                    Object object = ParserXml.parse(item, itemClass);
                    bean.set(propiedad, object);
                } else if (bean.get(propiedad) instanceof Collection) {
                    Type itemClass = getType(propiedad);
                    Collection object = (Collection) ParserXml.parse(item,
                            itemClass);
                    ((Collection) bean.get(propiedad)).addAll(object);
                }
            }
        }
    }

    private Type getType(String propiedad) {
        return Servicios.getType(o, propiedad);
    }

    @Override
    public void setValorXml(String tag, Object valor) throws ExcepcionParser {
        // Método deprecado
    }

    private String getFieldName(String tag) {
        return StringUtils.defaultIfEmpty(children.get(tag), attributes.get(tag));
    }

}
