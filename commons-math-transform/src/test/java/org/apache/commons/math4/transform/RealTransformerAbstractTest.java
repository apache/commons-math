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
package org.apache.commons.math4.transform;

import org.junit.Assert;
import org.junit.Test;

import java.util.function.DoubleUnaryOperator;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

/**
 * Abstract test for classes implementing the {@link RealTransform} interface.
 * This abstract test handles the automatic generation of random data of various
 * sizes. For each generated data array, actual values (returned by the
 * transformer to be tested) are compared to expected values, returned by the
 * {@link #transform(double[], boolean)} (to be implemented by the user: a naive method may
 * be used). Methods are also provided to test that invalid parameters throw the
 * expected exceptions.
 *
 * @since 3.0
 */
public abstract class RealTransformerAbstractTest {
    /** RNG. */
    private static final UniformRandomProvider RNG = RandomSource.MWC_256.create();

    /**
     * Returns a new instance of the {@link RealTransform} to be tested.
     *
     * @param inverse Whether to apply the inverse transform.
     * @return a the transformer to be tested
     */
    abstract RealTransform createRealTransformer(boolean inverse);

    /**
     * Returns an invalid data size. Transforms with this data size should
     * trigger a {@link IllegalArgumentException}.
     *
     * @param i the index of the invalid data size ({@code 0 <= i <}
     * {@link #getNumberOfInvalidDataSizes()}
     * @return an invalid data size
     */
    abstract int getInvalidDataSize(int i);

    /**
     * Returns the total number of invalid data sizes to be tested. If data
     * array of any
     * size can be handled by the {@link RealTransform} to be tested, this
     * method should return {@code 0}.
     *
     * @return the total number of invalid data sizes
     */
    abstract int getNumberOfInvalidDataSizes();

    /**
     * Returns the total number of valid data sizes to be tested.
     *
     * @return the total number of valid data sizes
     */
    abstract int getNumberOfValidDataSizes();

    /**
     * Returns the expected relative accuracy for data arrays of size
     * {@code getValidDataSize(i)}.
     *
     * @param i the index of the valid data size
     * @return the expected relative accuracy
     */
    abstract double getRelativeTolerance(int i);

    /**
     * Returns a valid data size. This method allows for data arrays of various
     * sizes to be automatically tested (by allowing multiple values of the
     * specified index).
     *
     * @param i the index of the valid data size ({@code 0 <= i <}
     * {@link #getNumberOfValidDataSizes()}
     * @return a valid data size
     */
    abstract int getValidDataSize(int i);

    /**
     * Returns a function for the accuracy check of
     * {@link RealTransform#apply(DoubleUnaryOperator, double, double, int)}.
     * This function should be valid. In other words, none of the above methods
     * should throw an exception when passed this function.
     *
     * @return a valid function
     */
    abstract DoubleUnaryOperator getValidFunction();

    /**
     * Returns a sampling lower bound for the accuracy check of
     * {@link RealTransform#apply(DoubleUnaryOperator, double, double, int)}.
     * This lower bound should be valid. In other words, none of the above
     * methods should throw an exception when passed this bound.
     *
     * @return a valid lower bound
     */
    abstract double getValidLowerBound();

    /**
     * Returns a sampling upper bound for the accuracy check of
     * {@link RealTransform#apply(DoubleUnaryOperator, double, double, int)}.
     * This upper bound should be valid. In other words, none of the above
     * methods should throw an exception when passed this bound.
     *
     * @return a valid bound
     */
    abstract double getValidUpperBound();

    /**
     * Returns the expected transform of the specified real data array.
     *
     * @param x Data to be transformed.
     * @param type Whether to perform the inverse) transform.
     * @return the expected transform.
     */
    abstract double[] transform(double[] x, boolean type);

    // Check of preconditions.

    /**
     * {@link RealTransform#apply(double[])} should throw a
     * {@link IllegalArgumentException} if data size is invalid.
     */
    @Test
    public void testTransformRealInvalidDataSize() {
        for (int i = 0; i < getNumberOfInvalidDataSizes(); i++) {
            final int n = getInvalidDataSize(i);
            for (boolean type : new boolean[] {true, false}) {
                try {
                    final RealTransform transformer = createRealTransformer(type);
                    transformer.apply(createRealData(n));
                    Assert.fail(type + ", " + n);
                } catch (IllegalArgumentException e) {
                    // Expected: do nothing
                }
            }
        }
    }

    /**
     * {@link RealTransform#apply(DoubleUnaryOperator, double, double, int)}
     * should throw {@link IllegalArgumentException} if number of samples is
     * invalid.
     */
    @Test
    public void testTransformFunctionInvalidDataSize() {
        final DoubleUnaryOperator f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        for (int i = 0; i < getNumberOfInvalidDataSizes(); i++) {
            final int n = getInvalidDataSize(i);
            for (boolean type : new boolean[] {true, false}) {
                try {
                    final RealTransform transformer = createRealTransformer(type);
                    transformer.apply(f, a, b, n);
                    Assert.fail(type + ", " + n);
                } catch (IllegalArgumentException e) {
                    // Expected: do nothing
                }
            }
        }
    }

