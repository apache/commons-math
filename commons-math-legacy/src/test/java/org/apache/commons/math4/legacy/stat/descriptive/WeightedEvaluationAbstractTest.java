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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math4.legacy.TestUtils;
import org.apache.commons.math4.legacy.exception.MathIllegalArgumentException;
import org.apache.commons.statistics.distribution.DiscreteDistribution;
import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.statistics.distribution.UniformDiscreteDistribution;
import org.apache.commons.math4.core.jdkmath.JdkMath;
import org.apache.commons.rng.simple.RandomSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link WeightedEvaluation} implementations.
 * Based on the {@link UnivariateStatisticAbstractTest}. 
 */
public abstract class WeightedEvaluationAbstractTest {

    protected double mean = 12.404545454545455d;

    protected double variance = 10.00235930735931d;
    protected double std = JdkMath.sqrt(variance);

    protected double product = 628096400563833396009676.9200400128d;
    protected double sum = 272.90d;

    protected double weightedMean = 12.366995073891626d;
    protected double weightedVariance = 9.974760968886391d;
    protected double weightedStd = JdkMath.sqrt(weightedVariance);
    protected double weightedProduct = 8517647448765288000000d;
    protected double weightedSum = 251.05d;

    protected double tolerance = 10E-12;

    protected double[] testArray =
        { 12.5, 12.0, 11.8, 14.2, 14.9, 14.5, 21.0,  8.2, 10.3, 11.3,
          14.1,  9.9, 12.2, 12.0, 12.1, 11.0, 19.8, 11.0, 10.0,  8.8,
           9.0, 12.3 };

    protected double[] testWeightsArray =
        {  1.5,  0.8,  1.2,  0.4,  0.8,  1.8,  1.2,  1.1,  1.0,  0.7,
           1.3,  0.6,  0.7,  1.3,  0.7,  1.0,  0.4,  0.1,  1.4,  0.9,
           1.1,  0.3 };

    protected double[] unitWeightsArray =
        {  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,
           1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,
           1.0,  1.0 };

    public abstract WeightedEvaluation getWeightedEvaluation();

    public abstract double expectedValue();

    public abstract double expectedUnweightedValue();

    public double emptyValue() {
        return Double.NaN;
    }

    public double getTolerance() {
        return tolerance;
    }

    @Test
    void testInvalidDataThrows() {
        final WeightedEvaluation stat = getWeightedEvaluation();
        final double[] values = {1, 2, 3};
        final double[] weights = {4, 5, 6};
        Assertions.assertThrows(NullPointerException.class, () -> stat.evaluate(null, weights));
        Assertions.assertThrows(NullPointerException.class, () -> stat.evaluate(values, null));

        final double[] w1 = Arrays.copyOf(weights, weights.length - 1);
        Assertions.assertThrows(MathIllegalArgumentException.class, () -> stat.evaluate(values, w1));
        final double[] w2 = {4, Double.POSITIVE_INFINITY, 6};
        Assertions.assertThrows(MathIllegalArgumentException.class, () -> stat.evaluate(values, w2));
        final double[] w3 = {4, Double.NaN, 6};
        Assertions.assertThrows(MathIllegalArgumentException.class, () -> stat.evaluate(values, w3));
        final double[] w4 = {4, -2, 6};
        Assertions.assertThrows(MathIllegalArgumentException.class, () -> stat.evaluate(values, w4));

        Assertions.assertThrows(MathIllegalArgumentException.class, () -> stat.evaluate(values, weights, -1, 3));
        Assertions.assertThrows(MathIllegalArgumentException.class, () -> stat.evaluate(values, weights, 1, 10));
    }

    @Test
    public void testEvaluation() {
        Assertions.assertEquals(expectedValue(),
            getWeightedEvaluation().evaluate(testArray, testWeightsArray), getTolerance());
    }

    @Test
    public void testUnweightedEvaluation() {
        Assertions.assertEquals(expectedUnweightedValue(),
            getWeightedEvaluation().evaluate(testArray, unitWeightsArray), getTolerance());
    }

    @Test
    public void testEmptyEvaluation() {
        final double[] x = {};
        Assertions.assertEquals(emptyValue(), getWeightedEvaluation().evaluate(x, x));
        Assertions.assertEquals(emptyValue(), getWeightedEvaluation().evaluate(testArray, testWeightsArray, 1, 0));
    }

