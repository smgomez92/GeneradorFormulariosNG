package com.fitbank.web.uci;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.fitbank.common.KnownCommonFields;
import com.fitbank.common.crypto.Decrypt;
import com.fitbank.dto.management.Criterion;
import com.fitbank.dto.management.CriterionType;
import com.fitbank.dto.management.Dependence;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.DetailField;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.FieldType;
import com.fitbank.dto.management.Join;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.DependencyType;
import com.fitbank.enums.Requerido;
import com.fitbank.util.Debug;
import com.fitbank.web.Contexto;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.exceptions.MensajeWeb;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.Password;
import com.fitbank.enums.DataSourceType;
import com.fitbank.enums.TipoFila;
import com.fitbank.js.GeneradorJS;
import com.fitbank.util.Pair;
import com.fitbank.web.ManejoExcepcion;
import com.fitbank.web.uci.providers.UCIWebPageProvider;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.JSBehavior;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.FieldData;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.formatters.DateFormatter;
import com.fitbank.webpages.formatters.NumberFormatter;
import com.fitbank.webpages.formatters.UpperCaseFormatter;
import com.fitbank.webpages.util.ArbolDependencias;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.NodoDependencia;
import com.fitbank.webpages.widgets.CheckBox;
import com.fitbank.webpages.widgets.ComboBox;
import com.fitbank.webpages.widgets.DeleteRecord;
import com.fitbank.webpages.widgets.FooterSeparator;
import com.fitbank.webpages.widgets.HeaderSeparator;
import com.fitbank.webpages.widgets.Input;
import com.fitbank.webpages.widgets.Label;
import java.util.Arrays;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Clase que se encarga de convertir los valores entre WebPage y el Detail.
 * 
 * @author FitBank CI
 */
public class Conversor {

    public static final String LETTERS_NUMBERS_REGEX = "[^a-zA-Z0-9]";

    private static final String PUNTO = ".";

    private static final Pattern COMPARATOR_PLACEHOLDER = Pattern.compile("\\{([^\\}]+)\\}");

    public static final String JOIN_PROCESS_TYPE = "Join";

    public static final String LEGACY_PROCESS_TYPE = "Legacy";

    public static final String EMPTY_CRITERIA_PLACEHOLDER = "{EMPTY_CRITERIA}";

    /**
     * Revisa que el codigo de respuesta sea positivo o de error
     *
     * @param detail
     * @param respuesta
     * 
     * @throws MensajeWeb
     * @deprecated Use ManejoExcepcion.checkOkCodes
     */
    @Deprecated
    public static void checkOkCodes(Detail detail, RespuestaWeb respuesta) throws MensajeWeb {
        ManejoExcepcion.checkOkCodes(respuesta);
    }

    /**
     * Crea las tablas para un pedido si no existen.
     * 
     * @param webPage
     *            WebPage donde se leen las referencias
     * @param detail
     *            Detail en el que se van a crear
     */
    public static void crearTablas(WebPage webPage, Detail detail) {
        ArbolDependencias arbolDependencias = EntornoWeb.getContexto().
                getArbolDependencias();

        if (webPage.getLegacy()) {
            detail.setProcessType(LEGACY_PROCESS_TYPE);
        } else {
            detail.setProcessType(JOIN_PROCESS_TYPE);
        }

        if (webPage.getJoinQuirk()) {
            detail.findFieldByNameCreate("__JOIN_QUIRK__").setValue("true");
        }

        for (NodoDependencia nodo : arbolDependencias.getPrincipales()) {
            Reference reference = nodo.getReference();

            Table table = detail.findTableByExample(new Table(
                    reference.getTable(), reference.getAlias()));
            table.setPageNumber(EntornoWeb.getContexto().getPaginacion().
                    getNumeroDePagina(reference.getAlias()));
            table.setCurrentRecord(EntornoWeb.getContexto().getPaginacion().
                    getRegistroActivo(reference.getAlias()));
            table.setReadonly(reference.isSpecial());
            // se usa ibloque pero se refiere a Distinct para cores viejos
            table.setIBloque(reference.isDistinct() ? 1 : 0);
            table.setDistinct(reference.isDistinct());
            if (table.getName().equalsIgnoreCase("FINANCIERO")) {
                table.setFinancial(true);
                table.setReadonly(true);
            }

            table.findRecordByExample(new Record(0));

            if (!webPage.getLegacy()) {
                table.getJoins().clear();
                for (NodoDependencia nodoDependencia : arbolDependencias.
                        getNodosConectados(nodo)) {
                    if (nodoDependencia == nodo) {
                        continue;
                    }

                    Reference referenceDependencia = nodoDependencia.
                            getReference();
                    table.addJoin(Conversor.getJoin(referenceDependencia, webPage, detail));
                }

                table.getDependencies().clear();
                for (Dependency dependency : reference.getDependencies()) {
                    if (dependency.getType() == DependencyType.DEFERRED) {
                        table.addDependence(getDependence(dependency, reference.
                                getAlias()));
                    }
                }
            }
        }
    }

    /**
     * Obtiene un form element dado un Field
     *
     * @param criterion
     *            Criterion del detail
     * @param webPage
     *            Webpage donde buscar el elemento
     * @param table
     *            Table donde está el criterion
     *
     * @return FormElement o null
     */
    public static FormElement getFormElement(WebPage webPage, Table table, Criterion criterion) {
        DataSource dataSource = new DataSource();

        dataSource.setAlias(getLegacyAlias(webPage, table, criterion));
        dataSource.setField(getLegacyFieldName(webPage, criterion));
        dataSource.setComparator(criterion.getCondition());

        if (ArbolDependencias.contains(webPage.getReferences(),
                dataSource.getAlias())) {
            dataSource.setType(DataSourceType.CRITERION);
        } else {
            for (Join join : table.getJoins()) {
                if (dataSource.getAlias().equals(join.getAlias())) {
                    dataSource.setType(DataSourceType.CRITERION_DESCRIPTION);
                    dataSource.setAlias(join.getName());
                    break;
                }
            }
        }

        return webPage.findFormElement(dataSource);
    }

