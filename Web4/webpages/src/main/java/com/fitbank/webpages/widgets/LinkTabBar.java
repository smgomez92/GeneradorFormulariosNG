package com.fitbank.webpages.widgets;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.sf.json.JSONArray;

import org.apache.commons.lang.StringUtils;

import com.fitbank.js.FuncionJS;
import com.fitbank.js.LiteralJS;
import com.fitbank.propiedades.PropiedadListaString;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.SerializableXml;
import com.fitbank.serializador.xml.UtilXML;
import com.fitbank.util.Editable;

/**
 * Clase que crea un TabBar con links a otros formularios
 *
 * @author FitBank CI
 */
public class LinkTabBar extends TabBar {
    
    private final static String TEMPLATE = "Tabs.irA('%s', %s, this, { preLink: %s, posLink: %s }); return false;";

    @Editable(weight = 1)
    private LiteralJS preLink = new FuncionJS("", "options");

    @Editable(weight = 2)
    private LiteralJS posLink = new FuncionJS("", "options");

    public LinkTabBar() {
        def("names", new PropiedadListaString<String>(""));
    }

    // ////////////////////////////////////////////////////////
    // Geters y seters de properties
    // ////////////////////////////////////////////////////////

    @Editable
    public List<String> getNames() {
        return ((PropiedadListaString<String>) properties.get("names")).getList();
    }

    public void setNames(List<String> tabLabels) {
        properties.get("names").setValor(tabLabels);
    }

    public LiteralJS getPreLink() {
        return preLink;
    }

    public void setPreLink(LiteralJS preLink) {
        this.preLink = preLink;
    }

    public LiteralJS getPosLink() {
        return posLink;
    }

    public void setPosLink(LiteralJS posLink) {
        this.posLink = posLink;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////

    @Override
    protected Collection<String> getAtributosElementos() {
        Collection<String> l = super.getAtributosElementos();

        Collections.addAll(l, "names");

        return l;
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////

    @Override
    protected void generarEventosHTML(ConstructorHtml xhtml, int i, String tab) {
        xhtml.extenderAtributo("onclick", String.format(TEMPLATE, tab,
                JSONArray.fromObject(getNames()).toString(), preLink.toJS(),
                posLink.toJS()));
        generarEventoJSInicial();
    }
    
    @Override
    public Collection<SerializableXml> getChildren() {
        Collection<SerializableXml> children = super.getChildren();
        
        if (StringUtils.isNotBlank(preLink.getValor())) {
            children.add(UtilXML.newInstance("preLink", preLink));
        }
        
        if (StringUtils.isNotBlank(posLink.getValor())) {
            children.add(UtilXML.newInstance("posLink", posLink));
        }
        
        return children;
    }

    @Override
    public Collection<String> getTabCSSClasses(String prefix, String tabString) {
        Collection<String> cssClasses = super.getTabCSSClasses(prefix, tabString);

        if (tabString.equals(getParentWebPage().getURI())) {
            cssClasses.add("activo");
        }

        return cssClasses;
    }

}
