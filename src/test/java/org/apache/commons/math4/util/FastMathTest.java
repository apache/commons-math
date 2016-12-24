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
package org.apache.commons.math4.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.dfp.Dfp;
import org.apache.commons.math4.dfp.DfpField;
import org.apache.commons.math4.dfp.DfpMath;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class FastMathTest {

    private static final double MAX_ERROR_ULP = 0.51;
    private static final int NUMBER_OF_TRIALS = 1000;

    private DfpField field;
    private UniformRandomProvider generator;

    @Before
    public void setUp() {
        field = new DfpField(40);
        generator = RandomSource.create(RandomSource.MT, 6176597458463500194l);
    }

    @Test
    public void testMinMaxDouble() {
        double[][] pairs = {
            { -50.0, 50.0 },
            {  Double.POSITIVE_INFINITY, 1.0 },
            {  Double.NEGATIVE_INFINITY, 1.0 },
            {  Double.NaN, 1.0 },
            {  Double.POSITIVE_INFINITY, 0.0 },
            {  Double.NEGATIVE_INFINITY, 0.0 },
            {  Double.NaN, 0.0 },
            {  Double.NaN, Double.NEGATIVE_INFINITY },
            {  Double.NaN, Double.POSITIVE_INFINITY },
            { Precision.SAFE_MIN, Precision.EPSILON }
        };
        for (double[] pair : pairs) {
            assertEquals("min(" + pair[0] + ", " + pair[1] + ")",
                         Math.min(pair[0], pair[1]),
                         FastMath.min(pair[0], pair[1]),
                         Precision.EPSILON);
            assertEquals("min(" + pair[1] + ", " + pair[0] + ")",
                         Math.min(pair[1], pair[0]),
                         FastMath.min(pair[1], pair[0]),
                         Precision.EPSILON);
            assertEquals("max(" + pair[0] + ", " + pair[1] + ")",
                         Math.max(pair[0], pair[1]),
                         FastMath.max(pair[0], pair[1]),
                         Precision.EPSILON);
            assertEquals("max(" + pair[1] + ", " + pair[0] + ")",
                         Math.max(pair[1], pair[0]),
                         FastMath.max(pair[1], pair[0]),
                         Precision.EPSILON);
        }
    }

    @Test
    public void testMinMaxFloat() {
        float[][] pairs = {
            { -50.0f, 50.0f },
            {  Float.POSITIVE_INFINITY, 1.0f },
            {  Float.NEGATIVE_INFINITY, 1.0f },
            {  Float.NaN, 1.0f },
            {  Float.POSITIVE_INFINITY, 0.0f },
            {  Float.NEGATIVE_INFINITY, 0.0f },
            {  Float.NaN, 0.0f },
            {  Float.NaN, Float.NEGATIVE_INFINITY },
            {  Float.NaN, Float.POSITIVE_INFINITY }
        };
        for (float[] pair : pairs) {
            assertEquals("min(" + pair[0] + ", " + pair[1] + ")",
                    Math.min(pair[0], pair[1]),
                    FastMath.min(pair[0], pair[1]),
                    Precision.EPSILON);
            assertEquals("min(" + pair[1] + ", " + pair[0] + ")",
                    Math.min(pair[1], pair[0]),
                    FastMath.min(pair[1], pair[0]),
                    Precision.EPSILON);
            assertEquals("max(" + pair[0] + ", " + pair[1] + ")",
                    Math.max(pair[0], pair[1]),
                    FastMath.max(pair[0], pair[1]),
                    Precision.EPSILON);
            assertEquals("max(" + pair[1] + ", " + pair[0] + ")",
                    Math.max(pair[1], pair[0]),
                    FastMath.max(pair[1], pair[0]),
                    Precision.EPSILON);
        }
    }

    @Test
    public void testConstants() {
        assertEquals(Math.PI, FastMath.PI, 1.0e-20);
        assertEquals(Math.E, FastMath.E, 1.0e-20);
    }

    @Test
    public void testAtan2() {
        double y1 = 1.2713504628280707e10;
        double x1 = -5.674940885228782e-10;
        assertEquals(Math.atan2(y1, x1), FastMath.atan2(y1, x1), 2 * Precision.EPSILON);
        double y2 = 0.0;
        double x2 = Double.POSITIVE_INFINITY;
        assertEquals(Math.atan2(y2, x2), FastMath.atan2(y2, x2), Precision.SAFE_MIN);
    }

    @Test
    public void testHyperbolic() {
        double maxErr = 0;
        for (double x = -30; x < 30; x += 0.001) {
            double tst = FastMath.sinh(x);
            double ref = Math.sinh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        assertEquals(0, maxErr, 2);

        maxErr = 0;
        for (double x = -30; x < 30; x += 0.001) {
            double tst = FastMath.cosh(x);
            double ref = Math.cosh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        assertEquals(0, maxErr, 2);

        maxErr = 0;
        for (double x = -0.5; x < 0.5; x += 0.001) {
            double tst = FastMath.tanh(x);
            double ref = Math.tanh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        assertEquals(0, maxErr, 4);

    }

    @Test
    public void testMath904() {
        final double x = -1;
        final double y = (5 + 1e-15) * 1e15;
        assertEquals(Math.pow(x, y),
                     FastMath.pow(x, y), 0);
        assertEquals(Math.pow(x, -y),
                FastMath.pow(x, -y), 0);
    }

    @Test
    public void testMath905LargePositive() {
        final double start = StrictMath.log(Double.MAX_VALUE);
        final double endT = StrictMath.sqrt(2) * StrictMath.sqrt(Double.MAX_VALUE);
        final double end = 2 * StrictMath.log(endT);

        double maxErr = 0;
        for (double x = start; x < end; x += 1e-3) {
            final double tst = FastMath.cosh(x);
            final double ref = Math.cosh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        assertEquals(0, maxErr, 3);

        for (double x = start; x < end; x += 1e-3) {
            final double tst = FastMath.sinh(x);
            final double ref = Math.sinh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        assertEquals(0, maxErr, 3);
    }

    @Test
    public void testMath905LargeNegative() {
        final double start = -StrictMath.log(Double.MAX_VALUE);
        final double endT = StrictMath.sqrt(2) * StrictMath.sqrt(Double.MAX_VALUE);
        final double end = -2 * StrictMath.log(endT);

        double maxErr = 0;
        for (double x = start; x > end; x -= 1e-3) {
            final double tst = FastMath.cosh(x);
            final double ref = Math.cosh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        assertEquals(0, maxErr, 3);

        for (double x = start; x > end; x -= 1e-3) {
            final double tst = FastMath.sinh(x);
            final double ref = Math.sinh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        assertEquals(0, maxErr, 3);
    }

    @Test
    public void testMath1269() {
        final double arg = 709.8125;
        final double vM = Math.exp(arg);
        final double vFM = FastMath.exp(arg);
        assertTrue("exp(" + arg + ") is " + vFM + " instead of " + vM,
                Precision.equalsIncludingNaN(vM, vFM));
    }

    @Test
    public void testHyperbolicInverses() {
        double maxErr = 0;
        for (double x = -30; x < 30; x += 0.01) {
            maxErr = FastMath.max(maxErr, FastMath.abs(x - FastMath.sinh(FastMath.asinh(x))) / (2 * FastMath.ulp(x)));
        }
        assertEquals(0, maxErr, 3);

        maxErr = 0;
        for (double x = 1; x < 30; x += 0.01) {
            maxErr = FastMath.max(maxErr, FastMath.abs(x - FastMath.cosh(FastMath.acosh(x))) / (2 * FastMath.ulp(x)));
        }
        assertEquals(0, maxErr, 2);

        maxErr = 0;
        for (double x = -1 + Precision.EPSILON; x < 1 - Precision.EPSILON; x += 0.0001) {
            maxErr = FastMath.max(maxErr, FastMath.abs(x - FastMath.tanh(FastMath.atanh(x))) / (2 * FastMath.ulp(x)));
        }
        assertEquals(0, maxErr, 2);
    }

    @Test
    public void testLogAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            double x = Math.exp(generator.nextDouble() * 1416.0 - 708.0) * generator.nextDouble();
            // double x = generator.nextDouble()*2.0;
            double tst = FastMath.log(x);
            double ref = DfpMath.log(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0.0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.log(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("log() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testLog10Accuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            double x = Math.exp(generator.nextDouble() * 1416.0 - 708.0) * generator.nextDouble();
            // double x = generator.nextDouble()*2.0;
            double tst = FastMath.log10(x);
            double ref = DfpMath.log(field.newDfp(x)).divide(DfpMath.log(field.newDfp("10"))).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0.0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.log(field.newDfp(x)).divide(DfpMath.log(field.newDfp("10")))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("log10() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testLog1pAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            double x = Math.exp(generator.nextDouble() * 10.0 - 5.0) * generator.nextDouble();
            // double x = generator.nextDouble()*2.0;
            double tst = FastMath.log1p(x);
            double ref = DfpMath.log(field.newDfp(x).add(field.getOne())).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0.0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.log(field.newDfp(x).add(field.getOne()))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("log1p() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testLog1pSpecialCases() {
        assertTrue("Logp of -1.0 should be -Inf", Double.isInfinite(FastMath.log1p(-1.0)));
    }

    @Test
    public void testLogSpecialCases() {
        assertEquals("Log of zero should be -Inf", Double.NEGATIVE_INFINITY, FastMath.log(0.0), 1.0);
        assertEquals("Log of -zero should be -Inf", Double.NEGATIVE_INFINITY, FastMath.log(-0.0), 1.0);
        assertTrue("Log of NaN should be NaN", Double.isNaN(FastMath.log(Double.NaN)));
        assertTrue("Log of negative number should be NaN", Double.isNaN(FastMath.log(-1.0)));
        assertEquals("Log of Double.MIN_VALUE should be -744.4400719213812", -744.4400719213812, FastMath.log(Double.MIN_VALUE), Precision.EPSILON);
        assertEquals("Log of infinity should be infinity", Double.POSITIVE_INFINITY, FastMath.log(Double.POSITIVE_INFINITY), 1.0);
    }

    @Test
    public void testExpSpecialCases() {
        // Smallest value that will round up to Double.MIN_VALUE
        assertEquals(Double.MIN_VALUE, FastMath.exp(-745.1332191019411), Precision.EPSILON);
        assertEquals("exp(-745.1332191019412) should be 0.0", 0.0, FastMath.exp(-745.1332191019412), Precision.EPSILON);
        assertTrue("exp of NaN should be NaN", Double.isNaN(FastMath.exp(Double.NaN)));
        assertEquals("exp of infinity should be infinity", Double.POSITIVE_INFINITY, FastMath.exp(Double.POSITIVE_INFINITY), 1.0);
        assertEquals("exp of -infinity should be 0.0", 0.0, FastMath.exp(Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("exp(1) should be Math.E", Math.E, FastMath.exp(1.0), Precision.EPSILON);
    }

    @Test
    public void testPowSpecialCases() {
        final double EXACT = -1.0;

        assertEquals("pow(-1, 0) should be 1.0", 1.0, FastMath.pow(-1.0, 0.0), Precision.EPSILON);
        assertEquals("pow(-1, -0) should be 1.0", 1.0, FastMath.pow(-1.0, -0.0), Precision.EPSILON);
        assertEquals("pow(PI, 1.0) should be PI", FastMath.PI, FastMath.pow(FastMath.PI, 1.0), Precision.EPSILON);
        assertEquals("pow(-PI, 1.0) should be -PI", -FastMath.PI, FastMath.pow(-FastMath.PI, 1.0), Precision.EPSILON);
        assertTrue("pow(PI, NaN) should be NaN", Double.isNaN(FastMath.pow(Math.PI, Double.NaN)));
        assertTrue("pow(NaN, PI) should be NaN", Double.isNaN(FastMath.pow(Double.NaN, Math.PI)));
        assertEquals("pow(2.0, Infinity) should be Infinity", Double.POSITIVE_INFINITY, FastMath.pow(2.0, Double.POSITIVE_INFINITY), 1.0);
        assertEquals("pow(0.5, -Infinity) should be Infinity", Double.POSITIVE_INFINITY, FastMath.pow(0.5, Double.NEGATIVE_INFINITY), 1.0);
        assertEquals("pow(0.5, Infinity) should be 0.0", 0.0, FastMath.pow(0.5, Double.POSITIVE_INFINITY), Precision.EPSILON);
        assertEquals("pow(2.0, -Infinity) should be 0.0", 0.0, FastMath.pow(2.0, Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("pow(0.0, 0.5) should be 0.0", 0.0, FastMath.pow(0.0, 0.5), Precision.EPSILON);
        assertEquals("pow(Infinity, -0.5) should be 0.0", 0.0, FastMath.pow(Double.POSITIVE_INFINITY, -0.5), Precision.EPSILON);
        assertEquals("pow(0.0, -0.5) should be Inf", Double.POSITIVE_INFINITY, FastMath.pow(0.0, -0.5), 1.0);
        assertEquals("pow(Inf, 0.5) should be Inf", Double.POSITIVE_INFINITY, FastMath.pow(Double.POSITIVE_INFINITY, 0.5), 1.0);
        assertEquals("pow(-0.0, -3.0) should be -Inf", Double.NEGATIVE_INFINITY, FastMath.pow(-0.0, -3.0), 1.0);
        assertEquals("pow(-0.0, Infinity) should be 0.0", 0.0, FastMath.pow(-0.0, Double.POSITIVE_INFINITY), Precision.EPSILON);
        assertTrue("pow(-0.0, NaN) should be NaN", Double.isNaN(FastMath.pow(-0.0, Double.NaN)));
        assertEquals("pow(-0.0, -tiny) should be Infinity", Double.POSITIVE_INFINITY, FastMath.pow(-0.0, -Double.MIN_VALUE), 1.0);
        assertEquals("pow(-0.0, -huge) should be Infinity", Double.POSITIVE_INFINITY, FastMath.pow(-0.0, -Double.MAX_VALUE), 1.0);
        assertEquals("pow(-Inf, 3.0) should be -Inf", Double.NEGATIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, 3.0), 1.0);
        assertEquals("pow(-Inf, -3.0) should be -0.0", -0.0, FastMath.pow(Double.NEGATIVE_INFINITY, -3.0), EXACT);
        assertEquals("pow(-0.0, -3.5) should be Inf", Double.POSITIVE_INFINITY, FastMath.pow(-0.0, -3.5), 1.0);
        assertEquals("pow(Inf, 3.5) should be Inf", Double.POSITIVE_INFINITY, FastMath.pow(Double.POSITIVE_INFINITY, 3.5), 1.0);
        assertEquals("pow(-2.0, 3.0) should be -8.0", -8.0, FastMath.pow(-2.0, 3.0), Precision.EPSILON);
        assertTrue("pow(-2.0, 3.5) should be NaN", Double.isNaN(FastMath.pow(-2.0, 3.5)));
        assertTrue("pow(NaN, -Infinity) should be NaN", Double.isNaN(FastMath.pow(Double.NaN, Double.NEGATIVE_INFINITY)));
        assertEquals("pow(NaN, 0.0) should be 1.0", 1.0, FastMath.pow(Double.NaN, 0.0), Precision.EPSILON);
        assertEquals("pow(-Infinity, -Infinity) should be 0.0", 0.0, FastMath.pow(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("pow(-huge, -huge) should be 0.0", 0.0, FastMath.pow(-Double.MAX_VALUE, -Double.MAX_VALUE), Precision.EPSILON);
        assertTrue("pow(-huge,  huge) should be +Inf", Double.isInfinite(FastMath.pow(-Double.MAX_VALUE, Double.MAX_VALUE)));
        assertTrue("pow(NaN, -Infinity) should be NaN", Double.isNaN(FastMath.pow(Double.NaN, Double.NEGATIVE_INFINITY)));
        assertEquals("pow(NaN, -0.0) should be 1.0", 1.0, FastMath.pow(Double.NaN, -0.0), Precision.EPSILON);
        assertEquals("pow(-Infinity, -Infinity) should be 0.0", 0.0, FastMath.pow(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("pow(-huge, -huge) should be 0.0", 0.0, FastMath.pow(-Double.MAX_VALUE, -Double.MAX_VALUE), Precision.EPSILON);
        assertEquals("pow(-huge,  huge) should be +Inf", Double.POSITIVE_INFINITY, FastMath.pow(-Double.MAX_VALUE, Double.MAX_VALUE), 1.0);

        // Added tests for a 100% coverage

        assertTrue("pow(+Inf, NaN) should be NaN", Double.isNaN(FastMath.pow(Double.POSITIVE_INFINITY, Double.NaN)));
        assertTrue("pow(1.0, +Inf) should be NaN", Double.isNaN(FastMath.pow(1.0, Double.POSITIVE_INFINITY)));
        assertTrue("pow(-Inf, NaN) should be NaN", Double.isNaN(FastMath.pow(Double.NEGATIVE_INFINITY, Double.NaN)));
        assertEquals("pow(-Inf, -1.0) should be -0.0", -0.0, FastMath.pow(Double.NEGATIVE_INFINITY, -1.0), EXACT);
        assertEquals("pow(-Inf, -2.0) should be 0.0", 0.0, FastMath.pow(Double.NEGATIVE_INFINITY, -2.0), EXACT);
        assertEquals("pow(-Inf, 1.0) should be -Inf", Double.NEGATIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, 1.0), 1.0);
        assertEquals("pow(-Inf, 2.0) should be +Inf", Double.POSITIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, 2.0), 1.0);
        assertTrue("pow(1.0, -Inf) should be NaN", Double.isNaN(FastMath.pow(1.0, Double.NEGATIVE_INFINITY)));
        assertEquals("pow(-0.0, 1.0) should be -0.0", -0.0, FastMath.pow(-0.0, 1.0), EXACT);
        assertEquals("pow(0.0, 1.0) should be 0.0", 0.0, FastMath.pow(0.0, 1.0), EXACT);
        assertEquals("pow(0.0, +Inf) should be 0.0", 0.0, FastMath.pow(0.0, Double.POSITIVE_INFINITY), EXACT);
        assertEquals("pow(-0.0, even) should be 0.0", 0.0, FastMath.pow(-0.0, 6.0), EXACT);
        assertEquals("pow(-0.0, odd) should be -0.0", -0.0, FastMath.pow(-0.0, 13.0), EXACT);
        assertEquals("pow(-0.0, -even) should be +Inf", Double.POSITIVE_INFINITY, FastMath.pow(-0.0, -6.0), EXACT);
        assertEquals("pow(-0.0, -odd) should be -Inf", Double.NEGATIVE_INFINITY, FastMath.pow(-0.0, -13.0), EXACT);
        assertEquals("pow(-2.0, 4.0) should be 16.0", 16.0, FastMath.pow(-2.0, 4.0), EXACT);
        assertEquals("pow(-2.0, 4.5) should be NaN", Double.NaN, FastMath.pow(-2.0, 4.5), EXACT);
        assertEquals("pow(-0.0, -0.0) should be 1.0", 1.0, FastMath.pow(-0.0, -0.0), EXACT);
        assertEquals("pow(-0.0, 0.0) should be 1.0", 1.0, FastMath.pow(-0.0, 0.0), EXACT);
        assertEquals("pow(0.0, -0.0) should be 1.0", 1.0, FastMath.pow(0.0, -0.0), EXACT);
        assertEquals("pow(0.0, 0.0) should be 1.0", 1.0, FastMath.pow(0.0, 0.0), EXACT);
    }

    @Test(timeout=20000L)
    public void testPowAllSpecialCases() {
        final double EXACT = -1.0;
        final double DOUBLES[] = new double[]
            {
                Double.NEGATIVE_INFINITY, -0.0, Double.NaN, 0.0, Double.POSITIVE_INFINITY,
                Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE, Byte.MIN_VALUE,
                -(double)Long.MIN_VALUE, -(double)Integer.MIN_VALUE, -(double)Short.MIN_VALUE, -(double)Byte.MIN_VALUE,
                Byte.MAX_VALUE, Short.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE,
                -Byte.MAX_VALUE, -Short.MAX_VALUE, -Integer.MAX_VALUE, -Long.MAX_VALUE,
                Float.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Float.MIN_VALUE,
                -Float.MAX_VALUE, -Double.MAX_VALUE, -Double.MIN_VALUE, -Float.MIN_VALUE,
                0.5, 0.1, 0.2, 0.8, 1.1, 1.2, 1.5, 1.8, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 1.3, 2.2, 2.5, 2.8, 33.0, 33.1, 33.5, 33.8, 10.0, 300.0, 400.0, 500.0,
                -0.5, -0.1, -0.2, -0.8, -1.1, -1.2, -1.5, -1.8, -1.0, -2.0, -3.0, -4.0, -5.0, -6.0, -7.0, -8.0, -9.0, -1.3, -2.2, -2.5, -2.8, -33.0, -33.1, -33.5, -33.8, -10.0, -300.0, -400.0, -500.0
            };

        // Special cases from Math.pow javadoc:
        // If the second argument is positive or negative zero, then the result is 1.0.
        for (double d : DOUBLES) {
            assertEquals(1.0, FastMath.pow(d, 0.0), EXACT);
        }
        for (double d : DOUBLES) {
            assertEquals(1.0, FastMath.pow(d, -0.0), EXACT);
        }
        // If the second argument is 1.0, then the result is the same as the first argument.
        for (double d : DOUBLES) {
            assertEquals(d, FastMath.pow(d, 1.0), EXACT);
        }
        // If the second argument is NaN, then the result is NaN.
        for (double d : DOUBLES) {
            assertEquals(Double.NaN, FastMath.pow(d, Double.NaN), EXACT);
        }
        // If the first argument is NaN and the second argument is nonzero, then the result is NaN.
        for (double i : DOUBLES) {
            if (i != 0.0) {
                assertEquals(Double.NaN, FastMath.pow(Double.NaN, i), EXACT);
            }
        }
        // If the absolute value of the first argument is greater than 1 and the second argument is positive infinity, or
        // the absolute value of the first argument is less than 1 and the second argument is negative infinity, then the result is positive infinity.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(d, Double.POSITIVE_INFINITY), EXACT);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(d, Double.NEGATIVE_INFINITY), EXACT);
            }
        }
        // If the absolute value of the first argument is greater than 1 and the second argument is negative infinity, or
        // the absolute value of the first argument is less than 1 and the second argument is positive infinity, then the result is positive zero.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertEquals(0.0, FastMath.pow(d, Double.NEGATIVE_INFINITY), EXACT);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertEquals(0.0, FastMath.pow(d, Double.POSITIVE_INFINITY), EXACT);
            }
        }
        // If the absolute value of the first argument equals 1 and the second argument is infinite, then the result is NaN.
        assertEquals(Double.NaN, FastMath.pow(1.0, Double.POSITIVE_INFINITY), EXACT);
        assertEquals(Double.NaN, FastMath.pow(1.0, Double.NEGATIVE_INFINITY), EXACT);
        assertEquals(Double.NaN, FastMath.pow(-1.0, Double.POSITIVE_INFINITY), EXACT);
        assertEquals(Double.NaN, FastMath.pow(-1.0, Double.NEGATIVE_INFINITY), EXACT);
        // If the first argument is positive zero and the second argument is greater than zero, or
        // the first argument is positive infinity and the second argument is less than zero, then the result is positive zero.
        for (double i : DOUBLES) {
            if (i > 0.0) {
                assertEquals(0.0, FastMath.pow(0.0, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0) {
                assertEquals(0.0, FastMath.pow(Double.POSITIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is positive zero and the second argument is less than zero, or
        // the first argument is positive infinity and the second argument is greater than zero, then the result is positive infinity.
        for (double i : DOUBLES) {
            if (i < 0.0) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(0.0, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i > 0.0) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(Double.POSITIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is greater than zero but not a finite odd integer, or
        // the first argument is negative infinity and the second argument is less than zero but not a finite odd integer, then the result is positive zero.
        for (double i : DOUBLES) {
            if (i > 0.0 && (Double.isInfinite(i) || i % 2.0 == 0.0)) {
                assertEquals(0.0, FastMath.pow(-0.0, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0 && (Double.isInfinite(i) || i % 2.0 == 0.0)) {
                assertEquals(0.0, FastMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is a positive finite odd integer, or
        // the first argument is negative infinity and the second argument is a negative finite odd integer, then the result is negative zero.
        for (double i : DOUBLES) {
            if (i > 0.0 && i % 2.0 == 1.0) {
                assertEquals(-0.0, FastMath.pow(-0.0, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0 && i % 2.0 == -1.0) {
                assertEquals(-0.0, FastMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is less than zero but not a finite odd integer, or
        // the first argument is negative infinity and the second argument is greater than zero but not a finite odd integer, then the result is positive infinity.
        for (double i : DOUBLES) {
            if (i > 0.0 && (Double.isInfinite(i) || i % 2.0 == 0.0)) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0 && (Double.isInfinite(i) || i % 2.0 == 0.0)) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(-0.0, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is a negative finite odd integer, or
        // the first argument is negative infinity and the second argument is a positive finite odd integer, then the result is negative infinity.
        for (double i : DOUBLES) {
            if (i > 0.0 && i % 2.0 == 1.0) {
                assertEquals(Double.NEGATIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0 && i % 2.0 == -1.0) {
                assertEquals(Double.NEGATIVE_INFINITY, FastMath.pow(-0.0, i), EXACT);
            }
        }
        for (double d : DOUBLES) {
            // If the first argument is finite and less than zero
            if (d < 0.0 && Math.abs(d) <= Double.MAX_VALUE) {
                for (double i : DOUBLES) {
                    if (Math.abs(i) <= Double.MAX_VALUE) {
                        // if the second argument is a finite even integer, the result is equal to the result of raising the absolute value of the first argument to the power of the second argument
                        if (i % 2.0 == 0.0) assertEquals(FastMath.pow(-d, i), FastMath.pow(d, i), EXACT);
                        // if the second argument is a finite odd integer, the result is equal to the negative of the result of raising the absolute value of the first argument to the power of the second argument
                        else if (Math.abs(i) % 2.0 == 1.0) assertEquals(-FastMath.pow(-d, i), FastMath.pow(d, i), EXACT);
                        // if the second argument is finite and not an integer, then the result is NaN.
                        else assertEquals(Double.NaN, FastMath.pow(d, i), EXACT);
                    }
                }
            }
        }
        // If both arguments are integers, then the result is exactly equal to the mathematical result of raising the first argument to the power
        // of the second argument if that result can in fact be represented exactly as a double value.
        final int TOO_BIG_TO_CALCULATE = 18; // This value is empirical: 2^18 > 200.000 resulting bits after raising d to power i.
        for (double d : DOUBLES) {
            if (d % 1.0 == 0.0) {
                boolean dNegative = Double.doubleToRawLongBits( d ) < 0L;
                for (double i : DOUBLES) {
                    if (i % 1.0 == 0.0) {
                        BigInteger bd = BigDecimal.valueOf(d).toBigInteger().abs();
                        BigInteger bi = BigDecimal.valueOf(i).toBigInteger().abs();
                        double expected;
                        if (bd.bitLength() > 1 && bi.bitLength() > 1 && 32 - Integer.numberOfLeadingZeros(bd.bitLength()) + bi.bitLength() > TOO_BIG_TO_CALCULATE) {
                            // Result would be too big.
                            expected = i < 0.0 ? 0.0 : Double.POSITIVE_INFINITY;
                        } else {
                            BigInteger res = ArithmeticUtils.pow(bd, bi);
                            if (i >= 0.0) {
                                expected = res.doubleValue();
                            } else if (res.signum() == 0) {
                                expected = Double.POSITIVE_INFINITY;
                            } else {
                                expected = BigDecimal.ONE.divide( new BigDecimal( res ), 1024, RoundingMode.HALF_UP ).doubleValue();
                            }
                        }
                        if (dNegative && bi.testBit( 0 )) {
                            expected = -expected;
                        }
                        assertEquals(d + "^" + i + "=" + expected + ", Math.pow=" + Math.pow(d, i), expected, FastMath.pow(d, i), expected == 0.0 || Double.isInfinite(expected) || Double.isNaN(expected) ? EXACT : 2.0 * Math.ulp(expected));
                    }
                }
            }
        }
    }

    @Test
    public void testPowLargeIntegralDouble() {
        double y = FastMath.scalb(1.0, 65);
        assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(FastMath.nextUp(1.0), y),    1.0);
        assertEquals(1.0,                      FastMath.pow(1.0, y),                     1.0);
        assertEquals(0.0,                      FastMath.pow(FastMath.nextDown(1.0), y),  1.0);
        assertEquals(0.0,                      FastMath.pow(FastMath.nextUp(-1.0), y),   1.0);
        assertEquals(1.0,                      FastMath.pow(-1.0, y),                    1.0);
        assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(FastMath.nextDown(-1.0), y), 1.0);
    }

    @Test
    public void testAtan2SpecialCases() {

        assertTrue("atan2(NaN, 0.0) should be NaN", Double.isNaN(FastMath.atan2(Double.NaN, 0.0)));
        assertTrue("atan2(0.0, NaN) should be NaN", Double.isNaN(FastMath.atan2(0.0, Double.NaN)));
        assertEquals("atan2(0.0, 0.0) should be 0.0", 0.0, FastMath.atan2(0.0, 0.0), Precision.EPSILON);
        assertEquals("atan2(0.0, 0.001) should be 0.0", 0.0, FastMath.atan2(0.0, 0.001), Precision.EPSILON);
        assertEquals("atan2(0.1, +Inf) should be 0.0", 0.0, FastMath.atan2(0.1, Double.POSITIVE_INFINITY), Precision.EPSILON);
        assertEquals("atan2(-0.0, 0.0) should be -0.0", -0.0, FastMath.atan2(-0.0, 0.0), Precision.EPSILON);
        assertEquals("atan2(-0.0, 0.001) should be -0.0", -0.0, FastMath.atan2(-0.0, 0.001), Precision.EPSILON);
        assertEquals("atan2(-0.0, +Inf) should be -0.0", -0.0, FastMath.atan2(-0.1, Double.POSITIVE_INFINITY), Precision.EPSILON);
        assertEquals("atan2(0.0, -0.0) should be PI", FastMath.PI, FastMath.atan2(0.0, -0.0), Precision.EPSILON);
        assertEquals("atan2(0.1, -Inf) should be PI", FastMath.PI, FastMath.atan2(0.1, Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("atan2(-0.0, -0.0) should be -PI", -FastMath.PI, FastMath.atan2(-0.0, -0.0), Precision.EPSILON);
        assertEquals("atan2(0.1, -Inf) should be -PI", -FastMath.PI, FastMath.atan2(-0.1, Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("atan2(0.1, 0.0) should be PI/2", FastMath.PI / 2.0, FastMath.atan2(0.1, 0.0), Precision.EPSILON);
        assertEquals("atan2(0.1, -0.0) should be PI/2", FastMath.PI / 2.0, FastMath.atan2(0.1, -0.0), Precision.EPSILON);
        assertEquals("atan2(Inf, 0.1) should be PI/2", FastMath.PI / 2.0, FastMath.atan2(Double.POSITIVE_INFINITY, 0.1), Precision.EPSILON);
        assertEquals("atan2(Inf, -0.1) should be PI/2", FastMath.PI / 2.0, FastMath.atan2(Double.POSITIVE_INFINITY, -0.1), Precision.EPSILON);
        assertEquals("atan2(-0.1, 0.0) should be -PI/2", -FastMath.PI / 2.0, FastMath.atan2(-0.1, 0.0), Precision.EPSILON);
        assertEquals("atan2(-0.1, -0.0) should be -PI/2", -FastMath.PI / 2.0, FastMath.atan2(-0.1, -0.0), Precision.EPSILON);
        assertEquals("atan2(-Inf, 0.1) should be -PI/2", -FastMath.PI / 2.0, FastMath.atan2(Double.NEGATIVE_INFINITY, 0.1), Precision.EPSILON);
        assertEquals("atan2(-Inf, -0.1) should be -PI/2", -FastMath.PI / 2.0, FastMath.atan2(Double.NEGATIVE_INFINITY, -0.1), Precision.EPSILON);
        assertEquals("atan2(Inf, Inf) should be PI/4", FastMath.PI / 4.0, FastMath.atan2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
                     Precision.EPSILON);
        assertEquals("atan2(Inf, -Inf) should be PI * 3/4", FastMath.PI * 3.0 / 4.0,
                     FastMath.atan2(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("atan2(-Inf, Inf) should be -PI/4", -FastMath.PI / 4.0, FastMath.atan2(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
                     Precision.EPSILON);
        assertEquals("atan2(-Inf, -Inf) should be -PI * 3/4", - FastMath.PI * 3.0 / 4.0,
                     FastMath.atan2(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Precision.EPSILON);
    }

    @Test
    public void testPowAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            double x = (generator.nextDouble() * 2.0 + 0.25);
            double y = (generator.nextDouble() * 1200.0 - 600.0) * generator.nextDouble();
            /*
             * double x = FastMath.floor(generator.nextDouble()*1024.0 - 512.0); double
             * y; if (x != 0) y = FastMath.floor(512.0 / FastMath.abs(x)); else
             * y = generator.nextDouble()*1200.0; y = y - y/2; x = FastMath.pow(2.0, x) *
             * generator.nextDouble(); y = y * generator.nextDouble();
             */

            // double x = generator.nextDouble()*2.0;
            double tst = FastMath.pow(x, y);
            double ref = DfpMath.pow(field.newDfp(x), field.newDfp(y)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.pow(field.newDfp(x), field.newDfp(y))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + y + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("pow() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testExpAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            /* double x = 1.0 + i/1024.0/2.0; */
            double x = ((generator.nextDouble() * 1416.0) - 708.0) * generator.nextDouble();
            // double x = (generator.nextDouble() * 20.0) - 10.0;
            // double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();
            /* double x = 3.0 / 512.0 * i - 3.0; */
            double tst = FastMath.exp(x);
            double ref = DfpMath.exp(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.exp(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("exp() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testSinAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            /* double x = 1.0 + i/1024.0/2.0; */
            // double x = ((generator.nextDouble() * 1416.0) - 708.0) * generator.nextDouble();
            double x = ((generator.nextDouble() * Math.PI) - Math.PI / 2.0) *
                       Math.pow(2, 21) * generator.nextDouble();
            // double x = (generator.nextDouble() * 20.0) - 10.0;
            // double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();
            /* double x = 3.0 / 512.0 * i - 3.0; */
            double tst = FastMath.sin(x);
            double ref = DfpMath.sin(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.sin(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("sin() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testCosAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            /* double x = 1.0 + i/1024.0/2.0; */
            // double x = ((generator.nextDouble() * 1416.0) - 708.0) * generator.nextDouble();
            double x = ((generator.nextDouble() * Math.PI) - Math.PI / 2.0) *
                       Math.pow(2, 21) * generator.nextDouble();
            // double x = (generator.nextDouble() * 20.0) - 10.0;
            // double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();
            /* double x = 3.0 / 512.0 * i - 3.0; */
            double tst = FastMath.cos(x);
            double ref = DfpMath.cos(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.cos(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("cos() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testTanAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            /* double x = 1.0 + i/1024.0/2.0; */
            // double x = ((generator.nextDouble() * 1416.0) - 708.0) * generator.nextDouble();
            double x = ((generator.nextDouble() * Math.PI) - Math.PI / 2.0) *
                       Math.pow(2, 12) * generator.nextDouble();
            // double x = (generator.nextDouble() * 20.0) - 10.0;
            // double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();
            /* double x = 3.0 / 512.0 * i - 3.0; */
            double tst = FastMath.tan(x);
            double ref = DfpMath.tan(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.tan(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("tan() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testAtanAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            /* double x = 1.0 + i/1024.0/2.0; */
            // double x = ((generator.nextDouble() * 1416.0) - 708.0) * generator.nextDouble();
            // double x = ((generator.nextDouble() * Math.PI) - Math.PI/2.0) *
            // generator.nextDouble();
            double x = ((generator.nextDouble() * 16.0) - 8.0) * generator.nextDouble();

            // double x = (generator.nextDouble() * 20.0) - 10.0;
            // double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();
            /* double x = 3.0 / 512.0 * i - 3.0; */
            double tst = FastMath.atan(x);
            double ref = DfpMath.atan(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.atan(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("atan() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testAtan2Accuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            /* double x = 1.0 + i/1024.0/2.0; */
            // double x = ((generator.nextDouble() * 1416.0) - 708.0) * generator.nextDouble();
            double x = generator.nextDouble() - 0.5;
            double y = generator.nextDouble() - 0.5;
            // double x = (generator.nextDouble() * 20.0) - 10.0;
            // double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();
            /* double x = 3.0 / 512.0 * i - 3.0; */
            double tst = FastMath.atan2(y, x);
            Dfp refdfp = DfpMath.atan(field.newDfp(y).divide(field.newDfp(x)));
            /* Make adjustments for sign */
            if (x < 0.0) {
                if (y > 0.0) {
                    refdfp = field.getPi().add(refdfp);
                } else {
                    refdfp = refdfp.subtract(field.getPi());
                }
            }

            double ref = refdfp.toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(refdfp).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + y + "\t" + tst + "\t" + ref + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("atan2() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testExpm1Accuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            /* double x = 1.0 + i/1024.0/2.0; */
            // double x = (generator.nextDouble() * 20.0) - 10.0;
            double x = ((generator.nextDouble() * 16.0) - 8.0) * generator.nextDouble();
            /* double x = 3.0 / 512.0 * i - 3.0; */
            double tst = FastMath.expm1(x);
            double ref = DfpMath.exp(field.newDfp(x)).subtract(field.getOne()).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.exp(field.newDfp(x)).subtract(field.getOne())).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("expm1() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testAsinAccuracy() {
        double maxerrulp = 0.0;

        for (int i=0; i<10000; i++) {
            double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();

            double tst = FastMath.asin(x);
            double ref = DfpMath.asin(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref - Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.asin(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
                //System.out.println(x+"\t"+tst+"\t"+ref+"\t"+err+"\t"+errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("asin() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testAcosAccuracy() {
        double maxerrulp = 0.0;

        for (int i=0; i<10000; i++) {
            double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();

            double tst = FastMath.acos(x);
            double ref = DfpMath.acos(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref - Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.acos(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
                //System.out.println(x+"\t"+tst+"\t"+ref+"\t"+err+"\t"+errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("acos() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    /**
     * Added tests for a 100% coverage of acos().
     */
    @Test
    public void testAcosSpecialCases() {

        assertTrue("acos(NaN) should be NaN", Double.isNaN(FastMath.acos(Double.NaN)));
        assertTrue("acos(-1.1) should be NaN", Double.isNaN(FastMath.acos(-1.1)));
        assertTrue("acos(-1.1) should be NaN", Double.isNaN(FastMath.acos(1.1)));
        assertEquals("acos(-1.0) should be PI", FastMath.acos(-1.0), FastMath.PI, Precision.EPSILON);
        assertEquals("acos(1.0) should be 0.0", FastMath.acos(1.0), 0.0, Precision.EPSILON);
        assertEquals("acos(0.0) should be PI/2", FastMath.acos(0.0), FastMath.PI / 2.0, Precision.EPSILON);
    }

    /**
     * Added tests for a 100% coverage of asin().
     */
    @Test
    public void testAsinSpecialCases() {

        assertTrue("asin(NaN) should be NaN", Double.isNaN(FastMath.asin(Double.NaN)));
        assertTrue("asin(1.1) should be NaN", Double.isNaN(FastMath.asin(1.1)));
        assertTrue("asin(-1.1) should be NaN", Double.isNaN(FastMath.asin(-1.1)));
        assertEquals("asin(1.0) should be PI/2", FastMath.asin(1.0), FastMath.PI / 2.0, Precision.EPSILON);
        assertEquals("asin(-1.0) should be -PI/2", FastMath.asin(-1.0), -FastMath.PI / 2.0, Precision.EPSILON);
        assertEquals("asin(0.0) should be 0.0", FastMath.asin(0.0), 0.0, Precision.EPSILON);
    }

    private Dfp cosh(Dfp x) {
      return DfpMath.exp(x).add(DfpMath.exp(x.negate())).divide(2);
    }

    private Dfp sinh(Dfp x) {
      return DfpMath.exp(x).subtract(DfpMath.exp(x.negate())).divide(2);
    }

    private Dfp tanh(Dfp x) {
      return sinh(x).divide(cosh(x));
    }

    @Test
    public void testSinhAccuracy() {
        double maxerrulp = 0.0;

        for (int i=0; i<10000; i++) {
            double x = ((generator.nextDouble() * 16.0) - 8.0) * generator.nextDouble();

            double tst = FastMath.sinh(x);
            double ref = sinh(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref - Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(sinh(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
                //System.out.println(x+"\t"+tst+"\t"+ref+"\t"+err+"\t"+errulp);
                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("sinh() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testCoshAccuracy() {
        double maxerrulp = 0.0;

        for (int i=0; i<10000; i++) {
            double x = ((generator.nextDouble() * 16.0) - 8.0) * generator.nextDouble();

            double tst = FastMath.cosh(x);
            double ref = cosh(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref - Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(cosh(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
                //System.out.println(x+"\t"+tst+"\t"+ref+"\t"+err+"\t"+errulp);
                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("cosh() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testTanhAccuracy() {
        double maxerrulp = 0.0;

        for (int i=0; i<10000; i++) {
            double x = ((generator.nextDouble() * 16.0) - 8.0) * generator.nextDouble();

            double tst = FastMath.tanh(x);
            double ref = tanh(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref - Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(tanh(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
                //System.out.println(x+"\t"+tst+"\t"+ref+"\t"+err+"\t"+errulp);
                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("tanh() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testCbrtAccuracy() {
        double maxerrulp = 0.0;

        for (int i=0; i<10000; i++) {
            double x = ((generator.nextDouble() * 200.0) - 100.0) * generator.nextDouble();

            double tst = FastMath.cbrt(x);
            double ref = cbrt(field.newDfp(x)).toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref - Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(cbrt(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
                //System.out.println(x+"\t"+tst+"\t"+ref+"\t"+err+"\t"+errulp);
                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("cbrt() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    private Dfp cbrt(Dfp x) {
        boolean negative=false;

        if (x.lessThan(field.getZero())) {
            negative = true;
            x = x.negate();
        }

        Dfp y = DfpMath.pow(x, field.getOne().divide(3));

        if (negative) {
            y = y.negate();
        }

        return y;
    }

    @Test
    public void testToDegrees() {
        double maxerrulp = 0.0;
        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            double x = generator.nextDouble();
            double tst = field.newDfp(x).multiply(180).divide(field.getPi()).toDouble();
            double ref = FastMath.toDegrees(x);
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.exp(field.newDfp(x)).subtract(field.getOne())).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }
        assertTrue("toDegrees() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testToRadians() {
        double maxerrulp = 0.0;
        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            double x = generator.nextDouble();
            double tst = field.newDfp(x).multiply(field.getPi()).divide(180).toDouble();
            double ref = FastMath.toRadians(x);
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double.doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.exp(field.newDfp(x)).subtract(field.getOne())).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        assertTrue("toRadians() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testNextAfter() {
        // 0x402fffffffffffff 0x404123456789abcd -> 4030000000000000
        assertEquals(16.0, FastMath.nextUp(15.999999999999998), 0.0);

        // 0xc02fffffffffffff 0x404123456789abcd -> c02ffffffffffffe
        assertEquals(-15.999999999999996, FastMath.nextAfter(-15.999999999999998, 34.27555555555555), 0.0);

        // 0x402fffffffffffff 0x400123456789abcd -> 402ffffffffffffe
        assertEquals(15.999999999999996, FastMath.nextDown(15.999999999999998), 0.0);

        // 0xc02fffffffffffff 0x400123456789abcd -> c02ffffffffffffe
        assertEquals(-15.999999999999996, FastMath.nextAfter(-15.999999999999998, 2.142222222222222), 0.0);

        // 0x4020000000000000 0x404123456789abcd -> 4020000000000001
        assertEquals(8.000000000000002, FastMath.nextAfter(8.0, 34.27555555555555), 0.0);

        // 0xc020000000000000 0x404123456789abcd -> c01fffffffffffff
        assertEquals(-7.999999999999999, FastMath.nextAfter(-8.0, 34.27555555555555), 0.0);

        // 0x4020000000000000 0x400123456789abcd -> 401fffffffffffff
        assertEquals(7.999999999999999, FastMath.nextAfter(8.0, 2.142222222222222), 0.0);

        // 0xc020000000000000 0x400123456789abcd -> c01fffffffffffff
        assertEquals(-7.999999999999999, FastMath.nextAfter(-8.0, 2.142222222222222), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a224 -> 3f2e43753d36a224
        assertEquals(2.308922399667661E-4, FastMath.nextAfter(2.3089223996676606E-4, 2.308922399667661E-4), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a223 -> 3f2e43753d36a223
        assertEquals(2.3089223996676606E-4, FastMath.nextAfter(2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a222 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, FastMath.nextAfter(2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a224 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, FastMath.nextAfter(2.3089223996676606E-4, -2.308922399667661E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a223 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, FastMath.nextAfter(2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a222 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, FastMath.nextAfter(2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a224 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, FastMath.nextAfter(-2.3089223996676606E-4, 2.308922399667661E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a223 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, FastMath.nextAfter(-2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a222 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, FastMath.nextAfter(-2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a224 -> bf2e43753d36a224
        assertEquals(-2.308922399667661E-4, FastMath.nextAfter(-2.3089223996676606E-4, -2.308922399667661E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a223 -> bf2e43753d36a223
        assertEquals(-2.3089223996676606E-4, FastMath.nextAfter(-2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a222 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, FastMath.nextAfter(-2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);

    }

    @Test
    public void testDoubleNextAfterSpecialCases() {
        assertEquals(-Double.MAX_VALUE,FastMath.nextAfter(Double.NEGATIVE_INFINITY, 0D), 0D);
        assertEquals(Double.MAX_VALUE,FastMath.nextAfter(Double.POSITIVE_INFINITY, 0D), 0D);
        assertEquals(Double.NaN,FastMath.nextAfter(Double.NaN, 0D), 0D);
        assertEquals(Double.POSITIVE_INFINITY,FastMath.nextAfter(Double.MAX_VALUE, Double.POSITIVE_INFINITY), 0D);
        assertEquals(Double.NEGATIVE_INFINITY,FastMath.nextAfter(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY), 0D);
        assertEquals(Double.MIN_VALUE, FastMath.nextAfter(0D, 1D), 0D);
        assertEquals(-Double.MIN_VALUE, FastMath.nextAfter(0D, -1D), 0D);
        assertEquals(0D, FastMath.nextAfter(Double.MIN_VALUE, -1), 0D);
        assertEquals(0D, FastMath.nextAfter(-Double.MIN_VALUE, 1), 0D);
    }

    @Test
    public void testFloatNextAfterSpecialCases() {
        assertEquals(-Float.MAX_VALUE, FastMath.nextAfter(Float.NEGATIVE_INFINITY, 0F), 0F);
        assertEquals(Float.MAX_VALUE, FastMath.nextAfter(Float.POSITIVE_INFINITY, 0F), 0F);
        assertEquals(Float.NaN, FastMath.nextAfter(Float.NaN, 0F), 0F);
        assertEquals(Float.POSITIVE_INFINITY, FastMath.nextUp(Float.MAX_VALUE), 0F);
        assertEquals(Float.NEGATIVE_INFINITY, FastMath.nextDown(-Float.MAX_VALUE), 0F);
        assertEquals(Float.MIN_VALUE, FastMath.nextAfter(0F, 1F), 0F);
        assertEquals(-Float.MIN_VALUE, FastMath.nextAfter(0F, -1F), 0F);
        assertEquals(0F, FastMath.nextAfter(Float.MIN_VALUE, -1F), 0F);
        assertEquals(0F, FastMath.nextAfter(-Float.MIN_VALUE, 1F), 0F);
    }

    @Test
    public void testDoubleScalbSpecialCases() {
        assertEquals(2.5269841324701218E-175,  FastMath.scalb(2.2250738585072014E-308, 442), 0D);
        assertEquals(1.307993905256674E297,    FastMath.scalb(1.1102230246251565E-16, 1040), 0D);
        assertEquals(7.2520887996488946E-217,  FastMath.scalb(Double.MIN_VALUE,        356), 0D);
        assertEquals(8.98846567431158E307,     FastMath.scalb(Double.MIN_VALUE,       2097), 0D);
        assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb(Double.MIN_VALUE,       2098), 0D);
        assertEquals(1.1125369292536007E-308,  FastMath.scalb(2.225073858507201E-308,   -1), 0D);
        assertEquals(1.0E-323,                 FastMath.scalb(Double.MAX_VALUE,      -2097), 0D);
        assertEquals(Double.MIN_VALUE,         FastMath.scalb(Double.MAX_VALUE,      -2098), 0D);
        assertEquals(0,                        FastMath.scalb(Double.MAX_VALUE,      -2099), 0D);
        assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb(Double.POSITIVE_INFINITY, -1000000), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-1.1102230246251565E-16, 1078), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-1.1102230246251565E-16,  1079), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-2.2250738585072014E-308, 2047), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-2.2250738585072014E-308, 2048), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-1.7976931348623157E308,  2147483647), 0D);
        assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb( 1.7976931348623157E308,  2147483647), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-1.1102230246251565E-16,  2147483647), 0D);
        assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb( 1.1102230246251565E-16,  2147483647), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-2.2250738585072014E-308, 2147483647), 0D);
        assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb( 2.2250738585072014E-308, 2147483647), 0D);
    }

    @Test
    public void testFloatScalbSpecialCases() {
        assertEquals(0f, FastMath.scalb(Float.MIN_VALUE, -30), 0F);
        assertEquals(2 * Float.MIN_VALUE, FastMath.scalb(Float.MIN_VALUE, 1), 0F);
        assertEquals(7.555786e22f, FastMath.scalb(Float.MAX_VALUE, -52), 0F);
        assertEquals(1.7014118e38f, FastMath.scalb(Float.MIN_VALUE, 276), 0F);
        assertEquals(Float.POSITIVE_INFINITY, FastMath.scalb(Float.MIN_VALUE, 277), 0F);
        assertEquals(5.8774718e-39f, FastMath.scalb(1.1754944e-38f, -1), 0F);
        assertEquals(2 * Float.MIN_VALUE, FastMath.scalb(Float.MAX_VALUE, -276), 0F);
        assertEquals(Float.MIN_VALUE, FastMath.scalb(Float.MAX_VALUE, -277), 0F);
        assertEquals(0, FastMath.scalb(Float.MAX_VALUE, -278), 0F);
        assertEquals(Float.POSITIVE_INFINITY, FastMath.scalb(Float.POSITIVE_INFINITY, -1000000), 0F);
        assertEquals(-3.13994498e38f, FastMath.scalb(-1.1e-7f, 151), 0F);
        assertEquals(Float.NEGATIVE_INFINITY, FastMath.scalb(-1.1e-7f, 152), 0F);
        assertEquals(Float.POSITIVE_INFINITY, FastMath.scalb(3.4028235E38f, 2147483647), 0F);
        assertEquals(Float.NEGATIVE_INFINITY, FastMath.scalb(-3.4028235E38f, 2147483647), 0F);
    }

    private boolean compareClassMethods(Class<?> class1, Class<?> class2){
        boolean allfound = true;
        for(Method method1 : class1.getDeclaredMethods()){
            if (Modifier.isPublic(method1.getModifiers())){
                Type []params = method1.getGenericParameterTypes();
                try {
                    class2.getDeclaredMethod(method1.getName(), (Class[]) params);
                } catch (NoSuchMethodException e) {
                    allfound = false;
                    System.out.println(class2.getSimpleName()+" does not implement: "+method1);
                }
            }
        }
        return allfound;
    }

    @Test
    public void checkMissingFastMathClasses() {
        boolean ok = compareClassMethods(StrictMath.class, FastMath.class);
        assertTrue("FastMath should implement all StrictMath methods", ok);
    }

    @Ignore
    @Test
    public void checkExtraFastMathClasses() {
        compareClassMethods(FastMath.class, StrictMath.class);
    }

    @Test
    public void testSignumDouble() {
        final double delta = 0.0;
        assertEquals(1.0, FastMath.signum(2.0), delta);
        assertEquals(0.0, FastMath.signum(0.0), delta);
        assertEquals(-1.0, FastMath.signum(-2.0), delta);
        TestUtils.assertSame(-0. / 0., FastMath.signum(Double.NaN));
    }

    @Test
    public void testSignumFloat() {
        final float delta = 0.0F;
        assertEquals(1.0F, FastMath.signum(2.0F), delta);
        assertEquals(0.0F, FastMath.signum(0.0F), delta);
        assertEquals(-1.0F, FastMath.signum(-2.0F), delta);
        TestUtils.assertSame(Float.NaN, FastMath.signum(Float.NaN));
    }

    @Test
    public void testLogWithBase() {
        assertEquals(2.0, FastMath.log(2, 4), 0);
        assertEquals(3.0, FastMath.log(2, 8), 0);
        assertTrue(Double.isNaN(FastMath.log(-1, 1)));
        assertTrue(Double.isNaN(FastMath.log(1, -1)));
        assertTrue(Double.isNaN(FastMath.log(0, 0)));
        assertEquals(0, FastMath.log(0, 10), 0);
        assertEquals(Double.NEGATIVE_INFINITY, FastMath.log(10, 0), 0);
    }

    @Test
    public void testIndicatorDouble() {
        double delta = 0.0;
        assertEquals(1.0, FastMath.copySign(1d, 2.0), delta);
        assertEquals(1.0, FastMath.copySign(1d, 0.0), delta);
        assertEquals(-1.0, FastMath.copySign(1d, -0.0), delta);
        assertEquals(1.0, FastMath.copySign(1d, Double.POSITIVE_INFINITY), delta);
        assertEquals(-1.0, FastMath.copySign(1d, Double.NEGATIVE_INFINITY), delta);
        assertEquals(1.0, FastMath.copySign(1d, Double.NaN), delta);
        assertEquals(-1.0, FastMath.copySign(1d, -2.0), delta);
    }

    @Test
    public void testIndicatorFloat() {
        float delta = 0.0F;
        assertEquals(1.0F, FastMath.copySign(1d, 2.0F), delta);
        assertEquals(1.0F, FastMath.copySign(1d, 0.0F), delta);
        assertEquals(-1.0F, FastMath.copySign(1d, -0.0F), delta);
        assertEquals(1.0F, FastMath.copySign(1d, Float.POSITIVE_INFINITY), delta);
        assertEquals(-1.0F, FastMath.copySign(1d, Float.NEGATIVE_INFINITY), delta);
        assertEquals(1.0F, FastMath.copySign(1d, Float.NaN), delta);
        assertEquals(-1.0F, FastMath.copySign(1d, -2.0F), delta);
    }

    @Test
    public void testIntPow() {
        final int maxExp = 300;
        DfpField field = new DfpField(40);
        final double base = 1.23456789;
        Dfp baseDfp = field.newDfp(base);
        Dfp dfpPower = field.getOne();
        for (int i = 0; i < maxExp; i++) {
            assertEquals("exp=" + i, dfpPower.toDouble(), FastMath.pow(base, i),
                         0.6 * FastMath.ulp(dfpPower.toDouble()));
            dfpPower = dfpPower.multiply(baseDfp);
        }
    }

    @Test
    public void testIntPowHuge() {
        assertTrue(Double.isInfinite(FastMath.pow(FastMath.scalb(1.0, 500), 4)));
    }

    @Test(timeout=5000L) // This test must finish in finite time.
    public void testIntPowLongMinValue() {
        assertEquals(1.0, FastMath.pow(1.0, Long.MIN_VALUE), -1.0);
    }

    @Test(timeout=5000L)
    public void testIntPowSpecialCases() {
        final double EXACT = -1.0;
        final double DOUBLES[] = new double[]
            {
                Double.NEGATIVE_INFINITY, -0.0, Double.NaN, 0.0, Double.POSITIVE_INFINITY,
                Long.MIN_VALUE, Integer.MIN_VALUE, Short.MIN_VALUE, Byte.MIN_VALUE,
                -(double)Long.MIN_VALUE, -(double)Integer.MIN_VALUE, -(double)Short.MIN_VALUE, -(double)Byte.MIN_VALUE,
                Byte.MAX_VALUE, Short.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE,
                -Byte.MAX_VALUE, -Short.MAX_VALUE, -Integer.MAX_VALUE, -Long.MAX_VALUE,
                Float.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Float.MIN_VALUE,
                -Float.MAX_VALUE, -Double.MAX_VALUE, -Double.MIN_VALUE, -Float.MIN_VALUE,
                0.5, 0.1, 0.2, 0.8, 1.1, 1.2, 1.5, 1.8, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 1.3, 2.2, 2.5, 2.8, 33.0, 33.1, 33.5, 33.8, 10.0, 300.0, 400.0, 500.0,
                -0.5, -0.1, -0.2, -0.8, -1.1, -1.2, -1.5, -1.8, -1.0, -2.0, -3.0, -4.0, -5.0, -6.0, -7.0, -8.0, -9.0, -1.3, -2.2, -2.5, -2.8, -33.0, -33.1, -33.5, -33.8, -10.0, -300.0, -400.0, -500.0
            };

        final long INTS[] = new long[]{Long.MAX_VALUE, Long.MAX_VALUE - 1, Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MIN_VALUE + 2, Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2, 0, 1, 2, 3, 5, 8, 10, 20, 100, 300, 500, -1, -2, -3, -5, -8, -10, -20, -100, -300, -500};
        // Special cases from Math.pow javadoc:
        // If the second argument is positive or negative zero, then the result is 1.0.
        for (double d : DOUBLES) {
            assertEquals(1.0, FastMath.pow(d, 0L), EXACT);
        }
        // If the second argument is 1.0, then the result is the same as the first argument.
        for (double d : DOUBLES) {
            assertEquals(d, FastMath.pow(d, 1L), EXACT);
        }
        // If the second argument is NaN, then the result is NaN. <- Impossible with int.
        // If the first argument is NaN and the second argument is nonzero, then the result is NaN.
        for (long i : INTS) {
            if (i != 0L) {
                assertEquals(Double.NaN, FastMath.pow(Double.NaN, i), EXACT);
            }
        }
        // If the absolute value of the first argument is greater than 1 and the second argument is positive infinity, or
        // the absolute value of the first argument is less than 1 and the second argument is negative infinity, then the result is positive infinity.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(d, Long.MAX_VALUE - 1L), EXACT);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(d, Long.MIN_VALUE), EXACT);
            }
        }
        // Note: Long.MAX_VALUE isn't actually an infinity, so its parity affects the sign of resulting infinity.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertTrue(Double.isInfinite(FastMath.pow(d, Long.MAX_VALUE)));
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertTrue(Double.isInfinite(FastMath.pow(d, Long.MIN_VALUE + 1L)));
            }
        }
        // If the absolute value of the first argument is greater than 1 and the second argument is negative infinity, or
        // the absolute value of the first argument is less than 1 and the second argument is positive infinity, then the result is positive zero.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertEquals(0.0, FastMath.pow(d, Long.MIN_VALUE), EXACT);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertEquals(0.0, FastMath.pow(d, Long.MAX_VALUE - 1L), EXACT);
            }
        }
        // Note: Long.MAX_VALUE isn't actually an infinity, so its parity affects the sign of resulting zero.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertTrue(FastMath.pow(d, Long.MIN_VALUE + 1L) == 0.0);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertTrue(FastMath.pow(d, Long.MAX_VALUE) == 0.0);
            }
        }
        // If the absolute value of the first argument equals 1 and the second argument is infinite, then the result is NaN. <- Impossible with int.
        // If the first argument is positive zero and the second argument is greater than zero, or
        // the first argument is positive infinity and the second argument is less than zero, then the result is positive zero.
        for (long i : INTS) {
            if (i > 0L) {
                assertEquals(0.0, FastMath.pow(0.0, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L) {
                assertEquals(0.0, FastMath.pow(Double.POSITIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is positive zero and the second argument is less than zero, or
        // the first argument is positive infinity and the second argument is greater than zero, then the result is positive infinity.
        for (long i : INTS) {
            if (i < 0L) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(0.0, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i > 0L) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(Double.POSITIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is greater than zero but not a finite odd integer, or
        // the first argument is negative infinity and the second argument is less than zero but not a finite odd integer, then the result is positive zero.
        for (long i : INTS) {
            if (i > 0L && (i & 1L) == 0L) {
                assertEquals(0.0, FastMath.pow(-0.0, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L && (i & 1L) == 0L) {
                assertEquals(0.0, FastMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is a positive finite odd integer, or
        // the first argument is negative infinity and the second argument is a negative finite odd integer, then the result is negative zero.
        for (long i : INTS) {
            if (i > 0L && (i & 1L) == 1L) {
                assertEquals(-0.0, FastMath.pow(-0.0, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L && (i & 1L) == 1L) {
                assertEquals(-0.0, FastMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is less than zero but not a finite odd integer, or
        // the first argument is negative infinity and the second argument is greater than zero but not a finite odd integer, then the result is positive infinity.
        for (long i : INTS) {
            if (i > 0L && (i & 1L) == 0L) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L && (i & 1L) == 0L) {
                assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(-0.0, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is a negative finite odd integer, or
        // the first argument is negative infinity and the second argument is a positive finite odd integer, then the result is negative infinity.
        for (long i : INTS) {
            if (i > 0L && (i & 1L) == 1L) {
                assertEquals(Double.NEGATIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L && (i & 1L) == 1L) {
                assertEquals(Double.NEGATIVE_INFINITY, FastMath.pow(-0.0, i), EXACT);
            }
        }
        for (double d : DOUBLES) {
            // If the first argument is finite and less than zero
            if (d < 0.0 && Math.abs(d) <= Double.MAX_VALUE) {
                for (long i : INTS) {
                    // if the second argument is a finite even integer, the result is equal to the result of raising the absolute value of the first argument to the power of the second argument
                    if ((i & 1L) == 0L) assertEquals(FastMath.pow(-d, i), FastMath.pow(d, i), EXACT);
                    // if the second argument is a finite odd integer, the result is equal to the negative of the result of raising the absolute value of the first argument to the power of the second argument
                    else assertEquals(-FastMath.pow(-d, i), FastMath.pow(d, i), EXACT);
                    // if the second argument is finite and not an integer, then the result is NaN. <- Impossible with int.
                }
            }
        }
        // If both arguments are integers, then the result is exactly equal to the mathematical result of raising the first argument to the power
        // of the second argument if that result can in fact be represented exactly as a double value. <- Other tests.
    }

    @Test
    public void testIncrementExactInt() {
        int[] specialValues = new int[] {
            Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2,
            Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 2,
            -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            -1 - (Integer.MIN_VALUE / 2), 0 - (Integer.MIN_VALUE / 2), 1 - (Integer.MIN_VALUE / 2),
            -1 + (Integer.MAX_VALUE / 2), 0 + (Integer.MAX_VALUE / 2), 1 + (Integer.MAX_VALUE / 2),
        };
        for (int a : specialValues) {
            BigInteger bdA   = BigInteger.valueOf(a);
            BigInteger bdSum = bdA.add(BigInteger.ONE);
            if (bdSum.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0 ||
                bdSum.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                try {
                    FastMath.incrementExact(a);
                    Assert.fail("an exception should have been thrown");
                } catch (MathArithmeticException mae) {
                    // expected
                }
            } else {
                assertEquals(bdSum, BigInteger.valueOf(FastMath.incrementExact(a)));
            }
        }
    }

    @Test
    public void testDecrementExactInt() {
        int[] specialValues = new int[] {
            Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2,
            Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 2,
            -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            -1 - (Integer.MIN_VALUE / 2), 0 - (Integer.MIN_VALUE / 2), 1 - (Integer.MIN_VALUE / 2),
            -1 + (Integer.MAX_VALUE / 2), 0 + (Integer.MAX_VALUE / 2), 1 + (Integer.MAX_VALUE / 2),
        };
        for (int a : specialValues) {
            BigInteger bdA   = BigInteger.valueOf(a);
            BigInteger bdSub = bdA.subtract(BigInteger.ONE);
            if (bdSub.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0 ||
                bdSub.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                try {
                    FastMath.decrementExact(a);
                    fail("an exception should have been thrown");
                } catch (MathArithmeticException mae) {
                    // expected
                }
            } else {
                assertEquals(bdSub, BigInteger.valueOf(FastMath.decrementExact(a)));
            }
        }
    }

    @Test
    public void testAddExactInt() {
        int[] specialValues = new int[] {
            Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2,
            Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 2,
            -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            -1 - (Integer.MIN_VALUE / 2), 0 - (Integer.MIN_VALUE / 2), 1 - (Integer.MIN_VALUE / 2),
            -1 + (Integer.MAX_VALUE / 2), 0 + (Integer.MAX_VALUE / 2), 1 + (Integer.MAX_VALUE / 2),
        };
        for (int a : specialValues) {
            for (int b : specialValues) {
                BigInteger bdA   = BigInteger.valueOf(a);
                BigInteger bdB   = BigInteger.valueOf(b);
                BigInteger bdSum = bdA.add(bdB);
                if (bdSum.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0 ||
                        bdSum.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                    try {
                        FastMath.addExact(a, b);
                        fail("an exception should have been thrown");
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdSum, BigInteger.valueOf(FastMath.addExact(a, b)));
                }
            }
        }
    }

    @Test
    public void testAddExactLong() {
        long[] specialValues = new long[] {
            Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MIN_VALUE + 2,
            Long.MAX_VALUE, Long.MAX_VALUE - 1, Long.MAX_VALUE - 2,
            -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            -1 - (Long.MIN_VALUE / 2), 0 - (Long.MIN_VALUE / 2), 1 - (Long.MIN_VALUE / 2),
            -1 + (Long.MAX_VALUE / 2), 0 + (Long.MAX_VALUE / 2), 1 + (Long.MAX_VALUE / 2),
        };
        for (long a : specialValues) {
            for (long b : specialValues) {
                BigInteger bdA   = BigInteger.valueOf(a);
                BigInteger bdB   = BigInteger.valueOf(b);
                BigInteger bdSum = bdA.add(bdB);
                if (bdSum.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0 ||
                        bdSum.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                    try {
                        FastMath.addExact(a, b);
                        fail("an exception should have been thrown");
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdSum, BigInteger.valueOf(FastMath.addExact(a, b)));
                }
            }
        }
    }

    @Test
    public void testSubtractExactInt() {
        int[] specialValues = new int[] {
            Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2,
            Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 2,
            -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            -1 - (Integer.MIN_VALUE / 2), 0 - (Integer.MIN_VALUE / 2), 1 - (Integer.MIN_VALUE / 2),
            -1 + (Integer.MAX_VALUE / 2), 0 + (Integer.MAX_VALUE / 2), 1 + (Integer.MAX_VALUE / 2),
        };
        for (int a : specialValues) {
            for (int b : specialValues) {
                BigInteger bdA   = BigInteger.valueOf(a);
                BigInteger bdB   = BigInteger.valueOf(b);
                BigInteger bdSub = bdA.subtract(bdB);
                if (bdSub.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0 ||
                        bdSub.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                    try {
                        FastMath.subtractExact(a, b);
                        fail("an exception should have been thrown");
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdSub, BigInteger.valueOf(FastMath.subtractExact(a, b)));
                }
            }
        }
    }

    @Test
    public void testSubtractExactLong() {
        long[] specialValues = new long[] {
            Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MIN_VALUE + 2,
            Long.MAX_VALUE, Long.MAX_VALUE - 1, Long.MAX_VALUE - 2,
            -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            -1 - (Long.MIN_VALUE / 2), 0 - (Long.MIN_VALUE / 2), 1 - (Long.MIN_VALUE / 2),
            -1 + (Long.MAX_VALUE / 2), 0 + (Long.MAX_VALUE / 2), 1 + (Long.MAX_VALUE / 2),
        };
        for (long a : specialValues) {
            for (long b : specialValues) {
                BigInteger bdA   = BigInteger.valueOf(a);
                BigInteger bdB   = BigInteger.valueOf(b);
                BigInteger bdSub = bdA.subtract(bdB);
                if (bdSub.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0 ||
                        bdSub.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                    try {
                        FastMath.subtractExact(a, b);
                        fail("an exception should have been thrown");
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdSub, BigInteger.valueOf(FastMath.subtractExact(a, b)));
                }
            }
        }
    }

    @Test
    public void testMultiplyExactInt() {
        int[] specialValues = new int[] {
            Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2,
            Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MAX_VALUE - 2,
            -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            -1 - (Integer.MIN_VALUE / 2), 0 - (Integer.MIN_VALUE / 2), 1 - (Integer.MIN_VALUE / 2),
            -1 + (Integer.MAX_VALUE / 2), 0 + (Integer.MAX_VALUE / 2), 1 + (Integer.MAX_VALUE / 2),
        };
        for (int a : specialValues) {
            for (int b : specialValues) {
                BigInteger bdA   = BigInteger.valueOf(a);
                BigInteger bdB   = BigInteger.valueOf(b);
                BigInteger bdMul = bdA.multiply(bdB);
                if (bdMul.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) < 0 ||
                    bdMul.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0) {
                    try {
                        FastMath.multiplyExact(a, b);
                        fail("an exception should have been thrown " + a + b);
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdMul, BigInteger.valueOf(FastMath.multiplyExact(a, b)));
                }
            }
        }
    }

    @Test
    public void testMultiplyExactLong() {
        long[] specialValues = new long[] {
            Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MIN_VALUE + 2,
            Long.MAX_VALUE, Long.MAX_VALUE - 1, Long.MAX_VALUE - 2,
            -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
            -1 - (Long.MIN_VALUE / 2), 0 - (Long.MIN_VALUE / 2), 1 - (Long.MIN_VALUE / 2),
            -1 + (Long.MAX_VALUE / 2), 0 + (Long.MAX_VALUE / 2), 1 + (Long.MAX_VALUE / 2),
        };
        for (long a : specialValues) {
            for (long b : specialValues) {
                BigInteger bdA   = BigInteger.valueOf(a);
                BigInteger bdB   = BigInteger.valueOf(b);
                BigInteger bdMul = bdA.multiply(bdB);
                if (bdMul.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0 ||
                    bdMul.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
                    try {
                        FastMath.multiplyExact(a, b);
                        fail("an exception should have been thrown " + a + b);
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdMul, BigInteger.valueOf(FastMath.multiplyExact(a, b)));
                }
            }
        }
    }

    @Test(expected=MathArithmeticException.class)
    public void testToIntExactTooLow() {
        FastMath.toIntExact(-1l + Integer.MIN_VALUE);
    }

    @Test(expected=MathArithmeticException.class)
    public void testToIntExactTooHigh() {
        FastMath.toIntExact(+1l + Integer.MAX_VALUE);
    }

    @Test
    public void testToIntExact() {
        for (int n = -1000; n < 1000; ++n) {
            assertEquals(n, FastMath.toIntExact(0l + n));
        }
        assertEquals(Integer.MIN_VALUE, FastMath.toIntExact(0l + Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, FastMath.toIntExact(0l + Integer.MAX_VALUE));
    }

    @Test
    public void testFloorDivInt() {
        assertEquals(+1, FastMath.floorDiv(+4, +3));
        assertEquals(-2, FastMath.floorDiv(-4, +3));
        assertEquals(-2, FastMath.floorDiv(+4, -3));
        assertEquals(+1, FastMath.floorDiv(-4, -3));
        try {
            FastMath.floorDiv(1, 0);
            fail("an exception should have been thrown");
        } catch (MathArithmeticException mae) {
            // expected
        }
        for (int a = -100; a <= 100; ++a) {
            for (int b = -100; b <= 100; ++b) {
                if (b != 0) {
                    assertEquals(poorManFloorDiv(a, b), FastMath.floorDiv(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorModInt() {
        assertEquals(+1, FastMath.floorMod(+4, +3));
        assertEquals(+2, FastMath.floorMod(-4, +3));
        assertEquals(-2, FastMath.floorMod(+4, -3));
        assertEquals(-1, FastMath.floorMod(-4, -3));
        try {
            FastMath.floorMod(1, 0);
            fail("an exception should have been thrown");
        } catch (MathArithmeticException mae) {
            // expected
        }
        for (int a = -100; a <= 100; ++a) {
            for (int b = -100; b <= 100; ++b) {
                if (b != 0) {
                    assertEquals(poorManFloorMod(a, b), FastMath.floorMod(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorDivModInt() {
        UniformRandomProvider generator = RandomSource.create(RandomSource.WELL_1024_A,
                                                              0x7ccab45edeaab90al);
        for (int i = 0; i < 10000; ++i) {
            int a = generator.nextInt();
            int b = generator.nextInt();
            if (b == 0) {
                try {
                    FastMath.floorDiv(a, b);
                    fail("an exception should have been thrown");
                } catch (MathArithmeticException mae) {
                    // expected
                }
            } else {
                int d = FastMath.floorDiv(a, b);
                int m = FastMath.floorMod(a, b);
                assertEquals(FastMath.toIntExact(poorManFloorDiv(a, b)), d);
                assertEquals(FastMath.toIntExact(poorManFloorMod(a, b)), m);
                assertEquals(a, d * b + m);
                if (b < 0) {
                    assertTrue(m <= 0);
                    assertTrue(-m < -b);
                } else {
                    assertTrue(m >= 0);
                    assertTrue(m < b);
                }
            }
        }
    }

    @Test
    public void testFloorDivLong() {
        assertEquals(+1l, FastMath.floorDiv(+4l, +3l));
        assertEquals(-2l, FastMath.floorDiv(-4l, +3l));
        assertEquals(-2l, FastMath.floorDiv(+4l, -3l));
        assertEquals(+1l, FastMath.floorDiv(-4l, -3l));
        try {
            FastMath.floorDiv(1l, 0l);
            fail("an exception should have been thrown");
        } catch (MathArithmeticException mae) {
            // expected
        }
        for (long a = -100l; a <= 100l; ++a) {
            for (long b = -100l; b <= 100l; ++b) {
                if (b != 0) {
                    assertEquals(poorManFloorDiv(a, b), FastMath.floorDiv(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorModLong() {
        assertEquals(+1l, FastMath.floorMod(+4l, +3l));
        assertEquals(+2l, FastMath.floorMod(-4l, +3l));
        assertEquals(-2l, FastMath.floorMod(+4l, -3l));
        assertEquals(-1l, FastMath.floorMod(-4l, -3l));
        try {
            FastMath.floorMod(1l, 0l);
            fail("an exception should have been thrown");
        } catch (MathArithmeticException mae) {
            // expected
        }
        for (long a = -100l; a <= 100l; ++a) {
            for (long b = -100l; b <= 100l; ++b) {
                if (b != 0) {
                    assertEquals(poorManFloorMod(a, b), FastMath.floorMod(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorDivModLong() {
        UniformRandomProvider generator = RandomSource.create(RandomSource.WELL_1024_A,
                                                              0xb87b9bc14c96ccd5l);
        for (int i = 0; i < 10000; ++i) {
            long a = generator.nextLong();
            long b = generator.nextLong();
            if (b == 0) {
                try {
                    FastMath.floorDiv(a, b);
                    fail("an exception should have been thrown");
                } catch (MathArithmeticException mae) {
                    // expected
                }
            } else {
                long d = FastMath.floorDiv(a, b);
                long m = FastMath.floorMod(a, b);
                assertEquals(poorManFloorDiv(a, b), d);
                assertEquals(poorManFloorMod(a, b), m);
                assertEquals(a, d * b + m);
                if (b < 0) {
                    assertTrue(m <= 0);
                    assertTrue(-m < -b);
                } else {
                    assertTrue(m >= 0);
                    assertTrue(m < b);
                }
            }
        }
    }

    private long poorManFloorDiv(long a, long b) {

        // find q0, r0 such that a = q0 b + r0
        BigInteger q0 = BigInteger.valueOf(a / b);
        BigInteger r0 = BigInteger.valueOf(a % b);
        BigInteger fd = BigInteger.valueOf(Integer.MIN_VALUE);
        BigInteger bigB = BigInteger.valueOf(b);

        for (int k = -2; k < 2; ++k) {
            // find another pair q, r such that a = q b + r
            BigInteger bigK = BigInteger.valueOf(k);
            BigInteger q = q0.subtract(bigK);
            BigInteger r = r0.add(bigK.multiply(bigB));
            if (r.abs().compareTo(bigB.abs()) < 0 &&
                (r.longValue() == 0l || ((r.longValue() ^ b) & 0x8000000000000000l) == 0)) {
                if (fd.compareTo(q) < 0) {
                    fd = q;
                }
            }
        }

        return fd.longValue();

    }

    private long poorManFloorMod(long a, long b) {
        return a - b * poorManFloorDiv(a, b);
    }

    /**
     * http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6430675
     */
    @Test
    public void testRoundDown() {
        double x = 0x1.fffffffffffffp-2; // greatest floating point value less than 0.5
        assertTrue(x < 0.5d);
        assertEquals(0, FastMath.round(x));

        x = 4503599627370497.0; // x = Math.pow(2, 52) + 1;
        assertEquals("4503599627370497", new BigDecimal(x).toString());
        assertTrue(x == Math.rint(x));
        assertTrue(x == FastMath.round(x));
        //assertTrue(x == Math.round(x)); // fails with Java 7, fixed in Java 8
    }

}
