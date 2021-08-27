package com.fitbank.ifg.swing.dialogs;

import java.util.Collection;
import java.util.LinkedList;

import javax.swing.JDialog;
import javax.swing.JTable;

import com.fitbank.ifg.iFG;
import com.fitbank.ifg.swing.tables.PropertiesTable;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadArchivo;
import com.fitbank.propiedades.PropiedadListener;
import com.fitbank.propiedades.PropiedadNumerica;
import com.fitbank.web.providers.HardDiskWebPageProvider;

public class PreferencesDialog extends EditorPropiedades {

    private static final long serialVersionUID = 1L;

    private static Collection<Propiedad<?>> getPreferencesData() {
        Collection<Propiedad<?>> propiedades = new LinkedList<Propiedad<?>>();

        PropiedadArchivo dirBase = new PropiedadArchivo();

        dirBase.setDescripcion("Directorio base");

        dirBase.setValor(HardDiskWebPageProvider.getBasePath());

        dirBase.addPropiedadListerner(new PropiedadListener<String>() {
            public void onChange(Propiedad<String> propiedad) {
                HardDiskWebPageProvider.setBasePath(propiedad.getValor());
            }
        });

        propiedades.add(dirBase);

        PropiedadArchivo dirTrans = new PropiedadArchivo();

        dirTrans.setDescripcion("Directorio transformación");
        dirTrans.setValor(HardDiskWebPageProvider.getHTMLPath());
        dirTrans.addPropiedadListerner(new PropiedadListener<String>() {
            public void onChange(Propiedad<String> propiedad) {
                HardDiskWebPageProvider.setHTMLPath(propiedad.getValor());             
            }
        }
        );

        propiedades.add(dirTrans);

        PropiedadNumerica<Integer> puerto = new PropiedadNumerica<Integer>(8082);

        puerto.setDescripcion("Puerto servidor");

        puerto.setValor(iFG.preferencias.getInt("port", 8082));

        puerto.addPropiedadListerner(new PropiedadListener<Integer>() {
            public void onChange(Propiedad<Integer> propiedad) {
                iFG.preferencias.putInt("port", propiedad.getValor());
            }
        });

        propiedades.add(puerto);

        return propiedades;
    }

    public PreferencesDialog(JDialog parent) {
        super(parent, "Preferencias", getPropertiesTable(parent));
        setLocationRelativeTo(null);
    }

    private static PropertiesTable getPropertiesTable(JDialog parent) {
        PropertiesTable propertiesTable = new PropertiesTable(parent,
                getPreferencesData());
        propertiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        return propertiesTable;
    }

}
