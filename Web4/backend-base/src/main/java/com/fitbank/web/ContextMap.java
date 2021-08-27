package com.fitbank.web;

import com.fitbank.util.Debug;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mapa especial con un tamaño limitado, y va eliminando entradas antiguas a
 * medida se va llenando
 *
 * @author Soft Warehouse S.A
 * @param <K> Key
 * @param <V> Value
 */
public class ContextMap<K, V> extends LinkedHashMap<K, V> {

    private static final String KEEP_CONTEXTS = "default|sig|lv|notificaciones|notifcomentarios";

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        if (!EntornoWeb.isKeepContextClean()) {
            return false;
        }

        boolean remove = this.size() > EntornoWeb.getMaxContexts();
        if (remove) {
            if (String.valueOf(eldest.getKey()).matches(KEEP_CONTEXTS)) {
                this.remove(eldest.getKey());
                this.put(eldest.getKey(), eldest.getValue());

                return false;
            }

            Debug.debug("Eliminando contexto antiguo " + eldest.getKey());
        }

        return remove;
    }
}
