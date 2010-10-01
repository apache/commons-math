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
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.FastMath;

/**
 * Default implementation of
 * {@link org.apache.commons.math.distribution.FDistribution}.
 *
 * @version $Revision$ $Date$
 */
public class FDistributionImpl
    extends AbstractContinuousDistribution
    implements FDistribution, Serializable  {
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
     * @throws NotStrictlyPositiveException if {@code numeratorDegreesOfFreedom <= 0}
     * or {@code denominatorDegreesOfFreedom <= 0}.
     */
    public FDistributionImpl(double numeratorDegreesOfFreedom,
                             double denominatorDegreesOfFreedom) {
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
     * @throws NotStrictlyPositiveException if {@code numeratorDegreesOfFreedom <= 0}
     * or {@code denominatorDegreesOfFreedom <= 0}.
     * @since 2.1
     */
    public FDistributionImpl(double numeratorDegreesOfFreedom,
                             double denominatorDegreesOfFreedom,
                             double inverseCumAccuracy) {
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
     * Returns the probability density for a particular point.
     *
     * @param x The point at which the density should be computed.
     * @return The pdf at point x.
     * @since 2.1
     */
    @Override
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
     * For this distribution, {@code X}, this method returns {@code P(X < x)}.
     *
     * The implementation of this method is based on
     * <ul>
     *  <li>
     *   <a href="http://mathworld.wolfram.com/F-Distribution.html">
     *   F-Distribution</a>, equation (4).
     *  </li>
     * </ul>
     *
     * @param x Value at which the CDF is evaluated.
     * @return CDF for this distribution.
     * @throws MathException if the cumulative probability cannot be
     * computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x) throws MathException {
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
     * For this distribution, {@code X}, this method returns the critical
     * point {@code x}, such that {@code P(X < x) = p}.
     * Returns 0 when p = 0 and {@code Double.POSITIVE_INFINITY} when p = 1.
     *
     * @param p Desired probability.
     * @return {@code x}, such that {@code P(X < x) = p}.
     * @throws MathException if the inverse cumulative probability cannot be
     * computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if {@code p} is not a valid
     * probability.
     */
    @Override
    public double inverseCumulativeProbability(final double p)
        throws MathException {
        if (p == 0) {
            return 0;
        }
        if (p == 1) {
            return Double.POSITIVE_INFINITY;
        }
        return super.inverseCumulativeProbability(p);
    }

    /**
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value lower bound, i.e. {@code P(X < 'lower bound') < p}.
     */
    @Override
    protected double getDomainLowerBound(double p) {
        return 0;
    }

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value upper bound, i.e. {@code P(X < 'upper bound') > p}.
     */
    @Override
    protected double getDomainUpperBound(double p) {
        return Double.MAX_VALUE;
    }

    /**
     * Access the initial domain value, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the initial domain value.
     */
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
     * {@inheritDoc}
     */
    public double getNumeratorDegreesOfFreedom() {
        return numeratorDegreesOfFreedom;
    }

    /**
     * {@inheritDoc}
     */
    public double getDenominatorDegreesOfFreedom() {
        return denominatorDegreesOfFreedom;
    }

    /**
     * Return the absolute accuracy setting of the solver used to estimate
     * inverse cumulative probabilities.
     *
     * @return the solver absolute accuracy
     * @since 2.1
     */
    @Override
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }
}
