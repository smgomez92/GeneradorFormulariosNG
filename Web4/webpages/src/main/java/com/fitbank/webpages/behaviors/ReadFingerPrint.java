package com.fitbank.webpages.behaviors;

import com.fitbank.webpages.AbstractJSBehaivor;
import com.fitbank.util.Editable;

public class ReadFingerPrint extends AbstractJSBehaivor {

    @Editable
    private String idElementName = "";

    @Editable
    private String resultElementName = "";

    @Editable
    private String qualityElementName = "";

    public String getIdElementName() {
        return idElementName;
    }

    public void setIdElementName(String idElementName) {
        this.idElementName = idElementName;
    }

    public String getResultElementName() {
        return resultElementName;
    }

    public void setResultElementName(String resultElementName) {
        this.resultElementName = resultElementName;
    }

    public String getQualityElementName() {
        return qualityElementName;
    }

    public void setQualityElementName(String qualityElementName) {
        this.qualityElementName = qualityElementName;
    }

}
