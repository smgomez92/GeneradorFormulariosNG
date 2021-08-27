package com.fitbank.ifg.swing.dialogs.datasource;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;

import com.fitbank.enums.DataSourceType;
import com.fitbank.ifg.swing.Boton;
import com.fitbank.schemautils.Table;
import com.fitbank.util.Clonador;
import com.fitbank.webpages.WebPage;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.Reference;

/**
 * Editor de DataSources
 *
 * @author FitBank CI
 */
public class DataSourceEditor extends javax.swing.JDialog {

    private DataSource dataSource;

    private Helper helper;

    public DataSourceEditor(JDialog parent) {
	super(parent, true);
	initComponents();
	dependencies.setEnableComparator(false);
    }

    public void load(WebPage webPage, DataSource dataSource) {
	helper = new Helper(webPage.getReferences());

	this.dataSource = Clonador.clonar(dataSource);

	alias.setSelectedItem(this.dataSource.getAlias());
	field.setSelectedItem(this.dataSource.getField());
	functionName.setSelectedItem(this.dataSource.getFunctionName());
	comparator.setSelectedItem(this.dataSource.getComparator());

	// Al setear type se llama a refresh
	type.setSelectedItem(this.dataSource.getType());
    }

    public DataSource getDataSource() {
	return dataSource;
    }

    private DataSource getExampleDS() {
	DataSource exampleDS = new DataSource();

	loadData(exampleDS);

	return exampleDS;
    }

    private void loadData(DataSource ds) {
	ds.setType((DataSourceType) type.getSelectedItem());
	ds.setAlias(String.valueOf(alias.getSelectedItem()));
	ds.setFunctionName(String.valueOf(functionName.getSelectedItem()));
	ds.setComparator(String.valueOf(comparator.getSelectedItem()));
	ds.setField(String.valueOf(field.getSelectedItem()));
    }

    private void refresh() {
	DataSource exampleDS = getExampleDS();

	alias.setVisible(!exampleDS.esControl());

	field.setEnabled(!exampleDS.estaVacio());

	functionName.setEnabled(exampleDS.getType() == DataSourceType.AGGREGATE);

	comparator.setEnabled(exampleDS.esCriterio());
	comparatorLabel.setEnabled(comparator.isEnabled());

	dependencies.setVisible(exampleDS.esDescripcion());

	dependencies.load(this.dataSource.getDependencies(),
		new Reference(exampleDS.getAlias(), exampleDS.getAlias()),
		helper, true);

	if (exampleDS.esDescripcion()) {
	    helper.loadModel(alias, helper.getTablesComboBoxModel());
	} else {
	    helper.loadModel(alias, helper.getAliasComboBoxModel());
	}

	pack();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dataSourcePanel = new javax.swing.JPanel();
        typeLabel = new javax.swing.JLabel();
        type = new javax.swing.JComboBox();
        alias = new javax.swing.JComboBox();
        fieldLabel = new javax.swing.JLabel();
        field = new javax.swing.JComboBox();
        comparatorLabel = new javax.swing.JLabel();
        comparator = new javax.swing.JComboBox();
        functionLabel = new javax.swing.JLabel();
        functionName = new javax.swing.JComboBox();
        buttonsPanel = new javax.swing.JPanel();
        ok = new javax.swing.JButton();
        cancel = new javax.swing.JButton();
        dependencies = new com.fitbank.ifg.swing.dialogs.datasource.Dependencies();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Editor DataSource");
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(400, 200));
        setModal(true);
        setName("Form"); // NOI18N

        dataSourcePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Origen de datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        dataSourcePanel.setName("dataSourcePanel"); // NOI18N

        typeLabel.setText("Tipo:");
        typeLabel.setName("typeLabel"); // NOI18N

