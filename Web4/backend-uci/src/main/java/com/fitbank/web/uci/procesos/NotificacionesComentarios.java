package com.fitbank.web.uci.procesos;

import com.fitbank.dto.GeneralResponse;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.util.Pair;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.Notification;
import com.fitbank.web.data.NotificationItem;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.uci.Conversor;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import java.util.List;
import net.sf.json.JSONSerializer;
import org.apache.commons.lang.StringUtils;

/**
 * Clase que consulta los comentarios de una cuenta o persona bajo la peticion
 * de consulta enviada recientemente.
 *
 * @author SoftWare House S.A.
 */
@Handler(GeneralRequestTypes.NOTIFCOM)
public class NotificacionesComentarios extends Notificaciones {

    /**
     * Método que consulta las notificaciones que tiene la cuenta
     * @param pedido
     * @return
     */
    @Override
    public RespuestaWeb consulta(PedidoWeb pedido) {
        Detail detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();
        String idParentContext = pedido.getHttpServletRequest().getParameter("_contextoPadre");
        Detail parentDetail = ((TransporteDBUCI) EntornoWeb.getContexto(idParentContext).getTransporteDBBase()).getDetail();

        // Eliminar el resto de tablas del mensaje (en caso de haberlas)
        detail.removeTables();
        detail.removeFields();

        // Establecer los campos necesarios para consultar notificaciones en UCI
        if (parentDetail != null) {
            List<String> fieldNames = ParametrosWeb.getValueStringList(ConsultaListaValores.class, "NOTIFICATION_FIELD");
            for (String fieldName : fieldNames) {
                Pair<String, String> pair = Conversor.findFieldInDetail(parentDetail, fieldName);
                String fieldRealName = pair.getFirst();
                String fieldValue = pair.getSecond();

                if (StringUtils.isNotBlank(fieldValue)) {
                    //Ignorar específicamente el campo CPERSONA_COMPANIA y CPERSONA_USUARIO
                    if ("CPERSONA_COMPANIA".equals(fieldRealName) 
                            || "CPERSONA_USUARIO".equals(fieldRealName)) {
                        continue;
                    }

                    detail.findFieldByNameCreate(fieldName).setValue(fieldValue);
                }
            }
        }

        // Enviar peticion a UCI
        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        Detail detailRespuesta = ((TransporteDBUCI) respuesta.getTransporteDB()).getDetail();

        if (detailRespuesta.getResponse().getCode().equals(GeneralResponse.OK)) {
            EntornoWeb.getContexto().setTransporteDBBase(respuesta.getTransporteDB());
            Notification notification = new Notification();

            for (Table table : detailRespuesta.getTables()) {
                //Ignorar la tabla de notificaciones de usuarios
                if (Notificaciones.ALIAS.equals(table.getAlias())) {
                    continue;
                }

                for (Record record : table.getRecords()) {
                    NotificationItem notificationItem = new NotificationItem();

                    notificationItem.setMensaje(
                            record.findFieldByNameCreate("comentario").getStringValue());
                    notificationItem.setCcuenta(
                            record.findFieldByNameCreate("ccuenta").getStringValue());
                    notificationItem.setIdentificacion(
                            detailRespuesta.findFieldByNameCreate("IDENTIFICACION").getStringValue());
                    notification.addItem(notificationItem);
                }
            }
            respuesta.setContenido(JSONSerializer.toJSON(notification));
        } else {
            throw new ErrorWeb(respuesta.getTransporteDB());
        }

        return respuesta;
    }
}
