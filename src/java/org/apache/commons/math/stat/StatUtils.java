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
 *    nor may "Apache" appear in their name without prior written
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
 * @version $Revision: 1.22 $ $Date: 2003/11/15 18:52:31 $
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
     * @param length processing at this point in the array
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
     * @param length processing at this point in the array
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
     * @param length processing at this point in the array
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
     * @param length processing at this point in the array
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
     * @param length processing at this point in the array
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
     * @param length processing at this point in the array
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
     * @param length processing at this point in the array
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
     * @param length processing at this point in the array
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
     * @param length processing at this point in the array
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