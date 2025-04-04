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

import java.util.Arrays;
import org.apache.commons.math4.legacy.stat.descriptive.WeightedEvaluation;
import org.apache.commons.math4.legacy.stat.descriptive.WeightedEvaluationAbstractTest;
import org.apache.commons.statistics.descriptive.Mean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases for the {@link WeightedVariance} class.
 */
public class WeightedVarianceTest extends WeightedEvaluationAbstractTest {

    @Override
    public WeightedEvaluation getWeightedEvaluation() {
        return WeightedVariance.getInstance();
    }

    @Override
    public double expectedValue() {
        return weightedVariance;
    }

    @Override
    public double expectedUnweightedValue() {
        return variance;
    }

    /**
     * Test population version of variance
     */
    @Test
    public void testPopulation() {
        final double[] values = {-1.0d, 3.1d, 4.0d, -2.1d, 22d, 11.7d, 3d, 14d};
        final double[] weights = new double[values.length];
        Arrays.fill(weights, 1);
        final WeightedVariance v1 = WeightedVariance.getInstance();
        v1.setBiasCorrected(false);
        Assertions.assertEquals(populationVariance(values), v1.evaluate(values, weights), 1E-14);
    }

    /**
     * Definitional formula for population variance
     */
    protected double populationVariance(double[] v) {
        final double mean = Mean.of(v).getAsDouble();
        double sum = 0;
        for (int i = 0; i < v.length; i++) {
           sum += (v[i] - mean) * (v[i] - mean);
        }
        return sum / v.length;
    }
}

