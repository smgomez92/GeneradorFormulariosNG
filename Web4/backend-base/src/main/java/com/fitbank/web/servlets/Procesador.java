package com.fitbank.web.servlets;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fitbank.util.CaseInsensitiveMap;
import com.fitbank.util.Clonador;
import com.fitbank.util.Debug;
import com.fitbank.util.Servicios;
import com.fitbank.web.Contexto;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ManejoExcepcion;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.procesos.Registro;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * Clase Procesador. Servlet que procesa los Pedidos Web.
 *
 * @author FitBank JT, CI
 * @version 2.0
 */
public class Procesador extends HttpServlet {

    private static final long serialVersionUID = 2L;

    private static final Map<String, Proceso> PROCESOS = new CaseInsensitiveMap<Proceso>();

    private static String mode = "";

    static {
        mode = System.getProperty("fitbank.test.mode", "");
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        Locale.setDefault(new Locale(ResourceBundle.getBundle("config")
                .getString("locale")));

        for (Proceso proceso : Servicios.load(Proceso.class)) {
            Handler handler = proceso.getClass().getAnnotation(Handler.class);

            if (handler != null) {
                Debug.debug("Registrando " + handler.value() + " ["
                        + proceso.getClass().getName() + "]");
                PROCESOS.put(handler.value(), proceso);
            } else {
                Debug.info("La clase " + proceso.getClass().getName()
                        + " no tiene anotacion @Handler");
            }
        }

        super.init(config);
    }

