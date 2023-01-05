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
package org.apache.commons.math4.legacy.core;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.numbers.core.Precision;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathArithmeticException;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.math4.legacy.exception.NoDataException;
import org.apache.commons.math4.legacy.exception.NonMonotonicSequenceException;
import org.apache.commons.math4.legacy.exception.NotANumberException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NotFiniteNumberException;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Test cases for the {@link MathArrays} class.
 */
public class MathArraysTest {

    private double[] testArray = {0, 1, 2, 3, 4, 5};
    private double[] testWeightsArray = {0.3, 0.2, 1.3, 1.1, 1.0, 1.8};
    private double[] testNegativeWeightsArray = {-0.3, 0.2, -1.3, 1.1, 1.0, 1.8};
    private double[] singletonArray = {0};

    @Test
    public void testScale() {
        final double[] test = new double[] {-2.5, -1, 0, 1, 2.5};
        final double[] correctTest = Arrays.copyOf(test, test.length);
        final double[] correctScaled = new double[]{5.25, 2.1, 0, -2.1, -5.25};

        final double[] scaled = MathArrays.scale(-2.1, test);

        // Make sure test has not changed
        for (int i = 0; i < test.length; i++) {
            Assert.assertEquals(correctTest[i], test[i], 0);
        }

        // Test scaled values
        for (int i = 0; i < scaled.length; i++) {
            Assert.assertEquals(correctScaled[i], scaled[i], 0);
        }
    }

    @Test
    public void testScaleInPlace() {
        final double[] test = new double[] {-2.5, -1, 0, 1, 2.5};
        final double[] correctScaled = new double[]{5.25, 2.1, 0, -2.1, -5.25};
        MathArrays.scaleInPlace(-2.1, test);

        // Make sure test has changed
        for (int i = 0; i < test.length; i++) {
            Assert.assertEquals(correctScaled[i], test[i], 0);
        }
    }

    @Test(expected = DimensionMismatchException.class)
    public void testEbeAddPrecondition() {
        MathArrays.ebeAdd(new double[3], new double[4]);
    }
    @Test(expected = DimensionMismatchException.class)
    public void testEbeSubtractPrecondition() {
        MathArrays.ebeSubtract(new double[3], new double[4]);
    }
    @Test(expected = DimensionMismatchException.class)
    public void testEbeMultiplyPrecondition() {
        MathArrays.ebeMultiply(new double[3], new double[4]);
    }
    @Test(expected = DimensionMismatchException.class)
    public void testEbeDividePrecondition() {
        MathArrays.ebeDivide(new double[3], new double[4]);
    }

    @Test
    public void testEbeAdd() {
        final double[] a = {0, 1, 2};
        final double[] b = {3, 5, 7};
        final double[] r = MathArrays.ebeAdd(a, b);

        for (int i = 0; i < a.length; i++) {
            Assert.assertEquals(a[i] + b[i], r[i], 0);
        }
    }
    @Test
    public void testEbeSubtract() {
        final double[] a = {0, 1, 2};
        final double[] b = {3, 5, 7};
        final double[] r = MathArrays.ebeSubtract(a, b);

        for (int i = 0; i < a.length; i++) {
            Assert.assertEquals(a[i] - b[i], r[i], 0);
        }
    }

    @Test
    public void testEbeMultiply() {
        final double[] a = {0, 1, 2};
        final double[] b = {3, 5, 7};
        final double[] r = MathArrays.ebeMultiply(a, b);

        for (int i = 0; i < a.length; i++) {
            Assert.assertEquals(a[i] * b[i], r[i], 0);
        }
    }

    @Test
    public void testEbeDivide() {
        final double[] a = {0, 1, 2};
        final double[] b = {3, 5, 7};
        final double[] r = MathArrays.ebeDivide(a, b);

        for (int i = 0; i < a.length; i++) {
            Assert.assertEquals(a[i] / b[i], r[i], 0);
        }
    }

    @Test
    public void testL1DistanceDouble() {
        double[] p1 = {2.5, 0.0};
        double[] p2 = {-0.5, 4.0};
        Assert.assertTrue(Precision.equals(7.0, MathArrays.distance1(p1, p2), 1));
    }

