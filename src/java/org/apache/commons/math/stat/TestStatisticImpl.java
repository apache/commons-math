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

package org.apache.commons.math.stat;

import org.apache.commons.math.stat.distribution.DistributionFactory;
import org.apache.commons.math.stat.distribution.TDistribution;
import org.apache.commons.math.stat.distribution.ChiSquaredDistribution;

/**
 * Implements test statistics defined in the TestStatistic interface.
 *
 * @version $Revision: 1.5 $ $Date: 2003/10/13 08:10:56 $
 */
public class TestStatisticImpl implements TestStatistic {
    
    /**
     * Default constructor
     */
    public TestStatisticImpl() {
    }
    
    /**
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return chi-square test statistic
     * @throws IllegalArgumentException if preconditions are not met
     * or length is less than 2
     */
    public double chiSquare(double[] expected, double[] observed)
        throws IllegalArgumentException {
        double sumSq = 0.0d;
        double dev = 0.0d;
        if ((expected.length < 2) || (expected.length != observed.length)) {
            throw new IllegalArgumentException
                ("observed, expected array lengths incorrect");
        }
        if ((StatUtils.min(expected) <= 0) || (StatUtils.min(observed) < 0)) {
            throw new IllegalArgumentException
                ("observed counts must be non-negative," + 
                    " expected counts must be postive");
        }
        for (int i = 0; i < observed.length; i++) {
            dev = (observed[i] - expected[i]);
            sumSq += dev * dev / expected[i];
        }
        
        return sumSq;
    }
    
    /**
     * @param observed array of observed frequency counts
     * @param expected array of exptected frequency counts
     * @return p-value
     * @throws IllegalArgumentException if preconditions are not met
     */
    public double chiSquareTest(double[] expected, double[] observed) 
        throws IllegalArgumentException {
        ChiSquaredDistribution chiSquaredDistribution = 
            DistributionFactory.newInstance().createChiSquareDistribution
                ((double) expected.length - 1);
        return 1 - chiSquaredDistribution.cummulativeProbability(
            chiSquare(expected, observed));     
    }
    
    /**
     * @param observed array of observed frequency counts
     * @param expected array of exptected frequency counts
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @throws IllegalArgumentException if preconditions are not met
     */
    public boolean chiSquareTest(double[] expected, double[] observed, 
        double alpha) 
        throws IllegalArgumentException {
        if ((alpha <= 0) || (alpha > 0.5)) {
           throw new IllegalArgumentException
                ("bad significance level: " + alpha);
        }
        return (chiSquareTest(expected, observed) < alpha);
    }

    /**
     * @param mu comparison constant
     * @param observed array of values
     * @return t statistic
     * @throws IllegalArgumentException if input array length is less than 5
     */
    public double t(double mu, double[] observed) 
    throws IllegalArgumentException {
        if ((observed == null) || (observed.length < 5)) {
            throw new IllegalArgumentException
                ("insufficient data for t statistic");
        }
        return t(StatUtils.mean(observed), mu, StatUtils.variance(observed), 
            observed.length);
    }
    
    /**
     * @param mu constant value to compare sample mean against
     * @param sample array of sample data values
     * @param alpha significance level of the test
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     */
    public boolean tTest(double mu, double[] sample, double alpha)
        throws IllegalArgumentException {
        if ((alpha <= 0) || (alpha > 0.5)) {
           throw new IllegalArgumentException
                ("bad significance level: " + alpha);
        }   
        return (tTest(mu, sample) < alpha);
    }
                       
    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return t-statistic
     * @throws IllegalArgumentException if the precondition is not met
     */
    public double t(double[] sample1, double[] sample2) 
        throws IllegalArgumentException {
        if ((sample1 == null) || (sample2 == null || 
            Math.min(sample1.length, sample2.length) < 5)) {
            throw new IllegalArgumentException
                ("insufficient data for t statistic");
        }
        return t(StatUtils.mean(sample1), StatUtils.mean(sample2), 
            StatUtils.variance(sample1), StatUtils.variance(sample2), 
            (double) sample1.length, (double) sample2.length);
    }
    
