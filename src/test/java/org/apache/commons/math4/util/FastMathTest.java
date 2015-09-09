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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigInteger;

import org.apache.commons.math4.TestUtils;
import org.apache.commons.math4.dfp.Dfp;
import org.apache.commons.math4.dfp.DfpField;
import org.apache.commons.math4.dfp.DfpMath;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.random.MersenneTwister;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well1024a;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Precision;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class FastMathTest {

    private static final double MAX_ERROR_ULP = 0.51;
    private static final int NUMBER_OF_TRIALS = 1000;


    private DfpField field;
    private RandomGenerator generator;

    @Before
    public void setUp() {
        field = new DfpField(40);
        generator = new MersenneTwister(6176597458463500194l);
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
            Assert.assertEquals("min(" + pair[0] + ", " + pair[1] + ")",
                                Math.min(pair[0], pair[1]),
                                FastMath.min(pair[0], pair[1]),
                                Precision.EPSILON);
            Assert.assertEquals("min(" + pair[1] + ", " + pair[0] + ")",
                                Math.min(pair[1], pair[0]),
                                FastMath.min(pair[1], pair[0]),
                                Precision.EPSILON);
            Assert.assertEquals("max(" + pair[0] + ", " + pair[1] + ")",
                                Math.max(pair[0], pair[1]),
                                FastMath.max(pair[0], pair[1]),
                                Precision.EPSILON);
            Assert.assertEquals("max(" + pair[1] + ", " + pair[0] + ")",
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
            Assert.assertEquals("min(" + pair[0] + ", " + pair[1] + ")",
                                Math.min(pair[0], pair[1]),
                                FastMath.min(pair[0], pair[1]),
                                Precision.EPSILON);
            Assert.assertEquals("min(" + pair[1] + ", " + pair[0] + ")",
                                Math.min(pair[1], pair[0]),
                                FastMath.min(pair[1], pair[0]),
                                Precision.EPSILON);
            Assert.assertEquals("max(" + pair[0] + ", " + pair[1] + ")",
                                Math.max(pair[0], pair[1]),
                                FastMath.max(pair[0], pair[1]),
                                Precision.EPSILON);
            Assert.assertEquals("max(" + pair[1] + ", " + pair[0] + ")",
                                Math.max(pair[1], pair[0]),
                                FastMath.max(pair[1], pair[0]),
                                Precision.EPSILON);
        }
    }

    @Test
    public void testConstants() {
        Assert.assertEquals(Math.PI, FastMath.PI, 1.0e-20);
        Assert.assertEquals(Math.E, FastMath.E, 1.0e-20);
    }

    @Test
    public void testAtan2() {
        double y1 = 1.2713504628280707e10;
        double x1 = -5.674940885228782e-10;
        Assert.assertEquals(Math.atan2(y1, x1), FastMath.atan2(y1, x1), 2 * Precision.EPSILON);
        double y2 = 0.0;
        double x2 = Double.POSITIVE_INFINITY;
        Assert.assertEquals(Math.atan2(y2, x2), FastMath.atan2(y2, x2), Precision.SAFE_MIN);
    }

    @Test
    public void testHyperbolic() {
        double maxErr = 0;
        for (double x = -30; x < 30; x += 0.001) {
            double tst = FastMath.sinh(x);
            double ref = Math.sinh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        Assert.assertEquals(0, maxErr, 2);

        maxErr = 0;
        for (double x = -30; x < 30; x += 0.001) {
            double tst = FastMath.cosh(x);
            double ref = Math.cosh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        Assert.assertEquals(0, maxErr, 2);

        maxErr = 0;
        for (double x = -0.5; x < 0.5; x += 0.001) {
            double tst = FastMath.tanh(x);
            double ref = Math.tanh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));
        }
        Assert.assertEquals(0, maxErr, 4);

    }

    @Test
    public void testMath904() {
        final double x = -1;
        final double y = (5 + 1e-15) * 1e15;
        Assert.assertEquals(Math.pow(x, y),
                            FastMath.pow(x, y), 0);
        Assert.assertEquals(Math.pow(x, -y),
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
        Assert.assertEquals(0, maxErr, 3);

        for (double x = start; x < end; x += 1e-3) {
            final double tst = FastMath.sinh(x);
            final double ref = Math.sinh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));            
        }
        Assert.assertEquals(0, maxErr, 3);
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
        Assert.assertEquals(0, maxErr, 3);

        for (double x = start; x > end; x -= 1e-3) {
            final double tst = FastMath.sinh(x);
            final double ref = Math.sinh(x);
            maxErr = FastMath.max(maxErr, FastMath.abs(ref - tst) / FastMath.ulp(ref));            
        }
        Assert.assertEquals(0, maxErr, 3);
    }

    @Test
    public void testHyperbolicInverses() {
        double maxErr = 0;
        for (double x = -30; x < 30; x += 0.01) {
            maxErr = FastMath.max(maxErr, FastMath.abs(x - FastMath.sinh(FastMath.asinh(x))) / (2 * FastMath.ulp(x)));
        }
        Assert.assertEquals(0, maxErr, 3);

        maxErr = 0;
        for (double x = 1; x < 30; x += 0.01) {
            maxErr = FastMath.max(maxErr, FastMath.abs(x - FastMath.cosh(FastMath.acosh(x))) / (2 * FastMath.ulp(x)));
        }
        Assert.assertEquals(0, maxErr, 2);

        maxErr = 0;
        for (double x = -1 + Precision.EPSILON; x < 1 - Precision.EPSILON; x += 0.0001) {
            maxErr = FastMath.max(maxErr, FastMath.abs(x - FastMath.tanh(FastMath.atanh(x))) / (2 * FastMath.ulp(x)));
        }
        Assert.assertEquals(0, maxErr, 2);

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
                                      Double.longBitsToDouble((Double
                                          .doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.log(field.newDfp(x))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        Assert.assertTrue("log() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("log10() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("log1p() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    @Test
    public void testLog1pSpecialCases() {

        Assert.assertTrue("Logp of -1.0 should be -Inf", Double.isInfinite(FastMath.log1p(-1.0)));

    }

    @Test
    public void testLogSpecialCases() {

        Assert.assertEquals("Log of zero should be -Inf", Double.NEGATIVE_INFINITY, FastMath.log(0.0), 1.0);

        Assert.assertEquals("Log of -zero should be -Inf", Double.NEGATIVE_INFINITY, FastMath.log(-0.0), 1.0);

        Assert.assertTrue("Log of NaN should be NaN", Double.isNaN(FastMath.log(Double.NaN)));

        Assert.assertTrue("Log of negative number should be NaN", Double.isNaN(FastMath.log(-1.0)));

        Assert.assertEquals("Log of Double.MIN_VALUE should be -744.4400719213812", -744.4400719213812, FastMath.log(Double.MIN_VALUE), Precision.EPSILON);

        Assert.assertEquals("Log of infinity should be infinity", Double.POSITIVE_INFINITY, FastMath.log(Double.POSITIVE_INFINITY), 1.0);
    }

    @Test
    public void testExpSpecialCases() {

        // Smallest value that will round up to Double.MIN_VALUE
        Assert.assertEquals(Double.MIN_VALUE, FastMath.exp(-745.1332191019411), Precision.EPSILON);

        Assert.assertEquals("exp(-745.1332191019412) should be 0.0", 0.0, FastMath.exp(-745.1332191019412), Precision.EPSILON);

        Assert.assertTrue("exp of NaN should be NaN", Double.isNaN(FastMath.exp(Double.NaN)));

        Assert.assertEquals("exp of infinity should be infinity", Double.POSITIVE_INFINITY, FastMath.exp(Double.POSITIVE_INFINITY), 1.0);

        Assert.assertEquals("exp of -infinity should be 0.0", 0.0, FastMath.exp(Double.NEGATIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("exp(1) should be Math.E", Math.E, FastMath.exp(1.0), Precision.EPSILON);
    }

    @Test
    public void testPowSpecialCases() {

        Assert.assertEquals("pow(-1, 0) should be 1.0", 1.0, FastMath.pow(-1.0, 0.0), Precision.EPSILON);

        Assert.assertEquals("pow(-1, -0) should be 1.0", 1.0, FastMath.pow(-1.0, -0.0), Precision.EPSILON);

        Assert.assertEquals("pow(PI, 1.0) should be PI", FastMath.PI, FastMath.pow(FastMath.PI, 1.0), Precision.EPSILON);

        Assert.assertEquals("pow(-PI, 1.0) should be -PI", -FastMath.PI, FastMath.pow(-FastMath.PI, 1.0), Precision.EPSILON);

        Assert.assertTrue("pow(PI, NaN) should be NaN", Double.isNaN(FastMath.pow(Math.PI, Double.NaN)));

        Assert.assertTrue("pow(NaN, PI) should be NaN", Double.isNaN(FastMath.pow(Double.NaN, Math.PI)));

        Assert.assertEquals("pow(2.0, Infinity) should be Infinity", Double.POSITIVE_INFINITY, FastMath.pow(2.0, Double.POSITIVE_INFINITY), 1.0);

        Assert.assertEquals("pow(0.5, -Infinity) should be Infinity", Double.POSITIVE_INFINITY, FastMath.pow(0.5, Double.NEGATIVE_INFINITY), 1.0);

        Assert.assertEquals("pow(0.5, Infinity) should be 0.0", 0.0, FastMath.pow(0.5, Double.POSITIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("pow(2.0, -Infinity) should be 0.0", 0.0, FastMath.pow(2.0, Double.NEGATIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("pow(0.0, 0.5) should be 0.0", 0.0, FastMath.pow(0.0, 0.5), Precision.EPSILON);

        Assert.assertEquals("pow(Infinity, -0.5) should be 0.0", 0.0, FastMath.pow(Double.POSITIVE_INFINITY, -0.5), Precision.EPSILON);

        Assert.assertEquals("pow(0.0, -0.5) should be Inf", Double.POSITIVE_INFINITY, FastMath.pow(0.0, -0.5), 1.0);

        Assert.assertEquals("pow(Inf, 0.5) should be Inf", Double.POSITIVE_INFINITY, FastMath.pow(Double.POSITIVE_INFINITY, 0.5), 1.0);

        Assert.assertEquals("pow(-0.0, -3.0) should be -Inf", Double.NEGATIVE_INFINITY, FastMath.pow(-0.0, -3.0), 1.0);

        Assert.assertEquals("pow(-0.0, Infinity) should be 0.0", 0.0, FastMath.pow(-0.0, Double.POSITIVE_INFINITY), Precision.EPSILON);

        Assert.assertTrue("pow(-0.0, NaN) should be NaN", Double.isNaN(FastMath.pow(-0.0, Double.NaN)));

        Assert.assertEquals("pow(-0.0, -tiny) should be Infinity", Double.POSITIVE_INFINITY, FastMath.pow(-0.0, -Double.MIN_VALUE), 1.0);

        Assert.assertEquals("pow(-0.0, -huge) should be Infinity", Double.POSITIVE_INFINITY, FastMath.pow(-0.0, -Double.MAX_VALUE), 1.0);

        Assert.assertEquals("pow(-Inf, -3.0) should be -Inf", Double.NEGATIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, 3.0), 1.0);

        Assert.assertEquals("pow(-0.0, -3.5) should be Inf", Double.POSITIVE_INFINITY, FastMath.pow(-0.0, -3.5), 1.0);

        Assert.assertEquals("pow(Inf, 3.5) should be Inf", Double.POSITIVE_INFINITY, FastMath.pow(Double.POSITIVE_INFINITY, 3.5), 1.0);

        Assert.assertEquals("pow(-2.0, 3.0) should be -8.0", -8.0, FastMath.pow(-2.0, 3.0), Precision.EPSILON);

        Assert.assertTrue("pow(-2.0, 3.5) should be NaN", Double.isNaN(FastMath.pow(-2.0, 3.5)));

        Assert.assertTrue("pow(NaN, -Infinity) should be NaN", Double.isNaN(FastMath.pow(Double.NaN, Double.NEGATIVE_INFINITY)));

        Assert.assertEquals("pow(NaN, 0.0) should be 1.0", 1.0, FastMath.pow(Double.NaN, 0.0), Precision.EPSILON);

        Assert.assertEquals("pow(-Infinity, -Infinity) should be 0.0", 0.0, FastMath.pow(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("pow(-huge, -huge) should be 0.0", 0.0, FastMath.pow(-Double.MAX_VALUE, -Double.MAX_VALUE), Precision.EPSILON);

        Assert.assertTrue("pow(-huge,  huge) should be +Inf", Double.isInfinite(FastMath.pow(-Double.MAX_VALUE, Double.MAX_VALUE)));

        Assert.assertTrue("pow(NaN, -Infinity) should be NaN", Double.isNaN(FastMath.pow(Double.NaN, Double.NEGATIVE_INFINITY)));

        Assert.assertEquals("pow(NaN, 0.0) should be 1.0", 1.0, FastMath.pow(Double.NaN, 0.0), Precision.EPSILON);

        Assert.assertEquals("pow(-Infinity, -Infinity) should be 0.0", 0.0, FastMath.pow(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("pow(-huge, -huge) should be 0.0", 0.0, FastMath.pow(-Double.MAX_VALUE, -Double.MAX_VALUE), Precision.EPSILON);

        Assert.assertEquals("pow(-huge,  huge) should be +Inf", Double.POSITIVE_INFINITY, FastMath.pow(-Double.MAX_VALUE, Double.MAX_VALUE), 1.0);

        // Added tests for a 100% coverage

        Assert.assertTrue("pow(+Inf, NaN) should be NaN", Double.isNaN(FastMath.pow(Double.POSITIVE_INFINITY, Double.NaN)));

        Assert.assertTrue("pow(1.0, +Inf) should be NaN", Double.isNaN(FastMath.pow(1.0, Double.POSITIVE_INFINITY)));

        Assert.assertTrue("pow(-Inf, NaN) should be NaN", Double.isNaN(FastMath.pow(Double.NEGATIVE_INFINITY, Double.NaN)));

        Assert.assertEquals("pow(-Inf, -1.0) should be 0.0", 0.0, FastMath.pow(Double.NEGATIVE_INFINITY, -1.0), Precision.EPSILON);

        Assert.assertEquals("pow(-Inf, -2.0) should be 0.0", 0.0, FastMath.pow(Double.NEGATIVE_INFINITY, -2.0), Precision.EPSILON);

        Assert.assertEquals("pow(-Inf, 1.0) should be -Inf", Double.NEGATIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, 1.0), 1.0);

        Assert.assertEquals("pow(-Inf, 2.0) should be +Inf", Double.POSITIVE_INFINITY, FastMath.pow(Double.NEGATIVE_INFINITY, 2.0), 1.0);

        Assert.assertTrue("pow(1.0, -Inf) should be NaN", Double.isNaN(FastMath.pow(1.0, Double.NEGATIVE_INFINITY)));

    }

    @Test
    public void testPowLargeIntegralDouble() {
        double y = FastMath.scalb(1.0, 65);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(FastMath.nextUp(1.0), y),    1.0);
        Assert.assertEquals(1.0,                      FastMath.pow(1.0, y),                     1.0);
        Assert.assertEquals(0.0,                      FastMath.pow(FastMath.nextDown(1.0), y),  1.0);
        Assert.assertEquals(0.0,                      FastMath.pow(FastMath.nextUp(-1.0), y),   1.0);
        Assert.assertEquals(1.0,                      FastMath.pow(-1.0, y),                    1.0);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.pow(FastMath.nextDown(-1.0), y), 1.0);
    }

    @Test
    public void testAtan2SpecialCases() {

        Assert.assertTrue("atan2(NaN, 0.0) should be NaN", Double.isNaN(FastMath.atan2(Double.NaN, 0.0)));

        Assert.assertTrue("atan2(0.0, NaN) should be NaN", Double.isNaN(FastMath.atan2(0.0, Double.NaN)));

        Assert.assertEquals("atan2(0.0, 0.0) should be 0.0", 0.0, FastMath.atan2(0.0, 0.0), Precision.EPSILON);

        Assert.assertEquals("atan2(0.0, 0.001) should be 0.0", 0.0, FastMath.atan2(0.0, 0.001), Precision.EPSILON);

        Assert.assertEquals("atan2(0.1, +Inf) should be 0.0", 0.0, FastMath.atan2(0.1, Double.POSITIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("atan2(-0.0, 0.0) should be -0.0", -0.0, FastMath.atan2(-0.0, 0.0), Precision.EPSILON);

        Assert.assertEquals("atan2(-0.0, 0.001) should be -0.0", -0.0, FastMath.atan2(-0.0, 0.001), Precision.EPSILON);

        Assert.assertEquals("atan2(-0.0, +Inf) should be -0.0", -0.0, FastMath.atan2(-0.1, Double.POSITIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("atan2(0.0, -0.0) should be PI", FastMath.PI, FastMath.atan2(0.0, -0.0), Precision.EPSILON);

        Assert.assertEquals("atan2(0.1, -Inf) should be PI", FastMath.PI, FastMath.atan2(0.1, Double.NEGATIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("atan2(-0.0, -0.0) should be -PI", -FastMath.PI, FastMath.atan2(-0.0, -0.0), Precision.EPSILON);

        Assert.assertEquals("atan2(0.1, -Inf) should be -PI", -FastMath.PI, FastMath.atan2(-0.1, Double.NEGATIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("atan2(0.1, 0.0) should be PI/2", FastMath.PI / 2.0, FastMath.atan2(0.1, 0.0), Precision.EPSILON);

        Assert.assertEquals("atan2(0.1, -0.0) should be PI/2", FastMath.PI / 2.0, FastMath.atan2(0.1, -0.0), Precision.EPSILON);

        Assert.assertEquals("atan2(Inf, 0.1) should be PI/2", FastMath.PI / 2.0, FastMath.atan2(Double.POSITIVE_INFINITY, 0.1), Precision.EPSILON);

        Assert.assertEquals("atan2(Inf, -0.1) should be PI/2", FastMath.PI / 2.0, FastMath.atan2(Double.POSITIVE_INFINITY, -0.1), Precision.EPSILON);

        Assert.assertEquals("atan2(-0.1, 0.0) should be -PI/2", -FastMath.PI / 2.0, FastMath.atan2(-0.1, 0.0), Precision.EPSILON);

        Assert.assertEquals("atan2(-0.1, -0.0) should be -PI/2", -FastMath.PI / 2.0, FastMath.atan2(-0.1, -0.0), Precision.EPSILON);

        Assert.assertEquals("atan2(-Inf, 0.1) should be -PI/2", -FastMath.PI / 2.0, FastMath.atan2(Double.NEGATIVE_INFINITY, 0.1), Precision.EPSILON);

        Assert.assertEquals("atan2(-Inf, -0.1) should be -PI/2", -FastMath.PI / 2.0, FastMath.atan2(Double.NEGATIVE_INFINITY, -0.1), Precision.EPSILON);

        Assert.assertEquals("atan2(Inf, Inf) should be PI/4", FastMath.PI / 4.0, FastMath.atan2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY),
                Precision.EPSILON);

        Assert.assertEquals("atan2(Inf, -Inf) should be PI * 3/4", FastMath.PI * 3.0 / 4.0,
                FastMath.atan2(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY), Precision.EPSILON);

        Assert.assertEquals("atan2(-Inf, Inf) should be -PI/4", -FastMath.PI / 4.0, FastMath.atan2(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY),
                Precision.EPSILON);

        Assert.assertEquals("atan2(-Inf, -Inf) should be -PI * 3/4", - FastMath.PI * 3.0 / 4.0,
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
                                      Double.longBitsToDouble((Double
                                          .doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.pow(field.newDfp(x), field.newDfp(y))).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + y + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        Assert.assertTrue("pow() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }
    
    @Test
    public void testExpHighBorder() {
        final double EXACT = -1.0;
        Assert.assertEquals(Double.NaN, FastMath.exp(Double.NaN), EXACT);
        Assert.assertTrue(FastMath.exp(709.0) < Double.MAX_VALUE);
        Assert.assertTrue(FastMath.exp(709.7) < Double.MAX_VALUE);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.8), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.81), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.812), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.8124), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.81245), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.8125), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.81255), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.8126), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.813), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.82), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(709.9), EXACT);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.exp(710.0), EXACT);
        // Values near log( Double.MAX_VALUE ) = 709.7827128933839967276924307167.
        for (long l = Double.doubleToRawLongBits(709.7827128933); l <= Double.doubleToRawLongBits(709.78271289341); l++) {
            double d = Double.longBitsToDouble(l);
            Assert.assertFalse(Double.isNaN(FastMath.exp(d)));
        }
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

        Assert.assertTrue("exp() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("sin() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("cos() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("tan() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("atan() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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
            Dfp refdfp = DfpMath.atan(field.newDfp(y)
                .divide(field.newDfp(x)));
            /* Make adjustments for sign */
            if (x < 0.0) {
                if (y > 0.0)
                    refdfp = field.getPi().add(refdfp);
                else
                    refdfp = refdfp.subtract(field.getPi());
            }

            double ref = refdfp.toDouble();
            double err = (tst - ref) / ref;

            if (err != 0) {
                double ulp = Math.abs(ref -
                                      Double.longBitsToDouble((Double
                                          .doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(refdfp).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + y + "\t" + tst + "\t" + ref + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        Assert.assertTrue("atan2() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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
                                      Double.longBitsToDouble((Double
                                          .doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.exp(field.newDfp(x)).subtract(field.getOne())).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }

        Assert.assertTrue("expm1() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("asin() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("acos() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
    }

    /**
     * Added tests for a 100% coverage of acos().
     */
    @Test
    public void testAcosSpecialCases() {
        
        Assert.assertTrue("acos(NaN) should be NaN", Double.isNaN(FastMath.acos(Double.NaN)));
        
        Assert.assertTrue("acos(-1.1) should be NaN", Double.isNaN(FastMath.acos(-1.1)));

        Assert.assertTrue("acos(-1.1) should be NaN", Double.isNaN(FastMath.acos(1.1)));
        
        Assert.assertEquals("acos(-1.0) should be PI", FastMath.acos(-1.0), FastMath.PI, Precision.EPSILON);

        Assert.assertEquals("acos(1.0) should be 0.0", FastMath.acos(1.0), 0.0, Precision.EPSILON);

        Assert.assertEquals("acos(0.0) should be PI/2", FastMath.acos(0.0), FastMath.PI / 2.0, Precision.EPSILON);
    }

    /**
     * Added tests for a 100% coverage of asin().
     */
    @Test
    public void testAsinSpecialCases() {
   
        Assert.assertTrue("asin(NaN) should be NaN", Double.isNaN(FastMath.asin(Double.NaN)));
        
        Assert.assertTrue("asin(1.1) should be NaN", Double.isNaN(FastMath.asin(1.1)));
        
        Assert.assertTrue("asin(-1.1) should be NaN", Double.isNaN(FastMath.asin(-1.1)));
        
        Assert.assertEquals("asin(1.0) should be PI/2", FastMath.asin(1.0), FastMath.PI / 2.0, Precision.EPSILON);

        Assert.assertEquals("asin(-1.0) should be -PI/2", FastMath.asin(-1.0), -FastMath.PI / 2.0, Precision.EPSILON);

        Assert.assertEquals("asin(0.0) should be 0.0", FastMath.asin(0.0), 0.0, Precision.EPSILON);
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

        Assert.assertTrue("sinh() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("cosh() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("tanh() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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

        Assert.assertTrue("cbrt() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);
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
        Assert.assertTrue("toDegrees() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);

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
                                      Double.longBitsToDouble((Double
                                          .doubleToLongBits(ref) ^ 1)));
                double errulp = field.newDfp(tst).subtract(DfpMath.exp(field.newDfp(x)).subtract(field.getOne())).divide(field.newDfp(ulp)).toDouble();
//                System.out.println(x + "\t" + tst + "\t" + ref + "\t" + err + "\t" + errulp);

                maxerrulp = Math.max(maxerrulp, Math.abs(errulp));
            }
        }
        Assert.assertTrue("toRadians() had errors in excess of " + MAX_ERROR_ULP + " ULP", maxerrulp < MAX_ERROR_ULP);

    }

    @Test
    public void testNextAfter() {
        // 0x402fffffffffffff 0x404123456789abcd -> 4030000000000000
        Assert.assertEquals(16.0, FastMath.nextUp(15.999999999999998), 0.0);

        // 0xc02fffffffffffff 0x404123456789abcd -> c02ffffffffffffe
        Assert.assertEquals(-15.999999999999996, FastMath.nextAfter(-15.999999999999998, 34.27555555555555), 0.0);

        // 0x402fffffffffffff 0x400123456789abcd -> 402ffffffffffffe
        Assert.assertEquals(15.999999999999996, FastMath.nextDown(15.999999999999998), 0.0);

        // 0xc02fffffffffffff 0x400123456789abcd -> c02ffffffffffffe
        Assert.assertEquals(-15.999999999999996, FastMath.nextAfter(-15.999999999999998, 2.142222222222222), 0.0);

        // 0x4020000000000000 0x404123456789abcd -> 4020000000000001
        Assert.assertEquals(8.000000000000002, FastMath.nextAfter(8.0, 34.27555555555555), 0.0);

        // 0xc020000000000000 0x404123456789abcd -> c01fffffffffffff
        Assert.assertEquals(-7.999999999999999, FastMath.nextAfter(-8.0, 34.27555555555555), 0.0);

        // 0x4020000000000000 0x400123456789abcd -> 401fffffffffffff
        Assert.assertEquals(7.999999999999999, FastMath.nextAfter(8.0, 2.142222222222222), 0.0);

        // 0xc020000000000000 0x400123456789abcd -> c01fffffffffffff
        Assert.assertEquals(-7.999999999999999, FastMath.nextAfter(-8.0, 2.142222222222222), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a224 -> 3f2e43753d36a224
        Assert.assertEquals(2.308922399667661E-4, FastMath.nextAfter(2.3089223996676606E-4, 2.308922399667661E-4), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a223 -> 3f2e43753d36a223
        Assert.assertEquals(2.3089223996676606E-4, FastMath.nextAfter(2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);

        // 0x3f2e43753d36a223 0x3f2e43753d36a222 -> 3f2e43753d36a222
        Assert.assertEquals(2.3089223996676603E-4, FastMath.nextAfter(2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a224 -> 3f2e43753d36a222
        Assert.assertEquals(2.3089223996676603E-4, FastMath.nextAfter(2.3089223996676606E-4, -2.308922399667661E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a223 -> 3f2e43753d36a222
        Assert.assertEquals(2.3089223996676603E-4, FastMath.nextAfter(2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);

        // 0x3f2e43753d36a223 0xbf2e43753d36a222 -> 3f2e43753d36a222
        Assert.assertEquals(2.3089223996676603E-4, FastMath.nextAfter(2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a224 -> bf2e43753d36a222
        Assert.assertEquals(-2.3089223996676603E-4, FastMath.nextAfter(-2.3089223996676606E-4, 2.308922399667661E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a223 -> bf2e43753d36a222
        Assert.assertEquals(-2.3089223996676603E-4, FastMath.nextAfter(-2.3089223996676606E-4, 2.3089223996676606E-4), 0.0);

        // 0xbf2e43753d36a223 0x3f2e43753d36a222 -> bf2e43753d36a222
        Assert.assertEquals(-2.3089223996676603E-4, FastMath.nextAfter(-2.3089223996676606E-4, 2.3089223996676603E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a224 -> bf2e43753d36a224
        Assert.assertEquals(-2.308922399667661E-4, FastMath.nextAfter(-2.3089223996676606E-4, -2.308922399667661E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a223 -> bf2e43753d36a223
        Assert.assertEquals(-2.3089223996676606E-4, FastMath.nextAfter(-2.3089223996676606E-4, -2.3089223996676606E-4), 0.0);

        // 0xbf2e43753d36a223 0xbf2e43753d36a222 -> bf2e43753d36a222
        Assert.assertEquals(-2.3089223996676603E-4, FastMath.nextAfter(-2.3089223996676606E-4, -2.3089223996676603E-4), 0.0);

    }

    @Test
    public void testDoubleNextAfterSpecialCases() {
        Assert.assertEquals(-Double.MAX_VALUE,FastMath.nextAfter(Double.NEGATIVE_INFINITY, 0D), 0D);
        Assert.assertEquals(Double.MAX_VALUE,FastMath.nextAfter(Double.POSITIVE_INFINITY, 0D), 0D);
        Assert.assertEquals(Double.NaN,FastMath.nextAfter(Double.NaN, 0D), 0D);
        Assert.assertEquals(Double.POSITIVE_INFINITY,FastMath.nextAfter(Double.MAX_VALUE, Double.POSITIVE_INFINITY), 0D);
        Assert.assertEquals(Double.NEGATIVE_INFINITY,FastMath.nextAfter(-Double.MAX_VALUE, Double.NEGATIVE_INFINITY), 0D);
        Assert.assertEquals(Double.MIN_VALUE, FastMath.nextAfter(0D, 1D), 0D);
        Assert.assertEquals(-Double.MIN_VALUE, FastMath.nextAfter(0D, -1D), 0D);
        Assert.assertEquals(0D, FastMath.nextAfter(Double.MIN_VALUE, -1), 0D);
        Assert.assertEquals(0D, FastMath.nextAfter(-Double.MIN_VALUE, 1), 0D);
    }

    @Test
    public void testFloatNextAfterSpecialCases() {
        Assert.assertEquals(-Float.MAX_VALUE,FastMath.nextAfter(Float.NEGATIVE_INFINITY, 0F), 0F);
        Assert.assertEquals(Float.MAX_VALUE,FastMath.nextAfter(Float.POSITIVE_INFINITY, 0F), 0F);
        Assert.assertEquals(Float.NaN,FastMath.nextAfter(Float.NaN, 0F), 0F);
        Assert.assertEquals(Float.POSITIVE_INFINITY,FastMath.nextUp(Float.MAX_VALUE), 0F);
        Assert.assertEquals(Float.NEGATIVE_INFINITY,FastMath.nextDown(-Float.MAX_VALUE), 0F);
        Assert.assertEquals(Float.MIN_VALUE, FastMath.nextAfter(0F, 1F), 0F);
        Assert.assertEquals(-Float.MIN_VALUE, FastMath.nextAfter(0F, -1F), 0F);
        Assert.assertEquals(0F, FastMath.nextAfter(Float.MIN_VALUE, -1F), 0F);
        Assert.assertEquals(0F, FastMath.nextAfter(-Float.MIN_VALUE, 1F), 0F);
    }

    @Test
    public void testDoubleScalbSpecialCases() {
        Assert.assertEquals(2.5269841324701218E-175,  FastMath.scalb(2.2250738585072014E-308, 442), 0D);
        Assert.assertEquals(1.307993905256674E297,    FastMath.scalb(1.1102230246251565E-16, 1040), 0D);
        Assert.assertEquals(7.2520887996488946E-217,  FastMath.scalb(Double.MIN_VALUE,        356), 0D);
        Assert.assertEquals(8.98846567431158E307,     FastMath.scalb(Double.MIN_VALUE,       2097), 0D);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb(Double.MIN_VALUE,       2098), 0D);
        Assert.assertEquals(1.1125369292536007E-308,  FastMath.scalb(2.225073858507201E-308,   -1), 0D);
        Assert.assertEquals(1.0E-323,                 FastMath.scalb(Double.MAX_VALUE,      -2097), 0D);
        Assert.assertEquals(Double.MIN_VALUE,         FastMath.scalb(Double.MAX_VALUE,      -2098), 0D);
        Assert.assertEquals(0,                        FastMath.scalb(Double.MAX_VALUE,      -2099), 0D);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb(Double.POSITIVE_INFINITY, -1000000), 0D);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-1.1102230246251565E-16, 1078), 0D);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-1.1102230246251565E-16,  1079), 0D);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-2.2250738585072014E-308, 2047), 0D);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-2.2250738585072014E-308, 2048), 0D);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-1.7976931348623157E308,  2147483647), 0D);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb( 1.7976931348623157E308,  2147483647), 0D);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-1.1102230246251565E-16,  2147483647), 0D);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb( 1.1102230246251565E-16,  2147483647), 0D);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, FastMath.scalb(-2.2250738585072014E-308, 2147483647), 0D);
        Assert.assertEquals(Double.POSITIVE_INFINITY, FastMath.scalb( 2.2250738585072014E-308, 2147483647), 0D);
    }

    @Test
    public void testFloatScalbSpecialCases() {
        Assert.assertEquals(0f,                       FastMath.scalb(Float.MIN_VALUE,  -30), 0F);
        Assert.assertEquals(2 * Float.MIN_VALUE,      FastMath.scalb(Float.MIN_VALUE,    1), 0F);
        Assert.assertEquals(7.555786e22f,             FastMath.scalb(Float.MAX_VALUE,  -52), 0F);
        Assert.assertEquals(1.7014118e38f,            FastMath.scalb(Float.MIN_VALUE,  276), 0F);
        Assert.assertEquals(Float.POSITIVE_INFINITY,  FastMath.scalb(Float.MIN_VALUE,  277), 0F);
        Assert.assertEquals(5.8774718e-39f,           FastMath.scalb(1.1754944e-38f,    -1), 0F);
        Assert.assertEquals(2 * Float.MIN_VALUE,      FastMath.scalb(Float.MAX_VALUE, -276), 0F);
        Assert.assertEquals(Float.MIN_VALUE,          FastMath.scalb(Float.MAX_VALUE, -277), 0F);
        Assert.assertEquals(0,                        FastMath.scalb(Float.MAX_VALUE, -278), 0F);
        Assert.assertEquals(Float.POSITIVE_INFINITY,  FastMath.scalb(Float.POSITIVE_INFINITY, -1000000), 0F);
        Assert.assertEquals(-3.13994498e38f,          FastMath.scalb(-1.1e-7f,         151), 0F);
        Assert.assertEquals(Float.NEGATIVE_INFINITY,  FastMath.scalb(-1.1e-7f,         152), 0F);
        Assert.assertEquals(Float.POSITIVE_INFINITY,  FastMath.scalb(3.4028235E38f,  2147483647), 0F);
        Assert.assertEquals(Float.NEGATIVE_INFINITY,  FastMath.scalb(-3.4028235E38f, 2147483647), 0F);
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
        Assert.assertTrue("FastMath should implement all StrictMath methods", ok);
    }

    @Ignore
    @Test
    public void checkExtraFastMathClasses() {
        compareClassMethods( FastMath.class, StrictMath.class);
    }

    @Test
    public void testSignumDouble() {
        final double delta = 0.0;
        Assert.assertEquals(1.0, FastMath.signum(2.0), delta);
        Assert.assertEquals(0.0, FastMath.signum(0.0), delta);
        Assert.assertEquals(-1.0, FastMath.signum(-2.0), delta);
        TestUtils.assertSame(-0. / 0., FastMath.signum(Double.NaN));
    }

    @Test
    public void testSignumFloat() {
        final float delta = 0.0F;
        Assert.assertEquals(1.0F, FastMath.signum(2.0F), delta);
        Assert.assertEquals(0.0F, FastMath.signum(0.0F), delta);
        Assert.assertEquals(-1.0F, FastMath.signum(-2.0F), delta);
        TestUtils.assertSame(Float.NaN, FastMath.signum(Float.NaN));
    }

    @Test
    public void testLogWithBase() {
        Assert.assertEquals(2.0, FastMath.log(2, 4), 0);
        Assert.assertEquals(3.0, FastMath.log(2, 8), 0);
        Assert.assertTrue(Double.isNaN(FastMath.log(-1, 1)));
        Assert.assertTrue(Double.isNaN(FastMath.log(1, -1)));
        Assert.assertTrue(Double.isNaN(FastMath.log(0, 0)));
        Assert.assertEquals(0, FastMath.log(0, 10), 0);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, FastMath.log(10, 0), 0);
    }

    @Test
    public void testIndicatorDouble() {
        double delta = 0.0;
        Assert.assertEquals(1.0, FastMath.copySign(1d, 2.0), delta);
        Assert.assertEquals(1.0, FastMath.copySign(1d, 0.0), delta);
        Assert.assertEquals(-1.0, FastMath.copySign(1d, -0.0), delta);
        Assert.assertEquals(1.0, FastMath.copySign(1d, Double.POSITIVE_INFINITY), delta);
        Assert.assertEquals(-1.0, FastMath.copySign(1d, Double.NEGATIVE_INFINITY), delta);
        Assert.assertEquals(1.0, FastMath.copySign(1d, Double.NaN), delta);
        Assert.assertEquals(-1.0, FastMath.copySign(1d, -2.0), delta);
    }

    @Test
    public void testIndicatorFloat() {
        float delta = 0.0F;
        Assert.assertEquals(1.0F, FastMath.copySign(1d, 2.0F), delta);
        Assert.assertEquals(1.0F, FastMath.copySign(1d, 0.0F), delta);
        Assert.assertEquals(-1.0F, FastMath.copySign(1d, -0.0F), delta);
        Assert.assertEquals(1.0F, FastMath.copySign(1d, Float.POSITIVE_INFINITY), delta);
        Assert.assertEquals(-1.0F, FastMath.copySign(1d, Float.NEGATIVE_INFINITY), delta);
        Assert.assertEquals(1.0F, FastMath.copySign(1d, Float.NaN), delta);
        Assert.assertEquals(-1.0F, FastMath.copySign(1d, -2.0F), delta);
    }

    @Test
    public void testIntPow() {
        final int maxExp = 300;
        DfpField field = new DfpField(40);
        final double base = 1.23456789;
        Dfp baseDfp = field.newDfp(base);
        Dfp dfpPower = field.getOne();
        for (int i = 0; i < maxExp; i++) {
            Assert.assertEquals("exp=" + i, dfpPower.toDouble(), FastMath.pow(base, i),
                                0.6 * FastMath.ulp(dfpPower.toDouble()));
            dfpPower = dfpPower.multiply(baseDfp);
        }
    }

    @Test
    public void testIntPowHuge() {
        Assert.assertTrue(Double.isInfinite(FastMath.pow(FastMath.scalb(1.0, 500), 4)));
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
                Assert.assertEquals(bdSum, BigInteger.valueOf(FastMath.incrementExact(a)));
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
                    Assert.fail("an exception should have been thrown");
                } catch (MathArithmeticException mae) {
                    // expected
                }
            } else {
                Assert.assertEquals(bdSub, BigInteger.valueOf(FastMath.decrementExact(a)));
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
                        Assert.fail("an exception should have been thrown");
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    Assert.assertEquals(bdSum, BigInteger.valueOf(FastMath.addExact(a, b)));
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
                        Assert.fail("an exception should have been thrown");
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    Assert.assertEquals(bdSum, BigInteger.valueOf(FastMath.addExact(a, b)));
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
                        Assert.fail("an exception should have been thrown");
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    Assert.assertEquals(bdSub, BigInteger.valueOf(FastMath.subtractExact(a, b)));
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
                        Assert.fail("an exception should have been thrown");
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    Assert.assertEquals(bdSub, BigInteger.valueOf(FastMath.subtractExact(a, b)));
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
                        Assert.fail("an exception should have been thrown " + a + b);
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    Assert.assertEquals(bdMul, BigInteger.valueOf(FastMath.multiplyExact(a, b)));
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
                        Assert.fail("an exception should have been thrown " + a + b);
                    } catch (MathArithmeticException mae) {
                        // expected
                    }
                } else {
                    Assert.assertEquals(bdMul, BigInteger.valueOf(FastMath.multiplyExact(a, b)));
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
            Assert.assertEquals(n, FastMath.toIntExact(0l + n));
        }
        Assert.assertEquals(Integer.MIN_VALUE, FastMath.toIntExact(0l + Integer.MIN_VALUE));
        Assert.assertEquals(Integer.MAX_VALUE, FastMath.toIntExact(0l + Integer.MAX_VALUE));
    }

    @Test
    public void testFloorDivInt() {
        Assert.assertEquals(+1, FastMath.floorDiv(+4, +3));
        Assert.assertEquals(-2, FastMath.floorDiv(-4, +3));
        Assert.assertEquals(-2, FastMath.floorDiv(+4, -3));
        Assert.assertEquals(+1, FastMath.floorDiv(-4, -3));
        try {
            FastMath.floorDiv(1, 0);
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException mae) {
            // expected
        }
        for (int a = -100; a <= 100; ++a) {
            for (int b = -100; b <= 100; ++b) {
                if (b != 0) {
                    Assert.assertEquals(poorManFloorDiv(a, b), FastMath.floorDiv(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorModInt() {
        Assert.assertEquals(+1, FastMath.floorMod(+4, +3));
        Assert.assertEquals(+2, FastMath.floorMod(-4, +3));
        Assert.assertEquals(-2, FastMath.floorMod(+4, -3));
        Assert.assertEquals(-1, FastMath.floorMod(-4, -3));
        try {
            FastMath.floorMod(1, 0);
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException mae) {
            // expected
        }
        for (int a = -100; a <= 100; ++a) {
            for (int b = -100; b <= 100; ++b) {
                if (b != 0) {
                    Assert.assertEquals(poorManFloorMod(a, b), FastMath.floorMod(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorDivModInt() {
        RandomGenerator generator = new Well1024a(0x7ccab45edeaab90al);
        for (int i = 0; i < 10000; ++i) {
            int a = generator.nextInt();
            int b = generator.nextInt();
            if (b == 0) {
                try {
                    FastMath.floorDiv(a, b);
                    Assert.fail("an exception should have been thrown");
                } catch (MathArithmeticException mae) {
                    // expected
                }
            } else {
                int d = FastMath.floorDiv(a, b);
                int m = FastMath.floorMod(a, b);
                Assert.assertEquals(FastMath.toIntExact(poorManFloorDiv(a, b)), d);
                Assert.assertEquals(FastMath.toIntExact(poorManFloorMod(a, b)), m);
                Assert.assertEquals(a, d * b + m);
                if (b < 0) {
                    Assert.assertTrue(m <= 0);
                    Assert.assertTrue(-m < -b);
                } else {
                    Assert.assertTrue(m >= 0);
                    Assert.assertTrue(m < b);
                }
            }
        }
    }

    @Test
    public void testFloorDivLong() {
        Assert.assertEquals(+1l, FastMath.floorDiv(+4l, +3l));
        Assert.assertEquals(-2l, FastMath.floorDiv(-4l, +3l));
        Assert.assertEquals(-2l, FastMath.floorDiv(+4l, -3l));
        Assert.assertEquals(+1l, FastMath.floorDiv(-4l, -3l));
        try {
            FastMath.floorDiv(1l, 0l);
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException mae) {
            // expected
        }
        for (long a = -100l; a <= 100l; ++a) {
            for (long b = -100l; b <= 100l; ++b) {
                if (b != 0) {
                    Assert.assertEquals(poorManFloorDiv(a, b), FastMath.floorDiv(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorModLong() {
        Assert.assertEquals(+1l, FastMath.floorMod(+4l, +3l));
        Assert.assertEquals(+2l, FastMath.floorMod(-4l, +3l));
        Assert.assertEquals(-2l, FastMath.floorMod(+4l, -3l));
        Assert.assertEquals(-1l, FastMath.floorMod(-4l, -3l));
        try {
            FastMath.floorMod(1l, 0l);
            Assert.fail("an exception should have been thrown");
        } catch (MathArithmeticException mae) {
            // expected
        }
        for (long a = -100l; a <= 100l; ++a) {
            for (long b = -100l; b <= 100l; ++b) {
                if (b != 0) {
                    Assert.assertEquals(poorManFloorMod(a, b), FastMath.floorMod(a, b));
                }
            }
        }
    }

    @Test
    public void testFloorDivModLong() {
        RandomGenerator generator = new Well1024a(0xb87b9bc14c96ccd5l);
        for (int i = 0; i < 10000; ++i) {
            long a = generator.nextLong();
            long b = generator.nextLong();
            if (b == 0) {
                try {
                    FastMath.floorDiv(a, b);
                    Assert.fail("an exception should have been thrown");
                } catch (MathArithmeticException mae) {
                    // expected
                }
            } else {
                long d = FastMath.floorDiv(a, b);
                long m = FastMath.floorMod(a, b);
                Assert.assertEquals(poorManFloorDiv(a, b), d);
                Assert.assertEquals(poorManFloorMod(a, b), m);
                Assert.assertEquals(a, d * b + m);
                if (b < 0) {
                    Assert.assertTrue(m <= 0);
                    Assert.assertTrue(-m < -b);
                } else {
                    Assert.assertTrue(m >= 0);
                    Assert.assertTrue(m < b);
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

}
