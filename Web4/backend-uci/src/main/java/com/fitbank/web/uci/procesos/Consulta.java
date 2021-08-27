package com.fitbank.web.uci.procesos;

import com.fitbank.dto.management.Criterion;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.MessageType;
import com.fitbank.util.Pair;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.ManejoExcepcion;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.Proceso;
import com.fitbank.web.RevisarSeguridad;
import com.fitbank.web.annotations.Handler;
import com.fitbank.web.data.Paginacion;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.exceptions.MensajeWeb;
import com.fitbank.web.json.TransporteWeb;
import com.fitbank.web.uci.Conversor;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.util.ArbolDependencias;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.NodoDependencia;
import com.fitbank.webpages.widgets.DeleteRecord;
import java.util.List;
import org.apache.commons.lang.StringUtils;

@Handler(GeneralRequestTypes.CONSULTA)
@RevisarSeguridad
public class Consulta implements Proceso {

    //Código del flujo que se desea ejecutar
    public static final String CODIGO_FLUJO_WORKFLOW = "CODIGO_FLUJO_WORKFLOW";

    //Código de la instancia de flujo que se desea ejecutar
    public static final String CODIGO_FLUJO_WORKFLOW_INSTANCIA = "CODIGO_FLUJO_WORKFLOW_INSTANCIA";

    //Código de la instancia de flujo que se desea ejecutar
    public static final String CODIGO_ENLACE_WORKFLOW_INSTANCIA = "CODIGO_ENLACE_WORKFLOW_INSTANCIA";

    @Override
    public RespuestaWeb procesar(PedidoWeb request) {
        WebPage webPage = EntornoWeb.getContexto().getWebPage();

        request.getTransporteDB().setMessageType(MessageType.QUERY);

        Detail detail = ((TransporteDBUCI) request.getTransporteDB()).getDetail();

        limpiarDetail(detail);
        revisarRequeridos(webPage);
        Conversor.crearTablas(webPage, detail);
        llenarTablas(webPage, detail);

        RespuestaWeb respuesta = new EnlaceUCI().procesar(request);

        ManejoExcepcion.checkOkCodes(respuesta);
        EntornoWeb.getContexto().setHayDatos(true);

        resetearCampos(webPage, true);
        Conversor.llenar(respuesta);

        //Verificar si existe informacion de una cuenta en el detail de salida
        Detail responseDetail = ((TransporteDBUCI) respuesta.getTransporteDB()).getDetail();
        boolean searchComments = false;
        List<String> fieldNames = ParametrosWeb.getValueStringList(Consulta.class, "NOTIFICATION_FIELD");
        for (String fieldName : fieldNames) {
            Pair<String, String> pair = Conversor.findFieldInDetail(responseDetail, fieldName);
            String fieldRealName = pair.getFirst();
            String fieldValue = pair.getSecond();

            //Ignorar el cpersona_usuario y cperson_compania especificamente
            if ("CPERSONA_COMPANIA".equals(fieldRealName) 
                    || "CPERSONA_USUARIO".equals(fieldRealName)) {
                continue;
            }

            searchComments = searchComments || StringUtils.isNotBlank(fieldValue);
        }

        TransporteWeb transporte = this.prepareTransporteWeb(respuesta, webPage, searchComments, detail);
        respuesta.setContenido(transporte);

        EntornoWeb.getContexto().setTransporteDBBase(respuesta.getTransporteDB());

        return respuesta;
    }

    /**
     * Limpia el detail.
     *
     * @param detail
     */
    private static void limpiarDetail(Detail detail) {
        detail.removeTables();
        detail.removeFields();
    }

    /**
     * Revisa si existen criterios faltantes.
     * 
     * @param webPage
     * 
     * @throws ErrorWeb
     */
    private void revisarRequeridos(WebPage webPage) throws MensajeWeb {
        boolean check = false;

        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            if (Conversor.esCriterioRequerido(formElement)) {
                Conversor.marcarRequerido(formElement, 0);
                check = true;
            }
        }

