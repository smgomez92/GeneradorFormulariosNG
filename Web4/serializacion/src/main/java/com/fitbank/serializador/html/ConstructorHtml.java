package com.fitbank.serializador.html;

import lombok.extern.slf4j.Slf4j;
import java.util.Stack;

@Slf4j
public class ConstructorHtml implements SerializableHtml {

    private static final long serialVersionUID = 1L;

    private Tag cabeza = new Tag("html");

    private Tag tagAtributos = cabeza;

    private Tag tagActual = cabeza;

    private Stack<Tag> tags = new Stack<Tag>();

    public ConstructorHtml() {
        tags.push(getCabeza());
    }

    public ConstructorHtml(boolean Ng) {
        tags.push(null);
    }

    public Tag getTagActual() {
        return tagActual;
    }

    public Tag getTagAtributos() {
        return tagAtributos;
    }

    public void setCabeza(Tag cabeza) {
        this.cabeza = cabeza;
    }

    public Tag getCabeza() {
        return cabeza;
    }

    public void generateHtml(ConstructorHtml xhtml) {
        xhtml.cabeza = this.getCabeza();
    }

    /**
     * Agrega un tag . Nuevos atributos serán agregados a este tag abierto.
     * Nuevos tags serán agregados al tag padre de este tag.
     *
     * @param tagName Nombre del tag
     */
    public void agregar(Tag tag) {
        tagActual.getHijos().add(tag);
    }

    /**
     * Abre un tag y lo agrega al tag actual. Nuevos tags y atributos serán
     * agregados a este nuevo tag.
     *
     * @param tagName Nombre del tag
     * @return El tag abierto
     */
    public Tag abrir(String tagName) {
        tagAtributos = new Tag(tagName);
        tagActual.getHijos().add(tagAtributos);
        tagActual = tagAtributos;
        tags.push(tagAtributos);

        return tagActual;
    }

    /**
     * Abre un tag y lo agrega al tag actual. Nuevos tags y atributos serán
     * agregados a este nuevo tag.
     *
     * @param tagName Nombre del tag
     * @return El tag abierto
     */
    public Tag abrirNg(String tagName) {
        tagAtributos = new Tag(tagName, true);
        tagActual.getHijos().add(tagAtributos);
        tagActual = tagAtributos;
        tags.push(tagAtributos);

        return tagActual;
    }

    /**
     * Agrega un tag y lo cierra. Nuevos atributos serán agregados a este tag
     * abierto. Nuevos tags serán agregados al tag padre de este tag.
     *
     * @param tagName Nombre del tag
     */
    public void agregar(String tagName) {
        Tag tag = new Tag(tagName);

        getTagActual().getHijos().add(tag);
        tagAtributos = tag;
    }

    /**
     * Agrega un tag y lo cierra. Nuevos atributos serán agregados a este tag
     * abierto. Nuevos tags serán agregados al tag padre de este tag.
     *
     * @param tagName Nombre del tag
     */
    public void agregarNg(String tagName) {
        Tag tag = new Tag(tagName, true);

        getTagActual().getHijos().add(tag);
        tagAtributos = tag;
    }

    /**
     * Agrega un tag y lo cierra. Nuevos atributos serán agregados a este tag
     * abierto. Nuevos tags serán agregados al tag padre de este tag.
     *
     * @param tagName Nombre del tag
     * @param texto Contenido del tag
     */
    public void agregar(String tagName, String texto) {
        Tag tag = new Tag(tagName);

        tag.getHijos().add(new Texto(texto));
        getTagActual().getHijos().add(tag);
        tagAtributos = tag;
    }

    /**
     * Agrega la cabeza del argumento al tag actual en este constructor.
     *
     * @param html
     */
    public void agregar(ConstructorHtml html) {
        getTagActual().getHijos().add(html.getCabeza());
    }

    /**
     * Cierra el tag actual si está abierto. Nuevos tags y atributos serán
     * agregados al tag padre.
     *
     * @param tagName Nombre del tag
     */
    public void cerrarCondicional(String tagName) {
        if (getTagActual().getNombre().equals(tagName)) {
            cerrar(tagName);
        }
    }

    /**
     * Cierra el tag actual. Nuevos tags y atributos serán agregados al tag
     * padre.
     *
     * @param tagName Nombre del tag
     */
    public void cerrar(String tagName) {
        if (!getTagActual().getNombre().equals(tagName)) {
            throw new Error("Tag que cierra no coincide: " + tagName
                    + ", se esperaba: " + getTagActual().getNombre());
        }

        // TODO: imprimir ideas de mejorar el código
        if (tagActual.getHijos().isEmpty() && !tagActual.getNombre().equals(
                "td")) {
            log.info("Mejor usar agregarTagSimple en vez de "
                    + "abrirTag y cerrarTag ya que '" + tagName
                    + "' no tiene tags interiores");
        }

        tags.pop();
        tagActual = tagAtributos = tags.peek();
    }

    public void borrarTagActual() {
        tags.pop();
        tags.peek().getHijos().remove(tagActual);
        tagActual = tagAtributos = tags.peek();
    }

    /**
     * Agrega un atributo al tag de atributos actual.
     *
     * @param nombre Nombre del atributo
     * @param valor Valor del atributo
     */
    public void setAtributo(String nombre, Object valor) {
        setAtributo(nombre, valor, null);
    }

    /**
     * Agrega un atributo al tag de atributos actual si el valor es diferente
     * del valor por defecto.
     *
     * @param nombre Nombre del atributo
     * @param valor Valor del atributo
     * @param valorPorDefecto Valor del atributo por defecto
     */
    public void setAtributo(String nombre, Object valor, Object defecto) {
        setAtributo(nombre, valor, defecto, false);
    }

