package com.fitbank.webpages.definition;

import com.fitbank.enums.TipoFila;
import com.fitbank.util.Editable;
import com.fitbank.util.Servicios;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebElement;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.definition.widgets.EditorWidget;
import com.fitbank.webpages.widgets.Label;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.commons.lang.StringUtils;

/**
 * Clase base de grupo de campos.
 *
 * @author FitBank CI
 */
public class Group {

    @Editable(weight = 1)
    private String title = "";

    @Editable(weight = 2)
    private String tab = "1";

    @Editable(weight = 3)
    private boolean floatable = false;

    @Editable(weight = 4)
    private Collection<Field> fields = new LinkedList<Field>();

    private transient String id = Servicios.generarIdUnicoTemporal();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public boolean getFloatable() {
        return floatable;
    }

    public void setFloatable(boolean floatable) {
        this.floatable = floatable;
    }

    public Collection<Field> getFields() {
        return fields;
    }

    public void setFields(Collection<Field> fields) {
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + getTitle() + " (" + getFields().
                size() + ") [" + getTab() + "]";
    }

    public void generate(WebPage webPage) {
        // Para ser sobrescrito
    }

    protected Container getContainer(WebPage webPage, TipoFila tipoFila) {
        Container container = new Container();

        container.setCSSClass(Servicios.toDashedString(getClass().getSimpleName())
                + (getFloatable() ? " flotable" : ""));
        container.setTitle(getTitle());
        container.setTab(getTab());
        container.setTipoFila(tipoFila);

        webPage.add(container);

        return container;
    }

    protected void addEditorWidget(Container container) {
        if (WebPageEnviroment.getDebug()) {
            container.add(new EditorWidget(this, container));
        }
    }

    protected void addEditorWidget(Container container, Field field, Label label) {
        if (WebPageEnviroment.getDebug()) {
            materializeId(label);
            container.add(new EditorWidget(field, label));
        }
    }

    protected void addEditorWidget(Container container, Widget widget, Widget widgetHTML) {
        if (WebPageEnviroment.getDebug()) {
            materializeId(widget);
            materializeId(widgetHTML);
            container.add(new EditorWidget(widget, widgetHTML));
        }
    }

    /**
     * Forzar a que se guarde en el xml temporal el id. Solo aplica a Label e
     * Input con sus respectivas subclases. Container automáticamente siempre
     * mantiene su id.
     *
     * @param webElement Elemento que se va a materializar el id.
     */
    private void materializeId(WebElement webElement) {
        if (webElement instanceof Label) {
            Label label = (Label) webElement;
            if (StringUtils.isEmpty(label.getIdentificador())) {
                label.setIdentificador(label.getId());
            }
        } else if (webElement instanceof FormElement) {
            FormElement formElement = (FormElement) webElement;
            if (StringUtils.isEmpty(formElement.getName())) {
                formElement.setName(formElement.getNameOrDefault());
            }
        }

        webElement.resetId();
    }

}
