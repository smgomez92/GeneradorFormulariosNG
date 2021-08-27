/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fitbank.webpages.widgets;

import com.fitbank.enums.EjecutadoPor;
import com.fitbank.enums.FormTypes;
import com.fitbank.enums.Modificable;
import com.fitbank.enums.Requerido;
import com.fitbank.js.FuncionJS;
import com.fitbank.js.JS;
import com.fitbank.js.LiteralJS;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadEnum;
import com.fitbank.propiedades.PropiedadEstilos;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadLista;
import com.fitbank.propiedades.PropiedadNumerica;
import com.fitbank.propiedades.PropiedadSeparador;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.SerializableXml;
import com.fitbank.serializador.xml.UtilXML;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.JSBehavior;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.WebPageEnviromentNG;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.None;
import com.fitbank.webpages.assistants.lov.LOVField;
import com.fitbank.webpages.data.FieldData;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author santy
 */
public class InputListOfValues extends Widget implements FormElement {

    private static final long serialVersionUID = 1L;

    @Editable(weight = 1)
    private final String title = "";

    @Editable(weight = 2)
    @XML(nombreSubitems = "reference")
    private final List<Reference> references = new LinkedList<Reference>();

    @Editable(weight = 3)
    @XML(nombreSubitems = "field")
    private final List<LOVField> fields = new LinkedList<LOVField>();

    @Editable(weight = 4)
    private final LiteralJS preQuery = new FuncionJS("", "registro");

    @Editable(weight = 5)
    private final LiteralJS callback = new FuncionJS("", "registro", "values");

    @Editable(weight = 7)
    private final String subsystem = "01";

    @Editable(weight = 8)
    private final String transaction = "0003";

    @Editable(weight = 9)
    private final String version = "01";

    @Editable(weight = 10)
    private final boolean head = true;

    @Editable(weight = 11)
    private final boolean registerEvents = true;

    @Editable(weight = 12)
    private final boolean visible = true;

    @Editable(weight = 13)
    private final boolean queryOnSuccess = false;

    @Editable(weight = 14)
    private final boolean multirecord = false;

    @Editable(weight = 15)
    private final int numberOfRecords;

    @Editable(weight = 16)
    private final boolean legacy;

    @Editable(weight = 17)
    private final String noDataMessage = "";

    @Editable(weight = 18)
    private final boolean initialQuery;

    @Editable(weight = 19)
    private final boolean callbackOnNoResults;

    @Editable(weight = 20)
    private final EjecutadoPor executedBy;

    @Editable(weight = 21)
    private final Integer timeout = 0;

    @Editable(weight = 22)
    private final boolean saveResponseInCache;
    
    private final static String FUNCTION_NAME_TEMPLATE = "parent.c.formulario.vars['%s_%s']";

    private int pagina;

    private FormElement formElement;

    public InputListOfValues() {
        this.saveResponseInCache = false;
        this.executedBy = EjecutadoPor.FORMULARIO;
        this.callbackOnNoResults = false;
        this.initialQuery = true;
        this.legacy = false;
        this.numberOfRecords = 10;
        //propiedades de la lista de valores 
        def("title", "");
        def("val", "");
        def("references", new PropiedadLista<>(0, new Reference()));
        def("fields", new PropiedadLista<>(0, new LOVField()));
        def("preQuery", new PropiedadJavascript(PropiedadJavascript.Tipo.SIMPLE));
        def("subs", "01");
        def("tran", "0003");
        def("ver", "01");
        def("head", true);
        def("registerEvents", true);
        def("vis", true);
        def("queryOnSuccess", false);
        def("multirecord", false);
        def("numberOfRecords", 10);
        def("legacy", false);
        def("noDataMessage", "");
        def("initialQuery", true);
        def("callbackOnNoResults", false);
        def("executedBy", EjecutadoPor.FORMULARIO);
        def("timeout", 10);
        def("callback", new PropiedadJavascript(PropiedadJavascript.Tipo.SIMPLE));
        //
        def("mod", Modificable.MODIFICABLE);
        //def("vis", true);
        def("cln", true);
        def("lon", 0);
        def("gia", "");
        def("req", Requerido.AUTOMATICO);
        def("jvs", new PropiedadJavascript(PropiedadJavascript.Tipo.EVENTOS));
        def("nameng", "");
        def("RecibirFoco", false);
        def("tipoFrm", FormTypes.ALFANUMERICOFORM);

        //
        def("cssClass", new PropiedadEstilos());
        def("x", 0);
        def("y", 0);
        def("z", 0);
        def("h", 0);
        def("w", 0);

        properties.get("w").setValorPorDefecto(150);

    }
//<editor-fold defaultstate="collapsed" desc="Getters y Setters ListOfValues">
    @Editable
    public String getValueInicial() {
        return ((PropiedadSimple) properties.get("val")).getValor();
    }

