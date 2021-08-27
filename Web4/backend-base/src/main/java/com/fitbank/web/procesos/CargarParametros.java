package com.fitbank.web.procesos;

import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

/**
 * Clase que permite poner variables arbitrarias en el entorno. Éstas variables
 * pueden obtenerse mediante la sintaxis Parametros["fitbank......NOMBRE_VARIABLE"] desde javascript.
 * El valor de las variables se encuentra en el archivo parametros.properties, usando como
 * prefijo el paquete de la clase js. Por ejemplo, fitbank.ui.tooltips.NOMBRE_VARIABLE=valor.
 * Para que estos parámetros estén disponibles desde el entorno, el paquete de la clase
 * debe comenzar con fitbank.
 *
 * @author Fitbank RB
 */
@Handler(GeneralRequestTypes.PARAMETROS_JS)
public class CargarParametros implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.getTransporteDB().cleanResponse();

        respuesta.setContenido(ParametrosWeb.obtenerParametrosJS(), "text/javascript");

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta, String mensaje, String mensajeUsuario, String stackTrace, TransporteDB datos) {
        
    }

}
