package com.fitbank.webpages.util.validators.js;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

/**
 * Clase que sirve de base para los NodeVisitors que arreglan JS.
 * Las clases derivadas deben implementar el método <code>visit</code>
 * de manera que tomen en cuenta el valor del parámetro validateOnly.
 * Si éste es <code>true</code>, el visitador deberá examinar el código en
 * busca de errores, y lanzar ValidationException si el código no cumple
 * con los requerimientos de validación. Caso contrario, el visitador deberá
 * arreglar los errores que encuentre.
 * @author Fitbank RB
 * @see ValidationException
 */
public abstract class FixValidateNodeVisitor implements NodeVisitor {

    protected boolean validateOnly = true;
    protected boolean eventCode = false;
    protected AstNode root = null;

    public FixValidateNodeVisitor() {}

    public FixValidateNodeVisitor(AstNode root) {
        this.root = root;
    }

    public boolean getValidateOnly() {
        return validateOnly;
    }

    public void setValidateOnly(boolean validateOnly) {
        this.validateOnly = validateOnly;
    }

    public boolean isEventCode() {
        return eventCode;
    }

    public void setEventCode(boolean value) {
        eventCode = value;
    }

    public AstNode getRoot() {
        return root;
    }

    public void setRoot(AstNode root) {
        this.root = root;
    }

    public abstract boolean visit(AstNode an);
}
