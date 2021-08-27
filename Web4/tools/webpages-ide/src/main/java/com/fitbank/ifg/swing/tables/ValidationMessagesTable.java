package com.fitbank.ifg.swing.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import com.fitbank.ifg.iFG;
import com.fitbank.ifg.swing.dialogs.EditorPropiedades;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.util.ValidationMessage;

public class ValidationMessagesTable extends BaseJTable {

    private static final long serialVersionUID = 1L;

    public static final int COLUMNA_SELECCIONADO = 0;

    public static final int COLUMNA_SEVERIDAD = 1;

    public static final int COLUMNA_MENSAJE = 2;

    public static final int COLUMNA_WEBELEMENT = 3;

    public static final int COLUMNA_OBJETO = 4;

    public static final int COLUMNA_ARREGLAR = 5;

    public ValidationMessagesTable(final Window parent,
            final List<ValidationMessage> resultados) {
        setModel(new ValidationMessagesTableModel(resultados));

        TableColumnModel model = getColumnModel();

        model.getColumn(COLUMNA_SELECCIONADO).setPreferredWidth(25);
        model.getColumn(COLUMNA_SEVERIDAD).setPreferredWidth(60);
        model.getColumn(COLUMNA_MENSAJE).setPreferredWidth(300);
        model.getColumn(COLUMNA_WEBELEMENT).setPreferredWidth(225);
        model.getColumn(COLUMNA_OBJETO).setPreferredWidth(225);
        model.getColumn(COLUMNA_ARREGLAR).setPreferredWidth(225);

        addMouseListener(new ValidationMouseListener(parent, resultados));

        addHighlighter(new ColorHighlighter(new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                Object value = adapter.getValue();
                return value.equals(ValidationMessage.Severity.ERROR.name());
            }

        }, new Color(1.0f, 0.0f, 0.0f, 0.75f), Color.BLACK,
                new Color(1.0f, 0.0f, 0.0f, 1f), Color.WHITE));

        addHighlighter(new ColorHighlighter(new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                Object value = adapter.getValue();
                return value.equals(ValidationMessage.Severity.WARN.name());
            }

        }, new Color(1f, 1.0f, 0.0f, 0.75f), Color.BLACK,
                new Color(0.75f, 1.0f, 0.0f, 1f), Color.WHITE));
    }

    public Collection<ValidationMessage> getSelected() {
        Collection<ValidationMessage> resultados =
                new LinkedList<ValidationMessage>();

        for (int a = 0; a < getModel().getRowCount(); a++) {
            ValidationMessagesTableModel model =
                    (ValidationMessagesTableModel) getModel();
            if (model.seleccionados[a]) {
                resultados.add(model.resultados.get(a));
            }
        }

        return resultados;
    }

    public class ValidationMessagesTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        final String[] columnNames = { "Sel.", "", "Mensaje (doble click)", "WebElement",
            "Objeto", "Arreglar" };

        final Class<?>[] longValues = { Boolean.class, String.class,
            String.class, String.class, String.class, String.class };

        private final List<ValidationMessage> resultados;

        private boolean[] seleccionados;

        public ValidationMessagesTableModel(List<ValidationMessage> resultados) {
            this.resultados = resultados;
            this.seleccionados = new boolean[resultados.size()];
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return resultados.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return longValues[col];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            ValidationMessage message = resultados.get(row);

            return col == COLUMNA_SELECCIONADO && message.isFixable();
        }

        public Object getValueAt(int row, int col) {
            ValidationMessage message = resultados.get(row);

            switch (col) {
                case COLUMNA_SELECCIONADO:
                    return seleccionados[row];

                case COLUMNA_SEVERIDAD:
                    return message.getSeverity().name();

                case COLUMNA_MENSAJE:
                    return message.toString();

                case COLUMNA_WEBELEMENT:
                    if (message.getWebElement() instanceof WebPage) {
                        return "Error a nivel del WebPage";
                    } else if (message.getWebElement() instanceof Container) {
                        return "Seleccionar Container con error ("
                                + (message.getWebElement().getPosicion() + 1)
                                + ")";
                    } else if (message.getWebElement() instanceof Widget) {
                        return "Seleccionar Widget con error ("
                                + (message.getWebElement().getParent().
                                getPosicion() + 1) + ", "
                                + (message.getWebElement().getPosicion() + 1)
                                + ")";
                    }
                    break;

                case COLUMNA_OBJETO:
                    return message.getValidatedObject() != null
                            ? "Editar objeto con error"
                            : "";

                case COLUMNA_ARREGLAR:
                    return message.isFixable() ? "Permite arreglo automático"
                            : "Requiere arreglo manual";

            }

            throw new RuntimeException("Columna inválida: " + col);
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            switch (col) {
                case COLUMNA_SELECCIONADO:
                    seleccionados[row] = (Boolean) value;
            }
        }

    }

    private class ValidationMouseListener extends MouseAdapter {

        private final List<ValidationMessage> resultados;

        private final Window parent;

        public ValidationMouseListener(Window parent,
                List<ValidationMessage> resultados) {
            this.resultados = resultados;
            this.parent = parent;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() != 2) {
                return;
            }
            ValidationMessage message = resultados.get(getRowSorter().
                    convertRowIndexToModel(getSelectedRow()));
            int col = getColumnExt(columnAtPoint(e.getPoint())).getModelIndex();

            switch (col) {
                case COLUMNA_MENSAJE:
                    if (StringUtils.isNotBlank(message.getDescription())) {
                        JTextArea textArea = new JTextArea(message.
                                getDescription());
                        textArea.setLineWrap(true);
                        textArea.setWrapStyleWord(true);
                        textArea.setMargin(new Insets(5, 5, 5, 5));
                        textArea.setEditable(false);
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        scrollPane.setPreferredSize(new Dimension(640, 480));
                        JOptionPane.showMessageDialog(parent, scrollPane);
                    }
                    break;

                case COLUMNA_WEBELEMENT:
                    if (message.getWebElement() instanceof Container) {
                        iFG.getSingleton().getPanelFilasElementos().
                                seleccionarFila(message.getWebElement().
                                getPosicion());
                    } else if (message.getWebElement() instanceof Widget) {
                        iFG.getSingleton().getPanelFilasElementos().
                                seleccionarFila(message.getWebElement().
                                getParent().getPosicion());

                        iFG.getSingleton().getPanelFilasElementos().
                                seleccionarElemento(message.getWebElement().
                                getPosicion());
                    }
                    break;

                case COLUMNA_OBJETO:
                    new EditorPropiedades(parent,
                            message.getValidatedObject()).setVisible(true);
                    break;

            }
        }

    }

}
