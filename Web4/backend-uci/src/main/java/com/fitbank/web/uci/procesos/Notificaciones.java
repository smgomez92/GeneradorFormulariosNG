package com.fitbank.web.uci.procesos;

import net.sf.json.JSONSerializer;

import com.fitbank.common.helper.FormatDates;
import com.fitbank.dto.GeneralResponse;
import com.fitbank.dto.management.Criterion;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.MessageType;
import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ManejoExcepcion;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.Notification;
import com.fitbank.web.data.NotificationItem;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.formatters.DateFormatter;
import java.util.Iterator;

@Handler(GeneralRequestTypes.NOTIF)
@RevisarSeguridad
public class Notificaciones implements Proceso {

    protected static final String ALIAS = "tusuariosnotificaciones";

    public RespuestaWeb procesar(PedidoWeb pedido) {
        String tipoPedido = pedido.getValorRequestHttp("notificacion");

        pedido.getTransporteDB().setMessageType(
                tipoPedido.equals(GeneralRequestTypes.CONSULTA)
                ? MessageType.QUERY
                : MessageType.STORE);
        String origSubsystem = pedido.getTransporteDB().getSubsystem();
        String origTransaction = pedido.getTransporteDB().getTransaction();
        String origVersion = pedido.getTransporteDB().getVersion();

        pedido.getTransporteDB().setSubsystem("01");
        pedido.getTransporteDB().setTransaction("0006");
        pedido.getTransporteDB().setVersion("01");

        RespuestaWeb respuesta;

        if (pedido.getTransporteDB().getMessageType() == MessageType.QUERY) {
            respuesta = consulta(pedido);
        } else {
            respuesta = mantenimiento(pedido);
        }

        // Setear el codigo de transaccion original a la respuesta
        respuesta.getTransporteDB().setSubsystem(origSubsystem);
        respuesta.getTransporteDB().setTransaction(origTransaction);
        respuesta.getTransporteDB().setVersion(origVersion);

        // Setear el codigo de transaccion original al pedido
        pedido.getTransporteDB().setSubsystem(origSubsystem);
        pedido.getTransporteDB().setTransaction(origTransaction);
        pedido.getTransporteDB().setVersion(origVersion);

        return respuesta;
    }

    /**
     * Método que consulta las notificaciones que tiene el usuario
     *
     * @param pedido
     * @return
     */
    public RespuestaWeb consulta(PedidoWeb pedido) {
        Detail detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();
        detail.removeTables();
        detail.removeFields();

        Table table = new Table("TUSUARIONOTIFICACIONES", ALIAS);
        detail.addTable(table);

        table.getCriteria().clear();
        table.clearRecords();
        table.setRequestedRecords(100);
        table.setPageNumber(1);

        Criterion criterioPersona = new Criterion(ALIAS, "cpersona_compania", detail.getCompany());
        criterioPersona.setCondition("=");
        Criterion criterioUsuario = new Criterion(ALIAS, "cusuario", detail.getUser());
        criterioUsuario.setCondition("=");

        table.addCriterion(criterioPersona);
        table.addCriterion(criterioUsuario);

        Criterion criterioFProceso = new Criterion(ALIAS, "fproceso", null);
        criterioFProceso.setCondition("IS NULL");
        table.addCriterion(criterioFProceso);

        Record recordConsulta = table.findRecordByExample(new Record(0));

        recordConsulta.addField(new Field(ALIAS, "csubsistema", null));
        recordConsulta.addField(new Field(ALIAS, "ctransaccion", null));
        recordConsulta.addField(new Field(ALIAS, "versiontransaccion", null));
        recordConsulta.addField(new Field(ALIAS, "textonotificacion", null));
        recordConsulta.addField(new Field(ALIAS, "fnotificacion", null));
        recordConsulta.addField(new Field(ALIAS, "fproceso", null));
        recordConsulta.addField(new Field(ALIAS, "numeromensaje", null));

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        Detail detailRespuesta = ((TransporteDBUCI) respuesta.getTransporteDB()).getDetail();

        if (detailRespuesta.getResponse().getCode().equals(GeneralResponse.OK)) {
            EntornoWeb.getContexto().setTransporteDBBase(respuesta.getTransporteDB());
            Notification notification = new Notification();

            Table tableRespuesta = detailRespuesta.findTableByAlias(ALIAS);
            if (tableRespuesta != null && tableRespuesta.getRecordCount() > 0) {
                for (Record record : tableRespuesta.getRecords()) {
                    try {
                        NotificationItem notificationItem = new NotificationItem();

                        notificationItem.setSubsistema(
                                record.findFieldByAlias(ALIAS, "csubsistema").getValue().toString());
                        notificationItem.setTransaccion(
                                record.findFieldByAlias(ALIAS, "ctransaccion").getValue().toString());

                        if (record.findFieldByAlias(ALIAS, "versiontransaccion").getValue() != null) {
                            notificationItem.setVersion(
                                    record.findFieldByAlias(ALIAS, "versiontransaccion").getValue().toString());
                        }

                        if (record.findFieldByAlias(ALIAS, "numeromensaje").getValue() != null) {
                            notificationItem.setNumeroMensaje(
                                    record.findFieldByAlias(ALIAS, "numeromensaje").getValue().toString());
                        }

                        notificationItem.setMensaje(
                                record.findFieldByAlias(ALIAS, "textonotificacion").getValue().toString());
                        notificationItem.setFechaNotificacion(
                                new DateFormatter().getDate(
                                        record.findFieldByAlias(ALIAS, "fnotificacion").getValue().toString()));
                        notificationItem.setRegistro(record.getNumber());

                        notification.addItem(notificationItem);
                    } catch (Exception e) {
                        Debug.error("Problemas al obtener los registros de la respuesta", e);
                    }
                }
            }

            respuesta.setContenido(JSONSerializer.toJSON(notification));
        } else {
            throw new ErrorWeb(respuesta.getTransporteDB());
        }

        return respuesta;
    }

    /**
     * Método que ejecuta el mantenimiento de las notificaciones
     *
     * @param pedido
     * @return
     * @throws java.lang.Exception
     */
    public RespuestaWeb mantenimiento(PedidoWeb pedido) {
        Detail detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();
        Table table = detail.findTableByAlias(ALIAS);

        //Borrar todas las tablas que no sean de notificacion de usuarios
        if (table != null) {
            Iterator<Table> it = detail.getTables().iterator();
            while (it.hasNext()) {
                Table tableIt = it.next();
                if (ALIAS.equals(tableIt.getAlias())) {
                    continue;
                }

                it.remove();
            }
        }

        int numeroregistro = pedido.getValorRequestHttpInt("item");

        Record recordMantenimiento = ((TransporteDBUCI) EntornoWeb.getContexto().getTransporteDBBase()).getDetail().findTableByAlias(ALIAS).findRecordByNumber(
                numeroregistro);

        try {
            recordMantenimiento.findFieldByAlias(ALIAS, "fproceso").setValue(
                    FormatDates.getDefaultExpiryTimestamp());
        } catch (Exception e) {
            Debug.error("Problemas al obtener la fecha del proceso", e);
        }

        if (table != null) {
            table.addRecord(recordMantenimiento);
        }

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        ManejoExcepcion.checkOkCodes(respuesta);
        respuesta.setContenido("OK");

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.setContenido(JSONSerializer.toJSON(new Notification()));
    }
}
