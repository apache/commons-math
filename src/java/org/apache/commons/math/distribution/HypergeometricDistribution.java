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
 * The Hypergeometric Distribution.
 * 
 * Instances of HypergeometricDistribution objects should be created using
 * {@link DistributionFactory#createHypergeometricDistribution(int, int, int)}.
 * 
 * References:
 * <ul>
 * <li><a href="http://mathworld.wolfram.com/HypergeometricDistribution.html">
 * Hypergeometric Distribution</a></li>
 * </ul>
 * 
 * @version $Revision: 1.2 $ $Date: 2003/10/13 08:10:57 $
 */
public interface HypergeometricDistribution extends DiscreteDistribution {
    /**
     * Access the number of successes.
     * @return the number of successes.
     */
    public abstract int getNumberOfSuccesses();
    
    /**
     * Access the population size.
     * @return the population size.
     */
    public abstract int getPopulationSize();
    
    /**
     * Access the sample size.
     * @return the sample size.
     */
    public abstract int getSampleSize();
    
    /**
     * Modify the number of successes.
     * @param num the new number of successes.
     */
    public abstract void setNumberOfSuccesses(int num);
    
    /**
     * Modify the population size.
     * @param size the new population size.
     */
    public abstract void setPopulationSize(int size);
    
    /**
     * Modify the sample size.
     * @param size the new sample size.
     */
    public abstract void setSampleSize(int size);
}
