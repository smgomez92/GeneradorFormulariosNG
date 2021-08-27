package com.fitbank.web.uci.db;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import lombok.Data;

import com.fitbank.common.helper.XMLParser;
import com.fitbank.dto.GeneralResponse;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.enums.MessageType;
import com.fitbank.enums.UtilEnums;
import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.db.Navigation;
import com.fitbank.web.db.TransporteDB;

@Data
public class TransporteDBUCI implements TransporteDB {

    private static final long serialVersionUID = 1L;

    private static final String BPM = "_BPM_";

    private final Detail detail;

    private Navigation navigation = new Navigation();

    public TransporteDBUCI() {
        this.detail = new Detail();
        getDetail().setResponse(new GeneralResponse(GeneralResponse.OK));
        load();
    }

    public TransporteDBUCI(XMLParser xmlParser) {
        this.detail = new Detail(xmlParser);
        load();
    }

    public TransporteDBUCI(Detail detail) {
        this.detail = detail;
        load();
    }

    @Override
    public String getMessageId() {
        return detail.getMessageId();
    }

    @Override
    public void setMessageId(String messageId) {
        detail.setMessageId(messageId);
    }

    @Override
    public MessageType getMessageType() {
        return UtilEnums.getEnumValue(MessageType.class, detail.getType());
    }

    @Override
    public void setMessageType(MessageType messageType) {
        detail.setType(messageType == null ? null : messageType.getValue());
    }

    @Override
    public String getUser() {
        return detail.getUser();
    }

    @Override
    public void setUser(String usuario) {
        detail.setUser(usuario);
    }

    @Override
    public String getPassword() {
        return detail.getPassword();
    }

    @Override
    public void setPassword(String clave) {
        detail.setPassword(clave);
    }

    @Override
    public String getNewPassword() {
        return detail.getNewpassword();
    }

    @Override
    public void setNewPassword(String clave) {
        detail.setNewPassword(clave);
    }

    @Override
    public String getCompany() {
        return detail.getCompany() == null ? "" : detail.getCompany().toString();
    }

    @Override
    public void setCompany(String company) {
        detail.setCompany(company == null ? null : Integer.parseInt(company));
    }

    @Override
    public String getSubsystem() {
        return detail.getSubsystem();
    }

    @Override
    public void setSubsystem(String subsystem) {
        detail.setSubsystem(subsystem);
    }

    @Override
    public String getTransaction() {
        return detail.getTransaction();
    }

    @Override
    public void setTransaction(String transaction) {
        detail.setTransaction(transaction);
    }

    @Override
    public String getVersion() {
        return detail.getVersion();
    }

    @Override
    public void setVersion(String version) {
        detail.setVersion(version);
    }

    @Override
    public String getLanguage() {
        return detail.getLanguage();
    }

    @Override
    public void setLanguage(String idioma) {
        detail.setLanguage(idioma);
    }

    @Override
    public String getSessionId() {
        return detail.getSessionid();
    }

    @Override
    public void setSessionId(String idSesion) {
        detail.setSessionid(idSesion);
    }

    @Override
    public String getIpAddress() {
        return detail.getIpaddress();
    }

    @Override
    public void setIpAddress(String ip) {
        detail.setIpaddress(ip);
    }

    @Override
    public Date getAccountingDate() {
        return detail.getAccountingDate();
    }

    @Override
    public String getTerminal() {
        return detail.getTerminal();
    }

    @Override
    public String getOriginBranch() {
        return detail.getOriginBranch() == null ? "" : detail.getOriginBranch().
                toString();
    }

    @Override
    public String getOriginOffice() {
        return detail.getOriginOffice() == null ? "" : detail.getOriginOffice().
                toString();
    }

    @Override
    public String getChannel() {
        return detail.getChannel() == null ? "" : detail.getChannel();
    }

    @Override
    public String getResponseCode() {
        return detail.getResponse() == null ? null : detail.getResponse().
                getCode();
    }

    @Override
    public void setResponseCode(String codigoRespuesta) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getMessage() {
        return detail.getResponse() == null ? null : detail.getResponse().
                getUserMessage();
    }

    @Override
    public void setMessage(String mensaje) {
        if (this.detail.getResponse() == null) {
            this.cleanResponse();
        }
        this.detail.getResponse().setUserMessage(mensaje);
    }

    @Override
    public String getErrorMessage() {
        return detail.getResponse() == null ? null : detail.getResponse().
                getTechnicalMessage();
    }

    @Override
    public void setErrorMessage(String mensaje) {
        if (this.detail.getResponse() == null) {
            this.cleanResponse();
        }
        this.detail.getResponse().setTechnicalMessage(mensaje);
    }

