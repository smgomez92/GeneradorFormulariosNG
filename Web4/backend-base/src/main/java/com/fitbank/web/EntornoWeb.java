package com.fitbank.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import com.fitbank.util.Clonador;
import com.fitbank.util.Debug;
import com.fitbank.web.data.Paginacion;
import com.fitbank.web.db.TransporteDB;

public final class EntornoWeb {

    // PROPIEDADES Y METODOS DE LA INSTANCIA
    private final HttpSession session;

    private final String idContexto;

    private int secuencia = -1;

    private final HttpServletRequest httpServletRequest;

    private EntornoWeb(HttpServletRequest req) {
        this.httpServletRequest = req;
        this.session = req.getSession();

        if (req.getParameterMap().containsKey("_contexto")) {
            this.idContexto = req.getParameter("_contexto");
        } else {
            this.idContexto = "default";
        }
    }

    // PROPIEDADES Y METODOS ESTATICOS
    private static final String DATOS_SESION = "DATOS_SESION";

    private static final ThreadLocal<EntornoWeb> entornoWeb = new ThreadLocal<EntornoWeb>() {

        @Override
        protected EntornoWeb initialValue() {
            return null;
        }

    };

    public static final String URI_INGRESO = "ingreso.html";

    public static final String URI_CLAVE = "clave.html";

    public static final String URI_ENTORNO = "entorno.html";

    public static void init(HttpServletRequest req) {
        entornoWeb.set(new EntornoWeb(req));

        // Reinicializar en caso de un pedido de ingreso
        String pathInfo = StringUtils.trimToEmpty(req.getPathInfo());
        String names = "/" + GeneralRequestTypes.NAMES;
        if (pathInfo.equalsIgnoreCase(names) && req.getParameter("_reset") != null) {
            setDatosSesion(null);
        }
    }

    public static HttpSession getSession() {
        return entornoWeb.get().session;
    }

    public static HttpServletRequest getHttpServletRequest() {
        return entornoWeb.get().httpServletRequest;
    }

    public static void setSecuencia(int secuencia) {
        entornoWeb.get().secuencia = secuencia;
    }

    public static void setDatosSesion(DatosSesion datosSesion) {
        getSession().setAttribute(DATOS_SESION, datosSesion);
    }

    public static DatosSesion getDatosSesion() {
        if (getSession().getAttribute(DATOS_SESION) == null) {
            setDatosSesion(new DatosSesion());
        }

        return (DatosSesion) getSession().getAttribute(DATOS_SESION);
    }

    public static int getSecuencia() {
        return entornoWeb.get().secuencia;
    }

    public static void setTransporteDBBase(TransporteDB transporteDBBase) {
        getDatosSesion().setTransporteDBBase(transporteDBBase);
    }

    public static TransporteDB getTransporteDBBase() {
        return getDatosSesion().getTransporteDBBase();
    }

    private static void setContexto(Contexto contexto) {
        getDatosSesion().getContextos().put(getIdContexto(), contexto);
    }

    /**
     * Obtiene el contexto para el thread actual o crea uno si es necesario.
     *
     * @return Contexto
     */
    public static Contexto getContexto() {
        return getContexto(getIdContexto());
    }

    /**
     * Obtiene el contexto o crea uno si es necesario.
     *
     * @param id String con el id del contexto a ser cargado
     *
     * @return Contexto
     */
    public static Contexto getContexto(String id) {
        Contexto contexto = getDatosSesion().getContextos().get(id);

        if (contexto == null) {
            Debug.debug("Creando contexto " + id);
            contexto = new Contexto(id);
            setContexto(contexto);
            contexto.setPaginacion(new Paginacion());
            contexto.setTransporteDBBase(Clonador.clonar(getTransporteDBBase()));
            contexto.getTransporteDBBase().clean();
            contexto.getTransporteDBBase().cleanResponse();

            if (EntornoWeb.isThreadSessionActive()) {
                Debug.debug("Contextos registrados para " + EntornoWeb.getSessionId()
                        + " => [" + StringUtils.join(getDatosSesion()
                                .getContextos().keySet(), ", ") + "]");
            }
        }

        return contexto;
    }

    public static void resetContextos() {
        Debug.debug("Limpiando contextos");
        getDatosSesion().getContextos().clear();
    }

    public static void cambiarCompania(String compania) {
        for (Contexto contexto : getDatosSesion().getContextos().values()) {
            contexto.getTransporteDBBase().setCompany(compania);
        }

        getTransporteDBBase().setCompany(compania);
    }

    public static String getIdContexto() {
        return entornoWeb.get().idContexto;
    }

    public static String getSessionId() {
        return entornoWeb.get().session.getId();
    }

    public static boolean isThreadSessionActive() {
        return entornoWeb.get() != null
                && entornoWeb.get().session != null;
    }

    public static boolean existeUsuario() {
        return StringUtils.isNotBlank(getDatosSesion().getTransporteDBBase().getUser());
    }

    public static boolean isKeepContextClean() {
        return ParametrosWeb.getValueBoolean(EntornoWeb.class, "keepContextClean");
    }

    public static Integer getMaxContexts() {
        return Integer.valueOf(ParametrosWeb.getValueString(EntornoWeb.class, "maxContextsToKeep"));
    }
}
