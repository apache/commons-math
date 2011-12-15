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

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateFunction;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * <p>
 * Implements the Fast Sine Transform for transformation of one-dimensional real
 * data sets. For reference, see James S. Walker, <em>Fast Fourier
 * Transforms</em>, chapter 3 (ISBN 0849371635).
 * </p>
 * <p>
 * There are several variants of the discrete sine transform. The present
 * implementation corresponds to DST-I, with various normalization conventions,
 * which are described below. <strong>It should be noted that regardless to the
 * convention, the first element of the dataset to be transformed must be
 * zero.</strong>
 * </p>
 * <h3><a id="standard">Standard DST-I</a></h3>
 * <p>
 * The standard normalization convention is defined as follows
 * <ul>
 * <li>forward transform: y<sub>n</sub> = &sum;<sub>k=0</sub><sup>N-1</sup>
 * x<sub>k</sub> sin(&pi; nk / N),</li>
 * <li>inverse transform: x<sub>k</sub> = (2 / N)
 * &sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> sin(&pi; nk / N),</li>
 * </ul>
 * where N is the size of the data sample, and x<sub>0</sub> = 0.
 * </p>
 * <p>
 * {@link RealTransformer}s following this convention are returned by the
 * factory method {@link #create()}.
 * </p>
 * <h3><a id="orthogonal">Orthogonal DST-I</a></h3>
 * <p>
 * The orthogonal normalization convention is defined as follows
 * <ul>
 * <li>Forward transform: y<sub>n</sub> = &radic;(2 / N)
 * &sum;<sub>k=0</sub><sup>N-1</sup> x<sub>k</sub> sin(&pi; nk / N),</li>
 * <li>Inverse transform: x<sub>k</sub> = &radic;(2 / N)
 * &sum;<sub>n=0</sub><sup>N-1</sup> y<sub>n</sub> sin(&pi; nk / N),</li>
 * </ul>
 * which makes the transform orthogonal. N is the size of the data sample, and
 * x<sub>0</sub> = 0.
 * </p>
 * <p>
 * {@link RealTransformer}s following this convention are returned by the
 * factory method {@link #createOrthogonal()}.
 * </p>
 * <h3>Link with the DFT, and assumptions on the layout of the data set</h3>
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
 * </p>
 * <p>
 * Then, the standard DST-I y<sub>0</sub>, &hellip;, y<sub>N-1</sub> of the real
 * data set x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is equal to <em>half</em>
 * of i (the pure imaginary number) times the N first elements of the DFT of the
 * extended data set x<sub>0</sub><sup>&#35;</sup>, &hellip;,
 * x<sub>2N-1</sub><sup>&#35;</sup> <br />
 * y<sub>n</sub> = (i / 2) &sum;<sub>k=0</sub><sup>2N-1</sup>
 * x<sub>k</sub><sup>&#35;</sup> exp[-2&pi;i nk / (2N)]
 * &nbsp;&nbsp;&nbsp;&nbsp;k = 0, &hellip;, N-1.
 * </p>
 * <p>
 * The present implementation of the discrete sine transform as a fast sine
 * transform requires the length of the data to be a power of two. Besides,
 * it implicitly assumes that the sampled function is odd. In particular, the
 * first element of the data set must be 0, which is enforced in
 * {@link #transform(UnivariateFunction, double, double, int)} and
 * {@link #inverseTransform(UnivariateFunction, double, double, int)}, after
 * sampling.
 * </p>
 * <p>
 * As of version 2.0 this no longer implements Serializable.
 * </p>
 *
 * @version $Id: FastSineTransformer.java 1213157 2011-12-12 07:19:23Z celestin$
 * @since 1.2
 */
public class FastSineTransformer implements RealTransformer {
    /**
     * {@code true} if the orthogonal version of the DCT should be used.
     *
     * @see #create()
     * @see #createOrthogonal()
     */
    private final boolean orthogonal;

    /**
     * Creates a new instance of this class, with various normalization
     * conventions.
     *
     * @param orthogonal {@code false} if the DST is <em>not</em> to be scaled,
     * {@code true} if it is to be scaled so as to make the transform
     * orthogonal.
     * @see #create()
     * @see #createOrthogonal()
     */
    private FastSineTransformer(final boolean orthogonal) {
        this.orthogonal = orthogonal;
    }

    /**
     * <p>
     * Returns a new instance of this class. The returned transformer uses the
     * <a href="#standard">standard normalizing conventions</a>.
     * </p>
     *
     * @return a new DST transformer, with standard normalizing conventions
     */
    public static FastSineTransformer create() {
        return new FastSineTransformer(false);
    }

    /**
     * <p>
     * Returns a new instance of this class. The returned transformer uses the
     * <a href="#orthogonal">orthogonal normalizing conventions</a>.
     * </p>
     *
     * @return a new DST transformer, with orthogonal normalizing conventions
     */
    public static FastSineTransformer createOrthogonal() {
        return new FastSineTransformer(true);
    }

    /**
     * {@inheritDoc}
     *
     * The first element of the specified data set is required to be {@code 0}.
     */
    public double[] transform(double[] f) throws IllegalArgumentException {
        if (orthogonal) {
            final double s = FastMath.sqrt(2.0 / f.length);
            return FastFourierTransformer.scaleArray(fst(f), s);
        }
        return fst(f);
    }

    /**
     * {@inheritDoc}
     *
     * This implementation enforces {@code f(x) = 0.0} at {@code x = 0.0}.
     */
    public double[] transform(UnivariateFunction f,
        double min, double max, int n) throws IllegalArgumentException {

        final double[] data = FastFourierTransformer.sample(f, min, max, n);
        data[0] = 0.0;
        if (orthogonal) {
            final double s = FastMath.sqrt(2.0 / n);
            return FastFourierTransformer.scaleArray(fst(data), s);
        }
        return fst(data);
    }

    /**
     * {@inheritDoc}
     *
     * The first element of the specified data set is required to be {@code 0}.
     */
    public double[] inverseTransform(double[] f)
        throws IllegalArgumentException {

        if (orthogonal) {
            return transform(f);
        }
        final double s = 2.0 / f.length;
        return FastFourierTransformer.scaleArray(fst(f), s);
    }

    /**
     * {@inheritDoc}
     *
     * This implementation enforces {@code f(x) = 0.0} at {@code x = 0.0}.
     */
    public double[] inverseTransform(UnivariateFunction f,
        double min, double max, int n)
        throws IllegalArgumentException {

        if (orthogonal) {
            return transform(f, min, max, n);
        }

        final double[] data = FastFourierTransformer.sample(f, min, max, n);
        data[0] = 0.0;
        final double s = 2.0 / n;

        return FastFourierTransformer.scaleArray(fst(data), s);
    }

    /**
     * Perform the FST algorithm (including inverse). The first element of the
     * data set is required to be {@code 0}.
     *
     * @param f the real data array to be transformed
     * @return the real transformed array
     * @throws IllegalArgumentException if any parameters are invalid
     */
    protected double[] fst(double[] f) throws IllegalArgumentException {

        final double[] transformed = new double[f.length];

        FastFourierTransformer.verifyDataSet(f);
        if (f[0] != 0.0) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.FIRST_ELEMENT_NOT_ZERO,
                    f[0]);
        }
        final int n = f.length;
        if (n == 1) {       // trivial case
            transformed[0] = 0.0;
            return transformed;
        }

        // construct a new array and perform FFT on it
        final double[] x = new double[n];
        x[0] = 0.0;
        x[n >> 1] = 2.0 * f[n >> 1];
        for (int i = 1; i < (n >> 1); i++) {
            final double a = FastMath.sin(i * FastMath.PI / n) * (f[i] + f[n - i]);
            final double b = 0.5 * (f[i] - f[n - i]);
            x[i]     = a + b;
            x[n - i] = a - b;
        }
        FastFourierTransformer transformer = FastFourierTransformer.create();
        Complex[] y = transformer.transform(x);

        // reconstruct the FST result for the original array
        transformed[0] = 0.0;
        transformed[1] = 0.5 * y[0].getReal();
        for (int i = 1; i < (n >> 1); i++) {
            transformed[2 * i]     = -y[i].getImaginary();
            transformed[2 * i + 1] = y[i].getReal() + transformed[2 * i - 1];
        }

        return transformed;
    }
}
