package com.fitbank.webpages.util.validators.js;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Scope;

import com.fitbank.util.Debug;
import com.fitbank.webpages.util.ValidationUtils;

/**
 * Validador que busca funciones dentro de javascript que no se deben usar,
 * como eval, alert, stringToFecha, etc.
 * @author Fitbank RB
 */
public class ForbiddenFunctionsValidator extends JSValidator {

    private FunctionsNodeVisitor visitor = new FunctionsNodeVisitor();

    private static Map<String, String> funciones = null;
    
    static {
        try {
            funciones = ValidationUtils.toMap(ResourceBundle.getBundle("forbiddenFunctions"));
        } catch (MissingResourceException ex) {
            Debug.error("No se encontró lista de funciones prohibidas.");
            funciones = null;
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

    private static class FunctionsNodeVisitor extends FixValidateNodeVisitor {

        @Override
        public boolean visit(AstNode nodo) {
            if (funciones != null && nodo instanceof FunctionCall) {
                FunctionCall func = (FunctionCall) nodo;
                String nombre = func.getTarget().toSource();

                if (funciones.containsKey(nombre) && validateOnly) {
                    throw new ValidationException("Función '" + nombre
                            + "' encontrada. " + funciones.get(nombre));
                }
            }
            
            return true;
        }

    }
    
}
