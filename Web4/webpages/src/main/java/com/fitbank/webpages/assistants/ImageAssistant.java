package com.fitbank.webpages.assistants;

import com.fitbank.util.Editable;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.widgets.Image;

/**
 * Asistente que se usa automáticamente en los widgets tipo Image.
 * No usar en otros widgets.
 *
 * @author FitBank CI
 */
public class ImageAssistant extends Scanner {

    private static final long serialVersionUID = 1L;

    @Editable
    private boolean showScannerLink = false;

    @Override
    public void init(FormElement formElement) {
        if (!(formElement instanceof Image)) {
            throw new RuntimeException("No se puede aplicar "
                    + ImageAssistant.class.getName() + " a un " + formElement.
                    getClass().getName());
        }
        super.init(formElement);
    }

    public boolean getShowScannerLink() {
        return showScannerLink;
    }

    public void setShowScannerLink(boolean showScannerLink) {
        this.showScannerLink = showScannerLink;
    }

}
