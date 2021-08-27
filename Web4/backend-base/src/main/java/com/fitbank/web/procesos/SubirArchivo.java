package com.fitbank.web.procesos;

import java.io.IOException;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.webpages.assistants.File;
import com.fitbank.webpages.data.FormElement;

@Handler(GeneralRequestTypes.SUBIR)
@RevisarSeguridad
public class SubirArchivo implements Proceso {

    @SuppressWarnings("unchecked")
    public RespuestaWeb procesar(PedidoWeb pedido) {
        if (FileUploadBase.isMultipartContent(new ServletRequestContext(pedido.
                getHttpServletRequest()))) {

            try {
                List<FileItem> files = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(pedido.getHttpServletRequest());

                for (FileItem file : files) {
                    try {
                        procesarArchivo(file);
                    } catch (IOException e) {
                        throw new ErrorWeb("No se pudo leer el archivo subido", e);
                    }
                }

            } catch (FileUploadException e) {
                throw new ErrorWeb("No se envió datos para subir archivos", e);
            }

        } else {
            throw new ErrorWeb("No se envió datos para subir archivos");
        }

        pedido.redireccionar("postsubir.html?" + pedido.getValorRequestHttp("_proceso"));

        return new RespuestaWeb(pedido);
    }

    private void procesarArchivo(FileItem actual) throws IOException {
        if (!actual.isFormField() && !actual.getName().equals("")) {
            // Obtener información del campo
            int pos = actual.getFieldName().lastIndexOf("_");
            String name = actual.getFieldName().substring(0, pos);
            int registro = Integer.parseInt(actual.getFieldName().substring(pos + 1));
            FormElement formElement = EntornoWeb.getContexto().getWebPage().
                    findFormElement(name);
            String alias = formElement.getDataSource().getAlias();

            if (StringUtils.isEmpty(alias)) {
                throw new ErrorWeb("El elemento " + name
                        + " no tiene alias definido");
            }

            byte[] datos = IOUtils.toByteArray(actual.getInputStream());
            int tamanoMax = ((File) formElement.getAssistant()).getMaxFileSize();

            if (datos.length > tamanoMax * 1024L) {
                throw new ErrorWeb("El tamaño del archivo seleccionado en el registro #" + (registro + 1)
                        + " excede el máximo tamaño permitido de " + tamanoMax + " KB.");
            }

            String contenido = Base64.encodeBase64String(datos);
            formElement.getFieldData().setValue(registro, contenido);
        }
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        Debug.error("Error al subir");

        String templateString = "<html><head><script>"
                + "top.Enlace.idProceso = null;"
                + "top.Estatus.finalizarProceso('Error.', '%s');"
                + "top.Estatus.mensaje('%s', '%s', 'error', '%s');"
                + "</script></head><body></body></html>";

        String proceso = pedido.getValorRequestHttp("_proceso");

        respuesta.setContenido(String.format(templateString,
                StringEscapeUtils.escapeJavaScript(proceso),
                StringEscapeUtils.escapeJavaScript(mensajeUsuario),
                StringEscapeUtils.escapeJavaScript(stackTrace),
                StringEscapeUtils.escapeJavaScript(mensaje)), "text/html");
    }

}
