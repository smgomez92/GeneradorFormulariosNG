package com.fitbank.serializador;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ClasePruebaSerializacion {

    public enum EnumPrueba {

        UNO, DOS, TRES;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

    private String string = "string";

    private String nullString = null;

    private boolean boolPrimitive = true;

    private Boolean bool = true;

    private Boolean nullBool = null;

    private int intPrimitive = 1;

    private Integer integer = 1;

    private Integer nullInteger = null;

    private double dblPrimitive = 1.1;

    private Double dbl = 1.1;

    private Double nullDbl = null;

    private float fltPrimitive = 1.1f;

    private Float flt = 1.1f;

    private Float nullFlt = null;

    private Collection<String> stringCollection = new LinkedList<String>();

    private Collection<String> nullStringCollection = null;

    private Map<String, String> map = new HashMap<String, String>();

    private EnumPrueba enumPrueba = EnumPrueba.UNO;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String string2 = "string";

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String string3 = "string";

    public void setSoloSetter(String string2) {
        this.string2 = string2;
    }

    public String getSoloGetter() {
        return this.string2;
    }

    public String getStringTres() {
        return string3;
    }

    public void setStringTres(String string3) {
        this.string3 = string3;
    }

}
