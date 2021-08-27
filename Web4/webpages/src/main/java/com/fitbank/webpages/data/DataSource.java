package com.fitbank.webpages.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.fitbank.util.Editable;
import com.fitbank.enums.DataSourceType;

/**
 * Clase que representa un origen de datos.
 * 
 * @author FitBank
 * @version 2.0
 */
public class DataSource implements Serializable {

    private static final long serialVersionUID = 1L;

    @Editable(weight = 1)
    private String alias = "";

    @Editable(weight = 2)
    private String field = "";

    @Editable(weight = 0)
    private DataSourceType type = DataSourceType.NONE;

    @Editable(weight = 3)
    private String comparator = "=";

    @Editable(weight = 4)
    private String functionName = "";

    @Editable(weight = 5)
    private Collection<Dependency> dependencies = new LinkedHashSet<Dependency>();

    public DataSource() {
    }

    public DataSource(String alias, String field, DataSourceType type) {
        this.alias = alias;
        this.field = field;
        this.type = type;
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters
    // ////////////////////////////////////////////////////////

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getField() {
        return field;
    }

    public void setField(String campo) {
        this.field = campo;
    }

    public DataSourceType getType() {
        return type;
    }

    public void setType(DataSourceType tipo) {
        this.type = tipo;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Collection<Dependency> getDependencies() {
        return dependencies;
    }

    public boolean addDependency(Dependency dependency) {
        return dependencies.add(dependency);
    }

    // ////////////////////////////////////////////////////////
    // Métodos varios
    // ////////////////////////////////////////////////////////

    public boolean esCriterio() {
        return getType() == DataSourceType.CRITERION
                || getType() == DataSourceType.CRITERION_DESCRIPTION
                || getType() == DataSourceType.CRITERION_CONTROL;
    }

    public boolean esRegistro() {
        return getType() == DataSourceType.RECORD
                || getType() == DataSourceType.DESCRIPTION
                || getType() == DataSourceType.CONTROL;
    }

    public boolean esDescripcion() {
        return getType() == DataSourceType.CRITERION_DESCRIPTION
                || getType() == DataSourceType.DESCRIPTION;
    }

    public boolean esControl() {
        return getType() == DataSourceType.CRITERION_CONTROL
                || getType() == DataSourceType.CONTROL;
    }

    public boolean esOrden() {
        return getType() == DataSourceType.ORDER;
    }

    public boolean estaVacio() {
        return getType() == DataSourceType.NONE;
    }

    public boolean esReporte() {
        return getType() == DataSourceType.REPORT;
    }

    @Override
    public String toString() {
        String alias2 = getAlias();

        switch (getType()) {
            default:
            case NONE:
                return String.format("Sin origen (%s)", getType());

            case CRITERION:
                return String.format("%s.%s:%s %s", alias2, getField(),
                        getType(), getComparator());

            case ORDER:
            case RECORD:
                return String.format("%s.%s:%s", alias2, getField(), getType());

            case AGGREGATE:
                return String.format("%s(%s.%s):%s", getFunctionName(), alias2, getField(), getType());

            case CRITERION_CONTROL:
            case CONTROL:
            case REPORT:
                return String.format("%s:%s", getField(), getType());

            case CRITERION_DESCRIPTION:
            case DESCRIPTION:
                return String.format("%s.%s:%s%s", alias2, getField(), getType(),
                        getDependencies() == null || getDependencies().isEmpty() ? "*" : "");
        }
    }

    @Override
    public boolean equals(Object objeto) {
        return objeto instanceof DataSource && hashCode() == objeto.hashCode();
    }

    public boolean equalsIgnoreNull(DataSource dataSource) {
        return equalsIgnoreNull(dataSource, "");
    }

    public boolean equalsIgnoreNull(DataSource dataSource, String variante, String... fromAliases) {
        if (!equalsNotNull(dataSource.getAlias(), getAlias())) {
            return false;
        }
        if (!equalsNotNull(dataSource.getField(), getField())) {
            return false;
        }
        if (!equalsNotNull(dataSource.getType(), getType())) {
            return false;
        }
        if (!equalsNotNull(dataSource.getFunctionName(), getFunctionName())) {
            return false;
        }
        if (!equalsNotNull(dataSource.getComparator(), getComparator())) {
            return false;
        }

        if (dataSource.getDependencies() == null) {
            if (variante != null) {
                for (Dependency dependency : getDependencies()) {
                    boolean isFromAlias = ArrayUtils.contains(fromAliases, dependency.getFromAlias());
                    if (StringUtils.isNotBlank(variante)) {
                        return isFromAlias && dependency.getFromField().endsWith("_" + variante);
                    } else {
                        return isFromAlias && !dependency.getFromField().contains("_");
                    }
                }
                return false;
            }
            return true;
        }

        return getDependenciesStringForHashCode().equals(
                dataSource.getDependenciesStringForHashCode());
    }

    private boolean equalsNotNull(Object a, Object b) {
        return a == null || a.equals(b);
    }

    @Override
    public int hashCode() {
        StringBuilder hash = new StringBuilder();

        append(hash, getAlias(), ".");
        append(hash, getField(), "!");
        append(hash, getFunctionName(), "$");
        append(hash, getComparator(), "+");

        hash.append(getType()).append("*");

        hash.append(getDependenciesStringForHashCode());

        return hash.toString().hashCode();
    }

    private String getDependenciesStringForHashCode() {
        StringBuilder hash = new StringBuilder();

        if (getDependencies() != null) {
            for (Dependency dependency : getDependencies()) {
                append(hash, dependency.getFromAlias(), ".");
                append(hash, dependency.getFromField(), "-");
                append(hash, dependency.getField(), "!");
                append(hash, dependency.getComparator(), "=");
            }
        }

        return hash.toString();
    }

    private void append(StringBuilder hash, String value, String separator) {
        if (StringUtils.isNotBlank(value)) {
            hash.append(value);
        }
        hash.append(separator);
    }

}