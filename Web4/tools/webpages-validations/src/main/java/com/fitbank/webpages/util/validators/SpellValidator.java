package com.fitbank.webpages.util.validators;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.InflaterInputStream;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.ast.*;

import com.inet.jortho.*;

import com.fitbank.js.JSParser;
import com.fitbank.js.JavascriptFormater;
import com.fitbank.js.LiteralJS;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.util.Debug;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.JSBehavior;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.ListOfValues;
import com.fitbank.webpages.assistants.lov.LOVField;
import com.fitbank.webpages.behaviors.Link;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.formulas.Formula;
import com.fitbank.webpages.formulas.FormulaParser;
import com.fitbank.webpages.util.IterableWebElement;
import com.fitbank.webpages.util.ValidationMessage;
import com.fitbank.webpages.util.ValidationMessage.Severity;
import com.fitbank.webpages.util.ValidationUtils;
import com.fitbank.webpages.util.Validator;
import com.fitbank.webpages.widgets.Button;
import com.fitbank.webpages.widgets.CheckBox;
import com.fitbank.webpages.widgets.ComboBox;
import com.fitbank.webpages.widgets.Input;
import com.fitbank.webpages.widgets.Label;
import com.fitbank.webpages.widgets.TabBar;

/**
 * Revisa la ortografía
 *
 * @author FitBank CI, RB
 */
public class SpellValidator extends Validator {

    private static final String SPELLING_ERROR = "SPELLING_ERROR";

    private static final String FUNCIONES_REGEX = "(?:changeValue|lv\\.consultar|"
            + "on|fire|fireDOMEvent|console\\.log)$";
    
    private static Pattern EXCLUSIONES_JS = Pattern.compile(FUNCIONES_REGEX);
    
    private final static Map<Integer, String> replacements =
            new HashMap<Integer, String>();

    private final static Set<String> allPhrases = new HashSet<String>();

    private static Map<String, Dictionary> diccionarios =
            new HashMap<String, Dictionary>(3);

    private static final ThreadLocal<String> currentLang = new ThreadLocal<String>() {

        @Override
        protected String initialValue() {
            return Locale.getDefault().getLanguage();
        }

    };
    
    private static final ThreadLocal<Set<String>> identificadores = new ThreadLocal<Set<String>>() {
        
        @Override
        public Set<String> initialValue() {
            return new HashSet<String>(50);
        }

    };
    
    public SpellValidator() {
        SpellCheckerOptions opciones = SpellChecker.getOptions();
        opciones.setIgnoreCapitalization(true);
        opciones.setIgnoreAllCapsWords(false);
        opciones.setCaseSensitive(false);
    }

    public static synchronized void loadDictionary(String lang) {
        if (!diccionarios.containsKey(lang)) {
            try {
                String nombreDic = String.format("dictionary_%s.ortho", lang.toLowerCase());
                Debug.info("Cargando diccionario: " + nombreDic);
                
                InputStream is = Thread.currentThread().getContextClassLoader().
                        getResourceAsStream(nombreDic);
                
                if (is == null) {
                    Debug.warn("Servicio de revisión de "
                        + "ortografía para idioma '" + lang + "' no inicializado.");
                    return;
                }
                
                WordIterator iterator = new WordIterator(new InflaterInputStream(is), "UTF8");
                DictionaryFactory factory = new DictionaryFactory();
                factory.loadWords(iterator);
                Debug.info("Diccionario '" + nombreDic + "' cargado.");
                
                //Cargar palabras extras desde un archivo de texto.
                is.close();
                nombreDic = String.format("extraWords_%s.txt", lang.toLowerCase());
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream(nombreDic);
                
                if (is == null) {
                    Debug.info("No se encontró archivo con palabras extras.");
                } else {
                    iterator = new WordIterator(is, "UTF-8");
                    factory.loadWords(iterator);
                    is.close();
                    Debug.info("Archivo con palabras extras cargado.");
                }
                
                Dictionary diccionario = factory.create();
                diccionarios.put(lang, diccionario);
            } catch (Throwable ex) {
                Debug.error(ex.getMessage() + "Servicio de revisión de "
                        + "ortografía para idioma '" + lang + "' no inicializado.");
                diccionarios.put(lang, null);
            }
        }
    }

