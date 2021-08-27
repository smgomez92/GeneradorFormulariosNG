package com.fitbank.webpages.widgets;

import java.util.Collection;
import java.util.Collections;

import com.fitbank.propiedades.Propiedad;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.webpages.Widget;

/**
 * Separa el header del cuerpo de una tabla
 * 
 * @author FitBank
 * @version 2.0
 */
public class HeaderSeparator extends Widget {

    private static final long serialVersionUID = 1L;

    public HeaderSeparator() {
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////

    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        return Collections.EMPTY_LIST;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////

    @Override
    protected Collection<String> getAtributosElementos() {
        return Collections.EMPTY_LIST;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////

    public void generateHtml(ConstructorHtml xhtml) {
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
    }

}