    /**
     * Obtiene un form element dado un Field
     *
     * @param field
     *            Field del detail
     * @param table
     *            Tabla donde están los fields
     * @param webPage
     *            Webpage donde buscar el elemento
     *
     * @return FormElement o null
     */
    public static Collection<FormElement> getFormElements(final WebPage webPage,
            Table table, Field field) {
        DataSource dataSource = new DataSource() {

            @Override
            public Collection<Dependency> getDependencies() {
                return webPage.getLegacy() ? null : super.getDependencies();
            }

        };

        dataSource.setAlias(getLegacyAlias(webPage, table, field));
        dataSource.setField(getLegacyFieldName(webPage, field));
        dataSource.setComparator(null);

        switch (field.getType()) {
            case AGGREGATE:
                dataSource.setType(DataSourceType.AGGREGATE);
                dataSource.setFunctionName(field.getFunctionName());
                break;
            
            case NORMAL:
                if (table == null) {
                    dataSource.setType(DataSourceType.CONTROL);
                } else {
                    dataSource.setType(DataSourceType.RECORD);
                }
                break;

            case INNER_SELECT:
                dataSource.setType(DataSourceType.DESCRIPTION);
                if (!webPage.getLegacy()) {
                    for (Dependence dependence : field.getDependencies()) {
                        dataSource.getDependencies().add(getDependency(dependence));
                    }
                }
                break;
        }

        String legacyVariant = getLegacyVariant(webPage, field);
        Collection<FormElement> formElements = webPage.findFormElementsIgnoreNull(
                dataSource, legacyVariant, table == null ? null : table.getAlias());

        if (formElements.isEmpty() && StringUtils.isNotBlank(field.getHint())) {
            // Para CRITERION_DESCRIPTION
            FormElement formElement = (FormElement) webPage.findWidget(field.
                    getHint());
            if (formElement != null) {
                formElements.add(formElement);
            }
        }

        if (formElements.isEmpty() && webPage.getLegacy()) {
            // Para campos de joins y descripciones en modo compatibilidad con fitbank 2
            if (table != null && ArbolDependencias.contains(webPage.getReferences(), table.getAlias())) {
                ArbolDependencias arbolDependencias = EntornoWeb.getContexto().getArbolDependencias();
                Collection<NodoDependencia> nodos =
                        arbolDependencias.getNodosConectados(table.getAlias());

                for (NodoDependencia nodo : nodos) {
                    Reference join = nodo.getReference();
                    // FIXME revisar aquí legacyVariant en las dependencias del join
                    if (join.getTable().equals(dataSource.getAlias())) {
                        dataSource.setAlias(join.getAlias());
                        dataSource.setType(DataSourceType.RECORD);
                        formElements = webPage.findFormElementsIgnoreNull(dataSource,
                                null, table.getAlias());
                        break;
                    }
                }

                if (formElements.isEmpty()) {
                    dataSource.setType(DataSourceType.DESCRIPTION);
                    formElements = webPage.findFormElementsIgnoreNull(dataSource,
                            legacyVariant, (String[]) CollectionUtils.collect(nodos,
                            new Transformer() {

                                @Override
                                public Object transform(Object input) {
                                    return ((NodoDependencia) input).getAlias();
                                }

                            }).toArray(new String[0]));
                }
            }
        }

        return formElements;
    }


    /**
     * Obtiene un form element dado un Field de control
     *
     * @param field
     *            Field del detail
     * @param webPage
     *            Webpage donde buscar el elemento
     *
     * @return FormElement o null
     */
    public static Collection<FormElement> getFormElements(final WebPage webPage, Field field) {
        DataSource dataSource = new DataSource();

        dataSource.setAlias(null);
        dataSource.setField(field.getName());
        dataSource.setComparator(null);

        Collection<FormElement> formElements = new LinkedList<FormElement>();

        dataSource.setType(DataSourceType.CRITERION_CONTROL);
        formElements.addAll(webPage.findFormElementsIgnoreNull(dataSource));

        dataSource.setType(DataSourceType.CONTROL);
        formElements.addAll(webPage.findFormElementsIgnoreNull(dataSource));

        return formElements;
    }

    /**
     * Obtiene el Table del Detail dado el FormElement
     * 
     * @param detail
     *            Detail donde buscar el table
     * @param formElement
     *            FormElement con los valores
     * 
     * @return Table o null si no se encontró o el campo es de tipo CONTROL
     */
    public static Table getTable(Detail detail, FormElement formElement) {
        switch (formElement.getDataSource().getType()) {
            case CRITERION:
            case ORDER:
            case RECORD:
            case AGGREGATE:
                return getTable(detail, formElement.getDataSource().getAlias());

            case CRITERION_DESCRIPTION:
                Table tableDes =
                        new Table(formElement.getDataSource().getAlias(),
                        formElement.getHTMLId().replaceAll(LETTERS_NUMBERS_REGEX, ""));
                return detail.findTableByExample(tableDes);

            case DESCRIPTION:
                Table table = null;
                for (Dependency dependency : formElement.getDataSource().
                        getDependencies()) {
                    if (StringUtils.isBlank(dependency.getFromAlias())) {
                        continue;
                    }
                    Table newTable = getTable(detail, dependency.getFromAlias());
                    if (table == null
                            || table.getAlias().equals(newTable.getAlias())) {
                        table = newTable;
                    } else {
                        throw new ErrorWeb("Dependency de descripción a más de "
                                + "una tabla: " + formElement.getDataSource());
                    }
                }
                return table;

            case CRITERION_CONTROL:
            case CONTROL:
            case REPORT:
            default:
                return null;
        }
    }

    /**
     * Obtiene el Table del Detail dado el alias. Primero busca una tabla
     * directamente en el detail y si no encuentra entonces usa el
     * ArbolDependencias para buscar la tabla principal para ese alias.
     * 
     * @param detail
     *            Detail donde buscar el table
     * @param alias
     *            Alias a buscar.
     * 
     * @return Table o null si no se encontró
     */
    public static Table getTable(Detail detail, String alias) {
        Table table = detail.findTableByAlias(alias);

        if (table != null) {
            return table;
        }

        Contexto contexto = EntornoWeb.getContexto();
        ArbolDependencias arbolDependencias = contexto.getArbolDependencias();
        NodoDependencia nodo = arbolDependencias.getNodos().get(alias);

        if (nodo == null) {
            return null;
        }

        NodoDependencia principal = nodo.getPrincipal();

        return detail.findTableByAlias(principal.getAlias());
    }

