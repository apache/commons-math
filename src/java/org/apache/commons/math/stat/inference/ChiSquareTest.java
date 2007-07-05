/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import org.apache.commons.math.MathException;

/**
 * An interface for Chi-Square tests.
 *
 * @version $Revision$ $Date$ 
 */
public interface ChiSquareTest {
     
     /**
     * Computes the <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda35f.htm">
     * Chi-Square statistic</a> comparing <code>observed</code> and <code>expected</code> 
     * freqeuncy counts. 
     * <p>
     * This statistic can be used to perform a Chi-Square test evaluating the null hypothesis that
     *  the observed counts follow the expected distribution.
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>Expected counts must all be positive.  
     * </li>
     * <li>Observed counts must all be >= 0.   
     * </li>
     * <li>The observed and expected arrays must have the same length and
     * their common length must be at least 2.  
     * </li></ul><p>
     * If any of the preconditions are not met, an 
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return chiSquare statistic
     * @throws IllegalArgumentException if preconditions are not met
     */
    double chiSquare(double[] expected, long[] observed) 
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
     * <li>Observed counts must all be >= 0.   
     * </li>
     * <li>The observed and expected arrays must have the same length and
     * their common length must be at least 2.  
     * </li></ul><p>
     * If any of the preconditions are not met, an 
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @return p-value
     * @throws IllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs computing the p-value
     */
    double chiSquareTest(double[] expected, long[] observed) 
        throws IllegalArgumentException, MathException;
    
    /**
     * Performs a <a href="http://www.itl.nist.gov/div898/handbook/eda/section3/eda35f.htm">
     * Chi-square goodness of fit test</a> evaluating the null hypothesis that the observed counts 
     * conform to the frequency distribution described by the expected counts, with 
     * significance level <code>alpha</code>.  Returns true iff the null hypothesis can be rejected
     * with 100 * (1 - alpha) percent confidence.
     * <p>
     * <strong>Example:</strong><br>
     * To test the hypothesis that <code>observed</code> follows 
     * <code>expected</code> at the 99% level, use <p>
     * <code>chiSquareTest(expected, observed, 0.01) </code>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>Expected counts must all be positive.  
     * </li>
     * <li>Observed counts must all be >= 0.   
     * </li>
     * <li>The observed and expected arrays must have the same length and
     * their common length must be at least 2.  
     * <li> <code> 0 < alpha < 0.5 </code>
     * </li></ul><p>
     * If any of the preconditions are not met, an 
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param observed array of observed frequency counts
     * @param expected array of expected frequency counts
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @throws IllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    boolean chiSquareTest(double[] expected, long[] observed, double alpha) 
        throws IllegalArgumentException, MathException;
    
    /**
     *  Computes the Chi-Square statistic associated with a 
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section4/prc45.htm">
     *  chi-square test of independence</a> based on the input <code>counts</code>
     *  array, viewed as a two-way table.  
     * <p>
     * The rows of the 2-way table are <code>count[0], ... , count[count.length - 1] </code>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>All counts must be >= 0.  
     * </li>
     * <li>The count array must be rectangular (i.e. all count[i] subarrays must have the same length). 
     * </li>
     * <li>The 2-way table represented by <code>counts</code> must have at least 2 columns and
     *        at least 2 rows.
     * </li>
     * </li></ul><p>
     * If any of the preconditions are not met, an 
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param counts array representation of 2-way table
     * @return chiSquare statistic
     * @throws IllegalArgumentException if preconditions are not met
     */
    double chiSquare(long[][] counts) 
    throws IllegalArgumentException;
    
    /**
     * Returns the <i>observed significance level</i>, or <a href=
     * "http://www.cas.lancs.ac.uk/glossary_v1.1/hyptest.html#pvalue">
     * p-value</a>, associated with a 
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section4/prc45.htm">
     * chi-square test of independence</a> based on the input <code>counts</code>
     * array, viewed as a two-way table.  
     * <p>
     * The rows of the 2-way table are <code>count[0], ... , count[count.length - 1] </code>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>All counts must be >= 0.  
     * </li>
     * <li>The count array must be rectangular (i.e. all count[i] subarrays must have the same length). 
     * </li>
     * <li>The 2-way table represented by <code>counts</code> must have at least 2 columns and
     *        at least 2 rows.
     * </li>
     * </li></ul><p>
     * If any of the preconditions are not met, an 
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param counts array representation of 2-way table
     * @return p-value
     * @throws IllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs computing the p-value
     */
    double chiSquareTest(long[][] counts) 
    throws IllegalArgumentException, MathException;
    
