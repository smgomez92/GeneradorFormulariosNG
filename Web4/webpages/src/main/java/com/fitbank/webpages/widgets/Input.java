package com.fitbank.webpages.widgets;

import com.fitbank.enums.FormTypes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fitbank.enums.Modificable;
import com.fitbank.enums.Requerido;
import com.fitbank.enums.TipoFila;
import com.fitbank.js.GeneradorJS;
import com.fitbank.js.JS;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadEnum;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadJavascript.Tipo;
import com.fitbank.propiedades.PropiedadNumerica;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.SerializableXml;
import com.fitbank.serializador.xml.UtilXML;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Editable;
import com.fitbank.util.Servicios;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.JSBehavior;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.WebPageEnviromentNG;
import com.fitbank.webpages.WebPageUtils;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.Calendar;
import com.fitbank.webpages.assistants.None;
import com.fitbank.webpages.assistants.ProgressBar;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FieldData;
import com.fitbank.webpages.data.FormElement;
import java.util.Map;

/**
 * Elemento tipo Input
 *
 * @author FitBank
 * @version 2.0
 */
public class Input extends Widget implements FormElement {

    private static final long serialVersionUID = 2L;

    private final FieldData fieldData = new FieldData(this);

    private final static String FUNCTION_NAME_TEMPLATE = "parent.c.formulario.vars['%s_%s']";
    private String FUNCTION_NAME_TEMPLATE_NG = "%s_%s()";

    public String getFUNCTION_NAME_TEMPLATE_NG() {
        return FUNCTION_NAME_TEMPLATE_NG;
    }

    public void setFUNCTION_NAME_TEMPLATE_NG(String FUNCTION_NAME_TEMPLATE_NG) {
        this.FUNCTION_NAME_TEMPLATE_NG = FUNCTION_NAME_TEMPLATE_NG;
    }

    @Editable
    private Assistant assistant = new None();

    @Editable
    private final Collection<JSBehavior> behaviors
            = new LinkedList<JSBehavior>();

    public Input() {
        def("val", "");
        def("mod", Modificable.MODIFICABLE);
        def("vis", true);
        def("cln", true);
        def("lon", 0);
        def("gia", "");
        def("req", Requerido.AUTOMATICO);
        def("jvs", new PropiedadJavascript(Tipo.EVENTOS));
        def("nameng", "");
        def("RecibirFoco", false);
        def("tipoFrm", FormTypes.ALFANUMERICOFORM);

        properties.get("w").setValorPorDefecto(150);
    }

    // ////////////////////////////////////////////////////////
    // Getters y Setters
    // ////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Getters y setters">
    @Override
    protected String getIdForHTMLId() {

        if (properties.get("tex").esValorPorDefecto()) {
            return super.getId();
        } else {
            return getName();
        }
    }

    @Override
    @Editable
    public DataSource getDataSource() {
        return super.getDataSource();
    }

    @Deprecated
    public boolean getRecibirFoco() {
        return ((PropiedadBooleana) properties.get("RecibirFoco")).getValor();
    }

