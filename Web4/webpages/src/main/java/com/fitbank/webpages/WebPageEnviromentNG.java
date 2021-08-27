/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fitbank.webpages;

import com.fitbank.enums.FormTypes;
import com.fitbank.js.JavascriptFormater;
import com.fitbank.util.Debug;
import com.fitbank.webpages.assistants.lov.LOVField;
import com.fitbank.webpages.data.Reference;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author santy
 */
public class WebPageEnviromentNG {

    private static String inicialTS = "import { Component, Input, DoCheck, AfterViewInit, OnChanges, OnInit } from '@angular/core';\n"
            + "import { FormControl, FormGroupDirective, NgForm, Validators, Form } from '@angular/forms';\n"
            + "import { ErrorStateMatcher } from '@angular/material/core';\n"
            + "import { internetComponent } from '../funciones/internet';\n"
            + "import { ApiService } from '../api.service';\n"
            + "import { Alerta } from '../funciones/alerta';\n"
            + "import { Router } from '@angular/router';\n"
            + "import { Servicios } from '../funciones/encryptar'\n"
            + "import { BaseDatos } from '../funciones/basededatos';\n"
            + "import { FormControlValidators } from '../funciones/formcontrol';\n"
            + "\n"
            + "${imports}\n"
            + "\n"
            + "\n"
            + "@Component({\n"
            + " selector: 'app-form{$component$}',\n"
            + " templateUrl: './form{$component$}.component.html',\n"
            + " styleUrls: ['./form{$component$}.component.css']\n"
            + "})\n"
            + "\n"
            + "\n"
            + "export class Form{$component$}Component ${implements} {\n"
            + "\n"
            + "@Input() idiomas: any;\n"
            + "@Input() grande: boolean;\n"
            + "@Input() mediano: boolean;\n"
            + "@Input() normal: boolean;\n"
            + "${exports}"
            + "\n"
            + "\n"
            + "constructor(\n"
            + "   private api: ApiService,\n"
            + "   public alerta: Alerta,\n"
            + "   private servicio: Servicios,\n"
            + "   private router: Router,\n"
            + "   private base: BaseDatos, public internet: internetComponent,  private validators: FormControlValidators) {{$ftc}\n{$lov}}\n";

