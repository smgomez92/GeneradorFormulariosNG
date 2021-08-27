package com.fitbank.webpages.widgets;

import java.util.Collection;
import java.util.Collections;

import com.fitbank.enums.Modificable;
import com.fitbank.enums.PosicionHorizontal;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadEnum;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Editable;
import com.fitbank.webpages.WebPageEnviromentNG;

/**
 *
 * @author FitBank
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public class CheckBox extends Input {

    private static final long serialVersionUID = 2L;

    public CheckBox() {
        def("sel", false);
        def("val", "1");
        def("vns", "0");
        def("dat", "");
        def("lad", PosicionHorizontal.DERECHA);

        properties.get("w").setValorPorDefecto(0);
    }

    // ////////////////////////////////////////////////////////
    // Geters y seters de properties
    // ////////////////////////////////////////////////////////
    @Override
    public String getRelleno() {
        return getSeleccionadoInicialmente() ? getValorSeleccionado()
                : getValorNoSeleccionado();
    }

    @Editable
    public String getValorNoSeleccionado() {
        return ((PropiedadSimple) properties.get("vns")).getValor();
    }

    public String getValorSeleccionado() {
        return getValueInicial();
    }

    public void setValorNoSeleccionado(String vns) {
        properties.get("vns").setValor(vns);
    }

    @Editable
    public boolean getSeleccionadoInicialmente() {
        return ((PropiedadBooleana) properties.get("sel")).getValor();
    }

    public void setSeleccionadoInicialmente(boolean seleccionado) {
        ((PropiedadBooleana) properties.get("sel")).setValor(seleccionado);
    }

    @Editable
    public String getEtiqueta() {
        return ((PropiedadSimple) properties.get("dat")).getValor();
    }

    public void setEtiqueta(String etiqueta) {
        properties.get("dat").setValor(etiqueta);
    }

    @Editable
    public PosicionHorizontal getLado() {
        return ((PropiedadEnum<PosicionHorizontal>) properties.get("lad"))
                .getValor();
    }

    public void setLado(PosicionHorizontal accion) {
        properties.get("lad").setValor(accion);
    }

    public boolean estaSeleccionadoFilaActual() {
        return getValorSeleccionado().equals(getValueFilaActual());
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////
    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = super.getPropiedadesEdicion();

        l.addAll(toPropiedades("sel", "vns", "dat", "lad"));

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////
    @Override
    protected Collection<String> getAtributosElementos() {
        Collection<String> l = super.getAtributosElementos();

        Collections.addAll(l, "sel", "vns", "dat", "lad");

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////
    @Override
    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();
        generarHtmlBase(html);
        generarHtmlGuia(html, getGuia());
        html.abrir("span");

        if (getLado() == PosicionHorizontal.IZQUIERDA) {
            generarLabel(html, getHTMLId());
        }

        html.agregar("input");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("name", getNameOrDefault(), "");
        html.setAtributo("value-on", getValorSeleccionado());
        html.setAtributo("value-off", getValorNoSeleccionado());

        if (getVisible()) {
            html.setAtributo("type", "checkbox");
            if (getSeleccionadoInicialmente()) {
                html.setAtributo("checked", true);
            }
            if (getModificable() == Modificable.SOLO_LECTURA) {
                html.setAtributo("disabled", true);
            }
        } else {
            html.setAtributo("type", "hidden");
        }

        generarTabIndex(html);
        generarClasesCSS(html);
        generarEventosJavascript(html);

        if (getVisible() && getLado() == PosicionHorizontal.DERECHA) {
            generarLabel(html, getHTMLId());
        }

        html.cerrar("span");
        finalizarHtmlBase(html);
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarEventoJSInicial();
//        generarHtmlBase(html);
//        generarHtmlGuia(html, getGuia());
        html.abrirNg("mat-checkbox");

        if (getLado() == PosicionHorizontal.IZQUIERDA) {
            generarLabel(html, getHTMLId());
        }
        html.setAtributo("abreCorch--abreParent--ngModel--cerrParent----cerrCorch--", getNameOrDefault());
        WebPageEnviromentNG.addVariables(getNameOrDefault());
        //html.setAtributo("id", getHTMLId());

        if (getVisible()) {
//            html.setAtributo("type", "checkbox");
            if (getSeleccionadoInicialmente()) {
                html.setAtributo("checked", true);
            }
            if (getModificable() == Modificable.SOLO_LECTURA) {
                html.setAtributo("disabled", true);
            }
        } else {
            html.setAtributo("type", "hidden");
        }
        // html.setTexto(getTexto());
        html.setTexto(getNameNg());
//        generarTabIndex(html);
//        generarClasesCSS(html);
//        generarEventosJavascript(html);
        generarEventosTypescript(html);
//        if (getVisible() && getLado() == PosicionHorizontal.DERECHA) {
//            generarLabel(html, getHTMLId());
//        }

        html.cerrar("mat-checkbox");
//        finalizarHtmlBase(html);
    }

    protected void generarLabel(ConstructorHtml xhtml, String id) {
        xhtml.agregar("label");
        xhtml.setAtributo("for", id);
        xhtml.setAtributo("class", getCSSClass(), "");

        xhtml.setTexto(getEtiqueta());
    }

    @Override
    protected void generarEventoJavascript(ConstructorHtml html, String evento,
            String nameOrId, String code) {
        if (evento.equals("onchange")) {
            code = "this.fixValue && this.fixValue();" + code;
        }
        html.extenderAtributo(evento, code);
    }

}
