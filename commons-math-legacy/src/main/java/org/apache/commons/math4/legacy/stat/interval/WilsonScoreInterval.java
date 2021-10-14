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
package org.apache.commons.math4.legacy.stat.interval;

import org.apache.commons.statistics.distribution.NormalDistribution;
import org.apache.commons.math4.core.jdkmath.JdkMath;

/**
 * Implements the <a href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Wilson_score_interval">
 * Wilson score method</a> for creating a binomial proportion confidence interval.
 *
 * @since 3.3
 */
public class WilsonScoreInterval implements BinomialConfidenceInterval {

    /** {@inheritDoc} */
    @Override
    public ConfidenceInterval createInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double alpha = (1 - confidenceLevel) / 2;
        final NormalDistribution normalDistribution = NormalDistribution.of(0, 1);
        final double z = normalDistribution.inverseCumulativeProbability(1 - alpha);
        final double zSquared = z * z;
        final double oneOverNumTrials = 1d / numberOfTrials;
        final double zSquaredOverNumTrials = zSquared * oneOverNumTrials;
        final double mean = oneOverNumTrials * numberOfSuccesses;

        final double factor = 1 / (1 + zSquaredOverNumTrials);
        final double modifiedSuccessRatio = mean + zSquaredOverNumTrials / 2;
        final double difference = z * JdkMath.sqrt(oneOverNumTrials * mean * (1 - mean) +
                                                    (oneOverNumTrials * zSquaredOverNumTrials / 4));

        final double lowerBound = factor * (modifiedSuccessRatio - difference);
        final double upperBound = factor * (modifiedSuccessRatio + difference);
        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }
}