    public void setValueInicial(String valueInicial) {
        properties.get("val").setValor(valueInicial);
    }

    @Editable
    public String getTran() {
        return ((PropiedadSimple) properties.get("tran")).getValor();
    }

    public void setTran(String tran) {
        properties.get("tran").setValor(tran);
    }

    @Editable
    public String getNoDataMessage() {
        return ((PropiedadSimple) properties.get("noDataMessage")).getValor();
    }

    public void setNoDataMessage(String noDataMessage) {
        properties.get("noDataMessage").setValor(noDataMessage);
    }

    @Editable
    public String getSubs() {
        return ((PropiedadSimple) properties.get("subs")).getValor();
    }

    public void setSubs(String subs) {
        properties.get("val").setValor(subs);
    }

    @Editable
    public String getVer() {
        return ((PropiedadSimple) properties.get("ver")).getValor();
    }

    public void setVer(String ver) {
        properties.get("ver").setValor(ver);
    }

    @Editable
    public String getTxt() {
        return ((PropiedadSimple) properties.get("txt")).getValor();
    }

    public void setTxt(String txt) {
        properties.get("txt").setValor(txt);
    }

    @Editable
    public String getTitle() {
        return ((PropiedadSimple) properties.get("title")).getValor();
    }

    public void setTitle(String title) {
        properties.get("title").setValor(title);
    }

    public Collection<Reference> getReferences() {
        return ((PropiedadLista<Reference>) properties.get("references")).
                getList();
    }

    public Collection<LOVField> getFields() {
        return ((PropiedadLista<LOVField>) properties.get("fields")).
                getList();
    }

    public String getJavaScript() {
        return ((PropiedadJavascript) properties.get("preQuery")).getValorString();
    }

    public void setJavaScript(String preQuery) {
        properties.get("preQuery").setValor(preQuery);
    }

    public String getCallBack() {
        return ((PropiedadJavascript) properties.get("callback")).getValorString();
    }

    public void setCallBack(String callback) {
        properties.get("callback").setValor(callback);
    }

    @Editable
    public boolean getHead() {
        return ((PropiedadBooleana) properties.get("head")).getValor();
    }

    public void setHead(boolean head) {
        properties.get("head").setValor(head);
    }

    @Editable
    public boolean getRegisterEvents() {
        return ((PropiedadBooleana) properties.get("registerEvents")).getValor();
    }

    public void setRegisterEvents(boolean registerEvents) {
        properties.get("registerEvents").setValor(registerEvents);
    }

    @Editable
    public boolean getQueryOnSuccess() {
        return ((PropiedadBooleana) properties.get("queryOnSuccess")).getValor();
    }

    public void setQueryOnSuccess(boolean queryOnSuccess) {
        properties.get("queryOnSuccess").setValor(queryOnSuccess);
    }

    @Editable
    public boolean getMultirecord() {
        return ((PropiedadBooleana) properties.get("multirecord")).getValor();
    }

    public void setMultirecord(boolean multirecord) {
        properties.get("multirecord").setValor(multirecord);
    }

    @Editable
    public boolean getLegacy() {
        return ((PropiedadBooleana) properties.get("legacy")).getValor();
    }

    public void setLegacy(boolean legacy) {
        properties.get("legacy").setValor(legacy);
    }

    @Editable
    public boolean getInitialQuery() {
        return ((PropiedadBooleana) properties.get("initialQuery")).getValor();
    }

    public void setInitialQuery(boolean initialQuery) {
        properties.get("initialQuery").setValor(initialQuery);
    }

    @Editable
    public boolean getCallbackOnNoResults() {
        return ((PropiedadBooleana) properties.get("callbackOnNoResults")).getValor();
    }

    public void setCallbackOnNoResults(boolean callbackOnNoResults) {
        properties.get("callbackOnNoResults").setValor(callbackOnNoResults);
    }

    @Editable
    public int getNumberOfRecords() {
        return ((PropiedadNumerica<Integer>) properties.get("numberOfRecords")).getValor();
    }

    public void setNumberOfRecords(int numberOfRecords) {
        properties.get("numberOfRecords").setValor(numberOfRecords);
    }

    @Editable
    public int getTimeOut() {
        return ((PropiedadNumerica<Integer>) properties.get("timeout")).getValor();
    }

    public void setTimeOut(int timeout) {
        properties.get("timeout").setValor(timeout);
    }

    public EjecutadoPor getExecuteBy() {
        return ((PropiedadEnum<EjecutadoPor>) properties.get("executedBy")).getValor();
    }

    public void setExecuteBy(EjecutadoPor executedBy) {
        properties.get("executeBy").setValor(executedBy);
    }
    
    //</editor-fold>

