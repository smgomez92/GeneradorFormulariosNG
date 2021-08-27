package com.fitbank.ifg;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Mensajes {
    private static final String BUNDLE_NAME = "com.fitbank.ifg.mensajes"; //$NON-NLS-1$

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
            .getBundle(BUNDLE_NAME);

    private Mensajes() {
    }

    public static String format(String key, Object... args) {
        try {
            return String.format(RESOURCE_BUNDLE.getString(key), args);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
