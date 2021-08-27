package com.fitbank.web.editor.procesos;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fitbank.js.GeneradorJS;
import com.fitbank.serializador.xml.SerializadorXml;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.providers.HardDiskWebPageProvider;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.definition.WebPageDefinition;
import com.fitbank.webpages.definition.WebPageDefinitionCompiler;

@Handler(EditorRequestTypes.COMPILE_WEBPAGE)
public class CompileWebPage implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        WebPageDefinition form = GeneradorJS.toJava(pedido
                .getValorRequestHttp("json"), WebPageDefinition.class);

        String uriWPX = HardDiskWebPageProvider.getPath(form.getSubsystem(),
                form.getTransaction());

        try {
            WebPage webPage = WebPageDefinitionCompiler.compile(form);

            FileOutputStream fos = new FileOutputStream(uriWPX);
            new SerializadorXml().serializar(webPage, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            throw new ErrorWeb(e);
        } catch (IOException e) {
            throw new ErrorWeb(e);
        }

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
