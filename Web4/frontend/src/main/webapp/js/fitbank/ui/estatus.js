include("lib.prototype");

/**
 * Namespace Estatus - Contiene funciones para manejar el estatus.
 */
var Estatus = {

    /**
     * Elemento sobre el que se construye el estatus.
     */
    _elemento: null,

    /**
     * Hash de procesos con su id y su estado.
     */
    _procesos: $H(),

    /**
     * Guarda los mensajes.
     */
    _logMensajes: $A(),

    /**
     * Elemento que es el dueño del mensaje.
     */
    _owner: null,
    
    /**
     * Guarda la información del proceso.
     */
    InfoProceso: Class.create({
        _mensaje: '',
        
        _callback: null,
        
        _request: null,
        
        initialize: function (mensaje, callback) {
            this._mensaje = mensaje || '';
            this._callback = callback;
        },
        
        setRequest: function(request) {
            this._request = request;
        },
        
        setCallback: function(callback) {
            this._callback = callback;
        },
        
        abortar: function() {
            if (this._request) {
                this._request.abort();
                
                if (this._callback) {
                    try {
                        this._callback();
                    } catch (e) {
                        Estatus.mensaje(e, e && e.stack, 'error');
                    }
                }
            }
        }
    }),

    init: function(elemento) {
        Estatus._elemento = $(elemento);

        Estatus._progreso = new Element("img", {
            src: "img/progreso2.gif"
        }).hide();
        Estatus._elemento.insert({
            after: Estatus._progreso
        });
        Estatus._progreso.on("click", function() {
            Estatus._procesos.keys().each(function(key) {
                Estatus.abortarProceso(key);
            });
        });

        Estatus._temporizador = new Element("input", {
            id: "estatus-temporizador",
            value: "00.000s",
            tabIndex: -1,
            disabled: true
        });
        Estatus._elemento.insert({
            before: Estatus._temporizador
        });

        Estatus._cuerpo = new Element("div");

        Estatus._debug = new Ventana({
            titulo: "Debug",
            contenido: Estatus._cuerpo,
            destruirAlCerrar: false,
            w: 640,
            h: 480
        });

        Estatus._elemento.on('dblclick', Estatus._debug.ver.bind(Estatus._debug));
    },

    /**
     * Muestra el estatus en la barra inferior de la aplicación.
     * 
     * @param mensajeUsuario
     *            Mensaje de usuario.
     * @param stack
     *            Stacktrace.
     * @param className
     *            Clase CSS.
     * @param owner
     *            Elemento que es dueño del mensaje (opcional).
     */
    mensaje: function(mensajeUsuario, stack, className, owner) {
        Estatus._elemento.className = className || "";
        Estatus._elemento.up().className = className || "";
        Estatus._owner = owner;

        if (stack) {
            Estatus.log(stack, className);
        } else {
            Estatus.log(mensajeUsuario, className);
        }

        if (mensajeUsuario) {
            Estatus._elemento.update(mensajeUsuario);
        }
    },

    /**
     * Aumenta un mensaje al cuerpo.
     */
    log: function(mensaje, className) {
        if (!mensaje) {
            return;
        }

        var item = new Element("div", {
            className: "estatus-log"
        });
        if (className) {
            item.addClassName(className);
        }
        Estatus._cuerpo.insert({
            top: item
        });

        var borrar = new Element("button").update("X");
        borrar.on("click", function() {
            item.remove();
        });
        item.insert(borrar);

        item.insert(new Element("span").update(new Date() + ": "));
        item.insert(new Element("pre").update(mensaje));

        while (Estatus._cuerpo.childNodes.length > 50) {
            $A(Estatus._cuerpo.childNodes).last().remove();
        }
    },

    /**
     * Limpia el estatus si el owner es null o si es el owner del mensaje.
     */
    limpiar: function(owner) {
        if (!owner || owner == Estatus._owner) {
            Estatus.mensaje("OK", null, "", null);
        }
    },

    /**
     * Inicia un proceso y presenta el mensaje de inicialización.
     * 
     * @param mensaje
     *            String con el mensaje
     * @param stopCallback función usada para cancelar el proceso.
     * 
     * @returns Id del proceso creado
     */
    iniciarProceso: function(mensaje, stopCallback) {
        var id = Math.random();
        Estatus._procesos.set(id, new this.InfoProceso(mensaje, stopCallback));

        Estatus._progreso.show();
        Estatus.mensaje(mensaje || "Procesando...", null, "processing");
        Entorno.cambiarCursor(true);
        if (!Estatus._temporizador.interval) {
            Estatus._temporizador.value = "";
            Estatus._temporizador.inicio = new Date();
            Estatus._temporizador.interval = setInterval(function() {
                Estatus._temporizador.value = ((new Date() - Estatus._temporizador.inicio) / 1000) + "s";
            }, 10);
        }

        return id;
    },

    /**
     * Termina un proceso y presenta el mensaje de finalización.
     * 
     * @param mensaje
     *            String con el mensaje a mostrar en el estatus si ya no hay
     *            más procesos activos.
     * @param id
     *            Id del proceso a finalizar.
     * @param claseCSS
     *            Clase que se aplicará al mensaje: "error", "warning" o nulo
     * @param stack
     *            Stacktrace
     */
    finalizarProceso: function(mensaje, id, claseCSS, stack) {
        Estatus._procesos.unset(id);
        
        if (Estatus.activo()) {
            Estatus._progreso.hide();
            Estatus.mensaje(mensaje || "Listo", stack, claseCSS);
            clearInterval(Estatus._temporizador.interval);
            Estatus._temporizador.interval = null;
            Entorno.cambiarCursor(false);
            if (Estatus._temporizador.value == ""
                && Estatus._temporizador.inicio) {
                Estatus._temporizador.value = ((new Date() - Estatus._temporizador.inicio) / 1000) + "s";
            }
            return true;
        } else {
            return false;
        }
    },
    
    /**
     * Cancela la ejecución de un proceso.
     */
    abortarProceso: function(id) {
        var proc = Estatus._procesos.get(id);
        
        if (proc) {
            proc.abortar();
            Estatus.finalizarProceso("Eliminado proceso '"
            + proc._mensaje + "'...", id);
        }
    },
    
    getProceso: function(id) {
        return Estatus._procesos.get(id);
    },

    /**
     * Registra un evento para ser enviado posteriormente a grabar.
     *
     * @param response Respuesta del servidor
     */
    registrar: function(response) {
        if (Parametros["fitbank.ui.estatus.REGISTER"] != "true") {
            return;
        }

        Estatus._logMensajes.push({
            messageId: response.responseJSON.messageId,
            realDate: new Date().getTime(),
            subsystem: c.subsystem,
            transaction: c.transaction,
            result: response.responseJSON.mensajeUsuario,
            code: response.responseJSON.codigo,
            time: Estatus._temporizador.value.replace(/[^0-9\.]/, ""),
            type: response.request.url
        });
    },

    /**
     * Envia a grabar los eventos registrados.
     */
    enviarLog: function(todo) {
        if (!todo && Estatus._logMensajes.size() < 10) {
            return;
        }

        var json = Object.toJSON(Estatus._logMensajes);
        Estatus._logMensajes.clear();

        new Ajax.Request("proc/" + GeneralRequestTypes.LOG_MENSAJES, {
            asynchronous: false,
            parameters: {
                logs: json
            }
        });
    },

    /**
     * Determina si hay procesos activos y está bloqueado el estatus.
     *
     * @deprecated Usar Entorno.bloqueado() que es más entendible
     */
    activo: function() {
        return !Estatus._procesos.keys().size();
    },

    /**
     * Determina si hay errores.
     */
    tieneError: function() {
        return Estatus._elemento.className != "";
    }

};
