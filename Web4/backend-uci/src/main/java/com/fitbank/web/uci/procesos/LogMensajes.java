package com.fitbank.web.uci.procesos;

import java.sql.Timestamp;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.MessageType;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.uci.Conversor;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;

/**
 * Envia los mensajes a ser guardados.
 *
 * @author FitBank CI
 */
@Handler(GeneralRequestTypes.LOG_MENSAJES)
public class LogMensajes implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        String logs = pedido.getValorRequestHttp("logs");
        JSONArray logsJSON = (JSONArray) JSONSerializer.toJSON(logs);

        TransporteDB transporteDB = pedido.getTransporteDB();
        transporteDB.setMessageType(MessageType.STORE);

        Detail detail = ((TransporteDBUCI) transporteDB).getDetail();

        // Se usa la transaccion de listas de valores
        detail.setSubsystem("01");
        detail.setTransaction("0003");
        detail.setVersion("01");

        Table tlogmensajes = detail.findTableByExample(new Table("TLOGMENSAJES", "TLOGMENSAJES"));

        for (Object object : logsJSON) {
            JSONObject json = (JSONObject) object;
            String tipo = getTipo(json);

            if (tipo == null) {
                continue;
            }

            Record record = tlogmensajes.findRecordByExample(new Record());

            record.findFieldByNameCreate("NUMEROMENSAJE").setValue(json.get("messageId"));
            record.findFieldByNameCreate("FREAL").setValue(new Timestamp(json.getLong("realDate")));
            record.findFieldByNameCreate("CCANAL").setValue(transporteDB.getChannel());
            record.findFieldByNameCreate("FCONTABLE").setValue(transporteDB.getAccountingDate());
            record.findFieldByNameCreate("ESTADO").setValue("TER");
            record.findFieldByNameCreate("RESULTADO").setValue(json.get("result"));
            record.findFieldByNameCreate("CODIGORESULTADO").setValue(json.get("code"));
            // FIXME Se está guardando el tiempo en INFORMACIONADICIONAL para no sobreescribir el tiempo del uci
            //record.findFieldByNameCreate("TIEMPO").setValue(json.get("time"));
            record.findFieldByNameCreate("INFORMACIONADICIONAL").setValue(json.get("time"));
            record.findFieldByNameCreate("TIPOMENSAJE").setValue(tipo);
            record.findFieldByNameCreate("CSUBSISTEMA").setValue(json.get("subsystem"));
            record.findFieldByNameCreate("CTRANSACCION").setValue(json.get("transaction"));
            record.findFieldByNameCreate("VERSIONTRANSACCION").setValue("01");
            record.findFieldByNameCreate("CUSUARIO").setValue(transporteDB.getUser());
            record.findFieldByNameCreate("CTERMINAL").setValue(transporteDB.getTerminal());
            record.findFieldByNameCreate("SESION").setValue(transporteDB.getSessionId());
        }

        RespuestaWeb respuesta;
        if (tlogmensajes.getRecordCount() == 0) {
            respuesta = new RespuestaWeb(pedido);
        } else {
            respuesta = new EnlaceUCI().procesar(pedido);
            Conversor.checkOkCodes(((TransporteDBUCI) respuesta.getTransporteDB()).getDetail(), respuesta);
        }

        respuesta.setContenido("OK");

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta, String mensaje,
            String mensajeUsuario, String stackTrace, TransporteDB datos) {
        respuesta.setContenido("Error: " + mensaje);
    }

    private String getTipo(JSONObject o) {
        String tipo = o.getString("type");

        if (tipo.endsWith(GeneralRequestTypes.CONSULTA)) {
            return MessageType.QUERY.getValue();
        } else if (tipo.endsWith(GeneralRequestTypes.MANTENIMIENTO)) {
            return MessageType.STORE.getValue();
        } else if (tipo.endsWith(GeneralRequestTypes.FORM)) {
            return MessageType.FORM.getValue();
        }

        return null;
    }

}