    /**
     * {@link RealTransform#apply(DoubleUnaryOperator, double, double, int)}
     * should throw {@link IllegalArgumentException} if number of samples
     * is not strictly positive.
     */
    @Test
    public void testTransformFunctionNotStrictlyPositiveNumberOfSamples() {
        final DoubleUnaryOperator f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            for (boolean type : new boolean[] {true, false}) {
                try {
                    final RealTransform transformer = createRealTransformer(type);
                    transformer.apply(f, a, b, -n);
                    Assert.fail(type + ", " + (-n));
                } catch (IllegalArgumentException e) {
                    // Expected: do nothing
                }
            }
        }
    }

    /**
     * {@link RealTransform#apply(DoubleUnaryOperator, double, double, int)}
     * should throw {@link IllegalArgumentException} if sampling bounds are
     * not correctly ordered.
     */
    @Test
    public void testTransformFunctionInvalidBounds() {
        final DoubleUnaryOperator f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            for (boolean type : new boolean[] {true, false}) {
                try {
                    final RealTransform transformer = createRealTransformer(type);
                    transformer.apply(f, b, a, n);
                    Assert.fail(type + ", " + b + ", " + a);
                } catch (IllegalArgumentException e) {
                    // Expected: do nothing
                }
            }
        }
    }

    // Accuracy tests of transform of valid data.

    /**
     * Accuracy check of {@link RealTransform#apply(double[])}.
     * For each valid data size returned by
     * {@link #getValidDataSize(int) getValidDataSize(i)},
     * a random data array is generated with
     * {@link #createRealData(int) createRealData(i)}. The actual
     * transform is computed and compared to the expected transform, return by
     * {@link #transform(double[], boolean)}. Actual and expected values
     * should be equal to within the relative error returned by
     * {@link #getRelativeTolerance(int) getRelativeTolerance(i)}.
     */
    @Test
    public void testTransformReal() {
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            final double tol = getRelativeTolerance(i);
            for (boolean type : new boolean[] {true, false}) {
                doTestTransformReal(n, tol, type);
            }
        }
    }

    /**
     * Accuracy check of
     * {@link RealTransform#apply(DoubleUnaryOperator, double, double, int)}.
     * For each valid data size returned by
     * {@link #getValidDataSize(int) getValidDataSize(i)},
     * the {@link org.apache.commons.math3.analysis.UnivariateFunction UnivariateFunction}
     * returned by {@link #getValidFunction()} is
     * sampled. The actual transform is computed and compared to the expected
     * transform, return by {@link #transform(double[], boolean)}. Actual
     * and expected values should be equal to within the relative error returned
     * by {@link #getRelativeTolerance(int) getRelativeTolerance(i)}.
     */
    @Test
    public void testTransformFunction() {
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            final double tol = getRelativeTolerance(i);
            for (boolean type : new boolean[] {true, false}) {
                doTestTransformFunction(n, tol, type);
            }
        }
    }

    // Utility methods.

    /**
     * Returns a random array of doubles.
     *
     * @param n the size of the array to be returned
     * @return a random array of specified size
     */
    double[] createRealData(final int n) {
        final double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = 2 * RNG.nextDouble() - 1;
        }
        return data;
    }

    // Actual tests.

    private void doTestTransformReal(final int n,
                                     final double tol,
                                     final boolean type) {
        final RealTransform transformer = createRealTransformer(type);
        final double[] x = createRealData(n);
        final double[] expected = transform(x, type);
        final double[] actual = transformer.apply(x);
        for (int i = 0; i < n; i++) {
            final String msg = String.format("%d, %d", n, i);
            final double delta = tol * Math.abs(expected[i]);
            Assert.assertEquals(msg, expected[i], actual[i], delta);
        }
    }

    private void doTestTransformFunction(final int n,
                                         final double tol,
                                         final boolean type) {
        final RealTransform transformer = createRealTransformer(type);
        final DoubleUnaryOperator f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        final double[] x = createRealData(n);
        for (int i = 0; i < n; i++) {
            final double t = a + i * (b - a) / n;
            x[i] = f.applyAsDouble(t);
        }
        final double[] expected = transform(x, type);
        final double[] actual = transformer.apply(f, a, b, n);
        for (int i = 0; i < n; i++) {
            final String msg = String.format("%d, %d", n, i);
            final double delta = tol * Math.abs(expected[i]);
            Assert.assertEquals(msg, expected[i], actual[i], delta);
        }
    }
}
