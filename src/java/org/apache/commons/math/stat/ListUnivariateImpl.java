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

import java.util.List;

import org.apache.commons.math.stat.univariate.UnivariateStatistic;
import org.apache.commons.math.util.DefaultTransformer;
import org.apache.commons.math.util.NumberTransformer;

/**
 * @version $Revision: 1.8 $ $Date: 2003/10/13 08:10:56 $
 */
public class ListUnivariateImpl
    extends AbstractStoreUnivariate
    implements StoreUnivariate {

    /**
     * Holds a reference to a list - GENERICs are going to make
     * out lives easier here as we could only accept List<Number>
     */
    protected List list;

    /** Number Transformer maps Objects to Number for us. */
    protected NumberTransformer transformer;

    /**
     * Construct a ListUnivariate with a specific List.
     * @param list The list that will back this Univariate
     */
    public ListUnivariateImpl(List list) {
        this(list, new DefaultTransformer());
    }
    
    /**
     * Construct a ListUnivariate with a specific List.
     * @param list The list that will back this Univariate
     * @param transformer the number transformer used to convert the list items.
     */
    public ListUnivariateImpl(List list, NumberTransformer transformer) {
        super();
        this.list = list;
        this.transformer = transformer;
    }

    /**
     * @see org.apache.commons.math.stat.StoreUnivariate#getValues()
     */
    public double[] getValues() {

        int length = list.size();

        // If the window size is not INFINITE_WINDOW AND
        // the current list is larger that the window size, we need to
        // take into account only the last n elements of the list
        // as definied by windowSize

        if (windowSize != Univariate.INFINITE_WINDOW &&
            windowSize < list.size())
        {
            length = list.size() - Math.max(0, list.size() - windowSize);
        }

        // Create an array to hold all values
        double[] copiedArray = new double[length];

        for (int i = 0; i < copiedArray.length; i++) {
            copiedArray[i] = getElement(i);
        }
        return copiedArray;
    }

    /**
     * @see org.apache.commons.math.stat.StoreUnivariate#getElement(int)
     */
    public double getElement(int index) {

        double value = Double.NaN;

        int calcIndex = index;

        if (windowSize != Univariate.INFINITE_WINDOW &&
            windowSize < list.size())
        {
            calcIndex = (list.size() - windowSize) + index;
        }

        try {
            value = transformer.transform(list.get(calcIndex));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#getN()
     */
    public int getN() {
        int n = 0;

        if (windowSize != Univariate.INFINITE_WINDOW) {
            if (list.size() > windowSize) {
                n = windowSize;
            } else {
                n = list.size();
            }
        } else {
            n = list.size();
        }
        return n;
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#addValue(double)
     */
    public void addValue(double v) {
        list.add(new Double(v));
    }
    
    /**
     * Adds an object to this list. 
     * @param o Object to add to the list
     */
    public void addObject(Object o) {
        list.add(o);
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#clear()
     */
    public void clear() {
        super.clear();
        list.clear();
    }
    
    /**
     * Apply the given statistic to this univariate collection.
     * @param stat the statistic to apply
     * @return the computed value of the statistic.
     */
    public double apply(UnivariateStatistic stat) {
        double[] v = this.getValues();

        if (v != null) {
            return stat.evaluate(v, 0, v.length);
        }
        return Double.NaN;
    }
    
    /**
     * Access the number transformer.
     * @return the number transformer.
     */
    public NumberTransformer getTransformer() {
        return transformer;
    }

    /**
     * Modify the number transformer.
     * @param transformer the new number transformer.
     */
    public void setTransformer(NumberTransformer transformer) {
        this.transformer = transformer;
    }

}