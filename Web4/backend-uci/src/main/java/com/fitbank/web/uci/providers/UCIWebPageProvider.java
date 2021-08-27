package com.fitbank.web.uci.providers;

import java.io.Serializable;
import java.text.MessageFormat;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.lang.StringUtils;

import com.fitbank.common.Uid;
import com.fitbank.dto.management.Detail;
import com.fitbank.dto.management.Field;
import com.fitbank.dto.management.Record;
import com.fitbank.dto.management.Table;
import com.fitbank.enums.TipoFila;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.util.Debug;
import com.fitbank.web.EntornoWeb;
import com.fitbank.web.ParametrosWeb;
import com.fitbank.web.data.PedidoWeb;
import com.fitbank.web.data.RespuestaWeb;
import com.fitbank.web.exceptions.ErrorWeb;
import com.fitbank.web.providers.WebPageProvider;
import com.fitbank.web.uci.EnlaceUCI;
import com.fitbank.web.uci.db.TransporteDBUCI;
import com.fitbank.webpages.AttachedWebPage;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.WebPageXml;
import com.fitbank.webpages.widgets.Input;
import com.fitbank.webpages.widgets.RemoteIFrame;
import net.sf.ehcache.Element;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import com.fitbank.web.ManejoExcepcion;

public class UCIWebPageProvider extends WebPageProvider {

    private static Cache cache = null;

    static {
        String cacheEnabled = ParametrosWeb.getValueString(
                WebPageProvider.class, "CACHE_ENABLED");
        if (cacheEnabled != null && cacheEnabled.equalsIgnoreCase("true")) {
            try {
                cache = CacheManager.getInstance().getCache("webPages");
                Debug.info("La cache de formularios se ha activado.");
            } catch (Throwable t) {
                Debug.error("No se pudo inicializar el cache", t);
            }
        }
    }

    @Override
    public WebPage getWebPage(PedidoWeb pedido, String subsystem,
            String transaction, boolean esAdjunto) {
        String webPage = null;
        String tipoFormato = null;
        String url = null;

        UCIWebPageProvider.WebPageCacheValue element = getFromCache(subsystem,
                transaction);

        if (element != null) {
            Debug.info("Cargando formulario " + subsystem + "-" + transaction + " desde Cache");
            if (!esAdjunto) {
                this.validateRequest(pedido, subsystem, transaction);
            }

            webPage = element.getXml();
            tipoFormato = element.getTipoFormato();
            url = element.getUrl();
        } else {
            Detail detail =
                    getFromUCI(pedido, subsystem, transaction, esAdjunto);

            Field frmField = detail.findFieldByName("FRM");

            if (frmField != null) {
                webPage = frmField.getStringValue();
            }

            Field tipoFormatoField = detail.findFieldByName("TIPOFORMATO");

            if (tipoFormatoField != null) {
                tipoFormato = tipoFormatoField.getStringValue();
            }

            Field urlField = detail.findFieldByName("URL");

            if (urlField != null) {
                url = urlField.getStringValue();
            }

            saveInCache(subsystem, transaction, webPage, tipoFormato, url);
        }

        return process(pedido, subsystem, transaction, webPage,
                tipoFormato, url);
    }

    private Detail getFromUCI(PedidoWeb pedido, String subsystem,
            String transaction, boolean esAdjunto) {
        Detail detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();

        // Guardar subsistema y transaccion originales
        String subsistemaOriginal = detail.getSubsystem();
        String transaccionOriginal = detail.getTransaction();
        String tipoOriginal = detail.getType();
        String versionOriginal = detail.getVersion();

        detail.setSubsystem(subsystem);
        detail.setTransaction(transaction);
        detail.setMessageId(Uid.getString());
        detail.setType(ParametrosWeb.getValueString(UCIWebPageProvider.class,
                "tipoMensaje"));
        detail.setVersion(ParametrosWeb.getValueString(
                UCIWebPageProvider.class, "version"));

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        Detail detailOut = ((TransporteDBUCI) respuesta.getTransporteDB()).
                getDetail();

        if (ManejoExcepcion.isError(detailOut.getResponse().getCode())) {
            throw new ErrorWeb(respuesta);
        }

        Table table = detailOut.findTableByName("TFORMATOXML");
        if (table != null) {
            for (Record record : table.getRecords()) {
                String subs = record.findFieldByName("CSUBSISTEMA").
                        getStringValue();
                String tran = record.findFieldByName("CTRANSACCION").
                        getStringValue();
                String xml = record.findFieldByName("FORMATOXML").
                        getStringValue();
                String tipoFormato = record.findFieldByName("TIPOFORMATO").
                        getStringValue();
                String url = record.findFieldByName("URL").getStringValue();

                attachedCache.put(subs + tran, xml);
                saveInCache(subs, tran, xml);
            }
        }

        // Restaurar valores en detail si fue una carga de un adjunto
        if (esAdjunto) {
            detail.setSubsystem(subsistemaOriginal);
            detail.setTransaction(transaccionOriginal);
            detail.setType(tipoOriginal);
            detail.setVersion(versionOriginal);
        }

        return detailOut;
    }

