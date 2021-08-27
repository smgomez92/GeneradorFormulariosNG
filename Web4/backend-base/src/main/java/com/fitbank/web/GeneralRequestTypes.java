package com.fitbank.web;

public final class GeneralRequestTypes {

    public static final String NAMES = "names";
    public static final String INGRESO = "sig";
    public static final String INGRESO_SESION = "sig_sesion";
    public static final String CLAVE = "clave";

    public static final String VARIABLES_CSS = "variables.css";
    public static final String CLASES_JS = "clases.js";
    public static final String MENSAJES_JS = "mensajes.js";
    public static final String PARAMETROS_JS = "parametros.js";

    public static final String INF = "inf";
    public static final String MENU = "menu";
    public static final String NOTIF = "notif";
    public static final String NOTIFCOM = "notifcomentarios";

    public static final String FORM = "form";
    public static final String RECARGAR = "recargar";
    public static final String ADJUNTAR = "adjuntar";
    public static final String LIMPIAR = "limpiar";
    public static final String BORRAR_CACHE = "cache";
    public static final String CONSULTA = "con";
    public static final String SUBIR = "subir";
    public static final String MANTENIMIENTO = "man";
    public static final String REPORTE = "rep";
    public static final String LV = "lv";
    public static final String IMG = "img";
    public static final String IMG_DISCO = "img_disco";
    public static final String FILE = "file";
    public static final String HUELLA = "huella";
    public static final String REPORTE_PENTAHO = "rep_pentaho";
    public static final String MANUAL_TECNICO = "manual_tecnico";

    public static final String CADUCAR = "cad";

    public static final String LOG = "log";
    public static final String LOG_MENSAJES = "log_mensajes";

    public static final String REGISTRO = "registro";
    public static final String REPORTE_DETAILS = "reporte_details";
    public static final String PROCESAR = "procesar";

    public static final String ERROR = "error";

    private GeneralRequestTypes() {
    }

    public static boolean requiresWebPage(String tipo) {
        return GeneralRequestTypes.CONSULTA.equals(tipo)
                || GeneralRequestTypes.MANTENIMIENTO.equals(tipo)
                || GeneralRequestTypes.REPORTE.equals(tipo)
                || GeneralRequestTypes.SUBIR.equals(tipo);
    }

    public static boolean requiresQuery(String tipo) {
        return GeneralRequestTypes.MANTENIMIENTO.equals(tipo)
                || GeneralRequestTypes.SUBIR.equals(tipo);
    }
}
