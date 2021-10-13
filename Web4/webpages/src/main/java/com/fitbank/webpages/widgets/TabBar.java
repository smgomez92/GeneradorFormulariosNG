package com.fitbank.webpages.widgets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fitbank.enums.OrientacionTabs;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadEnum;
import com.fitbank.propiedades.PropiedadListaString;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Servicios;
import com.fitbank.webpages.Container;

/**
 *
 * @author FitBank
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class TabBar extends Label {

    private static final long serialVersionUID = 2L;

    public TabBar() {
        def("tex", new PropiedadListaString<String>(""));
        def("url", new PropiedadListaString<String>("1"));
        def("ort", OrientacionTabs.HORIZONTAL);
    }

    @Override
    public String getTexto() {
        return StringUtils.join(getTabLabels(), ",");
    }

    @Override
    public void setTexto(String texto) {
        setTabLabels(Arrays.asList(texto.split(",", -1)));
    }

    @Override
    protected boolean usesDimensions() {
        return true;
    }

    // ////////////////////////////////////////////////////////
    // Geters y seters de properties
    // ////////////////////////////////////////////////////////
    public List<String> getTabLabels() {
        return ((PropiedadListaString<String>) properties.get("tex")).getList();
    }

    public void setTabLabels(List<String> tabLabels) {
        properties.get("tex").setValor(tabLabels);
    }

    public List<String> getTabs() {
        return ((PropiedadListaString<String>) properties.get("url")).getList();
    }

    public void setTabs(List<String> tabs) {
        properties.get("url").setValor(tabs);
    }

    public OrientacionTabs getOrientacion() {
        return ((PropiedadEnum<OrientacionTabs>) properties.get("ort"))
                .getValor();
    }

    public void setOrientacion(OrientacionTabs orientacion) {
        properties.get("ort").setValor(orientacion);
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////
    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = super.getPropiedadesEdicion();

        l.addAll(toPropiedades("ort", "tex", "url"));

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////
    @Override
    protected Collection<String> getAtributosElementos() {
        Collection<String> l = super.getAtributosElementos();

        Collections.addAll(l, "url", "ort");

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////
    @Override
    public void generateHtml(ConstructorHtml html) {
        /* for (Container container : this) {//obtener los contenedoresde alguna mandera.
//validar con un tipo TAB 
            container.generateHtmlNg(html);
        }*/
        if (getTabLabels().isEmpty()) {
            return;
        }

        generarEventoJSInicial();
        generarHtmlBase(html);

        html.abrir("ul");
        html.setAtributo("id", getHTMLId());
        html.setEstilo("width", getW(), "px", 0);

        if (!getVisible()) {
            html.setEstilo("display", "none");
        }

        generarClasesCSS(html);

        for (int i = 0; i < getTabs().size(); i++) {
            String tabActual = getTabs().get(i);
            String idTab = Servicios.generarIdUnicoTemporal();

            html.abrir("li");
            html.setAtributo("id", idTab);
            html.setAtributo("class", StringUtils.join(getTabCSSClasses("tab-bar", tabActual), " "), "");

            html.abrir("a");
            html.setAtributo("href", "#");

            generarEventosJavascript(html);
            generarEventosHTML(html, i, tabActual);
            generarHtmlGuia(html, "");

            html.setTexto(getTabLabels().get(i));
            html.cerrar("a");

            html.cerrar("li");
        }
        html.cerrar("ul");

        finalizarHtmlBase(html);
    }
    

    public void generarHtmlNg(ConstructorHtml html) {
       /**
        * 
        */
    }

    /**
     * Obtiene una lista de clases css para un tab. Por ejemplo si se pasa como
     * parametros "ABC" y "2-2-0-1", devuelve:
     *
     * ABC-2-2 ABC-2-2-0 ABC-2-2-0-1
     *
     * Debido a que este tab se muestra cuando se muestra un padre si el tab
     * termina en 0 o 1. Tambien a la lista se agrega:
     *
     * ABC-child-2 ABC-child-2-2 ABC-child-2-2-0
     *
     * Por que el tab es hijo de todos esos tabs.
     *
     * @param prefix Prefijo a ser agragado al principio
     * @param tabString String con el tab
     *
     * @return Collection de Strings con las clases css.
     */
    public Collection<String> getTabCSSClasses(String prefix, String tabString) {
        Collection<String> cssClasses = new LinkedList<String>();

        if (!prefix.endsWith("-")) {
            prefix += "-";
        }

        cssClasses.add(prefix + tabString);

        String tab = "";
        for (String tabPart : tabString.split("-")) {
            tab += tabPart;
            if (!tab.equals(tabString)) {
                cssClasses.add(prefix + "child-" + tab);
                tab += "-";
            }
        }
        while (tab.endsWith("-0") || tab.endsWith("-1")) {
            tab = tab.substring(0, tab.length() - 2);
            cssClasses.add(prefix + tab);
        }

        return cssClasses;
    }

    @Override
    protected Collection<String> getCSSClasses() {
        Collection<String> cssClasses = super.getCSSClasses();

        cssClasses.add("tabs");
        if (getOrientacion() == OrientacionTabs.VERTICAL) {
            cssClasses.add("vertical");
        }

        return cssClasses;
    }

    protected void generarEventosHTML(ConstructorHtml xhtml, int i, String tab) {
        xhtml.extenderAtributo("onclick", "Tabs.mostrar('" + tab + "', this); return false;");
    }
}