    @Override
    public String getStackTrace() {
        return detail.getResponse() == null ? null : detail.getResponse().
                getStackTrace();
    }

    @Override
    public void setStackTrace(String stackTrace) {
        if (this.detail.getResponse() == null) {
            this.cleanResponse();
        }
        this.detail.getResponse().setStackTrace(stackTrace);
    }

    @Override
    public String getCurrency() {
        return EntornoWeb.getDatosSesion().getCurrency();
    }

    @Override
    public String getSchemaVersion() {
        return EntornoWeb.getDatosSesion().getSchemaVersion();
    }

    @Override
    public String getRoleName() {
        return EntornoWeb.getDatosSesion().getRoleName();
    }

    @Override
    public String getBranchName() {
        return EntornoWeb.getDatosSesion().getBranchName();
    }

    @Override
    public String getOfficeName() {
        return EntornoWeb.getDatosSesion().getOfficeName();
    }

    @Override
    public String getAreaName() {
        return EntornoWeb.getDatosSesion().getAreaName();
    }

    @Override
    public void cleanResponse() {
        detail.setResponse(new GeneralResponse(GeneralResponse.OK));
    }

    @Override
    public void clean() {
        detail.removeTables();
    }

    @Override
    public boolean hasMorePages(String alias) {
        return detail.findTableByAlias(alias).getHasMorePages().equals("1");
    }

    /**
     * Carga los valores desde el Detail a la propiedad navigation
     */
    public final void load() {
        navigation.setKey(getStringValue(TransporteDBUCI.BPM + "KEY"));
        navigation.setAction(getStringValue(TransporteDBUCI.BPM + "ACTION"));
        navigation.setNext(getBooleanValue(TransporteDBUCI.BPM + "NEXT"));
        navigation.setPrev(getBooleanValue(TransporteDBUCI.BPM + "PREV"));
        load("FIELDS", navigation.getFields());
        load("VALUES", navigation.getValues());
    }

    private void load(String name, Map<String, String> items) {
        String itemsString = getStringValue(TransporteDBUCI.BPM + name);
        items.clear();

        if (StringUtils.isNotBlank(itemsString)) {
            for (String item : itemsString.split(Pattern.quote("|"))) {
                String[] parts = item.split(Pattern.quote(","), 2);

                items.put(parts[0], parts[1]);
            }
        }
    }

    private String getStringValue(String name) {
        Field field = detail.findFieldByName(name);

        if (field == null) {
            return null;
        }

        return field.getStringValue();
    }

    private boolean getBooleanValue(String name) {
        Field field = detail.findFieldByName(name);

        if (field == null) {
            return false;
        }

        return field.getBooleanValue();
    }

    /**
     * Guarda los valores desde la propiedad navigation en el Detail
     */
    public final void save() {
        save("KEY", navigation.getKey());
        save("ACTION", navigation.getAction());
        save("FIELDS", navigation.getFields());
        save("VALUES", navigation.getValues());
    }

    private void save(String name, String value) {
        if (StringUtils.isBlank(value)) {
            detail.removeField(TransporteDBUCI.BPM + name);
        } else {
            detail.findFieldByNameCreate(TransporteDBUCI.BPM + name).
                    setValue(value);
        }
    }

    private void save(String name, Map<String, String> itemMap) {
        if (itemMap.isEmpty()) {
            detail.removeField(TransporteDBUCI.BPM + name);
        } else {
            List<String> items = new LinkedList<String>();

            for (String item : itemMap.keySet()) {
                items.add(item + "," + itemMap.get(item));
            }

            detail.findFieldByNameCreate(TransporteDBUCI.BPM + name).
                    setValue(StringUtils.join(items, "|"));
        }
    }

    public void readFields() {
        EntornoWeb.getDatosSesion().setCurrency(
                detail.findFieldByNameCreate("_FITCURRENCY_").getStringValue());
        EntornoWeb.getDatosSesion().setSchemaVersion(
                detail.findFieldByNameCreate("_VersionEsquema").getStringValue());
        EntornoWeb.getDatosSesion().setRoleName(
                detail.findFieldByNameCreate("_RoleName").getStringValue());
        EntornoWeb.getDatosSesion().setBranchName(
                detail.findFieldByNameCreate("_BranchName").getStringValue());
        EntornoWeb.getDatosSesion().setOfficeName(
                detail.findFieldByNameCreate("_OfficeName").getStringValue());
        EntornoWeb.getDatosSesion().setAreaName(
                detail.findFieldByNameCreate("_AreaName").getStringValue());
    }

    @Override
    public String toString() {
        try {
            return detail.toErrorXml();
        } catch (Exception e) {
            // Excepcion desconocida!!!
            Debug.error(e);
            return "";
        }
    }

}
