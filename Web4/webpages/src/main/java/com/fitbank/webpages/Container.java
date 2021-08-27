package com.fitbank.webpages;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fitbank.enums.TipoFila;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadEnum;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadJavascript.Tipo;
import com.fitbank.propiedades.PropiedadListaString;
import com.fitbank.propiedades.PropiedadNumerica;
import com.fitbank.propiedades.PropiedadSeparador;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.util.Debug;
import com.fitbank.util.Servicios;
import com.fitbank.webpages.behaviors.FormulaHider;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.widgets.ColumnSeparator;
import com.fitbank.webpages.widgets.DeleteRecord;
import com.fitbank.webpages.widgets.FooterSeparator;
import com.fitbank.webpages.widgets.HeaderSeparator;
import com.fitbank.webpages.widgets.Input;
import com.fitbank.webpages.widgets.TabBar;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

/**
 * Clase Container, representacion de una fila de un formulario.
 *
 * @author FitBank
 * @version 2.0 JT
 */
@SuppressWarnings("unchecked")
public class Container extends WebElement<Widget> {

    private static final long serialVersionUID = 2L;

    private int consultadas = 0;

    private transient ThreadLocal<IteradorClonacion> iteradorClonacion = null;

    private final static String FUNCTION_NAME_TEMPLATE = "parent.c.formulario.vars['%s_%s']";

    /**
     * Constructor por defecto
     */
    public Container() {
        setTag("container");

        def("title", "");
        def("clo", TipoFila.COLUMNAS);
        def("tab", "1");
        def("max", 1);
        def("win", 1);
        def("horizontal", false);
        def("labels", new PropiedadListaString<String>(""));
        def("jvs", new PropiedadJavascript(Tipo.EVENTOS));
        def("visible", true);
        def("readOnly", false);
        def("numberCol", 1);
        def("tamG", 1);
        def("tamM", 1);
        def("tamP", 1);
        def("tableDS", "");
    }

    @Override
    public String getHTMLId() {
        if (getParentWebPage() == null) {
            return super.getId();
        } else {
            return Servicios.toDashedString(Container.class.getSimpleName())
                    + "_" + getPosicion();
        }
    }

