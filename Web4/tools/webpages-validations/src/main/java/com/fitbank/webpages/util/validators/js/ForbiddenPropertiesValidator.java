package com.fitbank.webpages.util.validators.js;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.Scope;

import com.fitbank.util.Debug;
import com.fitbank.webpages.util.ValidationUtils;

/**
 * Validador que no permite usar ciertas propiedades de los elementos del formulario
 * mediante código.
 * 
 * @author Fitbank RB
 */
public class ForbiddenPropertiesValidator extends JSValidator {

    private PropertiesNodeVisitor visitor = new PropertiesNodeVisitor();

    private static Map<String, String> propiedades = null;

    static {
        try {
            propiedades = ValidationUtils.toMap(ResourceBundle.getBundle("forbiddenProperties"));
        } catch (MissingResourceException ex) {
            Debug.error("No se encontró lista de propiedades prohibidas.");
        }
    }

    @Override
    public boolean isFixable() {
        return false;
    }

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        visitor.setRoot(root);
        return visitor;
    }

    private static class PropertiesNodeVisitor extends FixValidateNodeVisitor {

        @Override
        public boolean visit(AstNode nodo) {
            if (propiedades != null && nodo instanceof PropertyGet) {
                PropertyGet pget = (PropertyGet) nodo;
                String propiedad = pget.getProperty().getIdentifier();

                if (propiedades.containsKey(propiedad) && validateOnly) {
                    throw new ValidationException("Se está usando '" + propiedad
                            + "' en código. " + propiedades.get(propiedad));
                }
            }

            return true;
        }

    }

}
