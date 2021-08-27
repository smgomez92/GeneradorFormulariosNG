package com.fitbank.webpages.widgets;

import java.util.ArrayList;
import java.util.Collection;

import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.webpages.Widget;

/**
 * 
 * @author FitBank
 * @version 2.0
 */
public class Square extends Widget {

    private static final long serialVersionUID = 2L;

    public Square() {
    }

    @Override
    protected boolean usesDimensions() {
        return true;
    }

    // ////////////////////////////////////////////////////////
    // Geters y seters de properties
    // ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////

    @Override
    protected Collection<String> getAtributosElementos() {
        return new ArrayList<String>();
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////

    public void generateHtml(ConstructorHtml html) {
        generarHtmlBase(html);
        
        html.abrir("div");
        html.setComentario("");

        html.setAtributo("id", getHTMLId());
        html.setEstilo("height", getH(), "px", 0);
        html.setEstilo("width", getW(), "px", 0);

        generarClasesCSS(html);
        html.cerrar("div");
        
        finalizarHtmlBase(html);
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarHtmlBase(html);
        
        html.abrir("div");
        html.setComentario("");

        html.setAtributo("id", getHTMLId());
        html.setEstilo("height", getH(), "px", 0);
        html.setEstilo("width", getW(), "px", 0);

        generarClasesCSS(html);
        html.cerrar("div");
        
        finalizarHtmlBase(html);
    }
}
