package com.fitbank.web.editor.procesos;

import com.fitbank.web.EntornoWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.IterableWebElement;
import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.json.JSONArray;

@Handler(EditorRequestTypes.DATASOURCES_TABLE)
public class DataSourcesTable implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        Map<String, DataSource> dataSources = new LinkedHashMap<String, DataSource>();
        for (FormElement formElement : IterableWebElement.get(EntornoWeb.
                getContexto().getWebPage(), FormElement.class)) {
            dataSources.put(formElement.getHTMLId(), formElement.getDataSource());
        }

        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.setContenido(JSONArray.fromObject(dataSources));

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.setContenido(stackTrace);
    }

}
