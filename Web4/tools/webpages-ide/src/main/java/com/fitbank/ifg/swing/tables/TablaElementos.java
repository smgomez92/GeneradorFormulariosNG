package com.fitbank.ifg.swing.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.StringValue;

import com.fitbank.ifg.swing.dialogs.datasource.DataSourceEditor;
import com.fitbank.util.Debug;
import com.fitbank.webpages.Container;
import com.fitbank.webpages.WebPageUtils;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.None;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.FormElement;
import com.fitbank.webpages.widgets.Button;
import com.fitbank.webpages.widgets.ColumnSeparator;
import com.fitbank.webpages.widgets.DeleteRecord;
import com.fitbank.webpages.widgets.FooterSeparator;
import com.fitbank.webpages.widgets.HeaderSeparator;
import com.fitbank.webpages.widgets.Input;
import com.fitbank.webpages.widgets.Label;
import com.fitbank.webpages.widgets.Square;

public class TablaElementos extends BaseJTable {

    private static final long serialVersionUID = 1L;

    private JComboBox tipos = new JComboBox(WebPageUtils.getWidgetSubClasses().
            toArray());

    public static final int COLUMNA_NUMERO = 0;

    public static final int COLUMNA_TIPO = 1;

    public static final int COLUMNA_TEXTO = 2;

    public static final int COLUMNA_NAME = 3;

    public static final int COLUMNA_VALUE = 4;

    public static final int COLUMNA_ORIGEN = 5;

    public static final int COLUMNA_X = 6;

    public static final int COLUMNA_Y = 7;

    public static final int COLUMNA_W = 8;

    public static final int COLUMNA_H = 9;

    public static final int COLUMNA_Z = 10;

    private final JDialog parent;

    private ModeloTablaElementos modelo = new ModeloTablaElementos();

    public TablaElementos(JDialog parent, Container container) {
        this.parent = parent;

        setColumnControlVisible(true);

        tipos.setRenderer(new DefaultListCellRenderer() {

            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel jLabel = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                jLabel.setText(WebPageUtils.getDescription((Class<?>) value));
                return jLabel;
            }

        });

        setModel(modelo);

        TableColumnModel model = getColumnModel();

        model.getColumn(COLUMNA_NUMERO).setPreferredWidth(50);
        model.getColumn(COLUMNA_NUMERO).setMinWidth(50);
        model.getColumn(COLUMNA_TIPO).setPreferredWidth(90);
        model.getColumn(COLUMNA_TEXTO).setPreferredWidth(110);
        model.getColumn(COLUMNA_NAME).setPreferredWidth(110);
        model.getColumn(COLUMNA_VALUE).setPreferredWidth(110);
        model.getColumn(COLUMNA_ORIGEN).setPreferredWidth(300);
        model.getColumn(COLUMNA_X).setPreferredWidth(30);
        model.getColumn(COLUMNA_Y).setPreferredWidth(30);
        model.getColumn(COLUMNA_W).setPreferredWidth(30);
        model.getColumn(COLUMNA_H).setPreferredWidth(30);
        model.getColumn(COLUMNA_Z).setPreferredWidth(30);

        model.getColumn(COLUMNA_NUMERO).setCellRenderer(new DefaultTableRenderer(new StringValue() {

            public String getString(Object value) {
                Widget widget = (Widget) value;
                String js = "";
                String assistant = "";
                String vis = widget.getVisible() ? "" : " o";

                if (widget instanceof FormElement) {
                    Class a = ((FormElement) widget).getAssistant().getClass();
                    assistant = a.equals(None.class) ? "" : " -" + a.
                            getSimpleName().
                            replaceAll("[^A-Z]", "").toLowerCase();
                }

                if (widget instanceof Input) {
                    js = StringUtils.isNotBlank(((Input) widget).getJavaScript())
                            ? " j" : "";
                } else if (widget instanceof Label) {
                    js = StringUtils.isNotBlank(((Label) widget).getJavaScript())
                            ? " j" : "";
                }

                return String.format("%s%s%s%s", StringUtils.leftPad(String.
                        valueOf(
                        widget.getPosicion() + 1), 3, '0'), vis, js, assistant);
            }

        }));
        getColumnExt(COLUMNA_NUMERO).setComparator(new Comparator<Widget>() {

            public int compare(Widget o1, Widget o2) {
                return o1.getPosicion() - o2.getPosicion();
            }

        });

        model.getColumn(COLUMNA_TIPO).setCellRenderer(new DefaultTableRenderer(new StringValue() {

            public String getString(Object value) {
                return WebPageUtils.getDescription((Class) value);
            }

        }));
        getColumnExt(COLUMNA_TIPO).setComparator(new Comparator<Class>() {

            public int compare(Class o1, Class o2) {
                return o1.toString().compareTo(o2.toString());
            }

        });

