package com.fitbank.webpages.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fitbank.enums.DependencyType;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.Reference;

/**
 * Esta clase sirve para manejar un árbol de dependencias de un WebPage. El
 * arbol solo tiene las dependencias inmediatas. Agrupa las referencias en un
 * gráfico dirigido a las tablas principales (que serán usadas al momento de
 * armar las consultas)
 * 
 * @author FitBank CI
 */
public class ArbolDependencias implements Serializable {

    private final Map<String, NodoDependencia> nodos = new LinkedHashMap<String, NodoDependencia>();

    private final Set<NodoDependencia> principales = new LinkedHashSet<NodoDependencia>();

    private final ReferenceUtils referenceUtils;

    public static boolean contains(Collection<Reference> references,
            String alias) {
        for (Reference reference : references) {
            if (reference.getAlias().equals(alias)) {
                return true;
            }
        }

        return false;
    }

    public ArbolDependencias(Collection<Reference> references) {
        referenceUtils = new ReferenceUtils(references);

        // Encontrar dependencias y dependientes
        for (Reference reference : references) {
            String alias = reference.getAlias();
            NodoDependencia nodo = getNodoDependencia(alias, true);

            for (Dependency dependency : reference.getDependencies()) {
                if (dependency.getType() != DependencyType.IMMEDIATE) {
                    continue;
                }

                String fromAlias = dependency.getFromAlias();
                if (StringUtils.isNotBlank(fromAlias)) {
                    NodoDependencia nodoDependencia = getNodoDependencia(fromAlias, true);
                    nodoDependencia.getDependientes().put(alias, nodo);
                    nodo.getDependencias().put(fromAlias, nodoDependencia);
                }
            }

        }

        // Encontrar nodos principales (sin dependencias)
        for (Reference reference : references) {
            String alias = reference.getAlias();
            NodoDependencia nodo = getNodoDependencia(alias, true);

            if (nodo == null || !nodo.getDependencias().isEmpty()) {
                continue;
            }

            principales.add(nodo);

            for (NodoDependencia nodoDependiente : getNodosConectados(nodo)) {
                nodoDependiente.setPrincipal(nodo);
            }
        }
    }

    private NodoDependencia getNodoDependencia(String alias, boolean create) {
        if (StringUtils.isBlank(alias)) {
            return null;
        }

        NodoDependencia nodo = null;

        if (nodos.containsKey(alias)) {
            nodo = nodos.get(alias);
        } else if(create) {
            nodo = new NodoDependencia(alias, referenceUtils);
            nodos.put(alias, nodo);
        }

        return nodo;
    }

    public Collection<NodoDependencia> getNodosConectados(String alias) {
        return getNodosConectados(getNodoDependencia(alias, false));
    }

    public Collection<NodoDependencia> getNodosConectados(NodoDependencia nodo) {
        Set<NodoDependencia> nodos = new LinkedHashSet<NodoDependencia>();

        getNodosConectados(nodo, nodos);

        return nodos;
    }

    private void getNodosConectados(NodoDependencia nodo,
            Set<NodoDependencia> nodos) {
        nodos.add(nodo);

        for (NodoDependencia nodoDependencia : nodo.getDependencias().values()) {
            if (!nodos.contains(nodoDependencia)) {
                getNodosConectados(nodoDependencia, nodos);
            }
        }

        for (NodoDependencia nodoDependiente : nodo.getDependientes().values()) {
            if (!nodos.contains(nodoDependiente)) {
                getNodosConectados(nodoDependiente, nodos);
            }
        }
    }

    public Map<String, NodoDependencia> getNodos() {
        return nodos;
    }

    public Set<NodoDependencia> getPrincipales() {
        return principales;
    }

}