    @Override
    protected void service(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws ServletException,
            IOException {
        try {
            httpServletRequest.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            // No debería fallar, si no hay soporte de utf-8 estamos mal
            throw new RuntimeException(ex);
        }

        PedidoWeb pedido = null;
        RespuestaWeb respuesta = null;

        Proceso proceso = null;
        try {
            EntornoWeb.init(httpServletRequest);
            pedido = new PedidoWeb(httpServletRequest, httpServletResponse);

            // Obtener proceso
            proceso = PROCESOS.get(pedido.getTipoPedido());

            if (proceso == null) {
                throw new ErrorWeb("No existe manejador para el proceso: "
                        + pedido.getTipoPedido());
            }

            if (proceso.getClass().isAnnotationPresent(RevisarSeguridad.class)) {
                revisarSeguridad(pedido);
                revisarInactividad(pedido);
                completarPedido(pedido);
            }

            Registro.crearRegistro(pedido.getTipoPedido());
            Registro.getRegistro().salvarRequest(httpServletRequest);
            EntornoWeb.getContexto().getPaginacion().setPaginacion(pedido);

            // Enviar pedido al proceso elegido
            respuesta = proceso.procesar(pedido);

            if (respuesta != null) {
                respuesta.escribir();
            } else {
                throw new ErrorWeb("No hay respuesta!");
            }
        } catch (Throwable t) {
            try {
                Registro.getRegistro().setEstado(
                        Registro.Estado.ERROR.toString());
                Registro.getRegistro().salvarDatosActuales(pedido.getTransporteDB());

                respuesta = ManejoExcepcion.procesar(proceso, pedido,
                        respuesta, httpServletResponse, t);

                respuesta.escribir();
            } catch (Throwable t1) {
                Debug.error("ERROR TOTAL", t1);
                Debug.error("ERROR ORIGINAL", t);
            }
        }

        Registro.getRegistro().salvarResponse(httpServletResponse);
    }

    /**
     * Verificar si existe un transporte de datos completos antes de enviar
     * el mensaje al servicio de destino.
     * 
     * @param pedido PedidoWeb
     * @throws SecurityException 
     */
    public void revisarSeguridad(PedidoWeb pedido) throws SecurityException {
        if (!EntornoWeb.existeUsuario()) {
            Debug.debug("La sesión " + EntornoWeb.getSessionId() + " no fue encontrada en memoria");

            String requestUser = pedido.getValorRequestHttp("_user");
            boolean doSilentLogin = ParametrosWeb.getValueBoolean(Procesador.class, "silentLogin");
            if (StringUtils.isNotBlank(requestUser) && doSilentLogin) {
                Debug.debug("Intentando un Login automático para el usuario " + requestUser + " ...");

                //Crear un pedido para login
                PedidoWeb pedidoLogin = new PedidoWeb(pedido.getHttpServletRequest(), pedido.getHttpServletResponse());

                //Setar los datos minimos necesarios para el transportedb
                pedidoLogin.getTransporteDB().setUser(requestUser);
                pedidoLogin.getTransporteDB().setSessionId("WEB3SESSION:".concat(EntornoWeb.getSessionId()));

                //Setar los atributos necesarios para el proceso
                Map<String, List<String>> mValues = pedido.getValoresRequestHttp();
                mValues.put("cierre", Arrays.asList(new String[]{"1"}));
                pedidoLogin.setValoresRequestHttp(mValues);

                //Cargar el proceso según su ID y ejecutarlo
                Proceso p = PROCESOS.get("sig_sesion");
                p.procesar(pedidoLogin);

                //Completar datos comunes de la cabecera del transportedb
                this.completarPedido(pedido);

                Debug.debug("Login exitoso para el usuario " + requestUser + "!");
            }

            if (!EntornoWeb.existeUsuario()) {
                throw new SecurityException("Se han perdido los datos su actividad web. Favor vuelva a iniciar sesión");
            }
        }
    }

    /**
     * Verificar si el contexto padre de un pedido, aún existe. Esto puede
     * pasar, cuando se supera el límite de contextos creados por sesión.
     *
     * @param pedido Pedido Web
     * @throws ErrorWeb En caso de no existir el contexto
     */
    public void revisarInactividad(PedidoWeb pedido) throws ErrorWeb {
        String contextoPadre = pedido.getValorRequestHttp("_contexto_padre");
        if (StringUtils.isNotBlank(contextoPadre)) {
            Contexto contexto = EntornoWeb.getDatosSesion().getContextos().get(contextoPadre);
            if (contexto == null) {
                throw new ErrorWeb("El tiempo de actividad de su formulario ha caducado. Por favor recárguelo");
            }
        }

        //Mensajes básicos siempre deben haber tenido un WebPage ya cargado previamente
        //En caso de haberlo perdido en el contexto, recargarlo y sincronizar sus datos
        if (EntornoWeb.getContexto().getWebPage() == null
                && GeneralRequestTypes.requiresWebPage(pedido.getTipoPedido())) {
            try {
                //Omitir cargar formularios de seguridades del sistema
                String subs = pedido.getTransporteDB().getSubsystem();
                String tran = pedido.getTransporteDB().getTransaction();
                String uri = subs.concat(tran);
                if ("010000".equals(uri)
                        || "010001".equals(uri)
                        || "010328".equals(uri)
                        || "010329".equals(uri)) {
                    return;
                }

                Debug.debug("Intentado recargar el webpage " + uri);
                PedidoWeb pedidoWp = new PedidoWeb(pedido.getHttpServletRequest(), pedido.getHttpServletResponse());

                //Cargar el proceso según su ID y ejecutarlo
                Proceso p = PROCESOS.get("form");
                p.procesar(pedidoWp);
                pedido.sync(EntornoWeb.getContexto());
                Debug.debug("Webpage " + uri + " cargado exitosamente!");
            } catch (Exception e) {
                Debug.error("Excepción al recargar un webpage por inactividad", e);
            } catch (Error e) {
                Debug.error("Error al recargar un webpage por inactividad", e);
            }

            if (GeneralRequestTypes.requiresQuery(pedido.getTipoPedido())) {
                throw new ErrorWeb("Ha expirado el tiempo de validez de los datos del formulario. Favor respalde sus datos y reconsulte");
            }
        }
    }

    /**
     * Completa los campos comunes del transportedb en caso de haberlos perdido
     * 
     * @param pedido PedidoWeb
     */
    private void completarPedido(PedidoWeb pedido) {
        if (StringUtils.isBlank(pedido.getTransporteDB().getUser())) {
            //Indicar que el transporteweb debe ser refrescado en el JSON de respuesta
            pedido.setRecargarDB(true);

            //Armar el nuevo transporte web con los valores originales
            TransporteDB transporte = Clonador.clonar(EntornoWeb.getTransporteDBBase());
            transporte.setSubsystem(pedido.getTransporteDB().getSubsystem());
            transporte.setTransaction(pedido.getTransporteDB().getTransaction());
            transporte.setMessageId(pedido.getTransporteDB().getMessageId());
            transporte.clean();
            transporte.cleanResponse();

            //Actualizar el transportedb
            pedido.setTransporteDB(transporte);
        }
    }

    public static String getMode() {
        return mode;
    }

    public static void setMode(String mode) {
        Procesador.mode = mode;
    }

}
