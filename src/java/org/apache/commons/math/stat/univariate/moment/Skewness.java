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
package org.apache.commons.math.stat.univariate.moment;

import org.apache.commons.math.stat.univariate.AbstractStorelessUnivariateStatistic;

/**
 * @version $Revision: 1.10 $ $Date: 2003/10/13 08:10:58 $
 */
public class Skewness extends AbstractStorelessUnivariateStatistic {

    /** */
    protected ThirdMoment moment = null;

    /** */
    protected boolean incMoment = true;

    /** */
    protected double skewness = Double.NaN;

    /** */
    private int n = 0;

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
     * @param length processing at this point in the array
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
