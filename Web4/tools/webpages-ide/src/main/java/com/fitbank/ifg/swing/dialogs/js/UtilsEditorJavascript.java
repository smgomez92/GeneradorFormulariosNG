package com.fitbank.ifg.swing.dialogs.js;

import java.awt.Color;
import java.awt.Font;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.KeyStroke;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rsyntaxtextarea.templates.CodeTemplate;
import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSSerializer;

import com.fitbank.ifg.iFG;
import com.fitbank.ifg.providers.IFGWebPageProvider;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.ParserGeneral;
import com.fitbank.util.Clonador;
import com.fitbank.util.Debug;
import com.fitbank.webpages.WebPage;

/**
 * Métodos utilitarios para el editor de javascript.
 * @author Fitbank RB
 */
public class UtilsEditorJavascript {
    
    public static void initTextArea(RSyntaxTextArea textArea) {
        RSyntaxTextArea.setTemplatesEnabled(true);
        textArea.setMarkOccurrences(true);
        setElegantTheme(textArea);
        
        FitbankCompletionProvider provider = new FitbankCompletionProvider();
        provider.setAutoActivationRules(false, ".");
        loadTemplates(provider);
        
        AutoCompletion ac = new AutoCompletion(provider);
        ac.setParamChoicesRenderer(new CustomCellRenderer());
        ac.setAutoCompleteEnabled(true);
        ac.setAutoActivationEnabled(true);
        ac.setAutoActivationDelay(500);
        ac.setParameterAssistanceEnabled(true);
        ac.setShowDescWindow(true);
        ac.setAutoCompleteSingleChoices(true);
        ac.install(textArea);
    }
    
    public static WebPage getFullWebPage() {
        WebPage webPage = iFG.getSingleton().getWebPageActual();
        return new IFGWebPageProvider().processWebPage(
                Clonador.clonar(webPage), "0");
    }
    
    private static void loadTemplates(AbstractCompletionProvider provider) {
        CodeTemplateManager tplManager = RSyntaxTextArea.getCodeTemplateManager();
        tplManager.replaceTemplates(new CodeTemplate[0]);
        //NO funciona esto. La combinación de teclas ctrl + shift + espacio
        //inserta las plantillas y no se puede cambiar.
        //TODO: Cargar plantillas de código desde un archivo .properties.
        tplManager.setInsertTrigger(KeyStroke.getKeyStroke("ctrl pressed SPACE"));
        Document xml = null;
        
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().
                    getResourceAsStream("plantillasCodigo.xml");
            
            if (is == null) {
                Debug.error("No se encontró el archivo de plantillas de autocompletado.");
                return;
            }
            
            xml = ParserGeneral.parse(is);
        } catch (ExcepcionParser ex) {
            Debug.error(ex);
            return;
        }
        
        Element plantillasElement = xml.getDocumentElement();
        NodeList plantillas = plantillasElement.getElementsByTagName("Completion");
        List<Completion> completions = new LinkedList<Completion>();
        
        for (int i = 0; i < plantillas.getLength(); i++) {
            Element plantilla = (Element) plantillas.item(i);
            String clave = plantilla.getAttribute("key");
            NodeList aux = plantilla.getElementsByTagName("beforeCaret");
            NodeList aux2 = plantilla.getElementsByTagName("afterCaret");
            
            if (aux.getLength() == 0 || aux2.getLength() == 0) {
                Debug.error("La plantilla " + clave + " es inválida y se ha omitido.");
                continue;
            }
            
            String antesCursor = aux.item(0).getTextContent();
            String despuesCursor = aux2.item(0).getTextContent();
            
            if (StringUtils.isBlank(clave) || StringUtils.isBlank(antesCursor) ||
                    StringUtils.isBlank(despuesCursor)) {
                Debug.error("La plantilla " + (i + 1) + " tiene uno de sus "
                        + "elementos en blanco. Se ha omitido.");
                continue;
            }
            
            tplManager.addTemplate(new StaticCodeTemplate(clave, antesCursor,
                    despuesCursor));
            ShorthandCompletion template = new ShorthandCompletion(provider,
                    clave, antesCursor + despuesCursor, "Plantilla",
                    "Plantilla de código");
            completions.add(template);
        }
        