    /**
     * Crea un Field o Criterion adecuado en el Detail dado el FormElement para
     * consulta.
     * 
     * @param webPage
     *            WebPage de donde se encuentran el FormElement
     * @param formElement
     *            FormElement con los valores
     * @param detail
     *            Detail donde agregar el Field o Criterion
     */
    public static void convertirFormElementConsulta(WebPage webPage,
            FormElement formElement, Detail detail) {
        Table table = getTable(detail, formElement);

        DataSource dataSource = formElement.getDataSource();

        if (table == null && !dataSource.esControl()) {
            return;
        }

        String alias = webPage.getLegacy() ? null : dataSource.getAlias();
        String value = formElement.getFieldData().getValue(0);

        switch (dataSource.getType()) {
            case CRITERION:
                Criterion criterion = new Criterion(alias,
                        getLegacyFieldName(webPage, dataSource),
                        formElement.getAssistant().asObject(value));
                criterion.setCondition(filterCondition(dataSource.getComparator(),
                        webPage));
                table.addCriterion(criterion);
                break;

            case ORDER:
                int orden = 0;
                try {
                    if (StringUtils.isNotBlank(value)) {
                        orden = Integer.parseInt(value);
                    }
                } catch (NumberFormatException e) {
                    throw new MensajeWeb("Error en un criterio tipo Orden.", e);
                }

                Criterion order = new Criterion(alias,
                        getLegacyFieldName(webPage, dataSource), null);
                order.setType(CriterionType.ORDER);
                order.setOrder(orden);
                table.addCriterion(order);
                break;

            case CRITERION_CONTROL:
            case CONTROL:
                Field control = detail.findFieldByExample(new Field(dataSource.
                        getField(), value));

                control.setValue(formElement.getAssistant().asObject(value));
                break;

            case RECORD:
            case DESCRIPTION:
            case CRITERION_DESCRIPTION:
                DetailField field = createDetailField(webPage, formElement,
                        detail, table, 0);

                if (field == null && dataSource.getType()
                        == DataSourceType.CRITERION_DESCRIPTION) {
                    detail.removeTable(table.getAlias());
                } else {
                    field.setValue(null);
                    table.setRequestedRecords(formElement.getRegistrosConsulta());
                }
                break;

            case AGGREGATE:
                DetailField field2 = createDetailField(webPage, formElement,
                        detail, table, 0);

                field2.setValue(null);
                break;

            case REPORT:
                // No se envian en consulta
                break;
        }
    }

    /**
     * Reemplaza todos las ocurrencias de {NAME} por los valores del campo
     * referenciado en la forma ('a', 'b', 'c') en el comparador
     *
     * @param comparator Comparador
     * @param webPage WebPage
     *
     * @return El comparador filtrado
     */
    public static String filterCondition(String comparator, WebPage webPage) {
        final Matcher matcher = COMPARATOR_PLACEHOLDER.matcher(comparator);

        if (matcher.find()) {
            StringBuffer sb = new StringBuffer();

            matcher.reset();
            while (matcher.find()) {
                String name = matcher.group(1);
                FormElement formElement = webPage.findFormElement(name);

                if (formElement == null) {
                    throw new ErrorWeb("No se encontró el elemento con nombre "
                            + name);
                }

                List<String> values = new LinkedList<String>(formElement.getFieldData().getValues());

                CollectionUtils.filter(values, new Predicate() {

                    public boolean evaluate(Object object) {
                        return StringUtils.isNotBlank((String) object);
                    }

                });

                CollectionUtils.transform(values, new Transformer() {

                    public Object transform(Object input) {
                        return StringEscapeUtils.escapeSql((String) input);
                    }

                });

                String replacement = values.isEmpty() ?
                        "(" + EMPTY_CRITERIA_PLACEHOLDER + ")" :
                        "('" + StringUtils.join(values, "','") + "')";

                matcher.appendReplacement(sb, replacement);
            }

            matcher.appendTail(sb);

            comparator = sb.toString();
        }

        return comparator;
    }

    /**
     * Crea un Field o Criterion adecuado en el Detail dado el FormElement para
     * mantenimiento.
     * 
     * @param webPage
     *            WebPage donde se encuentra el FormElement
     * @param formElement
     *            FormElement con los valores
     * @param detail
     *            Detail donde agregar el Field o Criterion
     * @param registro
     *            Numero de registro del dato
     */
    public static void convertirFormElementMantenimiento(WebPage webPage,
            FormElement formElement, Detail detail, int registro) {
        Object object;
        Table table = getTable(detail, formElement);

        DataSource dataSource = formElement.getDataSource();

        if (table == null && !dataSource.esControl()) {
            return;
        }

        switch (dataSource.getType()) {
            case CRITERION:
                String alias = webPage.getLegacy() ? null : table.getAlias();
                String name = getLegacyFieldName(webPage, dataSource);

                object = formElement.getAssistant().asObject(formElement.
                        getFieldData().getValue(0));

                Criterion criterion = table.findCriterionByExample(
                        new Criterion(alias, name, null));

                criterion.setValue(object);
                break;

            case ORDER:
                // No se envian ORDER en mantenimiento
                break;

            case CRITERION_CONTROL:
            case CONTROL:
                String value = formElement.getFieldData().getValue(0);

                if (formElement.getAssistant() instanceof Password) {
                    try {
                        value = new Decrypt().encrypt(value);
                    } catch (Exception e) {
                        // Excepcion desconocida
                        throw new ErrorWeb(e);
                    }
                }

                object = formElement.getAssistant().asObject(value);

                Field field = detail.findFieldByExample(new Field(null,
                        dataSource.getField(), object));

                field.setValue(object);
                break;

            case DESCRIPTION:
                if (!webPage.getLegacy()) {
                    // No se envian *DESCRIPTION en mantenimiento si no es legacy
                    break;
                }
                // continua abajo
            case RECORD:
                createDetailField(webPage, formElement, detail, table, registro);
                break;

            case CRITERION_DESCRIPTION:
                if (table != null) {
                    detail.removeTable(table.getAlias());
                }
                break;

            case REPORT:
            case AGGREGATE:
                // No se envian en mantenimiento
                break;
        }
    }

    /**
     * Crea los campos de control adecuados en el Detail dado el FormElement para
     * reportes.
     *
     * @param formElement
     *            FormElement con los valores
     * @param detail
     *            Detail donde agregar el campo de control
     */
    public static void convertirFormElementReporte(FormElement formElement,
            Detail detail) {
        DataSource dataSource = formElement.getDataSource();
        Field field;

        switch (dataSource.getType()) {
            case CRITERION:
                field = detail.findFieldByExample(new Field(formElement.
                        getNameOrDefault(), null));
                field.setValue(formElement.getAssistant().asObject(formElement.
                        getFieldData().getValue(0)));
                break;

            case ORDER:
            case RECORD:
            case DESCRIPTION:
            case CRITERION_DESCRIPTION:
            case AGGREGATE:
                // No se envian en reportes
                break;

            case CRITERION_CONTROL:
            case CONTROL:
                field = detail.findFieldByExample(new Field(
                        dataSource.getField(), null));
                field.setValue(formElement.getAssistant().asObject(formElement.
                        getFieldData().getValue(0)));
                break;

            case REPORT:
                field = detail.findFieldByExample(new Field("R_" + dataSource.
                        getField(), null));
                field.setValue(formElement.getAssistant().asObject(formElement.
                        getFieldData().getValue(0)));
                break;
        }
    }

