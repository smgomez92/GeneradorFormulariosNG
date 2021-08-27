package com.fitbank.ifg.swing.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

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

import com.fitbank.ifg.DatosPortaPapeles;
import com.fitbank.ifg.swing.Boton;
import com.fitbank.propiedades.PropiedadLista;
import com.fitbank.util.Servicios;

/**
 * Clase que edita una PropiedadLista.
 * 
 * @author FitBank
 */
public class EditorPropiedadLista<TIPO> extends JDialog {

    private static final long serialVersionUID = 1L;

    private BorderLayout borderLayout1 = new BorderLayout();

    private JScrollPane scrollLista = new JScrollPane();

    private DefaultListModel modeloListaValores = new DefaultListModel();

    private JList listaValores = new JList(modeloListaValores);

    private JPanel panelBotonesAbajo = new JPanel();

    private JToolBar toolbar = new JToolBar();

    private JButton aceptar = new Boton("gtk-apply", "actions/document-save.png", false);

    private JButton cancelar = new Boton("gtk-cancel", "actions/process-stop.png", false);

    private JButton subir = new Boton("gtk-go-up", "subir.png", false);

    private JButton bajar = new Boton("gtk-go-down", "bajar.png", false);

    private JButton nuevo = new Boton("gtk-add", "mas.png", false);

    private JButton borrar = new Boton("gtk-remove", "menos.png", false);

    private JButton editar = new Boton("gtk-edit", "editor.png", false);

    private JButton todos = new JButton();

    private JButton copy = new Boton("gtk-copy", "actions/edit-copy.png", false);

    private JButton cut = new Boton("gtk-cut", "actions/edit-cut.png", false);

    private JButton paste = new Boton("gtk-paste", "actions/edit-paste.png",
            false);

    private PropiedadLista<TIPO> pl;

    /**
     * Crea un nuevo objeto EditorPropiedadLista.
     * 
     * @param parent
     *            Venatana que abrió esta
     */
    public EditorPropiedadLista(Window parent) {
        super(parent, "Editar Propiedades", ModalityType.APPLICATION_MODAL);

        jbInit();
        setLocationRelativeTo(null);
    }

