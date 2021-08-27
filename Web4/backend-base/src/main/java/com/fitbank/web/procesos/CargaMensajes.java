package com.fitbank.web.procesos;

import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.MensajesWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

@Handler(GeneralRequestTypes.MENSAJES_JS)
public class CargaMensajes implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.getTransporteDB().cleanResponse();

        respuesta.setContenido(MensajesWeb.getMensajesJSON(), "text/javascript");

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
    }

}
