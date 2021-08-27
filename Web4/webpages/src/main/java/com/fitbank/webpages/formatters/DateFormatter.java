package com.fitbank.webpages.formatters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Debug;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Formatter;

public class DateFormatter extends Formatter {

    public enum DateFormat {

        DATE, TIME, SHORT_TIME, DATETIME, TIMESTAMP;

    }

    public enum TransportDateFormat {

        DATE, TIME, SHORT_TIME, COMPACT_SHORT_TIME, DATETIME, TIMESTAMP, ISO;

    }

    private static final ResourceBundle parameters = ResourceBundle.getBundle(
            "parameters");

    private static final long serialVersionUID = 1L;

    private static final String[] PARSE_PATTERNS;

    static {
        Set<String> parseParameters = new HashSet<String>();

        for (TransportDateFormat tdf : TransportDateFormat.values()) {
            parseParameters.add(getTransportDateFormat(tdf));
        }

        PARSE_PATTERNS = parseParameters.toArray(new String[0]);
    }

    private static String getTransportDateFormat(
            TransportDateFormat transportDateFormat) {
        return parameters.getString(TransportDateFormat.class.getCanonicalName()
                + "." + transportDateFormat.name());
    }

    public static String formatISO(Date date) {
        return DateFormatUtils.format(date,
                getTransportDateFormat(TransportDateFormat.ISO));
    }

    @Editable
    private DateFormat format = DateFormat.TIMESTAMP;

    @Editable
    private TransportDateFormat transportFormat = TransportDateFormat.TIMESTAMP;

    public DateFormat getFormat() {
        return format;
    }

    public void setFormat(DateFormat formato) {
        this.format = formato;
    }

    public TransportDateFormat getTransportFormat() {
        return transportFormat;
    }

    public void setTransportFormat(TransportDateFormat transportFormat) {
        this.transportFormat = transportFormat;
    }

    @XML(ignore = true)
    public String getFormatString() {
        return parameters.getString(DateFormat.class.getCanonicalName() + "."
                + getFormat().name());
    }

    @XML(ignore = true)
    public String getFormatStringToShow() {
        return parameters.getString(DateFormat.class.getCanonicalName() + "."
                + getFormat().name() + "_SHOW");
    }

    @XML(ignore = true)
    public String getTransportFormatString() {
        return getTransportDateFormat(getTransportFormat());
    }

    public Date getDate(String valorSinFormato) {
        try {
            return parseDate(valorSinFormato);
        } catch (ParseException e) {
            Debug.warn("No se pudo parsear fecha: " + valorSinFormato, e);

            return null;
        }
    }

    private Date parseDate(String valorSinFormato) throws ParseException {
        String[] patron = new String[] { getTransportFormatString() };

        String valor = valorSinFormato.substring(0, Math.min(valorSinFormato.
                length(), patron[0].replaceAll("'", "").length()));

        try {
            return DateUtils.parseDate(valor, patron);

        } catch(ParseException ex) {
            Debug.error("Formato de fecha incorrecto: " + patron[0]
                    + " para fecha " + valorSinFormato);

            valor = valorSinFormato.substring(0, Math.min(valorSinFormato.
                length(), 23));

            return DateUtils.parseDate(valor, PARSE_PATTERNS);
        }
    }

    @Override
    public String format(String valorSinFormato) {
        if (StringUtils.isBlank(valorSinFormato)) {
            return valorSinFormato;
        }

        try {
            return DateFormatUtils.format(parseDate(valorSinFormato),
                    getFormatString());
        } catch (ParseException e) {
            Debug.warn("No se pudo parsear fecha: " + valorSinFormato, e);

            return valorSinFormato;
        }
    }

    @Override
    public String unformat(String valorFormateado) {
        if (StringUtils.isBlank(valorFormateado)) {
            return valorFormateado;
        }

        try {
            Date date = DateUtils.parseDate(valorFormateado,
                    new String[] { this.getFormatString() });

            String valorSinFormato = new SimpleDateFormat(
                    getTransportFormatString()).format(date);

            return valorSinFormato;
        } catch (ParseException e) {
            Debug.warn("No se pudo parsear fecha: "
                    + valorFormateado + " (" + getFormat() + ")", e);

            return valorFormateado;
        }
    }

}
