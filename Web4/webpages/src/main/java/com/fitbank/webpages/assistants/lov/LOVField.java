package com.fitbank.webpages.assistants.lov;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.apache.commons.beanutils.BeanUtils;

import com.fitbank.util.Clonador;
import com.fitbank.util.Debug;
import com.fitbank.util.Editable;
import com.fitbank.webpages.data.DataSource;
import org.apache.commons.lang.StringUtils;

/**
 * Clase Campo. Se usa dentro de listas valores para indicar que campos se
 * presentarán en las mismas.
 * 
 * @author FitBank
 * @version 2.0
 */
public class LOVField extends DataSource {

    private static final long serialVersionUID = 1L;

    @Editable(weight = 5)
    private String title = "";

    @Editable(weight = 6)
    private String elementName = "";

    @Editable(weight = 7)
    private Boolean visible = true;

    @Editable(weight = 8)
    private Boolean autoQuery = false;

    @Editable(weight = 9)
    private boolean fireAlways = false;

    @Editable(weight = 10)
    private Integer w = 0;

    @Editable(weight = 11)
    private boolean required = false;

    @Editable(weight = 12)
    private Integer order = 0;

    @Editable(weight = 13)
    private boolean keep = false;

    @Editable(weight = 14)
    private String dateTransportFormat = "yyyy-MM-dd HH:mm:ss.SSS";

    private String value = "";

    public LOVField() {
        setComparator("LIKE");
    }

    public LOVField(DataSource datasource) {
        try {
            BeanUtils.copyProperties(this, datasource);
            setComparator("LIKE");
            this.getDependencies().addAll(
                    Clonador.clonar(datasource.getDependencies()));
        } catch (IllegalAccessException e) {
            Debug.error(e);
        } catch (InvocationTargetException e) {
            Debug.error(e);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public boolean getVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public void setAutoQuery(Boolean autoQuery) {
        this.autoQuery = autoQuery;
    }

    public Boolean getAutoQuery() {
        return autoQuery;
    }

    public boolean getFireAlways() {
        return fireAlways;
    }

    public void setFireAlways(boolean fireAlways) {
        this.fireAlways = fireAlways;
    }

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean requerido) {
        this.required = requerido;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean getKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public String getDateTransportFormat() {
        return dateTransportFormat;
    }

    public void setDateTransportFormat(String dateTransportFormat) {
        this.dateTransportFormat = dateTransportFormat;
    }

    public String getComparator(boolean isSpecialTable) {
        if (isSpecialTable) {
            return super.getComparator();
        }

        return this.getComparator();
    }

    @Override
    public String getComparator() {
        if (StringUtils.isNotBlank(this.getValue())) {
            if (this.getValue().contains("%")) {
                if (super.getComparator().toUpperCase().contains("LIKE")) {
                    return super.getComparator();
                }

                return "LIKE";
            }

            if (super.getComparator().toUpperCase().contains("NOT LIKE")) {
                return "<>";
            } else if (super.getComparator().toUpperCase().contains("LIKE")) {
                return "=";
            }
        }

        return super.getComparator();
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} => {1} ({2}{3}{4})", super.toString(),
                getElementName(), getVisible() ? "" : "o",
                getRequired() ? "r" : "", getAutoQuery() ? "a" : "");
    }

}
