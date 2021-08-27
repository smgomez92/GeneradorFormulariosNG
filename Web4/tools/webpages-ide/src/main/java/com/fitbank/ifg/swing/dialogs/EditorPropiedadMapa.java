package com.fitbank.ifg.swing.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.fitbank.ifg.swing.Boton;
import com.fitbank.propiedades.PropiedadMapa;
import com.fitbank.util.Servicios;

/**
 * Clase que edita una PropiedadMapa.
 *
 * @author FitBank
 */
public class EditorPropiedadMapa<TIPO> extends JDialog {

    private static final long serialVersionUID = 1L;

    private BorderLayout borderLayout1 = new BorderLayout();

    private JScrollPane scrollLista = new JScrollPane();

    private DefaultListModel modeloListaValores = new DefaultListModel();

    private JList listaValores = new JList(modeloListaValores);

    private JPanel panelBotonesAbajo = new JPanel();

    private JToolBar toolbar = new JToolBar();

    private JButton aceptar = new Boton("gtk-apply", "", false);

    private JButton cancelar = new Boton("gtk-cancel", "", false);

    private JButton nuevo = new Boton("gtk-add", "mas.png", false);

    private JButton borrar = new Boton("gtk-remove", "menos.png", false);

    private JButton editar = new Boton("gtk-edit", "editor.png", false);

    private PropiedadMapa<String, TIPO> pm;

    /**
     * Crea un nuevo objeto EditorPropiedadMapa.
     *
     * @param parent
     *            Venatana que abrió esta
     */
    public EditorPropiedadMapa(Window parent) {
        super(parent, "Editar Propiedades", ModalityType.APPLICATION_MODAL);

        jbInit();
        setLocationRelativeTo(null);
    }

    public void updateButtons() {
        borrar.setEnabled(modeloListaValores.size() > 0);
    }

    private void jbInit() {
        this.setSize(new Dimension(400, 300));

        getContentPane().setLayout(borderLayout1);

        listaValores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaValores.setFixedCellHeight(20);
        listaValores.setOpaque(false);
        listaValores.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                updateButtons();
            }

        });

        aceptar.setText("Aceptar");
        aceptar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                aceptar_actionPerformed();
            }

        });
        cancelar.setText("Cancelar");
        cancelar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cancelar_actionPerformed();
            }

        });
        nuevo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                nuevo_actionPerformed();
            }

        });
        borrar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                borrar_actionPerformed();
            }

        });
        editar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editar_actionPerformed(e);
            }

        });

        scrollLista.getViewport().add(listaValores);
        getContentPane().add(scrollLista, BorderLayout.CENTER);

        panelBotonesAbajo.add(aceptar);
        panelBotonesAbajo.add(cancelar);
        getContentPane().add(panelBotonesAbajo, BorderLayout.SOUTH);

        toolbar.add(nuevo);
        toolbar.add(borrar);
        toolbar.add(editar);
        getContentPane().add(toolbar, BorderLayout.NORTH);

        listaValores.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editar_actionPerformed(null);
                }
            }

        });
    }

    /**
     * Edita la propiedad.
     *
     * @param valorActual
     *            Valor que tiene actualmente
     * @param pm
     *            Propiedad a ser editada
     *
     * @return PropiedadLista editada
     */
    public PropiedadMapa<String, TIPO> editar(PropiedadMapa<String, TIPO> pm) {
        this.pm = pm;
        listaValores.setModel(modeloListaValores);

        for (Map.Entry t : pm.getValor().entrySet()) {
            modeloListaValores.addElement(new Entry(t));
        }

        if (modeloListaValores.isEmpty()) {
            borrar.setEnabled(false);
        }

        if (Servicios.isSimpleType(pm.getItemsClass())) {
            editar.setVisible(false);
        }

        setVisible(true);

        return this.pm;
    }

    @SuppressWarnings("unchecked")
    private void aceptar_actionPerformed() {
        pm.getValor().clear();
        for (Object object : modeloListaValores.toArray()) {
            Entry entry = (Entry) object;
            pm.getValor().put(entry.getKey(), entry.getValue());
        }
        setVisible(false);
    }

    private void cancelar_actionPerformed() {
        setVisible(false);
    }

    @SuppressWarnings("unchecked")
    private void nuevo_actionPerformed() {
        String key = JOptionPane.showInputDialog(this, "Key");

        if (key == null) {
            return;
        } else {
            for (Object object : modeloListaValores.toArray()) {
                Entry entry = (Entry) object;
                if (entry.getKey().equals(key)) {
                    JOptionPane.showMessageDialog(this, "Clave duplicada");
                    return;
                }
            }
        }

        TIPO nuevoValor;
        Class<?> itemsClass;

        if (!pm.getItemsSubClasses().isEmpty()) {
            itemsClass = (Class<?>) JOptionPane.showInputDialog(this,
                    "Escoja una clase", "Escoja una clase",
                    JOptionPane.QUESTION_MESSAGE, null, pm.getItemsSubClasses().
                    toArray(), pm.getValor().getClass());
            if (itemsClass == null) {
                return;
            }
        } else {
            itemsClass = pm.getItemsClass();
        }

        if (Servicios.isSimpleType(itemsClass)) {
            nuevoValor = (TIPO) JOptionPane.showInputDialog(this, "Nuevo Valor");
        } else {
            try {
                nuevoValor = (TIPO) itemsClass.getConstructor().newInstance();
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        if (nuevoValor != null) {
            Entry entry = new Entry(key, nuevoValor);

            if (listaValores.getSelectedIndex() >= 0) {
                modeloListaValores.add(listaValores.getSelectedIndex(), entry);
            } else {
                modeloListaValores.addElement(entry);
            }
        }

        if (modeloListaValores.size() > 0) {
            borrar.setEnabled(true);
        }
    }

    private void borrar_actionPerformed() {
        if (!listaValores.isSelectionEmpty()) {
            modeloListaValores.removeElementAt(listaValores.getSelectedIndex());
        }

        if (modeloListaValores.isEmpty()) {
            borrar.setEnabled(false);
        }
    }

    private void editar_actionPerformed(ActionEvent e) {
        if (!listaValores.isSelectionEmpty()) {
            Entry entry = (Entry) modeloListaValores.elementAt(listaValores.
                    getSelectedIndex());
            Object object = entry.getValue();
            if (Servicios.isSimpleType(object.getClass())) {
                TIPO nuevoValor = (TIPO) JOptionPane.showInputDialog(
                        listaValores, "Editar valor", object);
                if (nuevoValor != null) {
                    entry.setValue(nuevoValor);
                }
            } else {
                new EditorPropiedades(this, object).setVisible(true);
            }
        }
    }

    private class Entry implements Map.Entry<String, TIPO> {

        String key;

        TIPO value;

        public Entry(String key, TIPO value) {
            this.key = key;
            this.value = value;
        }

        private Entry(Map.Entry<String, TIPO> t) {
            this.key = t.getKey();
            this.value = t.getValue();
        }

        public String getKey() {
            return key;
        }

        public TIPO getValue() {
            return value;
        }

        public TIPO setValue(TIPO value) {
            this.value = value;
            return value;
        }

        @Override
        public String toString() {
            return key + " <= " + value;
        }

    };

}
