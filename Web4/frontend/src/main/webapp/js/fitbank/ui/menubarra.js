include("lib.prototype");

/**
 * Clase MenuBarra - Funciones para crear un menú barra.
 */
var MenuBarra = Class.create( {

    initialize: function(elemento) {
        this.elemento = $(elemento);
    },

    reset: function() {
        this.elemento.update("");
    },

    cargarPrincipal: function(principal) {
        var ul = new Element("ul", {
            className: "nav",
            id: "nav"
        });
        this.elemento.insert(ul);

        principal.items.each(function(item){
            if(!item.transaccion){
                this.cargarItem(item, ul);
           }
        }, this);
    },

    cargarItem: function(item, ul) {
        var li = new Element("li", {
            className: "list-item"
        });
        ul.insert(li);

        var titulo = "";
        if (item.transaccion){
            titulo += item.transaccion + " ";
        } else if (item.subsistema){
            titulo += item.subsistema + " ";
        }
        titulo += item.nombre

        var a = new Element("a", {
            href: "#",
            tabIndex: -1
        }).update(titulo);
        li.insert(a);

        if (item.transaccion) {
            a.on("click", (function(e) {
            	if(ul.parentNode.parentNode.parentNode.parentNode && ul.parentNode.parentNode.parentNode.parentNode.className != "nav") {
            		ul.parentNode.parentNode.parentNode.parentNode.hide();
            	}
            	ul.parentNode.parentNode.hide();
                ul.hide();
                Element.show.defer(ul);
                Element.show.defer(ul.parentNode.parentNode);
            	if(ul.parentNode.parentNode.parentNode.parentNode && ul.parentNode.parentNode.parentNode.parentNode.className != "nav") {
            		Element.show.defer(ul.parentNode.parentNode.parentNode.parentNode);
            	}
                Entorno.contexto.cargar({
                    subsistema: item.subsistema,
                    transaccion: item.transaccion
                });
            }).bind(this));
        }

        if (item.items && item.items.length) {
            var ulSub = new Element("ul");
            li.insert(ulSub);
            li.addClassName("sub");

			a.on("click", (function(e) {
				if(ul.className != "nav") {
					if(ulSub.style.left != "40px") {
						ulSub.style.left = "40px";
					}else if(1024 - (e.pageX ? e.pageX : e.clientX) > 180 && ulSub.style.left != "-207px") {
						ulSub.style.left = "140px";
					}else {
						ulSub.style.left = "-207px";
					}
				}
            }).bind(this));

			a.on("mouseover", (function(e) {
				if(ul.className != "nav") {
					if(1024 - (e.pageX ? e.pageX : e.clientX) < 180) {	// screen.availWidth <=> 1024
						ulSub.style.left = "-207px";
					}else if(ulSub.style.left != "140px") {
						ulSub.style.left = "140px";
					}
					if((e.pageY ? e.pageY : e.clientY) > 140 && ulSub.style.top == "" && item.items.length > 2) {
						var pos = e.pageY ? e.pageY : e.clientY;
						pos = pos > 540 ? 540 : pos;
						ulSub.style.top = "-" + Math.round(pos * pos * (item.items.length - 2) / 14000) + "px";
					}
				}
            }).bind(this));

            item.items.each(function(item) {
                this.cargarItem(item, ulSub);
            }, this);
        }
    }

});
