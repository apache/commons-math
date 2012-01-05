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

import org.apache.commons.math.analysis.FunctionUtils;
import org.apache.commons.math.analysis.UnivariateFunction;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.NonMonotonicSequenceException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.ArithmeticUtils;
import org.apache.commons.math.util.FastMath;

/**
 * <p>
 * Implements the Fast Cosine Transform for transformation of one-dimensional
 * real data sets. For reference, see James S. Walker, <em>Fast Fourier
 * Transforms</em>, chapter 3 (ISBN 0849371635).
 * </p>
 * <p>
 * There are several variants of the discrete cosine transform. The present
 * implementation corresponds to DCT-I, with various normalization conventions,
 * which are described below.
 * </p>
 * <h3><a id="standard">Standard DCT-I</a></h3>
 * <p>
 * The standard normalization convention is defined as follows
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
 * </p>
 * <p> {@link RealTransformer}s following this convention are returned by the
 * factory method {@link #create()}.
 * </p>
 * <h3><a id="orthogonal">Orthogonal DCT-I</a></h3>
 * <p>
 * The orthogonal normalization convention is defined as follows
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
 * </p>
 * <p> {@link RealTransformer}s following this convention are returned by the
 * factory method {@link #createOrthogonal()}.
 * </p>
 * <h3>Link with the DFT, and assumptions on the layout of the data set</h3>
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
 * </p>
 * <p>
 * Then, the standard DCT-I y<sub>0</sub>, &hellip;, y<sub>N-1</sub> of the real
 * data set x<sub>0</sub>, &hellip;, x<sub>N-1</sub> is equal to <em>half</em>
 * of the N first elements of the DFT of the extended data set
 * x<sub>0</sub><sup>&#35;</sup>, &hellip;, x<sub>2N-3</sub><sup>&#35;</sup>
 * <br/>
 * y<sub>n</sub> = (1 / 2) &sum;<sub>k=0</sub><sup>2N-3</sup>
 * x<sub>k</sub><sup>&#35;</sup> exp[-2&pi;i nk / (2N - 2)]
 * &nbsp;&nbsp;&nbsp;&nbsp;k = 0, &hellip;, N-1.
 * </p>
 * <p>
 * The present implementation of the discrete cosine transform as a fast cosine
 * transform requires the length of the data set to be a power of two plus one
 * (N&nbsp;=&nbsp;2<sup>n</sup>&nbsp;+&nbsp;1). Besides, it implicitly assumes
 * that the sampled function is even.
 * </p>
 * <p>As of version 2.0 this no longer implements Serializable.</p>
 *
 * @version $Id$
 * @since 1.2
 */
public class FastCosineTransformer implements RealTransformer, Serializable {

    /** Serializable version identifier. */
    static final long serialVersionUID = 20120501L;

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
     * @param orthogonal {@code false} if the DCT is <em>not</em> to be scaled,
     * {@code true} if it is to be scaled so as to make the transform
     * orthogonal.
     * @see #create()
     * @see #createOrthogonal()
     */
    private FastCosineTransformer(final boolean orthogonal) {
        this.orthogonal = orthogonal;
    }

    /**
     * <p>
     * Returns a new instance of this class. The returned transformer uses the
     * <a href="#standard">standard normalizing conventions</a>.
     * </p>
     *
     * @return a new DCT transformer, with standard normalizing conventions
     */
    public static FastCosineTransformer create() {
        return new FastCosineTransformer(false);
    }

    /**
     * <p>
     * Returns a new instance of this class. The returned transformer uses the
     * <a href="#orthogonal">orthogonal normalizing conventions</a>.
     * </p>
     *
     * @return a new DCT transformer, with orthogonal normalizing conventions
     */
    public static FastCosineTransformer createOrthogonal() {
        return new FastCosineTransformer(true);
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two plus one
     */
    public double[] transform(double[] f) throws MathIllegalArgumentException {

        if (orthogonal) {
            final double s = FastMath.sqrt(2.0 / (f.length - 1));
            return TransformUtils.scaleArray(fct(f), s);
        }
        return fct(f);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NonMonotonicSequenceException if the lower bound is greater
     * than, or equal to the upper bound
     * @throws NotStrictlyPositiveException if the number of sample points is
     * negative
     * @throws MathIllegalArgumentException if the number of sample points is
     * not a power of two plus one
     */
    public double[] transform(UnivariateFunction f,
        double min, double max, int n) throws
        NonMonotonicSequenceException,
        NotStrictlyPositiveException,
        MathIllegalArgumentException {

        final double[] data = FunctionUtils.sample(f, min, max, n);
        return transform(data);
    }

    /**
     * {@inheritDoc}
     *
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two plus one
     */
    public double[] inverseTransform(double[] f) throws
        MathIllegalArgumentException {

        final double s2 = 2.0 / (f.length - 1);
        final double s1 = orthogonal ? FastMath.sqrt(s2) : s2;
        return TransformUtils.scaleArray(fct(f), s1);
    }

    /**
     * {@inheritDoc}
     *
     * @throws NonMonotonicSequenceException if the lower bound is greater
     * than, or equal to the upper bound
     * @throws NotStrictlyPositiveException if the number of sample points is
     * negative
     * @throws MathIllegalArgumentException if the number of sample points is
     * not a power of two plus one
     */
    public double[] inverseTransform(UnivariateFunction f,
        double min, double max, int n) throws
        NonMonotonicSequenceException,
        NotStrictlyPositiveException,
        MathIllegalArgumentException {

        final double[] data = FunctionUtils.sample(f, min, max, n);
        return inverseTransform(data);
    }

    /**
     * Perform the FCT algorithm (including inverse).
     *
     * @param f the real data array to be transformed
     * @return the real transformed array
     * @throws MathIllegalArgumentException if the length of the data array is
     * not a power of two plus one
     */
    protected double[] fct(double[] f)
        throws MathIllegalArgumentException {

        final double[] transformed = new double[f.length];

        final int n = f.length - 1;
        if (!ArithmeticUtils.isPowerOfTwo(n)) {
            throw new MathIllegalArgumentException(
                LocalizedFormats.NOT_POWER_OF_TWO_PLUS_ONE,
                Integer.valueOf(f.length));
        }
        if (n == 1) {       // trivial case
            transformed[0] = 0.5 * (f[0] + f[1]);
            transformed[1] = 0.5 * (f[0] - f[1]);
            return transformed;
        }

        // construct a new array and perform FFT on it
        final double[] x = new double[n];
        x[0] = 0.5 * (f[0] + f[n]);
        x[n >> 1] = f[n >> 1];
        // temporary variable for transformed[1]
        double t1 = 0.5 * (f[0] - f[n]);
        for (int i = 1; i < (n >> 1); i++) {
            final double a = 0.5 * (f[i] + f[n - i]);
            final double b = FastMath.sin(i * FastMath.PI / n) * (f[i] - f[n - i]);
            final double c = FastMath.cos(i * FastMath.PI / n) * (f[i] - f[n - i]);
            x[i] = a - b;
            x[n - i] = a + b;
            t1 += c;
        }
        FastFourierTransformer transformer = FastFourierTransformer.create();
        Complex[] y = transformer.transform(x);

        // reconstruct the FCT result for the original array
        transformed[0] = y[0].getReal();
        transformed[1] = t1;
        for (int i = 1; i < (n >> 1); i++) {
            transformed[2 * i]     = y[i].getReal();
            transformed[2 * i + 1] = transformed[2 * i - 1] - y[i].getImaginary();
        }
        transformed[n] = y[n >> 1].getReal();

        return transformed;
    }
}
