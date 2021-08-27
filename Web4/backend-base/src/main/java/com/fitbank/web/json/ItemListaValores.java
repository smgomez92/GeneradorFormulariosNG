package com.fitbank.web.json;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Clase ItemsMenu, representan los items en el Menu (ComboBox).
 * 
 * @author FitBank 2.0 JT
 * 
 */
public class ItemListaValores {

    private final Collection<String> values = new LinkedList<String>();

    // ////////////////////////////////////////////////////////
    // Métodos Getters y Setters
    // ////////////////////////////////////////////////////////

    public Collection<String> getValues() {
        return values;
    }

}
