package com.fitbank.ifg;

import javax.swing.JOptionPane;

import com.fitbank.enums.TipoFila;
import com.fitbank.ifg.swing.tables.TablaElementos;
import com.fitbank.util.Debug;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.widgets.Input;

public class iFGExtra {

    private final iFG ifg;

    public iFGExtra(iFG ifg) {
        this.ifg = ifg;
    }

    public boolean agregarValor(boolean agregar) {
        if (ifg.isAbierto()) {
            Debug.info(agregar ? Mensajes.format("iFG.AgregandoValorColumna") //$NON-NLS-1$
                    : Mensajes.format("iFG.FijandoValorColumna")); //$NON-NLS-1$
            TablaElementos elementos = ifg.getPanelFilasElementos().
                    getTablaElementos();
            int columna = elementos.getSelectedColumn();
            if (ifg.getPanelFilasElementos().getContainerActual().getType().
                    equals(TipoFila.TABLA)
                    && columna > 4 && columna < 7) {
                if (agregar) {
                    Debug.error(Mensajes.format(
                            "iFG.ColumnasXYNoHabilidadasAgregarValor")); //$NON-NLS-1$
                } else {
                    Debug.error(Mensajes.format(
                            "iFG.ColumnasXYNoHabilidadasFijarValor")); //$NON-NLS-1$
                }
                return false;
            }
            if (columna > 4 && columna < 9) {
                String valor = JOptionPane.showInputDialog(agregar ? Mensajes.
                        format("iFG.PreguntaValorAgregarColumna", elementos.
                        getColumnName(columna)) //$NON-NLS-1$
                        : Mensajes.format("iFG.PreguntaValorFijarColumna",
                        elementos.getColumnName(columna))); //$NON-NLS-1$
                if (valor != null && !valor.equals("")) { //$NON-NLS-1$
                    int cuantos = 0;
                    try {
                        cuantos = Integer.parseInt(valor, 10);
                        for (int i = 0; i < elementos.getRowCount(); i++) {
                            Widget actual = ifg.getPanelFilasElementos().
                                    getContainerActual().get(i);
                            if (actual instanceof Input && !actual.getVisible()) {
                                actual.setX(0);
                                actual.setY(0);
                                actual.setW(0);
                                actual.setH(0);
                                actual.setCSSClass(""); //$NON-NLS-1$
                            } else {
                                switch (columna) {
                                    default:
                                        break;
                                    case 5:
                                        actual.setX((agregar ? actual.getX() : 0)
                                                + cuantos);
                                        break;
                                    case 6:
                                        actual.setY((agregar ? actual.getY() : 0)
                                                + cuantos);
                                        break;
                                    case 7:
                                        actual.setW(Math.abs((agregar ? actual.
                                                getW() : 0)
                                                + cuantos));
                                        break;
                                    case 8:
                                        actual.setH(Math.abs((agregar ? actual.
                                                getH() : 0)
                                                + cuantos));
                                        break;
                                }
                            }
                        }

                        if (agregar) {
                            Debug.info(Mensajes.format(
                                    "iFG.ValorAgregadoColumna", //$NON-NLS-1$
                                    valor, elementos.getColumnName(columna)));
                        } else {
                            Debug.info(Mensajes.format("iFG.ValorFijadoColumna", //$NON-NLS-1$
                                    valor, elementos.getColumnName(columna)));
                        }
                        ifg.getPanelFilasElementos().saveUndoState();
                        elementos.refresh();
                    } catch (Exception e) {
                        Debug.error(e);
                        if (agregar) {
                            Debug.error(Mensajes.format(
                                    "iFG.ErrorValorInvalidoAgregarColumna", //$NON-NLS-1$
                                    valor));
                        } else {
                            Debug.error(Mensajes.format(
                                    "iFG.ErrorValorInvalidoFijarColumna", valor)); //$NON-NLS-1$
                        }

                        return false;
                    }
                } else {
                    Debug.info(
                            agregar ? Mensajes.format(
                            "iFG.AgregandoValorColumnaCancelado") //$NON-NLS-1$
                            : Mensajes.format("iFG.FijandoValorColumnaCancelado")); //$NON-NLS-1$
                }
            } else {
                Debug.info(agregar ? Mensajes.format(
                        "iFG.AgregarValorColumnaNoSeleccionada") //$NON-NLS-1$
                        : Mensajes.format("iFG.FijarValorColumnaNoSeleccionada"));
                //$NON-NLS-1$

            }
        } else {
            return false;
        }
        return true;
    }

