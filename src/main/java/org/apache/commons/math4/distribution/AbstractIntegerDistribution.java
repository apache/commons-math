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

import java.io.Serializable;

import org.apache.commons.math4.exception.MathInternalError;
import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.exception.NumberIsTooLargeException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.random.RandomGenerator;
import org.apache.commons.math4.util.FastMath;

/**
 * Base class for integer-valued discrete distributions.  Default
 * implementations are provided for some of the methods that do not vary
 * from distribution to distribution.
 *
 */
public abstract class AbstractIntegerDistribution implements IntegerDistribution, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -1146319659338487221L;

    /**
     * RNG instance used to generate samples from the distribution.
     * @since 3.1
     */
    protected final RandomGenerator random;

    /**
     * @param rng Random number generator.
     * @since 3.1
     */
    protected AbstractIntegerDistribution(RandomGenerator rng) {
        random = rng;
    }

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
        final double mu = getNumericalMean();
        final double sigma = FastMath.sqrt(getNumericalVariance());
        final boolean chebyshevApplies = !(Double.isInfinite(mu) || Double.isNaN(mu) ||
                Double.isInfinite(sigma) || Double.isNaN(sigma) || sigma == 0.0);
        if (chebyshevApplies) {
            double k = FastMath.sqrt((1.0 - p) / p);
            double tmp = mu - k * sigma;
            if (tmp > lower) {
                lower = ((int) FastMath.ceil(tmp)) - 1;
            }
            k = 1.0 / k;
            tmp = mu + k * sigma;
            if (tmp < upper) {
                upper = ((int) FastMath.ceil(tmp)) - 1;
            }
        }

        return solveInverseCumulativeProbability(p, lower, upper);
    }

    /**
     * This is a utility function used by {@link
     * #inverseCumulativeProbability(double)}. It assumes {@code 0 < p < 1} and
     * that the inverse cumulative probability lies in the bracket {@code
     * (lower, upper]}. The implementation does simple bisection to find the
     * smallest {@code p}-quantile <code>inf{x in Z | P(X<=x) >= p}</code>.
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

    /** {@inheritDoc} */
    @Override
    public void reseedRandomGenerator(long seed) {
        random.setSeed(seed);
    }

    /**
     * {@inheritDoc}
     *
     * The default implementation uses the
     * <a href="http://en.wikipedia.org/wiki/Inverse_transform_sampling">
     * inversion method</a>.
     */
    @Override
    public int sample() {
        return inverseCumulativeProbability(random.nextDouble());
    }

    /**
     * {@inheritDoc}
     *
     * The default implementation generates the sample by calling
     * {@link #sample()} in a loop.
     */
    @Override
    public int[] sample(int sampleSize) {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(
                    LocalizedFormats.NUMBER_OF_SAMPLES, sampleSize);
        }
        int[] out = new int[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            out[i] = sample();
        }
        return out;
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
        double result = Double.NaN;
        result = cumulativeProbability(argument);
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
        return FastMath.log(probability(x));
    }
}
