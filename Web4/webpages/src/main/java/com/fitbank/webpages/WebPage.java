package com.fitbank.webpages;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fitbank.enums.ControlCambio;
import com.fitbank.enums.EjecutadoPor;
import com.fitbank.enums.Paginacion;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadEnum;
import com.fitbank.propiedades.PropiedadEstilos;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadJavascript.Tipo;
import com.fitbank.propiedades.PropiedadLista;
import com.fitbank.propiedades.PropiedadListaString;
import com.fitbank.propiedades.PropiedadSeparador;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.SerializableXml;
import com.fitbank.serializador.xml.UtilXML;
import com.fitbank.serializador.xml.XML;
import com.fitbank.webpages.data.Reference;
import org.apache.commons.lang.StringUtils;

/**
 * Clase WebPage. Representacion de u formulario como objeto.
 *
 * @author FitBank
 * @version 2.0
 */
public class WebPage extends WebElement<Container> {

    public static final int VERSION = 6;

    public static final int[] ALLOWED_VERSIONS = {7};

    public WebPage() {
        setTag("webPage");

        def("version", VERSION);
        def("title", "");
        def("lang", "ES");
        def("sub", "");
        def("tra", "");
        def("eje", EjecutadoPor.FORMULARIO);
        def("tmo", 0);
        def("cssClass", new PropiedadEstilos());
        def("initialJS", new PropiedadJavascript(Tipo.FUNCIONES));
        def("pmr", Paginacion.HABILITADA);
        def("cma", ControlCambio.CAMBIO_AUTOMATICO);
        def("store", true);
        def("ngOninit", false);
        def("postQuery", false);
        def("requiresQuery", false);
        def("clean", false);
        def("legacy", false);
        def("joinQuirk", false);
        def("tabOrder", new PropiedadListaString(""));
        def("firstFocus", "");
        def("queryFocus", "");
        def("references", new PropiedadLista<Reference>(0, new Reference()));
        def("attached", new PropiedadLista<AttachedWebPage>(0, new AttachedWebPage()));
        def("cal", new PropiedadJavascript(Tipo.FUNCIONES));
        def("imports", new PropiedadJavascript(Tipo.FUNCIONES));
        def("exports", new PropiedadJavascript(Tipo.FUNCIONES));
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Getters y setters">
    @Override
    public String getHTMLId() {
        return getId();
    }

    public int getVersion() {
        return (Integer) properties.get("version").getValor();
    }

    public void setVersion(int version) {
        properties.get("version").setValor(version);
    }

    public String getTitle() {
        return properties.get("title").getValorString();
    }

    public void setTitle(String tit) {
        properties.get("title").setValor(tit);
    }

    public String getInitialJS() {
        return properties.get("initialJS").getValorString();
    }

    public void setInitialJS(String initialJS) {
        properties.get("initialJS").setValorString(initialJS);
    }

    public boolean getStore() {
        return ((PropiedadBooleana) properties.get("store")).getValor();
    }

    public void setStore(boolean store) {
        properties.get("store").setValor(store);
    }

    public boolean getNgOninit() {
        return ((PropiedadBooleana) properties.get("ngOninit")).getValor();
    }

    public void setNgOninit(boolean ngOninit) {
        properties.get("ngOninit").setValor(ngOninit);
    }
//ngOninit

    public boolean getPostQuery() {
        return ((PropiedadBooleana) properties.get("postQuery")).getValor();
    }

    public void setPostQuery(boolean postQuery) {
        properties.get("postQuery").setValor(postQuery);
    }

    public boolean getRequiresQuery() {
        return ((PropiedadBooleana) properties.get("requiresQuery")).getValor();
    }

    public void setRequiresQuery(boolean requiresQuery) {
        properties.get("requiresQuery").setValor(requiresQuery);
    }

    public boolean getClean() {
        return ((PropiedadBooleana) properties.get("clean")).getValor();
    }

    public void setClean(boolean clean) {
        properties.get("clean").setValor(clean);
    }

    public boolean getLegacy() {
        return ((PropiedadBooleana) properties.get("legacy")).getValor();
    }

    public void setLegacy(boolean legacy) {
        properties.get("legacy").setValor(legacy);
    }

    public boolean getJoinQuirk() {
        return ((PropiedadBooleana) properties.get("joinQuirk")).getValor();
    }

    public void setJoinQuirk(boolean joinQuirk) {
        properties.get("joinQuirk").setValor(joinQuirk);
    }

    public String getSubsystem() {
        return ((PropiedadSimple) properties.get("sub")).getValor();
    }

    public void setSubsystem(String subSystem) {
        properties.get("sub").setValor(subSystem);
    }

    public String getTransaction() {
        return ((PropiedadSimple) properties.get("tra")).getValor();
    }

    public void setTransaction(String tran) {
        properties.get("tra").setValor(tran);
    }

    public EjecutadoPor getEjecutadoPor() {
        return ((PropiedadEnum<EjecutadoPor>) properties.get("eje")).getValor();
    }

    public void setEjecutadoPor(EjecutadoPor pag) {
        properties.get("eje").setValor(pag);
    }

    public Integer getTimeout() {
        return (Integer) properties.get("tmo").getValor();
    }

    public void setTimeout(Integer timeout) {
        properties.get("tmo").setValor(timeout);
    }

    public String getLanguage() {
        return ((PropiedadSimple) properties.get("lang")).getValor();
    }

    public void setLanguage(String language) {
        properties.get("lang").setValor(language);
    }

    public Paginacion getPaginacion() {
        return ((PropiedadEnum<Paginacion>) properties.get("pmr")).getValor();
    }

    public void setPaginacion(Paginacion pag) {
        properties.get("pmr").setValor(pag);
    }

    public ControlCambio getControlDeCambio() {
        return ((PropiedadEnum<ControlCambio>) properties.get("cma")).getValor();
    }

    public void setControlDeCambio(ControlCambio conCam) {
        properties.get("cma").setValor(conCam);
    }

    public void setCalculos(String calculos) {
        properties.get("cal").setValorString(calculos);
    }

    public String getCalculos() {
        return ((PropiedadJavascript) properties.get("cal")).getValorString();
    }

    public void setImports(String imports) {
        properties.get("imports").setValorString(imports);
    }

    public String getImports() {
        return ((PropiedadJavascript) properties.get("imports")).getValorString();
    }

    public void setExports(String exports) {
        properties.get("exports").setValorString(exports);
    }

    public String getExports() {
        return ((PropiedadJavascript) properties.get("exports")).getValorString();
    }

    public List<String> getTabOrder() {
        return ((PropiedadListaString) properties.get("tabOrder")).getList();
    }

    public String getFirstFocus() {
        return ((PropiedadSimple) properties.get("firstFocus")).getValor();
    }

    public void setFirstFocus(String firstFocus) {
        properties.get("firstFocus").setValor(firstFocus);
    }

    public String getQueryFocus() {
        return ((PropiedadSimple) properties.get("queryFocus")).getValor();
    }

    public void setQueryFocus(String queryFocus) {
        properties.get("queryFocus").setValor(queryFocus);
    }

    public Collection<AttachedWebPage> getAttached() {
        return ((PropiedadLista<AttachedWebPage>) properties.get("attached")).
                getList();
    }

    public Collection<Reference> getReferences() {
        return ((PropiedadLista<Reference>) properties.get("references")).
                getList();
    }

    public String getURI() {
        return this.getSubsystem() + this.getTransaction();
    }
    //</editor-fold>

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////
    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        return toPropiedades("title", "lang", "sub", "tra", "eje", "tmo",
                new PropiedadSeparador("Visualización"), "x", "y", "cssClass",
                new PropiedadSeparador("Javascript"), "initialJS",
                new PropiedadSeparador("Comportamiento"), "pmr", "cma",
                "store", "postQuery", "requiresQuery", "clean", "ngOninit", "legacy",
                new PropiedadSeparador("Navegación interna"), "tabOrder",
                "firstFocus", "queryFocus",
                new PropiedadSeparador("Relaciones"), "references", "attached");
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Métodos de Xml">
    @Override
    public Node getNode(Document d) {
        Element element = d.createElement(getTag());

        element.setAttribute("version", String.valueOf(getVersion()));

        return element;
    }

    @Override
    public Collection<SerializableXml> getChildren() {
        List<SerializableXml> children = new LinkedList<SerializableXml>();

        children.add(new SerializableXml() {

            private static final long serialVersionUID = 1L;

            @Override
            public Node getNode(Document document) {
                Element e = document.createElement("properties");

                for (String s : getAtributosXml()) {
                    if (!properties.get(s).esValorPorDefecto()) {
                        e.setAttribute(s, properties.get(s).getValorString());
                    }
                }

                return e;
            }

            @Override
            public Collection<SerializableXml> getChildren() {
                List<SerializableXml> children
                        = new LinkedList<SerializableXml>();

                for (String s : getHijosXml()) {
                    if (!properties.get(s).esValorPorDefecto()) {
                        children.add(UtilXML.newInstance(s, properties.get(s).
                                getValorXml()));
                    }
                }

                if (getAttached().size() > 0) {
                    XML xml = UtilXML.getXml("attached", "webPage");
                    SerializableXml serializableXml = UtilXML.newInstance(xml,
                            getAttached());
                    children.add(serializableXml);
                }

                if (getReferences().size() > 0) {
                    XML xml = UtilXML.getXml("references", "reference");
                    SerializableXml serializableXml = UtilXML.newInstance(xml,
                            getReferences());
                    children.add(serializableXml);
                }

                return children;
            }

            @Override
            public Object parsear(Node node, Type type) throws ExcepcionParser {
                return null;
            }

            @Override
            public void setValorXml(String tag, Object valor)
                    throws ExcepcionParser {
            }

            protected List<String> getAtributosXml() {
                List<String> l = Arrays.asList(new String[]{"sub", "tra", "eje", "tmo", "x",
                    "y", "w", "h", "lang", "store", "ngOninit", "postQuery", "requiresQuery",
                    "clean", "legacy", "joinQuirk", "firstFocus", "queryFocus"});

                return l;
            }

            protected List<String> getHijosXml() {
                List<String> l = Arrays.asList(new String[]{"cma", "cssClass",
                    "cal", "imports", "exports", "initialJS", "pmr", "title", "tabOrder"});

                return l;
            }

        });

        children.add(new SerializableXml() {

            private static final long serialVersionUID = 1L;

            @Override
            public Node getNode(Document document) {
                Element e = document.createElement("containers");

                return e;
            }

            @Override
            public Collection<SerializableXml> getChildren() {
                List<SerializableXml> children
                        = new LinkedList<SerializableXml>();

                for (Container container : WebPage.this) {
                    children.add(container);
                }

                return children;
            }

            @Override
            public Object parsear(Node node, Type type) throws ExcepcionParser {
                return null;
            }

            @Override
            public void setValorXml(String tag, Object valor)
                    throws ExcepcionParser {
            }

        });

        return children;
    }

    @Override
    protected Collection<String> getHijosXml() {
        return null;
    }

    @Override
    protected Collection<String> getAtributosXml() {
        return null;
    }
    //</editor-fold>

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////
    /**
     * Genera el XHTML del formulario.
     *
     * @param html
     */
    @Override
    public void generateHtml(ConstructorHtml html) {
        WebPageEnviroment.reset(false);
        WebPageEnviroment.setFirstFocus(getFirstFocus());

        html.abrir("form");
        html.setAtributo("enctype", "multipart/form-data");
        html.setAtributo("method", "post");
        html.setAtributo("autocomplete", "off");
        html.setAtributo("class", "web-page " + getCSSClass(), "");
        html.extenderAtributo("onsubmit", "return false;");
        html.setEstilo("margin-left", getX(), "px", 0);
        html.setEstilo("margin-top", getY(), "px", 0);
        html.setEstilo("min-width", getW(), "px", 0);
        html.setEstilo("min-height", getH(), "px", 0);

        for (Container container : this) {
            container.generateHtml(html);
        }

        html.cerrar("form");
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        WebPageEnviroment.reset(false);
        WebPageEnviroment.setFirstFocus(getFirstFocus());

        html.abrir("div");
        //html.setAtributo("enctype", "multipart/form-data");
        //html.setAtributo("method", "post");
        //html.setAtributo("autocomplete", "off");
        html.setAtributo("class", "container", "");
        //html.extenderAtributo("onsubmit", "return false;");
        //html.setEstilo("margin-left", getX(), "px", 0);
        //html.setEstilo("margin-top", getY(), "px", 0);
        //html.setEstilo("min-width", getW(), "px", 0);
        //html.setEstilo("min-height", getH(), "px", 0);
        if (StringUtils.isNotBlank(getTitle())) {
//            html.abrirNg("mat-label");
//            html.setTexto(getTitle());
//            html.cerrar("mat-label");
            html.abrir("h3");
            html.setAtributo("style", "text-align: center");
            html.abrir("strong");
            html.setTexto(getTitle());
            html.cerrar("strong");
            html.cerrar("h3");
        }
        System.out.println("" + getInitialJS());
        WebPageEnviromentNG.addFunctions(getInitialJS());

        for (Container container : this) {
//validar con un tipo TAB se genera para ese 
            container.generateHtmlNg(html);
        }
        if (getNgOninit()) {
            WebPageEnviromentNG.setImplemnts("implements OnInit");
        }
        if (!getImports().isEmpty()) {
            WebPageEnviromentNG.addCustomImports(getImports());
        }
        if (!getExports().isEmpty()) {
            WebPageEnviromentNG.addCustomExports(getExports());
        }
        WebPageEnviromentNG.addVariablesWithValue("intento", "true");
        html.cerrar("div");
    }

}
