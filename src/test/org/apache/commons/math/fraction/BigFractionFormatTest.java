/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math.fraction;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import junit.framework.TestCase;

public class BigFractionFormatTest extends TestCase {
 
    BigFractionFormat properFormat = null;
    BigFractionFormat improperFormat = null;

    protected Locale getLocale() {
        return Locale.getDefault();
    }

    protected void setUp() throws Exception {
        properFormat = BigFractionFormat.getProperInstance(getLocale());
        improperFormat = BigFractionFormat.getImproperInstance(getLocale());
    }
   
    public void testFormat() {
        BigFraction c = new BigFraction(1, 2);
        String expected = "1 / 2";
        
        String actual = properFormat.format(c); 
        assertEquals(expected, actual);

        actual = improperFormat.format(c);
        assertEquals(expected, actual);
    }

    public void testFormatNegative() {
        BigFraction c = new BigFraction(-1, 2);
        String expected = "-1 / 2";

        String actual = properFormat.format(c); 
        assertEquals(expected, actual);

        actual = improperFormat.format(c); 
        assertEquals(expected, actual);
    }

    public void testFormatZero() {
        BigFraction c = new BigFraction(0, 1);
        String expected = "0 / 1";

        String actual = properFormat.format(c); 
        assertEquals(expected, actual);

        actual = improperFormat.format(c); 
        assertEquals(expected, actual);
    }
    
    public void testFormatImproper() {
        BigFraction c = new BigFraction(5, 3);

        String actual = properFormat.format(c); 
        assertEquals("1 2 / 3", actual);

        actual = improperFormat.format(c); 
        assertEquals("5 / 3", actual);
    }
    
    public void testFormatImproperNegative() {
        BigFraction c = new BigFraction(-5, 3);

        String actual = properFormat.format(c); 
        assertEquals("-1 2 / 3", actual);

        actual = improperFormat.format(c); 
        assertEquals("-5 / 3", actual);
    }
    
    public void testParse() {
        String source = "1 / 2";

        try {
            BigFraction c = properFormat.parse(source);
            assertNotNull(c);
            assertEquals(BigInteger.ONE, c.getNumerator());
            assertEquals(BigInteger.valueOf(2l), c.getDenominator());
            
            c = improperFormat.parse(source);
            assertNotNull(c);
            assertEquals(BigInteger.ONE, c.getNumerator());
            assertEquals(BigInteger.valueOf(2l), c.getDenominator());
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }
    
    public void testParseInteger() {
        String source = "10";
        try {
            BigFraction c = properFormat.parse(source);
            assertNotNull(c);
            assertEquals(BigInteger.TEN, c.getNumerator());
            assertEquals(BigInteger.ONE, c.getDenominator());
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
        try {
            BigFraction c = improperFormat.parse(source);
            assertNotNull(c);
            assertEquals(BigInteger.TEN, c.getNumerator());
            assertEquals(BigInteger.ONE, c.getDenominator());
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }
    
    public void testParseInvalid() {
        String source = "a";
        String msg = "should not be able to parse '10 / a'.";
        try {
            properFormat.parse(source);
            fail(msg);
        } catch (ParseException ex) {
            // success
        }
        try {
            improperFormat.parse(source);
            fail(msg);
        } catch (ParseException ex) {
            // success
        }
    }
    
    public void testParseInvalidDenominator() {
        String source = "10 / a";
        String msg = "should not be able to parse '10 / a'.";
        try {
            properFormat.parse(source);
            fail(msg);
        } catch (ParseException ex) {
            // success
        }
        try {
            improperFormat.parse(source);
            fail(msg);
        } catch (ParseException ex) {
            // success
        }
    }
    
    public void testParseNegative() {

        try {
            String source = "-1 / 2";
            BigFraction c = properFormat.parse(source);
            assertNotNull(c);
            assertEquals(-1, c.getNumeratorAsInt());
            assertEquals(2, c.getDenominatorAsInt());
            
            c = improperFormat.parse(source);
            assertNotNull(c);
            assertEquals(-1, c.getNumeratorAsInt());
            assertEquals(2, c.getDenominatorAsInt());

            source = "1 / -2";
            c = properFormat.parse(source);
            assertNotNull(c);
            assertEquals(-1, c.getNumeratorAsInt());
            assertEquals(2, c.getDenominatorAsInt());
            
            c = improperFormat.parse(source);
            assertNotNull(c);
            assertEquals(-1, c.getNumeratorAsInt());
            assertEquals(2, c.getDenominatorAsInt());
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
    }
    
    public void testParseProper() {
        String source = "1 2 / 3";

        try {
            BigFraction c = properFormat.parse(source);
            assertNotNull(c);
            assertEquals(5, c.getNumeratorAsInt());
            assertEquals(3, c.getDenominatorAsInt());
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
        
        try {
            improperFormat.parse(source);
            fail("invalid improper fraction.");
        } catch (ParseException ex) {
            // success
        }
    }
    
    public void testParseProperNegative() {
        String source = "-1 2 / 3";
        try {
            BigFraction c = properFormat.parse(source);
            assertNotNull(c);
            assertEquals(-5, c.getNumeratorAsInt());
            assertEquals(3, c.getDenominatorAsInt());
        } catch (ParseException ex) {
            fail(ex.getMessage());
        }
        
        try {
            improperFormat.parse(source);
            fail("invalid improper fraction.");
        } catch (ParseException ex) {
            // success
        }
    }
    
    public void testParseProperInvalidMinus() {
        String source = "2 -2 / 3";
        try {
            properFormat.parse(source);
            fail("invalid minus in improper fraction.");
        } catch (ParseException ex) {
            // expected
        }
        source = "2 2 / -3";
        try {
            properFormat.parse(source);
            fail("invalid minus in improper fraction.");
        } catch (ParseException ex) {
            // expected
        }
    }
    
    public void testNumeratorFormat() {
        NumberFormat old = properFormat.getNumeratorFormat();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setParseIntegerOnly(true);
        properFormat.setNumeratorFormat(nf);
        assertEquals(nf, properFormat.getNumeratorFormat());
        properFormat.setNumeratorFormat(old);

        old = improperFormat.getNumeratorFormat();
        nf = NumberFormat.getInstance();
        nf.setParseIntegerOnly(true);
        improperFormat.setNumeratorFormat(nf);
        assertEquals(nf, improperFormat.getNumeratorFormat());
        improperFormat.setNumeratorFormat(old);
    }
    
    public void testDenominatorFormat() {
        NumberFormat old = properFormat.getDenominatorFormat();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setParseIntegerOnly(true);
        properFormat.setDenominatorFormat(nf);
        assertEquals(nf, properFormat.getDenominatorFormat());
        properFormat.setDenominatorFormat(old);

        old = improperFormat.getDenominatorFormat();
        nf = NumberFormat.getInstance();
        nf.setParseIntegerOnly(true);
        improperFormat.setDenominatorFormat(nf);
        assertEquals(nf, improperFormat.getDenominatorFormat());
        improperFormat.setDenominatorFormat(old);
    }
    
    public void testWholeFormat() {
        ProperBigFractionFormat format = (ProperBigFractionFormat)properFormat;
        
        NumberFormat old = format.getWholeFormat();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setParseIntegerOnly(true);
        format.setWholeFormat(nf);
        assertEquals(nf, format.getWholeFormat());
        format.setWholeFormat(old);
    }
    
    public void testLongFormat() {
        assertEquals("10 / 1", improperFormat.format(10l));
    }
    
    public void testDoubleFormat() {
        assertEquals("1 / 16", improperFormat.format(0.0625));
    }
}
