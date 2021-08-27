package com.fitbank.web.db;

import com.fitbank.util.Debug;
import com.fitbank.util.Servicios;
import com.fitbank.web.exceptions.ErrorWeb;

public final class TransporteDBFactory {

    private static Class<TransporteDB> instanceClass = null;

    static {
        for (TransporteDB transporteDB : Servicios.load(TransporteDB.class)) {
            if (instanceClass == null) {
                instanceClass = (Class<TransporteDB>) transporteDB.getClass();
            }
        }

        if (instanceClass == null) {
            throw new ErrorWeb("Implementación de TransporteDB no encontrada");
        }
    }

    private TransporteDBFactory() {
    }

    public static TransporteDB newInstance() {
        try {
            return (TransporteDB) instanceClass.newInstance();
        } catch (InstantiationException ex) {
            Debug.error("Problemas al instancias una clase tipo TransporteDB", ex);
        } catch (IllegalAccessException ex) {
            Debug.error("Problemas al inicializar una clase tipo TransporteDB", ex);
        }

        return null;
    }

}
