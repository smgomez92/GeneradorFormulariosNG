package com.fitbank.webpages.definition.group;

import com.fitbank.enums.DataSourceType;
import com.fitbank.enums.TipoFila;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.definition.Field;
import com.fitbank.webpages.definition.Group;
import com.fitbank.webpages.definition.WebPageDefinitionCompiler;
import com.fitbank.webpages.widgets.HeaderSeparator;
import com.fitbank.webpages.widgets.Input;
import com.fitbank.webpages.widgets.Label;

/**
 * Crea un grupo de campos agrupados en una tabla.
 *
 * @author FitBank CI
 */
public class TableGroup extends Group {

    public enum TableGroupStyle {

        ON_TOP, INSIDE_TABLE, NONE

    }

    @Editable(weight = 11)
    private int records = 10;

    @Editable(weight = 12)
    private int visible = 10;

    @Editable(weight = 13)
    private TableGroupStyle style = TableGroupStyle.ON_TOP;

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public TableGroupStyle getStyle() {
        return style;
    }

    public void setStyle(TableGroupStyle style) {
        this.style = style;
    }

    @Override
    public void generate(WebPage webPage) {
        Container container = getContainer(webPage, TipoFila.TABLA);

        container.setClonacionMax(getRecords());
        container.setPresentacionMax(getVisible());

        addEditorWidget(container);

        int z = 1;

        // Generar criterios fuera de tabla
        if (getStyle() == TableGroupStyle.ON_TOP) {
            Label label = new Label("Buscar:");
            label.setCSSClass("buscar");
            container.add(label);

            generateTableCriteria(container, z);

            Label emptyLabel = new Label();
            emptyLabel.setZ(++z);
            container.add(emptyLabel);
        }

        // Generar Etiquetas
        for (Field field : getFields()) {
            Label label = new Label(field.getLabel());
            label.setZ(z);
            container.add(label);

            addEditorWidget(container, field, label);

            // Generar campo de orden
            for (Widget widget : field.getWidgets()) {
                if (widget.getDataSource().getType()  == DataSourceType.RECORD &&
                        applies(widget, DataSourceType.ORDER)) {
                    widget = WebPageDefinitionCompiler.generateWidget(widget,
                            DataSourceType.ORDER);
                } else {
                    widget = new Label();
                }
                widget.setZ(z);
                container.add(widget);
            }
        }

        // Generar criterios dentro de tabla
        if (getStyle() == TableGroupStyle.INSIDE_TABLE) {
            generateTableCriteria(container, ++z);
        }

        container.add(new HeaderSeparator());

        if (getStyle() == TableGroupStyle.ON_TOP) {
            container.add(new Label());
        }

        // Generar campos
        for (Field field : getFields()) {
            boolean first = true;
            for (Widget widget : field.getWidgets()) {
                widget = WebPageDefinitionCompiler.generateWidget(widget, null);
                if (first) {
                    widget.setX(2);
                    first = false;
                }
                container.add(widget);

                addEditorWidget(container, widget, widget);
            }
        }
    }

    private void generateTableCriteria(Container container, int z) {
        for (Field field : getFields()) {
            boolean first = true;
            for (Widget widget : field.getWidgets()) {
                if (widget.getDataSource().esRegistro() && applies(widget,
                        DataSourceType.CRITERION)) {
                    widget = WebPageDefinitionCompiler.generateWidget(widget,
                            DataSourceType.CRITERION);
                } else {
                    widget = new Label();
                }

                widget.setZ(z);

                if (first) {
                    widget.setX(2);
                    first = false;
                }

                container.add(widget);
            }
        }
    }

    private boolean applies(Widget widget, DataSourceType type) {
        if (widget instanceof Input) {
            return ((Input) widget).getAssistant().applyTo().contains(type);
        } else {
            return false;
        }
    }

}
