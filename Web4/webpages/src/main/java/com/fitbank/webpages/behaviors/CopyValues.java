package com.fitbank.webpages.behaviors;

import com.fitbank.util.Editable;
import com.fitbank.webpages.AbstractJSBehaivor;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Copia un valor a otro campo.
 *
 * @author FitBank CI, AV
 */
public class CopyValues extends AbstractJSBehaivor {

    @Editable(weight = 1)
    private Map<String, String> fields = new LinkedHashMap<String, String>();

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return String.format("%s %s", super.toString(), getFields().toString());
    }

}
