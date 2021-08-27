package com.fitbank.web.data;

import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;

import com.fitbank.common.Uid;
import com.fitbank.enums.TipoMenu;
import com.fitbank.util.Clonador;
import com.fitbank.util.Debug;
import com.fitbank.web.Contexto;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.WebPageUtils;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.widgets.DeleteRecord;
import javax.servlet.ServletContext;

/**
 * Maneja un pedido web. Envuelve el HttpServletRequest y el
 * HttpServletResponse.
 *
 * @author FitBank
 * @version 2.0
 */
public class PedidoWeb extends DatosWeb {

    private static final long serialVersionUID = 1L;

    private static final String FORWARD_ADDRESS_HEADER
            = ParametrosWeb.getValueString(PedidoWeb.class, "forwardAddressHeader");

    private static final Boolean USE_LOCAL_IP_ADDRESS
            = ParametrosWeb.getValueBoolean(PedidoWeb.class, "useLocalIpAddress");

    private TipoMenu tipoMenu = TipoMenu.NINGUNO;

    private Map<String, List<String>> valoresRequestHttp
            = new HashMap<String, List<String>>();

    private HttpServletRequest httpServletRequest;

    private HttpServletResponse httpServletResponse;

    private ServletContext servletContext;

    public PedidoWeb() {
    }

    @SuppressWarnings("unchecked")
    public PedidoWeb(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) {
        setHttpServletRequest(httpServletRequest);
        setHttpServletResponse(httpServletResponse);

        // HABILITAR O DESHABILITAR DEBUG
        WebPageEnviroment.setDebug(EntornoWeb.getDatosSesion().isDebug());

        // LEER VALORES DEL REQUEST Y CREAR UN MAPA
        valoresRequestHttp.putAll(PedidoWeb.httpRequestParamsToMap(httpServletRequest));

        // SOBREESCRIBIR VALORES QUE PODRIAN ESTAR EN SESSION
        // En este punto se puede cambiar la compania del contexto
        if (StringUtils.isNotBlank(getValorRequestHttp("_cia"))) {
            EntornoWeb.cambiarCompania(getValorRequestHttp("_cia"));
        }

        // CAMBIAR VALORES DEL CONTEXTO
        if (StringUtils.isNotBlank(getValorRequestHttp("_subs"))) {
            EntornoWeb.getContexto().getTransporteDBBase().setSubsystem(
                    getValorRequestHttp("_subs"));
        }

        if (StringUtils.isNotBlank(getValorRequestHttp("_tran"))) {
            EntornoWeb.getContexto().getTransporteDBBase().setTransaction(
                    getValorRequestHttp("_tran"));
        }

        if (StringUtils.isNotBlank(getValorRequestHttp("_version"))) {
            EntornoWeb.getContexto().getTransporteDBBase().setVersion(
                    getValorRequestHttp("_version"));
        }

        // INICIALIZAR UN NUEVO TRANSPORTEDB PARA EL PROCESO
        setTransporteDB(Clonador.clonar(EntornoWeb.getContexto().
                getTransporteDBBase()));
        getTransporteDB().cleanResponse();
        getTransporteDB().setSessionId(EntornoWeb.getSessionId());
        getTransporteDB().setMessageId(Uid.getString());
        getTransporteDB().setIpAddress(this.getIpAddress(httpServletRequest));

        if (httpServletRequest.getServletPath().matches("^/.+/proc/.+")) {
            String tipo = httpServletRequest.getPathInfo().substring(1);
            setTipoPedido(tipo);
        } else if (httpServletRequest.getPathInfo() != null) {
            String[] partes = httpServletRequest.getPathInfo().split("/", 3);
            setTipoPedido(partes[1]);
            if (partes.length == 3) {
                setExtraTipo(partes[2]);
            }
        } else {
            throw new ErrorWeb("No se especific� tipo de pedido");
        }

        // LEER VALORES DE LOGIN, SOLO SI ES LOGIN
        if (getTipoPedido().equalsIgnoreCase(GeneralRequestTypes.INGRESO)
                && EntornoWeb.getTransporteDBBase().getUser() == null) {
            getTransporteDB().setUser(
                    getValorRequestHttp(EntornoWeb.getDatosSesion().
                            getNameUsuario()));
            getTransporteDB().setPassword(
                    getValorRequestHttp(
                            EntornoWeb.getDatosSesion().getNameClave()));

            // Solo se puede usar una vez estos names.
            EntornoWeb.getDatosSesion().setNameUsuario(null);
            EntornoWeb.getDatosSesion().setNameClave(null);
            EntornoWeb.getDatosSesion().setNameClave2(null);
        }

        // LEER VALORES DE CAMBIO DE CLAVE, SOLO SI ES CAMBIO
        if (getTipoPedido().equalsIgnoreCase(GeneralRequestTypes.CLAVE)) {
            // Solo se puede usar una vez estos names.
            // Las claves se borran en el proceso de cambio de clave.
            EntornoWeb.getDatosSesion().setNameUsuario(null);
        }

        sync(EntornoWeb.getContexto());
        leerValoresNavegacion();
    }

