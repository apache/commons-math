/*
 * Copyright 2003-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealSolverUtils;

/**
 * Base class for various continuous distributions.  It provides default
 * implementations for some of the methods that do not vary from distribution
 * to distribution.
 *  
 * @version $Revision: 1.21 $ $Date: 2004/05/19 14:16:31 $
 */
public abstract class AbstractContinuousDistribution
    implements ContinuousDistribution {

    /**
     * Default constructor.
     */
    protected AbstractContinuousDistribution() {
        super();
    }

    /**
     * For this distribution, X, this method returns P(x0 &lt; X &lt; x1).  This
     * is accomplished by using the equality P(x0 &lt; X &lt; x1) =
     * P(X &lt; x1) - P(X &lt; x0).
     * 
     * @param x0 the lower bound
     * @param x1 the upper bound
     * @return the cumulative probability. 
     * @throws MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     */
    public double cumulativeProbability(double x0, double x1)
        throws MathException {
        return cumulativeProbability(x1) - cumulativeProbability(x0);
    }

    /**
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &lt; x) = <code>p</code>.
     *
     * @param p the desired probability
     * @return x, such that P(X &lt; x) = <code>p</code>
     * @throws MathException if the inverse cumulative probability can not be
     *         computed due to convergence or other numerical errors.
     * @throws IllegalArgumentException if <code>p</code> is not a valid
     *         probability.
     */
    public double inverseCumulativeProbability(final double p)
        throws MathException {
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("p must be between 0.0 and 1.0, inclusive.");
        }

        // by default, do simple root finding using bracketing and bisection.
        // subclasses can overide if there is a better method.
        UnivariateRealFunction rootFindingFunction =
            new UnivariateRealFunction() {

            public double value(double x) throws MathException {
                return cumulativeProbability(x) - p;
            }
        };

        // bracket root
        double[] bracket =
            UnivariateRealSolverUtils.bracket(
                rootFindingFunction,
                getInitialDomain(p),
                getDomainLowerBound(p),
                getDomainUpperBound(p));

        // find root
        double root =
            UnivariateRealSolverUtils.solve(
                rootFindingFunction,
                bracket[0],
                bracket[1]);

        return root;
    }

    /**
     * Access the initial domain value, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return initial domain value
     */
    protected abstract double getInitialDomain(double p);

    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code> 
     */
    protected abstract double getDomainLowerBound(double p);

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code> 
     */
    protected abstract double getDomainUpperBound(double p);
}
