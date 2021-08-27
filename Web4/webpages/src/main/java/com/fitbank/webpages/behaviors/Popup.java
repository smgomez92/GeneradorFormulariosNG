package com.fitbank.webpages.behaviors;

import com.fitbank.webpages.AbstractJSBehaivor;
import com.fitbank.util.Editable;

/**
 * Muestra un popup de un tab del webpage
 *
 * @author FitBank CI
 */
public class Popup extends AbstractJSBehaivor {

    @Editable
    private String tab = "";

    @Editable
    private String titulo = "";

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
