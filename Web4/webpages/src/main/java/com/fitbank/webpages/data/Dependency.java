package com.fitbank.webpages.data;

import java.io.Serializable;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fitbank.enums.DependencyType;
import com.fitbank.util.Editable;

/**
 * Clase que representa una dependencia. Se usa tanto en las listas valores como
 * a nivel de ReferenceUtils.
 * 
 * <dl>
 * <dt>field</dt>
 * <dd>Campo del alias actual al que se va a copiar el valor, ej: cidioma,
 * descripcion</dd>
 * <dt>fromAlias</dt>
 * <dd>Alias desde el que se copia el valor, ej: tidiomas0, timagenes1</dd>
 * <dt>fromField</dt>
 * <dd>Campo desde el que se copia el valor, ej: cidioma, cimagen</dd>
 * <dt>immediateValue</dt>
 * <dd>Si no se especifica fromAlias y fromField se usará este valor para la
 * dependencia</dd>
 * <dt>type</dt>
 * <dd>Tipo de dependencia:
 * <ul>
 * <li>IMMEDIATE: Se copiará al momento de hacer la consulta o el mantenimiento</li>
 * <li>DEFERRED: Se copiará internamente luego de consultar o mantener la tabla
 * denominada por 'fromAlias'</li>
 * </ul>
 * </dd>
 * </dl>
 * 
 * @author FitBank
 * @version 2.0
 */
public class Dependency implements Serializable {

    private static final long serialVersionUID = 1L;

    @Editable(weight = 1)
    private String field = "";

    @Editable(weight = 2)
    private String comparator = "=";

    @Editable(weight = 3)
    private String fromAlias = "";

    @Editable(weight = 4)
    private String fromField = "";

    @Editable(weight = 5)
    private String immediateValue = "";

    @Editable(weight = 0)
    private DependencyType type = DependencyType.IMMEDIATE;

    public Dependency() {
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public DependencyType getType() {
        return type;
    }

    public void setType(DependencyType type) {
        this.type = type;
    }

    public String getFromAlias() {
        return fromAlias;
    }

    public void setFromAlias(String fromAlias) {
        this.fromAlias = fromAlias;
    }

    public String getFromField() {
        return fromField;
    }

    public void setFromField(String fromField) {
        this.fromField = fromField;
    }

    public String getImmediateValue() {
        return immediateValue;
    }

    public void setImmediateValue(String immediateValue) {
        this.immediateValue = immediateValue;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0} {1} {2} ({3})", getField(),
                getComparator(),
                (StringUtils.isEmpty(getImmediateValue()) ? getFromAlias()
                        + "." + getFromField() : "'" + getImmediateValue()
                        + "'"), getType());
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
