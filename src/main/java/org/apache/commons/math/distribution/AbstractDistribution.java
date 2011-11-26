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

import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.util.LocalizedFormats;

/**
 * Base class for probability distributions.
 *
 * @version $Id$
 */
public abstract class AbstractDistribution
    implements Distribution, Serializable {

    /** Serializable version identifier */
    private static final long serialVersionUID = -38038050983108802L;

    /** Cached numerical mean */
    private double numericalMean = Double.NaN;

    /** Whether or not the numerical mean has been calculated */
    private boolean numericalMeanIsCalculated = false;

    /** Cached numerical variance */
    private double numericalVariance = Double.NaN;

    /** Whether or not the numerical variance has been calculated */
    private boolean numericalVarianceIsCalculated = false;

    /**
     * Default constructor.
     */
    protected AbstractDistribution() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * The default implementation uses the identity
     * <p>{@code P(x0 < X <= x1) = P(X <= x1) - P(X <= x0)}</p>
     */
    public double cumulativeProbability(double x0, double x1) throws NumberIsTooLargeException {
        if (x0 > x1) {
            throw new NumberIsTooLargeException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT,
                                                x0, x1, true);
        }
        return cumulativeProbability(x1) - cumulativeProbability(x0);
    }

    /**
     * Use this method to actually calculate the mean for the
     * specific distribution. Use {@link #getNumericalMean()}
     * (which implements caching) to actually get the mean.
     *
     * @return the mean or Double.NaN if it's not defined
     */
    protected abstract double calculateNumericalMean();

    /**
     * Use this method to get the numerical value of the mean of this
     * distribution.
     *
     * @return the mean or Double.NaN if it's not defined
     */
    public double getNumericalMean() {
        if (!numericalMeanIsCalculated) {
            numericalMean = calculateNumericalMean();
            numericalMeanIsCalculated = true;
        }

        return numericalMean;
    }

    /**
     * Use this method to actually calculate the variance for the
     * specific distribution.  Use {@link #getNumericalVariance()}
     * (which implements caching) to actually get the variance.
     *
     * @return the variance or Double.NaN if it's not defined
     */
    protected abstract double calculateNumericalVariance();

    /**
     * Use this method to get the numerical value of the variance of this
     * distribution.
     *
     * @return the variance (possibly Double.POSITIVE_INFINITY as
     * for certain cases in {@link TDistribution}) or
     * Double.NaN if it's not defined
     */
    public double getNumericalVariance() {
        if (!numericalVarianceIsCalculated) {
            numericalVariance = calculateNumericalVariance();
            numericalVarianceIsCalculated = true;
        }

        return numericalVariance;
    }

    /**
     * Use this method to get information about whether the lower bound
     * of the support is inclusive or not.
     *
     * @return whether the lower bound of the support is inclusive or not
     */
    public abstract boolean isSupportLowerBoundInclusive();

    /**
     * Use this method to get information about whether the upper bound
     * of the support is inclusive or not.
     *
     * @return whether the upper bound of the support is inclusive or not
     */
    public abstract boolean isSupportUpperBoundInclusive();

    /**
     * Use this method to get information about whether the support is connected,
     * i.e. whether all values between the lower and upper bound of the support
     * is included in the support.
     *
     * For {@link AbstractIntegerDistribution} the support is discrete, so
     * if this is true, then the support is
     * {lower bound, lower bound + 1, ..., upper bound}.
     *
     * For {@link AbstractContinuousDistribution} the support is continuous, so
     * if this is true, then the support is the interval
     * [lower bound, upper bound]
     * where the limits are inclusive or not according to
     * {@link #isSupportLowerBoundInclusive()} and {@link #isSupportUpperBoundInclusive()}
     * (in the example both are true). If both are false, then the support is the interval
     * (lower bound, upper bound)
     *
     * @return whether the support limits given by subclassed methods are connected or not
     */
    public boolean isSupportConnected() {
        return true;
    }
}