        if (check) {
            throw new MensajeWeb("Es necesario escoger un criterio");
        }
    }

    /**
     * Limpia los valores de los campos que no son criterios.
     * 
     * @param webPage WebPage contenedor de los campos a limpiar.
     * @param consulta Indica si es un proceso de consulta.
     */
    protected static void resetearCampos(WebPage webPage, boolean consulta) {
        for (Container container : webPage) {
            container.setNumeroDeFilasConsultadas(false, 0);
        }

        ArbolDependencias arbol = EntornoWeb.getContexto().getArbolDependencias();

        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            boolean limpiar = false;
            DataSource dataSource = formElement.getDataSource();

            if (dataSource.esRegistro()) {
                NodoDependencia nodo = arbol.getNodos().get(dataSource.getAlias());
                if (nodo != null) {
                    Reference reference = nodo.getReference();
                    if (consulta && reference.isStoreOnly()) {
                        continue;
                    }

                    if (!consulta && (dataSource.esDescripcion() || reference.isQueryOnly())) {
                        continue;
                    }
                }

                if (consulta || !dataSource.esDescripcion()) {
                    limpiar = true;
                }
            } else if (consulta && formElement.getRegistrosConsulta() > 1) {
                limpiar = true;
            }

            if (limpiar) {
                if (formElement.getLimpiable()) {
                    formElement.getFieldData().resetAll();
                } else {
                    formElement.getFieldData().resetStates();
                }
            }
        }

        for (DeleteRecord deleteRecord : IterableWebElement.get(webPage,
                DeleteRecord.class)) {
            deleteRecord.getFieldData().resetAll();
        }
    }

    /**
     * Construye las tablas y llena con los valores del formulario.
     * 
     * @param webPage
     * @param detail
     */
    public static void llenarTablas(WebPage webPage, Detail detail) {
        // Setear valores en el detail
        for (FormElement formElement : IterableWebElement.get(webPage,
                FormElement.class)) {
            Conversor.convertirFormElementConsulta(webPage, formElement, detail);
        }

        Conversor.copiarDependencias(webPage, detail);

        // Copiar criterios al registro si no existen
        for (Table table : detail.getTables()) {
            Record record = table.findRecordByNumber(0);
            for (Criterion criterion : table.getCriteria()) {
                Field field = new Field(criterion.getAlias(), criterion.getName(), null);
                record.findFieldByExample(field);
            }
        }

        for (Reference referencia : webPage.getReferences()) {
            String tableAlias = referencia.getAlias();
            if (referencia.isStoreOnly() && detail.findTableByAlias(tableAlias) != null) {
                detail.removeTable(tableAlias);
            }
        }
    }

    /**
     * Metodo que verifica si un campo Field tiene contenido.
     * @param field Campo del Detail
     * @return true si tiene contenido
     */
    private static boolean fieldHasData(Field field) {
        return field != null && !StringUtils.isEmpty(field.getStringValue());
    }

    @Override
    public void onError(PedidoWeb pedido, RespuestaWeb respuesta,
            String mensaje, String mensajeUsuario, String stackTrace,
            TransporteDB datos) {
        respuesta.getTransporteDB().setMessage(mensajeUsuario);
        respuesta.getTransporteDB().setStackTrace(stackTrace);

        WebPage webPage = EntornoWeb.getContexto().getWebPage();

        if (webPage == null) {
            webPage = new WebPage();
        }

        TransporteWeb transporte = new TransporteWeb(respuesta, webPage, false);

        respuesta.setContenido(transporte);
    }

    /**
     * Metodo que valida la respuesta para crear webpages dinamicos.
     * 
     * @param respuesta Respuesta UCI
     * @param webPage WebPage original
     * @return TransporteWeb con el webpage dinamico en caso de requerirlo
     */
    private TransporteWeb prepareTransporteWeb(RespuestaWeb respuesta, WebPage webPage, boolean consultarComentarios, Detail detail) {
        String codigo_flujo = ((TransporteDBUCI) respuesta.getTransporteDB()).
                getDetail().findFieldByNameCreate(Consulta.CODIGO_FLUJO_WORKFLOW).getStringValue();
        String codigo_instancia = ((TransporteDBUCI) respuesta.getTransporteDB()).
                getDetail().findFieldByNameCreate(Consulta.CODIGO_FLUJO_WORKFLOW_INSTANCIA).getStringValue();
        String codigo_enlace = ((TransporteDBUCI) respuesta.getTransporteDB()).
                getDetail().findFieldByNameCreate(Consulta.CODIGO_ENLACE_WORKFLOW_INSTANCIA).getStringValue();
        //Armar un transporteWeb estandar para respuestas de consultas (sin dibujar formularios)
        TransporteWeb transporte =
                new TransporteWeb(respuesta, webPage, codigo_flujo, codigo_instancia, codigo_enlace, false,
                        consultarComentarios);

        //Buscar el campo de control que indica que se debe armar una tabla dinamica
        Detail detailOut = ((TransporteDBUCI) respuesta.getTransporteDB()).getDetail();
        Field fTable = detailOut.findFieldByName("__TABLE_NAME__");
        if (fTable != null && fTable.getValue() != null 
                && StringUtils.isNotBlank(fTable.getStringValue())) {
            String tableName = fTable.getStringValue();
            Table table = detailOut.findTableByName(tableName);
            if (table != null) {
                //Armar el webPage dinamicamente
                webPage = Conversor.generateWebPageFromTable(table, webPage, 
                        "CONSULTA DE INFORMACIÓN HISTÓRICA DE LA TABLA " + table.getName(), true);

                //Actualizar el webPage del contexto actual
                EntornoWeb.getContexto().setWebPage(webPage);

                //Reiniciar la paginación con el nuevo formulario
                EntornoWeb.getContexto().setPaginacion(new Paginacion());
            }
            //Armar un nuevo transporteWeb indicnadole que debe dibujar el nuevo formulario
            transporte = new TransporteWeb(respuesta, webPage, codigo_flujo, codigo_instancia, codigo_enlace, true,
                    consultarComentarios, true);
        }

        return transporte;
    }
}