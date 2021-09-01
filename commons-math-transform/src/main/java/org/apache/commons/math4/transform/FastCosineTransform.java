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
 * Implements the Fast Cosine Transform for transformation of one-dimensional
 * real data sets. For reference, see James S. Walker, <em>Fast Fourier
 * Transforms</em>, chapter 3 (ISBN 0849371635).
 * <p>
 * There are several variants of the discrete cosine transform. The present
 * implementation corresponds to DCT-I, with various normalization conventions,
 * which are specified by the parameter {@link Norm}.
 * <p>
 * DCT-I is equivalent to DFT of an <em>even extension</em> of the data series.
 * More precisely, if x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is the data set
 * to be cosine transformed, the extended data set
 * x<sub>0</sub><sup>&#35;</sup>, &hellip;, x<sub>2N-3</sub><sup>&#35;</sup>
 * is defined as follows
 * <ul>
 * <li>x<sub>k</sub><sup>&#35;</sup> = x<sub>k</sub> if 0 &le; k &lt; N,</li>
 * <li>x<sub>k</sub><sup>&#35;</sup> = x<sub>2N-2-k</sub>
 * if N &le; k &lt; 2N - 2.</li>
 * </ul>
 * <p>
 * Then, the standard DCT-I y<sub>0</sub>, &hellip;, y<sub>N-1</sub> of the real
 * data set x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is equal to <em>half</em>
 * of the N first elements of the DFT of the extended data set
 * x<sub>0</sub><sup>&#35;</sup>, &hellip;, x<sub>2N-3</sub><sup>&#35;</sup>
 * <br>
 * y<sub>n</sub> = (1 / 2) &sum;<sub>k=0</sub><sup>2N-3</sup>
 * x<sub>k</sub><sup>&#35;</sup> exp[-2&pi;i nk / (2N - 2)]
 * &nbsp;&nbsp;&nbsp;&nbsp;k = 0, &hellip;, N-1.
 * <p>
 * The present implementation of the discrete cosine transform as a fast cosine
 * transform requires the length of the data set to be a power of two plus one
 * (N&nbsp;=&nbsp;2<sup>n</sup>&nbsp;+&nbsp;1). Besides, it implicitly assumes
 * that the sampled function is even.
 */
public class FastCosineTransform implements RealTransform {
    /** Operation to be performed. */
    private final UnaryOperator<double[]> op;

    /**
     * @param normalization Normalization to be applied to the
     * transformed data.
     * @param inverse Whether to perform the inverse transform.
     */
    public FastCosineTransform(final Norm normalization,
                               final boolean inverse) {
        op = create(normalization, inverse);
    }

