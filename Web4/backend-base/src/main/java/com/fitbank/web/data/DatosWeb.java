package com.fitbank.web.data;

import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.db.TransporteDBFactory;

/**
 * 
 * @author FitBank
 * @version 2.0
 */
public class DatosWeb {

    private static final long serialVersionUID = 2L;

    private String tipoPedido = GeneralRequestTypes.ERROR;

    private String extraTipo = null;

    private boolean recargarDB = false;

    private TransporteDB transporteDB = TransporteDBFactory.newInstance();

    protected DatosWeb() {
    }

    public String getTipoPedido() {
        return tipoPedido;
    }

    public void setTipoPedido(String tipoPedido) {
        this.tipoPedido = tipoPedido;
    }

    public String getExtraTipo() {
        return extraTipo;
    }

    public void setExtraTipo(String extraTipo) {
        this.extraTipo = extraTipo;
    }

    public TransporteDB getTransporteDB() {
        return transporteDB;
    }

    public void setTransporteDB(TransporteDB datos) {
        this.transporteDB = datos;
    }

    public boolean isRecargarDB() {
        return recargarDB;
    }

    public void setRecargarDB(boolean recargarDB) {
        this.recargarDB = recargarDB;
    }

    public void copiar(DatosWeb datosWeb) {
        tipoPedido = datosWeb.tipoPedido;
        transporteDB = datosWeb.transporteDB;
        recargarDB = datosWeb.recargarDB;
    }

}
