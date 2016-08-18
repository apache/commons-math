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

import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.special.Beta;
import org.apache.commons.math4.special.Gamma;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.Precision;

/**
 * Implements the Beta distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Beta_distribution">Beta distribution</a>
 * @since 2.0 (changed to concrete class in 3.0)
 */
public class BetaDistribution extends AbstractRealDistribution {
    /**
     * Default inverse cumulative probability accuracy.
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;
    /** Serializable version identifier. */
    private static final long serialVersionUID = 20160311L;
    /** First shape parameter. */
    private final double alpha;
    /** Second shape parameter. */
    private final double beta;
    /** Normalizing factor used in density computations.*/
    private final double z;
    /** Inverse cumulative probability accuracy. */
    private final double solverAbsoluteAccuracy;

    /**
     * Creates a new instance.
     *
     * @param alpha First shape parameter (must be positive).
     * @param beta Second shape parameter (must be positive).
     */
    public BetaDistribution(double alpha, double beta) {
        this(alpha, beta, DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Creates a new instance.
     *
     * @param alpha First shape parameter (must be positive).
     * @param beta Second shape parameter (must be positive).
     * @param inverseCumAccuracy Maximum absolute error in inverse
     * cumulative probability estimates (defaults to
     * {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY}).
     *
     * @since 3.1
     */
    public BetaDistribution(double alpha,
                            double beta,
                            double inverseCumAccuracy) {
        this.alpha = alpha;
        this.beta = beta;
        z = Gamma.logGamma(alpha) + Gamma.logGamma(beta) - Gamma.logGamma(alpha + beta);
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * Access the first shape parameter, {@code alpha}.
     *
     * @return the first shape parameter.
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Access the second shape parameter, {@code beta}.
     *
     * @return the second shape parameter.
     */
    public double getBeta() {
        return beta;
    }

    /** {@inheritDoc} */
    @Override
    public double density(double x) {
        final double logDensity = logDensity(x);
        return logDensity == Double.NEGATIVE_INFINITY ? 0 : FastMath.exp(logDensity);
    }

    /** {@inheritDoc} **/
    @Override
    public double logDensity(double x) {
        if (x < 0 || x > 1) {
            return Double.NEGATIVE_INFINITY;
        } else if (x == 0) {
            if (alpha < 1) {
                throw new NumberIsTooSmallException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA,
                                                    alpha, 1, false);
            }
            return Double.NEGATIVE_INFINITY;
        } else if (x == 1) {
            if (beta < 1) {
                throw new NumberIsTooSmallException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA,
                                                    beta, 1, false);
            }
            return Double.NEGATIVE_INFINITY;
        } else {
            double logX = FastMath.log(x);
            double log1mX = FastMath.log1p(-x);
            return (alpha - 1) * logX + (beta - 1) * log1mX - z;
        }
    }

    /** {@inheritDoc} */
    @Override
    public double cumulativeProbability(double x)  {
        if (x <= 0) {
            return 0;
        } else if (x >= 1) {
            return 1;
        } else {
            return Beta.regularizedBeta(x, alpha, beta);
        }
    }

    /**
     * Return the absolute accuracy setting of the solver used to estimate
     * inverse cumulative probabilities.
     *
     * @return the solver absolute accuracy.
     * @since 2.1
     */
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }

    /**
     * {@inheritDoc}
     *
     * For first shape parameter {@code alpha} and second shape parameter
     * {@code beta}, the mean is {@code alpha / (alpha + beta)}.
     */
    @Override
    public double getNumericalMean() {
        final double a = getAlpha();
        return a / (a + getBeta());
    }