    /**
     *
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return tTest p-value
     * @throws IllegalArgumentException if the precondition is not met
     */
    public double tTest(double[] sample1, double[] sample2)
        throws IllegalArgumentException  {
        if ((sample1 == null) || (sample2 == null ||
        Math.min(sample1.length, sample2.length) < 5)) {
            throw new IllegalArgumentException
            ("insufficient data");
        }
        return tTest(StatUtils.mean(sample1), StatUtils.mean(sample2), 
            StatUtils.variance(sample1), StatUtils.variance(sample2), 
            (double) sample1.length, (double) sample2.length);      
    }
    
    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @param alpha significance level
     * @return true if the null hypothesis can be rejected with 
     * confidence 1 - alpha
     * @throws IllegalArgumentException if the preconditions are not met
     */
    public boolean tTest(double[] sample1, double[] sample2, double alpha)
        throws IllegalArgumentException {
       if ((alpha <= 0) || (alpha > 0.5)) {
           throw new IllegalArgumentException
                ("bad significance level: " + alpha);
       }
       return (tTest(sample1, sample2) < alpha);
    }
    
    /**
     * @param mu constant value to compare sample mean against
     * @param sample array of sample data values
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     */
    public double tTest(double mu, double[] sample) 
        throws IllegalArgumentException {
        if ((sample == null) || (sample.length < 5)) {
            throw new IllegalArgumentException
                ("insufficient data for t statistic");
        }
        return tTest(StatUtils.mean(sample), mu, StatUtils.variance(sample),
            sample.length);
    }
    
    /**
     * @param mu comparison constant
     * @param sampleStats Univariate holding sample summary statitstics
     * @return t statistic
     * @throws IllegalArgumentException if the precondition is not met
     */
    public double t(double mu, Univariate sampleStats) 
        throws IllegalArgumentException {
        if ((sampleStats == null) || (sampleStats.getN() < 5)) {
            throw new IllegalArgumentException
                ("insufficient data for t statistic");
        }
        return t(sampleStats.getMean(), mu, sampleStats.getVariance(), 
            sampleStats.getN());
    }
    
    /**
     * @param sampleStats1 Univariate describing data from the first sample
     * @param sampleStats2 Univariate describing data from the second sample
     * @return t statistic
     * @throws IllegalArgumentException if the precondition is not met
     */
    public double t(Univariate sampleStats1, Univariate sampleStats2) 
        throws IllegalArgumentException {
        if ((sampleStats1 == null) || (sampleStats2 == null || 
            Math.min(sampleStats1.getN(), sampleStats2.getN()) < 5)) {
            throw new IllegalArgumentException
                ("insufficient data for t statistic");
        }
        return t(sampleStats1.getMean(), sampleStats2.getMean(), 
            sampleStats1.getVariance(), sampleStats2.getVariance(), 
            (double) sampleStats1.getN(), (double) sampleStats2.getN());
    }
    
    /**
     * @param sampleStats1 Univariate describing data from the first sample
     * @param sampleStats2 Univariate describing data from the second sample
     * @return p-value for t-test
     * @throws IllegalArgumentException if the precondition is not met
     */
    public double tTest(Univariate sampleStats1, Univariate sampleStats2)
        throws IllegalArgumentException {
        if ((sampleStats1 == null) || (sampleStats2 == null || 
            Math.min(sampleStats1.getN(), sampleStats2.getN()) < 5)) {
            throw new IllegalArgumentException
                ("insufficient data for t statistic");
        }
         return tTest(sampleStats1.getMean(), sampleStats2.getMean(), 
            sampleStats1.getVariance(), sampleStats2.getVariance(), 
            (double) sampleStats1.getN(), (double) sampleStats2.getN());
    }
    
