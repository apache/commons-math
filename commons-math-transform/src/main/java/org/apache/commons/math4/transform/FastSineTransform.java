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

import java.util.function.UnaryOperator;
import java.util.function.DoubleUnaryOperator;

import org.apache.commons.numbers.complex.Complex;
import org.apache.commons.numbers.core.ArithmeticUtils;

/**
 * Implements the Fast Sine Transform for transformation of one-dimensional real
 * data sets. For reference, see James S. Walker, <em>Fast Fourier
 * Transforms</em>, chapter 3 (ISBN 0849371635).
 * <p>
 * There are several variants of the discrete sine transform. The present
 * implementation corresponds to DST-I, with various normalization conventions,
 * which are specified by the parameter {@link Norm}.
 * <strong>It should be noted that regardless to the convention, the first
 * element of the dataset to be transformed must be zero.</strong>
 * <p>
 * DST-I is equivalent to DFT of an <em>odd extension</em> of the data series.
 * More precisely, if x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is the data set
 * to be sine transformed, the extended data set x<sub>0</sub><sup>&#35;</sup>,
 * &hellip;, x<sub>2N-1</sub><sup>&#35;</sup> is defined as follows
 * <ul>
 * <li>x<sub>0</sub><sup>&#35;</sup> = x<sub>0</sub> = 0,</li>
 * <li>x<sub>k</sub><sup>&#35;</sup> = x<sub>k</sub> if 1 &le; k &lt; N,</li>
 * <li>x<sub>N</sub><sup>&#35;</sup> = 0,</li>
 * <li>x<sub>k</sub><sup>&#35;</sup> = -x<sub>2N-k</sub> if N + 1 &le; k &lt;
 * 2N.</li>
 * </ul>
 * <p>
 * Then, the standard DST-I y<sub>0</sub>, &hellip;, y<sub>N-1</sub> of the real
 * data set x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is equal to <em>half</em>
 * of i (the pure imaginary number) times the N first elements of the DFT of the
 * extended data set x<sub>0</sub><sup>&#35;</sup>, &hellip;,
 * x<sub>2N-1</sub><sup>&#35;</sup> <br>
 * y<sub>n</sub> = (i / 2) &sum;<sub>k=0</sub><sup>2N-1</sup>
 * x<sub>k</sub><sup>&#35;</sup> exp[-2&pi;i nk / (2N)]
 * &nbsp;&nbsp;&nbsp;&nbsp;k = 0, &hellip;, N-1.
 * <p>
 * The present implementation of the discrete sine transform as a fast sine
 * transform requires the length of the data to be a power of two. Besides,
 * it implicitly assumes that the sampled function is odd. In particular, the
 * first element of the data set must be 0, which is enforced in
 * {@link #apply(DoubleUnaryOperator, double, double, int)},
 * after sampling.
 */
public class FastSineTransform implements RealTransform {
    /** Operation to be performed. */
    private final UnaryOperator<double[]> op;

    /**
     * @param normalization Normalization to be applied to the transformed data.
     * @param inverse Whether to perform the inverse transform.
     */
    public FastSineTransform(final Norm normalization,
                             final boolean inverse) {
        op = create(normalization, inverse);
    }

    /**
     * @param normalization Normalization to be applied to the
     * transformed data.
     */
    public FastSineTransform(final Norm normalization) {
        this(normalization, false);
    }

    /**
     * {@inheritDoc}
     *
     * The first element of the specified data set is required to be {@code 0}.
     *
     * @throws IllegalArgumentException if the length of the data array is
     * not a power of two, or the first element of the data array is not zero.
     */
    @Override
    public double[] apply(final double[] f) {
        return op.apply(f);
    }

    /**
     * {@inheritDoc}
     *
     * The implementation enforces {@code f(x) = 0} at {@code x = 0}.
     *
     * @throws IllegalArgumentException if the number of sample points is not a
     * power of two, if the lower bound is greater than, or equal to the upper bound,
     * if the number of sample points is negative.
     */
    @Override
    public double[] apply(final DoubleUnaryOperator f,
                          final double min,
                          final double max,
                          final int n) {
        final double[] data = TransformUtils.sample(f, min, max, n);
        data[0] = 0;
        return apply(data);
    }

