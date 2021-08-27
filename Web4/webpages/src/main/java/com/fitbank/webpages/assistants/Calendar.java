package com.fitbank.webpages.assistants;

import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Editable;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.formatters.DateFormatter;
import com.fitbank.webpages.formatters.DateFormatter.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Asistente que sirve para mostrar un calendario tipo drop-down.
 *
 * @author FitBank CI
 */
public class Calendar extends PlainText {

    @XML(ignore = true)
    private final DateFormatter dateFormatter = new DateFormatter();

    @Editable
    private boolean showIcon = true;

    /**
     * Deshabilita todas las fechas que caigan dentro de un día determinado.
     * Poner en el arreglo la abreviación de tres letras del día a deshabilitar,
     * por ejemplo, lun, mar, mié.
     */
    @Editable
    private Collection<String> disabledDays = new ArrayList<String>();

    /**
     * Si es true, muestra en el calendario los días del mes anterior y
     * siguiente que se encuentren dentro de la primera y última semana. Así, se
     * llenan todos los cuadros del calendario, y no deja cuadros vacíos para
     * los días que no son del mes actual.
     */
    @Editable
    private boolean fillGrid = true;

    /**
     * Muestra para cada fila el número de semana del año que le corresponde.
     */
    @Editable
    private boolean showWeeks = false;

    /**
     * Formato del texto que aparecerá en la parte inferior del calendario al
     * pasar el mouse sobre una fecha.
     */
    @Editable
    private String statusFormat = "%l, %d de %F de %Y";

    /**
     * Fecha mínima seleccionable. Las fechas anteriores a esta, aparecerán
     * deshabilitadas en el calendario. La fecha debe tener el formato YYYYMMDD.
     */
    @Editable
    private String minDate = "";

    /**
     * Fecha máxima seleccionable. Fechas después de esta aparecerán
     * deshabilitadas en el calendario. La fecha debe estar en la forma
     * YYYYMMDD.
     */
    @Editable
    private String maxDate = "";

    /**
     * Indica si es posible arrastrar el calendario.
     */
    @Editable
    private boolean draggable = true;

    public Calendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateFormatter.setFormat(DateFormat.DATE);
        dateFormatter.setDoInit(false);
        maxDate = sdf.format(new Date().getTime());
        minDate = "01-01-1970";
    }

    @Override
    public void init(FormElement formElement) {
        super.init(formElement);
        dateFormatter.setFormElement(formElement);
    }

    @Editable
    public DateFormat getFormat() {
        return dateFormatter.getFormat();
    }

    public void setFormat(DateFormat formato) {
        dateFormatter.setFormat(formato);
    }

    public String getFormatString() {
        return dateFormatter.getFormatString();
    }

    public DateFormatter getDateFormatter() {
        return dateFormatter;
    }

    public boolean getShowIcon() {
        return showIcon;
    }

    public void setShowIcon(boolean showIcon) {
        this.showIcon = showIcon;
    }

    @Override
    public boolean usesIcon() {
        return getShowIcon();
    }

    public Collection<String> getDisabledDays() {
        return disabledDays;
    }

    public void setDisabledDays(Collection<String> disabledDays) {
        this.disabledDays = disabledDays;
    }

    public boolean isFillGrid() {
        return fillGrid;
    }

    public void setFillGrid(boolean fillGrid) {
        this.fillGrid = fillGrid;
    }

    public boolean isShowWeeks() {
        return showWeeks;
    }

    public void setShowWeeks(boolean showWeeks) {
        this.showWeeks = showWeeks;
    }

    public String getStatusFormat() {
        return statusFormat;
    }

    public void setStatusFormat(String statusFormat) {
        this.statusFormat = statusFormat;
    }

    public String getMinDate() {
        return minDate;
    }

    public void setMinDate(String minDate) {
        this.minDate = minDate;
    }

    public String getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(String maxDate) {
        this.maxDate = maxDate;
    }

    public boolean isDraggable() {
        return draggable;
    }

    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }

    @Override
    public String format(String valorSinFormato) {
        return dateFormatter.format(valorSinFormato);
    }

    @Override
    public String unformat(String valorFormateado) {
        return dateFormatter.unformat(valorFormateado);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + getFormat() + ")";
    }

}
