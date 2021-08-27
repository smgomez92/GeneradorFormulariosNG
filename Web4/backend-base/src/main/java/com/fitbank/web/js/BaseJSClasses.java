package com.fitbank.web.js;

import java.util.LinkedHashSet;
import java.util.Set;

import com.fitbank.enums.AttachedPosition;
import com.fitbank.enums.DataSourceType;
import com.fitbank.enums.DependencyType;
import com.fitbank.enums.JoinType;
import com.fitbank.enums.MessageType;
import com.fitbank.enums.Paginacion;
import com.fitbank.enums.TipoImagen;
import com.fitbank.enums.TipoMenu;
import com.fitbank.enums.EjecutadoPor;
import com.fitbank.scanner.ScannerPages;
import com.fitbank.scanner.ScannerType;
import com.fitbank.scanner.ScanningJob;
import com.fitbank.util.Servicios;
import com.fitbank.web.GeneralRequestTypes;
import com.fitbank.web.data.Notification;
import com.fitbank.web.data.NotificationItem;
import com.fitbank.webpages.Assistant;
import com.fitbank.webpages.JSBehavior;
import com.fitbank.webpages.assistants.lov.LOVField;
import com.fitbank.webpages.behaviors.DigitValidator;
import com.fitbank.webpages.data.Dependency;
import com.fitbank.webpages.data.Reference;
import com.fitbank.webpages.formatters.DateFormatter;

public class BaseJSClasses implements JSClasses {

    @Override
    public Set<Class<?>> getFullClasses() {
        Set<Class<?>> clases = new LinkedHashSet<Class<?>>();

        // ///////////////////////
        // Enums
        clases.add(TipoImagen.class);
        clases.add(DependencyType.class);
        clases.add(TipoMenu.class);
        clases.add(GeneralRequestTypes.class);
        clases.add(AttachedPosition.class);
        clases.add(MessageType.class);
        clases.add(DateFormatter.DateFormat.class);
        clases.add(DateFormatter.TransportDateFormat.class);
        clases.add(Paginacion.class);
        clases.add(DigitValidator.ValidationTypes.class);
        clases.add(JoinType.class);
        clases.add(EjecutadoPor.class);

        // ///////////////////////
        // Escaneo
        clases.add(ScannerType.class);
        clases.add(ScannerPages.class);
        clases.add(ScanningJob.class);

        // ///////////////////////
        // Comportamientos JS
        for (Class<?> clase : Servicios.loadClasses(JSBehavior.class)) {
            clases.add(clase);
        }

        // ///////////////////////
        // Para listas valores
        clases.add(DataSourceType.class);
        clases.add(LOVField.class);
        clases.add(Dependency.class);
        clases.add(Reference.class);

        // ///////////////////////
        // Asistentes
        for (Class<?> clase : Servicios.loadClasses(Assistant.class)) {
            clases.add(clase);
        }

        // ///////////////////////
        // Notificaciones
        clases.add(Notification.class);
        clases.add(NotificationItem.class);

        return clases;
    }

    @Override
    public Set<Class<?>> getSimpleClasses() {
        return new LinkedHashSet<Class<?>>();
    }

}
