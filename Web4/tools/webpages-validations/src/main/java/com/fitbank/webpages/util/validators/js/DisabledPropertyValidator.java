package com.fitbank.webpages.util.validators.js;

import com.fitbank.util.Debug;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.Scope;

/**
 * Validador que revisa si en el formulario está puesto
 * c.$('elemento').disabled|readOnly = true|false, y lo cambia por las llamadas
 * correspondientes de setDisabled(true|false).
 * 
 * @author Fitbank RB
 */
public class DisabledPropertyValidator extends JSValidator {

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        return new DisabledPropertyNodeVisitor();
    }

    private static class DisabledPropertyNodeVisitor extends FixValidateNodeVisitor {

        @Override
        public boolean visit(AstNode nodo) {
            if (nodo instanceof Assignment) {
                Assignment asignacion = (Assignment) nodo;

                if (asignacion.getLeft() instanceof PropertyGet) {
                    PropertyGet pget = (PropertyGet) asignacion.getLeft();
                    String identifier = pget.getProperty().getIdentifier();

                    if (identifier.matches("disabled|readOnly")) {

                        if (validateOnly) {
                            throw new ValidationException(String.format(
                                    "Se está asignando true o false a la"
                                    + " propiedad %s de un elemento en: %s\n"
                                    + "Se debe usar la función setDisabled"
                                    + " para evitar errores en el envío de datos.",
                                    identifier, asignacion.toSource()));
                        }

                        //Última verificación... el problema solo es arreglable
                        //si el lado derecho de la asignación es true o false
                        if (asignacion.getRight() instanceof KeywordLiteral) {
                            AstNode parent = asignacion.getParent();

                            if (parent instanceof ExpressionStatement) {
                                pget.getProperty().setIdentifier("setDisabled");
                                FunctionCall func = new FunctionCall();
                                func.setTarget(pget);
                                func.addArgument(asignacion.getRight());
                                ((ExpressionStatement) parent).setExpression(func);
                            } else {
                                Debug.info("Fallo al reemplazar nodo. Parent "
                                        + "tipo " + parent.getClass().getSimpleName());
                            }
                        }
                    }
                }
            }

            return true;
        }
    }
}
