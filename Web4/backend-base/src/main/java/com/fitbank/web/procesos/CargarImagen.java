package com.fitbank.web.procesos;

import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.assistants.Scanner;
import net.sf.json.JSONObject;

@Handler(GeneralRequestTypes.IMG)
@RevisarSeguridad
public class CargarImagen extends BajarArchivo {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        DatosArchivo datosArchivo = getDatosArchivo(pedido);
        String value = datosArchivo.getValue();
        Assistant assistant = datosArchivo.getFormElement().getAssistant();

        if (!(assistant instanceof Scanner)) {
            return super.procesar(pedido);
        }

        Scanner scanner = (Scanner) assistant;

        int pageNumber = pedido.getValorRequestHttpInt("page");

        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        if ("_json".equals(pedido.getValorRequestHttp("extra"))) {
            JSONObject json = new JSONObject();

            json.put("numberOfPages", scanner.getNumberOfPages(value));

            respuesta.setContenido(json);

        } else {
            respuesta.setContenido(((Scanner) scanner).asImage(value, pageNumber != -1 ? pageNumber : 0));
        }

        return respuesta;
    }

}
