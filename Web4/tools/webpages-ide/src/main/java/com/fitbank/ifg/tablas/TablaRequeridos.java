package com.fitbank.ifg.tablas;

import javax.swing.table.AbstractTableModel;

public class TablaRequeridos extends AbstractTableModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    final String[] columnNames = { "Req", "Field", "Tipo", "Container", "Ele.",
            "Crit." };
    final Object[] longValues = { Boolean.FALSE, "", "", "", "", "" };

    public Object[][] data;

    public TablaRequeridos() {

    }

    /**
     * @param indexaciones
     *            Tiene la lista "nombre de campo, tipo, fila, elemento" de
     *            todos los campos habilitados para TabIndex.
     * @param marcados
     *            Tiene los nombres de los campos seleccionados, de la lista,
     *            para ejecutar tabIndex.
     */
    public TablaRequeridos(String[] listaRequeridos) {
        Object[][] contenidoVal = new Object[listaRequeridos.length][8];
        for (int j = 0; j < listaRequeridos.length; j++) {
            String[] aux = listaRequeridos[j].split(",");
            if (aux.length == 6) {
                contenidoVal[j][0] = new Boolean(aux[0]);
                for (int b = 1; b < 6; b++) {
                    contenidoVal[j][b] = new String("  " + aux[b]);
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
        return col < 1;
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