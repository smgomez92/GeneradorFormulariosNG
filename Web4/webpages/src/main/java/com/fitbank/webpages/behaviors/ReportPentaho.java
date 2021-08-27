package com.fitbank.webpages.behaviors;

import com.fitbank.webpages.AbstractJSBehaivor;
import com.fitbank.util.Editable;
import java.util.HashMap;
import java.util.Map;

public class ReportPentaho extends AbstractJSBehaivor {

    @Editable(weight = 1)
    private String name = "";

    @Editable(weight = 2)
    private String type = "";

    @Editable(weight = 3)
    private String folderName = "";

    @Editable(weight = 4)
    private final Map<String, String> parameters = new HashMap<String, String>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getName() + ")";
    }

}