    //Getters y setters Input 
    @Editable
    public String getNameNg() {
        return ((PropiedadSimple) properties.get("nameng")).getValor();
    }

    public void setNameNg(String nameng) {
        properties.get("nameng").setValor(nameng);
    }

    @Editable
    public int getLongitud() {
        return ((PropiedadNumerica<Integer>) properties.get("lon")).getValor();
    }

    public void setLongitud(int longitud) {
        properties.get("lon").setValor(longitud);
    }

    public String getJavaScriptIn() {
        return ((PropiedadJavascript) properties.get("jvs")).getValorString();
    }

    public void setJavaScriptIn(String javascriptIn) {
        properties.get("jvs").setValor(javascriptIn);
    }

    public String getGuia() {
        return ((PropiedadSimple) properties.get("gia")).getValor();
    }

    public void setGuia(String guia) {
        properties.get("gia").setValor(guia);
    }

    @Override
    protected String getIdForHTMLId() {
       
        if (properties.get("tex").esValorPorDefecto()) {
            return super.getId();
        } else {
            return getName();
        }
    }
//    @Editable
//    public boolean getLimpiable() {
//        return ((PropiedadBooleana) properties.get("cln")).getValor();
//    }
//
//    public void setLimpiable(boolean limpiable) {
//        properties.get("cln").setValor(limpiable);
//    }
    // /////////////////////
    // Metodos de FormElement
    // /////////////////////
    @Override
    @Editable
    @JS(ignore = false)
    public boolean getVisible() {
        return ((PropiedadBooleana) properties.get("vis")).getValor();
    }

    @Override
    public void setVisible(boolean vis) {
        properties.get("vis").setValor(vis);
    }

//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
    @Override
    protected Collection<String> getAtributosElementos() {
        List<String> l = new ArrayList<>();

        Collections.addAll(l, "vis", "title",
                "val", "subs", "tran", "preQuery", "callback", "ver", "head", "registerEvents", "queryOnSuccess",
                "multirecord", "numberOfRecords", "legacy", "noDataMessage", "initialQuery", "callbackOnNoResults", "executedBy", "timeout", "gia", "jvs", "lon", "mod", "req", "cln",
                "vis", "RecibirFoco", "nameng", "tipoFrm");

        return l;
    }

    @Override
    public Collection<SerializableXml> getChildren() {
        Collection<SerializableXml> children = super.getChildren();
        children.add(UtilXML.newInstance("references", getReferences()));
        children.add(UtilXML.newInstance("fields", getFields()));
        return children;
    }

    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = toPropiedades(new PropiedadSeparador("Propiedades Iniciales"), "title",
                "references", "fields", "preQuery", "callback", "subs", "tran", "ver", "head", "registerEvents", "vis", "queryOnSuccess",
                "multirecord", "numberOfRecords", "legacy", "noDataMessage", "initialQuery", "callbackOnNoResults", "executedBy", "timeout", new PropiedadSeparador("Propiedades Secundarias"), "gia", "jvs", "mod", "req", "nameng", "tipoFrm", new PropiedadSeparador("Propiedades Generales"), "x", "y", "z",
                "w", "h", "cssClass", "tex");