    private static String finalTS = "logout() {\n"
            + "   let obj = {\n"
            + "     'salir': true\n"
            + "   }\n"
            + "   this.alerta.generarOfflineDialogo(obj, this.idiomas);\n"
            + " }\n"
            + "}";
    private static String showHideRows = "\n"
            + "showHideRows() {\n"
            + "    try {\n"
            + "      let classContainer1Width = document.getElementsByClassName('container2Aux')[0].clientWidth;\n"
            + "      if (this.grande) {\n"
            + "        document.getElementsByClassName('%htmlid%')[0].setAttribute('style', 'width: %valor_desde_generadorG%px'); \n"
            + "        if (%valor_desde_generadorG% - classContainer1Width > 0) { \n"
            + "          document.getElementById('%flechaR%').setAttribute('style', 'visibility: visible;');\n"
            + "          if (!this.%flechaL%) {\n"
            + "            document.getElementById('%flechaL%').setAttribute('style', 'visibility: visible;');\n"
            + "          }\n"
            + "        } else {\n"
            + "          document.getElementById('%flechaR%').setAttribute('style', 'visibility: hidden;');\n"
            + "          document.getElementById('%flechaL%').setAttribute('style', 'visibility: hidden;');\n"
            + "          document.getElementsByClassName('%htmlid%')[0].setAttribute('style', 'width: 100%');\n"
            + "        }\n"
            + "      }\n"
            + "      if (this.mediano) {\n"
            + "        document.getElementsByClassName('%htmlid%')[0].setAttribute('style', 'width: %valor_desde_generadorM%px');\n"
            + "        if (%valor_desde_generadorM% - classContainer1Width > 0) {\n"
            + "          document.getElementById('%flechaR%').setAttribute('style', 'visibility: visible;');\n"
            + "          if (!this.%flechaL%) {\n"
            + "            document.getElementById('%flechaL%').setAttribute('style', 'visibility: visible;');\n"
            + "          }\n"
            + "        } else {\n"
            + "          document.getElementById('%flechaR%').setAttribute('style', 'visibility: hidden;');\n"
            + "          document.getElementById('%flechaL%').setAttribute('style', 'visibility: hidden;');\n"
            + "          document.getElementsByClassName('%htmlid%')[0].setAttribute('style', 'width: 100%');\n"
            + "        }\n"
            + "      }\n"
            + "      if (this.normal) {\n"
            + "        document.getElementsByClassName('%htmlid%')[0].setAttribute('style', 'width: %valor_desde_generadorN%px; font-size: 14px');\n"
            + "        if (%valor_desde_generadorN% - classContainer1Width > 0) {\n"
            + "          document.getElementById('%flechaR%').setAttribute('style', 'visibility: visible;');\n"
            + "          if (!this.%flechaL%) {\n"
            + "            document.getElementById('%flechaL%').setAttribute('style', 'visibility: visible;');\n"
            + "          }\n"
            + "        } else {\n"
            + "          document.getElementById('%flechaR%').setAttribute('style', 'visibility: hidden;');\n"
            + "          document.getElementById('%flechaL%').setAttribute('style', 'visibility: hidden;');\n"
            + "          document.getElementsByClassName('%htmlid%')[0].setAttribute('style', 'width: 100%; font-size: 14px');\n"
            + "        }\n"
            + "      }\n"
            + "    } catch (e) { }\n"
            + "  }";
    private static String showScrollTo = "\n scrollTo(clase, direccion) {\n"
            + "  let tabla = document.getElementsByClassName(clase);\n"
            + "  let container1 = document.getElementsByClassName('container2Aux');\n"
            + "  if (direccion == 'right') {\n"
            + "    container1.item(0).scrollLeft = tabla[0].clientWidth - container1[0].clientWidth\n"
            + "  } else {\n"
            + "    container1.item(0).scrollLeft = 0\n"
            + "    this.%flechaR% = false;\n"
            + "    this.%flechaL% = true;\n"
            + "  }\n"
            + "}";
    private static String scroll = "\nscroll() {\n"
            + "    let container1 = document.getElementsByClassName('container2Aux')\n"
            + "    var tabla = document.getElementsByClassName('%htmlid%')\n"
            + "    if (container1.item(0).scrollLeft != 0) {\n"
            + "      if (container1[0].scrollLeft + 1 >= (tabla[0].clientWidth - container1[0].clientWidth)) {\n"
            + "        this.%flechaR% = true;\n"
            + "        this.%flechaL% = false;\n"
            + "      } else {\n"
            + "        this.%flechaR% = false;\n"
            + "        this.%flechaL% = false;\n"
            + "      }\n"
            + "    } else {\n"
            + "      this.%flechaR% = false;\n"
            + "      this.%flechaL% = true;\n"
            + "    }\n"
            + "\n"
            + "  }"
            + "\n";
    private static String showHideRowsTmp = "";
    private static String showScrollToTmp = "";
    private static String scrollTmp = "";
    private static String onInit = "";
    private static String customImports = "";
    private static String customExports = "";
    private static StringBuilder plantilla = new StringBuilder();
    private static StringBuilder variables = new StringBuilder();
    private static StringBuilder variablesF = new StringBuilder();
    private static StringBuilder events = new StringBuilder();
    private static String fileName = "";
    private static String component = "";
    private static String path = "";
    private static String listaLovs = "";
    private static StringBuilder freeTsCode = new StringBuilder();
    private static StringBuilder freeFunctions = new StringBuilder();

    private static final ThreadLocal<WebPageEnviromentNG> webPageEnviromentNg
            = new ThreadLocal<WebPageEnviromentNG>() {

        @Override
        protected WebPageEnviromentNG initialValue() {
            return new WebPageEnviromentNG();
        }

    };

