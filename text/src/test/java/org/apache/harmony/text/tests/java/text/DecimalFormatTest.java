/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.harmony.text.tests.java.text;

import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.AttributedCharacterIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.BitSet;
import java.util.Currency;
import java.util.Locale;

import junit.framework.TestCase;

import org.apache.harmony.testframework.serialization.SerializationTest;


public class DecimalFormatTest extends TestCase {

    public void testAttributedCharacterIterator() throws Exception {
        // Regression for http://issues.apache.org/jira/browse/HARMONY-333
        AttributedCharacterIterator iterator = new DecimalFormat().formatToCharacterIterator(new Integer(1));
        assertNotNull(iterator);
        assertFalse("attributes should exist", iterator.getAttributes().isEmpty());
    } 
    
    /*
     * Test the getter and setter of parseBigDecimal and parseIntegerOnly and
     * test the default value of them.
     */
    public void test_isParseBigDecimalLjava_lang_Boolean_isParseIntegerOnlyLjava_lang_Boolean() {

        // parseBigDecimal default to false
        DecimalFormat form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        assertFalse(form.isParseBigDecimal());
        form.setParseBigDecimal(true);
        assertTrue(form.isParseBigDecimal());
        form.setParseBigDecimal(false);
        assertFalse(form.isParseBigDecimal());

        // parseIntegerOnly default to false
        assertFalse(form.isParseIntegerOnly());
    }

    // Test the type of the returned object

    public void test_parseLjava_lang_String_Ljava_text_ParsePosition() {
        DecimalFormat form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        Number number = form.parse("23.1", new ParsePosition(0));
        assertTrue(number instanceof Double);

        // Test parsed object of type double when
        // parseBigDecimal is set to true

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        number = form.parse("23.1", new ParsePosition(0));
        assertTrue(number instanceof Double);

        form.setParseBigDecimal(true);
        number = form.parse("23.1", new ParsePosition(0));

        assertTrue(number instanceof BigDecimal);
        assertEquals(new BigDecimal("23.1"), number);

        // When parseIntegerOnly set to true, all float numbers will be parsed
        // into Long.
        // With the exception that, the value is out of the bound of Long or
        // some special values such as NaN or Infinity.

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        form.setParseIntegerOnly(true);
        number = form.parse("23.1f", new ParsePosition(0));

        assertTrue(number instanceof Long);

        number = form.parse("23.0", new ParsePosition(0));
        assertTrue(number instanceof Long);

        number = form.parse("-0.0", new ParsePosition(0));
        assertTrue(number instanceof Long);
        assertTrue(new Long(0).equals(number));

        number = form.parse("-9,223,372,036,854,775,8080.00",
                new ParsePosition(0));
        assertTrue(number instanceof Double);

        // Even if parseIntegerOnly is set to true, NaN will be parsed to Double

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        form.setParseIntegerOnly(true);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        number = form.parse(symbols.getNaN(), new ParsePosition(0));
        assertTrue(number instanceof Double);

        // Even if parseIntegerOnly is set to true, Infinity will still be
        // parsed to Double

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        form.setParseIntegerOnly(true);
        symbols = new DecimalFormatSymbols();
        number = form.parse(symbols.getInfinity(), new ParsePosition(0));
        assertTrue(number instanceof Double);

        // ParseBigDecimal take precedence of parseBigInteger

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        form.setParseIntegerOnly(true);
        form.setParseBigDecimal(true);

        number = form.parse("23.1f", new ParsePosition(0));

        assertTrue(number instanceof BigDecimal);

        number = form.parse("23.0", new ParsePosition(0));
        assertTrue(number instanceof BigDecimal);

        number = form.parse("-9,223,372,036,854,775,8080.00",
                new ParsePosition(0));
        assertFalse(number instanceof BigInteger);
        assertTrue(number instanceof BigDecimal);

        // Test whether the parsed object is of type float. (To be specific,
        // they are of type Double)

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);

        number = form.parse("23.1f", new ParsePosition(0));
        assertTrue(number instanceof Double);

        form.setParseBigDecimal(true);
        number = form.parse("23.1f", new ParsePosition(0));
        assertTrue(number instanceof BigDecimal);
        assertEquals(new BigDecimal("23.1"), number);

