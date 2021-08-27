// encoding: UTF-8
include("lib.prototype");

include("fitbank.logger");
include("fitbank.validar");

ListOfValues.addMethods({

    /**
     * Página actual en la lista de valores.
     */
    pagina: 1,

    /**
     * Contiene objetos con la informacion de cada registro: lista, imagen
     */
    registros: $A(),

    /**
     * Elemento de html de la lista
     */
    _lista: null,

    /**
     * Arreglo de criterios.
     */
    _criterios: $A(),

    /**
     * Imagen que se muestra inicialmente en el elemento.
     */
    imagenVer: "img/asistentes/listavalores.png",

    /**
     * Imagen que se muestra en el elemento mientras se consulta.
     */
    imagenConsultando: "img/asistentes/listavalores-cargando.gif",

    /**
     * Número de fields visibles.
     */
    visibleFields: 0,

    /**
     * Registro seleccionado de la lista, para navegacion por teclado
     * la fila de criterios indica el registro -1
     */
    _registroSeleccionado: -1,

    /**
     * Variable de instancia con el numero de registros de la respuesta de consulta
     */
    _numeroRegistrosRespuesta: 0,

    /**
     * Registros de la respuesta de consulta
     */
    _datos: null,

    /**
     * @private
     */
    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        if (!parametros) {
            return;
        }

        // Guardar el contexto en que fue creada esta lista de valores
        this._contexto = c;

        if (!this._contexto) {
            throw new Error("No hay contexto!");
        }

        if (!this.visible) {
            this.imagenVer = "img/blanco.png";
        }

        // Registrar eventos en campos y encontrar primer campo que no
        // necesariamente es c.$(this.elementName)
        var primero = null;
        this.fields.each(function(field) {
            if (field.visible) {
                this.visibleFields++;
            }
            if (!field.elementName) {
                return
            }
            this._contexto.$N(field.elementName).each(function(elemento) {
                if (this.registrarEventos(field, elemento) && primero == null) {
                    primero = field;
                }
            }, this);
        }, this);

        if (!this.elementName && primero) {
            this.elementName = primero.elementName;
        }

        this.elementName && this._contexto.$N(this.elementName).each(this.initPrincipal.bind(this));
        primero && this._contexto.$N(primero.elementName).each(this.initPrimero.bind(this));
        this.crearLista();
    },

    /**
     * Inicial eventos en el elemento en sí.
     */
    registrarEventos: function(field, elemento) {
        if (field.autoQuery) {
            elemento.on("change", (function(e) {
                if (elemento.value) {
                    this.consultar(elemento.registro, {
                        soloConsulta: true,
                        fireAlways: field.fireAlways
                    }, e);
                } else {
                    this._limpiar(elemento.registro);
                }
            }).bind(this));
        }

        if (!elemento.visible() || elemento.type == 'hidden') {
            return false;
        } else if (!this.registerEvents) {
            return this.visible;
        }

        var ver = this.ver.bind(this, elemento.registro);

        if (field.visible) {
            elemento.on("keyup", function(e) {
                e = $E(e);

                if (e.tecla && e.tecla == e.ABAJO) {
                    ver(e);
                }
            });

            return this.visible;
        } else if(!this._usar(field)) {
            elemento.on("change", this._limpiar.bind(this, elemento.registro));
        }

        return false;
    },

    /**
     * Registra el elemento principal y la imagen para presentar la lista.
     *
     * @private
     */
    initPrincipal: function(principal) {
        // Poner referencias en todo el registro al elemento
        var registro = this.registros[principal.registro] = {
            elemento: principal,
            numero: principal.registro
        };

        // Registrando la lista de valores en el elemento
        principal.lv = principal.assistant = this;

        // Crear la imagen que presenta la lista
        registro.imagen = new Element("img", {
            'class': "asistente-icono",
            src: this.imagenVer,
            width: 16,
            height: 16,
            alt: this.title,
            title: this.title
        });

        principal.insert( {
            after: registro.imagen
        });

        if (this.visible && this.registerEvents) {
            // Registrar eventos
            registro.imagen.on("click", this.ver.bind(this, registro));
            principal.on("dblclick", this.ver.bind(this, registro));
        }
    },

    /**
     * Registra el primer elemento.
     *
     * @private
     */
    initPrimero: function(primero) {
        var registro = this.registros[primero.registro] 
            || this.registros[0];

        registro.primero = primero;
    },

    /**
     * Crea la lista
     *
     * @private
     */
    crearLista: function() {
        // Crear la lista a presentarse
        this._lista = new Element("div", {
            'class': "list-of-values-lista"
        }).hide();

        this._contexto.elemento.insert(this._lista);

        var tabla = new Element("table", {
            'class': "list-of-values-tabla"
        });
        this._lista.insert(tabla);

        var thead = new Element("thead");
        tabla.insert(thead);

        // Crear titulos y criterios
        if (this.head) {
            var trTitulos = new Element("tr");
            thead.insert(trTitulos);

            var trCriterios = new Element("tr");
            thead.insert(trCriterios);

            this.agregarCriterios(trTitulos, trCriterios);
        }

        this._lista.tbody = new Element("tbody");
        tabla.insert(this._lista.tbody);

        this._lista.on("mousedown", (function() {
            this.clearTimeout.bind(this).defer();
        }).bind(this));

        var form = new Element("form");
        form.onsubmit = function() { 
            return false;
        }
        
        this._lista.wrap(form);
    },

    /**
     * Agrega criterios al encabezado de la tabla de resultados.
     */
    agregarCriterios: function(trTitulos, trCriterios) {
        this.fields.each(function(field) {
            if (this._usar(field) && field.visible) {
                trTitulos.insert(new Element("td").update(field.title));

                var input = new Element("input", {
                    'class': "criterio",
                    value: field.value || "",
                    style: 'width: ' + (field.w ? field.w + 'px' : 'auto')
                });
                trCriterios.insert(new Element("td").update(input));

                input.field = field;

                this.registrarEventosCriterio(field, input);

                this._criterios.push(input);
            }
        }, this);
    },

    /**
     * Registra los eventos del teclado de un elemento a ser usado como
     * criterio.
     *
     * Si se esta navegando entre los registros de la lista, se bloquea
     * el ingreso de teclas para criterios a menos que vuelva el foco
     * al campo de criterio
     *
     * La nanvegacion se maneja:
     * Flecha Abajo: Ubicarse (descendentemente) sobre un registro
     * Flecha Arriba: Ubicarse (ascendentemente) sobre un registro
     * Flecha Derecha: Paginar hacia adelante
     * Flecha Izquierda: Paginar hacia atras
     * Enter: Seleccionar el registro, en caso de estar navegando, caso
     *        contrario, consulta por el filtro de criterio escrito
     */
    registrarEventosCriterio: function(field, elemento) {
        elemento.on("keyup", (function(e) {
            e = $E(e);

            if (this._lista.visible() && e.tecla && e.tecla == e.ESC) {
                this.esconder();
            }
        }).bind(this));

        elemento.on("keydown", (function(e) {
            e = new Evento(e);

            if (!this._lista.visible()) {
                return;
            }

            if (e.tecla == e.ABAJO) {
                this.navegar(1);
            }

            if (e.tecla == e.ARRIBA) {
                this.navegar(-1);
            }

            if (e.tecla == e.IZQUIERDA && this._registroSeleccionado >= 0) {
                this._registroSeleccionado = -1;
                
                if (this.pagina > 1) {
                    this.consultar(null, {
                        pagina: this.pagina - 1
                    }, e);
                }
            }

            if (e.tecla == e.DERECHA && this._registroSeleccionado >= 0) {
                this._registroSeleccionado = -1;

                if (this._numeroRegistrosRespuesta == 10) {
                    this.consultar(null, {
                        pagina: this.pagina + 1
                    }, e);
                }
            }
        }).bind(this));

        elemento.on("keypress", (function(e) {
            e = $E(e);

            if (e.tecla == e.ENTER && this._lista.visible()) {
                if (this._registroSeleccionado < 0) {
                    field.value = elemento.value;
                    this.consultar(null, {
                        pagina: 1
                    }, e);
                } else {
                    // FIXME guardar en _registroSeleccionado realmente el registro seleccionado
                    var r = this._registroSeleccionado - (this.pagina == 1 ? 0 : 1);
                    this.obtener(this._registroActual, this._datos[r].values);
                    this.esconder();
                }
            } else if (this._registroSeleccionado > -1 && this._lista.visible()) {
                e.cancelar();
            }
        }).bind(this));

        elemento.on("focus", (function(e) { 
            this.clearTimeout();
            this._registroSeleccionado = -1;
            this._seleccionarRegistro(false);
        }).bind(this));

        elemento.on("blur", this.esconder.bind(this));
    },

    /**
     * Control de navegacion por los registros, mediante el teclado
     *
     * @param direccion
     *            Arriba [positivo], Abajo [negativo]
     */
    navegar: function(direccion) {
        if (direccion > 0) {
            this._registroSeleccionado += 1;
        }

        if (direccion < 0) {
            this._registroSeleccionado = this._registroSeleccionado == -1 ?
                this._numeroRegistrosRespuesta : this._registroSeleccionado - 1;
        }

        if (this._registroSeleccionado < 0 || this._registroSeleccionado > this._numeroRegistrosRespuesta) {
            this._registroSeleccionado = -1;
            this._seleccionarRegistro(false);
            return;
        }

        if (this._registroSeleccionado > this._numeroRegistrosRespuesta - 1) {
            if (this.pagina == 1) {
                if (direccion > 0) {
                    this._registroSeleccionado = -1;
                    this._seleccionarRegistro(false);
                    return;
                } else {
                    this._registroSeleccionado = this._numeroRegistrosRespuesta - 1;
                }
            } else {
                this._registroSeleccionado = this._numeroRegistrosRespuesta;
            }
        }

        if (this._registroSeleccionado < 1) {
            if (this.pagina == 1) {
                this._registroSeleccionado = 0;
            } else if (direccion < 0) {
                this._registroSeleccionado = -1;
                this._seleccionarRegistro(false);
                return;
            } else {
                this._registroSeleccionado = 1;
            }
        }

        this._seleccionarRegistro(true);
    },

    /**
     * Selecciona el registro indicado en la lista
     * 
     * @param seleccionar
     *            pintar o no, el registro seleccionado
     */
    _seleccionarRegistro: function(seleccionar) {
        var tbody = this._lista.tbody;
        var elements = tbody.getElementsByTagName("tr");

        for (var i=0 ; i < elements.length ; i++) {
            elements[i].setAttribute("class", "list-of-values-tr-unselected");
        }

        if (seleccionar) {
            elements[this._registroSeleccionado].setAttribute("class", "list-of-values-tr-selected");
        }
    },

    /**
     * Obtiene el objeto de registro.
     * 
     * @param registro
     *            Numero o objeto
     */
    getRegistro: function(registro) {
        return typeof registro == "number" ? this.registros[registro] : registro;
    },

    /**
     * Muestra la lista de valores en la primera página.
     * 
     * @param registro
     *            Registro sobre el que se ejecuta la consulta
     * @param e
     *            Evento en caso de que haya
     */
    ver: function(registro, e) {
        registro = this.getRegistro(registro);

        var elemento = this._contexto.$(this.elementName, registro.numero);
        if (elemento && (elemento.readOnly || elemento.disabled) || !Estatus.activo()) {
            return;
        }

        if (this._lista.visible()) {
            this.consultar(registro, {}, e);

        } else {
            this.consultar(registro, {
                pagina: 1,
                primeraConsulta: true,
                consultaInicial: this.initialQuery
            }, e);
        }
    },

    /**
     * Oculta la lista de valores
     */
    esconder: function() {
        this.timeout = setTimeout((function() {
            Entorno.lv = null;
            this._lista.hide();
            this._lista.tbody.update("");
            if (this._registroActual) {
                this._registroActual.elemento.activate();
                this._registroActual = null;
            }
            this._registroSeleccionado = -1;
            this._datos = null;
            this._criterios.each(function(criterio) {
                criterio.field.value = "";
                criterio.value  = "";
            }, this);
        }).bind(this), 100);
    },

    /**
     * Limpia los timeouts.
     */
    clearTimeout: function() {
        clearTimeout(this.timeout);
    },

    /**
     * Consulta la lista de valores.
     * 
     * @param registro
     *            Registro sobre el que se ejecuta la consulta o null para
     *            repetir con el último registro
     * @param opciones
     *            Un objeto con opciones
     * @param e
     *            Evento en caso de que haya
     */
    consultar: function(registro, opciones, e) {
        // Setear opciones
        opciones = Object.extend(Object.extend({
            pagina: 1,
            primeraConsulta: false,
            soloConsulta: false,
            fireAlways: false,
            consultaInicial: true
        }, e && e.options || {}), opciones);

        if (!opciones.fireAlways && (opciones.lv == this || opciones.generated)) {
            return;
        }

        if (this._contexto.cargando || Entorno.bloqueado()) {
            // No consultar si se están cargando los valores de la consulta.
            return;
        }

        registro = this.getRegistro(registro != null ? registro : this._registroActual);
        this._registroActual = registro;

        // Probar si hay preQuery y ejecutar
        if (this.preQuery) {
            var res = this.preQuery.call(registro.elemento, registro, e);
            if (res && res !== true || res === false) {
                Estatus.mensaje(res, null, "error");
                return;
            }
        }

        this._proceso = Estatus.iniciarProceso(null, this._parar.bind(this, registro));
        registro.imagen.src = this.imagenConsultando;
        this.clearTimeout();

        this.pagina = opciones.pagina;

        // Los elementos son elementos del formulario
        // Los criterios son los elementos del encabezado de la lista de valores
        // Los values de los fields se usan para armar el pedido al backend.

        // Para que un criterio se copie tienen que cumplirse esto:
        // 1. Tenga elementName y
        // 2. Sea tipo criterio o control, o
        // 3. Estemos ejecutando solo consulta y el field sea autoQuery
        var mensaje = null;
        var required = $A();
        this.fields.each(function(field) {
            if (!field.elementName) {
                return;
            }
            if (field.type != DataSourceType.CRITERION
                && field.type != DataSourceType.CRITERION_CONTROL
                && !(opciones.soloConsulta && field.autoQuery)) {
                return;
            }

            if (this._contexto.$N(field.elementName).length == 0) {
                Logger.warning("No se encontró el campo " + field.elementName);
                return;
            }

            var r = 0;
            if (this._contexto.$V(field.elementName).length > 1) {
                r = registro.numero;
            }

            var element = this._contexto.$N(field.elementName, r);
            field.value = element.getObjectValue();

            if (Object.isDate(field.value)) {
                field.value = new SimpleDateFormat(field.dateTransportFormat).format(field.value);
            } else {
                field.value = field.value.toString();
            }

            if (field.required && !element.value) {
                required.push(element);
                if (!opciones.soloConsulta || !e || e.target == null || this.elementName == e.target.name) {
                    mensaje = this.getMensaje("REQUERIDO");
                }
            } else {
                Validar.ok(this._contexto.$N(field.elementName, r), "required");
            }
        }, this);

        if (mensaje && required.size()) {
            required.each(function(element) {
                Validar.error(element, this.getMensaje("REQUERIDO"), "required");
            }, this);

            if (opciones.soloConsulta) {
                this._limpiar(registro);
            }

            this._parar(registro, mensaje, true);
            return;
        }

        if (required.size()) {
            this._parar(registro, "", false);
            return;
        }

        // Se carga los values de los criterios en los fields. Se sobreescribe
        // cualquier criterio cargado en el bloque anterior.
        if (!opciones.soloConsulta) {
            this._criterios.each(function(criterio) {
                criterio.field.value = criterio.value ? criterio.value : "";
            }, this);
        }

        // Enviar pedido
        if (opciones.soloConsulta) {
            // Evitar que se serialice el valor del elemento actual
            registro.elemento.realValue = registro.elemento.value;
            registro.elemento.value = "";
        }

        if (opciones.consultaInicial) {
            var request = new Ajax.Request("proc/" + GeneralRequestTypes.LV, {
                parameters: $H({
                    _contexto: "lv",
                    _contexto_padre: this._contexto.id,
                    _lv: this._toDataJSON(),
                    _transaccion_origen: this._contexto.subsystem.concat("-").concat(this._contexto.transaction),
                    _subs: this._contexto.subsystem,
                    _tran: this._contexto.transaction,
                    _user: this._contexto.user,
                    _sessionId: this._contexto.sessionId
                }).toQueryString() + "&" + Form.serialize(this._contexto.form),
                onSuccess: this.mostrarResultados.bind(this, registro, opciones),
                onFailure: function(respuesta) {
                    var json = respuesta.responseJSON;
                    this._parar(registro, json.mensajeUsuario, true, json.stack);
                }.bind(this),
                onException: rethrow
            });

            Estatus.getProceso(this._proceso).setRequest(request);
        } else {
            this.mostrarResultados(registro, opciones, {
                responseJSON: {
                    codigo: '0',
                    mensajeUsuario: 'OK',
                    registros: $A()
                }
            });
        }

        if (opciones.soloConsulta) {
            // Recuperar el valor del elemento actual
            registro.elemento.value = registro.elemento.realValue;
            registro.elemento.removeAttribute("realValue");
        }
    },

    _parar: function(registro, mensaje, error, stack) {
        registro = this.getRegistro(registro);
        registro.imagen.src = this.imagenVer;
        Estatus.finalizarProceso(mensaje || (error && this.getMensaje("ERROR_CONSULTA")) || "",
                this._proceso, error ? "error" : "", stack);
    },

    /**
     * Obtiene un json solo con los datos necesarios.
     */
    _toDataJSON: function() {
        // FIXME: revisar incidencia #0002430
        // Solo deberán ir los fields y la página
        return Object.toJSON({
            references: this.references,
            fields: this.fields,
            subsystem: this.subsystem,
            transaction: this.transaction,
            numberOfRecords: this.numberOfRecords,
            pagina: this.pagina,
            legacy: this.legacy,
            executedBy: this.executedBy,
            timeout: this.timeout,
            saveResponseInCache: this.saveResponseInCache
        });
    },

    /**
     * Carga los resultados en la tabla de la respuesta o en los campos si
     * opciones.soloConsulta es true.
     */
    mostrarResultados: function(registro, opciones, respuesta) {
        registro = this.getRegistro(registro);
        var json = respuesta.responseJSON;
        
        var mensaje = json.mensajeUsuario || this.getMensaje("OK");
        var error = json.codigo == "0" ? "" : json.codigo;

        this._datos = json.registros;
        this._numeroRegistrosRespuesta = this._datos.length;
        this.setControlFields(json.control);
        if (!error) {
            if (opciones.primeraConsulta && !opciones.consultaInicial) {
                mensaje = this.getMensaje("OK");
            }else if (this._numeroRegistrosRespuesta != 1 && opciones.soloConsulta && !this.multirecord) {
                error = "error";
                mensaje = this.noDataMessage || this.getMensaje("CRITERIO_INVALIDO");
            } else if (this._numeroRegistrosRespuesta == 0 && !this.multirecord) {
                error = "error";
                mensaje = this.noDataMessage || this.getMensaje("NO_RESULTADOS");
            } else if (json.codigo == "0") {
                mensaje = this.getMensaje("OK");
            }
        }

        if (error && opciones.soloConsulta) {
            this.fields.each(function(field) {
                if (field.elementName && field.autoQuery) {
                    var el = this._contexto.$N(field.elementName, registro.numero);
                    if (el.value) {
                        Validar.error(el, mensaje, "empty");
                    }
                }
            }, this);
        }

        this._parar(registro, mensaje, error);
        if (this.callbackOnNoResults && this.callback) {
            this.callback.call(registro.elemento, registro);
        }

        if (error) {
            return;
        }

        if (this.multirecord) {
            this._datos.each(function(datos, n) {
                this.obtener({numero: n}, datos.values, false);
            }, this);

        } else if (opciones.soloConsulta) {
            this.obtener(registro, this._datos[0].values, false);

        } else {
            this._lista.tbody.update("");

            if (opciones.consultaInicial) {
                // Flecha de página anterior
                if (this.pagina > 1) {
                    var anterior = new Element("tr").update(new Element("td", {
                        'class': "list-of-values-anterior",
                        colspan: this.visibleFields
                    }).update("&#9650;"));
                    this._lista.tbody.insert(anterior);

                    anterior.on("click", (function() {
                        this.consultar(registro, {
                            pagina: this.pagina - 1
                        });
                        this._criterios[0].activate();
                    }).bind(this));
                }

                // Registros
                this._datos.each(function(resultado) {
                    this.agregarRespuesta(registro, resultado.values,
                        this._lista.tbody);
                }, this);

                // Flecha de página siguiente
                if (json.paginacion) {
                    var siguiente = new Element("tr").update(new Element("td", {
                        'class': "list-of-values-siguiente",
                        colspan: this.visibleFields
                    }).update("&#9660;"));
                    this._lista.tbody.insert(siguiente);

                    siguiente.on("click", (function() {
                        this.consultar(registro, {
                            pagina: this.pagina + 1
                        });
                        this._criterios[0].activate();
                    }).bind(this));
                }

                if (!registro.primero) {
                    throw new Error(this.getMensaje("SIN_PRIMERO"));
                }
            }

            Entorno.lv = this;
            this._lista.show();
            this.reposition(registro);

            if (this._repositionInterval) {
                clearInterval(this._repositionInterval);
            }

            this._repositionInterval = setInterval((function() {
                if (!this._lista.visible()) {
                    clearInterval(this._repositionInterval);
                    this._repositionInterval = null;
                    return;
                }
                this.reposition(registro);
            }).bind(this), 250);

            if (opciones.primeraConsulta) {
                this._criterios[0].activate();
            }
        }
    },

    reposition: function(registro) {
        registro = this.getRegistro(registro);

        this._lista.clonePosition(registro.primero, {
            setWidth: false,
            setHeight: false,
            offsetTop: registro.primero.getHeight()
        });

        this._lista.ensureInside();
    },

    _usar: function(field) {
        // Esto debe coincidir con ConsultaListaValores#usar
        return field.type == DataSourceType.RECORD
            || field.type == DataSourceType.DESCRIPTION;
    },

    /**
     * Agrega una fila a la tabla de la respuesta.
     */
    agregarRespuesta: function(registro, values, tbody) {
        registro = this.getRegistro(registro);

        var tr = new Element("tr");
        tbody.insert(tr);

        var pos = 0;
        this.fields.each(function(field) {
            if (this._usar(field)) {
                if (field.visible) {
                    var td = new Element("td").update(values[pos]);
                    tr.insert(td);

                    td.on("click", this.obtener.bind(this, registro, values));
                    td.on("click", this.esconder.bind(this, registro));
                }
                pos++;
            }
        }, this);
    },

    /**
     * Llena los valores de una fila de la tabla de la respuesta en los
     * elementos correspondientes.
     */
    obtener: function(registro, values, focus) {
        registro = this.getRegistro(registro);

        var pos = 0;
        this.fields.each(function(field) {
            if (this._usar(field)) {
                if (field.elementName) {
                    this._setValue(field, registro, values[pos]);
                }
                pos++;
            }
        }, this);

        if (focus !== false) {
            registro.elemento.activate();
        }

        if (this.callback) {
            this.callback.call(registro.elemento, registro, values);
        }

        if (this.queryOnSuccess) {
            this._contexto.calcular();
            this._contexto.consultar.bind(this._contexto).defer();
        }
    },

    /**
     * Limpia los valores de un registro.
     */
    _limpiar: function(registro, e) {
        if (e && e.options && (e.options.lv == this || e.options.load)) {
            return;
        }

        registro = this.getRegistro(registro);

        this.fields.each(function(field) {
            if (this._usar(field) && field.elementName && !field.keep) {
                this._setValue(field, registro, "", true);
            }
        }, this);
    },

    /**
     * Setea el valor en un registro.
     *
     * @param field Campo que identifica al elemento
     * @param registro Registro del elemento
     * @param valor String a ser seteada
     * @param keepError Indica si se mantiene el error del campo
     */
    _setValue: function(field, registro, valor, keepError) {
        registro = this.getRegistro(registro);

        var el = this._contexto.$N(field.elementName, registro.numero);

        if (el == null) {
            Logger.warning("No se encontró el elemento " + field.elementName +
                    " registro " + registro.numero);
            return;
        }

        if (!keepError) {
            Validar.ok(el, "empty");
            Validar.ok(el, "required");
        }

        el.changeValue(valor, {
            lv: this
        });
    },

    getMensaje: function(key) {
        return Mensajes["com.fitbank.webpages.assistants.ListOfValues." + key];
    },

    setControlFields: function(fields) {
        if (fields) {
            $H(fields).each(function(field){
                if (c.$(field.key) instanceof HTMLInputElement && !c.$(field.key).hasClassName('calendar') && !c.$(field.key).hasClassName('date-formatter')) {
                    c.$(field.key).changeValue(field.value);
                } else if (c.$(field.key) instanceof HTMLInputElement && !c.$(field.key).hasClassName('calendar') && !c.$(field.key).hasClassName('date-formatter')) {
                    c.$(field.key).value = field.value;
                }
            });
        }
    }
});
