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

/**
 * Computes the sample standard deviation.  The standard deviation
 * is the positive square root of the variance.  See {@link Variance} for
 * more information.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If 
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or 
 * <code>clear()</code> method, it must be synchronized externally.
 * 
 * @version $Revision: 1.19 $ $Date: 2004/07/04 09:02:36 $
 */
public class StandardDeviation extends Variance implements Serializable {

    /** Serializable version identifier */
    static final long serialVersionUID = 5728716329662425188L;    

    /**
     * Constructs a StandardDeviation
     */
    public StandardDeviation() {
        super();
    }

    /**
     * Constructs a StandardDeviation from an external second moment.
     * 
     * @param m2 the external moment
     */
    public StandardDeviation(final SecondMoment m2) {
        super(m2);
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#increment(double)
     */
    public void increment(final double d) {
        super.increment(d);
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        return Math.sqrt(super.getResult());
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        super.clear();
    }

    /**
     * Returns the Standard Deviation of the entries in the specified portion of
     * the input array, or <code>Double.NaN</code> if the designated subarray
     * is empty.
     * <p>
     * Returns 0 for a single-value (i.e. length = 1) sample.
     * 
     * @param values the input array
     * @param begin index of the first array element to include
     * @param length the number of elements to include
     * @return the standard deviation of the values or Double.NaN if length = 0
     * @throws IllegalArgumentException if the array is null or the array index
     *  parameters are not valid
     */
    public double evaluate(final double[] values, final int begin, final int length)  {
       return Math.sqrt(super.evaluate(values, begin, length));
    }

}