package com.fitbank.web.procesos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.HttpUtil;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.RegistroMap;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.servlets.Procesador;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Handler(GeneralRequestTypes.REGISTRO)
public class Registro implements Proceso {

    public static final Map<Integer, RegistroWeb> REGISTROS = Collections.
            synchronizedMap(new RegistroMap<Integer, RegistroWeb>());

    public static final String IGNORED_PATHS = "inf|notif|names|variables.css|" +
            "clases.js|menu|parametros.js|mensajes.js|reporte_details|log_mensajes";

    public static final File PATH_BASE;

    static {
        try {
            PATH_BASE = File.createTempFile("registro", "web");
            PATH_BASE.delete();
            PATH_BASE.mkdirs();
            PATH_BASE.deleteOnExit();
        } catch (IOException e) {
            throw new ErrorWeb(e);
        }

        if (!habilitado()) {
            Debug.info("Se ha desactivado el registro de transacciones.");
        }
    }

    public boolean filtrar(PedidoWeb pedido, String parametro, Object valor) {
        String filtro = pedido.getValorRequestHttp(parametro);
        String valorString = String.valueOf(valor).toLowerCase();

        if (StringUtils.isNotEmpty(filtro) && !valorString.contains(filtro.toLowerCase())) {
            return true;
        }

        return false;
    }

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        boolean registrar = habilitado();

        if (!registrar) {
            RespuestaWeb response = new RespuestaWeb(pedido);
            JSONObject params = consultarRegistro(pedido);
            if (params == null) {
                params = new JSONObject();
            }

            params.put("disabled", true);
            response.setContenido(params);
            return response;
        }

        String url = pedido.getHttpServletRequest().getPathInfo();