    /**
     * {@inheritDoc}
     *
     * For first shape parameter {@code alpha} and second shape parameter
     * {@code beta}, the variance is
     * {@code (alpha * beta) / [(alpha + beta)^2 * (alpha + beta + 1)]}.
     */
    @Override
    public double getNumericalVariance() {
        final double a = getAlpha();
        final double b = getBeta();
        final double alphabetasum = a + b;
        return (a * b) / ((alphabetasum * alphabetasum) * (alphabetasum + 1));
    }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always 0 no matter the parameters.
     *
     * @return lower bound of the support (always 0)
     */
    @Override
    public double getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * The upper bound of the support is always 1 no matter the parameters.
     *
     * @return upper bound of the support (always 1)
     */
    @Override
    public double getSupportUpperBound() {
        return 1;
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
     *
     * Sampling is performed using Cheng's algorithm:
     * <blockquote>
     * <pre>
     * R. C. H. Cheng,
     * "Generating beta variates with nonintegral shape parameters",
     * Communications of the ACM, 21, 317-322, 1978.
     * </pre>
     * </blockquote>
     */
    @Override
    public RealDistribution.Sampler createSampler(final UniformRandomProvider rng) {
        return new ChengBetaSampler(rng, alpha, beta);
    }

    /**
     * Utility class implementing Cheng's algorithms for beta distribution sampling.
     *
     * <blockquote>
     * <pre>
     * R. C. H. Cheng,
     * "Generating beta variates with nonintegral shape parameters",
     * Communications of the ACM, 21, 317-322, 1978.
     * </pre>
     * </blockquote>
     *
     * @since 3.6
     */
    private static class ChengBetaSampler implements RealDistribution.Sampler {
        /** RNG (uniform distribution). */
        private final UniformRandomProvider rng;
        /** First shape parameter. */
        private final double alphaShape;
        /** Second shape parameter. */
        private final double betaShape;

        /**
         * Creates a sampler instance.
         *
         * @param generator RNG.
         * @param alpha Distribution first shape parameter.
         * @param beta Distribution second shape parameter.
         */
        ChengBetaSampler(UniformRandomProvider generator,
                         double alpha,
                         double beta) {
            rng = generator;
            alphaShape = alpha;
            betaShape = beta;
        }

        /** {@inheritDoc} */
        @Override
        public double sample() {
            final double a = FastMath.min(alphaShape, betaShape);
            final double b = FastMath.max(alphaShape, betaShape);

            if (a > 1) {
                return algorithmBB(alphaShape, a, b);
            } else {
                return algorithmBC(alphaShape, b, a);
            }
        }

        /**
         * Computes one sample using Cheng's BB algorithm, when &alpha; and
         * &beta; are both larger than 1.
         *
         * @param a0 First shape parameter (&alpha;).
         * @param a Min(&alpha;, &beta;) where &alpha;, &beta; are the shape parameters.
         * @param b Max(&alpha;, &beta;) where &alpha;, &beta; are the shape parameters.
         * @return a random sample.
         */
        private double algorithmBB(double a0,
                                   double a,
                                   double b) {
            final double alpha = a + b;
            final double beta = FastMath.sqrt((alpha - 2) / (2 * a * b - alpha));
            final double gamma = a + 1 / beta;

            double r;
            double w;
            double t;
            do {
                final double u1 = rng.nextDouble();
                final double u2 = rng.nextDouble();
                final double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                w = a * FastMath.exp(v);
                final double z = u1 * u1 * u2;
                r = gamma * v - 1.3862944;
                final double s = a + r - w;
                if (s + 2.609438 >= 5 * z) {
                    break;
                }

                t = FastMath.log(z);
                if (s >= t) {
                    break;
                }
            } while (r + alpha * (FastMath.log(alpha) - FastMath.log(b + w)) < t);

            w = FastMath.min(w, Double.MAX_VALUE);

            return Precision.equals(a, a0) ? w / (b + w) : b / (b + w);
        }

        /**
         * Computes one sample using Cheng's BC algorithm, when at least one
         * of &alpha; and &beta; is smaller than 1.
         *
         * @param a0 First shape parameter (&alpha;).
         * @param a Max(&alpha;, &beta;) where &alpha;, &beta; are the shape parameters.
         * @param b min(&alpha;, &beta;) where &alpha;, &beta; are the shape parameters.
         * @return a random sample.
         */
        private double algorithmBC(double a0,
                                   double a,
                                   double b) {
            final double alpha = a + b;
            final double beta = 1 / b;
            final double delta = 1 + a - b;
            final double k1 = delta * (0.0138889 + 0.0416667 * b) / (a * beta - 0.777778);
            final double k2 = 0.25 + (0.5 + 0.25 / delta) * b;

            double w;
            while (true) {
                final double u1 = rng.nextDouble();
                final double u2 = rng.nextDouble();
                final double y = u1 * u2;
                final double z = u1 * y;
                if (u1 < 0.5) {
                    if (0.25 * u2 + z - y >= k1) {
                        continue;
                    }
                } else {
                    if (z <= 0.25) {
                        final double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                        w = a * FastMath.exp(v);
                        break;
                    }

                    if (z >= k2) {
                        continue;
                    }
                }

                final double v = beta * (FastMath.log(u1) - FastMath.log1p(-u1));
                w = a * FastMath.exp(v);
                if (alpha * (FastMath.log(alpha) - FastMath.log(b + w) + v) - 1.3862944 >= FastMath.log(z)) {
                    break;
                }
            }

            w = FastMath.min(w, Double.MAX_VALUE);

            return Precision.equals(a, a0) ? w / (b + w) : b / (b + w);
        }
    }
}