    @Test
    public void testL1DistanceInt() {
        int[] p1 = {3, 0};
        int[] p2 = {0, 4};
        Assert.assertEquals(7, MathArrays.distance1(p1, p2));
    }

    @Test
    public void testL2DistanceDouble() {
        double[] p1 = {2.5, 0.0};
        double[] p2 = {-0.5, 4.0};
        Assert.assertTrue(Precision.equals(5.0, MathArrays.distance(p1, p2), 1));
    }

    @Test
    public void testL2DistanceInt() {
        int[] p1 = {3, 0};
        int[] p2 = {0, 4};
        Assert.assertTrue(Precision.equals(5, MathArrays.distance(p1, p2), 1));
    }

    @Test
    public void testLInfDistanceDouble() {
        double[] p1 = {2.5, 0.0};
        double[] p2 = {-0.5, 4.0};
        Assert.assertTrue(Precision.equals(4.0, MathArrays.distanceInf(p1, p2), 1));
    }

    @Test
    public void testLInfDistanceInt() {
        int[] p1 = {3, 0};
        int[] p2 = {0, 4};
        Assert.assertEquals(4, MathArrays.distanceInf(p1, p2));
    }

    @Test
    public void testCheckOrder() {
        MathArrays.checkOrder(new double[] {-15, -5.5, -1, 2, 15},
                             MathArrays.OrderDirection.INCREASING, true);
        MathArrays.checkOrder(new double[] {-15, -5.5, -1, 2, 2},
                             MathArrays.OrderDirection.INCREASING, false);
        MathArrays.checkOrder(new double[] {3, -5.5, -11, -27.5},
                             MathArrays.OrderDirection.DECREASING, true);
        MathArrays.checkOrder(new double[] {3, 0, 0, -5.5, -11, -27.5},
                             MathArrays.OrderDirection.DECREASING, false);

        try {
            MathArrays.checkOrder(new double[] {-15, -5.5, -1, -1, 2, 15},
                                 MathArrays.OrderDirection.INCREASING, true);
            Assert.fail("an exception should have been thrown");
        } catch (NonMonotonicSequenceException e) {
            // Expected
        }
        try {
            MathArrays.checkOrder(new double[] {-15, -5.5, -1, -2, 2},
                                 MathArrays.OrderDirection.INCREASING, false);
            Assert.fail("an exception should have been thrown");
        } catch (NonMonotonicSequenceException e) {
            // Expected
        }
        try {
            MathArrays.checkOrder(new double[] {3, 3, -5.5, -11, -27.5},
                                 MathArrays.OrderDirection.DECREASING, true);
            Assert.fail("an exception should have been thrown");
        } catch (NonMonotonicSequenceException e) {
            // Expected
        }
        try {
            MathArrays.checkOrder(new double[] {3, -1, 0, -5.5, -11, -27.5},
                                 MathArrays.OrderDirection.DECREASING, false);
            Assert.fail("an exception should have been thrown");
        } catch (NonMonotonicSequenceException e) {
            // Expected
        }
        try {
            MathArrays.checkOrder(new double[] {3, 0, -5.5, -11, -10},
                                 MathArrays.OrderDirection.DECREASING, false);
            Assert.fail("an exception should have been thrown");
        } catch (NonMonotonicSequenceException e) {
            // Expected
        }
    }

