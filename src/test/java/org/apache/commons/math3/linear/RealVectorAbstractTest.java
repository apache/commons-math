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
package org.apache.commons.math3.linear;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.function.Abs;
import org.apache.commons.math3.analysis.function.Acos;
import org.apache.commons.math3.analysis.function.Asin;
import org.apache.commons.math3.analysis.function.Atan;
import org.apache.commons.math3.analysis.function.Cbrt;
import org.apache.commons.math3.analysis.function.Ceil;
import org.apache.commons.math3.analysis.function.Cos;
import org.apache.commons.math3.analysis.function.Cosh;
import org.apache.commons.math3.analysis.function.Exp;
import org.apache.commons.math3.analysis.function.Expm1;
import org.apache.commons.math3.analysis.function.Floor;
import org.apache.commons.math3.analysis.function.Inverse;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.function.Log10;
import org.apache.commons.math3.analysis.function.Log1p;
import org.apache.commons.math3.analysis.function.Power;
import org.apache.commons.math3.analysis.function.Rint;
import org.apache.commons.math3.analysis.function.Signum;
import org.apache.commons.math3.analysis.function.Sin;
import org.apache.commons.math3.analysis.function.Sinh;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.analysis.function.Tan;
import org.apache.commons.math3.analysis.function.Tanh;
import org.apache.commons.math3.analysis.function.Ulp;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public abstract class RealVectorAbstractTest {

    private enum BinaryOperation {
        ADD, SUB, MUL, DIV
    };

    /**
     * <p>
     * This is an attempt at covering most particular cases of combining two
     * values. Here {@code x} is the value returned by
     * {@link #getPreferredEntryValue()}, while {@code y} and {@code z} are two
     * "normal" values.
     * </p>
     * <ol>
     *   <li>
     *     Addition: the following cases should be covered
     *     <ul>
     *       <li>{@code (2 * x) + (-x)}</li>
     *       <li>{@code (-x) + 2 * x}</li>
     *       <li>{@code x + y}</li>
     *       <li>{@code y + x}</li>
     *       <li>{@code y + z}</li>
     *       <li>{@code y + (x - y)}</li>
     *       <li>{@code (y - x) + x}</li>
     *     </ul>
     *     The values to be considered are:
     *     {@code x, y, z, 2 * x, -x, x - y, y - x}.
     *   </li>
     *   <li>
     *     Subtraction: the following cases should be covered
     *     <ul>
     *       <li>{@code (2 * x) - x}</li>
     *       <li>{@code x - y}</li>
     *       <li>{@code y - x}</li>
     *       <li>{@code y - z}</li>
     *       <li>{@code y - (y - x)}</li>
     *       <li>{@code (y + x) - y}</li>
     *     </ul>
     *     The values to be considered are: {@code x, y, z, x + y, y - x}.
     *   </li>
     *   <li>
     *     Multiplication
     *     <ul>
     *       <li>{@code (x * x) * (1 / x)}</li>
     *       <li>{@code (1 / x) * (x * x)}</li>
     *       <li>{@code x * y}</li>
     *       <li>{@code y * x}</li>
     *       <li>{@code y * z}</li>
     *     </ul>
     *     The values to be considered are: {@code x, y, z, 1 / x, x * x}.
     *   </li>
     *   <li>
     *     Division
     *     <ul>
     *       <li>{@code (x * x) / x}</li>
     *       <li>{@code x / y}</li>
     *       <li>{@code y / x}</li>
     *       <li>{@code y / z}</li>
     *     </ul>
     *     The values to be considered are: {@code x, y, z, x * x}.
     *   </li>
     * </ol>
     * Also to be considered {@code NaN}, {@code POSITIVE_INFINITY},
     * {@code NEGATIVE_INFINITY}, {@code +0.0}, {@code -0.0}.
     */
    private final double[] values;

    /**
     * Creates a new instance of {@link RealVector}, with specified entries.
     * The returned vector must be of the type currently tested. It should be
     * noted that some tests assume that no references to the specified
     * {@code double[]} are kept in the returned object: if necessary, defensive
     * copy of this array should be made.
     *
     * @param data the entries of the vector to be created
     * @return a new {@link RealVector} of the type to be tested
     */
    public abstract RealVector create(double[] data);

    /**
     * Creates a new instance of {@link RealVector}, with specified entries.
     * The type of the returned vector must be different from the type currently
     * tested. It should be noted that some tests assume that no references to
     * the specified {@code double[]} are kept in the returned object: if
     * necessary, defensive copy of this array should be made.
     *
     * @param data the entries of the vector to be created
     * @return a new {@link RealVector} of an alien type
     */
    public abstract RealVector createAlien(double[] data);

    /**
     * Returns a preferred value of the entries, to be tested specifically. Some
     * implementations of {@link RealVector} (e.g. {@link OpenMapRealVector}) do
     * not store specific values of entries. In order to ensure that all tests
     * take into account this specific value, some entries of the vectors to be
     * tested are deliberately set to the value returned by the present method.
     * The default implementation returns {@code 0.0}.
     *
     * @return a value which <em>should</em> be present in all vectors to be
     * tested
     */
    public double getPreferredEntryValue() {
        return 0.0;
    }

    /** verifies that two vectors are close (sup norm) */
    protected void assertClose(String msg, double[] m, double[] n,
            double tolerance) {
        if (m.length != n.length) {
            Assert.fail("vectors have different lengths");
        }
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(msg + " " +  i + " elements differ", m[i],n[i],tolerance);
        }
    }

    protected double[][] ma1 = {{1d, 2d, 3d}, {4d, 5d, 6d}, {7d, 8d, 9d}};
    protected double[] vec1 = {1d, 2d, 3d};
    protected double[] vec2 = {4d, 5d, 6d};
    protected double[] vec3 = {7d, 8d, 9d};
    protected double[] vec4 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[] vec5 = { -4d, 0d, 3d, 1d, -6d, 3d};
    protected double[] vec_null = {0d, 0d, 0d};
    protected Double[] dvec1 = {1d, 2d, 3d, 4d, 5d, 6d, 7d, 8d, 9d};
    protected double[][] mat1 = {{1d, 2d, 3d}, {4d, 5d, 6d},{ 7d, 8d, 9d}};

    /**
     * Data which can be used to create a specific vector. The array is
     * interspersed with the value returned by
     * {@link #getPreferredEntryValue()}.
     */
    private final double[] data1;


    /**
     * Data which can be used to create a specific vector. The array is
     * interspersed with the value returned by
     * {@link #getPreferredEntryValue()}.
     */
    private final double[] data2;

    public RealVectorAbstractTest() {
        final double x = getPreferredEntryValue();
        data1 = new double[] {x, 1d, 2d, x, x};
        data2 = new double[] {x, x, 3d, x, 4d, x};
        /*
         * Make sure that x, y, z are three different values. Also, x is the
         * preferred value (e.g. the value which is not stored in sparse
         * implementations).
         */
        final double y = x + 1d;
        final double z = y + 1d;

        values =
            new double[] {
                Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
                0d, -0d, x, y, z, 2 * x, -x, 1 / x, x * x, x + y, x - y, y - x
            };
    }

    // tolerances
    protected double entryTolerance = 10E-16;
    protected double normTolerance = 10E-14;

    @Test
    public void testGetDimension() {
        Assert.assertEquals(data1.length, create(data1).getDimension());
    }

    @Test
    public void testGetEntry() {
        final RealVector v = create(data1);
        for (int i = 0; i < data1.length; i++) {
            Assert.assertEquals("entry " + i, data1[i], v.getEntry(i), 0d);
        }
    }

    @Test(expected=OutOfRangeException.class)
    public void testGetEntryInvalidIndex1() {
        create(data1).getEntry(-1);
    }

    @Test(expected=OutOfRangeException.class)
    public void testGetEntryInvalidIndex2() {
        create(data1).getEntry(data1.length);
    }

    @Test
    public void testSetEntry() {
        final double[] expected = MathArrays.copyOf(data1);
        final RealVector actual = create(data1);

        /*
         * Try setting to any value.
         */
        for (int i = 0; i < data1.length; i++) {
            final double oldValue = data1[i];
            final double newValue = oldValue + 1d;
            expected[i] = newValue;
            actual.setEntry(i, newValue);
            TestUtils.assertEquals("while setting entry #" + i, expected,
                actual, 0d);
            expected[i] = oldValue;
            actual.setEntry(i, oldValue);
        }

        /*
         * Try setting to the preferred value.
         */
        final double x = getPreferredEntryValue();
        for (int i = 0; i < data1.length; i++) {
            final double oldValue = data1[i];
            final double newValue = x;
            expected[i] = newValue;
            actual.setEntry(i, newValue);
            TestUtils.assertEquals("while setting entry #" + i, expected,
                actual, 0d);
            expected[i] = oldValue;
            actual.setEntry(i, oldValue);
        }
    }

    @Test(expected=OutOfRangeException.class)
    public void testSetEntryInvalidIndex1() {
        create(data1).setEntry(-1, getPreferredEntryValue());
    }

    @Test(expected=OutOfRangeException.class)
    public void testSetEntryInvalidIndex2() {
        create(data1).setEntry(data1.length, getPreferredEntryValue());
    }

    @Test
    public void testAddToEntry() {
        final double[] expected = MathArrays.copyOf(data1);
        final RealVector actual = create(data1);

        /*
         * Try adding any value.
         */
        double increment = 1d;
        for (int i = 0; i < data1.length; i++) {
            final double oldValue = data1[i];
            expected[i] += increment;
            actual.addToEntry(i, increment);
            TestUtils.assertEquals("while incrementing entry #" + i, expected,
                actual, 0d);
            expected[i] = oldValue;
            actual.setEntry(i, oldValue);
        }

        /*
         * Try incrementing so that result is equal to preferred value.
         */
        final double x = getPreferredEntryValue();
        for (int i = 0; i < data1.length; i++) {
            final double oldValue = data1[i];
            increment = x - oldValue;
            expected[i] = x;
            actual.addToEntry(i, increment);
            TestUtils.assertEquals("while incrementing entry #" + i, expected,
                actual, 0d);
            expected[i] = oldValue;
            actual.setEntry(i, oldValue);
        }
    }

    @Test(expected=OutOfRangeException.class)
    public void testAddToEntryInvalidIndex1() {
        create(data1).addToEntry(-1, getPreferredEntryValue());
    }

    @Test(expected=OutOfRangeException.class)
    public void testAddToEntryInvalidIndex2() {
        create(data1).addToEntry(data1.length, getPreferredEntryValue());
    }

    private void doTestAppendVector(final String message, final RealVector v1,
        final RealVector v2, final double delta) {

        final int n1 = v1.getDimension();
        final int n2 = v2.getDimension();
        final RealVector v = v1.append(v2);
        Assert.assertEquals(message, n1 + n2, v.getDimension());
        for (int i = 0; i < n1; i++) {
            final String msg = message + ", entry #" + i;
            Assert.assertEquals(msg, v1.getEntry(i), v.getEntry(i), delta);
        }
        for (int i = 0; i < n2; i++) {
            final String msg = message + ", entry #" + (n1 + i);
            Assert.assertEquals(msg, v2.getEntry(i), v.getEntry(n1 + i), delta);
        }
    }

    @Test
    public void testAppendVector() {
        doTestAppendVector("same type", create(data1), create(data2), 0d);
        doTestAppendVector("mixed types", create(data1), createAlien(data2), 0d);
    }

    private void doTestAppendScalar(final String message, final RealVector v,
        final double d, final double delta) {

        final int n = v.getDimension();
        final RealVector w = v.append(d);
        Assert.assertEquals(message, n + 1, w.getDimension());
        for (int i = 0; i < n; i++) {
            final String msg = message + ", entry #" + i;
            Assert.assertEquals(msg, v.getEntry(i), w.getEntry(i), delta);
        }
        final String msg = message + ", entry #" + n;
        Assert.assertEquals(msg, d, w.getEntry(n), delta);
    }

    @Test
    public void testAppendScalar() {

        doTestAppendScalar("", create(data1), 1d, 0d);
        doTestAppendScalar("", create(data1), getPreferredEntryValue(), 0d);
    }

    @Test
    public void testGetSubVector() {
        final double x = getPreferredEntryValue();
        final double[] data = {x, x, x, 1d, x, 2d, x, x, 3d, x, x, x, 4d, x, x, x};
        final int index = 1;
        final int n = data.length - 5;
        final RealVector actual = create(data).getSubVector(index, n);
        final double[] expected = new double[n];
        System.arraycopy(data, index, expected, 0, n);
        TestUtils.assertEquals("", expected, actual, 0d);
    }

    @Test(expected = OutOfRangeException.class)
    public void testGetSubVectorInvalidIndex1() {
        final int n = 10;
        create(new double[n]).getSubVector(-1, 2);
    }

    @Test(expected = OutOfRangeException.class)
    public void testGetSubVectorInvalidIndex2() {
        final int n = 10;
        create(new double[n]).getSubVector(n, 2);
    }

    @Test(expected = OutOfRangeException.class)
    public void testGetSubVectorInvalidIndex3() {
        final int n = 10;
        create(new double[n]).getSubVector(0, n + 1);
    }

    @Test(expected = NotPositiveException.class)
    public void testGetSubVectorInvalidIndex4() {
        final int n = 10;
        create(new double[n]).getSubVector(3, -2);
    }

    @Test
    public void testSetSubVectorSameType() {
        final double x = getPreferredEntryValue();
        final double[] expected = {x, x, x, 1d, x, 2d, x, x, 3d, x, x, x, 4d, x, x, x};
        final double[] sub = {5d, x, 6d, 7d, 8d};
        final RealVector actual = create(expected);
        final int index = 2;
        actual.setSubVector(index, create(sub));

        for (int i = 0; i < sub.length; i++){
            expected[index + i] = sub[i];
        }
        TestUtils.assertEquals("", expected, actual, 0d);
    }

    @Test
    public void testSetSubVectorMixedType() {
        final double x = getPreferredEntryValue();
        final double[] expected = {x, x, x, 1d, x, 2d, x, x, 3d, x, x, x, 4d, x, x, x};
        final double[] sub = {5d, x, 6d, 7d, 8d};
        final RealVector actual = create(expected);
        final int index = 2;
        actual.setSubVector(index, createAlien(sub));

        for (int i = 0; i < sub.length; i++){
            expected[index + i] = sub[i];
        }
        TestUtils.assertEquals("", expected, actual, 0d);
    }

    @Test(expected = OutOfRangeException.class)
    public void testSetSubVectorInvalidIndex1() {
        create(new double[10]).setSubVector(-1, create(new double[2]));
    }

    @Test(expected = OutOfRangeException.class)
    public void testSetSubVectorInvalidIndex2() {
        create(new double[10]).setSubVector(10, create(new double[2]));
    }

    @Test(expected = OutOfRangeException.class)
    public void testSetSubVectorInvalidIndex3() {
        create(new double[10]).setSubVector(9, create(new double[2]));
    }

    @Test
    public void testIsNaN() {
        final RealVector v = create(new double[] {0, 1, 2});

        Assert.assertFalse(v.isNaN());
        v.setEntry(1, Double.NaN);
        Assert.assertTrue(v.isNaN());
    }

    @Test
    public void testIsInfinite() {
        final RealVector v = create(new double[] { 0, 1, 2 });

        Assert.assertFalse(v.isInfinite());
        v.setEntry(0, Double.POSITIVE_INFINITY);
        Assert.assertTrue(v.isInfinite());
        v.setEntry(1, Double.NaN);
        Assert.assertFalse(v.isInfinite());
    }

    private void doTestEbeBinaryOperation(final BinaryOperation op, final boolean mixed) {
        final double[] data1 = new double[values.length * values.length];
        final double[] data2 = new double[values.length * values.length];
        int k = 0;
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values.length; j++) {
                data1[k] = values[i];
                data2[k] = values[j];
                ++k;
            }
        }
        final RealVector v1 = create(data1);
        final RealVector v2 = mixed ? createAlien(data2) : create(data2);
        final RealVector actual;
        switch (op) {
            case ADD:
                actual = v1.add(v2);
                break;
            case SUB:
                actual = v1.subtract(v2);
                break;
            case MUL:
                actual = v1.ebeMultiply(v2);
                break;
            case DIV:
                actual = v1.ebeDivide(v2);
                break;
            default:
                throw new AssertionError("unexpected value");
        }
        final double[] expected = new double[data1.length];
        for (int i = 0; i < expected.length; i++) {
            switch (op) {
                case ADD:
                    expected[i] = data1[i] + data2[i];
                    break;
                case SUB:
                    expected[i] = data1[i] - data2[i];
                    break;
                case MUL:
                    expected[i] = data1[i] * data2[i];
                    break;
                case DIV:
                    expected[i] = data1[i] / data2[i];
                    break;
                default:
                    throw new AssertionError("unexpected value");
            }
        }
        for (int i = 0; i < expected.length; i++) {
            final String msg = "entry #"+i+", left = "+data1[i]+", right = " + data2[i];
            Assert.assertEquals(msg, expected[i], actual.getEntry(i), 0.0);
        }
    }

    private void doTestEbeBinaryOperationDimensionMismatch(final BinaryOperation op) {
        final int n = 10;
        switch (op) {
            case ADD:
                create(new double[n]).add(create(new double[n + 1]));
                break;
            case SUB:
                create(new double[n]).subtract(create(new double[n + 1]));
                break;
            case MUL:
                create(new double[n]).ebeMultiply(create(new double[n + 1]));
                break;
            case DIV:
                create(new double[n]).ebeDivide(create(new double[n + 1]));
                break;
            default:
                throw new AssertionError("unexpected value");
        }
    }

    @Test
    public void testAddSameType() {
        doTestEbeBinaryOperation(BinaryOperation.ADD, false);
    }

    @Test
    public void testAddMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.ADD, true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testAddDimensionMismatch() {
        doTestEbeBinaryOperationDimensionMismatch(BinaryOperation.ADD);
    }

    @Test
    public void testSubtractSameType() {
        doTestEbeBinaryOperation(BinaryOperation.SUB, false);
    }

    @Test
    public void testSubtractMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.SUB, true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testSubtractDimensionMismatch() {
        doTestEbeBinaryOperationDimensionMismatch(BinaryOperation.SUB);
    }

    @Ignore("ebeMultiply(RealVector) is known to be faulty (MATH-803) and is deprecated.")
    @Test
    public void testEbeMultiplySameType() {
        doTestEbeBinaryOperation(BinaryOperation.MUL, false);
    }

    @Ignore("ebeMultiply(RealVector) is known to be faulty (MATH-803) and is deprecated.")
    @Test
    public void testEbeMultiplyMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.MUL, true);
    }

    @Ignore("ebeMultiply(RealVector) is known to be faulty (MATH-803) and is deprecated.")
    @Test(expected = DimensionMismatchException.class)
    public void testEbeMultiplyDimensionMismatch() {
        doTestEbeBinaryOperationDimensionMismatch(BinaryOperation.MUL);
    }

    @Ignore("ebeDivide(RealVector) is known to be faulty (MATH-803) and is deprecated.")
    @Test
    public void testEbeDivideSameType() {
        doTestEbeBinaryOperation(BinaryOperation.DIV, false);
    }

    @Ignore("ebeDivide(RealVector) is known to be faulty (MATH-803) and is deprecated.")
    @Test
    public void testEbeDivideMixedTypes() {
        doTestEbeBinaryOperation(BinaryOperation.DIV, true);
    }

    @Ignore("ebeDivide(RealVector) is known to be faulty (MATH-803) and is deprecated.")
    @Test(expected = DimensionMismatchException.class)
    public void testEbeDivideDimensionMismatch() {
        doTestEbeBinaryOperationDimensionMismatch(BinaryOperation.DIV);
    }

    private void doTestGetDistance(final boolean mixed) {
        final double x = getPreferredEntryValue();
        final double[] data1 = new double[] { x, x, 1d, x, 2d, x, x, 3d, x };
        final double[] data2 = new double[] { 4d, x, x, 5d, 6d, 7d, x, x, 8d };
        final RealVector v1 = create(data1);
        final RealVector v2;
        if (mixed) {
            v2 = createAlien(data2);
        } else {
            v2 = create(data2);
        }
        final double actual = v1.getDistance(v2);
        double expected = 0d;
        for (int i = 0; i < data1.length; i++) {
            final double delta = data2[i] - data1[i];
            expected += delta * delta;
        }
        expected = FastMath.sqrt(expected);
        Assert.assertEquals("", expected, actual, 0d);
    }

    @Test
    public void testGetDistanceSameType() {
        doTestGetDistance(false);
    }

    @Test
    public void testGetDistanceMixedTypes() {
        doTestGetDistance(true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testGetDistanceDimensionMismatch() {
        create(new double[4]).getDistance(createAlien(new double[5]));
    }

    @Test
    public void testGetNorm() {
        final double x = getPreferredEntryValue();
        final double[] data = new double[] { x, x, 1d, x, 2d, x, x, 3d, x };
        final RealVector v = create(data);
        final double actual = v.getNorm();
        double expected = 0d;
        for (int i = 0; i < data.length; i++) {
            expected += data[i] * data[i];
        }
        expected = FastMath.sqrt(expected);
        Assert.assertEquals("", expected, actual, 0d);
    }

    private void doTestGetL1Distance(final boolean mixed) {
        final double x = getPreferredEntryValue();
        final double[] data1 = new double[] { x, x, 1d, x, 2d, x, x, 3d, x };
        final double[] data2 = new double[] { 4d, x, x, 5d, 6d, 7d, x, x, 8d };
        final RealVector v1 = create(data1);
        final RealVector v2;
        if (mixed) {
            v2 = createAlien(data2);
        } else {
            v2 = create(data2);
        }
        final double actual = v1.getL1Distance(v2);
        double expected = 0d;
        for (int i = 0; i < data1.length; i++) {
            final double delta = data2[i] - data1[i];
            expected += FastMath.abs(delta);
        }
        Assert.assertEquals("", expected, actual, 0d);
    }

    @Test
    public void testGetL1DistanceSameType() {
        doTestGetL1Distance(false);
    }

    @Test
    public void testGetL1DistanceMixedTypes() {
        doTestGetL1Distance(true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testGetL1DistanceDimensionMismatch() {
        create(new double[4]).getL1Distance(createAlien(new double[5]));
    }

    @Test
    public void testGetL1Norm() {
        final double x = getPreferredEntryValue();
        final double[] data = new double[] { x, x, 1d, x, 2d, x, x, 3d, x };
        final RealVector v = create(data);
        final double actual = v.getL1Norm();
        double expected = 0d;
        for (int i = 0; i < data.length; i++) {
            expected += FastMath.abs(data[i]);
        }
        Assert.assertEquals("", expected, actual, 0d);

    }

    private void doTestGetLInfDistance(final boolean mixed) {
        final double x = getPreferredEntryValue();
        final double[] data1 = new double[] { x, x, 1d, x, 2d, x, x, 3d, x };
        final double[] data2 = new double[] { 4d, x, x, 5d, 6d, 7d, x, x, 8d };
        final RealVector v1 = create(data1);
        final RealVector v2;
        if (mixed) {
            v2 = createAlien(data2);
        } else {
            v2 = create(data2);
        }
        final double actual = v1.getLInfDistance(v2);
        double expected = 0d;
        for (int i = 0; i < data1.length; i++) {
            final double delta = data2[i] - data1[i];
            expected = FastMath.max(expected, FastMath.abs(delta));
        }
        Assert.assertEquals("", expected, actual, 0d);
    }

    @Test
    public void testGetLInfDistanceSameType() {
        doTestGetLInfDistance(false);
    }

    @Test
    public void testGetLInfDistanceMixedTypes() {
        doTestGetLInfDistance(true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testGetLInfDistanceDimensionMismatch() {
        create(new double[4]).getLInfDistance(createAlien(new double[5]));
    }

    @Test
    public void testGetLInfNorm() {
        final double x = getPreferredEntryValue();
        final double[] data = new double[] { x, x, 1d, x, 2d, x, x, 3d, x };
        final RealVector v = create(data);
        final double actual = v.getLInfNorm();
        double expected = 0d;
        for (int i = 0; i < data.length; i++) {
            expected = FastMath.max(expected, FastMath.abs(data[i]));
        }
        Assert.assertEquals("", expected, actual, 0d);

    }

    private void doTestMapBinaryOperation(final BinaryOperation op, final boolean inPlace) {
        final double[] expected = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            final double d = values[i];
            for (int j = 0; j < expected.length; j++) {
                switch (op) {
                    case ADD:
                        expected[j] = values[j] + d;
                        break;
                    case SUB:
                        expected[j] = values[j] - d;
                        break;
                    case MUL:
                        expected[j] = values[j] * d;
                        break;
                    case DIV:
                        expected[j] = values[j] / d;
                        break;
                    default:
                        throw new AssertionError("unexpected value");
                }
            }
            final RealVector v = create(values);
            final RealVector actual;
            if (inPlace) {
                switch (op) {
                    case ADD:
                        actual = v.mapAddToSelf(d);
                        break;
                    case SUB:
                        actual = v.mapSubtractToSelf(d);
                        break;
                    case MUL:
                        actual = v.mapMultiplyToSelf(d);
                        break;
                    case DIV:
                        actual = v.mapDivideToSelf(d);
                        break;
                    default:
                        throw new AssertionError("unexpected value");
                }
            } else {
                switch (op) {
                    case ADD:
                        actual = v.mapAdd(d);
                        break;
                    case SUB:
                        actual = v.mapSubtract(d);
                        break;
                    case MUL:
                        actual = v.mapMultiply(d);
                        break;
                    case DIV:
                        actual = v.mapDivide(d);
                        break;
                    default:
                        throw new AssertionError("unexpected value");
                }
            }
            TestUtils.assertEquals(Double.toString(d), expected, actual, 0d);
        }
    }

    @Test
    public void testMapAdd() {
        doTestMapBinaryOperation(BinaryOperation.ADD, false);
    }

    @Test
    public void testMapAddToSelf() {
        doTestMapBinaryOperation(BinaryOperation.ADD, true);
    }

    @Test
    public void testMapSubtract() {
        doTestMapBinaryOperation(BinaryOperation.SUB, false);
    }

    @Test
    public void testMapSubtractToSelf() {
        doTestMapBinaryOperation(BinaryOperation.SUB, true);
    }

    @Test
    public void testMapMultiply() {
        doTestMapBinaryOperation(BinaryOperation.MUL, false);
    }

    @Test
    public void testMapMultiplyToSelf() {
        doTestMapBinaryOperation(BinaryOperation.MUL, true);
    }

    @Test
    public void testMapDivide() {
        doTestMapBinaryOperation(BinaryOperation.DIV, false);
    }

    @Test
    public void testMapDivideToSelf() {
        doTestMapBinaryOperation(BinaryOperation.DIV, true);
    }

    private void doTestMapFunction(final UnivariateFunction f,
        final boolean inPlace) {
        final double[] data = new double[values.length + 6];
        System.arraycopy(values, 0, data, 0, values.length);
        data[values.length + 0] = 0.5 * FastMath.PI;
        data[values.length + 1] = -0.5 * FastMath.PI;
        data[values.length + 2] = FastMath.E;
        data[values.length + 3] = -FastMath.E;
        data[values.length + 4] = 1.0;
        data[values.length + 5] = -1.0;
        final double[] expected = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            expected[i] = f.value(data[i]);
        }
        final RealVector v = create(data);
        final RealVector actual;
        if (inPlace) {
            actual = v.mapToSelf(f);
            Assert.assertSame(v, actual);
        } else {
            actual = v.map(f);
        }
        TestUtils.assertEquals(f.getClass().getSimpleName(), expected, actual, 1E-16);
    }

    private UnivariateFunction[] createFunctions() {
        return new UnivariateFunction[] {
            new Power(2.0), new Exp(), new Expm1(), new Log(), new Log10(),
            new Log1p(), new Cosh(), new Sinh(), new Tanh(), new Cos(),
            new Sin(), new Tan(), new Acos(), new Asin(), new Atan(),
            new Inverse(), new Abs(), new Sqrt(), new Cbrt(), new Ceil(),
            new Floor(), new Rint(), new Signum(), new Ulp()
        };
    }

    @Test
    public void testMap() {
        final UnivariateFunction[] functions = createFunctions();
        for (UnivariateFunction f : functions) {
            doTestMapFunction(f, false);
        }
    }

    @Test
    public void testMapToSelf() {
        final UnivariateFunction[] functions = createFunctions();
        for (UnivariateFunction f : functions) {
            doTestMapFunction(f, true);
        }
    }

    private void doTestOuterProduct(final boolean mixed) {
        final double[] dataU = values;
        final RealVector u = create(dataU);
        final double[] dataV = new double[values.length + 3];
        System.arraycopy(values, 0, dataV, 0, values.length);
        dataV[values.length] = 1d;
        dataV[values.length] = -2d;
        dataV[values.length] = 3d;
        final RealVector v;
        if (mixed) {
            v = createAlien(dataV);
        } else {
            v = create(dataV);
        }
        final RealMatrix uv = u.outerProduct(v);
        Assert.assertEquals("number of rows", dataU.length, uv
            .getRowDimension());
        Assert.assertEquals("number of columns", dataV.length, uv
            .getColumnDimension());
        for (int i = 0; i < dataU.length; i++) {
            for (int j = 0; j < dataV.length; j++) {
                final double expected = dataU[i] * dataV[j];
                final double actual = uv.getEntry(i, j);
                Assert.assertEquals("[" + i + "][" + j + "]", expected, actual,
                    0d);
            }
        }
    }

    @Test
    public void testOuterProductSameType() {
        doTestOuterProduct(false);
    }

    @Test
    public void testOuterProductMixedTypes() {
        doTestOuterProduct(true);
    }

    @Test
    public void testSet() {
        for (int i = 0; i < values.length; i++) {
            final double expected = values[i];
            final RealVector v = create(values);
            v.set(expected);
            for (int j = 0; j < values.length; j++) {
                Assert.assertEquals("entry #" + j, expected, v.getEntry(j), 0);
            }
        }
    }

    @Test
    public void testToArray() {
        final double[] data = create(values).toArray();
        Assert.assertNotSame(values, data);
        for (int i = 0; i < values.length; i++) {
            Assert.assertEquals("entry #" + i, values[i], data[i], 0);
        }
    }

    private void doTestUnitVector(final boolean inPlace) {
        final double x = getPreferredEntryValue();
        final double[] data = {
            x, 1d, x, x, 2d, x, x, x, 3d, x, x, x, x
        };
        double norm = 0d;
        for (int i = 0; i < data.length; i++) {
            norm += data[i] * data[i];
        }
        norm = FastMath.sqrt(norm);
        final double[] expected = new double[data.length];
        for (int i = 0; i < expected.length; i++) {
            expected[i] = data[i] / norm;
        }
        final RealVector v = create(data);
        final RealVector actual;
        if (inPlace) {
            v.unitize();
            actual = v;
        } else {
            actual = v.unitVector();
            Assert.assertNotSame(v, actual);
        }
        TestUtils.assertEquals("", expected, actual, 0d);
    }

    @Test
    public void testUnitVector() {
        doTestUnitVector(false);
    }

    @Test
    public void testUnitize() {
        doTestUnitVector(true);
    }

    private void doTestUnitVectorNullVector(final boolean inPlace) {
        final double[] data = {
            0d, 0d, 0d, 0d, 0d
        };
        if (inPlace) {
            create(data).unitize();
        } else {
            create(data).unitVector();
        }
    }

    @Test(expected=ArithmeticException.class)
    public void testUnitVectorNullVector() {
        doTestUnitVectorNullVector(false);
    }

    @Test(expected=ArithmeticException.class)
    public void testUnitizeNullVector() {
        doTestUnitVectorNullVector(true);
    }

    @Test
    public void testIterator() {
        final RealVector v = create(values);
        final Iterator<RealVector.Entry> it = v.iterator();
        for (int i = 0; i < values.length; i++) {
            Assert.assertTrue("entry #" + i, it.hasNext());
            final RealVector.Entry e = it.next();
            Assert.assertEquals("", i, e.getIndex());
            Assert.assertEquals("", values[i], e.getValue(), 0d);
            try {
                it.remove();
                Assert.fail("UnsupportedOperationException should have been thrown");
            } catch (UnsupportedOperationException exc) {
                // Expected behavior
            }
        }
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("NoSuchElementException should have been thrown");
        } catch (NoSuchElementException e) {
            // Expected behavior
        }
    }

    private void doTestCombine(final boolean inPlace, final boolean mixed) {
        final int n = values.length * values.length;
        final double[] data1 = new double[n];
        final double[] data2 = new double[n];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values.length; j++) {
                final int index = values.length * i + j;
                data1[index] = values[i];
                data2[index] = values[j];
            }
        }
        final RealVector v1 = create(data1);
        final RealVector v2 = mixed ? createAlien(data2) : create(data2);
        final double[] expected = new double[n];
        for (int i = 0; i < values.length; i++) {
            final double a1 = values[i];
            for (int j = 0; j < values.length; j++) {
                final double a2 = values[j];
                for (int k = 0; k < n; k++) {
                    expected[k] = a1 * data1[k] + a2 * data2[k];
                }
                final RealVector actual;
                if (inPlace) {
                    final RealVector v1bis = v1.copy();
                    actual = v1bis.combineToSelf(a1, a2, v2);
                    Assert.assertSame(v1bis, actual);
                } else {
                    actual = v1.combine(a1, a2, v2);
                }
                TestUtils.assertEquals("a1 = " + a1 + ", a2 = " + a2, expected,
                    actual, 0.);
            }
        }
    }

    private void doTestCombineDimensionMismatch(final boolean inPlace, final boolean mixed) {
        final RealVector v1 = create(new double[10]);
        final RealVector v2;
        if (mixed) {
            v2 = createAlien(new double[15]);
        } else {
            v2 = create(new double[15]);
        }
        if (inPlace) {
            v1.combineToSelf(1.0, 1.0, v2);
        } else {
            v1.combine(1.0, 1.0, v2);
        }
    }

    @Test
    public void testCombineSameType() {
        doTestCombine(false, false);
    }

    @Test
    public void testCombineMixedTypes() {
        doTestCombine(false, true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testCombineDimensionMismatchSameType() {
        doTestCombineDimensionMismatch(false, false);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testCombineDimensionMismatchMixedTypes() {
        doTestCombineDimensionMismatch(false, true);
    }

    @Test
    public void testCombineToSelfSameType() {
        doTestCombine(true, false);
    }

    @Test
    public void testCombineToSelfMixedTypes() {
        doTestCombine(true, true);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testCombineToSelfDimensionMismatchSameType() {
        doTestCombineDimensionMismatch(true, false);
    }

    @Test(expected = DimensionMismatchException.class)
    public void testCombineToSelfDimensionMismatchMixedTypes() {
        doTestCombineDimensionMismatch(true, true);
    }

    @Test
    public void testDataInOut() {
        final RealVector v1 = create(vec1);
        final RealVector v2 = create(vec2);
        final RealVector v4 = create(vec4);
        final RealVector v2_t = createAlien(vec2);

        final RealVector v_set1 = v1.copy();
        v_set1.setEntry(1, 11.0);
        Assert.assertEquals("testData is 11.0 ", 11.0, v_set1.getEntry(1), 0);
        try {
            v_set1.setEntry(3, 11.0);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector v_set2 = v4.copy();
        v_set2.setSubVector(3, v1);
        Assert.assertEquals("testData is 1.0 ", 1.0, v_set2.getEntry(3), 0);
        Assert.assertEquals("testData is 7.0 ", 7.0, v_set2.getEntry(6), 0);
        try {
            v_set2.setSubVector(7, v1);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector v_set4 = v4.copy();
        v_set4.setSubVector(3, v2_t);
        Assert.assertEquals("testData is 1.0 ", 4.0, v_set4.getEntry(3), 0);
        Assert.assertEquals("testData is 7.0 ", 7.0, v_set4.getEntry(6), 0);
        try {
            v_set4.setSubVector(7, v2_t);
            Assert.fail("OutOfRangeException expected");
        } catch (OutOfRangeException ex) {
            // expected behavior
        }

        final RealVector vout10 = v1.copy();
        final RealVector vout10_2 = v1.copy();
        Assert.assertEquals(vout10, vout10_2);
        vout10_2.setEntry(0, 1.1);
        Assert.assertNotSame(vout10, vout10_2);
    }

    @Test
    public void testBasicFunctions() {
        final RealVector v1 = create(vec1);
        final RealVector v2 = create(vec2);
        final RealVector v5 = create(vec5);
        final RealVector v_null = create(vec_null);

        final RealVector v2_t = createAlien(vec2);

        // octave dot(v1,v2)
        double dot = v1.dotProduct(v2);
        Assert.assertEquals("compare val ", 32d, dot, normTolerance);

        // octave dot(v1,v2_t)
        double dot_2 = v1.dotProduct(v2_t);
        Assert.assertEquals("compare val ", 32d, dot_2, normTolerance);

        RealVector v_projection = v1.projection(v2);
        double[] result_projection = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection.toArray(), result_projection, normTolerance);

        RealVector v_projection_2 = v1.projection(v2_t);
        double[] result_projection_2 = {1.662337662337662, 2.0779220779220777, 2.493506493506493};
        assertClose("compare vect", v_projection_2.toArray(), result_projection_2, normTolerance);
    }

    @Test
    public void testMisc() {
        RealVector v1 = create(vec1);
        RealVector v4 = create(vec4);
        RealVector v4_2 = create(vec4);

        String out1 = v1.toString();
        Assert.assertTrue("some output ",  out1.length()!=0);
        try {
            v1.checkVectorDimensions(2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

       try {
            v1.checkVectorDimensions(v4);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }

        try {
            v1.checkVectorDimensions(v4_2);
            Assert.fail("MathIllegalArgumentException expected");
        } catch (MathIllegalArgumentException ex) {
            // expected behavior
        }
    }

    @Test
    public void testPredicates() {
        final RealVector v = create(new double[] { 0, 1, 2 });

        v.setEntry(0, 0);
        Assert.assertEquals(v, create(new double[] { 0, 1, 2 }));
        Assert.assertNotSame(v, create(new double[] { 0, 1, 2 + FastMath.ulp(2)}));
        Assert.assertNotSame(v, create(new double[] { 0, 1, 2, 3 }));

        Assert.assertTrue(v.equals(v));
        Assert.assertTrue(v.equals(v.copy()));
        Assert.assertFalse(v.equals(null));
        Assert.assertFalse(v.equals(v.getSubVector(0, v.getDimension() - 1)));
        Assert.assertTrue(v.equals(v.getSubVector(0, v.getDimension())));
    }

    @Test
    public void testSerial()  {
        RealVector v = create(new double[] { 0, 1, 2 });
        Assert.assertEquals(v,TestUtils.serializeAndRecover(v));
    }

    @Test
    public void testMinMax() {
        final RealVector v1 = create(new double[] {0, -6, 4, 12, 7});
        Assert.assertEquals(1, v1.getMinIndex());
        Assert.assertEquals(-6, v1.getMinValue(), 1.0e-12);
        Assert.assertEquals(3, v1.getMaxIndex());
        Assert.assertEquals(12, v1.getMaxValue(), 1.0e-12);
        final RealVector v2 = create(new double[] {Double.NaN, 3, Double.NaN, -2});
        Assert.assertEquals(3, v2.getMinIndex());
        Assert.assertEquals(-2, v2.getMinValue(), 1.0e-12);
        Assert.assertEquals(1, v2.getMaxIndex());
        Assert.assertEquals(3, v2.getMaxValue(), 1.0e-12);
        final RealVector v3 = create(new double[] {Double.NaN, Double.NaN});
        Assert.assertEquals(-1, v3.getMinIndex());
        Assert.assertTrue(Double.isNaN(v3.getMinValue()));
        Assert.assertEquals(-1, v3.getMaxIndex());
        Assert.assertTrue(Double.isNaN(v3.getMaxValue()));
        final RealVector v4 = create(new double[0]);
        Assert.assertEquals(-1, v4.getMinIndex());
        Assert.assertTrue(Double.isNaN(v4.getMinValue()));
        Assert.assertEquals(-1, v4.getMaxIndex());
        Assert.assertTrue(Double.isNaN(v4.getMaxValue()));
    }

    @Test
    public void testCosine() {
        final RealVector v = create(new double[] {1, 0, 0});

        double[] wData = new double[] {1, 1, 0};
        RealVector w = create(wData);
        Assert.assertEquals(FastMath.sqrt(2) / 2, v.cosine(w), normTolerance);

        wData = new double[] {1, 0, 0};
        w = create(wData);
        Assert.assertEquals(1, v.cosine(w), normTolerance);

        wData = new double[] {0, 1, 0};
        w = create(wData);
        Assert.assertEquals(0, v.cosine(w), 0);

        wData = new double[] {-1, 0, 0};
        w = create(wData);
        Assert.assertEquals(-1, v.cosine(w), normTolerance);
    }

    @Test(expected=MathArithmeticException.class)
    public void testCosinePrecondition1() {
        final RealVector v = create(new double[] {0, 0, 0});
        final RealVector w = create(new double[] {1, 0, 0});
        v.cosine(w);
    }

    @Test(expected=MathArithmeticException.class)
    public void testCosinePrecondition2() {
        final RealVector v = create(new double[] {0, 0, 0});
        final RealVector w = create(new double[] {1, 0, 0});
        w.cosine(v);
    }

    @Test(expected=DimensionMismatchException.class)
    public void testCosinePrecondition3() {
        final RealVector v = create(new double[] {1, 2, 3});
        final RealVector w = create(new double[] {1, 2, 3, 4});
        v.cosine(w);
    }

    /*
     * TESTS OF THE VISITOR PATTERN
     */

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            private int expectedIndex;

            public void visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            public void visit(int index, double value) {
                // Do nothing
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
            }
        };
        try {
            v.walkInDefaultOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInDefaultOrderPreservingVisitor3() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            private int expectedIndex;

            public void visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public void visit(final int actualIndex, final double actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            public double end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor);
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {

            public void visit(int index, double value) {
                // Do nothing
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
            }
        };
        try {
            v.walkInOptimizedOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInOptimizedOrderPreservingVisitor3() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorPreservingVisitor visitor;
        visitor = new RealVectorPreservingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public void visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                visited[actualIndex] = true;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            public double end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            private int expectedIndex;

            public double visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                expectedIndex = 0;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            public double visit(int index, double value) {
                return 0.0;
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
            }
        };
        try {
            v.walkInDefaultOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInDefaultOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInDefaultOrderChangingVisitor3() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            private int expectedIndex;

            public double visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(expectedIndex, actualIndex);
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                ++expectedIndex;
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                expectedIndex = expectedStart;
            }

            public double end() {
                return 0.0;
            }
        };
        v.walkInDefaultOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }

    /** The whole vector is visited. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor1() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public double visit(final int actualIndex, final double actualValue) {
                visited[actualIndex] = true;
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(0, actualStart);
                Assert.assertEquals(data.length - 1, actualEnd);
                Arrays.fill(visited, false);
            }

            public double end() {
                for (int i = 0; i < data.length; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor);
        for (int i = 0; i < data.length; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }

    /** Visiting an invalid subvector. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor2() {
        final RealVector v = create(new double[5]);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {

            public double visit(int index, double value) {
                return 0.0;
            }

            public void start(int dimension, int start, int end) {
                // Do nothing
            }

            public double end() {
                return 0.0;
            }
        };
        try {
            v.walkInOptimizedOrder(visitor, -1, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 5, 4);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, -1);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 0, 5);
            Assert.fail();
        } catch (OutOfRangeException e) {
            // Expected behavior
        }
        try {
            v.walkInOptimizedOrder(visitor, 4, 0);
            Assert.fail();
        } catch (NumberIsTooSmallException e) {
            // Expected behavior
        }
    }

    /** Visiting a valid subvector. */
    @Test
    public void testWalkInOptimizedOrderChangingVisitor3() {
        final double[] data = new double[] {
            0d, 1d, 0d, 0d, 2d, 0d, 0d, 0d, 3d
        };
        final int expectedStart = 2;
        final int expectedEnd = 7;
        final RealVector v = create(data);
        final RealVectorChangingVisitor visitor;
        visitor = new RealVectorChangingVisitor() {
            private final boolean[] visited = new boolean[data.length];

            public double visit(final int actualIndex, final double actualValue) {
                Assert.assertEquals(Integer.toString(actualIndex),
                                    data[actualIndex], actualValue, 0d);
                visited[actualIndex] = true;
                return actualIndex + actualValue;
            }

            public void start(final int actualSize, final int actualStart,
                              final int actualEnd) {
                Assert.assertEquals(data.length, actualSize);
                Assert.assertEquals(expectedStart, actualStart);
                Assert.assertEquals(expectedEnd, actualEnd);
                Arrays.fill(visited, true);
            }

            public double end() {
                for (int i = expectedStart; i <= expectedEnd; i++) {
                    Assert.assertTrue("entry " + i + "has not been visited",
                                      visited[i]);
                }
                return 0.0;
            }
        };
        v.walkInOptimizedOrder(visitor, expectedStart, expectedEnd);
        for (int i = expectedStart; i <= expectedEnd; i++) {
            Assert.assertEquals("entry " + i, i + data[i], v.getEntry(i), 0.0);
        }
    }
}
