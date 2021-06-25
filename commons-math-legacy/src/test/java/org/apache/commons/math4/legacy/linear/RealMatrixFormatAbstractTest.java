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

package org.apache.commons.math4.legacy.linear;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.math4.legacy.exception.MathParseException;

public abstract class RealMatrixFormatAbstractTest {

    private RealMatrixFormat realMatrixFormat = null;
    private RealMatrixFormat realMatrixFormatOctave = null;

    protected abstract Locale getLocale();

    protected abstract char getDecimalCharacter();

    public RealMatrixFormatAbstractTest() {
        realMatrixFormat = RealMatrixFormat.getInstance(getLocale());
        final NumberFormat nf = NumberFormat.getInstance(getLocale());
        nf.setMaximumFractionDigits(2);
        realMatrixFormatOctave = new RealMatrixFormat("[", "]", "", "", "; ", ", ", nf);
    }

    @Test
    public void testSimpleNoDecimals() {
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {{1, 1, 1}, {1, 1, 1}});
        String expected = "{{1,1,1},{1,1,1}}";
        String actual = realMatrixFormat.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimals() {
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {{1.23, 1.43, 1.63}, {2.46, 2.46, 2.66}});
        String expected =
            "{{1"    + getDecimalCharacter() +
            "23,1" + getDecimalCharacter() +
            "43,1" + getDecimalCharacter() +
            "63},{2" + getDecimalCharacter() +
            "46,2" + getDecimalCharacter() +
            "46,2" + getDecimalCharacter() +
            "66}}";
        String actual = realMatrixFormat.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimalsTrunc() {
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {{1.232323232323, 1.43, 1.63},
                                                                    {2.46, 2.46, 2.666666666666}});
        String expected =
                "{{1"    + getDecimalCharacter() +
                "2323232323,1" + getDecimalCharacter() +
                "43,1" + getDecimalCharacter() +
                "63},{2" + getDecimalCharacter() +
                "46,2" + getDecimalCharacter() +
                "46,2" + getDecimalCharacter() +
                "6666666667}}";
        String actual = realMatrixFormat.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeComponent() {
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {{-1.232323232323, 1.43, 1.63},
                                                                    {2.46, 2.46, 2.66}});
        String expected =
                "{{-1"    + getDecimalCharacter() +
                "2323232323,1" + getDecimalCharacter() +
                "43,1" + getDecimalCharacter() +
                "63},{2" + getDecimalCharacter() +
                "46,2" + getDecimalCharacter() +
                "46,2" + getDecimalCharacter() +
                "66}}";
        String actual = realMatrixFormat.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeComponent2() {
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {{1.23, -1.434343434343, 1.63},
                                                                    {2.46, 2.46, 2.66}});
        String expected =
                "{{1"    + getDecimalCharacter() +
                "23,-1" + getDecimalCharacter() +
                "4343434343,1" + getDecimalCharacter() +
                "63},{2" + getDecimalCharacter() +
                "46,2" + getDecimalCharacter() +
                "46,2" + getDecimalCharacter() +
                "66}}";
        String actual = realMatrixFormat.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeSecondRow() {
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {{1.23, 1.43, 1.63},
                                                                    {-2.66666666666, 2.46, 2.66}});
        String expected =
                "{{1"    + getDecimalCharacter() +
                "23,1" + getDecimalCharacter() +
                "43,1" + getDecimalCharacter() +
                "63},{-2" + getDecimalCharacter() +
                "6666666667,2" + getDecimalCharacter() +
                "46,2" + getDecimalCharacter() +
                "66}}";
        String actual = realMatrixFormat.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNonDefaultSetting() {
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {{1, 1, 1}, {1, 1, 1}});
        String expected = "[1, 1, 1; 1, 1, 1]";
        String actual = realMatrixFormatOctave.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDefaultFormat() {
        Locale defaultLocale = Locale.getDefault();
        Locale.setDefault(getLocale());

        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {{232.2222222222, -342.33333333333, 432.44444444444}});
        String expected =
            "{{232"    + getDecimalCharacter() +
            "2222222222,-342" + getDecimalCharacter() +
            "3333333333,432" + getDecimalCharacter() +
            "4444444444}}";
        String actual = (new RealMatrixFormat()).format(m);
        Assert.assertEquals(expected, actual);

