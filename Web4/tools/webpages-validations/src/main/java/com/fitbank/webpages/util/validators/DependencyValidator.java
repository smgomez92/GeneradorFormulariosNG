package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.fitbank.schemautils.Schema;
import com.fitbank.schemautils.Table;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebElement;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.util.ReferenceUtils;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;

public class DependencyValidator extends Validator {

    private static final String FIELD_INVALIDO = "FIELD_INVALIDO";

    private static final String TABLE_INVALIDO = "TABLE_INVALIDO";

    private static final String ALIAS_INVALIDO = "ALIAS_INVALIDO";

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        ReferenceUtils referenceUtils =
                new ReferenceUtils(fullWebPage.getReferences());

        for (Reference reference : webPage.getReferences()) {
            validate(messages, webPage, reference.getDependencies(), reference.
                    getTable(), referenceUtils);
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Container container,
            WebPage fullWebPage) {
        return Collections.emptyList();
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget,
            WebPage fullWebPage) {
        return Collections.emptyList();
    }

    protected void validate(Collection<ValidationMessage> messages,
            WebElement<?> webElement, Collection<Dependency> dependencies,
            String tableName, ReferenceUtils referenceUtils) {
        if (!Schema.get().getTables().containsKey(tableName)) {
            messages.add(new ValidationMessage(this, TABLE_INVALIDO,
                    webElement, webElement, ValidationMessage.Severity.WARN));

        } else {
            Table table = Schema.get().getTables().get(tableName);

            for (Dependency dependency : dependencies) {
                if (table.getFields().get(dependency.getField()) == null) {
                    messages.add(new ValidationMessage(this, FIELD_INVALIDO,
                            webElement, dependency,
                            ValidationMessage.Severity.WARN));
                    continue;
                }

                if (StringUtils.isNotBlank(dependency.getImmediateValue())
			|| StringUtils.isBlank(dependency.getFromAlias())) {
                    continue;
                }

                Reference reference =
                        referenceUtils.get(dependency.getFromAlias());

                if (reference == null) {
                    messages.add(new ValidationMessage(this, ALIAS_INVALIDO,
                            webElement, dependency,
                            ValidationMessage.Severity.WARN));
                    continue;
                }

                Table table2 = Schema.get().getTables().get(
                        reference.getTable());

                if (table2 == null) {
                    messages.add(new ValidationMessage(this, TABLE_INVALIDO,
                            webElement, dependency,
                            ValidationMessage.Severity.WARN));
                } else if (table2.getFields().get(dependency.getFromField())
                        == null) {
                    messages.add(new ValidationMessage(this, FIELD_INVALIDO,
                            webElement, dependency,
                            ValidationMessage.Severity.WARN));
                }
            }
        }
    }

}
