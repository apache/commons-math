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
 *
 * @version $Revision: 1.10 $ $Date: 2003/10/13 08:10:58 $
 */
public class Variance extends AbstractStorelessUnivariateStatistic {

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
    protected int n = 0;

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
