package com.fitbank.webpages.formatters;

import com.fitbank.util.Editable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.fitbank.webpages.data.DataSource;

public class NumberFormatter extends TextFormatter {

    private static final long serialVersionUID = 1L;

    @Editable
    private boolean positivesOnly = false;

    public NumberFormatter() {
        setFormat("#,##0.00");
    }

    public boolean isLike() {
        if (getFormElement() == null) {
            return false;
        }
        DataSource ds = getFormElement().getDataSource();
        return ds.esCriterio() && ds.getComparator().matches("(?i:.*LIKE)");
    }

    @Override
    public String format(String valorSinFormato) {
        if (StringUtils.isBlank(valorSinFormato) || isLike()) {
            return valorSinFormato;
        }
        return new DecimalFormat(getFormat(),
                DecimalFormatSymbols.getInstance(Locale.ENGLISH)).format(Double.
                parseDouble(unformat(valorSinFormato)));
    }

    @Override
    public String unformat(String valorFormateado) {
        if (isLike()) {
            return valorFormateado;
        } else {
            return valorFormateado.replaceAll("[^\\-\\d\\.\\%]", "");
        }
    }

    public boolean isPositivesOnly() {
        return this.positivesOnly;
    }

    public void setPositivesOnly(boolean positivesOnly) {
        this.positivesOnly = positivesOnly;
    }
}
