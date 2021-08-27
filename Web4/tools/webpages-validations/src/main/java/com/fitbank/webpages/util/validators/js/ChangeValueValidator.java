package com.fitbank.webpages.util.validators.js;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mozilla.javascript.ast.*;

/**
 * Validador que comprueba que en el formulario no se use XXX.value o
 * XXX.checked y lo cambie por XXX.changeValue
 * 
 * @author Fitbank RB
 */
public class ChangeValueValidator extends JSValidator {

    private final Pattern VALUE_REGEX = Pattern.compile(
            "\\s*\\.\\s*(value|checked)\\s*=[^=]");

    @Override
    protected boolean hasError(String js, boolean eventCode) {
        Matcher matcher = VALUE_REGEX.matcher(js);

        if (!matcher.find()) {
            return false;
        }
        
        validationDescription = "Código con error:\n" + js;

        return true;
    }

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        return new ChangeNodeVisitor(root);
    }

    private static class ChangeNodeVisitor extends FixValidateNodeVisitor {
        public ChangeNodeVisitor(AstNode raiz) {
            super(raiz);
        }

        @Override
        public boolean visit(AstNode nodo) {
            if (!(nodo instanceof ExpressionStatement)) {
                return true;
            }

            ExpressionStatement expr = (ExpressionStatement) nodo;

            if (expr.getExpression() instanceof Assignment) {
                Assignment assignment = (Assignment) expr.getExpression();

                if (assignment.getLeft() instanceof PropertyGet) {
                    PropertyGet elemValue = (PropertyGet) assignment.getLeft();

                    String propertyName = elemValue.getProperty().toSource();
                    if (propertyName.matches("value|checked")) {
                        String newProperty = propertyName.equals("value") ? 
                                "changeValue" : "setChecked";
                        elemValue.getProperty().setIdentifier(newProperty);
                        List<AstNode> args = new LinkedList<AstNode>();
                        args.add(assignment.getRight());

                        FunctionCall changeValue = new FunctionCall();
                        changeValue.setTarget(elemValue);
                        changeValue.setArguments(args);
                        expr.setExpression(changeValue);
                    }
                }
            }

            return true;
        }

    }

}
