package com.fitbank.web;

import com.fitbank.web.procesos.Registro;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mapa que almacena los mensajes del registro, manteniendo un máximo de elementos
 * en la cola. Luego de este límite, se van eliminando las entradas más antiguas
 * así como también los archivos que se generaron al crearlo.
 * 
 * 
 * @author Soft Warehouse S.A
 * @param <K> Tipo de Objeto para la llave del mapa
 * @param <V> Tipo de Objeto para el valor del mapa
 */
public class RegistroMap<K, V> extends LinkedHashMap<K, V> {

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean remove = this.size() > Registro.getMaximoRegistros();
        if (remove) {
            Registro.RegistroWeb registro = (Registro.RegistroWeb) eldest.getValue();
            new Thread(new RegistroRemoverThread(registro)).start();
        }

        return remove;
    }

    class RegistroRemoverThread implements Runnable {

        private final Registro.RegistroWeb registro;

        public RegistroRemoverThread(Registro.RegistroWeb registro) {
            this.registro = registro;
        }

        @Override
        public void run() {
            registro.eliminar();
        }
    }
}
