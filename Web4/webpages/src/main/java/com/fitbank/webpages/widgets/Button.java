package com.fitbank.webpages.widgets;

import java.util.Collection;
import java.util.Collections;

import com.fitbank.enums.Modificable;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadSeparador;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Editable;
import com.fitbank.webpages.assistants.FloatButton;

/**
 * Widget que implementa un boton.
 *
 * @author FitBank
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class Button extends Input {

    private static final long serialVersionUID = 2L;

    public Button() {
        def("dat", "");
        def("fdisa", "");

        properties.get("w").setValorPorDefecto(0);
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////
    @Editable
    public String getEtiqueta() {
        return ((PropiedadSimple) properties.get("dat")).getValor();
    }

    public void setEtiqueta(String etiqueta) {
        properties.get("dat").setValor(etiqueta);
    }

    @Editable
    public String getFormulaDisable() {
        return ((PropiedadSimple) properties.get("fdisa")).getValor();
    }

    public void setgetFormulaDisable(String etiqueta) {
        properties.get("fdisa").setValor(etiqueta);
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////
    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = super.getPropiedadesEdicion();

        l.addAll(toPropiedades(new PropiedadSeparador("Propiedades Botón"), "dat", "fdisa"));

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////
    @Override
    protected Collection<String> getAtributosElementos() {
        Collection<String> l = super.getAtributosElementos();

        Collections.addAll(l, "dat", "fdisa");

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////
    @Override
    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();

        generarHtmlBase(html);

        html.abrir("button");

        html.setAtributo("type", "button");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("name", getNameOrDefault(), "");
        html.setAtributo("value", getValueInicial(), "");

        generarTabIndex(html);
        generarClasesCSS(html);
        generarEventosJavascript(html);
        generarHtmlGuia(html, getGuia());

        if (getVisible()) {
            if (getModificable() == Modificable.SOLO_LECTURA) {
                html.setAtributo("disabled", "true");
            }
            html.setEstilo("width", getW(), "px", 0);
            html.setEstilo("height", getH(), "px", 0);

            html.setTexto(getEtiqueta());
        } else {
            html.setEstilo("display", "none");
            html.setTexto(" ");
        }

        html.cerrar("button");
        generarInputOculto("button");

        finalizarHtmlBase(html);
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarEventoJSInicial();
        if (getAssistant().getClass().equals(FloatButton.class)) {
            createFloatButtonElement(html, (FloatButton) getAssistant());
            return;
        }

//        generarHtmlBase(html);
        html.abrir("button");

//        html.setAtributo("type", "button");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("mat-raised-button", "true");
        html.setAtributo("color", "primary");
        if (getFormulaDisable() != null) {
            html.setAtributo("abreCorch--disabled--cerrCorch--", getFormulaDisable());
        }

//        generarTabIndex(html);
//        generarClasesCSS(html);
        this.setFUNCTION_NAME_TEMPLATE_NG("%s_%s($event)");
        generarEventosTypescript(html);
//        generarEventosJavascript(html);
//        generarHtmlGuia(html, getGuia());

        if (getVisible()) {
            if (getModificable() == Modificable.SOLO_LECTURA) {
                html.setAtributo("disabled", "true");
            }
            html.setEstilo("width", getW(), "px", 0);
            html.setEstilo("height", getH(), "px", 0);

            html.setTexto(getEtiqueta());
        } else {
            html.setEstilo("display", "none");
            html.setTexto(" ");
        }

        html.cerrar("button");
        generarInputOculto("button");

//        finalizarHtmlBase(html);
    }

    public void createFloatButtonElement(ConstructorHtml html, FloatButton fb) {
        this.setFUNCTION_NAME_TEMPLATE_NG("%s_%s($event)");
        html.abrir("div");
        if (fb.isCheckButton()) {
            html.setAtributo("class", "float-Sendmenu");
        } else if (fb.isPrintButton()) {
            html.setAtributo("class", "float-TopRighttBtn");
        } else {
            html.setAtributo("class", "float");
        }

        if (!fb.getFormula().equals("")) {
            html.setAtributo("asterisco--ngIf", fb.getFormula());
        }
        html.abrir("button");
        html.setAtributo("mat-fab", "true");

        generarEventosTypescript(html);

        if (fb.isCancelButton()) {
            html.setAtributo("class", "float-right");
        }

        html.setAtributo("color", fb.isCheckButton() || fb.isPrintButton() ? "primary" : "warn");
        html.abrirNg("mat-icon");
        if (fb.isCheckButton()) {
            html.setAtributo("color", "white");
        }

        if (fb.isCheckButton()) {
            html.setTexto("check_circle");
        } else if (fb.isPrintButton()) {
            html.setTexto("print");
        } else {
            html.setTexto("cancel");
        }

        html.cerrar("mat-icon");
        html.cerrar("button");
        html.cerrar("div");

        /*
        <div class="float-Sendmenu" *ngIf="valorCuota != ''">
        <button mat-fab (click)="consultarScore()" color="primary" *ngIf="!bandReporte && internet.internet">
            <mat-icon color="white">check_circle</mat-icon>
        </button>
        
    </div>
        
        <div class="float">
    <button mat-fab (click)="limpiar()" class="float-right" color="warn">
        <mat-icon>cancel</mat-icon>
    </button>
</div>
        
         */
    }
}
