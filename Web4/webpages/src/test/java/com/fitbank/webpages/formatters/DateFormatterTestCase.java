package com.fitbank.webpages.formatters;

import java.util.TimeZone;

import junit.framework.TestCase;

public class DateFormatterTestCase extends TestCase {

    private DateFormatter df;

    @Override
    public void setUp() throws Exception {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        df = new DateFormatter();
    }

    public void testTimestamp() {
        df.setFormat(DateFormatter.DateFormat.TIMESTAMP);
        df.setTransportFormat(DateFormatter.TransportDateFormat.TIMESTAMP);

        assertEquals("01-03-1983 08:50:30.123", df.format(
                "1983-03-01 08:50:30.123"));

        assertEquals("1983-03-01 08:50:30.123", df.unformat(
                "01-03-1983 08:50:30.123"));
    }

    public void testDateTime() {
        df.setFormat(DateFormatter.DateFormat.DATETIME);
        df.setTransportFormat(DateFormatter.TransportDateFormat.DATETIME);

        assertEquals("01-03-1983 08:50:30", df.format(
                "1983-03-01 08:50:30"));

        assertEquals("1983-03-01 08:50:30", df.unformat(
                "01-03-1983 08:50:30"));
    }

    public void testDate() {
        df.setFormat(DateFormatter.DateFormat.DATE);
        df.setTransportFormat(DateFormatter.TransportDateFormat.DATE);

        assertEquals("01-03-1983", df.format("1983-03-01"));

        assertEquals("1983-03-01", df.unformat("01-03-1983"));
    }

    public void testShortTime() {
        df.setFormat(DateFormatter.DateFormat.SHORT_TIME);
        df.setTransportFormat(DateFormatter.TransportDateFormat.SHORT_TIME);

        assertEquals("12:34", df.format("12:34"));

        assertEquals("12:34", df.unformat("12:34"));
    }

    public void testCompactShortTime() {
        df.setFormat(DateFormatter.DateFormat.SHORT_TIME);
        df.setTransportFormat(
                DateFormatter.TransportDateFormat.COMPACT_SHORT_TIME);

        assertEquals("12:34", df.format("1234"));

        assertEquals("1234", df.unformat("12:34"));
    }

    public void testMix() {
        df.setFormat(DateFormatter.DateFormat.TIMESTAMP);
        df.setTransportFormat(DateFormatter.TransportDateFormat.COMPACT_SHORT_TIME);

        assertEquals("01-01-1970 08:50:00.000", df.format("0850"));

        assertEquals("0850", df.unformat("01-01-1970 08:50:00.000"));
    }

}
