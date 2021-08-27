package com.fitbank.webpages.behaviors;

import com.fitbank.util.Editable;
import com.fitbank.webpages.AbstractJSBehaivor;
import java.util.LinkedList;
import java.util.List;

/**
 * Clase que borra los valores actuales en una lista de campos
 *
 * @author FitBank HB, JB
 */
public class ClearValues extends AbstractJSBehaivor {

    @Editable(weight = 1)
    private List<String> elements = new LinkedList<String>();

    public List<String> getElements() {
        return elements;
    }

    public void setElements(List<String> elements) {
        this.elements = elements;
    }
}