        // Integer will be parsed to Long, unless parseBigDecimal is set to true

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);

        number = form.parse("123", new ParsePosition(0));
        assertTrue(number instanceof Long);

        form.setParseBigDecimal(true);
        number = form.parse("123", new ParsePosition(0));
        assertTrue(number instanceof BigDecimal);
        assertEquals(new BigDecimal("123"), number);

        // NaN will be parsed to Double, no matter parseBigDecimal set or not.

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        symbols = new DecimalFormatSymbols();
        number = form.parse(symbols.getNaN() + "", new ParsePosition(0));
        assertTrue(number instanceof Double);

        form.setParseBigDecimal(true);
        number = form.parse(symbols.getNaN() + "", new ParsePosition(0));
        assertTrue(number instanceof Double);

        // Infinity will be parsed to Double, no matter parseBigDecimal set or
        // not.

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        symbols = new DecimalFormatSymbols();

        number = form.parse(symbols.getInfinity(), new ParsePosition(0));

        assertTrue(number instanceof Double);
        assertEquals("Infinity", number.toString());
        // When set bigDecimal to true, the result of parsing infinity

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        symbols = new DecimalFormatSymbols();
        form.setParseBigDecimal(true);

        number = form.parse(symbols.getInfinity(), new ParsePosition(0));
        assertTrue(number instanceof Double);
        assertEquals("Infinity", number.toString());

        // Negative infinity will be parsed to double no matter parseBigDecimal
        // set or not

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        symbols = new DecimalFormatSymbols();

        number = form.parse("-" + symbols.getInfinity(), new ParsePosition(0));

        assertTrue(number instanceof Double);
        assertEquals("-Infinity", number.toString());

        // When set bigDecimal to true, the result of parsing minus infinity

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        symbols = new DecimalFormatSymbols();
        form.setParseBigDecimal(true);

        number = form.parse("-" + symbols.getInfinity(), new ParsePosition(0));

        assertTrue(number instanceof Double);
        assertEquals("-Infinity", number.toString());

        // -0.0 will be parsed to different type according to the combination of
        // parseBigDecimal and parseIntegerOnly

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);

        // parseBigDecimal == true;
        // parseIntegerOnly == false;
        form.setParseBigDecimal(true);
        number = form.parse("-0", new ParsePosition(0));
        assertTrue(number instanceof BigDecimal);

        number = form.parse("-0.0", new ParsePosition(0));
        assertTrue(number instanceof BigDecimal);

        // parseBigDecimal == false;
        // parseIntegerOnly == true;
        form.setParseBigDecimal(false);
        form.setParseIntegerOnly(true);
        number = form.parse("-0", new ParsePosition(0));

        assertTrue(number instanceof Long);

        number = form.parse("-0.0", new ParsePosition(0));
        assertTrue(number instanceof Long);

        // parseBigDecimal == false;
        // parseIntegerOnly == false;
        form.setParseBigDecimal(false);
        form.setParseIntegerOnly(false);
        number = form.parse("-0", new ParsePosition(0));
        assertTrue(number instanceof Double);

        number = form.parse("-0.0", new ParsePosition(0));
        assertTrue(number instanceof Double);

        // parseBigDecimal == true;
        // parseIntegerOnly == true;
        // parseBigDecimal take precedence of parseBigInteger
        form.setParseBigDecimal(true);
        form.setParseIntegerOnly(true);
        number = form.parse("-0", new ParsePosition(0));
        assertTrue(number instanceof BigDecimal);

        number = form.parse("-0.0", new ParsePosition(0));
        assertTrue(number instanceof BigDecimal);

        number = form.parse("12.4", new ParsePosition(0));
        assertTrue(number instanceof BigDecimal);

        // When parseBigDecimal is set to false, no matter how massive the
        // mantissa part of a number is, the number will be parsed into Double

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);

        number = form.parse("9,223,372,036,854,775,808.00",
                new ParsePosition(0));

        assertTrue(number instanceof Double);
        assertEquals("9.223372036854776E18", number.toString());

        number = form.parse("-9,223,372,036,854,775,8080.00",
                new ParsePosition(0));
        assertTrue(number instanceof Double);
        assertEquals("-9.223372036854776E19", number.toString());

        // When parseBigDecimal is set to true, if mantissa part of number
        // exceeds Long.MAX_VALUE, the number will be parsed into IcuBigDecimal

        form = (DecimalFormat) DecimalFormat.getInstance(Locale.US);

        form.setParseBigDecimal(true);
        number = form.parse("9,223,372,036,854,775,808.00",
                new ParsePosition(0));

        assertTrue(number instanceof BigDecimal);

        assertEquals(9.223372036854776E18, number.doubleValue(), 0);

        number = form.parse("-9,223,372,036,854,775,8080.00",
                new ParsePosition(0));

        assertTrue(number instanceof BigDecimal);
        assertEquals(-9.223372036854776E19, number.doubleValue(), 0);

        // The minimum value of Long will be parsed to Long when parseBigDecimal
        // is not set

        ParsePosition pos = new ParsePosition(0);
        DecimalFormat df = new DecimalFormat();
        pos = new ParsePosition(0);
        Number nb = df.parse("" + Long.MIN_VALUE, pos);
        assertTrue(nb instanceof Long);

        // The maximum value of Long will be parsed to Long when parseBigDecimal
        // is set
        pos = new ParsePosition(0);
        df = new DecimalFormat();
        pos = new ParsePosition(0);
        nb = df.parse("" + Long.MAX_VALUE, pos);
        assertTrue(nb instanceof Long);

        // When parsing invalid string( which is neither consist of digits nor
        // NaN/Infinity), a null will be returned.

        pos = new ParsePosition(0);
        df = new DecimalFormat();
        try {
            nb = df.parse("invalid", pos);
            assertNull(nb);
        } catch (NullPointerException e) {
            fail("Should not throw NPE");
        }
    }

    public void test_getMaximumFractionDigits() {
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        // getMaximumFractionDigits of NumberFormat default to 3
        // getMaximumFractionDigits of DecimalFormat default to 3
        assertEquals(3, nform.getMaximumFractionDigits());
        assertEquals(3, form.getMaximumFractionDigits());

        // Greater than 340 (critical number used to distinguish
        // BigInteger and IcuBigDecimal)
        nform.setMaximumFractionDigits(500);
        assertEquals(500, nform.getMaximumFractionDigits());
        assertEquals(500, form.getMaximumFractionDigits());

        form.setMaximumFractionDigits(500);
        assertEquals(500, nform.getMaximumFractionDigits());
        assertEquals(500, form.getMaximumFractionDigits());

        form.format(12.3);
        assertEquals(500, nform.getMaximumFractionDigits());
        assertEquals(500, form.getMaximumFractionDigits());
    }

    public void test_getMinimumFractionDigits() {
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        // getMinimumFractionDigits from NumberFormat (default to 0)
        // getMinimumFractionDigits from DecimalFormat (default to 0)
        assertEquals(0, nform.getMinimumFractionDigits());
        assertEquals(0, form.getMinimumFractionDigits());

        // Greater than 340 (critical number used to distinguish
        // BigInteger and IcuBigDecimal)
        nform.setMinimumFractionDigits(500);
        assertEquals(500, nform.getMinimumFractionDigits());
        assertEquals(500, form.getMinimumFractionDigits());

        form.setMaximumFractionDigits(400);
        assertEquals(400, nform.getMinimumFractionDigits());
        assertEquals(400, form.getMinimumFractionDigits());
    }

    //FIXME This test fails on Harmony ClassLibrary
    public void test_getMaximumIntegerDigits() {
        final int maxIntDigit = 309;

        // When use default locale, in this case zh_CN
        // the returned instance of NumberFormat is a DecimalFormat
        DecimalFormat form = new DecimalFormat("00.###E0");
        assertEquals(2, form.getMaximumIntegerDigits());

        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        form = null;
        if (nform instanceof DecimalFormat) {
            form = (DecimalFormat) nform;
        }

        // Greater than 309 (critical number used to distinguish
        // BigInteger and IcuBigDecimal)
        nform.setMaximumIntegerDigits(500);
        assertEquals(500, nform.getMaximumIntegerDigits());
        assertEquals(500, form.getMaximumIntegerDigits());

        form = new DecimalFormat("00.###E0");
        assertEquals(2, form.getMaximumIntegerDigits());

        form.setMaximumIntegerDigits(500);
        assertEquals(500, nform.getMaximumIntegerDigits());
        assertEquals(500, form.getMaximumIntegerDigits());
        form.format(12.3);
        assertEquals(500, nform.getMaximumIntegerDigits());
        assertEquals(500, form.getMaximumIntegerDigits());

        nform = DecimalFormat.getInstance(Locale.US);
        form = null;
        if (nform instanceof DecimalFormat) {
            form = (DecimalFormat) nform;
        }
        // getMaximumIntegerDigits from NumberFormat default to 309
        // getMaximumIntegerDigits from DecimalFormat default to 309
        // the following 2 assertions will fail on RI implementation, since the
        // implementation of ICU and RI are not identical. RI does not give
        // DecimalFormat an initial bound about its maximumIntegerDigits
        // (default to Integer.MAX_VALUE: 2147483647 )
        assertEquals(maxIntDigit, nform.getMaximumIntegerDigits());
        assertEquals(maxIntDigit, form.getMaximumIntegerDigits());

        // regression test for HARMONY-878
        assertTrue(new DecimalFormat("0\t'0'").getMaximumIntegerDigits() > 0);
    }

    public void test_getMinimumIntegerDigits() {
        final int minIntDigit = 1;
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        // getMaximumIntegerDigits from NumberFormat (default to 1)
        // getMaximumIntegerDigits from DecimalFormat (default to 1)
        assertEquals(minIntDigit, nform.getMinimumIntegerDigits());
        assertEquals(minIntDigit, form.getMinimumIntegerDigits());

        // Greater than 309 (critical number used to distinguish
        // BigInteger and IcuBigDecimal)
        nform.setMinimumIntegerDigits(500);
        assertEquals(500, nform.getMinimumIntegerDigits());
        assertEquals(500, form.getMinimumIntegerDigits());

        form.setMaximumIntegerDigits(400);
        assertEquals(400, nform.getMinimumIntegerDigits());
        assertEquals(400, form.getMinimumIntegerDigits());

    }

    public void test_formatLjava_lang_Obj_Ljava_StringBuffer_Ljava_text_FieldPosition() {
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        // If Object(including null) is not of type Number,
        // IllegalArgumentException will be thrown out
        try {
            form.format(new Object(), new StringBuffer(), new FieldPosition(0));
            fail("Should throw IAE");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            form.format(null, new StringBuffer(), new FieldPosition(0));
            fail("Should throw IAE");
        } catch (IllegalArgumentException e) {
            // expected
        }

        // When StringBuffer == null || FieldPosition == null
        // NullPointerException will be thrown out.
        try {
            form.format(new Double(1.9), null, new FieldPosition(0));
            fail("Should throw NPE");
        } catch (NullPointerException e) {
            // expected
        }

        try {
            form.format(new Double(1.3), new StringBuffer(), null);
            fail("Should throw NPE");
        } catch (NullPointerException e) {
            // expected
        }

        try {
            form.format(new Double(1.4), null, null);
            fail("Should throw NPE");
        } catch (NullPointerException e) {
            // expected
        }

        try {
            form.format(new Object(), null, null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        FieldPosition pos;
        StringBuffer out;
        DecimalFormat format = (DecimalFormat) NumberFormat
                .getInstance(Locale.US);

        // format maxLong
        pos = new FieldPosition(0);
        out = format.format(new Long(Long.MAX_VALUE), new StringBuffer(), pos);
        assertTrue("Wrong result L1: " + out, out.toString().equals(
                "9,223,372,036,854,775,807"));

        // format minLong
        pos = new FieldPosition(0);
        out = format.format(new Long(Long.MIN_VALUE), new StringBuffer(), pos);
        assertTrue("Wrong result L2: " + out, out.toString().equals(
                "-9,223,372,036,854,775,808"));

        // format maxLong of type BigInteger
        pos = new FieldPosition(0);
        out = format.format(new java.math.BigInteger(String
                .valueOf(Long.MAX_VALUE)), new StringBuffer(), pos);
        assertTrue("Wrong result BI1: " + out, out.toString().equals(
                "9,223,372,036,854,775,807"));

        // format minLong of type BigInteger
        pos = new FieldPosition(0);
        out = format.format(new java.math.BigInteger(String
                .valueOf(Long.MIN_VALUE)), new StringBuffer(), pos);
        assertTrue("Wrong result BI2: " + out, out.toString().equals(
                "-9,223,372,036,854,775,808"));

        // format maxLong + 1
        java.math.BigInteger big;
        pos = new FieldPosition(0);
        big = new java.math.BigInteger(String.valueOf(Long.MAX_VALUE))
                .add(new java.math.BigInteger("1"));
        out = format.format(big, new StringBuffer(), pos);
        assertTrue("Wrong result BI3: " + out, out.toString().equals(
                "9,223,372,036,854,775,808"));

        // format minLong - 1
        pos = new FieldPosition(0);
        big = new java.math.BigInteger(String.valueOf(Long.MIN_VALUE))
                .add(new java.math.BigInteger("-1"));
        out = format.format(big, new StringBuffer(), pos);
        assertTrue("Wrong result BI4: " + out, out.toString().equals(
                "-9,223,372,036,854,775,809"));

        // format big decimal
        pos = new FieldPosition(0);
        out = format.format(new java.math.BigDecimal("51.348"),
                new StringBuffer(), pos);
        assertTrue("Wrong result BD1: " + out, out.toString().equals("51.348"));

        // format big decimal
        pos = new FieldPosition(0);
        out = format.format(new java.math.BigDecimal("51"), new StringBuffer(),
                pos);
        assertTrue("Wrong result BD2: " + out, out.toString().equals("51"));

        // format big decimal Double.MAX_VALUE * 2
        java.math.BigDecimal bigDecimal;
        pos = new FieldPosition(0);
        final String doubleMax2 = "359,538,626,972,463,141,629,054,847,463,408,"
                + "713,596,141,135,051,689,993,197,834,953,606,314,521,560,057,077,"
                + "521,179,117,265,533,756,343,080,917,907,028,764,928,468,642,653,"
                + "778,928,365,536,935,093,407,075,033,972,099,821,153,102,564,152,"
                + "490,980,180,778,657,888,151,737,016,910,267,884,609,166,473,806,"
                + "445,896,331,617,118,664,246,696,549,595,652,408,289,446,337,476,"
                + "354,361,838,599,762,500,808,052,368,249,716,736";
        bigDecimal = new BigDecimal(Double.MAX_VALUE).add(new BigDecimal(
                Double.MAX_VALUE));
        out = format.format(bigDecimal, new StringBuffer(), pos);
        assertTrue("Wrong result BDmax2: " + out, out.toString().equals(
                doubleMax2));

        // format big decimal Double.MIN_VALUE + Double.MIN_VALUE
        // and Double.MIN_VALUE - Double.MIN_VALUE
        pos = new FieldPosition(0);

        bigDecimal = new BigDecimal(Double.MIN_VALUE).add(new BigDecimal(
                Double.MIN_VALUE));
        out = format.format(bigDecimal, new StringBuffer(), pos);

        bigDecimal = new BigDecimal(Float.MAX_VALUE).add(new BigDecimal(
                Float.MAX_VALUE));
        out = format.format(bigDecimal, new StringBuffer(), pos);
        final String BDFloatMax2 = "680,564,693,277,057,719,623,408,366,969,033,850,880";
        assertTrue("Wrong result BDFloatMax2: " + out, out.toString().equals(
                BDFloatMax2));
        // format big decimal Float.MIN_VALUE + Float.MIN_VALUE
        // and Float.MIN_VALUE - Float.MIN_VALUE
        bigDecimal = new BigDecimal(Float.MIN_VALUE).add(new BigDecimal(
                Float.MIN_VALUE));
        out = format.format(bigDecimal, new StringBuffer(), pos);
        final String BDFloatMin2 = "0";

        bigDecimal = new BigDecimal(Float.MIN_VALUE).subtract(new BigDecimal(
                Float.MIN_VALUE));
        out = format.format(bigDecimal, new StringBuffer(), pos);

        assertTrue("Wrong result BDFloatMax2: " + out, out.toString().equals(
                BDFloatMin2));

    }

    public void test_setMaximumFractionDigitsLjava_lang_Integer() {
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        form.setMaximumFractionDigits(-2);
        assertEquals(0, form.getMaximumFractionDigits());

        form.setMaximumFractionDigits(341);
        assertEquals(341, form.getMaximumFractionDigits());
    }

    public void test_setMinimumFractionDigitsLjava_lang_Integer() {
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        form.setMinimumFractionDigits(-3);
        assertEquals(0, form.getMinimumFractionDigits());

        form.setMinimumFractionDigits(310);
        assertEquals(310, form.getMinimumFractionDigits());
    }

    public void test_setMaximumIntegerDigitsLjava_lang_Integer() {
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        form.setMaximumIntegerDigits(-3);
        assertEquals(0, form.getMaximumIntegerDigits());

        form.setMaximumIntegerDigits(310);
        assertEquals(310, form.getMaximumIntegerDigits());
    }

    public void test_setMinimumIntegerDigitsLjava_lang_Integer() {
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        form.setMinimumIntegerDigits(-3);
        assertEquals(0, form.getMinimumIntegerDigits());

        form.setMinimumIntegerDigits(310);
        assertEquals(310, form.getMinimumIntegerDigits());
    }

    // When MaxFractionDigits is set first and less than MinFractionDigits, max
    // will be changed to min value
    public void test_setMinimumFactionDigitsLjava_lang_Integer_setMaximumFractionDigitsLjava_lang_Integer() {
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        form.setMaximumFractionDigits(100);
        form.setMinimumFractionDigits(200);

        assertEquals(200, form.getMaximumFractionDigits());
        assertEquals(200, form.getMinimumFractionDigits());

        form.setMaximumIntegerDigits(100);
        form.setMinimumIntegerDigits(200);

        assertEquals(200, form.getMaximumIntegerDigits());
        assertEquals(200, form.getMinimumIntegerDigits());
    }

    // When MinFractionDigits is set first and less than MaxFractionDigits, min
    // will be changed to max value
    public void test_setMaximumFactionDigitsLjava_lang_Integer_setMinimumFractionDigitsLjava_lang_Integer() {
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;

        form.setMinimumFractionDigits(200);
        form.setMaximumFractionDigits(100);

        assertEquals(100, form.getMaximumFractionDigits());
        assertEquals(100, form.getMinimumFractionDigits());

        form.setMinimumIntegerDigits(200);
        form.setMaximumIntegerDigits(100);

        assertEquals(100, form.getMaximumIntegerDigits());
        assertEquals(100, form.getMinimumIntegerDigits());
    }

    public void test_equalsLjava_lang_Object() {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        DecimalFormat cloned = (DecimalFormat) format.clone();
        cloned.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        assertEquals(format, cloned);

        Currency c = Currency.getInstance(Locale.US);
        cloned.setCurrency(c);

        assertEquals(format, cloned);
    }

    public void test_setPositivePrefixLjava_lang_String() {
        DecimalFormat format = new DecimalFormat();
        assertEquals("", format.getPositivePrefix());
    }

    public void test_setPositiveSuffixLjava_lang_String() {
        DecimalFormat format = new DecimalFormat();
        assertEquals("", format.getPositiveSuffix());
    }

    public void test_setNegativePrefixLjava_lang_String() {
        DecimalFormat format = new DecimalFormat();
        assertEquals("-", format.getNegativePrefix());
    }

    public void test_setNegativeSuffixLjava_lang_String() {
        DecimalFormat format = new DecimalFormat();
        assertEquals("", format.getNegativeSuffix());
    }

    public void test_setGroupingUse() {
        DecimalFormat format = new DecimalFormat();
        StringBuffer buf = new StringBuffer();
        format.setGroupingUsed(false);
        format.format(new Long(1970), buf, new FieldPosition(0));
        assertEquals("1970", buf.toString());
        assertFalse(format.isGroupingUsed());
    }

    /**
     * @tests java.text.DecimalFormat#DecimalFormat(java.lang.String)
     */
    public void test_ConstructorLjava_lang_String() {
        // Test for method java.text.DecimalFormat(java.lang.String)
        // the constructor form that specifies a pattern is equal to the form
        // constructed with no pattern and applying that pattern using the
        // applyPattern call
        DecimalFormat format = new DecimalFormat("'$'0000.0000");
        DecimalFormat format1 = new DecimalFormat();
        format1.applyPattern("'$'0000.0000");
        assertTrue("Constructed format did not match applied format object",
                format.equals(format1));
    }

    /**
     * @tests java.text.DecimalFormat#applyPattern(java.lang.String)
     */
    public void test_applyPatternLjava_lang_String() {
        DecimalFormat format = new DecimalFormat("#.#");
        assertEquals("Wrong pattern 1", "#0.#", format.toPattern());
        format = new DecimalFormat("#.");
        assertEquals("Wrong pattern 2", "#0.", format.toPattern());
        format = new DecimalFormat("#");
        assertEquals("Wrong pattern 3", "#", format.toPattern());
        format = new DecimalFormat(".#");
        assertEquals("Wrong pattern 4", "#.0", format.toPattern());
        // Regression for HARMONY-6485
        format = new DecimalFormat();
        format.setMinimumIntegerDigits(0);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(0);
        format.applyPattern("00.0#");
        assertEquals("Minimum integer digits not set", 2, format.getMinimumIntegerDigits());
        assertEquals("Minimum fraction digits not set", 1, format.getMinimumFractionDigits());
        assertEquals("Maximum fraction digits not set", 2, format.getMaximumFractionDigits());
    }

    /**
     * @tests java.text.DecimalFormat#clone()
     */
    public void test_clone() {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
        DecimalFormat cloned = (DecimalFormat) format.clone();
        assertEquals(cloned.getDecimalFormatSymbols(), format
                .getDecimalFormatSymbols());

        format = new DecimalFormat("'$'0000.0000");
        DecimalFormat format1 = (DecimalFormat) (format.clone());
        // make sure the objects are equal
        assertTrue("Object's clone isn't equal!", format.equals(format1));
        // change the content of the clone and make sure it's not equal anymore
        // verifies that it's data is now distinct from the original
        format1.applyPattern("'$'0000.####");
        assertTrue("Object's changed clone should not be equal!", !format
                .equals(format1));
    }

    private void compare(String testName, String format, String expected) {
        assertTrue(testName + " got: " + format + " expected: " + expected,
                format.equals(expected));
    }

    private boolean compare(int count, String format, String expected) {
        boolean result = format.equals(expected);
        if (!result)
            System.out.println("Failure test: " + count + " got: " + format
                    + " expected: " + expected);
        return result;
    }

    /**
     * @tests java.text.DecimalFormat#format(double, java.lang.StringBuffer,
     *        java.text.FieldPosition)
     */
    //FIXME This test fails on Harmony ClassLibrary
    public void test_formatDLjava_lang_StringBufferLjava_text_FieldPosition() {
        new Support_DecimalFormat(
                "test_formatDLjava_lang_StringBufferLjava_text_FieldPosition")
                .t_format_with_FieldPosition();

        int failCount = 0;
        BitSet failures = new BitSet();

        final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

        DecimalFormat df = new DecimalFormat("00.0#E0", dfs);
        compare("00.0#E0: 0.0", df.format(0.0), "00.0E0");
        compare("00.0#E0: 1.0", df.format(1.0), "10.0E-1");
        compare("00.0#E0: 12.0", df.format(12.0), "12.0E0");
        compare("00.0#E0: 123.0", df.format(123.0), "12.3E1");
        compare("00.0#E0: 1234.0", df.format(1234.0), "12.34E2");
        compare("00.0#E0: 12346.0", df.format(12346.0), "12.35E3");
        compare("00.0#E0: 99999.0", df.format(99999.0), "10.0E4");
        compare("00.0#E0: 1.2", df.format(1.2), "12.0E-1");
        compare("00.0#E0: 12.3", df.format(12.3), "12.3E0");
        compare("00.0#E0: 123.4", df.format(123.4), "12.34E1");
        compare("00.0#E0: 1234.6", df.format(1234.6), "12.35E2");
        compare("00.0#E0: 9999.9", df.format(9999.9), "10.0E3");
        compare("00.0#E0: 0.1", df.format(0.1), "10.0E-2");
        compare("00.0#E0: 0.12", df.format(0.12), "12.0E-2");
        compare("00.0#E0: 0.123", df.format(0.123), "12.3E-2");
        compare("00.0#E0: 0.1234", df.format(0.1234), "12.34E-2");
        compare("00.0#E0: 0.12346", df.format(0.12346), "12.35E-2");
        compare("00.0#E0: 0.99999", df.format(0.99999), "10.0E-1");
        compare("00.0#E0: -0.0", df.format(-0.0), "-00.0E0");
        compare("00.0#E0: -1.0", df.format(-1.0), "-10.0E-1");
        compare("00.0#E0: -12.0", df.format(-12.0), "-12.0E0");
        compare("00.0#E0: -123.0", df.format(-123.0), "-12.3E1");
        compare("00.0#E0: -1234.0", df.format(-1234.0), "-12.34E2");
        compare("00.0#E0: -12346.0", df.format(-12346.0), "-12.35E3");
        compare("00.0#E0: -99999.0", df.format(-99999.0), "-10.0E4");

        df = new DecimalFormat("##0.0E0", dfs);
        compare("##0.0E0: -0.0", df.format(-0.0), "-0.0E0");
        compare("##0.0E0: 0.0", df.format(0.0), "0.0E0");
        compare("##0.0E0: 1.0", df.format(1.0), "1.0E0");
        compare("##0.0E0: 12.0", df.format(12.0), "12E0");
        compare("##0.0E0: 123.0", df.format(123.0), "123E0");
        compare("##0.0E0: 1234.0", df.format(1234.0), "1.234E3");
        compare("##0.0E0: 12346.0", df.format(12346.0), "12.35E3");
        // Fails in JDK 1.2.2
        if (!compare(failCount, df.format(99999.0), "100E3"))
            failures.set(failCount);
        failCount++;
        compare("##0.0E0: 999999.0", df.format(999999.0), "1.0E6");

        df = new DecimalFormat("#00.0##E0", dfs);
        compare("#00.0##E0: 0.1", df.format(0.1), ".100E0");
        compare("#00.0##E0: 0.12", df.format(0.12), ".120E0");
        compare("#00.0##E0: 0.123", df.format(0.123), ".123E0");
        compare("#00.0##E0: 0.1234", df.format(0.1234), ".1234E0");
        compare("#00.0##E0: 0.1234567", df.format(0.1234567), ".123457E0");
        compare("#00.0##E0: 0.01", df.format(0.01), "10.0E-3");
        compare("#00.0##E0: 0.012", df.format(0.012), "12.0E-3");
        compare("#00.0##E0: 0.0123", df.format(0.0123), "12.3E-3");
        compare("#00.0##E0: 0.01234", df.format(0.01234), "12.34E-3");
        compare("#00.0##E0: 0.01234567", df.format(0.01234567), "12.3457E-3");
        compare("#00.0##E0: 0.001", df.format(0.001), "1.00E-3");
        compare("#00.0##E0: 0.0012", df.format(0.0012), "1.20E-3");
        compare("#00.0##E0: 0.00123", df.format(0.00123), "1.23E-3");
        compare("#00.0##E0: 0.001234", df.format(0.001234), "1.234E-3");
        compare("#00.0##E0: 0.001234567", df.format(0.001234567), "1.23457E-3");
        compare("#00.0##E0: 0.0001", df.format(0.0001), "100E-6");
        compare("#00.0##E0: 0.00012", df.format(0.00012), "120E-6");
        compare("#00.0##E0: 0.000123", df.format(0.000123), "123E-6");
        compare("#00.0##E0: 0.0001234", df.format(0.0001234), "123.4E-6");
        compare("#00.0##E0: 0.0001234567", df.format(0.0001234567),
                "123.457E-6");

        // Fails in JDK 1.2.2
        if (!compare(failCount, df.format(0.0), "0.00E0"))
            failures.set(failCount);
        failCount++;
        compare("#00.0##E0: 1.0", df.format(1.0), "1.00E0");
        compare("#00.0##E0: 12.0", df.format(12.0), "12.0E0");
        compare("#00.0##E0: 123.0", df.format(123.0), "123E0");
        compare("#00.0##E0: 1234.0", df.format(1234.0), "1.234E3");
        compare("#00.0##E0: 12345.0", df.format(12345.0), "12.345E3");
        compare("#00.0##E0: 123456.0", df.format(123456.0), "123.456E3");
        compare("#00.0##E0: 1234567.0", df.format(1234567.0), "1.23457E6");
        compare("#00.0##E0: 12345678.0", df.format(12345678.0), "12.3457E6");
        compare("#00.0##E0: 99999999.0", df.format(99999999.0), "100E6");

        df = new DecimalFormat("#.0E0", dfs);
        compare("#.0E0: -0.0", df.format(-0.0), "-.0E0");
        compare("#.0E0: 0.0", df.format(0.0), ".0E0");
        compare("#.0E0: 1.0", df.format(1.0), ".1E1");
        compare("#.0E0: 12.0", df.format(12.0), ".12E2");
        compare("#.0E0: 123.0", df.format(123.0), ".12E3");
        compare("#.0E0: 1234.0", df.format(1234.0), ".12E4");
        compare("#.0E0: 9999.0", df.format(9999.0), ".1E5");

        df = new DecimalFormat("0.#E0", dfs);
        compare("0.#E0: -0.0", df.format(-0.0), "-0E0");
        compare("0.#E0: 0.0", df.format(0.0), "0E0");
        compare("0.#E0: 1.0", df.format(1.0), "1E0");
        compare("0.#E0: 12.0", df.format(12.0), "1.2E1");
        compare("0.#E0: 123.0", df.format(123.0), "1.2E2");
        compare("0.#E0: 1234.0", df.format(1234.0), "1.2E3");
        compare("0.#E0: 9999.0", df.format(9999.0), "1E4");

        df = new DecimalFormat(".0E0", dfs);
        compare(".0E0: -0.0", df.format(-0.0), "-.0E0");
        compare(".0E0: 0.0", df.format(0.0), ".0E0");
        compare(".0E0: 1.0", df.format(1.0), ".1E1");
        compare(".0E0: 12.0", df.format(12.0), ".1E2");
        compare(".0E0: 123.0", df.format(123.0), ".1E3");
        compare(".0E0: 1234.0", df.format(1234.0), ".1E4");
        compare(".0E0: 9999.0", df.format(9999.0), ".1E5");

        df = new DecimalFormat("0.E0", dfs);
        // Fails in JDK 1.2.2
        if (!compare(failCount, df.format(0.0), "0.E0"))
            failures.set(failCount);
        failCount++;
        if (!compare(failCount, df.format(1.0), "1.E0"))
            failures.set(failCount);
        failCount++;
        if (!compare(failCount, df.format(12.0), "1.E1"))
            failures.set(failCount);
        failCount++;
        if (!compare(failCount, df.format(123.0), "1.E2"))
            failures.set(failCount);
        failCount++;
        if (!compare(failCount, df.format(1234.0), "1.E3"))
            failures.set(failCount);
        failCount++;
        if (!compare(failCount, df.format(9999.0), "1.E4"))
            failures.set(failCount);
        failCount++;

        df = new DecimalFormat("##0.00#E0", dfs);
        compare("##0.00#E0: 0.1", df.format(0.1), ".100E0");
        compare("##0.00#E0: 0.1234567", df.format(0.1234567), ".123457E0");
        compare("##0.00#E0: 0.9999999", df.format(0.9999999), "1.00E0");
        compare("##0.00#E0: 0.01", df.format(0.01), "10.0E-3");
        compare("##0.00#E0: 0.01234567", df.format(0.01234567), "12.3457E-3");
        compare("##0.00#E0: 0.09999999", df.format(0.09999999), ".100E0");
        compare("##0.00#E0: 0.001", df.format(0.001), "1.00E-3");
        compare("##0.00#E0: 0.001234567", df.format(0.001234567), "1.23457E-3");
        compare("##0.00#E0: 0.009999999", df.format(0.009999999), "10.0E-3");
        compare("##0.00#E0: 0.0001", df.format(0.0001), "100E-6");
        compare("##0.00#E0: 0.0001234567", df.format(0.0001234567),
                "123.457E-6");
        compare("##0.00#E0: 0.0009999999", df.format(0.0009999999), "1.00E-3");

        df = new DecimalFormat("###0.00#E0", dfs);
        compare("###0.00#E0: 0.1", df.format(0.1), ".100E0");
        compare("###0.00#E0: 0.12345678", df.format(0.12345678), ".1234568E0");
        compare("###0.00#E0: 0.99999999", df.format(0.99999999), "1.00E0");
        compare("###0.00#E0: 0.01", df.format(0.01), "100E-4");
        compare("###0.00#E0: 0.012345678", df.format(0.012345678),
                "123.4568E-4");
        compare("###0.00#E0: 0.099999999", df.format(0.099999999), ".100E0");
        compare("###0.00#E0: 0.001", df.format(0.001), "10.0E-4");
        compare("###0.00#E0: 0.0012345678", df.format(0.0012345678),
                "12.34568E-4");
        compare("###0.00#E0: 0.0099999999", df.format(0.0099999999), "100E-4");
        compare("###0.00#E0: 0.0001", df.format(0.0001), "1.00E-4");
        compare("###0.00#E0: 0.00012345678", df.format(0.00012345678),
                "1.234568E-4");
        compare("###0.00#E0: 0.00099999999", df.format(0.00099999999),
                "10.0E-4");
        // Fails in JDK 1.2.2
        if (!compare(failCount, df.format(0.00001), "1000E-8"))
            failures.set(failCount);
        failCount++;
        compare("###0.00#E0: 0.000012345678", df.format(0.000012345678),
                "1234.568E-8");
        compare("###0.00#E0: 0.000099999999", df.format(0.000099999999),
                "1.00E-4");

        df = new DecimalFormat("###0.0#E0", dfs);
        compare("###0.0#E0: 0.1", df.format(0.1), ".10E0");
        compare("###0.0#E0: 0.1234567", df.format(0.1234567), ".123457E0");
        compare("###0.0#E0: 0.9999999", df.format(0.9999999), "1.0E0");
        // Fails in JDK 1.2.2
        if (!compare(failCount, df.format(0.01), "100E-4"))
            failures.set(failCount);
        failCount++;
        compare("###0.0#E0: 0.01234567", df.format(0.01234567), "123.457E-4");
        compare("###0.0#E0: 0.09999999", df.format(0.09999999), ".10E0");
        compare("###0.0#E0: 0.001", df.format(0.001), "10E-4");
        compare("###0.0#E0: 0.001234567", df.format(0.001234567), "12.3457E-4");
        // Fails in JDK 1.2.2
        if (!compare(failCount, df.format(0.009999999), "100E-4"))
            failures.set(failCount);
        failCount++;
        compare("###0.0#E0: 0.0001", df.format(0.0001), "1.0E-4");
        compare("###0.0#E0: 0.0001234567", df.format(0.0001234567),
                "1.23457E-4");
        compare("###0.0#E0: 0.0009999999", df.format(0.0009999999), "10E-4");
        // Fails in JDK 1.2.2
        if (!compare(failCount, df.format(0.00001), "1000E-8"))
            failures.set(failCount);
        failCount++;
        compare("###0.0#E0: 0.00001234567", df.format(0.00001234567),
                "1234.57E-8");
        compare("###0.0#E0: 0.00009999999", df.format(0.00009999999), "1.0E-4");

        assertTrue("Failed " + failures + " of " + failCount,
                failures.length() == 0);

        String formatString = "##0.#";
        df = new DecimalFormat(formatString, dfs);
        df.setMinimumFractionDigits(30);
        compare(formatString + ": 0.000000000000000000000000000000", df
                .format(0.0), "0.000000000000000000000000000000");
        compare(formatString + ": -0.000000000000000000000000000000", df
                .format(-0.0), "-0.000000000000000000000000000000");
        compare(formatString + ": 1.000000000000000000000000000000", df
                .format(1.0), "1.000000000000000000000000000000");
        compare(formatString + ": -1.000000000000000000000000000000", df
                .format(-1.0), "-1.000000000000000000000000000000");

        df = new DecimalFormat(formatString);
        df.setMaximumFractionDigits(30);
        compare(formatString + ": 0", df.format(0.0), "0");
        compare(formatString + ": -0", df.format(-0.0), "-0");
        compare(formatString + ": 1", df.format(1.0), "1");
        compare(formatString + ": -1", df.format(-1.0), "-1");
    }

    /**
     * @tests java.text.DecimalFormat#format(long, java.lang.StringBuffer,
     *        java.text.FieldPosition)
     */
    //FIXME This test fails on Harmony ClassLibrary
    public void test_formatJLjava_lang_StringBufferLjava_text_FieldPosition() {
        int failCount = 0;
        BitSet failures = new BitSet();

        final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

        DecimalFormat df = new DecimalFormat("00.0#E0", dfs);
        assertEquals("00.0#E0: 0", "00.0E0", df.format(0));
        assertEquals("00.0#E0: 1", "10.0E-1", df.format(1));
        assertEquals("00.0#E0: 12", "12.0E0", df.format(12));
        assertEquals("00.0#E0: 123", "12.3E1", df.format(123));
        assertEquals("00.0#E0: 1234", "12.34E2", df.format(1234));
        assertEquals("00.0#E0: 12346", "12.35E3", df.format(12346));
        assertEquals("00.0#E0: 99999", "10.0E4", df.format(99999));
        assertEquals("00.0#E0: -1", "-10.0E-1", df.format(-1));
        assertEquals("00.0#E0: -12", "-12.0E0", df.format(-12));
        assertEquals("00.0#E0: -123", "-12.3E1", df.format(-123));
        assertEquals("00.0#E0: -1234", "-12.34E2", df.format(-1234));
        assertEquals("00.0#E0: -12346", "-12.35E3", df.format(-12346));
        assertEquals("00.0#E0: -99999", "-10.0E4", df.format(-99999));

        df = new DecimalFormat("##0.0E0", dfs);
        assertEquals("##0.0E0: 0", "0.0E0", df.format(0));
        assertEquals("##0.0E0: 1", "1.0E0", df.format(1));
        assertEquals("##0.0E0: 12", "12E0", df.format(12));
        assertEquals("##0.0E0: 123", "123E0", df.format(123));
        assertEquals("##0.0E0: 1234", "1.234E3", df.format(1234));
        assertEquals("##0.0E0: 12346", "12.35E3", df.format(12346));
        // Fails in JDK 1.2.2
        if (!df.format(99999).equals("100E3"))
            failures.set(failCount);
        failCount++;
        assertEquals("##0.0E0: 999999", "1.0E6", df.format(999999));

        df = new DecimalFormat("#00.0##E0", dfs);
        // Fails in JDK 1.2.2
        if (!df.format(0).equals("0.00E0"))
            failures.set(failCount);
        failCount++;
        assertEquals("#00.0##E0: 1", "1.00E0", df.format(1));
        assertEquals("#00.0##E0: 12", "12.0E0", df.format(12));
        assertEquals("#00.0##E0: 123", "123E0", df.format(123));
        assertEquals("#00.0##E0: 1234", "1.234E3", df.format(1234));
        assertEquals("#00.0##E0: 12345", "12.345E3", df.format(12345));
        assertEquals("#00.0##E0: 123456", "123.456E3", df.format(123456));
        assertEquals("#00.0##E0: 1234567", "1.23457E6", df.format(1234567));
        assertEquals("#00.0##E0: 12345678", "12.3457E6", df.format(12345678));
        assertEquals("#00.0##E0: 99999999", "100E6", df.format(99999999));

        df = new DecimalFormat("#.0E0", dfs);
        assertEquals("#.0E0: 0", ".0E0", df.format(0));
        assertEquals("#.0E0: 1", ".1E1", df.format(1));
        assertEquals("#.0E0: 12", ".12E2", df.format(12));
        assertEquals("#.0E0: 123", ".12E3", df.format(123));
        assertEquals("#.0E0: 1234", ".12E4", df.format(1234));
        assertEquals("#.0E0: 9999", ".1E5", df.format(9999));

        df = new DecimalFormat("0.#E0", dfs);
        assertEquals("0.#E0: 0", "0E0", df.format(0));
        assertEquals("0.#E0: 1", "1E0", df.format(1));
        assertEquals("0.#E0: 12", "1.2E1", df.format(12));
        assertEquals("0.#E0: 123", "1.2E2", df.format(123));
        assertEquals("0.#E0: 1234", "1.2E3", df.format(1234));
        assertEquals("0.#E0: 9999", "1E4", df.format(9999));

        assertTrue("Failed " + failures + " of " + failCount,
                failures.length() == 0);
    }

    /**
     * @tests java.text.DecimalFormat#formatToCharacterIterator(java.lang.Object)
     */
    //FIXME This test fails on Harmony ClassLibrary
    public void test_formatToCharacterIteratorLjava_lang_Object() {

        try {
            // Regression for HARMONY-466
            new DecimalFormat().formatToCharacterIterator(null);
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            // expected
        }

        new Support_DecimalFormat(
                "test_formatToCharacterIteratorLjava_lang_Object")
                .t_formatToCharacterIterator();
    }

    /**
     * @tests java.text.DecimalFormat#format(double)
     */
    public void test_formatD() {
        DecimalFormat format = (DecimalFormat) NumberFormat
                .getInstance(Locale.ENGLISH);
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(400);
        for (int i = 0; i < 309; i++) {
            String tval = "1";
            for (int j = 0; j < i; j++)
                tval += "0";
            double d = Double.parseDouble(tval);
            String result = format.format(d);
            assertEquals(i + ") e:" + tval + " r:" + result, tval, result);
        }
        for (int i = 0; i < 322; i++) {
            String tval = "0.";
            for (int j = 0; j < i; j++)
                tval += "0";
            tval += "1";
            double d = Double.parseDouble(tval);
            String result = format.format(d);
            assertEquals(i + ") e:" + tval + " r:" + result, tval, result);
        }
        assertEquals("999999999999999", format.format(999999999999999.));
        assertEquals("1", "999999999999999.9", format.format(999999999999999.9));
        assertEquals("2", "99999999999999.98", format.format(99999999999999.99));
        assertEquals("3", "9999999999999.998", format.format(9999999999999.999));
        assertEquals("4", "999999999999.9999", format.format(999999999999.9999));
        assertEquals("5", "99999999999.99998", format.format(99999999999.99999));
        assertEquals("6", "9999999999.999998", format.format(9999999999.999999));
        assertEquals("7", "999999999.9999999", format.format(999999999.9999999));
        assertEquals("8", "99999999.99999999", format.format(99999999.99999999));
        assertEquals("9", "9999999.999999998", format.format(9999999.999999999));
        assertEquals("10", "99999.99999999999", format
                .format(99999.99999999999));
        assertEquals("11", "9999.999999999998", format
                .format(9999.999999999999));
        assertEquals("12", "999.9999999999999", format
                .format(999.9999999999999));
        assertEquals("13", "99.99999999999999", format
                .format(99.99999999999999));
        assertEquals("14", "9.999999999999998", format
                .format(9.999999999999999));
        assertEquals("15", "0.9999999999999999", format
                .format(.9999999999999999));
    }

    /**
     * @tests java.text.DecimalFormat#getDecimalFormatSymbols()
     */
    public void test_getDecimalFormatSymbols() {
        DecimalFormat df = (DecimalFormat) NumberFormat
                .getInstance(Locale.ENGLISH);
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        assertTrue("Identical symbols", dfs != df.getDecimalFormatSymbols());
    }

    /**
     * @tests java.text.DecimalFormat#getCurrency()
     */
    //FIXME This test fails on Harmony ClassLibrary
    public void test_getCurrency() {
        Currency currK = Currency.getInstance("KRW");
        Currency currX = Currency.getInstance("XXX");
        Currency currE = Currency.getInstance("EUR");
        Currency curr01;

        DecimalFormat df = (DecimalFormat) NumberFormat
                .getCurrencyInstance(new Locale("ko", "KR"));
        assertTrue("Test1: Returned incorrect currency",
                df.getCurrency() == currK);

        df = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("",
                "KR"));
        assertTrue("Test2: Returned incorrect currency",
                df.getCurrency() == currK);

        df = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("ko",
                ""));
        assertTrue("Test3: Returned incorrect currency",
                df.getCurrency() == currX);

        df = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("fr",
                "FR"));
        assertTrue("Test4: Returned incorrect currency",
                df.getCurrency() == currE);

        // Regression for HARMONY-1351
        df = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("QWERTY"));
        assertTrue("Test5: Returned incorrect currency",
                df.getCurrency() == currX);
        
        // JDK fails these tests since it doesn't have the PREEURO variant
        // df = (DecimalFormat)NumberFormat.getCurrencyInstance(new Locale("fr",
        // "FR","PREEURO"));
        // assertTrue("Test5: Returned incorrect currency", df.getCurrency() ==
        // currF);
    }

    /**
     * @tests java.text.DecimalFormat#getGroupingSize()
     */
    public void test_getGroupingSize() {
        DecimalFormat df = new DecimalFormat("###0.##");
        assertEquals("Wrong unset size", 0, df.getGroupingSize());
        df = new DecimalFormat("#,##0.##");
        assertEquals("Wrong set size", 3, df.getGroupingSize());
        df = new DecimalFormat("#,###,###0.##");
        assertEquals("Wrong multiple set size", 4, df.getGroupingSize());
    }

    /**
     * @tests java.text.DecimalFormat#getMultiplier()
     */
    public void test_getMultiplier() {
        final int defaultMultiplier = 1;
        NumberFormat nform = DecimalFormat.getInstance(Locale.US);
        DecimalFormat form = (DecimalFormat) nform;
        assertEquals(defaultMultiplier, form.getMultiplier());

        DecimalFormat df = new DecimalFormat("###0.##");
        assertEquals("Wrong unset multiplier", 1, df.getMultiplier());
        df = new DecimalFormat("###0.##%");
        assertEquals("Wrong percent multiplier", 100, df.getMultiplier());
        df = new DecimalFormat("###0.##\u2030");
        assertEquals("Wrong mille multiplier", 1000, df.getMultiplier());
    }

    /**
     * @tests java.text.DecimalFormat#isDecimalSeparatorAlwaysShown()
     */
    public void test_isDecimalSeparatorAlwaysShown() {
        DecimalFormat df = new DecimalFormat("###0.##");
        assertTrue("Wrong unset value", !df.isDecimalSeparatorAlwaysShown());
        df = new DecimalFormat("###0.00");
        assertTrue("Wrong unset2 value", !df.isDecimalSeparatorAlwaysShown());
        df = new DecimalFormat("###0.");
        assertTrue("Wrong set value", df.isDecimalSeparatorAlwaysShown());
    }

    /**
     * @tests java.text.DecimalFormat#parse(java.lang.String,
     *        java.text.ParsePosition)
     */
    //FIXME This test fails on Harmony ClassLibrary
    public void test_parseLjava_lang_StringLjava_text_ParsePosition() {
        DecimalFormat format = (DecimalFormat) NumberFormat
                .getNumberInstance(Locale.ENGLISH);
        ParsePosition pos = new ParsePosition(0);
        Number result = format.parse("9223372036854775807", pos);
        assertTrue("Wrong result type for Long.MAX_VALUE",
                result.getClass() == Long.class);
        assertTrue("Wrong result Long.MAX_VALUE",
                result.longValue() == Long.MAX_VALUE);
        pos = new ParsePosition(0);
        result = format.parse("-9223372036854775808", pos);
        assertTrue("Wrong result type for Long.MIN_VALUE",
                result.getClass() == Long.class);
        assertTrue("Wrong result Long.MIN_VALUE: " + result.longValue(), result
                .longValue() == Long.MIN_VALUE);
        pos = new ParsePosition(0);
        result = format.parse("9223372036854775808", pos);
        assertTrue("Wrong result type for Long.MAX_VALUE+1",
                result.getClass() == Double.class);
        assertTrue("Wrong result Long.MAX_VALUE + 1",
                result.doubleValue() == (double) Long.MAX_VALUE + 1);
        pos = new ParsePosition(0);
        result = format.parse("-9223372036854775809", pos);
        assertTrue("Wrong result type for Long.MIN_VALUE+1",
                result.getClass() == Double.class);
        assertTrue("Wrong result Long.MIN_VALUE - 1",
                result.doubleValue() == (double) Long.MIN_VALUE - 1);

        pos = new ParsePosition(0);
        result = format.parse("18446744073709551629", pos);
        assertTrue("Wrong result type for overflow",
                result.getClass() == Double.class);
        assertTrue("Wrong result for overflow",
                result.doubleValue() == 18446744073709551629d);

        pos = new ParsePosition(0);
        result = format.parse("42325917317067571199", pos);
        assertTrue("Wrong result type for overflow a: " + result, result
                .getClass() == Double.class);
        assertTrue("Wrong result for overflow a: " + result, result
                .doubleValue() == 42325917317067571199d);
        pos = new ParsePosition(0);
        result = format.parse("4232591731706757119E1", pos);
        assertTrue("Wrong result type for overflow b: " + result, result
                .getClass() == Double.class);
        assertTrue("Wrong result for overflow b: " + result, result
                .doubleValue() == 42325917317067571190d);
        pos = new ParsePosition(0);
        result = format.parse(".42325917317067571199E20", pos);
        assertTrue("Wrong result type for overflow c: " + result, result
                .getClass() == Double.class);
        assertTrue("Wrong result for overflow c: " + result, result
                .doubleValue() == 42325917317067571199d);
        pos = new ParsePosition(0);
        result = format.parse("922337203685477580.9E1", pos);
        assertTrue("Wrong result type for overflow d: " + result, result
                .getClass() == Double.class);
        assertTrue("Wrong result for overflow d: " + result, result
                .doubleValue() == 9223372036854775809d);
        pos = new ParsePosition(0);
        result = format.parse("9.223372036854775809E18", pos);
        assertTrue("Wrong result type for overflow e: " + result, result
                .getClass() == Double.class);
        assertTrue("Wrong result for overflow e: " + result, result
                .doubleValue() == 9223372036854775809d);

        // test parse with multipliers
        format.setMultiplier(100);
        result = format.parse("9223372036854775807", new ParsePosition(0));
        assertTrue("Wrong result type multiplier 100: " + result, result
                .getClass() == Long.class);
        assertTrue("Wrong result for multiplier 100: " + result, result
                .longValue() == 92233720368547758L);

        format.setMultiplier(1000);
        result = format.parse("9223372036854775807", new ParsePosition(0));
        assertTrue("Wrong result type multiplier 1000: " + result, result
                .getClass() == Long.class);
        assertTrue("Wrong result for multiplier 1000: " + result, result
                .longValue() == 9223372036854776L);

        format.setMultiplier(10000);
        result = format.parse("9223372036854775807", new ParsePosition(0));
        assertTrue("Wrong result type multiplier 10000: " + result, result
                .getClass() == Double.class);
        assertTrue("Wrong result for multiplier 10000: " + result, result
                .doubleValue() == 922337203685477.5807d);

    }

    /**
     * @tests java.text.DecimalFormat#setDecimalFormatSymbols(java.text.DecimalFormatSymbols)
     */
    public void test_setDecimalFormatSymbolsLjava_text_DecimalFormatSymbols() {
        DecimalFormat df = new DecimalFormat("###0.##");
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('@');
        df.setDecimalFormatSymbols(dfs);
        assertTrue("Not set", df.getDecimalFormatSymbols().equals(dfs));
        assertEquals("Symbols not used", "1@2", df.format(1.2));

        // The returned symbols may be cloned in two spots
        // 1. When set
        // 2. When returned
        DecimalFormat format = new DecimalFormat();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        format.setDecimalFormatSymbols(symbols);
        DecimalFormatSymbols symbolsOut = format.getDecimalFormatSymbols();
        assertNotSame(symbols, symbolsOut);
    }

    /**
     * @tests java.text.DecimalFormat#setDecimalSeparatorAlwaysShown(boolean)
     */
    public void test_setDecimalSeparatorAlwaysShownZ() {
        DecimalFormat df = new DecimalFormat("###0.##",
                                             new DecimalFormatSymbols(Locale.US));
        assertEquals("Wrong default result", "5", df.format(5));
        df.setDecimalSeparatorAlwaysShown(true);
        assertTrue("Not set", df.isDecimalSeparatorAlwaysShown());
        assertEquals("Wrong set result", "7.", df.format(7));
    }

    /**
     * @tests java.text.DecimalFormat#setCurrency(java.util.Currency)
     */
    public void test_setCurrencyLjava_util_Currency() {
        Locale locale = Locale.CANADA;
        DecimalFormat df = ((DecimalFormat) NumberFormat
                .getCurrencyInstance(locale));

        try {
            df.setCurrency(null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
        }

        Currency currency = Currency.getInstance("AED");
        df.setCurrency(currency);
        assertTrue("Returned incorrect currency", currency == df.getCurrency());
        assertTrue("Returned incorrect currency symbol", currency.getSymbol(
                locale)
                .equals(df.getDecimalFormatSymbols().getCurrencySymbol()));
        assertTrue("Returned incorrect international currency symbol", currency
                .getCurrencyCode().equals(
                        df.getDecimalFormatSymbols()
                                .getInternationalCurrencySymbol()));
    }

    /**
     * @tests java.text.DecimalFormat#setGroupingSize(int)
     */
    public void test_setGroupingSizeI() {
        DecimalFormat df = new DecimalFormat("###0.##",
                new DecimalFormatSymbols(Locale.ENGLISH));
        df.setGroupingUsed(true);
        df.setGroupingSize(2);
        assertEquals("Value not set", 2, df.getGroupingSize());
        String result = df.format(123);
        assertTrue("Invalid format:" + result, result.equals("1,23"));
    }

    /**
     * @tests java.text.DecimalFormat#setMaximumFractionDigits(int)
     */
    public void test_setMaximumFractionDigitsI() {
        DecimalFormat df = new DecimalFormat("###0.##",
                                             new DecimalFormatSymbols(Locale.US));
        df.setMaximumFractionDigits(3);
        assertEquals("Not set", 3, df.getMaximumFractionDigits());
        assertEquals("Wrong maximum", "1.235", df.format(1.23456));
        df.setMinimumFractionDigits(4);
        assertEquals("Not changed", 4, df.getMaximumFractionDigits());
        assertEquals("Incorrect fraction", "456.0000", df.format(456));
    }

    /**
     * @tests java.text.DecimalFormat#setMaximumIntegerDigits(int)
     */
    public void test_setMaximumIntegerDigitsI() {
        DecimalFormat df = new DecimalFormat("###0.##");
        df.setMaximumIntegerDigits(2);
        assertEquals("Not set", 2, df.getMaximumIntegerDigits());
        assertEquals("Wrong maximum", "34", df.format(1234));
        df.setMinimumIntegerDigits(4);
        assertEquals("Not changed", 4, df.getMaximumIntegerDigits());
        assertEquals("Incorrect integer", "0026", df.format(26));
    }

    /**
     * @tests java.text.DecimalFormat#setMinimumFractionDigits(int)
     */
    public void test_setMinimumFractionDigitsI() {
        DecimalFormat df = new DecimalFormat("###0.##",
                                             new DecimalFormatSymbols(Locale.US));
        df.setMinimumFractionDigits(4);
        assertEquals("Not set", 4, df.getMinimumFractionDigits());
        assertEquals("Wrong minimum", "1.2300", df.format(1.23));
        df.setMaximumFractionDigits(2);
        assertEquals("Not changed", 2, df.getMinimumFractionDigits());
        assertEquals("Incorrect fraction", "456.00", df.format(456));
    }

    /**
     * @tests java.text.DecimalFormat#setMinimumIntegerDigits(int)
     */
    public void test_setMinimumIntegerDigitsI() {
        DecimalFormat df = new DecimalFormat("###0.##",
                                             new DecimalFormatSymbols(Locale.US));
        df.setMinimumIntegerDigits(3);
        assertEquals("Not set", 3, df.getMinimumIntegerDigits());
        assertEquals("Wrong minimum", "012", df.format(12));
        df.setMaximumIntegerDigits(2);
        assertEquals("Not changed", 2, df.getMinimumIntegerDigits());
        assertEquals("Incorrect integer", "00.7", df.format(0.7));
    }

    /**
     * @tests java.text.DecimalFormat#setMultiplier(int)
     */
    //FIXME This test fails on Harmony ClassLibrary
    public void test_setMultiplierI() {
        DecimalFormat df = new DecimalFormat("###0.##");
        df.setMultiplier(10);
        assertEquals("Wrong multiplier", 10, df.getMultiplier());
        assertEquals("Wrong format", "50", df.format(5));
        assertEquals("Wrong parse", 5, df.parse("50", new ParsePosition(0))
                .intValue());
        
        // regression test for HARMONY-879
        df.setMultiplier(-1);
        assertEquals("Wrong  multiplier for negative value", -1, df.getMultiplier());
    }

    /**
     * @tests serialization/deserialization compatibility.
     */
    public void testSerializationSelf() throws Exception {
        SerializationTest.verifySelf(new DecimalFormat());
    }

    /**
     * @tests serialization compatibility with RI
     */
    public void test_serializationHarmonyRICompatible() throws Exception {
        NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);

        DecimalFormat df = null;
        if (!(nf instanceof DecimalFormat)) {
            throw new Error("This NumberFormat is not a DecimalFormat");

        }
        df = (DecimalFormat) nf;

        ObjectInputStream oinput = null;

        DecimalFormat deserializedDF = null;

        try {
            oinput = new ObjectInputStream(this.getClass().getResource(
                    "/serialization/java/text/DecimalFormat.ser").openStream());
            deserializedDF = (DecimalFormat) oinput.readObject();
        } finally {
            try {
                if (null != oinput) {
                    oinput.close();
                }
            } catch (Exception e) {
                // ignore
            }
        }

        assertEquals(df.getNegativePrefix(), deserializedDF.getNegativePrefix());
        assertEquals(df.getNegativeSuffix(), deserializedDF.getNegativeSuffix());
        assertEquals(df.getPositivePrefix(), deserializedDF.getPositivePrefix());
        assertEquals(df.getPositiveSuffix(), deserializedDF.getPositiveSuffix());
        assertEquals(df.getCurrency(), deserializedDF.getCurrency());

        DecimalFormatSymbolsTest.assertDecimalFormatSymbolsRIFrance(deserializedDF.getDecimalFormatSymbols());

        assertEquals(df.getGroupingSize(), df.getGroupingSize());
        assertEquals(df.getMaximumFractionDigits(), deserializedDF
                .getMaximumFractionDigits());

        assertEquals(df.getMaximumIntegerDigits(), deserializedDF
                .getMaximumIntegerDigits());

        assertEquals(df.getMinimumFractionDigits(), deserializedDF
                .getMinimumFractionDigits());
        assertEquals(df.getMinimumIntegerDigits(), deserializedDF
                .getMinimumIntegerDigits());
        assertEquals(df.getMultiplier(), deserializedDF.getMultiplier());

        // Deliberately omitted this assertion. Since different data resource
        // will cause the assertion fail.
        // assertEquals(df, deserializedDF);

    }

    /**
     * Test whether DecimalFormat can parse Positive infinity correctly
     */
    public void testParseInfinityBigDecimalFalse() {
        // Regression test for HARMONY-106
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        Number number = format.parse(symbols.getInfinity(),
                new ParsePosition(0));
        assertTrue(number instanceof Double);
        assertTrue(Double.isInfinite(number.doubleValue()));
    }

    /**
     * Test whether DecimalFormat can parse Negative infinity correctly
     */
    public void testParseMinusInfinityBigDecimalFalse() {
        // Regression test for HARMONY-106
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        Number number = format.parse("-" + symbols.getInfinity(),
                new ParsePosition(0));
        assertTrue(number instanceof Double);
        assertTrue(Double.isInfinite(number.doubleValue()));
    }
    
    /**
     * Test if setDecimalFormatSymbols method wont throw NullPointerException 
     * when it is called with null parameter.
     */
    public void testSetDecimalFormatSymbolsAsNull(){
	// Regression for HARMONY-1070
        DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance();
        format.setDecimalFormatSymbols(null);                     
    }
}
