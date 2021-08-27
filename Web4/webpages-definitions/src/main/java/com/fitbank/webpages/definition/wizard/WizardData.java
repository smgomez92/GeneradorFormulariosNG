package com.fitbank.webpages.definition.wizard;

import com.fitbank.js.JS;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Datos del wizard
 *
 * @author FitBank CI
 */
public class WizardData {

    @JS
    private String tableName = "";

    @JS
    private final Collection<WizardCriterion> criteria =
            new LinkedList<WizardCriterion>();

    @JS
    private final Collection<WizardField> fields =
            new LinkedList<WizardField>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Collection<WizardCriterion> getCriteria() {
        return criteria;
    }

    public Collection<WizardField> getFields() {
        return fields;
    }

}
