package com.fitbank.webpages.assistants;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.fitbank.enums.DataSourceType;
import com.fitbank.serializador.html.ConstructorHtml;

/**
 * Asistente para campos tipo orden.
 *
 * @author FitBank
 */
public class Order extends PlainText {

    private static final long serialVersionUID = 1L;

    private String aliasPrincipal = "";

    @Override
    public String format(String valorSinFormato) {
        return StringUtils.isBlank(valorSinFormato) ? "0" : valorSinFormato;
    }

    @Override
    public boolean usesIcon() {
        return true;
    }

    @Override
    public Collection<DataSourceType> applyTo() {
        return Arrays.asList(new DataSourceType[]{
                    DataSourceType.ORDER,
                });
    }

    @Override
    public void generateHtml(ConstructorHtml html) {
        this.setAliasPrincipal("");
        super.generateHtml(html);
    }

    public String getAliasPrincipal() {
        return aliasPrincipal;
    }

    public void setAliasPrincipal(String aliasPrincipal) {
        this.aliasPrincipal = aliasPrincipal;
    }

}
