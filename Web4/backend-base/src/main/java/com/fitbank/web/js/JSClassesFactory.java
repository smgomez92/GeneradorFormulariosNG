package com.fitbank.web.js;

import com.fitbank.util.Servicios;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.IteratorUtils;

/**
 * Fabrica de proveedores de clases js (JSClasses)
 *
 * @author Soft Warehouse S.A.
 */
public class JSClassesFactory {

    private static final List<JSClasses> JS_CLASSES = new ArrayList<JSClasses>();

    static {
        JS_CLASSES.addAll(IteratorUtils.toList(Servicios.load(JSClasses.class).iterator()));
    }

    public static List<JSClasses> listJSClasses() {
        return JS_CLASSES;
    }
}
