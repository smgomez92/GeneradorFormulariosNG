package com.fitbank.web.exceptions;

import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

/**
 * Clase que se usa para enviar un mensaje al web.
 *
 * @author FitBank
 */
public class MensajeWeb extends ErrorWeb {

    public MensajeWeb() {
        super();
    }

    public MensajeWeb(String mensaje, Throwable error) {
        super(mensaje, error);
    }

    public MensajeWeb(String mensaje) {
        super(mensaje);
    }

    public MensajeWeb(Throwable error) {
        super(error);
    }

    public MensajeWeb(RespuestaWeb respuesta) {
        super(respuesta);
    }

    public MensajeWeb(TransporteDB datos) {
        super(datos);
    }

}
