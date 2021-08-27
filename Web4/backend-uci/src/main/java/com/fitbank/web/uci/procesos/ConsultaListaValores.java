package com.fitbank.web.uci.procesos;

import com.fitbank.dto.management.Criterion;
import com.fitbank.dto.management.CriterionType;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.DataSourceType;
import com.fitbank.enums.MessageType;
import com.fitbank.enums.EjecutadoPor;
import com.fitbank.js.GeneradorJS;
import com.fitbank.util.Pair;
import com.fitbank.web.Contexto;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ManejoExcepcion;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.json.ItemListaValores;
import com.fitbank.web.json.TransporteListaValores;
import com.fitbank.web.uci.Conversor;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.assistants.ListOfValues;
import com.fitbank.webpages.assistants.lov.LOVField;
import com.fitbank.webpages.util.ArbolDependencias;
import java.util.List;
import org.apache.commons.lang.StringUtils;


@Handler(GeneralRequestTypes.LV)
@RevisarSeguridad
public class ConsultaListaValores implements Proceso {

    @Override
    public RespuestaWeb procesar(PedidoWeb request) {
        ListOfValues lv = GeneradorJS.toJava(request.getValorRequestHttp("_lv"), ListOfValues.class);

        String idContextoPadre = request.getValorRequestHttp("_contexto_padre");
        Contexto contexto = EntornoWeb.getContexto(idContextoPadre);

        request.sync(contexto);
        WebPage webPage = contexto.getWebPage();
        boolean legacy = webPage.getLegacy();
        EjecutadoPor executedBy = webPage.getEjecutadoPor();
        Integer timeout = webPage.getTimeout();
        webPage.setLegacy(lv.getLegacy());
        webPage.setTimeout(lv.getTimeout());
        webPage.setEjecutadoPor(lv.getExecutedBy());

        request.getTransporteDB().setMessageType(MessageType.QUERY);

        Detail detail = ((TransporteDBUCI) request.getTransporteDB()).getDetail();
        Detail detailPadre = ((TransporteDBUCI) contexto.getTransporteDBBase()).getDetail();

        detail.setSubsystem(lv.getSubsystem());
        detail.setTransaction(lv.getTransaction());
        detail.setVersion(lv.getVersion());
        detail.setCompany(detailPadre.getCompany());
        detail.setOriginBranch(detailPadre.getOriginBranch());
        detail.setOriginOffice(detailPadre.getOriginOffice());
        detail.setProcessType(legacy ? "Legacy" : "Join");
        this.setParentDetailData(detail, detailPadre);

        EntornoWeb.getContexto().setArbolDependencias(new ArbolDependencias(lv.getReferences()));

        limpiarDetail(detail);
        llenarTablas(webPage, detail, lv);
        llenarOrigen(detail, request.getValorRequestHttp("_transaccion_origen"));
        Conversor.copiarDependencias(webPage, detail);

        RespuestaWeb respuesta = new EnlaceUCI(lv.isSaveResponseInCache()).procesar(request);

        Detail detailSalida = ((TransporteDBUCI) respuesta.getTransporteDB()).getDetail();

        // Verificar si existe informacion de una cuenta/persona en el detail de salida
        boolean searchComments = false;
        List<String> fieldNames = ParametrosWeb.getValueStringList(ConsultaListaValores.class, "NOTIFICATION_FIELD");
        for (String fieldName : fieldNames) {
            Pair<String, String> pair = Conversor.findFieldInDetail(detailSalida, fieldName);
            String fieldRealName = pair.getFirst();
            String fieldValue = pair.getSecond();

            //Ignorar el cpersona_usuario y cperson_compania especificamente
            if ("CPERSONA_COMPANIA".equals(fieldRealName) 
                    || "CPERSONA_USUARIO".equals(fieldRealName)) {
                continue;
            }

            searchComments = searchComments || StringUtils.isNotBlank(fieldValue);
        }

        webPage.setLegacy(legacy);
        webPage.setEjecutadoPor(executedBy);
        webPage.setTimeout(timeout);

        ManejoExcepcion.checkOkCodes(respuesta);

        TransporteListaValores transporte = new TransporteListaValores(respuesta, searchComments);

        llenar(lv, transporte, detailSalida);

        respuesta.setContenido(transporte);

        EntornoWeb.getContexto().setTransporteDBBase(respuesta.getTransporteDB());

        return respuesta;
    }

