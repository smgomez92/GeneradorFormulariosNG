package com.fitbank.web.db;

import java.io.Serializable;
import java.util.Date;

import com.fitbank.enums.MessageType;

public interface TransporteDB extends Serializable {

    public String getMessageId();

    public void setMessageId(String messageId);

    public MessageType getMessageType();

    public void setMessageType(MessageType messageType);

    public String getUser();

    public void setUser(String user);

    public String getPassword();

    public void setPassword(String password);

    public String getNewPassword();

    public void setNewPassword(String newPassword);

    public String getCompany();

    public void setCompany(String company);

    public String getSubsystem();

    public void setSubsystem(String subsystem);

    public String getTransaction();

    public void setTransaction(String transaction);

    public String getVersion();

    public void setVersion(String version);

    public String getLanguage();

    public void setLanguage(String idioma);

    public String getSessionId();

    public void setSessionId(String idSesion);

    public String getIpAddress();

    public void setIpAddress(String ip);

    public Date getAccountingDate();

    public String getTerminal();

    public String getOriginBranch();

    public String getOriginOffice();

    public String getChannel();

    public String getCurrency();

    public String getSchemaVersion();

    public String getRoleName();

    public String getBranchName();

    public String getOfficeName();

    public String getAreaName();

    public String getResponseCode();

    public void setResponseCode(String codigoRespuesta);

    public String getMessage();

    public void setMessage(String mensaje);

    public String getErrorMessage();

    public void setErrorMessage(String mensaje);

    public String getStackTrace();

    public void setStackTrace(String stackTrace);

    public void cleanResponse();

    public void clean();

    public boolean hasMorePages(String alias);

    public Navigation getNavigation();

}