    private ThreadLocal<IteradorClonacion> getIteradorClonacion() {
        if (iteradorClonacion == null) {
            iteradorClonacion = new ThreadLocal<IteradorClonacion>();
        }

        return iteradorClonacion;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getType() + ")";
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////
    public String getTitle() {
        return ((PropiedadSimple) properties.get("title")).getValor();
    }

    public void setTitle(String title) {
        properties.get("title").setValor(title);
    }

    public String getTableDS() {
        return ((PropiedadSimple) properties.get("tableDS")).getValor();
    }

    public void setTableDS(String tableDS) {
        properties.get("tableDS").setValor(tableDS);
    }

    public TipoFila getType() {
        return ((PropiedadEnum<TipoFila>) properties.get("clo")).getValor();
    }

    public void setTipoFila(TipoFila clone) {
        properties.get("clo").setValor(clone);
    }

    public int getClonacionMax() {
        return ((PropiedadNumerica<Integer>) properties.get("max")).getValor();
    }

    public void setClonacionMax(int cloneMax) {
        properties.get("max").setValor(cloneMax);
    }

    public int getNumberCol() {
        return ((PropiedadNumerica<Integer>) properties.get("numberCol")).getValor();
    }

    public void setNumberCol(int NumberCol) {
        properties.get("numberCol").setValor(NumberCol);
    }

    public int getTamG() {
        return ((PropiedadNumerica<Integer>) properties.get("tamG")).getValor();
    }

    public void setTamG(int NumberCol) {
        properties.get("tamG").setValor(NumberCol);
    }

    public int getTamM() {
        return ((PropiedadNumerica<Integer>) properties.get("tamM")).getValor();
    }

    public void setTamM(int NumberCol) {
        properties.get("tamM").setValor(NumberCol);
    }

    public int getTamP() {
        return ((PropiedadNumerica<Integer>) properties.get("tamP")).getValor();
    }

    public void setTamP(int NumberCol) {
        properties.get("tamP").setValor(NumberCol);
    }

    public int getPresentacionMax() {
        return ((PropiedadNumerica<Integer>) properties.get("win")).getValor();
    }

    public void setPresentacionMax(int presMax) {
        properties.get("win").setValor(presMax);
    }

    public String getTab() {
        return ((PropiedadSimple) properties.get("tab")).getValor();
    }

    public void setTab(String tab) {
        properties.get("tab").setValor(tab);
    }

    public boolean getHorizontal() {
        return ((PropiedadBooleana) properties.get("horizontal")).getValor();
    }

    public void setHorizontal(boolean horizontal) {
        properties.get("horizontal").setValor(horizontal);
    }

    public Collection<String> getLabels() {
        return ((PropiedadListaString<String>) properties.get("labels")).getValor();
    }

    public List<String> getLabelsList() {
        return ((PropiedadListaString<String>) properties.get("labels")).getList();
    }

    public void setLabels(Collection<String> labels) {
        properties.get("labels").setValor(labels);
    }

    public String getJavaScript() {
        return ((PropiedadJavascript) properties.get("jvs")).getValorString();
    }

    public void setJavaScript(String javascript) {
        properties.get("jvs").setValor(javascript);
    }

    public boolean getVisible() {
        return ((PropiedadBooleana) properties.get("visible")).getValor();
    }

    public void setVisible(boolean visible) {
        properties.get("visible").setValor(visible);
    }

    public boolean getReadOnly() {
        return ((PropiedadBooleana) properties.get("readOnly")).getValor();
    }

    public void setReadOnly(boolean readOnly) {
        properties.get("readOnly").setValor(readOnly);
    }

    private PropiedadJavascript getPropiedadJavaScript() {
        return (PropiedadJavascript) properties.get("jvs");
    }

    public HeaderSeparator getHeaderSeparator() {
        for (Widget widget : this) {
            if (widget instanceof HeaderSeparator) {
                return (HeaderSeparator) widget;
            }
        }

        return null;
    }

    public FooterSeparator getFooterSeparator() {
        for (Widget widget : this) {
            if (widget instanceof FooterSeparator) {
                return (FooterSeparator) widget;
            }
        }

        return null;
    }

    public Collection<ColumnSeparator> getColumnSeparators() {
        List<ColumnSeparator> columnSeparators = new LinkedList<ColumnSeparator>();

        for (Widget widget : this) {
            if (widget instanceof ColumnSeparator) {
                columnSeparators.add((ColumnSeparator) widget);
            }
        }

        return columnSeparators;
    }

    public int indexOfHeaderSeparator() {
        HeaderSeparator headerSeparator = getHeaderSeparator();

        return headerSeparator == null ? -1 : headerSeparator.getPosicion();
    }

    public int indexOfFooterSeparator() {
        FooterSeparator footerSeparator = getFooterSeparator();

        return footerSeparator == null ? size() : footerSeparator.getPosicion();
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////
    @Override
    public Collection<Propiedad<?>> getPropiedadesEdicion() {
        return toPropiedades(new PropiedadSeparador("Propiedades Generales"),
                "title", "tab", "cssClass", "x", "w", "h", "jvs", "numberCol",
                new PropiedadSeparador("Propiedades Extra"),
                "clo", "max", "win", "labels", "horizontal", "visible", "readOnly", "tableDS", "tamG", "tamM", "tamP");
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////
    @Override
    protected Collection<String> getHijosXml() {
        return null;
    }

    @Override
    protected Collection<String> getAtributosXml() {
        List<String> l = Arrays.asList(new String[]{"cssClass", "title",
            "tab", "clo", "win", "max", "h", "w", "x", "labels", "numberCol",
            "horizontal", "jvs", "visible", "readOnly", "tableDS", "tamG", "tamM", "tamP"
        });

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Html
    // ////////////////////////////////////////////////////////
    @Override
    protected Collection<String> getCSSClasses() {
        Collection<String> cssClasses = super.getCSSClasses();

        cssClasses.addAll(new TabBar().getTabCSSClasses("tab", getTab()));
        cssClasses.add(Servicios.toDashedString(getType().name().toLowerCase()));

        return cssClasses;
    }

    /**
     * Genera el HTML para una fila en un formulario.
     *
     * @param html Contructor de html
     */
    public void generateHtml(ConstructorHtml html) {
        if (!anterior() && StringUtils.isNotBlank(getTitle())) {
            html.abrir("fieldset");

            html.setEstilo("display", "none");

            generarClasesCSS(html);

            html.agregar("legend", getTitle());
        }

        html.abrir("div");
        html.setAtributo("id", getHTMLId());

        html.setEstilo("display", "none");
        html.setEstilo("margin-left", getX(), "px", 0);
        if (!getHorizontal()) {
            html.setEstilo("width", getW(), "px", 0);
        } else {
            html.setEstilo("height", getH(), "px", 0);
        }

        generarEventoJSInicial();
        generarClasesCSS(html, "fila");

        switch (getType()) {
            case NORMAL:
                generarHtmlNormal(html);
                break;

            case MISMO_SITIO:
                generarHtmlClonacionMismoSitio(html);
                break;

            case TABLA:
                if (getHorizontal()) {
                    generarHtmlTablaHorizontal(html);
                } else {
                    generarHtmlTabla(html);
                }
                break;

            case COLUMNAS:
                generarHtmlColumnas(html);
                break;
        }

        html.cerrar("div");

        if (!siguiente() && StringUtils.isNotBlank(getTitle())) {
            html.cerrar("fieldset");
        }
    }

    /**
     * Genera el HTML del div que ocupara la fila.
     *
     * @param html Constructor de html.
     */
    private void generarHtmlNormal(ConstructorHtml html) {
        if (getPresentacionMax() < getClonacionMax()) {
            html.setEstilo("overflow", "auto");
            html.setEstilo("height", getH() * getPresentacionMax(), "px");
        }

        for (int indiceClonacionActual : iteradorClonacion()) {
            html.abrir("div");
            html.setAtributo("class", "clonada"
                    + (indiceClonacionActual % 2 == 0 ? " par" : "")
                    + (getHorizontal() ? " horizontal" : " vertical"));
            html.setEstilo("height", getH(), "px", 0);
            if (getHorizontal()) {
                html.setEstilo("width", getW(), "px", 0);
            }

            generarEventosJavascript(html);

            for (Widget widget : this) {
                widget.generateHtml(html);
            }

            html.cerrar("div");

        }
        limpiarIteradorClonacion();
    }

    /**
     * Genera el HTML en caso que la fila tenga Clonacion en mismo sitio.
     *
     * @param html Constructor de html
     */
    private void generarHtmlClonacionMismoSitio(ConstructorHtml html) {
        for (int indiceClonacionActual : iteradorClonacion()) {
            html.abrir("div");
            html.setEstilo("height", getH(), "px", 0);

            if (indiceClonacionActual > 0) {
                html.setEstilo("display", "none");
            }

            generarEventosJavascript(html);

            for (Widget widget : this) {
                widget.generateHtml(html);
            }

            html.cerrar("div");
        }
        limpiarIteradorClonacion();
    }

    /**
     * Genera el HTML de la fila en el caso que sea una fila tipo tabla.
     *
     * @param html Constructor de html.
     */
    @SuppressWarnings("unused")
    private void generarHtmlTabla(ConstructorHtml html) {
        HeaderSeparator headerSeparator = getHeaderSeparator();
        FooterSeparator footerSeparator = getFooterSeparator();

        html.abrir("div");
        html.setAtributo("class", "table-container");
        html.abrir("table");
        html.setAtributo("class", "tabla " + getCSSClass());

        if (headerSeparator != null) {
            // CABECERA
            html.abrir("thead");
            int filaCabecera = 0;
            for (Widget widget : this) {
                if (widget instanceof HeaderSeparator) {
                    break;
                }

                int z = Math.max(1, widget.getZ());

                if (z > filaCabecera) {
                    html.cerrarCondicional("tr");
                    filaCabecera = z;
                    html.abrir("tr");
                    html.setEstilo("height", getH(), "px", 0);

                    generarEventosJavascript(html);

                    html.agregar("th");
                }

                widget.generateHtml(html);
            }

            html.cerrarCondicional("tr");
            html.cerrar("thead");
        }

        // CUERPO
        html.abrir("tbody");
        for (int indiceClonacionActual : iteradorClonacion()) {
            int filaCuerpo = 0;
            boolean finCabeza = headerSeparator == null;
            for (Widget widget : this) {
                if (widget instanceof HeaderSeparator) {
                    finCabeza = true;
                } else if (widget instanceof FooterSeparator) {
                    break;
                } else if (finCabeza) {
                    int z = Math.max(1, widget.getZ());

                    if (z > filaCuerpo) {
                        html.cerrarCondicional("tr");
                        filaCuerpo = z;
                        html.abrir("tr");
                        html.setEstilo("height", getH(), "px", 0);
                        html.setAtributo("class", "clonada"
                                + (indiceClonacionActual % 2 == 0 ? " par" : ""));

                        generarEventosJavascript(html);

                        if (getLabelsList().size() > indiceClonacionActual) {
                            html.agregar("th", getLabelsList().get(indiceClonacionActual));
                        } else {
                            html.agregar("th", "");
                        }
                    }

                    widget.generateHtml(html);
                }
            }
            html.cerrarCondicional("tr");
        }
        limpiarIteradorClonacion();

        html.cerrar("tbody");

        if (footerSeparator != null) {
            // PIE
            html.abrir("tfoot");
            int filaPie = 0;
            boolean inicioPie = false;
            for (Widget widget : this) {
                if (widget instanceof FooterSeparator) {
                    inicioPie = true;
                    continue;
                }

                if (!inicioPie) {
                    continue;
                }

                int z = Math.max(1, widget.getZ());

                if (z > filaPie) {
                    html.cerrarCondicional("tr");
                    filaPie = z;
                    html.abrir("tr");
                    html.setEstilo("height", getH(), "px", 0);

                    generarEventosJavascript(html);

                    html.agregar("th");
                }

                widget.generateHtml(html);
            }
            html.cerrarCondicional("tr");
            html.cerrar("tfoot");
        }

        html.cerrar("table");
        html.cerrar("div");

        if (getPresentacionMax() < getClonacionMax()) {
            WebPageEnviroment.addJavascriptInicial("Util.initTableScroll('"
                    + getHTMLId() + "', " + getPresentacionMax() + ");");
        }
    }

    /**
     * Genera el HTML de la fila en el caso que sea una fila tipo tabla.
     *
     * @param html Constructor de html.
     */
    @SuppressWarnings("unused")
    private void generarHtmlTablaNg(ConstructorHtml html) {
        HeaderSeparator headerSeparator = getHeaderSeparator();
        FooterSeparator footerSeparator = getFooterSeparator();

        //html.abrir("div");
        html.setAtributo("class", "col");
        html.abrirNg("mat-card");
        html.setAtributo("id", "card" + getHTMLId());
        html.setAtributo("class", "cardServ");
        if (StringUtils.isNotBlank(getTitle())) {
            html.abrir("div");
            html.setAtributo("class", "row");
            html.abrir("div");
            html.setAtributo("class", "center col texto");
            html.abrir("h6");
            html.abrir("strong");
            html.setTexto(getTitle());
            html.cerrar("strong");
            html.cerrar("h6");
            html.cerrar("div");
            html.cerrar("div");
        }
        html.abrir("div");
        html.setAtributo("class", "row");
        html.setEstilo("margin-left", "-24px !important");
        html.abrir("div");
        html.setAtributo("class", "col flecha");
        html.setEstilo("margin-right", "4px");
        html.abrirNg("mat-icon");
        html.setAtributo("abreCorch--class.desaparecer--cerrCorch--", "flechaL_" + getHTMLId());
        html.setAtributo("id", "flechaL_" + getHTMLId());
        WebPageEnviromentNG.addVariablesWithType("flechaL_" + getHTMLId(), "boolean");
        html.setAtributo("abreParent--click--cerrParent--", "scrollTo('tabla_" + getHTMLId() + "','left')");
        html.setTexto("keyboard_arrow_left");
        html.cerrar("mat-icon");
        html.cerrar("div");
        html.abrir("div");
//        html.setAtributo("class", "col " + ("".equals(getCSSClass()) ? getId().substring(1, getId().length()) : getCSSClass()));//se puede mandar la clase en el container mismo 
        html.setAtributo("class", "col container2Aux");//se puede mandar la clase en el container mismo 
        html.setAtributo("abreCorch--class.heightconteiner2--cerrCorch--", "!(" + getTableDS() + ".length>numMaxRow" + getTableDS() + ")");
        html.setAtributo("abreParent--scroll--cerrParent--", "scroll()");
        WebPageEnviromentNG.addVariablesWithValue("numMaxRow" + getTableDS(), "" + getPresentacionMax());
        html.abrir("table");
        html.setAtributo("mat-table", "true");
        html.setAtributo("abreCorch--dataSource--cerrCorch--", getTableDS());
        WebPageEnviromentNG.addVariables(getTableDS());
        WebPageEnviromentNG.addEventoShowHide(getTamG(), getTamM(), getTamP(), ("".equals(getCSSClass()) ? getId().substring(1, getId().length()) : getCSSClass()), getHTMLId());
        WebPageEnviromentNG.addEventoScrollTo(("".equals(getCSSClass()) ? getId().substring(1, getId().length()) : getCSSClass()), getHTMLId());
        WebPageEnviromentNG.addEventoScroll(("".equals(getCSSClass()) ? getId().substring(1, getId().length()) : getCSSClass()), getHTMLId());
        html.setAtributo("class", "tabla_" + getHTMLId());
        html.setAtributo("id", getId());
        List<Widget> cabeceraWidgets = new ArrayList<>();
        if (headerSeparator != null) {
            // CABECERA
            for (Widget widget : this) {
                if (widget instanceof HeaderSeparator) {
                    break;
                }
                cabeceraWidgets.add(widget);

            }

        }
        String nombreArregloColumnas = "displayedColumns_" + getTableDS();
        WebPageEnviromentNG.addPlantillaColumnas(cabeceraWidgets, nombreArregloColumnas);
        List<String> listaColumnas = new ArrayList<>();
        // CUERPO
//        for (int indiceClonacionActual : iteradorClonacion()) {
        int filaCuerpo = 0;
        boolean finCabeza = headerSeparator == null;
        for (Widget widget : this) {
            if (widget instanceof HeaderSeparator) {
                finCabeza = true;
            } else if (widget instanceof FooterSeparator) {
                break;
            } else if (finCabeza) {
                //html.cerrarCondicional("tr");
                html.abrirNg("ng-container");
                String columnName = cabeceraWidgets.get(this.indexOf(widget) - filaCuerpo - 1).getTexto().equals("") ? cabeceraWidgets.get(this.indexOf(widget) - filaCuerpo - 1).getHTMLId() : cabeceraWidgets.get(this.indexOf(widget) - filaCuerpo - 1).getTexto();
                html.setAtributo("matColumnDef", columnName);
                html.abrir("th");
                html.setAtributo("mat-header-cell", "true");
                html.setAtributo("asterisco--matHeaderCellDef", "--reemplazar--");
                html.setTexto(cabeceraWidgets.get(this.indexOf(widget) - filaCuerpo - 1).getTexto().equals("") ? "" : columnName);
                html.cerrar("th");
                html.abrir("td");
                html.setAtributo("mat-cell", "true");
                html.setAtributo("asterisco--matCellDef", "let dato;");
//                    html.setAtributo("class", "clonada"
//                            + (indiceClonacionActual % 2 == 0 ? " par" : ""));

                // generarEventosJavascript(html);
//                    if (getLabelsList().size() > indiceClonacionActual) {
//                        html.agregar("th", getLabelsList().get(indiceClonacionActual));
//                    } else {
//                        html.agregar("th", "");
//                    }
                widget.generateHtmlNg(html);
                listaColumnas.add(widget.getTexto());
                html.cerrar("td");
                html.cerrar("ng-container");
            }
            if (!finCabeza) {
                filaCuerpo++;
            }
        }
        html.cerrarCondicional("tr");
//        }
        limpiarIteradorClonacion();
        WebPageEnviromentNG.addEmptyArrayForTable(getTableDS(), listaColumnas, getClonacionMax());

//        if (footerSeparator != null) {
//            // PIE
//            html.abrir("tfoot");
//            int filaPie = 0;
//            boolean inicioPie = false;
//            for (Widget widget : this) {
//                if (widget instanceof FooterSeparator) {
//                    inicioPie = true;
//                    continue;
//                }
//
//                if (!inicioPie) {
//                    continue;
//                }
//                
//                int z = Math.max(1, widget.getZ());
//
//                if (z > filaPie) {
//                    html.cerrarCondicional("tr");
//                    filaPie = z;
//                    html.abrir("tr");
//                    html.setEstilo("height", getH(), "px", 0);
//
//                    generarEventosJavascript(html);
//
//                    html.agregar("th");
//                }
//
//                widget.generateHtml(html);
//            }
//            html.cerrarCondicional("tr");
//            html.cerrar("tfoot");
//        }
        html.abrir("tr");
        html.setAtributo("mat-header-row", "true");
        html.setAtributo("class", "mat-header-row");
        html.setAtributo("asterisco--matHeaderRowDef", nombreArregloColumnas);
        html.setTexto(nombreArregloColumnas);
        html.cerrar("tr");
        html.abrir("tr");
        html.setAtributo("mat-row", "true");
        html.setAtributo("asterisco--matRowDef", "let row;let even = even; let last=last; columns:" + nombreArregloColumnas + ";");
        html.setAtributo("abreCorch--ngClass--cerrCorch--", "{gray: even}");
//        html.setTexto("{{showHideRows(0,last)}}");
        html.setTexto("{{ showHideRows() }}");
        html.cerrar("tr");
        html.cerrar("table");
        html.cerrar("div");
        html.abrir("div");
        html.setAtributo("class", "col flecha");
        html.setEstilo("margin-right", "4px");
        html.abrirNg("mat-icon");
        html.setAtributo("abreCorch--class.desaparecer--cerrCorch--", "flechaR_" + getHTMLId());
        html.setAtributo("id", "flechaR_" + getHTMLId());
        WebPageEnviromentNG.addVariablesWithType("flechaR_" + getHTMLId(), "boolean");
        html.setAtributo("abreParent--click--cerrParent--", "scrollTo('tabla_" + getHTMLId() + "','right')");
        html.setTexto("keyboard_arrow_right");
        html.cerrar("mat-icon");
        html.cerrar("div");
        html.cerrar("div");
        html.cerrar("mat-card");
        /**
         *
         * faltaria el paginator angular material
         * //<mat-paginator [pageSizeOptions]="[5, 10, 20]" showFirstLastButtons></mat-paginator>//
         *
         * agregar MatPaginatorModule en material module.ts
         */
//        html.cerrar("div");

//        if (getPresentacionMax() < getClonacionMax()) {
//            WebPageEnviroment.addJavascriptInicial("Util.initTableScroll('"
//                    + getHTMLId() + "', " + getPresentacionMax() + ");");
//        }
    }

    /**
     * Genera el HTML de la fila en el caso que sea una fila tipo Tabla.
     *
     * @param html Constructor de html.
     */
    @SuppressWarnings("unused")
    private void generarHtmlTablaHorizontal(ConstructorHtml html) {
        HeaderSeparator headerSeparator = getHeaderSeparator();
        FooterSeparator footerSeparator = getFooterSeparator();

        html.abrir("table");
        html.setAtributo("class", "tabla " + getCSSClass());

        // TITULOS
        html.abrir("thead");
        html.abrir("tr");

        html.agregar("th", "");
        for (String label : getLabels()) {
            html.agregar("th", label);
            html.setAtributo("class", "field-label");
        }
        html.cerrar("tr");
        html.cerrar("thead");

        // ELEMENTOS
        html.abrir("tbody");
        int procesados = 0;
        int filaActual = 0;
        do {
            html.abrir("tr");
            generarEventosJavascript(html);

            // CABECERA
            int filaEncabezado = 0;
            for (Widget widget : this) {
                if (widget instanceof HeaderSeparator) {
                    break;
                }
                if (filaEncabezado == filaActual) {
                    widget.generateHtml(html);
                    procesados++;
                }
                filaEncabezado += widget.getVisible() ? Math.max(1,
                        widget.getY()) : 0;
                if (filaEncabezado > filaActual) {
                    break;
                }
            }

            if (headerSeparator != null && filaActual == 1) {
                procesados++;
            }

            // CUERPO
            for (int indiceClonacionActual : iteradorClonacion()) {
                int filaCuerpo = 0;
                boolean finCabeza = headerSeparator == null;
                for (Widget widget : this) {
                    if (widget instanceof HeaderSeparator) {
                        finCabeza = true;
                    } else if (widget instanceof FooterSeparator) {
                        break;
                    } else if (!finCabeza) {
                        continue;
                    } else {
                        if (filaCuerpo == filaActual) {
                            widget.generateHtml(html);
                            if (indiceClonacionActual == 0) {
                                procesados++;
                            }
                        }
                        filaCuerpo += widget.getVisible() ? Math.max(1, widget.
                                getY()) : 0;
                        if (filaCuerpo > filaActual) {
                            break;
                        }
                    }
                }
            }
            limpiarIteradorClonacion();

            // PIE
            int filaPie = 0;
            boolean inicioPie = false;
            for (Widget widget : this) {
                if (widget instanceof FooterSeparator) {
                    inicioPie = true;
                    continue;
                }

                if (!inicioPie) {
                    continue;
                }

                if (filaPie == filaActual) {
                    widget.generateHtml(html);
                    procesados++;
                }
                filaPie += widget.getVisible() ? Math.max(1,
                        widget.getY()) : 0;
                if (filaPie > filaActual) {
                    break;
                }
            }

            if (footerSeparator != null && filaActual == 1) {
                procesados++;
            }

            if (html.getTagActual().getChildren().isEmpty()) {
                html.borrarTagActual();
            } else {
                html.cerrar("tr");
            }

            filaActual++;

        } while (procesados < size());

        getColumnsClosingHtml(html);

        if (getPresentacionMax() < getClonacionMax()) {
            WebPageEnviroment.addJavascriptInicial("Util.initTableScroll('"
                    + getHTMLId() + "', " + getPresentacionMax() + ", true);");
        }
    }

    /**
     * Genera el HTML de la fila en el caso que sea una fila tipo Columnas.
     *
     * @param html Constructor de html.
     */
    @SuppressWarnings("unused")
    private void generarHtmlColumnas(ConstructorHtml html) {
        int numElementosClon = 0;
        Collection<ColumnSeparator> columnSeparators = getColumnSeparators();

        if (!columnSeparators.isEmpty()) {
            html.abrir("table");
            html.setAtributo("class", "columns-with-separator " + getCSSClass());
            html.abrir("tr");
            html.abrir("td");
        }

        getColumnsOpeningHtml(html);

        for (int indiceClonacionActual : iteradorClonacion()) {
            int columna = 0;
            int fila = 0;

            for (Widget widget : this) {
                if (widget instanceof ColumnSeparator) {
                    getColumnsClosingHtml(html);
                    html.cerrar("td");
                    columna = 0;
                    fila = 0;
                    html.abrir("td");
                    getColumnsOpeningHtml(html);
                    continue;
                }

                int x = Math.max(1, widget.getX());
                int y = Math.max(1, widget.getY());

                if (y > fila) {
                    columna = 0;
                    html.cerrarCondicional("td");
                    html.cerrarCondicional("tr");
                    fila = y;
                    html.abrir("tr");
                    html.setEstilo("height", getH(), "px", 0);

                    generarEventosJavascript(html);

                }
                for (; columna < x; columna++) {
                    html.cerrarCondicional("td");
                    html.abrir("td");
                    html.setAtributo("class", "columna_" + columna);
                }

                widget.generateHtml(html);
                numElementosClon = Math.max(y, numElementosClon);
            }

            html.cerrarCondicional("td");
            html.cerrarCondicional("tr");
        }
        limpiarIteradorClonacion();

        getColumnsClosingHtml(html);

        if (!columnSeparators.isEmpty()) {
            html.cerrar("td");
            html.cerrar("tr");
            html.cerrar("table");
        }

        if (getPresentacionMax() < getClonacionMax()) {
            WebPageEnviroment.addJavascriptInicial("Util.initTableScroll('"
                    + getHTMLId() + "', " + getPresentacionMax() * numElementosClon + ");");
        }
    }

    private void getColumnsClosingHtml(ConstructorHtml html) {
        html.cerrarCondicional("td");
        html.cerrarCondicional("tr");
        html.cerrar("tbody");
        html.cerrar("table");
    }

    private void getColumnsOpeningHtml(ConstructorHtml html) {
        html.abrir("table");
        html.setAtributo("class", "columnas " + getCSSClass(), "");
        html.abrir("tbody");
    }

    /**
     * Genera los eventos del container
     *
     * @param html ConstructorHtml donde se va a generar los eventos
     */
    public void generarEventosJavascript(ConstructorHtml html) {
        html.setAtributo("registro", getIndiceClonacion());

        for (String evento : getPropiedadJavaScript().getEventos().keySet()) {
            String functionName = String.format(FUNCTION_NAME_TEMPLATE,
                    getHTMLId(), evento);
            this.generarEventoJavascript(html, evento, getHTMLId(),
                    String.format("%s.bind(this)(event);", functionName));
        }
    }

    public void generarEventoJSInicial() {
        Map<String, String> eventos = getPropiedadJavaScript().getEventos();
        for (String evento : eventos.keySet()) {
            String code = eventos.get(evento);

            String functionName = String.format(FUNCTION_NAME_TEMPLATE,
                    getHTMLId(), evento);
            WebPageEnviroment.addJavascriptInicial(String.format(
                    "%s = function(e) { %s }", functionName, code));
        }
    }

    @Override
    protected void generarEventoJavascript(ConstructorHtml html, String evento,
            String nameOrId, String code) {
        html.extenderAtributo(evento, code);
    }

    /**
     * Indica si se van a juntar la fila actual con la siguiente.
     *
     * @return true si se junta, si no false
     */
    private boolean siguiente() {
        if (getPosicion() < getParentWebPage().size() - 1) {
            Container siguiente = getParentWebPage().get(getPosicion() + 1);
            return siguiente.anterior();
        }
        return false;
    }

    /**
     * Indica si se van a juntar la fila actual con la anterior.
     *
     * @return true si se junta, si no false
     */
    private boolean anterior() {
        if (getPosicion() > 0) {
            Container anterior = getParentWebPage().get(getPosicion() - 1);
            if ((StringUtils.isNotBlank(getTitle()) ? getTitle().equals(anterior.
                    getTitle()) : anterior.getCSSClass().contains("flotable"))
                    && getTab().equals(anterior.getTab())) {
                return true;
            }
        }
        return false;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Clonacion
    // ////////////////////////////////////////////////////////
    /**
     * Devuelve un iterador optimizado para tareas de clonacion de filas.
     *
     * @return (Iterable) El Iterador de clonacion.
     */
    public Iterable<Integer> iteradorClonacion() {
        return new Iterable<Integer>() {

            public Iterator<Integer> iterator() {
                getIteradorClonacion().set(new IteradorClonacion());
                return getIteradorClonacion().get();
            }

        };
    }

    /**
     * Devuelve un iterador optimizado para tareas de clonacion de filas.
     *
     * @return (Iterable) El Iterador de clonacion.
     */
    public void limpiarIteradorClonacion() {
        getIteradorClonacion().remove();
    }

    /**
     * Devuelve el indice de clonacion de la fila.
     *
     * @return (int) indice de clonacion actual de la fila.
     */
    public int getIndiceClonacionActual() {
        if (getIteradorClonacion().get() == null) {
            return 0;
        } else {
            return getIteradorClonacion().get().getIndiceClonacionActual();
        }
    }

    /**
     * Actualiza los párametros de la clonación de los hijos de esta fila.
     */
    @Override
    public void updateChildren() {
        for (Widget widget : this) {
            if (widget instanceof FormElement) {
                ((FormElement) widget).actualizarPropiedadesValores();
            }
            if (widget instanceof DeleteRecord) {
                ((DeleteRecord) widget).actualizarPropiedadesValores();
            }
        }
    }

    /**
     * Obtiene el numero de filas consultadas.
     *
     * @return int con el numero de filas consultadas.
     */
    public int getNumeroDeFilasConsultadas() {
        return consultadas;
    }

    /**
     * Define si la fila ha sido consultada.
     *
     * @param consultada true si ha sido consultada
     * @param cual Numero de filas en caso de clonación
     */
    public void setNumeroDeFilasConsultadas(boolean consultada, int cual) {
        consultadas = consultada ? Math.max(consultadas, cual + 1) : 0;
    }

    /**
     * Obtiene el número de filas que se clonan para consulta.
     *
     * @return Número de filas clonadas.
     */
    public int getNumeroFilasClonadasConsulta() {
        switch (getType()) {
            case MISMO_SITIO:
                return getPresentacionMax();

            case NORMAL:
            case TABLA:
            case COLUMNAS:
                return getClonacionMax();

            default:
                return 1;
        }
    }

    /**
     * Obtiene el número de filas que se clonan para consulta.
     *
     * @return Número de filas clonadas.
     */
    public int getNumeroFilasClonadasMantenimiento() {
        switch (getType()) {
            case MISMO_SITIO:
            case NORMAL:
            case TABLA:
            case COLUMNAS:
                return getClonacionMax();

            default:
                return 1;
        }
    }

    /**
     * Obtiene el numero de columna de un elemento en el cuerpo de la TABLA Se
     * toman los elementos visibles con sus respectivos colspan
     *
     * @param element El elemento a buscar
     *
     * @param cual Numero de elementos [element] a omitir antes del buscado
     *
     * @return Posicion del elemento en el cuerpo de la TABLA
     */
    public int[] getElementColumn(WebElement element, int cual) {
        if (!verifyTable(false)) {
            Debug.error("Imposible determinar columna del elemento: No existe "
                    + "HeaderSeparator");
            return new int[]{0, 0};
        }

        int elementPosition = 0;
        int coincidencias = -1;

        for (Widget widget : this) {
            if ((widget.getPosicion() <= getHeaderSeparator().getPosicion())) {
                continue;
            }

            if (widget.getVisible()) {
                elementPosition += getWidgetX(widget);

                if (element.getClass().isAssignableFrom(widget.getClass())) {
                    coincidencias++;
                }
            }

            if (cual == coincidencias) {
                return new int[]{elementPosition, getWidgetX(widget)};
            }
        }

        return new int[]{0, 0};
    }

    /**
     * Oculta el/los elemento(s) del cuerpo de la TABLA, dada su posicion
     *
     * @param element Posicion del elemento a ocultar
     */
    public void hideBodyElementsByColumn(int[] element, boolean clearFormula) {
        if (!verifyTable(false)) {
            Debug.error("No se puede ocultar: No existe HeaderSeparator");
            return;
        }

        int position = 0;

        for (Widget widget : this) {
            if (widget.getPosicion()
                    <= getHeaderSeparator().getPosicion()) {
                continue;
            }

            if (widget.getVisible()) {
                position += getWidgetX(widget);
            }

            if (position >= element[0]) {
                if (clearFormula) {
                    CollectionUtils.filter(((Input) widget).getBehaviors(),
                            new Predicate() {

                        public boolean evaluate(Object object) {
                            return !(object instanceof FormulaHider);
                        }

                    });
                }

                widget.setVisible(false);
                return;
            }
        }
    }

    /**
     * Oculta el/los elemento(s) de la cabecera de la TABLA, dada su posicion
     *
     * @param element Posicion del elemento a ocultar
     */
    public void hideHeaderElementsByColumn(int[] element) {
        if (!verifyTable(false)) {
            Debug.error("No se puede ocultar cabecera: No existe HeaderSeparator");
            return;
        }

        int labelPosition = 0;
        int currentRow = 1;

        for (Widget widget : this) {
            if (widget instanceof HeaderSeparator) {
                break;
            }

            if (getWidgetZ(widget) < currentRow) {
                continue;
            } else if (getWidgetZ(widget) > currentRow) {
                Debug.warn("Filas (Z) de cabecera en desorden. "
                        + "Posible desalineamiento de elementos...");
                labelPosition = 0;
                currentRow++;
            }

            if (widget.getVisible()) {
                labelPosition += getWidgetX(widget);
            }

            if (labelPosition == element[0]) {
                if (getWidgetX(widget) < element[1]) {
                    try {
                        this.get(widget.getPosicion() - 1).setVisible(false);
                        widget.setVisible(false);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Debug.error("Imposible ocultar elemento colspan", e);
                    }
                } else if (getWidgetX(widget) > element[1]) {
                    Debug.info("Elemento " + widget.getTexto()
                            + " contenedor de colspan.. se omite");
                } else {
                    widget.setVisible(false);
                }
                labelPosition = 0;
                currentRow++;
            } else if (labelPosition > element[0]) {
                labelPosition = 0;
                currentRow++;
                Debug.warn(String.format(
                        "Colspan incorrecto en el elemento '%s' de la cabecera "
                        + "de container TABLA.",
                        widget.getTexto()));
            }
        }
    }

    /**
     * Oculta el/los elemento(s) de la cabecera de la TABLA, dada su posicion
     *
     * @param element Posicion del elemento a ocultar
     */
    public void hideFooterElementsByColumn(int[] element) {
        if (!verifyTable(true)) {
            Debug.error("No se puede ocultar pie: No existe FooterSeparator");
            return;
        }

        int labelPosition = 0;
        int currentRow = 1;

        for (Widget widget : this) {
            if (widget.getPosicion()
                    <= getFooterSeparator().getPosicion()) {
                continue;
            }

            if (getWidgetZ(widget) < currentRow) {
                continue;
            } else if (getWidgetZ(widget) > currentRow) {
                Debug.warn("Filas (Z) de pie en desorden. "
                        + "Posible desalineamiento de elementos...");
                labelPosition = 0;
                currentRow++;
            }

            if (widget.getVisible()) {
                labelPosition += getWidgetX(widget);
            }

            if (labelPosition == element[0]) {
                if (getWidgetX(widget) < element[1]) {
                    try {
                        this.get(widget.getPosicion() - 1).setVisible(false);
                        widget.setVisible(false);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Debug.error("Imposible ocultar elemento colspan", e);
                    }
                } else if (getWidgetX(widget) > element[1]) {
                    Debug.info("Elemento " + widget.getTexto()
                            + " contenedor de colspan.. se omite");
                } else {
                    widget.setVisible(false);
                }
                labelPosition = 0;
                currentRow++;
            } else if (labelPosition > element[0]) {
                labelPosition = 0;
                currentRow++;
                Debug.warn(String.format(
                        "Colspan incorrecto en el elemento '%s' de la cabecera "
                        + "de container TABLA.",
                        widget.getTexto()));
            }
        }
    }

    /**
     * Oculta columnas enteras de widgets segun su tipo
     */
    public void hideElements(WebElement element, boolean clearFormulas) {
        if (!verifyTable(false)) {
            Debug.warn("Container no es tabla. Se ocultaran solo los elementos "
                    + element.getClass().getSimpleName());

            for (Widget widget : this) {
                if (element.getClass().isAssignableFrom(widget.getClass())) {
                    widget.setVisible(false);
                }
            }

            return;
        }

        int[] elementParams = new int[]{1, 0};
        while (elementParams[0] > 0) {
            elementParams = getElementColumn(element, 0);
            if (elementParams[0] > 0) {
                Debug.info("Columna del " + element.getClass().getSimpleName()
                        + " a ocultar: " + elementParams[0]);
                hideHeaderElementsByColumn(elementParams);
                hideBodyElementsByColumn(elementParams, clearFormulas);
                if (verifyTable(true)) {
                    hideFooterElementsByColumn(elementParams);
                }

            }
        }
    }

    /**
     * Obtiene la propiedad X correcta para widgets
     *
     * @param widget Widget a evaluar
     */
    public int getWidgetX(Widget widget) {
        return widget.getX() == 0 ? 1 : widget.getX();
    }

    /**
     * Obtiene la propiedad Z correcta para widgets
     *
     * @param widget Widget a evaluar
     */
    public int getWidgetZ(Widget widget) {
        return widget.getZ() == 0 ? 1 : widget.getZ();
    }

    /**
     * Verifica si el container es TABLA debidamente armada
     *
     * @return (boolean) Es valida o no
     */
    public boolean verifyTable(boolean footer) {
        if (footer) {
            return (this.getType().equals(TipoFila.TABLA)
                    && this.getFooterSeparator() != null);
        } else {
            return (this.getType().equals(TipoFila.TABLA)
                    && this.getHeaderSeparator() != null);
        }
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
//        if (!anterior() && StringUtils.isNotBlank(getTitle())) {
//            html.abrir("fieldset");
//
//            html.setEstilo("display", "none");
//
//            generarClasesCSS(html);
//
//            html.agregar("legend", getTitle());
//        }
        if (StringUtils.isNotBlank(getTitle()) && !getType().equals(getType().TABLA)) {
            html.abrir("h3");
            html.setTexto(getTitle());
            html.cerrar("h3");
            html.abrir("hr");
        }
        html.abrir("div");
        html.setAtributo("id", getHTMLId());

        //html.setEstilo("display", "none");
        html.setEstilo("margin-left", getX(), "px", 0);
        if (!getHorizontal()) {
            html.setEstilo("width", getW(), "px", 0);
        } else {
            html.setEstilo("height", getH(), "px", 0);
        }

        generarEventoJSInicial();
        //generarClasesCSS(html, "fila");

        switch (getType()) {
            case NORMAL:
                generarHtmlNormal(html);
                break;

            case MISMO_SITIO:
                generarHtmlClonacionMismoSitio(html);
                break;

            case TABLA:
                if (getHorizontal()) {
                    generarHtmlTablaHorizontal(html);
                } else {
                    generarHtmlTablaNg(html);
                }
                break;

            case COLUMNAS:
                generarHtmlColumnasNg(html);
                break;
        }

        html.cerrar("div");

//        if (!siguiente() && StringUtils.isNotBlank(getTitle())) {
//            html.cerrar("fieldset");
//        }
    }

    private void generarHtmlColumnasNg(ConstructorHtml html) {
        int numElementosClon = 0;
        Collection<ColumnSeparator> columnSeparators = getColumnSeparators();
        html.setAtributo("class", "row "+ getCSSClass());
        if (!columnSeparators.isEmpty()) {
            html.abrir("table");
            html.setAtributo("class", "columns-with-separator " + getCSSClass());
            html.abrir("tr");
            html.abrir("td");
        }
        for (int indiceClonacionActual : iteradorClonacion()) {
            int columna = 0;
            int fila = 0;
            Map<Integer, Integer> m = new HashMap<Integer, Integer>();
//            for (Widget widget : this) {
//                Integer aa = m.get(widget.getX());
//                if (aa == null) {
//                    aa = 0;
//                }
//                m.put(widget.getX(), aa++);
//            }
            Integer auxiliarColumas = 0;
            for (Widget widget : this) {
                if (widget instanceof ColumnSeparator) {
                    getColumnsClosingHtml(html);
//                        html.cerrar("td");
                    columna = 0;
                    fila = 0;
//                        html.abrir("td");
                    getColumnsOpeningHtml(html);
                    continue;
                }
                int x = Math.max(1, widget.getX());
                int y = Math.max(1, widget.getY());

                if (y > fila) {
                    columna = 0;
                    //html.cerrarCondicional("td");
                    //html.cerrarCondicional("tr");
                    fila = y;
                    //html.abrir("tr");                  
                    //html.setEstilo("height", getH(), "px", 0);

                    //generarEventosJavascript(html);
                }
//                    for (; columna < x; columna++) {
//                        //html.cerrarCondicional("td");
//                        //html.abrir("td");
//                        //html.setAtributo("class", "columna_" + columna);
//                    }
                auxiliarColumas += widget.getX();
                if (auxiliarColumas > 12 && !(this.indexOf(widget) == this.size() - 1)) {
                    html.cerrar("div");
                    html.abrir("div");
                    html.setAtributo("id", getHTMLId() + "" + this.indexOf(widget));
                    html.setAtributo("class", "row");
                    auxiliarColumas = widget.getX();
                }
                html.abrir("div");
                html.setAtributo("class", "col-sm-12 " + "col-md-" + widget.getX() + (widget.getCSSClass() == null ? "" : " " + widget.getCSSClass()));
                widget.generateHtmlNg(html);

                html.cerrar("div");

                numElementosClon = Math.max(y, numElementosClon);
            }

//            html.cerrarCondicional("td");
//            html.cerrarCondicional("tr");
        }
        limpiarIteradorClonacion();

        //getColumnsClosingHtml(html);
        if (!columnSeparators.isEmpty()) {
            html.cerrar("td");
            html.cerrar("tr");
            html.cerrar("table");
        }

        if (getPresentacionMax() < getClonacionMax()) {
            WebPageEnviroment.addJavascriptInicial("Util.initTableScroll('"
                    + getHTMLId() + "', " + getPresentacionMax() * numElementosClon + ");");
        }
    }

    /**
     * Clase IteradorClonacion, iterador optimizado para tareas de clonacion de
     * filas.
     *
     * @author FitBank
     * @version 2.0
     */
    public class IteradorClonacion implements Iterator<Integer> {

        private int indiceClonacionActual = -1;

        /**
         * Devuleve el indice de clonacion actual de la fila.
         *
         * @return (int) el indice de clonacion actual.
         */
        public int getIndiceClonacionActual() {
            return indiceClonacionActual;
        }

        /**
         * Verifica si aun quedan filas por clonar.
         *
         * @return (boolean) true si todavia hay filas por clonar.
         */
        public boolean hasNext() {
            return indiceClonacionActual < getNumeroFilasClonadasConsulta() - 1;
        }

        /**
         * Devuelve el indice de clonacion siguiente con referencia en el actual
         * solo si este existe.
         *
         * @return (Integer) devuelve el siguiente indice de clonacion si es que
         * existe.
         */
        public Integer next() {
            return ++indiceClonacionActual;
        }

        /**
         * No se puede usar el metodo remove de Iterable para la clonacion de
         * filas.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