        provider.addCompletions(completions);
    }
    
    private static void setElegantTheme(RSyntaxTextArea textArea) {
        SyntaxScheme theme = textArea.getSyntaxScheme();
        Style[] styles = theme.styles;
        
        textArea.setMarkOccurrencesColor(new Color(236, 235, 163));
        textArea.setCurrentLineHighlightColor(new Color(233, 239, 248));
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        textArea.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");
        
        //Palabras reservadas
        Style style = styles[Token.RESERVED_WORD];
        style.foreground = new Color(0, 0, 230);
        
        //Comentarios
        style = styles[Token.COMMENT_DOCUMENTATION];
        style.foreground = new Color(162, 162, 162);
        style.font = style.font.deriveFont(Font.PLAIN);
        styles[Token.COMMENT_EOL].foreground = 
                styles[Token.COMMENT_MULTILINE].foreground = style.foreground;
        
        //Identificadores
        style = styles[Token.IDENTIFIER];
        style.foreground = Color.BLACK;
        
        //Literales de cadena
        style = styles[Token.LITERAL_STRING_DOUBLE_QUOTE];
        Color colorLiteralCadena = new Color(206, 123, 0);
        style.foreground = colorLiteralCadena;
        style = styles[Token.LITERAL_CHAR];
        style.foreground = colorLiteralCadena;
        
        //Literales de numero
        style = styles[Token.LITERAL_NUMBER_DECIMAL_INT];
        style.foreground = new Color(51, 170, 51);
        styles[Token.LITERAL_NUMBER_FLOAT].foreground =
                styles[Token.LITERAL_NUMBER_HEXADECIMAL].foreground = style.foreground;
        
        //Operadores
        styles[Token.OPERATOR].foreground = new Color(170, 34, 34);
        
        //Otros
        style = styles[Token.SEPARATOR];
        style.foreground = styles[Token.IDENTIFIER].foreground;
    }
    
    public static String deindent(String str) {
        Collection<String> lines = new ArrayList<String>(Arrays.asList(str.split("\n|\r\n")));
        
        if (lines.size() <= 1) {
            return str;
        }
        
        ArrayList<String> linesAux = (ArrayList<String>) lines;
        
        if (StringUtils.isBlank(linesAux.get(0))) {
            linesAux.remove(0);
        }
        
        if (StringUtils.isBlank(linesAux.get(lines.size() - 1))) {
            linesAux.remove(linesAux.size() - 1);
        }
        
        String pad = StringUtils.getCommonPrefix(lines.toArray(new String[0]));
        if (StringUtils.isBlank(pad)) {
            int maxIndent = pad.length();
            
            final String pattern = "^" + StringUtils.repeat(" ", maxIndent);
            lines = CollectionUtils.collect(lines, new Transformer() {
                
                @Override
                public Object transform(Object input) {
                    return ((String) input).replaceAll(pattern, "");
                }
            });
        }
        
        String res = StringUtils.join(lines.toArray(), "\n");
        
        return res;
    }
    
    public static String getInnerXML(Node elemento) {
        if (elemento == null) {
            return "";
        }
        
        DOMImplementationLS domImpl = (DOMImplementationLS) elemento.
                getOwnerDocument().getImplementation().getFeature("LS", "3.0");
        LSSerializer serializador = domImpl.createLSSerializer();
        serializador.getDomConfig().setParameter("xml-declaration", false);
        NodeList nodosInternos = elemento.getChildNodes();
        StringBuilder bufer = new StringBuilder();
        
        try {
            for (int i = 0; i < nodosInternos.getLength(); i++) {
                bufer.append(serializador.writeToString(nodosInternos.item(i)));
            }
        } catch (LSException ex) {
            Debug.error(ex);
        }
        
        return bufer.toString();
    }
    
}
