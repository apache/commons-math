/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.apache.commons.math3.util;

import java.util.Arrays;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.random.Well1024a;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases for the {@link MathArrays} class.
 *
 */
public class MathArraysTest {

    private double[] testArray = {0, 1, 2, 3, 4, 5};
    private double[] testWeightsArray = {0.3, 0.2, 1.3, 1.1, 1.0, 1.8};
    private double[] testNegativeWeightsArray = {-0.3, 0.2, -1.3, 1.1, 1.0, 1.8};
    private double[] nullArray = null;
    private double[] singletonArray = {0};

    @Test
    public void testScale() {
        final double[] test = new double[] { -2.5, -1, 0, 1, 2.5 };
        final double[] correctTest = MathArrays.copyOf(test);
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
        final double[] test = new double[] { -2.5, -1, 0, 1, 2.5 };
        final double[] correctScaled = new double[]{5.25, 2.1, 0, -2.1, -5.25};
        MathArrays.scaleInPlace(-2.1, test);

        // Make sure test has changed
        for (int i = 0; i < test.length; i++) {
            Assert.assertEquals(correctScaled[i], test[i], 0);
        }
    }

    @Test(expected=DimensionMismatchException.class)
    public void testEbeAddPrecondition() {
        MathArrays.ebeAdd(new double[3], new double[4]);
    }
    @Test(expected=DimensionMismatchException.class)
    public void testEbeSubtractPrecondition() {
        MathArrays.ebeSubtract(new double[3], new double[4]);
    }
    @Test(expected=DimensionMismatchException.class)
    public void testEbeMultiplyPrecondition() {
        MathArrays.ebeMultiply(new double[3], new double[4]);
    }
    @Test(expected=DimensionMismatchException.class)
    public void testEbeDividePrecondition() {
        MathArrays.ebeDivide(new double[3], new double[4]);
    }

    @Test
    public void testEbeAdd() {
        final double[] a = { 0, 1, 2 };
        final double[] b = { 3, 5, 7 };
        final double[] r = MathArrays.ebeAdd(a, b);

        for (int i = 0; i < a.length; i++) {
            Assert.assertEquals(a[i] + b[i], r[i], 0);
        }
    }
    @Test
    public void testEbeSubtract() {
        final double[] a = { 0, 1, 2 };
        final double[] b = { 3, 5, 7 };
        final double[] r = MathArrays.ebeSubtract(a, b);

        for (int i = 0; i < a.length; i++) {
            Assert.assertEquals(a[i] - b[i], r[i], 0);
        }
    }
    @Test
    public void testEbeMultiply() {
        final double[] a = { 0, 1, 2 };
        final double[] b = { 3, 5, 7 };
        final double[] r = MathArrays.ebeMultiply(a, b);

        for (int i = 0; i < a.length; i++) {
            Assert.assertEquals(a[i] * b[i], r[i], 0);
        }
    }
    @Test
    public void testEbeDivide() {
        final double[] a = { 0, 1, 2 };
        final double[] b = { 3, 5, 7 };
        final double[] r = MathArrays.ebeDivide(a, b);

        for (int i = 0; i < a.length; i++) {
            Assert.assertEquals(a[i] / b[i], r[i], 0);
        }
    }

    @Test
    public void testL1DistanceDouble() {
        double[] p1 = { 2.5,  0.0 };
        double[] p2 = { -0.5, 4.0 };
        Assert.assertTrue(Precision.equals(7.0, MathArrays.distance1(p1, p2), 1));
    }

    @Test
    public void testL1DistanceInt() {
        int[] p1 = { 3, 0 };
        int[] p2 = { 0, 4 };
        Assert.assertEquals(7, MathArrays.distance1(p1, p2));
    }

    @Test
    public void testL2DistanceDouble() {
        double[] p1 = { 2.5,  0.0 };
        double[] p2 = { -0.5, 4.0 };
        Assert.assertTrue(Precision.equals(5.0, MathArrays.distance(p1, p2), 1));
    }

    @Test
    public void testL2DistanceInt() {
        int[] p1 = { 3, 0 };
        int[] p2 = { 0, 4 };
        Assert.assertTrue(Precision.equals(5, MathArrays.distance(p1, p2), 1));
    }

    @Test
    public void testLInfDistanceDouble() {
        double[] p1 = { 2.5,  0.0 };
        double[] p2 = { -0.5, 4.0 };
        Assert.assertTrue(Precision.equals(4.0, MathArrays.distanceInf(p1, p2), 1));
    }

    @Test
    public void testLInfDistanceInt() {
        int[] p1 = { 3, 0 };
        int[] p2 = { 0, 4 };
        Assert.assertEquals(4, MathArrays.distanceInf(p1, p2));
    }

