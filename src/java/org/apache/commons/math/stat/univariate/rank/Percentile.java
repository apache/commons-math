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
package org.apache.commons.math.stat.univariate.rank;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math.stat.univariate.AbstractUnivariateStatistic;

/**
 * Provides percentile computation.
 * <p>
 * There are several commonly used methods for estimating percentiles (a.k.a. quantiles) based
 * on sample data.  For large samples, the different methods agree closely, but when sample sizes
 * are small, different methods will give significantly different results.  The implementation provided here
 * follows the first estimation procedure presented
 * <a href="http://www.itl.nist.gov/div898/handbook/prc/section2/prc252.htm">here.</a>
 * 
 * @version $Revision: 1.15 $ $Date: 2004/03/13 20:02:28 $
 */
public class Percentile extends AbstractUnivariateStatistic implements Serializable {

    static final long serialVersionUID = -8091216485095130416L; 
       
    /** Determines what percentile is computed when evaluate() is activated with no quantile argument */
    private double quantile = 0.0;

    /**
     * Constructs a Percentile with a default quantile
     * value of 50.0.
     */
    public Percentile() {
        super();
        quantile = 50.0;
    }

    /**
     * Constructs a Percentile with the specific quantile value.
     * @param p the quantile
     */
    public Percentile(final double p) {
        this.quantile = p;
    }

    /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array.
     * <p>
     * Calls to this method do not modify the internal <code>quantile</code>
     * state of this statistic.
     * <p>
     * See {@link Percentile} for a description of the percentile estimation algorithm used.
     * 
     * @param values Is a double[] containing the values
     * @param p Is the quantile to evaluate to.
     * @return the result of the evaluation or Double.NaN
     * if the array is empty
     */
    public double evaluate(final double[] values, final double p) {
        return evaluate(values, 0, values.length, p);
    }

    /**
     * Returns an estimate of the <code>quantile</code>th percentile of the values
     * in the <code>values</code> array.  The quantile estimated is determined by
     * the <code>quantile</code> property.
     * <p>
     * See {@link Percentile} for a description of the percentile estimation algorithm used.
     * 
     * @param values  array of input values
     * @param start  the first (0-based) element to include in the computation
     * @param length  the number of array elements to include
     * @return the result of the evaluation or Double.NaN
     * if the array is empty
     * 
     */
    public double evaluate(
        final double[] values,
        final int start,
        final int length) {

        return evaluate(values, start, length, quantile);
    }

     /**
     * Returns an estimate of the <code>p</code>th percentile of the values
     * in the <code>values</code> array, starting with the element in (0-based)
     * position <code>begin</code> in the array and including <code>length</code>
     * values.
     * <p>
     * Calls to this method do not modify the internal <code>quantile</code>
     * state of this statistic.
     * <p>
      * See {@link Percentile} for a description of the percentile estimation algorithm used.
     * 
     * @param values Is a double[] containing the values
     * @param p Is the quantile to evaluate to.
     * @param start  the first (0-based) element to include in the computation
     * @param length  the number of array elements to include
     * @return the result of the evaluation or Double.NaN
     * if the array is empty
     */
    public double evaluate(
        final double[] values,
        final int begin,
        final int length,
        final double p) {

        test(values, begin, length);

        if ((p > 100) || (p <= 0)) {
            throw new IllegalArgumentException("invalid quantile value");
        }
        double n = (double) length;
        if (n == 0) {
            return Double.NaN;
        }
        if (n == 1) {
            return values[begin]; // always return single value for n = 1
        }
        double pos = p * (n + 1) / 100;
        double fpos = Math.floor(pos);
        int intPos = (int) fpos;
        double dif = pos - fpos;
        double[] sorted = new double[length];
        System.arraycopy(values, begin, sorted, 0, length);
        Arrays.sort(sorted);

        if (pos < 1) {
            return sorted[0];
        }
        if (pos >= n) {
            return sorted[length - 1];
        }
        double lower = sorted[intPos - 1];
        double upper = sorted[intPos];
        return lower + dif * (upper - lower);
    }

    /**
     * Returns the value of the quantile field (determines what percentile is computed when evaluate()
     * is called with no quantile argument)
     * 
     * @return quantile
     */
    public double getQuantile() {
        return quantile;
    }

    /**
     * Sets the value of the quantile field (determines what percentile is computed when evaluate()
     * is called with no quantile argument)
     * 
     * @param p a value between 0 <= p <= 100 
     */
    public void setQuantile(final double p) {
        quantile = p;
    }

}