    @Test
    public void testIsMonotonic() {
        Assert.assertFalse(MathArrays.isMonotonic(new double[] {-15, -5.5, -1, -1, 2, 15},
                                                  MathArrays.OrderDirection.INCREASING, true));
        Assert.assertTrue(MathArrays.isMonotonic(new double[] {-15, -5.5, -1, 0, 2, 15},
                                                 MathArrays.OrderDirection.INCREASING, true));
        Assert.assertFalse(MathArrays.isMonotonic(new double[] {-15, -5.5, -1, -2, 2},
                                                  MathArrays.OrderDirection.INCREASING, false));
        Assert.assertTrue(MathArrays.isMonotonic(new double[] {-15, -5.5, -1, -1, 2},
                                                 MathArrays.OrderDirection.INCREASING, false));
        Assert.assertFalse(MathArrays.isMonotonic(new double[] {3, 3, -5.5, -11, -27.5},
                                                  MathArrays.OrderDirection.DECREASING, true));
        Assert.assertTrue(MathArrays.isMonotonic(new double[] {3, 2, -5.5, -11, -27.5},
                                                 MathArrays.OrderDirection.DECREASING, true));
        Assert.assertFalse(MathArrays.isMonotonic(new double[] {3, -1, 0, -5.5, -11, -27.5},
                                                  MathArrays.OrderDirection.DECREASING, false));
        Assert.assertTrue(MathArrays.isMonotonic(new double[] {3, 0, 0, -5.5, -11, -27.5},
                                                 MathArrays.OrderDirection.DECREASING, false));
    }

    @Test
    public void testIsMonotonicComparable() {
        Assert.assertFalse(MathArrays.isMonotonic(new Double[] {Double.valueOf(-15),
                                                                Double.valueOf(-5.5),
                                                                Double.valueOf(-1),
                                                                Double.valueOf(-1),
                                                                Double.valueOf(2),
                                                                Double.valueOf(15) },
                MathArrays.OrderDirection.INCREASING, true));
        Assert.assertTrue(MathArrays.isMonotonic(new Double[] {Double.valueOf(-15),
                                                               Double.valueOf(-5.5),
                                                               Double.valueOf(-1),
                                                               Double.valueOf(0),
                                                               Double.valueOf(2),
                                                               Double.valueOf(15) },
                MathArrays.OrderDirection.INCREASING, true));
        Assert.assertFalse(MathArrays.isMonotonic(new Double[] {Double.valueOf(-15),
                                                                Double.valueOf(-5.5),
                                                                Double.valueOf(-1),
                                                                Double.valueOf(-2),
                                                                Double.valueOf(2) },
                MathArrays.OrderDirection.INCREASING, false));
        Assert.assertTrue(MathArrays.isMonotonic(new Double[] {Double.valueOf(-15),
                                                               Double.valueOf(-5.5),
                                                               Double.valueOf(-1),
                                                               Double.valueOf(-1),
                                                               Double.valueOf(2) },
                MathArrays.OrderDirection.INCREASING, false));
        Assert.assertFalse(MathArrays.isMonotonic(new Double[] {Double.valueOf(3),
                                                                Double.valueOf(3),
                                                                Double.valueOf(-5.5),
                                                                Double.valueOf(-11),
                                                                Double.valueOf(-27.5) },
                MathArrays.OrderDirection.DECREASING, true));
        Assert.assertTrue(MathArrays.isMonotonic(new Double[] {Double.valueOf(3),
                                                               Double.valueOf(2),
                                                               Double.valueOf(-5.5),
                                                               Double.valueOf(-11),
                                                               Double.valueOf(-27.5) },
                MathArrays.OrderDirection.DECREASING, true));
        Assert.assertFalse(MathArrays.isMonotonic(new Double[] {Double.valueOf(3),
                                                                Double.valueOf(-1),
                                                                Double.valueOf(0),
                                                                Double.valueOf(-5.5),
                                                                Double.valueOf(-11),
                                                                Double.valueOf(-27.5) },
                MathArrays.OrderDirection.DECREASING, false));
        Assert.assertTrue(MathArrays.isMonotonic(new Double[] {Double.valueOf(3),
                                                               Double.valueOf(0),
                                                               Double.valueOf(0),
                                                               Double.valueOf(-5.5),
                                                               Double.valueOf(-11),
                                                               Double.valueOf(-27.5) },
                MathArrays.OrderDirection.DECREASING, false));
    }

    @Test
    public void testCheckRectangular() {
        final long[][] rect = new long[][] {{0, 1}, {2, 3}};
        final long[][] ragged = new long[][] {{0, 1}, {2}};
        final long[][] nullArray = null;
        final long[][] empty = new long[][] {};
        MathArrays.checkRectangular(rect);
        MathArrays.checkRectangular(empty);
        try {
            MathArrays.checkRectangular(ragged);
            Assert.fail("Expecting DimensionMismatchException");
        } catch (DimensionMismatchException ex) {
            // Expected
        }
        try {
            MathArrays.checkRectangular(nullArray);
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // Expected
        }
    }

