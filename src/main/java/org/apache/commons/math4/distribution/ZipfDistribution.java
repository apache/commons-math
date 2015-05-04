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
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.random.Well19937c;
import org.apache.commons.math4.util.FastMath;

/**
 * Implementation of the Zipf distribution.
 *
 * @see <a href="http://mathworld.wolfram.com/ZipfDistribution.html">Zipf distribution (MathWorld)</a>
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
    /** The sampler to be used for the sample() method */
    private transient ZipfRejectionSampler sampler;

    /**
     * Create a new Zipf distribution with the given number of elements and
     * exponent.
     * <p>
     * <b>Note:</b> this constructor will implicitly create an instance of
     * {@link Well19937c} as random generator to be used for sampling only (see
     * {@link #sample()} and {@link #sample(int)}). In case no sampling is
     * needed for the created distribution, it is advised to pass {@code null}
     * as random generator via the appropriate constructors to avoid the
     * additional initialisation overhead.
     *
     * @param numberOfElements Number of elements.
     * @param exponent Exponent.
     * @exception NotStrictlyPositiveException if {@code numberOfElements <= 0}
     * or {@code exponent <= 0}.
     */
    public ZipfDistribution(final int numberOfElements, final double exponent) {
        this(new Well19937c(), numberOfElements, exponent);
    }

    /**
     * Creates a Zipf distribution.
     *
     * @param rng Random number generator.
     * @param numberOfElements Number of elements.
     * @param exponent Exponent.
     * @exception NotStrictlyPositiveException if {@code numberOfElements <= 0}
     * or {@code exponent <= 0}.
     * @since 3.1
     */
    public ZipfDistribution(RandomGenerator rng,
                            int numberOfElements,
                            double exponent)
        throws NotStrictlyPositiveException {
        super(rng);

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

    /**
     * {@inheritDoc}
     * <p>
     * An instrumental distribution g(k) is used to generate random values by
     * rejection sampling. g(k) is defined as g(1):= 1 and g(k) := I(-s,k-1/2,k+1/2)
     * for k larger than 1, where s denotes the exponent of the Zipf distribution
     * and I(r,a,b) is the integral of x^r for x from a to b.
     * <p>
     * Since 1^x^s is a convex function, Jensens's inequality gives
     * I(-s,k-1/2,k+1/2) >= 1/k^s for all positive k and non-negative s.
     * In order to limit the rejection rate for large exponents s,
     * the instrumental distribution weight is differently defined for value 1.
     */
    @Override
    public int sample() {
        if (sampler == null) {
            sampler = new ZipfRejectionSampler(numberOfElements, exponent);
        }
        return sampler.sample(random);
    }

    /**
     * Utility class implementing a rejection sampling method for a discrete,
     * bounded Zipf distribution.
     *
     * @since 3.6
     */
    static final class ZipfRejectionSampler {

        /** Number of elements. */
        private final int numberOfElements;
        /** Exponent parameter of the distribution. */
        private final double exponent;
        /** Cached tail weight of instrumental distribution used for rejection sampling */
        private double instrumentalDistributionTailWeight = Double.NaN;

        /**
         * Simple constructor.
         * @param numberOfElements number of elements
         * @param exponent exponent parameter of the distribution
         */
        ZipfRejectionSampler(final int numberOfElements, final double exponent) {
            this.numberOfElements = numberOfElements;
            this.exponent = exponent;
        }

        /** Generate a random value sampled from this distribution.
         * @param random random generator to use
         * @return random value sampled from this distribution
         */
        int sample(final RandomGenerator random) {
            if (Double.isNaN(instrumentalDistributionTailWeight)) {
                instrumentalDistributionTailWeight = integratePowerFunction(-exponent, 1.5, numberOfElements+0.5);
            }

            while(true) {
                final double randomValue = random.nextDouble()*(instrumentalDistributionTailWeight + 1.);
                if (randomValue < instrumentalDistributionTailWeight) {
                    final double q = randomValue / instrumentalDistributionTailWeight;
                    final int sample = sampleFromInstrumentalDistributionTail(q);
                    if (random.nextDouble() < acceptanceRateForTailSample(sample)) {
                        return sample;
                    }
                }
                else {
                    return 1;
                }
            }
        }

        /**
         * Returns a sample from the instrumental distribution tail for a given
         * uniformly distributed random value.
         *
         * @param q a uniformly distributed random value taken from [0,1]
         * @return a sample in the range [2, {@link #numberOfElements}]
         */
        int sampleFromInstrumentalDistributionTail(double q) {
            final double a = 1.5;
            final double b = numberOfElements + 0.5;
            final double logBdviA = FastMath.log(b / a);

            final int result  = (int) (a * FastMath.exp(logBdviA * helper1(q, logBdviA * (1. - exponent))) + 0.5);
            if (result < 2) {
                return 2;
            }
            if (result > numberOfElements) {
                return numberOfElements;
            }
            return result;
        }

        /**
         * Helper function that calculates log((1-q)+q*exp(x))/x.
         * <p>
         * A Taylor series expansion is used, if x is close to 0.
         *
         * @param q a value in the range [0,1]
         * @param x free parameter
         * @return log((1-q)+q*exp(x))/x
         */
        static double helper1(final double q, final double x) {
            if (Math.abs(x) > 1e-8) {
                return FastMath.log((1.-q)+q*FastMath.exp(x))/x;
            }
            else {
                return q*(1.+(1./2.)*x*(1.-q)*(1+(1./3.)*x*((1.-2.*q) + (1./4.)*x*(6*q*q*(q-1)+1))));
            }
        }

        /**
         * Helper function to calculate (exp(x)-1)/x.
         * <p>
         * A Taylor series expansion is used, if x is close to 0.
         *
         * @param x free parameter
         * @return (exp(x)-1)/x if x is non-zero, 1 if x=0
         */
        static double helper2(final double x) {
            if (FastMath.abs(x)>1e-8) {
                return FastMath.expm1(x)/x;
            }
            else {
                return 1.+x*(1./2.)*(1.+x*(1./3.)*(1.+x*(1./4.)));
            }
        }

        /**
         * Integrates the power function x^r from x=a to b.
         *
         * @param r the exponent
         * @param a the integral lower bound
         * @param b the integral upper bound
         * @return the calculated integral value
         */
        static double integratePowerFunction(final double r, final double a, final double b) {
            final double logA = FastMath.log(a);
            final double logBdivA = FastMath.log(b/a);
            return FastMath.exp((1.+r)*logA)*helper2((1.+r)*logBdivA)*logBdivA;

        }

        /**
         * Calculates the acceptance rate for a sample taken from the tail of the instrumental distribution.
         * <p>
         * The acceptance rate is given by the ratio k^(-s)/I(-s,k-0.5, k+0.5)
         * where I(r,a,b) is the integral of x^r for x from a to b.
         *
         * @param k the value which has been sampled using the instrumental distribution
         * @return the acceptance rate
         */
        double acceptanceRateForTailSample(int k) {
            final double a = FastMath.log1p(1./(2.*k-1.));
            final double b = FastMath.log1p(2./(2.*k-1.));
            return FastMath.exp((1.-exponent)*a)/(k*b*helper2((1.-exponent)*b));
        }
    }
}
