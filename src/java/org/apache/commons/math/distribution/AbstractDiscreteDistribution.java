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


/**
 * Base class for various discrete distributions.  It provides default
 * implementations for some of the methods that do not vary from distribution
 * to distribution.
 *  
 * @version $Revision: 1.3 $ $Date: 2003/10/13 08:10:57 $
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
     * For this disbution, X, this method returns P(x0 &le; X &le; x1).
     * @param x0 the inclusive, lower bound
     * @param x1 the inclusive, upper bound
     * @return the cummulative probability. 
     */
    public double cummulativeProbability(int x0, int x1) {
        return cummulativeProbability(x1) - 
            cummulativeProbability(x0 - 1);
    }
    
    /**
     * For this distribution, X, this method returns the critical point x, such
     * that P(X &le; x) &le; <code>p</code>.
     *
     * @param p the desired probability
     * @return x, such that P(X &lt; x) = <code>p</code>
     */
    public int inverseCummulativeProbability(final double p) {
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException(
                "p must be between 0.0 and 1.0, inclusive.");
        }
        
        // by default, do simple bisection.
        // subclasses can override if there is a better method.
        int x0 = getDomainLowerBound(p);
        int x1 = getDomainUpperBound(p);
        double pm;
        while (x0 < x1) {
            int xm = x0 + (x1 - x0) / 2;
            pm = cummulativeProbability(xm);
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
        pm = cummulativeProbability(x0);
        while (pm > p) {
            --x0;
            pm = cummulativeProbability(x0);
        }
        
        return x0;        
    }
    
    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a PDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code> 
     */
    protected abstract int getDomainLowerBound(double p);
    
    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a PDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code> 
     */
    protected abstract int getDomainUpperBound(double p);
}
