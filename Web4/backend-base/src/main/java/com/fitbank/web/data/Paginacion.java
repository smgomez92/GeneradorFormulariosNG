package com.fitbank.web.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fitbank.web.EntornoWeb;
import com.fitbank.web.MensajesWeb;
import com.fitbank.web.exceptions.MensajeWeb;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.util.ArbolDependencias;
import com.fitbank.webpages.util.NodoDependencia;

public class Paginacion implements Serializable {

    private final Map<String, Integer> numeroDePaginasPorAlias = new HashMap<String, Integer>();

    private final Map<String, Integer> registroActivoPorAlias = new HashMap<String, Integer>();

    public Paginacion() {
        if (EntornoWeb.getContexto().getWebPage() != null) {
            for (Reference reference : EntornoWeb.getContexto().getWebPage()
                    .getReferences()) {
                numeroDePaginasPorAlias.put(reference.getAlias(), 1);
                registroActivoPorAlias.put(reference.getAlias(), 0);
            }
        }
    }

    public Integer getNumeroDePagina(String alias) {
        return numeroDePaginasPorAlias.get(alias);
    }

    public Integer getRegistroActivo(String alias) {
        return registroActivoPorAlias.get(alias);
    }

    public void setPaginacion(PedidoWeb pedido) {
        String paginacionString = pedido.getValorRequestHttp("_paginacion");
        String controlConFoco = pedido.getValorRequestHttp("_controlConFoco");
        String registroString = pedido.getValorRequestHttp("_registroActivo");

        if (paginacionString == null 
                || EntornoWeb.getContexto() == null
                || EntornoWeb.getContexto().getWebPage() == null) {
            return;
        }

        String alias = null;

        ArbolDependencias arbolDependencias = new ArbolDependencias(EntornoWeb.
                getContexto().getWebPage().getReferences());

        for (NodoDependencia nodoDependencia : arbolDependencias.getNodos().
                values()) {
            if (nodoDependencia.getDependientes().isEmpty()) {
                alias = nodoDependencia.getAlias();
                break;
            }
        }

        if (StringUtils.isNotBlank(controlConFoco)) {
            FormElement formElement = EntornoWeb.getContexto().getWebPage().
                    findFormElement(controlConFoco);
            if (formElement != null) {
                String alias2 = formElement.getDataSource().getAlias();

                if (numeroDePaginasPorAlias.containsKey(alias2)) {
                    alias = alias2;
                }
            }
        }

        Integer paginacion = paginacionString.matches("-?[01]") ? Integer.
                parseInt(paginacionString) : null;

        if (alias == null && paginacion != null && paginacion != 0) {
            throw new MensajeWeb(MensajesWeb.getValueString(Paginacion.class,
                    "SIN_CONSULTAR"));
        } else if (alias != null) {
            Integer registro = registroString.matches("\\d+") ? Integer.
                    parseInt(registroString) : 0;

            alias = arbolDependencias.getNodos().get(alias).getPrincipal().getAlias();

            cambiarPagina(pedido, alias, paginacion, registro, arbolDependencias);
        }
    }

    private void cambiarPagina(PedidoWeb pedido, String alias, Integer indicador,
            Integer registro, ArbolDependencias arbolDependencias) {
        if (!numeroDePaginasPorAlias.containsKey(alias)) {
            return;
        }

        int numeroDePagina = numeroDePaginasPorAlias.get(alias);

        if (indicador == null) {
            numeroDePagina = 1;
        } else if (indicador == -1) {
            if (!EntornoWeb.getContexto().getHayDatos()) {
                throw new MensajeWeb(MensajesWeb.getValueString(Paginacion.class,
                        "SIN_CONSULTAR"));
            } else if (numeroDePagina > 1) {
                numeroDePagina -= 1;
            } else {
                throw new MensajeWeb(MensajesWeb.getValueString(Paginacion.class,
                        "PRIMERA_PAGINA"));
            }
        } else if (indicador == 1) {
            if (!EntornoWeb.getContexto().getHayDatos()) {
                throw new MensajeWeb(MensajesWeb.getValueString(Paginacion.class,
                        "SIN_CONSULTAR"));
            } else if (pedido.getTransporteDB().hasMorePages(alias)) {
                numeroDePagina += 1;
            } else {
                throw new MensajeWeb(MensajesWeb.getValueString(Paginacion.class,
                        "ULTIMA_PAGINA"));
            }
        }
        numeroDePaginasPorAlias.put(alias, numeroDePagina);
        registroActivoPorAlias.put(alias, registro);

        for (NodoDependencia nodo : arbolDependencias.getNodosConectados(alias)) {
            numeroDePaginasPorAlias.put(nodo.getAlias(), numeroDePagina);
            registroActivoPorAlias.put(nodo.getAlias(), registro);
        }
    }
}
