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
package org.apache.commons.math.transform;

import java.util.Random;

import org.apache.commons.math.analysis.UnivariateFunction;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.util.FastMath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Abstract test for classes implementing the {@link RealTransformer} interface.
 * This abstract test handles the automatic generation of random data of various
 * sizes. For each generated data array, actual values (returned by the
 * transformer to be tested) are compared to expected values, returned by the
 * {@link #transform(double[], boolean)} (to be implemented by the user: a naive
 * method may be used). Methods are also provided to test that invalid parameters
 * throw the expected exceptions.
 *
 * @since 3.0
 * @version $Id$
 */
public abstract class RealTransformerAbstractTest {

    /** The common seed of all random number generators used in this test. */
    private final static long SEED = 20110119L;

    /**
     * Returns a new instance of the {@link RealTransformer} to be tested.
     *
     * @return a the transformer to be tested
     */
    abstract RealTransformer createRealTransformer();

    /**
     * Returns an invalid data size. Transforms with this data size should
     * trigger a {@link MathIllegalArgumentException}.
     *
     * @param i the index of the invalid data size ({@code 0 <= i <}
     * {@link #getNumberOfInvalidDataSizes()}
     * @return an invalid data size
     */
    abstract int getInvalidDataSize(int i);

    /**
     * Returns the total number of invalid data sizes to be tested. If data
     * array of any
     * size can be handled by the {@link RealTransformer} to be tested, this
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
     * {@link RealTransformer#transform(UnivariateFunction, double, double, int)}
     * and
     * {@link RealTransformer#inverseTransform(UnivariateFunction, double, double, int)}.
     * This function should be valid. In other words, none of the above methods
     * should throw an exception when passed this function.
     *
     * @return a valid function
     */
    abstract UnivariateFunction getValidFunction();

    /**
     * Returns a sampling lower bound for the accuracy check of
     * {@link RealTransformer#transform(UnivariateFunction, double, double, int)}
     * and
     * {@link RealTransformer#inverseTransform(UnivariateFunction, double, double, int)}.
     * This lower bound should be valid. In other words, none of the above
     * methods should throw an exception when passed this bound.
     *
     * @return a valid lower bound
     */
    abstract double getValidLowerBound();

    /**
     * Returns a sampling upper bound for the accuracy check of
     * {@link RealTransformer#transform(UnivariateFunction, double, double, int)}
     * and
     * {@link RealTransformer#inverseTransform(UnivariateFunction, double, double, int)}.
     * This upper bound should be valid. In other words, none of the above
     * methods should throw an exception when passed this bound.
     *
     * @return a valid bound
     */
    abstract double getValidUpperBound();

    /**
     * Returns the expected transform of the specified real data array.
     *
     * @param x the real data array to be transformed
     * @param forward {@code true} (resp. {@code false}) if the forward (resp.
     * inverse) transform is to be performed
     * @return the expected transform
     */
    abstract double[] transform(double[] x, boolean forward);

    /*
     * Check of preconditions.
     */

    /**
     * {@link RealTransformer#transform(double[])} should throw a
     * {@link MathIllegalArgumentException} if data size is invalid.
     */
    @Test
    public void testTransformRealInvalidDataSize() {
        final RealTransformer transformer = createRealTransformer();
        for (int i = 0; i < getNumberOfInvalidDataSizes(); i++) {
            final int n = getInvalidDataSize(i);
            try {
                transformer.transform(createRealData(n));
                Assert.fail(Integer.toString(n));
            } catch (MathIllegalArgumentException e) {
                // Expected: do nothing
            }
        }
    }

    /**
     * {@link RealTransformer#transform(UnivariateFunction, double, double, int)}
     * should throw a {@link MathIllegalArgumentException} if number of samples
     * is invalid.
     */
    @Test
    public void testTransformFunctionInvalidDataSize() {
        final RealTransformer transformer = createRealTransformer();
        final UnivariateFunction f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        for (int i = 0; i < getNumberOfInvalidDataSizes(); i++) {
            final int n = getInvalidDataSize(i);
            try {
                transformer.transform(f, a, b, n);
                Assert.fail(Integer.toString(n));
            } catch (MathIllegalArgumentException e) {
                // Expected: do nothing
            }
        }
    }

