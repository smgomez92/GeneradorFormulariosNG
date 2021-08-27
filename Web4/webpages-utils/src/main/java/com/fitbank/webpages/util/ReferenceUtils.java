package com.fitbank.webpages.util;

import java.io.Serializable;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.fitbank.webpages.data.Reference;

/**
 * Clase utilitaria para manejar dependencias.
 * 
 * @author Smart Financial Sysytems CI
 */
public class ReferenceUtils implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Collection<Reference> references;

    public ReferenceUtils(Collection<Reference> references) {
        this.references = references;
    }

    public Reference getReference(String alias) {
        return getReference(alias, null);
    }

    public void putReference(String alias, String nombre) {
        getReference(alias, nombre);
    }

    public Reference get(String alias) {
        for (Reference reference : references) {
            if (reference.getAlias().equals(alias)) {
                return reference;
            }
        }

        return null;
    }

    public Reference getReference(String alias, String tabla) {
        if (ReferenceUtils.get(references, alias) == null) {
            if (tabla == null) {
                throw new RuntimeException(String.format(
                        "Reference alias='%s' no existente y tabla=null.",
                        alias));
            }

            references.add(new Reference(alias, tabla));
        }

        return ReferenceUtils.get(references, alias);
    }

    public Reference findReference(String alias) {
        if (ReferenceUtils.get(references, alias) == null) {
            return null;
        }

        return ReferenceUtils.get(references, alias);
    }

    public static Reference get(Collection<Reference> references, String alias) {
        return new ReferenceUtils(references).get(alias);
    }

    public Object size() {
        return references.size();
    }

    public boolean isEmpty() {
        return references.isEmpty();
    }

    public Collection<String> getAliasList() {
        return CollectionUtils.collect(references, new Transformer() {

            public Object transform(Object input) {
                return ((Reference) input).getAlias();
            }

        });
    }

}