        if (url.length() > 0 && url.substring(1).contains("/")) {
            if (registrar) {
                String fileName = url.substring(url.lastIndexOf("/") + 1);
                return obtenerArchivo(pedido, fileName);
            } else {
                return new RespuestaWeb(pedido);
            }
        } else {
            if (registrar) {
                // HABILITAR MODO DE TEST
                String mode = pedido.getValorRequestHttp("mode");
                Procesador.setMode(mode);

                // HABILITAR MODO DEBUG
                String debug = pedido.getValorRequestHttp("debug");
                EntornoWeb.getDatosSesion().setDebug(StringUtils.isNotBlank(
                        debug));
            }

            RespuestaWeb respuesta = new RespuestaWeb(pedido);
            JSONObject registrosJSON = consultarRegistro(pedido);
            if (registrosJSON != null) {
                respuesta.setContenido(registrosJSON);
            }

            return respuesta;
        }
    }

    private RespuestaWeb obtenerArchivo(PedidoWeb pedido, String fileName) {
        RespuestaWeb respuesta = new RespuestaWeb(pedido);

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(PATH_BASE, fileName));
            respuesta.setContenido(IOUtils.toByteArray(fis));
        } catch (IOException e) {
            throw new ErrorWeb(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new ErrorWeb(e);
                }
            }
        }

        if (fileName.endsWith(".xml")) {
            respuesta.setContentType("text/xml");
        } else {
            respuesta.setContentType("text/plain");
        }

        return respuesta;
    }

    private JSONObject consultarRegistro(PedidoWeb pedido) {
        int secuencia = pedido.getValorRequestHttpInt("secuencia");
        int maximo = pedido.getValorRequestHttpInt("numero");
        String mySession = pedido.getValorRequestHttp("sesion");

        if (!habilitado() && secuencia == -1) {
            return null;
        }

        JSONArray jsonArray = new JSONArray();

        synchronized (REGISTROS) {
            List<RegistroWeb> lRegistros = new ArrayList<RegistroWeb>(REGISTROS.values());
            Collections.reverse(lRegistros);
            registros:
            for (RegistroWeb registroWeb : lRegistros) {
                DynaBean db = new WrapDynaBean(registroWeb);
                for (DynaProperty dp : db.getDynaClass().getDynaProperties()) {
                    if (filtrar(pedido, dp.getName(), db.get(dp.getName()))) {
                        continue registros;
                    }
                }

                if (StringUtils.isBlank(pedido.getValorRequestHttp("tipo"))
                        && registroWeb.getTipo().toLowerCase().
                        matches(IGNORED_PATHS)) {
                    continue;
                }

                if ("true".equals(mySession) &&
                    !EntornoWeb.getSessionId().equals(registroWeb.getSessionId())) {
                    continue;
                }

                if (secuencia == -1 || secuencia == registroWeb.getSecuencia()) {
                    jsonArray.element(registroWeb);
                }

                if (--maximo == 0) {
                    break;
                }
            }
            Collections.reverse(lRegistros);
        }

        JSONObject registrosJSON = new JSONObject();

        registrosJSON.put("registros", jsonArray);
        registrosJSON.put("debug", EntornoWeb.getDatosSesion().isDebug());
        registrosJSON.put("mode", Procesador.getMode());

        return registrosJSON;
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        // Se usa el manejo por default de errores
    }

    public static void crearRegistro(String tipoPedido) {
        if (tipoPedido.equals(GeneralRequestTypes.REGISTRO)
                || tipoPedido.equals(GeneralRequestTypes.INF)) {
            return;
        }

        if (EntornoWeb.getSecuencia() != -1) {
            throw new ErrorWeb(
                    "Solo se puede llamar una vez a Registro.crearRegistro");
        }

        synchronized (REGISTROS) {
            List<Integer> l = new ArrayList<Integer>();
            l.addAll(REGISTROS.keySet());

            int secuencia = l.isEmpty() ? 0 : l.get(l.size() - 1) + 1;
            REGISTROS.put(secuencia, new RegistroWeb(secuencia, tipoPedido));
            EntornoWeb.setSecuencia(secuencia);
        }
    }

    public static RegistroWeb getRegistro() {
        if (EntornoWeb.getSecuencia() == -1) {
            return new RegistroWeb();
        }

        RegistroWeb registro = REGISTROS.get(EntornoWeb.getSecuencia());
        if (registro == null) {
            registro = new RegistroWeb(EntornoWeb.getSecuencia(), null);
        }

        return registro;
    }

    /**
     * Indica si el registro está habilitado. El estado del registro está
     * controlado por la propiedad com.fitbank.web.procesos.Registro.registrar
     * en parametros.properties.
     * @return Devuelve true si el registro está habilitado.
     */
    public static boolean habilitado() {
        return ParametrosWeb.getValueBoolean(Registro.class, "registrar");
    }

    /**
     * Indica el número máximo de registros que se pueden almacenar simultáneamente
     * 
     * @return Número máximo de registros a almacenar
     */
    public static Integer getMaximoRegistros() {
        return Integer.valueOf(ParametrosWeb.getValueString(Registro.class, "maximo"));
    }

    public enum Estado {

        NINGUNO, INICIALIZADO, ENVIADO, RECIBIDO, COMPLETADO, ERROR;

    }

    @Data
    public static class RegistroWeb {

        private int secuencia = -1;

        private final String fecha = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss").format(new Date());

        private final String tipo;

        private final String thread;

        private String usuario = null;

        private String cia = null;

        private String contexto = null;

        private String tran = null;

        private String url = null;

        private String estado = Estado.NINGUNO.toString();

        private String codigoError = "0";

        private String mensajeError = "";

        private String stackTrace = null;

        @Setter(AccessLevel.NONE)
        private String sessionId = null;

        protected RegistroWeb() {
            this.tipo = null;
            this.thread = Thread.currentThread().getName();
            this.sessionId = EntornoWeb.getSessionId();
        }

        public RegistroWeb(int secuencia, String tipoPedido) {
            this.secuencia = secuencia;
            this.usuario = EntornoWeb.getTransporteDBBase().getUser();
            this.cia = EntornoWeb.getTransporteDBBase().getCompany();
            this.contexto = EntornoWeb.getIdContexto();
            this.tipo = tipoPedido;
            this.thread = Thread.currentThread().getName();
            this.sessionId = EntornoWeb.getSessionId();
        }

        public String getNombreBase() {
            return String.format("%s-%s-%s-%s-%s-%s", secuencia, cia, usuario,
                    contexto, tran, tipo);
        }

        public void salvarRequest(HttpServletRequest req) {
            if (habilitado() && secuencia != -1) {
                if (this.usuario == null) {
                    this.usuario = req.getParameter("usr");
                }

                if (req.getQueryString() != null) {
                    setUrl(req.getRequestURI() + "?" + req.getQueryString());
                } else {
                    setUrl(req.getRequestURI());
                }

                guardar(getNombreBase() + "-request.txt", HttpUtil.
                        formatHttpServletRequest(req));

                if (!Estado.ERROR.toString().equals(estado)) {
                    estado = Estado.INICIALIZADO.toString();
                }
            }
        }

        public void salvarDatosEntrada(TransporteDB transporte) {
            if (habilitado() && secuencia != -1) {
                String xml = transporte.toString();
                setTran(transporte.getSubsystem() + transporte.getTransaction());
                guardar(getNombreBase() + "-entrada.xml", xml);

                if (!Estado.ERROR.toString().equals(estado)) {
                    estado = Estado.ENVIADO.toString();
                }
            }
        }

        public void salvarDatosSalida(TransporteDB datos) {
            if (habilitado() && secuencia != -1) {
                String xml = datos.toString();
                guardar(getNombreBase() + "-salida.xml", xml);

                if (!Estado.ERROR.toString().equals(estado)) {
                    estado = Estado.RECIBIDO.toString();
                }
            }
        }

        public void salvarDatosActuales(TransporteDB datos) {
            File detailEntrada = new File(getNombreBase() + "-entrada.xml");
            if (detailEntrada.exists()) {
                return;
            }

            salvarDatosEntrada(datos);
        }

        public void salvarResponse(HttpServletResponse res) {
            if (habilitado() && secuencia != -1) {
                guardar(getNombreBase() + "-response.txt", HttpUtil.
                        formatHttpServletResponse(res));

                if (!Estado.ERROR.toString().equals(estado)) {
                    estado = Estado.COMPLETADO.toString();
                }
            }
        }

        private static void guardar(String nombre, String contenido) {
            try {
                FileOutputStream fos = new FileOutputStream(new File(PATH_BASE,
                        nombre));
                fos.write(contenido.getBytes("UTF-8"));
                fos.close();

            } catch (IOException e) {
                Debug.error("No se pudo guardar el archivo: " + nombre, e);
            }
        }

        public void eliminar() {
            final String prefix = String.format("%s-", secuencia);
            final File[] files = PATH_BASE.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(final File dir, final String name) {
                    return name.startsWith(prefix);
                }
            });

            for (final File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                    Debug.error("No se pudo eliminar el archivo: " + file.getAbsolutePath(), e);
                }
            }
        }
    }

}
