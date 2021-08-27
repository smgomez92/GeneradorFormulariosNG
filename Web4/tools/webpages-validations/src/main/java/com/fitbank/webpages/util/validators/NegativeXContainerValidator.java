package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.ValidationMessage.Severity;
import com.fitbank.webpages.util.Validator;

/**
 * Validador que marca con error los contenedores que tienen un valor negativo
 * en X.
 * @author Fitbank RB
 */
public class NegativeXContainerValidator extends Validator {
    
    public static final String CODIGO_MENSAJE = "X_NEGATIVO";
    
    @Override
    public Collection<ValidationMessage> validate(Container container, WebPage fullWebPage) {
        Collection<ValidationMessage> mensajes = new LinkedList<ValidationMessage>();
        
        if (container.getX() < 0) {
            mensajes.add(new ValidationMessage(this, CODIGO_MENSAJE,
                    container, container, Severity.ERROR));
        }
        
        return mensajes;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        return Collections.EMPTY_LIST;
    }
    
}
