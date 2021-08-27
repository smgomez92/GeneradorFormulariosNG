package com.fitbank.web.uci.procesos;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.WrapDynaBean;

import net.sf.json.JSONObject;

import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.JSBehavior;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.assistants.ListOfValues;
import com.fitbank.webpages.behaviors.Link;
import com.fitbank.webpages.behaviors.Report;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.util.IterableWebElement;


@RevisarSeguridad
@Handler(GeneralRequestTypes.MANUAL_TECNICO)
public class ManualTecnico implements Proceso {

    @Override
    public RespuestaWeb procesar(PedidoWeb pedido) {
        WebPage webPage = EntornoWeb.getContexto().getWebPage();

        Detail detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();

        detail.addField(new Field("_CSUBSISTEMA", pedido.getTransporteDB().getSubsystem()));
        detail.addField(new Field("_CTRANSACCION", pedido.getTransporteDB().getTransaction()));
        detail.addField(new Field("_VERSIONTRANSACCION", "01"));

        detail.setSubsystem("01");
        detail.setTransaction("2020");

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        detail = ((TransporteDBUCI) respuesta.getTransporteDB()).getDetail();

        JSONObject res = new JSONObject();

        res.put("webPage", getWebPageJSON(webPage));

        List<String> names = new LinkedList<String>();

        names.add("BATCH");
        names.add("QUERY");
        names.add("REPORT");
        names.add("SECURITY");
        names.add("MAINTENANCE");
        names.add("COMANDOS-RUBRO_0");
        names.add("COMANDOS-TRANSACCION_0");

        getWebPageParts(webPage, res);
        getDetailParts(detail, res, names);

        respuesta.setContenido(res);
        respuesta.noCachear();

        return respuesta;
    }

    private JSONObject getWebPageJSON(WebPage webPage) {
        DynaBean db = new WrapDynaBean(webPage);
        JSONObject webPageJSON = new JSONObject();

        for (DynaProperty dp : db.getDynaClass().getDynaProperties()) {
            try {
                PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(webPage, dp.getName());

                if (pd.getReadMethod() != null) {
                    webPageJSON.put(dp.getName(), db.get(dp.getName()));
                }
            } catch (IllegalAccessException ex) {
                Debug.error(ex);
            } catch (InvocationTargetException ex) {
                Debug.error(ex);
            } catch (NoSuchMethodException ex) {
                Debug.error(ex);
            }
        }

        return webPageJSON;
    }

    private void getWebPageParts(WebPage webPage, JSONObject res) {
        List<ListOfValues> lovs = new LinkedList<ListOfValues>();
        List<Link> links = new LinkedList<Link>();
        List<Report> reports = new LinkedList<Report>();

        for (FormElement formElement : IterableWebElement.get(webPage, FormElement.class)) {
            if (formElement.getAssistant() instanceof ListOfValues) {
                lovs.add((ListOfValues) formElement.getAssistant());
            }
            for (JSBehavior jsBehavior : formElement.getBehaviors()) {
                if (jsBehavior instanceof Link) {
                    links.add((Link) jsBehavior);
                } else if (jsBehavior instanceof Report) {
                    reports.add((Report) jsBehavior);
                }
            }
        }

        res.put("lovs", lovs);
        res.put("links", links);
        res.put("reports", reports);
    }

    private void getDetailParts(Detail detail, JSONObject res, List<String> names) {

        List<String> fields = new ArrayList<String>();

        fields.add("EVENTO");
        fields.add("ORDEN");
        fields.add("COMANDO");
        fields.add("DESCRIPCION");
        fields.add("EJECUTADO_POR");

        Map<String, List> typeCommand = new HashMap<String, List>();

        for (String name : names) {
            Table commandTable = detail.findTableByName(name);
            List<Map> commands = new ArrayList<Map>();

            if (commandTable != null) {
                Map<String, Map> command = new HashMap<String, Map>();

                for (Record rec: commandTable.getRecords()) {
                    Map<String, String> commandInfo = new HashMap<String, String>();
                    String currentCommandOrder = rec.findFieldByName("ORDEN").getStringValue();

                    for (Field field: rec.getFields()) {
                        commandInfo.put(field.getName().toString(), field.getValue().toString());
                    }
                    command.put(currentCommandOrder, commandInfo);
                }
                commands.add(command);
                typeCommand.put(name, commands);
            }
        }

        res.put("commands", typeCommand);

    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta, String mensaje, String mensajeUsuario, String stackTrace, TransporteDB datos) {
        JSONObject res = new JSONObject();

        res.put("error", true);
        res.put("id", EntornoWeb.getSecuencia());

        respuesta.setContenido(res);

    }

}
