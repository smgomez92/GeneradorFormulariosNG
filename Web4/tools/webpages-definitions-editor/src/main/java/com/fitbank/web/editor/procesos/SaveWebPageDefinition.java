package com.fitbank.web.editor.procesos;

import com.fitbank.js.GeneradorJS;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;
import com.fitbank.web.providers.HardDiskWebPageProvider;
import com.fitbank.webpages.definition.WebPageDefinition;
import com.fitbank.webpages.definition.WebPageDefinitionXml;
import java.io.File;

@Handler(EditorRequestTypes.SAVE_WEBPAGE)
public class SaveWebPageDefinition implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        WebPageDefinition form = GeneradorJS.toJava(pedido.getValorRequestHttp("json"), WebPageDefinition.class);

        String uriWPD = HardDiskWebPageProvider.getPath(form.getSubsystem(),
                form.getTransaction(), "wpd");

        File original = new File(uriWPD);
        if (original.exists() && original.length() > 0) {
            original.renameTo(new File(uriWPD + "~"));
        }

        WebPageDefinitionXml.save(form, uriWPD);

        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.setContenido("{}", "text/javascript");

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.setContenido("{}", "text/javascript");
    }

}
