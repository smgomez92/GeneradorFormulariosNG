package com.fitbank.webpages.definition.wizard;

import com.fitbank.enums.DataSourceType;
import com.fitbank.schemautils.DescriptionKey;
import com.fitbank.schemautils.Schema;
import com.fitbank.schemautils.Table;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.AutoListOfValues;
import com.fitbank.webpages.assistants.Calendar;
import com.fitbank.webpages.assistants.File;
import com.fitbank.webpages.assistants.LongText;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.definition.Field;
import com.fitbank.webpages.definition.Group;
import com.fitbank.webpages.definition.WebPageDefinition;
import com.fitbank.webpages.definition.group.CriteriaGroup;
import com.fitbank.webpages.definition.group.TableGroup;
import com.fitbank.webpages.formatters.DateFormatter;
import com.fitbank.webpages.formatters.DateFormatter.DateFormat;
import com.fitbank.webpages.formatters.NumberFormatter;
import com.fitbank.webpages.widgets.Input;
import org.apache.commons.lang.StringUtils;

/**
 * Genera un WebPageDefinition a partir de un WizardData
 *
 * @author FitBank CI
 */
public class WizardGenerator {

    public static void compile(WebPageDefinition wpd) {
        String tableName = wpd.getWizardData().getTableName();
        Table table = Schema.get().getTables().get(tableName);

        wpd.getReferences().clear();
        wpd.getReferences().add(new Reference(tableName.toLowerCase() + "1",
                tableName.toUpperCase()));

        wpd.getGroups().clear();

        Group criteria = new CriteriaGroup();
        for (WizardCriterion wizardCriterion : wpd.getWizardData().getCriteria()) {
            criteria.getFields().add(generate(wizardCriterion, table));
        }
        wpd.getGroups().add(criteria);

        Group fields = new TableGroup();
        for (WizardField wizardField : wpd.getWizardData().getFields()) {
            fields.getFields().add(generate(wizardField, table));
        }
        wpd.getGroups().add(fields);
    }

    private static Field generate(WizardField wizardField, Table table) {
        Field field = new Field();
        field.setLabel(wizardField.getTitle());

        com.fitbank.schemautils.Field sfield =
                table.getFields().get(wizardField.getName());

        FormElement formElement = getFormElement(wizardField, sfield, table);
        formElement.getDataSource().setType(wizardField instanceof WizardCriterion
                ? DataSourceType.CRITERION : DataSourceType.RECORD);
        field.getWidgets().add((Widget) formElement);

        Widget descripcion = getInputDescription(formElement, table, sfield);
        if (descripcion != null) {
            if (wizardField.getShowDescription()) {
                field.getWidgets().add(descripcion);
            }
            formElement.setAssistant(new AutoListOfValues());
        }

        return field;
    }

    private static FormElement getFormElement(WizardField wizardField,
            com.fitbank.schemautils.Field sfield, Table table) {
        Input input = new Input();

        if (sfield.getType().startsWith("VARCHAR")) {
            input.setLongitud(sfield.getLength());

            if (sfield.getLength() > 50) {
                input.setW(150);
                input.setAssistant(new LongText());
            } else {
                input.setW(sfield.getLength() * 10);
            }

        } else if (sfield.getType().startsWith("CHAR")) {
            input.setLongitud(sfield.getLength());

        } else if (sfield.getType().startsWith("NUMBER")) {
            NumberFormatter numberFormatter = new NumberFormatter();
            numberFormatter.setFormat(sfield.getNumberFormat());
            input.getBehaviors().add(numberFormatter);

        } else if (sfield.getType().startsWith("TIMESTAMP")) {
            DateFormatter dateFormatter = new DateFormatter();
            dateFormatter.setFormat(DateFormat.DATETIME);
            input.getBehaviors().add(dateFormatter);

        } else if (sfield.getType().startsWith("DATE")) {
            Calendar calendar = new Calendar();
            calendar.setFormat(DateFormat.DATE);
            input.setAssistant(calendar);

        } else if (sfield.getType().startsWith("BLOB")) {
            input.setAssistant(new File());

        } else if (sfield.getType().startsWith("CLOB")) {
            input.setAssistant(new File());

        }

        input.getDataSource().setAlias(table.getName().toLowerCase() + "1");
        input.getDataSource().setField(wizardField.getName());

        return input;
    }

    private static Input getInputDescription(FormElement formElement,
            Table table, com.fitbank.schemautils.Field sfield) {
        DescriptionKey dk = sfield.getDescriptionKey();

        if (dk == null) {
            return null;
        }

        String tableName = dk.getTable();

        if (StringUtils.isBlank(tableName)) {
            return null;
        }

        Input input = new Input();

        if (formElement.getDataSource().esCriterio()) {
            input.getDataSource().setType(DataSourceType.CRITERION_DESCRIPTION);
        } else {
            input.getDataSource().setType(DataSourceType.DESCRIPTION);
        }

        input.getDataSource().setAlias(tableName);
        input.getDataSource().setField(dk.getDescriptionField());

        for (String pkName : dk.getFields()) {
            String fromField = null;

            if (formElement.getDataSource().getField().startsWith(pkName)) {
                fromField = formElement.getDataSource().getField();
            } else {
                for (String fieldName : table.getFields().keySet()) {
                    if (fieldName.startsWith(pkName)) {
                        fromField = fieldName;
                        break;
                    }
                }
            }

            if (fromField == null || !formElement.getDataSource().getField().
                    startsWith("CIDIOMA") && pkName.equals("CIDIOMA")) {
                continue;
            }

            Dependency dependency = new Dependency();
            dependency.setField(pkName);
            dependency.setFromField(fromField);
            dependency.setFromAlias(formElement.getDataSource().getAlias());
            input.getDataSource().getDependencies().add(dependency);
        }

        return input;
    }

}
