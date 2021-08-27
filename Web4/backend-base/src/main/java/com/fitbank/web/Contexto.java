package com.fitbank.web;

import java.io.Serializable;

import com.fitbank.web.data.Paginacion;
import com.fitbank.web.db.TransporteDB;
import com.fitbank.web.db.TransporteDBFactory;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.util.ArbolDependencias;
import com.fitbank.webpages.util.NodoDependencia;

public class Contexto implements Serializable {

    private WebPage webPage = null;

    private Paginacion paginacion = null;

    private TransporteDB transporteDBBase = TransporteDBFactory.newInstance();

    private boolean hayDatos = false;

    private String id = null;

    private ArbolDependencias arbolDependencias = null;

    public Contexto() {
        this(EntornoWeb.getIdContexto());
    }

    public Contexto(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setWebPage(WebPage webPage) {
        this.webPage = webPage;
        setArbolDependencias(new ArbolDependencias(webPage.getReferences()));
    }

    public WebPage getWebPage() {
        return webPage;
    }

    public void setPaginacion(Paginacion paginacion) {
        this.paginacion = paginacion;
    }

    public Paginacion getPaginacion() {
        return paginacion;
    }

    public TransporteDB getTransporteDBBase() {
        return transporteDBBase;
    }

    public void setTransporteDBBase(TransporteDB transporteDBBase) {
        this.transporteDBBase = transporteDBBase;
    }

    public boolean getHayDatos() {
        return hayDatos;
    }

    public void setHayDatos(boolean hayDatos) {
        this.hayDatos = hayDatos;
    }

    public void setArbolDependencias(ArbolDependencias arbolDependencias) {
        this.arbolDependencias = arbolDependencias;
    }

    public ArbolDependencias getArbolDependencias() {
        return arbolDependencias;
    }

    public Reference getReference(String alias) {
        NodoDependencia nodo = getArbolDependencias().getNodos().get(alias);
        return nodo == null ? null : nodo.getReference();
    }

}
