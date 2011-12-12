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
 * Implements the <a href="http://documents.wolfram.com/v5/Add-onsLinks/
 * StandardPackages/LinearAlgebra/FourierTrig.html">Fast Cosine Transform</a>
 * for transformation of one-dimensional data sets. For reference, see
 * <b>Fast Fourier Transforms</b>, ISBN 0849371635, chapter 3.
 * <p>
 * FCT is its own inverse, up to a multiplier depending on conventions.
 * The equations are listed in the comments of the corresponding methods.</p>
 * <p>
 * Different from FFT and FST, FCT requires the length of data set to be
 * power of 2 plus one. Users should especially pay attention to the
 * function transformation on how this affects the sampling.</p>
 * <p>As of version 2.0 this no longer implements Serializable</p>
 *
 * @version $Id$
 * @since 1.2
 */
public class FastCosineTransformer implements RealTransformer {
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
    public FastCosineTransformer(final boolean orthogonal) {
        this.orthogonal = orthogonal;
    }

    /**
     * <p>
     * Returns a new instance of this class. The returned transformer uses the
     * normalizing conventions described below.
     * <ul>
     * <li>Forward transform:
     * y<sub>n</sub> = (1/2) [x<sub>0</sub> + (-1)<sup>n</sup>x<sub>N-1</sub>]
     * + &sum;<sub>k=1</sub><sup>N-2</sup>
     * x<sub>k</sub> cos[&pi; nk / (N - 1)],</li>
     * <li>Inverse transform:
     * x<sub>k</sub> = [1 / (N - 1)] [y<sub>0</sub>
     * + (-1)<sup>k</sup>y<sub>N-1</sub>]
     * + [2 / (N - 1)] &sum;<sub>n=1</sub><sup>N-2</sup>
     * y<sub>n</sub> cos[&pi; nk / (N - 1)],</li>
     * </ul>
     * where N is the size of the data sample.
     * </p>
     *
     * @return a new DCT transformer, with "standard" normalizing conventions
     */
    public static FastCosineTransformer create() {
        return new FastCosineTransformer(false);
    }

    /**
     * <p>
     * Returns a new instance of this class. The returned transformer uses the
     * normalizing conventions described below.
     * <ul>
     * <li>Forward transform:
     * y<sub>n</sub> = [2(N - 1)]<sup>-1/2</sup> [x<sub>0</sub>
     * + (-1)<sup>n</sup>x<sub>N-1</sub>]
     * + [2 / (N - 1)]<sup>1/2</sup> &sum;<sub>k=1</sub><sup>N-2</sup>
     * x<sub>k</sub> cos[&pi; nk / (N - 1)],</li>
     * <li>Inverse transform:
     * x<sub>k</sub> = [2(N - 1)]<sup>-1/2</sup> [y<sub>0</sub>
     * + (-1)<sup>k</sup>y<sub>N-1</sub>]
     * + [2 / (N - 1)]<sup>1/2</sup> &sum;<sub>n=1</sub><sup>N-2</sup>
     * y<sub>n</sub> cos[&pi; nk / (N - 1)],</li>
     * </ul>
     * which make the transform orthogonal. N is the size of the data sample.
     * </p>
     *
     * @return a new DCT transformer, with "orthogonal" normalizing conventions
     */
    public static FastCosineTransformer createOrthogonal() {
        return new FastCosineTransformer(true);
    }

    /** {@inheritDoc} */
    public double[] transform(double[] f) throws IllegalArgumentException {

        if (orthogonal) {
            final double s = FastMath.sqrt(2.0 / (f.length - 1));
            return FastFourierTransformer.scaleArray(fct(f), s);
        }
        return fct(f);
    }

    /** {@inheritDoc} */
    public double[] transform(UnivariateFunction f,
        double min, double max, int n) throws IllegalArgumentException {

        final double[] data = FastFourierTransformer.sample(f, min, max, n);
        return transform(data);
    }

    /** {@inheritDoc} */
    public double[] inverseTransform(double[] f)
        throws IllegalArgumentException {

        final double s2 = 2.0 / (f.length - 1);
        final double s1 = orthogonal ? FastMath.sqrt(s2) : s2;
        return FastFourierTransformer.scaleArray(fct(f), s1);
    }

    /** {@inheritDoc} */
    public double[] inverseTransform(UnivariateFunction f,
        double min, double max, int n) throws IllegalArgumentException {

        final double[] data = FastFourierTransformer.sample(f, min, max, n);
        return inverseTransform(data);
    }

    /**
     * Perform the FCT algorithm (including inverse).
     *
     * @param f the real data array to be transformed
     * @return the real transformed array
     * @throws IllegalArgumentException if any parameters are invalid
     */
    protected double[] fct(double[] f)
        throws IllegalArgumentException {

        final double[] transformed = new double[f.length];

        final int n = f.length - 1;
        if (!FastFourierTransformer.isPowerOf2(n)) {
            throw MathRuntimeException.createIllegalArgumentException(
                    LocalizedFormats.NOT_POWER_OF_TWO_PLUS_ONE,
                    f.length);
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