    /**
     * Crea un Field o Criterion adecuado en el Detail dado el FormElement para
     * mantenimiento para caducar el registro indicado.
     *
     * @param webPage
     *            WebPage donde se encuentra el DeleteRecord
     * @param deleteRecord
     *            DeleteRecord con los valores
     * @param detail
     *            Detail donde agregar el Field o Criterion
     * @param registro
     *            Numero de registro del dato
     */
    public static void convertirDeleteRecordMantenimiento(WebPage webPage,
            DeleteRecord deleteRecord, Detail detail, int registro) {
        if (!deleteRecord.delete(registro)) {
            return;
        }

        for (String alias : deleteRecord.getAliasList()) {
            Table table = getTable(detail, alias);
            
            if (table == null) {
                 throw new ErrorWeb("Referencia inexistente usada en DeleteRecord");
            }
            
            Record record = table.findRecordByNumber(registro);

            Field field;
            if (webPage.getLegacy()) {
                field = record.findFieldByExample(new Field(
                        KnownCommonFields.VERSIONCONTROL.getFieldName()));
            } else {
                field = record.findFieldByExample(new Field(alias,
                        KnownCommonFields.VERSIONCONTROL.getFieldName(), null));
            }

            field.setValue("-1");
        }
    }

    /**
     * Crea un Dependence desde una Dependency
     * 
     * @param dependency
     *            Dependency
     * 
     * @return Dependence convertido
     */
    public static Dependence getDependence(Dependency dependency, WebPage webPage, Detail detail) {
        Dependence dependence = new Dependence(dependency.getFromAlias(),
                dependency.getFromField(), dependency.getField());

        String immediateValue = dependency.getImmediateValue();
        if (StringUtils.isNotBlank(immediateValue)) {
            immediateValue = Conversor.processValue(immediateValue, webPage, detail);
            if (StringUtils.isNotBlank(immediateValue)) {
                //Si usamos una dependencia de valor inmediato, se descarta el los campos "desde"
                dependence.setFromAlias("");
                dependence.setFrom("");
                dependence.setValue(immediateValue);
            } else {
                return null;
            }
        }

        return dependence;
    }

    /**
     * Crea un Dependence desde una Dependency
     * 
     * @param dependency
     *            Dependency
     * @param alias
     *            Alias donde se encuentra la dependencia
     * 
     * @return Dependence convertido
     */
    public static Dependence getDependence(Dependency dependency, String alias) {
        return new Dependence(dependency.getFromAlias(),
                dependency.getFromField(), alias, dependency.getField());
    }

    /**
     * Crea una Dependency desde un Dependence
     * 
     * @param dependence
     *            Dependencia
     *
     * @return Dependence convertido
     */
    public static Dependency getDependency(Dependence dependence) {
        Dependency dependency = new Dependency();

        dependency.setField(dependence.getTo());
        dependency.setFromAlias(dependence.getFromAlias());
        dependency.setFromField(dependence.getFrom());
        if (dependence.getValue() != null) {
            dependency.setImmediateValue(dependence.getValue().toString());
        }

        return dependency;
    }

    /**
     * Copia dependencias tipo CRITERION y con valor inmediato.
     * 
     * @param detail
     * @param webPage
     */
    public static void copiarDependencias(WebPage webPage, Detail detail) {
        for (NodoDependencia nodo : EntornoWeb.getContexto().
                getArbolDependencias().getNodos().values()) {
            Reference reference = nodo.getReference();
            Table tableTo = Conversor.getTable(detail, reference.getAlias());

            if (tableTo == null) {
                return;
            }

            String alias = webPage.getLegacy() ? null : reference.getAlias();

            for (Dependency dependency : reference.getDependencies()) {
                String field = getLegacyFieldName(webPage, reference.getAlias(), dependency);
                Criterion criterion = new Criterion(alias, field, null);

                criterion.setCondition(filterCondition(dependency.getComparator(),
                        webPage));

                if (dependency.getType() == DependencyType.CRITERION) {
                    Table tableFrom = Conversor.getTable(detail,
                            dependency.getFromAlias());

                    if (tableFrom == null) {
                        throw new ErrorWeb("Referencia inexistente en "
                                + "dependencia CRITERION");
                    }

                    Criterion criterionDesde;
                    if (webPage.getLegacy()) {
                        criterionDesde = tableFrom.findCriterionByName(
                            getLegacyFromFieldName(webPage, dependency));
                    } else {
                       criterionDesde = tableFrom.findCriterionByAlias(
                            dependency.getFromAlias(), dependency.getFromField());
                    }

                    if (criterionDesde == null) {
                        continue;
                    }

                    criterion = tableTo.findCriterionNotNullCreate(criterion);

                    if (criterion.getValue() == null) {
                        criterion.setValue(criterionDesde.getValue());
                    }

                } else if (dependency.getType() == DependencyType.IMMEDIATE) {
                    criterion = tableTo.findCriterionNotNullCreate(criterion);

                    if (criterion.getValue() == null && StringUtils.isNotBlank(
                            dependency.getImmediateValue())) {
                        String immediateValue = Conversor.processValue(dependency.getImmediateValue(), webPage, detail);
                        if (StringUtils.isNotBlank(immediateValue)) {
                            criterion.setType(CriterionType.JOIN);
                            criterion.setValue(immediateValue);
                        }
                    }
                }
            }
        }
    }

    /**
     * Obtiene un CampoDetail dado un datoHttp y el registro.
     *
     * @param webPage
     *            WebPage donde se encuentra el FormElement
     * @param detail
     *            Detail donde buscar el Field o Criterion
     * @param formElement
     *            FormElement con los valores
     * @param registro
     *            Registro donde se encuentra el dato
     *
     * @return un DetailField que envuelve al Field o Criterion o null si no se
     *         encontró
     */
    public static DetailField getDetailField(WebPage webPage,
            FormElement formElement, Detail detail, int registro) {
        Table table = getTable(detail, formElement);
        DataSource dataSource = formElement.getDataSource();

        if (table == null && !dataSource.esControl()) {
            return null;
        }

        String fieldName = getLegacyFieldName(webPage, dataSource);

        switch (dataSource.getType()) {
            case CRITERION:
            case CRITERION_DESCRIPTION:
                if (webPage.getLegacy()) {
                    return table.findCriterionByName(fieldName,
                            CriterionType.NORMAL);
                } else {
                    return table.findCriterionByAlias(table.getAlias(),
                            dataSource.getField(), CriterionType.NORMAL);
                }

            case ORDER:
                if (webPage.getLegacy()) {
                    return table.findCriterionByName(fieldName,
                            CriterionType.ORDER);
                } else {
                    return table.findCriterionByAlias(table.getAlias(),
                            dataSource.getField(), CriterionType.ORDER);
                }

            case RECORD:
            case DESCRIPTION:
                if (webPage.getLegacy()) {
                    return table.findRecordByNumber(registro).findFieldByName(
                            fieldName);
                } else {
                    return table.findRecordByNumber(registro).findFieldByAlias(
                            table.getAlias(), dataSource.getField());
                }

            case CRITERION_CONTROL:
            case CONTROL:
                return detail.findFieldByName(dataSource.getField());

            case REPORT:
                return detail.findFieldByName("R_" + dataSource.getField());

            default:
                return null;
        }
    }

