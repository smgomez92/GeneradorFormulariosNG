package com.fitbank.webpages.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.fitbank.propiedades.PropiedadLista;
import com.fitbank.webpages.WebPageUtils;
import com.fitbank.webpages.widgets.DeleteRecord;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que contiene los datos iniciales, de consulta y de mantenimiento de un
 * campo en un WebPage.
 * 
 * @author FitBank CI
 */
@Slf4j
public class FieldData implements Serializable {

    private static final long serialVersionUID = 1L;

    private final FormElement formElement;

    private PropiedadLista<String> initialValues = new PropiedadLista<String>(
            1, PropiedadLista.ILIMITADO, "");

    private PropiedadLista<String> currentValues = new PropiedadLista<String>(
            1, PropiedadLista.ILIMITADO, "");

    private PropiedadLista<String> queryValues = new PropiedadLista<String>(
            0, PropiedadLista.ILIMITADO, "");

    private PropiedadLista<Error> errors = new PropiedadLista<Error>(
            1, PropiedadLista.ILIMITADO, new Error());

    private PropiedadLista<String> extraClasses = new PropiedadLista<String>(
            1, PropiedadLista.ILIMITADO, "");

    private PropiedadLista<Boolean> disabled = new PropiedadLista<Boolean>(
            1, PropiedadLista.ILIMITADO, false);

    private PropiedadLista<Boolean> query = new PropiedadLista<Boolean>(
            1, PropiedadLista.ILIMITADO, false);

    public FieldData(FormElement formElement) {
        this.formElement = formElement;
    }

    public FieldData(DeleteRecord deleteRecord) {
        this.formElement = null;
    }

    /**
     * Obtiene los <code>value</code>s iniciales.
     * 
     * @return Una lista con los <code>value</code>s iniciales
     */
    public List<String> getValuesIniciales() {
        return initialValues.getList();
    }

    /**
     * Cambia los <code>value</code>s iniciales.
     *
     * @param valores
     *            Vector con los nuevos <code>value</code>s
     */
    public void setValuesIniciales(List<String> valores) {
        initialValues.setValor(valores);
    }

    /**
     * Cambia un <code>value</code> inicial.
     *
     * @param registro
     *            Numero de registro
     *
     * @return String con el valor
     */
    public String getValueInicial(int registro) {
        return initialValues.getValor(registro);
    }

    /**
     * Obtiene los <code>value</code>s actuales.
     * 
     * @return Una lista con los <code>value</code>s actuales
     */
    public List<String> getValues() {
        return currentValues.getList();
    }

    /**
     * Cambia los <code>value</code>s actuales.
     *
     * @param valores
     *            Vector con los nuevos <code>value</code>s
     */
    public void setValues(List<String> valores) {
        currentValues.setValor(valores);
    }

    /**
     * Cambia un <code>value</code> actual.
     *
     * @param registro
     *            Numero de registro
     *
     * @return String con el valor
     */
    public String getValue(int registro) {
        if (formElement != null) {
            log.info("\nWidget: " + formElement.getName() + " datasource:" + formElement.getDataSource().toString());
        }
        return currentValues.getValor(registro);
    }

    /**
     * Cambia el <code>value</code> actual.
     *
     * @param registro
     *            Numero de registro
     * @param valor
     *            <code>value</code>
     */
    public void setValue(int registro, String valor) {
        currentValues.setValor(registro, valor);
    }

    /**
     * Obtiene los <code>value</code>s consultados.
     * 
     * @return Una lista con los <code>value</code>s consultados
     */
    public List<String> getValuesConsulta() {
        return queryValues.getList();
    }

    /**
     * Cambia los <code>value</code>s consulta.
     *
     * @param valores
     *            Vector con los nuevos <code>value</code>s
     */
    public void setValuesConsulta(List<String> valores) {
        queryValues.setValor(valores);
    }

    /**
     * Cambia un <code>value</code> actuales.
     * 
     * @param registro
     *            Numero de registro
     * 
     * @return String con el valor
     */
    public String getValueConsulta(int registro) {
        return queryValues.getValor(registro);
    }

    /**
     * Cambia el <code>value</code> consulta.
     * 
     * @param registro
     *            Numero de registro
     * @param valor
     *            <code>value</code>
     */
    public void setValueConsulta(int registro, String valor) {
        queryValues.setValor(registro, valor);
        currentValues.setValor(registro, valor);
    }

    /**
     * Obtiene los estados de errores actuales.
     *
     * @return Una lista con los estados de errores actuales
     */
    public List<Error> getErrors() {
        return errors.getList();
    }

    /**
     * cambia los estados de errores actuales.
     */
    public void setErrors(List<Error> valores) {
        errors.setValor(valores);
    }

    /**
     * Cambia el estado de error actual.
     *
     * @param registro
     *            Numero de registro
     * @param mensaje
     *            Mensaje
     * @param id
     *            Id de validación
     */
    public void setError(int registro, String mensaje, String id) {
        errors.setValor(registro, new Error(mensaje, id));
    }

    /**
     * Obtiene las clases extras actuales.
     *
     * @return Una lista con los estados de errores actuales
     */
    public List<String> getExtraClasses() {
        return extraClasses.getList();
    }

    /**
     * Cambia las clases extra actuales.
     */
    public void setExtraClasses(List<String> extraClasses) {
        this.extraClasses.setValor(extraClasses);
    }

