package com.fitbank.serializador.xml;

import org.w3c.dom.Document;

import junit.framework.TestCase;

import com.fitbank.serializador.ClasePruebaSerializacion;

public class TestSerializadorXml extends TestCase {

    private ClasePruebaSerializacion prueba;

    @Override
    protected void setUp() throws Exception {
        prueba = new ClasePruebaSerializacion();
    }

    private ClasePruebaSerializacion test() throws Throwable {
        String xml = new SerializadorXml().serializar(UtilXML.newInstance(
                "PRUEBA", prueba));
        Document doc = ParserGeneral.parseStringDoc(xml);

        ClasePruebaSerializacion res = ParserXml.parse(
                doc.getDocumentElement(), ClasePruebaSerializacion.class);

        String xml2 = new SerializadorXml().serializar(UtilXML.newInstance(
                "PRUEBA", res));

        assertEquals(xml, xml2);
        return res;
    }

    public void testDefault() throws Throwable {
        test();
    }

    public void testString() throws Throwable {
        prueba.setString("otro");
        assertEquals("otro", test().getString());

        prueba.setString(null);
        assertEquals(null, test().getString());
    }

    public void testBoolPrimitive() throws Throwable {
        prueba.setBoolPrimitive(false);
        assertEquals(false, test().isBoolPrimitive());

        prueba.setBoolPrimitive(true);
        assertEquals(true, test().isBoolPrimitive());
    }

    public void testBool() throws Throwable {
        prueba.setBool(Boolean.FALSE);
        assertEquals(Boolean.FALSE, test().getBool());

        prueba.setBool(Boolean.TRUE);
        assertEquals(Boolean.TRUE, test().getBool());

        prueba.setBool(null);
        assertEquals(null, test().getBool());
    }

    public void testIntPrimitive() throws Throwable {
        prueba.setIntPrimitive(0);
        assertEquals(0, test().getIntPrimitive());

        prueba.setIntPrimitive(1);
        assertEquals(1, test().getIntPrimitive());
    }

    public void testInteger() throws Throwable {
        prueba.setInteger(new Integer(0));
        assertEquals(new Integer(0), test().getInteger());

        prueba.setInteger(new Integer(1));
        assertEquals(new Integer(1), test().getInteger());

        prueba.setInteger(null);
        assertEquals(null, test().getInteger());
    }

    public void testDblPrimitive() throws Throwable {
        prueba.setDblPrimitive(0.1);
        assertEquals(0.1, test().getDblPrimitive());

        prueba.setDblPrimitive(1.1);
        assertEquals(1.1, test().getDblPrimitive());
    }

    public void testDbl() throws Throwable {
        prueba.setDbl(new Double(0.1));
        assertEquals(new Double(0.1), test().getDbl());

        prueba.setDbl(new Double(1.1));
        assertEquals(new Double(1.1), test().getDbl());

        prueba.setDbl(null);
        assertEquals(null, test().getDbl());
    }

    public void testFltPrimitive() throws Throwable {
        prueba.setFltPrimitive(0.1f);
        assertEquals(0.1f, test().getFltPrimitive());

        prueba.setFltPrimitive(1.1f);
        assertEquals(1.1f, test().getFltPrimitive());
    }

    public void testFlt() throws Throwable {
        prueba.setFlt(new Float(0.1f));
        assertEquals(new Float(0.1f), test().getFlt());

        prueba.setFlt(new Float(1.1f));
        assertEquals(new Float(1.1), test().getFlt());

        prueba.setFlt(null);
        assertEquals(null, test().getFlt());
    }

    public void testStringCollection() throws Throwable {
        prueba.getStringCollection().add("1");
        prueba.getStringCollection().add("2");
        prueba.getStringCollection().add("3");

        assertEquals(3, test().getStringCollection().size());
    }

    public void testMap() throws Throwable {
        prueba.getMap().put("1", "a");
        prueba.getMap().put("2", "b");
        prueba.getMap().put("3", "c");

        assertEquals(3, test().getMap().size());
    }

    public void testStringOtro() throws Throwable {
        // FIXME prueba.setSoloSetter("test");
        assertEquals("string", test().getSoloGetter());

        // FIXME prueba.setSoloSetter(null);
        assertEquals("string", test().getSoloGetter());
    }

    public void testString3() throws Throwable {
        prueba.setStringTres("tres");
        assertEquals("tres", test().getStringTres());

        prueba.setStringTres(null);
        assertEquals(null, test().getStringTres());
    }

    public void testEnum() throws Throwable {
        prueba.setEnumPrueba(ClasePruebaSerializacion.EnumPrueba.DOS);
        assertEquals(ClasePruebaSerializacion.EnumPrueba.DOS, test()
                .getEnumPrueba());

        prueba.setEnumPrueba(ClasePruebaSerializacion.EnumPrueba.TRES);
        assertEquals(ClasePruebaSerializacion.EnumPrueba.TRES, test()
                .getEnumPrueba());
    }

}
