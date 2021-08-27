package com.fitbank.web.procesos;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import org.apache.commons.lang.StringUtils;

@Handler(GeneralRequestTypes.INF)
@RevisarSeguridad
public class CargaInformacion implements Proceso {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        TransporteDB dg = EntornoWeb.getTransporteDBBase();

        if (StringUtils.isBlank(dg.getUser())) {
            jsonObject.put("nouser", "1");
        } else {
            Date fechaActual = new Date();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

            jsonArray.add(crearCampo("Sucursal", dg.getBranchName(), 100));
            jsonArray.add(crearCampo("Terminal", dg.getTerminal(), 100));
            jsonArray.add(crearCampo("Usuario", dg.getUser(), 100));
            jsonArray.add(crearCampo("Fecha actual", format.format(fechaActual), 50));

            jsonArray.add(crearCampo("Oficina", dg.getOfficeName(), 100));
            jsonArray.add(crearCampo("Rol", dg.getRoleName(), 100));
            jsonArray.add(crearCampo("Área", dg.getAreaName(), 100));
            jsonObject.put("fechaActual", fechaActual.getTime());

            if (dg.getCompany() != null) {
                Date fechaContable = dg.getAccountingDate();
                if (fechaContable != null) {
                    jsonArray.add(crearCampo("Fecha contable", format.format(fechaContable), 50));
                    jsonObject.put("fechaContable", fechaContable.getTime());
                }
            }

            jsonObject.put("version", dg.getSchemaVersion());

            jsonObject.put("valores", jsonArray);
        }

        respuesta.setContenido(jsonObject);

        return respuesta;
    }

    private JSONObject crearCampo(String nombre, String valor, Integer longitud) {
        JSONObject res = new JSONObject();

        res.put("nombre", nombre);
        res.put("valor", valor);
        res.put("longitud", longitud);

        return res;
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        JSONObject res = new JSONObject();

        res.put("mensaje", mensaje);
        res.put("mensajeUsuario", mensajeUsuario);
        res.put("stackTrace", stackTrace);

        respuesta.setContenido(res);
    }

}
