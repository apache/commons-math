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
package org.apache.commons.math4.legacy.distribution;

import org.apache.commons.statistics.distribution.DiscreteDistribution;
import org.apache.commons.math4.legacy.exception.MathInternalError;
import org.apache.commons.math4.legacy.exception.NumberIsTooLargeException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.distribution.InverseTransformDiscreteSampler;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Base class for integer-valued discrete distributions.  Default
 * implementations are provided for some of the methods that do not vary
 * from distribution to distribution.
 *
 */
public abstract class AbstractIntegerDistribution
    implements DiscreteDistribution {
    /**
     * {@inheritDoc}
     *
     * The default implementation uses the identity
     * <p>{@code P(x0 < X <= x1) = P(X <= x1) - P(X <= x0)}</p>
     *
     * @since 4.0, was previously named cumulativeProbability
     */
    @Override
    public double probability(int x0, int x1) throws NumberIsTooLargeException {
        if (x1 < x0) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT,
                    x0, x1, true);
        }
        return cumulativeProbability(x1) - cumulativeProbability(x0);
    }

    /**
     * {@inheritDoc}
     *
     * The default implementation returns
     * <ul>
     * <li>{@link #getSupportLowerBound()} for {@code p = 0},</li>
     * <li>{@link #getSupportUpperBound()} for {@code p = 1}, and</li>
     * <li>{@link #solveInverseCumulativeProbability(double, int, int)} for
     *     {@code 0 < p < 1}.</li>
     * </ul>
     */
    @Override
    public int inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }

        int lower = getSupportLowerBound();
        if (p == 0.0) {
            return lower;
        }
        if (lower == Integer.MIN_VALUE) {
            if (checkedCumulativeProbability(lower) >= p) {
                return lower;
            }
        } else {
            lower -= 1; // this ensures cumulativeProbability(lower) < p, which
                        // is important for the solving step
        }

        int upper = getSupportUpperBound();
        if (p == 1.0) {
            return upper;
        }

        // use the one-sided Chebyshev inequality to narrow the bracket
        // cf. AbstractRealDistribution.inverseCumulativeProbability(double)
        final double mu = getMean();
        final double sigma = JdkMath.sqrt(getVariance());
        final boolean chebyshevApplies = !(Double.isInfinite(mu) || Double.isNaN(mu) ||
                Double.isInfinite(sigma) || Double.isNaN(sigma) || sigma == 0.0);
        if (chebyshevApplies) {
            double k = JdkMath.sqrt((1.0 - p) / p);
            double tmp = mu - k * sigma;
            if (tmp > lower) {
                lower = ((int) JdkMath.ceil(tmp)) - 1;
            }
            k = 1.0 / k;
            tmp = mu + k * sigma;
            if (tmp < upper) {
                upper = ((int) JdkMath.ceil(tmp)) - 1;
            }
        }

        return solveInverseCumulativeProbability(p, lower, upper);
    }

    /**
     * This is a utility function used by {@link
     * #inverseCumulativeProbability(double)}. It assumes {@code 0 < p < 1} and
     * that the inverse cumulative probability lies in the bracket {@code
     * (lower, upper]}. The implementation does simple bisection to find the
     * smallest {@code p}-quantile {@code inf{x in Z | P(X<=x) >= p}}.
     *
     * @param p the cumulative probability
     * @param lower a value satisfying {@code cumulativeProbability(lower) < p}
     * @param upper a value satisfying {@code p <= cumulativeProbability(upper)}
     * @return the smallest {@code p}-quantile of this distribution
     */
    protected int solveInverseCumulativeProbability(final double p, int lower, int upper) {
        while (lower + 1 < upper) {
            int xm = (lower + upper) / 2;
            if (xm < lower || xm > upper) {
                /*
                 * Overflow.
                 * There will never be an overflow in both calculation methods
                 * for xm at the same time
                 */
                xm = lower + (upper - lower) / 2;
            }

            double pm = checkedCumulativeProbability(xm);
            if (pm >= p) {
                upper = xm;
            } else {
                lower = xm;
            }
        }
        return upper;
    }

    /**
     * Computes the cumulative probability function and checks for {@code NaN}
     * values returned. Throws {@code MathInternalError} if the value is
     * {@code NaN}. Rethrows any exception encountered evaluating the cumulative
     * probability function. Throws {@code MathInternalError} if the cumulative
     * probability function returns {@code NaN}.
     *
     * @param argument input value
     * @return the cumulative probability
     * @throws MathInternalError if the cumulative probability is {@code NaN}
     */
    private double checkedCumulativeProbability(int argument)
        throws MathInternalError {
        final double result = cumulativeProbability(argument);
        if (Double.isNaN(result)) {
            throw new MathInternalError(LocalizedFormats
                    .DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN, argument);
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <p>
     * The default implementation simply computes the logarithm of {@code probability(x)}.
     */
    @Override
    public double logProbability(int x) {
        return JdkMath.log(probability(x));
    }

    /**
     * Utility function for allocating an array and filling it with {@code n}
     * samples generated by the given {@code sampler}.
     *
     * @param n Number of samples.
     * @param sampler Sampler.
     * @return an array of size {@code n}.
     */
    public static int[] sample(int n,
                               DiscreteDistribution.Sampler sampler) {
        final int[] samples = new int[n];
        for (int i = 0; i < n; i++) {
            samples[i] = sampler.sample();
        }
        return samples;
    }

    /**{@inheritDoc} */
    @Override
    public DiscreteDistribution.Sampler createSampler(final UniformRandomProvider rng) {
        // Inversion method distribution sampler.
        return InverseTransformDiscreteSampler.of(rng, this::inverseCumulativeProbability)::sample;
    }
}