    /**
     * Crea un field desde un FormElement, setea el valor del FormElement en
     * el criterio solo si este valor no ha cambiado y lo agrega a un Record.
     * 
     * @param formElement
     *            Widget de donde tomar los datos
     * @param detail
     *            Detail donde se va a agregar el field
     * @param table
     *            Table donde se va a agregar el field
     * @param record
     *            Record donde se va a agregar el field
     * 
     * @return El Field creado envielto en un CampoDetail.
     */
    public static DetailField createDetailField(WebPage webPage,
            FormElement formElement, Detail detail, Table table, int registro) {
        Record record = table.findRecordByNumber(registro);
        DataSource dataSource = formElement.getDataSource();
        String alias = webPage.getLegacy() ? null : dataSource.getAlias();

        Field field = new Field(alias, getLegacyFieldName(webPage, dataSource), null);

        switch (dataSource.getType()) {
            case AGGREGATE:
                field.setType(FieldType.AGGREGATE);
                field.setFunctionName(dataSource.getFunctionName());
                break;

            case DESCRIPTION:
                if (!webPage.getLegacy()) {
                    for (Dependency dependency : dataSource.getDependencies()) {
                        Dependence dependence = getDependence(dependency, webPage, detail);
                        if (dependence != null) {
                            field.addDependence(dependence);
                        }
                    }
                }

                field.setType(FieldType.INNER_SELECT);
                break;

            case CRITERION_DESCRIPTION:
                if (!webPage.getLegacy()) {
                    for (Dependency dependency : dataSource.getDependencies()) {
                        Table tableDesde = detail.findTableByAlias(dependency.
                                getFromAlias());
                        Criterion criterionDesde = tableDesde.findCriterionByAlias(
                                dependency.getFromAlias(), dependency.getFromField());

                        if (criterionDesde.getValue() == null || StringUtils.isBlank(criterionDesde.
                                getValue().toString())) {
                            return null;
                        }

                        table.addCriterion(new Criterion(table.getAlias(),
                                dependency.getField(), criterionDesde.getValue()));
                    }

                    field.setAlias(formElement.getHTMLId().replaceAll(
                            LETTERS_NUMBERS_REGEX, ""));
                    field.setHint(formElement.getHTMLId());
                } else {
                    field.setType(FieldType.INNER_SELECT);
                }

                break;
        }

        field = record.findField(field);

        if (formElement.getFieldData().tieneCambios(registro) || field.
                getOldValue() == null) {
            if (formElement.getAssistant() instanceof Password) {
                try {
                    field.setValue(new Decrypt().encrypt(formElement.
                            getFieldData().getValue(registro)));
                } catch (Exception e) {
                    // Excepcion desconocida
                    throw new ErrorWeb(e);
                }
            } else {
                field.setValue(formElement.getAssistant().asObject(formElement.
                        getFieldData().getValue(registro)));
            }
        }

        return field;
    }

    /**
     * Crea un Join a partir de una referencia
     * 
     * @param reference
     *            referencia desde la que se crea el join
     * 
     * @return Join creado
     */
    public static Join getJoin(Reference reference, WebPage webPage, Detail detail) {
        Join join = new Join(reference.getTable(), reference.getAlias());

        join.setType(reference.getJoinType());

        for (Dependency dependency : reference.getDependencies()) {
            if (dependency.getType() != DependencyType.CRITERION
                    && (StringUtils.isNotBlank(dependency.getFromField())
                    || StringUtils.isNotBlank(dependency.getImmediateValue()))) {
                Dependence dependence = Conversor.getDependence(dependency, webPage, detail);
                if (dependence != null) {
                    join.addDependence(dependence);
                }
            }
        }

        return join;
    }

    /**
     * Llena el webPage con los valores del Detail de la respuesta.
     * 
     * @param respuesta
     *            Respuesta
     */
    public static void llenar(RespuestaWeb respuesta) {
        WebPage webPage = EntornoWeb.getContexto().getWebPage();
        Detail detail = ((TransporteDBUCI) respuesta.getTransporteDB()).
                getDetail();

        for (Table table : detail.getTables()) {
            for (Criterion criterion : table.getCriteria()) {
                leerValorCriterio(webPage, table, criterion);
            }

            for (Record record : table.getRecords()) {
                for (Field field : record.getFields()) {
                    leerValorField(webPage, table, record, field);
                }
            }
        }

        for (Field field : detail.getFields()) {
            leerValorCampoControl(webPage, field);
        }
    }

    /**
     * Lee el valor de un CRITERION del detail y lo setea en el webPage en el
     * lugar apropiado.
     * 
     * @param webPage
     *            WebPage donde setear el valor
     * @param table
     *            Table donde se encuentra el field
     * @param field
     *            Field de donde tomar el valor
     */
    private static void leerValorCriterio(WebPage webPage, Table table,
            Criterion criterion) {
        if (criterion.getOrder() != null) {
            return;
        }

        FormElement formElement = getFormElement(webPage, table, criterion);

        if (formElement == null) {
            Debug.debug("No se encontró el FormElement para el criterio "
                    + table.getAlias() + PUNTO + criterion.toString());
            return;
        }

        setFormElementValue(criterion, formElement, 0);

        if (webPage.getStore()) {
            DataSource ds = new DataSource();
            ds.setAlias(formElement.getDataSource().getAlias());
            ds.setField(formElement.getDataSource().getField());
            ds.setType(DataSourceType.RECORD);
            ds.setComparator(null);
            ds.setFunctionName(null);
            if (webPage.findFormElementsIgnoreNull(ds).isEmpty()) {
                formElement.getFieldData().setDisabled(0, true);
            }
        }
    }

