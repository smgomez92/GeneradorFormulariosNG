package com.fitbank.web.exceptions;

import com.fitbank.util.exceptions.RemoteException;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;

public class ErrorWeb extends Error {

    private static final long serialVersionUID = 1L;

    private TransporteDB transporteDB = null;

    public ErrorWeb() {
        super();
    }

    public ErrorWeb(String mensaje, Throwable error) {
        super(mensaje, error);
    }

    public ErrorWeb(String mensaje) {
        super(mensaje);
    }

    public ErrorWeb(Throwable error) {
        super(error);
    }

    public ErrorWeb(RespuestaWeb respuesta) {
        this(respuesta.getTransporteDB());
    }

    public ErrorWeb(TransporteDB transporteDB) {
        super(getInfo(transporteDB), getCause(transporteDB));
        this.transporteDB = transporteDB;
    }

    private static String getInfo(TransporteDB transporteDB) {
        String info = "";

        if (transporteDB != null) {
            info += transporteDB.getResponseCode() + ": ";
            info += transporteDB.getMessage();
        }

        return info;
    }

    private static Throwable getCause(TransporteDB transporteDB) {
        if (transporteDB != null) {
            return new RemoteException(transporteDB.getErrorMessage(),
                    transporteDB.getStackTrace());
        } else {
            return null;
        }
    }

    public TransporteDB getTransporteDB() {
        return transporteDB;
    }

}
