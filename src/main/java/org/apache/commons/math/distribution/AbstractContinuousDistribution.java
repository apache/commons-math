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
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.solvers.BrentSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils;
import org.apache.commons.math.exception.MathUserException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.exception.NumberIsTooLargeException;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.util.FastMath;

/**
 * Base class for continuous distributions.  Default implementations are
 * provided for some of the methods that do not vary from distribution to
 * distribution.
 *
 * @version $Revision$ $Date$
 */
public abstract class AbstractContinuousDistribution
    extends AbstractDistribution
    implements ContinuousDistribution, Serializable {
    /** Serializable version identifier */
    private static final long serialVersionUID = -38038050983108802L;
    /**
     * RandomData instance used to generate samples from the distribution
     * @since 2.2
     */
    protected final RandomDataImpl randomData = new RandomDataImpl();
    /**
     * Solver absolute accuracy for inverse cumulative computation
     * @since 2.1
     */
    private double solverAbsoluteAccuracy = BrentSolver.DEFAULT_ABSOLUTE_ACCURACY;
    /**
     * Default constructor.
     */
    protected AbstractContinuousDistribution() {}

    /**
     * {@inheritDoc}
     */
    public abstract double density(double x);

    /**
     * For this distribution, {@code X}, this method returns the critical
     * point {@code x}, such that {@code P(X < x) = p}.
     *
     * @param p Desired probability.
     * @return {@code x}, such that {@code P(X < x) = p}.
     * @throws MathException if the inverse cumulative probability can not be
     * computed due to convergence or other numerical errors.
     * @throws OutOfRangeException if {@code p} is not a valid probability.
     */
    public double inverseCumulativeProbability(final double p)
        throws MathException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }

        // by default, do simple root finding using bracketing and default solver.
        // subclasses can override if there is a better method.
        UnivariateRealFunction rootFindingFunction =
            new UnivariateRealFunction() {
            public double value(double x) throws MathUserException {
                double ret = Double.NaN;
                try {
                    ret = cumulativeProbability(x) - p;
                } catch (MathException ex) {
                    throw new MathUserException(ex,
                                                ex.getSpecificPattern(), ex.getGeneralPattern(),
                                                ex.getArguments());
                }
                if (Double.isNaN(ret)) {
                    throw new MathUserException(LocalizedFormats.CUMULATIVE_PROBABILITY_RETURNED_NAN, x, p);
                }
                return ret;
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
            throw new MathException(ex);
        }

        // find root
        double root = UnivariateRealSolverUtils.solve(rootFindingFunction,
                // override getSolverAbsoluteAccuracy() to use a Brent solver with
                // absolute accuracy different from BrentSolver default
                bracket[0],bracket[1], getSolverAbsoluteAccuracy());
        return root;
    }

    /**
     * Reseed the random generator used to generate samples.
     *
     * @param seed New seed.
     * @since 2.2
     */
    public void reseedRandomGenerator(long seed) {
        randomData.reSeed(seed);
    }

    /**
     * Generate a random value sampled from this distribution. The default
     * implementation uses the
     * <a href="http://en.wikipedia.org/wiki/Inverse_transform_sampling">
     *  inversion method.
     * </a>
     *
     * @return a random value.
     * @throws MathException if an error occurs generating the random value.
     * @since 2.2
     */
    public double sample() throws MathException {
        return randomData.nextInversionDeviate(this);
    }

    /**
     * Generate a random sample from the distribution.  The default implementation
     * generates the sample by calling {@link #sample()} in a loop.
     *
     * @param sampleSize Number of random values to generate.
     * @return an array representing the random sample.
     * @throws MathException if an error occurs generating the sample.
     * @throws NotStrictlyPositiveException if {@code sampleSize} is not positive.
     * @since 2.2
     */
    public double[] sample(int sampleSize) throws MathException {
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

    /**
     * Access the initial domain value, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the initial domain value.
     */
    protected abstract double getInitialDomain(double p);

    /**
     * Access the domain value lower bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value lower bound, i.e. {@code P(X < 'lower bound') < p}.
     */
    protected abstract double getDomainLowerBound(double p);

    /**
     * Access the domain value upper bound, based on {@code p}, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     *
     * @param p Desired probability for the critical value.
     * @return the domain value upper bound, i.e. {@code P(X < 'upper bound') > p}.
     */
    protected abstract double getDomainUpperBound(double p);

    /**
     * Returns the solver absolute accuracy for inverse cumulative computation.
     *
     * @return the maximum absolute error in inverse cumulative probability estimates
     * @since 2.1
     */
    protected double getSolverAbsoluteAccuracy() {
        return solverAbsoluteAccuracy;
    }

}
