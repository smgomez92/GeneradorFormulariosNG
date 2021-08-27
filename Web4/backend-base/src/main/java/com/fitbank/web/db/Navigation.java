package com.fitbank.web.db;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que contiene información sobre la navegación.
 *
 * @author FitBank CI
 */
public class Navigation implements Serializable {

    private String key = "";

    private String action = "";

    private final Map<String, String> fields = new HashMap<String, String>();

    private final Map<String, String> values = new HashMap<String, String>();

    private boolean next = false;

    private boolean prev = false;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public boolean getNext() {
        return next;
    }

    public void setNext(boolean next) {
        this.next = next;
    }

    public boolean getPrev() {
        return prev;
    }

    public void setPrev(boolean prev) {
        this.prev = prev;
    }

}
