package com.fitbank.webpages.widgets;

import com.fitbank.enums.Modificable;
import com.fitbank.enums.PosicionHorizontal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fitbank.js.JS;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadListaString;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadJavascript.Tipo;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Editable;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.FieldData;
import java.util.Map;

/**
 * Widget que sirve para borrar un registro.
 * 
 * @author FitBank CI
 */
public class DeleteRecord extends Widget {

    private final static String DELETE_VALUE = "1";

    private final static String FUNCTION_NAME_TEMPLATE = "parent.c.formulario.vars['%s_%s']";

    private final FieldData fieldData = new FieldData(this);

    public DeleteRecord() {
        this.def("alias", new PropiedadListaString<String>(""));
        this.def("jvs", new PropiedadJavascript(Tipo.EVENTOS));
        this.def("vis", true);
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////

    //<editor-fold defaultstate="collapsed" desc="Getters y setters">
    @Editable
    public List<String> getAliasList() {
        return ((PropiedadListaString<String>) this.properties.get("alias")).getList();
    }

    public void setAliasList(List<String> alias) {
        this.properties.get("alias").setValor(alias);
    }

    @Editable
    public String getName() {
        return getTexto();
    }

    @Editable
    public void setName(String name) {
        setTexto(name);
    }


    @JS(ignore = true)
    public FieldData getFieldData() {
        return this.fieldData;
    }

    @JS(ignore = true)
    public String getNameOrDefault() {
        return getIdForHTMLId();
    }

    public String getJavaScript() {
        return ((PropiedadJavascript) properties.get("jvs")).getValorString();
    }

    public void setJavaScript(String javascript) {
        properties.get("jvs").setValor(javascript);
    }

    @Override
    @JS(ignore = false)
    public boolean getVisible() {
        return ((PropiedadBooleana) properties.get("vis")).getValor();
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
    protected String getIdForHTMLId() {
        if (properties.get("tex").esValorPorDefecto()) {
            return super.getId();
        } else {
            return getName();
        }
    }
    //</editor-fold>

    // ////////////////////////////////////////////////////////
    // Métodos de edición en el generador
    // ////////////////////////////////////////////////////////

    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        Collection<Propiedad<?>> l = this.toPropiedades("alias", "jvs");

        l.addAll(super.getPropiedadesEdicion());

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////

    @Override
    protected Collection<String> getAtributosElementos() {
        Collection<String> l = new ArrayList<String>();

        Collections.addAll(l, "alias", "jvs");

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Html
    // ////////////////////////////////////////////////////////

    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();
        
        generarHtmlBase(html);
        html.abrir("span");

        html.agregar("input");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("name", getNameOrDefault());
        html.setAtributo("value-on", DeleteRecord.DELETE_VALUE);
        html.setAtributo("value-off", "");

        if (getVisible()) {
            html.setAtributo("type", "checkbox");
            if (getParentContainer().getIndiceClonacionActual() == 0) {
                WebPageEnviroment.addJavascriptInicial(String.format(
                        "Util.initDeleteRecord('%s');", getNameOrDefault()));
            }

            generarTabIndex(html, true);
            generarClasesCSS(html);
            generarEventosOcultosJavascript(html);
            generarLabel(html, getHTMLId());            
        } else {
            html.setAtributo("type", "hidden");
        }

        html.cerrar("span");
        finalizarHtmlBase(html);
    }

    protected void generarLabel(ConstructorHtml xhtml, String id) {
        xhtml.agregar("label");
        xhtml.setAtributo("for", id);
        xhtml.setAtributo("class", getCSSClass(), "");

        xhtml.setTexto("");
    }

    // ////////////////////////////////////////////////////////
    // Métodos Otros
    // ////////////////////////////////////////////////////////

    /**
     * Actualiza las properties valoresIniciales y valoresActuales.
     */
    public void actualizarPropiedadesValores() {
        this.getFieldData().actualizar(this.getRegistrosConsulta(), this.
                getRegistrosMantenimiento(), "");
    }

    /**
     * Indica si se debe borrar un registro.
     * 
     * @param registro Registro que se consulta
     * 
     * @return true si se debe borrar, si no false
     */
    public boolean delete(int registro) {
        return this.getFieldData().getValue(registro).equals(
                DeleteRecord.DELETE_VALUE);
    }

    /**
     * Genera los eventos para campos visibles que usan elementos ocultos
     *
     * @param html ConstructorHtml donde se va a generar los eventos
     */
    public void generarEventosOcultosJavascript(ConstructorHtml html) {
        html.setAtributo("registro", getIndiceClonacion());

        for (String evento : getPropiedadJavaScript().getEventos().keySet()) {
            String functionName = String.format(FUNCTION_NAME_TEMPLATE, 
                    getNameOrDefault(), evento);
            this.generarEventoJavascript(html, evento, getHTMLId(), 
                    String.format("%s.bind(this)(event);", functionName));
        }
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

    @Override
    public void generateHtmlNg(ConstructorHtml html) {    
        
        generarEventoJSInicial();
        generarHtmlBase(html);
        html.abrirNg("mat-checkbox");
        html.setAtributo("value-on", DeleteRecord.DELETE_VALUE);
        html.setAtributo("value-off", "");

        html.setAtributo("id", getHTMLId());

        html.setTexto(getTexto());
//        generarTabIndex(html);
//        generarClasesCSS(html);
//        generarEventosJavascript(html);

//        if (getVisible() && getLado() == PosicionHorizontal.DERECHA) {
//            generarLabel(html, getHTMLId());
//        }

        html.cerrar("mat-checkbox");
        finalizarHtmlBase(html);
    }
}
