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
package org.apache.commons.math4.legacy.stat.descriptive;

import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Value object representing the results of a univariate statistical summary.
 */
public class StatisticalSummaryValues
    implements StatisticalSummary {
    /** The sample mean. */
    private final double mean;

    /** The sample variance. */
    private final double variance;

    /** The number of observations in the sample. */
    private final long n;

    /** The maximum value. */
    private final double max;

    /** The minimum value. */
    private final double min;

    /** The sum of the sample values. */
    private final double sum;

    /**
      * Constructor.
      *
      * @param mean  the sample mean
      * @param variance  the sample variance
      * @param n  the number of observations in the sample
      * @param max  the maximum value
      * @param min  the minimum value
      * @param sum  the sum of the values
     */
    public StatisticalSummaryValues(double mean, double variance, long n,
        double max, double min, double sum) {
        super();
        this.mean = mean;
        this.variance = variance;
        this.n = n;
        this.max = max;
        this.min = min;
        this.sum = sum;
    }

    /**
     * @return the max.
     */
    @Override
    public double getMax() {
        return max;
    }

    /**
     * @return the mean.
     */
    @Override
    public double getMean() {
        return mean;
    }

    /**
     * @return the min.
     */
    @Override
    public double getMin() {
        return min;
    }

    /**
     * @return the number of values.
     */
    @Override
    public long getN() {
        return n;
    }

    /**
     * @return the sum.
     */
    @Override
    public double getSum() {
        return sum;
    }

    /**
     * @return the standard deviation
     */
    @Override
    public double getStandardDeviation() {
        return JdkMath.sqrt(variance);
    }

    /**
     * @return the variance.
     */
    @Override
    public double getVariance() {
        return variance;
    }

    /**
     * Generates a text report displaying values of statistics.
     * Each statistic is displayed on a separate line.
     *
     * @return String with line feeds displaying statistics
     */
    @Override
    public String toString() {
        StringBuffer outBuffer = new StringBuffer();
        String endl = "\n";
        outBuffer.append("StatisticalSummaryValues:").append(endl);
        outBuffer.append("n: ").append(getN()).append(endl);
        outBuffer.append("min: ").append(getMin()).append(endl);
        outBuffer.append("max: ").append(getMax()).append(endl);
        outBuffer.append("mean: ").append(getMean()).append(endl);
        outBuffer.append("std dev: ").append(getStandardDeviation())
            .append(endl);
        outBuffer.append("variance: ").append(getVariance()).append(endl);
        outBuffer.append("sum: ").append(getSum()).append(endl);
        return outBuffer.toString();
    }
}
