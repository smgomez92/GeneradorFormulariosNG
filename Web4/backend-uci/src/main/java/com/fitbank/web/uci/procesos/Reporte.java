package com.fitbank.web.uci.procesos;

import com.fitbank.common.FileHelper;
import com.fitbank.common.properties.PropertiesHandler;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.enums.MessageType;
import com.fitbank.enums.Requerido;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ManejoExcepcion;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.exceptions.MensajeWeb;
import com.fitbank.web.uci.Conversor;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.IterableWebElement;
import java.io.File;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

@Handler(GeneralRequestTypes.REPORTE)
@RevisarSeguridad
public class Reporte implements Proceso {

    public static final String BATCH_REPORT_CODE = "REP001";

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        String name = pedido.getValorRequestHttp("name");
        String extension = pedido.getValorRequestHttp("extension");
        boolean directDownload = "1".equals(pedido.getValorRequestHttp("directDownload"));
        String downloadName = pedido.getValorRequestHttp("downloadName");

        // ARMAR PEDIDO
        pedido.getTransporteDB().setMessageType(MessageType.REPORT);
        Detail detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();

        detail.addField(new Field("NAME", name));
        detail.addField(new Field("TYPE", extension));
        detail.addField(new Field("DIRECTDOWNLOAD", directDownload));

        boolean check = false;
        
        for (FormElement formElement : IterableWebElement.get(EntornoWeb.
                getContexto().getWebPage(), FormElement.class)) {
            if (formElement.getDataSource().esReporte()
                    && formElement.getRequerido() == Requerido.REQUERIDO
                    && StringUtils.isBlank(formElement.getFieldData().getValue(0))) {
                check |= Conversor.marcarRequerido(formElement, 0);
            }

            Conversor.convertirFormElementReporte(formElement, detail);
        }

        if (check) {
            throw new MensajeWeb("Hay valores requeridos no ingresados");
        }

        //VERIFICAR CAMPOS NECESARIOS PARA PROCESAR EL REPORTE
        name = detail.findFieldByName("NAME").getStringValue();
        if (StringUtils.isBlank(name)) {
            throw new ErrorWeb("PETICIÓN DE REPORTE SIN NOMBRE");
        }

        extension = detail.findFieldByName("TYPE").getStringValue();
        if (StringUtils.isBlank(extension)) {
            throw new ErrorWeb("PETICIÓN DE REPORTE SIN EXTENSIÓN");
        }

        Field fNombreFijo = detail.findFieldByName("NOMBREFIJO");
        if (fNombreFijo != null && StringUtils.isNotBlank(fNombreFijo.getStringValue())) {
            downloadName = fNombreFijo.getStringValue();
        }

        copiarEjecutadoPor(detail);

        // ENVIAR A UCI
        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        Detail res = ((TransporteDBUCI) respuesta.getTransporteDB()).getDetail();

        // CONTROLAR EL MENSAJE DE ERROR PARA REPORTES EN BATCH
        if (BATCH_REPORT_CODE.equals(respuesta.getTransporteDB().getResponseCode())) {
            throw new MensajeWeb(respuesta);
        } else {
            ManejoExcepcion.checkOkCodes(respuesta);
        }

        //ACTUALIZAR CAMPOS DEL REPORTE, PARA CASOS DE NOMBRES ESPECIALES
        name = res.findFieldByName("NAME").getStringValue();
        extension = res.findFieldByName("TYPE").getStringValue();
        String resRep = res.findFieldByName("REPORTE").getStringValue();
        downloadName = StringUtils.isBlank(downloadName) ? name + "." + extension : downloadName;

        if (StringUtils.isNotBlank(resRep)) {
            if (directDownload) {
                respuesta.getHttpServletResponse().addHeader("content-disposition", 
                        "attachment; filename=" + downloadName);
            } else {
                respuesta.getHttpServletResponse().addHeader("content-disposition", 
                        "inline; filename=" + downloadName);
            }

            byte[] reportBytes;
            if (Base64.isArrayByteBase64(resRep.getBytes())) {
                reportBytes = Base64.decodeBase64(resRep);
            } else {
                Configuration config = PropertiesHandler.getConfig("reports");
                String ruta = config.getString("rutaReportes");
                String carpeta = config.getString("carpetaOtros");
                String file = ruta.concat(carpeta).concat(resRep);
                File reportFile = new File(file);
                if (!reportFile.exists()) {
                    throw new ErrorWeb("REPORTE NO ENCONTRADO");
                }

                try {
                    reportBytes = FileUtils.readFileToByteArray(reportFile);
                } catch (IOException e) {
                    throw new ErrorWeb("NO SE PUDO OBTENER EL CONTENIDO DEL REPORTE", e);
                }
            }

            String reportContentType = this.getContentType(res, reportBytes);
            respuesta.setContenido(reportBytes, reportContentType);
        } else {
            throw new ErrorWeb("REPORTE NO DEVUELVE RESULTADOS");
        }

        return respuesta;
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
    }

    /**
     * Metodo que obtiene el MIME type del reporte.
     * Busca el campo de control CONTENT-TYPE y usa su valor como MIME type del
     * reporte.
     * En caso de no existir dicho campo, usa el utilitario FileHelper para
     * obtener el MIME type dependiento de los bytes del reporte.
     * 
     * @param res Mensaje procesado
     * @param reportBytes Bytes del reporte
     * @return MIME type del reporte
     */
    private String getContentType(Detail res, byte[] reportBytes) {
        Field fContentType = res.findFieldByName("CONTENT-TYPE");
        if (fContentType != null 
                && StringUtils.isNotBlank(fContentType.getStringValue())) {
            return fContentType.getStringValue();
        }

        try {
            return FileHelper.getContentType(reportBytes);
        } catch (Exception e) {
            throw new ErrorWeb("Problemas al obtener el content-type del reporte", e);
        }
    }

    /**
     * Asigna el valor de ejecutadoPor al detail basado en un campo de control
     *
     * @param detail
     */
    private void copiarEjecutadoPor(Detail detail) {
        detail.setExecutedBy(detail.findFieldByNameCreate("EJECUTADOPOR").getStringValue());
    }
}
