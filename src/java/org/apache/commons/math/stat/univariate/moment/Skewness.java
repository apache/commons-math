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
 * Computes <a href="http://en.wikipedia.org/wiki/Skewness">Skewness</a>.
 * <p>
 * We use the following (unbiased) formula to define skewness:
 *  <p>
 *  skewness = [n / (n -1) (n - 2)] sum[(x_i - mean)^3] / std^3
 *  <p>
 *  where n is the number of values, mean is the {@link Mean} and std is the {@link StandardDeviation}
 *
 * @version $Revision: 1.21 $ $Date: 2004/06/23 16:26:14 $
 */
public class Skewness extends AbstractStorelessUnivariateStatistic implements Serializable {

    /** Serializable version identifier */
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
     * Returns the value of the statistic based on the values that have been added.
     * <p>
     * See {@link Skewness} for the definition used in the computation.
     * 
     * @return the skewness of the available values.
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
     * Returns the Skewness of the values array.
     * <p>
     * See {@link Skewness} for the definition used in the computation.
     * 
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
                double stdDev = Math.sqrt((accum - (Math.pow(accum2, 2) / ((double) length))) /
                        (double) (length - 1));

                double accum3 = 0.0;
                for (int i = begin; i < begin + length; i++) {
                    accum3 += Math.pow(values[i] - m, 3.0d);
                }
                accum3 /= Math.pow(stdDev, 3.0d);

                // Get N
                double n0 = length;

                // Calculate skewness
                skew = (n0 / ((n0 - 1) * (n0 - 2))) * accum3;
            }
        }

        return skew;
    }

}
