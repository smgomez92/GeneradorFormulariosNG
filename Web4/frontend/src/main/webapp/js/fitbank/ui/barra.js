include("lib.prototype");

include("fitbank.logger");

include("fitbank.ui.teclas");

/**
 * Namespace Barra - Define funciones para manejo de la barra de tareas.
 */
var Barra = {

    BOTONES: $A( [ {
        nombre: "nueva",
        titulo: "Nueva ventana",
        onclick: Teclas.ACCIONES.nueva.evento
    }, {
        nombre: "menu",
        titulo: "Menu transacciones (F11)",
        onclick: Teclas.ACCIONES.menu.evento
    }, {
        nombre: "calculadora",
        titulo: "Calculadora (F6)",
        onclick: Teclas.ACCIONES.calculadora.evento
    }, {
        nombre: "manual_tecnico",
        titulo: "Manual Técnico",
        onclick: Teclas.ACCIONES.manualTecnico.evento
    }, {
        nombre: "ayuda",
        titulo: "Ayuda (F1)",
        onclick: Teclas.ACCIONES.ayuda.evento
    } ]),

    init: function(elemento) {
        this.elemento = $(elemento);

        if (this.elemento) {
            Barra.agregarSeparador();
            Barra.BOTONES.each(Barra.agregarBoton, this);
        }
    },

    agregarBoton: function(boton) {
        var button = new Element("button", {
            title: boton.titulo,
            className: "boton",
            tabIndex: -1
        });
        
        button.img = new Element("img", {
            src: "img/barra/" + boton.nombre + ".png"
        });

        button.insert(button.img);

        //No agregar ciertos botones a la barra en caso de asi requerirlo
        var parameter = Parametros['fitbank.ui.barra.' + boton.nombre + '.ENABLED'];
        if (!parameter || parameter === "true") {
            this.elemento.insert(button);
        }

        button.on("click", boton.onclick);
        
        return button;
    },

    agregarSeparador: function(boton) {
        this.elemento.insert(new Element("span", {
            className: "separador"
        }));
    }

};
