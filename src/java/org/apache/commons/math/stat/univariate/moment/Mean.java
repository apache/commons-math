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
package org.apache.commons.math.stat.univariate.moment;

import java.io.Serializable;

import org
    .apache
    .commons
    .math
    .stat
    .univariate
    .AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.stat.univariate.summary.Sum;

/**
 * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
 * arithmetic mean </a> of the available values.
 * @version $Revision: 1.16 $ $Date: 2004/03/04 04:25:09 $
 */
public class Mean extends AbstractStorelessUnivariateStatistic implements Serializable{

    static final long serialVersionUID = -1296043746617791564L;    
    
    /** first moment of values that have been added */
    protected FirstMoment moment = null;

    /** */
    protected boolean incMoment = true;

    /** */
    public Mean() {
        moment = new FirstMoment();
    }

    /**
     * Constructs a Mean with an External Moment.
     * @param m1 the moment
     */
    public Mean(final FirstMoment m1) {
        this.moment = m1;
        incMoment = false;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (incMoment) {
            moment.increment(d);
        }
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        if (incMoment) {
            moment.clear();
        }
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return moment.m1;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getN()
     */
    public double getN() {
        return moment.getN();
    }
    
    /*UnvariateStatistic Approach */

    /** */
    protected Sum sum = new Sum();

    /**
     * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
     * arithmetic mean </a> of a double[] of the available values.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the mean of the values or Double.NaN if the array is empty
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public double evaluate(
        final double[] values,
        final int begin,
        final int length) {
        if (test(values, begin, length)) {
            return sum.evaluate(values) / ((double) length);
        }
        return Double.NaN;
    }
}