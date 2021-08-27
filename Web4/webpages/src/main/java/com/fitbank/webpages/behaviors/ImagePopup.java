package com.fitbank.webpages.behaviors;

import com.fitbank.webpages.AbstractJSBehaivor;
import com.fitbank.util.Editable;

/**
 * Muestra un popup de la imagen en la que se aplica este comportamiento
 *
 * @author FitBank JB
 */
public class ImagePopup extends AbstractJSBehaivor {

    @Editable
    private String height = "";

    @Editable
    private String width = "";

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
