/**
 * Clase GeneratorProperties - Sirve para definir propiedades de clases en el
 * editor. Al momento se pueden definir eventos para los controles de un
 * formulario de edición.
 */
var GeneratorProperties = Class.create( {

    propertySubItems: "",

    events: $H(),

    autocomplete: {},

    initialize: copyConstructor

});

// Agregar metodos a todas las clases
Object.extend(GeneratorProperties.constructor.prototype, {

    getProperties: function() {
        return this.properties
            || (this.superclass && this.superclass.getProperties())
            || $H();
    },

    getGeneratorProperties: function() {
        return this.genProps
            || (this.superclass && this.superclass.getGeneratorProperties())
            || new GeneratorProperties();
    }

});
