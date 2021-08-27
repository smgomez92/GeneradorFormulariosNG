package com.fitbank.web.uci.procesos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import com.fitbank.common.KnownCommonFields;
import com.fitbank.dto.management.Criterion;
import com.fitbank.dto.management.Dependence;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.DetailField;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.FieldType;
import com.fitbank.dto.management.Join;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.DataSourceType;
import com.fitbank.enums.DependencyType;
import com.fitbank.enums.MessageType;
import com.fitbank.enums.Requerido;
import com.fitbank.util.Debug;
import com.fitbank.util.Pair;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ManejoExcepcion;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.exceptions.MensajeWeb;
import com.fitbank.web.json.TransporteWeb;
import com.fitbank.web.uci.Conversor;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.FieldData;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.util.ArbolDependencias;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.NodoDependencia;
import com.fitbank.webpages.widgets.DeleteRecord;

@Handler(GeneralRequestTypes.MANTENIMIENTO)
@RevisarSeguridad
public class Mantenimiento implements Proceso {

    /**
     * Procesa el request del mantenimiento
     * 
     * @param pedido
     * @return RespuestaWeb
     */
    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        WebPage webPage = EntornoWeb.getContexto().getWebPage();

        Detail detail = prepararDetail(webPage, pedido);

        llenarTablas(webPage, detail);
        limpiarRecordsMultiregistro(webPage, detail);
        copiarValoresCriterios(webPage, detail);
        limpiarRecords(webPage, detail);
        revisarRequeridos(webPage, detail);
        separarTablas(webPage, detail);
        limpiarTablas(webPage, detail);
        revisarCambios(webPage, detail);
        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);
        detail = ((TransporteDBUCI) respuesta.getTransporteDB()).getDetail();
        ManejoExcepcion.checkOkCodes(respuesta);
        EntornoWeb.getContexto().setHayDatos(true);

        Detail detailConsulta = ((TransporteDBUCI) EntornoWeb.getContexto().getTransporteDBBase()).getDetail();

        Conversor.crearTablas(webPage, detailConsulta);
        llenarTablasCompletas(webPage, detail, detailConsulta);
        guardarRegistros(webPage, detail, detailConsulta, respuesta);

        respuesta.setTransporteDB(new TransporteDBUCI(detailConsulta));

        Consulta.resetearCampos(webPage, false);
        Conversor.llenar(respuesta);

        TransporteWeb transporte = new TransporteWeb(respuesta, webPage, false);
        respuesta.setContenido(transporte);

        EntornoWeb.getContexto().setTransporteDBBase(respuesta.getTransporteDB());

        return respuesta;
    }

    /**
     * Preparamos el detail de mantenimiento
     *
     * @param pedido
     */
    private Detail prepararDetail(WebPage webPage, PedidoWeb pedido) {
        Conversor.crearTablas(webPage, ((TransporteDBUCI) pedido.
                    getTransporteDB()).getDetail());

        pedido.getTransporteDB().setMessageType(MessageType.STORE);

        return ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();
    }

    /**
     * Obtiene los valores del request
     *
     * @param webPage
     * @param detail
     */
    private void llenarTablas(WebPage webPage, Detail detail) {
        // Setear valores en el detail
        for (Container container : webPage) {
            for (int registro : container.iteradorClonacion()) {
                for (FormElement formElement : IterableWebElement.get(
                        container, FormElement.class)) {
                    Conversor.convertirFormElementMantenimiento(webPage,
                            formElement, detail, registro);
                }
                for (DeleteRecord deleteRecord : IterableWebElement.get(
                        container, DeleteRecord.class)) {
                    Conversor.convertirDeleteRecordMantenimiento(webPage,
                            deleteRecord, detail, registro);
                }
            }
            container.limpiarIteradorClonacion();
        }

        Conversor.copiarDependencias(webPage, detail);
    }

    /**
     * Quita registros sin cambios de tablas multiregistro.
     *
     * @param detail
     */
    private void limpiarRecordsMultiregistro(WebPage webPage, Detail detail) {
        for (Table table : detail.getTables()) {
            // Tablas con un solo registro no se limpian de antemano
            if (table.getRecordCount() == 1) {
                continue;
            }

            limpiarRecords(webPage, table);
        }
    }

        /**
         * Completa los campos desde los criterios. Para cada registro copia el
         * valor de los criterios en un campo con el mismo nombre, si existe este
         * campo.
         *
         * @param detail
         */
    private void copiarValoresCriterios(WebPage webPage, Detail detail) {
        for (Table table : detail.getTables()) {
            for (Criterion criterion : table.getCriteria()) {
                for (Record record : table.getRecords()) {
                    Field field;
                    if (webPage.getLegacy()) {
                        field = record.findFieldByName(criterion.getName());
                    } else {
                        field = record.findFieldByAlias(criterion.getAlias(), criterion.getName());
                    }

                    if (field != null) {
                        Collection<FormElement> formElements =
                                Conversor.getFormElements(webPage, table, field);

                        if (!formElements.isEmpty()) {
                            continue;
                        }
                    }

                    if (field == null && criterion.getValue() != null) {
                        field = new Field(criterion.getAlias(),
                                criterion.getName(), criterion.getValue());
                        record.addField(field);
                    }

                    if (field != null && field.getValue() == null) {
                        field.setValue(criterion.getValue());
                    }
                }
            }
        }
    }

    /**
     * Revisa si existen criterios o campos faltantes.
     *
     * @param webPage
     *
     * @throws ErrorWeb
     */
    private void revisarRequeridos(WebPage webPage, Detail detail) throws MensajeWeb {
        boolean check = false;

        // Revisar fields requeridos para los registros enviados
        for (Table table : detail.getTables()) {
            for (Record record : table.getRecords()) {
                // No se validan requeridos en registros a caducar
                Field f = record.findFieldByName("VERSIONCONTROL");
                if (f != null && f.getValue() != null && f.getIntegerValue() < 0) {
                    continue;
                }
                for (Field field : record.getFields()) {
                    for (FormElement formElement : Conversor.getFormElements(
                            webPage, table, field)) {
                        check |= marcarSiEsRequerido(formElement, field, record.getNumber());
                    }
                }
            }
        }

        // Revisar fields requeridos para los campos de control
        for (Field field : detail.getFields()) {
            for (FormElement formElement : Conversor.getFormElements(webPage, field)) {
                check |= marcarSiEsRequerido(formElement, field, 0);
            }
        }

        // Revisar criterios requeridos solo si no existe un field con el mismo
        // alias y nombre
        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            if (Conversor.esCriterioRequerido(formElement)) {
                check |= marcarCriterioRequeridoSinRegistro(formElement, webPage);
            }
        }

        if (check) {
            throw new MensajeWeb("Hay valores requeridos no ingresados");
        }

        // Revisar referencias requeridas para evitar que se vaya vacía
        for (Reference reference : webPage.getReferences()) {
            ArbolDependencias arbol = EntornoWeb.getContexto().getArbolDependencias();
            NodoDependencia nodo = arbol.getNodos().get(reference.getAlias());
            String alias = nodo.getPrincipal().getAlias();

            // FIXME mejorar el método para determinar si una tabla realmente
            // no tiene registros para mantener
            Table table = detail.findTableByAlias(alias);
            if (reference.isRequired() && (table == null || table.getRecordCount() == 0)) {
                throw new MensajeWeb("No hay registros para mantener");
            }
        }
    }

    private boolean marcarSiEsRequerido(FormElement formElement, Field field, int registro) {
        Requerido req = formElement.getRequerido();
        String value = formElement.getFieldData().getValue(registro);

        if ((req == Requerido.REQUERIDO || (field.isPrimaryKey() && req
                == Requerido.AUTOMATICO)) && StringUtils.isBlank(value)) {
            return Conversor.marcarRequerido(formElement, registro);
        }

        return false;
    }

    private boolean marcarCriterioRequeridoSinRegistro(FormElement formElement, WebPage webPage) {
        DataSource dataSource = formElement.getDataSource();

        if (dataSource.esControl() || formElement.getRequerido() != Requerido.AUTOMATICO) {
            return Conversor.marcarRequerido(formElement, 0);
        }

        DataSource recordDS = new DataSource(dataSource.getAlias(),
                    dataSource.getField(), DataSourceType.RECORD);
        recordDS.setComparator(null);

        boolean noRecord = webPage.findFormElementsIgnoreNull(recordDS).isEmpty();

        // No marcar si es requerido automático y existe un registro
        if (noRecord) {
            return Conversor.marcarRequerido(formElement, 0);
        }

        return false;
    }

    /**
     * Quita registros sin cambios de todas las tablas.
     *
     * @param detail
     */
    private void limpiarRecords(WebPage webPage, Detail detail) {
        for (Table table : detail.getTables()) {
            Reference reference = EntornoWeb.getContexto().getReference(table.
                    getAlias());

            if (reference != null && !reference.isKeep()) {
                limpiarRecords(webPage, table);
            }

            table.clearEmptyRecords();
        }
    }

    /**
     * Elimina los records que no tienen cambios segun los widgets del webPage.
     *
     * @param webPage
     * @param table
     */
    private void limpiarRecords(WebPage webPage, Table table) {
        Iterator<Record> records = table.getRecords().iterator();

        record:
        while (records.hasNext()) {
            Record record = records.next();

            for (Field field : record.getFields()) {
                String fn = KnownCommonFields.VERSIONCONTROL.getFieldName();
                String name = Conversor.getLegacyFieldName(webPage, field);
                if (name.equalsIgnoreCase(fn) && "-1".equals(field.getValue())) {
                    continue record;
                }
                for (FormElement formElement : Conversor.getFormElements(webPage, table, field)) {
                    if (!formElement.getRelleno().startsWith("=")
                            && formElement.getFieldData().tieneCambios(record.getNumber())) {
                        continue record;
                    }
                }
            }

            records.remove();
        }
    }

    /**
     * Separa las tablas y los joins en tablas independientes.
     *
     * @param detail
     */
    private void separarTablas(WebPage webPage, Detail detail) {
        if (webPage.getLegacy()) {
            return;
        }

        for (int i = 0; i < detail.getTablesCount(); i++) {
            Table table = ((List<Table>) detail.getTables()).get(i);

            Iterator<Join> joins = table.getJoins().iterator();
            while (joins.hasNext()) {
                Join join = joins.next();
                Table t = new Table(join.getName(), join.getAlias());
                detail.addTable(t);
                for (Dependence dep : join.getDependencies()) {
                    t.addDependence(dep);
                }
                joins.remove();
            }

            Iterator<Criterion> criteria = table.getCriteria().iterator();
            while (criteria.hasNext()) {
                Criterion criterion = criteria.next();
                if (criterion.getAlias().equals(table.getAlias())) {
                    continue;
                }
                detail.findTableByAlias(criterion.getAlias()).addCriterion(
                        criterion);
                criteria.remove();
            }

            for (Record record : table.getRecords()) {
                Iterator<Field> fields = record.getFields().iterator();
                while (fields.hasNext()) {
                    Field field = fields.next();

                    if (field.getAlias().equals(table.getAlias())) {
                        continue;
                    }

                    if (field.getType() != FieldType.NORMAL) {
                        fields.remove();
                        continue;
                    }

                    Table table2 = detail.findTableByAlias(field.getAlias());
                    table2.findRecordByExample(new Record(record.getNumber())).addField(field);
                    fields.remove();
                }
            }
        }

        // Copiar criterios de dependencias
        for (Reference reference : webPage.getReferences()) {
            for (Dependency dependency : reference.getDependencies()) {
                if (dependency.getType() == DependencyType.DEFERRED
                        || dependency.getType() == DependencyType.CRITERION) {
                    continue;
                }

                Object value = null;

                if (StringUtils.isNotBlank(dependency.getImmediateValue())) {
                    value = dependency.getImmediateValue();
                } else if (StringUtils.isNotBlank(dependency.getFromAlias())) {
                    Table tableFrom = detail.findTableByAlias(dependency.getFromAlias());

                    if (tableFrom == null) {
                        throw new ErrorWeb("Alias " + dependency.getFromAlias()
                                + " no encontrado.");
                    }

                    Criterion criterionFrom = tableFrom.findCriterionByAlias(
                            dependency.getFromAlias(), dependency.getFromField());

                    if (criterionFrom == null) {
                        continue;
                    }

                    value = criterionFrom.getValue();
                }

                Table table = detail.findTableByAlias(reference.getAlias());
                Criterion criterionTo = table.findCriterionByAlias(
                        reference.getAlias(), dependency.getField());

                if (criterionTo != null) {
                    continue;
                } else {
                    criterionTo = table.findCriterion(new Criterion(
                            reference.getAlias(), dependency.getField(),
                            value));
                }
            }
        }
    }

    /**
     * Limpia las tablas que son solo consulta o que no tienen registros.
     *
     * @param webPage WebPage donde se buscan las referencias
     * @param detail Detail donde estan las tablas
     */
    private void limpiarTablas(WebPage webPage, Detail detail) {
        for (Reference reference : webPage.getReferences()) {
            String alias = reference.getAlias();
            if (reference.isQueryOnly() && detail.findTableByAlias(alias) != null) {
                detail.removeTable(alias);
            }
        }

        Iterator<Table> tables = detail.getTables().iterator();
        while (tables.hasNext()) {
            Table table = tables.next();
            if (table.getRecordCount() == 0) {
                tables.remove();
            }
        }
    }

    /**
     * Revisa que el formulario tenga al menos un cambio para enviar el mantenimiento
     *
     * @param detail
     */
    private void revisarCambios(WebPage webPage, Detail detail) {
        for (Table table : detail.getTables()) {
            if (table.getRecordCount() > 0) {
                return;
            }
        }

        for (Field field : detail.getFields()) {
            if (field.isTransportField()) {
                continue;
            }
            for (FormElement formElement : Conversor.getFormElements(webPage, field)) {
                if (formElement.getFieldData().tieneCambios(0)) {
                    return;
                }
            }
        }

        throw new MensajeWeb("Mantenimiento no enviado, no hay cambios registrados para mantener");
    }

    /**
     * Construye las tablas y llena con los valores del formulario.
     *
     * @param webPage
     * @param detailRespuesta
     * @param detailConsulta
     */
    public static void llenarTablasCompletas(WebPage webPage,
            Detail detailRespuesta, Detail detailConsulta) {
        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            DataSourceType type = formElement.getDataSource().getType();
            if (type != DataSourceType.RECORD && type
                    != DataSourceType.DESCRIPTION && type
                    != DataSourceType.CRITERION_DESCRIPTION) {
                continue;
            }
            Table tableConsulta = Conversor.getTable(detailConsulta, formElement);
            if (tableConsulta == null) {
                continue;
            }
            for (int record = 0; record < tableConsulta.getRecordCount(); record++) {
                Conversor.createDetailField(webPage, formElement,
                        detailConsulta, tableConsulta, record);
            }
        }
    }

    /**
     * Copia los registros al transporte db de consulta.
     * 
     * @param detailRespuesta
     */
    private void guardarRegistros(WebPage webPage, Detail detailRespuesta,
            Detail detailConsulta, RespuestaWeb respuesta) {
        Set<Pair<String, Record>> clearRecordFields = new HashSet<Pair<String, Record>>();

        for (Table table : detailConsulta.getTables()) {
            table.getCriteria().clear();
        }

        // Copiar registros desde el detail de mantenimiento al de consulta
        for (Table table : detailRespuesta.getTables()) {
            for (Criterion criterion : table.getCriteria()) {
                Table tableConsulta = getTable(webPage, detailConsulta, table,
                        criterion);
                tableConsulta.addCriterion(criterion);
            }
            for (Record record : table.getRecords()) {
                for (Field field : record.getFields()) {
                    Table tableConsulta = getTable(webPage, detailConsulta, table, field);

                    if (tableConsulta == null) {
                        Debug.warn("No se encontró la tabla (o join) con alias "
                                + field.getAlias());
                        continue;
                    }

                    Record recordConsulta = tableConsulta.findRecordByNumber(record.getNumber());

                    if (recordConsulta == null) {
                        tableConsulta.addRecord(record);
                        recordConsulta = record;
                    }

                    Field fieldConsulta;
                    if (webPage.getLegacy()) {
                        fieldConsulta = recordConsulta.findFieldByName(field.getName());
                    } else {
                        fieldConsulta = recordConsulta.findFieldByAlias(field.getAlias(),
                                field.getName());
                    }

                    if (fieldConsulta == null) {
                        recordConsulta.addField(field);
                        fieldConsulta = field;
                    }

                    if (field.getName().equalsIgnoreCase(KnownCommonFields.VERSIONCONTROL.name())
                            && field.getValue() != null && field.getIntegerValue() == -1) {
                        clearRecordFields.add(new Pair<String, Record>(
                                field.getAlias(), recordConsulta));
                    }

                    fieldConsulta.setValue(field.getValue());
                    fieldConsulta.setOldValue(field.getValue());
                }
            }
        }

        detailConsulta.removeFields();
        // Copiar campos de control
        for (Field field : detailRespuesta.getFields()) {
            detailConsulta.addField(field);
        }

        // Copiar código de respuesta
        detailConsulta.setResponse(detailRespuesta.getResponse());

        // Borrar datos borrados
        for (Pair<String, Record> pair : clearRecordFields) {
            clear(webPage, pair.getFirst(), pair.getSecond().getNumber());

            Record recordConsulta = pair.getSecond();
            Table tableConsulta = (Table) recordConsulta.getParent();
            tableConsulta.removeRecord(((List) tableConsulta.getRecords()).indexOf(recordConsulta));
        }

        // Renumerar los records
        for (Table table : detailConsulta.getTables()) {
            int i = 0;
            for (Record record : table.getRecords()) {
                record.setNumber(i++);
            }
        }

        for (DeleteRecord deleteRecord : IterableWebElement.get(
                webPage, DeleteRecord.class)) {
            deleteRecord.getFieldData().resetAll();
        }
    }

    private Table getTable(WebPage webPage, Detail detailConsulta, Table table,
            DetailField field) {
        if (webPage.getLegacy()) {
            return Conversor.getTable(detailConsulta, table.getAlias());
        } else {
            return Conversor.getTable(detailConsulta, field.getAlias());
        }
    }

    private void clear(WebPage webPage, final String alias, int record) {
        Predicate depencyTreeContainsAlias = new Predicate() {

            @Override
            public boolean evaluate(Object object) {
                return ((NodoDependencia) object).getAlias().equals(alias);
            }

        };

        for (FormElement formElement : IterableWebElement.get(
                webPage, FormElement.class)) {
            DataSource dataSource = formElement.getDataSource();
            FieldData fieldData = formElement.getFieldData();

            if (dataSource.esRegistro() && dataSource.getAlias().equals(alias)) {
                fieldData.resetAll(record);

            } else if (dataSource.esDescripcion()
                    && CollectionUtils.exists((Collection) EntornoWeb.
                    getContexto().getArbolDependencias().getNodosConectados(
                    alias), depencyTreeContainsAlias)) {
                fieldData.resetAll(record);

                // FIXME en este punto no deberï¿½a moverse datos de un registro a otro
                if (dataSource.getType() == DataSourceType.DESCRIPTION) {
                    List<String> valoresActuales = fieldData.getValues();
                    List<String> valoresIniciales = fieldData.getValuesIniciales();
                    List<String> valoresConsulta = fieldData.getValuesConsulta();
                    List<FieldData.Error> errores = fieldData.getErrors();

                    for (int i = record; i < valoresActuales.size() - 1; i++) {
                        valoresActuales.set(i, valoresActuales.get(i + 1));
                    }
                    valoresActuales.set(valoresActuales.size() - 1,
                            valoresIniciales.get(valoresActuales.size() - 1));

                    if (!valoresConsulta.isEmpty()) {
                        for (int i = record; i < valoresConsulta.size() - 1; i++) {
                            valoresConsulta.set(i, valoresConsulta.get(i + 1));
                        }
                        valoresConsulta.remove(valoresConsulta.size() - 1);
                    }

                    for (int i = record; i < errores.size() - 1; i++) {
                        errores.set(i, errores.get(i + 1));
                    }
                    errores.set(errores.size() - 1, new FieldData.Error());

                    fieldData.setValues(valoresActuales);
                    fieldData.setValuesConsulta(valoresConsulta);
                    fieldData.setErrors(errores);
                }
            }
        }
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        new Consulta().onError(pedido, respuesta, mensaje,
                mensajeUsuario, stackTrace, datos);
    }

}
