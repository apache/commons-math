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
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
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

import org.apache.commons.math.stat.univariate.UnivariateStatistic;
import org.apache.commons.math.stat.univariate.moment.GeometricMean;
import org.apache.commons.math.stat.univariate.moment.Kurtosis;
import org.apache.commons.math.stat.univariate.moment.Mean;
import org.apache.commons.math.stat.univariate.moment.Skewness;
import org.apache.commons.math.stat.univariate.moment.Variance;
import org.apache.commons.math.stat.univariate.rank.Max;
import org.apache.commons.math.stat.univariate.rank.Median;
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
 *
 * @author <a href="mailto:mdiggory@apache.org">Mark Diggory</a>
 */
public class StatUtils {

    /** Sum Of Logs */
    private static UnivariateStatistic sumLog = new SumOfLogs();
    
    /** Product */
    private static UnivariateStatistic product = new Product();
    
    /** Geometric Mean */
    private static UnivariateStatistic geoMean = new GeometricMean();
    
    /** Mean */
    private static UnivariateStatistic mean = new Mean();
    
    /** Variance */
    private static UnivariateStatistic var = new Variance();
    
    /** Skewness */
    private static UnivariateStatistic skew = new Skewness();
    
    /** Kurtosis */
    private static UnivariateStatistic kurt = new Kurtosis();
    
    /** Min Of Logs */
    private static UnivariateStatistic min = new Min();
    
    /** Max */
    private static UnivariateStatistic max = new Max();
    
    /** Median */
    private static UnivariateStatistic median = new Median();
    
    /** Sum */
    private static UnivariateStatistic sum = new Sum();
    
    /** Sum Of Squares */
    private static UnivariateStatistic sumSq = new SumOfSquares();
    
    /** Percentile */
    private static Percentile percentile = new Percentile();

    /**
     * The sum of the values that have been added to Univariate.
     * @param values Is a double[] containing the values
     * @return the sum of the values or Double.NaN if the array is empty
     */
    public static double sum(double[] values) {
        return sum.evaluate(values, 0, values.length);
    }

    /**
     * The sum of the values that have been added to Univariate.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the sum of the values or Double.NaN if the array is empty
     */
    public static double sum(double[] values, int begin, int length) {
        return sum.evaluate(values, begin, length);
    }

    /**
     * Returns the sum of the squares of the available values.
     * @param values Is a double[] containing the values
     * @return the sum of the squared values or Double.NaN if the array is empty
     */
    public static double sumSq(double[] values) {
        return sumSq.evaluate(values);
    }

    /**
     * Returns the sum of the squares of the available values.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the sum of the squared values or Double.NaN if the array is empty
     */
    public static double sumSq(double[] values, int begin, int length) {
        return sumSq.evaluate(values, begin, length);
    }

    /**
     * Returns the product for this collection of values
     * @param values Is a double[] containing the values
     * @return the product values or Double.NaN if the array is empty
     */
    public static double product(double[] values) {
        return product.evaluate(values);
    }

    /**
     * Returns the product for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the product values or Double.NaN if the array is empty
     */
    public static double product(double[] values, int begin, int length) {
        return product.evaluate(values, begin, length);
    }

    /**
     * Returns the sum of the natural logs for this collection of values
     * @param values Is a double[] containing the values
     * @return the sumLog value or Double.NaN if the array is empty
     */
    public static double sumLog(double[] values) {
        return sumLog.evaluate(values);
    }

    /**
     * Returns the sum of the natural logs for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the sumLog value or Double.NaN if the array is empty
     */
    public static double sumLog(double[] values, int begin, int length) {
        return sumLog.evaluate(values, begin, length);
    }

    /**
     * Returns the geometric mean for this collection of values
     * @param values Is a double[] containing the values
     * @return the geometric mean or Double.NaN if the array is empty or
     * any of the values are &lt;= 0.
     */
    public static double geometricMean(double[] values) {
        return geoMean.evaluate(values);
    }

    /**
     * Returns the geometric mean for this collection of values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the geometric mean or Double.NaN if the array is empty or
     * any of the values are &lt;= 0.
     */
    public static double geometricMean(
        double[] values,
        int begin,
        int length) {
        return geoMean.evaluate(values, begin, length);
    }

