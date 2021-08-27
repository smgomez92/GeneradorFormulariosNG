package com.fitbank.ifg.swing.tables;

import java.util.Comparator;

import javax.swing.JDialog;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;

import com.fitbank.ifg.Mensajes;
import com.fitbank.ifg.swing.dialogs.EditorPropiedades;
import com.fitbank.util.Debug;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.WebPageUtils;

public final class TablaFilas extends BaseJTable {

    private static final long serialVersionUID = 1L;

    public static final int COLUMNA_NUMERO = 0;

    public static final int COLUMNA_TAB = 1;

    public static final int COLUMNA_X = 2;

    public static final int COLUMNA_HEIGHT = 3;

    public static final int COLUMNA_SIZE = 4;

    private ModeloTablaFilas modelo = new ModeloTablaFilas();

    private final JDialog parent;

    public TablaFilas(JDialog parent, WebPage webPage) {
        this.parent = parent;

        setColumnControlVisible(true);

        setModel(modelo);

        TableColumnModel model = getColumnModel();

        model.getColumn(COLUMNA_NUMERO).setPreferredWidth(30);
        model.getColumn(COLUMNA_NUMERO).setMinWidth(30);
        model.getColumn(COLUMNA_TAB).setPreferredWidth(40);
        model.getColumn(COLUMNA_X).setPreferredWidth(40);
        model.getColumn(COLUMNA_HEIGHT).setPreferredWidth(40);
        model.getColumn(COLUMNA_SIZE).setPreferredWidth(30);

        model.getColumn(COLUMNA_NUMERO).setCellRenderer(new DefaultTableRenderer(new StringValue() {

            public String getString(Object value) {
                Container container = (Container) value;
                String tipo = container.getType().name().substring(0, 1).
                        toLowerCase();
                tipo = tipo.equals("n") ? "" : " " + tipo;
                String js = StringUtils.isNotBlank(container.getJavaScript())
                        ? " j" : "";
                String oculto = container.getVisible() ? "" : " o";
                String modificable = container.getReadOnly() ? " d" : "";

                return String.format("%s%s%s%s%s", StringUtils.leftPad(String.
                        valueOf(
                        container.getPosicion() + 1), 2, '0'), 
                        tipo, js, oculto, modificable);
            }

        }));
        getColumnExt(COLUMNA_NUMERO).setComparator(new Comparator<Container>() {

            public int compare(Container o1, Container o2) {
                return o1.getPosicion() - o2.getPosicion();
            }

        });

        setWebPage(webPage);

        setHorizontalScrollEnabled(false);
    }

    public WebPage getWebPage() {
        return ((ModeloTablaFilas) getModel()).getWebPage();
    }

    public void setWebPage(WebPage webPage) {
        ((ModeloTablaFilas) getModel()).setWebPage(webPage);
    }

    public void refresh() {
        ((ModeloTablaFilas) getModel()).fireTableDataChanged();
    }

    public void editarFila() {
        Debug.info(Mensajes.format("iFG.EditandoFila")); //$NON-NLS-1$

        Container container = getWebPage().get(getSelectedRow());
        new EditorPropiedades(parent, container).setVisible(true);
        refresh();

        Debug.info(Mensajes.format("iFG.FilaEditada", //$NON-NLS-1$
                container.getPosicion() + 1));
        setRowSelectionInterval(container.getPosicion(), container.getPosicion());
    }

    public class ModeloTablaFilas extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        final String[] nombresColumnas = { "No.", "Tab", "X", "H", "#" };

        final Class<?>[] classes = { String.class, String.class, Integer.class,
            Integer.class, Integer.class };

        public WebPage webPage = null;

        public WebPage getWebPage() {
            return webPage;
        }

        public void setWebPage(WebPage webPage) {
            this.webPage = webPage;
            fireTableDataChanged();
        }

        public int getColumnCount() {
            return nombresColumnas.length;
        }

        public int getRowCount() {
            return webPage != null ? webPage.size() : 0;
        }

        @Override
        public String getColumnName(int col) {
            return nombresColumnas[col];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return classes[col];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col != COLUMNA_NUMERO && col != COLUMNA_SIZE;
        }

        public Object getValueAt(int row, int col) {
            Container container = webPage.get(row);

            switch (col) {
                case COLUMNA_NUMERO:
                    return container;

                case COLUMNA_TAB:
                    return container.getTab();

                case COLUMNA_X:
                    return container.getX();

                case COLUMNA_HEIGHT:
                    return container.getH();

                case COLUMNA_SIZE:
                    return container.size();

                default:
                    throw new RuntimeException("Columna inválida: " + col);
            }
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            Container container = webPage.get(row);

            switch (col) {
                case COLUMNA_NUMERO:
                    break;

                case COLUMNA_TAB:
                    container.setTab(String.valueOf(value));
                    break;

                case COLUMNA_X:
                    container.setX(WebPageUtils.resolverValor(value, false));
                    break;

                case COLUMNA_HEIGHT:
                    container.setH(WebPageUtils.resolverValor(value, true));
                    break;

                case COLUMNA_SIZE:
                    break;

                default:
                    throw new RuntimeException("Columna inválida: " + col);
            }

            fireTableCellUpdated(row, col);
        }

    }

}
