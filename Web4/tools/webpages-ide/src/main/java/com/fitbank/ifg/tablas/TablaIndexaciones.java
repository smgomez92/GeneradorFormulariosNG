package com.fitbank.ifg.tablas;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class TablaIndexaciones extends AbstractTableModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    final String[] columnNames = { "", "Field", "Tipo", "Container", "Elem.",
            "Tab" };
    final Object[] longValues = { Boolean.FALSE, "", "", "", "", "" };

    public Object[][] data;

    public TablaIndexaciones() {

    }

    /**
     * @param indexaciones
     *            Tiene la lista "nombre de campo, tipo, fila, elemento" de
     *            todos los campos habilitados para TabIndex.
     * @param marcados
     *            Tiene los nombres de los campos seleccionados, de la lista,
     *            para ejecutar tabIndex.
     */
    public TablaIndexaciones(Vector<String> indexaciones, String[] marcados) {
        Object[][] contenidoVal = new Object[indexaciones.size()][6];
        if (marcados.length > 0) {
            int numMarcados = 0;
            for (int j = 0; j < marcados.length; j++) {
                for (int a = 0; a < indexaciones.size(); a++) {
                    String[] aux = indexaciones.get(a).toString().split(";");
                    if (aux.length == 5) {
                        if (marcados[j].equals(aux[0])) {
                            contenidoVal[j][0] = Boolean.TRUE;
                            for (int b = 0; b < 5; b++) {
                                contenidoVal[j][b + 1] = new String("  "
                                        + aux[b]);
                            }
                            numMarcados++;
                        }
                    }
                }
            }
            boolean marcado = false;
            for (int a = 0; a < indexaciones.size(); a++) {
                String[] aux = indexaciones.get(a).toString().split(";");
                if (aux.length == 5) {
                    for (String marcado2 : marcados) {
                        marcado = false;
                        if (marcado2.equals(aux[0])) {
                            marcado = true;
                            break;
                        }
                    }
                    if (!marcado) {
                        contenidoVal[numMarcados][0] = Boolean.FALSE;
                        for (int b = 0; b < 5; b++) {
                            contenidoVal[numMarcados][b + 1] = new String("  "
                                    + aux[b]);
                        }
                        numMarcados++;
                    }
                }
            }
        } else {
            for (int a = 0; a < indexaciones.size(); a++) {
                String[] aux = indexaciones.get(a).toString().split(";");
                if (aux.length == 5) {
                    contenidoVal[a][0] = Boolean.FALSE;
                    for (int b = 0; b < 5; b++) {
                        contenidoVal[a][b + 1] = new String("  " + aux[b]);
                    }
                }
            }
        }
        data = contenidoVal;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 0;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        return longValues[c].getClass();
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }

}