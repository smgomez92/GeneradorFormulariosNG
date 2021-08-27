package com.fitbank.webpages.assistants;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.fitbank.enums.DataSourceType;
import com.fitbank.js.GeneradorJS;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.data.FormElement;

    public class FloatButton implements Assistant {

    private static final long serialVersionUID = 1L;

    private FormElement formElement;

    @Editable(weight = 3)
    private String formula = "";

    @Editable(weight = 0)
    private boolean checkButton = false;
    @Editable(weight = 1)
    private boolean cancelButton = false;
    @Editable(weight = 2)
    private boolean printButton = false;

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public boolean isCheckButton() {
        return checkButton;
    }

    public void setCheckButton(boolean checkButton) {
        this.checkButton = checkButton;
    }
    public boolean isPrintButton() {
        return printButton;
    }

    public void setPrintButton(boolean printButton) {
        this.printButton = printButton;
    }


    public boolean isCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(boolean cancelButton) {
        this.cancelButton = cancelButton;
    }

    @Override
    public void init(FormElement formElement) {
        this.formElement = formElement;
    }

    @Override
    public String format(String valorSinFormato) {
        return formElement == null || StringUtils.isEmpty(valorSinFormato) ? ""
                : String.valueOf(valorSinFormato.hashCode());
    }

    @Override
    public String unformat(String valorFormateado) {
        return valorFormateado;
    }

    @Override
    public Object asObject(String value) {
        return Base64.decodeBase64(value);
    }

    @Override
    public boolean readFromHttpRequest() {
        return false;
    }

    @Override
    public boolean usesIcon() {
        return false;
    }

    @Override
    public Collection<DataSourceType> applyTo() {
        return Arrays.asList(new DataSourceType[]{
            DataSourceType.CRITERION_CONTROL,
            DataSourceType.CONTROL,
            DataSourceType.RECORD
        });
    }

    @XML(ignore = true)
    @Override
    public String getElementName() {
        return formElement == null ? "" : formElement.getNameOrDefault();
    }

    @Override
    public void generateHtml(ConstructorHtml html) {
        if (formElement != null && formElement.getVisible()) {
            WebPageEnviroment.addJavascriptInicial(GeneradorJS.toJS(this) + ";");
        }
    }

    @Override
    public String getType() {
        // Debe ser "text" por que en javascript se crea otro campo con name =
        // NAME_REGISTRO para que pueda ser procesada por el servlet
        return "text";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        if (formElement != null && formElement.getVisible()) {
            WebPageEnviroment.addJavascriptInicial(GeneradorJS.toJS(this) + ";");
        }
    }

}
