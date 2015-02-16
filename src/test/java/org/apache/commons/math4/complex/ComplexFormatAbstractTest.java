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

package org.apache.commons.math4.complex;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.math4.complex.Complex;
import org.apache.commons.math4.complex.ComplexFormat;
import org.apache.commons.math4.util.FastMath;

public abstract class ComplexFormatAbstractTest {

    ComplexFormat complexFormat = null;
    ComplexFormat complexFormatJ = null;

    protected abstract Locale getLocale();

    protected abstract char getDecimalCharacter();

    protected ComplexFormatAbstractTest() {
        complexFormat = ComplexFormat.getInstance(getLocale());
        complexFormatJ = ComplexFormat.getInstance("j", getLocale());
    }

    @Test
    public void testSimpleNoDecimals() {
        Complex c = new Complex(1, 2);
        String expected = "1 + 2i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testTrimOneImaginary() {
        final ComplexFormat fmt = ComplexFormat.getInstance(getLocale());
        fmt.getImaginaryFormat().setMaximumFractionDigits(1);

        Complex c = new Complex(1, 1.04);
        String expected = "1 + i";
        String actual = fmt.format(c);
        Assert.assertEquals(expected, actual);

        c = new Complex(1, 1.09);
        expected = "1 + 1" + getDecimalCharacter() + "1i";
        actual = fmt.format(c);
        Assert.assertEquals(expected, actual);

        c = new Complex(1, -1.09);
        expected = "1 - 1" + getDecimalCharacter() + "1i";
        actual = fmt.format(c);
        Assert.assertEquals(expected, actual);

        c = new Complex(1, -1.04);
        expected = "1 - i";
        actual = fmt.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimals() {
        Complex c = new Complex(1.23, 1.43);
        String expected = "1" + getDecimalCharacter() + "23 + 1" + getDecimalCharacter() + "43i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimalsTrunc() {
        Complex c = new Complex(1.232323232323, 1.434343434343);
        String expected = "1" + getDecimalCharacter() + "2323232323 + 1" + getDecimalCharacter() + "4343434343i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeReal() {
        Complex c = new Complex(-1.232323232323, 1.43);
        String expected = "-1" + getDecimalCharacter() + "2323232323 + 1" + getDecimalCharacter() + "43i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeImaginary() {
        Complex c = new Complex(1.23, -1.434343434343);
        String expected = "1" + getDecimalCharacter() + "23 - 1" + getDecimalCharacter() + "4343434343i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeBoth() {
        Complex c = new Complex(-1.232323232323, -1.434343434343);
        String expected = "-1" + getDecimalCharacter() + "2323232323 - 1" + getDecimalCharacter() + "4343434343i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testZeroReal() {
        Complex c = new Complex(0.0, -1.434343434343);
        String expected = "0 - 1" + getDecimalCharacter() + "4343434343i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testZeroImaginary() {
        Complex c = new Complex(30.23333333333, 0);
        String expected = "30" + getDecimalCharacter() + "2333333333";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDifferentImaginaryChar() {
        Complex c = new Complex(1, 1);
        String expected = "1 + j";
        String actual = complexFormatJ.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDefaultFormatComplex() {
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(getLocale());

        Complex c = new Complex(232.22222222222, -342.3333333333);
        String expected = "232" + getDecimalCharacter() + "2222222222 - 342" + getDecimalCharacter() + "3333333333i";
        String actual = (new ComplexFormat()).format(c);
        Assert.assertEquals(expected, actual);

        Locale.setDefault(defaultLocal);
    }

    @Test
    public void testNan() {
        Complex c = new Complex(Double.NaN, Double.NaN);
        String expected = "(NaN) + (NaN)i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPositiveInfinity() {
        Complex c = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        String expected = "(Infinity) + (Infinity)i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeInfinity() {
        Complex c = new Complex(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        String expected = "(-Infinity) - (Infinity)i";
        String actual = complexFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleNoDecimals() {
        String source = "1 + 1i";
        Complex expected = new Complex(1, 1);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleWithDecimals() {
        String source = "1" + getDecimalCharacter() + "23 + 1" + getDecimalCharacter() + "43i";
        Complex expected = new Complex(1.23, 1.43);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleWithDecimalsTrunc() {
        String source = "1" + getDecimalCharacter() + "232323232323 + 1" + getDecimalCharacter() + "434343434343i";
        Complex expected = new Complex(1.232323232323, 1.434343434343);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeReal() {
        String source = "-1" + getDecimalCharacter() + "232323232323 + 1" + getDecimalCharacter() + "4343i";
        Complex expected = new Complex(-1.232323232323, 1.4343);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeImaginary() {
        String source = "1" + getDecimalCharacter() + "2323 - 1" + getDecimalCharacter() + "434343434343i";
        Complex expected = new Complex(1.2323, -1.434343434343);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeBoth() {
        String source = "-1" + getDecimalCharacter() + "232323232323 - 1" + getDecimalCharacter() + "434343434343i";
        Complex expected = new Complex(-1.232323232323, -1.434343434343);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseZeroReal() {
        String source = "0" + getDecimalCharacter() + "0 - 1" + getDecimalCharacter() + "4343i";
        Complex expected = new Complex(0.0, -1.4343);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseZeroImaginary() {
        String source = "-1" + getDecimalCharacter() + "2323";
        Complex expected = new Complex(-1.2323, 0);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseDifferentImaginaryChar() {
        String source = "-1" + getDecimalCharacter() + "2323 - 1" + getDecimalCharacter() + "4343j";
        Complex expected = new Complex(-1.2323, -1.4343);
        Complex actual = complexFormatJ.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNan() {
        String source = "(NaN) + (NaN)i";
        Complex expected = new Complex(Double.NaN, Double.NaN);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParsePositiveInfinity() {
        String source = "(Infinity) + (Infinity)i";
        Complex expected = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPaseNegativeInfinity() {
        String source = "(-Infinity) - (Infinity)i";
        Complex expected = new Complex(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        Complex actual = complexFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testConstructorSingleFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        ComplexFormat cf = new ComplexFormat(nf);
        Assert.assertNotNull(cf);
        Assert.assertEquals(nf, cf.getRealFormat());
    }

    @Test
    public void testGetImaginaryFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        ComplexFormat cf = new ComplexFormat(nf);
        Assert.assertSame(nf, cf.getImaginaryFormat());
    }

    @Test
    public void testGetRealFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        ComplexFormat cf = new ComplexFormat(nf);
        Assert.assertSame(nf, cf.getRealFormat());
    }

    @Test
    public void testFormatNumber() {
        ComplexFormat cf = ComplexFormat.getInstance(getLocale());
        Double pi = Double.valueOf(FastMath.PI);
        String text = cf.format(pi);
        Assert.assertEquals("3" + getDecimalCharacter() + "1415926536", text);
    }

    @Test
    public void testForgottenImaginaryCharacter() {
        ParsePosition pos = new ParsePosition(0);
        Assert.assertNull(new ComplexFormat().parse("1 + 1", pos));
        Assert.assertEquals(5, pos.getErrorIndex());
    }
}
