package com.fitbank.webpages.definition;

import java.util.LinkedList;
import java.util.List;

import com.fitbank.enums.DataSourceType;
import com.fitbank.enums.TipoFila;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.util.Clonador;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageXml;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.ListOfValues;
import com.fitbank.webpages.assistants.Order;
import com.fitbank.webpages.assistants.PlainText;
import com.fitbank.webpages.widgets.Input;
import com.fitbank.webpages.widgets.TabBar;

/**
 * Clase que convierte un WebPageDefinition en un WebPage.
 *
 * @author FitBank CI
 */
public final class WebPageDefinitionCompiler {

    private WebPageDefinitionCompiler() {
    }

    public static WebPage compile(WebPageDefinition webPageDefinition) {
        WebPage webPage = new WebPage();

        webPage.setTitle(webPageDefinition.getTitle());
        webPage.setSubsystem(webPageDefinition.getSubsystem());
        webPage.setTransaction(webPageDefinition.getTransaction());

        webPage.getAttached().addAll(webPageDefinition.getAttached());
        webPage.getReferences().addAll(webPageDefinition.getReferences());

        // Agregar TabBar
        if (!webPageDefinition.getTabLabels().isEmpty()) {
            Container container = new Container();
            container.setTipoFila(TipoFila.COLUMNAS);
            container.setTab("0");
            container.setCSSClass("tab-bar");

            TabBar tabBar = new TabBar();
            int tab = 1;
            List<String> tabs = new LinkedList<String>();
            List<String> textos = new LinkedList<String>();
            for (String tabLabel : webPageDefinition.getTabLabels()) {
                tabs.add(String.valueOf(tab++));
                textos.add(tabLabel);
            }
            tabBar.setTabs(tabs);
            tabBar.setTabLabels(textos);
            container.add(tabBar);

            webPage.add(container);
        }

        // Generar grupos
        for (Group group : webPageDefinition.getGroups()) {
            group.generate(webPage);
        }

        // Convertir a XML y leer nuevamente.
        try {
            return WebPageXml.parseString(webPage.toStringXml());
        } catch (ExcepcionParser e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Genera un widget de un WebPageDefinition para un WebPage. Si no se
     * especifica dataSourceType se refiere al widget principal que se usa para
     * el WebPage, caso contrario se refiere a un widget derivado.
     *
     * @param widget
     * @param dataSourceType
     *
     * @return Widget procesado
     */
    public static Widget generateWidget(Widget widget, DataSourceType dataSourceType) {
        widget = Clonador.clonar(widget);

        if (dataSourceType != null) {
            widget.resetId();
        }

        if (widget instanceof Input) {
            return process((Input) widget, dataSourceType);
        } else {
            return widget;
        }
    }

    private static Input process(Input input, DataSourceType dataSourceType) {
        if (!input.getDataSource().estaVacio() && dataSourceType != null) {
            input.setName(input.getNameOrDefault() + "_" + dataSourceType.name());

            if (input.getDataSource().getType() == DataSourceType.DESCRIPTION
                    && dataSourceType == DataSourceType.CRITERION) {
                input.getDataSource().setType(DataSourceType.CRITERION_DESCRIPTION);
            } else {
                input.getDataSource().setType(dataSourceType);
            }

            if (dataSourceType == DataSourceType.ORDER) {
                input.setAssistant(new Order());
                input.setW(10);
            }

            if (!input.getAssistant().applyTo().contains(dataSourceType)) {
                input.setAssistant(new PlainText());
            }

            // FIXME analizar bien este caso! Se debe reescribir la lista de
            // valores para que sirva o simplemente descartarla?
            if (input.getAssistant() instanceof ListOfValues) {
                input.setAssistant(new PlainText());
            }
        }

        return input;
    }

}