    @Deprecated
    public void setRecibirFoco(boolean recibirFoco) {
        properties.get("RecibirFoco").setValor(recibirFoco);
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////
    @Editable
    public String getValueInicial() {
        return ((PropiedadSimple) properties.get("val")).getValor();
    }

    public void setValueInicial(String valueInicial) {
        properties.get("val").setValor(valueInicial);
    }

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

    public String getJavaScript() {
        return ((PropiedadJavascript) properties.get("jvs")).getValorString();
    }

    public void setJavaScript(String javascript) {
        properties.get("jvs").setValor(javascript);
    }

    public String getGuia() {
        return ((PropiedadSimple) properties.get("gia")).getValor();
    }

    public void setGuia(String guia) {
        properties.get("gia").setValor(guia);
    }

    // /////////////////////
    // Metodos de FormElement
    // /////////////////////
    @Override
    @Editable
    @JS(ignore = false)
    public boolean getVisible() {
        return ((PropiedadBooleana) properties.get("vis")).getValor();
    }

    @Editable
    public boolean getLimpiable() {
        return ((PropiedadBooleana) properties.get("cln")).getValor();
    }

    public void setLimpiable(boolean limpiable) {
        properties.get("cln").setValor(limpiable);
    }

    @JS(ignore = true)
    public String getRelleno() {
        return getValueInicial();
    }

    @JS(ignore = true)
    public FieldData getFieldData() {
        return fieldData;
    }

    @JS(ignore = true)
    public String getValueFilaActual() {
        return getFieldData().getValues().get(getIndiceClonacion());
    }

    @JS(ignore = true)
    public String getValueConsultaFilaActual() {
        return getFieldData().getValuesConsulta().get(getIndiceClonacion());
    }

    @JS(ignore = true)
    public String getNameOrDefault() {
        return getIdForHTMLId();
    }

    public String getName() {
        return getTexto();
    }

    public void setName(String name) {
        setTexto(name);
    }

    public void setValueConsulta(int registro, String valor) {
        getFieldData().setValueConsulta(registro, valor);
    }

    public Assistant getAssistant() {
        return assistant;
    }

    public void setAssistant(Assistant assistant) {
        this.assistant = assistant;

        if (assistant != null) {
            assistant.init(this);
        }
    }

    @Editable
    public Collection<JSBehavior> getBehaviors() {
        return behaviors;
    }

    /**
     * Ahora se guarda en jsBehaviors pero se mantiene este método para poder
     * cargar formateadores creados con anterioridad.
     *
     * @deprecated
     */
    @Deprecated
    @XML(ignore = true)
    @JS(ignore = true)
    public Collection<JSBehavior> getFormatters() {
        return behaviors;
    }

    // ////////////////////////////
    // Metodos de Queryable
    // ////////////////////////////
    public Requerido getRequerido() {
        return ((PropiedadEnum<Requerido>) properties.get("req")).getValor();
    }

    public void setRequerido(Requerido requerido) {
        properties.get("req").setValor(requerido);
    }

    public Modificable getModificable() {
        return ((PropiedadEnum<Modificable>) properties.get("mod")).getValor();
    }

    public void setModificable(Modificable modificable) {
        properties.get("mod").setValor(modificable);
    }

    public FormTypes getTypeFrm() {
        return ((PropiedadEnum<FormTypes>) properties.get("tipoFrm")).getValor();
    }

    public void setTypeFrm(FormTypes typeFrm) {
        properties.get("tipoFrm").setValor(typeFrm);
    }
    //</editor-fold>

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////
    //<editor-fold defaultstate="collapsed" desc="Métodos de Xml">
    @Override
    protected Collection<String> getAtributosElementos() {
        List<String> l = new ArrayList<String>();

        Collections.addAll(l, "val", "gia", "jvs", "lon", "mod", "req", "cln",
                "vis", "RecibirFoco", "nameng", "tipoFrm");

        return l;
    }

    @Override
    public Collection<SerializableXml> getChildren() {
        Collection<SerializableXml> children = super.getChildren();

        if (!getAssistant().getClass().equals(None.class)) {
            children.add(UtilXML.newInstance("assistant", getAssistant()));
        }

        if (!getBehaviors().isEmpty()) {
            children.add(UtilXML.newInstance("behaviors", getBehaviors()));
        }

        return children;
    }
    //</editor-fold>

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////
    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = toPropiedades("gia", "jvs", "mod", "req", "nameng", "tipoFrm");

        l.addAll(super.getPropiedadesEdicion());
        return l;
    }

    // ////////////////////////////////////////////////////////
    // Metodos Otros
    // ////////////////////////////////////////////////////////
    /**
     * Actualiza las properties valoresIniciales y valoresActuales.
     */
    public void actualizarPropiedadesValores() {
        int registrosConsulta = getRegistrosConsulta();
        int registrosMantenimiento = getRegistrosMantenimiento();

        getFieldData().actualizar(registrosConsulta, registrosMantenimiento,
                getRelleno().startsWith("=") ? "" : getRelleno());

        // FIXME: temporal hasta pasar todos los formularios a que usen queryFocus
        if (getParentWebPage() != null && getRecibirFoco()) {
            if (StringUtils.isBlank(getName())) {
                setName(getNameOrDefault());
            }
            if (StringUtils.isBlank(getParentWebPage().getQueryFocus())) {
                getParentWebPage().setQueryFocus(getName());
            }
            setRecibirFoco(false);
        }
    }

    /**
     * @FIXME buscar una mejor manera de exponer esto:
     * @deprecated
     */
    @Deprecated
    @JS(ignore = true)
    public PropiedadJavascript getPropiedadJavaScript() {
        return (PropiedadJavascript) properties.get("jvs");
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getHTMLId() + ")";
    }

