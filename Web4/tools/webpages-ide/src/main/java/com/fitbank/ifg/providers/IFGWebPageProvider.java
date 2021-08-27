package com.fitbank.ifg.providers;

import com.fitbank.ifg.iFG;
import com.fitbank.ifg.swing.PanelFilasElementos;
import com.fitbank.util.Clonador;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.providers.HardDiskWebPageProvider;
import com.fitbank.webpages.WebPage;

public class IFGWebPageProvider extends HardDiskWebPageProvider {

    @Override
    public WebPage getWebPage(PedidoWeb pedido, String subsystem,
            String transaction, boolean esAdjunto) {
        for (PanelFilasElementos pfe : iFG.getSingleton().getPaneles()) {
            WebPage webPage = pfe.getWebPage();

            if (webPage != null && webPage.getSubsystem().equals(subsystem)
                    && webPage.getTransaction().equals(transaction)) {
                return Clonador.clonar(webPage);
            }
        }

        return super.getWebPage(pedido, subsystem, transaction, esAdjunto);
    }

    @Override
    public int getWeight() {
        return 10;
    }

}