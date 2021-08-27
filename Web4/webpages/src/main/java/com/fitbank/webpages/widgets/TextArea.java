package com.fitbank.webpages.widgets;

import java.util.Collection;
import java.util.Collections;

import com.fitbank.enums.Modificable;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.webpages.assistants.LongText;

/**
 * 
 * @author FitBank
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class TextArea extends Input {

    private static final long serialVersionUID = 2L;

    public TextArea() {
        setAssistant(new LongText());

        def("wra", false);
        def("lon", 150);
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////
    public boolean getWrap() {
        return ((PropiedadBooleana) properties.get("wra")).getValor();
    }

    public void setWrap(boolean wrap) {
        properties.get("wra").setValor(wrap);
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////
    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = super.getPropiedadesEdicion();

        l.addAll(toPropiedades("wra"));

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////
    @Override
    protected Collection<String> getAtributosElementos() {
        Collection<String> l = super.getAtributosElementos();

        Collections.addAll(l, "wra");

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////
    @Override
    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();
        generarHtmlBase(html);

        html.abrir("textarea");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("name", getNameOrDefault());
        html.setAtributo("maxlength", getLongitud(), 0);
        html.setAtributo("wrap", !getWrap() ? "off" : "", "");

        if (getVisible()) {
            if (getModificable() == Modificable.SOLO_LECTURA) {
                html.setAtributo("disabled", "true");
            }

            html.setEstilo("width", getW(), "px");
            html.setEstilo("height", getH(), "px", 0);
        } else {
            html.setEstilo("display", "none");
        }


        generarTabIndex(html);
        generarClasesCSS(html);
        generarHtmlGuia(html, getGuia());
        generarEventosJavascript(html);
        generarInputOculto("textarea");

        html.setTexto(getValueFilaActual());

        html.cerrar("textarea");

        finalizarHtmlBase(html);
    }
}