    /**
     * Cambia las clases extra de este elemento.
     *
     * @param registro
     *            Numero de registro
     * @param e
     *            <code>error</code>
     */
    public void addExtraClass(int registro, String extraClass) {
        String valor = extraClasses.getValor(registro) + " " + extraClass;
        extraClasses.setValor(registro, valor.trim());
    }

    /**
     * Obtiene los estados de errores actuales.
     *
     * @return Una lista con los estados de errores actuales
     */
    public List<Boolean> getDisabled() {
        return disabled.getList();
    }

    /**
     * Cambia el estado de error actual.
     *
     * @param registro
     *            Numero de registro
     * @param error
     *            <code>error</code>
     */
    public void setDisabled(int registro, boolean disabled) {
        this.disabled.setValor(registro, disabled);
    }

    /**
     * Obtiene los estados de consultados actuales.
     *
     * @return Una lista con los estados de errores actuales
     */
    public List<Boolean> getQuery() {
        return query.getList();
    }

    /**
     * Cambia el estado de consultado actual.
     *
     * @param registro
     *            Numero de registro
     * @param error
     *            <code>error</code>
     */
    public void qetQuery(int registro, boolean query) {
        this.query.setValor(registro, query);
    }

    /**
     * Setea los <code>value</code>s actuales y de consulta en su valor inicial.
     */
    public void resetAll() {
        resetValues();
        resetStates();
    }

    /**
     * Setea los <code>value</code>s actuales y de consulta en su valor inicial.
     *
     * @param record Registro
     */
    public void resetAll(int record) {
        resetValues(record);
        resetStates(record);
    }

    /**
     * Setea los <code>value</code>s actuales y de consulta en su valor inicial.
     */
    public void resetValues() {
        currentValues.clonar(getValuesIniciales());
        queryValues.setValor(new LinkedList<String>());
    }

    /**
     * Setea los <code>value</code>s actuales y de consulta en su valor inicial.
     */
    public void resetValues(int record) {
        if (currentValues.getList().size() > record) {
            currentValues.setValor(record, initialValues.getValor(record));
        }

        if (queryValues.getList().size() > record) {
            queryValues.setValor(record, "");
        }
    }

    /**
     * Setea los estadoss actuales de error y deshabilitado.
     */
    public void resetStates() {
        disabled.resetear();
        resetErrors();
    }

    /**
     * Setea los estadoss actuales de error y deshabilitado.
     */
    public void resetStates(int record) {
        if (disabled.getList().size() > record) {
            disabled.setValor(record, disabled.getRelleno());
        }
        resetErrors(record);
    }

    /**
     * Setea los estadoss actuales de error y deshabilitado.
     */
    public void resetErrors() {
        errors.resetear();
    }

    /**
     * Setea los estadoss actuales de error y deshabilitado.
     */
    public void resetExtraClasses() {
        extraClasses.resetear();
    }

    /**
     * Setea los estadoss actuales de error y deshabilitado.
     */
    public void resetErrors(int record) {
        if (errors.getList().size() > record) {
            errors.setValor(record, errors.getRelleno());
        }
    }

    /**
     * Actualiza los contenedores de los valores iniciales, actuales y de
     * consulta.
     * 
     * @param registrosConsulta
     *            Numero de registros de consulta que se necesita que se
     *            contenga
     * @param registrosMantenimiento
     *            Numero de registros de mantenimiento que se necesita que se
     *            contenga
     * @param relleno
     *            Valor que se usará de relleno cuando se necesite expandir el
     *            contenedor
     */
    public void actualizar(int registrosConsulta, int registrosMantenimiento,
            String relleno) {
        initialValues.setMin(registrosConsulta);
        initialValues.setMax(registrosConsulta);
        initialValues.setRelleno(relleno);
        initialValues.resetear();

        currentValues.setMin(registrosConsulta);
        currentValues.setMax(registrosMantenimiento);
        currentValues.setRelleno(relleno);
        currentValues.resetear();

        queryValues.setMin(0);
        queryValues.setMax(registrosConsulta);
        queryValues.setRelleno(relleno);
        queryValues.resetear();

        errors.setMin(registrosConsulta);
        errors.setMax(registrosMantenimiento);
        errors.resetear();

        disabled.setMin(registrosConsulta);
        disabled.setMax(registrosMantenimiento);
        disabled.resetear();

        extraClasses.setMin(registrosConsulta);
        extraClasses.setMax(registrosMantenimiento);
        extraClasses.resetear();

        query.setMin(registrosConsulta);
        query.setMax(registrosMantenimiento);
        query.resetear();
    }

    /**
     * Indica si existe cambios entre el valor consultado y el valor actual.
     * 
     * @param registro Registro a ser revisado.
     * 
     * @return true si existen cambios
     */
    public boolean tieneCambios(int registro) {
        if (formElement != null) {
            String value = WebPageUtils.normalize(formElement, getValue(registro));

            String valueConsulta = WebPageUtils.normalize(formElement,
                    getValueConsulta(registro));

            return !value.equals(valueConsulta);

        } else if (getValuesConsulta().size() <= registro) {
            return true;

        } else {
            return !getValue(registro).equals(getValueConsulta(registro));
        }
    }

    public static class Error implements Serializable {

        private String mensaje;

        private String id;

        public Error() {
        }

        public Error(String mensaje, String id) {
            this.mensaje = mensaje;
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }

    }

}
