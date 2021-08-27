package com.fitbank.webpages.widgets;

import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Editable;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.WebPageEnviroment;
import com.fitbank.webpages.assistants.ImageAssistant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Clase usada para presentar una imagen.
 * 
 * @author FitBank CI
 * @version 2.0
 */
public class Image extends Input {

    private static final long serialVersionUID = 2L;
    private static final String BASE64 ="iVBORw0KGgoAAAANSUhEUgAAATgAAACXAgMAAAD8LK92AAAACXBIW"
            + "XMAAAsSAAALEgHS3X78AAAADFBMVEXDw8PJycnHx8fPz8/wEPhXAAAATElEQVRo3u3MMREAIAwEsF8rkEM"
            + "jVYmJHxMBSZomOp1Op9PpdDqdTqfT6XQ6nU6n0+l0Op1Op9PpdDqdTqfT6XQ6nU6n63fb9HKa7gcP8FLxiAcoK"
            + "AAAAABJRU5ErkJggg==";
    public Image() {
       def("base64", BASE64);       
        
       properties.get("w").setValorPorDefecto(0);

        super.setAssistant(new ImageAssistant());
    }

    
     // ////////////////////////////////////////////////////////
    // Getters y Setters
    // ////////////////////////////////////////////////////////

    //<editor-fold defaultstate="collapsed" desc="Getters y setters">
    
    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////

//    @Editable
//    public String getBase64() {
//        return ((PropiedadSimple) properties.get("base64")).getValor();
//    }
//
//    public void setBase64(String base64) {
//        properties.get("base64").setValor(base64);
//    }
        
    //</editor-fold>
    
    // ////////////////////////////////////////////////////////
    // Métodos de Xml
    // ////////////////////////////////////////////////////////

    //<editor-fold defaultstate="collapsed" desc="Métodos de Xml">
//    @Override
//    protected Collection<String> getAtributosElementos() {
//        List<String> l = new ArrayList<String>();
//
//        Collections.addAll(l, "base64");
//
//        return l;
//    }
    //</editor-fold>
    
    
     // ////////////////////////////////////////////////////////
    // Métodos de Edicion en el generador
    // ////////////////////////////////////////////////////////

//    @Override
//    public Collection<Propiedad<?>> getPropiedadesEdicion() {
//        Collection<Propiedad<?>> l = toPropiedades("base64");
//
//        l.addAll(super.getPropiedadesEdicion());
//        return l;
//    }
    
    @Override
    @XML(ignore = true)
    @Editable(ignore = true)
    public Assistant getAssistant() {
        return super.getAssistant();
    }

    @Override
    public void setAssistant(Assistant assistant) {
        if (assistant instanceof ImageAssistant) {
            super.setAssistant(assistant);
        }
    }

    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////
    @Override
    public void generateHtml(ConstructorHtml html) {
        generarEventoJSInicial();

        generarHtmlBase(html);

        html.agregar("input");
        html.setAtributo("type", "hidden");
        html.setAtributo("id", getHTMLId() + "_oculto");
        html.setAtributo("imageid", getHTMLId());
        html.setAtributo("name", getNameOrDefault());
        html.setAtributo("registro", getParentContainer().getIndiceClonacionActual());

        html.agregar("img");
        html.setAtributo("id", getHTMLId());
        html.setAtributo("alt", "");
        html.setAtributo("width", getW(), 0);
        html.setAtributo("height", getH(), 0);
        html.setEstilo("display", "block");

        generarInputOculto("imagen");
        generarClasesCSS(html);
        generarEventosJavascript(html);

        finalizarHtmlBase(html);
    }
    // ////////////////////////////////////////////////////////
    // Métodos de XHtml
    // ////////////////////////////////////////////////////////
    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        generarEventoJSInicial();

        generarHtmlBase(html);

        html.agregar("input");
        html.setAtributo("type", "hidden");
        html.setAtributo("id", getHTMLId() + "_oculto");
        html.setAtributo("imageid", getHTMLId());
        html.setAtributo("name", getNameOrDefault());
        html.setAtributo("registro", getParentContainer().getIndiceClonacionActual());

        html.agregar("img");    
        html.setAtributo("id", getHTMLId());
        html.setAtributo("alt", "");
        html.setAtributo("width", getW(), 0);
        html.setAtributo("height", getH(), 0);
        /**
         * TODO ponerle entre corchetes al atributo src para que se convierta en atributo de angular
         */
        html.setAtributo("src", "data:image/jpeg;base64,"+properties.get("base64").getValorString());
        html.setEstilo("display", "block");

        generarInputOculto("imagen");
        generarClasesCSS(html);
        generarEventosJavascript(html);

        finalizarHtmlBase(html);
    }

    @Override
    protected void generarInputOculto(String suffix) {
        WebPageEnviroment.addJavascriptInicial(String.format(
                "Util.initHtmlElement('%s');", getHTMLId()));
    }

}