    @Test
    public void testCosAngle2D() {
        double expected;

        final double[] v1 = { 1, 0 };
        expected = 1;
        Assert.assertEquals(expected, MathArrays.cosAngle(v1, v1), 0d);

        final double[] v2 = { 0, 1 };
        expected = 0;
        Assert.assertEquals(expected, MathArrays.cosAngle(v1, v2), 0d);

        final double[] v3 = { 7, 7 };
        expected = Math.sqrt(2) / 2;
        Assert.assertEquals(expected, MathArrays.cosAngle(v1, v3), 1e-15);
        Assert.assertEquals(expected, MathArrays.cosAngle(v3, v2), 1e-15);

        final double[] v4 = { -5, 0 };
        expected = -1;
        Assert.assertEquals(expected, MathArrays.cosAngle(v1, v4), 0);

        final double[] v5 = { -100, 100 };
        expected = 0;
        Assert.assertEquals(expected, MathArrays.cosAngle(v3, v5), 0);
    }

    @Test
    public void testCosAngle3D() {
        double expected;

        final double[] v1 = { 1, 1, 0 };
        expected = 1;
        Assert.assertEquals(expected, MathArrays.cosAngle(v1, v1), 1e-15);

        final double[] v2 = { 1, 1, 1 };
        expected = Math.sqrt(2) / Math.sqrt(3);
        Assert.assertEquals(expected, MathArrays.cosAngle(v1, v2), 1e-15);
    }

    @Test
    public void testCosAngleExtreme() {
        double expected;

        final double tiny = 1e-200;
        final double[] v1 = { tiny, tiny };
        final double big = 1e200;
        final double[] v2 = { -big, -big };
        expected = -1;
        Assert.assertEquals(expected, MathArrays.cosAngle(v1, v2), 1e-15);

        final double[] v3 = { big, -big };
        expected = 0;
        Assert.assertEquals(expected, MathArrays.cosAngle(v1, v3), 1e-15);
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
        Assert.assertFalse(MathArrays.isMonotonic(new double[] { -15, -5.5, -1, -1, 2, 15 },
                                                  MathArrays.OrderDirection.INCREASING, true));
        Assert.assertTrue(MathArrays.isMonotonic(new double[] { -15, -5.5, -1, 0, 2, 15 },
                                                 MathArrays.OrderDirection.INCREASING, true));
        Assert.assertFalse(MathArrays.isMonotonic(new double[] { -15, -5.5, -1, -2, 2 },
                                                  MathArrays.OrderDirection.INCREASING, false));
        Assert.assertTrue(MathArrays.isMonotonic(new double[] { -15, -5.5, -1, -1, 2 },
                                                 MathArrays.OrderDirection.INCREASING, false));
        Assert.assertFalse(MathArrays.isMonotonic(new double[] { 3, 3, -5.5, -11, -27.5 },
                                                  MathArrays.OrderDirection.DECREASING, true));
        Assert.assertTrue(MathArrays.isMonotonic(new double[] { 3, 2, -5.5, -11, -27.5 },
                                                 MathArrays.OrderDirection.DECREASING, true));
        Assert.assertFalse(MathArrays.isMonotonic(new double[] { 3, -1, 0, -5.5, -11, -27.5 },
                                                  MathArrays.OrderDirection.DECREASING, false));
        Assert.assertTrue(MathArrays.isMonotonic(new double[] { 3, 0, 0, -5.5, -11, -27.5 },
                                                 MathArrays.OrderDirection.DECREASING, false));
    }

