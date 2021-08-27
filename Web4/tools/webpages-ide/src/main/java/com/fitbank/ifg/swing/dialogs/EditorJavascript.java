package com.fitbank.ifg.swing.dialogs;

import java.awt.Window;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.mozilla.javascript.EvaluatorException;

import com.fitbank.ifg.swing.dialogs.js.UtilsEditorJavascript;
import com.fitbank.js.JSParser;
import com.fitbank.js.JavascriptFormater;
import com.fitbank.js.LiteralJS;
import com.fitbank.js.NamedJSFunction;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadJavascript.Tipo;
import com.fitbank.util.Debug;

/**
 * Editor de JavaScript
 *
 * @author FitBank CI
 */
public class EditorJavascript extends javax.swing.JDialog {

    private static final String[] EVENTOS = {"change", "click",
        "dblclick", "focus", "keydown", "keypress",
        "keyup", "mousedown", "mousemove", "mouseout",
        "mouseover", "mouseup", "selectionChange"};

    private static final String[] FUNCIONES = {"c.formulario.preConsultar",
        "c.formulario.preMantener", "c.formulario.posMantener",
        "c.formulario.posConsultar", "c.formulario.ngOnInit", "c.formulario.codigoLibre"};

    private final PropiedadJavascript propiedad;

    private ComboBoxModel model = new DefaultComboBoxModel();

    private String text;

    private String currentName;

    private boolean validate = true;

    public EditorJavascript(Window parent, Tipo tipo) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.propiedad = new PropiedadJavascript(tipo);
        initComponents();
        UtilsEditorJavascript.initTextArea(code);
        setLocationRelativeTo(null);