    public boolean ajustarX() {
        if (ifg.isAbierto()) {
            if (ifg.getPanelFilasElementos().getContainerActual().getType().
                    equals(TipoFila.TABLA)) {
                Debug.error(Mensajes.format("iFG.ColumnaNoHabilitadaAjustarValor")); //$NON-NLS-1$
                return false;
            }
            Debug.info(Mensajes.format("iFG.AjustandoValorColumna")); //$NON-NLS-1$
            TablaElementos elementos = ifg.getPanelFilasElementos().
                    getTablaElementos();
            int valor = 0;
            int valorPos = 10000;
            int valorNeg = 0;
            for (int i = 0; i < elementos.getRowCount(); i++) {
                Widget actual = ifg.getPanelFilasElementos().getContainerActual().
                        get(i);
                if (actual instanceof Input && !actual.getVisible()) {
                    actual.setX(0);
                    actual.setY(0);
                    actual.setW(0);
                    actual.setH(0);
                    actual.setCSSClass(""); //$NON-NLS-1$
                } else {
                    valorNeg = actual.getX() < 0 && actual.getX() < valorNeg
                            ? actual.getX()
                            : valorNeg;
                    valorPos = valorPos > 0 && actual.getX() >= 0
                            && actual.getX() < valorPos ? actual.getX()
                            : valorPos;
                }
            }
            valor = valorNeg < 0 ? valorNeg
                    : valorPos > 0 && valorPos != 10000 ? valorPos : 0;
            for (int i = 0; i < elementos.getRowCount(); i++) {
                Widget actual = ifg.getPanelFilasElementos().getContainerActual().
                        get(i);
                if (!(actual instanceof Input && !actual.getVisible())) {
                    actual.setX(actual.getX() - valor);
                }
            }
            try {
                ifg.getPanelFilasElementos().getContainerActual().setX(
                        ifg.getPanelFilasElementos().getContainerActual().getX()
                        + valor);
                Debug.info(Mensajes.format("iFG.ValorAjustadoColumna", valor)); //$NON-NLS-1$
                ifg.getPanelFilasElementos().saveUndoState();
                elementos.refresh();
            } catch (Exception e) {
                Debug.error(Mensajes.format(
                        "iFG.ErrorAgregarValorColumna", valor), e); //$NON-NLS-1$
                return false;
            }
        }

        return true;
    }

    public boolean limpiarEstilo() {
        if (ifg.isAbierto()) {
            int cuentaL = 0;
            for (Container container : ifg.getWebPageActual()) {
                for (Widget widget : container) {
                    if (widget instanceof Input && !widget.getVisible()) {
                        if (widget.getX() != 0 || widget.getY() != 0
                                || widget.getW() != 0 || widget.getH() != 0
                                || !widget.getCSSClass().equals("")) { //$NON-NLS-1$
                            cuentaL++;
                        }
                        widget.setX(0);
                        widget.setY(0);
                        widget.setW(0);
                        widget.setH(0);
                        widget.setCSSClass(""); //$NON-NLS-1$
                    }
                }
            }
            if (cuentaL == 1) {
                Debug.info(Mensajes.format("iFG.LimpioEstilosValoresUnElemento")); //$NON-NLS-1$
                ifg.getPanelFilasElementos().saveUndoState();
            } else if (cuentaL > 1) {
                Debug.info(Mensajes.format(
                        "iFG.LimpioEstilosValoresVariosElementos", cuentaL)); //$NON-NLS-1$
                ifg.getPanelFilasElementos().saveUndoState();
            } else {
                Debug.info(Mensajes.format(
                        "iFG.ElementosNoEncontradosParaLimpiarEstilosValores")); //$NON-NLS-1$
            }
        }
        return true;
    }

}
