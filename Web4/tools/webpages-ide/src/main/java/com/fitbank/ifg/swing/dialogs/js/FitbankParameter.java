package com.fitbank.ifg.swing.dialogs.js;

import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;

/**
 * Extensión del parámetro general que incluye una etiqueta, mediante la cual
 * se puede seleccionar el tipo de autocompletado para este parámetro.
 * @author Fitbank RB
 */
public class FitbankParameter extends Parameter {
    
    public static final String WIDGET_NAMES = "widgetNames";
    
    private String tag = "";
    
    public FitbankParameter(Object type, String name) {
        super(type, name);
    }
    
    public FitbankParameter(Object type, String name, String tag) {
        this(type, name);
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
}
