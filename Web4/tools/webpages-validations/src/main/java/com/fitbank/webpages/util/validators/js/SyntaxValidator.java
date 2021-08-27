package com.fitbank.webpages.util.validators.js;

import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Scope;

/**
 * Clase que valida errores de sintaxis en el código del formulario. No corrige
 * los errores, solo los reporta en la consola.
 * @author Fitbank RB
 */
public class SyntaxValidator extends JSValidator {

    @Override
    public boolean hasError(String js, boolean eventCode) {
        try {
            super.hasError(js, eventCode);
            validationDescription = null;
            return false;
        } catch (EvaluatorException ex) {
            validationDescription = "Error de sintaxis en javascript: " + ex.getMessage() +"\n"
                    + "Línea-columna: " + ex.lineNumber() + "-" + ex.columnNumber()
                    +"\nCódigo con error: " + ex.lineSource();

            return true;
        }
    }

    @Override
    public boolean isFixable() {
        return false;
    }

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        return new DummyNodeVisitor(root);
    }

    private static class DummyNodeVisitor extends FixValidateNodeVisitor {

        public DummyNodeVisitor() {}

        public DummyNodeVisitor(AstNode raiz) {
            super(raiz);
        }

        @Override
        public boolean visit(AstNode an) {
            return false;
        }
    }
}
