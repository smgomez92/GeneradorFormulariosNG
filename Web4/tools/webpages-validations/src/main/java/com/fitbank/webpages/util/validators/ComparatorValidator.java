package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.fitbank.schemautils.Field;
import com.fitbank.schemautils.Schema;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebElement;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.ListOfValues;
import com.fitbank.webpages.assistants.lov.LOVField;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;

public class ComparatorValidator extends Validator {

    public static final String LOV_FIELD_IGUAL = "LOV_FIELD_IGUAL";

    private static final String CAMPO_PK_COMPARADOR_INVALIDO =
            "CAMPO_PK_COMPARADOR_INVALIDO";

    private static final String COMPARATOR_LIKE = "LIKE";

    private static final String COMPARATOR_IGUAL = "=";

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage, WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        this.verifyReferences(webPage, webPage.getReferences(), messages);

        for (Container container : webPage) {
            for (FormElement formElement : IterableWebElement.get(container,
                    FormElement.class)) {
                if (formElement.getAssistant() instanceof ListOfValues) {
                    ListOfValues lov = (ListOfValues) formElement.getAssistant();

                    this.verifyReferences((WebElement) formElement, lov.
                            getReferences(), messages);

                    for (final LOVField field : lov.getFields()) {
                        if (field.getComparator().equals(COMPARATOR_IGUAL)) {
                            messages.add(new ValidationMessage(this,
                                    LOV_FIELD_IGUAL, (WebElement) formElement,
                                    lov, true) {

                                @Override
                                public void fix() {
                                    field.setComparator(COMPARATOR_LIKE);
                                }

                            });
                        }
                    }
                }
            }
        }

        return messages;
    }

    private Collection<ValidationMessage> verifyReferences(WebElement webElement,
            Collection<Reference> references,
            Collection<ValidationMessage> messages) {
        for (Reference reference : references) {
            for (final Dependency dependency : reference.getDependencies()) {
                if (StringUtils.isBlank(dependency.getFromAlias())
                        && StringUtils.isBlank(dependency.getFromField())) {
                    continue;
                }

                if (!dependency.getComparator().equals(COMPARATOR_LIKE)) {
                    continue;
                }

                Field field = Schema.get().getField(reference.getAlias(),
                        dependency.getField());

                if (field != null && field.getPrimaryKey()) {
                    messages.add(new ValidationMessage(this,
                            CAMPO_PK_COMPARADOR_INVALIDO, webElement, dependency,
                            true) {

                        @Override
                        public void fix() {
                            dependency.setComparator(COMPARATOR_IGUAL);
                        }

                    });
                }
            }
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        return null;
    }

}
