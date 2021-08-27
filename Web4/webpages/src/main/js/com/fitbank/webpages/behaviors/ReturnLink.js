include("lib.prototype");

/**
 * @remote
 */
ReturnLink.addMethods({

    initialize: function(parametros) {
        copyConstructor.bind(this)(parametros);

        if (c.navegacion.size() > 0) {
            c.$N(this.elementName).each(this.init, this);

        } else {
            c.$N(this.elementName).each(function(element) {
                element.hide();
            });
        }
    },

    _getValues: function($super, values, registros, defaultValues) {
        var obj = c.navegacion.pop();

        this.subsystem = obj.subsistema;
        this.transaction = obj.transaccion;
        this.nameMap = false;

        if (!values || !values.length) {
            values = $H();
            $H(obj.nameMap).each(function(p) {
                values.set(p.value, p.key);
            }, this);
        }

        return $super(values, registros, defaultValues);
    },

    _restoreNav: function(navStep) {
        //Si el prelink de un returnLink falla, restaurar el estado
        //de la navegación
        if (navStep) {
            var obj = c.navegacion.push(navStep);
        }
    }

});