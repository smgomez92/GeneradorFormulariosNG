package com.fitbank.serializador;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author FitBank
 * @version 2.0
 */
public abstract class Serializador<T> {

    /**
     * Serializa el objeto.
     * 
     * @param s
     *            Objeto serializable
     * 
     * @throws IOException
     *             En caso de error de entrada/salida
     */
    public abstract void serializar(T s, OutputStream os) throws IOException;

    public String serializar(T s) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            this.serializar(s, os);

            return os.toString("UTF-8");
        } catch (IOException e) {
            // No debería ocurrir
            throw new Error(e);
        }
    }

}
