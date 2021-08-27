package com.fitbank.webpages.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Editable;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.Widget;

public class RemoteIFrame extends Widget {

    private static final long serialVersionUID = 1L;

    public RemoteIFrame() {
        def("post", true);
        def("query", false);
        def("hideKeys", false);
        def("initRemoto", true);
        def("expand", true);
    }


    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////

    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = toPropiedades("post", "query", "hideKeys", "initRemoto", "expand");

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////
    @Override
    protected Collection<String> getAtributosElementos() {
        List<String> l = new ArrayList<String>();

        Collections.addAll(l, "post", "query", "hideKeys", "initRemoto", "expand");

        return l;
    }

    @Editable
    public boolean getPost() {
        return ((PropiedadBooleana) properties.get("post")).getValor();
    }

    public void setPost(boolean query) {
        properties.get("post").setValor(query);
    }

    @Editable
    public boolean getQuery() {
        return ((PropiedadBooleana) properties.get("query")).getValor();
    }

    public void setQuery(boolean query) {
        properties.get("query").setValor(query);
    }

    @Editable
    public boolean getEsconderTeclas() {
        return ((PropiedadBooleana) properties.get("hideKeys")).getValor();
    }

    public void setEsconderTeclas(boolean teclas) {
        properties.get("hideKeys").setValor(teclas);
    }

    @Editable
    public boolean getInitRemoto(){
        return ((PropiedadBooleana) properties.get("initRemoto")).getValor();
    }

    public void setInitRemoto(boolean valor){
        properties.get("initRemoto").setValor(valor);
    }

    @Editable
    public boolean getExpand(){
        return ((PropiedadBooleana) properties.get("expand")).getValor();
    }

    public void setExpand(boolean valor){
        properties.get("expand").setValor(valor);
    }

    public void generateHtml(ConstructorHtml html) {
        generarHtmlBase(html);

        html.abrir("iframe");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("name", getHTMLId());
        html.setAtributo("border", 0);
        html.setAtributo("frameborder", 0);

        generarEventosJS();
        generarClasesCSS(html);

        html.cerrar("iframe");

        finalizarHtmlBase(html);
    }

    private void generarEventosJS() {
        String src = WebPageEnviroment.getRemoteURL();

        WebPageEnviroment.addJavascriptInicial("c.initRemoto('" + StringEscapeUtils.
                escapeJavaScript(src) + "', '" + getHTMLId() + "', "
                + getQuery() + ", " + getPost() + ", " + getEsconderTeclas() + 
                ", " + getInitRemoto()+ ", " + getExpand() + ");");
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarHtmlBase(html);

        html.abrir("iframe");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("name", getHTMLId());
        html.setAtributo("border", 0);
        html.setAtributo("frameborder", 0);

        generarEventosJS();
        generarClasesCSS(html);

        html.cerrar("iframe");

        finalizarHtmlBase(html);
    }
}
