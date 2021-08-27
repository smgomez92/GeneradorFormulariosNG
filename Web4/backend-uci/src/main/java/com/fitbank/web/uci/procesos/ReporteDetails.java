package com.fitbank.web.uci.procesos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.fitbank.dto.management.Detail;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.html.SerializadorHtml;
import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.detailreport.DetailToHtml;
import com.fitbank.web.procesos.Registro;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;

@Handler(GeneralRequestTypes.REPORTE_DETAILS)
public class ReporteDetails implements Proceso {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        ConstructorHtml html = new ConstructorHtml();

        html.abrir("html");

        html.abrir("head");
        html.agregar("title", "Reporte");
        html.agregar("base");
        html.setAtributo("href", "../");
        this.setStyle(pedido, html);
        this.setScript(pedido, html);
        html.cerrar("head");

        html.abrir("body");

        synchronized (Registro.REGISTROS) {
            List<Registro.RegistroWeb> lRegistros = new ArrayList<Registro.RegistroWeb>(Registro.REGISTROS.values());
            for (Registro.RegistroWeb registroWeb : lRegistros) {
                if (registroWeb.getTipo().toLowerCase().matches(Registro.IGNORED_PATHS)) {
                    continue;
                }

                if (!EntornoWeb.getSessionId().equals(registroWeb.getSessionId())) {
                    continue;
                }

                addTransactionHeader(html, registroWeb);

                html.abrir("section");

                html.agregar("h2", "Pedido");
                DetailToHtml.addDetail(html, getDetail(registroWeb, "entrada.xml"));
                html.agregar("h2", "Respuesta");
                DetailToHtml.addDetail(html, getDetail(registroWeb, "salida.xml"));

                html.cerrar("section");
            }
        }

        html.cerrar("body");
        html.cerrar("html");

        String contenido = new SerializadorHtml().serializar(html);
        respuesta.setContenido(contenido, "text/html");

        return respuesta;
    }

    private void addTransactionHeader(ConstructorHtml html, Registro.RegistroWeb registroWeb) {
        if (StringUtils.isNotBlank(registroWeb.getTran())) {
            html.agregar("h1", String.format("Transacción %s (%s)", registroWeb.getTran(), registroWeb.getTipo()));
        } else {
            html.agregar("h1", String.format("Transacción desconocida (%s)", registroWeb.getTipo()));
        }
        html.setAtributo("onclick", "toggle(this);");
    }

    private Detail getDetail(Registro.RegistroWeb registroWeb, String extra) {
        File file = new File(Registro.PATH_BASE, registroWeb.getNombreBase() + "-" + extra);

        try {
            return Detail.valueOf(IOUtils.toString(new FileInputStream(file)));
        } catch (IOException e) {
            Debug.error(e);
            return new Detail();
        } catch (Exception e) {
            throw new UnknownError(e.getLocalizedMessage());
        }
    }

    private void setStyle(PedidoWeb pedido, ConstructorHtml html) {
        try {
            ServletContext context = pedido.getHttpServletRequest().getSession().getServletContext();
            String styleString = IOUtils.toString(context.getResourceAsStream("/css/reporte_details.css"), "ISO-8859-1");
            html.agregar("style", styleString);
        } catch (IOException ioe) {
            Debug.error("Problemas leyendo el archivo de estilos de reporte de details", ioe);
        }
    }

    private void setScript(PedidoWeb pedido, ConstructorHtml html) {
        try {
            ServletContext context = pedido.getHttpServletRequest().getSession().getServletContext();
            String scriptString = IOUtils.toString(context.getResourceAsStream("/js/fitbank/reporte_details.js"), "ISO-8859-1");
            html.agregar("script", scriptString);
        } catch (IOException ioe) {
            Debug.error("Problemas leyendo el archivo de scripts de reporte de details", ioe);
        }
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
                        String mensaje, String mensajeUsuario, String stackTrace,
                        TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

}