    /**
     * Lee los valores que vienen en el request y los pone en el WebPage del
     * contexto especificado
     *
     * @param contexto Contexto donde se va a guardar los valores
     */
    public void sync(Contexto contexto) {
        leerValoresRequest(contexto);
    }

    public final String getValorRequestHttp(String nombre) {
        return valoresRequestHttp.get(nombre) != null ? valoresRequestHttp.get(
                nombre).get(0) : null;
    }

    public final int getValorRequestHttpInt(String name) {
        String value = getValorRequestHttp(name);
        try {
            return Integer.parseInt(value);
        } catch (NullPointerException e) {
            Debug.debug("Valor no definido para " + name + ": " + value);
            return -1;
        } catch (NumberFormatException e) {
            Debug.debug("Valor no numerico para " + name + ": " + value);
            return -1;
        }
    }

    public Map<String, List<String>> getValoresRequestHttp() {
        return valoresRequestHttp;
    }

    public void setValoresRequestHttp(
            Map<String, List<String>> valoresRequestHttp) {
        this.valoresRequestHttp = valoresRequestHttp;
    }

    public TipoMenu getTipoMenu() {
        return tipoMenu;
    }

    public void setTipoMenu(TipoMenu tipoMenu) {
        this.tipoMenu = tipoMenu;
    }

    public void setTipoMenu(String tipoMenu) {
        if (StringUtils.isNotBlank(tipoMenu)) {
            try {
                setTipoMenu(TipoMenu.valueOf(tipoMenu));
            } catch (IllegalArgumentException e) {
                Debug.warn("Tipo menu incorrecto: " + tipoMenu,
                        e);
                setTipoMenu(TipoMenu.NINGUNO);
            }
        }
    }

    @SuppressWarnings("unused")
    private void leerValoresRequest(Contexto contexto) {
        actualizarValoresWebPage(contexto.getWebPage(), getValoresRequestHttp());
    }

    /**
     * Actualiza un WebPage en base a los valores enviados desde un Request Http
     * 
     * @param webPage WebPage actualmente cargado en el contexto
     * @param valoresRequest Valores del PedidoWeb
     */
    public static void actualizarValoresWebPage(WebPage webPage, Map<String, List<String>> valoresRequest) {
        if (webPage == null) {
            return;
        }

        for (Container container : webPage) {
            for (FormElement formElement : IterableWebElement.get(container,
                    FormElement.class)) {
                if (!formElement.getAssistant().readFromHttpRequest()) {
                    continue;
                }

                List<String> valores = valoresRequest.get(
                        formElement.getNameOrDefault());

                if (valores != null) {
                    Debug.debug("Leyendo valores para " + formElement.
                            getNameOrDefault() + " = " + valores);
                    WebPageUtils.unformat(formElement, valores);
                }
            }
            for (DeleteRecord deleteRecord : IterableWebElement.get(
                    container,
                    DeleteRecord.class)) {
                List<String> valores = valoresRequest.get(
                        deleteRecord.getNameOrDefault());

                if (valores != null) {
                    deleteRecord.getFieldData().setValues(valores);
                }
            }
        }
    }

