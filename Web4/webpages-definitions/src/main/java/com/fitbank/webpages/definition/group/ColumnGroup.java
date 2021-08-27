package com.fitbank.webpages.definition.group;

import com.fitbank.enums.TipoFila;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.definition.Field;
import com.fitbank.webpages.definition.Group;
import com.fitbank.webpages.definition.WebPageDefinitionCompiler;
import com.fitbank.webpages.widgets.Label;

/**
 * Crea un grupo de campos agrupados en columnas.
 *
 * @author FitBank CI
 */
public class ColumnGroup extends Group {

    @Editable(weight=11)
    private int columnCount = 1;

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    @Override
    public void generate(WebPage webPage) {
        Container container = getContainer(webPage, TipoFila.COLUMNAS);

        addEditorWidget(container);

        int row = 1;
        int col = 1;
        for (Field field : getFields()) {
            Label label = new Label(field.getLabel());
            label.setCSSClass("field-label");
            label.setX(col++);
            label.setY(row);
            container.add(label);

            addEditorWidget(container, field, label);

            for (Widget widget : field.getWidgets()) {
                widget = WebPageDefinitionCompiler.generateWidget(widget, null);
                widget.setX(col);
                widget.setY(row);
                container.add(widget);

                addEditorWidget(container, widget, widget);
            }

            if (col++ >= getColumnCount() * 2) {
                row++;
                col = 1;
            }
        }
    }

}
