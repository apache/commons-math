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

import org.apache.commons.math.special.Beta;

/**
 * Default implementation of
 * {@link org.apache.commons.math.stat.distribution.TDistribution}.
 * 
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:10:57 $
 */
public class TDistributionImpl
    extends AbstractContinuousDistribution
    implements TDistribution {

    /** The degrees of freedom*/
    private double degreesOfFreedom;
    
    /**
     * Create a t distribution using the given degrees of freedom.
     * @param degreesOfFreedom the degrees of freedom.
     */
    public TDistributionImpl(double degreesOfFreedom) {
        super();
        setDegreesOfFreedom(degreesOfFreedom);
    }

    /**
     * Modify the degrees of freedom.
     * @param degreesOfFreedom the new degrees of freedom.
     */
    public void setDegreesOfFreedom(double degreesOfFreedom) {
        if (degreesOfFreedom <= 0.0) {
            throw new IllegalArgumentException(
                "degrees of freedom must be positive.");
        }
        this.degreesOfFreedom = degreesOfFreedom;
    }

    /**
     * Access the degrees of freedom.
     * @return the degrees of freedom.
     */
    public double getDegreesOfFreedom() {
        return degreesOfFreedom;
    }

    /**
     * For this disbution, X, this method returns P(X &lt; <code>x</code>).
     * @param x the value at which the CDF is evaluated.
     * @return CDF evaluted at <code>x</code>. 
     */
    public double cummulativeProbability(double x) {
        double ret;
        if (x == 0.0) {
            ret = 0.5;
        } else {
            double t = Beta.regularizedBeta(
                getDegreesOfFreedom() / (getDegreesOfFreedom() + (x * x)),
                0.5 * getDegreesOfFreedom(), 0.5);
                
            if (x < 0.0) {
                ret = 0.5 * t;
            } else {
                ret = 1.0 - 0.5 * t;
            }
        }
        
        return ret;
    }
        
    /**
     * Access the domain value lower bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value lower bound, i.e.
     *         P(X &lt; <i>lower bound</i>) &lt; <code>p</code> 
     */
    protected double getDomainLowerBound(double p) {
        return -Double.MAX_VALUE;
    }

    /**
     * Access the domain value upper bound, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return domain value upper bound, i.e.
     *         P(X &lt; <i>upper bound</i>) &gt; <code>p</code> 
     */
    protected double getDomainUpperBound(double p) {
        return Double.MAX_VALUE;
    }

    /**
     * Access the initial domain value, based on <code>p</code>, used to
     * bracket a CDF root.  This method is used by
     * {@link #inverseCummulativeProbability(double)} to find critical values.
     * 
     * @param p the desired probability for the critical value
     * @return initial domain value
     */
    protected double getInitialDomain(double p) {
        return 0.0;
    }
}