    /**
     * Metodo que lee valores del request y crea un mapa
     * 
     * @param httpServletRequest Request
     * @return Mapa de valores del request
     */
    public static Map<String, List<String>> httpRequestParamsToMap(HttpServletRequest httpServletRequest) {
        Map<String, List<String>> valoresRequestHttp = new HashMap<String, List<String>>();
        Enumeration e = httpServletRequest.getParameterNames();

        while (e.hasMoreElements()) {
            String s = String.valueOf(e.nextElement());
            valoresRequestHttp.put(s, Arrays.asList(httpServletRequest.
                    getParameterValues(s)));
        }

        return valoresRequestHttp;
    }

    private void leerValoresNavegacion() {
        String action = StringUtils.defaultIfEmpty(
                getValorRequestHttp("_action"), "");

        getTransporteDB().getNavigation().setAction(action);

        String fields = getValorRequestHttp("_fields");
        getTransporteDB().getNavigation().getFields().clear();

        if (StringUtils.isNotBlank(fields)) {
            Debug.debug("Leyendo campos navegacion " + fields);

            JSONObject fieldsJSON = JSONObject.fromObject(fields);

            for (String field : (Set<String>) fieldsJSON.keySet()) {
                getTransporteDB().getNavigation().getFields().put(field, fieldsJSON.
                        getString(field));
            }
        }

        String values = getValorRequestHttp("_values");
        getTransporteDB().getNavigation().getValues().clear();

        if (StringUtils.isNotBlank(values)) {
            Debug.debug("Leyendo valores navegacion " + values);

            JSONObject valuesJSON = JSONObject.fromObject(values);

            for (String value : (Set<String>) valuesJSON.keySet()) {
                getTransporteDB().getNavigation().getValues().put(value, valuesJSON.
                        getString(value));
            }
        }
    }

    public final HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public final void setHttpServletRequest(
            HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public final HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public final void setHttpServletResponse(
            HttpServletResponse httpServletRsponse) {
        this.httpServletResponse = httpServletRsponse;
    }

    public final ServletContext getServletContext() {
        return servletContext;
    }

    public final void setServletContext(
            ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void redireccionar(String path) {
        try {
            if (!path.startsWith("http") && !path.startsWith("/")) {
                path = getHttpServletRequest().getContextPath() + "/" + path;
            }
            getHttpServletResponse().sendRedirect(path);
        } catch (IOException e) {
            throw new ErrorWeb(e);
        }
    }

    /**
     * Obtiene la ipaddress real de la peticion, en caso de provenir de un
     * balanceador de carga u otra interfaz sobre la peticion original.
     *
     * @param httpServletRequest peticion web
     * @return ipaddress real de la peticion
     */
    private String getIpAddress(HttpServletRequest httpServletRequest) {
        String headerAddress = httpServletRequest.getHeader(PedidoWeb.FORWARD_ADDRESS_HEADER);
        String remoteAddress = httpServletRequest.getRemoteAddr();
        String localAddress = PedidoWeb.USE_LOCAL_IP_ADDRESS ? getValorRequestHttp("_localip") : null;
        if (EntornoWeb.getDatosSesion() != null) {
            if (StringUtils.isNotBlank(localAddress)) {
                EntornoWeb.getDatosSesion().setIpaddress(localAddress);
            }

            localAddress = EntornoWeb.getDatosSesion().getIpaddress();
        }

        return StringUtils.isNotBlank(headerAddress) ? headerAddress : 
                StringUtils.isNotBlank(localAddress) ? localAddress : 
                remoteAddress;
    }
}
