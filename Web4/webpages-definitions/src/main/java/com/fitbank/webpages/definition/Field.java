package com.fitbank.webpages.definition;

import java.util.Collection;
import java.util.LinkedList;

import com.fitbank.util.Editable;
import com.fitbank.util.Servicios;
import com.fitbank.webpages.Widget;

/**
 * Clase que define un campo en el formulario.
 *
 * @author FitBank CI
 */
public class Field {

    @Editable(weight = 1)
    private String label = "";

    @Editable(weight = 2)
    private Collection<Widget> widgets = new LinkedList<Widget>();

    private transient String id = Servicios.generarIdUnicoTemporal();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Collection<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(Collection<Widget> widgets) {
        this.widgets = widgets;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
