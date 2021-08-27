package com.fitbank.web.detailreport;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import com.fitbank.dto.management.*;
import com.fitbank.serializador.html.ConstructorHtml;

public final class DetailToHtml {

    public static void addDetail(ConstructorHtml html, Detail detail) {
        if (detail.getTablesCount() > 0) {
            html.agregar("h3", "Tablas");

            for (Table table : detail.getTables()) {
                addTable(html, table);
            }
        }

        addControlFields(html, detail);
        addTransactionResponse(html, detail);
    }

    private static void addTable(ConstructorHtml html, Table table) {
        html.abrir("div");
        html.setAtributo("class", "table");

        html.agregar("h4", String.format("%s (%s)", table.getName(), table.getAlias()));

        addCriteria(html, table);
        addRecords(html, table);

        html.cerrar("div");
    }

    private static void addCriteria(ConstructorHtml html, Table table) {
        if (table.getCriteria().isEmpty()) {
            return;
        }

        html.agregar("h5", "Criterios");

        html.abrir("table");

        html.abrir("thead");
        html.abrir("tr");
        for (Criterion criterion : table.getCriteria()) {
            if (criterion.getValue() == null) {
                break;
            }
            html.agregar("td", criterion.getName());
        }
        html.cerrar("tr");
        html.cerrar("thead");

        html.abrir("tbody");
        html.abrir("tr");
        for (Criterion criterion : table.getCriteria()) {
            if (criterion.getValue() == null) {
                break;
            }
            html.agregar("td", String.valueOf(criterion.getValue()));
        }
        html.cerrar("tr");
        html.cerrar("tbody");

        html.cerrar("table");
    }

    private static void addRecords(ConstructorHtml html, Table table) {
        if (table.getRecordCount() == 0) {
            return;
        }

        html.agregar("h5", "Records");

        html.abrir("table");

        html.abrir("thead");
        html.abrir("tr");
        html.agregar("th", "#");
        for (Field field : table.getRecords().iterator().next().getFields()) {
            html.agregar("td", field.getName());
        }
        html.cerrar("tr");
        html.cerrar("thead");

        html.abrir("tbody");
        for (Record record : table.getRecords()) {
            html.abrir("tr");
            html.agregar("th", String.valueOf(record.getNumber()));
            for (Field field : record.getFields()) {
                addField(html, field);
            }
            html.cerrar("tr");
        }
        html.cerrar("tbody");

        html.cerrar("table");
    }

    private static void addField(ConstructorHtml html, Field field) {
        if (field.isChanged()) {
            if (field.getOldValue() == null) {
                html.agregar("td", String.valueOf(field.getValue()));
                html.setAtributo("class", "new");
            } else if (field.getValue() == null) {
                html.agregar("td", String.valueOf(field.getOldValue()));
                html.setAtributo("class", "removed");
            } else {
                html.agregar("td", field.getOldValue() + " → " + field.getValue());
                html.setAtributo("class", "changed");
            }
        } else {
            html.agregar("td", String.valueOf(field.getValue()));
        }
    }

    private static void addControlFields(ConstructorHtml html, Detail detail) {
        Collection<Field> fields = CollectionUtils.select((Collection) detail.getFields(), new Predicate() {

            @Override
            public boolean evaluate(Object object) {
                Field field = (Field) object;
                return !field.getName().startsWith("_") 
                        && !field.isTransportField()
                        && !field.getName().equals("FRM")
                        && !field.getName().equals("MENU");
            }

        });

        if (fields.isEmpty()) {
            return;
        }

        html.agregar("h3", "Campos de control");

        html.abrir("table");

        html.abrir("thead");
        html.abrir("tr");
        html.agregar("td", "Nombre");
        html.agregar("td", "Valor");
        html.cerrar("tr");
        html.cerrar("thead");

        html.abrir("tbody");
        for (Field field : fields) {
            html.abrir("tr");
            html.agregar("td", field.getName());
            addField(html, field);
            html.cerrar("tr");
        }
        html.cerrar("tbody");

        html.cerrar("table");
    }

    private static void addTransactionResponse(ConstructorHtml html, Detail detail) {
        if (detail.getResponse() == null || StringUtils.isBlank(detail.getResponse().getCode())) {
            return;
        }

        html.agregar("h3", "Respuesta: " + detail.getResponse().getCode());
        html.agregar("p", detail.getResponse().getUserMessage());
        html.agregar("p", detail.getResponse().getTechnicalMessage());
        html.agregar("pre", detail.getResponse().getStackTrace());

    }

}
