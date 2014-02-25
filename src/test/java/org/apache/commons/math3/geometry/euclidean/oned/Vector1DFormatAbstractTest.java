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

package org.apache.commons.math3.geometry.euclidean.oned;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.math3.exception.MathParseException;
import org.junit.Assert;
import org.junit.Test;

public abstract class Vector1DFormatAbstractTest {

    Vector1DFormat vector1DFormat = null;
    Vector1DFormat vector1DFormatSquare = null;

    protected abstract Locale getLocale();

    protected abstract char getDecimalCharacter();

    protected Vector1DFormatAbstractTest() {
        vector1DFormat = Vector1DFormat.getInstance(getLocale());
        final NumberFormat nf = NumberFormat.getInstance(getLocale());
        nf.setMaximumFractionDigits(2);
        vector1DFormatSquare = new Vector1DFormat("[", "]", nf);
    }

    @Test
    public void testSimpleNoDecimals() {
        Vector1D c = new Vector1D(1);
        String expected = "{1}";
        String actual = vector1DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimals() {
        Vector1D c = new Vector1D(1.23);
        String expected =
            "{1"    + getDecimalCharacter() +
            "23}";
        String actual = vector1DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimalsTrunc() {
        Vector1D c = new Vector1D(1.232323232323);
        String expected =
            "{1"    + getDecimalCharacter() +
            "2323232323}";
        String actual = vector1DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeX() {
        Vector1D c = new Vector1D(-1.232323232323);
        String expected =
            "{-1"    + getDecimalCharacter() +
            "2323232323}";
        String actual = vector1DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNonDefaultSetting() {
        Vector1D c = new Vector1D(1);
        String expected = "[1]";
        String actual = vector1DFormatSquare.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDefaultFormatVector1D() {
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(getLocale());

        Vector1D c = new Vector1D(232.22222222222);
        String expected =
            "{232"    + getDecimalCharacter() +
            "2222222222}";
        String actual = (new Vector1DFormat()).format(c);
        Assert.assertEquals(expected, actual);

        Locale.setDefault(defaultLocal);
    }

    @Test
    public void testNan() {
        Vector1D c = Vector1D.NaN;
        String expected = "{(NaN)}";
        String actual = vector1DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPositiveInfinity() {
        Vector1D c = Vector1D.POSITIVE_INFINITY;
        String expected = "{(Infinity)}";
        String actual = vector1DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tesNegativeInfinity() {
        Vector1D c = Vector1D.NEGATIVE_INFINITY;
        String expected = "{(-Infinity)}";
        String actual = vector1DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleNoDecimals() throws MathParseException {
        String source = "{1}";
        Vector1D expected = new Vector1D(1);
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseIgnoredWhitespace() {
        Vector1D expected = new Vector1D(1);
        ParsePosition pos1 = new ParsePosition(0);
        String source1 = "{1}";
        Assert.assertEquals(expected, vector1DFormat.parse(source1, pos1));
        Assert.assertEquals(source1.length(), pos1.getIndex());
        ParsePosition pos2 = new ParsePosition(0);
        String source2 = " { 1 } ";
        Assert.assertEquals(expected, vector1DFormat.parse(source2, pos2));
        Assert.assertEquals(source2.length() - 1, pos2.getIndex());
    }

    @Test
    public void testParseSimpleWithDecimals() throws MathParseException {
        String source =
            "{1" + getDecimalCharacter() +
            "23}";
        Vector1D expected = new Vector1D(1.23);
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleWithDecimalsTrunc() throws MathParseException {
        String source =
            "{1" + getDecimalCharacter() +
            "2323}";
        Vector1D expected = new Vector1D(1.2323);
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeX() throws MathParseException {
        String source =
            "{-1" + getDecimalCharacter() +
            "2323}";
        Vector1D expected = new Vector1D(-1.2323);
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeY() throws MathParseException {
        String source =
            "{1" + getDecimalCharacter() +
            "2323}";
        Vector1D expected = new Vector1D(1.2323);
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeZ() throws MathParseException {
        String source =
            "{1" + getDecimalCharacter() +
            "2323}";
        Vector1D expected = new Vector1D(1.2323);
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeAll() throws MathParseException {
        String source =
            "{-1" + getDecimalCharacter() +
            "2323}";
        Vector1D expected = new Vector1D(-1.2323);
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseZeroX() throws MathParseException {
        String source =
            "{0" + getDecimalCharacter() +
            "0}";
        Vector1D expected = new Vector1D(0.0);
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNonDefaultSetting() throws MathParseException {
        String source =
            "[1" + getDecimalCharacter() +
            "2323]";
        Vector1D expected = new Vector1D(1.2323);
        Vector1D actual = vector1DFormatSquare.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNan() throws MathParseException {
        String source = "{(NaN)}";
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(Vector1D.NaN, actual);
    }

    @Test
    public void testParsePositiveInfinity() throws MathParseException {
        String source = "{(Infinity)}";
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(Vector1D.POSITIVE_INFINITY, actual);
    }

    @Test
    public void testParseNegativeInfinity() throws MathParseException {
        String source = "{(-Infinity)}";
        Vector1D actual = vector1DFormat.parse(source);
        Assert.assertEquals(Vector1D.NEGATIVE_INFINITY, actual);
    }

    @Test
    public void testConstructorSingleFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        Vector1DFormat cf = new Vector1DFormat(nf);
        Assert.assertNotNull(cf);
        Assert.assertEquals(nf, cf.getFormat());
    }

    @Test
    public void testForgottenPrefix() {
        ParsePosition pos = new ParsePosition(0);
        Assert.assertNull(new Vector1DFormat().parse("1}", pos));
        Assert.assertEquals(0, pos.getErrorIndex());
    }

    @Test
    public void testForgottenSuffix() {
        ParsePosition pos = new ParsePosition(0);
        Assert.assertNull(new Vector1DFormat().parse("{1 ", pos));
        Assert.assertEquals(2, pos.getErrorIndex());
    }

}
