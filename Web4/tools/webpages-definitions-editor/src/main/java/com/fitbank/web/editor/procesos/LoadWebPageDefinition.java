package com.fitbank.web.editor.procesos;

import com.fitbank.js.GeneradorJS;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.providers.HardDiskWebPageProvider;
import com.fitbank.webpages.definition.WebPageDefinition;
import com.fitbank.webpages.definition.WebPageDefinitionXml;

@Handler(EditorRequestTypes.LOAD_WEBPAGE)
public class LoadWebPageDefinition implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        TransporteDB transporteDB = pedido.getTransporteDB();
        String uri = HardDiskWebPageProvider.getPath(transporteDB
                .getSubsystem(), transporteDB
                .getTransaction(), "wpd");

        WebPageDefinition webPageDefinition;

        try {
            webPageDefinition = WebPageDefinitionXml.load(uri);
        } catch (ExcepcionParser e) {
            throw new ErrorWeb(e);
        }

        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.setContenido(GeneradorJS.toJS(webPageDefinition, true),
                "text/javascript");

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.setContenido("{}", "text/javascript");
    }

}
