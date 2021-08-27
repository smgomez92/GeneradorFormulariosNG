package com.fitbank.webpages.util.validators.js;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.Scope;

import com.fitbank.webpages.util.ValidationMessage.Severity;

/**
 * Validador que encuentra for en los que la condición está establecida a una
 * constante, por ejemplo:
 * <pre>
 *  for(var i = 0; <b>i &lt; 10</b>; i++) ...
 * </pre>
 * @author Fitbank RB
 */
public class FixedLimitForValidator extends JSValidator {

    private ForCorrectorNodeVisitor validador = new ForCorrectorNodeVisitor();

    @Override
    public boolean isFixable() {
        return false;
    }
    
    @Override
    public Severity getSeverity() {
        return Severity.WARN;
    }

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        validador.cleanMessages();
        return validador;
    }

    private static class ForCorrectorNodeVisitor extends FixValidateNodeVisitor {

        private List<String> mensajes = new LinkedList<String>();

        @Override
        public boolean visit(AstNode nodo) {
            if (nodo instanceof ForLoop) {
                ForLoop forLoop = (ForLoop) nodo;
                AstNode condicion = forLoop.getCondition();

                if (condicion instanceof InfixExpression) {
                    if (validateOnly) {
                            checkInfixExpression((InfixExpression) condicion,
                                    mensajes);
                            if (!mensajes.isEmpty()) {
                                StringBuilder descripcion = new StringBuilder(1024);
                                
                                descripcion.append("Encontrados lazos for con condiciones fijas:\n\n");
                                for (String mensaje : mensajes) {
                                    descripcion.append(mensaje);
                                    descripcion.append("\n");
                                }
                                
                                descripcion.append("\nEsto puede generar errores si se cambia ").
                                        append("el número de filas clonadas del container. ").
                                        append("Para solucionar este problema, cree una variable ").
                                        append("temporal var elementos = c.$('nombre');\n").
                                        append("y use en la condición:\n").
                                        append("for (var i = 0; i < elementos.length;...).");

                                throw new ValidationException(descripcion.toString());
                            }
                    }
                }
            }

            return true;
        }

        private void checkInfixExpression(InfixExpression ie,
                List<String> msjs) {
            //Para casos en que la condición sea del tipo
            //i < 5, j > 3, k != 0
            if (ie.getLeft() instanceof InfixExpression) {
                checkInfixExpression((InfixExpression) ie.getLeft(), msjs);
            }

            if (ie.getRight() instanceof InfixExpression) {
                checkInfixExpression((InfixExpression) ie.getRight(), msjs);
            }

            switch (ie.getType()) {
                case Token.LT:
                case Token.LE:
                case Token.GE:
                case Token.GT:
                case Token.EQ:

                    if (ie.getLeft() instanceof NumberLiteral ||
                            ie.getRight() instanceof NumberLiteral) {
                        msjs.add("La condición del for tiene una constante: "
                                + ie.toSource());
                    }

                    break;
            }
        }

        public List<String> getMensajes() {
            return mensajes;
        }

        public void cleanMessages() {
            mensajes.clear();
        }
    }
    
}
