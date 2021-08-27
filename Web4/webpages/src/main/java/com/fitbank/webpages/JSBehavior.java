package com.fitbank.webpages;

import com.fitbank.util.Editable;
import com.fitbank.webpages.data.FormElement;

/**
 * Clase que representa un comportamiento de javascript generico.
 *
 * @author FitBank CI
 */
public interface JSBehavior {

    public String getElementName();

    public void setFormElement(FormElement formElement);

    @Editable
    public String getMessage();

    public void setMessage(String message);

    public boolean isFireAlways();

}
