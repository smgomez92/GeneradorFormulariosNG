package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.LinkedList;

import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.util.ArbolDependencias;
import com.fitbank.webpages.util.NodoDependencia;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;

public class ReferenceValidator extends Validator {

    private static final String MULTIPLES_PRINCIPALES = "MULTIPLES_PRINCIPALES";

    private static final String REFERENCIA_AISLADA = "REFERENCIA_AISLADA";

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        ArbolDependencias arbolDependencias = new ArbolDependencias(
                fullWebPage.getReferences());

        // Encontrar nodos principales (sin dependencias)
        for (Reference reference : webPage.getReferences()) {
            String alias = reference.getAlias();
            NodoDependencia nodo = arbolDependencias.getNodos().get(alias);

            if (!nodo.getDependencias().isEmpty()) {
                continue;
            }

            if (!nodo.getPrincipal().getAlias().equals(alias)) {
                messages.add(new ValidationMessage(this, MULTIPLES_PRINCIPALES,
                        webPage, reference, ValidationMessage.Severity.ERROR));
            }
        }

        // Encontrar nodos que no tienen tabla principal
        for (Reference reference : webPage.getReferences()) {
            if (arbolDependencias.getNodos().get(reference.getAlias()).
                    getPrincipal() == null) {
                messages.add(new ValidationMessage(this, REFERENCIA_AISLADA,
                        webPage, reference, ValidationMessage.Severity.ERROR));
            }
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget,
            WebPage fullWebPage) {
        return null;
    }

}
