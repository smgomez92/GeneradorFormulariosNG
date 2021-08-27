include("lib.prototype");
include("lib.tristate");

include("fitbank.proc.clases");
include("fitbank.formulario");
include("fitbank.util");
include("fitbank.logger");

include("fitbank.ui.estatus");
include("fitbank.ui.tabs");
include("fitbank.ui.teclas");
include("fitbank.ui.notificaciones");

/**
 * Class Contexto - Contiene información sobre el contexto en que se ejecuta un
 * formulario.
 */
var Contexto = Class.create( {

    /**
     * Espacio horizontal que se debe guardar para poner barras de scroll
     */
    WIDTH_SPACE: 20,

    /**
     * Espacio vertical que se debe guardar para poner barras de scroll
     * (contando tambien barras de herramientas, de teclas, etc).
     */
    HEIGHT_SPACE: 200,

    /**
     * Espacio vertical que se debe guardar para poner barras de scroll
     * (contando tambien barras de herramientas, de teclas, etc).
     */
    HEIGHT_SPACE_NORMAL: 200,

    /**
     * Espacio vertical que se debe guardar para poner barras de scroll
     * (contando tambien barras de herramientas, etc) menos las teclas.
     * Para mostrar aplicaciones remotas unicamente
     */
    HEIGHT_SPACE_NO_KEYS: 80,

    /**
     * Codigo general que lanza el bpm cuando se inicia un flujo de
     * autorizacion
     */
    BPM_CODE: "OK-BPM035",

    /**
     * Id del contexto.
     */
    id: null,

    /**
     * Formulario.
     */
    formulario: null,

    /**
     * Elemento FORM que contiene los campos.
     */
    form: null,

    /**
     * Elemento donde se carga el contexto.
     */
    elemento: null,

    /**
     * Lista Valores actualmente visibles.
     */
    listaValores: null,

    /**
     * Es un contexto secundario.
     */
    secundario: false,

    /**
     * Subcontextos.
     */
    subContextos: $A(),

    /**
     * Si el contexto es remoto, contiene el iframe del formulario.
     */
    remoto: null,

    /**
     * Variables del contexto.
     */
    vars: {},

    /**
     * Elementos en su estado original (al cargar o despues de una peticion).
     */
    originalElements: {},

    /**
     * Guarda la navegación para cuando se utiliza ReturnLink.
     */
    navegacion: $A(),

    //Constructor, llamado automáticamente por Prototype.
    initialize: function(elemento, secundario) {
        this.elemento = $(elemento);
        this.secundario = secundario;
        this.reset();
    },

    reset: function(keepContent) {
        this.id = Math.round(Math.random() * 10000);
        this.formulario = new Formulario();
        this.subContextos = $A();
        this.consultar = this._consultar;
        this.mantener = this._mantener;
        this.limpiar = this._limpiar;
        this.ayudar = this._ayudar;
        this.menuRapido = this._menuRapido;
        this.recargar = this._recargar;
        this.calcular = this._calcular;
        this.manualTecnico = this._manualTecnico;
        if (!keepContent) {
            this.elemento.update("");
        }
        this.elemento.removeClassName("remote");
        this.remoto = null;
        ListaFormularios.cargar();
        this.WIDTH_SPACE = 20;
        this.HEIGHT_SPACE = this.HEIGHT_SPACE_NORMAL;
    },

    resize: function(contenedor) {
        var resizeElement = document.onresize ? document : window;

        if (!contenedor.visible()) {
            contenedor.resize.stop();
            return;
        }

        contenedor.setStyle({
            maxWidth: ($(document.body).getWidth() - this.WIDTH_SPACE) + "px",
            maxHeight: ($(document.body).getHeight() - this.HEIGHT_SPACE) + "px"
        });

        if (!contenedor.resize) {
            contenedor.resize = Event.on(resizeElement, "resize",
                this.resize.bind(this, contenedor));
        }
    },

    /**
     * Revisa si existen cambios pendientes en el formulario y pregunta si se
     * quiere continuar. Devuelve true si se cancela la acción. False en caso
     * de que no existan cambios.
     */
    revisarCambios: function() {
        return Parametros['fitbank.revisarCambios.ENABLED'] == "true"
                && Util.checkForChanges(this.originalElements) 
                && !confirm(Mensajes["fitbank.contexto.CAMBIOS"]);
    },

    /**
     * Devuelve las opciones indicadas con los defaults para los campos no
     * especificados.
     *
     * @private
     */
    _default: function(opciones) {
        return Object.extend({
            subsistema: null,
            transaccion: null,
            campos: null,
            nameMap: null,
            objetoJS: null,
            action: null,
            fields: null,
            consulta: true,
            reset: true,
            registro: 0
        }, opciones);
    },

    /**
     * Carga un formulario. Las opciones que acepta son las siguientes:
     *
     * <dl>
     * <dt>subsistema</dt><dd>(String) Subsistema</dd>
     * <dt>transaccion</dt><dd>(String) Transaccion</dd>
     * <dt>campos</dt><dd>(Objeto) Indica los campos que se van a cargar en el prÃ³ximo
     *              formulario, en la forma { "nameOtroFormulario": valor, ... }</dd>
     * <dt>nameMap</dt><dd>(Objeto) Es el mapa de nombres de campos en la forma
     *              { "nameFormularioDestino": "nameFormularioActual", ... }
     *              para poder tener links de regreso</dd>
     * <dt>objetoJS</dt><dd>En Caso de que el link quiera mandar un objeto JS</dd>
     * <dt>action</dt><dd>(String) AcciÃ³n extra a ser ejecutada: "NEXT", "PREV", "END"</dd>
     * <dt>fields</dt><dd>(Objeto) Es el mapa de nombres de campos en la forma
     *              { "nameFormularioActual": "id", ... }</dd>
     * <dt>consulta</dt><dd>(boolean) Indica si se consulta despues de cargar el
     *              formulario, solo sirve si se paso la opcion campos</dd>
     * <dt>registro</dt><dd>(int) Registro destino de la carga de campos</dd>
     * </dl>
     *
     * @param opciones (Object) objeto con las opciones
     */
    cargar: function(opciones) {
        if (this._revisarBloqueo() || this.revisarCambios()) {
            return;
        }

        // Soportar llamada con muchos parámetros
        if (Object.isString(opciones)) {
            Logger.warning("Se está llamando a Contexto#cargar incorrectamente");

            var n = Object.isString(arguments[1]) ? 2 : 1;

            opciones = {
                subsistema: arguments[0],
                transaccion: arguments[1],
                campos: arguments[n++],
                nameMap: arguments[n++],
                objetoJS: arguments[n++]
            }

            if (n == 1) {
                opciones.st = opciones.subsistema;
            }
        }

        if (opciones.st) {
            opciones.st = opciones.st.replace(/[^\d]/g, "");

            if (opciones.st.length <= 2) {
                Estatus.mensaje("Ingrese por lo menos 3 digitos.", null, "error");
                Form.Element.activate("entorno-pt");
                return;
            }

            opciones.subsistema = opciones.st.substring(0, 2);
            opciones.transaccion = opciones.st.substring(2, 6);
            opciones.st = null;
        }

        opciones = this._default(opciones);

        while (opciones.transaccion.length < 4) {
            opciones.transaccion = 0 + opciones.transaccion;
        }

        // Información necesaria para los links de regreso
        if (opciones.nameMap) {
            this.navegacion.push({
                subsistema: this.formulario.subsistema,
                transaccion: this.formulario.transaccion,
                nameMap: opciones.nameMap,
                objetoJS: opciones.objetoJS
            });
        } else if (typeof opciones.nameMap == "undefined") {
            this.navegacion.clear();
        }

        if (!this.secundario) {
            $("entorno-pt").value = opciones.subsistema + "-" + opciones.transaccion;
        }

        this._obtener(opciones);
    },

    _revisarBloqueo: function() {
        if (Entorno.bloqueado()) {
            Estatus.mensaje("Por favor espere a que el proceso actual termine.",
                null, "warning");
            return true;
        } else {
            return false;
        }
    },

    /**
     * @private
     */
    _obtener: function(opciones) {
        if (opciones.reset) {
            this.reset(true);
        }

        this.idProceso = Estatus.iniciarProceso("Cargando formulario...");

        var request = new Ajax.Request('proc/' + (opciones.tipo || GeneralRequestTypes.FORM), {
            parameters: {
                _contexto: this.id,
                _subs: opciones.subsistema,
                _tran: opciones.transaccion,
                _values: Object.toJSON(opciones.values),
                _fields: Object.toJSON(opciones.fields),
                _action: opciones.action,
                _user: this.user,
                _sessionId: this.sessionId
            },
            onSuccess: this.mostrarForm.bind(this, opciones),
            onFailure: this.onError.bind(this),
            onException: rethrow
        });

        Estatus.getProceso(this.idProceso).setRequest(request);
    },

    /**
     * @private
     */
    mostrarForm: function(opciones, respuesta) {
        var json = respuesta.responseJSON;
        if (!json) {
            Estatus.finalizarProceso(Mensajes["fitbank.contexto.NO_DISPONIBLE"],
                this.idProceso, "warning");

            return;
        }

        opciones = this._default(opciones);

        // Cargar variables del contexto que vienen del transporte db
        $H(json && json.db).keys().each(function(key) {
            this[key] = json.db[key];
        }, this);
        this.accountingDate = new Date(this.accountingDate);

        var original = this.formulario;
        this.formulario = new Formulario(Object.extend({
            subsistema: this.subsystem,
            transaccion: this.transaction
        }, json));

        // Copiar estados originales
        if (!opciones.reset) {
            this.formulario.consultado = original.consultado;
        }

        ListaFormularios.cargar(json.navigation);

        var contenedor;
        if (this.secundario) {
            contenedor = this.elemento;
        } else {
            contenedor = new Element("div", {
                className: "entorno-html"
            });
            this.resize(contenedor);

            this.elemento.update(this._crearTitulo(json));
            document.title = json.titulo + " - FitBank";
        }

        contenedor.insert(json.html);

		//Limpiar datepickers del formulario anterior
        datePickerController && datePickerController.cleanUp();
        //En este punto ya está disponible la información de idioma.
        //Configurar a todos los datepickers con el idioma del formulario.
        datePickerController && datePickerController.setGlobalOptions({
            lang: (this.language || "es").toLowerCase()
        });

        if (!this.secundario) {
            this.elemento.insert(contenedor);
        }

        this._initFormulario.bind(this).defer(respuesta, opciones);
    },

    /**
     * @private
     */
    _crearTitulo: function(json) {
        var div = new Element("div", {
            className: "entorno-formulario-titulo"
        });

        div.update(json.titulo);

        return div;
    },

    /**
     * Inicializa un formulario remoto
     *
     * @param src dirección de la pÃ¡gina a la que se hace el post
     * @param id Id del elemento
     * @param consultar si es true hace una consulta previa.
     * @param post usar POST o GET.
     * @param hideKeys Muestra o no las teclas de F1...F12
     * @param initRemoto Especifica si se debe iniciar un proceso y mostrar la
     *                   animación en la barra de estado.
     * @param expand Indica si se va a expandir el iFrame en todo el entorno
     */
    initRemoto: function(src, id, consultar, post, hideKeys, initRemoto, expand) {
        this.remoto = $(id);

        this.remoto.on("load", c._registrarRemoto.bind(c));

        var callback = (function() {
            var params = $H({
                _subs: this.formulario.subsistema,
                _tran: this.formulario.transaccion,
                _contexto: this.id
            });

            if (initRemoto) {
                this.idInicial = Estatus.iniciarProceso("Cargando formulario externo");
            } else {
                Estatus.mensaje("Formulario externo activo", null, false);
            }

            var files = Form.getInputs(this.form, "file");
            var action = this.form.action;
            var separador = src.indexOf("?") > 0 ? "&" : "?";

            this.form.onsubmit = "return true;";
            this.form.method = post ? "POST" : "GET";
            this.form.enctype = post && files.length ? "multipart/form-data" : "";
            this.form.action = src + separador + params.toQueryString();
            this.form.target = id;
            this.form.submit();
            this.form.action = action;
            this.form.onsubmit = "return false;";
        }).bind(this);

        if (consultar) {
            this.consultar.bind(this).defer(null, Function.defer.bind(callback));
        } else {
            callback.defer();
        }

        if (hideKeys) {
            Teclas.esconder("entorno-teclas");
            this.HEIGHT_SPACE = this.HEIGHT_SPACE_NO_KEYS;
        }

        if (expand) {
            var interval = setInterval(function() {
                if (!c.remoto) {
                    clearInterval(interval);
                    return;
                }
                c.remoto.setStyle({
                    width: ($(document.body).getWidth() - c.WIDTH_SPACE - 10) + "px",
                    height: ($(document.body).getHeight() - c.HEIGHT_SPACE - 10) + "px"
                });
            }, 1000);

            this.resize(this.form.up());
        }
    },

    /**
     * Registra un formulario remoto.
     *
     * @private
     */
    _registrarRemoto: function() {
        this.elemento.addClassName("remote");
        var cw = Util.getContentWindow(this.remoto);

        this.consultar = cw.consultar;
        this.mantener = cw.mantener;
        this.limpiar = cw.limpiar;
        this.recargar = cw.recargar;
        this.ayudar = cw.ayudar;
        this.menuRapido = cw.menuRapido;
        this.calcular = cw.calcular;
        this.manualTecnico = cw.manualTecnico;

        var proceso = c.idInicial;
        cw.addStatusListener && cw.addStatusListener(function(mensaje, iniciar, cod) {
            if (iniciar) {
                proceso = Estatus.iniciarProceso(mensaje);
            } else {
                Estatus.finalizarProceso(mensaje, proceso, cod != 0 ? "error" : "");
            }
        });
        cw.addTransactionListener && cw.addTransactionListener(function(st, t) {
            st = Object.isString(t) ? st + ":" + t : st;
            st = st.replace(/[^\d]/g, "");
            var subs = st.substring(0, 2);
            var tran = st.substring(2, 6);
            $("entorno-pt").value = subs + "-" + tran;
        });
    },

    /**
     * Inicializa el formulario
     *
     * @private
     */
    _initFormulario: function(respuesta, opciones) {
        var json = respuesta.responseJSON;

        this.form = this.elemento.select("form")[0];
        Tabs.reset(opciones === true || opciones && opciones.mantenerTab);

        this.$$("input, select, textarea, button").each(function(elemento) {
            elemento.on("focus", (function(e) {
                elemento.focused = true;
                this.formulario.controlConFoco = elemento.name;
                this.formulario.registroActivo = elemento.registro;
            }).bind(this));

            elemento.on("blur", (function(e) {
                elemento.focused = false;
            }).bind(this));

            elemento.focused = false;

            // Para llamar formateadores con valores completos
            var change = (function(e) {
                e.options = e.options || {};

                if (e.options.load) {
                    return;
                }

                // Solo llamar una vez este onchange por evento
                if (elemento._disparando) {
                    return;
                }

                elemento._disparando = true;

                (function() {
                    elemento._disparando = false;
                    elemento._processValue(e.options);
                    this.calcular();
                }).bind(this).defer();
            }).bind(this);

            elemento.on("blur", change);
            elemento.on("change", change);

            // Para llamar formateadores con valores parciales y asegurarse que
            // se dispare onchange si se cambio el valor. Bug de Chrome: 92492
            elemento.on("keyup", (function(e) {
                if (elemento._processValue({
                    partial: true
                }) && !elemento.onblurchange) {
                    elemento.onblurchange = elemento.on("blur", function() {
                        elemento.fireDOMEvent("change", {
                            generated: false
                        });

                        elemento.onblurchange.stop();
                        elemento.onblurchange = null;
                    });
                }
            }).bind(this));

            if (elemento.type == "checkbox") {
                Util.initCheckBox(elemento);
            }

            if (elemento.tagName.toLowerCase() == "select") {
                Util.initComboBox(elemento);
            }

            elemento.originalTabIndex = elemento.getAttribute("tabindex-original");
        }, this);

        this.$$("*[registro]").each(function(elemento) {
            elemento.registro = parseInt(elemento.getAttribute("registro")) || 0;
        });

        $H(json.values).each(function(pair) {
            var name = pair.key;
            var obj = pair.value;

            this.$N(name).each(function(element, n) {
                if (element.widget) {
                    element = element.widget;
                }
                element.setDisabled(obj.disabled[n]);
            }, this);
        }, this);

        Teclas.mostrar("entorno-teclas");

        this.formulario.jsInicial();
        this.loadValues(respuesta);
        this.formulario.evalFormulas();
        this.calcular();

        (function () {
            this.formulario.jsInicialWebPage();

            var firstFocus = this.$N(this.formulario.firstFocus, 0);
            if (firstFocus) {
                Form.Element.activate(firstFocus);
            }

            if (opciones.campos) {
                opciones.campos = $H(opciones.campos);
                opciones.campos.keys().each(function(campo) {
                    c.$N(campo, opciones.registro) && c.$N(campo, opciones.registro).changeValue(opciones.campos.get(campo));
                }, this);

                if (opciones.consulta) {
                    this.consultar.bind(this).defer();
                }
            }

            if (opciones.posLink) {
                this._checkError(opciones.posLink.curry(opciones), Mensajes['fitbank.contexto.ERROR_POSLINK']);
            }

            //Registrar los campos y sus valores originales en la carga del formulario
            this.originalElements = Util.getOriginalElements();
        }).bind(this).defer();

        //Ocultar elementos mediante un estilo
        this.$$(".oculto").invoke("hide");

        if (Parametros['fitbank.notificaciones.PUSH_ON_FORM_LOAD'] === "true") {
            //Enviar peticion de consulta de notificaciones en cada carga de formularios
            if (Parametros['fitbank.notificaciones.ENABLED'] === "true") {
                Notificaciones.consultar();
            }

            //Enviar peticion de consulta de notificaciones en cada carga de formularios
            if (Parametros['fitbank.notificacionescomentarios.ENABLED'] === "true") {
                NotificacionesComentarios.notificar();
            }
        }
    },

    _checkError: function(funcion, mensaje) {
        var res;
        var stacktrace = null;

        if (!funcion) {
            return false;
        }

        try {
            res = funcion();
        } catch(e) {
            res = false;
            stacktrace = e && e.stack;
        }

        if (res === false) {
            Estatus.mensaje(mensaje, stacktrace, "error");
            return true;
        } else if (res && res.length > 1) {
            Estatus.mensaje(res, stacktrace, "error");
            return true;
        }

        return false;
    },

    /**
     * Revisa que no existan campos sin validarse.
     **/
    _validar: function() {
        var errorElements = [];
        this.$$('tr.delete-record').each(function(row) {
            row.getElementsBySelector('.error').each(function(element) {
                element.removeClassName('error');
                errorElements.push(element);
            });
        });
        this.$$(".error-required").each(function(e) {
            Validar.ok(e, "required");
        }, this);
        var errors = this.$$(".error");
        if (errors && errors.length) {
            Estatus.mensaje(Validar.getMessage(errors[0])
                || Mensajes["fitbank.contexto.ERROR_VALIDACION"], null, "error");
            errorElements.each(function(element) {
                element && element.addClassName('error');
            });
            return true;
        }

        errorElements.each(function(element) {
            element && element.addClassName('error');
        });

        return false;
    },

    /**
     * Quita el foco del elemento y lo regresa al terminar de procesar el thread.
     */
    refrescarFoco: function() {
        var controlConFoco = this.formulario.controlConFoco;
        var registroActivo = this.formulario.registroActivo;
        var elemento = this.$(controlConFoco, registroActivo);

        if (elemento && elemento.blur) {
            elemento.blur();
            Form.Element.activate.defer(elemento);
        }
    },

    /**
     * Obtiene una lista de contextos y subcontextos en orden.
     */
    _getContextos: function(behaviorType) {
        var contextos = [];

        this.subContextos.each(function(contexto) {
            if (contexto.adjunto[behaviorType] == AttachedBehavior.BEFORE) {
                contextos.push(contexto);
            }
        });

        contextos.push(this);

        this.subContextos.each(function(contexto) {
            if (contexto.adjunto[behaviorType] == AttachedBehavior.AFTER) {
                contextos.push(contexto);
            }
        });

        contextos.reverse();

        return contextos;
    },

    /**
     * Hace una consulta con el formulario del contexto actual.
     */
    _consultar: function(paginacion, callback) {
        if (paginacion && this.formulario.paginacion != Paginacion.HABILITADA) {
            Estatus.mensaje(Mensajes["fitbank.contexto.PAGINACION_NO_HABILITADA"], null, "error");
            return;
        }

        if (this._revisarBloqueo() || this._checkError(this.formulario.preConsultar,
            Mensajes["fitbank.contexto.ERROR_PRE_CONSULTA"]) || this._validar()) {
            return;
        }

        this.formulario.consultando = true;

        var contextos = this._getContextos("queryBehavior");

        var submit = (function() {
            if (!contextos.length) {
                this.formulario.consultando = false;

                //Registrar los campos y sus valores luego de la consulta
		this.originalElements = Util.getOriginalElements();

                if (this._checkError(this.formulario.posConsultar,
                    Mensajes["fitbank.contexto.ERROR_POS_CONSULTA"])) {
                    return;
                }

                this.formulario.consultado = true;
                this.formulario.evalFormulasWithElements();
                callback && callback();
                this.calcular();

                var queryFocus = this.$N(this.formulario.queryFocus, 0);

                if (queryFocus) {
                    queryFocus.activate();
                }
            } else {
                Enlace.submit(contextos.pop(), {
                    tipo: GeneralRequestTypes.CONSULTA,
                    paginacion: paginacion,
                    callback: submit
                });
            }
        }).bind(this);

        submit();
    },

    /**
     * Hace un mantenimiento con el formulario del contexto actual.
     */
    _mantener: function(callback) {
        if (this.formulario.requiresQuery && !this.formulario.consultado) {
            Estatus.mensaje(Mensajes["fitbank.contexto.ERROR_REQUIERE_CONSULTA"],
                null, "error");
            return;
        }

        if (!this.formulario.store) {
            Estatus.mensaje(Mensajes["fitbank.contexto.ERROR_NO_APLICA_MANTENIMIENTO"],
                null, "error");
            return;
        }

        if (this._revisarBloqueo() || this._checkError(this.formulario.preMantener,
            Mensajes["fitbank.contexto.ERROR_PRE_MANTENIMIENTO"]) || this._validar()) {
            return;
        }

        this.formulario.manteniendo = true;

        var contextos = this._getContextos("storeBehavior");

        var submit = (function() {
            if (!contextos.length) {
                this.formulario.manteniendo = false;

                //Registrar los campos y sus valores luego del mantenimiento
		this.originalElements = Util.getOriginalElements();

                if (this._checkError(this.formulario.posMantener,
                    Mensajes["fitbank.contexto.ERROR_POS_MANTENIMIENTO"])) {
                    return;
                }

                this.formulario.evalFormulasWithElements();
                callback && callback();
                this.calcular();

                if (this.formulario.postQuery) {
                    this.consultar.bind(this).defer(0);
                }
            } else {
                Enlace.submit(contextos.pop(), {
                    tipo: GeneralRequestTypes.MANTENIMIENTO,
                    callback: submit
                });
            }
        }).bind(this);

        submit();
    },

    /**
     * Limpia el formulario en el contexto actual
     */
    _limpiar: function() {
        if (c.formulario.clean) {
            if (!this._revisarBloqueo()) {
                this._obtener(this._default({
                    tipo: GeneralRequestTypes.LIMPIAR,
                    subsistema: c.subsystem,
                    transaccion: c.transaction,
                    reset: false
                }));
            }
        } else {
            Estatus.mensaje(Mensajes["fitbank.contexto.ERROR_NO_APLICA_LIMPIEZA"], null, "error");
        }
    },

    /**
     * Recarga el formulario
     */
    _recargar: function() {
        var requestConfirm = Parametros['fitbank.recargar.confirmar.ENABLED'] == "true";
        var recargar = (function () {
            if (!this._revisarBloqueo()) {
                this._obtener({
                    subsistema: this.subsystem,
                    transaccion: this.transaction,
                    tipo: GeneralRequestTypes.RECARGAR
                });

                this.formulario.recargarVentana && this.formulario.recargarVentana.cerrar();
            }
        }).bind(this);

        //Verificar si est� habilitada la confirmaci�n de recarga de formularios
        if (requestConfirm) {
            //Si la ventana ya est� abierta, no volver a mostrarla
            if (this.formulario.recargarVentana && this.formulario.recargarVentana.visible) {
                return;
            }

            var div = new Element("div");
            var ventana = new Ventana({
                titulo: "Recargar formulario",
                contenido: div
            });

            var table = new Element("table");
            var messageTr = new Element("tr");
            var messageTd = new Element("td", {
                colspan: "2"
            });
            var message = new Element("span")
                    .update("Est&aacute; seguro/a que desea recargar el formulario?\n\
                <br>Se perder&aacute;n todos los cambios no guardados");
            messageTd.insert(message);
            messageTr.insert(messageTd);
            table.insert(messageTr);

            var buttonsTr = new Element("tr");
            var bOkTd = new Element("td");
            bOkTd.setStyle({
                textAlign: "center"
            });

            var bOk = new Element("button", {
                'class': "button"
            }).update("Ok");

            bOk.on("click", (function () {
                if (this.formulario.recargarVentana) {
                    this.formulario.recargarVentana.closing = true;
                }

                recargar();
            }).bind(this));
            bOk.on("keyup", (function(e) {
                if (e.keyCode == 27) {
                    this.formulario.recargarVentana &&
                    this.formulario.recargarVentana.cerrar();
                }
            }).bind(this));
            bOkTd.insert(bOk);

            var bCancelarTd = new Element("td");
            bCancelarTd.setStyle({
                textAlign: "center"
            });

            var bCancelar = new Element("button", {
                'class': "button"
            }).update("Cancelar");

            bCancelar.on("click", function () {
                ventana.cerrar();
            });

            bCancelarTd.insert(bCancelar);
            buttonsTr.insert(bOkTd);
            buttonsTr.insert(bCancelarTd);
            table.insert(buttonsTr);
            div.insert(table);

            ventana.ver();
            this.formulario.recargarVentana = ventana;
            bOk.activate();
        } else {
            recargar();
        }
    },

    confirmandoRecargar: function() {
        return this.formulario 
                && this.formulario.recargarVentana 
                && this.formulario.recargarVentana.visible 
                && !this.formulario.recargarVentana.closing;
    },

    /**
     * Llama al proceso BorrarCache, que recarga el formulario actual sin
     * traerlo desde la cache de formularios.
     */
    borrarCache: function() {
        if (this.revisarCambios() || this._revisarBloqueo() || !this.subsystem
            || !this.form) {
            return;
        }

        this._obtener({
            subsistema: this.subsystem,
            transaccion: this.transaction,
            tipo: GeneralRequestTypes.BORRAR_CACHE
        });
    },

    /**
     * Carga los values del response simplemente.
     *
     * @private
     */
    loadValues: function(response, error, opciones) {
        if (!response.responseJSON) {
            c.codigo_flujo = "";
            c.codigo_instancia_flujo = "";
            c.codigo_enlace = "";
            Estatus.finalizarProceso(Mensajes["fitbank.contexto.NO_DISPONIBLE"],
                this.idProceso, "warning");
            return;
        }

        this.cargando = true;

        $H(response.responseJSON.values).each((function(pair) {
            var name = pair.key;
            var obj = pair.value;

            this.$N(name).each(function(element, n) {
                element.changeValue(obj.values[n], {
                    load: true
                });

                if (element.widget) {
                    element = element.widget;
                }

                !error && element.setDisabled(obj.disabled[n]);

                if (obj.error[n] && obj.error[n].id) {
                    Validar.error(element, obj.error[n].mensaje, obj.error[n].id);

                    if (obj.error[n].id == "required") {
                        Logger.debug("Requerido: " + element.name);
                    }
                }

                if (obj.classNames && obj.classNames[n]) {
                    obj.classNames[n].split(" ").each(function(className) {
                        element.addClassName(className);
                    });
                }

                element.fire("widget:init");
            }, this);
        }).bindTryCatch(this));
        
        c.codigo_flujo = response.responseJSON.codigo_flujo;
        c.codigo_instancia_flujo = response.responseJSON.codigo_instancia_flujo;
        c.codigo_enlace = response.responseJSON.codigo_enlace;


        if (opciones && opciones.drawHtml) {
            this.mostrarForm(opciones, response);
        }

        this.cargando = false;
        if (response.responseJSON && response.responseJSON.codigo
            && this.BPM_CODE == response.responseJSON.codigo) {
            error = "error";
        }
        this._ready(response, error);
    },

    /**
     * Maneja mensajes de error.
     */
    onError: function(response) {
        var json = response.responseJSON;

        if (!json) {
            Estatus.finalizarProceso(Mensajes["fitbank.contexto.NO_DISPONIBLE"],
                this.idProceso, "warning");
        } else {
            this.loadValues(response, "error");
        }
    },

    /**
     * Función llamada al terminar de cargar un formulario.
     *
     * @param response La respuesta
     * @param error Boolean que indica si hubo error
     */
    _ready: function(response, error) {
        var json = response.responseJSON;
        if (!json) {
            Estatus.finalizarProceso(Mensajes["fitbank.contexto.NO_DISPONIBLE"],
                this.idProceso, "warning");
            return;
        }

        if (error) {
            Estatus.finalizarProceso(json.mensajeUsuario
                || Mensajes["fitbank.contexto.ERROR_CARGANDO"],
                this.idProceso, error && "error", json.stack);
        } else {
            Estatus.finalizarProceso(json.mensajeUsuario
                || Mensajes["fitbank.contexto.CARGADO"], this.idProceso);
        }
        Estatus.registrar(response);
    },


    /**
     * Función que ejecuta calculos.
     */
    _calcular: function() {
        this.formulario.calcular();
    },

    /**
     * Función que muestra ventana emergente de ayuda
     */
    _ayudar: function() {
        var ventana = new Ventana({
            titulo: "Ayuda",
            contenido: "Cargando...",
            w: 500,
            h: 300,
            verFondo: false
        });
        new Ajax.Updater(ventana.contenido, "../MANUAL/manuales/"
            + c.subsystem + c.transaction + ".html");
        ventana.ver();
    },

    /**
     * Función que muestra ventana emergente del menu
     */
    _menuRapido: function() {
        Menu.menuRapido.alternar();
    },

    /**
     * Borra todos los campos indicados.
     *
     * @param a
     *            Primer parámetro (se pueden pasar más parametros tambien)
     */
    borrar: function(a) {
        a.split(",").each(function(e) {
            this.$(e).value = '';
        }, this);
    },

    /**
     * Funci�n que muestra ventana emergente de ayuda
     */
    _manualTecnico: function () {
        open("manualtecnico.html#" + c.id, "manual_tecnico");
    },

    /**
     * Ejecuta una Lista de Valores de forma automatica.
     */
    ejecutarLoV: function (name, record, value, opciones) {
        record = record || 0;
        opciones = Object.extend({
            soloConsulta: true
        }, opciones);

        if (this.$(name, record)) {
            if (value) {
                this.$(name, record).changeValue(value);
            }

            this.$(name, record).lv && this.$(name, record).lv.consultar(record, opciones);
        }
    },
    
    /**
     * Adjunta uno o varios formularios en la posici�n que se le indique.
     */
    adjuntar: function(transactions, index) {
        Enlace.submit(this,{
            tipo: GeneralRequestTypes.ADJUNTAR,
            params: {
                    _transacciones: transactions,
                    _indice: index
            }
        });
    },

    /**
     * Obtiene el elemento (o elementos) referenciados por el name y el
     * registro. Si hay un solo elemento no devuelve un array si no el elemento
     * en sí.
     *
     * @param name
     *            Nombre del elemento.
     * @param registro
     *            Opcional, el numero de registro que se quiere obtener.
     *
     * @return Un elemento o un array de elementos
     */
    $: function(name, registro) {
        var e = this.$W(name, registro);
        return Object.isArray(e) && e.length == 1 ? e[0] : e;
    },

    /**
     * Obtiene un array de los elementos referenciados por el name y el
     * registro.
     *
     * @param name
     *            Nombre del elemento.
     * @param registro
     *            Opcional, el numero de registro que se quiere obtener.
     *
     * @return Un array de elementos incluso si hay un solo elemento
     */
    $N: function(name, registro) {
        return $N(this.form, name, registro);
    },

    /**
     * Obtiene un array de los valores de los elementos referenciados por el
     * name y el registro.
     *
     * @param name
     *            Nombre del elemento.
     * @param registro
     *            Opcional, el numero de registro que se quiere obtener.
     *
     * @return Un array de valores incluso si hay un solo valor
     */
    $V: function(name, registro) {
        return $V(this.form, name, registro);
    },

    /**
     * Obtiene un array de los widgets visible de los elementos referenciados por el
     * name y el registro.
     *
     * @param name
     *            Nombre del elemento.
     * @param registro
     *            Opcional, el numero de registro que se quiere obtener.
     *
     * @return Un array de valores incluso si hay un solo valor
     */
    $W: function(name, registro) {
        return $W(this.form, name, registro);
    },

    /**
     * Obtiene un array de los elementos que cumplen con el selector.
     *
     * @param selector
     *            Un selector CSS
     *
     * @return Un array de elementos
     */
    $$: function(selector) {
        return this.form.select(selector);
    }

});
