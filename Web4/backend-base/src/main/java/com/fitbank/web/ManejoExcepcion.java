package com.fitbank.web;

import com.fitbank.web.exceptions.ErrorWeb;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletResponse;

import com.fitbank.enums.MessageType;
import com.fitbank.util.Debug;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.MensajeWeb;
import com.fitbank.web.procesos.Registro;

/**
 * 
 * @author FitBank
 * @version 2.0
 */
public final class ManejoExcepcion {

    private ManejoExcepcion() {
    }

    public static RespuestaWeb procesar(Proceso proceso, PedidoWeb pedido,
            RespuestaWeb respuestaOriginal, HttpServletResponse response,
            Throwable t) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);
        StringWriter stackTrace = new StringWriter();
        PrintWriter pw = new PrintWriter(stackTrace);
        boolean isError = !(t instanceof MensajeWeb);
        String mensajeUsuario = "";
        String mensaje = isError ? "Error al ejecutar un proceso" 
                : "Mensaje recibido desde un proceso";
        TransporteDB transporteDB = respuestaOriginal != null
                ? respuestaOriginal.getTransporteDB() : respuesta.
                getTransporteDB();

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        t.printStackTrace(pw);

        if (respuestaOriginal != null && transporteDB != null) {
            pw.println("Causado por:");
            pw.println(transporteDB.getStackTrace());
            mensajeUsuario = transporteDB.getMessage();
        } else if (t instanceof ErrorWeb) {
            ErrorWeb errorWeb = (ErrorWeb) t;

            if (errorWeb.getTransporteDB() != null) {
                transporteDB = errorWeb.getTransporteDB();
                mensajeUsuario = transporteDB.getMessage();
            } else {
                mensajeUsuario = t.getLocalizedMessage();
            }
        } else {
            mensajeUsuario = t.getMessage();
        }

        if (pedido != null) {
            MessageType tipo = pedido.getTransporteDB().getMessageType();

            mensaje += String.format("\nTipo %s (%s)", pedido.getTipoPedido(),
                    tipo);

            if (pedido.getTransporteDB() != null) {
                mensaje += String.format("-> %s", pedido.getTransporteDB().
                        getResponseCode());
            }
        }

        if (respuestaOriginal != null && transporteDB != null) {
            MessageType tipo = transporteDB.getMessageType();
            String codigo = transporteDB.getResponseCode();

            mensaje += String.format("\nRespuesta: %s -> %s", tipo, codigo);

            mensaje += "\n" + transporteDB.getMessage();
            mensaje += "\n" + transporteDB.getStackTrace();
        }

        if (!(t instanceof MensajeWeb)) {
            Debug.error(mensaje, t);
        }

        if (proceso != null) {
            proceso.onError(pedido, respuesta, mensaje, mensajeUsuario,
                    stackTrace.toString(), transporteDB);
        }

        if (respuesta.getContenido() == null) {
            String redirect;
            if (isError) {
                redirect = pedido.getHttpServletRequest().getContextPath()
                    + "/error.html#" + Registro.getRegistro().getSecuencia();
            } else {
                redirect = pedido.getHttpServletRequest().getContextPath()
                    + "/message.html#" + Registro.getRegistro().getSecuencia();
            }

            try {
                response.sendRedirect(redirect);
            } catch (IOException e) {
                Debug.error("No se pudo enviar un redirect!", e);
            }
        }

        if (pedido.getTransporteDB() != null) {
            Registro.getRegistro().setCodigoError(pedido.getTransporteDB().
                    getResponseCode());
        }

        Registro.getRegistro().setMensajeError(t.getLocalizedMessage());

        if (!isError(Registro.getRegistro().getCodigoError())) {
            Registro.getRegistro().setCodigoError("-1");
        }

        Registro.getRegistro().setStackTrace(stackTrace.toString());

        return respuesta;
    }

    /**
     * Revisa el codigo de respuesta y bota una excepcion
     *
     * @param respuesta
     * 
     * @throws ErrorWeb
     */
    public static void checkOkCodes(RespuestaWeb respuesta) {
        String codigo = respuesta.getTransporteDB().getResponseCode();
        if (isError(codigo)) {
            throw new ErrorWeb(respuesta);
        }
    }

    /**
     * Revisa que el codigo de respuesta sea positivo o de error
     *
     * @param respuesta
     * 
     * @throws boolean | true es un error, false no es un error
     */
    public static boolean isError(String codigo) {
        // Esto debe ser igual a Util.isError en util.js
        return codigo != null && !codigo.matches("(?i)0|.*-0|ok-.*");
    }

}