    /**
     * Lee el valor de un field del detail y lo setea en el webPage en el lugar
     * apropiado.
     * 
     * @param webPage
     *            WebPage donde setear el valor
     * @param table
     *            Table donde se encuentra el field
     * @param record
     *            Record donde se encuentra el field
     * @param field
     *            Field de donde tomar el valor
     */
    private static void leerValorField(WebPage webPage, Table table,
            Record record, Field field) {
        Collection<FormElement> formElements = getFormElements(webPage, table, field);

        if (formElements.isEmpty()) {
            String fieldName = field.getName().toUpperCase();
            if (!fieldName.endsWith(KnownCommonFields.VERSIONCONTROL.toString())
                    && !fieldName.endsWith(KnownCommonFields.FHASTA.toString())) {
                Debug.debug("No se encontró ni un FormElement para el campo "
                        + table.getAlias() + PUNTO + field.toString());
            }
            return;
        }

        for (FormElement formElement : formElements) {
            setFormElementValue(field, formElement, record.getNumber());

            if (field.isPrimaryKey() ||(webPage.getRequiresQuery() && !EntornoWeb.
                    getContexto().getHayDatos())) {
                formElement.getFieldData().setDisabled(record.getNumber(), true);
            }

            ((Widget) formElement).getParentContainer().
                    setNumeroDeFilasConsultadas(true, record.getNumber());
        }

        if (field.getName().equalsIgnoreCase(KnownCommonFields.VERSIONCONTROL.getFieldName())) {
            if (field.getValue() == null) {
                for (DeleteRecord deleteRecord : IterableWebElement.get(webPage,
                        DeleteRecord.class)) {
                    if (deleteRecord.getAliasList().contains(table.getAlias())) {
                        deleteRecord.getFieldData().setDisabled(record.getNumber(), true);
                    }
                }
            }
        }
    }

    /**
     * Lee el valor de un campo de control del detail y lo setea en el webPage
     * en el lugar apropiado.
     * 
     * @param webPage
     *            WebPage donde setear el valor
     * @param field
     *            Field de donde tomar el valor
     */
    private static void leerValorCampoControl(WebPage webPage, Field field) {
        if (field.getValue() == null) {
            return;
        }

        Collection<FormElement> formElements = getFormElements(webPage, field);

        if (formElements.isEmpty()) {
            if (!field.isTransportField()) {
                Debug.debug("No se encontró el FormElement para el campo de control "
                        + field.toString());
            }
            return;
        }

        for (FormElement formElement : formElements) {
            setFormElementValue(field, formElement, 0);
        }
    }

    /**
     * Cambia el valor de un FormElement dado un field y el registro donde está
     * el field.
     *
     * @param detailField
     *            DetailField de donde se toma el valor
     * @param formElement
     *            FormElement donde se va a poner el valor
     * @param record
     *            Registro del field
     */
    private static void setFormElementValue(DetailField detailField,
            FormElement formElement, int registro) {
        Object value = detailField.getValue();
        FieldData fieldData = formElement.getFieldData();

        if (value instanceof char[]) {
            fieldData.setValueConsulta(registro, new String((char[]) value));
        } else if (value instanceof byte[]) {
            fieldData.setValueConsulta(registro, Base64.encodeBase64String(
                    (byte[]) value));
        } else {
            String stringValue = value == null ? "" : value.toString();
            fieldData.setValueConsulta(registro, stringValue);
        }
    }

    public static boolean esCriterioRequerido(FormElement formElement) {
        Requerido req = formElement.getRequerido();

        if (formElement.getDataSource().esCriterio() && req == Requerido.REQUERIDO) {
            String value = formElement.getFieldData().getValue(0);

            return StringUtils.isBlank(value);
        }

        return false;
    }

    public static boolean marcarRequerido(FormElement formElement, int registro) {
        Debug.debug(String.format("Campo requerido %s[%s]", formElement.getName(), registro));
        formElement.getFieldData().setError(registro, "Valor requerido", "required");
        return true;
    }

    public static String getLegacyAlias(WebPage webPage, Table table, DetailField field) {
        if (webPage.getLegacy() && field.getName().contains("+")) {
            return field.getName().split("\\+")[0];
        } else if (webPage.getLegacy() && field.getName().contains(".")) {
            return field.getName().split("\\.")[0];
        } else if (webPage.getLegacy() && table != null) {
            return table.getAlias();
        } else {
            return field.getAlias();
        }
    }

    public static String getLegacyFieldName(WebPage webPage, DetailField field) {
        if (webPage.getLegacy() && field.getName().contains("+")) {
            return field.getName().split("\\+")[1].split("_")[0];
        } else if (webPage.getLegacy() && field.getName().contains(".")) {
            return field.getName().split("\\.")[1];
        } else {
            return field.getName();
        }
    }

    public static String getLegacyVariant(WebPage webPage, DetailField field) {
        if (webPage.getLegacy() && field.getName().contains("+")) {
            String name = field.getName().split("\\+")[1];
            if (field.getName().contains("_")) {
                return name.split("_")[1];
            } else {
                return "";
            }
        } else {
            return null;
        }
    }

    public static String getLegacyFieldName(WebPage webPage, DataSource dataSource) {
        if (!webPage.getLegacy()) {
            return dataSource.getField();
        }

        ArbolDependencias arbolDependencias = EntornoWeb.getContexto().
                getArbolDependencias();
        String alias = dataSource.getAlias();
        NodoDependencia nodo = arbolDependencias.getNodos().get(alias);
        String aliasPrincipal = nodo != null ? nodo.getPrincipal().getAlias() : null;

        if (dataSource.esDescripcion()) {
            return getLegacyFieldName(alias, dataSource,
                    dataSource.getDependencies());
        } else if (nodo != null && !alias.equals(aliasPrincipal)) {
            return getLegacyFieldName(nodo.getReference().getTable(), dataSource,
                    nodo.getReference().getDependencies());
        }

        return dataSource.getField();
    }

    public static String getLegacyFieldName(String alias,
            DataSource dataSource, Collection<Dependency> dependencies) {
        String name = alias + "+" + dataSource.getField();

        for (Dependency dependency : dependencies) {
            if (dependency.getFromField().contains("_")) {
                name += "_" + dependency.getFromField().split("_", 2)[1];
            }
            break;
        }

        return name;
    }

    public static String getLegacyFieldName(WebPage webPage, String alias, Dependency dependency) {
        if (!webPage.getLegacy()) {
            return dependency.getField();
        }

        ArbolDependencias arbolDependencias = EntornoWeb.getContexto().
                getArbolDependencias();
        NodoDependencia nodo = arbolDependencias.getNodos().get(alias);

        if (nodo == null || nodo.getPrincipal().getAlias().equals(alias)) {
            return dependency.getField();
        } else {
            return nodo.getReference().getTable() + "+" + dependency.getField();
        }
    }

