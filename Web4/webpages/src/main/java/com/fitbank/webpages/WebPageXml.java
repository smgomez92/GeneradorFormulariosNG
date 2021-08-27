package com.fitbank.webpages;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.ParserGeneral;
import com.fitbank.serializador.xml.ParserXml;
import com.fitbank.util.Debug;
import com.fitbank.webpages.assistants.Password;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.updates.WebPageVersionUpdater;
import java.util.Arrays;
import org.apache.commons.collections.CollectionUtils;

/**
 * Clase ParserFormulario, Genera el formulario desde String con el XML, desde
 * un archivo XML y desde un Documento.
 * 
 * @author FitBank 2.0 JT
 */
public class WebPageXml {

    /**
     * Generar un WebPage desde un String que contenga el XML.
     * 
     * @param contenido
     *            String con XML
     * 
     * @return WebPage Generado
     * 
     * @throws ExcepcionParser
     *             Excepción Genérica
     */
    public static WebPage parseString(String contenido) throws ExcepcionParser {
        return parse(ParserGeneral.parseStringDoc(contenido));
    }

    /**
     * Generar un WebPage desde un String que contenga el XML.
     * 
     * @param contenido
     *            String con XML
     * 
     * @return WebPage Generado
     * 
     * @throws ExcepcionParser
     *             Excepción Genérica
     */
    public static WebPage parse(InputStream contenido) throws ExcepcionParser {
        return parse(ParserGeneral.parse(contenido));
    }

    /**
     * Generar un WebPage desde un archivo XML.
     * 
     * @param uri
     *            Nombre del archivo
     * 
     * @return WebPage generado
     * @throws ExcepcionParser
     *             En caso de no poder parsear.
     * @throws FileNotFoundException
     *             En caso de que no exista el archivo.
     * 
     * @throws ExcepcionParser
     *             Excepción Genérica
     */
    public static WebPage parse(String uri) throws ExcepcionParser,
            FileNotFoundException {
        return parse(ParserGeneral.parseDoc(uri));
    }

    /**
     * Generar un WebPage desde un Document.
     * 
     * @param doc
     *            Documento a ser parseado
     * 
     * @return WebPage Generado
     * 
     * @throws ExcepcionParser
     *             en caso de que el documento no sea un documento valido
     */
    public static WebPage parse(Document doc) throws ExcepcionParser {
        Element element = doc == null ? null : doc.getDocumentElement();

        if (element == null) {
            throw new ExcepcionParser(
                    "Error el archivo no es un xml valido o está vacio");
        }

        doc = WebPageVersionUpdater.transformBase(doc);

        int version = Integer.parseInt(StringUtils.defaultIfEmpty(element.
                getAttribute("version"), "0"));

        //Version 7 es la unica compatible con webpages version 6
        if (version > WebPage.VERSION 
                && Arrays.asList(WebPage.ALLOWED_VERSIONS).contains(version)) {
            throw new ExcepcionParser(
                    "WebPage con versión superior a los fuentes.");
        }

        for (int i = version + 1; i <= WebPage.VERSION; i++) {
            Debug.info("Actualizando WebPage a version " + i);
            doc = WebPageVersionUpdater.transform(doc, i);
        }

        return parseWebPage(doc.getDocumentElement());
    }

