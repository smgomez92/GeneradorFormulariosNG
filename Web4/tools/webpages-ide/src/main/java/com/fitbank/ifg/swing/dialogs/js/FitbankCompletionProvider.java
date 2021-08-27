package com.fitbank.ifg.swing.dialogs.js;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang.StringUtils;
import org.fife.ui.autocomplete.*;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.ParserGeneral;
import com.fitbank.util.Debug;

/**
 * Clase que provee completado de código sensible al contexto.
 * @author Fitbank RB
 */
public class FitbankCompletionProvider extends DefaultCompletionProvider {
    
    public static final String NORMAL_COMPLETIONS = "normalCompletions";
    
    public static final String GLOBAL_SCOPE = "ifg:globalScope";
    
    private static ResourceBundle autocompletado = null;
    
    private Map<String, List<Completion>> completionsRepo = new HashMap<String, List<Completion>>(10);
    
    private NamesChoicesProvider namesProvider = new NamesChoicesProvider();
    
    static {
        try {
            autocompletado = ResourceBundle.getBundle("plantillasSimples");
        } catch (MissingResourceException ex) {
            Debug.error("No se pudo cargar lista de plantillas simples. Detalles: "
                    + ex.getLocalizedMessage());
        }
    }
    
    public FitbankCompletionProvider() {
        completionsRepo.put(NORMAL_COMPLETIONS, new ArrayList<Completion>(20));
        setParameterChoicesProvider(namesProvider);
        loadAutoCompletions();
        setParameterizedCompletionParams('(', ", ", ')');
        loadContextAwareCompletions("autocompletado.xml");
        setListCellRenderer(new CustomCellRenderer());
    }
    
    @Override
    public void addCompletion(Completion c) {
        List<Completion> normalCompletions = completionsRepo.get(NORMAL_COMPLETIONS);
        checkProviderAndAdd(c);
        Collections.sort(normalCompletions);
    }
    
    @Override
    public void addCompletions(List completions) {
        for (int i = 0; i < completions.size(); i++) {
            Completion c = (Completion) completions.get(i);
            checkProviderAndAdd(c);
        }
        
        Collections.sort(completionsRepo.get(NORMAL_COMPLETIONS));
    }
    
    @Override
    protected void checkProviderAndAdd(Completion c) {
        List<Completion> normalCompletions = completionsRepo.get(NORMAL_COMPLETIONS);
        
        if (c.getProvider() != this) {
            throw new IllegalArgumentException("La plantilla pertenece a otro completion provider!");
        }
        
        normalCompletions.add(c);
    }
    
    @Override
    protected List getCompletionsImpl(JTextComponent comp) {
        completions.clear();
        String entered = getEnteredText(comp);

        if (StringUtils.isBlank(entered)) {
            completions.addAll(completionsRepo.get(NORMAL_COMPLETIONS));
        } else {
            boolean matched = false;
            List<String> matches = new LinkedList<String>();
            
            for (String pattern : completionsRepo.keySet()) {
                if (!pattern.equals(NORMAL_COMPLETIONS) &&
                        entered.matches(pattern)) {
                    matched = true;
                    matches.add(pattern);
                }
            }
            
            if (matched) {
                //FIXME: Seleccionar la coincidencia más específica, que generalmente es
                //la más larga. Esto sirve para cuando hay un objeto dentro de otro,
                //como es el caso de c.formulario....
                String mostSpecificPattern = "";
                for (String pattern : matches) {
                    if (pattern.length() > mostSpecificPattern.length()) {
                        mostSpecificPattern = pattern;
                    }
                }

                this.completions.addAll(completionsRepo.get(mostSpecificPattern));

            } else {
                completions.addAll(completionsRepo.get(NORMAL_COMPLETIONS));
            }
        }
        
        return super.getCompletionsImpl(comp);
    }
    
    @Override
    protected boolean isValidChar(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '_' || ch == '$';
    }
    
    protected String getEnteredText(JTextComponent comp) {
        javax.swing.text.Document doc = comp.getDocument();
        javax.swing.text.Element root = doc.getDefaultRootElement();
        
        int dot = comp.getCaretPosition();
        int index = root.getElementIndex(dot);
        javax.swing.text.Element elem = root.getElement(index);
        int start = elem.getStartOffset();
        int len = dot - start;
        
        try {
            doc.getText(start, len, seg);
        } catch (BadLocationException ble) {
            Debug.error(ble);
            return EMPTY_STRING;
        }

        int segEnd = seg.offset + len;
        start = segEnd - 1;
        
        while (start >= seg.offset
                && (isValidChar(seg.array[start]) || '.' == seg.array[start])) {
            start--;
        }
        
        start++;
        len = segEnd - start;
        
        return len == 0 ? EMPTY_STRING : new String(seg.array, start, len);
        
    }
    
