/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.commons.math.stat.distribution;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealSolverUtils;

/**
 * Base class for various continuous distributions.  It provides default
 * implementations for some of the methods that do not vary from distribution
 * to distribution.
 *  
 * @version $Revision: 1.10 $ $Date: 2003/10/13 08:10:57 $
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
     * @return the cummulative probability. 
     */
    public double cummulativeProbability(double x0, double x1) {
        return cummulativeProbability(x1) - cummulativeProbability(x0);
    }
    
    /**
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &lt; x) = <code>p</code>.
     *
     * @param p the desired probability
     * @return x, such that P(X &lt; x) = <code>p</code>
     */
    public double inverseCummulativeProbability(final double p) {
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException(
                "p must be between 0.0 and 1.0, inclusive.");
        }
        
        // by default, do simple root finding using bracketing and bisection.
        // subclasses can overide if there is a better method.
        UnivariateRealFunction rootFindingFunction =
            new UnivariateRealFunction() {
                
            public double value(double x) throws MathException {
                return cummulativeProbability(x) - p;
            }

            public double firstDerivative(double x) throws MathException {
                return 0;
            }

            public double secondDerivative(double x) throws MathException {
                return 0;
            }
        };
        
        try {
            // bracket root
            double[] bracket = UnivariateRealSolverUtils.bracket(rootFindingFunction,
                getInitialDomain(p), getDomainLowerBound(p),
                getDomainUpperBound(p));
            
            // find root
            double root = UnivariateRealSolverUtils.solve(
                rootFindingFunction, bracket[0], bracket[1]);
        
            return root;
        } catch (MathException ex) {
            // this should never happen.
            return Double.NaN;
        }
    }
    
    /**
     * Access the initial domain value, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return initial domain value
     */
    protected abstract double getInitialDomain(double p);
    
    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code> 
     */
    protected abstract double getDomainLowerBound(double p);
    
    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code> 
     */
    protected abstract double getDomainUpperBound(double p);
}
