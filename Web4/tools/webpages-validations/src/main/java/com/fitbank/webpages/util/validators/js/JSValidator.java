package com.fitbank.webpages.util.validators.js;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.ast.Scope;

import com.fitbank.js.JSParser;
import com.fitbank.js.JavascriptFormater;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadJavascript.Tipo;
import com.fitbank.util.Debug;
import com.fitbank.util.Servicios;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebElement;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.ListOfValues;
import com.fitbank.webpages.behaviors.Link;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.ValidationMessage.Severity;
import com.fitbank.webpages.util.ValidationUtils;
import com.fitbank.webpages.util.Validator;
import com.fitbank.webpages.widgets.Input;

/**
 * Validador base para javascript
 *
 * @author Fitbank RB, CI
 */
public abstract class JSValidator extends Validator {

    public static final String CALCULOS = "CALCULOS";

    public static final String CALLBACK = "CALLBACK";

    public static final String INITIALJS = "INITIALJS";

    public static final String JAVASCRIPT = "JAVASCRIPT";

    public static final String PREQUERY = "PREQUERY";
    
    public static final String PRELINK = "PRELINK";
    
    public static final String POSLINK = "POSLINK";

    protected abstract FixValidateNodeVisitor getNodeVisitor(Scope root);

    protected String validationDescription = null;
    
    private Predicate tieneComportamientoLink = new Predicate() {

        @Override
        public boolean evaluate(Object object) {
            return object instanceof Link;
        }
    };

    /**
     * Devuelve <code>true</code> si el código contiene el error especificado
     * por este validador. El método usa un FixValidateNodeVisitor provisto
     * por las clases derivadas para parsear el código y buscar errores. Las
     * clases que usen otro método para detectar errores deben
     * sobrescribir este método.
     * @param js El código a analizar.
     * @param eventCode Indica si el código a validar pertenece a un evento.
     * @return <code>true</code> si el código no cumple con la validación
     * @see FixValidateNodeVisitor
     * @see ValidationException
     */
    protected boolean hasError(String js, boolean eventCode) {
        try {
            Scope root = JSParser.getRootNode(js);

            FixValidateNodeVisitor visitor = getNodeVisitor(root);
            visitor.setValidateOnly(true);
            visitor.setEventCode(eventCode);
            validationDescription = null;
            root.visit(visitor);
            return false;

        } catch (ValidationException ex) {
            validationDescription = ex.getMessage();
            return true;
        }
    }

    /**
     * Método que devuelve un mensaje opcional con los resultados de la
     * validación y posibles soluciones para el usuario. Éste mensaje se
     * obtiene de la excepción lanzada en el método hasError. Los validadores
     * que deseen incluir este tipo de mensajes, deben lanzar una excepción
     * ValidationException pasándole al constructor el mensaje que desean
     * mostrar.
     * @return La descripción que se mostrará al usuario en el IDE.
     */
    protected String getValidationDescription() {
        return validationDescription;
    }

    /**
     * Indica si el problema de este validador puede solucionarse automáticamente.
     * Las clases cuyos NodeVisitors no puedan corregir el error, deben
     * sobrescribir este método devolviendo <code>false</code>.
     * @return <code>true</code> si el problema puede ser solucionado por el
     * validador automáticamente, caso contrario devuelve <code>false</code>.
     */
    public boolean isFixable() {
        return true;
    }
    
