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

/**
 * Base class for integer-valued discrete distributions.  Default
 * implementations are provided for some of the methods that do not vary
 * from distribution to distribution.
 *
 * @version $Id$
 */
public abstract class AbstractIntegerDistribution
implements IntegerDistribution, Serializable {
    /** Serializable version identifier */
    private static final long serialVersionUID = -1146319659338487221L;

    /**
     * RandomData instance used to generate samples from the distribution.
     */
    protected final RandomDataImpl randomData = new RandomDataImpl();

    /** Default constructor. */
    protected AbstractIntegerDistribution() { }

    /**
     * {@inheritDoc}
     *
     * The default implementation uses the identity
     * <p>{@code P(x0 <= X <= x1) = P(X <= x1) - P(X <= x0 - 1)}</p>
     */
    public double cumulativeProbability(int x0, int x1) {
        if (x1 < x0) {
            throw new NumberIsTooSmallException(
                    LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT,
                    x1, x0, true);
        }
        return cumulativeProbability(x1) - cumulativeProbability(x0 - 1);
    }

    /** {@inheritDoc} */
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
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a CDF root. This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value ({@code 0 < p < 1})
     * @return a domain value lower bound, i.e. a value {@code x} such that
     * {@code P(X <= x) < p}
     */
    protected abstract int getDomainLowerBound(double p);

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a CDF root. This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p the desired probability for the critical value ({@code 0 < p < 1})
     * @return a domain value upper bound, i.e. a value {@code x} such that
     * {@code P(X <= x) >= p}
     */
    protected abstract int getDomainUpperBound(double p);

    /** {@inheritDoc} */
    public void reseedRandomGenerator(long seed) {
        randomData.reSeed(seed);
    }

    /**
     * {@inheritDoc}
     *
     * The default implementation uses the
     * <a href="http://en.wikipedia.org/wiki/Inverse_transform_sampling">
     * inversion method</a>.
     */
    public int sample() {
        return randomData.nextInversionDeviate(this);
    }

    /**
     * {@inheritDoc}
     *
     * The default implementation generates the sample by calling
     * {@link #sample()} in a loop.
     */
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
}
