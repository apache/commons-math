/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.commons.math.stat.inference;

import java.io.Serializable;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.DistributionFactory;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.univariate.StatisticalSummary;

/**
 * Implements t-test statistics defined in the {@link TTest} interface.
 *
 * @version $Revision: 1.3 $ $Date: 2004/05/24 05:29:05 $
 */
public class TTestImpl implements TTest, Serializable {

    /** Serializable version identifier */
    static final long serialVersionUID = 3003851743922752186L;
    
    public TTestImpl() {
        super();
    }

    //----------------------------------------------- Protected methods 

    /**
     * Computes approximate degrees of freedom for 2-sample t-test.
     * 
     * @param v1 first sample variance
     * @param v2 second sample variance
     * @param n1 first sample n
     * @param n2 second sample n
     * @return approximate degrees of freedom
     */
    protected double df(double v1, double v2, double n1, double n2) {
        return (((v1 / n1) + (v2 / n2)) * ((v1 / n1) + (v2 / n2))) /
        ((v1 * v1) / (n1 * n1 * (n1 - 1d)) + (v2 * v2) /
                (n2 * n2 * (n2 - 1d)));
    }
    
    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.inference.TTest#pairedT(double[], double[])
     */
    public double pairedT(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        if ((sample1 == null) || (sample2 == null ||
                Math.min(sample1.length, sample2.length) < 2)) {
            throw new IllegalArgumentException("insufficient data for t statistic");
        }
        double meanDifference = StatUtils.meanDifference(sample1, sample2);
        return t(meanDifference, 0,  
                StatUtils.varianceDifference(sample1, sample2, meanDifference),
                (double) sample1.length);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.inference.TTest#pairedTTest(double[], double[])
     */
    public double pairedTTest(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException {
        double meanDifference = StatUtils.meanDifference(sample1, sample2);
        return tTest(meanDifference, 0, 
                StatUtils.varianceDifference(sample1, sample2, meanDifference), 
                (double) sample1.length);
    }

    /* (non-Javadoc)
     * @see org.apache.commons.math.stat.inference.TTest#pairedTTest(double[], double[], double)
     */
    public boolean pairedTTest(
        double[] sample1,
        double[] sample2,
        double alpha)
        throws IllegalArgumentException, MathException {
        // TODO Auto-generated method stub
        return false;
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
    protected double t(double m, double mu, double v, double n) {
        return (m - mu) / Math.sqrt(v / n);
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
    protected double t(double m1, double m2,  double v1, double v2, double n1,double n2)  {
        return (m1 - m2) / Math.sqrt((v1 / n1) + (v2 / n2));
    }

    /**
     * @param mu comparison constant
     * @param observed array of values
     * @return t statistic
     * @throws IllegalArgumentException if input array length is less than 2
     */
    public double t(double mu, double[] observed)
    throws IllegalArgumentException {
        if ((observed == null) || (observed.length < 2)) {
            throw new IllegalArgumentException("insufficient data for t statistic");
        }
        return t(StatUtils.mean(observed), mu, StatUtils.variance(observed), observed.length);
    }

    /**
     * @param mu comparison constant
     * @param sampleStats StatisticalSummary holding sample summary statitstics
     * @return t statistic
     * @throws IllegalArgumentException if the precondition is not met
     */
    public double t(double mu, StatisticalSummary sampleStats)
    throws IllegalArgumentException {
        if ((sampleStats == null) || (sampleStats.getN() < 2)) {
            throw new IllegalArgumentException("insufficient data for t statistic");
        }
        return t(sampleStats.getMean(), mu, sampleStats.getVariance(), sampleStats.getN());
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
                Math.min(sample1.length, sample2.length) < 2)) {
            throw new IllegalArgumentException("insufficient data for t statistic");
        }
        return t(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1),
                StatUtils.variance(sample2),  (double) sample1.length, (double) sample2.length);
    }

    /**
     * @param sampleStats1 StatisticalSummary describing data from the first sample
     * @param sampleStats2 StatisticalSummary describing data from the second sample
     * @return t statistic
     * @throws IllegalArgumentException if the precondition is not met
     */
    public double t(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException {
        if ((sampleStats1 == null) ||
                (sampleStats2 == null ||
                        Math.min(sampleStats1.getN(), sampleStats2.getN()) < 2)) {
            throw new IllegalArgumentException("insufficient data for t statistic");
        }
        return t(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(),
                sampleStats2.getVariance(), (double) sampleStats1.getN(), (double) sampleStats2.getN());
    }

    /**
     * Computes p-value for 2-sided, 1-sample t-test.
     * 
     * @param m sample mean
     * @param mu constant to test against
     * @param v sample variance
     * @param n sample n
     * @return p-value
     * @throws MathException if an error occurs computing the p-value
     */
    protected double tTest(double m, double mu, double v, double n)
    throws MathException {
        double t = Math.abs(t(m, mu, v, n));
        TDistribution tDistribution =
            DistributionFactory.newInstance().createTDistribution(n - 1);
        return 1.0 - tDistribution.cumulativeProbability(-t, t);
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
     * @throws MathException if an error occurs computing the p-value
     */
    protected double tTest(double m1, double m2, double v1, double v2, double n1, double n2)
    throws MathException {
        double t = Math.abs(t(m1, m2, v1, v2, n1, n2));
        TDistribution tDistribution =
            DistributionFactory.newInstance().createTDistribution(df(v1, v2, n1, n2));
        return 1.0 - tDistribution.cumulativeProbability(-t, t);
    }

    /**
     * @param mu constant value to compare sample mean against
     * @param sample array of sample data values
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    public double tTest(double mu, double[] sample)
    throws IllegalArgumentException, MathException {
        if ((sample == null) || (sample.length < 2)) {
            throw new IllegalArgumentException("insufficient data for t statistic");
        }
        return tTest( StatUtils.mean(sample), mu, StatUtils.variance(sample), sample.length);
    }

    /**
     * @param mu constant value to compare sample mean against
     * @param sample array of sample data values
     * @param alpha significance level of the test
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    public boolean tTest(double mu, double[] sample, double alpha)
    throws IllegalArgumentException, MathException {
        if ((alpha <= 0) || (alpha > 0.5)) {
            throw new IllegalArgumentException("bad significance level: " + alpha);
        }
        return (tTest(mu, sample) < alpha);
    }

    /**
     * @param mu constant value to compare sample mean against
     * @param sampleStats StatisticalSummary describing sample data
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    public double tTest(double mu, StatisticalSummary sampleStats)
    throws IllegalArgumentException, MathException {
        if ((sampleStats == null) || (sampleStats.getN() < 2)) {
            throw new IllegalArgumentException("insufficient data for t statistic");
        }
        return tTest(sampleStats.getMean(), mu, sampleStats.getVariance(), sampleStats.getN());
    }

    /**
     * @param mu constant value to compare sample mean against
     * @param sampleStats StatisticalSummary describing sample data values
     * @param alpha significance level of the test
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    public boolean tTest( double mu, StatisticalSummary sampleStats,double alpha)
    throws IllegalArgumentException, MathException {
        if ((alpha <= 0) || (alpha > 0.5)) {
            throw new IllegalArgumentException("bad significance level: " + alpha);
        }
        return (tTest(mu, sampleStats) < alpha);
    }

    /**
     *
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return tTest p-value
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    public double tTest(double[] sample1, double[] sample2)
    throws IllegalArgumentException, MathException {
        if ((sample1 == null) || (sample2 == null ||
                Math.min(sample1.length, sample2.length) < 2)) {
            throw new IllegalArgumentException("insufficient data");
        }
        return tTest(StatUtils.mean(sample1), StatUtils.mean(sample2), StatUtils.variance(sample1),
                StatUtils.variance(sample2), (double) sample1.length, (double) sample2.length);
    }

    /**
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @param alpha significance level
     * @return true if the null hypothesis can be rejected with 
     *     confidence 1 - alpha
     * @throws IllegalArgumentException if the preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    public boolean tTest(double[] sample1, double[] sample2, double alpha)
    throws IllegalArgumentException, MathException {
        if ((alpha <= 0) || (alpha > 0.5)) {
            throw new IllegalArgumentException("bad significance level: " + alpha);
        }
        return (tTest(sample1, sample2) < alpha);
    }

    /**
     * @param sampleStats1 StatisticalSummary describing data from the first sample
     * @param sampleStats2 StatisticalSummary describing data from the second sample
     * @return p-value for t-test
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    public double tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
    throws IllegalArgumentException, MathException {
        if ((sampleStats1 == null) || (sampleStats2 == null ||
                Math.min(sampleStats1.getN(), sampleStats2.getN()) < 2)) {
            throw new IllegalArgumentException("insufficient data for t statistic");
        }
        return tTest(sampleStats1.getMean(), sampleStats2.getMean(), sampleStats1.getVariance(),
                sampleStats2.getVariance(), (double) sampleStats1.getN(), (double) sampleStats2.getN());
    }

    /**
     * @param sampleStats1 StatisticalSummary describing sample data values
     * @param sampleStats2 StatisticalSummary describing sample data values
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with 
     *     confidence 1 - alpha
     * @throws IllegalArgumentException if the preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    public boolean tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2,
            double alpha)
    throws IllegalArgumentException, MathException {
        if ((alpha <= 0) || (alpha > 0.5)) {
            throw new IllegalArgumentException("bad significance level: " + alpha);
        }
        return (tTest(sampleStats1, sampleStats2) < alpha);
    }

}
