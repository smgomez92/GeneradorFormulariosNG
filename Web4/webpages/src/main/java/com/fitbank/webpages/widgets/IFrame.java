package com.fitbank.webpages.widgets;

import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.webpages.WebPageUtils;

public class IFrame extends Input {

    private static final long serialVersionUID = 1L;

    public IFrame() {
        properties.get("w").setValorPorDefecto(1000);
        properties.get("h").setValorPorDefecto(480);
    }

    @Override
    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();
        generarHtmlBase(html);

        html.agregar("input");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("name", getNameOrDefault());
        html.setAtributo("value", WebPageUtils.format(this, getValueInicial()));
        html.setAtributo("type", "hidden");
        html.extenderAtributo("onchange", String.format("Util.getContentWindow($('%s_iframe')).document.body.innerHTML = this.value;", getHTMLId()));

        generarClasesCSS(html);
        generarEventosJavascript(html);

        html.abrir("iframe");
        html.setAtributo("id", getHTMLId() + "_iframe");
        html.setAtributo("border", 0);
        html.setAtributo("frameborder", 0);
        html.setEstilo("width", getW(), "px");
        html.setEstilo("height", getH(), "px");

        generarClasesCSS(html);

        html.setTexto("xxx");

        html.cerrar("iframe");

        finalizarHtmlBase(html);
    }

}

