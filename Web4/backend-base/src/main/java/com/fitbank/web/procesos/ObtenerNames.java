package com.fitbank.web.procesos;

import net.sf.json.JSONObject;

import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

@Handler(GeneralRequestTypes.NAMES)
public class ObtenerNames implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        boolean init = "true".equals(pedido.getValorRequestHttp("init"));
        boolean activa = EntornoWeb.existeUsuario();
        boolean cambio = "true".equals(pedido.getValorRequestHttp("cambio"));

        JSONObject res = new JSONObject();

        if (cambio) {
            res.put("nameClave", EntornoWeb.getDatosSesion().getNameClave());
            res.put("nameClave2", EntornoWeb.getDatosSesion().getNameClave2());

        } else if (init || activa) {
            res.put("activa", activa);

        } else {
            EntornoWeb.setDatosSesion(null);

            res.put("nameUsuario", EntornoWeb.getDatosSesion().getNameUsuario());
            res.put("nameClave", EntornoWeb.getDatosSesion().getNameClave());
        }

        respuesta.setContenido(res);
        respuesta.noCachear();

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        JSONObject res = new JSONObject();
        
        res.put("error", true);
        res.put("id", EntornoWeb.getSecuencia());

        respuesta.setContenido(res);
    }

}
