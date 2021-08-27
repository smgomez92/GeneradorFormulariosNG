package com.fitbank.webpages.behaviors;

import com.fitbank.webpages.AbstractJSBehaivor;
import com.fitbank.util.Editable;

public class Report extends AbstractJSBehaivor {

    @Editable
    private String name = "";

    @Editable
    private boolean directDownload = false;

    @Editable
    private String downloadName = name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getDirectDownload() {
        return directDownload;
    }

    public void setDirectDownload(boolean directDownload) {
        this.directDownload = directDownload;
    }

    public String getDownloadName() {
        return downloadName;
    }

    public void setDownloadName(String downloadName) {
        this.downloadName = downloadName;
    }

    @Override
    public String toString() {
        return super.toString() + " (" + getName() + ")";
    }

}
