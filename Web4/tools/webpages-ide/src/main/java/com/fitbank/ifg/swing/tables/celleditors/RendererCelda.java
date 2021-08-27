package com.fitbank.ifg.swing.tables.celleditors;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadCombo;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadLista;
import com.fitbank.propiedades.PropiedadMapa;
import com.fitbank.propiedades.PropiedadObjeto;
import java.awt.Color;

/**
 * Se usa esta clase para hacer el render de las celdas del editor properties.
 */
public class RendererCelda implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Propiedad propiedad = (Propiedad) value;

        JLabel label = new JLabel(propiedad.getValorString());
        label.setForeground(Color.BLACK);

        JComponent ret = label;
        if (value instanceof PropiedadBooleana) {
            ret = new JCheckBox();
            ((JCheckBox) ret).setSelected((Boolean) propiedad.getValor());

        } else if (value instanceof PropiedadCombo) {
            ret = new JLabel(String.valueOf(((PropiedadCombo) propiedad).
                    getEtiquetaSeleccionada()));

        } else if (value instanceof PropiedadLista
                || value instanceof PropiedadMapa
                || value instanceof PropiedadJavascript
                || value instanceof PropiedadObjeto) {
            ret = new JPanel(new BorderLayout());
            ret.add(label, BorderLayout.CENTER);
            ret.add(EditorCeldaPropiedad.getButton(), BorderLayout.EAST);
        }

        ret.setOpaque(true);
        ret.setForeground(Color.BLACK);

        return ret;
    }

}
