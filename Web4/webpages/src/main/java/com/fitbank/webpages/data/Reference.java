package com.fitbank.webpages.data;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Data;

import com.fitbank.enums.JoinType;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Editable;

/**
 * Clase ReferenceUtils, representa las referencias de un formulario.
 * 
 * @author FitBank
 * @version 2.0
 */
@Data
public class Reference implements Serializable {

    private static final long serialVersionUID = 1L;

    @Editable(weight = 1)
    private String alias = "";

    @Editable(weight = 2)
    private String table = "";

    @Editable(weight = 3)
    @XML(nombreSubitems = "dependency")
    private final Set<Dependency> dependencies = new LinkedHashSet<Dependency>();

    @Editable(weight = 4)
    private boolean special = false;

    @Editable(weight = 5)
    private boolean queryOnly = false;
    
    @Editable(weight = 6)
    private boolean storeOnly = false;

    @Editable(weight = 7)
    private boolean distinct = false;

    @Editable(weight = 8)
    private boolean required = false;

    @Editable(weight = 9)
    private boolean keep = false;

    @Editable(weight = 10)
    private JoinType joinType = JoinType.INNER_JOIN;

    public Reference() {
    }

    public Reference(String alias, String table) {
        this.alias = alias;
        this.table = table;
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters
    // ////////////////////////////////////////////////////////

    public void setDependencies(Set<Dependency> dependencies) {
        this.dependencies.clear();
        this.dependencies.addAll(dependencies);
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} -> {1} ({2} dep){3}", getAlias(),
                getTable(), getDependencies().size(), isSpecial() ? "*" : "");
    }

}
