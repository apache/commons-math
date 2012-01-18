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

import java.io.Serializable;
import java.lang.reflect.Array;

import org.apache.commons.math.analysis.FunctionUtils;
import org.apache.commons.math.analysis.UnivariateFunction;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.MathIllegalStateException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.ArithmeticUtils;
import org.apache.commons.math.util.FastMath;

/**
 * <p>
 * Implements the Fast Fourier Transform for transformation of one-dimensional
 * real or complex data sets. For reference, see <em>Applied Numerical Linear
 * Algebra</em>, ISBN 0898713897, chapter 6.
 * </p>
 * <p>
 * There are several variants of the discrete Fourier transform, with various
 * normalization conventions, which are described below.
 * </p>
 * <p>
 * The current implementation of the discrete Fourier transform as a fast
 * Fourier transform requires the length of the data set to be a power of 2.
 * This greatly simplifies and speeds up the code. Users can pad the data with
 * zeros to meet this requirement. There are other flavors of FFT, for
 * reference, see S. Winograd,
 * <i>On computing the discrete Fourier transform</i>, Mathematics of
 * Computation, 32 (1978), 175 - 199.
 * </p>
 * <h3><a id="standard">Standard DFT</a></h3>
 * <p>
 * The standard normalization convention is defined as follows
 * <ul>
 * <li>forward transform: y<sub>n</sub> = &sum;<sub>k=0</sub><sup>N-1</sup>
 * x<sub>k</sub> exp(-2&pi;i n k / N),</li>
 * <li>inverse transform: x<sub>k</sub> = N<sup>-1</sup>
 * &sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> exp(2&pi;i n k / N),</li>
 * </ul>
 * where N is the size of the data sample.
 * </p>
 * <p>
 * {@link FastFourierTransformer}s following this convention are returned by the
 * factory method {@link #create()}.
 * </p>
 * <h3><a id="unitary">Unitary DFT</a></h3>
 * <p>
 * The unitary normalization convention is defined as follows
 * <ul>
 * <li>forward transform: y<sub>n</sub> = (1 / &radic;N)
 * &sum;<sub>k=0</sub><sup>N-1</sup> x<sub>k</sub> exp(-2&pi;i n k / N),</li>
 * <li>inverse transform: x<sub>k</sub> = (1 / &radic;N)
 * &sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> exp(2&pi;i n k / N),</li>
 * </ul>
 * which makes the transform unitary. N is the size of the data sample.
 * </p>
 * <p>
 * {@link FastFourierTransformer}s following this convention are returned by the
 * factory method {@link #createUnitary()}.
 * </p>
 *
 * @version $Id$
 * @since 1.2
 */
public class FastFourierTransformer implements Serializable {

    /** Serializable version identifier. */
    static final long serialVersionUID = 20120501L;

    /**
     * {@code true} if the unitary version of the DFT should be used.
     *
     * @see #create()
     * @see #createUnitary()
     */
    private final boolean unitary;

    /** The roots of unity. */
    private RootsOfUnity roots = new RootsOfUnity();

    /**
     * Creates a new instance of this class, with various normalization
     * conventions.
     *
     * @param unitary {@code false} if the DFT is <em>not</em> to be scaled,
     * {@code true} if it is to be scaled so as to make the transform unitary.
     * @see #create()
     * @see #createUnitary()
     */
    private FastFourierTransformer(final boolean unitary) {
        this.unitary = unitary;
    }


    /**
     * <p>
     * Returns a new instance of this class. The returned transformer uses the
     * <a href="#standard">standard normalizing conventions</a>.
     * </p>
     *
     * @return a new DFT transformer, with standard normalizing conventions
     */
    public static FastFourierTransformer create() {
        return new FastFourierTransformer(false);
    }

    /**
     * <p>
     * Returns a new instance of this class. The returned transformer uses the
     * <a href="#unitary">unitary normalizing conventions</a>.
     * </p>
     *
     * @return a new DFT transformer, with unitary normalizing conventions
     */
    public static FastFourierTransformer createUnitary() {
        return new FastFourierTransformer(true);
    }


    /**
     * Returns the forward transform of the specified real data set.
     *
     * @param f the real data array to be transformed
     * @return the complex transformed array
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two
     */
    public Complex[] transform(double[] f) {
        if (unitary) {
            final double s = 1.0 / FastMath.sqrt(f.length);
            return TransformUtils.scaleArray(fft(f, false), s);
        }
        return fft(f, false);
    }

