package com.fitbank.web.procesos;


import com.fitbank.common.FileHelper;
import com.fitbank.enums.ReportTypes;
import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.webpages.data.FormElement;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;


/**
 * Obtiene un archivo del WebPage y lo envía al navegador.
 *
 * @author FitBank CI
 */
@Handler(GeneralRequestTypes.FILE)
@RevisarSeguridad
public class BajarArchivo implements Proceso {

    private static final String PUNTO = ".";

    protected static class DatosArchivo {

        private final FormElement formElement;

        private final String fileName;

        private final String extension;

        private final int record;

        private final String value;

        private DatosArchivo(FormElement formElement, String fileName, 
                String extension, int record) {
            this.formElement = formElement;
            this.fileName = fileName;
            this.extension = extension;
            this.record = record;
            this.value = formElement.getFieldData().getValue(record);
        }

        public FormElement getFormElement() {
            return formElement;
        }

        public String getFileName() {
            return fileName;
        }

        public String getExtension() {
            return extension;
        }

        public int getRecord() {
            return record;
        }

        public String getValue() {
            return value;
        }

    }

    protected DatosArchivo getDatosArchivo(PedidoWeb pedido) {
        String fileName = pedido.getValorRequestHttp("elementName");
        String extension = pedido.getValorRequestHttp("extension");
        int record = pedido.getValorRequestHttpInt("registro");
        String downloadName = pedido.getValorRequestHttp("downloadName");

        downloadName = StringUtils.isBlank(downloadName) ? "archivo" : downloadName;
        FormElement dato = EntornoWeb.getContexto().getWebPage().findFormElement(fileName);

        return new DatosArchivo(dato, downloadName, extension, record);
    }

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        String plainDownload = pedido.getValorRequestHttp("plainDownload");

        if (StringUtils.isNotBlank(plainDownload) && "1".equals(plainDownload)) {
            return procesarDescargaEnPlano(pedido);
        }

        DatosArchivo datosArchivo = getDatosArchivo(pedido);

        return procesar(pedido, datosArchivo);
    }

    public RespuestaWeb procesar(PedidoWeb pedido, DatosArchivo datosArchivo) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.setContenido(
                datosArchivo.getFormElement().getAssistant().asObject(datosArchivo.getValue()));

        byte[] bData = respuesta.getContenido();

        respuesta.getHttpServletResponse().setHeader("Content-Disposition",
                String.format("attachment; filename=\"%s.%s\"", datosArchivo.getFileName(),
                getExtension(datosArchivo, bData)));

        return respuesta;
    }

    public RespuestaWeb procesarDescargaEnPlano(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);
        String path = pedido.getValorRequestHttp("path");
        String name = pedido.getValorRequestHttp("name");
        String directDownload = pedido.getValorRequestHttp("directDownload");
        String fileName = path.concat("/").concat(name);

        if (StringUtils.isNotBlank(fileName)) {
            File file = new File(fileName);

            if (file.exists() && file.isFile()) {
                byte[] data = this.getFileRawData(file);
                String extension;

                if (name.contains(".")) {
                    extension = name.substring(name.lastIndexOf("."), name.length());
                } else {
                    extension = this.getExtension(null, data);
                }

                String contentType = this.getContentType(data, extension);

                respuesta.setContenido(data, contentType);
                respuesta.getHttpServletResponse().setHeader("Content-Disposition", 
                        String.format(
                        ("1".equals(directDownload) ? "attachment" : "inline") + "; filename=\"%s\"",
                        name));
            }
        }

        return respuesta;
    }

    protected byte[] getFileRawData(File file) {
        byte[] data = null;

        try {
            data = FileUtils.readFileToByteArray(file);
        } catch (IOException ioe) {
            Debug.error("Problemas al leer el archivo " + file.getName(), ioe);
        }

        return data;
    }

    protected String getExtension(DatosArchivo datosArchivo, byte[] pData) {
        if (datosArchivo != null && StringUtils.isNotBlank(datosArchivo.getExtension())
                && !PUNTO.equals(datosArchivo.getExtension())) {
            return datosArchivo.getExtension();
        }

        if (pData == null) {
            return "unknown";
        }

        try {
            return FileHelper.getExtension(pData).replaceAll("\\.", "");
        } catch (Exception e) {
            return "unknown";
        }
    }

    protected String getContentType(byte[] data, String extension) {
        String contentType = "text/plain";

        try {
            contentType = this.getContentTypeByExtension(extension);
            if (StringUtils.isBlank(contentType)) {
                contentType = FileHelper.getContentType(data);
            }
        } catch (Exception e) {
            Debug.error("Problemas al obtener el content type del archivo", e);
        }

        return contentType;
    }

    public String getContentTypeByExtension(String extension) {
        String ctype = StringUtils.EMPTY;

        for (ReportTypes rt : ReportTypes.values()) {
            if (rt.getExtension().equals(extension)) {
                ctype = rt.getContentType();
            }

        }

        return ctype;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {}
}
