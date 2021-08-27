package com.fitbank.web.procesos;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.json.TransporteWeb;
import com.fitbank.web.providers.WebPageProvider;
import com.fitbank.web.providers.WebPageProviderFactory;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageEnviroment;

/**
 * Proceso para cargar formularios.
 * 
 * @author FitBank
 * @version 2.0
 */
@Handler(GeneralRequestTypes.FORM)
@RevisarSeguridad
public class CargaFormulario implements Proceso {

    public static final List<WebPageProvider> WEB_PAGE_PROVIDERS = WebPageProviderFactory.listProviders();

    static {
        Collections.sort(WEB_PAGE_PROVIDERS, new Comparator<WebPageProvider>() {

            @Override
            public int compare(WebPageProvider o1, WebPageProvider o2) {
                return o2.getWeight() - o1.getWeight();
            }

        });
    }

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        String subs = pedido.getTransporteDB().getSubsystem();
        String tran = pedido.getTransporteDB().getTransaction();

        EntornoWeb.getContexto().getTransporteDBBase().clean();
        EntornoWeb.getContexto().setHayDatos(false);
        EntornoWeb.getContexto().setWebPage(new WebPage());
        EntornoWeb.getContexto().setPaginacion(new Paginacion());
        WebPageEnviroment.reset(true);

        RespuestaWeb respuesta = null;

        Registro.getRegistro().setTran(subs + tran);

        for (WebPageProvider webPageProvider : WEB_PAGE_PROVIDERS) {
            Debug.info("Probando: " + webPageProvider.getClass().getName());

            respuesta = webPageProvider.process(pedido, subs, tran);

            if (respuesta != null) {
                break;
            }
        }

        if (respuesta == null) {
            throw new ErrorWeb("No se encontró formulario");
        }

        Registro.getRegistro().setTran(subs + tran);

        // Se necesita reiniciar la paginación con el nuevo formulario
        EntornoWeb.getContexto().setPaginacion(new Paginacion());

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
