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


/**
 * Base class for various discrete distributions.  It provides default
 * implementations for some of the methods that do not vary from distribution
 * to distribution.
 *  
 * @version $Revision: 1.13 $ $Date: 2004/05/11 02:04:21 $
 */
public abstract class AbstractDiscreteDistribution
    implements DiscreteDistribution {
        
    /**
     * Default constructor.
     */
    protected AbstractDiscreteDistribution() {
        super();
    }
    
    /**
     * For this distribution, X, this method returns P(x0 &le; X &le; x1).
     * @param x0 the inclusive, lower bound
     * @param x1 the inclusive, upper bound
     * @return the cumulative probability. 
     * @exception MathException if the cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     * @exception IllegalArgumentException if x0 > x1
     */
    public double cumulativeProbability(int x0, int x1) throws MathException {
        if (x0 > x1) {
            throw new IllegalArgumentException
            	("lower endpoint must be less than or equal to upper endpoint");
        }
        return cumulativeProbability(x1) - cumulativeProbability(x0 - 1);
    }
    
    /**
     * For this distribution, X, this method returns the lagest x, such
     * that P(X &le; x) &le; <code>p</code>.
     *
     * @param p the desired probability
     * @return the largest x such that P(X &le; x) <= p
     * @exception MathException if the inverse cumulative probability can not be
     *            computed due to convergence or other numerical errors.
     * @exception IllegalArgumentException if p < 0 or p >= 1
     */
    public int inverseCumulativeProbability(final double p) throws MathException{
        if (p < 0.0 || p >= 1.0) {
            throw new IllegalArgumentException(
                "p must be greater than or equal to 0.0 and strictly less than 1.0");
        }
        
        // by default, do simple bisection.
        // subclasses can override if there is a better method.
        int x0 = getDomainLowerBound(p);
        int x1 = getDomainUpperBound(p);
        double pm;
        while (x0 < x1) {
            int xm = x0 + (x1 - x0) / 2;
            pm = cumulativeProbability(xm);
            if (pm > p) {
                // update x1
                if (xm == x1) {
                    // this can happen with integer division
                    // simply decrement x1
                    --x1;
                } else {
                    // update x1 normally
                    x1 = xm;
                }
            } else {
                // update x0
                if (xm == x0) {
                    // this can happen with integer division
                    // simply increment x0
                    ++x0;
                } else {
                    // update x0 normally
                    x0 = xm;
                }
            }
        }
        
        // insure x0 is the correct critical point
        pm = cumulativeProbability(x0);
        while (pm > p) {
            --x0;
            pm = cumulativeProbability(x0);
        }
    
        return x0;        
    }
    
    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a PDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code> 
     */
    protected abstract int getDomainLowerBound(double p);
    
    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a PDF root.  This method is used by
     * {@link #inverseCumulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code> 
     */
    protected abstract int getDomainUpperBound(double p);
}