    /**
     * Perform the FST algorithm (including inverse).
     * The first element of the data set is required to be {@code 0}.
     *
     * @param f Data array to be transformed.
     * @return the transformed array.
     * @throws IllegalArgumentException if the length of the data array is
     * not a power of two, or the first element of the data array is not zero.
     */
    private double[] fst(double[] f) {
        if (!ArithmeticUtils.isPowerOfTwo(f.length)) {
            throw new TransformException(TransformException.NOT_POWER_OF_TWO,
                                         f.length);
        }
        if (f[0] != 0) {
            throw new TransformException(TransformException.FIRST_ELEMENT_NOT_ZERO,
                                         f[0]);
        }

        final double[] transformed = new double[f.length];
        final int n = f.length;
        if (n == 1) {
            transformed[0] = 0;
            return transformed;
        }

        // construct a new array and perform FFT on it
        final double[] x = new double[n];
        x[0] = 0;
        final int nShifted = n >> 1;
        x[nShifted] = 2 * f[nShifted];
        final double piOverN = Math.PI / n;
        for (int i = 1; i < nShifted; i++) {
            final int nMi = n - i;
            final double fi = f[i];
            final double fnMi = f[nMi];
            final double a = Math.sin(i * piOverN) * (fi + fnMi);
            final double b = 0.5 * (fi - fnMi);
            x[i] = a + b;
            x[nMi] = a - b;
        }

        final FastFourierTransform transform = new FastFourierTransform(FastFourierTransform.Norm.STD);
        final Complex[] y = transform.apply(x);

        // reconstruct the FST result for the original array
        transformed[0] = 0;
        transformed[1] = 0.5 * y[0].getReal();
        for (int i = 1; i < nShifted; i++) {
            final int i2 = 2 * i;
            transformed[i2] = -y[i].getImaginary();
            transformed[i2 + 1] = y[i].getReal() + transformed[i2 - 1];
        }

        return transformed;
    }

    /**
     * Factory method.
     *
     * @param normalization Normalization to be applied to the
     * transformed data.
     * @param inverse Whether to perform the inverse transform.
     * @return the transform operator.
     */
    private UnaryOperator<double[]> create(final Norm normalization,
                                           final boolean inverse) {
        if (inverse) {
            return normalization == Norm.ORTHO ?
                f -> TransformUtils.scaleInPlace(fst(f), Math.sqrt(2d / f.length)) :
                f -> TransformUtils.scaleInPlace(fst(f), 2d / f.length);
        } else {
            return normalization == Norm.ORTHO ?
                f -> TransformUtils.scaleInPlace(fst(f), Math.sqrt(2d / f.length)) :
                f -> fst(f);
        }
    }

    /**
     * Normalization types.
     */
    public enum Norm {
        /**
         * Should be passed to the constructor of {@link FastSineTransform} to
         * use the <em>standard</em> normalization convention. The standard DST-I
         * normalization convention is defined as follows
         * <ul>
         * <li>forward transform: y<sub>n</sub> = &sum;<sub>k=0</sub><sup>N-1</sup>
         * x<sub>k</sub> sin(&pi; nk / N),</li>
         * <li>inverse transform: x<sub>k</sub> = (2 / N)
         * &sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> sin(&pi; nk / N),</li>
         * </ul>
         * where N is the size of the data sample, and x<sub>0</sub> = 0.
         */
        STD,

        /**
         * Should be passed to the constructor of {@link FastSineTransform} to
         * use the <em>orthogonal</em> normalization convention. The orthogonal
         * DCT-I normalization convention is defined as follows
         * <ul>
         * <li>Forward transform: y<sub>n</sub> = &radic;(2 / N)
         * &sum;<sub>k=0</sub><sup>N-1</sup> x<sub>k</sub> sin(&pi; nk / N),</li>
         * <li>Inverse transform: x<sub>k</sub> = &radic;(2 / N)
         * &sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> sin(&pi; nk / N),</li>
         * </ul>
         * which makes the transform orthogonal. N is the size of the data sample,
         * and x<sub>0</sub> = 0.
         */
        ORTHO
    }
}