    /**
     * Agrega un atributo al tag de atributos actual si el valor es diferente
     * del valor por defecto.
     *
     * @param nombre Nombre del atributo
     * @param valor Valor del atributo
     * @param valorPorDefecto Valor del atributo por defecto
     * @param extender Extiende si existe un valor anterior para este atributo.
     * Usado internamente solo como indicador de mensajes. Si se requiere
     * extender revisar metodo extenderAtributo
     */
    private void setAtributo(String nombre, Object valor, Object defecto,
            boolean extender) {
        String valorActual = tagAtributos.getAtributos().get(valor);

        if (!nombre.matches("[\\w_\\-]+")) {
//            log.warn("Nombre de atributo inválido: " + nombre);
        }

        if (valorActual != null) {
           // log.warn("Se está sobreescribiendo el valor de '"
             //       + nombre + "', antes: '" + valorActual + "' ahora: "
               //     + valor);
        }

        if (!extender && nombre.substring(0, 2).equalsIgnoreCase("on")) {
           // log.info("Mejor usar extenderAtributo para eventos");
        }

//        if (!nombre.equals(nombre.toLowerCase())) {
//            log.warn("Se está añadiendo un tag que no está en minúsculas: '"
//                    + nombre + "'");
//        }

        if (!valor.equals(defecto)) {
            tagAtributos.getAtributos().put(nombre, String.valueOf(valor));
        }
    }

    public void setAtributoNg(String nombre) {
        tagAtributos.getAtributos().put(nombre, null);
    }

    /**
     * Extiende un atributo en el tag actual de atributos.
     *
     * @param nombre Nombre del atributo.
     * @param valor Valor a ser agregado.
     */
    public void extenderAtributo(String nombre, Object valor) {
        if (tagAtributos.getAtributos().containsKey(nombre)) {
            if (nombre.equals("class")) {
                valor = " " + valor;
            }
            valor = tagAtributos.getAtributos().get(nombre) + valor;
            tagAtributos.getAtributos().remove(nombre);
        }

        setAtributo(nombre, valor, null, true);
    }

    /**
     * Extiende un atributo en el tag actual de atributos si el valor es
     * diferente al valor por defecto.
     *
     * @param nombre Nombre del atributo.
     * @param valor Valor a ser agregado.
     * @param defecto Valor por defecto
     */
    public void extenderAtributo(String nombre, Object valor, Object defecto) {
        if ("style".equals(nombre)) {
            log.info("Mejor usar setEstilo para propiedad style");
        }

        if (!valor.equals(defecto)) {
            extenderAtributo(nombre, valor);
        }
    }

    /**
     * Agrega un estilo al atributo style del tag actual de atributos.
     *
     * @param propiedad Nombre de la propiedad del estilo.
     * @param valor Valor de la propiedad del estilo.
     */
    public void setEstilo(String propiedad, Object valor) {
        extenderAtributo("style", propiedad + ":" + valor + ";");
    }

    /**
     * Agrega un estilo al atributo style del tag actual de atributos.
     *
     * @param propiedad Nombre de la propiedad del estilo.
     * @param valor Valor de la propiedad del estilo.
     * @param unidad Unidad de la propiedad a ser agregada despues del valor si
     * este no es igual a 0.
     */
    public void setEstilo(String propiedad, Object valor, String unidad) {
        setEstilo(propiedad, valor, unidad, new Object());
    }

    /**
     * Agrega un estilo al atributo style del tag actual de atributos si el
     * valor es diferente al valor por defecto.
     *
     * @param propiedad Nombre de la propiedad del estilo.
     * @param valor Valor de la propiedad del estilo.
     * @param unidad Unidad de la propiedad a ser agregada despues del valor si
     * este no es igual a 0.
     * @param defecto Valor por defecto.
     */
    public void setEstilo(String propiedad, Object valor, String unidad,
            Object defecto) {
        if (!valor.equals(defecto)) {
            String valueOf = String.valueOf(valor);

            extenderAtributo("style", propiedad + ":" + valor
                    + (!"0".equals(valueOf) ? unidad : "") + ";");
        }
    }

    /**
     * Agrega texto dentro del tag actual.
     *
     * @param texto Texto a ser agregado.
     */
    public void setTexto(String texto) {
        if (tagAtributos.getHijos().size() > 0
                && tagAtributos.getHijos().get(
                        tagAtributos.getHijos().size() - 1) instanceof Texto) {
            log.warn("Se está añadiendo dos textos a un solo tag!!!");
        }

        tagAtributos.getHijos().add(new Texto(texto));
    }

    /**
     * Agrega CDATA dentro del tag actual.
     *
     * @param cdata Contenido del CDATA
     */
    public void setCDATA(String cdata) {
        if (tagAtributos.getHijos().size() > 0
                && tagAtributos.getHijos().get(
                        tagAtributos.getHijos().size() - 1) instanceof CDATA) {
            log.warn("Se está añadiendo dos CDATAs a un solo tag!!!");
        }

        tagAtributos.getHijos().add(new CDATA(cdata));
    }

    /**
     * Agrega un comentario dentro del tag actual.
     *
     * @param comentario Contenido del comentario
     */
    public void setComentario(String comentario) {
        if (tagAtributos.getHijos().size() > 0
                && tagAtributos.getHijos().get(
                        tagAtributos.getHijos().size() - 1) instanceof Comentario) {
            log.warn("Se está añadiendo dos comentarios a un solo tag!!!");
        }

        tagAtributos.getHijos().add(new Comentario(comentario));
    }

    @Override
    public void generateHtmlNg(ConstructorHtml html) {
        //To change body of generated methods, choose Tools | Templates.
        html.cabeza = null;
    }
}
