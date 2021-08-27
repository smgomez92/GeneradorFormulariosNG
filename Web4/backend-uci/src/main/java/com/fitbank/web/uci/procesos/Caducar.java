package com.fitbank.web.uci.procesos;

import com.fitbank.enums.MessageType;
import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.uci.EnlaceUCI;

/**
 * Servlet que se va a encargar de caducar la sesión
 * 
 * @author FitBank
 * @version 2.0
 */
@Handler(GeneralRequestTypes.CADUCAR)
public class Caducar implements Proceso {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        if (!EntornoWeb.existeUsuario()) {
            return new RespuestaWeb(pedido, true);
        }

        Debug.info("Inicio proceso Caducar");
        pedido.getTransporteDB().setMessageType(MessageType.STORE);
        pedido.getTransporteDB().setSubsystem("00");
        pedido.getTransporteDB().setTransaction("9999");
        pedido.getTransporteDB().setVersion("01");

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);
        Debug.info("Respuesta de UCI caducar recibida");

        String idSesionAntigua = pedido.getHttpServletRequest().getSession().getId();
        EntornoWeb.resetContextos();
        pedido.getHttpServletRequest().getSession().invalidate();
        Debug.info("Sesión invalidada: " + idSesionAntigua);

        //No necesitamos controlar los errores de este tipo de peticiones
        respuesta.setContenido("");

        return respuesta;
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
    }
}
