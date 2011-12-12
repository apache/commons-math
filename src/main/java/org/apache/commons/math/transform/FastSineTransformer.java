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
 * StandardPackages/LinearAlgebra/FourierTrig.html">Fast Sine Transform</a>
 * for transformation of one-dimensional data sets. For reference, see
 * <b>Fast Fourier Transforms</b>, ISBN 0849371635, chapter 3.
 * <p>
 * FST is its own inverse, up to a multiplier depending on conventions.
 * The equations are listed in the comments of the corresponding methods.</p>
 * <p>
 * Similar to FFT, we also require the length of data set to be power of 2.
 * In addition, the first element must be 0 and it's enforced in function
 * transformation after sampling.</p>
 * <p>As of version 2.0 this no longer implements Serializable</p>
 *
 * @version $Id$
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
     * normalizing conventions described below.
     * <ul>
     * <li>Forward transform:
     * y<sub>n</sub> = &sum;<sub>k=0</sub><sup>N-1</sup>
     * x<sub>k</sub> sin(&pi; nk / N),</li>
     * <li>Inverse transform:
     * x<sub>k</sub> = (2 / N) &sum;<sub>n=0</sub><sup>N-1</sup>
     * y<sub>n</sub> sin(&pi; nk / N),</li>
     * </ul>
     * where N is the size of the data sample.
     * </p>
     *
     * @return a new DST transformer, with "standard" normalizing conventions
     */
    public static FastSineTransformer create() {
        return new FastSineTransformer(false);
    }

    /**
     * <p>
     * Returns a new instance of this class. The returned transformer uses the
     * normalizing conventions described below.
     * <ul>
     * <li>Forward transform:
     * y<sub>n</sub> = &radic;(2 / N) &sum;<sub>k=0</sub><sup>N-1</sup>
     * x<sub>k</sub> sin(&pi; nk / N),</li>
     * <li>Inverse transform:
     * x<sub>k</sub> = &radic;(2 / N) &sum;<sub>n=0</sub><sup>N-1</sup>
     * y<sub>n</sub> sin(&pi; nk / N),</li>
     * </ul>
     * which make the transform orthogonal. N is the size of the data sample.
     * </p>
     *
     * @return a new DST transformer, with "orthogonal" normalizing conventions
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
        if (orthogonal){
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