    public static String getLegacyFromFieldName(WebPage webPage, Dependency dependency) {
        if (!webPage.getLegacy()) {
            return dependency.getField();
        }

        ArbolDependencias arbolDependencias = EntornoWeb.getContexto().
                getArbolDependencias();
        String alias = dependency.getFromAlias();
        NodoDependencia nodo = arbolDependencias.getNodos().get(alias);

        if (nodo == null || nodo.getPrincipal().getAlias().equals(alias)) {
            return dependency.getFromField();
        } else {
            return nodo.getReference().getTable() + "+" + dependency.getFromField();
        }
    }

    public static int max(Table table) {
        int max = -1;

        if (table != null) {
            for (Record record : table.getRecords()) {
                max = Math.max(max, record.getNumber());
            }
        }

        return max;
    }

    public static String processValue(String value, WebPage webPage, Detail detail) {
        if (StringUtils.isNotBlank(value)) {
            if (value.startsWith("=")) {
                return Conversor.processFormulaValue(value, webPage, detail);
            } else if (value.startsWith("$")) {
                return Conversor.processSessionValue(value, detail);
            }
        }

        return value;
    }

    public static String processFormulaValue(String value, WebPage webPage, Detail detail) {
        //Verificar si es un valor a ser reemplazado por campos del formulario
        if (value.startsWith("=$")) {
            return Conversor.processSessionValue(value.substring(1), detail);
        }

        String elementName = value.substring(1);
        Debug.debug("Buscando valores del campo " + elementName + " como formula");
        for (FormElement formElement : IterableWebElement.get(webPage, FormElement.class)) {
            if (formElement.getName().equals(elementName)) {
                value = formElement.getFieldData().getValue(0);
                Debug.debug("Valor encontrado para el campo " 
                        + elementName + ". El valor actual fue reemplazado por " 
                        + (StringUtils.isBlank(value) ? "{valor vacio}" : value));
            }
        }

        return value;
    }

    public static String processSessionValue(String value, Detail detail) {
        //Verificar si es un valor a ser reemplazado por campos de la session
        String sessionField = StringUtils.capitalize(value.substring(1));

        try {
            Debug.debug("Buscando un valor para la variable de sesion " + sessionField);

            Constructor tdbuciConstuctor = TransporteDBUCI.class.getConstructor(Detail.class);
            Object tdbUci = tdbuciConstuctor.newInstance(detail);
            Method requestedMethod = TransporteDBUCI.class.getMethod("get" + sessionField);
            value = String.valueOf(requestedMethod.invoke(tdbUci));

            Debug.debug("Valor encontrado para la variable de sesion " + sessionField 
                            + ". El valor actual fue reemplazado por " + value);
        } catch (InstantiationException e) {
            Debug.error("Error al instanciar el TransporteDBUCI", e);
        } catch (IllegalAccessException e) {
            Debug.error("Error al instanciar el TransporteDBUCI", e);
        } catch (NoSuchMethodException e) {
            Debug.warn("No se encontro la variable de sesion "
                    + "(" + sessionField + "), se enviara el valor completo", e);
        } catch (InvocationTargetException e) {
            Debug.error("Error al instanciar el TransporteDBUCI", e);
        }

        return value;
    }

    /**
     * Metodo que genera un webPage en base al contenido de un objeto
     * Table obtenido desde un Detail.
     * 
     * @param table Tabla a procesar para generar el webPage en base a ella
     * @param webPage WebPage base (subsistema, transaccion, propiedades, etc)
     * @param title Titulo a ubicar al nuevo webPage
     * @return WebPage generado en base al contenido de la tabla
     */
    public static WebPage generateWebPageFromTable(Table table, WebPage webPage, 
            String title) {
        return Conversor.generateWebPageFromTable(table, webPage, title, false);
    }

