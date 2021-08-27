/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fitbank.webpages.assistants;

import com.fitbank.enums.DataSourceType;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.data.FormElement;
import java.util.Collection;

/**
 *
 * @author santy
 */
public class ProgressBar implements Assistant {

    public enum ModeTypes {
        DETERMINATE_MODE, INDETERMINATE_MODE, BUFFER_MODE, QUERY_MODE;

    }

    @Editable
    private  ModeTypes modeType = ModeTypes.QUERY_MODE;
    @Editable(weight = 1)
    private int valor = 0;
    @Editable(weight = 1)
    private String formula = "";

    @Editable(weight = 1)
    private String message = "";
    private String modo = "indeterminate";

    public ModeTypes getModeType() {
        return modeType;
    }

    public void setModeType(ModeTypes modeType) {
        switch (modeType) {
            case QUERY_MODE:

                this.setModo("query");
                break;

            case INDETERMINATE_MODE:

                this.setModo("indeterminate");
                break;
            case BUFFER_MODE:

                this.setModo("buffer");
                break;
            case DETERMINATE_MODE:

                this.setModo("determinate");
                break;
        }
        this.modeType = modeType;

    }

    /*public ModeTypes getModeType() {
        System.out.println("Mode: " + modeType.getNombre());
        return modeType;
    }

    public void setValidationType(ModeTypes modeType) {
         System.out.println("Mode: " + modeType.getNombre());
        this.modeType = modeType;
        System.out.println("Mode: " + this.modeType.getNombre());
    }*/
    public String getModo() {
        return modo;
    }

    public void setModo(String modo) {
        this.modo = modo;
    }

    public ProgressBar() {
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void init(FormElement formElement) {

    }

    @Override
    public String format(String unformatedValue) {
        return unformatedValue;
    }

    @Override
    public String unformat(String formatedValue) {
        return formatedValue;
    }

    @Override
    public Object asObject(String value) {
        return null;
    }

    @Override
    public boolean readFromHttpRequest() {
        return false;
    }

    @Override
    public boolean usesIcon() {
        return false;
    }

    @Override
    public Collection<DataSourceType> applyTo() {
        return null;
    }

    @Override
    public String getElementName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getType() {
        return "text";
    }

    @Override
    public void generateHtml(ConstructorHtml html) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        html.abrir("span");
        html.setAtributo("asterisco--ngIf", getFormula());
        html.setTexto("Proceso...");
        html.cerrar("span");
        html.abrirNg("mat-progress-bar");
        html.setAtributo("asterisco--ngIf", getFormula());
        html.setAtributo("mode", "indeterminate");
        html.setTexto(" ");
        html.cerrar("mat-progress-bar");

    }
}
