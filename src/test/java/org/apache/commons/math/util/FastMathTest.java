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
package org.apache.commons.math.util;

import org.apache.commons.math.dfp.Dfp;
import org.apache.commons.math.dfp.DfpField;
import org.apache.commons.math.dfp.DfpMath;
import org.apache.commons.math.random.MersenneTwister;
import org.apache.commons.math.random.RandomGenerator;
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
            { MathUtils.SAFE_MIN, MathUtils.EPSILON }
        };
        for (double[] pair : pairs) {
            Assert.assertEquals("min(" + pair[0] + ", " + pair[1] + ")",
                                Math.min(pair[0], pair[1]),
                                FastMath.min(pair[0], pair[1]),
                                MathUtils.EPSILON);
            Assert.assertEquals("min(" + pair[1] + ", " + pair[0] + ")",
                                Math.min(pair[1], pair[0]),
                                FastMath.min(pair[1], pair[0]),
                                MathUtils.EPSILON);
            Assert.assertEquals("max(" + pair[0] + ", " + pair[1] + ")",
                                Math.max(pair[0], pair[1]),
                                FastMath.max(pair[0], pair[1]),
                                MathUtils.EPSILON);
            Assert.assertEquals("max(" + pair[1] + ", " + pair[0] + ")",
                                Math.max(pair[1], pair[0]),
                                FastMath.max(pair[1], pair[0]),
                                MathUtils.EPSILON);
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
                                MathUtils.EPSILON);
            Assert.assertEquals("min(" + pair[1] + ", " + pair[0] + ")",
                                Math.min(pair[1], pair[0]),
                                FastMath.min(pair[1], pair[0]),
                                MathUtils.EPSILON);
            Assert.assertEquals("max(" + pair[0] + ", " + pair[1] + ")",
                                Math.max(pair[0], pair[1]),
                                FastMath.max(pair[0], pair[1]),
                                MathUtils.EPSILON);
            Assert.assertEquals("max(" + pair[1] + ", " + pair[0] + ")",
                                Math.max(pair[1], pair[0]),
                                FastMath.max(pair[1], pair[0]),
                                MathUtils.EPSILON);
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
        Assert.assertEquals(Math.atan2(y1, x1), FastMath.atan2(y1, x1), 2 * MathUtils.EPSILON);
        double y2 = 0.0;
        double x2 = Double.POSITIVE_INFINITY;
        Assert.assertEquals(Math.atan2(y2, x2), FastMath.atan2(y2, x2), MathUtils.SAFE_MIN);
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
        for (double x = -1 + MathUtils.EPSILON; x < 1 - MathUtils.EPSILON; x += 0.0001) {
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
    public void testLogSpecialCases() {
        double x;

        x = FastMath.log(0.0);
        if (x != Double.NEGATIVE_INFINITY)
            throw new RuntimeException("Log of zero should be -Inf");

        x = FastMath.log(-0.0);
        if (x != Double.NEGATIVE_INFINITY)
            throw new RuntimeException("Log of zero should be -Inf");

        x = FastMath.log(Double.NaN);
        if (x == x)
            throw new RuntimeException("Log of NaN should be NaN");

        x = FastMath.log(-1.0);
        if (x == x)
            throw new RuntimeException("Log of negative number should be NaN");

        x = FastMath.log(Double.MIN_VALUE);
        if (x != -744.4400719213812)
            throw new RuntimeException(
                                       "Log of Double.MIN_VALUE should be -744.4400719213812");

        x = FastMath.log(-1.0);
        if (x == x)
            throw new RuntimeException("Log of negative number should be NaN");

        x = FastMath.log(Double.POSITIVE_INFINITY);
        if (x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("Log of infinity should be infinity");
    }

    @Test
    public void testExpSpecialCases() {
        double x;

        /* Smallest value that will round up to Double.MIN_VALUE */
        x = FastMath.exp(-745.1332191019411);
        if (x != Double.MIN_VALUE)
            throw new RuntimeException(
                                       "exp(-745.1332191019411) should be Double.MIN_VALUE");

        x = FastMath.exp(-745.1332191019412);
        if (x != 0.0)
            throw new RuntimeException("exp(-745.1332191019412) should be 0.0");

        x = FastMath.exp(Double.NaN);
        if (x == x)
            throw new RuntimeException("exp of NaN should be NaN");

        x = FastMath.exp(Double.POSITIVE_INFINITY);
        if (x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("exp of infinity should be infinity");

        x = FastMath.exp(Double.NEGATIVE_INFINITY);
        if (x != 0.0)
            throw new RuntimeException("exp of -infinity should be 0.0");

        x = FastMath.exp(1.0);
        if (x != Math.E)
            throw new RuntimeException("exp(1) should be Math.E");
    }

    @Test
    public void testPowSpecialCases() {
        double x;

        x = FastMath.pow(-1.0, 0.0);
        if (x != 1.0)
            throw new RuntimeException("pow(x, 0) should be 1.0");

        x = FastMath.pow(-1.0, -0.0);
        if (x != 1.0)
            throw new RuntimeException("pow(x, -0) should be 1.0");

        x = FastMath.pow(Math.PI, 1.0);
        if (x != Math.PI)
            throw new RuntimeException("pow(PI, 1.0) should be PI");

        x = FastMath.pow(-Math.PI, 1.0);
        if (x != -Math.PI)
            throw new RuntimeException("pow(-PI, 1.0) should be PI");

        x = FastMath.pow(Math.PI, Double.NaN);
        if (x == x)
            throw new RuntimeException("pow(PI, NaN) should be NaN");

        x = FastMath.pow(Double.NaN, Math.PI);
        if (x == x)
            throw new RuntimeException("pow(NaN, PI) should be NaN");

        x = FastMath.pow(2.0, Double.POSITIVE_INFINITY);
        if (x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("pow(2.0, Infinity) should be Infinity");

        x = FastMath.pow(0.5, Double.NEGATIVE_INFINITY);
        if (x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("pow(0.5, -Infinity) should be Infinity");

        x = FastMath.pow(0.5, Double.POSITIVE_INFINITY);
        if (x != 0.0)
            throw new RuntimeException("pow(0.5, Infinity) should be 0.0");

        x = FastMath.pow(2.0, Double.NEGATIVE_INFINITY);
        if (x != 0.0)
            throw new RuntimeException("pow(2.0, -Infinity) should be 0.0");

        x = FastMath.pow(0.0, 0.5);
        if (x != 0.0)
            throw new RuntimeException("pow(0.0, 0.5) should be 0.0");

        x = FastMath.pow(Double.POSITIVE_INFINITY, -0.5);
        if (x != 0.0)
            throw new RuntimeException("pow(Inf, -0.5) should be 0.0");

        x = FastMath.pow(0.0, -0.5);
        if (x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("pow(0.0, -0.5) should be Inf");

        x = FastMath.pow(Double.POSITIVE_INFINITY, 0.5);
        if (x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("pow(Inf, 0.5) should be Inf");

        x = FastMath.pow(-0.0, -3.0);
        if (x != Double.NEGATIVE_INFINITY)
            throw new RuntimeException("pow(-0.0, -3.0) should be -Inf");

        x = FastMath.pow(Double.NEGATIVE_INFINITY, 3.0);
        if (x != Double.NEGATIVE_INFINITY)
            throw new RuntimeException("pow(-Inf, -3.0) should be -Inf");

        x = FastMath.pow(-0.0, -3.5);
        if (x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("pow(-0.0, -3.5) should be Inf");

        x = FastMath.pow(Double.POSITIVE_INFINITY, 3.5);
        if (x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("pow(Inf, 3.5) should be Inf");

        x = FastMath.pow(-2.0, 3.0);
        if (x != -8.0)
            throw new RuntimeException("pow(-2.0, 3.0) should be -8.0");

        x = FastMath.pow(-2.0, 3.5);
        if (x == x)
            throw new RuntimeException("pow(-2.0, 3.5) should be NaN");
    }

    @Test
    public void testAtan2SpecialCases() {
        double x;

        x = FastMath.atan2(Double.NaN, 0.0);
        if (x == x)
            throw new RuntimeException("atan2(NaN, 0.0) should be NaN");

        x = FastMath.atan2(0.0, Double.NaN);
        if (x == x)
            throw new RuntimeException("atan2(0.0, NaN) should be NaN");

        x = FastMath.atan2(0.0, 0.0);
        if (x != 0.0 || 1 / x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("atan2(0.0, 0.0) should be 0.0");

        x = FastMath.atan2(0.0, 0.001);
        if (x != 0.0 || 1 / x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("atan2(0.0, 0.001) should be 0.0");

        x = FastMath.atan2(0.1, Double.POSITIVE_INFINITY);
        if (x != 0.0 || 1 / x != Double.POSITIVE_INFINITY)
            throw new RuntimeException("atan2(0.1, +Inf) should be 0.0");

        x = FastMath.atan2(-0.0, 0.0);
        if (x != 0.0 || 1 / x != Double.NEGATIVE_INFINITY)
            throw new RuntimeException("atan2(-0.0, 0.0) should be -0.0");

        x = FastMath.atan2(-0.0, 0.001);
        if (x != 0.0 || 1 / x != Double.NEGATIVE_INFINITY)
            throw new RuntimeException("atan2(-0.0, 0.001) should be -0.0");

        x = FastMath.atan2(-0.1, Double.POSITIVE_INFINITY);
        if (x != 0.0 || 1 / x != Double.NEGATIVE_INFINITY)
            throw new RuntimeException("atan2(-0.0, +Inf) should be -0.0");

        x = FastMath.atan2(0.0, -0.0);
        if (x != Math.PI)
            throw new RuntimeException("atan2(0.0, -0.0) should be PI");

        x = FastMath.atan2(0.1, Double.NEGATIVE_INFINITY);
        if (x != Math.PI)
            throw new RuntimeException("atan2(0.1, -Inf) should be PI");

        x = FastMath.atan2(-0.0, -0.0);
        if (x != -Math.PI)
            throw new RuntimeException("atan2(-0.0, -0.0) should be -PI");

        x = FastMath.atan2(-0.1, Double.NEGATIVE_INFINITY);
        if (x != -Math.PI)
            throw new RuntimeException("atan2(0.1, -Inf) should be -PI");

        x = FastMath.atan2(0.1, 0.0);
        if (x != Math.PI / 2)
            throw new RuntimeException("atan2(0.1, 0.0) should be PI/2");

        x = FastMath.atan2(0.1, -0.0);
        if (x != Math.PI / 2)
            throw new RuntimeException("atan2(0.1, -0.0) should be PI/2");

        x = FastMath.atan2(Double.POSITIVE_INFINITY, 0.1);
        if (x != Math.PI / 2)
            throw new RuntimeException("atan2(Inf, 0.1) should be PI/2");

        x = FastMath.atan2(Double.POSITIVE_INFINITY, -0.1);
        if (x != Math.PI / 2)
            throw new RuntimeException("atan2(Inf, -0.1) should be PI/2");

        x = FastMath.atan2(-0.1, 0.0);
        if (x != -Math.PI / 2)
            throw new RuntimeException("atan2(-0.1, 0.0) should be -PI/2");

        x = FastMath.atan2(-0.1, -0.0);
        if (x != -Math.PI / 2)
            throw new RuntimeException("atan2(-0.1, -0.0) should be -PI/2");

        x = FastMath.atan2(Double.NEGATIVE_INFINITY, 0.1);
        if (x != -Math.PI / 2)
            throw new RuntimeException("atan2(-Inf, 0.1) should be -PI/2");

        x = FastMath.atan2(Double.NEGATIVE_INFINITY, -0.1);
        if (x != -Math.PI / 2)
            throw new RuntimeException("atan2(-Inf, -0.1) should be -PI/2");

        x = FastMath.atan2(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        if (x != Math.PI / 4)
            throw new RuntimeException("atan2(Inf, Inf) should be PI/4");

        x = FastMath.atan2(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        if (x != Math.PI * 3.0 / 4.0)
            throw new RuntimeException("atan2(Inf, -Inf) should be PI * 3/4");

        x = FastMath.atan2(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        if (x != -Math.PI / 4)
            throw new RuntimeException("atan2(-Inf, Inf) should be -PI/4");

        x = FastMath.atan2(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        if (x != -Math.PI * 3.0 / 4.0)
            throw new RuntimeException("atan2(-Inf, -Inf) should be -PI * 3/4");
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

    @Ignore
    @Test
    public void testPerformance() {
        final int numberOfRuns = 10000000;
        for (int j = 0; j < 10; j++) {
            double x = 0;
            long time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.log(Math.PI + i/* 1.0 + i/1e9 */);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.log " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.log(Math.PI + i/* 1.0 + i/1e9 */);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.log " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.pow(Math.PI + i / 1e6, i / 1e6);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.pow " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.pow(Math.PI + i / 1e6, i / 1e6);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.pow " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.exp(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.exp " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.exp(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.exp " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.sin(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.sin " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.sin(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.sin " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.asin(i / 10000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.asin " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.asin(i / 10000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.asin " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.cos(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.cos " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.cos(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.cos " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.acos(i / 10000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.acos " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.acos(i / 10000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.acos " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.tan(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.tan " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.tan(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.tan " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.atan(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.atan " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.atan(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.atan " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.cbrt(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.cbrt " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.cbrt(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.cbrt " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.cosh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.cosh " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.cosh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.cosh " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.sinh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.sinh " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.sinh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.sinh " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.tanh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.tanh " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.tanh(i / 1000000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.tanh " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += StrictMath.expm1(-i / 100000.0);
            time = System.currentTimeMillis() - time;
            System.out.print("StrictMath.expm1 " + time + "\t" + x + "\t");

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.expm1(-i / 100000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.expm1 " + time + "\t" + x);

            x = 0;
            time = System.currentTimeMillis();
            for (int i = 0; i < numberOfRuns; i++)
                x += FastMath.expm1(-i / 100000.0);
            time = System.currentTimeMillis() - time;
            System.out.println("FastMath.expm1 " + time + "\t" + x);
        }
    }

}
