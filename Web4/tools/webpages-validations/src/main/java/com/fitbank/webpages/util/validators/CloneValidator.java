package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.fitbank.enums.DataSourceType;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.ArbolDependencias;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.NodoDependencia;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;

public class CloneValidator extends Validator {

    private static final String REFERENCIA_INEXISTENTE =
            "REFERENCIA_INEXISTENTE";

    private static final String ERROR_REFERENCIA = "ERROR_REFERENCIA";

    private static final String CLONACION_IMPAR = "CLONACION_IMPAR";

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage, WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        ArbolDependencias arbolDependencias = new ArbolDependencias(webPage.
                getReferences());
        Map<String, Integer> registros = new HashMap<String, Integer>();
        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            DataSource dataSource = formElement.getDataSource();

            if (dataSource.getType() != DataSourceType.RECORD) {
                continue;
            }

            NodoDependencia nodo = arbolDependencias.getNodos().get(
                    dataSource.getAlias());

            if (nodo == null) {
                messages.add(new ValidationMessage(this, REFERENCIA_INEXISTENTE,
                        (Widget) formElement, dataSource,
                        ValidationMessage.Severity.ERROR));
                return messages;
            }

            if (nodo.getPrincipal() == null) {
                messages.add(new ValidationMessage(this, ERROR_REFERENCIA,
                        (Widget) formElement, dataSource,
                        ValidationMessage.Severity.ERROR));
                return messages;
            }

            String aliasPrincipal = nodo.getPrincipal().getAlias();

            int clonacionMax = ((Widget) formElement).getParentContainer().
                    getClonacionMax();

            if (registros.containsKey(aliasPrincipal)
                    && registros.get(aliasPrincipal) != clonacionMax) {
                messages.add(new ValidationMessage(this, CLONACION_IMPAR,
                        webPage, webPage, ValidationMessage.Severity.ERROR));
            }

            registros.put(aliasPrincipal, clonacionMax);
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        return Collections.emptyList();
    }

}
