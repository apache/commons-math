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

import org.apache.commons.math4.legacy.stat.descriptive.WeightedEvaluation;
import org.apache.commons.math4.legacy.stat.descriptive.WeightedEvaluationAbstractTest;

/**
 * Test cases for the {@link WeightedMean} class.
 */
public class WeightedMeanTest extends WeightedEvaluationAbstractTest {

    @Override
    public WeightedEvaluation getWeightedEvaluation() {
        return WeightedMean.getInstance();
    }

    @Override
    public double expectedValue() {
        return weightedMean;
    }

    @Override
    public double expectedUnweightedValue() {
        return mean;
    }
}
