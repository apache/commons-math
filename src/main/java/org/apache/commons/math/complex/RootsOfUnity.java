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
package org.apache.commons.math.complex;

import java.io.Serializable;

import org.apache.commons.math.exception.MathIllegalArgumentException;
import org.apache.commons.math.exception.MathIllegalStateException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * A helper class for the computation and caching of the {@code n}<sup>th</sup>
 * roots of unity.
 *
 * @version $Id$
 * @since 3.0
 */
public class RootsOfUnity implements Serializable {

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