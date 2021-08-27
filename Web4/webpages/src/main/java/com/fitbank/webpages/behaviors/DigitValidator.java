package com.fitbank.webpages.behaviors;

import com.fitbank.util.Editable;
import com.fitbank.webpages.AbstractJSBehaivor;

/**
 * Validar un campo con el algoritmo seleccionado
 *
 * @author Fitbank JB
 */
public class DigitValidator extends AbstractJSBehaivor {

    public enum ValidationTypes {
        ID_ECUADOR, RUC_ECUADOR;
    }

    @Editable
    private ValidationTypes validationType = ValidationTypes.ID_ECUADOR;
    
    public ValidationTypes getValidationType() {
        return validationType;
    }

    public void setValidationType(ValidationTypes validationType) {
        this.validationType = validationType;
    }
}