    /**
     * @param sampleStats1 Univariate describing sample data values
     * @param sampleStats2 Univariate describing sample data values
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with 
     * confidence 1 - alpha
     * @throws IllegalArgumentException if the preconditions are not met
     */
    public boolean tTest(Univariate sampleStats1, Univariate sampleStats2, 
    double alpha) throws IllegalArgumentException {
        if ((alpha <= 0) || (alpha > 0.5)) {
            throw new IllegalArgumentException
                ("bad significance level: " + alpha);
        }
        return (tTest(sampleStats1, sampleStats2) < alpha);
    }
    
    /**
     * @param mu constant value to compare sample mean against
     * @param sampleStats Univariate describing sample data values
     * @param alpha significance level of the test
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     */
    public boolean tTest(double mu, Univariate sampleStats, double alpha)
        throws IllegalArgumentException {
        if ((alpha <= 0) || (alpha > 0.5)) {
           throw new IllegalArgumentException
                ("bad significance level: " + alpha);
        }   
        return (tTest(mu, sampleStats) < alpha);
    }
    
    /**
     * @param mu constant value to compare sample mean against
     * @param sampleStats Univariate describing sample data
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     */
    public double tTest(double mu, Univariate sampleStats)
        throws IllegalArgumentException {
        if ((sampleStats == null) || (sampleStats.getN() < 5)) {
            throw new IllegalArgumentException
                ("insufficient data for t statistic");
        }
        return tTest(sampleStats.getMean(), mu, sampleStats.getVariance(),
            sampleStats.getN());
    }
    
    //----------------------------------------------- Private methods 
    
    /**
     * Computes approximate degrees of freedom for 2-sample t-test.
     * 
     * @param v1 first sample variance
     * @param v2 second sample variance
     * @param n1 first sample n
     * @param n2 second sample n
     * @return approximate degrees of freedom
     */
    private double df(double v1, double v2, double n1, double n2) {
        return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2))) /
            ((v1 * v1) / (n1 * n1 * (n1 - 1d)) + 
                (v2 * v2) / (n2 * n2 * (n2 - 1d)));       
    }
    
     /**
     * Computes t test statistic for 2-sample t-test.
     * 
     * @param m1 first sample mean
     * @param m2 second sample mean
     * @param v1 first sample variance
     * @param v2 second sample variance
     * @param n1 first sample n
     * @param n2 second sample n
     * @return t test statistic
     */
    private double t(double m1, double m2, double v1, double v2, double n1, 
        double n2) {
        return (m1 - m2) / Math.sqrt((v1 / n1) + (v2 / n2));
    }
    
    /**
     * Computes t test statistic for 1-sample t-test.
     * 
     * @param m sample mean
     * @param mu constant to test against
     * @param v sample variance
     * @param n sample n
     * @return t test statistic
     */
    private double t(double m, double mu, double v, double n) {
        return (m - mu) / Math.sqrt(v / n);
    }
    
    /**
     * Computes p-value for 2-sided, 2-sample t-test.
     * 
     * @param m1 first sample mean
     * @param m2 second sample mean
     * @param v1 first sample variance
     * @param v2 second sample variance
     * @param n1 first sample n
     * @param n2 second sample n
     * @return p-value
     */
    private double tTest(double m1, double m2, double v1, double v2, double n1, 
        double n2) {
        double t = Math.abs(t(m1, m2, v1, v2, n1, n2));
        TDistribution tDistribution = 
            DistributionFactory.newInstance().createTDistribution
                (df(v1, v2, n1, n2));
        return 1.0 - tDistribution.cummulativeProbability(-t, t); 
    }
    
    /**
     * Computes p-value for 2-sided, 1-sample t-test.
     * 
     * @param m sample mean
     * @param mu constant to test against
     * @param v sample variance
     * @param n sample n
     * @return p-value
     */
    private double tTest(double m, double mu, double v, double n) {
    double t = Math.abs(t(m, mu, v, n)); 
        TDistribution tDistribution = 
            DistributionFactory.newInstance().createTDistribution
                (n - 1);
        return 1.0 - tDistribution.cummulativeProbability(-t, t);
    }          
}
