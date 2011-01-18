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

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.NotPositiveException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.FastMath;

/**
 * The default implementation of {@link BinomialDistribution}.
 *
 * @version $Revision$ $Date$
 */
public class BinomialDistributionImpl extends AbstractIntegerDistribution
        implements BinomialDistribution, Serializable {
    /** Serializable version identifier. */
    private static final long serialVersionUID = 6751309484392813623L;
    /** The number of trials. */
    private final int numberOfTrials;
    /** The probability of success. */
    private final double probabilityOfSuccess;

    /**
     * Create a binomial distribution with the given number of trials and
     * probability of success.
     *
     * @param trials Number of trials.
     * @param p Probability of success.
     * @throws NotPositiveException if {@code trials < 0}.
     * @throws OutOfRangeException if {@code p < 0} or {@code p > 1}.
     */
    public BinomialDistributionImpl(int trials, double p) {
        if (trials < 0) {
            throw new NotPositiveException(LocalizedFormats.NUMBER_OF_TRIALS,
                                           trials);
        }
        if (p < 0 || p > 1) {
            throw new OutOfRangeException(p, 0, 1);
        }

        probabilityOfSuccess = p;
        numberOfTrials = trials;
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfTrials() {
        return numberOfTrials;
    }

    /**
     * {@inheritDoc}
     */
    public double getProbabilityOfSuccess() {
        return probabilityOfSuccess;
    }

    /**
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a PDF root.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value lower bound, i.e. {@code P(X < 'lower bound') < p}.
     */
    @Override
    protected int getDomainLowerBound(double p) {
        return -1;
    }

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a PDF root.
     *
     * @param p Desired probability for the critical value
     * @return the domain value upper bound, i.e. {@code P(X < 'upper bound') > p}.
     */
    @Override
    protected int getDomainUpperBound(double p) {
        return numberOfTrials;
    }

    /**
     * For this distribution, {@code X}, this method returns {@code P(X < x)}.
     *
     * @param x Value at which the PDF is evaluated.
     * @return PDF for this distribution.
     * @throws MathException if the cumulative probability can not be computed
     * due to convergence or other numerical errors.
     */
    @Override
    public double cumulativeProbability(int x) throws MathException {
        double ret;
        if (x < 0) {
            ret = 0.0;
        } else if (x >= numberOfTrials) {
            ret = 1.0;
        } else {
            ret = 1.0 - Beta.regularizedBeta(getProbabilityOfSuccess(),
                    x + 1.0, numberOfTrials - x);
        }
        return ret;
    }

    /**
     * For this distribution, {@code X}, this method returns {@code P(X = x)}.
     *
     * @param x Value at which the PMF is evaluated.
     * @return PMF for this distribution.
     */
    public double probability(int x) {
        double ret;
        if (x < 0 || x > numberOfTrials) {
            ret = 0.0;
        } else {
            ret = FastMath.exp(SaddlePointExpansion.logBinomialProbability(x,
                    numberOfTrials, probabilityOfSuccess,
                    1.0 - probabilityOfSuccess));
        }
        return ret;
    }

    /**
     * For this distribution, {@code X}, this method returns the largest
     * {@code x}, such that {@code P(X < x) p}.
     * It will return -1 when p = 0 and {@code Integer.MAX_VALUE} when p = 1.
     *
     * @param p Desired probability.
     * @return the largest {@code x} such that {@code P(X < x) <= p}.
     * @throws MathException if the inverse cumulative probability can not be
     * computed due to convergence or other numerical errors.
     * @throws OutOfRangeException if {@code p < 0} or {@code p > 1}.
     */
    @Override
    public int inverseCumulativeProbability(final double p)
            throws MathException {
        // handle extreme values explicitly
        if (p == 0) {
            return -1;
        }
        if (p == 1) {
            return Integer.MAX_VALUE;
        }

        // use default bisection impl
        return super.inverseCumulativeProbability(p);
    }

    /**
     * {@inheritDoc}
     *
     * The lower bound of the support is always 0 no matter the number of trials
     * and probability parameter.
     *
     * @return lower bound of the support (always 0)
     */
    @Override
    public int getSupportLowerBound() {
        return 0;
    }

    /**
     * {@inheritDoc}
     *
     * The upper bound of the support is the number of trials.
     *
     * @return upper bound of the support (equal to number of trials)
     */
    @Override
    public int getSupportUpperBound() {
        return getNumberOfTrials();
    }

    /**
     * {@inheritDoc}
     *
     * For <code>n</code> number of trials and
     * probability parameter <code>p</code>, the mean is
     * <code>n * p</code>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalMean() {
        return (double)getNumberOfTrials() * getProbabilityOfSuccess();
    }

    /**
     * {@inheritDoc}
     *
     * For <code>n</code> number of trials and
     * probability parameter <code>p</code>, the variance is
     * <code>n * p * (1 - p)</code>
     *
     * @return {@inheritDoc}
     */
    @Override
    protected double calculateNumericalVariance() {
        final double p = getProbabilityOfSuccess();
        return (double)getNumberOfTrials() * p * (1 - p);
    }
}