    public static void addEventSelectionListOfValues(String eventName, Collection<LOVField> fields, String callBack, boolean callbackOnNoResults, String nombreLv) {
        String asignaciones = "";
        List<String> lsAsignaciones = new ArrayList<>();
        for (LOVField field : fields) {
            if (!field.getElementName().equals("") && !field.getElementName().equals(nombreLv)) {
                String asignacion = "this." + field.getElementName() + ".setValue(this." + nombreLv + ".value." + field.getField() + ");";
                lsAsignaciones.add(asignacion);
            }
        }
        asignaciones = StringUtils.join(lsAsignaciones, "\n");
        String nombreCallback = "";
        if (!StringUtils.isEmpty(callBack)) {
            nombreCallback = "callbackLv" + nombreLv + "()";
            WebPageEnviromentNG.addEventos(nombreCallback, callBack);
        }
        events.append(eventName).append("(){\n").append(asignaciones).append(StringUtils.isEmpty(nombreCallback) ? "" : "this.".concat(nombreCallback)).append("\n}\n");

    }

    public static void setImplemnts(String implemnts) {
        WebPageEnviromentNG.onInit = implemnts;
    }

    public static void addCustomImports(String imports) {
        customImports = customImports.concat("//Imports Personalizados\n");
        String imps[] = imports.split(";");
        for (String imp : imps) {
            customImports += imp.concat(";");
        }

    }

    public static void addCustomExports(String imports) {
        customExports = customExports.concat("//Exports Personalizados\n");
        String imps[] = imports.split(";");
        for (String imp : imps) {
            customExports += imp.concat(";");
        }

    }

    public static void addEventosConsultaLv(String nameEven, Collection<Reference> references, Collection<LOVField> fields, String nameList, Integer numReg, String subsystem, String transaction) {

        events.append(nameEven).append("(){\n");
        listaLovs = listaLovs.concat("\nthis." + nameEven + "();\n");
        String evento = "";
        evento = evento.concat("this.intento = true;\n");
        evento = evento.concat("let subsystem = '" + subsystem + "' ;\n");
        evento = evento.concat("let trx = '" + transaction + "' ;\n");
        List<String> lsTablas = new ArrayList<>();
        Integer contadorTablas = 0;
        for (Reference reference : references) {
            String tabla = "\"".concat(reference.getAlias()).concat("^").concat(reference.getTable()).concat("^\"+this.num").concat(nameList).concat("+\"^").concat("" + numReg).concat("\"");
            evento = evento.concat("let json" + contadorTablas + "Temp={};\n");
            String value = "json" + contadorTablas + "Temp";
            evento = evento.concat("json" + contadorTablas + "Temp[" + tabla + "]={");

            List<String> lsCampos = new ArrayList<>();
            for (LOVField field : fields) {
                if (field.getAlias().equals(reference.getAlias())) {
                    String campo = "\"".concat(field.getAlias()).concat("^").concat(field.getField());
                    switch (field.getType()) {
                        case CRITERION:
                            campo = campo.concat("^").concat(field.getComparator()).concat("^CRI").concat("^NORMAL").concat("\":").concat("this.").concat(field.getElementName()).concat("value");
                            break;
                        case ORDER:
                            campo = campo.concat("^").concat(field.getComparator()).concat("^CRI").concat("^ORDER").concat("^").concat(field.getValue()).concat("\":").concat("\"null\"");
                            break;
                        case RECORD:
                            campo = campo.concat("^REC").concat("^0").concat("\":").concat("\"null\"");
                            break;
                        default:
                            break;
                    }
                    lsCampos.add(campo);
                }
            }
            evento = evento.concat(StringUtils.join(lsCampos, ",")).concat("};\n");
            lsTablas.add(value);
            contadorTablas++;
        }

        evento = evento.concat("let pedido={");
        evento = evento.concat("\"sub\":subsystem,");
        evento = evento.concat("\"trx\":trx,");
        evento = evento.concat("\"sid\":this.base.id_token,");
        String tablas = "\"TBLS\":";

        tablas = tablas.concat(StringUtils.join(lsTablas, ","));
        evento = evento.concat(tablas).concat("};\n");
        evento = evento.concat("this.api.postProvider2InternetCheck('/consulta_generica',this.base.id_token, pedido).then((data: any) => {\n"
                + "      this.alerta.presentarAlerta(this.idiomas.TransaccionE)\n"
                + "      this.intento = false;\n");
        evento = evento.concat("this.").concat(nameList).concat("=data;\n");
        evento = evento.concat("this.").concat(nameList + "2").concat("=").concat("this.").concat(nameList).concat("['").concat(fields.iterator().next().getAlias()).concat("'];\n}");
        evento = evento.concat(", (err) => {\n"
                + "      if (err.status != 0 && err.status != 504 && err.status != -1) {  //Con Internet\n"
                + "        console.log(err)\n"
                + "        this.intento = false;\n"
                + "        if (err.error) {\n"
                + "          if (err.error.mensaje == \"Error de autenticación via token JWT.\") { this.logout() }\n"
                + "        }\n"
                + "        else\n"
                + "          this.alerta.presentarAlerta(this.idiomas.ServidorError)\n"
                + "      }\n"
                + "      else { //Sin Internet\n"
                + "        // this.consultarOffline();\n"
                + "      }\n"
                + "    });");
        events.append(evento).append("}");

    }

