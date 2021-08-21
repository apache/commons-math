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
package org.apache.commons.math4.legacy.stat.descriptive.moment;

import org.apache.commons.math4.legacy.stat.descriptive.StorelessUnivariateStatisticAbstractTest;
import org.apache.commons.math4.legacy.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math4.legacy.core.MathArrays;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

/**
 * Test cases for the {@link UnivariateStatistic} class.
 *
 */
public class VarianceTest extends StorelessUnivariateStatisticAbstractTest{

    protected Variance stat;

    /**
     * {@inheritDoc}
     */
    @Override
    public UnivariateStatistic getUnivariateStatistic() {
        return new Variance();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double expectedValue() {
        return this.var;
    }

    /**Expected value for  the testArray defined in UnivariateStatisticAbstractTest */
    public double expectedWeightedValue() {
        return this.weightedVar;
    }

    /**
     * Make sure Double.NaN is returned iff n = 0
     *
     */
    @Test
    public void testNaN() {
        StandardDeviation std = new StandardDeviation();
        Assert.assertTrue(Double.isNaN(std.getResult()));
        std.increment(1d);
        Assert.assertEquals(0d, std.getResult(), 0);
    }

    /**
     * Test population version of variance
     */
    @Test
    public void testPopulation() {
        double[] values = {-1.0d, 3.1d, 4.0d, -2.1d, 22d, 11.7d, 3d, 14d};
        SecondMoment m = new SecondMoment();
        m.incrementAll(values);  // side effect is to add values
        Variance v1 = new Variance();
        v1.setBiasCorrected(false);
        Assert.assertEquals(populationVariance(values), v1.evaluate(values), 1E-14);
        v1.incrementAll(values);
        Assert.assertEquals(populationVariance(values), v1.getResult(), 1E-14);
        v1 = new Variance(false, m);
        Assert.assertEquals(populationVariance(values), v1.getResult(), 1E-14);
        v1 = new Variance(false);
        Assert.assertEquals(populationVariance(values), v1.evaluate(values), 1E-14);
        v1.incrementAll(values);
        Assert.assertEquals(populationVariance(values), v1.getResult(), 1E-14);
    }

    /**
     * Definitional formula for population variance
     */
    protected double populationVariance(double[] v) {
        double mean = new Mean().evaluate(v);
        double sum = 0;
        for (int i = 0; i < v.length; i++) {
           sum += (v[i] - mean) * (v[i] - mean);
        }
        return sum / v.length;
    }

    @Test
    public void testWeightedVariance() {
        Variance variance = new Variance();
        Assert.assertEquals(expectedWeightedValue(),
                variance.evaluate(testArray, testWeightsArray, 0, testArray.length), getTolerance());

        // All weights = 1 -> weighted variance = unweighted variance
        Assert.assertEquals(expectedValue(),
                variance.evaluate(testArray, unitWeightsArray, 0, testArray.length), getTolerance());

        // All weights the same -> when weights are normalized to sum to the length of the values array,
        // weighted variance = unweighted value
        Assert.assertEquals(expectedValue(),
                variance.evaluate(testArray, MathArrays.normalizeArray(identicalWeightsArray, testArray.length),
                        0, testArray.length), getTolerance());

    }

    @Test
    public void testZeroWeights() {
        Variance variance = new Variance();
        final double[] values = {1, 2, 3, 4};
        final double[] weights = new double[values.length];

        // No weights
        Assertions.assertThrows(MathIllegalArgumentException.class, () -> {
            variance.evaluate(values, weights);
        });

        // No length
        final int begin = 1;
        final int zeroLength = 0;
        Assertions.assertEquals(Double.NaN, variance.evaluate(values, weights, begin, zeroLength));

        // One weight (must be non-zero)
        Assertions.assertThrows(MathIllegalArgumentException.class, () -> {
            variance.evaluate(values, weights, begin, zeroLength + 1);
        });

        weights[begin] = Double.MIN_VALUE;
        Assertions.assertEquals(0.0, variance.evaluate(values, weights, begin, zeroLength + 1));
    }

}
