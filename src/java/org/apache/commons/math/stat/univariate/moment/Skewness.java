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
 * @version $Revision: 1.17 $ $Date: 2004/03/04 04:25:09 $
 */
public class Skewness extends AbstractStorelessUnivariateStatistic implements Serializable {

    static final long serialVersionUID = 7101857578996691352L;    
    
    /** */
    protected ThirdMoment moment = null;

    /** */
    protected boolean incMoment = true;

    /** */
    protected double skewness = Double.NaN;

    /** */
    private long n = 0;

    /**
     * Constructs a Skewness
     */
    public Skewness() {
        moment = new ThirdMoment();
    }

    /**
     * Constructs a Skewness with an external moment
     * @param m3 external moment
     */
    public Skewness(final ThirdMoment m3) {
        incMoment = false;
        this.moment = m3;
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
                skewness = Double.NaN;
            }

            double variance =
                (moment.n < 1) ? 0.0 : moment.m2 / (double) (moment.n - 1);

            if (moment.n <= 2 || variance < 10E-20) {
                skewness = 0.0;
            } else {
                skewness = (moment.n0 * moment.m3) /
                    (moment.n1 * moment.n2 * Math.sqrt(variance) * variance);
            }
            n = moment.n;
        }
        return skewness;
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
        skewness = Double.NaN;
        n = 0;
    }

    /*UnvariateStatistic Approach */

    /** */
    Mean mean = new Mean();

    /**
     * Returns the skewness of a collection of values.  Skewness is a
     * measure of the assymetry of a given distribution.
     * This algorithm uses a corrected two pass algorithm of the following
     * <a href="http://lib-www.lanl.gov/numerical/bookcpdf/c14-1.pdf">
     * corrected two pass formula (14.1.8)</a>, and also referenced in
     * <p>
     * "Algorithms for Computing the Sample Variance: Analysis and
     * Recommendations", Chan, T.F., Golub, G.H., and LeVeque, R.J.
     * 1983, American Statistician, vol. 37, pp. 242?247.
     * </p>
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the skewness of the values or Double.NaN if the array is empty
     * @see org.apache.commons.math.stat.univariate.UnivariateStatistic#evaluate(double[], int, int)
     */
    public double evaluate(
        final double[] values,
        final int begin,
        final int length) {

        // Initialize the skewness
        double skew = Double.NaN;

        if (test(values, begin, length)) {

            if (length <= 2) {
                skew = 0.0;
            } else {
                // Get the mean and the standard deviation
                double m = mean.evaluate(values, begin, length);

                // Calc the std, this is implemented here instead
                // of using the standardDeviation method eliminate
                // a duplicate pass to get the mean
                double accum = 0.0;
                double accum2 = 0.0;
                for (int i = begin; i < begin + length; i++) {
                    accum += Math.pow((values[i] - m), 2.0);
                    accum2 += (values[i] - m);
                }
                double stdDev =
                    Math.sqrt(
                        (accum - (Math.pow(accum2, 2) / ((double) length))) /
                            (double) (length - 1));

                // Calculate the skew as the sum the cubes of the distance
                // from the mean divided by the standard deviation.
                double accum3 = 0.0;
                for (int i = begin; i < begin + length; i++) {
                    accum3 += Math.pow((values[i] - m) / stdDev, 3.0);
                }

                // Get N
                double n0 = length;

                // Calculate skewness
                skew = (n0 / ((n0 - 1) * (n0 - 2))) * accum3;
            }
        }

        return skew;
    }

}
