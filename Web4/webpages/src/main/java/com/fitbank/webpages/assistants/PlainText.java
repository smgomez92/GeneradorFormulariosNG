package com.fitbank.webpages.assistants;


import com.fitbank.js.GeneradorJS;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.webpages.WebPageEnviroment;

/**
 * Asistente básico que se usa para texto plano. Hace lo mismo que el asistente
 * None.
 *
 * @author FitBank CI
 */
public class PlainText extends None {

    private static final long serialVersionUID = 1L;

    @Override
    public void generateHtml(ConstructorHtml html) {
        if (formElement == null || !formElement.getVisible()) {
            return;
        }

        if (!this.getClass().equals(PlainText.class)) {
            WebPageEnviroment
                    .addJavascriptInicial(GeneradorJS.toJS(this) + ";");
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
