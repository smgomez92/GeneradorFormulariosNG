package com.fitbank.webpages.util.validators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.Validator;

/**
 * Validador para no permitir usar $D.
 * @author Fitbank RB
 */
public class RegisterValidator extends Validator {
    
    public static final String MENSAJE_REGISTRO = "MSJ_REGISTRO";

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        Collection<ValidationMessage> mensajes = new LinkedList<ValidationMessage>();
        String serializedWidget = widget.toStringXml();
        Matcher matcher = Pattern.compile("(?i)\\$D").matcher(serializedWidget);
        
        if (matcher.find()) {
            mensajes.add(new ValidationMessage(this, MENSAJE_REGISTRO,
                    "Se está usando $D en widget " + widget.getId()
                    + " Se recomienda usar this.registro o elemento.registro.",
                    widget, widget, ValidationMessage.Severity.ERROR, false));
        }
        
        return mensajes;
    }
    
}
