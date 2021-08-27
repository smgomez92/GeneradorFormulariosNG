package com.fitbank.web.simulado.db;

import java.util.Date;

import lombok.Data;

import com.fitbank.enums.MessageType;
import com.fitbank.web.db.Navigation;
import com.fitbank.web.db.TransporteDB;

@Data
public class TransporteDBImpl implements TransporteDB {

    private static final long serialVersionUID = 1L;

    private MessageType messageType = MessageType.ERROR;

    private String messageId = "";

    private String user = null;

    private String password = null;

    private String newPassword = null;

    private String company = null;

    private String subsystem = null;

    private String transaction = null;

    private String version = null;

    private String language = null;

    private String sessionId = null;

    private String ipAddress = null;

    private Date accountingDate = new Date();

    private String terminal = null;

    private String originBranch = null;

    private String originOffice = null;

    private String channel = null;

    private String responseCode = null;

    private String message = null;

    private String errorMessage = null;

    private String stackTrace = null;

    private String currency = "";

    private String schemaVersion = "";

    private String roleName = "";

    private String branchName = "";

    private String areaName = "";

    private String officeName = "";

    @Override
    public void cleanResponse() {
        setResponseCode("");
        setMessage("");
        setStackTrace("");
    }

    @Override
    public void clean() {
        // No hay como hacer nada
    }

    @Override
    public boolean hasMorePages(String alias) {
        return true;
    }

    @Override
    public Navigation getNavigation() {
        return new Navigation();
    }

}
