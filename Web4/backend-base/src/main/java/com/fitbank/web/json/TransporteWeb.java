package com.fitbank.web.json;

import com.fitbank.enums.Modificable;
import com.fitbank.serializador.html.SerializadorHtml;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.WebPageUtils;
import com.fitbank.webpages.data.FieldData;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.formulas.FormulaException;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.WebPageFormulasUtils;
import com.fitbank.webpages.widgets.DeleteRecord;
import java.beans.PropertyDescriptor;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * Clase que sirve de transporte entre el contenedor web y la aplicación js.
 * Puede transportar tanto una pagina web en html como los valores. Además
 * transporta datos de error si fuera el caso.
 * 
 * @author FitBank
 * @version 2.0
 */
public class TransporteWeb {

    private final RespuestaWeb respuesta;

    private final WebPage webPage;

    private final boolean initial;

    private final boolean tieneCuenta;

    private final String codigo_flujo;

    private final String codigo_instancia;

    private final String codigo_enlace;

    private final boolean drawHtml;

    private boolean refreshDB = false;

    protected TransporteWeb() {
        this.respuesta = null;
        this.webPage = null;
        this.initial = true;
        this.tieneCuenta = false;
        this.drawHtml = false;
        this.codigo_flujo = null;
        this.codigo_instancia = null;
        this.codigo_enlace = null;
    }

    public TransporteWeb(RespuestaWeb respuesta, WebPage webPage,
            boolean initial, boolean tieneCuenta, boolean drawHtml) {
        this.respuesta = respuesta;
        this.webPage = webPage;
        this.initial = initial;
        this.tieneCuenta = tieneCuenta;
        this.drawHtml = drawHtml;
        this.codigo_flujo = null;
        this.codigo_instancia = null;
        this.codigo_enlace = null;
    }

    public TransporteWeb(RespuestaWeb respuesta, WebPage webPage, String codigo_flujo, String codigo_instancia,
                         String codigo_enlace, boolean initial, boolean tieneCuenta, boolean drawHtml) {
        this.respuesta = respuesta;
        this.webPage = webPage;
        this.initial = initial;
        this.tieneCuenta = tieneCuenta;
        this.drawHtml = drawHtml;
        this.codigo_flujo = codigo_flujo;
        this.codigo_instancia = codigo_instancia;
        this.codigo_enlace = codigo_enlace;
    }

    public TransporteWeb(RespuestaWeb respuesta, WebPage webPage, 
            boolean initial, boolean tieneCuenta) {
        this.respuesta = respuesta;
        this.webPage = webPage;
        this.initial = initial;
        this.tieneCuenta = tieneCuenta;
        this.drawHtml = false;
        this.codigo_flujo = null;
        this.codigo_instancia = null;
        this.codigo_enlace = null;
    }

    public TransporteWeb(RespuestaWeb respuesta, WebPage webPage, String codigo_flujo, String codigo_instancia,
                         String codigo_enlace, boolean initial, boolean tieneCuenta) {
        this.respuesta = respuesta;
        this.webPage = webPage;
        this.initial = initial;
        this.tieneCuenta = tieneCuenta;
        this.drawHtml = false;
        this.codigo_flujo = codigo_flujo;
        this.codigo_instancia = codigo_instancia;
        this.codigo_enlace = codigo_enlace;
    }

    public TransporteWeb(RespuestaWeb respuesta, WebPage webPage,
            boolean initial) {
        this.respuesta = respuesta;
        this.webPage = webPage;
        this.initial = initial;
        this.tieneCuenta = false;
        this.drawHtml = false;
        this.codigo_flujo = null;
        this.codigo_instancia = null;
        this.codigo_enlace = null;
    }

    public TransporteWeb(RespuestaWeb respuesta, WebPage webPage) {
        this.respuesta = respuesta;
        this.webPage = webPage;
        this.initial = true;
        this.tieneCuenta = false;
        this.drawHtml = false;
        this.codigo_flujo = null;
        this.codigo_instancia = null;
        this.codigo_enlace = null;
    }

    public TransporteWeb(RespuestaWeb respuesta) {
        this.respuesta = respuesta;
        this.webPage = null;
        this.initial = true;
        this.tieneCuenta = false;
        this.drawHtml = false;
        this.codigo_flujo = null;
        this.codigo_instancia = null;
        this.codigo_enlace = null;
    }

    // ////////////////////////////////////////////////////////
    // Métodos Getters y Setters
    // ////////////////////////////////////////////////////////
    public WebPage getWebPage() {
        return webPage;
    }

