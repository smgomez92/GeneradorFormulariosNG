package com.fitbank.web.uci.procesos;

import java.util.HashMap;
import java.util.Map;

import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.MessageType;
import com.fitbank.menujson.MenuCompania;
import com.fitbank.menujson.MenuJSON;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.uci.Conversor;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import net.sf.json.JSONObject;

@Handler(GeneralRequestTypes.MENU)
@RevisarSeguridad
public class CargaMenu implements Proceso {

    private EnlaceUCI uci = new EnlaceUCI();

    @SuppressWarnings("unchecked")
    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = null;

        pedido.setTipoMenu(pedido.getValorRequestHttp("menu"));

        Detail detail;
        switch (pedido.getTipoMenu()) {
        case CIAS:
            detail = ((TransporteDBUCI) EntornoWeb.getTransporteDBBase())
                    .getDetail();
            respuesta = procesarCias(pedido);

            Map<String, String> m = new HashMap<String, String>();
            Table table = detail.findTableByAlias("TCOMPANIAUSUARIOS");
            for (Record r : table.getRecords()) {
                m.put(r.findFieldByName("CPERSONA").getValue().toString(), r
                        .findFieldByName("CROL").getValue().toString());
            }
            pedido.getHttpServletRequest().getSession().setAttribute("rol", m);

            break;

        case TRANS:
            detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();
            detail.setCompany(pedido.getValorRequestHttpInt("cia"));
            Map mapa = (Map) pedido.getHttpServletRequest().getSession()
                    .getAttribute("rol");
            int valor = Integer.parseInt(mapa.get(
                    String.valueOf(pedido.getValorRequestHttpInt("cia")))
                    .toString());
            Detail detailPedido = ((TransporteDBUCI) pedido.getTransporteDB())
                    .getDetail();
            detailPedido.setRole(valor);
            detail.setRole(valor);

            respuesta = procesarTrans(pedido);

            Detail detailRespuesta = ((TransporteDBUCI) respuesta
                    .getTransporteDB()).getDetail();
            
            Conversor.checkOkCodes(detailRespuesta, respuesta);
            
            String menuString = (String) detailRespuesta.findFieldByNameCreate(
                    "MENU").getValue();
            respuesta.setContenido(menuString, "application/json");

            break;
        }

        return respuesta;
    }

    /**
     * Método que se encarga de llenar los datos en el combo de la companía
     * 
     * @param cias
     *            Variable que va a contener el combo
     * @return ComboBox en formato html
     */
    private RespuestaWeb procesarCias(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido.getTransporteDB(), pedido);

        MenuJSON cias = new MenuJSON("cias");
        Detail detail = ((TransporteDBUCI) EntornoWeb.getTransporteDBBase())
                .getDetail();
        Table table = detail.findTableByAlias("TCOMPANIAUSUARIOS");

        for (Record r : table.getRecords()) {
            String nombre = r.findFieldByNameCreate("NOMBRELEGAL").getValue()
                    .toString();
            String codigo = r.findFieldByNameCreate("CPERSONA").getValue()
                    .toString();

            cias.getItems().add(new MenuCompania(nombre, codigo));
        }

        respuesta.setContenido(cias.toJSON());

        return respuesta;
    }

    private RespuestaWeb procesarTrans(PedidoWeb pedido) {
        String tipoMensaje = ParametrosWeb.getValueString(CargaMenu.class, "tipoMensaje");
        String subsistema = ParametrosWeb.getValueString(CargaMenu.class, "subsistema");
        String transaccion = ParametrosWeb.getValueString(CargaMenu.class, "transaccion");
        String version = ParametrosWeb.getValueString(CargaMenu.class, "version");

        pedido.getTransporteDB().setMessageType(this.getMessageType(tipoMensaje));
        pedido.getTransporteDB().setSubsystem(subsistema);
        pedido.getTransporteDB().setTransaction(transaccion);
        pedido.getTransporteDB().setVersion(version);

        RespuestaWeb respuesta = uci.procesar(pedido);

        return respuesta;
    }

    private MessageType getMessageType(String type) {
        for (MessageType mt : MessageType.values()) {
            if (mt.getValue().equals(type)) {
                return mt;
            }
        }

        return MessageType.ERROR;
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        JSONObject json = new JSONObject();

        json.put("mensaje", mensaje);
        json.put("mensajeUsuario", mensajeUsuario);
        json.put("stackTrace", stackTrace);

        respuesta.setContenido(json);
    }
}