    private WebPage process(PedidoWeb pedido, String subsystem,
            String transaction, String webPageString, String tipoFormato,
            String url) {
        WebPage webPage = null;

        boolean allowExternal = ParametrosWeb.getValueBoolean(
                UCIWebPageProvider.class, "allowExternal");

        if (WebPage.class.getName().equals(tipoFormato)) {
            try {
                webPage = WebPageXml.parseString(webPageString);
            } catch (ExcepcionParser e) {
                throw new ErrorWeb("No se pudo leer el formulario desde la base"
                        + " de datos", e);
            }

        } else if (webPageString != null && allowExternal) {
            webPage = new WebPage();

            webPage.setSubsystem(subsystem);
            webPage.setTransaction(transaction);
            webPage.setTitle("");

            webPage.add(new Container());

            webPage.get(0).setTipoFila(TipoFila.COLUMNAS);
            webPage.get(0).setW(1024);
            RemoteIFrame ri = new RemoteIFrame();
            ri.setExpand(true);
            webPage.get(0).add(ri);

            Input input = new Input();
            input.setVisible(false);
            input.setValueInicial(webPageString);

            webPage.get(0).add(input);

        } else {
            throw new ErrorWeb("No se obtuvo un formulario de respuesta");
        }

        if (StringUtils.isNotBlank(url)) {
            WebPageEnviroment.setRemoteURL(url);

        } else {
            String template = ParametrosWeb.getValueString(
                    UCIWebPageProvider.class, "url");
            String usr = EntornoWeb.getTransporteDBBase().getUser();
            String session = EntornoWeb.getTransporteDBBase().getSessionId();
            String subs = pedido.getTransporteDB().getSubsystem();
            String trans = pedido.getTransporteDB().getTransaction();

            WebPageEnviroment.setRemoteURL(MessageFormat.format(template, usr,
                    session, subs, trans));
        }

        return webPage;
    }

    @Override
    public int getWeight() {
        return -1;
    }

    public static void saveInCache(String subsystem, String transaction,
            String xml) {
        saveInCache(subsystem + transaction, xml);
    }

    public static void saveInCache(String subsystem, String transaction,
            String xml, String tipoFormato, String url) {
        if (cache == null || StringUtils.isBlank(xml)) {
            return;
        }

        WebPageCacheValue webPageCacheValue = new WebPageCacheValue(xml);

        webPageCacheValue.setTipoFormato(tipoFormato);
        webPageCacheValue.setUrl(url);

        cache.put(new Element(subsystem + transaction, webPageCacheValue));
    }

    public static void saveInCache(String id, String xml) {
        if (cache == null || StringUtils.isBlank(xml)) {
            return;
        }

        WebPageCacheValue value = new WebPageCacheValue(xml);
        cache.put(new Element(id, value));
    }

    public static WebPageCacheValue getFromCache(String subsystem,
            String transaction) {
        if (cache == null) {
            return null;
        }

        Element item = cache.get(subsystem + transaction);

        if (item == null) {
            return null;
        } else {
            return (WebPageCacheValue) item.getValue();
        }
    }

    public static boolean deleteFromCache(String subsystem, String transaction) {
        if (cache == null) {
            return false;
        }

        return cache.remove(subsystem + transaction);
    }

    public static void deleteAttached(WebPage webPage) {
        if (cache == null) {
            return;
        }

        for (AttachedWebPage adjunto : webPage.getAttached()) {
            String key = adjunto.getSubsystem() + adjunto.getTransaction();
            Element cacheElement = cache.get(key);

            if (cacheElement != null) {
                WebPageCacheValue value = (WebPageCacheValue) cacheElement.
                        getValue();
                WebPage webPageAdjunto = null;

                try {
                    webPageAdjunto = WebPageXml.parseString(value.getXml());
                } catch (ExcepcionParser ex) {
                    Debug.error(ex);
                }

                if (webPageAdjunto != null) {
                    deleteAttached(webPageAdjunto);
                }

                cache.remove(key);
            }
        }
    }

    public void validateRequest(PedidoWeb pedido, String subsystem, String transaction) {
        Detail detail = ((TransporteDBUCI) pedido.getTransporteDB()).getDetail();

        // Guardar subsistema y transaccion originales
        String subsistemaOriginal = detail.getSubsystem();
        String transaccionOriginal = detail.getTransaction();
        String tipoOriginal = detail.getType();
        String versionOriginal = detail.getVersion();
        Integer seguridadOriginal = detail.getSecuritylevel();

        detail.setSubsystem(subsystem);
        detail.setTransaction(transaction);
        detail.setMessageId("CFV" + Uid.getString());
        detail.setSecuritylevel(0);
        detail.setType(ParametrosWeb.getValueString(UCIWebPageProvider.class,
                "tipoMensaje"));
        detail.setVersion(ParametrosWeb.getValueString(
                UCIWebPageProvider.class, "version"));

        RespuestaWeb respuesta = new EnlaceUCI().procesar(pedido);

        Detail detailOut = ((TransporteDBUCI) respuesta.getTransporteDB()).
                getDetail();

        if (ManejoExcepcion.isError(detailOut.getResponse().getCode())) {
            throw new ErrorWeb(respuesta);
        }

        //Restaurar valores originales
        detail.setSubsystem(subsistemaOriginal);
        detail.setTransaction(transaccionOriginal);
        detail.setType(tipoOriginal);
        detail.setVersion(versionOriginal);
        detail.setSecuritylevel(seguridadOriginal);
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class WebPageCacheValue implements Serializable {

        private final String xml;

        private String tipoFormato = WebPage.class.getName();

        private String url = null;

    }

}
