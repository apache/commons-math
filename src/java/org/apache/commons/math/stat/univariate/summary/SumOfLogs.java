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
package org.apache.commons.math.stat.univariate.summary;

import java.io.Serializable;

import org
    .apache
    .commons
    .math
    .stat
    .univariate
    .AbstractStorelessUnivariateStatistic;

/**
 * Returns the sum of the natural logs for this collection of values.
 * 
 * @version $Revision: 1.16 $ $Date: 2004/04/26 19:15:48 $
 */
public class SumOfLogs extends AbstractStorelessUnivariateStatistic implements Serializable {

    static final long serialVersionUID = -370076995648386763L;    

    /** */
    private int n = 0;
    
    /**
     * The currently running value
     */
    private double value = Double.NaN;

    /** */
    private boolean init = true;

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        if (init) {
            value = Math.log(d);
            init = false;
        } else {
            value += Math.log(d);
        }
        n++;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return value;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getN()
     */
    public double getN() {
        return n;
    }
    
    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        value = Double.NaN;
        init = true;
        n = 0;
    }

    /**
     * Returns the sum of the natural logs for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the sumLog value or Double.NaN if the array is empty
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public double evaluate(
        final double[] values,
        final int begin,
        final int length) {
        double sumLog = Double.NaN;
        if (test(values, begin, length)) {
            sumLog = 0.0;
            for (int i = begin; i < begin + length; i++) {
                sumLog += Math.log(values[i]);
            }
        }
        return sumLog;
    }
}