    /**
     * Metodo que genera un webPage en base al contenido de un objeto
     * Table obtenido desde un Detail.
     * 
     * @param table Tabla a procesar para generar el webPage en base a ella
     * @param webPage WebPage base (subsistema, transaccion, propiedades, etc)
     * @param title Titulo a ubicar al nuevo webPage
     * @param history Armar para poder consultar historicos de tablas
     * @return WebPage generado en base al contenido de la tabla
     */
    public static WebPage generateWebPageFromTable(Table table, WebPage webPage, 
            String title, boolean history) {
        //Obtener el nombre de la tabla a procesare
        String tableName = table.getName();

        //Limpiar informacion de containers y solo conservar atributos del webpageActual
        webPage.clear();

        //Setear el titulo de la nueva transaccion
        webPage.setTitle(title);

        //Agregar la referencia en base a la tabla enviada
        Reference reference = new Reference(tableName, tableName);
        webPage.getReferences().add(reference);

        //Agregar el container que tendra la tabla con los datos a mostrar
        Container container = new Container();
        container.setTipoFila(TipoFila.TABLA);
        container.setClonacionMax(20);
        container.setPresentacionMax(20);
        container.setCSSClass("table-group");
        container.setReadOnly(history);

        //Indica si la tabla posee fhasta
        boolean hasFhasta = false;

        //Leer los criterios de la tabla, para armar la cabecera de filtros del container
        for (Criterion criterion : table.getCriteria()) {
            //Campos tipo order no son filtros, se omiten
            if (CriterionType.ORDER.equals(criterion.getType())) {
                continue;
            }

            //Armar el nuevo input de filtro para la cabecera con su respectivo datasource
            Input input = new Input();
            input.setName("cri_" + criterion.getName());
            DataSource ds = new DataSource(reference.getAlias(), criterion.getName(), DataSourceType.CRITERION);

            //Por defecto se usa un comparador like para todos los criterios
            ds.setComparator("LIKE");
            input.setDataSource(ds);

            input.setZ(1);
            container.add(input);
        }

        //Leer los criterios de la tabla, para armar los labels del container
        for (Criterion criterion : table.getCriteria()) {
            if (CriterionType.ORDER.equals(criterion.getType())) {
                continue;
            }

            Label label = new Label(criterion.getName());
            label.setZ(2);
            container.add(label);
        }

        //Separador de la cabecera con el cuerpo de la tabla
        HeaderSeparator hs = new HeaderSeparator();
        container.add(hs);

        //Leer los campos del 1er registro de la tabla para armar el cuerpo del container
        Record record = table.findRecordByNumber(0);
        int fieldCount = 0;
        for (Field field : record.getFields()) {
            //Verificar si la tabla tiene un FHASTA para filtrar luego por dicho campo
            if ("FHASTA".equals(field.getName())) {
                hasFhasta = true;
            }

            //Obtener el criterio asociado al campo procesado
            Input criteria = (Input) container.get(fieldCount++);
            Input input = new Input();

            //Si el campo tiene una longitud de 1, se asume que es un checkbox
            if (field.getLength() == 1) {
                input = new CheckBox();

                //Filtros de esta columna mediante un comboBox
                ComboBox combo = new ComboBox();
                combo.setOpcionVacia(true);
                combo.setChoice(Arrays.asList("1", "0"));
                combo.setDatos(Arrays.asList("SI " + field.getName(), "NO " + field.getName()));
                criteria = combo;
                container.set(fieldCount - 1, criteria);
            }

            //Agregar un HINT al criterio con el comentario del campo
            try {
                criteria.setGuia(field.getHint());
            } catch (Exception e) {
                Debug.warn("Problemas al leer el hint de " + field.getName(), e);
            }

            //Crear el field con su respectivo datasource
            input.setName(field.getName());
            input.setDataSource(new DataSource(reference.getAlias(), field.getName(), DataSourceType.RECORD));
            container.add(input);

            //Controlar el tipo de dato del campo para agregar un formateador adecuado
            String dataType = field.getDatatype();
            if ("S".equals(dataType)) {
                //Campos tipo fecha
                DateFormatter df = new DateFormatter();
                df.setFormat(DateFormatter.DateFormat.DATE);
                criteria.getBehaviors().add(df);

                DateFormatter dfRecord = new DateFormatter();
                dfRecord.setFormat(DateFormatter.DateFormat.DATETIME);
                input.getBehaviors().add(dfRecord);

                //Filtrar siempre por la fecha inferior
                criteria.getDataSource().setComparator(">=");
            } else if ("N".equals(dataType)) {
                //Campos numericos
                input.getBehaviors().add(new NumberFormatter());
                criteria.getBehaviors().add(new NumberFormatter());

                //Campos numericos se filtran con valor exacto
                criteria.getDataSource().setComparator("=");
            } else if ("T".equals(dataType)) {
                //Campos tipo texto
                input.getBehaviors().add(new UpperCaseFormatter());
                criteria.getBehaviors().add(new UpperCaseFormatter());
            }

            //Procesar eventos JS por el asistente y por los comportamientos JS.
            //Para inputs
            input.getAssistant().generateHtml(null);
            for (JSBehavior jsBehavior : input.getBehaviors()) {
                jsBehavior.setFormElement(input);
                WebPageEnviroment.addJavascriptInicial(GeneradorJS.toJS(jsBehavior) + ";");
            }

            //Para criterios
            criteria.getAssistant().generateHtml(null);
            for (JSBehavior jsBehavior : criteria.getBehaviors()) {
                jsBehavior.setFormElement(criteria);
                WebPageEnviroment.addJavascriptInicial(GeneradorJS.toJS(jsBehavior) + ";");
            }
        }

        //Si la tabla posee un campo FHASTA, permitir filtrar historicos
        if (history && hasFhasta) {
            //Agregar un separador de cuerpo para agregar un pie de formulario
            //De esta seccion para abajo, no se muestra nada, solo son campos de filtros especiales
            FooterSeparator fs = new FooterSeparator();
            container.add(fs);

            //Buscar registros historicos sin importar el valor de fhasta
            Input fhasta = new Input();
            fhasta.setName("ocri_FHASTA");
            fhasta.setValueInicial("2999-12-31");
            fhasta.setVisible(false);
            DataSource ds = new DataSource(reference.getAlias(), "FHASTA", DataSourceType.CRITERION);
            ds.setComparator("<=");
            fhasta.setDataSource(ds);
            container.add(fhasta);

            //Ordenar por fhasta descendentemente
            Input fhastaOrder = new Input();
            fhastaOrder.setName("order_FHASTA");
            fhastaOrder.setValueInicial("-1");
            fhastaOrder.setVisible(false);
            DataSource dsOrder = new DataSource(reference.getAlias(), "FHASTA", DataSourceType.ORDER);
            fhastaOrder.setDataSource(dsOrder);
            container.add(fhastaOrder);
        }

        //Agregar el container generado, al webPage
        webPage.add(container);

        //Procesar las propiedades de los containers antes de dibujarlo
        webPage = new UCIWebPageProvider().processWebPage(webPage, "0");

        return webPage;
    }

    /**
     * Metodo que busca un campo especifico (o parecidos) en el detail, desde
     * los campos de control, a los criterios, y terminando en los records de
     * tablas.
     *
     * @param pDetail Mensaje de entrada
     * @param fieldName Nombre del campo a encontrar
     * @return Mapa<NombreCampo, ValorCampo> encontrado en el detail
     */
    public static Pair<String, String> findFieldInDetail(Detail pDetail, String fieldName) {
        //Coincidencia completa en campos de control
        Field field = pDetail.findFieldByName(fieldName);
        if (field != null && field.getValue() != null
                && StringUtils.isNotBlank(field.getStringValue())) {
            return new Pair(field.getName(), field.getStringValue());
        }

        //Coincidencias parciales en campos de control
        for (Field detailField : pDetail.getFields()) {
            if (!detailField.hasData() 
                    || StringUtils.isBlank(detailField.getStringValue())) {
                continue;
            }

            //Coincidencia parcial por la izquierda
            if (detailField.getName().startsWith(fieldName)) {
                return new Pair(detailField.getName(), detailField.getStringValue());
            }

            //Coincidencia parcial por la derecha
            if (detailField.getName().endsWith(fieldName)) {
                return new Pair(detailField.getName(), detailField.getStringValue());
            }

            //Coincidencia parcial intermedia
            if (detailField.getName().contains(fieldName)) {
                return new Pair(detailField.getName(), detailField.getStringValue());
            }
        }

        //Coincidencias en table-criterions-records
        for (Table table : pDetail.getTables()) {
            //Coincidencia completa en criterions de table
            Criterion criterion = table.findCriterionByName(fieldName);
            if (criterion != null && criterion.getValue() != null
                    && StringUtils.isNotBlank(criterion.getStringValue())) {
                return new Pair(criterion.getName(), criterion.getStringValue());
            }

            //Coincidencias parciales en criterions de table
            for (Criterion tableCriterion : table.getCriteria()) {
                if (tableCriterion.getValue() == null
                        || StringUtils.isBlank(tableCriterion.getStringValue())) {
                    continue;
                }

                //Coincidencia parcial por la izquierda
                if (tableCriterion.getName().startsWith(fieldName)) {
                    return new Pair(tableCriterion.getName(), tableCriterion.getStringValue());
                }

                //Coincidencia parcial por la derecha
                if (tableCriterion.getName().endsWith(fieldName)) {
                    return new Pair(tableCriterion.getName(), tableCriterion.getStringValue());
                }

                //Coincidencia parcial intermedia
                if (tableCriterion.getName().contains(fieldName)) {
                    return new Pair(tableCriterion.getName(), tableCriterion.getStringValue());
                }
            }
        }

        return new Pair(StringUtils.EMPTY, StringUtils.EMPTY);
    }
}