package com.fitbank.webpages.util.validators.js;

import java.util.LinkedList;
import java.util.List;
import org.mozilla.javascript.ast.*;

/**
 * Verifica que no se dejen catch vacíos ya que se pueden perder excepciones que
 * pueden ayudar a depurar el código js.
 * 
 * @author Fitbank RB
 */
public class EmptyCatchValidator extends JSValidator {

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        return new EmptyCatchNodeVisitor();
    }
    
    private static class EmptyCatchNodeVisitor extends FixValidateNodeVisitor {
        
        private static final String MENSAJE = "Se ha encontrado una cláusula "
                + "catch vacía. Para facilitar la depuración del código de los formularios, "
                + "es mejor que las cláusulas catch informen de la excepción al usuario "
                + "o envíen algún mensaje a la consola del navegador.\n"
                + "El arreglo automático hará que se muestre la excepción en el estatus.\n"
                + "Código con error:\n";

        @Override
        public boolean visit(AstNode nodo) {
            if (nodo instanceof TryStatement) {
                TryStatement tryStatement = (TryStatement) nodo;
                List<CatchClause> catches = tryStatement.getCatchClauses();
                
                for (CatchClause clause : catches) {
                    if (isBodyEmpty(clause.getBody().toSource())) {
                        if (validateOnly) {
                            throw new ValidationException(MENSAJE
                                    + tryStatement.toSource());
                        }
                        
                        AstNode defaultContent = createDefaultCatchContent(
                                clause.getVarName().getIdentifier());
                        
                        clause.getBody().addChildToFront(defaultContent);
                    }
                }
            }
            
            return true;
        }
        
        private boolean isBodyEmpty(String bodySource) {
            bodySource = bodySource.replaceAll("\\s+", "");
            return bodySource.equals("{}");
        }
        
        private ExpressionStatement createDefaultCatchContent(String varName) {
            ExpressionStatement statement = new ExpressionStatement();
            FunctionCall func = new FunctionCall();
            PropertyGet estatusMensaje = new PropertyGet(new Name(0, "Estatus"),
                    new Name(1, "mensaje"));
            
            //mensaje, stack, "error"
            List<AstNode> params = new LinkedList<AstNode>();
            
            //argumento message
            AstNode paramTemp = new PropertyGet(new Name(0, varName),
                    new Name(0, "message"));
            params.add(paramTemp);
            
            //argumento stack
            paramTemp = new PropertyGet(new Name(0, varName), new Name(1, "stack"));
            params.add(paramTemp);
            
            //parametro className
            StringLiteral paramTemp2 = new StringLiteral();
            paramTemp2.setValue("error");
            paramTemp2.setQuoteCharacter('"');
            params.add(paramTemp2);
            
            func.setTarget(estatusMensaje);
            func.setArguments(params);
            statement.setExpression(func);
            
            return statement;
        }
        
    }
    
}
