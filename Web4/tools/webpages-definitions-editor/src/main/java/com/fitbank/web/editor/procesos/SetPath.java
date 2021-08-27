package com.fitbank.web.editor.procesos;

import com.fitbank.web.Proceso;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;
import com.fitbank.web.providers.HardDiskWebPageProvider;

@Handler(EditorRequestTypes.SET_PATH)
public class SetPath implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        String path = pedido.getValorRequestHttp("path");
        HardDiskWebPageProvider.setBasePath(path);

        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.setContenido(HardDiskWebPageProvider.getBasePath());

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

}
