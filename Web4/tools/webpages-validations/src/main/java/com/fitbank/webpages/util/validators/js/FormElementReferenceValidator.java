package com.fitbank.webpages.util.validators.js;

import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.ValidationMessage;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.ast.*;

/**
 * Al usar c.$('nombre'), valida que 'nombre' exista dentro
 * del formulario o sus adjuntos.
 * @author Rober
 */
public class FormElementReferenceValidator extends JSValidator {
    
    private static ThreadLocal<Set<String>> nombres = new ThreadLocal<Set<String>>();
    
    @Override
    public Collection<ValidationMessage> validate(WebPage webPage,
            WebPage webPageCompleto) {
        
        nombres.set(new HashSet<String>(50));
        
        for (FormElement element : IterableWebElement.get(webPageCompleto,
                FormElement.class)) {
            String nombre = element.getName();
            if (!StringUtils.isBlank(nombre)) {
                nombres.get().add(nombre);
            }
        }
        
        return super.validate(webPageCompleto, webPageCompleto);
    }
    
    @Override
    public boolean isFixable() {
        return false;
    }

    @Override
    protected FixValidateNodeVisitor getNodeVisitor(Scope root) {
        return new ReferenceNodeVisitor();
    }
    
    private static class ReferenceNodeVisitor extends FixValidateNodeVisitor {

        @Override
        public boolean visit(AstNode nodo) {
            if (nodo instanceof FunctionCall) {
                FunctionCall func = (FunctionCall) nodo;
                String target = func.getTarget().toSource();
                
                if (target.matches("^c\\.\\$[NV]?$") && 
                        func.getArguments().size() > 0) {
                    AstNode arg1 = func.getArguments().get(0);
                    
                    if (arg1 instanceof StringLiteral) {
                        String nombre = ((StringLiteral) arg1).getValue();
                        
                        if (validateOnly && !nombres.get().contains(nombre)) {
                            throw new ValidationException("Se está haciendo referencia "
                                    + "desde javascript a un elemento de nombre '"
                                    + nombre + "' que no existe en este formulario "
                                    + "o sus adjuntos. Por favor revise que el nombre "
                                    + "esté bien escrito.");
                        }
                    }
                }
            }
            
            return true;
        }
        
    }
    
}
