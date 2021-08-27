package com.fitbank.web.procesos;

import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

/**
 * 
 * @author FitBank
 * @version 2.0
 */
@Handler(GeneralRequestTypes.RECARGAR)
@RevisarSeguridad
public class RecargaFormulario implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        // FIXME arreglar para no tener que llamar de nuevo a CargarFormulario y reusar el formulario ya cargado
        return new CargaFormulario().procesar(pedido);
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        new CargaFormulario().onError(pedido, respuesta, mensaje, mensajeUsuario,
                stackTrace, datos);
    }

}
