package com.fitbank.web.data;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSON;

import com.fitbank.common.FileHelper;
import com.fitbank.util.Debug;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.json.TransporteListaValores;
import com.fitbank.web.json.TransporteWeb;

/**
 * Contiene los datos de una respuesta web.
 * 
 * @author FitBank
 * @version 2.0
 */
public final class RespuestaWeb extends DatosWeb {

    private static final long serialVersionUID = 2L;

    private byte[] contenido = null;

    private String contentType = "application/json";

    private HttpServletResponse servletResponse = null;

    public RespuestaWeb(TransporteDB datos, PedidoWeb pedido) {
        setTransporteDB(datos);
        setRecargarDB(pedido.isRecargarDB());
        servletResponse = pedido.getHttpServletResponse();
    }

    public RespuestaWeb(PedidoWeb pedido) {
        this(pedido, false);
    }

    public RespuestaWeb(PedidoWeb pedido, boolean inicializarContenido) {
        copiar(pedido);
        servletResponse = pedido.getHttpServletResponse();
        if (inicializarContenido) {
            setContenido("");
        }
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido, String contentType) {
        this.contenido = contenido;
        setContentType(contentType);
    }

    public void setContenido(Object contenido) {
        if (contenido instanceof byte[]) {
            this.contenido = (byte[]) contenido;
        } else {
            try {
                this.contenido = String.valueOf(contenido).getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new ErrorWeb(e);
            }
        }

        if (contenido == null) {
            setContentType("application/other");
        }
        try {
            setContentType(FileHelper.getContentType(this.contenido));
        } catch (Exception e) {
            Debug.error(e);
        }
    }

    public void setContenido(TransporteWeb contenido) {
        contenido.setRefreshDB(this.isRecargarDB());
        setContenido(contenido.toJSON(), "application/json");
    }

    public void setContenido(TransporteListaValores contenido) {
        contenido.setRefreshDB(this.isRecargarDB());
        setContenido(contenido.toJSON(), "application/json");
    }

    public void setContenido(JSON json) {
        setContenido(json.toString(), "application/json");
    }

    public void setContenido(String contenido) {
        setContenido(contenido, "text/plain");
    }

    public void setContenido(String contenido, String contentType) {
        try {
            this.contenido = contenido.getBytes(getCharacterEncoding());
            setContentType(contentType);
        } catch (UnsupportedEncodingException e) {
            Debug.error("No se pudo convertir a "
                    + getCharacterEncoding(), e);
        }
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCharacterEncoding() {
        return "UTF-8";
    }

    public HttpServletResponse getHttpServletResponse() {
        return servletResponse;
    }

    public void setHttpServletResponse(HttpServletResponse response) {
        servletResponse = response;
    }

    public void escribir() throws IOException {
        if (this.getContenido() != null) {
            servletResponse.setCharacterEncoding(this.getCharacterEncoding());
            servletResponse.setContentType(this.getContentType());
            OutputStream os = servletResponse.getOutputStream();
            os.write(this.getContenido());
            os.close();
        } else if (!servletResponse.containsHeader("Location")) {
            throw new ErrorWeb("No hay contenido");
        }
    }

    /**
     * Agrega encabezados a la respuesta para asegurarse que no quede guardada
     * en la caché del navegador.
     */
    public void noCachear() {
        servletResponse.setDateHeader("Date", new Date().getTime());
        servletResponse.setDateHeader("Expires", 0);
        servletResponse.setHeader("Pragma", "no-cache");
        servletResponse.setHeader("Cache-Control", "no-cache, must-revalidate");
        servletResponse.setHeader("ETag", UUID.randomUUID().toString().replace(
                "-", ""));
    }

}
