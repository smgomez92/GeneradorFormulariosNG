package com.fitbank.web.procesos;

import com.fitbank.util.Debug;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

@Handler(GeneralRequestTypes.LOG)
public class LoggerWeb implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        String severidad = pedido.getValorRequestHttp("severidad");
        String mensaje = pedido.getValorRequestHttp("mensaje");

        if ("LOG".equals(severidad)) {
            Debug.info(severidad + ": " + mensaje);
        } else if ("DEBUG".equals(severidad)) {
            Debug.info(severidad + ": " + mensaje);
        } else if ("INFO".equals(severidad)) {
            Debug.info(severidad + ": " + mensaje);
        } else if ("WARNING".equals(severidad)) {
            Debug.warn(mensaje);
        } else if ("ERROR".equals(severidad)) {
            Debug.error(mensaje);
        }

        RespuestaWeb respuesta = new RespuestaWeb(pedido.getTransporteDB(), pedido);

        respuesta.getTransporteDB().cleanResponse();

        respuesta.setContenido("<OK/>", "text/xml");

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

}
