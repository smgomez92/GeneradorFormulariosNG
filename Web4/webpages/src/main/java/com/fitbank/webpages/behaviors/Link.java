package com.fitbank.webpages.behaviors;

import com.fitbank.js.FuncionJS;
import com.fitbank.js.LiteralJS;
import com.fitbank.webpages.AbstractJSBehaivor;
import java.util.HashMap;
import java.util.Map;

import com.fitbank.util.Editable;

/**
 * Clase que agrega un link a un Button o a un InputLabel
 *
 * @author FitBank CI
 */
public class Link extends AbstractJSBehaivor {

    @Editable(weight = 1)
    private String subsystem = "";

    @Editable(weight = 2)
    private String transaction = "";

    @Editable(weight = 3)
    private final Map<String, String> values = new HashMap<String, String>();
    
    @Editable(weight = 4, hints={"javascript"})
    private String jsObject = "";

    @Editable(weight = 5)
    private boolean goToRecord = false;

    @Editable(weight = 6)
    private boolean query = true;
    
    @Editable(weight = 7)
    private LiteralJS preLink = new FuncionJS("", "options");

    @Editable(weight = 8)
    private LiteralJS posLink = new FuncionJS("", "options");

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public String getJsObject() {
        return jsObject;
    }

    public void setJsObject(String jsObject) {
        this.jsObject = jsObject;
    }

    public boolean getGoToRecord() {
        return goToRecord;
    }

    public void setGoToRecord(boolean goToRecord) {
        this.goToRecord = goToRecord;
    }

    public boolean getQuery() {
        return query;
    }

    public void setQuery(boolean query) {
        this.query = query;
    }

    public LiteralJS getPosLink() {
        return posLink;
    }

    public void setPosLink(LiteralJS posLink) {
        this.posLink = posLink;
    }

    public LiteralJS getPreLink() {
        return preLink;
    }

    public void setPreLink(LiteralJS preLink) {
        this.preLink = preLink;
    }

}
