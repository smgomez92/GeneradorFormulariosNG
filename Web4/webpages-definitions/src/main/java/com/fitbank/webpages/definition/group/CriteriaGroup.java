package com.fitbank.webpages.definition.group;

import com.fitbank.enums.TipoFila;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.definition.Field;
import com.fitbank.webpages.definition.Group;
import com.fitbank.webpages.definition.WebPageDefinitionCompiler;
import com.fitbank.webpages.definition.widgets.EditorWidget;
import com.fitbank.webpages.widgets.Label;

/**
 * Crea un grupo de campos tipo criterio.
 *
 * @author FitBank CI
 */
public class CriteriaGroup extends Group {

    @Override
    public void generate(WebPage webPage) {
        Container container = getContainer(webPage, TipoFila.COLUMNAS);

        addEditorWidget(container);

        int row = 1;
        for (Field field : getFields()) {
            Label label = new Label(field.getLabel());
            label.setX(1);
            label.setY(row);
            container.add(label);

            addEditorWidget(container, field, label);

            for (Widget widget : field.getWidgets()) {
                widget = WebPageDefinitionCompiler.generateWidget(widget, null);
                widget.setX(2);
                widget.setY(row);
                container.add(widget);

                addEditorWidget(container, widget, widget);
            }

            row++;
        }
    }

}
