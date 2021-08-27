package com.fitbank.ifg.swing.tables.celleditors;

import com.fitbank.ifg.swing.Boton;
import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renderiza la columna de pegar en las propiedades
 *
 * @author FitBank CI
 */
public class PasteTableCellRenderer implements TableCellRenderer {

    private static Image icon = Boton.load("gtk-paste",
            "actions/edit-paste.png", false).getImage().getScaledInstance(10,
            10, Image.SCALE_SMOOTH);

    public Component getTableCellRendererComponent(JTable table,
            final Object value, boolean isSelected, boolean hasFocus,
            final int row, int column) {
        if (value == null) {
            return new JPanel();
        }

        JLabel label = new JLabel(new ImageIcon(icon));

        label.setToolTipText("Pegar: " + value);

        label.setOpaque(true);

        return label;
    }

}