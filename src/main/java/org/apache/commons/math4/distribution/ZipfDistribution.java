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

package org.apache.commons.math4.distribution;

import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.DiscreteSampler;
import org.apache.commons.rng.sampling.distribution.RejectionInversionZipfSampler;

/**
 * Implementation of the Zipf distribution.
 * <p>
 * <strong>Parameters:</strong>
 * For a random variable {@code X} whose values are distributed according to this
 * distribution, the probability mass function is given by
 * <pre>
 *   P(X = k) = H(N,s) * 1 / k^s    for {@code k = 1,2,...,N}.
 * </pre>
 * {@code H(N,s)} is the normalizing constant
 * which corresponds to the generalized harmonic number of order N of s.
 * <p>
 * <ul>
 * <li>{@code N} is the number of elements</li>
 * <li>{@code s} is the exponent</li>
 * </ul>
 * @see <a href="https://en.wikipedia.org/wiki/Zipf's_law">Zipf's law (Wikipedia)</a>
 * @see <a href="https://en.wikipedia.org/wiki/Harmonic_number#Generalized_harmonic_numbers">Generalized harmonic numbers</a>
 */
public class ZipfDistribution extends AbstractIntegerDistribution {
    /** Serializable version identifier. */
    private static final long serialVersionUID = 20150501L;
    /** Number of elements. */
    private final int numberOfElements;
    /** Exponent parameter of the distribution. */
    private final double exponent;
    /** Cached values of the nth generalized harmonic. */
    private final double nthHarmonic;
    /** Cached numerical mean */
    private double numericalMean = Double.NaN;
    /** Whether or not the numerical mean has been calculated */
    private boolean numericalMeanIsCalculated = false;
    /** Cached numerical variance */
    private double numericalVariance = Double.NaN;
    /** Whether or not the numerical variance has been calculated */
    private boolean numericalVarianceIsCalculated = false;

    /**
     * Creates a distribution.
     *
     * @param numberOfElements Number of elements.
     * @param exponent Exponent.
     * @exception NotStrictlyPositiveException if {@code numberOfElements <= 0}
     * or {@code exponent <= 0}.
     */
    public ZipfDistribution(int numberOfElements,
                            double exponent)
        throws NotStrictlyPositiveException {
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
        this.nthHarmonic = generalizedHarmonic(numberOfElements, exponent);
    }

    /**
     * Get the number of elements (e.g. corpus size) for the distribution.
     *
     * @return the number of elements
     */
    public int getNumberOfElements() {
        return numberOfElements;
    }

    /**
     * Get the exponent characterizing the distribution.
     *
     * @return the exponent
     */
    public double getExponent() {
        return exponent;
    }

    /** {@inheritDoc} */
    @Override
    public double probability(final int x) {
        if (x <= 0 || x > numberOfElements) {
            return 0.0;
        }

        return (1.0 / FastMath.pow(x, exponent)) / nthHarmonic;
    }

    /** {@inheritDoc} */
    @Override
    public double logProbability(int x) {
        if (x <= 0 || x > numberOfElements) {
            return Double.NEGATIVE_INFINITY;
        }

        return -FastMath.log(x) * exponent - FastMath.log(nthHarmonic);
    }

    /** {@inheritDoc} */
    @Override
    public double cumulativeProbability(final int x) {
        if (x <= 0) {
            return 0.0;
        } else if (x >= numberOfElements) {
            return 1.0;
        }

        return generalizedHarmonic(x, exponent) / nthHarmonic;
    }

    /**
     * {@inheritDoc}
     *
     * For number of elements {@code N} and exponent {@code s}, the mean is
     * {@code Hs1 / Hs}, where
     * <ul>
     *  <li>{@code Hs1 = generalizedHarmonic(N, s - 1)},</li>
     *  <li>{@code Hs = generalizedHarmonic(N, s)}.</li>
     * </ul>
     */
    @Override
    public double getNumericalMean() {
        if (!numericalMeanIsCalculated) {
            numericalMean = calculateNumericalMean();
            numericalMeanIsCalculated = true;
        }
        return numericalMean;
    }

    /**
     * Used by {@link #getNumericalMean()}.
     *
     * @return the mean of this distribution
     */
    protected double calculateNumericalMean() {
        final int N = getNumberOfElements();
        final double s = getExponent();

        final double Hs1 = generalizedHarmonic(N, s - 1);
        final double Hs = nthHarmonic;

        return Hs1 / Hs;
    }

    /**
     * {@inheritDoc}
     *
     * For number of elements {@code N} and exponent {@code s}, the mean is
     * {@code (Hs2 / Hs) - (Hs1^2 / Hs^2)}, where
     * <ul>
     *  <li>{@code Hs2 = generalizedHarmonic(N, s - 2)},</li>
     *  <li>{@code Hs1 = generalizedHarmonic(N, s - 1)},</li>
     *  <li>{@code Hs = generalizedHarmonic(N, s)}.</li>
     * </ul>
     */
    @Override
    public double getNumericalVariance() {
        if (!numericalVarianceIsCalculated) {
            numericalVariance = calculateNumericalVariance();
            numericalVarianceIsCalculated = true;
        }
        return numericalVariance;
    }

    /**
     * Used by {@link #getNumericalVariance()}.
     *
     * @return the variance of this distribution
     */
    protected double calculateNumericalVariance() {
        final int N = getNumberOfElements();
        final double s = getExponent();

        final double Hs2 = generalizedHarmonic(N, s - 2);
        final double Hs1 = generalizedHarmonic(N, s - 1);
        final double Hs = nthHarmonic;

        return (Hs2 / Hs) - ((Hs1 * Hs1) / (Hs * Hs));
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
     * The upper bound of the support is the number of elements.
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
     * The support of this distribution is connected.
     *
     * @return {@code true}
     */
    @Override
    public boolean isSupportConnected() {
        return true;
    }

    /**{@inheritDoc} */
    @Override
    public IntegerDistribution.Sampler createSampler(final UniformRandomProvider rng) {
        return new IntegerDistribution.Sampler() {
            private final DiscreteSampler sampler =
                new RejectionInversionZipfSampler(rng, numberOfElements, exponent);

            /**{@inheritDoc} */
            @Override
            public int sample() {
                return sampler.sample();
            }
        };
    }
}
