package com.fitbank.webpages.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fitbank.webpages.data.Reference;

public class NodoDependencia implements Serializable {

    private final ReferenceUtils referenceUtils;

    private final String alias;

    private final Map<String, NodoDependencia> dependencias = new HashMap<String, NodoDependencia>();

    private final Map<String, NodoDependencia> dependientes = new HashMap<String, NodoDependencia>();

    private NodoDependencia principal = null;

    public NodoDependencia(String alias, ReferenceUtils referenceUtils) {
        this.referenceUtils = referenceUtils;
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public Map<String, NodoDependencia> getDependencias() {
        return dependencias;
    }

    public Map<String, NodoDependencia> getDependientes() {
        return dependientes;
    }

    public NodoDependencia getPrincipal() {
        return principal;
    }

    public void setPrincipal(NodoDependencia principal) {
        this.principal = principal;
    }

    public Reference getReference() {
        return referenceUtils.get(getAlias());
    }
}
