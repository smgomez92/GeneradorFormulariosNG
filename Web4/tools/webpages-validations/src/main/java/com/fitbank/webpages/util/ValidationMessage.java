package com.fitbank.webpages.util;

import com.fitbank.webpages.WebElement;

public class ValidationMessage {

    public enum Severity {

        WARN, ERROR;

    }

    private final Object validatorObject;

    private final String code;

    private final Object validatedObject;

    private final boolean fixable;

    private final Severity severity;

    private final WebElement<?> webElement;

    private final String description;

    public ValidationMessage(Object validatorObject, String code,
            WebElement<?> webElement, Object validatedObject,
            Severity severity) {
        this(validatorObject, code, "", webElement, validatedObject,
                severity, false);
    }

    public ValidationMessage(Object validatorObject, String code,
            String description, WebElement<?> webElement, Object validatedObject,
            Severity severity) {
        this(validatorObject, code, description, webElement, validatedObject,
                severity, false);
    }

    public ValidationMessage(Object validatorObject, String code,
            WebElement<?> webElement, Object validatedObject, boolean fixable) {
        this(validatorObject, code, "", webElement, validatedObject,
                fixable ? Severity.WARN : Severity.ERROR, fixable);
    }

    public ValidationMessage(Object validatorObject, String code,
            WebElement<?> webElement, Object validatedObject, Severity severity,
            boolean fixable) {
        this(validatorObject, code, "", webElement, validatedObject, severity,
                fixable);
    }

    public ValidationMessage(Object validatorObject, String code,
            String description, WebElement<?> webElement, Object validatedObject,
            boolean fixable) {
        this(validatorObject, code, description, webElement, validatedObject,
                fixable ? Severity.WARN : Severity.ERROR, fixable);
    }

    public ValidationMessage(Object validatorObject, String code,
            String description, WebElement<?> webElement, Object validatedObject,
            Severity severity, boolean fixable) {
        this.validatorObject = validatorObject;
        this.code = code;
        this.description = description;
        this.webElement = webElement;
        this.validatedObject = validatedObject;
        this.fixable = fixable;
        this.severity = severity;
    }

    public Object getValidatorObject() {
        return validatorObject;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public Object getValidatedObject() {
        return validatedObject;
    }

    public boolean isFixable() {
        return fixable;
    }

    public WebElement<?> getWebElement() {
        return webElement;
    }

    public Severity getSeverity() {
        return severity;
    }

    /**
     * Método a ser sobreescrito para proveer un metodo de arreglar un problema.
     */
    public void fix() {
        // Por default no se hace nada.
    }

    @Override
    public String toString() {
        try {
            return ValidationUtils.DESCRIPTIONS.getString(validatorObject.
                    getClass().getName() + "." + getCode());
        } catch (Exception e) {
            return validatorObject.getClass().getName() + "." + getCode();
        }
    }

}