    public String getMinDate(String minDate) {
        //formato dd-MM-yyyy
        String newDate = "new Date(%anio,%mes,%dia)";
        String dates[];
        if (minDate != null) {
            dates = minDate.split("-");
            return newDate.replace("%anio", String.valueOf(Integer.valueOf(dates[2])))
                    .replace("%mes", String.valueOf(Integer.valueOf(dates[1]) == 0 ? 0 : Integer.valueOf(dates[1]) - 1))
                    .replace("%dia", String.valueOf(Integer.valueOf(dates[0])));
        }
        //new Date(2019,3,1)

        return "new Date()";
    }

    public void createCalendarElement(ConstructorHtml html, Calendar calendar) {
        html.abrirNg("mat-form-field");
        Container container = this.getParentContainer();
        if (container.getType().equals(TipoFila.COLUMNAS)) {
            html.abrirNg("mat-label");
            html.setTexto(getNameNg());
            html.cerrar("mat-label");
        }

        html.setAtributo("appearance", getModificable().equals(Modificable.SOLO_LECTURA) ? "fill" : "outline");
        html.setAtributo("fxFlexFill", "true");
        html.agregar("input");
        html.setAtributo("matInput", "true");
        html.setAtributo("readonly", "true");
        html.setAtributo("abreCorch--ngModel--cerrCorch--", getNameOrDefault().concat(" | formatoFechaCalendar:idiomas.IdiomaSeleccionado"));
        WebPageEnviromentNG.addVariablesWithValue(getNameOrDefault(), getValueInicial().isEmpty() ? "new Date()" : getValueInicial());
        html.setAtributo("abreCorch--ngModelOptions--cerrCorch--", "{standalone: true}");
        html.setAtributo("abreCorch--disabled--cerrCorch--", "disableFechas_" + getName());
        WebPageEnviromentNG.addVariablesWithTypeAndValue("disableFechas_" + getName(), "boolean", "false");
        html.agregar("input");
        html.setAtributo("matInput", "true");
        html.setAtributo("readonly", "true");
        html.setAtributo("name", "fdesde_" + getName());
        html.setAtributo("abreCorch--hidden--cerrCorch--", "true");
        html.setAtributo("abreCorch--abreParent--ngModel--cerrParent----cerrCorch--", getNameOrDefault());
        html.setAtributo("abreCorch--min--cerrCorch--", "minDate_" + getName());
        WebPageEnviromentNG.addVariablesWithValue("minDate_" + getName(), getMinDate(calendar.getMinDate()));
        html.setAtributo("abreCorch--matDatepicker--cerrCorch--", "picker_" + getName());
        html.setAtributo("abreCorch--disabled--cerrCorch--", "disableFechas_" + getName());

        //  html.setAtributo("place", "true");
        if (!getParentContainer().getType().equals(TipoFila.TABLA)) {
            html.setAtributo("id", getHTMLId());
        }
        //--abreCorch--cerrCorch--
//        if (!getModificable().equals(Modificable.SOLO_LECTURA)) {
//            html.setAtributo("abreCorch--matDatepicker--cerrCorch--", getNameOrDefault());
//            WebPageEnviromentNG.addVariablesForm(getNameOrDefault(), getTypeFrm());
//            //html.setAtributo("value", WebPageUtils.format(this, getValueInicial()));
//        } else {
//            WebPageEnviromentNG.addVariables(getNameOrDefault());
//            html.setAtributo("abreCorch--ngModel--cerrCorch--", getNameOrDefault());
//            html.setAtributo("value", WebPageUtils.format(this, getValueInicial()));
//            html.setAtributo("name", getNameOrDefault());
//
//        }
//        if (getVisible()) {
//            //html.setAtributo("type", getAssistant().getType());
////            html.setAtributo("maxlength", getLongitud(), 0);
////            if (getModificable().equals(Modificable.SOLO_LECTURA)) {
////                html.setAtributo("readonly", "true");
////            }
////            if (getW() != 0) {
////                html.setEstilo("width", getW(), "px");
////            }
////
////            html.setEstilo("height", getH(), "px", 0);
//        } else {
//            html.setAtributo("type", "hidden");
//        }

        //generarTabIndex(html);
        //generarClasesCSS(html);
//        generarEventosJavascript(html);
        generarEventosTypescript(html);
        finalizarHtmlBaseNg(html);

        html.abrirNg("mat-datepicker-toggle");
        html.setAtributo("matSuffix", "true");
        html.setAtributo("abreCorch--for--cerrCorch--", "picker_" + getName());
        html.setTexto("");
        html.cerrar("mat-datepicker-toggle");
        html.abrirNg("mat-datepicker");
        html.setAtributo("touchUi", "true");
        html.setAtributo("marca--picker_" + getName(), "#picker");
        html.setTexto("");
        html.cerrar("mat-datepicker");
        html.cerrar("mat-form-field");

    }

