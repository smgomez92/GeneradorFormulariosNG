package com.fitbank.serializador.xml;

/**
 * Excepcion lanzada en caso de que ocurra una caida en el parser.
 * 
 * @author FitBank
 * @version 2.0
 */
public class ExcepcionParser extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor con mensaje.
     * 
     * @param mensaje
     *            Mensaje de error
     */
    public ExcepcionParser(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa.
     * 
     * @param mensaje
     *            Mensaje de error
     * @param cause
     *            Causa
     */
    public ExcepcionParser(String mensaje, Throwable cause) {
        super(mensaje, cause);
    }

    /**
     * Constructor con causa.
     * 
     * @param cause
     *            Causa
     */
    public ExcepcionParser(Throwable cause) {
        super(cause);
    }
}
