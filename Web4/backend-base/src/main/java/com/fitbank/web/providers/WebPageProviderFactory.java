package com.fitbank.web.providers;

import com.fitbank.util.Servicios;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.IteratorUtils;

/**
 * Fábrica de proveedores de formularios.
 * 
 * @author Soft Warehouse S.A
 */
public class WebPageProviderFactory {

    private static final List<WebPageProvider> PROVIDERS = new ArrayList<WebPageProvider>();

    static {
        PROVIDERS.addAll(IteratorUtils.toList(Servicios.load(WebPageProvider.class).iterator()));
    }

    public static List<WebPageProvider> listProviders() {
        return PROVIDERS;
    }
}
