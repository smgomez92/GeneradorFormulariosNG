package com.fitbank.web.uci.procesos;

import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.io.IOUtils;

import com.fitbank.common.FileHelper;
import com.fitbank.common.helper.XMLParser;
import com.fitbank.dto.management.Detail;
import com.fitbank.enums.MessageType;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ManejoExcepcion;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.procesos.Registro;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.WebPage;

/**
 * Procesa un archivo
 *
 * @author FitBank CI
 */
@Handler(GeneralRequestTypes.PROCESAR)
@RevisarSeguridad
public class ProcesarArchivo implements Proceso {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        if (!Registro.habilitado()) {
            throw new ErrorWeb("No se puede realizar la operación debido a que "
                    + "se ha deshabilitado el registro.");
        }

        if (!FileUploadBase.isMultipartContent(new ServletRequestContext(pedido.
                getHttpServletRequest()))) {
            throw new ErrorWeb("No se envió datos para subir archivos");
        }

        try {
            List<FileItem> files = new ServletFileUpload(
                    new DiskFileItemFactory()).parseRequest(pedido.
                    getHttpServletRequest());

            for (FileItem file : files) {
                if (file.isFormField()) {
                    continue;
                }

                if (file.getName().endsWith(".wpx")) {
                    return processWebPage(pedido, file);
                } else if (file.getName().endsWith(".xml")) {
                    return processDetail(pedido, file);
                }
            }

        } catch (Exception e) {
            throw new ErrorWeb("No se envió datos adecuados", e);
        }

        return new RespuestaWeb(pedido);
    }

    private RespuestaWeb processWebPage(PedidoWeb pedido, FileItem file) throws
            ErrorWeb {
        String name = file.getName().replaceAll("\\.wpx$", "") + "01";

        pedido.getTransporteDB().setMessageType(MessageType.FORM);
        pedido.getTransporteDB().setSubsystem("01");
        pedido.getTransporteDB().setTransaction("0002");

        Detail detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();
        detail.findFieldByNameCreate("TIPOFORMATO").setValue(WebPage.class.
                getName());
        detail.findFieldByNameCreate("NMR").setValue(name);

        try {
            String base64 = Base64.encodeBase64String(IOUtils.toByteArray(file.
                    getInputStream()));

            detail.findFieldByNameCreate("FRM").setValue(base64);
        } catch (IOException e) {
            throw new ErrorWeb("Error al leer archivo", e);
        }

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        ManejoExcepcion.checkOkCodes(respuesta);

        respuesta.setContenido("<!DOCTYPE html><html><head><title>Resultado</title></head>"
                + "<body>Resultado: " + respuesta.getTransporteDB().
                getErrorMessage() + "</body></html>", "text/html");

        return respuesta;
    }

    private RespuestaWeb processDetail(PedidoWeb pedido, FileItem file) throws
            IOException {
        String data = FileHelper.readStream(file.getInputStream());

        try {
            pedido.setTransporteDB(new TransporteDBUCI(new XMLParser(data)));
        } catch (Exception e) {
            throw new Error("Excepción desconocida!", e);
        }

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        ManejoExcepcion.checkOkCodes(respuesta);

        respuesta.setContenido("<!DOCTYPE html><html><head><title>Resultado</title></head>"
                + "<body>Resultado: " + respuesta.getTransporteDB().
                getErrorMessage() + "</body></html>", "text/html");

        return respuesta;
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta, String mensaje,
            String mensajeUsuario, String stackTrace, TransporteDB datos) {
        respuesta.setContenido("<!DOCTYPE html><html><head><title>Resultado</title></head>"
                + "<body>Error: " + mensaje + "</body></html>", "text/html");
    }

}
