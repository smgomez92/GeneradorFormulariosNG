package com.fitbank.web;

import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

public interface Proceso {

    RespuestaWeb procesar(PedidoWeb pedido);

    void onError(PedidoWeb pedido, RespuestaWeb respuesta, String mensaje,
            String mensajeUsuario, String stackTrace, TransporteDB datos);

}
