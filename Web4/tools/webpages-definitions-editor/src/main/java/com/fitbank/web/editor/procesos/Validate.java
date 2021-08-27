package com.fitbank.web.editor.procesos;


import net.sf.json.JSONArray;

import com.fitbank.web.EntornoWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;
import com.fitbank.webpages.util.ValidationUtils;

@Handler(EditorRequestTypes.VALIDATE)
public class Validate implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.setContenido(JSONArray.fromObject(
                ValidationUtils.validate(EntornoWeb.getContexto().getWebPage())));

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.setContenido(stackTrace);
    }

}
