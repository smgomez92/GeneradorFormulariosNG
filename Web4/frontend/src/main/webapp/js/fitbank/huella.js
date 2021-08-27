include('lib.prototype');

/**
 * Captura de huellas digitales para FingKey Hamster
 */
var Huella = {

    /**
     * Capturar la huella
     * 
     * @param id Identificación de la persona
     * @param callback Funcion a ser ejecutada cuando se haya capturado la huella
     * @param callbackError Funcion a ser ejecutada cuando se haya error
     */
    capturar: function(id, callback, callbackError) {
        var process = Estatus.iniciarProceso(Mensajes["fitbank.huella.CAPTURANDO"]);
        id = ("0".times(14 - id.length)) + id;
        var url = Parametros['fitbank.huella.FINGERPRINT_URL'].replace("{0}", id);
        var provider = Huella[Parametros['fitbank.huella.FINGERPRINT_PROVIDER']];

        var request = new Ajax.Request(url, {
            method: "get",
            onComplete: function(response) {
                var json = provider(response);
                var message = null;
                var stackTrace = null;

                if (json.error) {
                    message = callbackError && callbackError(json) || json.message;
                    stackTrace = json.stackTrace;
                } else {
                    message = callback && callback(json);
                }

                Estatus.finalizarProceso(message || Mensajes["fitbank.huella.CAPTURADA"],
                    process, message && "error", stackTrace);
            }
        });
        
        Estatus.getProceso(process).setRequest(request);
    },

    BIOMETRIKA: function(response) {
        var result = response.responseText;
        var code = parseInt(result.substr(0, 2));

        if (code != 0) {
            return {
                error: code,
                message: result.substring(3, result.length)
            }
        }

        return {
            result: result.substring(6, result.length),
            quality: result.substring(3, 6)
        }
    }

}
