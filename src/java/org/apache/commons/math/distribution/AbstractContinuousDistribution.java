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

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealSolverUtils;

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
     * Default constructor.
     */
    protected AbstractContinuousDistribution() {
        super();
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

        // by default, do simple root finding using bracketing and default solver.
        // subclasses can overide if there is a better method.
        UnivariateRealFunction rootFindingFunction =
            new UnivariateRealFunction() {

            public double value(double x) throws FunctionEvaluationException {
                try {
                    return cumulativeProbability(x) - p;
                } catch (MathException ex) {
                    throw new FunctionEvaluationException(x, ex.getPattern(), ex.getArguments(), ex);
                }
            }
        };
              
        // Try to bracket root, test domain endoints if this fails     
        double lowerBound = getDomainLowerBound(p);
        double upperBound = getDomainUpperBound(p);
        double[] bracket = null;
        try {
            bracket = UnivariateRealSolverUtils.bracket(
                    rootFindingFunction, getInitialDomain(p),
                    lowerBound, upperBound);
        }  catch (ConvergenceException ex) {
            /* 
             * Check domain endpoints to see if one gives value that is within
             * the default solver's defaultAbsoluteAccuracy of 0 (will be the
             * case if density has bounded support and p is 0 or 1).
             * 
             * TODO: expose the default solver, defaultAbsoluteAccuracy as
             * a constant.
             */ 
            if (Math.abs(rootFindingFunction.value(lowerBound)) < 1E-6) {
                return lowerBound;
            }
            if (Math.abs(rootFindingFunction.value(upperBound)) < 1E-6) {
                return upperBound;
            }     
            // Failed bracket convergence was not because of corner solution
            throw new MathException(ex);
        }

        // find root
        double root = UnivariateRealSolverUtils.solve(rootFindingFunction,
                bracket[0],bracket[1]);
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