    /**
     * @param normalization Normalization to be applied to the
     * transformed data.
     */
    public FastCosineTransform(final Norm normalization) {
        this(normalization, false);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the length of the data array is
     * not a power of two plus one.
     */
    @Override
    public double[] apply(final double[] f) {
        return op.apply(f);
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the number of sample points is
     * not a power of two plus one, if the lower bound is greater than or
     * equal to the upper bound, if the number of sample points is negative.
     */
    @Override
    public double[] apply(final DoubleUnaryOperator f,
                          final double min,
                          final double max,
                          final int n) {
        return apply(TransformUtils.sample(f, min, max, n));
    }

    /**
     * Perform the FCT algorithm (including inverse).
     *
     * @param f Data to be transformed.
     * @return the transformed array.
     * @throws IllegalArgumentException if the length of the data array is
     * not a power of two plus one.
     */
    private double[] fct(double[] f) {
        final int n = f.length - 1;
        if (!ArithmeticUtils.isPowerOfTwo(n)) {
            throw new TransformException(TransformException.NOT_POWER_OF_TWO_PLUS_ONE,
                                         Integer.valueOf(f.length));
        }

        final double[] transformed = new double[f.length];

        if (n == 1) {       // trivial case
            transformed[0] = 0.5 * (f[0] + f[1]);
            transformed[1] = 0.5 * (f[0] - f[1]);
            return transformed;
        }

        // construct a new array and perform FFT on it
        final double[] x = new double[n];
        x[0] = 0.5 * (f[0] + f[n]);
        final int nShifted = n >> 1;
        x[nShifted] = f[nShifted];
        // temporary variable for transformed[1]
        double t1 = 0.5 * (f[0] - f[n]);
        final double piOverN = Math.PI / n;
        for (int i = 1; i < nShifted; i++) {
            final int nMi = n - i;
            final double fi = f[i];
            final double fnMi = f[nMi];
            final double a = 0.5 * (fi + fnMi);
            final double arg = i * piOverN;
            final double b = Math.sin(arg) * (fi - fnMi);
            final double c = Math.cos(arg) * (fi - fnMi);
            x[i] = a - b;
            x[nMi] = a + b;
            t1 += c;
        }
        final FastFourierTransform transformer = new FastFourierTransform(FastFourierTransform.Norm.STD,
                                                                          false);
        final Complex[] y = transformer.apply(x);

        // reconstruct the FCT result for the original array
        transformed[0] = y[0].getReal();
        transformed[1] = t1;
        for (int i = 1; i < nShifted; i++) {
            final int i2 = 2 * i;
            transformed[i2] = y[i].getReal();
            transformed[i2 + 1] = transformed[i2 - 1] - y[i].getImaginary();
        }
        transformed[n] = y[nShifted].getReal();

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
                f -> TransformUtils.scaleInPlace(fct(f), Math.sqrt(2d / (f.length - 1))) :
                f -> TransformUtils.scaleInPlace(fct(f), 2d / (f.length - 1));
        } else {
            return normalization == Norm.ORTHO ?
                f -> TransformUtils.scaleInPlace(fct(f), Math.sqrt(2d / (f.length - 1))) :
                f -> fct(f);
        }
    }

    /**
     * Normalization types.
     */
    public enum Norm {
        /**
         * Should be passed to the constructor of {@link FastCosineTransform}
         * to use the <em>standard</em> normalization convention.  The standard
         * DCT-I normalization convention is defined as follows
         * <ul>
         * <li>forward transform:
         * y<sub>n</sub> = (1/2) [x<sub>0</sub> + (-1)<sup>n</sup>x<sub>N-1</sub>]
         * + &sum;<sub>k=1</sub><sup>N-2</sup>
         * x<sub>k</sub> cos[&pi; nk / (N - 1)],</li>
         * <li>inverse transform:
         * x<sub>k</sub> = [1 / (N - 1)] [y<sub>0</sub>
         * + (-1)<sup>k</sup>y<sub>N-1</sub>]
         * + [2 / (N - 1)] &sum;<sub>n=1</sub><sup>N-2</sup>
         * y<sub>n</sub> cos[&pi; nk / (N - 1)],</li>
         * </ul>
         * where N is the size of the data sample.
         */
        STD,

        /**
         * Should be passed to the constructor of {@link FastCosineTransform}
         * to use the <em>orthogonal</em> normalization convention. The orthogonal
         * DCT-I normalization convention is defined as follows
         * <ul>
         * <li>forward transform:
         * y<sub>n</sub> = [2(N - 1)]<sup>-1/2</sup> [x<sub>0</sub>
         * + (-1)<sup>n</sup>x<sub>N-1</sub>]
         * + [2 / (N - 1)]<sup>1/2</sup> &sum;<sub>k=1</sub><sup>N-2</sup>
         * x<sub>k</sub> cos[&pi; nk / (N - 1)],</li>
         * <li>inverse transform:
         * x<sub>k</sub> = [2(N - 1)]<sup>-1/2</sup> [y<sub>0</sub>
         * + (-1)<sup>k</sup>y<sub>N-1</sub>]
         * + [2 / (N - 1)]<sup>1/2</sup> &sum;<sub>n=1</sub><sup>N-2</sup>
         * y<sub>n</sub> cos[&pi; nk / (N - 1)],</li>
         * </ul>
         * which makes the transform orthogonal. N is the size of the data sample.
         */
        ORTHO;
    }
}
