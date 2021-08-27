/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fitbank.webpages.widgets;

import com.fitbank.enums.Modificable;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadListaString;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Editable;
import com.fitbank.webpages.WebPageEnviromentNG;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author santy
 */
public class RadioButtonNG extends Input {

    public RadioButtonNG() {
        def("ops", new PropiedadListaString<String>(""));
        def("dats", new PropiedadListaString<String>(""));
        // def("blk", true);
        // def("mul", false);
        def("w", 0);
    }

    // ////////////////////////////////////////////////////////
    // Geters y seters de properties
    // ////////////////////////////////////////////////////////
    @Editable
    public List<String> getDatos() {
        return ((PropiedadListaString<String>) properties.get("dats")).getList();
    }

    public void setDatos(List<String> datos) {
        properties.get("dats").setValor(datos);
    }

    @Editable
    public List<String> getChoice() {
        return ((PropiedadListaString<String>) properties.get("ops")).getList();
    }

    public void setChoice(List<String> choice) {
        properties.get("ops").setValor(choice);
    }

//    public boolean getMultiple() {
//        return ((PropiedadBooleana) properties.get("mul")).getValor();
//    }
//
//    public void setMultiple(boolean multiple) {
//        properties.get("mul").setValor(multiple);
//    }
//    public boolean getOpcionVacia() {
//        return ((PropiedadBooleana) properties.get("blk")).getValor();
//    }
//
//    public void setOpcionVacia(boolean opcionVacia) {
//        properties.get("blk").setValor(opcionVacia);
//    }
    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////
    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = super.getPropiedadesEdicion();
        l.addAll(toPropiedades("ops", "dats"));
        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////
    @Override
    protected Collection<String> getAtributosElementos() {
        Collection<String> l = super.getAtributosElementos();
        Collections.addAll(l, "ops", "dats");
        return l;
    }
    
    
    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarEventoJSInicial();
//        generarHtmlBase(html);
        html.abrirNg("mat-radio-group");
//        html.abrirNg("mat-label");
//        html.setTexto(getNameNg());
//        html.cerrar("mat-label");
        html.setAtributo("aria-label", getNameNg());
//        html.setAtributo("fxFlexFill", "true");
//        html.abrirNg("mat-select");
        html.setAtributo("id", getHTMLId());
//        html.setAtributo("abreCorch--formControl--cerrCorch--", getNameOrDefault());
//        WebPageEnviromentNG.addVariablesForm(getNameOrDefault(), getTypeFrm());
//        if (getMultiple()) {
//            html.setAtributo("multiple", "multiple");
//        }
//        if (getModificable() == Modificable.SOLO_LECTURA) {
//            html.setAtributo("disabled", "true", "");
//        }

//        html.setEstilo("width", getW(), "px", 0);
//        html.setEstilo("height", getH(), "px", 0);

//        generarTabIndex(html);
//        generarClasesCSS(html);
//        generarEventosJavascript(html);
//        generarHtmlGuia(html, getGuia());
//        if (getOpcionVacia()) {
//            html.abrirNg("mat-option");
//            html.setTexto("None");
//            html.cerrar("mat-option");
//        }

        for (int a = 0; a < getChoice().size(); a++) {
            html.abrirNg("mat-radio-button");
            html.setAtributo("value", getChoice().get(a));
            if (getValueFilaActual().equals(getChoice().get(a))) {
               // html.setAtributo("selected", "selected");
            }
            html.setTexto(getDatos().get(a));
            html.cerrar("mat-radio-button");
        }

       // html.cerrar("mat-select");
        html.cerrar("mat-radio-group");
//        finalizarHtmlBase(html);
    }

}