    /**
     * Performs a <a href="http://www.itl.nist.gov/div898/handbook/prc/section4/prc45.htm">
     * chi-square test of independence</a> evaluating the null hypothesis that the classifications 
     * represented by the counts in the columns of the input 2-way table are independent of the rows,
     * with significance level <code>alpha</code>.  Returns true iff the null hypothesis can be rejected
     * with 100 * (1 - alpha) percent confidence.
     * <p>
     * The rows of the 2-way table are <code>count[0], ... , count[count.length - 1] </code>
     * <p>
     * <strong>Example:</strong><br>
     * To test the null hypothesis that the counts in <code>count[0], ... , count[count.length - 1] </code>
     *  all correspond to the same underlying probability distribution at the 99% level, use <p>
     * <code>chiSquareTest(counts, 0.01) </code>
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>All counts must be >= 0.  
     * </li>
     * <li>The count array must be rectangular (i.e. all count[i] subarrays must have the same length). 
     * </li>
     * <li>The 2-way table represented by <code>counts</code> must have at least 2 columns and
     *        at least 2 rows.
     * </li>
     * </li></ul><p>
     * If any of the preconditions are not met, an 
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param counts array representation of 2-way table
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @throws IllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    boolean chiSquareTest(long[][] counts, double alpha) 
    throws IllegalArgumentException, MathException;

    /**
     * <p>Computes a 
     * <a href="http://www.itl.nist.gov/div898/software/dataplot/refman1/auxillar/chi2samp.htm">
     * Chi-Square two sample test statistic</a> comparing bin frequency counts
     * in <code>observed1</code> and <code>observed2</code>.  The
     * sums of frequency counts in the two samples are not required to be the
     * same.  The formula used to compute the test statistic is</p>
     * <code>
     * &sum;[(K * observed1[i] - observed2[i]/K)<sup>2</sup> / (observed1[i] + observed2[i])]
     * </code> where 
     * <br/><code>K = &sqrt;[&sum(observed2 / &sum;(observed1)]</code>
     * </p>
     * <p>This statistic can be used to perform a Chi-Square test evaluating the null hypothesis that
     * both observed counts follow the same distribution.
     * <p>
     * <strong>Preconditions</strong>: <ul>
     * <li>Observed counts must be non-negative.
     * </li>
     * <li>Observed counts for a specific bin must not both be zero.
     * </li>
     * <li>Observed counts for a specific sample must not all be 0.
     * </li>
     * <li>The arrays <code>observed1</code> and <code>observed2</code> must have the same length and
     * their common length must be at least 2.
     * </li></ul><p>
     * If any of the preconditions are not met, an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data set
     * @return chiSquare statistic
     * @throws IllegalArgumentException if preconditions are not met
     */
    double chiSquareDataSetsComparison(long[] observed1, long[] observed2)
    	throws IllegalArgumentException;

    /**
     * <p>Returns the <i>observed significance level</i>, or <a href=
     * "http://www.cas.lancs.ac.uk/glossary_v1.1/hyptest.html#pvalue">
     * p-value</a>, associated with a Chi-Square two sample test comparing
     * bin frequency counts in <code>observed1</code> and 
     * <code>observed2</code>.
     * </p>
     * <p>The number returned is the smallest significance level at which one
     * can reject the null hypothesis that the observed counts conform to the
     * same distribution.
     * </p>
     * <p>See {@link #chiSquareDataSetsComparison(long[], long[])} for details
     * on the formula used to compute the test statistic. The degrees of
     * of freedom used to perform the test is one less than the common length
     * of the input observed count arrays.
     * </p>
     * <strong>Preconditions</strong>: <ul>
     * <li>Observed counts must be non-negative.
     * </li>
     * <li>Observed counts for a specific bin must not both be zero.
     * </li>
     * <li>Observed counts for a specific sample must not all be 0.
     * </li>
     * <li>The arrays <code>observed1</code> and <code>observed2</code> must
     * have the same length and
     * their common length must be at least 2.
     * </li></ul><p>
     * If any of the preconditions are not met, an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data set
     * @return p-value
     * @throws IllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs computing the p-value
     */
    double chiSquareTestDataSetsComparison(long[] observed1, long[] observed2)
    	throws IllegalArgumentException, MathException;

    /**
     * <p>Performs a Chi-Square two sample test comparing two binned data
     * sets. The test evaluates the null hypothesis that the two lists of
     * observed counts conform to the same frequency distribution, with
     * significance level <code>alpha</code>.  Returns true iff the null
     * hypothesis can be rejected with 100 * (1 - alpha) percent confidence.
     * </p>
     * <p>See {@link #chiSquareDataSetsComparison(long[], long[])} for 
     * details on the formula used to compute the Chisquare statistic used
     * in the test. The degrees of of freedom used to perform the test is
     * one less than the common length of the input observed count arrays.
     * </p>
     * <strong>Preconditions</strong>: <ul>
     * <li>Observed counts must be non-negative.
     * </li>
     * <li>Observed counts for a specific bin must not both be zero.
     * </li>
     * <li>Observed counts for a specific sample must not all be 0.
     * </li>
     * <li>The arrays <code>observed1</code> and <code>observed2</code> must
     * have the same length and their common length must be at least 2.
     * </li>
     * <li> <code> 0 < alpha < 0.5 </code>
     * </li></ul><p>
     * If any of the preconditions are not met, an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param observed1 array of observed frequency counts of the first data set
     * @param observed2 array of observed frequency counts of the second data set
     * @param alpha significance level of the test
     * @return true iff null hypothesis can be rejected with confidence
     * 1 - alpha
     * @throws IllegalArgumentException if preconditions are not met
     * @throws MathException if an error occurs performing the test
     */
    boolean chiSquareTestDataSetsComparison(long[] observed1, long[] observed2, double alpha)
    	throws IllegalArgumentException, MathException;

}
