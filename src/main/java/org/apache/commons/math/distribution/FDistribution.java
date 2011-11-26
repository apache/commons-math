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
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.FastMath;

/**
 * Implementation of the F-distribution.
 *
 * @see <a href="http://en.wikipedia.org/wiki/F-distribution">F-distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/F-Distribution.html">F-distribution (MathWorld)</a>
 * @version $Id$
 */
public class FDistribution
    extends AbstractContinuousDistribution
    implements Serializable  {
    /**
     * Default inverse cumulative probability accuracy.
     * @since 2.1
     */
    public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1e-9;
    /** Serializable version identifier. */
    private static final long serialVersionUID = -8516354193418641566L;
    /** The numerator degrees of freedom. */
    private final double numeratorDegreesOfFreedom;
    /** The numerator degrees of freedom. */
    private final double denominatorDegreesOfFreedom;
    /** Inverse cumulative probability accuracy. */
    private final double solverAbsoluteAccuracy;

    /**
     * Create a F distribution using the given degrees of freedom.
     * @param numeratorDegreesOfFreedom Numerator degrees of freedom.
     * @param denominatorDegreesOfFreedom Denominator degrees of freedom.
     * @throws NotStrictlyPositiveException if
     * {@code numeratorDegreesOfFreedom <= 0} or
     * {@code denominatorDegreesOfFreedom <= 0}.
     */
    public FDistribution(double numeratorDegreesOfFreedom,
                             double denominatorDegreesOfFreedom)
        throws NotStrictlyPositiveException {
        this(numeratorDegreesOfFreedom, denominatorDegreesOfFreedom,
             DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
    }

    /**
     * Create an F distribution using the given degrees of freedom
     * and inverse cumulative probability accuracy.
     * @param numeratorDegreesOfFreedom Numerator degrees of freedom.
     * @param denominatorDegreesOfFreedom Denominator degrees of freedom.
     * @param inverseCumAccuracy the maximum absolute error in inverse
     * cumulative probability estimates.
     * (defaults to {@link #DEFAULT_INVERSE_ABSOLUTE_ACCURACY})
     * @throws NotStrictlyPositiveException if
     * {@code numeratorDegreesOfFreedom <= 0} or
     * {@code denominatorDegreesOfFreedom <= 0}.
     * @since 2.1
     */
    public FDistribution(double numeratorDegreesOfFreedom,
                             double denominatorDegreesOfFreedom,
                             double inverseCumAccuracy)
        throws NotStrictlyPositiveException {
        if (numeratorDegreesOfFreedom <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DEGREES_OF_FREEDOM,
                                                   numeratorDegreesOfFreedom);
        }
        if (denominatorDegreesOfFreedom <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.DEGREES_OF_FREEDOM,
                                                   denominatorDegreesOfFreedom);
        }
        this.numeratorDegreesOfFreedom = numeratorDegreesOfFreedom;
        this.denominatorDegreesOfFreedom = denominatorDegreesOfFreedom;
        solverAbsoluteAccuracy = inverseCumAccuracy;
    }

    /**
     * {@inheritDoc}
     *
     * @since 2.1
     */
    public double density(double x) {
        final double nhalf = numeratorDegreesOfFreedom / 2;
        final double mhalf = denominatorDegreesOfFreedom / 2;
        final double logx = FastMath.log(x);
        final double logn = FastMath.log(numeratorDegreesOfFreedom);
        final double logm = FastMath.log(denominatorDegreesOfFreedom);
        final double lognxm = FastMath.log(numeratorDegreesOfFreedom * x +
                                           denominatorDegreesOfFreedom);
        return FastMath.exp(nhalf * logn + nhalf * logx - logx +
                            mhalf * logm - nhalf * lognxm - mhalf * lognxm -
                            Beta.logBeta(nhalf, mhalf));
    }

    /**
     * {@inheritDoc}
     *
     * The implementation of this method is based on
     * <ul>
     *  <li>
     *   <a href="http://mathworld.wolfram.com/F-Distribution.html">
     *   F-Distribution</a>, equation (4).
     *  </li>
     * </ul>
     */
    public double cumulativeProbability(double x)  {
        double ret;
        if (x <= 0) {
            ret = 0;
        } else {
            double n = numeratorDegreesOfFreedom;
            double m = denominatorDegreesOfFreedom;

            ret = Beta.regularizedBeta((n * x) / (m + n * x),
                0.5 * n,
                0.5 * m);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     *
     * Returns {@code 0} when {@code p == 0} and
     * {@code Double.POSITIVE_INFINITY} when {@code p == 1}.
     */
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p == 0) {
            return 0;
        }
        if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        return super.inverseCumulativeProbability(p);
    }

    /** {@inheritDoc} */
    @Override
    protected double getDomainLowerBound(double p) {
        return 0;
    }

    /** {@inheritDoc} */
    @Override
    protected double getDomainUpperBound(double p) {
        return Double.MAX_VALUE;
    }

    /** {@inheritDoc} */
    @Override
    protected double getInitialDomain(double p) {
        double ret = 1;
        double d = denominatorDegreesOfFreedom;
        if (d > 2) {
            // use mean
            ret = d / (d - 2);
        }
        return ret;
    }

    /**
     * Access the numerator degrees of freedom.
     *
     * @return the numerator degrees of freedom.
     */
    public double getNumeratorDegreesOfFreedom() {
        return numeratorDegreesOfFreedom;
    }

    /**
     * Access the denominator degrees of freedom.
     *
     * @return the denominator degrees of freedom.
     */
    public double getDenominatorDegreesOfFreedom() {
        return denominatorDegreesOfFreedom;
    }

    /** {@inheritDoc} */
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
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
     * The upper bound of the support is always positive infinity
     * no matter the parameters.
     *
     * @return upper bound of the support (always Double.POSITIVE_INFINITY)
     */
    @Override
    public double getSupportUpperBound() {
        return Double.POSITIVE_INFINITY;
    }

    /**
     * {@inheritDoc}
     *
     * For denominator degrees of freedom parameter {@code b}, the mean is
     * <ul>
     *  <li>if {@code b > 2} then {@code b / (b - 2)},</li>
     *  <li>else undefined ({@code Double.NaN}).
     * </ul>
     */
    @Override
    protected double calculateNumericalMean() {
        final double denominatorDF = getDenominatorDegreesOfFreedom();

        if (denominatorDF > 2) {
            return denominatorDF / (denominatorDF - 2);
        }

        return Double.NaN;
    }

    /**
     * {@inheritDoc}
     *
     * For numerator degrees of freedom parameter {@code a} and denominator
     * degrees of freedom parameter {@code b}, the variance is
     * <ul>
     *  <li>
     *    if {@code b > 4} then
     *    {@code [2 * b^2 * (a + b - 2)] / [a * (b - 2)^2 * (b - 4)]},
     *  </li>
     *  <li>else undefined ({@code Double.NaN}).
     * </ul>
     */
    @Override
    protected double calculateNumericalVariance() {
        final double denominatorDF = getDenominatorDegreesOfFreedom();

        if (denominatorDF > 4) {
            final double numeratorDF = getNumeratorDegreesOfFreedom();
            final double denomDFMinusTwo = denominatorDF - 2;

            return ( 2 * (denominatorDF * denominatorDF) * (numeratorDF + denominatorDF - 2) ) /
                   ( (numeratorDF * (denomDFMinusTwo * denomDFMinusTwo) * (denominatorDF - 4)) );
        }

        return Double.NaN;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSupportUpperBoundInclusive() {
        return false;
    }
}
