package com.fitbank.webpages;

import java.io.Serializable;
import java.text.MessageFormat;

import com.fitbank.enums.AttachedPosition;
import com.fitbank.util.Editable;

public class AttachedWebPage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Editable(weight = 1)
    private String subsystem = "";

    @Editable(weight = 2)
    private String transaction = "";

    @Editable(weight = 3)
    private AttachedPosition position = AttachedPosition.BEFORE;

    @Editable(weight = 4)
    private String tabBase = "0";

    @Editable(weight = 5)
    private Integer containerIndex = 0;

    @Editable(weight = 6)
    private boolean readOnly = false;

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public AttachedPosition getPosition() {
        return position;
    }

    public void setPosition(AttachedPosition position) {
        this.position = position;
    }

    public String getTabBase() {
        return tabBase;
    }

    public void setTabBase(String tabBase) {
        this.tabBase = tabBase;
    }

    public Integer getContainerIndex() {
        return containerIndex;
    }

    public void setContainerIndex(Integer containerIndex) {
        this.containerIndex = containerIndex;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}:{1}", getSubsystem(), getTransaction());
    }

}
