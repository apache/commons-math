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
package org.apache.commons.math.stat;

/**
 * StoreUnivariate implements the Univariate interface 
 * but maintains the set of values which contribute to 
 * the values being returned.  This implementation of 
 * Univariate provides additional percentile functionality 
 * such as.  This additional functionality comes with 
 * a price of increased storage costs.
 * @version $Revision: 1.8 $ $Date: 2003/10/13 08:10:56 $
 */
public interface StoreUnivariate extends Univariate {

    /**
     * Returns the current set of values in an array of double primitives.  
     * The order of addition is preserved.  The returned array is a fresh
     * copy of the underlying data -- i.e., it is not a reference to the
     * stored data.
     * 
     * @return returns the current set of numbers in the order in which they 
     *         were added to this set
     */
    double[] getValues();

    /**
     * Returns the current set of values in an array of double primitives,  
     * sorted in ascending order.  The returned array is a fresh
     * copy of the underlying data -- i.e., it is not a reference to the
     * stored data.
     * @return returns the current set of 
     * numbers sorted in ascending order        
     */
    double[] getSortedValues();

    /**
     * Returns the element at the specified index
     * @param index The Index of the element
     * @return return the element at the specified index
     */
    double getElement(int index);

    /**
     * Returns an estimate for the pth percentile of the stored values. 
     * This estimate follows the interpolation-adjusted defintion presented 
     * <a href="http://www.utdallas.edu/~ammann/stat5311/node8.html">here</a>
     * <p/>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>0 &lt; p &lt; 100</code> (otherwise an 
     * <code>IllegalArgumentException</code> is thrown)</li>
     * <li>at least one value must be stored (returns <code>Double.NaN
     *     </code> otherwise)</li>
     * </ul>
     * 
     * @param p the requested percentile (scaled from 0 - 100)
     * @return An estimate for the pth percentile of the stored data 
     * values
     */
    double getPercentile(double p);

}
