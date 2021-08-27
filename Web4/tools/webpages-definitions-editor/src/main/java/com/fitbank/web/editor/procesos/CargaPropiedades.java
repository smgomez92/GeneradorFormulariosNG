package com.fitbank.web.editor.procesos;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fitbank.js.GeneradorJS;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.anotaciones.UtilPropiedades;
import com.fitbank.util.Debug;
import com.fitbank.util.Servicios;
import com.fitbank.web.Proceso;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.editor.EditorRequestTypes;
import com.fitbank.web.js.JSClasses;
import com.fitbank.web.procesos.CargaJavaScripts;

@Handler(EditorRequestTypes.PROPERTIES)
public class CargaPropiedades implements Proceso {

    public RespuestaWeb procesar(PedidoWeb pedido) {
        Set<Class<?>> clases = new LinkedHashSet<Class<?>>();

        for (JSClasses jsClasses : Servicios.load(JSClasses.class)) {
            clases.addAll(jsClasses.getFullClasses());
        }

        // Primero superclases
        Set<Class<?>> clases2 = new LinkedHashSet<Class<?>>();
        for (Class<?> clase : clases) {
            CargaJavaScripts.agregarSuperclases(clases2, clase);
        }

        // ///////////////////////
        // Generar JS Propiedades
        StringBuffer contenido = new StringBuffer();

        for (Class<?> clase : clases2) {
            if (clase.isEnum() || clase.isInterface()) {
                continue;
            }

            if (Propiedad.class.isAssignableFrom(clase)) {
                continue;
            }

            Object objeto;
            try {
                objeto = clase.getConstructor().newInstance();
            } catch (Exception e) {
                Debug.warn(e);
                continue;
            }

            Debug.debug("Cargando propiedades de " + clase.getName());
            contenido.append(GeneradorJS.getJSClassName(clase)
                    + ".properties=" + GeneradorJS.toJS(UtilPropiedades
                            .getMapaPropiedades(objeto)) + ";\n");
        }

        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        respuesta.setContenido(contenido.toString(), "text/javascript");

        return respuesta;
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        new CargaJavaScripts().onError(pedido, respuesta, mensaje,
                mensajeUsuario, stackTrace, datos);
    }

}
