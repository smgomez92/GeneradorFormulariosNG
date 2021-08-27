package com.fitbank.web.uci.procesos;


import org.apache.commons.lang.StringUtils;

import com.fitbank.common.crypto.Decrypt;
import com.fitbank.dto.management.Detail;
import com.fitbank.enums.MessageType;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.MensajeWeb;
import com.fitbank.web.uci.Conversor;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;

@Handler(GeneralRequestTypes.CLAVE)
public class CambioClave implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        pedido.getTransporteDB().setMessageType(MessageType.SIGN_ON);
        pedido.getTransporteDB().setSubsystem("01");
        pedido.getTransporteDB().setTransaction("0000");
        pedido.getTransporteDB().setVersion("01");

        String clave = pedido.getValorRequestHttp(EntornoWeb.getDatosSesion().
                getNameClave());
        String clave2 = pedido.getValorRequestHttp(EntornoWeb.getDatosSesion().
                getNameClave2());

        EntornoWeb.getDatosSesion().setNameClave(null);
        EntornoWeb.getDatosSesion().setNameClave2(null);

        if (StringUtils.isBlank(clave)) {
            throw new MensajeWeb("Contraseña no ingresada");
        }

        if (!clave.equals(clave2)) {
            throw new MensajeWeb("Contraseñas no coinciden");
        }

        Decrypt decrypt = new Decrypt();
        try {
            clave = decrypt.encrypt(clave);
            clave2 = decrypt.encrypt(clave2);
        } catch (Exception e) {
            // Excepcion desconocida!!!
            throw new Error(e);
        }
        pedido.getTransporteDB().setNewPassword(clave);

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);
        Detail detail = ((TransporteDBUCI) respuesta.getTransporteDB()).
                getDetail();

        pedido.getHttpServletRequest().getSession().invalidate();

        Conversor.checkOkCodes(detail, respuesta);
        pedido.redireccionar(EntornoWeb.URI_INGRESO);

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

}