    /**
     * Returns the <a href=http://www.xycoon.com/arithmetic_mean.htm>
     * arithmetic mean </a> of the available values 
     * @param values Is a double[] containing the values
     * @return the mean of the values or Double.NaN if the array is empty
     */
    public static double mean(double[] values) {
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
    public static double mean(double[] values, int begin, int length) {
        return mean.evaluate(values, begin, length);
    }

    /**
     *      
     * @param values Is a double[] containing the values
     * @return the result, Double.NaN if no values for an empty array 
     * or 0.0 for a single value set.  
     */
    public static double standardDeviation(double[] values) {
        return standardDeviation(values, 0, values.length);
    }

    /**
     *      
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the result, Double.NaN if no values for an empty array 
     * or 0.0 for a single value set.  
     */
    public static double standardDeviation(
        double[] values,
        int begin,
        int length) {

        double stdDev = Double.NaN;
        if (values.length != 0) {
            stdDev = Math.sqrt(variance(values, begin, length));
        }
        return (stdDev);
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
    public static double variance(double[] values) {
        return variance(values, 0, values.length);
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
    public static double variance(double[] values, int begin, int length) {
        return var.evaluate(values, begin, length);
    }

    /**
     * Returns the skewness of a collection of values.  Skewness is a 
     * measure of the assymetry of a given distribution. 
     * @param values Is a double[] containing the values
     * @return the skewness of the values or Double.NaN if the array is empty
     */
    public static double skewness(double[] values) {
        return skewness(values, 0, values.length);
    }
    /**
    * Returns the skewness of a collection of values.  Skewness is a 
    * measure of the assymetry of a given distribution. 
    * @param values Is a double[] containing the values
    * @param begin processing at this point in the array
    * @param length processing at this point in the array
    * @return the skewness of the values or Double.NaN if the array is empty
    */
    public static double skewness(double[] values, int begin, int length) {
        return skew.evaluate(values, begin, length);
    }

    /**
     * Returns the kurtosis for this collection of values. Kurtosis is a 
     * measure of the "peakedness" of a distribution.
     * @param values Is a double[] containing the values
     * @return the kurtosis of the values or Double.NaN if the array is empty
     */
    public static double kurtosis(double[] values) {
        return kurtosis(values, 0, values.length);
    }

    /**
     * Returns the kurtosis for this collection of values. Kurtosis is a 
     * measure of the "peakedness" of a distribution.
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the kurtosis of the values or Double.NaN if the array is empty
     */
    public static double kurtosis(double[] values, int begin, int length) {
        return kurt.evaluate(values, begin, length);
    }

    /**
     * Returns the maximum of the available values
     * @param values Is a double[] containing the values
     * @return the maximum of the values or Double.NaN if the array is empty
     */
    public static double max(double[] values) {
        return max(values, 0, values.length);
    }

    /**
     * Returns the maximum of the available values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the maximum of the values or Double.NaN if the array is empty
     */
    public static double max(double[] values, int begin, int length) {
        return max.evaluate(values, begin, length);
    }

    /**
     * Returns the minimum of the available values
     * @param values Is a double[] containing the values
     * @return the minimum of the values or Double.NaN if the array is empty
     */
    public static double min(double[] values) {
        return min(values, 0, values.length);
    }

    /**
     * Returns the minimum of the available values
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @return the minimum of the values or Double.NaN if the array is empty
     */
    public static double min(double[] values, int begin, int length) {
        return min.evaluate(values, begin, length);
    }

    /**
     * Returns the p'th percentile for a double[]
     * @param values Is a double[] containing the values
     * @param p is 0 <= p <= 100
     * @return the value at the p'th percentile
     */
    public static double percentile(double[] values, double p) {
        return percentile.evaluate(values, p);
    }

    /**
     * Returns the p'th percentile for a double[]
     * @param values Is a double[] containing the values
     * @param begin processing at this point in the array
     * @param length processing at this point in the array
     * @param p is 0 <= p <= 100
     * @return the value at the p'th percentile
     */
    public static double percentile(
        double[] values,
        int begin,
        int length,
        double p) {
        return percentile.evaluate(values, begin, length, p);
    }
}
