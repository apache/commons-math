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

import org.apache.commons.math.stat.univariate.AbstractStorelessUnivariateStatistic;

/**
 * Updating forumulas use West's algorithm as described in
 * <a href="http://doi.acm.org/10.1145/359146.359152">Chan, T. F. and
 * J. G. Lewis 1979, <i>Communications of the ACM</i>,
 * vol. 22 no. 9, pp. 526-531.</a>.
 * 
 * @version $Revision: 1.17 $ $Date: 2004/02/21 21:35:15 $
 */
public class Variance extends AbstractStorelessUnivariateStatistic implements Serializable {

    static final long serialVersionUID = -9111962718267217978L;  
      
    /** SecondMoment is used in incremental calculation of Variance*/
    protected SecondMoment moment = null;

    /**
     * Boolean test to determine if this Variance should also increment
     * the second moment, this evaluates to false when this Variance is
     * constructed with an external SecondMoment as a parameter.
     */
    protected boolean incMoment = true;

    /**
     * This property maintains the latest calculated
     * variance for efficiency when getResult() is called
     * many times between increments.
     */
    protected double variance = Double.NaN;

    /**
     * Maintains the current count of inrementations that have occured.
     * If the external SecondMoment is used, the this is updated from
     * that moments counter
     */
    protected long n = 0;

    /**
     * Constructs a Variance.
     */
    public Variance() {
        moment = new SecondMoment();
    }

    /**
     * Constructs a Variance based on an externalized second moment.
     * @param m2 the SecondMoment (Thrid or Fourth moments work
     * here as well.)
     */
    public Variance(final SecondMoment m2) {
        incMoment = false;
        this.moment = m2;
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
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getResult()
     */
    public double getResult() {
        if (n < moment.n) {
            if (moment.n <= 0) {
                variance = Double.NaN;
            } else if (moment.n <= 1) {
                variance = 0.0;
            } else {
                variance = moment.m2 / (moment.n0 - 1);
            }
            n = moment.n;
        }

        return variance;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getN()
     */
    public double getN() {
        return moment.getN();
    }
    
    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        if (incMoment) {
            moment.clear();
        }
        variance = Double.NaN;
        n = 0;
    }

    /** Mean to be used in UnvariateStatistic evaluation approach. */
    protected Mean mean = new Mean();

    /**
     * Returns the variance of the available values. This uses a corrected
     * two pass algorithm of the following
     * <a href="http://lib-www.lanl.gov/numerical/bookcpdf/c14-1.pdf">
     * corrected two pass formula (14.1.8)</a>, and also referenced in:
     * <p>
     * "Algorithms for Computing the Sample Variance: Analysis and
     * Recommendations", Chan, T.F., Golub, G.H., and LeVeque, R.J.
     * 1983, American Statistician, vol. 37, pp. 242?247.
     * </p>
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the result, Double.NaN if no values for an empty array
     * or 0.0 for a single value set.
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public double evaluate(
        final double[] values,
        final int begin,
        final int length) {

        double var = Double.NaN;

        if (test(values, begin, length)) {
            if (length == 1) {
                var = 0.0;
            } else if (length > 1) {
                double m = mean.evaluate(values, begin, length);
                double accum = 0.0;
                double accum2 = 0.0;
                for (int i = begin; i < begin + length; i++) {
                    accum += Math.pow((values[i] - m), 2.0);
                    accum2 += (values[i] - m);
                }
                var = (accum - (Math.pow(accum2, 2) / ((double) length))) /
                    (double) (length - 1);
            }
        }
        return var;
    }

}
