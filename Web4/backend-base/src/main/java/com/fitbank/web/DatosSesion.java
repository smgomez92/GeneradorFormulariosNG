package com.fitbank.web;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

import com.fitbank.util.Debug;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.db.TransporteDBFactory;

@Data
public class DatosSesion implements Serializable {

    private String nameUsuario;

    private String nameClave;

    private String nameClave2;

    private String currency = "";

    private String schemaVersion = "";

    private String roleName = "";

    private String branchName = "";

    private String officeName = "";

    private String areaName = "";

    private String ipaddress = "";

    private boolean debug = false;

    private TransporteDB transporteDBBase = TransporteDBFactory.newInstance();

    private Map<String, Contexto> contextos = new ContextMap<String, Contexto>();

    public DatosSesion() {
        resetNames();
    }

    public final void resetNames() {
        Debug.debug("Reseteando nombres de DatosSesion...");
        nameUsuario = String.valueOf(Math.random());
        nameClave = String.valueOf(Math.random());
        nameClave2 = String.valueOf(Math.random());
    }

}
