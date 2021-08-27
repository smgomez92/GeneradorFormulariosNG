package com.fitbank.webpages.util.validators;

import com.fitbank.enums.Modificable;
import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

import com.fitbank.schemautils.Schema;
import com.fitbank.schemautils.Table;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.ReferenceUtils;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;

/**
 * Valida que todos los alias usados sean correctos.
 * 
 * @author FitBank CI
 */
public class DataSourceValidator extends Validator {

    private static final String DATASOURCE_NO_FORMELEMENT =
            "DATASOURCE_NO_FORMELEMENT";

    private static final String FIELD_INVALIDO = "FIELD_INVALIDO";

    private static final String TIENE_DEPENDENCIES = "TIENE_DEPENDENCIES";

    private static final String CONTROL_CON_ALIAS = "CONTROL_CON_ALIAS";

    private static final String TABLA_INVALIDA = "TABLA_INVALIDA";

    private static final String ALIAS_INVALIDO = "ALIAS_INVALIDO";

    private static final String CRITERIO_CONTROL_CLONADO =
            "CRITERIO_CONTROL_CLONADO";

    private static final String DESCRIPCION_NO_DESABILITADO = "DESCRIPCION_NO_DESABILITADO";

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        ReferenceUtils referenceUtils = new ReferenceUtils(fullWebPage.
                getReferences());

        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            validate(messages, referenceUtils, formElement);
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Container container,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        ReferenceUtils referenceUtils =
                new ReferenceUtils(fullWebPage.getReferences());

        for (FormElement formElement : IterableWebElement.get(container,
                FormElement.class)) {
            validate(messages, referenceUtils, formElement);
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(final Widget widget,
            WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        ReferenceUtils referenceUtils =
                new ReferenceUtils(fullWebPage.getReferences());

        if (widget instanceof FormElement) {
            validate(messages, referenceUtils, (FormElement) widget);
        } else if (!widget.getDataSource().estaVacio()) {
            messages.add(new ValidationMessage(this, DATASOURCE_NO_FORMELEMENT,
                    widget, widget, true) {

                @Override
                public void fix() {
                    widget.setDataSource(new DataSource());
                }

            });
        }

        return messages;
    }

    private void validate(Collection<ValidationMessage> messages,
            ReferenceUtils referenceUtils, final FormElement formElement) {
        final DataSource dataSource = formElement.getDataSource();

        switch (dataSource.getType()) {
            case CRITERION:
            case ORDER:
            case RECORD:
                final Reference reference = referenceUtils.get(dataSource.
                        getAlias());

                if (reference == null) {
                    messages.add(new ValidationMessage(this, ALIAS_INVALIDO,
                            (Widget) formElement, dataSource,
                            ValidationMessage.Severity.ERROR));

                } else if (!dataSource.getDependencies().isEmpty()) {
                    messages.add(new ValidationMessage(this, TIENE_DEPENDENCIES,
                            (Widget) formElement, dataSource, true) {

                        @Override
                        public void fix() {
                            dataSource.getDependencies().clear();
                        }

                    });

                } else if (!reference.isSpecial()) {
                    Table table = Schema.get().getTables().get(reference.
                            getTable());
                    if (table == null) {
                        messages.add(new ValidationMessage(this, TABLA_INVALIDA,
                                (Widget) formElement, dataSource,
                                ValidationMessage.Severity.WARN));
                    } else if (!table.getFields().containsKey(dataSource.
                            getField())) {
                        messages.add(new ValidationMessage(this, FIELD_INVALIDO,
                                (Widget) formElement, dataSource,
                                ValidationMessage.Severity.WARN));
                    }
                }

                break;

            case CRITERION_DESCRIPTION:
            case DESCRIPTION:
                if (!Schema.get().getTables().containsKey(dataSource.getAlias())) {
                    messages.add(new ValidationMessage(this, TABLA_INVALIDA,
                            (Widget) formElement, dataSource,
                            ValidationMessage.Severity.WARN));
                } else {
                    new DependencyValidator().validate(messages,
                            (Widget) formElement, dataSource.getDependencies(),
                            dataSource.getAlias(), referenceUtils);

                    if (!Schema.get().getTables().get(dataSource.getAlias()).
                            getFields().containsKey(dataSource.getField())) {
                        messages.add(new ValidationMessage(this, FIELD_INVALIDO,
                                (Widget) formElement, dataSource,
                                ValidationMessage.Severity.WARN));
                    }
                }

                if (!formElement.getModificable().equals(Modificable.SOLO_LECTURA)) {
                    messages.add(new ValidationMessage(this,
                            DESCRIPCION_NO_DESABILITADO, (Widget) formElement, formElement, true) {

                        @Override
                        public void fix() {
                            formElement.setModificable(Modificable.SOLO_LECTURA);
                        }

                    });
                }

                break;

            case CRITERION_CONTROL:
            case CONTROL:
                if (StringUtils.isNotBlank(dataSource.getAlias())) {
                    messages.add(new ValidationMessage(this, CONTROL_CON_ALIAS,
                            (Widget) formElement, dataSource, true) {

                        @Override
                        public void fix() {
                            dataSource.setAlias("");
                        }

                    });
                }
                break;
        }

        Widget widget = (Widget) formElement;
        Container container = widget.getParentContainer();

        if (!dataSource.estaVacio() && !dataSource.esRegistro()
                && widget.getPosicion() > container.indexOfHeaderSeparator()
                && widget.getPosicion() < container.indexOfFooterSeparator()
                && container.getNumeroFilasClonadasMantenimiento() > 1) {
            messages.add(new ValidationMessage(this, CRITERIO_CONTROL_CLONADO,
                    (Widget) formElement, dataSource,
                    ValidationMessage.Severity.ERROR));
        }

    }

}
