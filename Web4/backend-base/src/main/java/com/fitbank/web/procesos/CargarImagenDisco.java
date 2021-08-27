package com.fitbank.web.procesos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.IOUtils;

import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;

@Handler(GeneralRequestTypes.IMG_DISCO)
@RevisarSeguridad
public class CargarImagenDisco implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        String base = ParametrosWeb.getValueString(CargarImagenDisco.class,
                "path");
        String path = pedido.getHttpServletRequest().getPathInfo().replaceFirst(
                "/" + GeneralRequestTypes.IMG_DISCO, "");

        try {
            File file = new File(base, path);
            InputStream contenido = new FileInputStream(file);
            respuesta.setContenido(IOUtils.toByteArray(contenido));
        } catch (IOException e) {
            throw new ErrorWeb(e);
        }

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta, String mensaje,
            String mensajeUsuario, String stackTrace, TransporteDB datos) {
    }

}
