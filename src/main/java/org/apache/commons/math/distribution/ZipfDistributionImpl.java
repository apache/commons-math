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

package org.apache.commons.math.distribution;

import java.io.Serializable;

import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;

/**
 * Implementation for the {@link ZipfDistribution}.
 *
 * @version $Revision$ $Date$
 */
public class ZipfDistributionImpl extends AbstractIntegerDistribution
    implements ZipfDistribution, Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = -140627372283420404L;
    /** Number of elements. */
    private final int numberOfElements;
    /** Exponent parameter of the distribution. */
    private final double exponent;

    /**
     * Create a new Zipf distribution with the given number of elements and
     * exponent.
     *
     * @param numberOfElements Number of elements.
     * @param exponent Exponent.
     * @exception NotStrictlyPositiveException if {@code numberOfElements <= 0}
     * or {@code exponent <= 0}.
     */
    public ZipfDistributionImpl(final int numberOfElements,
                                final double exponent) {
        if (numberOfElements <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DIMENSION,
                                                   numberOfElements);
        }
        if (exponent <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.EXPONENT,
                                                   exponent);
        }

        this.numberOfElements = numberOfElements;
        this.exponent = exponent;
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfElements() {
        return numberOfElements;
    }

    /**
     * {@inheritDoc}
     */
    public double getExponent() {
        return exponent;
    }

    /**
     * The probability mass function {@code P(X = x)} for a Zipf distribution.
     *
     * @param x Value at which the probability density function is evaluated.
     * @return the value of the probability mass function at {@code x}.
     */
    public double probability(final int x) {
        if (x <= 0 || x > numberOfElements) {
            return 0.0;
        }

        return (1.0 / FastMath.pow(x, exponent)) / generalizedHarmonic(numberOfElements, exponent);
    }

    /**
     * The probability distribution function {@code P(X <= x)} for a
     * Zipf distribution.
     *
     * @param x Value at which the PDF is evaluated.
     * @return Zipf distribution function evaluated at {@code x}.
     */
    @Override
    public double cumulativeProbability(final int x) {
        if (x <= 0) {
            return 0.0;
        } else if (x >= numberOfElements) {
            return 1.0;
        }

        return generalizedHarmonic(x, exponent) / generalizedHarmonic(numberOfElements, exponent);
    }

    /**
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a PDF root.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value lower bound, i.e. {@code P(X < 'lower bound') < p}.
     */
    @Override
    protected int getDomainLowerBound(final double p) {
        return 0;
    }

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a PDF root.
     *
     * @param p Desired probability for the critical value
     * @return the domain value upper bound, i.e. {@code P(X < 'upper bound') > p}.
     */
    @Override
    protected int getDomainUpperBound(final double p) {
        return numberOfElements;
    }

    /**
     * Calculates the Nth generalized harmonic number. See
     * <a href="http://mathworld.wolfram.com/HarmonicSeries.html">Harmonic
     * Series</a>.
     *
     * @param n Term in the series to calculate (must be larger than 1)
     * @param m Exponent (special case {@code m = 1} is the harmonic series).
     * @return the n<sup>th</sup> generalized harmonic number.
     */
    private double generalizedHarmonic(final int n, final double m) {
        double value = 0;
        for (int k = n; k > 0; --k) {
            value += 1.0 / FastMath.pow(k, m);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always 1 no matter the parameters.
     *
     * @return lower bound of the support (always 1)
     */
    @Override
    public int getSupportLowerBound() {
        return 1;
    }

    /**
     * {@inheritDoc}
     *
     * The upper bound of the support is the number of elements
     *
     * @return upper bound of the support
     */
    @Override
    public int getSupportUpperBound() {
        return getNumberOfElements();
    }

    /**
     * {@inheritDoc}
     *
     * For number of elements N and exponent s, the mean is
     * <code>Hs1 / Hs</code> where
     * <ul>
     *  <li><code>Hs1 = generalizedHarmonic(N, s - 1)</code></li>
     *  <li><code>Hs = generalizedHarmonic(N, s)</code></li>
     * </ul>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalMean() {
        final int N = getNumberOfElements();
        final double s = getExponent();

        final double Hs1 = generalizedHarmonic(N, s - 1);
        final double Hs = generalizedHarmonic(N, s);

        return Hs1 / Hs;
    }

    /**
     * {@inheritDoc}
     *
     * For number of elements N and exponent s, the mean is
     * <code>(Hs2 / Hs) - (Hs1^2 / Hs^2)</code> where
     * <ul>
     *  <li><code>Hs2 = generalizedHarmonic(N, s - 2)</code></li>
     *  <li><code>Hs1 = generalizedHarmonic(N, s - 1)</code></li>
     *  <li><code>Hs = generalizedHarmonic(N, s)</code></li>
     * </ul>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalVariance() {
        final int N = getNumberOfElements();
        final double s = getExponent();

        final double Hs2 = generalizedHarmonic(N, s - 2);
        final double Hs1 = generalizedHarmonic(N, s - 1);
        final double Hs = generalizedHarmonic(N, s);

        return (Hs2 / Hs) - ((Hs1 * Hs1) / (Hs * Hs));
    }
}
