package com.fitbank.web.procesos;

import com.fitbank.enums.AttachedPosition;
import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.Paginacion;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.json.TransporteWeb;
import com.fitbank.web.providers.WebPageProvider;
import com.fitbank.webpages.AttachedWebPage;
import com.fitbank.webpages.WebPage;

@Handler(GeneralRequestTypes.ADJUNTAR)
@RevisarSeguridad
public class CargaAdjunto implements Proceso {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        String transacciones = pedido.getValorRequestHttp("_transacciones");
        String index = pedido.getValorRequestHttp("_indice");

        RespuestaWeb respuesta = new RespuestaWeb(pedido);
        WebPage webPage = EntornoWeb.getContexto().getWebPage();
        Error error = null;
        
        webPage.getAttached().clear();
        forTrans:
        for (String transaccion : transacciones.split(",")) {
            if (transaccion.length() != 6) {
                break;
            }
            for (AttachedWebPage attached : webPage.getAttached()) {
                if (transaccion.equals(attached.getSubsystem()
                        .concat(attached.getTransaction()))) {
                    continue forTrans;
                }
            }
            AttachedWebPage attachedWebPage = new AttachedWebPage();
            attachedWebPage.setSubsystem(transaccion.substring(0, 2));
            attachedWebPage.setTransaction(transaccion.substring(2));
            attachedWebPage.setContainerIndex(Integer.valueOf(index));
            attachedWebPage.setPosition(AttachedPosition.FIXED);

            webPage.getAttached().add(attachedWebPage);
        }

        for (WebPageProvider webPageProvider : CargaFormulario.WEB_PAGE_PROVIDERS) {
            error = null;
            Debug.info("Probando: " + webPageProvider.getClass().getName());

            try {
                webPageProvider.setPedidoLocal(pedido);
                webPage = webPageProvider.processWebPage(webPage, "0");
                break;
            } catch (Error e) {
                error = e;
                Debug.debug("Adjunto no encontrado con el proveedor: "
                        + webPageProvider.getClass().getName());
            }

        }

        if (error != null) {
            throw error;
        }

        TransporteWeb transporte = new TransporteWeb(respuesta, webPage, true, false, true);

        respuesta.setContenido(transporte);

        EntornoWeb.getContexto().setWebPage(webPage);
        EntornoWeb.getContexto().setPaginacion(new Paginacion());
        EntornoWeb.getContexto().setTransporteDBBase(respuesta.getTransporteDB());

        return respuesta;
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.getTransporteDB().setMessage(mensajeUsuario);
        respuesta.getTransporteDB().setStackTrace(stackTrace);

        WebPage webPage = EntornoWeb.getContexto().getWebPage();

        if (webPage == null) {
            webPage = new WebPage();
        }

        TransporteWeb transporte = new TransporteWeb(respuesta, webPage);

        respuesta.setContenido(transporte);
    }
}
