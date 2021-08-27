/**
 * Lista de clases de objetos primitivos.
 */
var PRIMITIVES = $A( [ String, Number, Boolean ]);

/**
 * Clase PrimitiveWrapper que sirve para envolver un objeto primitivo.
 */
var PrimitiveWrapper = Class.create( {
    initialize: function(value) {
        this.__value__ = value;
    },

    getLabel: function() {
        return this.__value__;
    },

    toJSON: function() {
        return Object.toJSON(this.__value__);
    }
});

/**
 * Funcion que envuelve un objeto solo si es un primitivo.
 * 
 * @param object
 *            Objeto a ser envolvido.
 * 
 * @return Objeto envolvido u objeto original.
 */
var wrap = function(object) {
    if (typeof object == "undefined" || object == null) {
        return object;
    } else if (PRIMITIVES.include(object.constructor)) {
        return new PrimitiveWrapper(object);
    } else {
        return object;
    }
};

/**
 * Funcion que desenvuelve un objeto probablemente envolvido.
 * 
 * @param object
 *            Objeto que puede ser envolvido o no.
 * 
 * @return Objeto desenvolvido o si no estaba envolvido el original.
 */
var unwrap = function(object) {
    if (object.constructor == PrimitiveWrapper) {
        return object.__value__;
    } else {
        return object;
    }
};
