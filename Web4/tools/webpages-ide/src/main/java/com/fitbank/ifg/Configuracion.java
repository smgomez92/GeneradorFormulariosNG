package com.fitbank.ifg;

import com.fitbank.css.HojaDeEstilos;
import com.fitbank.css.ParserCSS;

/**
 * Clase Configuracion.
 * 
 * @author FitBank
 */
public final class Configuracion {

    private static HojaDeEstilos hojaDeEstilos;

    static {
        Configuracion.actualizarEstilos();
    }

    private Configuracion() {
    }

    public static HojaDeEstilos getHojaDeEstilos() {
        return Configuracion.hojaDeEstilos;
    }

    public static void actualizarEstilos() {
        Configuracion.hojaDeEstilos = ParserCSS.parse(Thread.currentThread()
                .getContextClassLoader().getResource(
                        "com/fitbank/web/css/estilo.css"));
    }

}
