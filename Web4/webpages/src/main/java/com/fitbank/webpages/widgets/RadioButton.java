package com.fitbank.webpages.widgets;

import com.fitbank.enums.PosicionHorizontal;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Servicios;

/**
 * 
 * @author FitBank
 * @version 2.0
 */
public class RadioButton extends CheckBox {

    private static final long serialVersionUID = 2L;

    public RadioButton() {
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////

    @Override
    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();
        generarHtmlBase(html);
        generarHtmlGuia(html, getGuia());

        // FIXME: en este caso no se puede acceder a los radiobuttons con js por
        // el id que se general. Buscar una mejor solución.
        String forId = getHTMLId() + "_" + Servicios.generarIdUnicoTemporal();

        if (getLado() == PosicionHorizontal.IZQUIERDA) {
            generarLabel(html, forId);
        }

        html.abrir("input");
        html.setAtributo("type", "radio");
        html.setAtributo("id", forId);
        html.setAtributo("name", getNameOrDefault(), "");
        if (getSeleccionadoInicialmente()) {
            html.setAtributo("checked", true);
        }

        generarTabIndex(html);
        generarClasesCSS(html);
        generarEventosJavascript(html);

        html.cerrar("input");

        if (getLado() == PosicionHorizontal.DERECHA) {
            generarLabel(html, forId);
        }

        finalizarHtmlBase(html);
    }

}
