/**
 * @remote
 */
PropiedadObjeto.addMethods( {

    getListItem: function(object, owner, propertyName) {
        var label = this.getLabel();
        var li = new Element("li").update(label);

        if (object) {
            label.insert(this.getEditButton(owner, propertyName));

            var ul = FormGenerator.generateTree(object);
            li.insert(ul);
            li.addClassName("subitems");
            li.on("click", function(e) {
                if ($E(e).elemento == li) {
                    li.toggleClassName("show");
                    object._visible = li.hasClassName("show");
                }
            });
            if (object._visible) {
                li.addClassName("show");
            }
        }

        return li;
    },

    getInput: function(valor, owner, propertyName) {
        var div = new Element('div');

        if (this.instanceSubClasses.length > 1) {
            div.insert(this.getSelect(owner, propertyName));
        } else {
            div.insert(valor != null && valor.getLabel ? valor.getLabel()
                    : null);
        }

        div.insert(this.getEditButton(owner, propertyName));

        return div;
    },

    getEditButton: function(owner, propertyName) {
        var but = new Element("div", {
            className: "editor-edit inline",
            title: "Editar"
        });

        var valor = owner[propertyName];
        var sc = this.instanceSubClasses;

        if (valor && valor.constructor.getProperties().size()) {
            but.on("click", function(event) {
                if (valor == null && (!sc || sc.length == 0)) {
                    valor = Editor.changeValue(valor, new this.instanceClass(),
                            owner, propertyName);
                }

                FormGenerator.generateWindow(valor);
            }.bind(this));
        } else {
            but.addClassName("disabled");
            but.setOpacity(0.5);
        }

        return but;
    },

    getSelect: function(owner, propertyName) {
        var sel = new Element("select");

        var blank = new Option("", null);
        sel.add(blank, null);
        blank.selected = true;

        this.instanceSubClasses.each(function(clase) {
            var valor = propertyName && owner[propertyName];
            var option = new Option(clase.simpleClassName, clase.className);

            if (valor && valor.constructor == clase) {
                option.defaultSelected = true;
                option.selected = true;
                blank.remove();
            }

            option.clase = clase;

            sel.add(option, null);
        });

        if (!propertyName) {
            return sel;
        }

        sel.on("change", function(event) {
            var clase = sel.options[sel.selectedIndex].clase;

            if (!clase) {
                return;
            }

            if (!confirm("Esta seguro que desea cambiar este valor?")) {
                Editor.refresh(true);
                return;
            }

            Editor.changeValue(owner[propertyName], new clase(), owner,
                    propertyName);
            Editor.refresh(true);
        });

        return sel;
    }

});
