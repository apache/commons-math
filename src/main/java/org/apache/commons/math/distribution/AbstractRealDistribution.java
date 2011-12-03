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

import org.apache.commons.math.analysis.UnivariateFunction;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils;
import org.apache.commons.math.exception.MathInternalError;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.util.FastMath;

/**
 * Base class for probability distributions on the reals.
 * Default implementations are provided for some of the methods
 * that do not vary from distribution to distribution.
 *
 * @version $Id$
 * @since 3.0
 */
public abstract class AbstractRealDistribution
implements RealDistribution, Serializable {
    /** Serializable version identifier */
    private static final long serialVersionUID = -38038050983108802L;

    /** Default accuracy. */
    public static final double SOLVER_DEFAULT_ABSOLUTE_ACCURACY = 1e-6;

    /** Solver absolute accuracy for inverse cumulative computation */
    private double solverAbsoluteAccuracy = SOLVER_DEFAULT_ABSOLUTE_ACCURACY;

    /** RandomData instance used to generate samples from the distribution. */
    protected final RandomDataImpl randomData = new RandomDataImpl();

    /** Default constructor. */
    protected AbstractRealDistribution() {}

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

    /** {@inheritDoc} */
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {

        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }

        // by default, do simple root finding using bracketing and default solver.
        // subclasses can override if there is a better method.
        UnivariateFunction rootFindingFunction =
            new UnivariateFunction() {
            public double value(double x) {
                return cumulativeProbability(x) - p;
            }
        };

        // Try to bracket root, test domain endpoints if this fails
        double lowerBound = getDomainLowerBound(p);
        double upperBound = getDomainUpperBound(p);
        double[] bracket = null;
        try {
            bracket = UnivariateRealSolverUtils.bracket(
                    rootFindingFunction, getInitialDomain(p),
                    lowerBound, upperBound);
        } catch (NumberIsTooLargeException ex) {
            /*
             * Check domain endpoints to see if one gives value that is within
             * the default solver's defaultAbsoluteAccuracy of 0 (will be the
             * case if density has bounded support and p is 0 or 1).
             */
            if (FastMath.abs(rootFindingFunction.value(lowerBound)) < getSolverAbsoluteAccuracy()) {
                return lowerBound;
            }
            if (FastMath.abs(rootFindingFunction.value(upperBound)) < getSolverAbsoluteAccuracy()) {
                return upperBound;
            }
            // Failed bracket convergence was not because of corner solution
            throw new MathInternalError(ex);
        }

        // find root
        double root = UnivariateRealSolverUtils.solve(rootFindingFunction,
                // override getSolverAbsoluteAccuracy() to use a Brent solver with
                // absolute accuracy different from the default.
                bracket[0],bracket[1], getSolverAbsoluteAccuracy());
        return root;
    }

    /**
     * Access the initial domain value, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the initial domain value.
     * TODO to be deleted when applying MATH-699
     */
    protected abstract double getInitialDomain(double p);

    /**
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value lower bound, i.e. {@code P(X < 'lower bound') < p}.
     * TODO to be deleted when applying MATH-699
     */
    protected abstract double getDomainLowerBound(double p);

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value upper bound, i.e. {@code P(X < 'upper bound') > p}.
     * TODO to be deleted when applying MATH-699
     */
    protected abstract double getDomainUpperBound(double p);

    /**
     * Returns the solver absolute accuracy for inverse cumulative computation.
     * You can override this method in order to use a Brent solver with an
     * absolute accuracy different from the default.
     *
     * @return the maximum absolute error in inverse cumulative probability estimates
     */
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }

    /** {@inheritDoc} */
    public void reseedRandomGenerator(long seed) {
        randomData.reSeed(seed);
    }

    /**
     * {@inheritDoc}
     * 
     * The default implementation uses the
     * <a href="http://en.wikipedia.org/wiki/Inverse_transform_sampling">
     * inversion method.
     * </a>
     */
    public double sample() {
        return randomData.nextInversionDeviate(this);
    }

    /**
     * {@inheritDoc}
     * 
     * The default implementation generates the sample by calling
     * {@link #sample()} in a loop.
     */
    public double[] sample(int sampleSize) {
        if (sampleSize <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_SAMPLES,
                    sampleSize);
        }
        double[] out = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            out[i] = sample();
        }
        return out;
    }
}
