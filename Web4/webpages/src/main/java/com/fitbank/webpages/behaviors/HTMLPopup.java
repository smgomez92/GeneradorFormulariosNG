package com.fitbank.webpages.behaviors;

import com.fitbank.webpages.AbstractJSBehaivor;
import com.fitbank.util.Editable;

/**
 * Popup en html
 *
 * @author FitBank JB
 */
public class HTMLPopup extends AbstractJSBehaivor {
    
    @Editable
    private String label = "";

    @Editable
    private String height = "";

    @Editable
    private String width = "";

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

}
