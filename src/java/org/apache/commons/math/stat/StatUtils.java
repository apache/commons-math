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

import org.apache.commons.math.stat.univariate.UnivariateStatistic;
import org.apache.commons.math.stat.univariate.moment.Mean;
import org.apache.commons.math.stat.univariate.moment.Variance;
import org.apache.commons.math.stat.univariate.rank.Max;
import org.apache.commons.math.stat.univariate.rank.Min;
import org.apache.commons.math.stat.univariate.rank.Percentile;
import org.apache.commons.math.stat.univariate.summary.Product;
import org.apache.commons.math.stat.univariate.summary.Sum;
import org.apache.commons.math.stat.univariate.summary.SumOfLogs;
import org.apache.commons.math.stat.univariate.summary.SumOfSquares;

/**
 * StatUtils provides static implementations of common double[] based
 * statistical methods. These return a single result value or in some cases, as
 * identified in the javadoc for each method, <code>Double.NaN.</code>
 * @version $Revision: 1.29 $ $Date: 2004/06/23 16:26:17 $
 */
public final class StatUtils {

    /** sum */
    private static UnivariateStatistic sum = new Sum();

    /** sumSq */
    private static UnivariateStatistic sumSq = new SumOfSquares();

    /** prod */
    private static UnivariateStatistic prod = new Product();

    /** sumLog */
    private static UnivariateStatistic sumLog = new SumOfLogs();

    /** min */
    private static UnivariateStatistic min = new Min();

    /** max */
    private static UnivariateStatistic max = new Max();

    /** mean */
    private static UnivariateStatistic mean = new Mean();

    /** variance */
    private static UnivariateStatistic variance = new Variance();

    /** variance */
    private static Percentile percentile = new Percentile();

    /**
     * Private Constructor
     */
    private StatUtils() {
    }

    /**
     * The sum of the values that have been added to Univariate.
     * @param values Is a double[] containing the values
     * @return the sum of the values or <code>Double.NaN</code> if the array is empty
     */
    public static double sum(final double[] values) {
        return sum.evaluate(values);
    }

    /**
     * The sum of the values that have been added to Univariate.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the sum of the values or <code>Double.NaN</code> if the array is empty
     */
    public static double sum(
        final double[] values,
        final int begin,
        final int length) {
        return sum.evaluate(values, begin, length);
    }

    /**
     * Returns the sum of the squares of the available values.
     * @param values Is a double[] containing the values
     * @return the sum of the squared values or <code>Double.NaN</code> if the array is empty
     */
    public static double sumSq(final double[] values) {
        return sumSq.evaluate(values);
    }

    /**
     * Returns the sum of the squares of the available values.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the sum of the squared values or <code>Double.NaN</code> if the array is empty
     */
    public static double sumSq(
        final double[] values,
        final int begin,
        final int length) {
        return sumSq.evaluate(values, begin, length);
    }

    /**
     * Returns the product for this collection of values
     * @param values Is a double[] containing the values
     * @return the product values or <code>Double.NaN</code> if the array is empty
     */
    public static double product(final double[] values) {
        return prod.evaluate(values);
    }

    /**
     * Returns the product for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the product values or <code>Double.NaN</code> if the array is empty
     */
    public static double product(
        final double[] values,
        final int begin,
        final int length) {
        return prod.evaluate(values, begin, length);
    }

    /**
     * Returns the sum of the natural logs for this collection of values
     * @param values Is a double[] containing the values
     * @return the sumLog value or <code>Double.NaN</code> if the array is empty
     */
    public static double sumLog(final double[] values) {
        return sumLog.evaluate(values);
    }

    /**
     * Returns the sum of the natural logs for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the sumLog value or <code>Double.NaN</code> if the array is empty
     */
    public static double sumLog(
        final double[] values,
        final int begin,
        final int length) {
        return sumLog.evaluate(values, begin, length);
    }

    /**
     * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
     * arithmetic mean </a> of the available values
     * @param values Is a double[] containing the values
     * @return the mean of the values or <code>Double.NaN</code> if the array is empty
     */
    public static double mean(final double[] values) {
        return mean.evaluate(values);
    }

    /**
      * Returns the <a href="http://www.xycoon.com/arithmetic_mean.htm">
      * arithmetic mean </a> of the available values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
      * @return the mean of the values or <code>Double.NaN</code> if the array is empty
      */
    public static double mean(
        final double[] values,
        final int begin,
        final int length) {
        return mean.evaluate(values, begin, length);
    }

    /**
     * Returns the variance of the available values. This uses a corrected
     * two pass algorithm as described in:
     * <p>
     * "Algorithms for Computing the Sample Variance: Analysis and
     * Recommendations", Chan, T.F., Golub, G.H., and LeVeque, R.J.
     * 1983, American Statistician, vol. 37, pp. 242-247.
     *
     * @param values Is a double[] containing the values
     * @return the result, <code>Double.NaN</code> for an empty array
     * or 0.0 for a single value set.
     */
    public static double variance(final double[] values) {
        return variance.evaluate(values);
    }

