include("lib.prototype");

/**
 * Namespace Logger - Define funciones para el logging de JS. Si el entorno no
 * está en modo GEN hace el log en el application server. Siempre se muestra en
 * la consola del navegador si existe o si no muestra un alert con la
 * severidad y el mensaje.
 */
var Logger = {

    LOG : 'LOG',

    INFO : 'INFO',

    DEBUG : 'DEBUG',

    WARNING : 'WARNING',

    ERROR : 'ERROR',

    /**
     * Función que procesa el log.
     *
     * @param mensaje El String con el mensaje.
     * @param severidad Puede tomar estos valores: Logger.LOG, Logger.INFO, Logger.DEBUG, 
     *        Logger.ERROR. Si no se pasa toma valor de Logger.LOG.
     */
    log : function(mensaje, severidad) {
        severidad = severidad ? severidad : Logger.LOG;

        if (typeof(console) != 'undefined') {
            switch (severidad) {
            case Logger.LOG:
                console.log(mensaje);
                break;

            case Logger.INFO:
                console.info(mensaje);
                break;

            case Logger.DEBUG:
                console.debug(mensaje);
                break;

            case Logger.WARNING:
                console.warn(mensaje);
                break;

            case Logger.ERROR:
                console.error(mensaje);
                break;
            }
        }

        new Ajax.Request('proc/log', {
            parameters : 'mensaje=' + mensaje + '&severidad='
                    + severidad,
            onComplete : Logger.resultado
        });
    },

    /**
     * Función que presenta un mensaje en el log con severidad Logger.INFO.
     *
     * @param mensaje El String con el mensaje.
     */
    info : function(mensaje) {
        Logger.log(mensaje, Logger.INFO);
    },

    /**
     * Función que presenta un mensaje en el log con severidad Logger.DEBUG.
     *
     * @param mensaje El String con el mensaje.
     */
    debug : function(mensaje) {
        Logger.log(mensaje, Logger.DEBUG);
    },

    /**
     * Función que presenta un mensaje en el log con severidad Logger.WARNING.
     *
     * @param mensaje El String con el mensaje.
     */
    warning : function(mensaje) {
        Logger.log(mensaje, Logger.WARNING);
    },

    /**
     * Función que presenta un mensaje en el log con severidad Logger.ERROR.
     *
     * @param mensaje El String con el mensaje.
     */
    error : function(mensaje) {
        Logger.log(mensaje, Logger.ERROR);
    },

    /**
     * Función que presenta un mensaje en el log junto con un trace.
     *
     * @param mensaje El String con el mensaje.
     */
    trace : function(mensaje) {
        Logger.log(mensaje, Logger.INFO);
        if (typeof(console) != 'undefined') {
            console.trace();
        } else {
            Logger.log(new Error().stack, Logger.INFO);
        }
    },

    /**
     * @private
     */
    resultado : function(respuesta) {
        // TODO: No hacer nada por el momento, revisar que se debería hacer
    }

};
