package com.fitbank.webpages.definition;

import com.fitbank.js.JS;
import java.util.Collection;
import java.util.LinkedList;

import com.fitbank.util.Editable;
import com.fitbank.webpages.AttachedWebPage;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.definition.wizard.WizardData;

/**
 * Modelo de datos más sencillo para generar automáticamente formularios.
 * 
 * @author FitBank CI
 */
public class WebPageDefinition {

    @Editable(weight = 1)
    private String title = "";

    @Editable(weight = 2)
    private String description = "";

    @Editable(weight = 3)
    private String subsystem = "";

    @Editable(weight = 4)
    private String transaction = "";

    @Editable(weight = 5)
    private Collection<String> tabLabels = new LinkedList<String>();

    @Editable(weight = 6)
    private Collection<Reference> references = new LinkedList<Reference>();

    @Editable(weight = 7)
    private Collection<AttachedWebPage> attached = new LinkedList<AttachedWebPage>();

    @Editable(weight = 8)
    private Collection<Group> groups = new LinkedList<Group>();

    @JS
    private WizardData wizardData = null;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsistema) {
        this.subsystem = subsistema;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaccion) {
        this.transaction = transaccion;
    }

    public Collection<String> getTabLabels() {
        return tabLabels;
    }

    public void setTabLabels(Collection<String> tabLabels) {
        this.tabLabels = tabLabels;
    }

    public Collection<Group> getGroups() {
        return groups;
    }

    public void getGroups(Collection<Group> groups) {
        this.groups = groups;
    }

    public Collection<Reference> getReferences() {
        return references;
    }

    public void setReferences(Collection<Reference> references) {
        this.references = references;
    }

    public Collection<AttachedWebPage> getAttached() {
        return attached;
    }

    public void setAttached(Collection<AttachedWebPage> attached) {
        this.attached = attached;
    }

    public WizardData getWizardData() {
        return wizardData;
    }

    public void setWizardData(WizardData wizardData) {
        this.wizardData = wizardData;
    }

}
