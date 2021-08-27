package com.fitbank.ifg.swing.dialogs.datasource;

import com.fitbank.schemautils.Table;
import java.util.Collection;

import javax.swing.DefaultComboBoxModel;

import org.apache.commons.lang.StringUtils;

import com.fitbank.enums.DependencyType;
import com.fitbank.ifg.swing.Boton;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.Reference;
import java.awt.Component;
import javax.swing.SwingUtilities;

/**
 * Panel para editar dependencias
 *
 * @author FitBank CI
 */
public class Dependencies extends javax.swing.JPanel {

    private Helper helper;

    private Collection<Dependency> dependencies;

    private Dependency dependency;

    private boolean description;

    private boolean enableComparator = true;

    public Dependencies() {
        initComponents();
    }

    void setEnableComparator(boolean enableComparator) {
        this.enableComparator = enableComparator;
    }

    public Collection<Dependency> getDependencies() {
        return dependencies;
    }

    public void load(Collection<Dependency> dependencies, Reference reference,
            Helper helper, boolean description) {
        this.dependencies = dependencies;
        this.helper = helper;
        this.description = description;

        helper.loadModel(fromAlias, helper.getAliasComboBoxModel());

        reload(reference);
    }

    public void reload(Reference reference) {
        alias.setText(reference.getAlias());

        Table table = helper.getSchema().getTables().get(reference.getTable());

        if (table == null) {
            helper.loadModel(field, new DefaultComboBoxModel());
        } else {
            helper.loadModel(field, new DefaultComboBoxModel(table.getFields().
                    keySet().toArray()));
        }

        refresh();
        load(null);
    }

    private void refresh() {
        dependencyList.getSelectionModel().clearSelection();
        dependencyList.setModel(new DefaultComboBoxModel(dependencies.toArray()));
    }

    private void refreshDependency() {
        for (Component component : dependencyPanel.getComponents()) {
            component.setEnabled(true);
        }

        type.setEnabled(!description);
        typeLabel.setEnabled(!description);

        boolean imm = type.getSelectedItem() == DependencyType.IMMEDIATE;

        radioImmediateValue.setEnabled(imm);

        if (!imm) {
            radioFromField.setSelected(true);
        }

        immediateValue.setEnabled(radioImmediateValue.isSelected());

        fromAlias.setEnabled(radioFromField.isSelected());
        fromField.setEnabled(radioFromField.isSelected());

        comparator.setEnabled(enableComparator);
        comparatorLabel.setEnabled(enableComparator);
    }

    private void load(Dependency dependency) {
        if (this.dependency != null) {
            save(this.dependency);
        }

        boolean disable = false;
        this.dependency = null;

        if (dependency == null) {
            dependency = new Dependency();
            disable = true;
        }

        type.setSelectedItem(dependency.getType());
        field.setSelectedItem(dependency.getField());
        comparator.setSelectedItem(dependency.getComparator());
        immediateValue.setText(dependency.getImmediateValue());
        fromField.setSelectedItem(dependency.getFromField());
        fromAlias.setSelectedItem(dependency.getFromAlias());

        if (StringUtils.isNotBlank(immediateValue.getText())) {
            radioImmediateValue.setSelected(true);
        } else {
            radioFromField.setSelected(true);
        }

        refreshDependency();

        this.dependency = dependency;

        if (disable) {
            for (Component component : dependencyPanel.getComponents()) {
                component.setEnabled(false);
            }
        }
    }

    public void save() {
        if (this.dependency != null) {
            save(this.dependency);
        }
        SwingUtilities.updateComponentTreeUI(dependencyList);
    }

