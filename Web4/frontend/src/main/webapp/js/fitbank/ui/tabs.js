include("lib.prototype");

include("fitbank.proc.clases");

/**
 * Namespace Tabs - Define funciones para manejar los Tabs.
 */
var Tabs = {

    tabActual: $A(),

    /**
     * @private
     */
    _listeners: $A(),

    /**
     * @private
     */
    _disabled: $H(),

    /**
     * @private
     */
    _hidden: $H(),

    /**
     * @private
     */
    reset: function(mantenerTab) {
        if (!mantenerTab) {
            Tabs.tabActual = $A([ "1" ]);
        }
        Tabs._listeners.clear();
        Tabs._disabled = $H();
        Tabs.mostrar.defer(Tabs.tabActual.join("-"));
        Tabs.mostrar("0");
    },

    get: function(length) {
        if (!Tabs.tabActual) {
            return null;
        }

        if (!length) {
            length = Tabs.tabActual.length;
        }

        if (!Object.isNumber(length)) {
            length = length.split("-").length;
        }

        return Tabs.tabActual.inGroupsOf(length)[0].join("-") || null;
    },

    getLast: function(length) {
        return Tabs.get(length).split("-").last();
    },

    /**
     * Función que muestra los tabs.
     */
    mostrar: function(tab, elemento) {
        if (elemento && elemento.blur) {
            elemento.blur();
        }
        
        if (Tabs.get(tab) == tab || Tabs.isDisabled(tab)) {
            return;
        }

        // Primero intentar mostrar el primer tab de ese grupo
        if (!tab.endsWith("-1") && tab.split("-").length > Tabs.tabActual.length) {
            Tabs.mostrar(tab.substring(0, tab.lastIndexOf("-")) + "-1");
        }

        // Ocultar tab actual si existe y si no es el tab 0 (siempre visible)
        if (Tabs.get(tab) && Tabs.getLast(tab) != "0") {
            c.$$(".tab-" + Tabs.get(tab)).each(Element.hide);
            c.$$(".tab-child-" + Tabs.get(tab)).each(Element.hide);
        }

        // Mostrar nuevo tab
        c.$$(".tab-" + tab).each(Element.show);

        // Arreglar elementos TabBar
        Tabs.setTabBar(Tabs.get(tab), false);
        Tabs.tabActual = tab.split("-");
        Tabs.setTabBar(Tabs.get(tab), true);

        Tabs._listeners.each(function(listener) {
            listener(Tabs.tabActual);
        });
    },

    /**
     * Deshabilita o habilita un tab.
     * 
     * @param tab Tab a deshabilitar o habilitar
     * @param disable Indica si se tiene que deshabilitar (default: true)
     */
    disable: function(tab, disable) {
        Tabs._disabled.set(tab, disable !== false);

        c.$$(".tab-bar-" + tab).each(function(e) {
            if (Tabs.isDisabled(tab)) {
                e.addClassName("disabled");
            } else {
                e.removeClassName("disabled");
            }
        });
    },

    isDisabled: function(tab) {
        return Tabs._disabled.get(tab);
    },

    hide: function(tab, hide) {
        Tabs._hidden.set(tab, hide !== false);

        c.$$(".tab-bar-" + tab).each(function(e) {
            if (Tabs.isHidden(tab)) {
                e.hide();
            } else {
                e.show();
            }
        });
    },

    isHidden: function(tab) {
        return Tabs._hidden.get(tab);
    },

    addListener: function(listener) {
        Tabs.removeListener(listener);
        Tabs._listeners.push(listener);
    },

    removeListener: function(listener) {
        Tabs._listeners = Tabs._listeners.without(listener);
    },

    /**
     * Maneja los elementos TabBar
     * @private
     */
    setTabBar: function(tab, activo) {
        if (activo) {
            c.$$("li.tab-bar-" + tab).each(function(li) {
                li.addClassName("activo");
            });
        } else {
            c.$$("li.tab-bar-" + tab + ", li.tab-bar-child-" + tab).each(function(li) {
                li.removeClassName("activo");
            });
        }
    },

    /**
     * Función para cambiar de tab.
     * 
     * @param formulario Contiene el nombre completo del formulario (ej. '010234').
     * @param namesCampos Name de los campos que se van a pasar a c.cargar
     * @param elemento Elemento donde se hizo click
     * @param options Opciones
     */
    irA: function(formulario, namesCampos, elemento, options) {
        if (elemento && elemento.blur) {
            elemento.blur();
        }

        if (Tabs.isDisabled(formulario)) {
            return;
        }
        
        options = Object.extend({
            subsistema: formulario.substring(0, 2),
            transaccion: formulario.substring(2),
            campos: namesCampos,
            registro: elemento.registro,
            preLink: function(options) {},
            posLink: function(options) {}
        }, options);
        
        if (options.preLink && c._checkError(options.preLink.curry(options),
            Mensajes['fitbank.ui.tabs.ERROR_PRELINK'])) {
            return;
        }

        if (c.$(formulario, 0) != null) {
            formulario = c.$V(formulario, 0);
        }

        var campos = {};
        
        namesCampos.each(function(name) {
            if (c.$(name, 0)) {
                campos[name] = c.$V(name, 0);
            }
        });

        c.cargar({
            st: formulario,
            campos: campos,
            posLink: options.posLink.curry(options)
        });
    }

};
