package com.fitbank.web.providers;

import com.fitbank.web.EntornoWeb;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.json.TransporteWeb;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.util.WebPageSource;

public abstract class WebPageProvider extends WebPageSource {

    private static final ThreadLocal<PedidoWeb> PEDIDO_LOCAL =
            new ThreadLocal<PedidoWeb>() {

                @Override
                protected PedidoWeb initialValue() {
                    return null;
                }

            };

    protected abstract WebPage getWebPage(PedidoWeb pedido, String subsystem,
            String transaction, boolean esAdjunto);

    public abstract int getWeight();

    @Override
    public WebPage getWebPage(String subsystem, String transaction,
            boolean attached) {
        return getWebPage(PEDIDO_LOCAL.get(), subsystem, transaction, attached);
    }

    public RespuestaWeb process(PedidoWeb pedido, String subsystem,
            String transaction) {
        PEDIDO_LOCAL.set(pedido);

        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        WebPage webPage = getWebPage(pedido, subsystem, transaction, false);

        if (webPage == null) {
            return null;
        }

        webPage = processWebPage(webPage, "0");

        PEDIDO_LOCAL.set(null);

        TransporteWeb transporteWeb = new TransporteWeb(respuesta, webPage);

        respuesta.setContenido(transporteWeb);
        EntornoWeb.getContexto().setWebPage(webPage);

        return respuesta;
    }

    public void setPedidoLocal(PedidoWeb pedido){
        PEDIDO_LOCAL.set(pedido);
    }
}
