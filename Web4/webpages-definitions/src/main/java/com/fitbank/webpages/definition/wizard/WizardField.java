package com.fitbank.webpages.definition.wizard;

import com.fitbank.js.JS;

/**
 * Campo del wizard.
 *
 * @author FitBank CI
 */
public class WizardField {

    @JS
    private String name = "";

    @JS
    private String title = "";

    @JS
    private boolean showDescription = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getShowDescription() {
        return showDescription;
    }

    public void setShowDescription(boolean showDescription) {
        this.showDescription = showDescription;
    }

}
