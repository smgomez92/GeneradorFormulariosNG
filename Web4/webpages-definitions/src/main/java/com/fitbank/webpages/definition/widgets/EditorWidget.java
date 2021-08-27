package com.fitbank.webpages.definition.widgets;

import com.fitbank.js.GeneradorJS;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.SerializableXml;
import com.fitbank.serializador.xml.UtilXML;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.definition.Field;
import com.fitbank.webpages.definition.Group;
import com.fitbank.webpages.widgets.Label;
import java.util.Collection;
import java.util.Collections;

/**
 * Widget que sirve para el editor
 *
 * @author FitBank CI
 */
public class EditorWidget extends Widget {

    private String htmlObjectJS = "";

    private String jsObjectId = "";

    private String jsObjectClass = "";

    public EditorWidget() {
    }

    public EditorWidget(Group group, Container container) {
        this.htmlObjectJS = container.getHTMLId();
        this.jsObjectId = group.getId();
        this.jsObjectClass = GeneradorJS.getJSClassName(Group.class);
    }

    public EditorWidget(Field field, Label label) {
        this.htmlObjectJS = label.getHTMLId();
        this.jsObjectId = field.getId();
        this.jsObjectClass = GeneradorJS.getJSClassName(Field.class);
    }

    public EditorWidget(Widget widget, Widget widgetHTML) {
        this.htmlObjectJS = widgetHTML.getHTMLId();
        this.jsObjectId = widget.getId();
        this.jsObjectClass = GeneradorJS.getJSClassName(Widget.class);
    }

    public String getHtmlObjectJS() {
        return htmlObjectJS;
    }

    public void setHtmlObjectJS(String htmlObjectJS) {
        this.htmlObjectJS = htmlObjectJS;
    }

    public String getJsObjectId() {
        return jsObjectId;
    }

    public void setJsObjectId(String jsObjectId) {
        this.jsObjectId = jsObjectId;
    }

    public String getJsObjectClass() {
        return jsObjectClass;
    }

    public void setJsObjectClass(String jsObjectClass) {
        this.jsObjectClass = jsObjectClass;
    }

    @Override
    public boolean getVisible() {
        return false;
    }

    @Override
    protected Collection<String> getAtributosElementos() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public Collection<SerializableXml> getChildren() {
        Collection<SerializableXml> l = super.getChildren();

        l.add(UtilXML.newInstance("htmlObjectJS", getHtmlObjectJS()));
        l.add(UtilXML.newInstance("jsObjectId", getJsObjectId()));
        l.add(UtilXML.newInstance("jsObjectClass", getJsObjectClass()));

        return l;
    }

    public void generateHtml(ConstructorHtml html) {
        if (getParentContainer().getIndiceClonacionActual() == 0) {
            WebPageEnviroment.addJavascriptInicial("FormGenerator.generateInlineEditor('"
                    + htmlObjectJS + "', '" + jsObjectId + "', " + jsObjectClass + ");");
        }
    }

}
