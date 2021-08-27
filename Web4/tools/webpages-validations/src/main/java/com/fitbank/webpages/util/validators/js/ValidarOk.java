package com.fitbank.webpages.util.validators.js;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.Scope;

/**
 * Agrega Validar.ok(...) en donde es necesario si no existe.
 *
 * @author FitBank CI, HB
 */
public class ValidarOk extends JSValidator {

    @Override
    protected boolean hasError(String js, boolean eventCode) {
        return js.contains("Validar.error") && !js.contains("Validar.ok");
    }

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        return new FixNodeVisitor(root);
    }

    private class FixNodeVisitor extends FixValidateNodeVisitor {

        public FixNodeVisitor(Scope scope) {
            super(scope);
        }

        public boolean visit(AstNode an) {
            if (an == root) {
                return true;
            }

            if (an instanceof ForLoop || an instanceof FunctionNode) {
                Scope scopeNode = (Scope) an;

                scopeNode.visit(new FixNodeVisitor(scopeNode));
                return false;
            }

            if (an instanceof FunctionCall) {
                FunctionCall fc = (FunctionCall) an;
                if (fc.getTarget().toSource().equals("Validar.error")) {
                    AstNode target;
                    if (root instanceof ForLoop) {
                        target = ((ForLoop) root).getBody();
                    } else if (root instanceof FunctionNode) {
                        target = ((FunctionNode) root).getBody();
                    } else {
                        target = root;
                    }

                    FunctionCall ok = new FunctionCall();

                    ok.setTarget(new PropertyGet(new Name(0, "Validar"),
                            new Name(1, "ok")));

                    List<AstNode> args = fc.getArguments();
                    List<AstNode> argsOk = new LinkedList<AstNode>();
                    argsOk.add(args.get(0));

                    if (args.size() == 3) {
                        argsOk.add(args.get(2));
                    }

                    ok.setArguments(argsOk);

                    target.addChildToFront(new ExpressionStatement(ok));

                    return false;
                }
            }

            return true;
        }

    }

}
