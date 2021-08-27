package com.fitbank.webpages.util.validators.js;

import com.fitbank.util.Debug;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.UnaryExpression;

/**
 * Valida que en un formulario no se use expresiones de tipo c.$('XXX')[YYY]
 * y las cambia por c.$('XXX', YYY)
 * @author Fitbank RB
 */
public class ArrayNotationValidator extends JSValidator {

    private FixArrayNodeVisitor visitor = new FixArrayNodeVisitor();

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        visitor.setRoot(root);
        return visitor;
    }

    private static class FixArrayNodeVisitor extends FixValidateNodeVisitor {

        public FixArrayNodeVisitor() {}

        public FixArrayNodeVisitor(AstNode raiz) {
            super(raiz);
        }

        public boolean visit(AstNode nodo) {
            if (!(nodo instanceof ElementGet)) {
                return true;
            }

            ElementGet elem = (ElementGet) nodo;

            if (elem.getTarget() instanceof FunctionCall) {
                //llamada es todo el nodo que tiene c.$(XXX)
                FunctionCall llamada = (FunctionCall) elem.getTarget();

                if (llamada.getTarget() instanceof PropertyGet) {
                    PropertyGet nombreFunc = (PropertyGet) llamada.getTarget();
                    Matcher matcher2 = Pattern.compile("^c\\.\\$[NV]?$").
                            matcher(nombreFunc.toSource());

                    if (!matcher2.find()) {
                        Debug.info("Nombre de llamada no coincide con el "
                                + "patrón: " + nombreFunc.toSource());
                        return true;
                    }
                    
                    if (validateOnly) {
                        throw new ValidationException(
                                "El código usa la notación c.$(XXX)[YYY]:\n"
                                + elem.toSource());
                    }
                    
                    AstNode padre = elem.getParent();
                    
                    if (padre == null) {
                        Debug.info("Padre es null!");
                        return true;
                    }

                    llamada.addArgument(elem.getElement());

                    if (padre instanceof PropertyGet) {

                        PropertyGet pg = (PropertyGet) padre;
                        pg.setTarget(llamada);

                    } else if (padre instanceof FunctionCall) {
                        FunctionCall fc = (FunctionCall) padre;
                        List<AstNode> args2 = fc.getArguments();
                        int pos = args2.indexOf(elem);

                        if (pos == -1) {
                            Debug.warn("El nodo a cambiar no se encuentra "
                                    + "dentro de los argumentos de la función.");
                            llamada.getArguments().remove(elem.getElement());
                        } else {
                            args2.set(pos, llamada);
                        }

                    } else if (padre instanceof UnaryExpression) {
                        
                        UnaryExpression uexpr = (UnaryExpression) padre;
                        uexpr.setOperand(llamada);

                    } else if (padre instanceof ExpressionStatement) {

                        ExpressionStatement expr = (ExpressionStatement) padre;
                        expr.setExpression(llamada);
                        
                    } else if (padre instanceof InfixExpression) {
                        //Expresión con un operador binario
                        InfixExpression exprBinaria = (InfixExpression) padre;

                        if (exprBinaria.getLeft() == elem) {
                            exprBinaria.setLeft(llamada);
                        } else if (exprBinaria.getRight() == elem) {
                            exprBinaria.setRight(llamada);
                        } else {
                            Debug.error("No se pudo colocar el nodo corregido "
                                    + "como hijo de InfixExpression.");
                            llamada.getArguments().remove(elem.getElement());
                        }
                    } else if (padre instanceof IfStatement) {
                        IfStatement nodoIf = (IfStatement) padre;
                        if (nodoIf.getCondition() == elem) {
                            nodoIf.setCondition(llamada);
                        } else if (nodoIf.getThenPart() == elem) {
                            nodoIf.setThenPart(llamada);
                        } else if (nodoIf.getElsePart() == elem) {
                            nodoIf.setElsePart(llamada);
                        } else {
                            Debug.info("No se pudo colocar el nodo corregido "
                                    + "como hijo de IfStatement.");
                            llamada.getArguments().remove(elem.getElement());
                        }
                    } else {
                        llamada.getArguments().remove(elem.getElement());
                        Debug.warn("No existe un cambio para la clase "
                                + "padre del nodo: " + elem.getParent().
                                getClass().getName() + "\n>> Código del padre:\n"
                                + padre.toSource());
                    }
                }
            }

            return true;
        }
    }
}
