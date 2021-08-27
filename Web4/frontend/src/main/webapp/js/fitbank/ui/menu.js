include("lib.prototype");

include("fitbank.ui.estatus");
include("fitbank.ui.menurapido");
include("fitbank.ui.menubarra");

/**
 * Namespace Menu - Funciones para manejar el menú.
 */
var Menu = {

    menuRapido: null,

    /**
     * Inicializa el menu pidiendo al backend una lista de conpañias.
     * 
     * @private
     */
    init: function() {
        Menu.idProceso = Estatus.iniciarProceso("Cargando compañías...");
        var request = new Ajax.Request('proc/' + GeneralRequestTypes.MENU, {
            parameters: 'menu=CIAS',
            onSuccess: Menu.llenarCias,
            onFailure: function() {
                Estatus.finalizarProceso(Mensajes['fitbank.ui.menu.ERROR_CIAS'],
                        Menu.idProceso, "error");
            },
            onException: rethrow
        });
        
        Estatus.getProceso(Menu.idProceso).setRequest(request);
        Menu.menuRapido = new MenuRapido("menuRapido");
        Menu.barra = new MenuBarra("entorno-barra-menu");
    },

    /**
     * Llena el combo de compañias con la respuesta del backend.
     * 
     * @private
     */
    llenarCias: function(respuesta) {
        var menu = respuesta.responseJSON;

        if (!menu || !menu.items) {
            Estatus.finalizarProceso(Mensajes['fitbank.ui.menu.ERROR_CIAS'],
                    Menu.idProceso, "error", "Resultado: "
                    + Util.encodeHTML(respuesta.responseText));
            return;
        } else {
            Estatus.finalizarProceso("OK", Menu.idProceso);
        }

        var nombre = new Element("div");
        $('cc').insert(nombre);
        var hashParts = document.location.hash &&
                document.location.hash.substring(1).split("/");

        if (menu.items.length == 1) {
            nombre.update(menu.items[0].nombre);
            Menu.cargarTrans(menu.items[0].compania);

        } else {
            var combo = new Element("select").update(respuesta.responseText);
            var seleccionar = new Element("option").update("--Seleccionar--");

            $('cc').insert(combo);
            nombre.hide();

            combo.insert(seleccionar);

            menu.items.each(function(item) {
                combo.insert(new Element("option", {
                    value: item.compania
                }).update(item.nombre));
            });

            nombre.on("click", function() {
                nombre.hide();
                combo.show();
            });

            combo.on("change", function() {
                if (combo.value) {
                    var seleccionado = combo.options[combo.selectedIndex];
                    Menu.cargarTrans(combo.value);
                    nombre.update(seleccionado.innerHTML).show();
                    combo.hide();
                }
                try {
                    seleccionar.remove();
                } catch(ex) {
                    // Ignorar
                }
            });

            if(!Entorno.activo && hashParts) {
                var cia = parseInt(hashParts[0]);
                combo.select("option")[cia].selected = true;
                combo.fireDOMEvent("change");
            }
        }
    },

    /**
     * Hace un pedido al backend pidiendo una lista de transacciones.
     * 
     * @private
     */
    cargarTrans: function(cia) {
        Entorno.activar();
        Entorno.contexto.reset();

        new Ajax.Request('proc/menu', {
            parameters: 'menu=TRANS&cia=' + cia,
            onSuccess: Menu.llenarTrans,
            onFailure: function(transport) {
                var json = transport.responseJSON || {};
                Estatus.mensaje(json.mensajeUsuario ||
                    Mensajes['fitbank.ui.menu.ERROR_TRANS'],
                    json.stackTrace || "", "error");
            },
            onException: rethrow
        });

        $('entorno-pt').value = "";
    },

    /**
     * Llena el menu de transacciones con la respuesta del backend.
     * 
     * @private
     */
    llenarTrans: function(respuesta) {
        var menu = respuesta.responseJSON;

        Menu.barra.reset();
        Menu.menuRapido.reset();

        if (!menu || !menu.items) {
            Estatus.mensaje(Mensajes['fitbank.ui.menu.ERROR_TRANS'], "Resultado: "
                    + Util.encodeHTML(respuesta.responseText), "error");
            return;
        }

        Menu.barra.cargarPrincipal(menu);
		
        var transacciones = $A();
        var cargarTransacciones = function(parent, item) {
            item.parent = parent;
            if (item.transaccion) {
                transacciones.push(item);
            } else if (item.items) {
                item.items.each(cargarTransacciones.curry(item));
            }
        };
        if (menu.items) {
            menu.items.each(cargarTransacciones.curry(null));
        } 

        Menu.menuRapido.cargarPrincipal(menu.items[0].items);
        Menu.menuRapido.cargar(transacciones);
    }
}