    public static void addEmptyArrayForTable(String tableDS, List<String> listaColumnas, int clonacionMax) {
        String evento = "this." + tableDS + "=[$DS$];\n";
        String elementosJson = "";
        elementosJson = listaColumnas.stream().map((columna) -> columna + ":'',").reduce(elementosJson, String::concat);
        String json = "{" + elementosJson.substring(0, elementosJson.length() - 1) + "},", tmpJson = "";
        for (int i = 0; i < clonacionMax; i++) {
            tmpJson += json;
        }
        freeTsCode.append(evento.replace("$DS$", tmpJson.substring(0, tmpJson.length() - 1))).append("\n");
    }

    /**
     * Constructor por defecto.
     */
    public WebPageEnviromentNG() {
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        WebPageEnviromentNG.path = path;
    }

    public static void addName(String trx, String subsistema) {
        fileName = String.format("%s%s.component.ts", subsistema, trx);
        addVariablesWithValue("subsistema", "'" + subsistema + "'");
        addVariablesWithValue("transaccion", "'" + trx + "'");
        component = subsistema + trx;

    }

    //nombres de las columnas
    public static void addPlantillaColumnas(List<Widget> widgets, String nameColumns) {
        String lista = "[";

        for (int i = 0; i < widgets.size() - 1; i++) {
            lista = lista + "'" + (widgets.get(i).getTexto().equals("") ? widgets.get(i).getHTMLId() : widgets.get(i).getTexto()) + "'" + " , ";
        }
        lista = lista + "'" + (widgets.get(widgets.size() - 1).getTexto().equals("") ? widgets.get(widgets.size() - 1).getHTMLId() : widgets.get(widgets.size() - 1).getTexto()) + "'];";
        plantilla.append(nameColumns).append(": string[] =").append(lista);

    }

    public static void addVariables(String variablesV) {

        variables.append(variablesV).append(": any; \n");

    }

    public static void addVariablesWithValue(String variablesV, String value) {

        variables.append(variablesV).append(" = ").append(value).append("; \n");

    }

    public static void addVariablesWithType(String variablesV, String type) {

        variables.append(variablesV).append(":").append(type).append("; \n");
        addFunctions("this." + variablesV + " = false;//agregado desde addVariable\n");
    }

    public static void addVariablesWithTypeAndValue(String variablesN, String type, String value) {
        variables.append(variablesN).append(":").append(type).append("; \n");
        addFunctions("this." + variablesN + " = " + value + ";\n");
    }

    public static void addEventos(String functionName, String event) {
        event = functionName.contains("$event") ? "$event.preventDefault();\n" + event : event;
        //arreglar por eventos 
        events.append(functionName).append("{\n").append(event).append("}\n");
    }

    /**
     * Metodo que añade funciones libres
     *
     * @param pString
     */
    /**
     *
     * TODO: revisar o mejorar para cuando sean funciones con parametros
     *
     * @param pString
     */
    public static void addFunctions(String pString) {
        if (pString == null) {
            return;
        }
        freeFunctions.append("\n");
        String functions[] = pString.split("c.formulario.");
        for (String function : functions) {
            if (!function.startsWith("c.") && -1 == function.indexOf("=function()")) {
                freeTsCode.append(function).append("\n");
            } else if (function.toLowerCase().contains("codigolibre")) {
                // int end = function.indexOf("=function()");
                int parentesis = function.indexOf("(){");
                function = function.substring(parentesis + 3, function.length() - 2);//codigoLibre=function(){codigo}
                freeTsCode.append(function).append("\n");
            } else {
                int end = function.indexOf("=function()");
                int parentesis = function.indexOf("()");
                if (function.startsWith("c.")) {
                    function = function.substring(13, end) + function.substring(parentesis);
                    //freeFunctions.append(function.concat("\n")).append("\n");
                    freeFunctions.append(function).append("\n");
                } else {
                    function = function.substring(0, end) + function.substring(parentesis);
                    //freeFunctions.append(function.concat("\n").concat("\n")).append("\n");
                    freeFunctions.append(function).append("\n");

                }
            }
        }

    }