        switch (tipo) {
            case SIMPLE:
                joinFreeCode.setVisible(false);
                items.setVisible(false);
                remove.setVisible(false);
                add.setVisible(false);
                break;

            case FUNCIONES:
                break;

            case EVENTOS:
                joinFreeCode.setVisible(false);
                break;

        }
    }

    protected boolean isSimple() {
        return propiedad.getTipo() == Tipo.SIMPLE;
    }

    protected boolean isEvents() {
        return propiedad.getTipo() == Tipo.EVENTOS;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        items = new javax.swing.JComboBox();
        remove = new javax.swing.JButton();
        add = new javax.swing.JButton();
        save = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        format = new javax.swing.JButton();
        joinFreeCode = new javax.swing.JButton();
        codeScroll = new org.fife.ui.rtextarea.RTextScrollPane();
        code = new org.fife.ui.rsyntaxtextarea.RSyntaxTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        items.setModel(model);
        items.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemsActionPerformed(evt);
            }
        });

        remove.setText("Borrar");
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        add.setText("Agregar...");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        save.setText("Guardar");
        save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveActionPerformed(evt);
            }
        });

        cancel.setText("Cancelar");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        format.setText("Formatear");
        format.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formatActionPerformed(evt);
            }
        });

        joinFreeCode.setText("Juntar código libre");
        joinFreeCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinFreeCodeActionPerformed(evt);
            }
        });

        code.setColumns(20);
        code.setRows(5);
        code.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        codeScroll.setViewportView(code);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(codeScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 730, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(items, 0, 592, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(remove)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(add))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(format)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(joinFreeCode)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 379, Short.MAX_VALUE)
                        .addComponent(cancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(save)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(items, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(add)
                    .addComponent(remove))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(codeScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(save)
                    .addComponent(cancel)
                    .addComponent(format)
                    .addComponent(joinFreeCode))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void setJSText(String text) {
        code.setText(JavascriptFormater.format(text));
        code.setCaretPosition(0);
    }

    /**
     * Muestra este editor y carga el texto indicado.
     *
     * @param text Texto a ser editado.
     *
     * @return Texto editado cuando se cierra el editor.
     */
    public String editar(String text) {
        this.text = text;
        propiedad.setValorString(text);

        if (isSimple()) {
            setJSText(text);
        } else {
            reloadItems();
            selectItem(null);
            loadSelected();
        }

        setVisible(true);

        return this.text;
    }

    /**
     * Carga el elemento actual seleccionado del combobox en el editor.
     */
    private void loadSelected() {
        currentName = getSelectedItem();

        if (StringUtils.isBlank(currentName)) {
            setJSText("");
            return;
        }

        if (isEvents()) {
            setJSText(StringUtils.defaultIfEmpty(propiedad.getEventos().
                    get(currentName), ""));

        } else {
            LiteralJS literalJS = propiedad.getFunciones().get(currentName);

            if (literalJS == null) {
                setJSText(new NamedJSFunction(currentName, "").toJS());
            } else {
                setJSText(literalJS.toJS());
            }
        }
    }

    /**
     * Recarga los items del combobox.
     */
    private void reloadItems() {
        reloadItems(null);
    }

    /**
     * Recarga los items del combobox y selecciona el item indicado.
     *
     * @param name Item a ser eleccionado.
     */
    private void reloadItems(String name) {
        Collection<String> newItems = Collections.EMPTY_LIST;

        switch (propiedad.getTipo()) {
            case SIMPLE:
                return;

            case FUNCIONES:
                newItems = getItems(FUNCIONES, propiedad.getFunciones().keySet(),
                        new LinkedHashSet<String>());
                break;

            case EVENTOS:
                newItems = getItems(EVENTOS, propiedad.getEventos().keySet(),
                        new TreeSet<String>());
                break;
        }

        String selected = getSelectedItem();
        int sel = items.getSelectedIndex();

        items.removeAllItems();

        for (String item : newItems) {

            items.addItem(item);
        }

        if (items.getItemCount() > sel) {
            items.setSelectedIndex(sel);
        }

        selectItem(StringUtils.defaultIfEmpty(name, selected));
        loadSelected();
    }

    private Collection<String> getItems(String[] baseItems,
            Collection<String> existing, Set<String> items) {
        items.addAll(Arrays.asList(baseItems));
        items.addAll(existing);

        return CollectionUtils.collect(items, new Transformer() {

            public Object transform(Object input) {
                boolean existe = false;

                if (isEvents()) {
                    existe = StringUtils.isNotBlank(propiedad.getEventos().get(
                            (String) input));
                } else {
                    LiteralJS literalJS1 = propiedad.getFunciones().get(
                            (String) input);

                    if (literalJS1 != null) {
                        existe = StringUtils.isNotBlank(literalJS1.getValor());
                    }
                }

                return input + (existe ? " *" : "");
            }

        });
    }

    /**
     * Selecciona un item en el combo.
     *
     * @param name Item a ser seleccionado.
     */
    private void selectItem(String name) {
        if (name == null && items.getModel().getSize() > 0) {
            items.setSelectedIndex(0);
        } else {
            items.setSelectedItem(name + " *");
            items.setSelectedItem(name);
        }
    }

    /**
     * Guarda el código del item actual en la propiedad. Esto puede generar
     * nuevas funciones por lo que recarga tambien los items.
     */
    private void saveCurrent() {
        if (!isSimple() && StringUtils.isBlank(currentName)) {
            Debug.debug("Nombre actual es nulo!");
            return;
        }

        String js = code.getText();        
        if (false) {
            try {
                JSParser.getRootNode(js);
            } catch (EvaluatorException e) {
                JOptionPane.showMessageDialog(this, "Error en el js: " + e.
                        getLocalizedMessage() + "\nEn: [" + e.lineNumber() + "] "
                        + e.lineSource());
                if (StringUtils.isNotBlank(currentName)) {
                    selectItem(currentName);
                }
                throw e;

            }
        }

        switch (propiedad.getTipo()) {
            case SIMPLE:
                propiedad.setValor(js);
                break;

            case FUNCIONES:
                propiedad.getFunciones().remove(currentName);
                propiedad.parseReplaceFunctions(js);
                currentName = "";
                reloadItems();
                break;

            case EVENTOS:
                propiedad.getEventos().put(currentName, js);
                break;
        }
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    /**
     * Obtiene el item actualmente seleccionado.
     *
     * @return Nombre del item seleccionado en el combobox.
     */
    private String getSelectedItem() {
        String selected = StringUtils.defaultIfEmpty(((String) items.
                getSelectedItem()), "");

        return selected.replaceAll(" \\*$", "");
    }

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Seguro desea borrar?")
                != JOptionPane.OK_OPTION) {
            return;
        }

        if (isEvents()) {
            propiedad.getEventos().remove(currentName);
        } else {
            propiedad.getFunciones().remove(currentName);
        }

        reloadItems();
    }//GEN-LAST:event_removeActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        String name = JOptionPane.showInputDialog(this, "Nombre:");

        if (StringUtils.isBlank(name)) {
            return;
        }

        saveCurrent();

        if (isEvents()) {
            if (!propiedad.getEventos().containsKey(name)) {
                propiedad.getEventos().put(name, "");
            }
        } else {
            if (!propiedad.getFunciones().containsKey(name)) {
                propiedad.getFunciones().put(name, new NamedJSFunction(name, ""));
            }
        }

        reloadItems(name);
    }//GEN-LAST:event_addActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelActionPerformed

    private void saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveActionPerformed
        saveCurrent();
        propiedad.limpiar();
        this.text = propiedad.getValorString();
        setVisible(false);
    }//GEN-LAST:event_saveActionPerformed

    private void itemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemsActionPerformed
        if (getSelectedItem().equals(currentName)) {
            return;
        }

        try {
            saveCurrent();
            loadSelected();
        } catch (EvaluatorException e) {
            Debug.error(e.getMessage());
        }
    }//GEN-LAST:event_itemsActionPerformed

    private void formatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formatActionPerformed
        setJSText(JavascriptFormater.format(code.getText()));
    }//GEN-LAST:event_formatActionPerformed

    private void joinFreeCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinFreeCodeActionPerformed
        saveCurrent();
        selectItem(null);
        loadSelected();
        propiedad.juntarCodigoLibre();
        reloadItems();
    }//GEN-LAST:event_joinFreeCodeActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JButton cancel;
    private org.fife.ui.rsyntaxtextarea.RSyntaxTextArea code;
    private org.fife.ui.rtextarea.RTextScrollPane codeScroll;
    private javax.swing.JButton format;
    private javax.swing.JComboBox items;
    private javax.swing.JButton joinFreeCode;
    private javax.swing.JButton remove;
    private javax.swing.JButton save;
    // End of variables declaration//GEN-END:variables

}