    @Test
    public void testCheckPositive() {
        final double[] positive = new double[] {1, 2, 3};
        final double[] nonNegative = new double[] {0, 1, 2};
        final double[] nullArray = null;
        final double[] empty = new double[] {};
        MathArrays.checkPositive(positive);
        MathArrays.checkPositive(empty);
        try {
            MathArrays.checkPositive(nullArray);
            Assert.fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // Expected
        }
        try {
            MathArrays.checkPositive(nonNegative);
            Assert.fail("Expecting NotStrictlyPositiveException");
        } catch (NotStrictlyPositiveException ex) {
            // Expected
        }
    }

    @Test
    public void testCheckNonNegative() {
        final long[] nonNegative = new long[] {0, 1};
        final long[] hasNegative = new long[] {-1};
        final long[] nullArray = null;
        final long[] empty = new long[] {};
        MathArrays.checkNonNegative(nonNegative);
        MathArrays.checkNonNegative(empty);
        try {
            MathArrays.checkNonNegative(nullArray);
            Assert.fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // Expected
        }
        try {
            MathArrays.checkNonNegative(hasNegative);
            Assert.fail("Expecting NotPositiveException");
        } catch (NotPositiveException ex) {
            // Expected
        }
    }

    @Test
    public void testCheckNonNegative2D() {
        final long[][] nonNegative = new long[][] {{0, 1}, {1, 0}};
        final long[][] hasNegative = new long[][] {{-1}, {0}};
        final long[][] nullArray = null;
        final long[][] empty = new long[][] {};
        MathArrays.checkNonNegative(nonNegative);
        MathArrays.checkNonNegative(empty);
        try {
            MathArrays.checkNonNegative(nullArray);
            Assert.fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // Expected
        }
        try {
            MathArrays.checkNonNegative(hasNegative);
            Assert.fail("Expecting NotPositiveException");
        } catch (NotPositiveException ex) {
            // Expected
        }
    }

    @Test
    public void testCheckNotNaN() {
        final double[] withoutNaN = {Double.NEGATIVE_INFINITY,
                                     -Double.MAX_VALUE,
                                     -1, 0,
                                     Double.MIN_VALUE,
                                     JdkMath.ulp(1d),
                                     1, 3, 113, 4769,
                                     Double.MAX_VALUE,
                                     Double.POSITIVE_INFINITY };

        final double[] withNaN = {Double.NEGATIVE_INFINITY,
                                  -Double.MAX_VALUE,
                                  -1, 0,
                                  Double.MIN_VALUE,
                                  JdkMath.ulp(1d),
                                  1, 3, 113, 4769,
                                  Double.MAX_VALUE,
                                  Double.POSITIVE_INFINITY,
                                  Double.NaN };


        final double[] nullArray = null;
        final double[] empty = new double[] {};
        MathArrays.checkNotNaN(withoutNaN);
        MathArrays.checkNotNaN(empty);
        try {
            MathArrays.checkNotNaN(nullArray);
            Assert.fail("Expecting NullPointerException");
        } catch (NullPointerException ex) {
            // Expected
        }
        try {
            MathArrays.checkNotNaN(withNaN);
            Assert.fail("Expecting NotANumberException");
        } catch (NotANumberException ex) {
            // Expected
        }
    }

    @Test(expected = DimensionMismatchException.class)
    public void testCheckEqualLength1() {
        MathArrays.checkEqualLength(new double[] {1, 2, 3},
                                    new double[] {1, 2, 3, 4});
    }

    @Test
    public void testCheckEqualLength2() {
        final double[] a = new double[] {-1, -12, -23, -34};
        final double[] b = new double[] {56, 67, 78, 89};
        Assert.assertTrue(MathArrays.checkEqualLength(a, b, false));
    }

