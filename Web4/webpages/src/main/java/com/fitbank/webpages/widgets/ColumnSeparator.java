package com.fitbank.webpages.widgets;

import java.util.Collection;
import java.util.Collections;

import com.fitbank.propiedades.Propiedad;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.webpages.Widget;

/**
 * Separador de columnas en un container tipo COLUMNS.
 *
 * @author FitBank CI
 */
public class ColumnSeparator extends Widget {

    private static final long serialVersionUID = 1L;

    public ColumnSeparator() {
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
