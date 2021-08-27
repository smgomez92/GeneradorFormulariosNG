package com.fitbank.serializador.html;

import java.io.Serializable;

/**
 * Interfaz que debe ser implementada por objetos que pueden ser serializados a
 * html.
 * 
 * @author FitBank
 * @version 2.0
 */
public interface SerializableHtml extends Serializable {
    void generateHtml(ConstructorHtml html);
}
