include("lib.prototype");
include("lib.bsharptree");

include("fitbank.evento");

include("fitbank.ui.ventana");

/**
 * Clase MenuRapido - Funciones para crear un menú rapido.
 */
var MenuRapido = Class.create( {

    transacciones: null,

    seleccionado: null,

    cuenta: 0,

    initialize: function() {
        this.elemento = new Element("div", {
            className: "menu-rapido"
        });
        this.input = new Element("input");
        this.filtrosDiv = new Element("div", {
            className: "menu"
        });
        this.resultadosDiv = new Element("div", {
            className: "resultados"
        });

        this.elemento.insert(new Element("div").update(this.input));
        this.elemento.insert(this.filtrosDiv);
        this.elemento.insert(this.resultadosDiv);

        this.input.on("keyup", this.keyup.bind(this));
        this.resultadosDiv.on("click", this.activar.bind(this));

        this.resultadosDiv.on("mouseover", function(e) {
            this.seleccionar(Event.element(e));
        }.bind(this));

        this.ventana = new Ventana( {
            titulo: "Cargar nueva transacción",
            contenido: this.elemento,
            fondo: true,
            destruirAlCerrar: false
        });

        this.reset();
    },

    reset: function() {
        this.transacciones = new BSharpTree(function(x) {
            return x.n;
        });
        this.filtrosDiv.update("");
    },

    cargarPrincipal: function(principal) {
	var ul = new Element("ul", {
            className: "menu-rapido-arbol"
        });
        this.filtrosDiv.insert(ul);

        principal.each(function(item) {
            if (!item.transaccion) {
                this.cargarItem(item, ul);
            }
        }, this);
    },


    cargarItem: function(item, ul) {
        var li = new Element("li");
        ul.insert(li);

        var a = new Element("a", {
            href: "#"
        }).update(item.nombre);
        li.insert(a);

        a.on("click", (function(e) {
            this.input.value = "";
            this.query("p:" + item.nombre + "*");
            $E(e).cancelar();
        }).bind(this));

        a.on("dblclick", (function(e) {
            li.toggleClassName("show");
            $E(e).cancelar();
        }).bind(this));

        li.on("click", (function(e) {
            this.input.value = "";
            this.input.select();
            li.toggleClassName("show");
            $E(e).cancelar();
        }).bind(this));

        var ulSub = new Element("ul");
        item.items.each(function(item) {
            if (!item.transaccion) {
                this.cargarItem(item, ulSub);
            }
        }, this);

        if (ulSub.hasChildNodes()) {
            li.addClassName("subitems");
            li.insert(ulSub);
        }
    },

    cargar: function(transacciones) {
        transacciones.each(this.indexar.bind(this));
    },

    ver: function() {
        this.ventana.ver();
        this.input.activate.bind(this.input).delay(0.1);
    },

    esconder: function() {
        this.ventana.esconder();
        this.deseleccionar();
        this.input.value = "";
        this.resultadosDiv.update("");
    },

    alternar: function() {
        if (this.ventana.visible) {
            this.esconder();
        } else {
            this.ver();
        }
    },

    limpiarResultados: function() {
        this.deseleccionar();
        this.resultadosDiv.childElements().each(Element.remove);
    },

    /**
     * @private
     */
    seleccionar: function(elemento) {
        if (elemento && elemento.contenido) {
            var seleccionado = this.deseleccionar();
            this.seleccionado = elemento || seleccionado;

            if (this.seleccionado) {
                this.seleccionado.addClassName("seleccionado");
                this.seleccionado.ensureVisible();
            }
        }
    },

    /**
     * @private
     */
    deseleccionar: function() {
        var seleccionado = this.seleccionado;

        this.seleccionado = null;

        if (seleccionado) {
            seleccionado.removeClassName("seleccionado");
        }

        return seleccionado;
    },

    /**
     * @private
     */
    activar: function() {
        if (this.seleccionado) {
            Entorno.contexto.cargar({
                subsistema: this.seleccionado.contenido.subsistema,
                transaccion: this.seleccionado.contenido.transaccion
            });
            this.esconder();
        }
    },

    /**
     * @private
     */
    keyup: function(e) {
        e = $E(e);

        switch (e.tecla) {
        case e.ARRIBA:
            if (this.seleccionado) {
                this.seleccionar(this.seleccionado.previous());
            }
            return;

        case e.ABAJO:
            if (this.seleccionado) {
                this.seleccionar(this.seleccionado.next());
            }
            return;

        case e.ENTER:
            this.activar();
            return;

        case e.ESC:
            this.esconder();
            return;
        }

        if (!this.input.value) {
            this.limpiarResultados();
            return;
        }

        if (!this.timeout) {
            var query = this.input.value;
            this.timeout = setTimeout(this.query.bind(this, query), 100);
        }
    },

    query: function(query) {
        this.limpiarResultados();
        var resultados = this.transacciones.search(query.toLowerCase());
        resultados.each(this.resultadosDiv.insert
                .bind(this.resultadosDiv));
        if (resultados.length > 0) {
            this.seleccionar(this.resultadosDiv.firstChild);
        }
        this.timeout = null;
    },

    /**
     * @private
     */
    indexar: function(contenido) {
        var elemento = this.getElemento(contenido);

        this.addIndice(contenido.subsistema, elemento);
        this.addIndice(contenido.transaccion, elemento);
        this.addIndice(contenido.nombre, elemento);
        this.addIndice(contenido.parent && contenido.parent.nombre, elemento);
        this.addIndice(contenido.subsistema + contenido.transaccion, elemento);

        this.addIndice("p:" + (contenido.parent && contenido.parent.nombre)
                + "*", elemento);
        this.addIndice("st:" + contenido.subsistema + contenido.transaccion
                + "*", elemento);
    },

    /**
     * @private
     */
    addIndice: function(texto, elemento) {
        if (texto) {
            this.transacciones.add(texto.toLowerCase(), elemento);
        }
    },

    /**
     * @private
     */
    getElemento: function(contenido) {
        var elemento = new Element("div").update(contenido.nombre);

        if (contenido.transaccion) {
            elemento.insert( {
                top: new Element("span", {
                    className: "tag transaccion"
                }).update(contenido.transaccion)
            });
        }

        if (contenido.subsistema) {
            elemento.insert( {
                top: new Element("span", {
                    className: "tag subsistema"
                }).update(contenido.subsistema)
            });
        }

        elemento.contenido = contenido;
        elemento.n = this.cuenta++;

        return elemento;
    }
});
