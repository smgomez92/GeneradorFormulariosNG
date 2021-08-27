package com.fitbank.webpages.util.validators.js;

import com.fitbank.util.Debug;
import java.util.Collections;
import java.util.List;
import org.mozilla.javascript.ast.*;

/**
 * Validador para reemplazar c.$('campo').setStyle({display: 'none'}) por
 * hide();
 * @author Fitbank RB
 */
public class DisplayNoneValidator extends JSValidator {
    
    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        return new DisplayNoneNodeVisitor();
    }
    
    private static class DisplayNoneNodeVisitor extends FixValidateNodeVisitor {
        
        @Override
        public boolean visit(AstNode nodo) {
            if (nodo instanceof Assignment) {
                revisarAsignacion((Assignment) nodo);
            }
            
            if (nodo instanceof FunctionCall) {
                FunctionCall func = (FunctionCall) nodo;
                
                if (func.getTarget() instanceof PropertyGet) {
                    PropertyGet pget = (PropertyGet) func.getTarget();
                    
                    String target = pget.getTarget().toSource();
                    if (pget.getProperty().getIdentifier().equals("setStyle")) {
                        revisarEstilos(func, pget, target.equals("Element") ? true : false);
                    }
                }
            }
            
            return true;
        }

        private void revisarAsignacion(Assignment asignacion) throws ValidationException {
            AstNode nodo = asignacion.getLeft();
            
            if (nodo instanceof PropertyGet) {
                PropertyGet pget = (PropertyGet) nodo;
                
                if (pget.toSource().endsWith("style.display")) {
                    
                    if (validateOnly && asignacion.getRight().toSource()
                            .replaceAll("[\"']", "").matches("none|block")) {
                        throw new ValidationException("Es preferible usar hide() "
                                + "o show() en lugar de asignar directamente "
                                + "la propiedad display de style. Error en:\n"
                                + asignacion.toSource());
                    }
                    
                    AstNode valor = asignacion.getRight();
                    AstNode parent = asignacion.getParent();
                    PropertyGet hijo = (PropertyGet) pget.getTarget();
                    
                    //Solo si la propiedad se establece en "none" o "block" se
                    //realiza el cambio.
                    if (valor instanceof StringLiteral) {
                        String strValor = ((StringLiteral) valor).getValue();
                        
                        if (parent instanceof ExpressionStatement) {
                            if (strValor.equals("none")) {
                                hijo.getProperty().setIdentifier("hide");
                                FunctionCall hide = new FunctionCall();
                                hide.setTarget(hijo);
                                ((ExpressionStatement) parent).setExpression(hide);
                            } else if (strValor.equals("block")) {
                                //Se asume que querían mostrar el botón.
                                hijo.getProperty().setIdentifier("show");
                                FunctionCall hide = new FunctionCall();
                                hide.setTarget(hijo);
                                ((ExpressionStatement) parent).setExpression(hide);
                            }
                        } else {
                            Debug.warn("No se pudo cambiar style.display por hide() "
                                    + "para nodo padre tipo " + parent.getClass().getName()
                                    + "\nEn: " + parent.toSource());
                        }
                    }
                }
            }
        }
        
        private void revisarEstilos(FunctionCall llamada, PropertyGet pget,
                boolean ignorarPrimerArgumento) throws ValidationException {
            List<AstNode> args = llamada.getArguments();
            
            int i = ignorarPrimerArgumento ? 1 : 0;
            AstNode arg = args.get(i);
            
            if (arg instanceof ObjectLiteral) {
                ObjectLiteral obj = (ObjectLiteral) arg;
                List<ObjectProperty> estilos = obj.getElements();

                for (int k = 0; k < estilos.size(); k++) {
                    ObjectProperty prop = estilos.get(k);
                    String identificador = prop.getLeft().toSource().replaceAll("[\"']", "");
                    String valor = prop.getRight().toSource().replaceAll("[\"']", "");

                    //Preguntar antes de hacer cualquier cambio...
                    if (identificador.equals("display") && valor.matches("none|block")) {
                        if (validateOnly) {
                            throw new ValidationException("Es preferible usar "
                                    + "hide() o show() en lugar de setStyle(...). "
                                    + "Error en: \n" + llamada.toSource());
                        }
                        
                        //Realizar el cambio solo si es el único estilo especificado.
                        if (estilos.size() > 1) {
                            Debug.info("No se pudo aplicar corrección automática "
                                    + "debido a que hay otros estilos en la declaración.");
                            return;
                        }

                        if (valor.equals("none")) {
                            pget.getProperty().setIdentifier("hide");
                        } else {
                            pget.getProperty().setIdentifier("show");
                        }

                        llamada.setArguments(Collections.EMPTY_LIST);
                    }
                }
                
            } else if (arg instanceof StringLiteral) {
                String propiedadCSS = ((StringLiteral) arg).getValue();
                if (propiedadCSS.equals("display") && i + 1 < args.size()) {
                    String valor = args.get(i + 1).toSource().replaceAll("[\"']", "");
                    
                    if (validateOnly && valor.matches("none|block")) {
                        throw new ValidationException("Es preferible usar "
                                + "hide() o show() en lugar de setStyle(...). "
                                + "Error en:\n" + llamada.toSource());
                    }
                    
                    if (valor.equals("none")) {
                        pget.getProperty().setIdentifier("hide");
                        llamada.setArguments(Collections.EMPTY_LIST);
                    } else if (valor.equals("block")) {
                        pget.getProperty().setIdentifier("show");
                        llamada.setArguments(Collections.EMPTY_LIST);
                    }
                }
            }
            
        }
        
    }
    
}
