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
 * StatUtils provides easy static implementations of common double[] based
 * statistical methods. These return a single result value or in some cases, as
 * identified in the javadoc for each method, Double.NaN.
 * @version $Revision: 1.25 $ $Date: 2004/03/04 04:25:09 $
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
     * @return the sum of the values or Double.NaN if the array is empty
     */
    public static double sum(final double[] values) {
        return sum.evaluate(values);
    }

    /**
     * The sum of the values that have been added to Univariate.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the sum of the values or Double.NaN if the array is empty
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
     * @return the sum of the squared values or Double.NaN if the array is empty
     */
    public static double sumSq(final double[] values) {
        return sumSq.evaluate(values);
    }

    /**
     * Returns the sum of the squares of the available values.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the sum of the squared values or Double.NaN if the array is empty
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
     * @return the product values or Double.NaN if the array is empty
     */
    public static double product(final double[] values) {
        return prod.evaluate(values);
    }

    /**
     * Returns the product for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the product values or Double.NaN if the array is empty
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
     * @return the sumLog value or Double.NaN if the array is empty
     */
    public static double sumLog(final double[] values) {
        return sumLog.evaluate(values);
    }

    /**
     * Returns the sum of the natural logs for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the sumLog value or Double.NaN if the array is empty
     */
    public static double sumLog(
        final double[] values,
        final int begin,
        final int length) {
        return sumLog.evaluate(values, begin, length);
    }

    /**
     * Returns the <a href=http://www.xycoon.com/arithmetic_mean.htm>
     * arithmetic mean </a> of the available values
     * @param values Is a double[] containing the values
     * @return the mean of the values or Double.NaN if the array is empty
     */
    public static double mean(final double[] values) {
        return mean.evaluate(values);
    }

    /**
      * Returns the <a href=http://www.xycoon.com/arithmetic_mean.htm>
      * arithmetic mean </a> of the available values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
      * @return the mean of the values or Double.NaN if the array is empty
      */
    public static double mean(
        final double[] values,
        final int begin,
        final int length) {
        return mean.evaluate(values, begin, length);
    }

    /**
     * Returns the variance of the available values. This uses a corrected
     * two pass algorithm of the following
     * <a href="http://lib-www.lanl.gov/numerical/bookcpdf/c14-1.pdf">
     * corrected two pass formula (14.1.8)</a>, and also referenced in:<p/>
     * "Algorithms for Computing the Sample Variance: Analysis and
     * Recommendations", Chan, T.F., Golub, G.H., and LeVeque, R.J.
     * 1983, American Statistician, vol. 37, pp. 242?247.
     *
     * @param values Is a double[] containing the values
     * @return the result, Double.NaN if no values for an empty array
     * or 0.0 for a single value set.
     */
    public static double variance(final double[] values) {
        return variance.evaluate(values);
    }

    /**
     * Returns the variance of the available values. This uses a corrected
     * two pass algorithm of the following
     * <a href="http://lib-www.lanl.gov/numerical/bookcpdf/c14-1.pdf">
     * corrected two pass formula (14.1.8)</a>, and also referenced in:<p/>
     * "Algorithms for Computing the Sample Variance: Analysis and
     * Recommendations", Chan, T.F., Golub, G.H., and LeVeque, R.J.
     * 1983, American Statistician, vol. 37, pp. 242?247.
     *
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the result, Double.NaN if no values for an empty array
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
     * @return the maximum of the values or Double.NaN if the array is empty
     */
    public static double max(final double[] values) {
        return max.evaluate(values);
    }

    /**
     * Returns the maximum of the available values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the maximum of the values or Double.NaN if the array is empty
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
     * @return the minimum of the values or Double.NaN if the array is empty
     */
    public static double min(final double[] values) {
        return min.evaluate(values);
    }

    /**
     * Returns the minimum of the available values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length the number of elements to include
     * @return the minimum of the values or Double.NaN if the array is empty
     */
    public static double min(
        final double[] values,
        final int begin,
        final int length) {
        return min.evaluate(values, begin, length);
    }
    
    /**
     * Returns an estimate for the pth percentile of the stored values. 
     * This estimate follows the interpolation-adjusted defintion presented 
     * <a href="http://www.utdallas.edu/~ammann/stat5311/node8.html">here</a>
     * <p/>
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
     * This estimate follows the interpolation-adjusted defintion presented 
     * <a href="http://www.utdallas.edu/~ammann/stat5311/node8.html">here</a>
     * <p/>
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
    

}