    /**
     * Devuelve la severidad del error de validación. El valor por defecto es
     * Severity.ERROR. Los validadores que califiquen sus incidencias como
     * advertencias deben sobrescribir este método.
     * 
     * @see Severity
     */
    public Severity getSeverity() {
        return Severity.ERROR;
    }

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage, WebPage fullWebPage) {
        Collection<ValidationMessage> mensajes = super.validate(webPage, null);

        String calculos = webPage.getCalculos();
        String jsInic = webPage.getInitialJS();

        add(validate(calculos, webPage, webPage, CALCULOS, false), mensajes);
        add(validate(jsInic, webPage, webPage, INITIALJS, false), mensajes);

        return mensajes;
    }

    @Override
    public Collection<ValidationMessage> validate(Container container, WebPage fullWebPage) {
        Collection<ValidationMessage> mensajes = super.validate(container, null);

        add(validate(container.getJavaScript(), container, container,
                JAVASCRIPT, true), mensajes);

        return mensajes;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        Collection<ValidationMessage> mensajes =
                new LinkedList<ValidationMessage>();

        if (widget instanceof Input) {
            Input input = (Input) widget;
            validateEvents(input, widget, mensajes);

            if (input.getAssistant() instanceof ListOfValues) {
                ListOfValues ldv = (ListOfValues) input.getAssistant();
                validate(ldv, widget, mensajes);
            }
            
            Link link = (Link) CollectionUtils.find(
                    input.getBehaviors(), tieneComportamientoLink);
            if (link != null) {
                validate(link, widget, mensajes);
            }
        }

        return mensajes;
    }

    private void validate(ListOfValues ldv, Widget widget,
            Collection<ValidationMessage> mensajes) {
        if (ldv.getPreQuery() != null) {
            add(validate(ldv.getPreQuery().getValor(), widget, ldv,
                    PREQUERY, true), mensajes);
        }

        if (ldv.getCallback() != null) {
            add(validate(ldv.getCallback().getValor(), widget, ldv,
                    CALLBACK, true), mensajes);
        }
    }
    
    private void validate(Link link, Widget widget,
            Collection<ValidationMessage> mensajes) {
        add(validate(link.getPreLink().getValor(), widget, link, PRELINK, false), 
                mensajes);
        add(validate(link.getPosLink().getValor(), widget, link, POSLINK, false), 
                mensajes);
    }

    private ValidationMessage validate(String js, WebElement element,
            final Object validatedObject, String key, boolean eventCode) {

        String resourceKey = null;
        if (validatedObject instanceof Input) {
            resourceKey =
                    Servicios.toUnderscoreString(Input.class.getSimpleName())
                    + "_" + key;
        } else {
            resourceKey =
                    Servicios.toUnderscoreString(validatedObject.getClass().
                    getSimpleName()) + "_" + key;
        }

        try {
            if (!hasError(js, eventCode)) {
                return null;
            }
        } catch (EvaluatorException ex) {
            String mensaje = "Error de sintaxis en javascript: " + ex.getMessage() +"\n"
                    + "Línea-columna: " + ex.lineNumber() + "-" + ex.columnNumber()
                    +"\nCódigo con error: " + ex.lineSource() + "\n\n";

            Debug.error(mensaje);
            
            return new ValidationMessage(new SyntaxValidator(), resourceKey, mensaje, element, 
                    validatedObject, false) {
                        
                        @Override
                        public String toString() {
                            String prefix = ValidationUtils.DESCRIPTIONS.getString(
                                     getValidatorObject().getClass().getName() + ".MESSAGE");
                            String suffix = ValidationUtils.DESCRIPTIONS.getString(
                                    getCode());
                            return prefix + " " + suffix;
                        }
                    };
        }

        return new ValidationMessage(this, resourceKey, validationDescription,
                element, validatedObject, getSeverity(), isFixable()) {

            @Override
            public void fix() {
                if (validatedObject instanceof Input) {
                    Input input = (Input) validatedObject;

                    input.setJavaScript(fixEvents(input.getJavaScript()));

                } else if (validatedObject instanceof ListOfValues) {
                    ListOfValues ldv = (ListOfValues) validatedObject;

                    if (ldv.getPreQuery() != null) {
                        ldv.getPreQuery().setValor(fixJS(
                                ldv.getPreQuery().getValor(), true));
                    }

                    if (ldv.getCallback() != null) {
                        ldv.getCallback().setValor(fixJS(
                                ldv.getCallback().getValor(), true));
                    }

                } else if (validatedObject instanceof Container) {
                    Container container = (Container) validatedObject;

                    container.setJavaScript(fixEvents(container.getJavaScript()));

                } else if (validatedObject instanceof WebPage) {
                    WebPage webPage = (WebPage) validatedObject;

                    webPage.setCalculos(fixJS(webPage.getCalculos(), false));
                    webPage.setInitialJS(fixJS(webPage.getInitialJS(), false));
                }
            }
            
            @Override
            public String toString() {
                String prefix = ValidationUtils.DESCRIPTIONS.getString(
                        getValidatorObject().getClass().getName() + ".MESSAGE");
                String suffix = ValidationUtils.DESCRIPTIONS.getString(
                        getCode());
                return prefix + " " + suffix;
            }

        };
    }

    private void add(ValidationMessage validationMessage,
            Collection<ValidationMessage> mensajes) {
        if (validationMessage != null) {
            mensajes.add(validationMessage);
        }
    }

    protected String fixEvents(String js) {
        PropiedadJavascript jvs = new PropiedadJavascript(Tipo.EVENTOS);

        jvs.setValor(js);

        for (String evento : jvs.getEventos().keySet()) {
            jvs.getEventos().put(evento, JavascriptFormater.format(
                    fixJS(jvs.getEventos().get(evento), true), true));
        }

        return jvs.getValorString();
    }

    protected void validateEvents(Input input, Widget widget,
            Collection<ValidationMessage> mensajes) {

        PropiedadJavascript jvs = new PropiedadJavascript(Tipo.EVENTOS);
        jvs.setValor(input.getJavaScript());

        for (String evento : jvs.getEventos().keySet()) {
            add(validate(jvs.getEventos().get(evento), widget, widget,
                    JAVASCRIPT, true), mensajes);
        }
    }

    protected String fixJS(String js, boolean eventCode) throws EvaluatorException {
        if (!isFixable() || StringUtils.isBlank(js)) {
            return js;
        }

        try {
            Scope root = JSParser.getRootNode(js);

            FixValidateNodeVisitor visitor = getNodeVisitor(root);
            visitor.setValidateOnly(false);
            visitor.setEventCode(eventCode);
            root.visit(visitor);

            return JavascriptFormater.format(root.toSource());

        } catch (EvaluatorException ex) {
            Debug.error("Error al parsear js: " + js, ex);

            return js;
        }
    }
}