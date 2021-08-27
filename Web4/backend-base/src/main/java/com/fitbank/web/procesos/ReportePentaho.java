package com.fitbank.web.procesos;

import com.fitbank.util.Debug;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Proceso que se conecta a un servidor Pentaho para obtener reportería remota
 *
 * @author Soft Warehouse S.A.
 */
@Handler(GeneralRequestTypes.REPORTE_PENTAHO)
@RevisarSeguridad
public class ReportePentaho implements Proceso {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        InputStream is;
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        try {
            String parametros = "?";
            String folderName = pedido.getValorRequestHttp("folderName");
            String name = pedido.getValorRequestHttp("name");

            String type = pedido.getValorRequestHttp("type");
            Debug.debug("name = " + name + "   /   type = " + type);

            Map<String, List<String>> params = pedido.getValoresRequestHttp();
            if (params != null) {
                for (Map.Entry<String, List<String>> entry : params.entrySet()) {
                    String key = entry.getKey();
                    List<String> values = entry.getValue();

                    for (String value : values) {
                        if (!"name".equals(key) && !"type".equals(key) && !"folderName".equals(key)) {
                            Debug.debug("Parameter Name = " + key + " Value=" + value);
                            parametros = parametros + key + "=" + value.replaceAll(" ", "%20") + "&";
                        }
                    }
                }
            }

            Debug.debug("folderName = " + folderName + " name = " + name + " type = " + type);
            Debug.debug("parametros = " + parametros);
            if ("?".equals(parametros)) {
                parametros = "";
            }

            //Obtener datos de conexión con el servicio remoto
            String urlInicial = ParametrosWeb.getValueString(ReportePentaho.class, "url");
            String usr = ParametrosWeb.getValueString(ReportePentaho.class, "usuario");
            String psw = ParametrosWeb.getValueString(ReportePentaho.class, "password");
            String tmo = ParametrosWeb.getValueString(ReportePentaho.class, "timeout");
            Integer timeout = StringUtils.isNumeric(tmo) ? Integer.valueOf(tmo) : 18;

            String urlPath = this.obtenerUrl(urlInicial, folderName, name, type, parametros);
            String nomArchivo = name + "." + type.toLowerCase();
            Debug.debug("fileName=" + nomArchivo);

            String authString = usr + ":" + psw;
            byte[] authEncBytes = Base64.encodeBase64((byte[]) authString
                    .getBytes());
            String authStringEnc = new String(authEncBytes);
            URL url = new URL(urlPath);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(timeout * 1000);
            urlConnection.setRequestProperty("Authorization", "Basic "
                    + authStringEnc);

            respuesta.getHttpServletResponse().setHeader("Content-disposition",
                    "attachment; filename=" + nomArchivo);
            is = urlConnection.getInputStream();
            byte[] respBytes = IOUtils.toByteArray(is);
            is.close();

            respuesta.setContenido(respBytes, "application/octet-stream");
        } catch (MalformedURLException e) {
            Debug.error("Url no armada correctamente", e);
        } catch (IOException e) {
            Debug.error("Error en la lectura de respuestas", e);
        } catch (NumberFormatException e) {
            Debug.error("Problemas al obtener parámetros del Request", e);
        }

        return respuesta;
    }

    public String obtenerUrl(String urlInicial, String carpeta, String nombreReporte, String tipoReporte, String parametros) {
        if (StringUtils.isNotBlank(parametros)) {
            parametros = parametros.substring(1, parametros.length());
        }

        String url = StringUtils.EMPTY;
        if ("XLS".equals(tipoReporte.toUpperCase())) {
            url = urlInicial + carpeta + "%3A" + nombreReporte + ".prpt/report?" + parametros + "output-target=application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;page-mode=flow";
        }

        if ("PDF".equals(tipoReporte.toUpperCase())) {
            url = urlInicial + carpeta + "%3A" + nombreReporte + ".prpt/report?" + parametros + "output-target=pageable%2Fpdf";
        }

        Debug.debug(">>>>CRED_PENTAHO: " + url);
        return url;
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta, String mensaje, String mensajeUsuario, String stackTrace, TransporteDB datos) {
    }

}