    @Test
    public void testEvaluateArraySegment() {
        final WeightedEvaluation stat = getWeightedEvaluation();
        final double[] arrayZero = new double[5];
        System.arraycopy(testArray, 0, arrayZero, 0, 5);
        double[] w = unitWeights(arrayZero.length);
        Assertions.assertEquals(stat.evaluate(arrayZero, w), stat.evaluate(testArray, unitWeightsArray, 0, 5));
        final double[] arrayOne = new double[5];
        System.arraycopy(testArray, 5, arrayOne, 0, 5);
        Assertions.assertEquals(stat.evaluate(arrayOne, w), stat.evaluate(testArray, unitWeightsArray, 5, 5));
        final double[] arrayEnd = new double[5];
        System.arraycopy(testArray, testArray.length - 5, arrayEnd, 0, 5);
        Assertions.assertEquals(stat.evaluate(arrayEnd, w), stat.evaluate(testArray, unitWeightsArray, testArray.length - 5, 5));
    }

    @Test
    public void testEvaluateArraySegmentWeighted() {
        final WeightedEvaluation stat = getWeightedEvaluation();
        final double[] arrayZero = new double[5];
        final double[] weightZero = new double[5];
        System.arraycopy(testArray, 0, arrayZero, 0, 5);
        System.arraycopy(testWeightsArray, 0, weightZero, 0, 5);
        Assertions.assertEquals(stat.evaluate(arrayZero, weightZero),
                stat.evaluate(testArray, testWeightsArray, 0, 5));
        final double[] arrayOne = new double[5];
        final double[] weightOne = new double[5];
        System.arraycopy(testArray, 5, arrayOne, 0, 5);
        System.arraycopy(testWeightsArray, 5, weightOne, 0, 5);
        Assertions.assertEquals(stat.evaluate(arrayOne, weightOne),
                stat.evaluate(testArray, testWeightsArray, 5, 5));
        final double[] arrayEnd = new double[5];
        final double[] weightEnd = new double[5];
        System.arraycopy(testArray, testArray.length - 5, arrayEnd, 0, 5);
        System.arraycopy(testWeightsArray, testArray.length - 5, weightEnd, 0, 5);
        Assertions.assertEquals(stat.evaluate(arrayEnd, weightEnd),
                stat.evaluate(testArray, testWeightsArray, testArray.length - 5, 5));
    }

    /**
     * Tests consistency of weighted statistic computation.
     * For statistics that support weighted evaluation, this test case compares
     * the result of direct computation on an array with repeated values with
     * a weighted computation on the corresponding (shorter) array with each
     * value appearing only once but with a weight value equal to its multiplicity
     * in the repeating array.
     */
    @Test
    public void testWeightedConsistency() {

        // See if this statistic computes weighted statistics
        // If not, skip this test
        final WeightedEvaluation stat = getWeightedEvaluation();

        // Create arrays of values and corresponding integral weights
        // and longer array with values repeated according to the weights
        final int len = 10;        // length of values array
        final double mu = 0;       // mean of test data
        final double sigma = 5;    // std dev of test data
        double[] values = new double[len];
        double[] weights = new double[len];

        // Fill weights array with random int values between 1 and 5
        int[] intWeights = new int[len];
        final DiscreteDistribution.Sampler weightDist =
            UniformDiscreteDistribution.of(1, 5).createSampler(RandomSource.WELL_512_A.create(234878544L));
        for (int i = 0; i < len; i++) {
            intWeights[i] = weightDist.sample();
            weights[i] = intWeights[i];
        }

        // Fill values array with random data from N(mu, sigma)
        // and fill valuesList with values from values array with
        // values[i] repeated weights[i] times, each i
        final ContinuousDistribution.Sampler valueDist =
            NormalDistribution.of(mu, sigma).createSampler(RandomSource.WELL_512_A.create(64925784252L));
        List<Double> valuesList = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            double value = valueDist.sample();
            values[i] = value;
            for (int j = 0; j < intWeights[i]; j++) {
                valuesList.add(Double.valueOf(value));
            }
        }

        // Dump valuesList into repeatedValues array
        int sumWeights = valuesList.size();
        double[] repeatedValues = new double[sumWeights];
        for (int i = 0; i < sumWeights; i++) {
            repeatedValues[i] = valuesList.get(i);
        }

        // Compare result of weighted statistic computation with direct computation
        // on array of repeated values
        TestUtils.assertRelativelyEquals(stat.evaluate(repeatedValues, unitWeights(repeatedValues.length)),
                stat.evaluate(values, weights, 0, values.length),
                10E-12);

        // Check consistency of weighted evaluation methods
        Assertions.assertEquals(stat.evaluate(values, weights, 0, values.length),
            stat.evaluate(values, weights), Double.MIN_VALUE);
    }

    static double[] unitWeights(int n) {
        final double[] w = new double[n];
        Arrays.fill(w, 1);
        return w;
    }
}
