package com.fitbank.webpages;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fitbank.js.JS;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadEstilos;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadNumerica;
import com.fitbank.propiedades.anotaciones.UtilPropiedades;
import com.fitbank.serializador.html.ConstructorHtml;
import com.fitbank.serializador.html.SerializableHtml;
import com.fitbank.serializador.xml.ExcepcionParser;
import com.fitbank.serializador.xml.SerializableXml;
import com.fitbank.serializador.xml.SerializadorXml;
import com.fitbank.serializador.xml.XML;
import com.fitbank.util.Clonador;
import com.fitbank.util.Debug;
import com.fitbank.util.Editable;
import com.fitbank.util.Servicios;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FormElement;

/**
 * Clase WebElement. Esta clase es la base para otras clases como WebPage,
 * Container, Widget. Extiende una lista para poder proporcionar la capacidad de
 * contener otros elementos en los objetos de esta clase.
 * 
 * @author FitBank JT
 * @version 2.0
 */
@SuppressWarnings("unchecked")
public abstract class WebElement<T extends WebElement> extends ArrayList<T>
        implements SerializableXml, SerializableHtml {

    private static final long serialVersionUID = 1L;

    private final static String EVENT_TEMPLATE = "tryEvent('%s', '%s', function(e){\n"
            + "%s;\n"
            + "});\n";

    protected final Map<String, Propiedad> properties = new LinkedHashMap<String, Propiedad>();

    @XML(ignore=true)
    @JS(ignore=true)
    private String tag = null;

    @XML(ignore=true)
    @JS(ignore=true)
    private WebElement parent;

    @XML(ignore=true)
    private String id;

    /**
     * Variable que indica si el container es una tabla dinamica.
     * (Versiones superiores)
     */
    @XML(ignore=true)
    @JS(ignore=true)
    private boolean isDTableContainer = false;

    /**
     * Variable que indica si el container es una tabla horizontal.
     * (Versiones superiores)
     */
    @XML(ignore=true)
    @JS(ignore=true)
    private boolean isHTableContainer = false;

    public WebElement() {
        resetId();
        def("cssClass", new PropiedadEstilos());
        def("x", 0);
        def("y", 0);
        def("z", 0);
        def("h", 0);
        def("w", 0);
    }

    protected final void def(String name, Propiedad property) {
        property.setNombre(name);
        property.setDescripcion(UtilPropiedades.getDescription(this.getClass(), name));
        properties.put(name, property);
    }

    protected final void def(String name, Object o) {
        properties.put(name, UtilPropiedades.getPropiedad(this, o, name));
    }

    /**
     * Obtiene el id único interno de este elemento. Nota: NO es equivalente a
     * la propiedad id en el html, para eso usar getHTMLId.
     *
     * @return String con un id
     */
    public final String getId() {
        return id;
    }

    public final void resetId() {
        this.id = Servicios.generarIdUnicoTemporal();
    }

    /**
     * Obtiene el id único de este elemento a ser usado en HTML.
     *
     * @return String con un id
     */
    public abstract String getHTMLId();

    @Override
    public String toString() {
        return getClass().getName();
    }

    // ////////////////////////////////////////////////////////
    // Metodos manejo xml
    // ////////////////////////////////////////////////////////

    /**
     * Metodo que extrae los nodos child de un objeto Base Formas.
     * 
     * @return (List<String>) Lista con los strings de los child nodes del
     *         objeto WebElement
     */
    @JS(ignore=true)
    protected abstract Collection<String> getHijosXml();

    /**
     * Método que retorna los atributos de un elemento XML.
     * 
     * @return (List<String>) Lista con los strings de los atributos de un
     *         elemento XML
     */
    @JS(ignore=true)
    protected abstract Collection<String> getAtributosXml();

    /**
     * Método que devuelve el nodo DOM al que pertenece un elemento en el XML.
     * 
     * @param (Document) document La representación en objeto DOM de un
     *        documento.
     * @return (Node) Un nodo del DOM.
     */
    @JS(ignore=true)
    public Node getNode(Document document) {
        Element elemento = document.createElement(getTag());

        if (getAtributosXml() != null) {
            for (String attr : getAtributosXml()) {
                if (!properties.get(attr).esValorPorDefecto()) {
                    elemento.setAttribute(attr, properties.get(attr)
                            .getValorString());
                }
            }
        }

        if (getHijosXml() != null) {
            for (String hijo : getHijosXml()) {
                if (!properties.get(hijo).esValorPorDefecto()) {
                    Element interno = document.createElement(hijo);
                    interno.appendChild(document.createTextNode(properties.get(
                            hijo).getValorString()));
                }

            }
        }

        return elemento;
    }

    public Object parsear(Node node, Type type) throws ExcepcionParser {
        return null;
    }

    /**
     * Setea un valor para el tag o propiedad especificada.
     * 
     * @param propiedad El string del tag o propiedad.
     * @param valor Object con el valor.
     */
    @JS(ignore=true)
    public void setValorXml(String propiedad, Object valor) throws ExcepcionParser {
        if (propiedad.equals("class")) {
            //Modo compatibilidad con formularios en versiones superiores
            if (this instanceof Container) {
                this.byPassContainerClass(valor);
            }

            // Para el resto de elementos, solo se pasa el tipo, no hacer nada.
            return;
        }

        if (!properties.containsKey(propiedad)) {
            DynaBean db = new WrapDynaBean(this);
            if (db.getDynaClass().getDynaProperty(propiedad) != null) {
                db.set(propiedad, valor);
            } else if (!propiedad.matches("(url|pos)")) {
                Debug.error("Tag " + propiedad + " no existe para el elemento "
                        + getClass().getName());
            }
            return;
        }

        if (valor instanceof String) {
            properties.get(propiedad).setValorString((String) valor);
        } else {
            properties.get(propiedad).setValor(valor);
        }

        this.processNewerWebElements(propiedad, valor);
    }

    /**
     * Metodo que setea el tipo de clonacion para containers antiguos
     * segun la clase del container en versiones superiores.
     * 
     * @param valor Clase de tipo de container
     * @throws ExcepcionParser 
     */
    private void byPassContainerClass(Object valor) throws ExcepcionParser {
        String containerClass = valor.toString();

        if (containerClass.contains(".TableContainer")) {
            this.setValorXml("clo", "T");
        } else if (containerClass.contains(".HorizontalTableContainer")) {
            this.setValorXml("clo", "T");
            this.isHTableContainer = true;
        } else if (containerClass.contains(".DynamicTableContainer")) {
            this.setValorXml("clo", "T");
            this.isDTableContainer = true;
        } else if (containerClass.contains(".ColumnsContainer")) {
            this.setValorXml("clo", "C");
        } else if (containerClass.contains(".NormalContainer")) {
            this.setValorXml("clo", "N");
        } else if (containerClass.contains(".StackContainer")) {
            this.setValorXml("clo", "M");
        }
    }

    /**
     * Metodo que procesa propiedades extras para containers en versiones
     * anteriores, basandose en propiedades de containers en versiones
     * superiores.
     * 
     * @param propiedad Propiedad a validar
     * @param valor Valor a cambiar
     */
    private void processNewerWebElements(String propiedad, Object valor) {
        if (this.isDTableContainer) {
            if ("max".equals(propiedad)) {
                properties.get("win").setValorString((String) valor);
            }
        }

        if (this.isHTableContainer) {
            if ("horizontal".equals(propiedad)) {
                properties.get("horizontal").setValorString("1");
            }
        }
    }

    /**
     * Devuelve todos los strings de los hijos en una lista.
     * 
     * @return (Collection<SerializableXml>) Lista con los strings de los hijos del
     *         objeto WebElement.
     */
    @JS(ignore=true)
    public Collection<SerializableXml> getChildren() {
        return new ArrayList<SerializableXml>(this);
    }

    // ////////////////////////////////////////////////////////
    // Metodos edicion para el generador
    // ////////////////////////////////////////////////////////

    /**
     * Método que devuelve una lista de objetos Propiedad con las properties que
     * serán usadas solo en el editor de formularios.
     * 
     * @return (List<Propiedad>) Lista con las properties para uso dentro del
     *         Editor de Formularios.
     */
    @JS(ignore=true)
    public abstract Collection<Propiedad<?>> getPropiedadesEdicion();

    /**
     * Convierte una lista de objetos de tipo String a una lista con objetos de
     * tipo Propiedad.
     * 
     * @param List
     *            <String>()nombres Lista con los strings de los nombres de las
     *            properties.
     * @return
     */
    protected List<Propiedad<?>> toPropiedades(Object... objects) {
        List<Propiedad<?>> props = new ArrayList<Propiedad<?>>(objects.length);

        for (Object object : objects) {
            if (object instanceof Propiedad) {
                props.add((Propiedad) object);
            } else if (object instanceof String) {
                props.add(properties.get((String) object));
            } else if (object == null) {
                // No hacer nada
            } else {
                throw new RuntimeException("Objeto inválido: " + object);
            }
        }

        return props;
    }

    // ////////////////////////////////////////////////////////
    // Getters y setters de properties
    // ////////////////////////////////////////////////////////

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public String getCSSClass() {
        return ((PropiedadEstilos) properties.get("cssClass")).getValor();
    }

    public void setCSSClass(String cssClass) {
        properties.get("cssClass").setValor(cssClass);
    }

    public int getX() {
        return ((PropiedadNumerica<Integer>) properties.get("x")).getValor();
    }

    public void setX(int x) {
        properties.get("x").setValor(x);
    }

    public int getY() {
        return ((PropiedadNumerica<Integer>) properties.get("y")).getValor();
    }

    public void setY(int y) {
        properties.get("y").setValor(y);
    }

    public int getZ() {
        return ((PropiedadNumerica<Integer>) properties.get("z")).getValor();
    }

    public void setZ(int z) {
        properties.get("z").setValor(z);
    }

    @Editable
    public int getH() {
        return ((PropiedadNumerica<Integer>) properties.get("h")).getValor();
    }

    public void setH(int h) {
        properties.get("h").setValor(h);
    }

    @Editable
    public int getW() {
        return ((PropiedadNumerica<Integer>) properties.get("w")).getValor();
    }

    public void setW(int w) {
        properties.get("w").setValor(w);
    }

    // ////////////////////////////////////////////////////////
    // Métodos generales
    // ////////////////////////////////////////////////////////

    /**
     * Subclases deben implementar esto para generar una lista de clases CSS a
     * ser usadas.
     */
    @JS(ignore=true)
    protected Collection<String> getCSSClasses() {
        Collection<String> cssClasses = new HashSet<String>();

        cssClasses.add(getCSSClass());
        cssClasses.add(Servicios.toDashedString(getClass().getSimpleName()));

        return cssClasses;
    }

    /**
     * Agrega las clases CSS como deben ser usadas.
     * @param html
     */
    protected void generarClasesCSS(ConstructorHtml html, String... extra) {
        Collection<String> classes = getCSSClasses();
        classes.addAll(Arrays.asList(extra));

        String classString = StringUtils.join(classes, " ");

        if (html.getTagActual().getAtributos().containsKey("class")) {
            html.extenderAtributo("class", " " + classString, " ");
        } else {
            html.setAtributo("class", classString, "");
        }
    }

    /**
     * Método devuelve un Widget.
     * 
     * @param baseFormas
     *            Número del Widget que se requiere
     * 
     * @return Widget solicitado
     */
    @JS(ignore=true)
    public final T getBaseFormas(int baseFormas) {
        if (baseFormas < size()) {
            return get(baseFormas);
        }

        Debug.error("Se está obtieniendo una WebElement que no existe");

        return null;
    }

    /**
     * Obtiene el formulario parent.
     * 
     * @return WebPage parent.
     */
    @JS(ignore=true)
    public final WebPage getParentWebPage() {
        return getParent() == null ? null
                : getParent() instanceof WebPage ? (WebPage) getParent()
                        : getParent().getParentWebPage();
    }

    /**
     * Obtiene la fila madre.
     * 
     * @return WebPage parent.
     */
    @JS(ignore=true)
    public final Container getParentContainer() {
        return getParent() == null ? null
                : getParent() instanceof Container ? (Container) getParent()
                        : getParent().getParentContainer();
    }

    /**
     * Mover un objeto.
     * 
     * @param cual
     *            Posicion del objeto
     * @param cuanto
     *            Cuanto moverlo
     */
    public void moveChild(int cual, int cuanto) {
        if (cual + cuanto < size() && cual >= 0) {
            T temp = get(cual);
            remove(cual);
            add(cual + cuanto, temp);
        }
    }

    /**
     * Método devuelve el <i>primer</i> FormElement dado el nombre.
     *
     * @param name
     *            Nombre del elemento
     *
     * @return FormElement solicitado
     */
    public FormElement findFormElement(String name) {
        FormElement dato = null;
        for (WebElement<?> b : this) {
            if (b instanceof FormElement) {
                dato = (FormElement) b;
                if (dato.getNameOrDefault().equals(name)) {
                    return dato;
                }
            }
            dato = b.findFormElement(name);
            if (dato != null) {
                return dato;
            }
        }
        return null;
    }

    /**
     * Método devuelve el Widget dado el id.
     *
     * @param id
     *            Id del elemento
     *
     * @return Widget solicitado
     */
    public Widget findWidget(String id) {
        Widget widget = null;
        for (WebElement<?> b : this) {
            if (b instanceof FormElement && b instanceof Widget) {
                widget = (Widget) b;
                if (widget.getHTMLId().equals(id)) {
                    return widget;
                }
            }
            widget = b.findWidget(id);
            if (widget != null) {
                return widget;
            }
        }
        return null;
    }

    /**
     * Método devuelve el <i>primer</i> FormElement dado el DataSource.
     *
     * @param name
     *            Nombre del elemento
     *
     * @return FormElement solicitado
     */
    public FormElement findFormElement(DataSource datasource) {
        FormElement dato = null;
        for (WebElement b : this) {
            if (b instanceof FormElement) {
                dato = (FormElement) b;
                if (dato.getDataSource().equals(datasource)) {
                    return dato;
                }
            }
            dato = b.findFormElement(datasource);
            if (dato != null) {
                return dato;
            }
        }
        return null;
    }

    /**
     * Método devuelve <i>todos</i> los FormElement dado el DataSource.
     *
     * @param name
     *            Nombre del elemento
     *
     * @return FormElement solicitado
     */
    public Collection<FormElement> findFormElements(DataSource datasource) {
        return findFormElements(datasource, true);
    }

    /**
     * Método devuelve <i>todos</i> los FormElement dado el DataSource.
     *
     * @param dataSource
     *            DataSource del elemento
     *
     * @return FormElement solicitado
     */
    public Collection<FormElement> findFormElementsIgnoreNull(DataSource datasource) {
        return findFormElements(datasource, false);
    }

    /**
     * Método devuelve <i>todos</i> los FormElement dado el DataSource.
     *
     * @param dataSource
     *            DataSource del elemento
     * @param fromAlias
     *            Alias de las dependencias
     * @param variante
     *            Variante de las dependencias
     *
     * @return FormElement solicitado
     */
    public Collection<FormElement> findFormElementsIgnoreNull(
            DataSource datasource, String variante, String... fromAliases) {
        return findFormElements(datasource, false, variante, fromAliases);
    }

    private Collection<FormElement> findFormElements(DataSource datasource,
            boolean normal) {
        return findFormElements(datasource, normal, "");
    }

    private Collection<FormElement> findFormElements(DataSource datasource,
            boolean normal, String variante, String... fromAliases) {
        Collection<FormElement> elements = new LinkedList<FormElement>();
        FormElement dato = null;
        for (WebElement b : this) {
            if (b instanceof FormElement) {
                dato = (FormElement) b;
                if (normal) {
                    if (dato.getDataSource().equals(datasource)) {
                        elements.add(dato);
                    }
                } else {
                    if (dato.getDataSource().equalsIgnoreNull(datasource, variante, fromAliases)) {
                        elements.add(dato);
                    }
                }
            } else {
                elements.addAll(b.findFormElements(datasource, normal, variante, fromAliases));
            }
        }
        return elements;
    }

    /**
     * Agrega los Eventos de Javascript a utilizar
     *
     * @param html Constructor html
     * @param js PropiedadJavascript de eventos con los valores
     */
    protected final void generarEventosJavascript(ConstructorHtml html, PropiedadJavascript js) {
        html.setAtributo("registro", getIndiceClonacion());

        if (getIndiceClonacion() == 0) {
            String name = null;

            if (this instanceof FormElement) {
                name = ((FormElement) this).getNameOrDefault();
            }

            Map<String, String> eventos = js.getEventos();
            for (String evento : eventos.keySet()) {
                String code = eventos.get(evento);

                generarEventoJavascript(html, evento, name, code);
            }
        }
    }

    /**
     * Genera un evento en específico para un elemento. Puede ser sobreescrito.
     *
     * @param html Generador de html
     * @param evento Nombre del evento, ej. onclick
     * @param name nombre o id del elemento (si es que tiene)
     * @param code Código
     */
    protected void generarEventoJavascript(ConstructorHtml html, String evento,
            String name, String code) {
        evento = evento.replaceFirst("^on", "");

        WebPageEnviroment.addRawJavascriptInicial(String.format(
                EVENT_TEMPLATE, name, evento, code));
    }

    // ////////////////////////////////////////////////////////
    // Métodos para manejar elementos de la lista
    // ////////////////////////////////////////////////////////

    /**
     * Obtiene la posicion de este objeto en el parent.
     * 
     * @return int con el valor
     */
    @JS(ignore=true)
    public int getPosicion() {
        return getParent() != null ? getParent().indexOf(this) : -1;
    }

    /**
     * Obtiene el parent de este elemento.
     * 
     * @return Padre
     */
    @JS(ignore=true)
    public final WebElement getParent() {
        return parent;
    }

    /**
     * Cambia el parent de este elemento.
     * 
     * @param parent
     *            Padre
     */
    public void setParent(WebElement padre) {
        this.parent = padre;
    }

    /**
     * Obtiene el tipo de este objeto. El tipo es simplemente el nombre de la
     * clase sin el paquete.
     * 
     * @return String con el tipo
     */
    public String getTipo() {
        return getClass().getSimpleName();
    }

    /**
     * Actualiza los parámetros de los hijos.
     */
    public void updateChildren() {
    }

    /**
     * Actualiza los parámetros de los hijos.
     */
    @Override
    public WebElement clone() {
        return Clonador.clonar(this);
    }

    /**
     * Usado solo en casos en que no se va a guardar a un archivo. Para esos
     * casos mejor usar SerializadorXml directo con un FileWriter.
     * 
     * @return String con el xml
     */
    public String toStringXml() {
        String xml = new SerializadorXml().serializar(this);

        return xml.substring(xml.indexOf("\n") + 1);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o != null && getClass().equals(o.getClass())
                && hashCode() == o.hashCode();
    }

    // ///////////////////
    // Métodos de Clonacion
    // /////////////////////

    /**
     * Obtiene el número de clonación de este elemento.
     *
     * @return Numero de registro
     */

    protected int getIndiceClonacion() {
        if (this instanceof Container) {
            return ((Container) this).getIndiceClonacionActual();
        } else if (getParentContainer() != null) {
            return getParentContainer().getIndiceClonacionActual();
        } else {
            return 0;
        }
    }

    // ////////////////////////////////////////////////////////
    // Métodos del interfaz Lista
    // ////////////////////////////////////////////////////////

    //<editor-fold defaultstate="collapsed" desc="Métodos de List">
    /**
     * Metodo implementado de la interface List. Remueve un elemento de la
     * lista.
     *
     * @param (int) index El indice de ubcacion del elemento en la lista.
     */
    @Override
    public T remove(int index) {
        T elemento = super.remove(index);

        onRemove(elemento);

        return elemento;
    }

    /**
     * Metodo implementado de la interface List. Remueve un elemento de la
     * lista. de cualquier tipo.
     *
     * @param (Object) o El objeto que se desea remover.
     */
    @Override
    public boolean remove(Object o) {
        boolean res = super.remove((T) o);

        if (res) {
            onRemove((T) o);
        }

        return res;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean res = super.addAll(c);

        if (res) {
            for (T elemento : c) {
                onAdd(elemento);
            }
            updateChildren();
        }

        return res;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        boolean res = super.addAll(index, c);

        if (res) {
            for (T elemento : c) {
                onAdd(elemento);
            }
            updateChildren();
        }

        return res;
    }

    @Override
    public void clear() {
        for (T elemento : this) {
            onRemove(elemento);
        }

        super.clear();
    }

    @Override
    public synchronized boolean add(T elemento) {
        boolean res = super.add(elemento);

        if (res) {
            onAdd(elemento);
            updateChildren();
        }

        return res;
    }

    @Override
    public void add(int index, T elemento) {
        super.add(index, elemento);

        onAdd(elemento);
        updateChildren();
    }

    /**
     * Sobreescribe el método de LinkedList.
     *
     * @param index
     *            Indice
     * @param element
     *            Widget
     *
     * @return Objeto
     */
    @Override
    public synchronized T set(int index, T elemento) {
        T eliminado = super.set(index, elemento);

        onRemove(eliminado);
        onAdd(elemento);
        updateChildren();

        return eliminado;
    }

    /**
     * Reemplaza un elemento por otro copiando las properties posibles.
     *
     * @param elemento
     * @param clase
     *
     * @return El nuevo elemento o null si no existe el elemento original
     */
    public T replace(T elemento, Class<? extends T> clase) {
        int pos = elemento.getPosicion();
        if (pos >= 0) {
            T reemplazo;
            try {
                reemplazo = clase.getConstructor().newInstance();
            } catch (Exception e) {
                throw new Error(e);
            }

            set(pos, reemplazo);

            try {
                BeanUtils.copyProperties(reemplazo, elemento);
            } catch (IllegalAccessException e) {
                Debug.error(e);
            } catch (InvocationTargetException e) {
                Debug.error(e);
            }

            return reemplazo;
        }
        return null;
    }

    private void onAdd(T elemento) {
        if (elemento == null) {
            return;
        }

        elemento.setParent(this);
    }

    private void onRemove(T elemento) {
        if (elemento == null) {
            return;
        }

        elemento.setParent(null);
    }
    //</editor-fold>

}
