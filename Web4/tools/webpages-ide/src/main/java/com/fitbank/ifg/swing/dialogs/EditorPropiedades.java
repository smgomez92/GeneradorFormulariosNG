package com.fitbank.ifg.swing.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.fitbank.ifg.Mensajes;
import com.fitbank.ifg.iFG;
import com.fitbank.ifg.swing.Boton;
import com.fitbank.ifg.swing.tables.PropertiesTable;
import com.fitbank.ifg.swing.tables.PropertiesTable.PropertiesTableModel;
import com.fitbank.util.Debug;
import java.awt.Window;
import javax.swing.JFrame;

/**
 * Clase que sirve para editar las properties de los formularios y sus
 * derivados.
 * 
 * @author FitBank
 */
public class EditorPropiedades extends JDialog {

    private static final long serialVersionUID = 1L;

    private JScrollPane scrollTabla;

    private BorderLayout borderLayout = new BorderLayout();

    private PropertiesTable tablaPropiedades;

    private JPanel panelBotones = new JPanel();

    private JButton cerrar = new Boton("gtk-close",
            "actions/system-shutdown.png", false);

    private int altoVentana = 416;

    /**
     * Constructor con un Dialog.
     *
     * @param parent
     *            Diálogo que abrio este diálogo
     * @param object
     *            Objeto que va a ser editado
     */
    public EditorPropiedades(Window parent, Object object) {
        this(parent, "Editar Propiedades", new PropertiesTable(parent, object));
    }

    protected EditorPropiedades(Window parent, String title,
            PropertiesTable tablaPropiedades) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        this.tablaPropiedades = tablaPropiedades;
        jbInit();
    }

    protected final void jbInit() {
        PropertiesTableModel modelo = (PropertiesTableModel) tablaPropiedades.
                getModel();
        altoVentana = modelo.getRowCount() * 22 + 100;
        this.setLocation(70, 90);
        this.setSize(new Dimension(450, altoVentana));

        getContentPane().setLayout(borderLayout);

        cerrar.setText("Cerrar");
        cerrar.setActionCommand("cerrar");
        cerrar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                EditorPropiedades.this.cerrarActionPerformed();
            }

        });

        tablaPropiedades.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    EditorPropiedades.this.setVisible(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    EditorPropiedades.this.cerrarActionPerformed();
                }
            }

        });

        scrollTabla = new JScrollPane();
        scrollTabla.getViewport().add(tablaPropiedades);
        getContentPane().add(scrollTabla, BorderLayout.CENTER);

        panelBotones.add(cerrar);
        getContentPane().add(panelBotones, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void cerrarActionPerformed() {
        if (tablaPropiedades.isEditing()) {
            tablaPropiedades.getCellEditor().stopCellEditing();
        }

        setVisible(false);
    }

    @Override
    public void setVisible(boolean b) {
        if (b && tablaPropiedades.getModel().getRowCount() == 0) {
            Debug.warn(Mensajes.format("iFG.SinParametrosParaEditar"));//$NON-NLS-1$

        } else {
            super.setVisible(b);
            if (iFG.getSingleton().isAbierto()) {
                iFG.getSingleton().getPanelFilasElementos().saveUndoState();
            }
        }
    }

}