        //l.addAll(super.getPropiedadesEdicion());
        return l;
    }

    @Override
    public void generateHtml(ConstructorHtml html) {
        System.out.println("generateHtml");
    }
    public FormTypes getTypeFrm() {
        return ((PropiedadEnum<FormTypes>) properties.get("tipoFrm")).getValor();
    }

    public void setTypeFrm(FormTypes typeFrm) {
        properties.get("tipoFrm").setValor(typeFrm);
    }   
    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarEventoJSInicial();
       // generarHtmlBase(html);
        html.abrirNg("mat-form-field");
        html.abrirNg("mat-label");
        html.setTexto(getNameNg());
        html.cerrar("mat-label");
        html.setAtributo("appearance", "outline");
        html.setAtributo("fxFlexFill", "true");
        html.abrirNg("mat-select");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("abreCorch--formControl--cerrCorch--", getTexto());
        html.setAtributo("abreParent--selectionChange--cerrParent--", "seleccion"+getTexto()+"abreParent----cerrParent--");
        //aqui se debe poner la logica para la copia de los valores seleccionados 
        WebPageEnviromentNG.addEventSelectionListOfValues("seleccion"+getTexto(), getFields(),getCallBack(),getCallbackOnNoResults(),getTexto());
        //armar componente de lista de valores 
        WebPageEnviromentNG.addVariables(getTexto()+"list");
        WebPageEnviromentNG.addVariablesWithValue(getTexto()+"list2","[]");
        WebPageEnviromentNG.addVariablesWithValue("num".concat(getTexto()).concat("list"),"1");
        WebPageEnviromentNG.addEventosConsultaLv("consultaLv"+getTexto(), getReferences(),getFields(),getTexto()+"list",getNumberOfRecords(),getSubs(),getTran());
        WebPageEnviromentNG.addVariablesForm(getTexto(), getTypeFrm());
        
        if (getModificable() == Modificable.SOLO_LECTURA) {
            html.setAtributo("disabled", "true", "");
        }
        //Se empieza con el HTML
        html.setEstilo("width", getW(), "px", 0);
        html.setEstilo("height", getH(), "px", 0);
        html.agregar("input");
        html.setAtributo("matInput","true");
        String opcionPrincipal=obtenerOpcionPrincipal(getTexto(),getFields());
        html.setAtributo("abreParent--keyup--cerrParent--", "applyFilter ($event.target.value.toUpperCase())");
        if(!opcionPrincipal.isEmpty()){
            html.abrirNg("mat-select-trigger");
            html.setTexto("abreLlave--abreLlave--"+getTexto()+".value."+opcionPrincipal+"--cerrLlave----cerrLlave--");
            html.cerrar("mat-select-trigger");
        }
        html.abrirNg("mat-option");
        html.setAtributo("abreCorch--disabled--cerrCorch--", "num".concat(getTexto()).concat("list").concat(">1"));
        html.setTexto(""+(char)30);
        html.cerrar("mat-option");
        html.abrirNg("mat-option");
        html.setAtributo("abreCorch--value--cerrCorch--", "dato_"+getTexto());
        html.setAtributo("asterisco--ngFor", "let dato_"+getTexto()+" of "+getTexto()+"list2");
        
        //aqui va la logica para poner todos los campos que se deben mostrar de la lista de valores
        String datos="";
        for (LOVField field : getFields()) {
            if(field.getVisible()){
                datos=datos.concat("abreLlave--abreLlave--").concat("dato_"+getTexto()+"."+field.getField()).concat("--cerrLlave----cerrLlave--").concat("\t");
            }
        }
        
        html.setTexto(datos);
        html.cerrar("mat-option");
        html.abrirNg("mat-option");
        html.setTexto(""+(char)31);
        html.cerrar("mat-option");
//        generarTabIndex(html);
//        generarClasesCSS(html);
//        generarEventosJavascript(html);
//        generarHtmlGuia(html, getGuia());


        html.cerrar("mat-select");
        html.cerrar("mat-form-field");

       // finalizarHtmlBase(html);
    }
     public void generarEventoJSInicial() {
        if (getIndiceClonacion() == 0) {
            Map<String, String> eventos = getPropiedadJavaScript().getEventos();
            for (String evento : eventos.keySet()) {
                String code = eventos.get(evento);

                String functionName = String.format(FUNCTION_NAME_TEMPLATE, 
                        getNameOrDefault(), evento);
                WebPageEnviroment.addJavascriptInicial(String.format(
                        "%s = function(e) { %s }", functionName, code));
            }
        }
    }
     /**
     * @return 
     * @FIXME buscar una mejor manera de exponer esto:
     * @deprecated
     */
    @Deprecated
    @JS(ignore = true)
    public PropiedadJavascript getPropiedadJavaScript() {
        return (PropiedadJavascript) properties.get("jvs");
    }
    @Override
    public FieldData getFieldData() {
        return null;
    }

    @Override
    public String getValueFilaActual() {
        return "";
    }

    @Override
    public String getValueConsultaFilaActual() {
        return "";
    }

    @Override
    public String getRelleno() {
        return "";
    }

    @Override
    public String getNameOrDefault() {
        return "";
    }

   
    @Override
     public String getName() {
        return getTexto();
    }

    
    @Override
    public void setName(String name) {        
        setTexto(name);
    }

    @Override
    public void actualizarPropiedadesValores() {

    }

    @Override
    public Assistant getAssistant() {
        Assistant assistant = new None();
        return assistant;
    }

    @Override
    public void setAssistant(Assistant assistant) {

    }

    @Override
    public boolean getLimpiable() {
        return ((PropiedadBooleana) properties.get("cln")).getValor();
    }

    @Override
    public void setLimpiable(boolean limpiable) {
        properties.get("cln").setValor(limpiable);
    }

    @Override
    public Collection<JSBehavior> getBehaviors() {
        return null;
    }

    @Override
    public Requerido getRequerido() {
        return null;
    }

    @Override
    public void setRequerido(Requerido requerido) {

    }

    @Override
    public Modificable getModificable() {
        return null;
    }

    @Override
    public void setModificable(Modificable modificable) {

    }

    private String obtenerOpcionPrincipal(String widgetBuscar,Collection<LOVField> fields) {
        for (LOVField field : fields) {
            if(field.getElementName().equals(widgetBuscar)){
                return field.getAlias()+"_"+field.getField();
            }
        }
        return "";
    }

}
