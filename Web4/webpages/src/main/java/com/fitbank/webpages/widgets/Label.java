package com.fitbank.webpages.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fitbank.js.JS;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadJavascript.Tipo;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Editable;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.Widget;
import java.util.Map;

/**
 * Un Label
 * 
 * @author FitBank
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class Label extends Widget {

    private static final long serialVersionUID = 2L;

    private final static String FUNCTION_NAME_TEMPLATE = "parent.c.formulario.vars['%s_%s']";

    public Label() {
        def("jvs", new PropiedadJavascript(Tipo.EVENTOS));
        def("ide", "");
        def("gia", "");
        def("vis", true);
    }

    public Label(String texto) {
        this();

        setTexto(texto);
    }

    @Override
    protected String getIdForHTMLId() {
        if (properties.get("ide").esValorPorDefecto()) {
            return super.getId();
        } else {
            return getIdentificador();
        }
    }

    @Override
    public String toString() {
        return super.toString() + " (\"" + getTexto() + "\")";
    }

    @Override
    protected boolean usesDimensions() {
        return true;
    }

    // ////////////////////////////////////////////////////////
    // Geters y seters de properties
    // ////////////////////////////////////////////////////////

    public String getJavaScript() {
        return ((PropiedadJavascript) properties.get("jvs")).getValorString();
    }

    public void setJavaScript(String jvs) {
        properties.get("jvs").setValorString(jvs);
    }

    public PropiedadJavascript getPropiedadJavaScript() {
        return (PropiedadJavascript) properties.get("jvs");
    }

    public String getIdentificador() {
            return ((PropiedadSimple) properties.get("ide")).getValor();
        }

    public void setIdentificador(String identificador) {
        properties.get("ide").setValor(identificador);
    }

    public String getGuia() {
        return ((PropiedadSimple) properties.get("gia")).getValor();
    }

    public void setGuia(String guia) {
        properties.get("gia").setValor(guia);
    }

    @Override
    @Editable
    @JS
    public boolean getVisible() {
        return ((PropiedadBooleana) properties.get("vis")).getValor();
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////

    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = super.getPropiedadesEdicion();

        l.addAll(toPropiedades("gia", "jvs"));

        if (getClass().equals(Label.class)) {
            l.addAll(toPropiedades("tex", "ide"));
        }

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////

    @Override
    protected Collection<String> getAtributosElementos() {
        List<String> l = new ArrayList<String>();

        Collections.addAll(l, "gia", "ide", "jvs", "vis");

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////

    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();
        generarHtmlBase(html);

        html.abrir("span");

        generarHtmlGuia(html, getGuia());

        html.setAtributo("id", getHTMLId());

        if (getParentContainer().getHorizontal()) {
            html.setEstilo("width", getParentContainer().getW());
        }

        if (!getVisible()) {
            html.setEstilo("display", "none");
        }
        
        generarEventosJavascript(html);
        generarClasesCSS(html);

        html.setTexto(getTexto());

        html.cerrar("span");

        finalizarHtmlBase(html);
    }

    @Override
    protected Collection<String> getCSSClasses() {
        Collection<String> classes = super.getCSSClasses();

        if (StringUtils.isBlank(getTexto())) {
            classes.add("empty");
        }

        return classes;
    }

    /**
     * Genera los eventos para campos visibles que usan elementos ocultos
     *
     * @param html ConstructorHtml donde se va a generar los eventos
     */
    public void generarEventosJavascript(ConstructorHtml html) {
        html.setAtributo("registro", getIndiceClonacion());

        generarHtmlGuia(html, getGuia());

        for (String evento : getPropiedadJavaScript().getEventos().keySet()) {
            String functionName = String.format(FUNCTION_NAME_TEMPLATE, 
                    getHTMLId(), evento);
            this.generarEventoJavascript(html, evento, getHTMLId(), 
                    String.format("%s.bind(this)(event);", functionName));
        }
    }

    public void generarEventoJSInicial() {
        if (getIndiceClonacion() == 0) {
            Map<String, String> eventos = getPropiedadJavaScript().getEventos();
            for (String evento : eventos.keySet()) {
                String code = eventos.get(evento);

                String functionName = String.format(FUNCTION_NAME_TEMPLATE, 
                        getHTMLId(), evento);
                WebPageEnviroment.addJavascriptInicial(String.format(
                        "%s = function(e) { %s }", functionName, code));
            }
        }
    }

    @Override
    protected void generarEventoJavascript(ConstructorHtml html, String evento,
            String nameOrId, String code) {
        html.extenderAtributo(evento, code);
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarEventoJSInicial();
        generarHtmlBase(html);

        html.abrirNg("mat-label");

        generarHtmlGuia(html, getGuia());

        html.setAtributo("id", getHTMLId());

        if (getParentContainer().getHorizontal()) {
            html.setEstilo("width", getParentContainer().getW());
        }

        if (!getVisible()) {
            html.setEstilo("display", "none");
        }
        
        //generarEventosJavascript(html);
        //generarClasesCSS(html);

        html.setTexto(getTexto());

        html.cerrar("mat-label");

        finalizarHtmlBase(html);
    }

}
