package com.fitbank.webpages.assistants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

import com.fitbank.enums.DataSourceType;
import com.fitbank.js.GeneradorJS;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.XML;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.data.FormElement;

/**
 * Asistente usado como base para todos los asistentes. No efectúa ningun cambio
 * sobre el elemento en el que es usado.
 *
 * @author FitBank CI
 */
public class None implements Assistant, Serializable {

    private static final long serialVersionUID = 1L;

    protected FormElement formElement;

    @Override
    public void init(FormElement formElement) {
        this.formElement = formElement;
    }

    @Override
    public String format(String valorSinFormato) {
        return valorSinFormato;
    }

    @Override
    public String unformat(String valorFormateado) {
        return valorFormateado;
    }

    @Override
    public Object asObject(String value) {
        return value != null ? value.trim().replaceAll("\\s+", " ") : value;
    }

    @Override
    public boolean readFromHttpRequest() {
        return true;
    }

    @Override
    public boolean usesIcon() {
        return false;
    }

    @Override
    public Collection<DataSourceType> applyTo() {
        return Arrays.asList(DataSourceType.values());
    }

    @XML(ignore = true)
    @Override
    public String getElementName() {
        return formElement == null ? "" : formElement.getNameOrDefault();
    }

    @XML(ignore=true)
    public String getElementId() {
        return formElement == null ? "" : formElement.getHTMLId();
    }

    @Override
    public void generateHtml(ConstructorHtml html) {
        if (formElement == null || !formElement.getVisible()) {
            return;
        }

        if (!this.getClass().equals(None.class)) {
            WebPageEnviroment.addJavascriptInicial(GeneradorJS.toJS(this) + ";");
        }
    }

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public boolean equals(Object obj) {
        if (None.class.equals(getClass())) {
            return None.class.equals(obj.getClass());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        if (formElement == null || !formElement.getVisible()) {
            return;
        }

        if (!this.getClass().equals(None.class)) {
            WebPageEnviroment.addJavascriptInicial(GeneradorJS.toJS(this) + ";");
        }
    }

}
