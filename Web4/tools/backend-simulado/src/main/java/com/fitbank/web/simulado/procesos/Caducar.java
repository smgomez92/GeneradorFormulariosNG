package com.fitbank.web.simulado.procesos;

import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

@Handler(GeneralRequestTypes.CADUCAR)
public class Caducar implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        pedido.getHttpServletRequest().getSession().invalidate();

        return new RespuestaWeb(pedido);
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

}