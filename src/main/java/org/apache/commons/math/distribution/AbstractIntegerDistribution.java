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

import org.apache.commons.math.exception.MathInternalError;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.util.FastMath;


/**
 * Base class for integer-valued discrete distributions.  Default
 * implementations are provided for some of the methods that do not vary
 * from distribution to distribution.
 *
 * @version $Id$
 */
public abstract class AbstractIntegerDistribution extends AbstractDistribution
    implements IntegerDistribution, Serializable {
   /** Serializable version identifier */
    private static final long serialVersionUID = -1146319659338487221L;
    /**
     * RandomData instance used to generate samples from the distribution.
     * @since 2.2
     */
    protected final RandomDataImpl randomData = new RandomDataImpl();

    /**
     * Default constructor.
     */
    protected AbstractIntegerDistribution() {}

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(X <= x)}.  In other
     * words, this method represents the (cumulative) distribution function,
     * or CDF, for this distribution.
     * If {@code x} does not represent an integer value, the CDF is
     * evaluated at the greatest integer less than {@code x}.
     *
     * @param x Value at which the distribution function is evaluated.
     * @return the cumulative probability that a random variable with this
     * distribution takes a value less than or equal to {@code x}.
     */
    public double cumulativeProbability(double x) {
        return cumulativeProbability((int) FastMath.floor(x));
    }

    /**
     * For a random variable {@code X} whose values are distributed
     * according to this distribution, this method returns
     * {@code P(x0 <= X <= x1)}.
     *
     * @param x0 Inclusive lower bound.
     * @param x1 Inclusive upper bound.
     * @return the probability that a random variable with this distribution
     * will take a value between {@code x0} and {@code x1},
     * including the endpoints.
     * @throws NumberIsTooSmallException if {@code x1 > x0}.
     */
    @Override
    public double cumulativeProbability(double x0, double x1) {
        if (x1 < x0) {
            throw new NumberIsTooSmallException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT,
                                                x1, x0, true);
        }
        if (FastMath.floor(x0) < x0) {
            return cumulativeProbability(((int) FastMath.floor(x0)) + 1,
               (int) FastMath.floor(x1)); // don't want to count mass below x0
        } else { // x0 is mathematical integer, so use as is
            return cumulativeProbability((int) FastMath.floor(x0),
                (int) FastMath.floor(x1));
        }
    }

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(X <= x)}. In other
     * words, this method represents the probability distribution function,
     * or PDF, for this distribution.
     *
     * @param x Value at which the PDF is evaluated.
     * @return PDF for this distribution.
     */
    public abstract double cumulativeProbability(int x);

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(X = x)}. In other
     * words, this method represents the probability mass function, or PMF,
     * for the distribution.
     * If {@code x} does not represent an integer value, 0 is returned.
     *
     * @param x Value at which the probability density function is evaluated.
     * @return the value of the probability density function at {@code x}.
     */
    public double probability(double x) {
        double fl = FastMath.floor(x);
        if (fl == x) {
            return this.probability((int) x);
        } else {
            return 0;
        }
    }

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(x0 < X < x1)}.
     *
     * @param x0 Inclusive lower bound.
     * @param x1 Inclusive upper bound.
     * @return the cumulative probability.
     * @throws NumberIsTooSmallException {@code if x0 > x1}.
     */
    public double cumulativeProbability(int x0, int x1) {
        if (x1 < x0) {
            throw new NumberIsTooSmallException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT,
                                                x1, x0, true);
        }
        return cumulativeProbability(x1) - cumulativeProbability(x0 - 1);
    }

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns the largest {@code x}, such
     * that {@code P(X <= x) <= p}.
     *
     * @param p Desired probability.
     * @return the largest {@code x} such that {@code P(X < x) <= p}.
     * @throws OutOfRangeException if {@code p < 0} or {@code p > 1}.
     */
    public int inverseCumulativeProbability(final double p) {
        if (p < 0 || p > 1) {
            throw new OutOfRangeException(p, 0, 1);
        }

        // by default, do simple bisection.
        // subclasses can override if there is a better method.
        int x0 = getDomainLowerBound(p);
        int x1 = getDomainUpperBound(p);
        double pm;
        while (x0 < x1) {
            int xm = x0 + (x1 - x0) / 2;
            pm = checkedCumulativeProbability(xm);
            if (pm > p) {
                // update x1
                if (xm == x1) {
                    // this can happen with integer division
                    // simply decrement x1
                    --x1;
                } else {
                    // update x1 normally
                    x1 = xm;
                }
            } else {
                // update x0
                if (xm == x0) {
                    // this can happen with integer division
                    // simply increment x0
                    ++x0;
                } else {
                    // update x0 normally
                    x0 = xm;
                }
            }
        }

        // insure x0 is the correct critical point
        pm = checkedCumulativeProbability(x0);
        while (pm > p) {
            --x0;
            pm = checkedCumulativeProbability(x0);
        }

        return x0;
    }

    /**
     * {@inheritDoc}
     */
    public void reseedRandomGenerator(long seed) {
        randomData.reSeed(seed);
    }

    /**
     * Generates a random value sampled from this distribution. The default
     * implementation uses the
     * <a href="http://en.wikipedia.org/wiki/Inverse_transform_sampling">
     *  inversion method.
     * </a>
     *
     * @return a random value.
     * @since 2.2
     */
    public int sample() {
        return randomData.nextInversionDeviate(this);
    }

    /**
     * Generates a random sample from the distribution.  The default
     * implementation generates the sample by calling {@link #sample()}
     * in a loop.
     *
     * @param sampleSize number of random values to generate.
     * @since 2.2
     * @return an array representing the random sample.
     * @throws NotStrictlyPositiveException if {@code sampleSize <= 0}.
     */
    public int[] sample(int sampleSize) {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES,
                                                   sampleSize);
        }
        int[] out = new int[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            out[i] = sample();
        }
        return out;
    }

    /**
     * Computes the cumulative probability function and checks for NaN
     * values returned.
     * Throws MathInternalError if the value is NaN. Rethrows any Exception encountered
     * evaluating the cumulative probability function. Throws
     * MathInternalError if the cumulative probability function returns NaN.
     *
     * @param argument Input value.
     * @return the cumulative probability.
     * @throws MathInternalError if the cumulative probability is NaN
     */
    private double checkedCumulativeProbability(int argument)
        throws MathInternalError {
        double result = Double.NaN;
            result = cumulativeProbability(argument);
        if (Double.isNaN(result)) {
            throw new MathInternalError(
                    LocalizedFormats.DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN, argument);
        }
        return result;
    }

    /**
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a PDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value
     * @return the domain value lower bound, i.e. {@code P(X < 'lower bound') < p}.
     */
    protected abstract int getDomainLowerBound(double p);

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a PDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value upper bound, i.e. {@code P(X < 'upper bound') > p}.
     */
    protected abstract int getDomainUpperBound(double p);

    /**
     * Access the lower bound of the support.
     *
     * @return lower bound of the support (Integer.MIN_VALUE for negative infinity)
     */
    public abstract int getSupportLowerBound();

    /**
     * Access the upper bound of the support.
     *
     * @return upper bound of the support (Integer.MAX_VALUE for positive infinity)
     */
    public abstract int getSupportUpperBound();

    /**
     * Use this method to get information about whether the lower bound
     * of the support is inclusive or not. For discrete support,
     * only true here is meaningful.
     *
     * @return true (always but at Integer.MIN_VALUE because of the nature of discrete support)
     */
    @Override
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    /**
     * Use this method to get information about whether the upper bound
     * of the support is inclusive or not. For discrete support,
     * only true here is meaningful.
     *
     * @return true (always but at Integer.MAX_VALUE because of the nature of discrete support)
     */
    @Override
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }
}
