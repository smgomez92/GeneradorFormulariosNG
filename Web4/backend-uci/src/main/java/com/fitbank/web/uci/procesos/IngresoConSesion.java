package com.fitbank.web.uci.procesos;

import org.apache.commons.lang.StringUtils;

import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.enums.MessageType;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ManejoExcepcion;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.MensajeWeb;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;

@Handler(GeneralRequestTypes.INGRESO_SESION)
public class IngresoConSesion implements Proceso {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        boolean cierre = pedido.getValorRequestHttp("cierre") != null;

        pedido.getTransporteDB().setMessageType(MessageType.SIGN_ON);
        pedido.getTransporteDB().setSubsystem("01");
        pedido.getTransporteDB().setTransaction("0000");
        pedido.getTransporteDB().setVersion("01");

        if (StringUtils.isBlank(pedido.getTransporteDB().getUser())) {
            throw new MensajeWeb("Usuario no ingresado");
        }

        pedido.getTransporteDB().setPassword("");

        Detail detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();

        detail.addField(new Field("_CLOSE_ACTIVE_SESSIONS", cierre ? "1" : "0"));

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        TransporteDBUCI tdbuci = (TransporteDBUCI) respuesta.getTransporteDB();

        checkOkCodes(tdbuci, respuesta, pedido);

        return respuesta;
    }

    /**
     * Revisa que el codigo de respuesta sea positivo o de error
     *
     * @param detail
     * @param respuesta
     */
    public void checkOkCodes(TransporteDBUCI tdbuci, RespuestaWeb respuesta,
            PedidoWeb pedido) throws MensajeWeb {
        Detail detail = tdbuci.getDetail();
        String codigo = detail.getResponse().getCode();

        if (ManejoExcepcion.isError(codigo)) {
            pedido.getHttpServletRequest().getSession().invalidate();

            throw new MensajeWeb(respuesta);
        } else {
            tdbuci.readFields();

            respuesta.getTransporteDB().setPassword(null);
            detail.removeFields();

            EntornoWeb.setTransporteDBBase(respuesta.getTransporteDB());
        }
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

}