    private void save(Dependency dependency) {
        dependency.setType((DependencyType) type.getSelectedItem());
        dependency.setField((String) field.getSelectedItem());
        dependency.setComparator((String) comparator.getSelectedItem());
        dependency.setFromAlias((String) fromAlias.getSelectedItem());
        dependency.setFromField((String) fromField.getSelectedItem());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dependencyButtonGroup = new javax.swing.ButtonGroup();
        dependenciesPanel = new javax.swing.JPanel();
        dependencyListScrollPane = new javax.swing.JScrollPane();
        dependencyList = new javax.swing.JList();
        add = new javax.swing.JButton();
        wizard = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        dependencyPanel = new javax.swing.JPanel();
        fromAlias = new javax.swing.JComboBox();
        fromField = new javax.swing.JComboBox();
        aliasLabel = new javax.swing.JLabel();
        radioImmediateValue = new javax.swing.JRadioButton();
        typeLabel = new javax.swing.JLabel();
        comparator = new javax.swing.JComboBox();
        type = new javax.swing.JComboBox();
        comparatorLabel = new javax.swing.JLabel();
        radioFromField = new javax.swing.JRadioButton();
        field = new javax.swing.JComboBox();
        immediateValue = new javax.swing.JTextField();
        alias = new javax.swing.JLabel();

        dependenciesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Dependencias", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        dependencyList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        dependencyList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                dependencyListValueChanged(evt);
            }
        });
        dependencyListScrollPane.setViewportView(dependencyList);

        add.setIcon(Boton.load("gtk-add", "actions/list-add.png", false));
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });

        wizard.setIcon(Boton.load("gtk-new", "actions/window-new.png", false));
        wizard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wizardActionPerformed(evt);
            }
        });

        remove.setIcon(Boton.load("gtk-remove", "actions/list-remove.png", false));
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        fromAlias.setEditable(true);
        fromAlias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromAliasActionPerformed(evt);
            }
        });

        fromField.setEditable(true);
        fromField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fromFieldActionPerformed(evt);
            }
        });

        aliasLabel.setText("Campo:");

        dependencyButtonGroup.add(radioImmediateValue);
        radioImmediateValue.setText("Valor Inmediato:");
        radioImmediateValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioImmediateValueActionPerformed(evt);
            }
        });

        typeLabel.setText("Tipo:");

        comparator.setEditable(true);
        comparator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">", ">=", "<", "<=", "LIKE", "NOT LIKE", "IN ('a', 'b')", "NOT IN ('a', 'b')", "IN {campo}", "NOT IN {campo}" }));
        comparator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comparatorActionPerformed(evt);
            }
        });

        type.setModel(new DefaultComboBoxModel(DependencyType.values()));
        type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeActionPerformed(evt);
            }
        });

        comparatorLabel.setText("Comparador:");

        dependencyButtonGroup.add(radioFromField);
        radioFromField.setText("Otro campo");
        radioFromField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioFromFieldActionPerformed(evt);
            }
        });

        field.setEditable(true);
        field.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldActionPerformed(evt);
            }
        });

        immediateValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                immediateValueActionPerformed(evt);
            }
        });

        alias.setText("XXXXXX");

        javax.swing.GroupLayout dependencyPanelLayout = new javax.swing.GroupLayout(dependencyPanel);
        dependencyPanel.setLayout(dependencyPanelLayout);
        dependencyPanelLayout.setHorizontalGroup(
            dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dependencyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(dependencyPanelLayout.createSequentialGroup()
                        .addGroup(dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comparatorLabel)
                            .addComponent(typeLabel)
                            .addComponent(aliasLabel)
                            .addComponent(radioFromField))
                        .addGap(40, 40, 40))
                    .addGroup(dependencyPanelLayout.createSequentialGroup()
                        .addComponent(radioImmediateValue)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dependencyPanelLayout.createSequentialGroup()
                        .addComponent(alias)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(comparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dependencyPanelLayout.createSequentialGroup()
                        .addComponent(fromAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fromField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(immediateValue, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        dependencyPanelLayout.setVerticalGroup(
            dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dependencyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aliasLabel)
                    .addComponent(alias)
                    .addComponent(field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comparatorLabel)
                    .addComponent(comparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioFromField)
                    .addComponent(fromAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fromField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dependencyPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(radioImmediateValue)
                    .addComponent(immediateValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dependenciesPanelLayout = new javax.swing.GroupLayout(dependenciesPanel);
        dependenciesPanel.setLayout(dependenciesPanelLayout);
        dependenciesPanelLayout.setHorizontalGroup(
            dependenciesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dependenciesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dependenciesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dependencyListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
                    .addGroup(dependenciesPanelLayout.createSequentialGroup()
                        .addComponent(wizard)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(add)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(remove)
                        .addGap(139, 139, 139)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dependencyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        dependenciesPanelLayout.setVerticalGroup(
            dependenciesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dependenciesPanelLayout.createSequentialGroup()
                .addComponent(dependencyListScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dependenciesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(wizard)
                    .addComponent(add)
                    .addComponent(remove)))
            .addGroup(dependenciesPanelLayout.createSequentialGroup()
                .addComponent(dependencyPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(187, 187, 187))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dependenciesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dependenciesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void dependencyListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_dependencyListValueChanged
        load((Dependency) dependencyList.getSelectedValue());
    }//GEN-LAST:event_dependencyListValueChanged

    private void radioImmediateValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioImmediateValueActionPerformed
        refreshDependency();
    }//GEN-LAST:event_radioImmediateValueActionPerformed

    private void radioFromFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioFromFieldActionPerformed
        refreshDependency();
    }//GEN-LAST:event_radioFromFieldActionPerformed

    private void fromAliasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromAliasActionPerformed
        Reference reference = helper.getReferenceUtils().findReference(
                (String) fromAlias.getSelectedItem());

        if (reference == null) {
            helper.loadModel(fromField, new DefaultComboBoxModel());
            return;
        }

        Table table = helper.getSchema().getTables().get(reference.getTable());

        if (table == null) {
            helper.loadModel(fromField, new DefaultComboBoxModel());
            return;
        }

        helper.loadModel(fromField, new DefaultComboBoxModel(table.getFields().
                keySet().toArray()));

        save();
    }//GEN-LAST:event_fromAliasActionPerformed

    private void wizardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wizardActionPerformed
    }//GEN-LAST:event_wizardActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        Dependency newDependency = new Dependency();
        dependencies.add(newDependency);
        refresh();
        dependencyList.setSelectedValue(newDependency, true);
    }//GEN-LAST:event_addActionPerformed

    private void removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeActionPerformed
        dependencies.remove((Dependency) dependencyList.getSelectedValue());
        refresh();
        dependencyList.getSelectionModel().clearSelection();
    }//GEN-LAST:event_removeActionPerformed

    private void typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeActionPerformed
        refreshDependency();
        save();
    }//GEN-LAST:event_typeActionPerformed

    private void fieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldActionPerformed
        save();
    }//GEN-LAST:event_fieldActionPerformed

    private void comparatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comparatorActionPerformed
        save();
    }//GEN-LAST:event_comparatorActionPerformed

    private void fromFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fromFieldActionPerformed
        save();
    }//GEN-LAST:event_fromFieldActionPerformed

    private void immediateValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_immediateValueActionPerformed
        save();
    }//GEN-LAST:event_immediateValueActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JLabel alias;
    private javax.swing.JLabel aliasLabel;
    private javax.swing.JComboBox comparator;
    private javax.swing.JLabel comparatorLabel;
    private javax.swing.JPanel dependenciesPanel;
    private javax.swing.ButtonGroup dependencyButtonGroup;
    private javax.swing.JList dependencyList;
    private javax.swing.JScrollPane dependencyListScrollPane;
    private javax.swing.JPanel dependencyPanel;
    private javax.swing.JComboBox field;
    private javax.swing.JComboBox fromAlias;
    private javax.swing.JComboBox fromField;
    private javax.swing.JTextField immediateValue;
    private javax.swing.JRadioButton radioFromField;
    private javax.swing.JRadioButton radioImmediateValue;
    private javax.swing.JButton remove;
    private javax.swing.JComboBox type;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JButton wizard;
    // End of variables declaration//GEN-END:variables

}
