Dependency.addMethods( {

    toQueryString: function() {
        var val = $H();

        val.set("dep_tablaDesde", this.fromAlias);
        val.set("dep_campoDesde", this.fromField);
        val.set("dep_campo", this.field);
        val.set("dep_comparador", this.comparator);
        val.set("dep_valor", this.valor ? this.valor + "%" : "");

        return val.toQueryString();
    }

});
