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
 *
 * @version $Revision: 1.15 $ $Date: 2004/03/04 04:25:09 $
 */
public class StandardDeviation extends Variance implements Serializable {

    static final long serialVersionUID = 5728716329662425188L;    
    
    /** */
    protected double std = Double.NaN;

    /** */
    private double lastVar = 0.0;

    /**
     * Constructs a StandardDeviation
     */
    public StandardDeviation() {
        super();
    }

    /**
     * Constructs a StandardDeviation with an external moment
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
        if (lastVar != super.getResult()) {
            lastVar = super.getResult();
            if (Double.isNaN(lastVar)) {
                std = Double.NaN;
            } else if (lastVar == 0.0) {
                std = 0.0;
            } else {
                std = Math.sqrt(lastVar);
            }
        }
        return std;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        super.clear();
        lastVar = 0.0;
    }

    /**
     * Returns the Standard Deviation on an array of values.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the result, Double.NaN if no values for an empty array
     * or 0.0 for a single value set.
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public double evaluate(
        final double[] values,
        final int begin,
        final int length) {

        double var = super.evaluate(values, begin, length);

        if (Double.isNaN(var)) {
            return Double.NaN;
        }

        return var != 0.0 ? Math.sqrt(var) : 0.0;
    }

}