    /**
     * Genera un WebPage a partir de un nodo padre.
     * 
     * @param node
     * @return WebPage generado
     * @throws ExcepcionParser
     */
    private static WebPage parseWebPage(Element node)
            throws ExcepcionParser {
        if (!node.getNodeName().equals("webPage")) {
            throw new ExcepcionParser("El archivo no es un WebPage");
        }

        WebPage webPage = new WebPage();

        NodeList children = node.getChildNodes();

        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    generarObjeto(children.item(i), webPage);
                }
            }
        }

        return webPage;
    }

    private static WebPage parseWebPage(Node node, WebPage webPage)
            throws ExcepcionParser {
        NodeList children = node.getChildNodes();

        if (children != null) {
            for (int i = 0; i < children.getLength(); i++) {
                if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    parseContainer(children.item(i), webPage);
                }
            }
        }

        return webPage;
    }

    /**
     * Genera el encabezado de un formulario.
     * 
     * @param node
     * @param webPage
     * @return Encabezado del formulario generado
     * @throws ExcepcionParser
     */
    private static WebPage parseWebPageHeader(Node node, WebPage webPage)
            throws ExcepcionParser {
        NamedNodeMap attrs = node.getAttributes();
        NodeList nodos = node.getChildNodes();

        for (int i = 0; i < attrs.getLength(); i++) {
            Node nodo = attrs.item(i);
            if (nodo.getNodeType() != Node.TEXT_NODE) {
                webPage.setValorXml(nodo.getNodeName(), nodo.getNodeValue());
            }
        }

        for (int i = 0; i < nodos.getLength(); i++) {
            Node item = nodos.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE
                    && item.getFirstChild() != null) {
                if (item.getNodeName().equals("references")) {
                    parseReferences(item, webPage);
                } else if (item.getNodeName().equals("attached")) {
                    parseAttachedWebPages(item, webPage);
                } else {
                    webPage.setValorXml(item.getNodeName(), item.getFirstChild().
                            getNodeValue());
                }
            }
        }

        return webPage;
    }

    /**
     * Genera un objeto de un formulario FIT. Pueden ser PRO, FOR.
     * 
     * @param node
     * @param webPage
     * @throws ExcepcionParser
     */
    private static void generarObjeto(Node node, WebPage webPage)
            throws ExcepcionParser {
        if (node.getNodeName().equalsIgnoreCase("properties")) {
            parseWebPageHeader(node, webPage);
        }

        if (node.getNodeName().equalsIgnoreCase("containers")) {
            parseWebPage(node, webPage);
        }
    }

    /**
     * Genera una fila de un formulario.
     * 
     * @param node
     * @return
     * @throws ExcepcionParser
     */
    private static Container parseContainer(Node node, WebPage webPage) throws
            ExcepcionParser {
        Container container = new Container();
        webPage.add(container);

        NamedNodeMap attrs = node.getAttributes();
        NodeList children = node.getChildNodes();

        for (int i = 0; i < attrs.getLength(); i++) {
            Node nodo = attrs.item(i);
            if (nodo.getNodeType() != Node.TEXT_NODE) {
                container.setValorXml(nodo.getNodeName(), nodo.getNodeValue());
            }
        }

        Collection<Widget> items = new ArrayList<Widget>(children.getLength());
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                items.add(parseWidget(children.item(i)));
            }
        }

        container.addAll(items);

        return container;
    }

    /**
     * Genera una referencia para un formulario.
     * 
     * @param node
     * @param webPage
     * @throws ExcepcionParser
     */
    @SuppressWarnings("unchecked")
    private static void parseReferences(Node node, WebPage webPage)
            throws ExcepcionParser {
        Collection<Reference> references = ParserXml.parse(node,
                LinkedList.class);

        webPage.getReferences().addAll(references);
    }

    /**
     * Genera una referencia para un formulario.
     * 
     * @param node
     * @param webPage
     * @throws ExcepcionParser
     */
    @SuppressWarnings("unchecked")
    private static void parseAttachedWebPages(Node node, WebPage webPage)
            throws ExcepcionParser {
        Collection<AttachedWebPage> attached = ParserXml.parse(node,
                LinkedList.class);

        for (AttachedWebPage attachedWebPage : attached) {
            webPage.getAttached().add(attachedWebPage);
        }
    }

    /**
     * Genera un elemento en el nodo seleccionado del formulario y lo agraga a
     * la fila.
     * 
     * @param node
     * @param container
     *
     * @throws ExcepcionParser
     */
    @SuppressWarnings("unchecked")
    private static Widget parseWidget(Node node) throws ExcepcionParser {
        String tipo = getClassName(node.getAttributes());
        String tipoAux = "";

        if (tipo.equals("Password")) {
            tipoAux = tipo;
            tipo = "com.fitbank.webpages.widgets.Input";
        }

        Class clase;
        try {
            clase = Class.forName(tipo, true,
                    Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new ExcepcionParser("No existe el tipo de elemento " + tipo,
                    e);
        }

        Widget widget = parseWidget(node, clase);

        // FIXME hacer esto en el xslt
        if (widget instanceof FormElement && tipoAux.equals("Password")) {
            ((FormElement) widget).setAssistant(new Password());
        }

        return widget;
    }

    /**
     * Genera un elemento en el nodo seleccionado.
     *
     * @param node
     * @param type
     * 
     * @return Widget
     * 
     * @throws ExcepcionParser
     */
    @SuppressWarnings("unchecked")
    public static Widget parseWidget(Node node, Type type) throws
            ExcepcionParser {
        NamedNodeMap attrs = node.getAttributes();
        NodeList nodos = node.getChildNodes();
        Widget widget;
        Class clase = null;

        if (type instanceof Class) {
            clase = (Class) type;
        } else {
            throw new ExcepcionParser("No se pudo crear elemento del tipo "
                    + type);
        }

        try {
            widget = (Widget) clase.newInstance();
        } catch (InstantiationException e) {
            throw new ExcepcionParser("No se pudo crear elemento de la clase "
                    + clase.getName(), e);
        } catch (IllegalAccessException e) {
            throw new ExcepcionParser("No se pudo crear elemento de la clase "
                    + clase.getName(), e);
        }

        for (int i = 0; i < attrs.getLength(); i++) {
            Node nodo = attrs.item(i);
            if (nodo.getNodeType() != Node.TEXT_NODE) {
                widget.setValorXml(nodo.getNodeName(), nodo.getNodeValue());
            }
        }

        for (int i = 0; i < nodos.getLength(); i++) {
            Node nodo = nodos.item(i);

            try {
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    if (nodo.getNodeName().equals("attribute")) {
                        NamedNodeMap attrNodo = nodo.getAttributes();
                        Node nom = attrNodo.getNamedItem("nom");
                        Node val = nodo.getFirstChild();

                        if (nom == null || val == null) {
                            Debug.warn("No se pudo leer un tag del xml");
                        } else {
                            widget.setValorXml(nom.getNodeValue(),
                                    val.getNodeValue());
                        }
                    } else if (nodo.getNodeName().equals("dataSource")) {
                        widget.setDataSource(parseDataSource(nodo));
                    } else if (nodo.getNodeName().equals("assistant")) {
                        ((FormElement) widget).setAssistant(ParserXml.parse(nodo,
                                Assistant.class));
                    } else if (nodo.getNodeName().equals("formatters")) {
                        ((FormElement) widget).getBehaviors().addAll(
                                ParserXml.parse(nodo, LinkedList.class));
                    } else if (nodo.getNodeName().equals("behaviors")) {
                        ((FormElement) widget).getBehaviors().addAll(
                                ParserXml.parse(nodo, LinkedList.class));
                    } else {
                        DynaBean db = new WrapDynaBean(widget);
                        DynaProperty dp = db.getDynaClass().getDynaProperty(nodo.
                                getNodeName());
                        if (dp != null) {
                            widget.setValorXml(nodo.getNodeName(),
                                    ParserXml.parse(nodo, dp.getType()));
                        }
                    }
                }
            } catch (ExcepcionParser ep) {
                //Assistant, o Behavior no encontrado (nuevas versiones)
                //En tal caso, sacarlo del nodo y reprocesar
                if (ep.getMessage().contains("no se pudo obtener")) {
                    Debug.warn(ep.getMessage());

                    NodeList childNodes = nodo.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node item = childNodes.item(j);

                        if (item.getNodeType() == Node.ELEMENT_NODE) {
                            String itemCN = getClassName(item.getAttributes());

                            if (ep.getMessage().contains(itemCN)) {
                                nodo.removeChild(item);
                                return parseWidget(node, type);
                            }
                        }
                    }
                }
            }
        }

        return widget;
    }

    /**
     * Genera un Datasource en un formulario FIT.
     * 
     * @param node
     * @return Datasource generado
     * @throws ExcepcionParser
     */
    private static DataSource parseDataSource(Node node) throws ExcepcionParser {
        return ParserXml.parse(node, DataSource.class);
    }

    /**
     * Devuelve el tipo de elemento.
     * 
     * @param children
     * @return El tipo de un elemento
     */
    private static String getClassName(NamedNodeMap children) {
        return children.getNamedItem("class").getNodeValue();
    }

}