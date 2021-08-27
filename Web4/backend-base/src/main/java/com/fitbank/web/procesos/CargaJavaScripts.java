package com.fitbank.web.procesos;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import com.fitbank.js.GeneradorJS;
import com.fitbank.util.Debug;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.js.JSClasses;
import com.fitbank.web.js.JSClassesFactory;

@Handler(GeneralRequestTypes.CLASES_JS)
@RevisarSeguridad
public class CargaJavaScripts implements Proceso {

    private static Boolean ie;

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        String userAgent = pedido.getHttpServletRequest().getHeader("User-Agent");
        ie = userAgent.toLowerCase().contains("msie");

        respuesta.getTransporteDB().cleanResponse();

        respuesta.setContenido(getClasesJS(), "text/javascript");

        return respuesta;
    }

    public static String getClasesJS() {
        Set<Class<?>> simpleClases = new LinkedHashSet<Class<?>>();
        Set<Class<?>> fullClases = new LinkedHashSet<Class<?>>();

        for (JSClasses jsClasses : JSClassesFactory.listJSClasses()) {
            simpleClases.addAll(jsClasses.getSimpleClasses());
            fullClases.addAll(jsClasses.getFullClasses());
        }

        // ///////////////////////
        // Generar JS
        StringBuilder contenido = new StringBuilder();

        // Primero superclases
        Set<Class<?>> simpleClases2 = new LinkedHashSet<Class<?>>();
        for (Class<?> clase : simpleClases) {
            agregarSuperclases(simpleClases2, clase);
        }

        Set<Class<?>> fullClases2 = new LinkedHashSet<Class<?>>();
        for (Class<?> clase : fullClases) {
            agregarSuperclases(fullClases2, clase);
        }

        for (Class<?> clase : simpleClases2) {
            Debug.info("Cargando clase simple " + clase.getName());
            contenido.append("eval('");
            if(ie) {
                contenido.append(StringEscapeUtils.escapeJavaScript(GeneradorJS.classToSimpleJS(clase)));
            } else {
                contenido.append(StringEscapeUtils.escapeJavaScript(GeneradorJS.
                        classToSimpleJS(clase) + "\n//@ sourceURL=fitbank/proc/clases/"
                        + clase.getName().replaceAll("\\.", "/") + ".js\n"));
            }
            contenido.append("');\n");
        }

        for (Class<?> clase : fullClases2) {
            Debug.info("Cargando clase " + clase.getName());
            contenido.append("eval('");
            if(ie) {
                contenido.append(StringEscapeUtils.escapeJavaScript(GeneradorJS.classToJS(clase)));
            } else {
                contenido.append(StringEscapeUtils.escapeJavaScript(GeneradorJS.
                        classToJS(clase) + "\n//@ sourceURL=fitbank/proc/clases/"
                        + clase.getName().replaceAll("\\.", "/") + ".js\n"));
            }
            contenido.append("');\n");
        }

        return contenido.toString();
    }

    public static void agregarSuperclases(Set<Class<?>> clases2, Class<?> clase) {
        if (!clase.isInterface()
                && !clase.getSuperclass().getName().startsWith("java")) {
            Class<?> superclase = clase.getSuperclass();
            if (!clases2.contains(superclase)) {
                agregarSuperclases(clases2, superclase);
            }
        }

        if (!clases2.contains(clase)) {
            clases2.add(clase);
        }
    }

    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.setContenido(String.format(
                "Estatus.mensaje('Error al cargar JS', '%s', 'error');",
                StringEscapeUtils.escapeJavaScript(stackTrace)), "text/javascript");
    }

}
