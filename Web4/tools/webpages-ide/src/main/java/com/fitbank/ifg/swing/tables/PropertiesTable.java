package com.fitbank.ifg.swing.tables;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.beanutils.WrapDynaBean;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import com.fitbank.ifg.DatosPortaPapeles;
import com.fitbank.ifg.swing.tables.celleditors.EditorCeldaPropiedad;
import com.fitbank.ifg.swing.tables.celleditors.PasteTableCellRenderer;
import com.fitbank.ifg.swing.tables.celleditors.RendererCelda;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadSeparador;
import com.fitbank.propiedades.anotaciones.UtilPropiedades;
import com.fitbank.webpages.WebElement;
import java.awt.Font;
import javax.swing.JLabel;
import org.jdesktop.swingx.decorator.FontHighlighter;

public class PropertiesTable extends BaseJTable {

    private static final long serialVersionUID = 1L;

    public static final int COLUMNA_TITULO = 0;

    public static final int COLUMNA_PROPIEDAD = 1;

    public static final int COLUMNA_PEGAR = 2;

    private PropertiesTableModel modelo;

    private final Window parent;

    public PropertiesTable(Window parent, Object object) {
        this(parent, object instanceof WebElement<?> ? ((WebElement<?>) object).
                getPropiedadesEdicion() : UtilPropiedades.getPropiedades(object));
    }

    public PropertiesTable(Window parent, Collection<Propiedad<?>> propiedades) {
        this.modelo = new PropertiesTableModel(propiedades);
        this.parent = parent;

        jbInit();
    }

    private void jbInit() {
        setModel(modelo);
        setSortable(false);

        getColumnModel().getColumn(COLUMNA_TITULO).setPreferredWidth(240);
        getColumnModel().getColumn(COLUMNA_TITULO).setMaxWidth(240);
        getColumnModel().getColumn(COLUMNA_PROPIEDAD).setPreferredWidth(200);
        getColumnModel().getColumn(COLUMNA_PEGAR).setPreferredWidth(20);
        getColumnModel().getColumn(COLUMNA_PEGAR).setMaxWidth(20);
        getColumnModel().getColumn(COLUMNA_PEGAR).setMinWidth(20);

        getColumnModel().getColumn(COLUMNA_PROPIEDAD).setCellRenderer(
                new RendererCelda());
        getColumnModel().getColumn(COLUMNA_PROPIEDAD).setCellEditor(
                new EditorCeldaPropiedad(this.parent));

        getColumnModel().getColumn(COLUMNA_PEGAR).setCellRenderer(
                new PasteTableCellRenderer());
        
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                int row = rowAtPoint(point);
                int col = columnAtPoint(point);

                if (col == COLUMNA_PEGAR) {
                    Object value = getValueAt(row, col);
                    if (value != null) {
                        ((Propiedad) getValueAt(row, COLUMNA_PROPIEDAD)).
                                setValor(value);
                        modelo.setValueAt(value, row, COLUMNA_PROPIEDAD);
                    }
                }
            }

        });

        // Resaltar separadores
        addHighlighter(new ColorHighlighter(new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                int r = getRowSorter().convertRowIndexToModel(adapter.row);

                Propiedad propiedad = modelo.propiedades.get(r);

                return propiedad instanceof PropiedadSeparador;
            }

        }, new Color(0f, 0f, 0f, 0.5f), Color.WHITE));

        // Bold para propiedades cambiadas
        addHighlighter(new FontHighlighter(new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                int c = getColumnExt(adapter.column).getModelIndex();

                if (c == COLUMNA_PROPIEDAD) {
                    return false;
                }

                int r = getRowSorter().convertRowIndexToModel(adapter.row);

                Propiedad propiedad = modelo.propiedades.get(r);

                return !propiedad.esValorPorDefecto();
            }

        }, new JLabel().getFont().deriveFont(Font.BOLD)));

        // Aclarar valores por defecto
        addHighlighter(new ColorHighlighter(new HighlightPredicate() {

            public boolean isHighlighted(Component renderer,
                    ComponentAdapter adapter) {
                int c = getColumnExt(adapter.column).getModelIndex();

                if (c == COLUMNA_TITULO) {
                    return false;
                }

                int r = getRowSorter().convertRowIndexToModel(adapter.row);

                Propiedad propiedad = modelo.propiedades.get(r);

                return propiedad.esValorPorDefecto();
            }

        }, new Color(0f, 0f, 0f, 0f), new Color(1f, 1f, 1f, 0.5f)));
    }

    public class PropertiesTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private List<Propiedad> propiedades = null;

        private final String[] nombresColumnas = { "Nombre", "Valor", "" };

        protected Object datosPortapapeles = null;

        public PropertiesTableModel(Collection<Propiedad<?>> propiedades) {
            this.propiedades = new ArrayList<Propiedad>(propiedades);
            this.datosPortapapeles = DatosPortaPapeles.get(Object.class);
        }

        public int getColumnCount() {
            return nombresColumnas.length;
        }

        public int getRowCount() {
            return propiedades.size();
        }

        @Override
        public String getColumnName(int col) {
            return nombresColumnas[col];
        }

        @Override
        public Class getColumnClass(int c) {
            Object value = getValueAt(0, c);
            return value != null ? value.getClass() : String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == COLUMNA_PROPIEDAD
                    && !(propiedades.get(row) instanceof PropiedadSeparador)
                    && propiedades.get(row) instanceof Propiedad
                    && propiedades.get(row).getActiva();
        }

        public Object getValueAt(int row, int col) {
            if (propiedades.size() <= row) {
                return null;
            }

            Propiedad propiedad = propiedades.get(row);

            switch (col) {
                case COLUMNA_TITULO:
                    return propiedad.getDescripcion();

                case COLUMNA_PROPIEDAD:
                    return propiedad;

                case COLUMNA_PEGAR:
                    if (datosPortapapeles == null
                            || propiedad instanceof PropiedadSeparador) {
                        return null;
                    }

                    Object valorPorDefecto = propiedad.getValorPorDefecto();
                    if (valorPorDefecto.getClass().isInstance(datosPortapapeles)
                            && !(datosPortapapeles instanceof Collection)) {
                        return datosPortapapeles;
                    }

                    WrapDynaBean bean = new WrapDynaBean(datosPortapapeles);

                    if (bean.getDynaClass().getDynaProperty(
                            propiedad.getNombre()) != null) {
                        return bean.get(propiedad.getNombre());
                    }

                    return null;
            }

            throw new RuntimeException("Columna especificada incorrecta: " + col);
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            fireTableCellUpdated(row, col);

            if (value instanceof Propiedad) {
                fireTableDataChanged();
            }
        }

    }

}