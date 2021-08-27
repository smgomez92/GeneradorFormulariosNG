package com.fitbank.webpages;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fitbank.enums.TipoFila;
import com.fitbank.js.JS;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadSeparador;
import com.fitbank.propiedades.PropiedadSerializable;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.propiedades.anotaciones.UtilPropiedades;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.html.Tag;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.SerializableXml;
import com.fitbank.serializador.xml.UtilXML;
import com.fitbank.util.Editable;
import com.fitbank.util.Debug;
import com.fitbank.webpages.data.DataSource;

/**
 * Clase Widget, elemento de un formulario.
 * 
 * @author FitBank
 * @version 2.0
 */
public abstract class Widget extends WebElement<Widget> {

    private static final long serialVersionUID = 1L;

    @Editable
    private DataSource dataSource = new DataSource();

    /**
     * Constructor por defecto.
     */
    public Widget() {
        setTag("widget");

        def("tex", "");
    }

    @JS(ignore = true)
    public boolean getVisible() {
        return true;
    }

    @JS(ignore = true)
    public void setVisible(boolean visible) {
        if (properties.containsKey("vis")) {
            properties.get("vis").setValor(visible);
        } else {
            Debug.error("No se encontró la propiedad vis para el widget");
        }
    }

    protected boolean usesDimensions() {
        return false;
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////

    @Override
    public String getHTMLId() {
        String contexto = "c" + WebPageEnviroment.getContextId();

        if (getParentContainer() != null) {
            return contexto + '_' + getIdForHTMLId() + "_" + getParentContainer().
                    getIndiceClonacionActual();
        } else {
            return contexto + '_' + getIdForHTMLId() + "_0";
        }
    }

    protected String getIdForHTMLId() {
        return getId();
    }

    @Editable
    public String getTexto() {       
        return ((PropiedadSimple) properties.get("tex")).getValor();
    }

    public void setTexto(String texto) {      
        ((PropiedadSimple) properties.get("tex")).setValor(texto);
    }

    @Editable(ignore = true)
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////

    /**
     * Método que devuelve una lista de objetos Propiedad con las properties que
     * serán usadas solo en el editor de formularios.
     * 
     * @return (List<Propiedad>) Lista con las properties para uso dentro del
     *         Editor de Formularios.
     */
    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = toPropiedades(
                new PropiedadSeparador("Propiedades Generales"), "x", "y", "z",
                "w", "h", "cssClass");

        l.add(new PropiedadSeparador("Propiedades Extra"));
        l.addAll(UtilPropiedades.getPropiedades(this));

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////

    /**
     * Devuelve una lista con los nombre de los atributos del elemento.
     * 
     * @return (List<String>) Lista de strings con los atributos del elemento
     */
    @JS(ignore = true)
    protected abstract Collection<String> getAtributosElementos();

    /**
     * Metodo que extrae los nodos child de un objeto Base Formas.
     * 
     * @return (List<String>) Lista con los strings de los child nodes del
     *         objeto WebElement
     */
    @Override
    protected Collection<String> getHijosXml() {
        return null;
    }

    /**
     * Método que retorna los atributos de un elemento XML.
     * 
     * @return (List<String>) Lista con los strings de los atributos de un
     *         elemento XML
     */
    @Override
    protected final Collection<String> getAtributosXml() {
        List<String> l = Arrays.asList(new String[] { "x", "y", "z", "w", "h",
                    "cssClass", "tex" });

        return l;
    }

    /**
     * Método que retorna los atributos de un elemento XML.
     * 
     * @return (List<SerializableXml>) Lista con los objetos que se pueden
     *         serializar a XML.
     */
    @Override
    public Collection<SerializableXml> getChildren() {
        List<SerializableXml> l = new LinkedList<SerializableXml>();

        if (!dataSource.estaVacio()) {
            l.add(UtilXML.newInstance("dataSource", dataSource));
        }

        for (String s : getAtributosElementos()) {
            if (!properties.get(s).esValorPorDefecto()) {
                l.add(new PropiedadSerializable(s, properties.get(s)));
            }
        }

        return l;
    }

    /**
     * Método que devuelve el nodo DOM al que pertenece un elemento en el XML.
     * 
     * @param (Document) document La representación en objeto DOM de un
     *        documento.
     * @return (Node) Un nodo del DOM.
     */
    @Override
    public Node getNode(Document document) {
        Element element = (Element) super.getNode(document);

        element.setAttribute("class", getClass().getName());

        return element;
    }

    @Override
    public Object parsear(Node node, Type type) throws ExcepcionParser {
        return WebPageXml.parseWidget(node, type);
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////

    /**
     * Genera el HTML base del formulario. Se lo utiliza como punto de partida
     * para construir el HTML de un formulario.
     * 
     * @param html
     *            Constructor de html
     */
    public void generarHtmlBase(ConstructorHtml html) {
        if (!getVisible()) {
            return;
        }

        switch (getParentContainer().getType()) {
            case TABLA:
                html.abrir("td");
                if (getParentContainer().getHorizontal()) {
                    html.setEstilo("width", getParentContainer().getW(), "px", 0);
                    getParentContainer().generarEventosJavascript(html);
                }
                if (!getVisible()) {
                    html.setEstilo("display", "none");
                }
                html.setAtributo("colspan", getX(), 0);
                html.setAtributo("rowspan", getY(), 0);
                generarClasesCSS(html);
                break;

            case COLUMNAS:
                // No hacer nada, se hace en Container.generarHTMLColumnas
                break;

            default:
                html.abrir("div");

                generateCSSInlineStyles(html);
        }
    }
    /**
     * Genera el HTML base del formulario. Se lo utiliza como punto de partida
     * para construir el HTML de un formulario.
     * 
     * @param html
     *            Constructor de html
     */
    public void generarHtmlBaseNg(ConstructorHtml html) {
        if (!getVisible()) {
            return;
        }

        switch (getParentContainer().getType()) {
            case TABLA:
//                html.abrir("td");
//                if (getParentContainer().getHorizontal()) {
//                    html.setEstilo("width", getParentContainer().getW(), "px", 0);
//                    getParentContainer().generarEventosJavascript(html);
//                }
//                if (!getVisible()) {
//                    html.setEstilo("display", "none");
//                }
//                html.setAtributo("colspan", getX(), 0);
//                html.setAtributo("rowspan", getY(), 0);
//                generarClasesCSS(html);
                break;

            case COLUMNAS:
                // No hacer nada, se hace en Container.generarHTMLColumnas
                break;

            default:
                html.abrir("div");

                generateCSSInlineStyles(html);
        }
    }

    /**
     * Finaliza el HTML base del formulario. Se lo utiliza como punto final para
     * construir el HTML de un formulario y finalizarlo.
     * 
     * @param html
     *            Constructor de html
     */
    public void finalizarHtmlBase(ConstructorHtml html) {
        reemplazarTag(html.getTagActual());

        if (!getVisible()) {
            return;
        }

        switch (getParentContainer().getType()) {
            case TABLA:
                html.cerrar("td");
                break;

            case COLUMNAS:
                // No hacer nada, se hace en Container.generarHTMLColumnas
                break;

            default:
                html.cerrar("div");
        }
    }
    /**
     * Finaliza el HTML base del formulario. Se lo utiliza como punto final para
     * construir el HTML de un formulario y finalizarlo.
     * 
     * @param html
     *            Constructor de html
     */
    public void finalizarHtmlBaseNg(ConstructorHtml html) {
        reemplazarTagNg(html.getTagActual());

        if (!getVisible()) {
            return;
        }

        switch (getParentContainer().getType()) {
            case TABLA:
//                html.cerrar("td");
                break;

            case COLUMNAS:
                // No hacer nada, se hace en Container.generarHTMLColumnas
                break;

            default:
                html.cerrar("div");
        }
    }

    protected void generarTabIndex(ConstructorHtml html, boolean modificable) {
        if (!getVisible()) {
            return;
        }

        WebPage webPage = getParentWebPage();

        int tabIndex = 0;

        if (!webPage.getTabOrder().isEmpty()) {
            tabIndex = webPage.getTabOrder().indexOf(getHTMLId()) + 1;
        }

        html.setAtributo("tabindex-original", tabIndex);

        if (modificable) {
            html.setAtributo("tabindex", tabIndex);
        } else {
            html.setAtributo("tabindex", -1);
        }

        if (StringUtils.isBlank(WebPageEnviroment.getFirstFocus())) {
            WebPageEnviroment.setFirstFocus(getHTMLId());
        }
    }

    /**
     * Genera los eventos de javascript para presentar una guia.
     * 
     * @param html
     *            Constructor de html
     */
    protected void generarHtmlGuia(ConstructorHtml html, String guia) {
        if (StringUtils.isNotBlank(guia)) {
            guia = guia.length() > 0 && guia.substring(0, 1).equals("*") ? guia.
                    substring(1) : "'" + guia + "'";
            html.extenderAtributo("onmouseover", "window.status=title=" + guia
                    + ";");
            html.extenderAtributo("onmouseout", "window.status='';");
        }
    }

    /**
     * Reemplaza el tag en el que se encuentre con el tag pasado como parametro.
     * 
     * @param (Tag) tag El tag que se quiere reemplazar.
     */
    public void reemplazarTag(Tag tag) {
        for (String nombre : tag.getAtributos().keySet()) {
            tag.getAtributos().put(nombre,
                    reemplazarValor(tag.getAtributos().get(nombre)));
        }

        if (tag.getValor() != null) {
            tag.setValor(reemplazarValor(tag.getValor()));
        }

        for (Tag t : tag.getHijos()) {
            reemplazarTag(t);
        }
    }
    /**
     * Reemplaza el tag en el que se encuentre con el tag pasado como parametro.
     * 
     * @param (Tag) tag El tag que se quiere reemplazar.
     */
    public void reemplazarTagNg(Tag tag) {
        for (String nombre : tag.getAtributos().keySet()) {
            tag.getAtributos().put(nombre,tag.getAtributos().get(nombre)!=null?
                    reemplazarValor(tag.getAtributos().get(nombre)):"");
        }

        if (tag.getValor() != null) {
            tag.setValor(reemplazarValor(tag.getValor()));
        }

        for (Tag t : tag.getHijos()) {
            reemplazarTagNg(t);
        }
    }

    /**
     * Reemplaza el valor de un elemento con el especificado en el parametro
     * pasado.
     * 
     * @param (String) valor String con el valor que reemplazara al
     *        especificado.
     * @return (String) valor Devuelve el string con el nuevo valor
     */
    public String reemplazarValor(String valor) {
        valor = valor.replace("$D", String.valueOf(getIndiceClonacion()));
        valor = valor.replace("$d", String.valueOf(getIndiceClonacion()));

        return valor;
    }

    /**
     * Devuelve un StringBuffer que contiene los estilos CSS del elemento.
     * 
     * @param xhtml
     *            Donde se van a escribir los estilos
     * 
     * @return (StringBuffer) estilos StringBuffer con los estilos CSS del
     *         elemento.
     */
    private void generateCSSInlineStyles(ConstructorHtml xhtml) {
        xhtml.setEstilo("left", getX(), "px");
        xhtml.setEstilo("top", getY(), "px");

        if (usesDimensions()) {
            xhtml.setEstilo("width", getW(), "px", 0);
            xhtml.setEstilo("height", getH(), "px", 0);
        }
    }

    public boolean esActivoFilaActual() {
        if (getIndiceClonacion() == 0) {
            return true;
        } else if (getParentContainer().getType() == TipoFila.TABLA) {
            int pos = getPosicion();
            return pos > getParentContainer().indexOfHeaderSeparator()
                    && pos < getParentContainer().indexOfFooterSeparator();
        } else {
            return true;
        }
    }

    @JS(ignore = true)
    public int getRegistrosConsulta() {
        Container container = getParentContainer();

        if (container == null) {
            return -1;
        } else if (container.getType() == TipoFila.TABLA) {
            int pos = getPosicion();
            if (pos <= container.indexOfHeaderSeparator()
                    || pos >= container.indexOfFooterSeparator()) {
                return 1;
            }
        }

        return container.getNumeroFilasClonadasConsulta();
    }

    @JS(ignore = true)
    public int getRegistrosMantenimiento() {
        Container container = getParentContainer();

        if (container == null) {
            return -1;
        } else if (container.getType() == TipoFila.TABLA) {
            int pos = getPosicion();
            if (pos <= container.indexOfHeaderSeparator()
                    || pos >= container.indexOfFooterSeparator()) {
                return 1;
            }
        }

        return container.getNumeroFilasClonadasMantenimiento();
    }

}