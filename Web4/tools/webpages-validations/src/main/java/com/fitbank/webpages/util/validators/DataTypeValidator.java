package com.fitbank.webpages.util.validators;

import com.fitbank.enums.Modificable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import com.fitbank.enums.Requerido;
import com.fitbank.schemautils.Field;
import com.fitbank.schemautils.Schema;
import com.fitbank.schemautils.Table;
import com.fitbank.webpages.JSBehavior;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.formatters.NumberFormatter;
import com.fitbank.webpages.formatters.UpperCaseFormatter;
import com.fitbank.webpages.util.ArbolDependencias;
import com.fitbank.webpages.util.NodoDependencia;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;
import com.fitbank.webpages.widgets.Input;
import com.fitbank.webpages.widgets.TextArea;

/**
 * Revisa y arregla tipos de datos en los elementos
 *
 * @author FitBank CI
 */
public class DataTypeValidator extends Validator {

    private static final String VALOR_POR_DEFECTO = "VALOR_POR_DEFECTO";

    private static final String SIN_NUMBER_FORMATTER = "SIN_NUMBER_FORMATTER";

    public static final String LONGITUD_INCORRECTA = "LONGITUD_INCORRECTA";

    public static final String NO_REQUERIDO = "NO_REQUERIDO";

    private ArbolDependencias arbolDependencias;

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage, WebPage fullWebPage) {
        arbolDependencias = new ArbolDependencias(webPage.getReferences());

        return super.validate(webPage, null);
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        if (!(widget instanceof Input)) {
            return messages;
        }

        final Input input = (Input) widget;
        DataSource dataSource = input.getDataSource();

        String tableName = null;
        switch (dataSource.getType()) {
            case CRITERION_CONTROL:
            case CONTROL:
            case NONE:
            case ORDER:
            case REPORT:
                return Collections.EMPTY_LIST;

            case CRITERION:
            case RECORD:
                NodoDependencia nodo = arbolDependencias.getNodos().get(dataSource.getAlias());

                if (nodo == null || nodo.getPrincipal() == null) {
                    return Collections.EMPTY_LIST;
                }

                Reference reference = nodo.getPrincipal().getReference();

                if (reference.isSpecial()) {
                    return CollectionUtils.EMPTY_COLLECTION;
                }

                tableName = reference.getTable();
                break;

            case CRITERION_DESCRIPTION:
            case DESCRIPTION:
                tableName = dataSource.getAlias();
                break;
        }

        Table table = Schema.get().getTables().get(tableName);

        if (table == null) {
            return messages;
        }

        final Field field = table.getFields().get(dataSource.getField());

        if (field == null) {
            return messages;
        }

        if (field.getType().startsWith("NUMBER")) {
            final NumberFormatter numberFormatter = get(input.getBehaviors(),
                    NumberFormatter.class);

            if (numberFormatter == null) {
                messages.add(new ValidationMessage(this,
                        SIN_NUMBER_FORMATTER, widget, input, true) {

                    @Override
                    public void fix() {
                        NumberFormatter numberFormatter =
                                new NumberFormatter();
                        numberFormatter.setFormat(field.getNumberFormat());
                        input.getBehaviors().add(numberFormatter);
                        CollectionUtils.filter(input.getBehaviors(), new Predicate() {

                            public boolean evaluate(Object object) {
                                return !(object instanceof UpperCaseFormatter);
                            }

                        });
                    }

                });
            }

            if (input.getVisible() && checkLength(input, field) && input.getModificable()
                    == Modificable.MODIFICABLE) {
                messages.add(new ValidationMessage(this,
                        LONGITUD_INCORRECTA, widget, input, true) {

                    @Override
                    public void fix() {
                        input.setLongitud(field.getLength());
                    }

                });
            }

        } else if (field.getType().startsWith("VARCHAR2")
                || field.getType().startsWith("CHAR")) {
            if (input.getVisible() && (input.getClass().equals(Input.class)
                    || input.getClass().equals(TextArea.class))
                    && checkLength(input, field)) {
                messages.add(new ValidationMessage(this, LONGITUD_INCORRECTA,
                        widget, input, true) {

                    @Override
                    public void fix() {
                        input.setLongitud(field.getLength());
                    }

                });
            }
        }

        String def = field.getDefaultValue().replaceFirst("^\\'", "").
                replaceFirst("\\'$", "").trim();

        if (def.equals("Systimestamp")) {
            def = "=NOW()";
        }

        final String defaultValue = def;

        if (StringUtils.isBlank(input.getRelleno())
                && !input.getRelleno().equals(defaultValue)
                && !input.getRelleno().startsWith("=")) {
            messages.add(new ValidationMessage(this, VALOR_POR_DEFECTO,
                    widget, input, true) {

                @Override
                public void fix() {
                    input.setValueInicial(defaultValue);
                }

            });
        }

        // TODO revisar más a fondo requerido automático
        if (field.getRequired() && input.getRequerido()
                == Requerido.NO_REQUERIDO) {
            messages.add(new ValidationMessage(this, NO_REQUERIDO,
                    widget, input, true) {

                @Override
                public void fix() {
                    input.setRequerido(Requerido.AUTOMATICO);
                }

            });
        }

        return messages;
    }

    private boolean checkLength(final Input input, final Field field) {
        int len = input.getLongitud();

        if (field.getName().toUpperCase().matches("identificacion|ccuenta")) {
            return len == 0 || len > field.getLength();
        } else {
            return len == 0 || len != field.getLength();
        }
    }

    private <T> T get(Collection<JSBehavior> behaviors, final Class<T> aClass) {
        return (T) CollectionUtils.find(behaviors, new Predicate() {

            public boolean evaluate(Object object) {
                return aClass.isInstance(object);
            }

        });
    }

}
