package com.fitbank.web.editor.procesos;

import com.fitbank.js.GeneradorJS;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.Paginacion;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;
import com.fitbank.web.json.TransporteWeb;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.definition.WebPageDefinition;
import com.fitbank.webpages.definition.WebPageDefinitionCompiler;

@Handler(EditorRequestTypes.PREVIEW_WEBPAGE)
public class Preview implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        WebPageDefinition form = GeneradorJS.toJava(pedido
                .getValorRequestHttp("json"), WebPageDefinition.class);

        WebPageEnviroment.setDebug(true);
        WebPage webPage = WebPageDefinitionCompiler.compile(form);
        WebPageEnviroment.setDebug(false);

        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        TransporteWeb transporte = new TransporteWeb(respuesta, webPage);

        EntornoWeb.getContexto().getTransporteDBBase().setSubsystem(
                respuesta.getTransporteDB().getSubsystem());
        EntornoWeb.getContexto().getTransporteDBBase().setTransaction(
                respuesta.getTransporteDB().getTransaction());
        EntornoWeb.getContexto().setWebPage(webPage);
        EntornoWeb.getContexto().setPaginacion(new Paginacion());

        respuesta.setContenido(transporte);

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.setContenido("{}", "text/javascript");
    }

}
