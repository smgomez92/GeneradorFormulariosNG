package com.fitbank.propiedades;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.fitbank.js.JSParser;
import com.fitbank.js.JavascriptFormater;
import com.fitbank.js.LiteralJS;
import com.fitbank.js.NamedJSFunction;

/**
 * Clase PropiedadJavascript.
 *
 * @author FitBank
 * @version 2.0
 */
public class PropiedadJavascript extends Propiedad {

    public static enum Tipo {

        SIMPLE, FUNCIONES, EVENTOS

    }

    private static final long serialVersionUID = 2L;

    private Tipo tipo = Tipo.SIMPLE;

    /**
     * Crea un nuevo objeto PropiedadJavascript.
     */
    public PropiedadJavascript() {
        this(Tipo.SIMPLE, "");
    }

    /**
     * Crea un nuevo objeto PropiedadJavascript.
     *
     * @param tipo Indica el tipo del contenido.
     */
    public PropiedadJavascript(Tipo tipo) {
        this(tipo, "");
    }

    /**
     * Crea un nuevo objeto PropiedadJavascript.
     *
     * @param valorPorDefecto Valor por defecto de la propiedad.
     */
    public PropiedadJavascript(Object valorPorDefecto) {
        this(Tipo.SIMPLE, valorPorDefecto);
    }

    /**
     * Crea un nuevo objeto PropiedadJavascript.
     *
     * @param tipo Indica el tipo del contenido.
     * @param valorPorDefecto Valor por defecto de la propiedad.
     */
    private PropiedadJavascript(Tipo tipo, Object valorPorDefecto) {
        super(valorPorDefecto);

        this.tipo = tipo;

        switch (tipo) {
            case SIMPLE:
                super.setValor("");
                break;

            case EVENTOS:
                super.setValor(new TreeMap<String, String>());
                break;

            case FUNCIONES:
                super.setValor(new LinkedHashMap<String, LiteralJS>());
                break;
        }
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Map<String, String> getEventos() {
        if (getValor() instanceof TreeMap) {
            return (Map<String, String>) getValor();
        }

        return null;
    }

    public Map<String, LiteralJS> getFunciones() {
        if (getValor() instanceof LinkedHashMap) {
            return (Map<String, LiteralJS>) getValor();
        }

        return null;
    }

    public void extenderEvento(String evento, String js) {
        if (getTipo() != Tipo.EVENTOS) {
            throw new IllegalStateException("Propiedad no es de eventos html");
        }

        if (StringUtils.isNotBlank(js)) {
            evento = evento.toLowerCase();

            if (!getEventos().containsKey(evento)) {
                getEventos().put(evento, js);
            } else {
                getEventos().put(evento, getEventos().get(evento) + js);
            }
        }
    }

    @Override
    public boolean esValorPorDefecto() {
        if (getValorPorDefecto() instanceof LiteralJS) {
            return getValorString().equals(((LiteralJS) getValorPorDefecto()).
                    getValor());

        } else {
            return getValorString().equals(getValorPorDefecto());

        }
    }

    @Override
    public String valorValido(Object o) {
        if (o == null || o instanceof String || o instanceof LiteralJS
                || o instanceof TreeMap || o instanceof LinkedHashMap) {
            return VALOR_VALIDO;
        } else {
            return "Error valor no válido para propiedad javascript";
        }
    }

    public void setValorString(String o) {
        if (getTipo() == null) {
            return;
        }
        
        switch (getTipo()) {
            case SIMPLE:
                if (getValor() instanceof LiteralJS) {
                    ((LiteralJS) getValor()).setValor(o);
                    notifyChange();
                } else {
                    super.setValor(o);
                }
                break;

            case FUNCIONES:
                getFunciones().clear();
                parseReplaceFunctions(o);
                notifyChange();
                break;

            case EVENTOS:
                getEventos().clear();
                parseReplaceEvents(o);
                notifyChange();
                break;
        }
    }

    public void parseReplaceFunctions(String text) {
        for (LiteralJS literalJS : JSParser.parse(text)) {
            if (literalJS instanceof NamedJSFunction) {
                getFunciones().put(((NamedJSFunction) literalJS).getName(),
                        literalJS);
            } else {
                getFunciones().put("Código Libre " + (getFunciones().size() + 1),
                        literalJS);
            }
        }
    }

    public void parseReplaceFunctionsNg(String text) {
        System.out.println("EL CODIGOOOOO " + text);
        if (text.startsWith("c.formulario.")) {
            String name = text.split("=")[0];
            String content = text.split("function")[1].substring(4, text.split("function")[1].length() - 1);
            System.out.println("nombressss " + name + " content " + content);
            LiteralJS literalJS = new LiteralJS(text);
            //literalJS.setValor(content);
            getFunciones().put(name,
                    literalJS);
            System.out.println("Valor " + literalJS.getValor());

        }

    }

    protected void parseReplaceEvents(String text) {
        String xml = String.format("<html><body %s><body></html>", text);

        try {
            Source source = new Source(new StringReader(xml));
            Element element = source.getFirstElement("body");

            for (Attribute attribute : element.getAttributes()) {
                getEventos().put(attribute.getName(), attribute.getValue());
            }
        } catch (IOException e) {
            throw new Error("No se pudo leer los eventos", e);
        }
    }

    @Override
    public Object getValorXml() {
        return getValorString();
    }

    @Override
    public String getValorString() {
        Object valor = getValor();
        StringBuilder res = new StringBuilder();

        if (valor == null) {
            return "";

        } else if (valor instanceof String) {
            res.append(valor);

        } else if (valor instanceof LiteralJS) {
            res.append(((LiteralJS) valor).getValor());

        } else if (getFunciones() != null) {
            for (LiteralJS js : getFunciones().values()) {
                if (StringUtils.isNotBlank(js.getValor())) {
                    res.append(js.toJS().trim()).append("\n");
                }
            }

        } else if (getEventos() != null) {
            for (Entry<String, String> evento : getEventos().entrySet()) {
                String js = JavascriptFormater.format(evento.getValue(), true);
                if (StringUtils.isNotBlank(js)) {
                    res.append(String.format("%s=\"%s\" ",
                            evento.getKey(), StringEscapeUtils.escapeHtml(js)));
                }
            }
        }

        return res.toString().trim();
    }

    @Override
    public void setValor(Object o) {
        if (o == null) {
            setValorString("");
        } else if (o instanceof String) {
            setValorString((String) o);
        } else if (o == null) {
            setValor("");
        } else {
            super.setValor(o);
        }
    }

    public void juntarCodigoLibre() {
        StringBuilder codigoLibre = new StringBuilder();

        Iterator<LiteralJS> values = getFunciones().values().iterator();

        while (values.hasNext()) {
            LiteralJS value = values.next();
            if (value.getClass().equals(LiteralJS.class)) {
                codigoLibre.append(value.getValor());
                values.remove();
            }
        }

        getFunciones().put("Código Libre", new LiteralJS(codigoLibre.toString()));
    }

    public void limpiar() {
        switch (getTipo()) {
            case EVENTOS:
                Iterator<String> values1 = getEventos().values().iterator();
                while (values1.hasNext()) {
                    String value = values1.next();
                    if (StringUtils.isBlank(value)) {
                        values1.remove();
                    }
                }
                break;

            case FUNCIONES:
                Iterator<LiteralJS> values2 = getFunciones().values().iterator();
                while (values2.hasNext()) {
                    LiteralJS value = values2.next();
                    if (StringUtils.isBlank(value.getValor())) {
                        values2.remove();
                    }
                }
        }
    }

}
