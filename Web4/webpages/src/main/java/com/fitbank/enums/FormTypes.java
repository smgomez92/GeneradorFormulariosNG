/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fitbank.enums;

/**
 *
 * @author santy
 */
public enum FormTypes {

    IDFORM("id", "idForm()", "pattern-required", "idiomas.CedulaInv-idiomas.msjIdRequerido"), COMBOFORM("combo", "comboForm()", "pattern", "idiomas.ValidarCampo"), FECHAFORM("fecha", "fechaForm()", "pattern", "idiomas.ValidarCampo"), TEXTFORM("texto", "textForm()", "pattern-required", "idiomas.msjSoloTexto-idiomas.ValidarCampo"),
    NUMEROFORM("numero", "numberForm()", "pattern-required", "idiomas.msjSoloNumeros-idiomas.ValidarCampo"), ALFANUMERICOFORM("textoynumero", "textNumberForm()", "required", "idiomas.ValidarCampo"), NONE("", "noRequired()", "", "");
    private String nombre;
    private String metodo;
    private String tipos;
    private String msjs;

    private FormTypes(String nombre, String metodo, String tipos, String msjs) {
        this.nombre = nombre;
        this.metodo = metodo;
        this.tipos = tipos;
        this.msjs = msjs;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public String getTipos() {
        return tipos;
    }

    public void setTipos(String tipos) {
        this.tipos = tipos;
    }

    public String getMsjs() {
        return msjs;
    }

    public void setMsjs(String msjs) {
        this.msjs = msjs;
    }

    public static FormTypes getFormType(String nombre) {
        for (FormTypes formType : FormTypes.values()) {
            if (formType.getNombre().equals(nombre)) {
                return formType;
            }
        }
        //alerta
        return FormTypes.IDFORM;
    }

}
