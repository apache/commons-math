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

import org.apache.commons.statistics.distribution.BetaDistribution;

/**
 * Implements the Clopper-Pearson method for creating a binomial proportion confidence interval.
 *
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval#Clopper-Pearson_interval">
 *      Clopper-Pearson interval (Wikipedia)</a>
 * @since 3.3
 */
public class ClopperPearsonInterval implements BinomialConfidenceInterval {

    /** {@inheritDoc} */
    @Override
    public ConfidenceInterval createInterval(int numberOfTrials,
                                             int numberOfSuccesses,
                                             double confidenceLevel) {
        IntervalUtils.checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        double lowerBound = 0;
        double upperBound = 1;

        // alpha = 1 - confidence level
        final double halfAlpha = 0.5 * (1 - confidenceLevel);
        final int n = numberOfTrials;
        final int x = numberOfSuccesses;

        if (numberOfSuccesses > 0) {
            lowerBound = BetaDistribution.of(x, n - x + 1).inverseCumulativeProbability(halfAlpha);
        }

        if (numberOfSuccesses < numberOfTrials) {
            upperBound = BetaDistribution.of(x + 1, n - x).inverseSurvivalProbability(halfAlpha);
        }

        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }
}
