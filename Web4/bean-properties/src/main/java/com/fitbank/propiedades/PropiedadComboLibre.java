package com.fitbank.propiedades;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Propiedad que se usa como un combo pero que permite ingreso libre de valores
 *
 * @author FitBank CI
 */
public abstract class PropiedadComboLibre extends PropiedadCombo<String> {

    private static final long serialVersionUID = 1L;

    private static Map<String, String> getMap(List<String> valores) {
        Map<String, String> map = new LinkedHashMap<String, String>();

        for (String s : valores) {
            map.put(s, s);
        }

        return map;
    }

    public PropiedadComboLibre(String valorPorDefecto, List<String> valores) {
        super(valorPorDefecto, getMap(valores));
    }

    @Override
    public String valorValido(Object o) {
        return VALOR_VALIDO;
    }

    @Override
    public Map<String, String> getEtiquetas() {
        return valores;
    }

    @Override
    public String getEtiquetaSeleccionada() {
        return getValorString();
    }

    @Override
    public String getEtiqueta(int cual) {
        return new ArrayList<String>(valores.keySet()).get(cual);
    }

}