    @SuppressWarnings("unchecked")
    public String toJSON() {
        JSONObject res = new JSONObject();

        if (initial) {
            String html = new SerializadorHtml().serializar(webPage);

            generateExtraJS(webPage, respuesta.getTransporteDB());

            res.element("actual", this.webPage.getURI());
            res.element("titulo", this.webPage.getTitle());
            res.element("html", html);
            res.element("formulas", WebPageEnviroment.getFormulas());
            res.element("calculos", this.webPage.getCalculos());
            res.element("jsInicial", WebPageEnviroment.getJavascriptInicial());
            res.element("jsInicialWebPage", this.webPage.getInitialJS());
            res.element("store", this.webPage.getStore());
            res.element("requiresQuery", this.webPage.getRequiresQuery());
            res.element("clean", this.webPage.getClean());
            res.element("postQuery", this.webPage.getPostQuery());
            res.element("firstFocus", WebPageEnviroment.getFirstFocus());
            res.element("queryFocus", this.webPage.getQueryFocus());
            res.element("paginacion", this.webPage.getPaginacion().ordinal());
            res.element("drawHtml", this.drawHtml);

            Map<String, Object> val = new HashMap<String, Object>();
            DynaBean db = new WrapDynaBean(respuesta.getTransporteDB());

            for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(
                    TransporteDB.class)) {
                val.put(pd.getName(), db.get(pd.getName()));
            }
            if (respuesta.getTransporteDB().getAccountingDate() != null) {
                SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                String stFecha = df.format(respuesta.getTransporteDB().getAccountingDate());
                val.put("accountingDate", stFecha);
            }

            res.element("db", val);
        }

        if (refreshDB) {
            Map<String, Object> val = new HashMap<String, Object>();
            DynaBean db = new WrapDynaBean(respuesta.getTransporteDB());

            for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(
                    TransporteDB.class)) {
                val.put(pd.getName(), db.get(pd.getName()));
            }
            if (respuesta.getTransporteDB().getAccountingDate() != null) {
                SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
                String stFecha = df.format(respuesta.getTransporteDB().getAccountingDate());
                val.put("accountingDate", stFecha);
            }

            res.element("db", val);
        }

        Map<String, JSONObject> elementValues =
                new LinkedHashMap<String, JSONObject>();

        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            boolean isFormula = formElement.getRelleno().startsWith("=");
            if (!(isFormula && initial)) {
                String name = formElement.getNameOrDefault();
                elementValues.put(name, getFieldData(formElement));
            }
        }

        for (DeleteRecord deleteRecord : IterableWebElement.get(webPage,
                DeleteRecord.class)) {
            String name = deleteRecord.getNameOrDefault();
            elementValues.put(name, getFieldData(deleteRecord));
        }

        res.element("values", elementValues);
        res.element("messageId", respuesta.getTransporteDB().getMessageId());
        res.element("navegacion", respuesta.getTransporteDB().getNavigation());
        res.element("notifica", this.tieneCuenta);
        res.element("codigo_flujo", this.codigo_flujo);
        res.element("codigo_instancia_flujo", this.codigo_instancia);
        res.element("codigo_enlace", this.codigo_enlace);
        setResponse(res);

        return res.toString();
    }

    public void setResponse(JSONObject res) {
        res.element("codigo", respuesta.getTransporteDB().getResponseCode());
        res.element("mensajeUsuario", respuesta.getTransporteDB().getMessage());
        res.element("stack", StringEscapeUtils.escapeHtml(respuesta.
                getTransporteDB().getStackTrace()));
    }

    private JSONObject getFieldData(DeleteRecord deleteRecord) {
        JSONObject data = new JSONObject();
        final int query = deleteRecord.getParentContainer().
                getNumeroDeFilasConsultadas();
        FieldData fieldData = deleteRecord.getFieldData();

        data.element("disabled", CollectionUtils.collect(fieldData.getDisabled(),
                new Transformer() {

                    private int i = 0;

                    public Object transform(Object input) {
                        return i++ >= query;
                    }

                }));

        data.element("values", fieldData.getValues());

        readFieldData(data, fieldData);

        return data;
    }

    private JSONObject getFieldData(final FormElement formElement) {
        JSONObject data = new JSONObject();
        final boolean disabled = formElement.getModificable()
                == Modificable.SOLO_LECTURA;
        FieldData fieldData = formElement.getFieldData();

        data.element("disabled", CollectionUtils.collect(fieldData.getDisabled(),
                new Transformer() {

                    public Object transform(Object input) {
                        return disabled || ((Boolean) input).booleanValue();
                    }

                }));

        readFieldData(data, fieldData);

        data.element("values", WebPageUtils.format(formElement));

        return data;
    }

    private void readFieldData(JSONObject data, FieldData fieldData) {
        data.element("error", fieldData.getErrors());
        fieldData.resetErrors();

        data.element("classNames", fieldData.getExtraClasses());
        fieldData.resetExtraClasses();
    }

    private void generateExtraJS(WebPage webPage, TransporteDB transporteDB) {
        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            try {
                WebPageFormulasUtils.process(formElement, transporteDB);
            } catch (FormulaException ex) {
                throw new ErrorWeb(ex);
            }
        }
    }

    public void setRefreshDB(boolean refreshDB) {
        this.refreshDB = refreshDB;
    }
}