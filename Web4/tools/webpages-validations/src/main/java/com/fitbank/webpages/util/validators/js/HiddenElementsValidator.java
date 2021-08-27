package com.fitbank.webpages.util.validators.js;


import org.mozilla.javascript.ast.*;

import com.fitbank.util.Debug;

/**
 * Validador que arregla cosas como c.$N('nombre', 0).checkbox y los convierte
 * en c.$('nombre')
 * @author Fitbank RB
 */
public class HiddenElementsValidator extends JSValidator {
    
    private static final String PROPERTIES = "checkbox|combobox|widget";

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        return new HiddenElementsNodeVisitor();
    }
    
    private static class HiddenElementsNodeVisitor extends FixValidateNodeVisitor {

        @Override
        public boolean visit(AstNode nodo) {
            if (nodo instanceof PropertyGet) {
                PropertyGet pget = (PropertyGet) nodo;
                String property = pget.getProperty().getIdentifier();
                
                if (!property.matches(PROPERTIES) || !(pget.getTarget() instanceof FunctionCall)) {
                    return true;
                }
                
                PropertyGet func = checkFunction((FunctionCall) pget.getTarget());
                
                if (func != null) {
                    if (validateOnly) {
                        throw new ValidationException("Uso de propiedad para "
                                + "obtener el elemento visible de input en:\n"
                                + nodo.toSource()
                                + "\nEn lugar de usar las propiedades checkbox, "
                                + "combobox o widget, use c.$ que devuelve el "
                                + "elemento visible.");
                    }
                    
                    //Cambiar a c.$ y quitar la propiedad...
                    //Nodo debería ser parte de un propertyGet más grande.
                    AstNode parent = pget.getParent();
                    
                    if (parent instanceof PropertyGet) {
                        func.getProperty().setIdentifier("$");
                        ((PropertyGet) parent).setTarget(pget.getTarget());
                    } else {
                        Debug.info("No se pudo arreglar nodo padre de tipo "
                                + parent.getClass().getName());
                    }
                }
            }
            
            return true;
        }
        
        private PropertyGet checkFunction(FunctionCall func) {
            if (func.getTarget() instanceof PropertyGet) {
                PropertyGet nombreFunc = (PropertyGet) func.getTarget();

                if (nombreFunc.toSource().matches("^c\\.\\$[NV]?$")) {
                    return nombreFunc;
                }
            }
            
            return null;
        }
        
    }
    
}