    private void loadAutoCompletions() {
        List<Completion> normalCompletions = completionsRepo.get(NORMAL_COMPLETIONS);
        
        if (autocompletado != null) {
            for (String clave : autocompletado.keySet()) {
                normalCompletions.add(new BasicCompletion(this,
                        autocompletado.getString(clave)));
            }
        }
    }
    
    private void loadContextAwareCompletions(String completionsFile) {
        Document xml = null;
        boolean globalScope = false;
        
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().
                    getResourceAsStream(completionsFile);
            
            if (is == null) {
                Debug.error("No se encontró el archivo de plantillas de autocompletado.");
                return;
            }
            
            xml = ParserGeneral.parse(is);
        } catch (ExcepcionParser ex) {
            Debug.error(ex);
            return;
        }
        
        NodeList allCompletions = xml.getDocumentElement().getElementsByTagName("Completions");
        
        for (int k = 0; k < allCompletions.getLength(); k++) {
            Element completionsElement = (Element) allCompletions.item(k);
            String pattern = completionsElement.getAttribute("match");
            
            if (StringUtils.isBlank(pattern)) {
                continue;
            }
            
            globalScope = pattern.equals(GLOBAL_SCOPE);
            NodeList completionsTags = completionsElement.getElementsByTagName("Completion");
            List<Completion> completionsList = new ArrayList<Completion>(
                    completionsTags.getLength());
            
            for (int i = 0; i < completionsTags.getLength(); i++) {
                Element completion = (Element) completionsTags.item(i);
                Completion compl = parseCompletion(completion);
                
                if (compl != null) {
                    completionsList.add(compl);
                }
            }
            
            Collections.sort(completionsList);
            
            if (globalScope) {
                addCompletions(completionsList);
            } else {
                completionsRepo.put(pattern, completionsList);
            }
            
        }
    }
    
    private Completion parseCompletion(Element elem) {
        String type = elem.getAttribute("memberType");
        String replaceTxt = elem.getAttribute("replaceText");
        NodeList doc = elem.getElementsByTagName("doc");
        
        if (StringUtils.isBlank(replaceTxt)) {
            return null;
        }
        
        if (type.equalsIgnoreCase("property")) {
            String summary = null;
            String dataType = elem.getAttribute("dataType");
            NodeList definedInElement = elem.getElementsByTagName("defined-in");
            String definedIn = null;
            
            if (doc.getLength() == 1) {
                summary = UtilsEditorJavascript.getInnerXML(doc.item(0));
            }
            
            if (definedInElement.getLength() == 1) {
                definedIn = definedInElement.item(0).getTextContent();
            }
            
            if (StringUtils.isBlank(dataType)) {
                dataType = "Object";
            }
            
            VariableCompletion varCompl = new VariableCompletion(this,
                    replaceTxt, dataType);
            varCompl.setSummary(summary);
            varCompl.setShortDescription(summary);
            varCompl.setDefinedIn(definedIn);
            
            return varCompl;
            
            
        } else if (type.equalsIgnoreCase("function")) {
            String returnType = elem.getAttribute("returnType");
            NodeList params = elem.getElementsByTagName("param");
            List<Parameter> paramsList = new LinkedList<Parameter>();
            
            for (int i = 0; i < params.getLength(); i++) {
                Element param = (Element) params.item(i);
                String completionType = param.getAttribute("paramCompletion");

                FitbankParameter parameter = new FitbankParameter(param.getAttribute("type"),
                        param.getAttribute("name"));
                String docParam = UtilsEditorJavascript.getInnerXML(param);
                parameter.setDescription(docParam);
                
                if (StringUtils.isNotBlank(completionType)) {
                    parameter.setTag(completionType);
                }
                
                paramsList.add(parameter);
            }
            
            FunctionCompletion completion = new FunctionCompletion(this,
                    replaceTxt, returnType);
            
            if (paramsList.size() > 0) {
                completion.setParams(paramsList);
            }
            
            return completion;
        }
        
        return null;
    }
}