    public static Set<String> getAllPhrases() {
        return allPhrases;
    }

    public static void addReplacement(int originalHash, String replacement) {
        if (originalHash != replacement.hashCode()) {
            replacements.put(originalHash, replacement);
        }
    }
    
    public static String obtenerDescripcion(String clave) {
        String descripcion = ValidationUtils.DESCRIPTIONS.getString(
                SpellValidator.class.getName() + "." + clave);
        
        if (descripcion == null) {
            return "";
        }
        
        return descripcion;
    }
    
    public static void setCurrentLang(String lang) {
        currentLang.set(lang.substring(0, 2).toLowerCase());
    }

    @Override
    public Collection<ValidationMessage> validate(WebPage webPage, WebPage webPageCompleto) {

        String dictionaryKey = webPage.getLanguage().substring(0, 2).toLowerCase();
        currentLang.set(dictionaryKey);
        loadDictionary(dictionaryKey);
        
        Set<String> nombres = identificadores.get();
        nombres.clear();
        
        for (FormElement elemento : IterableWebElement.get(webPageCompleto, FormElement.class)) {
            nombres.add(elemento.getName());
        }
        
        Collection<ValidationMessage> messages = super.validate(webPage, null);

        messages.addAll(validate(webPageCompleto, false));

        return messages;
    }

    public Collection<ValidationMessage> validate(final WebPage webPage,
            boolean fix) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        Set<String> phrases = new HashSet<String>();

        webPage.setTitle(checkText(webPage.getTitle(), phrases, fix, "TITULO_WEB_PAGE"));
        webPage.setInitialJS(checkJS(webPage.getInitialJS(), phrases, fix, "JS_INICIAL"));
        webPage.setCalculos(checkJS(webPage.getCalculos(), phrases, fix, "CALCULOS"));

        if (!phrases.isEmpty()) {
            StringBuilder mensaje = new StringBuilder(256);
            mensaje.append("Se han detectado los siguientes errores ortográficos:\n");
            Iterator<String> iterador = phrases.iterator();
            
            while (iterador.hasNext()) {
                String palabra = iterador.next();
                mensaje.append(palabra).append("\n");
            }
            
            messages.add(new ValidationMessage(this, SPELLING_ERROR,
                    mensaje.toString(), webPage, webPage, Severity.WARN,
                    !replacements.isEmpty()) {

                @Override
                public void fix() {
                    validate(webPage, true);
                }

            });
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Container container, WebPage fullWebPage) {
        Collection<ValidationMessage> messages = super.validate(container, null);

        messages.addAll(validate(container, false));

        return messages;
    }

    public Collection<ValidationMessage> validate(final Container container,
            boolean fix) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();
        Set<String> phrases = new HashSet<String>();

        container.setTitle(checkText(container.getTitle(), phrases, fix, "TITULO_CONTAINER"));
        container.setJavaScript(checkEvents(container.getJavaScript(), phrases,
                fix, "JS_EVENTOS"));
        checkText(container.getLabels(), phrases, fix, "ETIQUETAS_CONTAINER");

        if (!phrases.isEmpty()) {
            StringBuilder mensaje = new StringBuilder(256);
            mensaje.append("Se han detectado los siguientes errores ortográficos:\n");
            Iterator<String> iterador = phrases.iterator();
            
            while (iterador.hasNext()) {
                String palabra = iterador.next();
                mensaje.append(palabra).append("\n");
            }
            
            messages.add(new ValidationMessage(this, SPELLING_ERROR,
                    mensaje.toString(), container, container, Severity.WARN,
                    !replacements.isEmpty()) {

                @Override
                public void fix() {
                    validate(container, true);
                }

            });
        }