    public static void addVariablesForm(String variablesFr, FormTypes variablesT) {

        variablesF.append(variablesFr).append("= this.validators.").append(variablesT.getMetodo()).append(";\n");
    }

    public static void addEventoShowHide(Integer grande, Integer mediano, Integer normal, String idContainer, String htmlId) {
        showHideRowsTmp = showHideRows.replace("%valor_desde_generadorG%", "" + grande)
                .replace("%valor_desde_generadorM%", "" + mediano)
                .replace("%valor_desde_generadorN%", "" + normal)
                .replace("%idContainer%", idContainer)
                .replace("%htmlid%", "tabla_" + htmlId)
                .replace("%flechaL%", "flechaL_" + htmlId)
                .replace("%flechaR%", "flechaR_" + htmlId);

    }

    public static void addEventoScrollTo(String idContainer, String htmlId) {
        showScrollToTmp = showScrollTo.replace("%idContainer%", idContainer)
                .replace("%flechaL%", "flechaL_" + htmlId)
                .replace("%flechaR%", "flechaR_" + htmlId);

    }

    public static void addEventoScroll(String idContainer, String htmlId) {
        scrollTmp = scroll.replace("%idContainer%", idContainer)
                .replace("%htmlid%", "tabla_" + htmlId)
                .replace("%flechaL%", "flechaL_" + htmlId)
                .replace("%flechaR%", "flechaR_" + htmlId);

    }

    public static void saveFile(String subsistema, String trx) {

        try {

            // File file = new File("D:\\GeneradorNG\\pwa_base_pruebas\\src\\apaddp\\form000001\\form" + fileName);
            Debug.info(WebPageEnviromentNG.getPath() + "\\form" + subsistema + trx + "\\form" + fileName);
            File file = new File(WebPageEnviromentNG.getPath() + "\\form" + subsistema + trx + "\\form" + fileName);
            try ( //File file = new File("C:\\Users\\santy\\OneDrive\\Documentos\\Fitbank Repository\\PWA CAJAS\\src\\app\\form036401\\form" + fileName);
                    FileWriter fileWriter = new FileWriter(file, false);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                bufferedWriter.append(inicialTS.replace("{$component$}", component)
                        .replace("{$lov}", listaLovs)
                        .replace("{$ftc}", freeTsCode.toString())
                        .replace("${implements}", onInit)
                        .replace("${imports}", customImports)
                        .replace("${exports}", customExports));
                String plantillaFormat = JavascriptFormater.format(plantilla.toString());
                bufferedWriter.append(plantillaFormat);
                String variablesFormat = JavascriptFormater.format(variables.toString());
                bufferedWriter.append(variablesFormat);
                String variablesfFormat = JavascriptFormater.format(variablesF.toString());
                bufferedWriter.append(variablesfFormat);
                String eventsFormat = JavascriptFormater.format(events.toString());
                bufferedWriter.append(events.toString());
                bufferedWriter.append(freeFunctions);
                bufferedWriter.append(showHideRowsTmp);
                bufferedWriter.append(showScrollToTmp);
                bufferedWriter.append(scrollTmp);
                bufferedWriter.append(finalTS);

            }

        } catch (IOException e) {
            Debug.error(e);
        }
    }

    public static void clear() {
        events = new StringBuilder("");
        variables = new StringBuilder("");
        variablesF = new StringBuilder("");
        listaLovs = "";
        freeTsCode = new StringBuilder("");
        freeFunctions = new StringBuilder("");
        plantilla = new StringBuilder("");
        showHideRowsTmp = "";
        showScrollToTmp = "";
        scrollTmp = "";
        onInit = "";
        customImports = "";
        customExports = "";
    }
}
