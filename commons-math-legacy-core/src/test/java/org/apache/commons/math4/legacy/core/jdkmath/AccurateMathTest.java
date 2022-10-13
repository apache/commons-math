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
package org.apache.commons.math4.legacy.core.jdkmath;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.apache.commons.numbers.core.ArithmeticUtils;
import org.apache.commons.numbers.core.Precision;
import org.apache.commons.math4.legacy.core.dfp.Dfp;
import org.apache.commons.math4.legacy.core.dfp.DfpField;
import org.apache.commons.math4.legacy.core.dfp.DfpMath;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

// Unit test should be moved to module "commons-math-core".
// [Currently, it can't be because it depends on "legacy" classes.]
import org.apache.commons.math4.core.jdkmath.AccurateMath;

public class AccurateMathTest {
    // CHECKSTYLE: stop Regexp
    // The above comment allows System.out.print

    private static final double MAX_ERROR_ULP = 0.51;
    private static final int NUMBER_OF_TRIALS = 1000;

    private DfpField field;
    private UniformRandomProvider generator;

    @Before
    public void setUp() {
        field = new DfpField(40);
        generator = RandomSource.MT.create(6176597458463500194L);
    }

    @Test
    public void testMinMaxDouble() {
        double[][] pairs = {
            {-50.0, 50.0},
            {Double.POSITIVE_INFINITY, 1.0},
            {Double.NEGATIVE_INFINITY, 1.0},
            {Double.NaN, 1.0},
            {Double.POSITIVE_INFINITY, 0.0},
            {Double.NEGATIVE_INFINITY, 0.0},
            {Double.NaN, 0.0},
            {Double.NaN, Double.NEGATIVE_INFINITY},
            {Double.NaN, Double.POSITIVE_INFINITY},
            {Precision.SAFE_MIN, Precision.EPSILON}
        };
        for (double[] pair : pairs) {
            assertEquals("min(" + pair[0] + ", " + pair[1] + ")",
                         Math.min(pair[0], pair[1]),
                         AccurateMath.min(pair[0], pair[1]),
                         Precision.EPSILON);
            assertEquals("min(" + pair[1] + ", " + pair[0] + ")",
                         Math.min(pair[1], pair[0]),
                         AccurateMath.min(pair[1], pair[0]),
                         Precision.EPSILON);
            assertEquals("max(" + pair[0] + ", " + pair[1] + ")",
                         Math.max(pair[0], pair[1]),
                         AccurateMath.max(pair[0], pair[1]),
                         Precision.EPSILON);
            assertEquals("max(" + pair[1] + ", " + pair[0] + ")",
                         Math.max(pair[1], pair[0]),
                         AccurateMath.max(pair[1], pair[0]),
                         Precision.EPSILON);
        }
    }

    @Test
    public void testMinMaxFloat() {
        float[][] pairs = {
            {-50.0f, 50.0f},
            {Float.POSITIVE_INFINITY, 1.0f},
            {Float.NEGATIVE_INFINITY, 1.0f},
            {Float.NaN, 1.0f},
            {Float.POSITIVE_INFINITY, 0.0f},
            {Float.NEGATIVE_INFINITY, 0.0f},
            {Float.NaN, 0.0f},
            {Float.NaN, Float.NEGATIVE_INFINITY},
            {Float.NaN, Float.POSITIVE_INFINITY}
        };
        for (float[] pair : pairs) {
            assertEquals("min(" + pair[0] + ", " + pair[1] + ")",
                    Math.min(pair[0], pair[1]),
                    AccurateMath.min(pair[0], pair[1]),
                    Precision.EPSILON);
            assertEquals("min(" + pair[1] + ", " + pair[0] + ")",
                    Math.min(pair[1], pair[0]),
                    AccurateMath.min(pair[1], pair[0]),
                    Precision.EPSILON);
            assertEquals("max(" + pair[0] + ", " + pair[1] + ")",
                    Math.max(pair[0], pair[1]),
                    AccurateMath.max(pair[0], pair[1]),
                    Precision.EPSILON);
            assertEquals("max(" + pair[1] + ", " + pair[0] + ")",
                    Math.max(pair[1], pair[0]),
                    AccurateMath.max(pair[1], pair[0]),
                    Precision.EPSILON);
        }
    }

    @Test
    public void testConstants() {
        assertEquals(Math.PI, AccurateMath.PI, 1.0e-20);
        assertEquals(Math.E, AccurateMath.E, 1.0e-20);
    }

    @Test
    public void testAtan2() {
        double y1 = 1.2713504628280707e10;
        double x1 = -5.674940885228782e-10;
        assertEquals(Math.atan2(y1, x1), AccurateMath.atan2(y1, x1), 2 * Precision.EPSILON);
        double y2 = 0.0;
        double x2 = Double.POSITIVE_INFINITY;
        assertEquals(Math.atan2(y2, x2), AccurateMath.atan2(y2, x2), Precision.SAFE_MIN);
    }

    @Test
    public void testHyperbolic() {
        double maxErr = 0;
        for (double x = -30; x < 30; x += 0.001) {
            double tst = AccurateMath.sinh(x);
            double ref = Math.sinh(x);
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(ref - tst) / AccurateMath.ulp(ref));
        }
        assertEquals(0, maxErr, 2);

