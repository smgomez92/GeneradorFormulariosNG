package com.fitbank.web.data;

import java.util.Date;

/**
 * Clase que se encarga del manejo de notificaiones
 * 
 * @author
 */
public class NotificationItem {

    private int registro;

    private String subsistema;

    private String transaccion;

    private String version;

    private String mensaje;

    private Date fechaNotificacion;

    private String numeroMensaje;

    private Date fechaProceso;

    private  String ccuenta;

    private  String identificacion;

    public String getCcuenta() {
        return ccuenta;
    }

    public void setCcuenta(String ccuenta) {
        this.ccuenta = ccuenta;
    }
    public String getIdentificacion() {
        return identificacion;
    }
    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public Date getFechaNotificacion() {
        return fechaNotificacion;
    }

    public void setFechaNotificacion(Date fechaNotificacion) {
        this.fechaNotificacion = fechaNotificacion;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getRegistro() {
        return registro;
    }

    public void setRegistro(int registro) {
        this.registro = registro;
    }

    public String getSubsistema() {
        return subsistema;
    }

    public void setSubsistema(String subsistema) {
        this.subsistema = subsistema;
    }

    public String getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(String transaccion) {
        this.transaccion = transaccion;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getNumeroMensaje() {
        return numeroMensaje;
    }

    public void setNumeroMensaje(String numeroMensaje) {
        this.numeroMensaje = numeroMensaje;
    }

    public Date getFechaProceso() {
        return fechaProceso;
    }

    public void setFechaProceso(Date fechaProceso) {
        this.fechaProceso = fechaProceso;
    }
}