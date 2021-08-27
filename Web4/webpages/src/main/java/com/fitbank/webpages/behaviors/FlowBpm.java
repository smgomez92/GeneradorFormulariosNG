package com.fitbank.webpages.behaviors;

import java.util.HashMap;
import java.util.Map;

import com.fitbank.util.Editable;

/**
 * Clase que se encarga de manupular el flujo bpm
 *
 * @author FitBank HB, JB
 */
public class FlowBpm extends HTMLPopup {
    private static final long serialVersionUID = 1L;
    @Editable(weight = 3)
    private final Map<String, String> values = new HashMap<String, String>();

    public Map<String, String> getValues() {
        return values;
    }
}