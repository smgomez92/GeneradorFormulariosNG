package com.fitbank.web.procesos;

import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.json.TransporteWeb;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.widgets.DeleteRecord;

/**
 * Clase LimpiaTuplas. Procesa pedidos tipo SF2 para limpiar las tuplas de un
 * formulario cargado. Se ejecuta cuando en el entorno se presiona Shift + F2.
 * 
 * @author FitBank JT
 * @version 2.0
 */
@Handler(GeneralRequestTypes.LIMPIAR)
@RevisarSeguridad
public class LimpiaTuplas implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        WebPage webPage = EntornoWeb.getContexto().getWebPage();

        for (FormElement formElement : IterableWebElement.get(webPage, FormElement.class)) {
            DataSource dataSource = formElement.getDataSource();
            if (formElement.getLimpiable() && (dataSource.esRegistro() || dataSource.estaVacio())) {
                if (EntornoWeb.getContexto().getHayDatos()) {
                    formElement.getFieldData().resetAll();
                } else {
                    formElement.getFieldData().resetErrors();
                    formElement.getFieldData().resetValues();
                }
            }
        }

        for (DeleteRecord deleteRecord : IterableWebElement.get(webPage, DeleteRecord.class)) {
            deleteRecord.getFieldData().resetAll();
        }

        for (Container container : webPage) {
            container.setNumeroDeFilasConsultadas(false, 0);
        }

        EntornoWeb.getContexto().getTransporteDBBase().clean();
        EntornoWeb.getContexto().setHayDatos(false);

        RespuestaWeb respuesta = new RespuestaWeb(pedido.getTransporteDB(), pedido);
        respuesta.getTransporteDB().cleanResponse();

        TransporteWeb transporte = new TransporteWeb(respuesta, webPage, true);

        respuesta.setContenido(transporte);

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

}
