package com.fitbank.web.uci.procesos;

import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.util.Debug;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.procesos.CargaFormulario;
import com.fitbank.web.uci.providers.UCIWebPageProvider;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageXml;

/**
 * Borra de ehcache el formulario desde donde se hizo la petición junto con
 * todos los subformularios referenciados y carga de nuevo el formulario
 * dejando la versión fresca en cache. Este proceso se llama al presionar
 * Ctrl + F2 en el navegador.
 *
 * @author Fitbank RB
 */
@Handler(GeneralRequestTypes.BORRAR_CACHE)
@RevisarSeguridad
public class BorrarCache extends CargaFormulario {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        String subsystem = pedido.getTransporteDB().getSubsystem();
        String transaction = pedido.getTransporteDB().getTransaction();
        UCIWebPageProvider.WebPageCacheValue value = UCIWebPageProvider.
                getFromCache(subsystem, transaction);

        if (value != null) {
            try {
                WebPage webPage = WebPageXml.parseString(value.getXml());
                UCIWebPageProvider.deleteFromCache(subsystem, transaction);
                UCIWebPageProvider.deleteAttached(webPage);
            } catch (ExcepcionParser ex) {
                Debug.error(ex);
            }
        }

        return super.procesar(pedido);
    }

}
