package com.fitbank.web.editor.procesos;

import com.fitbank.js.GeneradorJS;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;
import com.fitbank.webpages.definition.WebPageDefinition;
import com.fitbank.webpages.definition.wizard.WizardGenerator;

/**
 * Genera el WebPageDefinition a partir del wizard data
 *
 * @author FitBank CI
 */
@Handler(EditorRequestTypes.GENERATE_WIZARD)
public class GenerateWizard implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        WebPageDefinition wpd = GeneradorJS.toJava(pedido.getValorRequestHttp(
                "json"), WebPageDefinition.class);

        WizardGenerator.compile(wpd);

        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.setContenido(GeneradorJS.toJS(wpd, true), "text/javascript");

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.setContenido("{}", "text/javascript");
    }

}