        return messages;
    }

    @Override
    public Collection<ValidationMessage> validate(Widget widget, WebPage fullWebPage) {
        return validate(widget, false);
    }

    public Collection<ValidationMessage> validate(final Widget widget,
            boolean fix) {
        Collection<ValidationMessage> messages =
                new LinkedList<ValidationMessage>();

        Set<String> phrases = new HashSet<String>();

        if (widget instanceof Button) {
            Button button = (Button) widget;
            button.setEtiqueta(checkText(button.getEtiqueta(), phrases, fix, "ETIQUETA"));
        }

        if (widget instanceof CheckBox) {
            CheckBox checkBox = (CheckBox) widget;
            checkBox.setEtiqueta(checkText(checkBox.getEtiqueta(), phrases, fix, "ETIQUETA"));
        }

        if (widget instanceof ComboBox) {
            ComboBox comboBox = (ComboBox) widget;
            checkText(comboBox.getDatos(), phrases, fix, "ITEMS_COMBOBOX");
        }

        if (widget instanceof Input) {
            Input input = (Input) widget;
            input.setGuia(checkText(input.getGuia(), phrases, fix, "GUIA"));
            input.setJavaScript(checkEvents(input.getJavaScript(), phrases, fix, "JS_EVENTOS"));

            for (JSBehavior behavior : input.getBehaviors()) {
                behavior.setMessage(checkText(behavior.getMessage(), phrases,
                        fix, "MENSAJE_COMPORTAMIENTO"));
                
                if (behavior instanceof Link) {
                    Link link = (Link) behavior;
                    LiteralJS funcion = link.getPreLink();
                    funcion.setValor(checkJS(funcion.getValor(), phrases, fix, "PRE_LINK"));
                    funcion = link.getPosLink();
                    funcion.setValor(checkJS(funcion.getValor(), phrases, fix, "POS_LINK"));
                }
            }

            if (input.getAssistant() instanceof ListOfValues) {
                ListOfValues lov = (ListOfValues) input.getAssistant();
                lov.setTitle(checkText(lov.getTitle(), phrases, fix, "TITULO_LOV"));
                for (LOVField lOVField : lov.getFields()) {
                    lOVField.setTitle(checkText(lOVField.getTitle(), phrases,
                            fix, "TITULO_LOV_FIELD"));
                }
            }

        }

        if (widget instanceof Label) {
            Label label = (Label) widget;
            label.setTexto(checkText(label.getTexto(), phrases, fix, "TEXTO_WIDGET"));
            label.setGuia(checkText(label.getGuia(), phrases, fix, "GUIA"));
        }

        if (widget instanceof TabBar) {
            TabBar tabBar = (TabBar) widget;
            checkText(tabBar.getTabLabels(), phrases, fix, "ETIQUETAS_TAB_BAR");
        }
        
        //Revisar fórmulas
        if (widget instanceof FormElement) {
            FormElement fe = (FormElement) widget;
            String valorInicial = fe.getRelleno();
            
            if (valorInicial.startsWith("=")) {
                Formula formula = FormulaParser.parse(fe);
                Collection<String> literales = new ArrayList<String>(formula.getStringLiterals());
                
                checkText(literales, phrases, fix, "TEXTO_FORMULAS");
            }
        }

        if (!phrases.isEmpty()) {
            StringBuilder mensaje = new StringBuilder(256);
            mensaje.append("Se han detectado los siguientes errores ortográficos:\n");
            Iterator<String> iterador = phrases.iterator();
            
            while (iterador.hasNext()) {
                String palabra = iterador.next();
                mensaje.append(palabra).append("\n");
            }
            
            messages.add(new ValidationMessage(this, SPELLING_ERROR, mensaje.toString(),
                    widget, widget, Severity.WARN, !replacements.isEmpty()) {

                @Override
                public void fix() {
                    validate(widget, true);
                }

            });
        }

        return messages;
    }

    private void checkText(final Collection<String> textos,
            final Set<String> phrases, final boolean fix, final String codigoError) {
        CollectionUtils.transform(textos, new Transformer() {

            @Override
            public Object transform(Object input) {
                return checkText((String) input, phrases, fix, codigoError);
            }

        });
    }

    public String checkEvents(String js, Set<String> phrases, boolean fix, String codigoError) {
        PropiedadJavascript propiedadJavascript =
                new PropiedadJavascript(PropiedadJavascript.Tipo.EVENTOS);

        propiedadJavascript.setValorString(js);

        Map<String, String> eventos = propiedadJavascript.getEventos();

        for (String eventoJS : eventos.keySet()) {
            eventos.put(eventoJS, checkJS(eventos.get(eventoJS), phrases, fix, codigoError));
        }

        return propiedadJavascript.getValorString();
    }

    public String checkJS(final String js, final Set<String> phrases,
            final boolean fix, String codigoError) {
        try {
            Scope root = JSParser.getRootNode(js);

            root.visit(new SpellingNodeVisitor(this, phrases, fix, codigoError));

            if (fix) {
                return JavascriptFormater.format(root.toSource());
            } else {
                return js;
            }

        } catch (EvaluatorException ex) {
            Debug.error("Error al parsear js: " + js, ex);

            return js;
        }
    }

    public String checkText(String texto, Set<String> phrases, boolean fix, String codigoError) {
        allPhrases.add(texto);
        Dictionary dic = diccionarios.get(currentLang.get());
        String formatoError = "==> '%s' en %s. Sugerencias: %s";
        
        if (dic != null) {
            Document documento = new DefaultStyledDocument();
            
            try {
                documento.insertString(0, texto, null);
                Tokenizer tokenizer = new Tokenizer(documento, dic,
                        new Locale(currentLang.get()), 0, texto.length(), null);
                
                String palabra = tokenizer.nextInvalidWord();
                
                while (palabra != null) {
                    List<Suggestion> sugerencias = Collections.EMPTY_LIST;
                    
                    //Soporte para abreviaciones provisional
                    String abreviacion = palabra + ".";
                    boolean hayAbrev = dic.exist(abreviacion);
                    
                    if (hayAbrev && texto.contains(abreviacion)) {
                        palabra = tokenizer.nextInvalidWord();
                        continue;
                    }
                    
                    synchronized (dic) {
                        sugerencias = dic.searchSuggestions(
                                palabra.toLowerCase());
                    }
                    
                    if (hayAbrev) {
                        sugerencias.add(0, new Suggestion(abreviacion, 1));
                    }
                    
                    String coincidencias = "";
                    if (sugerencias.isEmpty()) {
                        coincidencias = "(ninguna)";
                    } else {
                        sugerencias = sugerencias.subList(0, Math.min(5,
                                sugerencias.size()));
                        coincidencias = StringUtils.join(sugerencias, ", ");
                    }
                    
                    if (codigoError == null) {
                        phrases.add(palabra);
                    } else {
                        phrases.add(String.format(formatoError, palabra,
                                obtenerDescripcion(codigoError), coincidencias));
                    }
                    
                    palabra = tokenizer.nextInvalidWord();
                }
                
            } catch (Exception ex) {
                Debug.info("Error al revisar ortografía. Detalles: "
                        + ex.getLocalizedMessage());
            }
        }

        if (replacements.containsKey(texto.hashCode())) {
            if (fix) {
                return replacements.get(texto.hashCode());
            } else {
                phrases.add(texto);
            }
        }

        return texto;
    }
    
    
    private static class SpellingNodeVisitor implements NodeVisitor {
        
        private Set<String> frases = null;
        
        private boolean arreglar = false;
        
        private SpellValidator validador = null;
        
        private String fuente = "";
        
        public SpellingNodeVisitor(SpellValidator validador, Set<String> frases, boolean fix, String fuente) {
            this.validador = validador;
            this.frases = frases;
            this.arreglar = fix;
            this.fuente = fuente;
        }

        @Override
        public boolean visit(AstNode nodo) {
            if (nodo instanceof StringLiteral) {
                StringLiteral literal = (StringLiteral) nodo;
                String valor = literal.getValue();
                
                if (identificadores.get().contains(valor)) {
                    return true;
                }
                
                //No verificar ortografía en funciones donde se admiten solo
                //identificadores, como $$
                AstNode parent = nodo.getParent();
                if (parent != null && parent instanceof FunctionCall) {
                    //Este literal de cadena debe ser parte de los argumentos
                    //de la función.
                    FunctionCall funcion = (FunctionCall) parent;
                    String target = funcion.getTarget().toSource();
                    Matcher buscador = EXCLUSIONES_JS.matcher(target);
                    
                    boolean noRevisar = target.matches("(?:c\\.)?\\$[NV\\$]?");
                    noRevisar |= buscador.find();
                    
                    if (noRevisar) {
                        return true;
                    }
                    
                    //Si el literal de cadena no cumple ninguna de las
                    //condiciones anteriores, se analiza.
                    literal.setValue(validador.checkText(valor, frases, arreglar, fuente));
                }
            }
            
            return true;
        }
        
    }

}
