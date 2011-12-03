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

        double lowerBound = getSupportLowerBound();
        if (p == 0.0) {
            return lowerBound;
        }

        double upperBound = getSupportUpperBound();
        if (p == 1.0) {
            return upperBound;
        }

        final double mu = getNumericalMean();
        final double sig = FastMath.sqrt(getNumericalVariance());
        final boolean chebyshevApplies;
        chebyshevApplies = !(Double.isInfinite(mu) || Double.isNaN(mu) ||
                             Double.isInfinite(sig) || Double.isNaN(sig));

        if (lowerBound == Double.NEGATIVE_INFINITY) {
            if (chebyshevApplies) {
                lowerBound = mu - sig * FastMath.sqrt((1. - p) / p);
            } else {
                lowerBound = -1.0;
                while (cumulativeProbability(lowerBound) >= p) {
                    lowerBound *= 2.0;
                }
            }
        }

        if (upperBound == Double.POSITIVE_INFINITY) {
            if (chebyshevApplies) {
                upperBound = mu + sig * FastMath.sqrt(p / (1. - p));
            } else {
                upperBound = 1.0;
                while (cumulativeProbability(upperBound) < p) {
                    upperBound *= 2.0;
                }
            }
        }

        final UnivariateFunction toSolve = new UnivariateFunction() {

            public double value(final double x) {
                return cumulativeProbability(x) - p;
            }
        };

        double x = UnivariateRealSolverUtils.solve(toSolve,
                                                   lowerBound,
                                                   upperBound,
                                                   getSolverAbsoluteAccuracy());

        if (!isSupportConnected()) {
            /* Test for plateau. */
            final double dx = getSolverAbsoluteAccuracy();
            if (x - dx >= getSupportLowerBound()) {
                double px = cumulativeProbability(x);
                if (cumulativeProbability(x - dx) == px) {
                    upperBound = x;
                    while (upperBound - lowerBound > dx) {
                        final double midPoint = 0.5 * (lowerBound + upperBound);
                        if (cumulativeProbability(midPoint) < px) {
                            lowerBound = midPoint;
                        } else {
                            upperBound = midPoint;
                        }
                    }
                    return upperBound;
                }
            }
        }
        return x;
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
