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
 * Computes <a href="http://en.wikipedia.org/wiki/Kurtosis">Kurtosis</a>.
 * <p>
 * We use the following (unbiased) formula to define kurtosis:
 *  <p>
 *  kurtosis = { [n(n+1) / (n -1)(n - 2)(n-3)] sum[(x_i - mean)^4] / std^4 } - [3(n-1)^2 / (n-2)(n-3)]
 *  <p>
 *  where n is the number of values, mean is the {@link Mean} and std is the {@link StandardDeviation}
 * 
 * @version $Revision: 1.18 $ $Date: 2004/03/21 00:22:26 $
 */
public class Kurtosis extends AbstractStorelessUnivariateStatistic implements Serializable {

    static final long serialVersionUID = 2784465764798260919L;  
      
    /** */
    protected FourthMoment moment = null;

    /** */
    protected boolean incMoment = true;

    /** */
    private double kurtosis = Double.NaN;

    /** */
    private long n = 0;

    /**
     * Construct a Kurtosis
     */
    public Kurtosis() {
        moment = new FourthMoment();
    }

    /**
     * Construct a Kurtosis with an external moment
     * @param m4 external Moment
     */
    public Kurtosis(final FourthMoment m4) {
        incMoment = false;
        this.moment = m4;
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
                kurtosis = Double.NaN;
            }

            double variance =
                (moment.n < 1) ? 0.0 : moment.m2 / (double) (moment.n - 1);

            if (moment.n <= 3 || variance < 10E-20) {
                kurtosis = 0.0;
            } else {
                kurtosis =
                    (moment.n0 * (moment.n0 + 1) * moment.m4 -
                    3 * moment.m2 * moment.m2 * moment.n1) /
                    (moment.n1 * moment.n2 * moment.n3 * variance * variance);
            }
            n = moment.n;
        }

        return kurtosis;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#clear()
     */
    public void clear() {
        if (incMoment) {
            moment.clear();
        }
        kurtosis = Double.NaN;
        n = 0;
    }

    /**
     * @see org.apache.commons.math.stat.univariate.StorelessUnivariateStatistic#getN()
     */
    public double getN() {
        return moment.getN();
    }
    
    /*UnvariateStatistic Approach */

    /** */
    Mean mean = new Mean();

    /**
     * Returns the kurtosis for this collection of values.  
     * <p>
     * See {@link Kurtosis} for the definition used in the computation.
     * 
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the kurtosis of the values or Double.NaN if the array is empty
     */
    public double evaluate(
        final double[] values,
        final int begin,
        final int length) {

        // Initialize the kurtosis
        double kurt = Double.NaN;

        if (test(values, begin, length)) {
            if (length <= 3) {
                kurt = 0.0;
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

                // Sum the ^4 of the distance from the mean divided by the
                // standard deviation
                double accum3 = 0.0;
                for (int i = begin; i < begin + length; i++) {
                    accum3 += Math.pow((values[i] - m), 4.0);
                }
                accum3 /= Math.pow(stdDev, 4.0d);

                // Get N
                double n0 = length;

                double coefficientOne =
                    (n0 * (n0 + 1)) / ((n0 - 1) * (n0 - 2) * (n0 - 3));
                double termTwo =
                    ((3 * Math.pow(n0 - 1, 2.0)) / ((n0 - 2) * (n0 - 3)));

                // Calculate kurtosis
                kurt = (coefficientOne * accum3) - termTwo;
            }
        }

        return kurt;
    }

}
