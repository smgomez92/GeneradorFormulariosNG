include("lib.prototype");

include("fitbank.ui.estatus");

/**
 * Namespace Validar - Define funciones para manejar las validaciones.
 */
var Validar = {

    /**
     * Marca con error un campo requerido.
     *
     * @param elemento Elemento que se va a marcar con error
     * @param e Mensaje o objeto de excepción
     */
    required: function(elemento, e) {
        Validar.error(elemento, e, 'required');
    },

    /**
     * Marca con error un campo.
     *
     * @param elemento Elemento que se va a marcar con error
     * @param e Mensaje o objeto de excepciÃ³n
     * @param id Id de validacion
     */
    error: function(elemento, e, id) {
        id = id || "default";

        if (Object.isArray(elemento)) {
            elemento.each(function(el) {
                Validar.error(el, e, id);
            });
            return;
        }

        elemento = Validar._filtrar(elemento);

        if (!elemento) {
            return;
        }

        Validar._error(elemento, e, id);
    },

    _error: function(elemento, e, id) {
        elemento.addClassName("error");
        elemento.addClassName("error-" + id);

        var mensaje = e && e.message || e;
        var stack = e && e.stack || new Error(mensaje).stack;

        elemento._validar.set(id, mensaje);

        Tooltip.mostrar(elemento);

        if (Estatus.tieneError() && !mensaje) {
            return;
        }

        mensaje = mensaje || Mensajes["fitbank.validar.ERROR"];

        Estatus.mensaje(mensaje, stack, "error", elemento);
    },

    /**
     * Desmarca un error de un campo si ya no hay mas errores.
     *
     * @param elemento Elemento que se quiere desmarcar;
     * @param id Id de validacion
     */
    ok: function(elemento, id) {
        id = id || "default";

        if (Object.isArray(elemento)) {
            elemento.each(function(el) {
                Validar.ok(el, id);
            });
            return;
        }

        elemento = Validar._filtrar(elemento);

        if (!elemento) {
            return;
        }

        elemento.removeClassName("error-" + id);
        elemento._validar.unset(id);

        if (!elemento._validar.size()) {
            elemento.removeClassName("error");
            Tooltip.quitar(elemento);
        }
        
        if (!elemento.hasClassName('error')) {
            Estatus.limpiar(elemento);
        }
    },

    disable: function(elemento, disabled) {
        elemento.setDisabled(disabled);
    },

    /**
     * Obtiene el mensaje de validacion del campo.
     */
    getMessage: function(elemento) {
        var mensaje = null;

        if (elemento._validar) {
            elemento._validar.values().each(function(m) {
                if (m && mensaje) {
                    mensaje += "<br/>" + m;
                } else if (m) {
                    mensaje = m;
                }
            });
        }

        return mensaje;
    },

    _filtrar: function(elemento) {
        if (!elemento || elemento.disabled || elemento.readOnly || !elemento.visible()) {
            return null;
        }

        if (!Object.isElement(elemento)) {
            elemento = c.$(elemento);
        }

        if (elemento.widget) {
            elemento = elemento.widget;
        }

        if (!elemento._validar) {
            elemento._validar = $H();
        }

        return elemento;
    }

};