        maxErr = 0;
        for (double x = -30; x < 30; x += 0.001) {
            double tst = AccurateMath.cosh(x);
            double ref = Math.cosh(x);
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(ref - tst) / AccurateMath.ulp(ref));
        }
        assertEquals(0, maxErr, 2);

        maxErr = 0;
        for (double x = -0.5; x < 0.5; x += 0.001) {
            double tst = AccurateMath.tanh(x);
            double ref = Math.tanh(x);
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(ref - tst) / AccurateMath.ulp(ref));
        }
        assertEquals(0, maxErr, 4);
    }

    @Test
    public void testMath904() {
        final double x = -1;
        final double y = (5 + 1e-15) * 1e15;
        assertEquals(Math.pow(x, y),
                     AccurateMath.pow(x, y), 0);
        assertEquals(Math.pow(x, -y),
                AccurateMath.pow(x, -y), 0);
    }

    @Test
    public void testMath905LargePositive() {
        final double start = StrictMath.log(Double.MAX_VALUE);
        final double endT = StrictMath.sqrt(2) * StrictMath.sqrt(Double.MAX_VALUE);
        final double end = 2 * StrictMath.log(endT);

        double maxErr = 0;
        for (double x = start; x < end; x += 1e-3) {
            final double tst = AccurateMath.cosh(x);
            final double ref = Math.cosh(x);
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(ref - tst) / AccurateMath.ulp(ref));
        }
        assertEquals(0, maxErr, 3);

        for (double x = start; x < end; x += 1e-3) {
            final double tst = AccurateMath.sinh(x);
            final double ref = Math.sinh(x);
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(ref - tst) / AccurateMath.ulp(ref));
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
            final double tst = AccurateMath.cosh(x);
            final double ref = Math.cosh(x);
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(ref - tst) / AccurateMath.ulp(ref));
        }
        assertEquals(0, maxErr, 3);

        for (double x = start; x > end; x -= 1e-3) {
            final double tst = AccurateMath.sinh(x);
            final double ref = Math.sinh(x);
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(ref - tst) / AccurateMath.ulp(ref));
        }
        assertEquals(0, maxErr, 3);
    }

    @Test
    public void testMath1269() {
        final double arg = 709.8125;
        final double vM = Math.exp(arg);
        final double vFM = AccurateMath.exp(arg);
        assertTrue("exp(" + arg + ") is " + vFM + " instead of " + vM,
                Precision.equalsIncludingNaN(vM, vFM));
    }

    @Test
    public void testHyperbolicInverses() {
        double maxErr = 0;
        for (double x = -30; x < 30; x += 0.01) {
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(x - AccurateMath.sinh(AccurateMath.asinh(x))) / (2 * AccurateMath.ulp(x)));
        }
        assertEquals(0, maxErr, 3);

        maxErr = 0;
        for (double x = 1; x < 30; x += 0.01) {
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(x - AccurateMath.cosh(AccurateMath.acosh(x))) / (2 * AccurateMath.ulp(x)));
        }
        assertEquals(0, maxErr, 2);

        maxErr = 0;
        for (double x = -1 + Precision.EPSILON; x < 1 - Precision.EPSILON; x += 0.0001) {
            maxErr = AccurateMath.max(maxErr, AccurateMath.abs(x - AccurateMath.tanh(AccurateMath.atanh(x))) / (2 * AccurateMath.ulp(x)));
        }
        assertEquals(0, maxErr, 2);
    }

    @Test
    public void testLogAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            double x = Math.exp(generator.nextDouble() * 1416.0 - 708.0) * generator.nextDouble();
            // double x = generator.nextDouble()*2.0;
            double tst = AccurateMath.log(x);
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
            double tst = AccurateMath.log10(x);
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
            double tst = AccurateMath.log1p(x);
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
        assertTrue("Logp of -1.0 should be -Inf", Double.isInfinite(AccurateMath.log1p(-1.0)));
    }

    @Test
    public void testLogSpecialCases() {
        assertEquals("Log of zero should be -Inf", Double.NEGATIVE_INFINITY, AccurateMath.log(0.0), 1.0);
        assertEquals("Log of -zero should be -Inf", Double.NEGATIVE_INFINITY, AccurateMath.log(-0.0), 1.0);
        assertTrue("Log of NaN should be NaN", Double.isNaN(AccurateMath.log(Double.NaN)));
        assertTrue("Log of negative number should be NaN", Double.isNaN(AccurateMath.log(-1.0)));
        assertEquals("Log of Double.MIN_VALUE should be -744.4400719213812", -744.4400719213812, AccurateMath.log(Double.MIN_VALUE), Precision.EPSILON);
        assertEquals("Log of infinity should be infinity", Double.POSITIVE_INFINITY, AccurateMath.log(Double.POSITIVE_INFINITY), 1.0);
    }

    @Test
    public void testExpSpecialCases() {
        // Smallest value that will round up to Double.MIN_VALUE
        assertEquals(Double.MIN_VALUE, AccurateMath.exp(-745.1332191019411), Precision.EPSILON);
        assertEquals("exp(-745.1332191019412) should be 0.0", 0.0, AccurateMath.exp(-745.1332191019412), Precision.EPSILON);
        assertTrue("exp of NaN should be NaN", Double.isNaN(AccurateMath.exp(Double.NaN)));
        assertEquals("exp of infinity should be infinity", Double.POSITIVE_INFINITY, AccurateMath.exp(Double.POSITIVE_INFINITY), 1.0);
        assertEquals("exp of -infinity should be 0.0", 0.0, AccurateMath.exp(Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("exp(1) should be Math.E", Math.E, AccurateMath.exp(1.0), Precision.EPSILON);
    }

    @Test
    public void testPowSpecialCases() {
        final double EXACT = -1.0;

        assertEquals("pow(-1, 0) should be 1.0", 1.0, AccurateMath.pow(-1.0, 0.0), Precision.EPSILON);
        assertEquals("pow(-1, -0) should be 1.0", 1.0, AccurateMath.pow(-1.0, -0.0), Precision.EPSILON);
        assertEquals("pow(PI, 1.0) should be PI", AccurateMath.PI, AccurateMath.pow(AccurateMath.PI, 1.0), Precision.EPSILON);
        assertEquals("pow(-PI, 1.0) should be -PI", -AccurateMath.PI, AccurateMath.pow(-AccurateMath.PI, 1.0), Precision.EPSILON);
        assertTrue("pow(PI, NaN) should be NaN", Double.isNaN(AccurateMath.pow(Math.PI, Double.NaN)));
        assertTrue("pow(NaN, PI) should be NaN", Double.isNaN(AccurateMath.pow(Double.NaN, Math.PI)));
        assertEquals("pow(2.0, Infinity) should be Infinity", Double.POSITIVE_INFINITY, AccurateMath.pow(2.0, Double.POSITIVE_INFINITY), 1.0);
        assertEquals("pow(0.5, -Infinity) should be Infinity", Double.POSITIVE_INFINITY, AccurateMath.pow(0.5, Double.NEGATIVE_INFINITY), 1.0);
        assertEquals("pow(0.5, Infinity) should be 0.0", 0.0, AccurateMath.pow(0.5, Double.POSITIVE_INFINITY), Precision.EPSILON);
        assertEquals("pow(2.0, -Infinity) should be 0.0", 0.0, AccurateMath.pow(2.0, Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("pow(0.0, 0.5) should be 0.0", 0.0, AccurateMath.pow(0.0, 0.5), Precision.EPSILON);
        assertEquals("pow(Infinity, -0.5) should be 0.0", 0.0, AccurateMath.pow(Double.POSITIVE_INFINITY, -0.5), Precision.EPSILON);
        assertEquals("pow(0.0, -0.5) should be Inf", Double.POSITIVE_INFINITY, AccurateMath.pow(0.0, -0.5), 1.0);
        assertEquals("pow(Inf, 0.5) should be Inf", Double.POSITIVE_INFINITY, AccurateMath.pow(Double.POSITIVE_INFINITY, 0.5), 1.0);
        assertEquals("pow(-0.0, -3.0) should be -Inf", Double.NEGATIVE_INFINITY, AccurateMath.pow(-0.0, -3.0), 1.0);
        assertEquals("pow(-0.0, Infinity) should be 0.0", 0.0, AccurateMath.pow(-0.0, Double.POSITIVE_INFINITY), Precision.EPSILON);
        assertTrue("pow(-0.0, NaN) should be NaN", Double.isNaN(AccurateMath.pow(-0.0, Double.NaN)));
        assertEquals("pow(-0.0, -tiny) should be Infinity", Double.POSITIVE_INFINITY, AccurateMath.pow(-0.0, -Double.MIN_VALUE), 1.0);
        assertEquals("pow(-0.0, -huge) should be Infinity", Double.POSITIVE_INFINITY, AccurateMath.pow(-0.0, -Double.MAX_VALUE), 1.0);
        assertEquals("pow(-Inf, 3.0) should be -Inf", Double.NEGATIVE_INFINITY, AccurateMath.pow(Double.NEGATIVE_INFINITY, 3.0), 1.0);
        assertEquals("pow(-Inf, -3.0) should be -0.0", -0.0, AccurateMath.pow(Double.NEGATIVE_INFINITY, -3.0), EXACT);
        assertEquals("pow(-0.0, -3.5) should be Inf", Double.POSITIVE_INFINITY, AccurateMath.pow(-0.0, -3.5), 1.0);
        assertEquals("pow(Inf, 3.5) should be Inf", Double.POSITIVE_INFINITY, AccurateMath.pow(Double.POSITIVE_INFINITY, 3.5), 1.0);
        assertEquals("pow(-2.0, 3.0) should be -8.0", -8.0, AccurateMath.pow(-2.0, 3.0), Precision.EPSILON);
        assertTrue("pow(-2.0, 3.5) should be NaN", Double.isNaN(AccurateMath.pow(-2.0, 3.5)));
        assertTrue("pow(NaN, -Infinity) should be NaN", Double.isNaN(AccurateMath.pow(Double.NaN, Double.NEGATIVE_INFINITY)));
        assertEquals("pow(NaN, 0.0) should be 1.0", 1.0, AccurateMath.pow(Double.NaN, 0.0), Precision.EPSILON);
        assertEquals("pow(-Infinity, -Infinity) should be 0.0", 0.0, AccurateMath.pow(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("pow(-huge, -huge) should be 0.0", 0.0, AccurateMath.pow(-Double.MAX_VALUE, -Double.MAX_VALUE), Precision.EPSILON);
        assertTrue("pow(-huge,  huge) should be +Inf", Double.isInfinite(AccurateMath.pow(-Double.MAX_VALUE, Double.MAX_VALUE)));
        assertTrue("pow(NaN, -Infinity) should be NaN", Double.isNaN(AccurateMath.pow(Double.NaN, Double.NEGATIVE_INFINITY)));
        assertEquals("pow(NaN, -0.0) should be 1.0", 1.0, AccurateMath.pow(Double.NaN, -0.0), Precision.EPSILON);
        assertEquals("pow(-Infinity, -Infinity) should be 0.0", 0.0, AccurateMath.pow(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Precision.EPSILON);
        assertEquals("pow(-huge, -huge) should be 0.0", 0.0, AccurateMath.pow(-Double.MAX_VALUE, -Double.MAX_VALUE), Precision.EPSILON);
        assertEquals("pow(-huge,  huge) should be +Inf", Double.POSITIVE_INFINITY, AccurateMath.pow(-Double.MAX_VALUE, Double.MAX_VALUE), 1.0);

        // Added tests for a 100% coverage

        assertTrue("pow(+Inf, NaN) should be NaN", Double.isNaN(AccurateMath.pow(Double.POSITIVE_INFINITY, Double.NaN)));
        assertTrue("pow(1.0, +Inf) should be NaN", Double.isNaN(AccurateMath.pow(1.0, Double.POSITIVE_INFINITY)));
        assertTrue("pow(-Inf, NaN) should be NaN", Double.isNaN(AccurateMath.pow(Double.NEGATIVE_INFINITY, Double.NaN)));
        assertEquals("pow(-Inf, -1.0) should be -0.0", -0.0, AccurateMath.pow(Double.NEGATIVE_INFINITY, -1.0), EXACT);
        assertEquals("pow(-Inf, -2.0) should be 0.0", 0.0, AccurateMath.pow(Double.NEGATIVE_INFINITY, -2.0), EXACT);
        assertEquals("pow(-Inf, 1.0) should be -Inf", Double.NEGATIVE_INFINITY, AccurateMath.pow(Double.NEGATIVE_INFINITY, 1.0), 1.0);
        assertEquals("pow(-Inf, 2.0) should be +Inf", Double.POSITIVE_INFINITY, AccurateMath.pow(Double.NEGATIVE_INFINITY, 2.0), 1.0);
        assertTrue("pow(1.0, -Inf) should be NaN", Double.isNaN(AccurateMath.pow(1.0, Double.NEGATIVE_INFINITY)));
        assertEquals("pow(-0.0, 1.0) should be -0.0", -0.0, AccurateMath.pow(-0.0, 1.0), EXACT);
        assertEquals("pow(0.0, 1.0) should be 0.0", 0.0, AccurateMath.pow(0.0, 1.0), EXACT);
        assertEquals("pow(0.0, +Inf) should be 0.0", 0.0, AccurateMath.pow(0.0, Double.POSITIVE_INFINITY), EXACT);
        assertEquals("pow(-0.0, even) should be 0.0", 0.0, AccurateMath.pow(-0.0, 6.0), EXACT);
        assertEquals("pow(-0.0, odd) should be -0.0", -0.0, AccurateMath.pow(-0.0, 13.0), EXACT);
        assertEquals("pow(-0.0, -even) should be +Inf", Double.POSITIVE_INFINITY, AccurateMath.pow(-0.0, -6.0), EXACT);
        assertEquals("pow(-0.0, -odd) should be -Inf", Double.NEGATIVE_INFINITY, AccurateMath.pow(-0.0, -13.0), EXACT);
        assertEquals("pow(-2.0, 4.0) should be 16.0", 16.0, AccurateMath.pow(-2.0, 4.0), EXACT);
        assertEquals("pow(-2.0, 4.5) should be NaN", Double.NaN, AccurateMath.pow(-2.0, 4.5), EXACT);
        assertEquals("pow(-0.0, -0.0) should be 1.0", 1.0, AccurateMath.pow(-0.0, -0.0), EXACT);
        assertEquals("pow(-0.0, 0.0) should be 1.0", 1.0, AccurateMath.pow(-0.0, 0.0), EXACT);
        assertEquals("pow(0.0, -0.0) should be 1.0", 1.0, AccurateMath.pow(0.0, -0.0), EXACT);
        assertEquals("pow(0.0, 0.0) should be 1.0", 1.0, AccurateMath.pow(0.0, 0.0), EXACT);
    }

    @Test(timeout = 20000L)
    public void testPowAllSpecialCases() {
        final double EXACT = -1.0;
        final double[] DOUBLES = new double[] {
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
            assertEquals(1.0, AccurateMath.pow(d, 0.0), EXACT);
        }
        for (double d : DOUBLES) {
            assertEquals(1.0, AccurateMath.pow(d, -0.0), EXACT);
        }
        // If the second argument is 1.0, then the result is the same as the first argument.
        for (double d : DOUBLES) {
            assertEquals(d, AccurateMath.pow(d, 1.0), EXACT);
        }
        // If the second argument is NaN, then the result is NaN.
        for (double d : DOUBLES) {
            assertEquals(Double.NaN, AccurateMath.pow(d, Double.NaN), EXACT);
        }
        // If the first argument is NaN and the second argument is nonzero, then the result is NaN.
        for (double i : DOUBLES) {
            if (i != 0.0) {
                assertEquals(Double.NaN, AccurateMath.pow(Double.NaN, i), EXACT);
            }
        }
        // If the absolute value of the first argument is greater than 1 and the second argument is positive infinity, or
        // the absolute value of the first argument is less than 1 and the second argument is negative infinity, then the result is positive infinity.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(d, Double.POSITIVE_INFINITY), EXACT);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(d, Double.NEGATIVE_INFINITY), EXACT);
            }
        }
        // If the absolute value of the first argument is greater than 1 and the second argument is negative infinity, or
        // the absolute value of the first argument is less than 1 and the second argument is positive infinity, then the result is positive zero.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertEquals(0.0, AccurateMath.pow(d, Double.NEGATIVE_INFINITY), EXACT);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertEquals(0.0, AccurateMath.pow(d, Double.POSITIVE_INFINITY), EXACT);
            }
        }
        // If the absolute value of the first argument equals 1 and the second argument is infinite, then the result is NaN.
        assertEquals(Double.NaN, AccurateMath.pow(1.0, Double.POSITIVE_INFINITY), EXACT);
        assertEquals(Double.NaN, AccurateMath.pow(1.0, Double.NEGATIVE_INFINITY), EXACT);
        assertEquals(Double.NaN, AccurateMath.pow(-1.0, Double.POSITIVE_INFINITY), EXACT);
        assertEquals(Double.NaN, AccurateMath.pow(-1.0, Double.NEGATIVE_INFINITY), EXACT);
        // If the first argument is positive zero and the second argument is greater than zero, or
        // the first argument is positive infinity and the second argument is less than zero, then the result is positive zero.
        for (double i : DOUBLES) {
            if (i > 0.0) {
                assertEquals(0.0, AccurateMath.pow(0.0, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0) {
                assertEquals(0.0, AccurateMath.pow(Double.POSITIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is positive zero and the second argument is less than zero, or
        // the first argument is positive infinity and the second argument is greater than zero, then the result is positive infinity.
        for (double i : DOUBLES) {
            if (i < 0.0) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(0.0, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i > 0.0) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(Double.POSITIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is greater than zero but not a finite odd integer, or
        // the first argument is negative infinity and the second argument is less than zero but not a finite odd integer, then the result is positive zero.
        for (double i : DOUBLES) {
            if (i > 0.0 && (Double.isInfinite(i) || i % 2.0 == 0.0)) {
                assertEquals(0.0, AccurateMath.pow(-0.0, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0 && (Double.isInfinite(i) || i % 2.0 == 0.0)) {
                assertEquals(0.0, AccurateMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is a positive finite odd integer, or
        // the first argument is negative infinity and the second argument is a negative finite odd integer, then the result is negative zero.
        for (double i : DOUBLES) {
            if (i > 0.0 && i % 2.0 == 1.0) {
                assertEquals(-0.0, AccurateMath.pow(-0.0, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0 && i % 2.0 == -1.0) {
                assertEquals(-0.0, AccurateMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is less than zero but not a finite odd integer, or
        // the first argument is negative infinity and the second argument is greater than zero but not a finite odd integer, then the result is positive infinity.
        for (double i : DOUBLES) {
            if (i > 0.0 && (Double.isInfinite(i) || i % 2.0 == 0.0)) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0 && (Double.isInfinite(i) || i % 2.0 == 0.0)) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(-0.0, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is a negative finite odd integer, or
        // the first argument is negative infinity and the second argument is a positive finite odd integer, then the result is negative infinity.
        for (double i : DOUBLES) {
            if (i > 0.0 && i % 2.0 == 1.0) {
                assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        for (double i : DOUBLES) {
            if (i < 0.0 && i % 2.0 == -1.0) {
                assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.pow(-0.0, i), EXACT);
            }
        }
        for (double d : DOUBLES) {
            // If the first argument is finite and less than zero
            if (d < 0.0 && Math.abs(d) <= Double.MAX_VALUE) {
                for (double i : DOUBLES) {
                    if (Math.abs(i) <= Double.MAX_VALUE) {
                        // if the second argument is a finite even integer, the result is equal to the result of raising the absolute value of the first argument to the power of the second argument
                        if (i % 2.0 == 0.0) {
                            assertEquals(AccurateMath.pow(-d, i), AccurateMath.pow(d, i), EXACT);
                        } else if (Math.abs(i) % 2.0 == 1.0) {
                            // if the second argument is a finite odd integer, the result is equal to the negative of the result of raising the absolute value of the first argument to the power of the second argument
                            assertEquals(-AccurateMath.pow(-d, i), AccurateMath.pow(d, i), EXACT);
                        } else {
                            // if the second argument is finite and not an integer, then the result is NaN.
                            assertEquals(Double.NaN, AccurateMath.pow(d, i), EXACT);
                        }
                    }
                }
            }
        }
        // If both arguments are integers, then the result is exactly equal to the mathematical result of raising the first argument to the power
        // of the second argument if that result can in fact be represented exactly as a double value.
        final int TOO_BIG_TO_CALCULATE = 18; // This value is empirical: 2^18 > 200.000 resulting bits after raising d to power i.
        for (double d : DOUBLES) {
            if (d % 1.0 == 0.0) {
                boolean dNegative = Double.doubleToRawLongBits(d) < 0L;
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
                                expected = BigDecimal.ONE.divide(new BigDecimal(res), 1024, RoundingMode.HALF_UP).doubleValue();
                            }
                        }
                        if (dNegative && bi.testBit(0)) {
                            expected = -expected;
                        }
                        assertEquals(d + "^" + i + "=" + expected + ", Math.pow=" + Math.pow(d, i), expected, AccurateMath.pow(d, i), expected == 0.0 || Double.isInfinite(expected) || Double.isNaN(expected) ? EXACT : 2.0 * Math.ulp(expected));
                    }
                }
            }
        }
    }

    @Test
    public void testPowLargeIntegralDouble() {
        double y = AccurateMath.scalb(1.0, 65);
        assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(AccurateMath.nextUp(1.0), y),    1.0);
        assertEquals(1.0,                      AccurateMath.pow(1.0, y),                     1.0);
        assertEquals(0.0,                      AccurateMath.pow(AccurateMath.nextDown(1.0), y),  1.0);
        assertEquals(0.0,                      AccurateMath.pow(AccurateMath.nextUp(-1.0), y),   1.0);
        assertEquals(1.0,                      AccurateMath.pow(-1.0, y),                    1.0);
        assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(AccurateMath.nextDown(-1.0), y), 1.0);
    }

    @Test
    public void testAtan2SpecialCases() {

        assertTrue("atan2(NaN, 0.0) should be NaN", Double.isNaN(AccurateMath.atan2(Double.NaN, 0.0)));
        assertTrue("atan2(0.0, NaN) should be NaN", Double.isNaN(AccurateMath.atan2(0.0, Double.NaN)));

        final double pinf = Double.POSITIVE_INFINITY;
        final double ninf = Double.NEGATIVE_INFINITY;
        // Test using fractions of pi
        assertAtan2(+0.0,  0.0,  0.0, 1.0);
        assertAtan2(+0.0, -0.0,  1.0, 1.0);
        assertAtan2(+0.0,  0.1,  0.0, 1.0);
        assertAtan2(+0.0, -0.1,  1.0, 1.0);
        assertAtan2(+0.0, pinf,  0.0, 1.0);
        assertAtan2(+0.0, ninf,  1.0, 1.0);
        assertAtan2(-0.0,  0.0, -0.0, 1.0);
        assertAtan2(-0.0, -0.0, -1.0, 1.0);
        assertAtan2(-0.0,  0.1, -0.0, 1.0);
        assertAtan2(-0.0, -0.1, -1.0, 1.0);
        assertAtan2(-0.0, pinf, -0.0, 1.0);
        assertAtan2(-0.0, ninf, -1.0, 1.0);
        assertAtan2(+0.1,  0.0,  1.0, 2.0);
        assertAtan2(+0.1, -0.0,  1.0, 2.0);
        assertAtan2(+0.1,  0.1,  1.0, 4.0);
        assertAtan2(+0.1, -0.1,  3.0, 4.0);
        assertAtan2(+0.1, pinf,  0.0, 1.0);
        assertAtan2(+0.1, ninf,  1.0, 1.0);
        assertAtan2(-0.1,  0.0, -1.0, 2.0);
        assertAtan2(-0.1, -0.0, -1.0, 2.0);
        assertAtan2(-0.1,  0.1, -1.0, 4.0);
        assertAtan2(-0.1, -0.1, -3.0, 4.0);
        assertAtan2(-0.1, pinf, -0.0, 1.0);
        assertAtan2(-0.1, ninf, -1.0, 1.0);
        assertAtan2(pinf,  0.0,  1.0, 2.0);
        assertAtan2(pinf, -0.0,  1.0, 2.0);
        assertAtan2(pinf,  0.1,  1.0, 2.0);
        assertAtan2(pinf, -0.1,  1.0, 2.0);
        assertAtan2(pinf, pinf,  1.0, 4.0);
        assertAtan2(pinf, ninf,  3.0, 4.0);
        assertAtan2(ninf,  0.0, -1.0, 2.0);
        assertAtan2(ninf, -0.0, -1.0, 2.0);
        assertAtan2(ninf,  0.1, -1.0, 2.0);
        assertAtan2(ninf, -0.1, -1.0, 2.0);
        assertAtan2(ninf, pinf, -1.0, 4.0);
        assertAtan2(ninf, ninf, -3.0, 4.0);
    }

    /**
     * Assert the atan2(y, x) value is a fraction of pi.
     *
     * @param y ordinate
     * @param x abscissa
     * @param numerator numerator of the fraction of pi (including the sign)
     * @param denominator denominator of the fraction of pi
     */
    private static void assertAtan2(double y, double x, double numerator, double denominator) {
        final double v = AccurateMath.atan2(y, x);
        if (numerator == 0) {
            // Exact including the sign.
            Assertions.assertEquals(numerator, v,
                () -> String.format("atan2(%s, %s) should be %s but was %s", y, x, numerator, v));
        } else {
            final double expected = AccurateMath.PI * numerator / denominator;
            Assertions.assertEquals(expected, v, Precision.EPSILON,
                () -> String.format("atan2(%s, %s) should be pi * %s / %s", y, x, numerator, denominator));
        }
    }

    @Test
    public void testPowAccuracy() {
        double maxerrulp = 0.0;

        for (int i = 0; i < NUMBER_OF_TRIALS; i++) {
            double x = (generator.nextDouble() * 2.0 + 0.25);
            double y = (generator.nextDouble() * 1200.0 - 600.0) * generator.nextDouble();
            /*
             * double x = AccurateMath.floor(generator.nextDouble()*1024.0 - 512.0); double
             * y; if (x != 0) y = AccurateMath.floor(512.0 / AccurateMath.abs(x)); else
             * y = generator.nextDouble()*1200.0; y = y - y/2; x = AccurateMath.pow(2.0, x) *
             * generator.nextDouble(); y = y * generator.nextDouble();
             */

            // double x = generator.nextDouble()*2.0;
            double tst = AccurateMath.pow(x, y);
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
            double tst = AccurateMath.exp(x);
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
            double tst = AccurateMath.sin(x);
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
            double tst = AccurateMath.cos(x);
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
            double tst = AccurateMath.tan(x);
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
            double tst = AccurateMath.atan(x);
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
            double tst = AccurateMath.atan2(y, x);
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
            double tst = AccurateMath.expm1(x);
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

        for (int i = 0; i < 10000; i++) {
            double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();

            double tst = AccurateMath.asin(x);
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

        for (int i = 0; i < 10000; i++) {
            double x = ((generator.nextDouble() * 2.0) - 1.0) * generator.nextDouble();

            double tst = AccurateMath.acos(x);
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

        assertTrue("acos(NaN) should be NaN", Double.isNaN(AccurateMath.acos(Double.NaN)));
        assertTrue("acos(-1.1) should be NaN", Double.isNaN(AccurateMath.acos(-1.1)));
        assertTrue("acos(-1.1) should be NaN", Double.isNaN(AccurateMath.acos(1.1)));
        assertEquals("acos(-1.0) should be PI", AccurateMath.acos(-1.0), AccurateMath.PI, Precision.EPSILON);
        assertEquals("acos(1.0) should be 0.0", AccurateMath.acos(1.0), 0.0, Precision.EPSILON);
        assertEquals("acos(0.0) should be PI/2", AccurateMath.acos(0.0), AccurateMath.PI / 2.0, Precision.EPSILON);
    }

    /**
     * Added tests for a 100% coverage of asin().
     */
    @Test
    public void testAsinSpecialCases() {

        assertTrue("asin(NaN) should be NaN", Double.isNaN(AccurateMath.asin(Double.NaN)));
        assertTrue("asin(1.1) should be NaN", Double.isNaN(AccurateMath.asin(1.1)));
        assertTrue("asin(-1.1) should be NaN", Double.isNaN(AccurateMath.asin(-1.1)));
        assertEquals("asin(1.0) should be PI/2", AccurateMath.asin(1.0), AccurateMath.PI / 2.0, Precision.EPSILON);
        assertEquals("asin(-1.0) should be -PI/2", AccurateMath.asin(-1.0), -AccurateMath.PI / 2.0, Precision.EPSILON);
        assertEquals("asin(0.0) should be 0.0", AccurateMath.asin(0.0), 0.0, Precision.EPSILON);
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

        for (int i = 0; i < 10000; i++) {
            double x = ((generator.nextDouble() * 16.0) - 8.0) * generator.nextDouble();

            double tst = AccurateMath.sinh(x);
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

        for (int i = 0; i < 10000; i++) {
            double x = ((generator.nextDouble() * 16.0) - 8.0) * generator.nextDouble();

            double tst = AccurateMath.cosh(x);
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

        for (int i = 0; i < 10000; i++) {
            double x = ((generator.nextDouble() * 16.0) - 8.0) * generator.nextDouble();

            double tst = AccurateMath.tanh(x);
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

        for (int i = 0; i < 10000; i++) {
            double x = ((generator.nextDouble() * 200.0) - 100.0) * generator.nextDouble();

            double tst = AccurateMath.cbrt(x);
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
        boolean negative = false;

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
            double ref = AccurateMath.toDegrees(x);
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
            double ref = AccurateMath.toRadians(x);
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
        assertEquals(16.0, AccurateMath.nextUp(15.999999999999998), 0.0);

        // 0xc02fffffffffffff 0x404123456789abcd -> c02ffffffffffffe
        assertEquals(-15.999999999999996, AccurateMath.nextAfter(-15.999999999999998, 34.27555555555555), 0.0);

        // 0x402fffffffffffff 0x400123456789abcd -> 402ffffffffffffe
        assertEquals(15.999999999999996, AccurateMath.nextDown(15.999999999999998), 0.0);

        // 0xc02fffffffffffff 0x400123456789abcd -> c02ffffffffffffe
        assertEquals(-15.999999999999996, AccurateMath.nextAfter(-15.999999999999998, 2.142222222222222), 0.0);

        // 0x4020000000000000 0x404123456789abcd -> 4020000000000001
        assertEquals(8.000000000000002, AccurateMath.nextAfter(8.0, 34.27555555555555), 0.0);

        // 0xc020000000000000 0x404123456789abcd -> c01fffffffffffff
        assertEquals(-7.999999999999999, AccurateMath.nextAfter(-8.0, 34.27555555555555), 0.0);

        // 0x4020000000000000 0x400123456789abcd -> 401fffffffffffff
        assertEquals(7.999999999999999, AccurateMath.nextAfter(8.0, 2.142222222222222), 0.0);

        // 0xc020000000000000 0x400123456789abcd -> c01fffffffffffff
        assertEquals(-7.999999999999999, AccurateMath.nextAfter(-8.0, 2.142222222222222), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a224 -> 3f2e43753d36a224
        assertEquals(2.308922399667661E-4, AccurateMath.nextAfter(2.3089223996676606E-4, 2.308922399667661E-4), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a223 -> 3f2e43753d36a223
        assertEquals(2.3089223996676606E-4, AccurateMath.nextAfter(2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a222 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, AccurateMath.nextAfter(2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a224 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, AccurateMath.nextAfter(2.3089223996676606E-4, -2.308922399667661E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a223 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, AccurateMath.nextAfter(2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a222 -> 3f2e43753d36a222
        assertEquals(2.3089223996676603E-4, AccurateMath.nextAfter(2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a224 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, AccurateMath.nextAfter(-2.3089223996676606E-4, 2.308922399667661E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a223 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, AccurateMath.nextAfter(-2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a222 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, AccurateMath.nextAfter(-2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a224 -> bf2e43753d36a224
        assertEquals(-2.308922399667661E-4, AccurateMath.nextAfter(-2.3089223996676606E-4, -2.308922399667661E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a223 -> bf2e43753d36a223
        assertEquals(-2.3089223996676606E-4, AccurateMath.nextAfter(-2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a222 -> bf2e43753d36a222
        assertEquals(-2.3089223996676603E-4, AccurateMath.nextAfter(-2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);
    }

    @Test
    public void testDoubleNextAfterSpecialCases() {
        assertEquals(-Double.MAX_VALUE, AccurateMath.nextAfter(Double.NEGATIVE_INFINITY, 0D), 0D);
        assertEquals(Double.MAX_VALUE, AccurateMath.nextAfter(Double.POSITIVE_INFINITY, 0D), 0D);
        assertEquals(Double.NaN, AccurateMath.nextAfter(Double.NaN, 0D), 0D);
        assertEquals(Double.POSITIVE_INFINITY, AccurateMath.nextAfter(Double.MAX_VALUE, Double.POSITIVE_INFINITY), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.nextAfter(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY), 0D);
        assertEquals(Double.MIN_VALUE, AccurateMath.nextAfter(0D, 1D), 0D);
        assertEquals(-Double.MIN_VALUE, AccurateMath.nextAfter(0D, -1D), 0D);
        assertEquals(0D, AccurateMath.nextAfter(Double.MIN_VALUE, -1), 0D);
        assertEquals(0D, AccurateMath.nextAfter(-Double.MIN_VALUE, 1), 0D);
    }

    @Test
    public void testFloatNextAfterSpecialCases() {
        assertEquals(-Float.MAX_VALUE, AccurateMath.nextAfter(Float.NEGATIVE_INFINITY, 0F), 0F);
        assertEquals(Float.MAX_VALUE, AccurateMath.nextAfter(Float.POSITIVE_INFINITY, 0F), 0F);
        assertEquals(Float.NaN, AccurateMath.nextAfter(Float.NaN, 0F), 0F);
        assertEquals(Float.POSITIVE_INFINITY, AccurateMath.nextUp(Float.MAX_VALUE), 0F);
        assertEquals(Float.NEGATIVE_INFINITY, AccurateMath.nextDown(-Float.MAX_VALUE), 0F);
        assertEquals(Float.MIN_VALUE, AccurateMath.nextAfter(0F, 1F), 0F);
        assertEquals(-Float.MIN_VALUE, AccurateMath.nextAfter(0F, -1F), 0F);
        assertEquals(0F, AccurateMath.nextAfter(Float.MIN_VALUE, -1F), 0F);
        assertEquals(0F, AccurateMath.nextAfter(-Float.MIN_VALUE, 1F), 0F);
    }

    @Test
    public void testDoubleScalbSpecialCases() {
        assertEquals(2.5269841324701218E-175,  AccurateMath.scalb(2.2250738585072014E-308, 442), 0D);
        assertEquals(1.307993905256674E297,    AccurateMath.scalb(1.1102230246251565E-16, 1040), 0D);
        assertEquals(7.2520887996488946E-217,  AccurateMath.scalb(Double.MIN_VALUE,        356), 0D);
        assertEquals(8.98846567431158E307,     AccurateMath.scalb(Double.MIN_VALUE,       2097), 0D);
        assertEquals(Double.POSITIVE_INFINITY, AccurateMath.scalb(Double.MIN_VALUE,       2098), 0D);
        assertEquals(1.1125369292536007E-308,  AccurateMath.scalb(2.225073858507201E-308,   -1), 0D);
        assertEquals(1.0E-323,                 AccurateMath.scalb(Double.MAX_VALUE,      -2097), 0D);
        assertEquals(Double.MIN_VALUE,         AccurateMath.scalb(Double.MAX_VALUE,      -2098), 0D);
        assertEquals(0,                        AccurateMath.scalb(Double.MAX_VALUE,      -2099), 0D);
        assertEquals(Double.POSITIVE_INFINITY, AccurateMath.scalb(Double.POSITIVE_INFINITY, -1000000), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.scalb(-1.1102230246251565E-16, 1078), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.scalb(-1.1102230246251565E-16,  1079), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.scalb(-2.2250738585072014E-308, 2047), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.scalb(-2.2250738585072014E-308, 2048), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.scalb(-1.7976931348623157E308,  2147483647), 0D);
        assertEquals(Double.POSITIVE_INFINITY, AccurateMath.scalb(+1.7976931348623157E308,  2147483647), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.scalb(-1.1102230246251565E-16,  2147483647), 0D);
        assertEquals(Double.POSITIVE_INFINITY, AccurateMath.scalb(+1.1102230246251565E-16,  2147483647), 0D);
        assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.scalb(-2.2250738585072014E-308, 2147483647), 0D);
        assertEquals(Double.POSITIVE_INFINITY, AccurateMath.scalb(+2.2250738585072014E-308, 2147483647), 0D);
    }

    @Test
    public void testFloatScalbSpecialCases() {
        assertEquals(0f, AccurateMath.scalb(Float.MIN_VALUE, -30), 0F);
        assertEquals(2 * Float.MIN_VALUE, AccurateMath.scalb(Float.MIN_VALUE, 1), 0F);
        assertEquals(7.555786e22f, AccurateMath.scalb(Float.MAX_VALUE, -52), 0F);
        assertEquals(1.7014118e38f, AccurateMath.scalb(Float.MIN_VALUE, 276), 0F);
        assertEquals(Float.POSITIVE_INFINITY, AccurateMath.scalb(Float.MIN_VALUE, 277), 0F);
        assertEquals(5.8774718e-39f, AccurateMath.scalb(1.1754944e-38f, -1), 0F);
        assertEquals(2 * Float.MIN_VALUE, AccurateMath.scalb(Float.MAX_VALUE, -276), 0F);
        assertEquals(Float.MIN_VALUE, AccurateMath.scalb(Float.MAX_VALUE, -277), 0F);
        assertEquals(0, AccurateMath.scalb(Float.MAX_VALUE, -278), 0F);
        assertEquals(Float.POSITIVE_INFINITY, AccurateMath.scalb(Float.POSITIVE_INFINITY, -1000000), 0F);
        assertEquals(-3.13994498e38f, AccurateMath.scalb(-1.1e-7f, 151), 0F);
        assertEquals(Float.NEGATIVE_INFINITY, AccurateMath.scalb(-1.1e-7f, 152), 0F);
        assertEquals(Float.POSITIVE_INFINITY, AccurateMath.scalb(3.4028235E38f, 2147483647), 0F);
        assertEquals(Float.NEGATIVE_INFINITY, AccurateMath.scalb(-3.4028235E38f, 2147483647), 0F);
    }

    @Test
    public void testSignumDouble() {
        final double delta = 0.0;
        assertEquals(1.0, AccurateMath.signum(2.0), delta);
        assertEquals(0.0, AccurateMath.signum(0.0), delta);
        assertEquals(-1.0, AccurateMath.signum(-2.0), delta);
        Assert.assertTrue(Double.isNaN(AccurateMath.signum(Double.NaN)));
    }

    @Test
    public void testSignumFloat() {
        final float delta = 0.0F;
        assertEquals(1.0F, AccurateMath.signum(2.0F), delta);
        assertEquals(0.0F, AccurateMath.signum(0.0F), delta);
        assertEquals(-1.0F, AccurateMath.signum(-2.0F), delta);
        Assert.assertTrue(Double.isNaN(AccurateMath.signum(Float.NaN)));
    }

    @Test
    public void testLogWithBase() {
        assertEquals(2.0, AccurateMath.log(2, 4), 0);
        assertEquals(3.0, AccurateMath.log(2, 8), 0);
        assertTrue(Double.isNaN(AccurateMath.log(-1, 1)));
        assertTrue(Double.isNaN(AccurateMath.log(1, -1)));
        assertTrue(Double.isNaN(AccurateMath.log(0, 0)));
        assertEquals(0, AccurateMath.log(0, 10), 0);
        assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.log(10, 0), 0);
    }

    @Test
    public void testIndicatorDouble() {
        double delta = 0.0;
        assertEquals(1.0, AccurateMath.copySign(1d, 2.0), delta);
        assertEquals(1.0, AccurateMath.copySign(1d, 0.0), delta);
        assertEquals(-1.0, AccurateMath.copySign(1d, -0.0), delta);
        assertEquals(1.0, AccurateMath.copySign(1d, Double.POSITIVE_INFINITY), delta);
        assertEquals(-1.0, AccurateMath.copySign(1d, Double.NEGATIVE_INFINITY), delta);
        assertEquals(1.0, AccurateMath.copySign(1d, Double.NaN), delta);
        assertEquals(-1.0, AccurateMath.copySign(1d, -2.0), delta);
    }

    @Test
    public void testIndicatorFloat() {
        float delta = 0.0F;
        assertEquals(1.0F, AccurateMath.copySign(1d, 2.0F), delta);
        assertEquals(1.0F, AccurateMath.copySign(1d, 0.0F), delta);
        assertEquals(-1.0F, AccurateMath.copySign(1d, -0.0F), delta);
        assertEquals(1.0F, AccurateMath.copySign(1d, Float.POSITIVE_INFINITY), delta);
        assertEquals(-1.0F, AccurateMath.copySign(1d, Float.NEGATIVE_INFINITY), delta);
        assertEquals(1.0F, AccurateMath.copySign(1d, Float.NaN), delta);
        assertEquals(-1.0F, AccurateMath.copySign(1d, -2.0F), delta);
    }

    @Test
    public void testIntPow() {
        final int maxExp = 300;
        DfpField localField = new DfpField(40);
        final double base = 1.23456789;
        Dfp baseDfp = localField.newDfp(base);
        Dfp dfpPower = localField.getOne();
        for (int i = 0; i < maxExp; i++) {
            assertEquals("exp=" + i, dfpPower.toDouble(), AccurateMath.pow(base, i),
                         0.6 * AccurateMath.ulp(dfpPower.toDouble()));
            dfpPower = dfpPower.multiply(baseDfp);
        }
    }

    @Test
    public void testIntPowHuge() {
        assertTrue(Double.isInfinite(AccurateMath.pow(AccurateMath.scalb(1.0, 500), 4)));
    }

    @Test(timeout = 5000L) // This test must finish in finite time.
    public void testIntPowLongMinValue() {
        assertEquals(1.0, AccurateMath.pow(1.0, Long.MIN_VALUE), -1.0);
    }

    @Test(timeout = 5000L)
    public void testIntPowSpecialCases() {
        final double EXACT = -1.0;
        final double[] DOUBLES = new double[] {
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

        final long[] INTS = new long[]{Long.MAX_VALUE, Long.MAX_VALUE - 1, Long.MIN_VALUE, Long.MIN_VALUE + 1, Long.MIN_VALUE + 2, Integer.MAX_VALUE, Integer.MAX_VALUE - 1, Integer.MIN_VALUE, Integer.MIN_VALUE + 1, Integer.MIN_VALUE + 2, 0, 1, 2, 3, 5, 8, 10, 20, 100, 300, 500, -1, -2, -3, -5, -8, -10, -20, -100, -300, -500};
        // Special cases from Math.pow javadoc:
        // If the second argument is positive or negative zero, then the result is 1.0.
        for (double d : DOUBLES) {
            assertEquals(1.0, AccurateMath.pow(d, 0L), EXACT);
        }
        // If the second argument is 1.0, then the result is the same as the first argument.
        for (double d : DOUBLES) {
            assertEquals(d, AccurateMath.pow(d, 1L), EXACT);
        }
        // If the second argument is NaN, then the result is NaN. <- Impossible with int.
        // If the first argument is NaN and the second argument is nonzero, then the result is NaN.
        for (long i : INTS) {
            if (i != 0L) {
                assertEquals(Double.NaN, AccurateMath.pow(Double.NaN, i), EXACT);
            }
        }
        // If the absolute value of the first argument is greater than 1 and the second argument is positive infinity, or
        // the absolute value of the first argument is less than 1 and the second argument is negative infinity, then the result is positive infinity.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(d, Long.MAX_VALUE - 1L), EXACT);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(d, Long.MIN_VALUE), EXACT);
            }
        }
        // Note: Long.MAX_VALUE isn't actually an infinity, so its parity affects the sign of resulting infinity.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertTrue(Double.isInfinite(AccurateMath.pow(d, Long.MAX_VALUE)));
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertTrue(Double.isInfinite(AccurateMath.pow(d, Long.MIN_VALUE + 1L)));
            }
        }
        // If the absolute value of the first argument is greater than 1 and the second argument is negative infinity, or
        // the absolute value of the first argument is less than 1 and the second argument is positive infinity, then the result is positive zero.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertEquals(0.0, AccurateMath.pow(d, Long.MIN_VALUE), EXACT);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertEquals(0.0, AccurateMath.pow(d, Long.MAX_VALUE - 1L), EXACT);
            }
        }
        // Note: Long.MAX_VALUE isn't actually an infinity, so its parity affects the sign of resulting zero.
        for (double d : DOUBLES) {
            if (Math.abs(d) > 1.0) {
                assertEquals(0.0, AccurateMath.pow(d, Long.MIN_VALUE + 1L), 0.0);
            }
        }
        for (double d : DOUBLES) {
            if (Math.abs(d) < 1.0) {
                assertEquals(0.0, AccurateMath.pow(d, Long.MAX_VALUE), 0.0);
            }
        }
        // If the absolute value of the first argument equals 1 and the second argument is infinite, then the result is NaN. <- Impossible with int.
        // If the first argument is positive zero and the second argument is greater than zero, or
        // the first argument is positive infinity and the second argument is less than zero, then the result is positive zero.
        for (long i : INTS) {
            if (i > 0L) {
                assertEquals(0.0, AccurateMath.pow(0.0, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L) {
                assertEquals(0.0, AccurateMath.pow(Double.POSITIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is positive zero and the second argument is less than zero, or
        // the first argument is positive infinity and the second argument is greater than zero, then the result is positive infinity.
        for (long i : INTS) {
            if (i < 0L) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(0.0, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i > 0L) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(Double.POSITIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is greater than zero but not a finite odd integer, or
        // the first argument is negative infinity and the second argument is less than zero but not a finite odd integer, then the result is positive zero.
        for (long i : INTS) {
            if (i > 0L && (i & 1L) == 0L) {
                assertEquals(0.0, AccurateMath.pow(-0.0, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L && (i & 1L) == 0L) {
                assertEquals(0.0, AccurateMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is a positive finite odd integer, or
        // the first argument is negative infinity and the second argument is a negative finite odd integer, then the result is negative zero.
        for (long i : INTS) {
            if (i > 0L && (i & 1L) == 1L) {
                assertEquals(-0.0, AccurateMath.pow(-0.0, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L && (i & 1L) == 1L) {
                assertEquals(-0.0, AccurateMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is less than zero but not a finite odd integer, or
        // the first argument is negative infinity and the second argument is greater than zero but not a finite odd integer, then the result is positive infinity.
        for (long i : INTS) {
            if (i > 0L && (i & 1L) == 0L) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L && (i & 1L) == 0L) {
                assertEquals(Double.POSITIVE_INFINITY, AccurateMath.pow(-0.0, i), EXACT);
            }
        }
        // If the first argument is negative zero and the second argument is a negative finite odd integer, or
        // the first argument is negative infinity and the second argument is a positive finite odd integer, then the result is negative infinity.
        for (long i : INTS) {
            if (i > 0L && (i & 1L) == 1L) {
                assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.pow(Double.NEGATIVE_INFINITY, i), EXACT);
            }
        }
        for (long i : INTS) {
            if (i < 0L && (i & 1L) == 1L) {
                assertEquals(Double.NEGATIVE_INFINITY, AccurateMath.pow(-0.0, i), EXACT);
            }
        }
        for (double d : DOUBLES) {
            // If the first argument is finite and less than zero
            if (d < 0.0 && Math.abs(d) <= Double.MAX_VALUE) {
                for (long i : INTS) {
                    // if the second argument is a finite even integer, the result is equal to the result of raising the absolute value of the first argument to the power of the second argument
                    if ((i & 1L) == 0L) {
                        assertEquals(AccurateMath.pow(-d, i), AccurateMath.pow(d, i), EXACT);
                    } else {
                        // if the second argument is a finite odd integer, the result is equal to the negative of the result of raising the absolute value of the first argument to the power of the second argument
                        assertEquals(-AccurateMath.pow(-d, i), AccurateMath.pow(d, i), EXACT);
                    }
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
                    AccurateMath.incrementExact(a);
                    Assert.fail("an exception should have been thrown");
                } catch (ArithmeticException mae) {
                    // expected
                }
            } else {
                assertEquals(bdSum, BigInteger.valueOf(AccurateMath.incrementExact(a)));
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
                    AccurateMath.decrementExact(a);
                    fail("an exception should have been thrown");
                } catch (ArithmeticException mae) {
                    // expected
                }
            } else {
                assertEquals(bdSub, BigInteger.valueOf(AccurateMath.decrementExact(a)));
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
                        AccurateMath.addExact(a, b);
                        fail("an exception should have been thrown");
                    } catch (ArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdSum, BigInteger.valueOf(AccurateMath.addExact(a, b)));
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
                        AccurateMath.addExact(a, b);
                        fail("an exception should have been thrown");
                    } catch (ArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdSum, BigInteger.valueOf(AccurateMath.addExact(a, b)));
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
                        AccurateMath.subtractExact(a, b);
                        fail("an exception should have been thrown");
                    } catch (ArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdSub, BigInteger.valueOf(AccurateMath.subtractExact(a, b)));
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
                        AccurateMath.subtractExact(a, b);
                        fail("an exception should have been thrown");
                    } catch (ArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdSub, BigInteger.valueOf(AccurateMath.subtractExact(a, b)));
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
                        AccurateMath.multiplyExact(a, b);
                        fail("an exception should have been thrown " + a + b);
                    } catch (ArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdMul, BigInteger.valueOf(AccurateMath.multiplyExact(a, b)));
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
                        AccurateMath.multiplyExact(a, b);
                        fail("an exception should have been thrown " + a + b);
                    } catch (ArithmeticException mae) {
                        // expected
                    }
                } else {
                    assertEquals(bdMul, BigInteger.valueOf(AccurateMath.multiplyExact(a, b)));
                }
            }
        }
    }

    @Test(expected = ArithmeticException.class)
    public void testToIntExactTooLow() {
        AccurateMath.toIntExact(-1L + Integer.MIN_VALUE);
    }

    @Test(expected = ArithmeticException.class)
    public void testToIntExactTooHigh() {
        AccurateMath.toIntExact(+1L + Integer.MAX_VALUE);
    }

    @Test
    public void testToIntExact() {
        for (int n = -1000; n < 1000; ++n) {
            assertEquals(n, AccurateMath.toIntExact(0L + n));
        }
        assertEquals(Integer.MIN_VALUE, AccurateMath.toIntExact(0L + Integer.MIN_VALUE));
        assertEquals(Integer.MAX_VALUE, AccurateMath.toIntExact(0L + Integer.MAX_VALUE));
    }

    @Test
    public void testFloorDivInt() {
        assertEquals(+1, AccurateMath.floorDiv(+4, +3));
        assertEquals(-2, AccurateMath.floorDiv(-4, +3));
        assertEquals(-2, AccurateMath.floorDiv(+4, -3));
        assertEquals(+1, AccurateMath.floorDiv(-4, -3));
        try {
            AccurateMath.floorDiv(1, 0);
            fail("an exception should have been thrown");
        } catch (ArithmeticException mae) {
            // expected
        }
        for (int a = -100; a <= 100; ++a) {
            for (int b = -100; b <= 100; ++b) {
                if (b != 0) {
                    assertEquals(poorManFloorDiv(a, b), AccurateMath.floorDiv(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorModInt() {
        assertEquals(+1, AccurateMath.floorMod(+4, +3));
        assertEquals(+2, AccurateMath.floorMod(-4, +3));
        assertEquals(-2, AccurateMath.floorMod(+4, -3));
        assertEquals(-1, AccurateMath.floorMod(-4, -3));
        try {
            AccurateMath.floorMod(1, 0);
            fail("an exception should have been thrown");
        } catch (ArithmeticException mae) {
            // expected
        }
        for (int a = -100; a <= 100; ++a) {
            for (int b = -100; b <= 100; ++b) {
                if (b != 0) {
                    assertEquals(poorManFloorMod(a, b), AccurateMath.floorMod(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorDivModInt() {
        UniformRandomProvider rng = RandomSource.WELL_1024_A.create(0x7ccab45edeaab90aL);
        for (int i = 0; i < 10000; ++i) {
            int a = rng.nextInt();
            int b = rng.nextInt();
            if (b == 0) {
                try {
                    AccurateMath.floorDiv(a, b);
                    fail("an exception should have been thrown");
                } catch (ArithmeticException mae) {
                    // expected
                }
            } else {
                int d = AccurateMath.floorDiv(a, b);
                int m = AccurateMath.floorMod(a, b);
                assertEquals(AccurateMath.toIntExact(poorManFloorDiv(a, b)), d);
                assertEquals(AccurateMath.toIntExact(poorManFloorMod(a, b)), m);
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
        assertEquals(+1L, AccurateMath.floorDiv(+4L, +3L));
        assertEquals(-2L, AccurateMath.floorDiv(-4L, +3L));
        assertEquals(-2L, AccurateMath.floorDiv(+4L, -3L));
        assertEquals(+1L, AccurateMath.floorDiv(-4L, -3L));
        try {
            AccurateMath.floorDiv(1L, 0L);
            fail("an exception should have been thrown");
        } catch (ArithmeticException mae) {
            // expected
        }
        for (long a = -100L; a <= 100L; ++a) {
            for (long b = -100L; b <= 100L; ++b) {
                if (b != 0) {
                    assertEquals(poorManFloorDiv(a, b), AccurateMath.floorDiv(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorModLong() {
        assertEquals(+1L, AccurateMath.floorMod(+4L, +3L));
        assertEquals(+2L, AccurateMath.floorMod(-4L, +3L));
        assertEquals(-2L, AccurateMath.floorMod(+4L, -3L));
        assertEquals(-1L, AccurateMath.floorMod(-4L, -3L));
        try {
            AccurateMath.floorMod(1L, 0L);
            fail("an exception should have been thrown");
        } catch (ArithmeticException mae) {
            // expected
        }
        for (long a = -100L; a <= 100L; ++a) {
            for (long b = -100L; b <= 100L; ++b) {
                if (b != 0) {
                    assertEquals(poorManFloorMod(a, b), AccurateMath.floorMod(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorDivModLong() {
        UniformRandomProvider rng = RandomSource.WELL_1024_A.create(0xb87b9bc14c96ccd5L);
        for (int i = 0; i < 10000; ++i) {
            long a = rng.nextLong();
            long b = rng.nextLong();
            if (b == 0) {
                try {
                    AccurateMath.floorDiv(a, b);
                    fail("an exception should have been thrown");
                } catch (ArithmeticException mae) {
                    // expected
                }
            } else {
                long d = AccurateMath.floorDiv(a, b);
                long m = AccurateMath.floorMod(a, b);
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
                (r.longValue() == 0L || ((r.longValue() ^ b) & 0x8000000000000000L) == 0)) {
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
        assertEquals(0, AccurateMath.round(x));

        x = 4503599627370497.0; // x = Math.pow(2, 52) + 1;
        assertEquals("4503599627370497", new BigDecimal(x).toString());
        assertEquals(x, Math.rint(x), 0.0);
        assertEquals(x, AccurateMath.round(x), 0.0);
        //assertTrue(x == Math.round(x)); // fails with Java 7, fixed in Java 8
    }
}