        DefaultCellEditor editor = new DefaultCellEditor(tipos);
        editor.setClickCountToStart(2);
        model.getColumn(COLUMNA_TIPO).setCellEditor(editor);

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && getColumnExt(columnAtPoint(e.
                        getPoint())).getModelIndex() == COLUMNA_ORIGEN) {
                    editarDataSource();
                }
            }

        });

        addHighlighter(new ColorHighlighter(new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                int r = getRowSorter().convertRowIndexToModel(adapter.row);
                Widget widget = modelo.getContainer().get(r);

                return isSeparator(widget);
            }

        }, new Color(0f, 0f, 0f, 0.5f), Color.WHITE));

        setContainer(container);
        
        setHorizontalScrollEnabled(true);
        
    }

    private boolean isSeparator(Widget widget) {
        return widget instanceof HeaderSeparator
                || widget instanceof FooterSeparator
                || widget instanceof ColumnSeparator;
    }

    public Container getContainerActual() {
        return modelo.getContainer();
    }

    public final void setContainer(Container container) {
        modelo.setContainer(container);
    }

    public void refresh() {
        modelo.fireTableDataChanged();
    }

    public void editarDataSource() {
        Widget widget = getContainerActual().get(getSelectedRow());
        DataSourceEditor dse = new DataSourceEditor(parent);
        dse.load(widget.getParentWebPage(), widget.getDataSource());
        dse.setVisible(true);
        widget.setDataSource(dse.getDataSource());
        refresh();
        setRowSelectionInterval(widget.getPosicion(), widget.getPosicion());
    }

    public class ModeloTablaElementos extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        final String[] columnNames = { "No.", "Tipo", "Texto", "Name", "Value",
            "DataSource", "X", "Y", "W", "H", "Z" };

        final Class<?>[] classes = { String.class, String.class, String.class,
            String.class, String.class, DataSource.class, Integer.class,
            Integer.class, Integer.class, Integer.class, Integer.class };

        private Container container = null;

        public Container getContainer() {
            return container;
        }

        public void setContainer(Container container) {
            this.container = container;
            fireTableDataChanged();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return getContainer() != null ? getContainer().size() : 0;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return classes[col];
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col != COLUMNA_NUMERO && col != COLUMNA_ORIGEN;
        }

        public Object getValueAt(int row, int col) {
            Widget widget = getContainer().get(row);

            switch (col) {
                case COLUMNA_NUMERO:
                    return widget;

                case COLUMNA_TIPO:
                    return widget.getClass();

                case COLUMNA_TEXTO:
                    if (widget instanceof Button) {
                        return ((Button) widget).getEtiqueta();
                    } else if (widget instanceof Input ||
                            widget instanceof DeleteRecord) {
                        return "";
                    } else {
                        return widget.getTexto();
                    }

                case COLUMNA_NAME:
                    if (widget instanceof Input) {
                        return ((Input) widget).getName();
                    } else if (widget instanceof Label) {
                        return ((Label) widget).getIdentificador();
                    } else if (widget instanceof DeleteRecord) {
                        return ((DeleteRecord) widget).getName();
                    } else {
                        return "";
                    }

                case COLUMNA_VALUE:
                    if (widget instanceof Input) {
                        return ((Input) widget).getValueInicial();
                    } else {
                        return "";
                    }

                case COLUMNA_ORIGEN:
                    if (widget instanceof FormElement) {
                        return widget.getDataSource();
                    } else {
                        return "";
                    }

                case COLUMNA_X:
                    return isSeparator(widget) ? "" : widget.getX();

                case COLUMNA_Y:
                    return isSeparator(widget) ? "" : widget.getY();

                case COLUMNA_W:
                    return isSeparator(widget) ? "" : widget.getW();

                case COLUMNA_H:
                    return isSeparator(widget) ? "" : widget.getH();

                case COLUMNA_Z:
                    return isSeparator(widget) ? "" : widget.getZ();

                default:
                    throw new RuntimeException("Columna inválida: " + col);
            }
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            Widget widget = getContainer().get(row);

            switch (col) {
                case COLUMNA_NUMERO:
                    break;

                case COLUMNA_TIPO:
                    if (!widget.getClass().equals(value)) {
                        try {
                            Widget nuevoWidget = (Widget) ((Class<?>) value).
                                    getConstructor().newInstance();
                            if (!isSeparator(nuevoWidget) && !(nuevoWidget instanceof Square)) {
                                BeanUtils.copyProperties(nuevoWidget, widget);                                
                            }
                            getContainer().set(widget.getPosicion(), nuevoWidget);
                        } catch (Exception e) {
                            Debug.error(e);
                        }
                    }
                    break;

                case COLUMNA_TEXTO:
                    if (widget instanceof Button) {
                        ((Button) widget).setEtiqueta((String) value);
                    } else if (widget instanceof Input ||
                            widget instanceof DeleteRecord) {
                        // Nada
                    } else {
                        widget.setTexto((String) value);
                    }
                    break;

                case COLUMNA_NAME:
                    if (widget instanceof Input) {
                        ((Input) widget).setName((String) value);
                    } else if (widget instanceof Label) {
                        ((Label) widget).setIdentificador((String) value);
                    } else if (widget instanceof DeleteRecord) {
                        ((DeleteRecord) widget).setName((String) value);
                    }
                    break;

                case COLUMNA_VALUE:
                    if (widget instanceof Input) {
                        ((Input) widget).setValueInicial((String) value);
                    }
                    break;

                case COLUMNA_ORIGEN:
                    break;

                case COLUMNA_X:
                    widget.setX(WebPageUtils.resolverValor(value, false));
                    break;

                case COLUMNA_Y:
                    widget.setY(WebPageUtils.resolverValor(value, false));
                    break;

                case COLUMNA_W:
                    widget.setW(WebPageUtils.resolverValor(value, true));
                    break;

                case COLUMNA_H:
                    widget.setH(WebPageUtils.resolverValor(value, true));
                    break;

                case COLUMNA_Z:
                    widget.setZ(WebPageUtils.resolverValor(value, true));
                    break;

                default:
                    throw new RuntimeException("Columna inválida: " + col);
            }

            fireTableCellUpdated(row, col);
        }

    }

}