        type.setModel(new DefaultComboBoxModel(DataSourceType.values()));
        type.setName("type"); // NOI18N
        type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeActionPerformed(evt);
            }
        });

        alias.setEditable(true);
        alias.setMinimumSize(new java.awt.Dimension(250, 29));
        alias.setName("alias"); // NOI18N
        alias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aliasActionPerformed(evt);
            }
        });

        fieldLabel.setText("Campo:");
        fieldLabel.setName("fieldLabel"); // NOI18N

        field.setEditable(true);
        field.setName("field"); // NOI18N

        comparatorLabel.setText("Comparador:");
        comparatorLabel.setName("comparatorLabel"); // NOI18N

        comparator.setEditable(true);
        comparator.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "=", ">", ">=", "<", "<=", "LIKE", "NOT LIKE", "IN ('a', 'b')", "NOT IN ('a', 'b')", "IN {campo}", "NOT IN {campo}" }));
        comparator.setName("comparator"); // NOI18N

        functionLabel.setText("Función:");
        functionLabel.setName("functionLabel"); // NOI18N

        functionName.setEditable(true);
        functionName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SUM", "MAX", "MIN", "AVG", "COUNT", "FIRST", "LAST" }));
        functionName.setName("functionName"); // NOI18N

        javax.swing.GroupLayout dataSourcePanelLayout = new javax.swing.GroupLayout(dataSourcePanel);
        dataSourcePanel.setLayout(dataSourcePanelLayout);
        dataSourcePanelLayout.setHorizontalGroup(
            dataSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataSourcePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dataSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(typeLabel)
                    .addComponent(fieldLabel)
                    .addComponent(comparatorLabel)
                    .addComponent(functionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(functionName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dataSourcePanelLayout.createSequentialGroup()
                        .addComponent(alias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(comparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(365, Short.MAX_VALUE))
        );
        dataSourcePanelLayout.setVerticalGroup(
            dataSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataSourcePanelLayout.createSequentialGroup()
                .addGroup(dataSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldLabel)
                    .addComponent(alias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(functionLabel)
                    .addComponent(functionName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataSourcePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comparatorLabel)
                    .addComponent(comparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        buttonsPanel.setName("buttonsPanel"); // NOI18N
        buttonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 5, 0));

        ok.setIcon(Boton.load("gtk-apply", "", false));
        ok.setText("Aceptar");
        ok.setName("ok"); // NOI18N
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });
        buttonsPanel.add(ok);

        cancel.setIcon(Boton.load("gtk-cancel", "", false));
        cancel.setText("Cancelar");
        cancel.setName("cancel"); // NOI18N
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });
        buttonsPanel.add(cancel);

        dependencies.setName("dependencies"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataSourcePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dependencies, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(7, 7, 7)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dataSourcePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dependencies, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeActionPerformed
	refresh();
    }//GEN-LAST:event_typeActionPerformed

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
	loadData(dataSource);
	dependencies.save();

	setVisible(false);
    }//GEN-LAST:event_okActionPerformed

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
	setVisible(false);
    }//GEN-LAST:event_cancelActionPerformed

    private void aliasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aliasActionPerformed
	DataSource exampleDS = getExampleDS();

	String tableName;
	if (exampleDS.esDescripcion()) {
	    tableName = exampleDS.getAlias();

	} else {
	    Reference reference = helper.getReferenceUtils().findReference(
		    exampleDS.getAlias());
	    if (reference != null) {
		tableName = reference.getTable();
	    } else {
		helper.loadModel(field, new DefaultComboBoxModel());
		return;
	    }
	}

	Table table = helper.getSchema().getTables().get(tableName);

	if (table == null) {
	    helper.loadModel(field, new DefaultComboBoxModel());
	    return;
	}

	helper.loadModel(field, new DefaultComboBoxModel(table.getFields().
		keySet().toArray()));

	if (exampleDS.esDescripcion()) {
	    dependencies.reload(new Reference(exampleDS.getAlias(), exampleDS.
		    getAlias()));
	}
    }//GEN-LAST:event_aliasActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox alias;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton cancel;
    private javax.swing.JComboBox comparator;
    private javax.swing.JLabel comparatorLabel;
    private javax.swing.JPanel dataSourcePanel;
    private com.fitbank.ifg.swing.dialogs.datasource.Dependencies dependencies;
    private javax.swing.JComboBox field;
    private javax.swing.JLabel fieldLabel;
    private javax.swing.JLabel functionLabel;
    private javax.swing.JComboBox functionName;
    private javax.swing.JButton ok;
    private javax.swing.JComboBox type;
    private javax.swing.JLabel typeLabel;
    // End of variables declaration//GEN-END:variables

}
