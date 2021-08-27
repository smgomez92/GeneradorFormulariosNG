package com.fitbank.webpages.widgets;

import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.WebPageUtils;

/**
 * Clase que representa un Label que funciona como un Input.
 *
 * @author FitBank CI
 */
public class InputLabel extends Input {
    
    @Override
    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();
        
        generarHtmlBase(html);

        html.agregar("input");
        html.setAtributo("type", "hidden");
        html.setAtributo("id", getHTMLId() + "_oculto");
        html.setAtributo("labelid", getHTMLId());
        html.setAtributo("name", getNameOrDefault());
        html.setAtributo("registro", getParentContainer().getIndiceClonacionActual());
        html.extenderAtributo("onchange", "$('" + getHTMLId() + "').update(this.value);");

        html.agregar("span", WebPageUtils.format(this, getValueInicial()));
        html.setAtributo("id", getHTMLId());

        generarInputOculto("label");
        generarClasesCSS(html);
        generarEventosJavascript(html);
        generarHtmlGuia(html, getGuia());

        finalizarHtmlBase(html);
    }

    @Override
    protected void generarInputOculto(String suffix) {
        WebPageEnviroment.addJavascriptInicial(String.format(
                "Util.initHtmlElement('%s');", getHTMLId()));
    }

}
