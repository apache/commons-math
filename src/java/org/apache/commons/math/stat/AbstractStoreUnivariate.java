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

import java.util.Arrays;

import org.apache.commons.math.stat.univariate.rank.Percentile;

/**
 * Provides univariate measures for an array of doubles. 
 * @version $Revision: 1.12 $ $Date: 2003/10/13 08:10:56 $
 */
public abstract class AbstractStoreUnivariate
    extends AbstractUnivariate
    implements StoreUnivariate {

    /** Percentile */
    protected Percentile percentile = new Percentile(50);
        
    /**
     * Create an AbstractStoreUnivariate
     */
    public AbstractStoreUnivariate() {
        super();
    }

    /**
     * Create an AbstractStoreUnivariate with a specific Window
     * @param window WindowSIze for stat calculation
     */
    public AbstractStoreUnivariate(int window) {
        super(window);
    }

    /**
     * @see org.apache.commons.math.stat.StoreUnivariate#getPercentile(double)
     */
    public double getPercentile(double p) {
        percentile.setPercentile(p);
        return apply(percentile);
    }
    
    /**
     * @see org.apache.commons.math.stat.StoreUnivariate#getSortedValues()
     */
    public double[] getSortedValues() {
        double[] sort = getValues();
        Arrays.sort(sort);
        return sort;
    }
    
    /**
     * @see org.apache.commons.math.stat.Univariate#addValue(double)
     */
    public abstract void addValue(double value);

    /**
     * @see org.apache.commons.math.stat.StoreUnivariate#getValues()
     */
    public abstract double[] getValues();


    /**
     * @see org.apache.commons.math.stat.StoreUnivariate#getElement(int)
     */
    public abstract double getElement(int index);



}
