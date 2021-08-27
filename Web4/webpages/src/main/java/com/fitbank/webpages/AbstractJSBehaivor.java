package com.fitbank.webpages;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import com.fitbank.util.Editable;
import com.fitbank.webpages.data.FormElement;

/**
 * JSBehaivor abstracto.
 *
 * @author FitBank CI
 */
@Data
public class AbstractJSBehaivor implements JSBehavior, Serializable {

    @Getter(AccessLevel.PROTECTED)
    private FormElement formElement = null;

    @Editable
    private String message = "";

    @Editable
    private boolean fireAlways = false;

    @Override
    public String getElementName() {
        return formElement == null ? "" : formElement.getNameOrDefault();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public void setFormElement(FormElement formElement) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMessage() {
        //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return "";
    }

    @Override
    public void setMessage(String message) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isFireAlways() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return false;
    }

}
