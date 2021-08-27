package com.fitbank.webpages.assistants;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.fitbank.enums.DataSourceType;
import com.fitbank.enums.EjecutadoPor;
import com.fitbank.js.FuncionJS;
import com.fitbank.js.GeneradorJS;
import com.fitbank.js.LiteralJS;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.assistants.lov.LOVField;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;

/**
 * Clase que representa una lista valores.
 * 
 * Esta clase se puede convertir a codigo JavaScript usando la clase
 * GeneradorJSON. El resultado sería algo como esto:
 * 
 * <code>
 *     new ListOfValues({
 *     	titulo: 'Titulo de la ventana',
 *     	x: 10,
 *      y: 20,
 *      elemento: 'F25CIDIOMA',
 *      campos: [
 *      	...
 *      ]
 *     });
 * </code>
 * 
 * A continuación la explicación de cada uno de los campos:
 * 
 * <dl>
 * <dt>titulo</dt>
 * <dd>El titulo de la ventana de Lista de Valores</dd>
 * <dt>centrada</dt>
 * <dd>boolean que indica si se centra o no la ventana</dd>
 * <dt>campos</dt>
 * <dd>Array de campos y/o filtros que se utilizarán en la ventana de lista
 * valores, ej:
 * <code>[ new Campo({...}), new Campo({...}), new Filtro({...}) ]</code></dd>
 * <dt>dependencies</dt>
 * <dd>Array de dependencies de las listas de valores, ej: <code>[ new
 * Dependency({...}), new Dependency({...}) ]</code></dd>
 * <dt>callback</dt>
 * <dd>Funcion que se ejecuta una vez concluida la lista de valores, en Fit V1
 * se llamaba qfun, ej: <code>function(res) { alert(res); }</code></dd>
 * <dt>cabeza</dt>
 * <dd>Indica si se presenta o no la fila de criterios</dd>
 * <dt>registrarEventos</dt>
 * <dd>Indica si se va o no a añadir registrarEventos al elemento</dd>
 * </dl>
 * 
 * @author FitBank
 * @version 2.0
 */
public class ListOfValues implements Assistant {

    private static final long serialVersionUID = 1L;

    @Editable(weight = 1)
    private String title = "";

    @Editable(weight = 2)
    @XML(nombreSubitems = "reference")
    private final List<Reference> references = new LinkedList<Reference>();

    @Editable(weight = 3)
    @XML(nombreSubitems = "field")
    private final List<LOVField> fields = new LinkedList<LOVField>();

    @Editable(weight = 4)
    private LiteralJS preQuery = new FuncionJS("", "registro");

    @Editable(weight = 5)
    private LiteralJS callback = new FuncionJS("", "registro", "values");

    @Editable(weight = 7)
    private String subsystem = "01";

    @Editable(weight = 8)
    private String transaction = "0003";

    @Editable(weight = 9)
    private String version = "01";

    @Editable(weight = 10)
    private boolean head = true;

    @Editable(weight = 11)
    private boolean registerEvents = true;

    @Editable(weight = 12)
    private boolean visible = true;

    @Editable(weight = 13)
    private boolean queryOnSuccess = false;

    @Editable(weight = 14)
    private boolean multirecord = false;

    @Editable(weight = 15)
    private int numberOfRecords = 10;

    @Editable(weight = 16)
    private boolean legacy = false;

    @Editable(weight = 17)
    private String noDataMessage = "";

    @Editable(weight = 18)
    private boolean initialQuery = true;

    @Editable(weight = 19)
    private boolean callbackOnNoResults = false;

    @Editable(weight = 20)
    private EjecutadoPor executedBy = EjecutadoPor.FORMULARIO;

    @Editable(weight = 21)
    private Integer timeout = 0;

    @Editable(weight = 22)
    private boolean saveResponseInCache = false;

    private int pagina;

    private FormElement formElement;

    @Override
    public void init(FormElement formElement) {
        this.formElement = formElement;
    }

    public Boolean getHead() {
        return head;
    }

    public void setHead(Boolean cabeza) {
        this.head = cabeza;
    }

    public LiteralJS getCallback() {
        return callback;
    }

    public void setCallback(LiteralJS callback) {
        this.callback = callback;
    }

    public List<LOVField> getFields() {
        return fields;
    }

    public int getPagina() {
        return pagina;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
    }

    public List<Reference> getReferences() {
        return references;
    }

    public boolean getRegisterEvents() {
        return registerEvents;
    }

    public void setRegisterEvents(boolean registerEvents) {
        this.registerEvents = registerEvents;
    }

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public String format(String valorSinFormato) {
        return valorSinFormato;
    }

    @Override
    public String unformat(String valorFormateado) {
        return valorFormateado;
    }

    @Override
    public Object asObject(String value) {
        return value;
    }

    @Override
    public boolean readFromHttpRequest() {
        return true;
    }

    @Override
    public boolean usesIcon() {
        return getRegisterEvents() && getVisible();
    }

    @Override
    public Collection<DataSourceType> applyTo() {
        return Arrays.asList(new DataSourceType[]{
                    DataSourceType.CRITERION_CONTROL,
                    DataSourceType.CONTROL,
                    DataSourceType.CRITERION,
                    DataSourceType.RECORD
                });
    }

    @XML(ignore = true)
    @Override
    public String getElementName() {
        return formElement == null ? "" : formElement.getNameOrDefault();
    }

    public LiteralJS getPreQuery() {
        return preQuery;
    }

    public void setPreQuery(LiteralJS preQuery) {
        this.preQuery = preQuery;
    }

    public boolean getQueryOnSuccess() {
        return queryOnSuccess;
    }

    public void setQueryOnSuccess(boolean queryOnSuccess) {
        this.queryOnSuccess = queryOnSuccess;
    }

    public boolean getMultirecord() {
        return multirecord;
    }

    public void setNumberOfRecords(int numberOfRecords) {
        this.numberOfRecords = numberOfRecords;
    }

    public int getNumberOfRecords() {
        return numberOfRecords;
    }

    public void setMultirecord(boolean multirecord) {
        this.multirecord = multirecord;
    }

    public boolean getLegacy() {
        return legacy;
    }

    public void setLegacy(boolean legacy) {
        this.legacy = legacy;
    }

    public String getNoDataMessage() {
        return noDataMessage;
    }

    public void setNoDataMessage(String noDataMessage) {
        this.noDataMessage = noDataMessage;
    }

    public boolean getInitialQuery() {
        return initialQuery;
    }

    public void setInitialQuery(boolean initialQuery) {
        this.initialQuery = initialQuery;
    }

    public boolean isCallbackOnNoResults() {
        return callbackOnNoResults;
    }

    public void setCallbackOnNoResults(boolean callbackOnNoResults) {
        this.callbackOnNoResults = callbackOnNoResults;
    }

    public EjecutadoPor getExecutedBy() {
        return executedBy;
    }

    public void setExecutedBy(EjecutadoPor executedBy) {
        this.executedBy = executedBy;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public boolean isSaveResponseInCache() {
        return saveResponseInCache;
    }

    public void setSaveResponseInCache(boolean saveResponseInCache) {
        this.saveResponseInCache = saveResponseInCache;
    }

    @Override
    public void generateHtml(ConstructorHtml html) {
        if (formElement != null && formElement.getVisible()) {
            WebPageEnviroment.addJavascriptInicial(toJS() + ";");
        }
    }

    @Override
    public String getType() {
        return "text";
    }

    public String toJS() {
        return GeneradorJS.toJS(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        if (formElement != null && formElement.getVisible()) {
            WebPageEnviroment.addJavascriptInicial(toJS() + ";");
        }
    }

}