    // ////////////////////////////////////////////////////////
    // Métodos de Html
    // ////////////////////////////////////////////////////////
    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();

        generarHtmlBase(html);

        // ///////////////////////
        // Html general
        html.agregar("input");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("name", getNameOrDefault());
        html.setAtributo("value", WebPageUtils.format(this, getValueInicial()));

        if (getVisible()) {
            html.setAtributo("type", getAssistant().getType());
            html.setAtributo("maxlength", getLongitud(), 0);
            if (getModificable().equals(Modificable.SOLO_LECTURA)) {
                html.setAtributo("readonly", "true");
            }

            html.setEstilo("width", getW() == 0 ? 150 : getW(), "px");
            html.setEstilo("height", getH(), "px", 0);
        } else {
            html.setAtributo("type", "hidden");
        }

        generarTabIndex(html);
        generarClasesCSS(html);
        generarEventosJavascript(html);

        finalizarHtmlBase(html);
    }

    protected void generarTabIndex(ConstructorHtml html) {
        generarTabIndex(html, getModificable() == Modificable.MODIFICABLE);
    }

    @Override
    protected Collection<String> getCSSClasses() {
        Collection<String> cssClasses = super.getCSSClasses();

        cssClasses.add(Servicios.toDashedString(Servicios.fromUnderscoreString(getDataSource().
                getType().toString())));

        cssClasses.add(Servicios.toDashedString(getAssistant().getClass().
                getSimpleName()));

        for (JSBehavior jsBehavior : getBehaviors()) {
            cssClasses.add(Servicios.toDashedString(jsBehavior.getClass().
                    getSimpleName()));
        }

        if (getAssistant().usesIcon()) {
            cssClasses.add("usaIcono");
        }

        return cssClasses;
    }

    /**
     * Agrega o quita un input oculto al elemento actual para eventos especiales
     *
     * @param suffix sufijo para el nombre del elemento oculto (input_checkbox)
     */
    protected void generarInputOculto(String suffix) {
        WebPageEnviroment.addJavascriptInicial(String.format(
                "Util.initHiddenInput(c.$('%s', %s), '%s');",
                getNameOrDefault(), getIndiceClonacion(), suffix));
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

    @Override
    protected void generarEventoJavascript(ConstructorHtml html, String evento,
            String nameOrId, String code) {
        html.extenderAtributo(evento, code);
    }

    /**
     * Genera los eventos para cada elemento por registro
     *
     * @param html ConstructorHtml donde se va a generar los eventos
     */
    protected void generarEventosJavascript(ConstructorHtml html) {
        html.setAtributo("registro", getIndiceClonacion());

        generarEventosJSAssistants(html);
        generarHtmlGuia(html, getGuia());

        for (String evento : getPropiedadJavaScript().getEventos().keySet()) {
            String functionName = String.format(FUNCTION_NAME_TEMPLATE,
                    getNameOrDefault(), evento);
            this.generarEventoJavascript(html, evento, getHTMLId(),
                    String.format("%s.bind(this)(event);", functionName));
        }
    }

    protected void generarEventosTypescript(ConstructorHtml html) {

        //generarEventosJSAssistants(html);
        //generarHtmlGuia(html, getGuia());
        for (String evento : getPropiedadJavaScript().getEventos().keySet()) {
            String code = getPropiedadJavaScript().getEventos().get(evento);
            String functionName = String.format(FUNCTION_NAME_TEMPLATE_NG,
                    getNameOrDefault(), evento);
            html.setAtributo("abreParent--" + evento + "--cerrParent--", functionName);
//            this.generarEventoJavascript(html, evento, getHTMLId(),
//                    String.format("%s.bind(this)(event);", functionName));
            WebPageEnviromentNG.addEventos(functionName, code);
        }
    }

    protected void generarEventosJSAssistants(ConstructorHtml html) {
        if (getIndiceClonacion() == 0) {
            getAssistant().generateHtml(html);
            for (JSBehavior jsBehavior : getBehaviors()) {
                jsBehavior.setFormElement(this);
                WebPageEnviroment.addJavascriptInicial(GeneradorJS.toJS(
                        jsBehavior) + ";");
            }
        }
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarEventoJSInicial();
        generarHtmlBaseNg(html);

        if (getAssistant().getClass().equals(Calendar.class)) {
            createCalendarElement(html, (Calendar) getAssistant());
        } else {

            // ///////////////////////
            // Html general
            html.abrirNg("mat-form-field");
            Container container = this.getParentContainer();
            if (container.getType().equals(TipoFila.COLUMNAS)) {
                html.abrirNg("mat-label");
                html.setTexto(getNameNg());
                html.cerrar("mat-label");
            }

            html.setAtributo("appearance", getModificable().equals(Modificable.SOLO_LECTURA) ? "fill" : "outline");
            html.setAtributo("fxFlexFill", "true");
            html.agregar("input");
            html.setAtributo("matInput", "true");
            //  html.setAtributo("place", "true");
            if (!getParentContainer().getType().equals(TipoFila.TABLA)) {
                html.setAtributo("id", getHTMLId());
            }
            //--abreCorch--cerrCorch--
            if (!getModificable().equals(Modificable.SOLO_LECTURA)) {
                html.setAtributo("abreCorch--formControl--cerrCorch--", getNameOrDefault());
                WebPageEnviromentNG.addVariablesForm(getNameOrDefault(), getTypeFrm());
                html.setAtributo("value", WebPageUtils.format(this, getValueInicial()));
            } else {
                WebPageEnviromentNG.addVariables(getNameOrDefault());
                html.setAtributo("abreCorch--ngModel--cerrCorch--", getNameOrDefault());
                html.setAtributo("value", WebPageUtils.format(this, getValueInicial()));
                html.setAtributo("name", getNameOrDefault());

            }
            if (getVisible()) {
                html.setAtributo("type", getAssistant().getType());
                html.setAtributo("maxlength", getLongitud(), 0);
                if (getModificable().equals(Modificable.SOLO_LECTURA)) {
                    html.setAtributo("readonly", "true");
                }
                if (getW() != 0) {
                    html.setEstilo("width", getW(), "px");
                }

                html.setEstilo("height", getH(), "px", 0);
            } else {
                html.setAtributo("type", "hidden");
            }
            if (getAssistant().getClass().equals(ProgressBar.class)) {
                addProgressBar(html, (ProgressBar) getAssistant());
            }
            //generarTabIndex(html);
            //generarClasesCSS(html);
//        generarEventosJavascript(html);
            generarEventosTypescript(html);

            finalizarHtmlBaseNg(html);
            //agregar mensajes de error 

            if (!getModificable().equals(Modificable.SOLO_LECTURA)) {
                addErrorMessages(getNameOrDefault(), getTypeFrm(), html);
            }

            html.cerrar("mat-form-field");
        }
    }

    private void addProgressBar(ConstructorHtml html, ProgressBar assistan) {
        html.abrir("span");
        html.setAtributo("asterisco--ngIf", assistan.getFormula());
        html.setTexto(assistan.getMessage() == null ? "Proceso..." : assistan.getMessage());
        html.cerrar("span");
        html.abrirNg("mat-progress-bar");
        html.setAtributo("asterisco--ngIf", assistan.getFormula());
        html.setAtributo("mode", assistan.getModo());
        html.setTexto(" ");
        html.cerrar("mat-progress-bar");
    }

    private void addErrorMessages(String nameOrDefault, FormTypes typeFrm, ConstructorHtml html) {
        for (int i = 0; i < typeFrm.getTipos().split("-").length; i++) {

            html.abrirNg("mat-error");

            html.setAtributo("asterisco--ngIf", nameOrDefault + ".hasError('" + typeFrm.getTipos().split("-")[i] + "')");
            html.abrir("strong");
            html.setTexto("abreLlave--abreLlave--" + typeFrm.getMsjs().split("-")[i] + "--cerrLlave----cerrLlave--");
            // html.setTexto(typeFrm.getMsjs().split("-")[i] );
            html.cerrar("strong");
            html.cerrar("mat-error");

        }
    }

}
