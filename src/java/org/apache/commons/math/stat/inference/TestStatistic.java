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
package org.apache.commons.math.stat;

import org.apache.commons.math.MathException;

/**
 * A collection of commonly used test statistics and statistical tests.
 * 
 * @version $Revision: 1.1 $ $Date: 2004/04/11 20:19:14 $ 
 */
public interface TestStatistic {
    
    /**
     * Computes the <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda35f.htm">
     * Chi-Square statistic</a> comparing <code>observed</code> and <code>expected</code> 
     * freqeuncy counts. 
     * <p>
     * This statistic can be used to perform Chi-Square tests.
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>Expected counts must all be positive.  
     * </li>
     * <li>Observed counds must all be >= 0.   
     * </li>
     * <li>The observed and expected arrays must have the same length and
     * their common length must be at least 2.  
     * </li></ul><p>
     * If any of the preconditions are not met, an 
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param observed array of observed frequency counts
     * @param expected array of exptected frequency counts
     * @return chiSquare statistic
     * @throws IllegalArgumentException if preconditions are not met
     */
    double chiSquare(double[] expected, double[] observed) 
        throws IllegalArgumentException;
    
    /**
     * Returns the <i>observed significance level</i>, or <a href=
     * "http://www.cas.lancs.ac.uk/glossary_v1.1/hyptest.html#pvalue">
     * p-value</a>, associated with a 
     * <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda35f.htm">
     * Chi-square goodness of fit test</a> comparing the <code>observed</code> 
     * frequency counts to those in the <code>expected</code> array.
     * <p>
     * The number returned is the smallest significance level at which one can reject 
     * the null hypothesis that the observed counts conform to the frequency distribution 
     * described by the expected counts. 
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>Expected counts must all be positive.  
     * </li>
     * <li>Observed counds must all be >= 0.   
     * </li>
     * <li>The observed and expected arrays must have the same length and
     * their common length must be at least 2.  
     * </li></ul><p>
     * If any of the preconditions are not met, an 
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param observed array of observed frequency counts
     * @param expected array of exptected frequency counts
     * @return p-value
     * @throws IllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs computing the p-value
     */
    double chiSquareTest(double[] expected, double[] observed) 
        throws IllegalArgumentException, MathException;
    
    /**
     * Performs a <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda35f.htm">
     * Chi-square goodness of fit test</a> evaluating the null hypothesis that the observed counts 
     * conform to the frequency distribution described by the expected counts, with 
     * significance level <code>alpha</code>.
     * <p>
     * <strong>Example:</strong><br>
     * To test the hypothesis that <code>observed</code> follows 
     * <code>expected</code> at the 99% level, use <p>
     * <code>chiSquareTest(expected, observed, 0.01) </code>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>Expected counts must all be positive.  
     * </li>
     * <li>Observed counds must all be >= 0.   
     * </li>
     * <li>The observed and expected arrays must have the same length and
     * their common length must be at least 2.  
     * <li> <code> 0 < alpha < 0.5 </code>
     * </li></ul><p>
     * If any of the preconditions are not met, an 
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param observed array of observed frequency counts
     * @param expected array of exptected frequency counts
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @throws IllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    boolean chiSquareTest(double[] expected, double[] observed, double alpha) 
        throws IllegalArgumentException, MathException;
    
    /**
     * Computes a <a href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc22.htm#formula"> 
     * t statistic </a> given observed values and a comparison constant.
     * <p>
     * This statistic can be used to perform a one sample t-test for the mean.
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The observed array length must be at least 2.
     * </li></ul>
     *
     * @param mu comparison constant
     * @param observed array of values
     * @return t statistic
     * @throws IllegalArgumentException if input array length is less than 2
     */
    double t(double mu, double[] observed) 
        throws IllegalArgumentException;
    
    /**
     * Computes a <a href="http://www.itl.nist.gov/div898/handbook/prc/section3/prc31.htm">
     * 2-sample t statistic </a>, without the assumption of equal sample variances.
     * <p>
     * This statistic can be used to perform a two-sample t-test to compare
     * sample means.
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The observed array lengths must both be at least 5.
     * </li></ul>
     *
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return t statistic
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if the statistic can not be computed do to a
     *         convergence or other numerical error.
     */
    double t(double[] sample1, double[] sample2) 
        throws IllegalArgumentException, MathException;
    
