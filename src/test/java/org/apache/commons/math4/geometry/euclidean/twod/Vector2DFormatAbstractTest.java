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

package org.apache.commons.math4.geometry.euclidean.twod;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.apache.commons.math4.exception.MathParseException;
import org.apache.commons.math4.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math4.geometry.euclidean.twod.Vector2DFormat;
import org.junit.Assert;
import org.junit.Test;

public abstract class Vector2DFormatAbstractTest {

    Vector2DFormat vector2DFormat = null;
    Vector2DFormat vector2DFormatSquare = null;

    protected abstract Locale getLocale();

    protected abstract char getDecimalCharacter();

    protected Vector2DFormatAbstractTest() {
        vector2DFormat = Vector2DFormat.getInstance(getLocale());
        final NumberFormat nf = NumberFormat.getInstance(getLocale());
        nf.setMaximumFractionDigits(2);
        vector2DFormatSquare = new Vector2DFormat("[", "]", " : ", nf);
    }

    @Test
    public void testSimpleNoDecimals() {
        Vector2D c = new Vector2D(1, 1);
        String expected = "{1; 1}";
        String actual = vector2DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimals() {
        Vector2D c = new Vector2D(1.23, 1.43);
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43}";
        String actual = vector2DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimalsTrunc() {
        Vector2D c = new Vector2D(1.232323232323, 1.434343434343);
        String expected =
            "{1"    + getDecimalCharacter() +
            "2323232323; 1" + getDecimalCharacter() +
            "4343434343}";
        String actual = vector2DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeX() {
        Vector2D c = new Vector2D(-1.232323232323, 1.43);
        String expected =
            "{-1"    + getDecimalCharacter() +
            "2323232323; 1" + getDecimalCharacter() +
            "43}";
        String actual = vector2DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeY() {
        Vector2D c = new Vector2D(1.23, -1.434343434343);
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; -1" + getDecimalCharacter() +
            "4343434343}";
        String actual = vector2DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeZ() {
        Vector2D c = new Vector2D(1.23, 1.43);
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43}";
        String actual = vector2DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNonDefaultSetting() {
        Vector2D c = new Vector2D(1, 1);
        String expected = "[1 : 1]";
        String actual = vector2DFormatSquare.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDefaultFormatVector2D() {
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(getLocale());

        Vector2D c = new Vector2D(232.22222222222, -342.3333333333);
        String expected =
            "{232"    + getDecimalCharacter() +
            "2222222222; -342" + getDecimalCharacter() +
            "3333333333}";
        String actual = (new Vector2DFormat()).format(c);
        Assert.assertEquals(expected, actual);

        Locale.setDefault(defaultLocal);
    }

    @Test
    public void testNan() {
        Vector2D c = Vector2D.NaN;
        String expected = "{(NaN); (NaN)}";
        String actual = vector2DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPositiveInfinity() {
        Vector2D c = Vector2D.POSITIVE_INFINITY;
        String expected = "{(Infinity); (Infinity)}";
        String actual = vector2DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tesNegativeInfinity() {
        Vector2D c = Vector2D.NEGATIVE_INFINITY;
        String expected = "{(-Infinity); (-Infinity)}";
        String actual = vector2DFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleNoDecimals() throws MathParseException {
        String source = "{1; 1}";
        Vector2D expected = new Vector2D(1, 1);
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseIgnoredWhitespace() {
        Vector2D expected = new Vector2D(1, 1);
        ParsePosition pos1 = new ParsePosition(0);
        String source1 = "{1;1}";
        Assert.assertEquals(expected, vector2DFormat.parse(source1, pos1));
        Assert.assertEquals(source1.length(), pos1.getIndex());
        ParsePosition pos2 = new ParsePosition(0);
        String source2 = " { 1 ; 1 } ";
        Assert.assertEquals(expected, vector2DFormat.parse(source2, pos2));
        Assert.assertEquals(source2.length() - 1, pos2.getIndex());
    }

    @Test
    public void testParseSimpleWithDecimals() throws MathParseException {
        String source =
            "{1" + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43}";
        Vector2D expected = new Vector2D(1.23, 1.43);
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleWithDecimalsTrunc() throws MathParseException {
        String source =
            "{1" + getDecimalCharacter() +
            "2323; 1" + getDecimalCharacter() +
            "4343}";
        Vector2D expected = new Vector2D(1.2323, 1.4343);
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeX() throws MathParseException {
        String source =
            "{-1" + getDecimalCharacter() +
            "2323; 1" + getDecimalCharacter() +
            "4343}";
        Vector2D expected = new Vector2D(-1.2323, 1.4343);
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeY() throws MathParseException {
        String source =
            "{1" + getDecimalCharacter() +
            "2323; -1" + getDecimalCharacter() +
            "4343}";
        Vector2D expected = new Vector2D(1.2323, -1.4343);
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeZ() throws MathParseException {
        String source =
            "{1" + getDecimalCharacter() +
            "2323; 1" + getDecimalCharacter() +
            "4343}";
        Vector2D expected = new Vector2D(1.2323, 1.4343);
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeAll() throws MathParseException {
        String source =
            "{-1" + getDecimalCharacter() +
            "2323; -1" + getDecimalCharacter() +
            "4343}";
        Vector2D expected = new Vector2D(-1.2323, -1.4343);
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseZeroX() throws MathParseException {
        String source =
            "{0" + getDecimalCharacter() +
            "0; -1" + getDecimalCharacter() +
            "4343}";
        Vector2D expected = new Vector2D(0.0, -1.4343);
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNonDefaultSetting() throws MathParseException {
        String source =
            "[1" + getDecimalCharacter() +
            "2323 : 1" + getDecimalCharacter() +
            "4343]";
        Vector2D expected = new Vector2D(1.2323, 1.4343);
        Vector2D actual = vector2DFormatSquare.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNan() throws MathParseException {
        String source = "{(NaN); (NaN)}";
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(Vector2D.NaN, actual);
    }

    @Test
    public void testParsePositiveInfinity() throws MathParseException {
        String source = "{(Infinity); (Infinity)}";
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(Vector2D.POSITIVE_INFINITY, actual);
    }

    @Test
    public void testParseNegativeInfinity() throws MathParseException {
        String source = "{(-Infinity); (-Infinity)}";
        Vector2D actual = vector2DFormat.parse(source);
        Assert.assertEquals(Vector2D.NEGATIVE_INFINITY, actual);
    }

    @Test
    public void testConstructorSingleFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        Vector2DFormat cf = new Vector2DFormat(nf);
        Assert.assertNotNull(cf);
        Assert.assertEquals(nf, cf.getFormat());
    }

    @Test
    public void testForgottenPrefix() {
        ParsePosition pos = new ParsePosition(0);
        Assert.assertNull(new Vector2DFormat().parse("1; 1}", pos));
        Assert.assertEquals(0, pos.getErrorIndex());
    }

    @Test
    public void testForgottenSeparator() {
        ParsePosition pos = new ParsePosition(0);
        Assert.assertNull(new Vector2DFormat().parse("{1 1}", pos));
        Assert.assertEquals(3, pos.getErrorIndex());
    }

    @Test
    public void testForgottenSuffix() {
        ParsePosition pos = new ParsePosition(0);
        Assert.assertNull(new Vector2DFormat().parse("{1; 1 ", pos));
        Assert.assertEquals(5, pos.getErrorIndex());
    }

}
