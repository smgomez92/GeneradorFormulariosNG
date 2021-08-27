package com.fitbank.web.simulado.procesos;

import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

@Handler(GeneralRequestTypes.INGRESO)
public class Ingreso implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        pedido.getTransporteDB().setUser("Simulado");

        EntornoWeb.setTransporteDBBase(pedido.getTransporteDB());

        String pt = pedido.getValorRequestHttp("pt");

        if (pt != null) {
            pedido.redireccionar(EntornoWeb.URI_ENTORNO + "#1/" + pt);
        } else {
            pedido.redireccionar(EntornoWeb.URI_ENTORNO);
        }

        return new RespuestaWeb(pedido);
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

}