    /**
     * Returns the <i>observed significance level</i>, or 
     * <a href="http://www.cas.lancs.ac.uk/glossary_v1.1/hyptest.html#pvalue">
     * p-value</a>, associated with a two-sample, two-tailed t-test 
     * comparing the means of the input arrays.
     * <p>
     * The number returned is the smallest significance level
     * at which one can reject the null hypothesis that the two means are
     * equal in favor of the two-sided alternative that they are different. 
     * For a one-sided test, divide the returned value by 2.
     * <p>
     * The test does not assume that the underlying popuation variances are
     * equal and it uses approximated degrees of freedom computed from the 
     * sample data as described 
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section3/prc31.htm">here</a>
     * <p>
     * <strong>Usage Note:</strong><br>
     * The validity of the p-value depends on the assumptions of the parametric
     * t-test procedure, as discussed 
     * <a href="http://www.basic.nwu.edu/statguidefiles/ttest_unpaired_ass_viol.html">here</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The observed array lengths must both be at least 5.
     * </li></ul>
     *
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @return p-value for t-test
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    double tTest(double[] sample1, double[] sample2)
        throws IllegalArgumentException, MathException;
    
    /**
     * Performs a <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda353.htm">
     * two-sided t-test</a> evaluating the null hypothesis that <code>sample1</code> 
     * and <code>sample2</code> are drawn from populations with the same mean, 
     * with significance level <code>alpha</code>.
     * <p>
     * Returns <code>true</code> iff the null hypothesis that the means are
     * equal can be rejected with confidence <code>1 - alpha</code>.  To 
     * perform a 1-sided test, use <code>alpha / 2</code>
     * <p>
     * <strong>Examples:</strong><br><ol>
     * <li>To test the (2-sided) hypothesis <code>mean 1 = mean 2 </code> at
     * the 95% level, use <br><code>tTest(sample1, sample2, 0.05) </code>
     * </li>
     * <li>To test the (one-sided) hypothesis <code> mean 1 < mean 2 </code>
     * at the 99% level, first verify that the measured mean of 
     * <code>sample 1</code> is less than the mean of <code>sample 2</code>
     * and then use <br><code>tTest(sample1, sample2, 0.005) </code>
     * </li></ol>
     * <p>
     * The test does not assume that the underlying popuation variances are
     * equal and it uses approximated degrees of freedom computed from the 
     * sample data as described 
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section3/prc31.htm">here</a>
     * <p>
     * <strong>Usage Note:</strong><br>
     * The validity of the test depends on the assumptions of the parametric
     * t-test procedure, as discussed 
     * <a href="http://www.basic.nwu.edu/statguidefiles/ttest_unpaired_ass_viol.html">here</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The observed array lengths must both be at least 5.
     * </li>
     * <li> <code> 0 < alpha < 0.5 </code>
     * </li></ul>
     *
     * @param sample1 array of sample data values
     * @param sample2 array of sample data values
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with 
     * confidence 1 - alpha
     * @throws IllegalArgumentException if the preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    boolean tTest(double[] sample1, double[] sample2, double alpha)
        throws IllegalArgumentException, MathException;
    
    /**
     * Performs a <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda353.htm">
     * two-sided t-test</a> evaluating the null hypothesis that the mean of the population from
     *  which <code>sample</code> is drawn equals <code>mu</code>.
     * <p>
     * Returns <code>true</code> iff the null hypothesis can be 
     * rejected with confidence <code>1 - alpha</code>.  To 
     * perform a 1-sided test, use <code>alpha / 2</code>
     * <p>
     * <strong>Examples:</strong><br><ol>
     * <li>To test the (2-sided) hypothesis <code>sample mean = mu </code> at
     * the 95% level, use <br><code>tTest(mu, sample, 0.05) </code>
     * </li>
     * <li>To test the (one-sided) hypothesis <code> sample mean < mu </code>
     * at the 99% level, first verify that the measured sample mean is less 
     * than <code>mu</code> and then use 
     * <br><code>tTest(mu, sample, 0.005) </code>
     * </li></ol>
     * <p>
     * <strong>Usage Note:</strong><br>
     * The validity of the test depends on the assumptions of the one-sample 
     * parametric t-test procedure, as discussed 
     * <a href="http://www.basic.nwu.edu/statguidefiles/sg_glos.html#one-sample">here</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The observed array length must be at least 5.
     * </li></ul>
     *
     * @param mu constant value to compare sample mean against
     * @param sample array of sample data values
     * @param alpha significance level of the test
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error computing the p-value
     */
    boolean tTest(double mu, double[] sample, double alpha)
        throws IllegalArgumentException, MathException;
    
