package com.fitbank.ifg.tablas;

import javax.swing.table.AbstractTableModel;

import com.fitbank.schemautils.Schema;
import com.fitbank.schemautils.Table;

public class TablaGeneradorAutomatico extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private final String[] columnNames = { "No.", "Tipo", "Texto",
            "ListOfValues" };

    private final Object[] longValues = { new Integer(0), "", "", Boolean.FALSE };

    private Object[][] data;

    public TablaGeneradorAutomatico() {
        data = new Object[0][4];
    }

    public void init(Schema cc, String tabla, Object[] seleccionados) {
        Object[][] contenido = new Object[seleccionados.length][4];
        Table t = cc.getTables().get(tabla);
        for (int a = 0; a < seleccionados.length; a++) {
            contenido[a][0] = new Integer(a);
            contenido[a][1] = t.getFields().get(
                    String.valueOf(seleccionados[a])).getType();
            contenido[a][2] = t.getFields().get(
                    String.valueOf(seleccionados[a])).getName();
            contenido[a][3] = Boolean.FALSE;
        }
        data = contenido;
        fireTableDataChanged();
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
    public Class<?> getColumnClass(int c) {
        return longValues[c].getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col > 1;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}