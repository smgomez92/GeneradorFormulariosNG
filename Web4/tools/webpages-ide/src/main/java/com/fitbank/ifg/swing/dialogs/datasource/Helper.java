package com.fitbank.ifg.swing.dialogs.datasource;

import java.util.Collection;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import com.fitbank.schemautils.Schema;
import com.fitbank.util.Clonador;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.util.ReferenceUtils;

/**
 * Ayudante para dialogos que manejan datasources y descripciones
 *
 * @author FitBank CI
 */
public class Helper {

    private final ReferenceUtils referenceUtils;

    private final Schema schema = Schema.get();

    private final DefaultComboBoxModel tablesComboBoxModel =
            new DefaultComboBoxModel(schema.getTables().keySet().toArray());

    private final DefaultComboBoxModel aliasComboBoxModel;

    public Helper(Collection<Reference> references) {
        this(new ReferenceUtils(references));
    }

    public Helper(ReferenceUtils referenceUtils) {
        this.referenceUtils = referenceUtils;
        this.aliasComboBoxModel = new DefaultComboBoxModel(this.referenceUtils.
                getAliasList().toArray());
    }

    public ReferenceUtils getReferenceUtils() {
        return referenceUtils;
    }

    public Schema getSchema() {
        return schema;
    }

    public DefaultComboBoxModel getTablesComboBoxModel() {
        return Clonador.clonar(tablesComboBoxModel);
    }

    public DefaultComboBoxModel getAliasComboBoxModel() {
        return Clonador.clonar(aliasComboBoxModel);
    }

    public void loadModel(JComboBox comboBox, ComboBoxModel model) {
        Object currentItem = comboBox.getSelectedItem();
        model.setSelectedItem(currentItem);
        comboBox.setModel(model);
        comboBox.setSelectedItem(currentItem);
    }

}