    public void updateButtons() {
        if (pl.getMax() == pl.getMin()) {
            nuevo.setEnabled(false);
            borrar.setEnabled(false);
        }

        if (Servicios.isSimpleType(pl.getItemsClass())) {
            editar.setEnabled(false);
        } else {
            todos.setEnabled(false);
        }

        nuevo.setEnabled(modeloListaValores.size() < pl.getMax());
        borrar.setEnabled(modeloListaValores.size() > pl.getMin());

        copy.setEnabled(!listaValores.isSelectionEmpty());
        cut.setEnabled(!listaValores.isSelectionEmpty() && borrar.isEnabled());
        paste.setEnabled(nuevo.isEnabled() && DatosPortaPapeles.isAvailable(pl.
                getItemsClass()));
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
        subir.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                subir_actionPerformed();
            }

        });
        bajar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                bajar_actionPerformed();
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
        todos.setText("<<");
        todos.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                todos_actionPerformed(e);
            }

        });
        editar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                editar_actionPerformed(e);
            }

        });
        copy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                copy_actionPerformed(e);
            }

        });
        cut.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cut_actionPerformed(e);
            }

        });
        paste.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                paste_actionPerformed(e);
            }

        });

        scrollLista.getViewport().add(listaValores);
        getContentPane().add(scrollLista, BorderLayout.CENTER);

        panelBotonesAbajo.add(aceptar, null);
        panelBotonesAbajo.add(cancelar, null);
        getContentPane().add(panelBotonesAbajo, BorderLayout.SOUTH);

        toolbar.add(nuevo);
        toolbar.add(borrar);
        toolbar.add(subir);
        toolbar.add(bajar);
        toolbar.addSeparator();
        toolbar.add(todos);
        toolbar.add(editar);
        toolbar.addSeparator();
        toolbar.add(copy);
        toolbar.add(cut);
        toolbar.add(paste);
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
     * @param pl
     *            Propiedad a ser editada
     * 
     * @return PropiedadLista editada
     */
    public PropiedadLista<TIPO> editar(PropiedadLista<TIPO> pl) {
        this.pl = pl;
        listaValores.setModel(modeloListaValores);

        for (TIPO t : pl.getList()) {
            modeloListaValores.addElement(t);
        }

        updateButtons();
        setVisible(true);

        return this.pl;
    }

    @SuppressWarnings("unchecked")
    private void aceptar_actionPerformed() {
        pl.setValor((List<TIPO>) Arrays.asList(modeloListaValores.toArray()));
        setVisible(false);
    }

    private void cancelar_actionPerformed() {
        setVisible(false);
    }

    private void subir_actionPerformed() {
        int s = listaValores.getSelectedIndex();

        if (!listaValores.isSelectionEmpty() && s > 0) {
            Object o = listaValores.getSelectedValue();
            modeloListaValores.removeElementAt(s);
            modeloListaValores.insertElementAt(o, s - 1);
            listaValores.setSelectedIndex(s - 1);
        }
    }

    private void bajar_actionPerformed() {
        int s = listaValores.getSelectedIndex();

        if (!listaValores.isSelectionEmpty()
                && s < modeloListaValores.getSize() - 1) {
            Object o = listaValores.getSelectedValue();
            modeloListaValores.removeElementAt(s);
            modeloListaValores.insertElementAt(o, s + 1);
            listaValores.setSelectedIndex(s + 1);
        }
    }

    private void nuevo_actionPerformed() {
        TIPO nuevoValor;
        Class<?> itemsClass;

        if (!pl.getItemsSubClasses().isEmpty()) {
            itemsClass = (Class<?>) JOptionPane.showInputDialog(this,
                    "Escoja una clase", "Escoja una clase",
                    JOptionPane.QUESTION_MESSAGE, null, pl.getItemsSubClasses().
                    toArray(), pl.getValor().getClass());
            if (itemsClass == null) {
                return;
            }
        } else {
            itemsClass = pl.getItemsClass();
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
            if (listaValores.getSelectedIndex() >= 0) {
                modeloListaValores.add(listaValores.getSelectedIndex(),
                        nuevoValor);
            } else {
                modeloListaValores.addElement(nuevoValor);
            }
        }

        updateButtons();
    }

    private void borrar_actionPerformed() {
        if (!listaValores.isSelectionEmpty()) {
            modeloListaValores.removeElementAt(listaValores.getSelectedIndex());
        }

        updateButtons();
    }

    private void todos_actionPerformed(ActionEvent e) {
        String nuevoValor = JOptionPane.showInputDialog(this,
                "Cambiar todos los valores");

        if (nuevoValor != null) {
            for (int a = 0; a < modeloListaValores.getSize(); a++) {
                modeloListaValores.setElementAt(nuevoValor, a);
            }
        }
    }

    private void editar_actionPerformed(ActionEvent e) {
        if (!listaValores.isSelectionEmpty()) {
            Object object = modeloListaValores.elementAt(listaValores.
                    getSelectedIndex());
            if (Servicios.isSimpleType(object.getClass())) {
                int cual = listaValores.getSelectedIndex();
                modeloListaValores.setElementAt(JOptionPane.showInputDialog(
                        listaValores, "Editar valor", modeloListaValores.
                        elementAt(cual)), cual);
            } else {
                new EditorPropiedades(this, object).setVisible(true);
            }
        }
    }

    private void copy_actionPerformed(ActionEvent e) {
        DatosPortaPapeles.copy(listaValores.getSelectedValue(), null);

        updateButtons();
    }

    private void cut_actionPerformed(ActionEvent e) {
        copy_actionPerformed(e);
        borrar_actionPerformed();
    }

    private void paste_actionPerformed(ActionEvent e) {
        modeloListaValores.addElement(DatosPortaPapeles.get(pl.getItemsClass()));

        updateButtons();
    }

}
