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

package org.apache.commons.math4.linear;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.math4.exception.MathParseException;
import org.apache.commons.math4.linear.ArrayRealVector;
import org.apache.commons.math4.linear.RealVectorFormat;

public abstract class RealVectorFormatAbstractTest {

    RealVectorFormat realVectorFormat = null;
    RealVectorFormat realVectorFormatSquare = null;

    protected abstract Locale getLocale();

    protected abstract char getDecimalCharacter();

    public RealVectorFormatAbstractTest() {
        realVectorFormat = RealVectorFormat.getInstance(getLocale());
        final NumberFormat nf = NumberFormat.getInstance(getLocale());
        nf.setMaximumFractionDigits(2);
        realVectorFormatSquare = new RealVectorFormat("[", "]", " : ", nf);
    }

    @Test
    public void testSimpleNoDecimals() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1, 1, 1});
        String expected = "{1; 1; 1}";
        String actual = realVectorFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimals() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1.23, 1.43, 1.63});
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43; 1" + getDecimalCharacter() +
            "63}";
        String actual = realVectorFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testSimpleWithDecimalsTrunc() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1.232323232323, 1.43434343434343, 1.633333333333});
        String expected =
            "{1"    + getDecimalCharacter() +
            "2323232323; 1" + getDecimalCharacter() +
            "4343434343; 1" + getDecimalCharacter() +
            "6333333333}";
        String actual = realVectorFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeX() {
        ArrayRealVector c = new ArrayRealVector(new double[] {-1.232323232323, 1.43, 1.63});
        String expected =
            "{-1"    + getDecimalCharacter() +
            "2323232323; 1" + getDecimalCharacter() +
            "43; 1" + getDecimalCharacter() +
            "63}";
        String actual = realVectorFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeY() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1.23, -1.434343434343, 1.63});
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; -1" + getDecimalCharacter() +
            "4343434343; 1" + getDecimalCharacter() +
            "63}";
        String actual = realVectorFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNegativeZ() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1.23, 1.43, -1.633333333333});
        String expected =
            "{1"    + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43; -1" + getDecimalCharacter() +
            "6333333333}";
        String actual = realVectorFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testNonDefaultSetting() {
        ArrayRealVector c = new ArrayRealVector(new double[] {1, 1, 1});
        String expected = "[1 : 1 : 1]";
        String actual = realVectorFormatSquare.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testDefaultFormatRealVectorImpl() {
        Locale defaultLocal = Locale.getDefault();
        Locale.setDefault(getLocale());

        ArrayRealVector c = new ArrayRealVector(new double[] {232.22222222222, -342.3333333333, 432.44444444444});
        String expected =
            "{232"    + getDecimalCharacter() +
            "2222222222; -342" + getDecimalCharacter() +
            "3333333333; 432" + getDecimalCharacter() +
            "4444444444}";
        String actual = (new RealVectorFormat()).format(c);
        Assert.assertEquals(expected, actual);

        Locale.setDefault(defaultLocal);
    }

    @Test
    public void testNan() {
        ArrayRealVector c = new ArrayRealVector(new double[] {Double.NaN, Double.NaN, Double.NaN});
        String expected = "{(NaN); (NaN); (NaN)}";
        String actual = realVectorFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testPositiveInfinity() {
        ArrayRealVector c = new ArrayRealVector(new double[] {
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
        });
        String expected = "{(Infinity); (Infinity); (Infinity)}";
        String actual = realVectorFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void tesNegativeInfinity() {
        ArrayRealVector c = new ArrayRealVector(new double[] {
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY
        });
        String expected = "{(-Infinity); (-Infinity); (-Infinity)}";
        String actual = realVectorFormat.format(c);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleNoDecimals() {
        String source = "{1; 1; 1}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1, 1, 1});
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseIgnoredWhitespace() {
        ArrayRealVector expected = new ArrayRealVector(new double[] {1, 1, 1});
        ParsePosition pos1 = new ParsePosition(0);
        String source1 = "{1;1;1}";
        Assert.assertEquals(expected, realVectorFormat.parse(source1, pos1));
        Assert.assertEquals(source1.length(), pos1.getIndex());
        ParsePosition pos2 = new ParsePosition(0);
        String source2 = " { 1 ; 1 ; 1 } ";
        Assert.assertEquals(expected, realVectorFormat.parse(source2, pos2));
        Assert.assertEquals(source2.length() - 1, pos2.getIndex());
    }

    @Test
    public void testParseSimpleWithDecimals() {
        String source =
            "{1" + getDecimalCharacter() +
            "23; 1" + getDecimalCharacter() +
            "43; 1" + getDecimalCharacter() +
            "63}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.23, 1.43, 1.63});
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseSimpleWithDecimalsTrunc() {
        String source =
            "{1" + getDecimalCharacter() +
            "2323; 1" + getDecimalCharacter() +
            "4343; 1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.2323, 1.4343, 1.6333});
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeX() {
        String source =
            "{-1" + getDecimalCharacter() +
            "2323; 1" + getDecimalCharacter() +
            "4343; 1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {-1.2323, 1.4343, 1.6333});
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeY() {
        String source =
            "{1" + getDecimalCharacter() +
            "2323; -1" + getDecimalCharacter() +
            "4343; 1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.2323, -1.4343, 1.6333});
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeZ() {
        String source =
            "{1" + getDecimalCharacter() +
            "2323; 1" + getDecimalCharacter() +
            "4343; -1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.2323, 1.4343, -1.6333});
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNegativeAll() {
        String source =
            "{-1" + getDecimalCharacter() +
            "2323; -1" + getDecimalCharacter() +
            "4343; -1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {-1.2323, -1.4343, -1.6333});
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseZeroX() {
        String source =
            "{0" + getDecimalCharacter() +
            "0; -1" + getDecimalCharacter() +
            "4343; 1" + getDecimalCharacter() +
            "6333}";
        ArrayRealVector expected = new ArrayRealVector(new double[] {0.0, -1.4343, 1.6333});
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNonDefaultSetting() {
        String source =
            "[1" + getDecimalCharacter() +
            "2323 : 1" + getDecimalCharacter() +
            "4343 : 1" + getDecimalCharacter() +
            "6333]";
        ArrayRealVector expected = new ArrayRealVector(new double[] {1.2323, 1.4343, 1.6333});
        ArrayRealVector actual = realVectorFormatSquare.parse(source);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testParseNan() {
        String source = "{(NaN); (NaN); (NaN)}";
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(new ArrayRealVector(new double[] {Double.NaN, Double.NaN, Double.NaN}), actual);
    }

    @Test
    public void testParsePositiveInfinity() {
        String source = "{(Infinity); (Infinity); (Infinity)}";
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(new ArrayRealVector(new double[] {
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
        }), actual);
    }

    @Test
    public void testParseNegativeInfinity() {
        String source = "{(-Infinity); (-Infinity); (-Infinity)}";
        ArrayRealVector actual = realVectorFormat.parse(source);
        Assert.assertEquals(new ArrayRealVector(new double[] {
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY
        }), actual);
    }

    @Test
    public void testParseNoComponents() {
        try {
            realVectorFormat.parse("{ }");
            Assert.fail("Expecting MathParseException");
        } catch (MathParseException pe) {
            // expected behavior
        }
    }

    @Test
    public void testParseManyComponents() {
        ArrayRealVector parsed = realVectorFormat.parse("{0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0}");
        Assert.assertEquals(24, parsed.getDimension());
    }

    @Test
    public void testConstructorSingleFormat() {
        NumberFormat nf = NumberFormat.getInstance();
        RealVectorFormat cf = new RealVectorFormat(nf);
        Assert.assertNotNull(cf);
        Assert.assertEquals(nf, cf.getFormat());
    }

    @Test
    public void testForgottenPrefix() {
        ParsePosition pos = new ParsePosition(0);
        final String source = "1; 1; 1}";
        Assert.assertNull("Should not parse <"+source+">",new RealVectorFormat().parse(source, pos));
        Assert.assertEquals(0, pos.getErrorIndex());
    }

    @Test
    public void testForgottenSeparator() {
        ParsePosition pos = new ParsePosition(0);
        final String source = "{1; 1 1}";
        Assert.assertNull("Should not parse <"+source+">",new RealVectorFormat().parse(source, pos));
        Assert.assertEquals(6, pos.getErrorIndex());
    }

    @Test
    public void testForgottenSuffix() {
        ParsePosition pos = new ParsePosition(0);
        final String source = "{1; 1; 1 ";
        Assert.assertNull("Should not parse <"+source+">",new RealVectorFormat().parse(source, pos));
        Assert.assertEquals(8, pos.getErrorIndex());
    }
}