    /**
     * Construye las tablas y llena con los valores del formulario.
     *
     * @param webPage
     * @param detail
     */
    private void llenarTablas(WebPage webPage, Detail detail, ListOfValues lv) {
        Conversor.crearTablas(webPage, detail);
        boolean isDefaultLoV = this.isDefaultLoV(lv);

        for (LOVField field : lv.getFields()) {
            if (field.esControl()) {
                detail.addField(new Field(field.getField(), field.getValue()));

            } else if (field.esRegistro() || field.esCriterio() || field.esOrden()) {
                Table table = Conversor.getTable(detail, field.getAlias());
                String alias = lv.getLegacy() ? null : field.getAlias();

                Criterion criterion = new Criterion(alias, field.getField(), field.getValue());
                criterion.setCondition(Conversor.filterCondition(field.
                        getComparator(table.isSpecial() || table.isReadonly()), webPage));

                if (field.esOrden()) {
                    criterion.setType(CriterionType.ORDER);
                    criterion.setOrder(field.getOrder());
                }

                if (field.esRegistro()) {
                    table.findRecordByNumber(0).addField(
                            new Field(alias, field.getField(), null));

                    if (field.getVisible() || !isDefaultLoV 
                            || table.isSpecial() || table.isReadonly()) {
                        table.addCriterion(criterion);
                    }
                } else {
                    table.addCriterion(criterion);
                }

                table.setRequestedRecords(lv.getNumberOfRecords());
                table.setPageNumber(lv.getPagina());
            }
        }
    }

    /**
     * LLena la respuesta.
     *
     * @param lv
     * @param transporte
     * @param detail
     */
    private void llenar(ListOfValues lv, TransporteListaValores transporte,
            Detail detail) {
        transporte.setPaginacion(true);

        for (LOVField lovfield : lv.getFields()) {
            if (usar(lovfield)) {
                Table table = Conversor.getTable(detail, lovfield.getAlias());

                for (Record record : table.getRecords()) {
                    ItemListaValores valores = transporte.get(record.getNumber());
                    Field field = record.findFieldByAlias(lovfield.getAlias(),
                            lovfield.getField());
                    valores.getValues().add(field == null || field.getValue()
                            == null ? "" : field.getValue().toString());
                }

                transporte.setPaginacion(table.getHasMorePages().equals("1"));
            } else {
                if (lovfield.getType() != null && lovfield.getType().compareTo(DataSourceType.CRITERION_CONTROL) == 0) {
                    Object value = detail.findFieldByNameCreate(lovfield.getField()).getValue();
                    transporte.getControl().put(lovfield.getElementName(), value != null ? value.toString() : value);
                }
            }
        }
    }

    private boolean usar(LOVField lovfield) {
        // Esto debe coincidir con el js de ListOfValues#_usar
        return lovfield.esRegistro();
    }

    private void setParentDetailData(Detail pDetail, Detail parentDetail) {
        Field fSubsystem = pDetail.findFieldByName("CSUBSISTEMA_PADRE");

        if (!this.fieldHasValue(fSubsystem)) {
            pDetail.findFieldByNameCreate("CSUBSISTEMA_PADRE").setValue(parentDetail.getSubsystem());
        }

        Field fTransaction = pDetail.findFieldByName("CTRANSACCION_PADRE");

        if (!this.fieldHasValue(fTransaction)) {
            pDetail.findFieldByNameCreate("CTRANSACCION_PADRE").setValue(
                    parentDetail.getTransaction());
        }

        Field tVersion = pDetail.findFieldByName("VERSIONTRANSACCION_PADRE");

        if (!this.fieldHasValue(tVersion)) {
            pDetail.findFieldByNameCreate("VERSIONTRANSACCION_PADRE").setValue(
                    parentDetail.getVersion());
        }
    }

    private boolean fieldHasValue(Field field) {
        return field != null && field.getValue() != null 
                && StringUtils.isNotBlank(field.getStringValue());
    }

    /**
     * Limpia el detail.
     *
     * @param detail
     */
    private static void limpiarDetail(Detail detail) {
        detail.removeTables();
        detail.removeFields();
    }

    /**
     * Indica si un request de ListaDeValores usa la transacción por defecto
     * para Listas de Valores.
     * 
     * @param lv Lista de Valores del request
     * @return true si usa un código de transacción definida por default para LoV.
     */
    private boolean isDefaultLoV(ListOfValues lv) {
        String subsistema = ParametrosWeb.getValueString(ConsultaListaValores.class, "subsistema");
        String transaccion = ParametrosWeb.getValueString(ConsultaListaValores.class, "transaccion");
        String version = ParametrosWeb.getValueString(ConsultaListaValores.class, "version");

        return subsistema.equals(lv.getSubsystem()) && transaccion.equals(lv.getTransaction()) && version.equals(lv.getVersion());
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        new Consulta().onError(pedido, respuesta, mensaje, mensajeUsuario, stackTrace, datos);
    }

    /**
     * Construye las tablas y llena con los valores del formulario.
     *
     * @param detail
     */
    private void llenarOrigen(Detail detail, String origen) {
        detail.findFieldByNameCreate("_LV_TRANSACCION_ORIGEN").setValue(origen);
    }
}