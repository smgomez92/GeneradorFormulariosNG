package com.fitbank.webpages.assistants;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.fitbank.enums.DataSourceType;
import com.fitbank.schemautils.DescriptionKey;
import com.fitbank.schemautils.Field;
import com.fitbank.schemautils.Schema;
import com.fitbank.schemautils.Table;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Debug;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.lov.LOVField;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;

/**
 * Lista de valores automática. Genera una lista de valores usando los datos
 * proporcionados por el proyecto schema-utils
 *
 * @author FitBank CI
 */
public class AutoListOfValues implements Assistant {

    private static final long serialVersionUID = 1L;

    private FormElement formElement;

    @Editable(weight = 1)
    private boolean queryOnSuccess = false;

    @Editable(weight = 2)
    private String codeTitle = "Código";

    @Editable(weight = 3)
    private String descriptionTitle = "Descripción";

    @Editable(weight = 4)
    private String noDataMessage = "";

    @Override
    public void init(FormElement formElement) {
        this.formElement = formElement;
    }

    @Override
    public String format(String valorSinFormato) {
        return valorSinFormato;
    }

    @Override
    public String unformat(String valorFormateado) {
        return valorFormateado;
    }

    @Override
    public Object asObject(String value) {
        return value;
    }

    @Override
    public boolean readFromHttpRequest() {
        return true;
    }

    @Override
    public boolean usesIcon() {
        return true;
    }

    @Override
    public Collection<DataSourceType> applyTo() {
        return Arrays.asList(new DataSourceType[] {
                    DataSourceType.CRITERION_CONTROL,
                    DataSourceType.CONTROL,
                    DataSourceType.CRITERION,
                    DataSourceType.RECORD
                });
    }

    @XML(ignore = true)
    @Override
    public String getElementName() {
        return formElement == null ? "" : formElement.getNameOrDefault();
    }

    public boolean getQueryOnSuccess() {
        return queryOnSuccess;
    }

    public void setQueryOnSuccess(boolean queryOnSuccess) {
        this.queryOnSuccess = queryOnSuccess;
    }

    public String getCodeTitle() {
        return codeTitle;
    }

    public void setCodeTitle(String codeTitle) {
        this.codeTitle = codeTitle;
    }

    public String getDescriptionTitle() {
        return descriptionTitle;
    }

    public void setDescriptionTitle(String descriptionTitle) {
        this.descriptionTitle = descriptionTitle;
    }

    public String getNoDataMessage() {
        return noDataMessage;
    }

    public void setNoDataMessage(String noDataMessage) {
        this.noDataMessage = noDataMessage;
    }

    @Override
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
        ListOfValues listOfValues = generateBaseListOfValues();
        DataSource dataSource = formElement.getDataSource();
        FormElement lovFormElement = findFormElement();
        String keyFieldName = dataSource.getField();

        if (lovFormElement != null) {
            listOfValues.getReferences().get(0).setTable(lovFormElement.
                    getDataSource().getAlias());

            completeListOfValues(listOfValues, keyFieldName, lovFormElement.
                    getDataSource().getField(), lovFormElement.getNameOrDefault());
            addHidden(listOfValues,
                    lovFormElement.getDataSource().getDependencies());

        } else {
            String tableName = listOfValues.getReferences().get(0).getTable();

            Table table = Schema.get().getTables().get(tableName);
            if (table == null) {
                Debug.error("No se encontró la tabla " + tableName);
                return null;
            }

            Field field = table.getFields().get(keyFieldName);
            if (field == null) {
                Debug.error("No se encontró el campo " + keyFieldName
                        + " en la tabla " + tableName);
                return null;
            }

            DescriptionKey dk = field.getDescriptionKey();
            if (dk == null) {
                Debug.error("No se encontró una descripción para el campo "
                        + keyFieldName + " en la tabla " + tableName);
                return null;
            }

            String var = keyFieldName.contains("_") ? keyFieldName.substring(
                    keyFieldName.indexOf('_') + 1) : "";

            Collection<Dependency> dependencies = getDependencies(dk.getFields(),
                    var, dataSource.getAlias());

            listOfValues.getReferences().get(0).setTable(dk.getTable());

            completeListOfValues(listOfValues, keyFieldName, dk.
                    getDescriptionField(), "");
            addHidden(listOfValues, dependencies);
        }

