package com.fitbank.webpages.util.validators.js;

/**
 * Indica que el código no cumple con los requerimientos de validación. Esta
 * excepción debe ser lanzada por un validador solo si su NodeVisitor tiene
 * la propiedad validateOnly puesta en <code>true</code>.
 * @author Fitbank RB
 * @see FixValidateNodeVisitor
 */
public class ValidationException extends RuntimeException {

    public ValidationException() {
        super();
    }

    public ValidationException(String mensaje) {
        super(mensaje);
    }
}