    @Test
    public void testIsMonotonicComparable() {
        Assert.assertFalse(MathArrays.isMonotonic(new Double[] { new Double(-15),
                                                                 new Double(-5.5),
                                                                 new Double(-1),
                                                                 new Double(-1),
                                                                 new Double(2),
                                                                 new Double(15) },
                MathArrays.OrderDirection.INCREASING, true));
        Assert.assertTrue(MathArrays.isMonotonic(new Double[] { new Double(-15),
                                                                new Double(-5.5),
                                                                new Double(-1),
                                                                new Double(0),
                                                                new Double(2),
                                                                new Double(15) },
                MathArrays.OrderDirection.INCREASING, true));
        Assert.assertFalse(MathArrays.isMonotonic(new Double[] { new Double(-15),
                                                                 new Double(-5.5),
                                                                 new Double(-1),
                                                                 new Double(-2),
                                                                 new Double(2) },
                MathArrays.OrderDirection.INCREASING, false));
        Assert.assertTrue(MathArrays.isMonotonic(new Double[] { new Double(-15),
                                                                new Double(-5.5),
                                                                new Double(-1),
                                                                new Double(-1),
                                                                new Double(2) },
                MathArrays.OrderDirection.INCREASING, false));
        Assert.assertFalse(MathArrays.isMonotonic(new Double[] { new Double(3),
                                                                 new Double(3),
                                                                 new Double(-5.5),
                                                                 new Double(-11),
                                                                 new Double(-27.5) },
                MathArrays.OrderDirection.DECREASING, true));
        Assert.assertTrue(MathArrays.isMonotonic(new Double[] { new Double(3),
                                                                new Double(2),
                                                                new Double(-5.5),
                                                                new Double(-11),
                                                                new Double(-27.5) },
                MathArrays.OrderDirection.DECREASING, true));
        Assert.assertFalse(MathArrays.isMonotonic(new Double[] { new Double(3),
                                                                 new Double(-1),
                                                                 new Double(0),
                                                                 new Double(-5.5),
                                                                 new Double(-11),
                                                                 new Double(-27.5) },
                MathArrays.OrderDirection.DECREASING, false));
        Assert.assertTrue(MathArrays.isMonotonic(new Double[] { new Double(3),
                                                                new Double(0),
                                                                new Double(0),
                                                                new Double(-5.5),
                                                                new Double(-11),
                                                                new Double(-27.5) },
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
        final double[] withoutNaN = { Double.NEGATIVE_INFINITY,
                                      -Double.MAX_VALUE,
                                      -1, 0,
                                      Double.MIN_VALUE,
                                      FastMath.ulp(1d),
                                      1, 3, 113, 4769,
                                      Double.MAX_VALUE,
                                      Double.POSITIVE_INFINITY };

        final double[] withNaN = { Double.NEGATIVE_INFINITY,
                                   -Double.MAX_VALUE,
                                   -1, 0,
                                   Double.MIN_VALUE,
                                   FastMath.ulp(1d),
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

    @Test(expected=DimensionMismatchException.class)
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
    public void testSortInPlace() {
        final double[] x1 = {2,   5,  -3, 1,  4};
        final double[] x2 = {4,  25,   9, 1, 16};
        final double[] x3 = {8, 125, -27, 1, 64};

        MathArrays.sortInPlace(x1, x2, x3);

        Assert.assertEquals(-3,  x1[0], FastMath.ulp(1d));
        Assert.assertEquals(9,   x2[0], FastMath.ulp(1d));
        Assert.assertEquals(-27, x3[0], FastMath.ulp(1d));

        Assert.assertEquals(1, x1[1], FastMath.ulp(1d));
        Assert.assertEquals(1, x2[1], FastMath.ulp(1d));
        Assert.assertEquals(1, x3[1], FastMath.ulp(1d));

        Assert.assertEquals(2, x1[2], FastMath.ulp(1d));
        Assert.assertEquals(4, x2[2], FastMath.ulp(1d));
        Assert.assertEquals(8, x3[2], FastMath.ulp(1d));

        Assert.assertEquals(4,  x1[3], FastMath.ulp(1d));
        Assert.assertEquals(16, x2[3], FastMath.ulp(1d));
        Assert.assertEquals(64, x3[3], FastMath.ulp(1d));

        Assert.assertEquals(5,   x1[4], FastMath.ulp(1d));
        Assert.assertEquals(25,  x2[4], FastMath.ulp(1d));
        Assert.assertEquals(125, x3[4], FastMath.ulp(1d));
    }

    @Test
    public void testSortInPlaceDecresasingOrder() {
        final double[] x1 = {2,   5,  -3, 1,  4};
        final double[] x2 = {4,  25,   9, 1, 16};
        final double[] x3 = {8, 125, -27, 1, 64};

        MathArrays.sortInPlace(x1,
                               MathArrays.OrderDirection.DECREASING,
                               x2, x3);

        Assert.assertEquals(-3,  x1[4], FastMath.ulp(1d));
        Assert.assertEquals(9,   x2[4], FastMath.ulp(1d));
        Assert.assertEquals(-27, x3[4], FastMath.ulp(1d));

        Assert.assertEquals(1, x1[3], FastMath.ulp(1d));
        Assert.assertEquals(1, x2[3], FastMath.ulp(1d));
        Assert.assertEquals(1, x3[3], FastMath.ulp(1d));

        Assert.assertEquals(2, x1[2], FastMath.ulp(1d));
        Assert.assertEquals(4, x2[2], FastMath.ulp(1d));
        Assert.assertEquals(8, x3[2], FastMath.ulp(1d));

        Assert.assertEquals(4,  x1[1], FastMath.ulp(1d));
        Assert.assertEquals(16, x2[1], FastMath.ulp(1d));
        Assert.assertEquals(64, x3[1], FastMath.ulp(1d));

        Assert.assertEquals(5,   x1[0], FastMath.ulp(1d));
        Assert.assertEquals(25,  x2[0], FastMath.ulp(1d));
        Assert.assertEquals(125, x3[0], FastMath.ulp(1d));
    }

    @Test
    /** Example in javadoc */
    public void testSortInPlaceExample() {
        final double[] x = {3, 1, 2};
        final double[] y = {1, 2, 3};
        final double[] z = {0, 5, 7};
        MathArrays.sortInPlace(x, y, z);
        final double[] sx = {1, 2, 3};
        final double[] sy = {2, 3, 1};
        final double[] sz = {5, 7, 0};
        Assert.assertTrue(Arrays.equals(sx, x));
        Assert.assertTrue(Arrays.equals(sy, y));
        Assert.assertTrue(Arrays.equals(sz, z));
    }

    @Test
    public void testSortInPlaceFailures() {
        final double[] nullArray = null;
        final double[] one = {1};
        final double[] two = {1, 2};
        final double[] onep = {2};
        try {
            MathArrays.sortInPlace(one, two);
            Assert.fail("Expecting DimensionMismatchException");
        } catch (DimensionMismatchException ex) {
            // expected
        }
        try {
            MathArrays.sortInPlace(one, nullArray);
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
        try {
            MathArrays.sortInPlace(one, onep, nullArray);
            Assert.fail("Expecting NullArgumentException");
        } catch (NullArgumentException ex) {
            // expected
        }
    }

    @Test
    public void testCopyOfInt() {
        final int[] source = { Integer.MIN_VALUE,
                               -1, 0, 1, 3, 113, 4769,
                               Integer.MAX_VALUE };
        final int[] dest = MathArrays.copyOf(source);

        Assert.assertEquals(dest.length, source.length);
        for (int i = 0; i < source.length; i++) {
            Assert.assertEquals(source[i], dest[i]);
        }
    }

    @Test
    public void testCopyOfInt2() {
        final int[] source = { Integer.MIN_VALUE,
                               -1, 0, 1, 3, 113, 4769,
                               Integer.MAX_VALUE };
        final int offset = 3;
        final int[] dest = MathArrays.copyOf(source, source.length - offset);

        Assert.assertEquals(dest.length, source.length - offset);
        for (int i = 0; i < source.length - offset; i++) {
            Assert.assertEquals(source[i], dest[i]);
        }
    }

    @Test
    public void testCopyOfInt3() {
        final int[] source = { Integer.MIN_VALUE,
                               -1, 0, 1, 3, 113, 4769,
                               Integer.MAX_VALUE };
        final int offset = 3;
        final int[] dest = MathArrays.copyOf(source, source.length + offset);

        Assert.assertEquals(dest.length, source.length + offset);
        for (int i = 0; i < source.length; i++) {
            Assert.assertEquals(source[i], dest[i]);
        }
        for (int i = source.length; i < source.length + offset; i++) {
            Assert.assertEquals(0, dest[i], 0);
        }
    }

    @Test
    public void testCopyOfDouble() {
        final double[] source = { Double.NEGATIVE_INFINITY,
                                  -Double.MAX_VALUE,
                                  -1, 0,
                                  Double.MIN_VALUE,
                                  FastMath.ulp(1d),
                                  1, 3, 113, 4769,
                                  Double.MAX_VALUE,
                                  Double.POSITIVE_INFINITY };
        final double[] dest = MathArrays.copyOf(source);

        Assert.assertEquals(dest.length, source.length);
        for (int i = 0; i < source.length; i++) {
            Assert.assertEquals(source[i], dest[i], 0);
        }
    }

    @Test
    public void testCopyOfDouble2() {
        final double[] source = { Double.NEGATIVE_INFINITY,
                                  -Double.MAX_VALUE,
                                  -1, 0,
                                  Double.MIN_VALUE,
                                  FastMath.ulp(1d),
                                  1, 3, 113, 4769,
                                  Double.MAX_VALUE,
                                  Double.POSITIVE_INFINITY };
        final int offset = 3;
        final double[] dest = MathArrays.copyOf(source, source.length - offset);

        Assert.assertEquals(dest.length, source.length - offset);
        for (int i = 0; i < source.length - offset; i++) {
            Assert.assertEquals(source[i], dest[i], 0);
        }
    }

    @Test
    public void testCopyOfDouble3() {
        final double[] source = { Double.NEGATIVE_INFINITY,
                                  -Double.MAX_VALUE,
                                  -1, 0,
                                  Double.MIN_VALUE,
                                  FastMath.ulp(1d),
                                  1, 3, 113, 4769,
                                  Double.MAX_VALUE,
                                  Double.POSITIVE_INFINITY };
        final int offset = 3;
        final double[] dest = MathArrays.copyOf(source, source.length + offset);

        Assert.assertEquals(dest.length, source.length + offset);
        for (int i = 0; i < source.length; i++) {
            Assert.assertEquals(source[i], dest[i], 0);
        }
        for (int i = source.length; i < source.length + offset; i++) {
            Assert.assertEquals(0, dest[i], 0);
        }
    }

    @Test
    public void testCopyOfRange() {
        final double[] source = { Double.NEGATIVE_INFINITY,
                                  -Double.MAX_VALUE,
                                  -1, 0,
                                  Double.MIN_VALUE,
                                  FastMath.ulp(1d),
                                  1, 3, 113, 4769,
                                  Double.MAX_VALUE,
                                  Double.POSITIVE_INFINITY };
        final int from = 3;
        final int to = source.length + 14;
        final double[] dest = MathArrays.copyOfRange(source, from, to);

        Assert.assertEquals(dest.length, to - from);
        for (int i = from; i < source.length; i++) {
            Assert.assertEquals(source[i], dest[i - from], 0);
        }
        for (int i = source.length; i < dest.length; i++) {
            Assert.assertEquals(0, dest[i - from], 0);
        }
    }

    // MATH-1005
    @Test
    public void testLinearCombinationWithSingleElementArray() {
        final double[] a = { 1.23456789 };
        final double[] b = { 98765432.1 };

        Assert.assertEquals(a[0] * b[0], MathArrays.linearCombination(a, b), 0d);
    }

    @Test
    public void testLinearCombination1() {
        final double[] a = new double[] {
            -1321008684645961.0 / 268435456.0,
            -5774608829631843.0 / 268435456.0,
            -7645843051051357.0 / 8589934592.0
        };
        final double[] b = new double[] {
            -5712344449280879.0 / 2097152.0,
            -4550117129121957.0 / 2097152.0,
            8846951984510141.0 / 131072.0
        };

        final double abSumInline = MathArrays.linearCombination(a[0], b[0],
                                                                a[1], b[1],
                                                                a[2], b[2]);
        final double abSumArray = MathArrays.linearCombination(a, b);

        Assert.assertEquals(abSumInline, abSumArray, 0);
        Assert.assertEquals(-1.8551294182586248737720779899, abSumInline, 1.0e-15);

        final double naive = a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
        Assert.assertTrue(FastMath.abs(naive - abSumInline) > 1.5);

    }

    @Test
    public void testLinearCombination2() {
        // we compare accurate versus naive dot product implementations
        // on regular vectors (i.e. not extreme cases like in the previous test)
        Well1024a random = new Well1024a(553267312521321234l);

        for (int i = 0; i < 10000; ++i) {
            final double ux = 1e17 * random.nextDouble();
            final double uy = 1e17 * random.nextDouble();
            final double uz = 1e17 * random.nextDouble();
            final double vx = 1e17 * random.nextDouble();
            final double vy = 1e17 * random.nextDouble();
            final double vz = 1e17 * random.nextDouble();
            final double sInline = MathArrays.linearCombination(ux, vx,
                                                                uy, vy,
                                                                uz, vz);
            final double sArray = MathArrays.linearCombination(new double[] {ux, uy, uz},
                                                               new double[] {vx, vy, vz});
            Assert.assertEquals(sInline, sArray, 0);
        }
    }

    @Test
    public void testLinearCombinationHuge() {
        int scale = 971;
        final double[] a = new double[] {
                                         -1321008684645961.0 / 268435456.0,
                                         -5774608829631843.0 / 268435456.0,
                                         -7645843051051357.0 / 8589934592.0
                                     };
        final double[] b = new double[] {
                                         -5712344449280879.0 / 2097152.0,
                                         -4550117129121957.0 / 2097152.0,
                                          8846951984510141.0 / 131072.0
                                     };

        double[] scaledA = new double[a.length];
        double[] scaledB = new double[b.length];
        for (int i = 0; i < scaledA.length; ++i) {
            scaledA[i] = FastMath.scalb(a[i], -scale);
            scaledB[i] = FastMath.scalb(b[i], +scale);
        }
        final double abSumInline = MathArrays.linearCombination(scaledA[0], scaledB[0],
                                                                scaledA[1], scaledB[1],
                                                                scaledA[2], scaledB[2]);
        final double abSumArray = MathArrays.linearCombination(scaledA, scaledB);

        Assert.assertEquals(abSumInline, abSumArray, 0);
        Assert.assertEquals(-1.8551294182586248737720779899, abSumInline, 1.0e-15);

        final double naive = scaledA[0] * scaledB[0] + scaledA[1] * scaledB[1] + scaledA[2] * scaledB[2];
        Assert.assertTrue(FastMath.abs(naive - abSumInline) > 1.5);

    }

    @Test
    public void testLinearCombinationInfinite() {
        final double[][] a = new double[][] {
            { 1, 2, 3, 4},
            { 1, Double.POSITIVE_INFINITY, 3, 4},
            { 1, 2, Double.POSITIVE_INFINITY, 4},
            { 1, Double.POSITIVE_INFINITY, 3, Double.NEGATIVE_INFINITY},
            { 1, 2, 3, 4},
            { 1, 2, 3, 4},
            { 1, 2, 3, 4},
            { 1, 2, 3, 4}
        };
        final double[][] b = new double[][] {
            { 1, -2, 3, 4},
            { 1, -2, 3, 4},
            { 1, -2, 3, 4},
            { 1, -2, 3, 4},
            { 1, Double.POSITIVE_INFINITY, 3, 4},
            { 1, -2, Double.POSITIVE_INFINITY, 4},
            { 1, Double.POSITIVE_INFINITY, 3, Double.NEGATIVE_INFINITY},
            { Double.NaN, -2, 3, 4}
        };

        Assert.assertEquals(-3,
                            MathArrays.linearCombination(a[0][0], b[0][0],
                                                         a[0][1], b[0][1]),
                            1.0e-10);
        Assert.assertEquals(6,
                            MathArrays.linearCombination(a[0][0], b[0][0],
                                                         a[0][1], b[0][1],
                                                         a[0][2], b[0][2]),
                            1.0e-10);
        Assert.assertEquals(22,
                            MathArrays.linearCombination(a[0][0], b[0][0],
                                                         a[0][1], b[0][1],
                                                         a[0][2], b[0][2],
                                                         a[0][3], b[0][3]),
                            1.0e-10);
        Assert.assertEquals(22, MathArrays.linearCombination(a[0], b[0]), 1.0e-10);

        Assert.assertEquals(Double.NEGATIVE_INFINITY,
                            MathArrays.linearCombination(a[1][0], b[1][0],
                                                         a[1][1], b[1][1]),
                            1.0e-10);
        Assert.assertEquals(Double.NEGATIVE_INFINITY,
                            MathArrays.linearCombination(a[1][0], b[1][0],
                                                         a[1][1], b[1][1],
                                                         a[1][2], b[1][2]),
                            1.0e-10);
        Assert.assertEquals(Double.NEGATIVE_INFINITY,
                            MathArrays.linearCombination(a[1][0], b[1][0],
                                                         a[1][1], b[1][1],
                                                         a[1][2], b[1][2],
                                                         a[1][3], b[1][3]),
                            1.0e-10);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, MathArrays.linearCombination(a[1], b[1]), 1.0e-10);

        Assert.assertEquals(-3,
                            MathArrays.linearCombination(a[2][0], b[2][0],
                                                         a[2][1], b[2][1]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY,
                            MathArrays.linearCombination(a[2][0], b[2][0],
                                                         a[2][1], b[2][1],
                                                         a[2][2], b[2][2]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY,
                            MathArrays.linearCombination(a[2][0], b[2][0],
                                                         a[2][1], b[2][1],
                                                         a[2][2], b[2][2],
                                                         a[2][3], b[2][3]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY, MathArrays.linearCombination(a[2], b[2]), 1.0e-10);

        Assert.assertEquals(Double.NEGATIVE_INFINITY,
                            MathArrays.linearCombination(a[3][0], b[3][0],
                                                         a[3][1], b[3][1]),
                            1.0e-10);
        Assert.assertEquals(Double.NEGATIVE_INFINITY,
                            MathArrays.linearCombination(a[3][0], b[3][0],
                                                         a[3][1], b[3][1],
                                                         a[3][2], b[3][2]),
                            1.0e-10);
        Assert.assertEquals(Double.NEGATIVE_INFINITY,
                            MathArrays.linearCombination(a[3][0], b[3][0],
                                                         a[3][1], b[3][1],
                                                         a[3][2], b[3][2],
                                                         a[3][3], b[3][3]),
                            1.0e-10);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, MathArrays.linearCombination(a[3], b[3]), 1.0e-10);

        Assert.assertEquals(Double.POSITIVE_INFINITY,
                            MathArrays.linearCombination(a[4][0], b[4][0],
                                                         a[4][1], b[4][1]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY,
                            MathArrays.linearCombination(a[4][0], b[4][0],
                                                         a[4][1], b[4][1],
                                                         a[4][2], b[4][2]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY,
                            MathArrays.linearCombination(a[4][0], b[4][0],
                                                         a[4][1], b[4][1],
                                                         a[4][2], b[4][2],
                                                         a[4][3], b[4][3]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY, MathArrays.linearCombination(a[4], b[4]), 1.0e-10);

        Assert.assertEquals(-3,
                            MathArrays.linearCombination(a[5][0], b[5][0],
                                                         a[5][1], b[5][1]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY,
                            MathArrays.linearCombination(a[5][0], b[5][0],
                                                         a[5][1], b[5][1],
                                                         a[5][2], b[5][2]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY,
                            MathArrays.linearCombination(a[5][0], b[5][0],
                                                         a[5][1], b[5][1],
                                                         a[5][2], b[5][2],
                                                         a[5][3], b[5][3]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY, MathArrays.linearCombination(a[5], b[5]), 1.0e-10);

        Assert.assertEquals(Double.POSITIVE_INFINITY,
                            MathArrays.linearCombination(a[6][0], b[6][0],
                                                         a[6][1], b[6][1]),
                            1.0e-10);
        Assert.assertEquals(Double.POSITIVE_INFINITY,
                            MathArrays.linearCombination(a[6][0], b[6][0],
                                                         a[6][1], b[6][1],
                                                         a[6][2], b[6][2]),
                            1.0e-10);
        Assert.assertTrue(Double.isNaN(MathArrays.linearCombination(a[6][0], b[6][0],
                                                                    a[6][1], b[6][1],
                                                                    a[6][2], b[6][2],
                                                                    a[6][3], b[6][3])));
        Assert.assertTrue(Double.isNaN(MathArrays.linearCombination(a[6], b[6])));

        Assert.assertTrue(Double.isNaN(MathArrays.linearCombination(a[7][0], b[7][0],
                                                                    a[7][1], b[7][1])));
        Assert.assertTrue(Double.isNaN(MathArrays.linearCombination(a[7][0], b[7][0],
                                                                    a[7][1], b[7][1],
                                                                    a[7][2], b[7][2])));
        Assert.assertTrue(Double.isNaN(MathArrays.linearCombination(a[7][0], b[7][0],
                                                                    a[7][1], b[7][1],
                                                                    a[7][2], b[7][2],
                                                                    a[7][3], b[7][3])));
        Assert.assertTrue(Double.isNaN(MathArrays.linearCombination(a[7], b[7])));
    }

    @Test
    public void testArrayEquals() {
        Assert.assertFalse(MathArrays.equals(new double[] { 1d }, null));
        Assert.assertFalse(MathArrays.equals(null, new double[] { 1d }));
        Assert.assertTrue(MathArrays.equals((double[]) null, (double[]) null));

        Assert.assertFalse(MathArrays.equals(new double[] { 1d }, new double[0]));
        Assert.assertTrue(MathArrays.equals(new double[] { 1d }, new double[] { 1d }));
        Assert.assertTrue(MathArrays.equals(new double[] { Double.POSITIVE_INFINITY,
                                                           Double.NEGATIVE_INFINITY, 1d, 0d },
                                            new double[] { Double.POSITIVE_INFINITY,
                                                           Double.NEGATIVE_INFINITY, 1d, 0d }));
        Assert.assertFalse(MathArrays.equals(new double[] { Double.NaN },
                                             new double[] { Double.NaN }));
        Assert.assertFalse(MathArrays.equals(new double[] { Double.POSITIVE_INFINITY },
                                             new double[] { Double.NEGATIVE_INFINITY }));
        Assert.assertFalse(MathArrays.equals(new double[] { 1d },
                                             new double[] { FastMath.nextAfter(FastMath.nextAfter(1d, 2d), 2d) }));

    }

    @Test
    public void testArrayEqualsIncludingNaN() {
        Assert.assertFalse(MathArrays.equalsIncludingNaN(new double[] { 1d }, null));
        Assert.assertFalse(MathArrays.equalsIncludingNaN(null, new double[] { 1d }));
        Assert.assertTrue(MathArrays.equalsIncludingNaN((double[]) null, (double[]) null));

        Assert.assertFalse(MathArrays.equalsIncludingNaN(new double[] { 1d }, new double[0]));
        Assert.assertTrue(MathArrays.equalsIncludingNaN(new double[] { 1d }, new double[] { 1d }));
        Assert.assertTrue(MathArrays.equalsIncludingNaN(new double[] { Double.NaN, Double.POSITIVE_INFINITY,
                                                                       Double.NEGATIVE_INFINITY, 1d, 0d },
                                                        new double[] { Double.NaN, Double.POSITIVE_INFINITY,
                                                                       Double.NEGATIVE_INFINITY, 1d, 0d }));
        Assert.assertFalse(MathArrays.equalsIncludingNaN(new double[] { Double.POSITIVE_INFINITY },
                                                         new double[] { Double.NEGATIVE_INFINITY }));
        Assert.assertFalse(MathArrays.equalsIncludingNaN(new double[] { 1d },
                                                         new double[] { FastMath.nextAfter(FastMath.nextAfter(1d, 2d), 2d) }));
    }

    @Test
    public void testNormalizeArray() {
        double[] testValues1 = new double[] {1, 1, 2};
        TestUtils.assertEquals( new double[] {.25, .25, .5},
                                MathArrays.normalizeArray(testValues1, 1),
                                Double.MIN_VALUE);

        double[] testValues2 = new double[] {-1, -1, 1};
        TestUtils.assertEquals( new double[] {1, 1, -1},
                                MathArrays.normalizeArray(testValues2, 1),
                                Double.MIN_VALUE);

        // Ignore NaNs
        double[] testValues3 = new double[] {-1, -1, Double.NaN, 1, Double.NaN};
        TestUtils.assertEquals( new double[] {1, 1,Double.NaN, -1, Double.NaN},
                                MathArrays.normalizeArray(testValues3, 1),
                                Double.MIN_VALUE);

        // Zero sum -> MathArithmeticException
        double[] zeroSum = new double[] {-1, 1};
        try {
            MathArrays.normalizeArray(zeroSum, 1);
            Assert.fail("expecting MathArithmeticException");
        } catch (MathArithmeticException ex) {}

        // Infinite elements -> MathArithmeticException
        double[] hasInf = new double[] {1, 2, 1, Double.NEGATIVE_INFINITY};
        try {
            MathArrays.normalizeArray(hasInf, 1);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {}

        // Infinite target -> MathIllegalArgumentException
        try {
            MathArrays.normalizeArray(testValues1, Double.POSITIVE_INFINITY);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {}

        // NaN target -> MathIllegalArgumentException
        try {
            MathArrays.normalizeArray(testValues1, Double.NaN);
            Assert.fail("expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {}
    }

    @Test
    public void testConvolve() {
        /* Test Case (obtained via SciPy)
         * x=[1.2,-1.8,1.4]
         * h=[1,0.8,0.5,0.3]
         * convolve(x,h) -> array([ 1.2 , -0.84,  0.56,  0.58,  0.16,  0.42])
         */
        double[] x1 = { 1.2, -1.8, 1.4 };
        double[] h1 = { 1, 0.8, 0.5, 0.3 };
        double[] y1 = { 1.2, -0.84, 0.56, 0.58, 0.16, 0.42 };
        double tolerance = 1e-13;

        double[] yActual = MathArrays.convolve(x1, h1);
        Assert.assertArrayEquals(y1, yActual, tolerance);

        double[] x2 = { 1, 2, 3 };
        double[] h2 = { 0, 1, 0.5 };
        double[] y2 = { 0, 1, 2.5, 4, 1.5 };

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
    public void testShuffleTail() {
        final int[] orig = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        final int[] list = orig.clone();
        final int start = 4;
        MathArrays.shuffle(list, start, MathArrays.Position.TAIL, new Well1024a(7654321L));

        // Ensure that all entries below index "start" did not move.
        for (int i = 0; i < start; i++) {
            Assert.assertEquals(orig[i], list[i]);
        }

        // Ensure that at least one entry has moved.
        boolean ok = false;
        for (int i = start; i < orig.length - 1; i++) {
            if (orig[i] != list[i]) {
                ok = true;
                break;
            }
        }
        Assert.assertTrue(ok);
    }

    @Test
    public void testShuffleHead() {
        final int[] orig = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        final int[] list = orig.clone();
        final int start = 4;
        MathArrays.shuffle(list, start, MathArrays.Position.HEAD, new Well1024a(1234567L));

        // Ensure that all entries above index "start" did not move.
        for (int i = start + 1; i < orig.length; i++) {
            Assert.assertEquals(orig[i], list[i]);
        }

        // Ensure that at least one entry has moved.
        boolean ok = false;
        for (int i = 0; i <= start; i++) {
            if (orig[i] != list[i]) {
                ok = true;
                break;
            }
        }
        Assert.assertTrue(ok);
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
        Assert.assertFalse(MathArrays.verifyValues(singletonArray, 0, 0));
        Assert.assertFalse(MathArrays.verifyValues(testArray, 0, 0));
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
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
            // expected
        }
        try {
            MathArrays.verifyValues(testArray, nullArray, 0, 1);  // null weights array
            Assert.fail("Expecting MathIllegalArgumentException");
        } catch (MathIllegalArgumentException ex) {
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
    public void testConcatentateSingle() {
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

    @Test(expected=NullPointerException.class)
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
        final double [] x = {0, Double.NEGATIVE_INFINITY, 3, Double.NEGATIVE_INFINITY,
            3, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};
        final double[] u = {Double.POSITIVE_INFINITY, 3, 0, Double.NEGATIVE_INFINITY};
        Assert.assertArrayEquals(u , MathArrays.unique(x), 0);
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

    @Test(expected=NullPointerException.class)
    public void testUniqueNullArgument() {
        MathArrays.unique(null);
    }
}
