package com.fitbank.webpages.assistants;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fitbank.enums.DataSourceType;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.assistants.lov.LOVField;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.widgets.Input;

/**
 * Lista de valores especial. Genera una lista de valores usando los datos
 * proporcionados y llama una consulta especial.
 *
 * @author FitBank CI
 */
public class SpecialListOfValues implements Assistant {

    private static final long serialVersionUID = 1L;

    private FormElement formElement;

    @Editable(weight = 1)
    private String name = "";

    @Editable(weight = 2)
    private String tableName = "";

    @Editable(weight = 7)
    private String noDataMessage = "";

    @Editable(weight = 5)
    private boolean visible = true;

    @Editable(weight = 6)
    private boolean fireAlways = true;

    @Editable(weight = 3)
    private final Map<String, String> criterions = new HashMap<String, String>();

    @Editable(weight = 4)
    private final Map<String, String> fields = new HashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean getFireAlways() {
        return fireAlways;
    }

    public void setFireAlways(boolean fireAlways) {
        this.fireAlways = fireAlways;
    }

    public String getNoDataMessage() {
        return noDataMessage;
    }

    public void setNoDataMessage(String noDataMessage) {
        this.noDataMessage = noDataMessage;
    }

    public Map<String, String> getCriterions() {
        return criterions;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    @Override
    public void init(FormElement formElement) {
        this.formElement = formElement;
    }

    public String format(String valorSinFormato) {
        return valorSinFormato;
    }

    public String unformat(String valorFormateado) {
        return valorFormateado;
    }

    public Object asObject(String value) {
        return value;
    }

    public boolean readFromHttpRequest() {
        return true;
    }

    public boolean usesIcon() {
        return false;
    }

    public Collection<DataSourceType> applyTo() {
        return Arrays.asList(new DataSourceType[] {
                    DataSourceType.CRITERION_CONTROL,
                    DataSourceType.CONTROL,
                    DataSourceType.CRITERION,
                    DataSourceType.RECORD
                });
    }

    @XML(ignore = true)
    public String getElementName() {
        return formElement == null ? "" : formElement.getNameOrDefault();
    }

    public void generateHtml(ConstructorHtml html) {
        if (formElement == null || !formElement.getVisible()) {
            return;
        }

        ListOfValues listOfValues = generateListOfValues();

        if (listOfValues != null) {
            WebPageEnviroment.addJavascriptInicial(listOfValues.toJS() + ";");
        }
    }

    /**
     * Genera la lista de valores automáticamente.
     *
     * @return ListOfValues generada
     */
    public ListOfValues generateListOfValues() {
        String alias = "lov";

        ListOfValues listOfValues = new ListOfValues();

        listOfValues.init(formElement);

        listOfValues.setSubsystem("03");
        listOfValues.setTransaction("7003");
        listOfValues.setVisible(getVisible());
        listOfValues.setNoDataMessage(getNoDataMessage());

        listOfValues.getReferences().add(new Reference(alias, getTableName()));

        LOVField nameField = new LOVField();
        nameField.setField("NAME");
        nameField.setValue(getName());
        nameField.setType(DataSourceType.CRITERION_CONTROL);
        nameField.setVisible(false);

        listOfValues.getFields().add(nameField);

        // El proceso especial de estas listas en el core requiere que los
        // criterios se envíen como campos de control
        for (String fieldName : getCriterions().keySet()) {
            LOVField controlField = new LOVField();
            controlField.setAlias(alias);
            controlField.setField("cLov_" + fieldName);
            controlField.setElementName(getCriterions().get(fieldName));
            controlField.setType(DataSourceType.CRITERION_CONTROL);
            controlField.setVisible(false);
            controlField.setAutoQuery(true);
            controlField.setRequired(!getVisible() || !controlField.
                    getElementName().equals(getElementName()));
            controlField.setFireAlways(fireAlways);
            listOfValues.getFields().add(controlField);
        }

        for (String fieldName : getFields().keySet()) {
            LOVField field = new LOVField();
            field.setAlias(alias);
            field.setField(fieldName);
            field.setElementName(getFields().get(fieldName));
            field.setType(DataSourceType.RECORD);
            field.setVisible(true);
            listOfValues.getFields().add(field);
        }

        return listOfValues;
    }

    public String getType() {
        return "text";
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        if (formElement == null || !formElement.getVisible()) {
            return;
        }

        ListOfValues listOfValues = generateListOfValues();

        if (listOfValues != null) {
            WebPageEnviroment.addJavascriptInicial(listOfValues.toJS() + ";");
        }
    }

}