    /**
     * {@link RealTransformer#transform(UnivariateFunction, double, double, int)}
     * should throw a {@link NotStrictlyPositiveException} if number of samples
     * is not strictly positive.
     */
    @Test
    public void testTransformFunctionNotStrictlyPositiveNumberOfSamples() {
        final RealTransformer transformer = createRealTransformer();
        final UnivariateFunction f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            try {
                transformer.transform(f, a, b, -n);
                Assert.fail(Integer.toString(-n));
            } catch (NotStrictlyPositiveException e) {
                // Expected: do nothing
            }
        }
    }

    /**
     * {@link RealTransformer#transform(UnivariateFunction, double, double, int)}
     * should throw a {@link NumberIsTooLargeException} if sampling bounds are
     * not correctly ordered.
     */
    @Test
    public void testTransformFunctionInvalidBounds() {
        final RealTransformer transformer = createRealTransformer();
        final UnivariateFunction f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            try {
                transformer.transform(f, b, a, n);
                Assert.fail(Double.toString(b) + ", " + Double.toString(a));
            } catch (NumberIsTooLargeException e) {
                // Expected: do nothing
            }
        }
    }

    /**
     * {@link RealTransformer#inverseTransform(double[])} should throw a
     * {@link MathIllegalArgumentException} if data size is invalid.
     */
    @Test
    public void testInverseTransformRealInvalidDataSize() {
        final RealTransformer transformer = createRealTransformer();
        for (int i = 0; i < getNumberOfInvalidDataSizes(); i++) {
            final int n = getInvalidDataSize(i);
            try {
                transformer.inverseTransform(createRealData(n));
                Assert.fail(Integer.toString(n));
            } catch (MathIllegalArgumentException e) {
                // Expected: do nothing
            }
        }
    }

    /**
     * {@link RealTransformer#inverseTransform(UnivariateFunction, double, double, int)}
     * should throw a {@link MathIllegalArgumentException} if number of samples
     * is invalid.
     */
    @Test
    public void testInverseTransformFunctionInvalidDataSize() {
        final RealTransformer transformer = createRealTransformer();
        final UnivariateFunction f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        for (int i = 0; i < getNumberOfInvalidDataSizes(); i++) {
            final int n = getInvalidDataSize(i);
            try {
                transformer.inverseTransform(f, a, b, n);
                Assert.fail(Integer.toString(n));
            } catch (MathIllegalArgumentException e) {
                // Expected: do nothing
            }
        }
    }

    /**
     * {@link RealTransformer#inverseTransform(UnivariateFunction, double, double, int)}
     * should throw a {@link NotStrictlyPositiveException} if number of samples
     * is not strictly positive.
     */
    @Test
    public void
        testInverseTransformFunctionNotStrictlyPositiveNumberOfSamples() {
        final RealTransformer transformer = createRealTransformer();
        final UnivariateFunction f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            try {
                transformer.inverseTransform(f, a, b, -n);
                Assert.fail(Integer.toString(-n));
            } catch (NotStrictlyPositiveException e) {
                // Expected: do nothing
            }
        }
    }

    /**
     * {@link RealTransformer#inverseTransform(UnivariateFunction, double, double, int)}
     * should throw a {@link NumberIsTooLargeException} if sampling bounds are
     * not correctly ordered.
     */
    @Test
    public void testInverseTransformFunctionInvalidBounds() {
        final RealTransformer transformer = createRealTransformer();
        final UnivariateFunction f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            try {
                transformer.inverseTransform(f, b, a, n);
                Assert.fail(Double.toString(b) + ", " + Double.toString(a));
            } catch (NumberIsTooLargeException e) {
                // Expected: do nothing
            }
        }
    }

    /*
     * Accuracy tests of transform of valid data.
     */

    /**
     * Accuracy check of {@link RealTransformer#transform(double[])}. For each
     * valid data size returned by
     * {@link #getValidDataSize(int) getValidDataSize(i)},
     * a random data array is generated with
     * {@link #createRealData(int) createRealData(i)}. The actual
     * transform is computed and compared to the expected transform, return by
     * {@link #transform(double[], boolean)}. Actual and expected values should
     * be equal to within the relative error returned by
     * {@link #getRelativeTolerance(int) getRelativeTolerance(i)}.
     */
    @Test
    public void testTransformReal() {
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            final double tol = getRelativeTolerance(i);
            doTestTransformReal(n, tol, true);
        }
    }

    /**
     * Accuracy check of
     * {@link RealTransformer#transform(UnivariateFunction, double, double, int)}.
     * For each valid data size returned by
     * {@link #getValidDataSize(int) getValidDataSize(i)},
     * the {@link UnivariateFunction} returned by {@link #getValidFunction()} is
     * sampled. The actual transform is computed and compared to the expected
     * transform, return by {@link #transform(double[], boolean)}. Actual and
     * expected values should be equal to within the relative error returned by
     * {@link #getRelativeTolerance(int) getRelativeTolerance(i)}.
     */
    @Test
    public void testTransformFunction() {
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            final double tol = getRelativeTolerance(i);
            doTestTransformFunction(n, tol, true);
        }
    }

    /**
     * Accuracy check of {@link RealTransformer#inverseTransform(double[])}. For
     * each valid data size returned by
     * {@link #getValidDataSize(int) getValidDataSize(i)},
     * a random data array is generated with
     * {@link RealTransformerAbstractTest#createRealData(int)}. The actual
     * transform is computed and compared to the expected transform, return by
     * {@link #transform(double[], boolean)}. Actual and expected values should
     * be equal to within the relative error returned by
     * {@link #getRelativeTolerance(int) getRelativeTolerance(i)}.
     */
    @Test
    public void testInverseTransformReal() {
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            final double tol = getRelativeTolerance(i);
            doTestTransformReal(n, tol, false);
        }
    }

    /**
     * Accuracy check of
     * {@link RealTransformer#inverseTransform(UnivariateFunction, double, double, int)}.
     * For each valid data size returned by
     * {@link #getValidDataSize(int) getValidDataSize(i)},
     * the {@link UnivariateFunction} returned by {@link #getValidFunction()} is
     * sampled. The actual transform is computed and compared to the expected
     * transform, return by {@link #transform(double[], boolean)}. Actual and
     * expected values should be equal to within the relative error returned by
     * {@link #getRelativeTolerance(int) getRelativeTolerance(i)}.
     */
    @Test
    public void testInverseTransformFunction() {
        for (int i = 0; i < getNumberOfValidDataSizes(); i++) {
            final int n = getValidDataSize(i);
            final double tol = getRelativeTolerance(i);
            doTestTransformFunction(n, tol, false);
        }
    }

    /*
     * Utility methods.
     */

    /**
     * Returns a random array of doubles. Random generator always uses the same
     * seed.
     *
     * @param n the size of the array to be returned
     * @return a random array of specified size
     */
    double[] createRealData(final int n) {
        final Random random = new Random(SEED);
        final double[] data = new double[n];
        for (int i = 0; i < n; i++) {
            data[i] = 2.0 * random.nextDouble() - 1.0;
        }
        return data;
    }

    /*
     * The tests per se.
     */

    private void doTestTransformReal(final int n, final double tol,
        final boolean forward) {
        final RealTransformer transformer = createRealTransformer();
        final double[] x = createRealData(n);
        final double[] expected = transform(x, forward);
        final double[] actual;
        if (forward) {
            actual = transformer.transform(x);
        } else {
            actual = transformer.inverseTransform(x);
        }
        for (int i = 0; i < n; i++) {
            final String msg = String.format("%d, %d", n, i);
            final double delta = tol * FastMath.abs(expected[i]);
            Assert.assertEquals(msg, expected[i], actual[i], delta);
        }
    }

    private void doTestTransformFunction(final int n, final double tol,
        final boolean forward) {
        final RealTransformer transformer = createRealTransformer();
        final UnivariateFunction f = getValidFunction();
        final double a = getValidLowerBound();
        final double b = getValidUpperBound();
        final double[] x = createRealData(n);
        for (int i = 0; i < n; i++) {
            final double t = a + i * (b - a) / n;
            x[i] = f.value(t);
        }
        final double[] expected = transform(x, forward);
        final double[] actual;
        if (forward) {
            actual = transformer.transform(f, a, b, n);
        } else {
            actual = transformer.inverseTransform(f, a, b, n);
        }
        for (int i = 0; i < n; i++) {
            final String msg = String.format("%d, %d", n, i);
            final double delta = tol * FastMath.abs(expected[i]);
            Assert.assertEquals(msg, expected[i], actual[i], delta);
        }
    }
}
