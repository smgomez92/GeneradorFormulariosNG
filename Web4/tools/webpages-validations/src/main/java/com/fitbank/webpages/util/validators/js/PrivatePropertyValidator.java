package com.fitbank.webpages.util.validators.js;

import com.fitbank.webpages.util.ValidationMessage.Severity;
import org.mozilla.javascript.ast.*;

/**
 * Validador que revisa que no se usen propiedades privadas en javascript.
 * @author Fitbank RB
 */
public class PrivatePropertyValidator extends JSValidator {
    
    @Override
    public boolean isFixable() {
        return false;
    }
    
    @Override
    public Severity getSeverity() {
        return Severity.WARN;
    }

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        return new PrivatePropertyNodeVisitor();
    }
    
    private static class PrivatePropertyNodeVisitor extends FixValidateNodeVisitor {

        @Override
        public boolean visit(AstNode nodo) {
            if (nodo instanceof PropertyGet) {
                String prop = ((PropertyGet) nodo).getProperty().getIdentifier();
                
                if (prop.startsWith("_") && validateOnly) {
                    throw new ValidationException("Se detectó el uso de la propiedad '"
                            + prop + "' en javascript. Se consideran propiedades o funciones "
                            + "privadas aquellas que inician con '_'. Estas propiedades "
                            + "son de uso exclusivo del sistema y no deben usarse desde "
                            + "el código del formulario.");
                }
            }
            
            return true;
        }
        
    }
    
}
