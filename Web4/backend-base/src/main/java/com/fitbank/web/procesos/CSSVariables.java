package com.fitbank.web.procesos;

import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import org.apache.commons.lang.StringUtils;

/**
 * Proceso que imprime variables para cambiar el aspecto de la aplicación.
 *
 * @author FitBank CI
 */
@Handler(GeneralRequestTypes.VARIABLES_CSS)
public class CSSVariables implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        String path = ParametrosWeb.getValueString(CSSVariables.class, "path");
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        if (StringUtils.isNotBlank(path)) {
            pedido.redireccionar(path);
        } else {
            respuesta.setContenido("/* No se especificó un reemplazo de variables.css */", "text/css");
        }

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta, String mensaje,
            String mensajeUsuario, String stackTrace, TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

}
