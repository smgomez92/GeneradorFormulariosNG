package com.fitbank.web.editor.procesos;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.fitbank.schemautils.Schema;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;

@Handler(EditorRequestTypes.TABLE)
public class GetTableInformation implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.setContenido(JSONSerializer.toJSON(Schema.get().getTables()
                .get(pedido.getValorRequestHttp("table"))));

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        JSONObject json = new JSONObject();
        json.put("tables", new String[0]);
        respuesta.setContenido(json);
    }

}
