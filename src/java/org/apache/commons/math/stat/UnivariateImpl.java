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

import java.io.Serializable;

import org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic;
import org.apache.commons.math.stat.univariate.UnivariateStatistic;
import org.apache.commons.math.util.FixedDoubleArray;

/**
 *
 * Accumulates univariate statistics for values fed in
 * through the addValue() method.  Does not store raw data values.
 * All data are represented internally as doubles.
 * Integers, floats and longs can be added, but they will be converted
 * to doubles by addValue().
 *
 * @version $Revision: 1.22 $ $Date: 2003/10/13 08:10:56 $
*/
public class UnivariateImpl
    extends AbstractUnivariate
    implements Univariate, Serializable {

    /** fixed storage */
    private FixedDoubleArray storage = null;

    /** Creates new univariate with an infinite window */
    public UnivariateImpl() {
        super();
    }

    /** 
     * Creates a new univariate with a fixed window 
     * @param window Window Size
     */
    public UnivariateImpl(int window) {
        super(window);
        storage = new FixedDoubleArray(window);
    }

    /**
     *  If windowSize is set to Infinite, moments 
     *  are calculated using the following 
     * <a href="http://www.spss.com/tech/stat/Algorithms/11.5/descriptives.pdf">
     * recursive strategy
     * </a>.
     * Otherwise, stat methods delegate to StatUtils.
     * @see org.apache.commons.math.stat.Univariate#addValue(double)
     */
    public void addValue(double value) {

        if (storage != null) {
            /* then all getters deligate to StatUtils
             * and this clause simply adds/rolls a value in the storage array 
             */
            if (getWindowSize() == n) {
                storage.addElementRolling(value);
            } else {
                n++;
                storage.addElement(value);
            }

        } else {
            /* If the windowSize is infinite don't store any values and there 
             * is no need to discard the influence of any single item.
             */
            n++;
            min.increment(value);
            max.increment(value);
            sum.increment(value);
            sumsq.increment(value);
            sumLog.increment(value);
            geoMean.increment(value);

            moment.increment(value);
            //mean.increment(value);
            //variance.increment(value);
            //skewness.increment(value);
            //kurtosis.increment(value);
        }
    }

    /**
     * Generates a text report displaying
     * univariate statistics from values that
     * have been added.
     * @return String with line feeds displaying statistics
     */
    public String toString() {
        StringBuffer outBuffer = new StringBuffer();
        outBuffer.append("UnivariateImpl:\n");
        outBuffer.append("n: " + getN() + "\n");
        outBuffer.append("min: " + getMin() + "\n");
        outBuffer.append("max: " + getMax() + "\n");
        outBuffer.append("mean: " + getMean() + "\n");
        outBuffer.append("std dev: " + getStandardDeviation() + "\n");
        outBuffer.append("skewness: " + getSkewness() + "\n");
        outBuffer.append("kurtosis: " + getKurtosis() + "\n");
        return outBuffer.toString();
    }

    /**
     * @see org.apache.commons.math.stat.Univariate#clear()
     */
    public void clear() {
        super.clear();
        if (getWindowSize() != INFINITE_WINDOW) {
            storage = new FixedDoubleArray(getWindowSize());
        }
    }

    /**
     * Apply the given statistic to this univariate collection.
     * @param stat the statistic to apply
     * @return the computed value of the statistic.
     */
    public double apply(UnivariateStatistic stat) {
        
        if (storage != null) {
            return stat.evaluate(storage.getValues(), storage.start(), storage.getNumElements());
        } else if (stat instanceof StorelessUnivariateStatistic) {
            return ((StorelessUnivariateStatistic) stat).getResult();
        }

        return Double.NaN;
    }

}