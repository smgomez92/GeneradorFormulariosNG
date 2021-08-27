package com.fitbank.web.json;

import com.fitbank.web.data.RespuestaWeb;
import java.util.LinkedList;
import java.util.List;
import net.sf.json.JSONObject;

/**
 * Trasporta los datos como json al navegador.
 *
 * @author FitBank
 * @version 2.0
 */
public class TransporteListaValores extends TransporteWeb {

    private List<ItemListaValores> registros = new LinkedList<ItemListaValores>();

    private JSONObject control = new JSONObject();

    private boolean paginacion = false;

    private boolean searchComments = false;

    public TransporteListaValores(RespuestaWeb respuesta, boolean searchComments) {
        super(respuesta);
        this.searchComments = searchComments;
    }

    public TransporteListaValores(RespuestaWeb respuesta) {
        super(respuesta);
    }
    
    public ItemListaValores get(int index) {
        for (int a = registros.size(); a <= index; a++) {
            registros.add(new ItemListaValores());
        }

        return registros.get(index);
    }

    public List<ItemListaValores> getRegistros() {
        return registros;
    }

    public void setRegistros(List<ItemListaValores> registros) {
        this.registros = registros;
    }

    public boolean getPaginacion() {
        return paginacion;
    }

    public void setPaginacion(boolean paginacion) {
        this.paginacion = paginacion;
    }

    public JSONObject getControl() {
        return control;
    }

    public void setControl(JSONObject control) {
        this.control = control;
    }

    public boolean isSearchComments() {
        return searchComments;
    }

    public void setSearchComments(boolean searchComments) {
        this.searchComments = searchComments;
    }

    @Override
    public String toJSON() {
        JSONObject res = new JSONObject();

        res.element("registros", registros);
        res.element("paginacion", paginacion);
        res.element("control", control);
        res.element("notifica", searchComments);

        setResponse(res);

        return res.toString();
    }

}
