package com.fitbank.web.uci;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import com.fitbank.common.helper.XMLParser;
import com.fitbank.dto.GeneralResponse;
import com.fitbank.dto.management.Criterion;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.EjecutadoPor;
import com.fitbank.uci.client.UCIClient;
import com.fitbank.util.Debug;
import com.fitbank.web.Contexto;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.procesos.Registro;
import com.fitbank.web.providers.HardDiskWebPageProvider;
import com.fitbank.web.servlets.Procesador;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageEnviroment;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class EnlaceUCI {

    /**
     * Cache de respuestas de formularios.
     */
    private static Cache cache = null;

    static {
        try {
            if (ParametrosWeb.getValueBoolean(EnlaceUCI.class, "useDetailHash")) {
                cache = CacheManager.getInstance().getCache("listOfValues");
                Debug.info("La cache de listas de valores se ha activado.");
            }
        } catch (CacheException t) {
            Debug.error("No se pudo inicializar el cache de listas de valores", t);
        } catch (IllegalStateException t) {
            Debug.error("No se pudo inicializar el cache de listas de valores", t);
        } catch (ClassCastException t) {
            Debug.error("No se pudo inicializar el cache de listas de valores", t);
        }
    }

    /**
     * Indica si necesitamos guardar el detail de entrada/salida de este mensaje
     * para recuperarlo directamente en futuros mensajes, sin necesidad de ir a
     * UCI.
     */
    private final boolean useLocalTransportDB;

    /**
     * Constructor por defecto que envía todas las peticiones al UCI.
     */
    public EnlaceUCI() {
        this(false);
    }

    /**
     * Contructor indicando que busque una respuesta desde la caché, antes de ir
     * a UCI.
     *
     * @param useLocalTransportDB Indica si es necesario cargar un detail de
     * salida desde la caché antes de ir a UCI
     */
    public EnlaceUCI(boolean useLocalTransportDB) {
        boolean defaultUseHash = ParametrosWeb.getValueBoolean(EnlaceUCI.class, "useDetailHash");

        this.useLocalTransportDB = defaultUseHash ? useLocalTransportDB : false;
    }

    public RespuestaWeb procesar(PedidoWeb pedido) {
        TransporteDBUCI tdbuci = (TransporteDBUCI) pedido.getTransporteDB();
        Detail detail = tdbuci.getDetail();

        //Detectar segmentación especial para este mensaje
        this.detectarEjecutadoPor(detail, pedido);

        //Detectar timeout especial para este mensaje
        this.detectarTimeout(detail, pedido);

        if (WebPageEnviroment.getDebug()) {
            detail.findFieldByNameCreate("__DEBUG__").setValue("true");
        } else {
            detail.removeField("__DEBUG__");
        }

        tdbuci.save();

        Registro.getRegistro().salvarDatosEntrada(pedido.getTransporteDB());

        String mode = StringUtils.defaultIfEmpty(Procesador.getMode(), "");

        if ("save".equals(mode)) {
            save(tdbuci);
        }

        TransporteDBUCI datosRespuesta = null;
        if ("replay".equals(mode)) {
            datosRespuesta = new TransporteDBUCI(load(detail));
        } else {
            if (this.useLocalTransportDB) {
                try {
                    datosRespuesta = loadTransportDB(detail);
                } catch (ErrorWeb e) {
                    Debug.error("Problemas al cargar un transportedb en cache", e);
                }
            }

            if (datosRespuesta == null) {
                datosRespuesta = new TransporteDBUCI(fix(UCIClient.send(detail)));
            }

            if ("save".equals(mode)) {
                save(detail, datosRespuesta);
            }

            if (this.useLocalTransportDB) {
                saveTransportDB(detail, datosRespuesta);
            }
        }

        Registro.getRegistro().salvarDatosSalida(datosRespuesta);

        RespuestaWeb respuesta = new RespuestaWeb(datosRespuesta, pedido);
        respuesta.setTipoPedido(pedido.getTipoPedido());
        this.limpiarEjecutadoPor(detail);
        this.limpiarEjecutadoPor(((TransporteDBUCI) respuesta.getTransporteDB()).getDetail());
        return respuesta;
    }

    /**
     * En caso de que venga un Detail sin alias en campos asumir que son campos
     * de la tabla principal.
     *
     * @param detail Detail a ser procesado
     *
     * @return el mismo Detail despues de ser procesado
     */
    private Detail fix(Detail detail) {
        GeneralResponse res = detail.getResponse();
        if (res != null && res.getUserMessage() != null) {
            String code = res.getCode() + ": ";
            String mes = res.getUserMessage();

            if (mes.startsWith(code)) {
                mes = mes.substring(code.length());
            }

            if (res.getUserMessage().endsWith(" <*>")) {
                mes = mes.substring(0, mes.length() - 4);
            }

            res.setUserMessage(mes);
        }

        if (Conversor.JOIN_PROCESS_TYPE.equals(detail.getProcessType())) {
            for (Table table : detail.getTables()) {
                table.addMissing();
            }
        }

        return detail;
    }

    private void save(TransporteDBUCI tdbuci) {
        save(tdbuci.toString(), tdbuci.getDetail(), "entrada");
    }

    private void save(Detail detail, TransporteDBUCI datosRespuesta) {
        save(datosRespuesta.toString(), detail, "salida");
    }

    private void save(String xml, Detail detail, String var) {
        File path = new File(HardDiskWebPageProvider.getPath(
                detail.getSubsystem(), detail.getTransaction(), "test.d"));
        path.mkdirs();

        File file = new File(path, getSignature(detail) + "-" + var + ".xml");
        try {
            IOUtils.write(xml, new FileOutputStream(file), "UTF-8");
        } catch (Exception ex) {
            Debug.error(ex);
        }
    }

    private Detail load(Detail detail) {
        File path = new File(HardDiskWebPageProvider.getPath(
                detail.getSubsystem(), detail.getTransaction(), "test.d"));
        path.mkdirs();

        File file = new File(path, getSignature(detail) + "-salida.xml");

        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), "UTF8"));

            String data = StringUtils.EMPTY;
            String line;
            while ((line = in.readLine()) != null) {
                data += line;
            }

            in.close();

            return new Detail(new XMLParser(data));
        } catch (Exception e) {
            throw new ErrorWeb("No se encontró el detail correspondiente", e);
        }
    }

    /**
     * Obtiene una firma del detail única para poder identificarlo.
     *
     * @param detail
     * @return
     */
    private String getSignature(Detail detail) {
        int hash = 0;
        int i = 0;

        for (Table table : detail.getTables()) {
            // Tomar el alias, registro actual y página de la tabla para el cálculo
            hash += hash(table.getAlias());
            hash += hash(table.getCurrentRecord());
            hash += hash(table.getPageNumber());
            hash += 1 << i++;

            for (Criterion criterion : table.getCriteria()) {
                // Tomar alias y nombre del criterio
                hash += hash(criterion.getAlias());
                hash += hash(criterion.getName());
                hash += hash(criterion.getValue());
                hash += hash(criterion.getOrder());
                hash += hash(criterion.getCondition());
                hash += 1 << i++;
            }

            for (Record record : table.getRecords()) {
                // Tomar el número de registro
                hash += hash(record.getNumber());
                hash += 1 << i++;

                for (Field field : record.getFields()) {
                    // Tomar el alias y nombre del campo
                    hash += hash(field.getAlias());
                    hash += hash(field.getName());
                    hash += hash(field.getFunctionName());
                    hash += hash(field.getValue());
                    hash += 1 << i++;
                }
            }
        }

        for (Field field : detail.getFields()) {
            // Tomar el alias y nombre del campo
            hash += hash(field.getName());
            hash += hash(field.getValue());
            hash += 1 << i++;
        }

        return String.format("%s%s-%s-%s", detail.getSubsystem(), detail.
                getTransaction(), detail.getType(), hash);
    }

    public int hash(Object o) {
        if (o == null) {
            return 0;
        } else {
            return o.hashCode();
        }
    }

    /**
     * Asigna el valor de ejecutadoPor al detail basado en un campo de control
     *
     * @param detail
     */
    private void detectarEjecutadoPor(Detail pDetail, PedidoWeb request) {
        //Detectar si existe un contexto padre, y usarlo
        String idContextoPadre = request.getValorRequestHttp("_contexto_padre");
        Contexto contexto = EntornoWeb.getContexto();
        if (StringUtils.isNotBlank(idContextoPadre)) {
            contexto = EntornoWeb.getContexto(idContextoPadre);
        }

        //Detectar si se ha definido un timeout a nivel del formulario y usarlo
        WebPage webPage = contexto.getWebPage();
        EjecutadoPor ejecutadoPor = webPage != null ? webPage.getEjecutadoPor() : null;
        if (ejecutadoPor != null && !EjecutadoPor.FORMULARIO.equals(ejecutadoPor)) {
            pDetail.setExecutedBy(ejecutadoPor.getValue());
        }

        //En caso de haber una ejecución dinámica definida en el form, usarla
        Field field = pDetail.findFieldByName("EJECUTADOPOR");
        if (field != null && field.getValue() != null
                && StringUtils.isNotBlank(field.getStringValue())) {
            pDetail.setExecutedBy(field.getStringValue());
        }

        //En caso de haber una ejecución dinámica definida en el form, usarla
        field = pDetail.findFieldByName("__EJECUTADOPOR__");
        if (field != null && field.getValue() != null
                && StringUtils.isNotBlank(field.getStringValue())) {
            pDetail.setExecutedBy(field.getStringValue());
        }
    }

    /**
     * Asigna el valor de timeout al detail basado en un campo de control
     *
     * @param pDetail Mensaje de entrada
     * @param request PedidoWeb asociado al mensaje
     */
    private void detectarTimeout(Detail pDetail, PedidoWeb request) {
        //Detectar si existe un contexto padre, y usarlo
        String idContextoPadre = request.getValorRequestHttp("_contexto_padre");
        Contexto contexto = EntornoWeb.getContexto();
        if (StringUtils.isNotBlank(idContextoPadre)) {
            contexto = EntornoWeb.getContexto(idContextoPadre);
        }

        //Detectar si se ha definido un timeout a nivel del formulario y usarlo
        WebPage webPage = contexto.getWebPage();
        Integer timeout = webPage != null ? webPage.getTimeout() : null;
        if (timeout != null && timeout > 0) {
            pDetail.setTimeout(timeout);
        }

        //En caso de definir timeouts dinamicos, remplazar el timeout del formulario.
        Field field = pDetail.findFieldByName("__TIMEOUT__");
        if (field != null && field.getValue() != null
                && StringUtils.isNotBlank(field.getStringValue())) {
            pDetail.setTimeout(field.getIntegerValue());
        }
    }

    private void limpiarEjecutadoPor(Detail detail) {
        detail.setExecutedBy("F");
        detail.findFieldByNameCreate("EJECUTADOPOR").setValue("F");
    }

    /**
     * Registar un transporteDB obtenido en base al hash del detail original
     *
     * @param detailIn Mensaje de entrada
     * @param datosRespuesta Respuesta generada con el detail de entrada
     */
    private void saveTransportDB(Detail detailIn, TransporteDBUCI datosRespuesta) {
        String hash = getSignature(detailIn);
        Element e = new Element(hash, datosRespuesta.toString());
        cache.put(e);
    }

    /**
     * Carga un detail de respuesta almacenado en cache
     *
     * @param detailIn Mensaje de entrada para computar el hash de comparacion
     * @return Detail de salida almacenado en cache
     */
    private TransporteDBUCI loadTransportDB(Detail detailIn) {
        String hash = getSignature(detailIn);
        Element element = cache.get(hash);

        if (element != null) {
            try {
                return new TransporteDBUCI(new XMLParser((String) element.getValue()));
            } catch (Exception ex) {
                Debug.error("Problemas al leer un detail en cache", ex);
            }
        }

        return null;
    }
}
