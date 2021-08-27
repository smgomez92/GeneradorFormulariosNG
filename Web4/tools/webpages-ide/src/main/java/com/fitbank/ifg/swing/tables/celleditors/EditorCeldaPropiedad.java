package com.fitbank.ifg.swing.tables.celleditors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;

import com.fitbank.ifg.Configuracion;
import com.fitbank.ifg.iFG;
import com.fitbank.ifg.swing.Boton;
import com.fitbank.ifg.swing.dialogs.EditorJavascript;
import com.fitbank.ifg.swing.dialogs.EditorPropiedadLista;
import com.fitbank.ifg.swing.dialogs.EditorPropiedadMapa;
import com.fitbank.ifg.swing.dialogs.EditorPropiedades;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadBooleana;
import com.fitbank.propiedades.PropiedadCombo;
import com.fitbank.propiedades.PropiedadComboLibre;
import com.fitbank.propiedades.PropiedadEstilos;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadLista;
import com.fitbank.propiedades.PropiedadListaString;
import com.fitbank.propiedades.PropiedadMapa;
import com.fitbank.propiedades.PropiedadNumerica;
import com.fitbank.propiedades.PropiedadObjeto;
import com.fitbank.propiedades.PropiedadSimple;
import com.fitbank.util.Debug;

/**
 * Clase que edita properties por defecto y escoge que clase debe usar para
 * editar.
 */