    /**
     * Returns the forward transform of the specified real function, sampled on
     * the specified interval.
     *
     * @param f the function to be sampled and transformed
     * @param min the (inclusive) lower bound for the interval
     * @param max the (exclusive) upper bound for the interval
     * @param n the number of sample points
     * @return the complex transformed array
     * @throws org.apache.commons.math.exception.NumberIsTooLargeException
     * if the lower bound is greater than, or equal to the upper bound
     * @throws org.apache.commons.math.exception.NotStrictlyPositiveException
     * if the number of sample points {@code n} is negative
     * @throws MathIllegalArgumentException if the number of sample points
     * {@code n} is not a power of two
     */
    public Complex[] transform(UnivariateFunction f,
            double min, double max, int n) {

        final double[] data = FunctionUtils.sample(f, min, max, n);
        if (unitary) {
            final double s = 1.0 / FastMath.sqrt(n);
            return TransformUtils.scaleArray(fft(data, false), s);
        }
        return fft(data, false);
    }

    /**
     * Returns the forward transform of the specified complex data set.
     *
     * @param f the complex data array to be transformed
     * @return the complex transformed array
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two
     */
    public Complex[] transform(Complex[] f) {
        // TODO Is this necessary?
        roots.computeOmega(f.length);
        if (unitary) {
            final double s = 1.0 / FastMath.sqrt(f.length);
            return TransformUtils.scaleArray(fft(f), s);
        }
        return fft(f);
    }

    /**
     * Returns the inverse transform of the specified real data set.
     *
     * @param f the real data array to be inversely transformed
     * @return the complex inversely transformed array
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two
     */
    public Complex[] inverseTransform(double[] f) {
        final double s = 1.0 / (unitary ? FastMath.sqrt(f.length) : f.length);
        return TransformUtils.scaleArray(fft(f, true), s);
    }

    /**
     * Returns the inverse transform of the specified real function, sampled
     * on the given interval.
     *
     * @param f the function to be sampled and inversely transformed
     * @param min the (inclusive) lower bound for the interval
     * @param max the (exclusive) upper bound for the interval
     * @param n the number of sample points
     * @return the complex inversely transformed array
     * @throws org.apache.commons.math.exception.NumberIsTooLargeException
     * if the lower bound is greater than, or equal to the upper bound
     * @throws org.apache.commons.math.exception.NotStrictlyPositiveException
     * if the number of sample points {@code n} is negative
     * @throws MathIllegalArgumentException if the number of sample points
     * {@code n} is not a power of two
     */
    public Complex[] inverseTransform(UnivariateFunction f,
            double min, double max, int n) {
        final double[] data = FunctionUtils.sample(f, min, max, n);
        final double s = 1.0 / (unitary ? FastMath.sqrt(n) : n);
        return TransformUtils.scaleArray(fft(data, true), s);
    }

    /**
     * Returns the inverse transform of the specified complex data set.
     *
     * @param f the complex data array to be inversely transformed
     * @return the complex inversely transformed array
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two
     */
    public Complex[] inverseTransform(Complex[] f) {
        roots.computeOmega(-f.length);    // pass negative argument
        final double s = 1.0 / (unitary ? FastMath.sqrt(f.length) : f.length);
        return TransformUtils.scaleArray(fft(f), s);
    }

