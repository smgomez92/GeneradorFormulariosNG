package com.fitbank.webpages.formulas;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Una formula y parseador de formulas.
 *
 * @author FitBank CI
 */
public class Formula {

    private final String elementName;

    private final String javaScript;

    private final Collection<String> elements = new LinkedHashSet<String>();
    
    private final Collection<String> strings = new LinkedHashSet<String>();

    public Formula(String elementName, String javaScript, Collection<String> elements,
            Collection<String> strings) {
        this.elementName = elementName;
        this.javaScript = javaScript;
        this.elements.addAll(elements);
        this.strings.addAll(strings);
    }

    public String getElementName() {
        return elementName;
    }

    public String getJavaScript() {
        return javaScript;
    }

    public Collection<String> getElements() {
        return Collections.unmodifiableCollection(elements);
    }
    
    public Collection<String> getStringLiterals() {
        return Collections.unmodifiableCollection(strings);
    }

}