    /**
     * Returns the <i>observed significance level</i>, or 
     * <a href="http://www.cas.lancs.ac.uk/glossary_v1.1/hyptest.html#pvalue">
     * p-value</a>, associated with a one-sample, two-tailed t-test 
     * comparing the mean of the input array with the constant <code>mu</code>.
     * <p>
     * The number returned is the smallest significance level
     * at which one can reject the null hypothesis that the mean equals 
     * <code>mu</code> in favor of the two-sided alternative that the mean
     * is different from <code>mu</code>. For a one-sided test, divide the 
     * returned value by 2.
     * <p>
     * <strong>Usage Note:</strong><br>
     * The validity of the test depends on the assumptions of the parametric
     * t-test procedure, as discussed 
     * <a href="http://www.basic.nwu.edu/statguidefiles/ttest_unpaired_ass_viol.html">here</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The observed array length must be at least 5.
     * </li></ul>
     *
     * @param mu constant value to compare sample mean against
     * @param sample array of sample data values
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    double tTest(double mu, double[] sample)
        throws IllegalArgumentException, MathException;
    
    /**
     * Computes a <a href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc22.htm#formula">
     * t statistic </a> to use in comparing the dataset described by <code>sampleStats</code>
     *  to <code>mu</code>.
     * <p>
     * This statistic can be used to perform a one sample t-test for the mean.
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li><code>observed.getN() > = 2</code>.
     * </li></ul>
     *
     * @param mu comparison constant
     * @param sampleStats DescriptiveStatistics holding sample summary statitstics
     * @return t statistic
     * @throws IllegalArgumentException if the precondition is not met
     */
    double t(double mu, StatisticalSummary sampleStats) 
        throws IllegalArgumentException;
    
    /**
     * Computes a <a href="http://www.itl.nist.gov/div898/handbook/prc/section3/prc31.htm">
     * 2-sample t statistic </a>, comparing the means of the datasets described
     * by two {@link StatisticalSummary} instances without the assumption of equal sample variances.
     * <p>
     * This statistic can be used to perform a two-sample t-test to compare
     * sample means.
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The datasets described by the two Univariates must each contain
     * at least 5 observations.
     * </li></ul>
     *
     * @param sampleStats1 StatisticalSummary describing data from the first sample
     * @param sampleStats2 StatisticalSummary describing data from the second sample
     * @return t statistic
     * @throws IllegalArgumentException if the precondition is not met
     */
    double t(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2) 
        throws IllegalArgumentException;
    
