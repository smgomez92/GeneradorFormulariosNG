package com.fitbank.ifg.tablas;

import javax.swing.table.AbstractTableModel;

public class TablaIdentificadores extends AbstractTableModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    final String[] columnNames = { "Identificador", "Texto", "Container",
            "Ele." };
    final Object[] longValues = { "", "", "", "" };

    public Object[][] data;

    public TablaIdentificadores() {

    }

    /**
     * @param indexaciones
     *            Tiene la lista "nombre de campo, tipo, fila, elemento" de
     *            todos los campos habilitados para TabIndex.
     * @param marcados
     *            Tiene los nombres de los campos seleccionados, de la lista,
     *            para ejecutar tabIndex.
     */
    public TablaIdentificadores(String[] listaIdentificadores) {
        Object[][] contenidoVal = new Object[listaIdentificadores.length][8];
        for (int j = 0; j < listaIdentificadores.length; j++) {
            String[] aux = listaIdentificadores[j].split(",-,");
            if (aux.length == 4) {
                contenidoVal[j][0] = new String(aux[0]);
                contenidoVal[j][1] = new String(aux[1]);
                for (int b = 2; b < 4; b++) {
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
        return col < 2;
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
