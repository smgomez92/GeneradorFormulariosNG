include("lib.prototype");

include("fitbank.proc.clases");
include("fitbank.util");

/**
 * Namespace Enlace - Define funciones de conexión con el servidor.
 */
var Enlace = {

    /**
     * Usado para guardar los datos en caso de tener que hacer un submit con
     * archivos.
     */
    callbacks: {},

    /**
     * Funcion envia un submit al servidor.
     * 
     * @param contexto Contexto sobre el que se ejecuta las acciones
     * @param opciones Objeto que puede tener estas propiedades:
     *    - tipo: Tipo del pedido
     *    - paginacion: Indica si se debe cambiar de pagina (-1, 0, 1,
     *      null=volver a la página 1)
     *    - callback: Funcion a ser llamada despues de recibir respuesta
     * @param posSubir Usado internamente despues de un submit de archivos.
     *
     * @return true si no hubo problemas o false si hubo problemas
     */
    submit: function(contexto, opciones, posSubir) {
        opciones = Object.extend({
            tipo: null,
            paginacion: 0,
            callback: function() {},
            target: null,
            action: null,
            skipFiles: false,
            params: $H()
        }, opciones);

        var files = Form.getInputs(contexto.form, "file");

        if (!opciones.target && (opciones.tipo == GeneralRequestTypes.CONSULTA
            || posSubir || !files.length)) {
            if (Enlace.idProceso) {
                Estatus.finalizarProceso("OK", Enlace.idProceso);
            }
            contexto.idProceso = Estatus.iniciarProceso(
                    Mensajes["fitbank.enlace.PROCESANDO"]);

            var request = new Ajax.Request("proc/" + opciones.tipo, {
                parameters: $H({
                    _contexto: contexto.id,
                    _controlConFoco: contexto.formulario.controlConFoco,
                    _registroActivo: contexto.formulario.registroActivo,
                    _paginacion: opciones.paginacion,
                    _subs: contexto.subsystem || c.subsystem,
                    _tran: contexto.transaction || c.transaction,
                    _user: contexto.user || c.user,
                    _sessionId: contexto.sessionId || c.sessionId,
                    codigo_flujo: contexto.codigo_flujo,
                    codigo_instancia_flujo: contexto.codigo_instancia_flujo,
                    codigo_enlace: contexto.codigo_enlace
                }).merge(opciones.params).toQueryString() + "&" + Form.serialize(contexto.form),
                onSuccess: function(response) {
                    var error = response.responseJSON && response.responseJSON.codigo || "";
                    if (error != contexto.BPM_CODE && !Util.isError(error.toLowerCase())) {
                        error = "";
                    }

                    if (response.responseJSON && response.responseJSON.notifica) {
                        NotificacionesComentarios.consultar(contexto, opciones);
                    }

                    opciones.drawHtml = response.responseJSON && response.responseJSON.drawHtml || false;

                    contexto.loadValues(response, error, opciones);

                    if (!error) {
                        opciones.callback();
                    }
                },
                onFailure: contexto.onError.bind(contexto),
                onException: rethrow
            });
            
            Estatus.getProceso(contexto.idProceso).setRequest(request);
        } else {
            if (files.length && !opciones.skipFiles) {
                Enlace.idProceso = Estatus.iniciarProceso(
                        Mensajes["fitbank.enlace.CARGANDO_ARCHIVOS"]);
    
                Enlace.callbacks[Enlace.idProceso] = function() {
                    Enlace.submit(contexto, opciones, true);
                };
                opciones.action = "proc/subir";
            } else {
                Enlace.idProceso = null;
            }

            var params = $H({
                _contexto: contexto.id,
                _proceso: Enlace.idProceso,
                _subs: contexto.subsystem,
                _tran: contexto.transaction,
                _user: contexto.user,
                _sessionId: contexto.sessionId
            }).merge(opciones.params);

            var action = contexto.form.action;

            contexto.form.onsubmit = "return true;";
            contexto.form.action = opciones.action + "?" + params.toQueryString();
            contexto.form.target = opciones.target || "entorno-iframe-ajax";
            contexto.form.method = "POST";
            contexto.form.enctype = (files.length && !opciones.skipFiles) ? "multipart/form-data" : "";
            contexto.form.submit();
            contexto.form.action = action;
            contexto.form.onsubmit = "return false;";
        }
    }

};