    @Test
    public void testArrayEquals() {
        Assert.assertFalse(MathArrays.equals(new double[] {1d}, null));
        Assert.assertFalse(MathArrays.equals(null, new double[] {1d}));
        Assert.assertTrue(MathArrays.equals((double[]) null, (double[]) null));

        Assert.assertFalse(MathArrays.equals(new double[] {1d}, new double[0]));
        Assert.assertTrue(MathArrays.equals(new double[] {1d}, new double[] {1d}));
        Assert.assertTrue(MathArrays.equals(new double[] {Double.POSITIVE_INFINITY,
                                                          Double.NEGATIVE_INFINITY, 1d, 0d},
                                            new double[] {Double.POSITIVE_INFINITY,
                                                          Double.NEGATIVE_INFINITY, 1d, 0d}));
        Assert.assertFalse(MathArrays.equals(new double[] {Double.NaN},
                                             new double[] {Double.NaN}));
        Assert.assertFalse(MathArrays.equals(new double[] {Double.POSITIVE_INFINITY},
                                             new double[] {Double.NEGATIVE_INFINITY}));
        Assert.assertFalse(MathArrays.equals(new double[] {1d},
                                             new double[] {JdkMath.nextAfter(JdkMath.nextAfter(1d, 2d), 2d)}));
    }

    @Test
    public void testArrayEqualsIncludingNaN() {
        Assert.assertFalse(MathArrays.equalsIncludingNaN(new double[] {1d}, null));
        Assert.assertFalse(MathArrays.equalsIncludingNaN(null, new double[] {1d}));
        Assert.assertTrue(MathArrays.equalsIncludingNaN((double[]) null, (double[]) null));

        Assert.assertFalse(MathArrays.equalsIncludingNaN(new double[] {1d}, new double[0]));
        Assert.assertTrue(MathArrays.equalsIncludingNaN(new double[] {1d}, new double[] {1d}));
        Assert.assertTrue(MathArrays.equalsIncludingNaN(new double[] {Double.NaN, Double.POSITIVE_INFINITY,
                                                                      Double.NEGATIVE_INFINITY, 1d, 0d},
                                                        new double[] {Double.NaN, Double.POSITIVE_INFINITY,
                                                                      Double.NEGATIVE_INFINITY, 1d, 0d}));
        Assert.assertFalse(MathArrays.equalsIncludingNaN(new double[] {Double.POSITIVE_INFINITY},
                                                         new double[] {Double.NEGATIVE_INFINITY}));
        Assert.assertFalse(MathArrays.equalsIncludingNaN(new double[] {1d},
                                                         new double[] {JdkMath.nextAfter(JdkMath.nextAfter(1d, 2d), 2d)}));
    }