    /**
     * Returns the FFT of the specified real data set. Performs the base-4
     * Cooley-Tukey FFT algorithm.
     *
     * @param f the real data array to be transformed
     * @param isInverse {@code true} if inverse transform is to be carried out
     * @return the complex transformed array
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two
     */
    protected Complex[] fft(double[] f, boolean isInverse) {

        if (!ArithmeticUtils.isPowerOfTwo(f.length)) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING,
                    Integer.valueOf(f.length));
        }
        Complex[] transformed = new Complex[f.length];
        if (f.length == 1) {
            transformed[0] = new Complex(f[0], 0.0);
            return transformed;
        }

        // Rather than the naive real to complex conversion, pack 2N
        // real numbers into N complex numbers for better performance.
        int n = f.length >> 1;
        Complex[] repacked = new Complex[n];
        for (int i = 0; i < n; i++) {
            repacked[i] = new Complex(f[2 * i], f[2 * i + 1]);
        }
        roots.computeOmega(isInverse ? -n : n);
        Complex[] z = fft(repacked);

        // reconstruct the FFT result for the original array
        roots.computeOmega(isInverse ? -2 * n : 2 * n);
        transformed[0] = new Complex(2 * (z[0].getReal() + z[0].getImaginary()), 0.0);
        transformed[n] = new Complex(2 * (z[0].getReal() - z[0].getImaginary()), 0.0);
        for (int i = 1; i < n; i++) {
            Complex a = z[n - i].conjugate();
            Complex b = z[i].add(a);
            Complex c = z[i].subtract(a);
            //Complex D = roots.getOmega(i).multiply(Complex.I);
            Complex d = new Complex(-roots.getOmegaImaginary(i),
                                    roots.getOmegaReal(i));
            transformed[i] = b.subtract(c.multiply(d));
            transformed[2 * n - i] = transformed[i].conjugate();
        }

        return TransformUtils.scaleArray(transformed, 0.5);
    }

    /**
     * Returns the FFT of the specified complex data set. Performs the base-4
     * Cooley-Tukey FFT algorithm.
     *
     * @param data the complex data array to be transformed
     * @return the complex transformed array
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two
     */
    protected Complex[] fft(Complex[] data) {

        if (!ArithmeticUtils.isPowerOfTwo(data.length)) {
            throw new MathIllegalArgumentException(
                    LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING,
                    Integer.valueOf(data.length));
        }

        final int n = data.length;
        final Complex[] f = new Complex[n];

        // initial simple cases
        if (n == 1) {
            f[0] = data[0];
            return f;
        }
        if (n == 2) {
            f[0] = data[0].add(data[1]);
            f[1] = data[0].subtract(data[1]);
            return f;
        }

        // permute original data array in bit-reversal order
        int ii = 0;
        for (int i = 0; i < n; i++) {
            f[i] = data[ii];
            int k = n >> 1;
            while (ii >= k && k > 0) {
                ii -= k; k >>= 1;
            }
            ii += k;
        }

        // the bottom base-4 round
        for (int i = 0; i < n; i += 4) {
            final Complex a = f[i].add(f[i + 1]);
            final Complex b = f[i + 2].add(f[i + 3]);
            final Complex c = f[i].subtract(f[i + 1]);
            final Complex d = f[i + 2].subtract(f[i + 3]);
            final Complex e1 = c.add(d.multiply(Complex.I));
            final Complex e2 = c.subtract(d.multiply(Complex.I));
            f[i] = a.add(b);
            f[i + 2] = a.subtract(b);
            // omegaCount indicates forward or inverse transform
            f[i + 1] = roots.isForward() ? e2 : e1;
            f[i + 3] = roots.isForward() ? e1 : e2;
        }

        // iterations from bottom to top take O(N*logN) time
        for (int i = 4; i < n; i <<= 1) {
            final int m = n / (i << 1);
            for (int j = 0; j < n; j += i << 1) {
                for (int k = 0; k < i; k++) {
                    //z = f[i+j+k].multiply(roots.getOmega(k*m));
                    final int km = k * m;
                    final double omegaKmReal = roots.getOmegaReal(km);
                    final double omegaKmImag = roots.getOmegaImaginary(km);
                    //z = f[i+j+k].multiply(omega[k*m]);
                    final Complex z = new Complex(
                        f[i + j + k].getReal() * omegaKmReal -
                        f[i + j + k].getImaginary() * omegaKmImag,
                        f[i + j + k].getReal() * omegaKmImag +
                        f[i + j + k].getImaginary() * omegaKmReal);

                    f[i + j + k] = f[j + k].subtract(z);
                    f[j + k] = f[j + k].add(z);
                }
            }
        }
        return f;
    }

    /**
     * Performs a multi-dimensional Fourier transform on a given array. Use
     * {@link #transform(Complex[])} and {@link #inverseTransform(Complex[])} in
     * a row-column implementation in any number of dimensions with
     * O(N&times;log(N)) complexity with
     * N = n<sub>1</sub> &times; n<sub>2</sub> &times;n<sub>3</sub> &times; ...
     * &times; n<sub>d</sub>, where n<sub>k</sub> is the number of elements in
     * dimension k, and d is the total number of dimensions.
     *
     * @param mdca Multi-Dimensional Complex Array id est
     * {@code Complex[][][][]}
     * @param forward {@link #inverseTransform} is performed if this is
     * {@code false}
     * @return transform of {@code mdca} as a Multi-Dimensional Complex Array
     * id est {@code Complex[][][][]}
     * @throws IllegalArgumentException if any dimension is not a power of two
     */
    public Object mdfft(Object mdca, boolean forward) {
        MultiDimensionalComplexMatrix mdcm = (MultiDimensionalComplexMatrix)
                new MultiDimensionalComplexMatrix(mdca).clone();
        int[] dimensionSize = mdcm.getDimensionSizes();
        //cycle through each dimension
        for (int i = 0; i < dimensionSize.length; i++) {
            mdfft(mdcm, forward, i, new int[0]);
        }
        return mdcm.getArray();
    }

    /**
     * Performs one dimension of a multi-dimensional Fourier transform.
     *
     * @param mdcm input matrix
     * @param forward {@link #inverseTransform} is performed if this is
     * {@code false}
     * @param d index of the dimension to process
     * @param subVector recursion subvector
     * @throws IllegalArgumentException if any dimension is not a power of two
     */
    private void mdfft(MultiDimensionalComplexMatrix mdcm,
            boolean forward, int d, int[] subVector) {

        int[] dimensionSize = mdcm.getDimensionSizes();
        //if done
        if (subVector.length == dimensionSize.length) {
            Complex[] temp = new Complex[dimensionSize[d]];
            for (int i = 0; i < dimensionSize[d]; i++) {
                //fft along dimension d
                subVector[d] = i;
                temp[i] = mdcm.get(subVector);
            }

            if (forward) {
                temp = transform(temp);
            } else {
                temp = inverseTransform(temp);
            }

            for (int i = 0; i < dimensionSize[d]; i++) {
                subVector[d] = i;
                mdcm.set(temp[i], subVector);
            }
        } else {
            int[] vector = new int[subVector.length + 1];
            System.arraycopy(subVector, 0, vector, 0, subVector.length);
            if (subVector.length == d) {
                //value is not important once the recursion is done.
                //then an fft will be applied along the dimension d.
                vector[d] = 0;
                mdfft(mdcm, forward, d, vector);
            } else {
                for (int i = 0; i < dimensionSize[subVector.length]; i++) {
                    vector[subVector.length] = i;
                    //further split along the next dimension
                    mdfft(mdcm, forward, d, vector);
                }
            }
        }
        return;
    }

    /**
     * Complex matrix implementation. Not designed for synchronized access may
     * eventually be replaced by jsr-83 of the java community process
     * http://jcp.org/en/jsr/detail?id=83
     * may require additional exception throws for other basic requirements.
     */
    private static class MultiDimensionalComplexMatrix
        implements Cloneable {

        /** Size in all dimensions. */
        protected int[] dimensionSize;

        /** Storage array. */
        protected Object multiDimensionalComplexArray;

        /**
         * Simple constructor.
         *
         * @param multiDimensionalComplexArray array containing the matrix
         * elements
         */
        public MultiDimensionalComplexMatrix(
                Object multiDimensionalComplexArray) {

            this.multiDimensionalComplexArray = multiDimensionalComplexArray;

            // count dimensions
            int numOfDimensions = 0;
            for (Object lastDimension = multiDimensionalComplexArray;
                 lastDimension instanceof Object[];) {
                final Object[] array = (Object[]) lastDimension;
                numOfDimensions++;
                lastDimension = array[0];
            }

            // allocate array with exact count
            dimensionSize = new int[numOfDimensions];

            // fill array
            numOfDimensions = 0;
            for (Object lastDimension = multiDimensionalComplexArray;
                 lastDimension instanceof Object[];) {
                final Object[] array = (Object[]) lastDimension;
                dimensionSize[numOfDimensions++] = array.length;
                lastDimension = array[0];
            }

        }

        /**
         * Get a matrix element.
         *
         * @param vector indices of the element
         * @return matrix element
         * @exception DimensionMismatchException if dimensions do not match
         */
        public Complex get(int... vector)
                throws DimensionMismatchException {

            if (vector == null) {
                if (dimensionSize.length > 0) {
                    throw new DimensionMismatchException(
                            0,
                            dimensionSize.length);
                }
                return null;
            }
            if (vector.length != dimensionSize.length) {
                throw new DimensionMismatchException(
                        vector.length,
                        dimensionSize.length);
            }

            Object lastDimension = multiDimensionalComplexArray;

            for (int i = 0; i < dimensionSize.length; i++) {
                lastDimension = ((Object[]) lastDimension)[vector[i]];
            }
            return (Complex) lastDimension;
        }

        /**
         * Set a matrix element.
         *
         * @param magnitude magnitude of the element
         * @param vector indices of the element
         * @return the previous value
         * @exception DimensionMismatchException if dimensions do not match
         */
        public Complex set(Complex magnitude, int... vector)
                throws DimensionMismatchException {

            if (vector == null) {
                if (dimensionSize.length > 0) {
                    throw new DimensionMismatchException(
                            0,
                            dimensionSize.length);
                }
                return null;
            }
            if (vector.length != dimensionSize.length) {
                throw new DimensionMismatchException(
                        vector.length,
                        dimensionSize.length);
            }

            Object[] lastDimension = (Object[]) multiDimensionalComplexArray;
            for (int i = 0; i < dimensionSize.length - 1; i++) {
                lastDimension = (Object[]) lastDimension[vector[i]];
            }

            Complex lastValue = (Complex) lastDimension[vector[dimensionSize.length - 1]];
            lastDimension[vector[dimensionSize.length - 1]] = magnitude;

            return lastValue;
        }

        /**
         * Get the size in all dimensions.
         *
         * @return size in all dimensions
         */
        public int[] getDimensionSizes() {
            return dimensionSize.clone();
        }

        /**
         * Get the underlying storage array.
         *
         * @return underlying storage array
         */
        public Object getArray() {
            return multiDimensionalComplexArray;
        }

        /** {@inheritDoc} */
        @Override
        public Object clone() {
            MultiDimensionalComplexMatrix mdcm =
                    new MultiDimensionalComplexMatrix(Array.newInstance(
                    Complex.class, dimensionSize));
            clone(mdcm);
            return mdcm;
        }

        /**
         * Copy contents of current array into mdcm.
         *
         * @param mdcm array where to copy data
         */
        private void clone(MultiDimensionalComplexMatrix mdcm) {

            int[] vector = new int[dimensionSize.length];
            int size = 1;
            for (int i = 0; i < dimensionSize.length; i++) {
                size *= dimensionSize[i];
            }
            int[][] vectorList = new int[size][dimensionSize.length];
            for (int[] nextVector : vectorList) {
                System.arraycopy(vector, 0, nextVector, 0,
                                 dimensionSize.length);
                for (int i = 0; i < dimensionSize.length; i++) {
                    vector[i]++;
                    if (vector[i] < dimensionSize[i]) {
                        break;
                    } else {
                        vector[i] = 0;
                    }
                }
            }

            for (int[] nextVector : vectorList) {
                mdcm.set(get(nextVector), nextVector);
            }
        }
    }


    /**
     * Computes the {@code n}<sup>th</sup> roots of unity. A cache of already
     * computed values is maintained.
     */
    private static class RootsOfUnity implements Serializable {

        /** Serializable version id. */
        private static final long serialVersionUID = 6404784357747329667L;

        /** Number of roots of unity. */
        private int omegaCount;

        /** Real part of the roots. */
        private double[] omegaReal;

        /** Imaginary part of the roots for forward transform. */
        private double[] omegaImaginaryForward;

        /** Imaginary part of the roots for reverse transform. */
        private double[] omegaImaginaryInverse;

        /** Forward/reverse indicator. */
        private boolean isForward;

        /**
         * Build an engine for computing the {@code n}<sup>th</sup> roots of
         * unity.
         */
        public RootsOfUnity() {

            omegaCount = 0;
            omegaReal = null;
            omegaImaginaryForward = null;
            omegaImaginaryInverse = null;
            isForward = true;
        }

        /**
         * Check if computation has been done for forward or reverse transform.
         *
         * @return {@code true} if computation has been done for forward transform
         * @throws MathIllegalStateException if no roots of unity have been computed
         * yet
         */
        public synchronized boolean isForward()
                throws MathIllegalStateException {

            if (omegaCount == 0) {
                throw new MathIllegalStateException(
                        LocalizedFormats.ROOTS_OF_UNITY_NOT_COMPUTED_YET);
            }
            return isForward;
        }

        /**
         * <p>
         * Computes the {@code n}<sup>th</sup> roots of unity. The roots are
         * stored in {@code omega[]}, such that {@code omega[k] = w ^ k}, where
         * {@code k = 0, ..., n - 1}, {@code w = exp(-2 &pi; i / n)} and
         * {@code i = sqrt(-1)}.
         * </p>
         * <p>
         * Note that {@code n} is positive for forward transform and negative
         * for inverse transform.
         * </p>
         *
         * @param n number of roots of unity to compute, positive for forward
         * transform, negative for inverse transform
         * @throws ZeroException if {@code n = 0}
         */
        public synchronized void computeOmega(int n) throws ZeroException {

            if (n == 0) {
                throw new ZeroException(
                        LocalizedFormats.CANNOT_COMPUTE_0TH_ROOT_OF_UNITY);
            }

            isForward = n > 0;

            // avoid repetitive calculations
            final int absN = FastMath.abs(n);

            if (absN == omegaCount) {
                return;
            }

            // calculate everything from scratch, for both forward and inverse
            // versions
            final double t = 2.0 * FastMath.PI / absN;
            final double cosT = FastMath.cos(t);
            final double sinT = FastMath.sin(t);
            omegaReal = new double[absN];
            omegaImaginaryForward = new double[absN];
            omegaImaginaryInverse = new double[absN];
            omegaReal[0] = 1.0;
            omegaImaginaryForward[0] = 0.0;
            omegaImaginaryInverse[0] = 0.0;
            for (int i = 1; i < absN; i++) {
                omegaReal[i] = omegaReal[i - 1] * cosT +
                        omegaImaginaryForward[i - 1] * sinT;
                omegaImaginaryForward[i] = omegaImaginaryForward[i - 1] * cosT -
                        omegaReal[i - 1] * sinT;
                omegaImaginaryInverse[i] = -omegaImaginaryForward[i];
            }
            omegaCount = absN;
        }

        /**
         * Get the real part of the {@code k}<sup>th</sup>
         * {@code n}<sup>th</sup> root of unity.
         *
         * @param k index of the {@code n}<sup>th</sup> root of unity
         * @return real part of the {@code k}<sup>th</sup>
         * {@code n}<sup>th</sup> root of unity
         * @throws MathIllegalStateException if no roots of unity have been
         * computed yet
         * @throws MathIllegalArgumentException if {@code k} is out of range
         */
        public synchronized double getOmegaReal(int k)
                throws MathIllegalStateException, MathIllegalArgumentException {

            if (omegaCount == 0) {
                throw new MathIllegalStateException(
                        LocalizedFormats.ROOTS_OF_UNITY_NOT_COMPUTED_YET);
            }
            if ((k < 0) || (k >= omegaCount)) {
                throw new OutOfRangeException(
                        LocalizedFormats.OUT_OF_RANGE_ROOT_OF_UNITY_INDEX,
                        Integer.valueOf(k),
                        Integer.valueOf(0),
                        Integer.valueOf(omegaCount - 1));
            }

            return omegaReal[k];
        }

        /**
         * Get the imaginary part of the {@code k}<sup>th</sup>
         * {@code n}<sup>th</sup> root of unity.
         *
         * @param k index of the {@code n}<sup>th</sup> root of unity
         * @return imaginary part of the {@code k}<sup>th</sup>
         * {@code n}<sup>th</sup> root of unity
         * @throws MathIllegalStateException if no roots of unity have been
         * computed yet
         * @throws OutOfRangeException if {@code k} is out of range
         */
        public synchronized double getOmegaImaginary(int k)
                throws MathIllegalStateException, OutOfRangeException {

            if (omegaCount == 0) {
                throw new MathIllegalStateException(
                        LocalizedFormats.ROOTS_OF_UNITY_NOT_COMPUTED_YET);
            }
            if ((k < 0) || (k >= omegaCount)) {
                throw new OutOfRangeException(
                        LocalizedFormats.OUT_OF_RANGE_ROOT_OF_UNITY_INDEX,
                        Integer.valueOf(k),
                        Integer.valueOf(0),
                        Integer.valueOf(omegaCount - 1));
            }

            return isForward ? omegaImaginaryForward[k] :
                omegaImaginaryInverse[k];
        }
    }
}
