package com.fitbank.webpages.widgets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fitbank.enums.Modificable;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadListaString;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Editable;
import com.fitbank.webpages.WebPageEnviromentNG;

/**
 * 
 * @author FitBank
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class ComboBox extends Input {

    private static final long serialVersionUID = 2L;

    public ComboBox() {
        def("cho", new PropiedadListaString<String>(""));
        def("dat", new PropiedadListaString<String>(""));
        def("blk", true);
        def("mul", false);
        def("w", 0);
    }

    // ////////////////////////////////////////////////////////
    // Geters y seters de properties
    // ////////////////////////////////////////////////////////

    @Editable
    public List<String> getDatos() {
        return ((PropiedadListaString<String>) properties.get("dat")).getList();
    }

    public void setDatos(List<String> datos) {
        properties.get("dat").setValor(datos);
    }

    @Editable
    public List<String> getChoice() {
        return ((PropiedadListaString<String>) properties.get("cho")).getList();
    }

    public void setChoice(List<String> choice) {
        properties.get("cho").setValor(choice);
    }

    public boolean getMultiple() {
        return ((PropiedadBooleana) properties.get("mul")).getValor();
    }

    public void setMultiple(boolean multiple) {
        properties.get("mul").setValor(multiple);
    }

    public boolean getOpcionVacia() {
        return ((PropiedadBooleana) properties.get("blk")).getValor();
    }

    public void setOpcionVacia(boolean opcionVacia) {
        properties.get("blk").setValor(opcionVacia);
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////

    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = super.getPropiedadesEdicion();

        l.addAll(toPropiedades("cho", "dat", "blk", "mul"));

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////

    @Override
    protected Collection<String> getAtributosElementos() {
        Collection<String> l = super.getAtributosElementos();

        Collections.addAll(l, "blk", "cho", "dat", "mul");

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////

    @Override
    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();
        generarHtmlBase(html);

        html.abrir("select");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("name", getNameOrDefault(), "");
        if (getMultiple()) {
            html.setAtributo("multiple", "multiple");
        }
        if (getModificable() == Modificable.SOLO_LECTURA) {
            html.setAtributo("disabled", "true", "");
        }

        html.setEstilo("width", getW(), "px", 0);
        html.setEstilo("height", getH(), "px", 0);

        generarTabIndex(html);
        generarClasesCSS(html);
        generarEventosJavascript(html);
        generarHtmlGuia(html, getGuia());

        if (getOpcionVacia()) {
            html.abrir("option");
            html.setAtributo("value", "");
            html.setTexto("");
            html.cerrar("option");
        }

        for (int a = 0; a < getChoice().size(); a++) {
            html.abrir("option");
            html.setAtributo("value", getChoice().get(a));
            if (getValueFilaActual().equals(getChoice().get(a))) {
                html.setAtributo("selected", "selected");
            }
            html.setTexto(getDatos().get(a));
            html.cerrar("option");
        }

        html.cerrar("select");

        finalizarHtmlBase(html);
    }
    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarEventoJSInicial();
//        generarHtmlBase(html);
        html.abrirNg("mat-form-field");
        html.abrirNg("mat-label");
        html.setTexto(getNameNg());
        html.cerrar("mat-label");
        html.setAtributo("appearance", "outline");
        html.setAtributo("fxFlexFill", "true");
        html.abrirNg("mat-select");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("abreCorch--formControl--cerrCorch--", getNameOrDefault());
        WebPageEnviromentNG.addVariablesForm(getNameOrDefault(), getTypeFrm());
        //solo funcionara selectionChange
        generarEventosTypescript(html);
        if (getMultiple()) {
            html.setAtributo("multiple", "multiple");
        }
        if (getModificable() == Modificable.SOLO_LECTURA) {
            html.setAtributo("disabled", "true", "");
        }

        html.setEstilo("width", getW(), "px", 0);
        html.setEstilo("height", getH(), "px", 0);

//        generarTabIndex(html);
//        generarClasesCSS(html);
 //      generarEventosJavascript(html);
//        generarHtmlGuia(html, getGuia());
        if (getOpcionVacia()) {
            html.abrirNg("mat-option");
            html.setTexto(" ");
            html.cerrar("mat-option");
        }

        for (int a = 0; a < getChoice().size(); a++) {
            html.abrirNg("mat-option");
            html.setAtributo("value", getChoice().get(a));
            if (getValueFilaActual().equals(getChoice().get(a))) {
                html.setAtributo("selected", "selected");
            }
            html.setTexto(getDatos().get(a));
            html.cerrar("mat-option");
        }

        html.cerrar("mat-select");
        html.cerrar("mat-form-field");

//        finalizarHtmlBase(html);
    }
}