    /**
     * Returns the <i>observed significance level</i>, or 
     * <a href="http://www.cas.lancs.ac.uk/glossary_v1.1/hyptest.html#pvalue">
     * p-value</a>, associated with a two-sample, two-tailed t-test 
     * comparing the means of the datasets described by two Univariates.
     * <p>
     * The number returned is the smallest significance level
     * at which one can reject the null hypothesis that the two means are
     * equal in favor of the two-sided alternative that they are different. 
     * For a one-sided test, divide the returned value by 2.
     * <p>
     * The test does not assume that the underlying popuation variances are
     * equal and it uses approximated degrees of freedom computed from the 
     * sample data as described 
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section3/prc31.htm">here</a>
     * <p>
     * <strong>Usage Note:</strong><br>
     * The validity of the p-value depends on the assumptions of the parametric
     * t-test procedure, as discussed 
     * <a href="http://www.basic.nwu.edu/statguidefiles/ttest_unpaired_ass_viol.html">here</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The datasets described by the two Univariates must each contain
     * at least 5 observations.
     * </li></ul>
     *
     * @param sampleStats1 StatisticalSummary describing data from the first sample
     * @param sampleStats2 StatisticalSummary describing data from the second sample
     * @return p-value for t-test
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    double tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2)
        throws IllegalArgumentException, MathException;
    
    /**
     * Performs a <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda353.htm">
     * two-sided t-test</a> evaluating the null hypothesis that <code>sampleStats1</code>
     * and <code>sampleStats2</code> describe datasets drawn from populations with the 
     * same mean, with significance level <code>alpha</code>.
     * <p>
     * Returns <code>true</code> iff the null hypothesis that the means are
     * equal can be rejected with confidence <code>1 - alpha</code>.  To 
     * perform a 1-sided test, use <code>alpha / 2</code>
     * <p>
     * <strong>Examples:</strong><br><ol>
     * <li>To test the (2-sided) hypothesis <code>mean 1 = mean 2 </code> at
     * the 95% level, use 
     * <br><code>tTest(sampleStats1, sampleStats2, 0.05) </code>
     * </li>
     * <li>To test the (one-sided) hypothesis <code> mean 1 < mean 2 </code>
     * at the 99% level, first verify that the measured mean of 
     * <code>sample 1</code> is less than the mean of <code>sample 2</code>
     * and then use <br><code>tTest(sampleStats1, sampleStats2, 0.005) </code>
     * </li></ol>
     * <p>
     * The test does not assume that the underlying popuation variances are
     * equal and it uses approximated degrees of freedom computed from the 
     * sample data as described 
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section3/prc31.htm">here</a>
     * <p>
     * <strong>Usage Note:</strong><br>
     * The validity of the test depends on the assumptions of the parametric
     * t-test procedure, as discussed 
     * <a href="http://www.basic.nwu.edu/statguidefiles/ttest_unpaired_ass_viol.html">here</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The datasets described by the two Univariates must each contain
     * at least 5 observations.
     * </li>
     * <li> <code> 0 < alpha < 0.5 </code>
     * </li></ul>
     *
     * @param sampleStats1 StatisticalSummary describing sample data values
     * @param sampleStats2 StatisticalSummary describing sample data values
     * @param alpha significance level of the test
     * @return true if the null hypothesis can be rejected with 
     * confidence 1 - alpha
     * @throws IllegalArgumentException if the preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    boolean tTest(StatisticalSummary sampleStats1, StatisticalSummary sampleStats2, 
        double alpha)
        throws IllegalArgumentException, MathException;
    
    /**
     * Performs a <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda353.htm">
     * two-sided t-test</a> evaluating the null hypothesis that the mean of the population from
     * which the dataset described by <code>stats</code> is drawn equals <code>mu</code>.
     * <p>
     * Returns <code>true</code> iff the null hypothesis can be 
     * rejected with confidence <code>1 - alpha</code>.  To 
     * perform a 1-sided test, use <code>alpha / 2</code>
     * <p>
     * <strong>Examples:</strong><br><ol>
     * <li>To test the (2-sided) hypothesis <code>sample mean = mu </code> at
     * the 95% level, use <br><code>tTest(mu, sampleStats, 0.05) </code>
     * </li>
     * <li>To test the (one-sided) hypothesis <code> sample mean < mu </code>
     * at the 99% level, first verify that the measured sample mean is less 
     * than <code>mu</code> and then use 
     * <br><code>tTest(mu, sampleStats, 0.005) </code>
     * </li></ol>
     * <p>
     * <strong>Usage Note:</strong><br>
     * The validity of the test depends on the assumptions of the one-sample 
     * parametric t-test procedure, as discussed 
     * <a href="http://www.basic.nwu.edu/statguidefiles/sg_glos.html#one-sample">here</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The sample must include at least 5 observations.
     * </li></ul>
     *
     * @param mu constant value to compare sample mean against
     * @param sampleStats StatisticalSummary describing sample data values
     * @param alpha significance level of the test
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    boolean tTest(double mu, StatisticalSummary sampleStats, double alpha)
        throws IllegalArgumentException, MathException;
    
    /**
     * Returns the <i>observed significance level</i>, or 
     * <a href="http://www.cas.lancs.ac.uk/glossary_v1.1/hyptest.html#pvalue">
     * p-value</a>, associated with a one-sample, two-tailed t-test 
     * comparing the mean of the dataset described by <code>sampleStats</code>
     * with the constant <code>mu</code>.
     * <p>
     * The number returned is the smallest significance level
     * at which one can reject the null hypothesis that the mean equals 
     * <code>mu</code> in favor of the two-sided alternative that the mean
     * is different from <code>mu</code>. For a one-sided test, divide the 
     * returned value by 2.
     * <p>
     * <strong>Usage Note:</strong><br>
     * The validity of the test depends on the assumptions of the parametric
     * t-test procedure, as discussed 
     * <a href="http://www.basic.nwu.edu/statguidefiles/ttest_unpaired_ass_viol.html">here</a>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>The sample must contain at least 5 observations.
     * </li></ul>
     *
     * @param mu constant value to compare sample mean against
     * @param sampleStats StatisticalSummary describing sample data
     * @return p-value
     * @throws IllegalArgumentException if the precondition is not met
     * @throws MathException if an error occurs computing the p-value
     */
    double tTest(double mu, StatisticalSummary sampleStats)
        throws IllegalArgumentException, MathException;
}