public class EditorCeldaPropiedad extends DefaultCellEditor implements
        ActionListener {

    private static final long serialVersionUID = 1L;

    private static Image icon = Boton.load("gtk-edit",
            "actions/view-fullscreen.png", false).getImage().getScaledInstance(10,
            10, Image.SCALE_SMOOTH);

    private Propiedad propiedad = null;

    private final Window parent;

    public EditorCeldaPropiedad(Window parent) {
        super(new JTextField());
        this.parent = parent;
        setClickCountToStart(1);
    }

    protected static JButton getButton() {
        return new JButton(new ImageIcon(icon));
    }

    @Override
    public Object getCellEditorValue() {
        return propiedad;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        propiedad = (Propiedad) value;

        if (propiedad instanceof PropiedadBooleana) {
            editorComponent = getComponentPropiedadBooleana(value);

        } else if (propiedad instanceof PropiedadCombo) {
            editorComponent = getComponentPropiedadCombo();

        } else if (propiedad instanceof PropiedadNumerica) {
            editorComponent = getComponentPropiedadNumerica();

        } else if (propiedad instanceof PropiedadListaString) {
            editorComponent = getComponentPropiedadListaString();

        } else if (propiedad instanceof PropiedadLista) {
            editorComponent = getComponentPropiedadLista();

        } else if (propiedad instanceof PropiedadMapa) {
            editorComponent = getComponentPropiedadMapa();

        } else if (propiedad instanceof PropiedadJavascript) {
            editorComponent = getComponentPropiedadJavascript();

        } else if (propiedad instanceof PropiedadSimple) {
            editorComponent = getComponentPropiedadSimple();

        } else if (propiedad instanceof PropiedadObjeto) {
            editorComponent = getComponentPropiedadObjeto();

        } else {
            Debug.warn("Tipo de propiedad no manejada: " + propiedad.getClass());

            editorComponent = new JTextField(propiedad.getValorString());
        }

        editorComponent.setForeground(Color.BLACK);

        return editorComponent;
    }

    private JComponent getComponentPropiedadObjeto() {
        // //////////////////////////////
        // PropiedadObjeto
        PropiedadObjeto propiedadObjeto = (PropiedadObjeto) propiedad;
        Collection<Class<?>> instanceSubClasses = propiedadObjeto.
                getInstanceSubClasses();
        List<String> s = new ArrayList<String>(CollectionUtils.collect(
                instanceSubClasses, new Transformer() {

            public Object transform(Object arg0) {
                return ((Class<?>) arg0).getSimpleName();
            }

        }));

        try {
            if (instanceSubClasses.isEmpty()) {
                s.add(propiedadObjeto.getInstanceClass().getSimpleName());
                if (propiedad.getValor() == null) {
                    propiedad.setValor(propiedadObjeto.getInstanceClass().
                            getConstructor().newInstance());
                }
            } else {
                if (propiedad.getValor() == null) {
                    propiedad.setValor(instanceSubClasses.iterator().next().
                            getConstructor().newInstance());
                }
            }
        } catch (Exception e) {
            throw new Error(e);
        }

        JComponent ret;

        if (s.size() > 1) {
            ret = new JPanel(new BorderLayout());

            JComboBox comboBox = new JComboBox(s.toArray());
            ret.add(comboBox, BorderLayout.CENTER);

            comboBox.setSelectedIndex(s.indexOf(propiedad.getValor().getClass().
                    getSimpleName()));
            comboBox.addActionListener(this);
            comboBox.setForeground(Color.BLACK);

            ret.add(getButton(), BorderLayout.EAST);
        } else {
            ret = getLabelAndButton();
        }

        getButton(ret).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                new EditorPropiedades(parent, propiedad.getValor()).setVisible(
                        true);
                fireEditingStopped();
            }

        });

        return ret;
    }

    private JComponent getComponentPropiedadSimple() {
        // //////////////////////////////
        // PropiedadSimple
        return getTextField();
    }

    private JComponent getComponentPropiedadJavascript() {
        // //////////////////////////////
        // PropiedadJavascript
        JComponent ret = getLabelAndButton();
        getButton(ret).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                propiedad.setValor(new EditorJavascript(parent,
                        ((PropiedadJavascript) propiedad).getTipo()).editar(
                        propiedad.getValorString()));
                fireEditingStopped();
            }

        });
        return ret;
    }

    private JComponent getComponentPropiedadMapa() {
        // //////////////////////////////
        // PropiedadMapa
        JComponent ret = getLabelAndButton();
        getButton(ret).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                propiedad = new EditorPropiedadMapa(parent).editar(
                        (PropiedadMapa) propiedad);
            }

        });
        return ret;
    }

    private JComponent getComponentPropiedadLista() {
        // //////////////////////////////
        // PropiedadLista
        JComponent ret = getLabelAndButton();
        getButton(ret).addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                propiedad = new EditorPropiedadLista(parent).editar(
                        (PropiedadLista) propiedad);
                EditorCeldaPropiedad.this.fireEditingStopped();
            }

        });
        return ret;
    }

    private JComponent getComponentPropiedadListaString() {
        // //////////////////////////////
        // PropiedadListaString
        JComponent ret = new JPanel(new BorderLayout());

        final JTextField textField = getTextField();
        ret.add(textField, BorderLayout.CENTER);

        final JButton button = getButton();
        ret.add(button, BorderLayout.EAST);

        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                propiedad = new EditorPropiedadLista(parent).editar(
                        (PropiedadLista) propiedad);

                textField.setText(propiedad.getValorString());

                EditorCeldaPropiedad.this.fireEditingStopped();
            }

        });

        return ret;
    }

    private JComponent getComponentPropiedadNumerica() {
        // //////////////////////////////
        // PropiedadNumerica
        JSpinner spinner = new JSpinner();

        spinner.setValue(((PropiedadNumerica) propiedad).getNumber());

        return spinner;
    }

    private JComponent getComponentPropiedadCombo() {
        // //////////////////////////////
        // PropiedadCombo
        if (propiedad instanceof PropiedadEstilos) {
            ((PropiedadEstilos) propiedad).setEstilos(Configuracion.getHojaDeEstilos().getNombresEstilos());
        }

        String[] s = (String[]) ((PropiedadCombo) propiedad).getEtiquetas().
                keySet().toArray(new String[0]);

        JComboBox comboBox = new JComboBox(s);

        comboBox.setBorder(BorderFactory.createEmptyBorder());

        if (propiedad instanceof PropiedadComboLibre) {
            comboBox.setEditable(true);
            comboBox.setSelectedItem(propiedad.getValor());

        } else {
            for (int a = 0; a < s.length; a++) {
                if (propiedad.getValor().equals(((PropiedadCombo) propiedad).getValor(a))) {
                    comboBox.setSelectedIndex(a);
                }
            }
        }

        comboBox.addActionListener(this);

        return comboBox;
    }

    private JComponent getComponentPropiedadBooleana(Object value) {
        // //////////////////////////////
        // PropiedadBooleana
        JCheckBox checkBox = new JCheckBox();

        checkBox.setSelected(((PropiedadBooleana) value).getValor());
        checkBox.addActionListener(this);

        return checkBox;
    }

    private JTextField getTextField() {
        JTextField textField = new JTextField(propiedad.getValorString());

        textField.setBorder(BorderFactory.createEmptyBorder());

        return textField;
    }

    private JComponent getLabelAndButton() {
        JComponent ret = new JPanel(new BorderLayout());

        JLabel label = new JLabel(propiedad.getValorString());
        label.setForeground(Color.BLACK);

        ret.add(label, BorderLayout.CENTER);
        ret.add(getButton(), BorderLayout.EAST);

        return ret;
    }

    private JButton getButton(JComponent ret) {
        return (JButton) ret.getComponent(1);
    }

    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
    }

    /**
     * Se llama cuando se ha acabado de editar.
     */
    @Override
    protected void fireEditingStopped() {
        if (propiedad instanceof PropiedadBooleana) {
            propiedad.setValor(((JCheckBox) editorComponent).isSelected());

        } else if (propiedad instanceof PropiedadCombo) {
            JComboBox comboBox = (JComboBox) editorComponent;

            if (comboBox.getSelectedIndex() == -1) {
                propiedad.setValor(comboBox.getSelectedItem().toString());
            } else {
                propiedad.setValor(((PropiedadCombo) propiedad).getValor(comboBox.getSelectedIndex()));
            }

        } else if (propiedad instanceof PropiedadNumerica) {
            propiedad.setValor(((JSpinner) editorComponent).getValue());

        } else if (propiedad instanceof PropiedadListaString) {
            propiedad.setValorString(((JTextField) editorComponent.getComponent(
                    0)).getText());

        } else if (propiedad instanceof PropiedadLista) {
            // No hacer nada
        } else if (propiedad instanceof PropiedadMapa) {
            // No hacer nada
        } else if (propiedad instanceof PropiedadJavascript) {
            // No hacer nada
        } else if (propiedad instanceof PropiedadSimple) {
            propiedad.setValor(((JTextField) editorComponent).getText());

        } else if (propiedad instanceof PropiedadObjeto) {
            PropiedadObjeto propiedadObjeto = (PropiedadObjeto) propiedad;
            ArrayList<Class<?>> s = new ArrayList<Class<?>>(propiedadObjeto.getInstanceSubClasses());
            Class<?> clase = propiedadObjeto.getInstanceClass();

            if (!propiedadObjeto.getInstanceSubClasses().isEmpty()) {
                clase = s.get(((JComboBox) editorComponent.getComponent(0)).getSelectedIndex());
            }

            try {
                if (!propiedad.getValor().getClass().equals(clase)) {
                    propiedad.setValor(clase.getConstructor().newInstance());
                }
            } catch (Exception e) {
                Debug.error(e);
            }

        } else {
            Debug.warn("Tipo de propiedad no manejada: " + propiedad.getClass());
        }

        if (iFG.getSingleton().getPanelFilasElementos() != null) {
            iFG.getSingleton().getPanelFilasElementos().saveUndoState();
        }

        super.fireEditingStopped();
    }

}