    @Test
    public void testNormalizeArray() {
        double[] testValues1 = new double[] {1, 1, 2};
        Assert.assertArrayEquals(new double[] {.25, .25, .5},
                                 MathArrays.normalizeArray(testValues1, 1),
                                 Double.MIN_VALUE);

        double[] testValues2 = new double[] {-1, -1, 1};
        Assert.assertArrayEquals(new double[] {1, 1, -1},
                                 MathArrays.normalizeArray(testValues2, 1),
                                 Double.MIN_VALUE);

        // Ignore NaNs
        double[] testValues3 = new double[] {-1, -1, Double.NaN, 1, Double.NaN};
        Assert.assertArrayEquals(new double[] {1, 1, Double.NaN, -1, Double.NaN},
                                 MathArrays.normalizeArray(testValues3, 1),
                                 Double.MIN_VALUE);

        // Zero sum -> MathArithmeticException
        double[] zeroSum = new double[] {-1, 1};
        try {
            MathArrays.normalizeArray(zeroSum, 1);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) { /* ignore */ }

        // Infinite elements -> MathArithmeticException
        double[] hasInf = new double[] {1, 2, 1, Double.NEGATIVE_INFINITY};
        try {
            MathArrays.normalizeArray(hasInf, 1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) { /* ignore */ }

        // Infinite target -> MathIllegalArgumentException
        try {
            MathArrays.normalizeArray(testValues1, Double.POSITIVE_INFINITY);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) { /* ignore */ }

        // NaN target -> MathIllegalArgumentException
        try {
            MathArrays.normalizeArray(testValues1, Double.NaN);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) { /* ignore */ }
    }

    @Test
    public void testConvolve() {
        /* Test Case (obtained via SciPy)
         * x=[1.2,-1.8,1.4]
         * h=[1,0.8,0.5,0.3]
         * convolve(x,h) -> array([ 1.2 , -0.84,  0.56,  0.58,  0.16,  0.42])
         */
        double[] x1 = {1.2, -1.8, 1.4};
        double[] h1 = {1, 0.8, 0.5, 0.3};
        double[] y1 = {1.2, -0.84, 0.56, 0.58, 0.16, 0.42};
        double tolerance = 1e-13;

        double[] yActual = MathArrays.convolve(x1, h1);
        Assert.assertArrayEquals(y1, yActual, tolerance);

        double[] x2 = {1, 2, 3};
        double[] h2 = {0, 1, 0.5};
        double[] y2 = {0, 1, 2.5, 4, 1.5};

        yActual = MathArrays.convolve(x2, h2);
        Assert.assertArrayEquals(y2, yActual, tolerance);

        try {
            MathArrays.convolve(new double[]{1, 2}, null);
            Assert.fail("an exception should have been thrown");
        } catch (NullArgumentException e) {
            // expected behavior
        }

        try {
            MathArrays.convolve(null, new double[]{1, 2});
            Assert.fail("an exception should have been thrown");
        } catch (NullArgumentException e) {
            // expected behavior
        }

        try {
            MathArrays.convolve(new double[]{1, 2}, new double[]{});
            Assert.fail("an exception should have been thrown");
        } catch (NoDataException e) {
            // expected behavior
        }

        try {
            MathArrays.convolve(new double[]{}, new double[]{1, 2});
            Assert.fail("an exception should have been thrown");
        } catch (NoDataException e) {
            // expected behavior
        }

        try {
            MathArrays.convolve(new double[]{}, new double[]{});
            Assert.fail("an exception should have been thrown");
        } catch (NoDataException e) {
            // expected behavior
        }
    }

    @Test
    public void testNatural() {
        final int n = 4;
        final int[] expected = {0, 1, 2, 3};

        final int[] natural = MathArrays.natural(n);
        for (int i = 0; i < n; i++) {
            Assert.assertEquals(expected[i], natural[i]);
        }
    }

    @Test
    public void testNaturalZero() {
        final int[] natural = MathArrays.natural(0);
        Assert.assertEquals(0, natural.length);
    }

    @Test
    public void testSequence() {
        final int size = 4;
        final int start = 5;
        final int stride = 2;
        final int[] expected = {5, 7, 9, 11};

        final int[] seq = MathArrays.sequence(size, start, stride);
        for (int i = 0; i < size; i++) {
            Assert.assertEquals(expected[i], seq[i]);
        }
    }

    @Test
    public void testSequenceZero() {
        final int[] seq = MathArrays.sequence(0, 12345, 6789);
        Assert.assertEquals(0, seq.length);
    }

    @Test
    public void testVerifyValuesPositive() {
        for (int j = 0; j < 6; j++) {
            for (int i = 1; i < (7 - j); i++) {
                Assert.assertTrue(MathArrays.verifyValues(testArray, 0, i));
            }
        }
        Assert.assertTrue(MathArrays.verifyValues(singletonArray, 0, 1));
        Assert.assertTrue(MathArrays.verifyValues(singletonArray, 0, 0, true));
    }

    @Test
    public void testVerifyValuesNegative() {
        final double[] nullArray = null;
        Assert.assertFalse(MathArrays.verifyValues(singletonArray, 0, 0));
        Assert.assertFalse(MathArrays.verifyValues(testArray, 0, 0));
        Assert.assertTrue(MathArrays.verifyValues(testArray, 0, 0, true));
        Assert.assertFalse(MathArrays.verifyValues(testArray, testWeightsArray, 0, 0));
        Assert.assertTrue(MathArrays.verifyValues(testArray, testWeightsArray, 0, 0, true));
        try {
            MathArrays.verifyValues(singletonArray, 2, 1);  // start past end
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MathArrays.verifyValues(testArray, 0, 7);  // end past end
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MathArrays.verifyValues(testArray, -1, 1);  // start negative
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MathArrays.verifyValues(testArray, 0, -1);  // length negative
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MathArrays.verifyValues(nullArray, 0, 1);  // null array
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
        try {
            MathArrays.verifyValues(testArray, nullArray, 0, 1);  // null weights array
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
        try {
            MathArrays.verifyValues(singletonArray, testWeightsArray, 0, 1);  // weights.length != value.length
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MathArrays.verifyValues(testArray, testNegativeWeightsArray, 0, 6);  // can't have negative weights
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MathArrays.verifyValues(testArray, new double[testArray.length], 0, 6);  // can't have all zero weights
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testConcatenate() {
        final double[] u = new double[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        final double[] x = new double[] {0, 1, 2};
        final double[] y = new double[] {3, 4, 5, 6, 7, 8};
        final double[] z = new double[] {9};
        Assert.assertArrayEquals(u, MathArrays.concatenate(x, y, z), 0);
    }

    @Test
    public void testConcatenateSingle() {
        final double[] x = new double[] {0, 1, 2};
        Assert.assertArrayEquals(x, MathArrays.concatenate(x), 0);
    }

    public void testConcatenateEmptyArguments() {
        final double[] x = new double[] {0, 1, 2};
        final double[] y = new double[] {3};
        final double[] z = new double[] {};
        final double[] u = new double[] {0, 1, 2, 3};
        Assert.assertArrayEquals(u,  MathArrays.concatenate(x, z, y), 0);
        Assert.assertArrayEquals(u,  MathArrays.concatenate(x, y, z), 0);
        Assert.assertArrayEquals(u,  MathArrays.concatenate(z, x, y), 0);
        Assert.assertEquals(0,  MathArrays.concatenate(z, z, z).length);
    }

    @Test(expected = NullPointerException.class)
    public void testConcatenateNullArguments() {
        final double[] x = new double[] {0, 1, 2};
        MathArrays.concatenate(x, null);
    }

    @Test
    public void testUnique() {
        final double[] x = {0, 9, 3, 0, 11, 7, 3, 5, -1, -2};
        final double[] values = {11, 9, 7, 5, 3, 0, -1, -2};
        Assert.assertArrayEquals(values, MathArrays.unique(x), 0);
    }

    @Test
    public void testUniqueInfiniteValues() {
        final double[] x = {0, Double.NEGATIVE_INFINITY, 3, Double.NEGATIVE_INFINITY,
            3, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        final double[] u = {Double.POSITIVE_INFINITY, 3, 0, Double.NEGATIVE_INFINITY};
        Assert.assertArrayEquals(u, MathArrays.unique(x), 0);
    }

    @Test
    public void testUniqueNaNValues() {
        final double[] x = new double[] {10, 2, Double.NaN, Double.NaN, Double.NaN,
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        final double[] u = MathArrays.unique(x);
        Assert.assertEquals(5, u.length);
        Assert.assertTrue(Double.isNaN(u[0]));
        Assert.assertEquals(Double.POSITIVE_INFINITY, u[1], 0);
        Assert.assertEquals(10, u[2], 0);
        Assert.assertEquals(2, u[3], 0);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, u[4], 0);
    }

    @Test(expected = NullPointerException.class)
    public void testUniqueNullArgument() {
        MathArrays.unique(null);
    }

    @Test
    public void testCheckFinite() {
        try {
            MathArrays.checkFinite(new double[] {0, -1, Double.POSITIVE_INFINITY, -2, 3});
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            MathArrays.checkFinite(new double[] {1, Double.NEGATIVE_INFINITY, -2, 3});
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
        try {
            MathArrays.checkFinite(new double[] {4, 3, -1, Double.NaN, -2, 1});
            Assert.fail("an exception should have been thrown");
        } catch (NotFiniteNumberException e) {
            // Expected
        }
    }
}
