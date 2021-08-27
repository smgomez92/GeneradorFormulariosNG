package com.fitbank.web.editor;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fitbank.enums.AttachedPosition;
import com.fitbank.enums.DataSourceType;
import com.fitbank.enums.DependencyType;
import com.fitbank.enums.JoinType;
import com.fitbank.enums.Modificable;
import com.fitbank.enums.OrientacionTabs;
import com.fitbank.enums.PosicionHorizontal;
import com.fitbank.enums.PosicionTexto;
import com.fitbank.enums.Requerido;
import com.fitbank.js.EventFunction;
import com.fitbank.js.FuncionJS;
import com.fitbank.js.LiteralJS;
import com.fitbank.propiedades.ListaLimitada;
import com.fitbank.propiedades.Propiedad;
import com.fitbank.propiedades.PropiedadJavascript;
import com.fitbank.propiedades.PropiedadSerializable;
import com.fitbank.scanner.ScannerPages;
import com.fitbank.scanner.ScannerType;
import com.fitbank.scanner.ScanningJob;
import com.fitbank.serializador.xml.SerializableXmlBean;
import com.fitbank.util.Servicios;
import com.fitbank.web.js.JSClasses;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.AttachedWebPage;
import com.fitbank.webpages.Formatter;
import com.fitbank.webpages.JSBehavior;
import com.fitbank.webpages.Widget;
import com.fitbank.webpages.assistants.PlainText;
import com.fitbank.webpages.behaviors.DigitValidator;
import com.fitbank.webpages.data.DataSource;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.FieldData;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.definition.Field;
import com.fitbank.webpages.definition.Group;
import com.fitbank.webpages.definition.WebPageDefinition;
import com.fitbank.webpages.definition.group.TableGroup.TableGroupStyle;
import com.fitbank.webpages.definition.wizard.WizardCriterion;
import com.fitbank.webpages.definition.wizard.WizardData;
import com.fitbank.webpages.definition.wizard.WizardField;
import com.fitbank.webpages.formatters.DateFormatter;

public class EditorJSClasses implements JSClasses {

    public Set<Class<?>> getFullClasses() {
        Set<Class<?>> clases = new LinkedHashSet<Class<?>>();

        // //////////////////////////////
        // Enums
        clases.add(JoinType.class);
        clases.add(EditorRequestTypes.class);
        clases.add(DataSourceType.class);
        clases.add(DependencyType.class);
        clases.add(OrientacionTabs.class);
        clases.add(Modificable.class);
        clases.add(PosicionHorizontal.class);
        clases.add(PosicionTexto.class);
        clases.add(Requerido.class);

        // //////////////////////////////
        // Javascript
        clases.add(LiteralJS.class);
        clases.add(FuncionJS.class);
        clases.add(EventFunction.class);
        clases.add(Assistant.class);
        clases.add(PlainText.class);

        // //////////////////////////////
        // WebPage
        clases.add(AttachedPosition.class);
        clases.add(AttachedWebPage.class);

        // //////////////////////////////
        // Wizard
        clases.add(WizardData.class);
        clases.add(WizardField.class);
        clases.add(WizardCriterion.class);

        // //////////////////////////////
        // WebPageDefinition
        clases.add(DataSource.class);
        clases.add(Field.class);
        clases.add(Dependency.class);
        clases.add(Reference.class);
        clases.add(Group.class);
        clases.add(WebPageDefinition.class);
        clases.add(TableGroupStyle.class);
        for (Class<?> clase : Servicios.loadClasses(Group.class)) {
            clases.add(clase);
        }

        // //////////////////////////////
        // Propiedades
        clases.add(ListaLimitada.class);
        clases.add(PropiedadJavascript.Tipo.class);
        for (Class<?> clase : Servicios.loadClasses(Propiedad.class)) {
            clases.add(clase);
        }

        // ///////////////////////
        // Escaneo
        clases.add(ScannerType.class);
        clases.add(ScannerPages.class);
        clases.add(ScanningJob.class);

        // //////////////////////////////
        // Widgets
        clases.add(JSBehavior.class);
        clases.add(Formatter.class);
        clases.add(DateFormatter.DateFormat.class);
        clases.add(DateFormatter.TransportDateFormat.class);
        clases.add(DigitValidator.ValidationTypes.class);
        for (Class<?> clase : Servicios.loadClasses(JSBehavior.class)) {
            clases.add(clase);
        }
        clases.add(Assistant.class);
        for (Class<?> clase : Servicios.loadClasses(Assistant.class)) {
            clases.add(clase);
        }
        clases.add(FieldData.class);
        clases.add(Widget.class);
        for (Class<?> clase : Servicios.loadClasses(Widget.class)) {
            clases.add(clase);
        }

        return clases;
    }

    public Set<Class<?>> getSimpleClasses() {
        Set<Class<?>> clases = new LinkedHashSet<Class<?>>();

        clases.add(PropiedadSerializable.class);
        clases.add(SerializableXmlBean.class);

        return clases;
    }

}