        Locale.setDefault(defaultLocale);
    }

    @Test
    public void testNan() {
        RealMatrix m = MatrixUtils.createRealMatrix(new double[][] {{Double.NaN, Double.NaN, Double.NaN}});
        String expected = "{{(NaN),(NaN),(NaN)}}";
        String actual = realMatrixFormat.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPositiveInfinity() {
        RealMatrix m = MatrixUtils.createRealMatrix(
                new double[][] {{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY}});
        String expected = "{{(Infinity),(Infinity),(Infinity)}}";
        String actual = realMatrixFormat.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tesNegativeInfinity() {
        RealMatrix m = MatrixUtils.createRealMatrix(
                new double[][] {{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY}});
        String expected = "{{(-Infinity),(-Infinity),(-Infinity)}}";
        String actual = realMatrixFormat.format(m);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleNoDecimals() {
        String source = "{{1, 1, 1}, {1, 1, 1}}";
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{1, 1, 1}, {1, 1, 1}});
        RealMatrix actual = realMatrixFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    @Ignore
    public void testParseSimpleWithClosingRowSeparator() {
        String source = "{{1, 1, 1},{1, 1, 1}, }}";
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{1, 1, 1}, {1, 1, 1}});
        RealMatrix actual = realMatrixFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseIgnoredWhitespace() {
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{1, 1, 1}, {1, 1, 1}});
        ParsePosition pos1 = new ParsePosition(0);
        String source1 = "{{1,1,1},{1,1,1}}";
        Assert.assertEquals(expected, realMatrixFormat.parse(source1, pos1));
        Assert.assertEquals(source1.length(), pos1.getIndex());
        ParsePosition pos2 = new ParsePosition(0);
        String source2 = " { { 1 , 1 , 1 } , { 1 , 1 , 1 } } ";
        Assert.assertEquals(expected, realMatrixFormat.parse(source2, pos2));
        Assert.assertEquals(source2.length() - 1, pos2.getIndex());
    }

    @Test
    public void testParseSimpleWithDecimals() {
        String source =
            "{{1" + getDecimalCharacter() +
            "23,1" + getDecimalCharacter() +
            "43,1" + getDecimalCharacter() +
            "63}}";
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{1.23, 1.43, 1.63}});
        RealMatrix actual = realMatrixFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleWithDecimalsTrunc() {
        String source =
            "{{1" + getDecimalCharacter() +
            "2323,1" + getDecimalCharacter() +
            "4343,1" + getDecimalCharacter() +
            "6333}}";
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{1.2323, 1.4343, 1.6333}});
        RealMatrix actual = realMatrixFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeComponent() {
        String source =
            "{{-1" + getDecimalCharacter() +
            "2323,1" + getDecimalCharacter() +
            "4343,1" + getDecimalCharacter() +
            "6333}}";
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{-1.2323, 1.4343, 1.6333}});
        RealMatrix actual = realMatrixFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeAll() {
        String source =
            "{{-1" + getDecimalCharacter() +
            "2323,-1" + getDecimalCharacter() +
            "4343,-1" + getDecimalCharacter() +
            "6333}}";
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{-1.2323, -1.4343, -1.6333}});
        RealMatrix actual = realMatrixFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseZeroComponent() {
        String source =
            "{{0" + getDecimalCharacter() +
            "0,-1" + getDecimalCharacter() +
            "4343,1" + getDecimalCharacter() +
            "6333}}";
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{0.0, -1.4343, 1.6333}});
        RealMatrix actual = realMatrixFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNonDefaultSetting() {
        String source =
            "[1" + getDecimalCharacter() +
            "2323, 1" + getDecimalCharacter() +
            "4343, 1" + getDecimalCharacter() +
            "6333]";
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{1.2323, 1.4343, 1.6333}});
        RealMatrix actual = realMatrixFormatOctave.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNan() {
        String source = "{{(NaN), (NaN), (NaN)}}";
        RealMatrix actual = realMatrixFormat.parse(source);
        RealMatrix expected = MatrixUtils.createRealMatrix(new double[][] {{Double.NaN, Double.NaN, Double.NaN}});
        for (int i = 0; i < expected.getRowDimension(); i++) {
            for (int j = 0; j < expected.getColumnDimension(); j++) {
                Assert.assertTrue(Double.isNaN(actual.getEntry(i, j)));
            }
        }
    }

    @Test
    public void testParsePositiveInfinity() {
        String source = "{{(Infinity), (Infinity), (Infinity)}}";
        RealMatrix actual = realMatrixFormat.parse(source);
        RealMatrix expected = MatrixUtils.createRealMatrix(
                new double[][] {{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY}});
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeInfinity() {
        String source = "{{(-Infinity), (-Infinity), (-Infinity)}}";
        RealMatrix actual = realMatrixFormat.parse(source);
        RealMatrix expected = MatrixUtils.createRealMatrix(
                new double[][] {{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY}});
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNoComponents() {
        try {
            realMatrixFormat.parse("{{ }}");
            Assert.fail("Expecting MathParseException");
        } catch (MathParseException pe) {
            // expected behavior
        }
    }

    @Test
    public void testParseManyComponents() {
        RealMatrix parsed = realMatrixFormat.parse("{{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}}");
        Assert.assertEquals(24, parsed.getColumnDimension());
    }

    @Test
    public void testConstructorSingleFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        RealMatrixFormat mf = new RealMatrixFormat(nf);
        Assert.assertNotNull(mf);
        Assert.assertEquals(nf, mf.getFormat());
    }

    @Test
    public void testForgottenPrefix() {
        ParsePosition pos = new ParsePosition(0);
        final String source = "1; 1; 1]";
        Assert.assertNull("Should not parse <"+source+">", realMatrixFormat.parse(source, pos));
        Assert.assertEquals(0, pos.getErrorIndex());
    }

    @Test
    public void testForgottenSeparator() {
        ParsePosition pos = new ParsePosition(0);
        final String source = "{{1, 1 1}}";
        Assert.assertNull("Should not parse <"+source+">", realMatrixFormat.parse(source, pos));
        Assert.assertEquals(7, pos.getErrorIndex());
    }

    @Test
    public void testForgottenSuffix() {
        ParsePosition pos = new ParsePosition(0);
        final String source = "{{1, 1, 1 ";
        Assert.assertNull("Should not parse <"+source+">", realMatrixFormat.parse(source, pos));
        Assert.assertEquals(9, pos.getErrorIndex());
    }
}