        return listOfValues;
    }

    /**
     * Genera la lista de valores base.
     *
     * @return ListOfValues
     */
    private ListOfValues generateBaseListOfValues() {
        ListOfValues listOfValues = new ListOfValues();

        listOfValues.init(formElement);

        listOfValues.setQueryOnSuccess(getQueryOnSuccess());
        listOfValues.setNoDataMessage(getNoDataMessage());

        DataSource dataSource = formElement.getDataSource();
        Reference reference = new Reference(dataSource.getAlias(), "");
        for (Reference reference2 : ((Widget) formElement).getParentWebPage()
                .getReferences()) {
            if (reference2.getAlias().equals(dataSource.getAlias())) {
                reference.setTable(reference2.getTable());
            }
        }
        listOfValues.getReferences().add(reference);

        return listOfValues;
    }

    /**
     * Buscar elemento que se hace referencia en la misma fila
     * 
     * @return
     */
    private FormElement findFormElement() {
        for (Widget widget : ((Widget) formElement).getParentContainer()) {
            if (!(widget instanceof FormElement)) {
                continue;
            }

            FormElement formElement2 = (FormElement) widget;
            DataSourceType dst1 = formElement.getDataSource().getType();
            DataSourceType dst2 = formElement2.getDataSource().getType();

            if (dst1 == DataSourceType.CRITERION && dst2
                    != DataSourceType.CRITERION_DESCRIPTION) {
                continue;
            }
            if (dst1 == DataSourceType.RECORD && dst2
                    != DataSourceType.DESCRIPTION) {
                continue;
            }

            Dependency dependency = (Dependency) CollectionUtils.find(
                    formElement2.getDataSource().getDependencies(),
                    new Predicate() {

                        @Override
                        public boolean evaluate(Object object) {
                            return isMainDependency((Dependency) object);
                        }

                    });

            if (dependency != null) {
                return formElement2;
            }
        }

        return null;
    }

    /**
     * Convierte una lista de String en una lista de Dependency
     *
     * @param fields Lista de String
     * @param var Variación del pk (ej: "_abc")
     * @param fromAlias Alias desde el que se va a definir las dependencias
     *
     * @return Lista de Dependency
     */
    private Collection<Dependency> getDependencies(List<String> fields,
            String var, String fromAlias) {
        Collection<Dependency> dependencies = new LinkedList<Dependency>();

        for (String field : fields) {
            Dependency dependency = new Dependency();

            dependency.setField(field);
            dependency.setFromField(field + var);
            dependency.setFromAlias(fromAlias);

            dependencies.add(dependency);
        }

        return dependencies;
    }

    /**
     * Define si una dependencia es la dependencia principal
     *
     * @param dependency Dependencia a ser probada
     *
     * @return true si es la dependencia principal
     */
    private boolean isMainDependency(Dependency dependency) {
        return dependency.getFromAlias().equals(formElement.getDataSource().getAlias())
                && dependency.getFromField().equals(formElement.getDataSource().
                getField());
    }

    /**
     * Completa la lista de valores con los datos indicados.
     *
     * @param listOfValues ListOfValues a ser modificada
     * @param field1Name Nombre del primer campo
     * @param field2Name Nombre del segundo campo
     * @param field2ElementName Nombre del elemento del segundo campo
     */
    private void completeListOfValues(ListOfValues listOfValues,
            String field1Name, String field2Name, String field2ElementName) {
        // Crear field 1 con la información indicada
        LOVField field1 = new LOVField(formElement.getDataSource());
        field1.setType(DataSourceType.RECORD);
        field1.setField(Schema.baseKey(field1Name));
        field1.setElementName(formElement.getNameOrDefault());
        field1.setW(((Widget) formElement).getW());
        field1.setAutoQuery(true);
        field1.setTitle(getCodeTitle());
        listOfValues.getFields().add(field1);

        // Crear field 2 con la información indicada
        LOVField field2 = new LOVField(formElement.getDataSource());
        field2.setType(DataSourceType.RECORD);
        field2.setField(field2Name);
        field2.setElementName(field2ElementName);
        field2.setTitle(getDescriptionTitle());
        listOfValues.getFields().add(field2);

    }

    /**
     * Agregar demas campos ocultos que hacen referencia a elementos en otras filas
     *
     * @param listOfValues ListOfValues a ser modificada
     * @param dependencies Lista de Dependencias
     */
    public void addHidden(ListOfValues listOfValues,
            Collection<Dependency> dependencies) {
        for (Dependency dependency : dependencies) {
            if (isMainDependency(dependency)) {
                continue;
            }

            LOVField depField = new LOVField(formElement.getDataSource());

            depField.setField(dependency.getFromField());
            depField.setVisible(false);
            depField.setRequired(true);
            depField.setType(DataSourceType.CRITERION);

            FormElement formElement2 = ((Widget) formElement).getParentWebPage().
                    findFormElement(depField);

            if (formElement2 == null) {
                Debug.warn("No se encontró un widget para: " + depField);
                continue;
            } else {
                depField.setElementName(formElement2.getNameOrDefault());
            }

            depField.setField(dependency.getField());

            listOfValues.getFields().add(depField);
        }
    }

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
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