    /**
     * Returns the variance of the available values. This uses a corrected
     * two pass algorithm as described in:
     * <p>
     * "Algorithms for Computing the Sample Variance: Analysis and
     * Recommendations", Chan, T.F., Golub, G.H., and LeVeque, R.J.
     * 1983, American Statistician, vol. 37, pp. 242-247.
     *
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the result, <code>Double.NaN</code> for an empty array
     * or 0.0 for a single value set.
     */
    public static double variance(
        final double[] values,
        final int begin,
        final int length) {
        return variance.evaluate(values, begin, length);
    }

    /**
     * Returns the maximum of the available values
     * @param values Is a double[] containing the values
     * @return the maximum of the values or <code>Double.NaN</code> if the array is empty
     */
    public static double max(final double[] values) {
        return max.evaluate(values);
    }

    /**
     * Returns the maximum of the available values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the maximum of the values or <code>Double.NaN</code> if the array is empty
     */
    public static double max(
        final double[] values,
        final int begin,
        final int length) {
        return max.evaluate(values, begin, length);
    }

    /**
     * Returns the minimum of the available values
     * @param values Is a double[] containing the values
     * @return the minimum of the values or <code>Double.NaN</code> if the array is empty
     */
    public static double min(final double[] values) {
        return min.evaluate(values);
    }

    /**
     * Returns the minimum of the available values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the minimum of the values or <code>Double.NaN</code> if the array is empty
     */
    public static double min(
        final double[] values,
        final int begin,
        final int length) {
        return min.evaluate(values, begin, length);
    }
    
    /**
     * Returns an estimate for the pth percentile of the stored values. 
     * <p>
     * The implementation provided here follows the first estimation procedure presented
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc252.htm">here.</a>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>0 &lt; p &lt; 100</code> (otherwise an 
     * <code>IllegalArgumentException</code> is thrown)</li>
     * <li>at least one value must be stored (returns <code>Double.NaN
     *     </code> otherwise)</li>
     * </ul>
     * 
     * @param values Is a double[] containing the values
     * @param p the requested percentile (scaled from 0 - 100)
     * @return An estimate for the pth percentile of the data values
     */
    public static double percentile(final double[] values, final double p) {
            return percentile.evaluate(values,p);
    }

    /**
     * Returns an estimate for the pth percentile of the stored values. 
     *<p>
    * The implementation provided here follows the first estimation procedure presented
     * <a href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc252.htm">here.</a>
     * <p>
     * <strong>Preconditions</strong>:<ul>
     * <li><code>0 &lt; p &lt; 100</code> (otherwise an 
     * <code>IllegalArgumentException</code> is thrown)</li>
     * <li>at least one value must be stored (returns <code>Double.NaN
     *     </code> otherwise)</li>
     * </ul>
     * 
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @param p the requested percentile (scaled from 0 - 100)
     * @return An estimate for the pth percentile of the data values
     */
    public static double percentile(
            final double[] values,
            final int begin,
            final int length, 
            final double p) {
            return percentile.evaluate(values, begin, length, p);
    }   
    
    /**
     * Returns the sum of the (signed) differences between corresponding elements of the
     * input arrays -- i.e., sum(sample1[i] - sample2[i]).
     * 
     * @param sample1  the first array
     * @param sample2  the second array
     * @return sum of paired differences
     * @throws IllegalArgumentException if the arrays do not have the same
     * (positive) length
     */
    public static double sumDifference(final double[] sample1, final double[] sample2)
        throws IllegalArgumentException {
        int n = sample1.length;
        if (n  != sample2.length || n < 1) {
            throw new IllegalArgumentException 
                ("Input arrays must have the same (positive) length.");
        }
        double result = 0;
        for (int i = 0; i < n; i++) {
            result += sample1[i] - sample2[i];
        }
        return result;
    }
    
    /**
     * Returns the mean of the (signed) differences between corresponding elements of the
     * input arrays -- i.e., sum(sample1[i] - sample2[i]) / sample1.length.
     * 
     * @param sample1  the first array
     * @param sample2  the second array
     * @return mean of paired differences
     * @throws IllegalArgumentException if the arrays do not have the same
     * (positive) length
     */
    public static double meanDifference(final double[] sample1, final double[] sample2)
    throws IllegalArgumentException {
        return sumDifference(sample1, sample2) / (double) sample1.length;
    }
    
    /**
     * Returns the variance of the (signed) differences between corresponding elements of the
     * input arrays -- i.e., var(sample1[i] - sample2[i]).
     * 
     * @param sample1  the first array
     * @param sample2  the second array
     * @param meanDifference   the mean difference between corresponding entries 
     * @see #meanDifference(double[],double[])
     * @return variance of paired differences
     * @throws IllegalArgumentException if the arrays do not have the same
     * length or their common length is less than 2.
     */
    public static double varianceDifference(final double[] sample1, final double[] sample2, 
            double meanDifference)  throws IllegalArgumentException {
        double sum1 = 0d;
        double sum2 = 0d;
        double diff = 0d;
        int n = sample1.length;
        if (n < 2) {
            throw new IllegalArgumentException("Input array lengths must be at least 2.");
        }
        for (int i = 0; i < n; i++) {
            diff = sample1[i] - sample2[i];
            sum1 += (diff - meanDifference) *(diff - meanDifference);
            sum2 += diff - meanDifference;
        }
        return (sum1 - (sum2 * sum2 / (double) n)) / (double) (n - 1);
    }      
    
}