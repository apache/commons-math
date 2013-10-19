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
package org.apache.commons.math3.stat.inference;

import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;

/**
 * Factory methods to generate confidence intervals for a binomial proportion.
 * The supported methods are:
 * <ul>
 * <li>Clopper-Pearson method (exact method)</li>
 * <li>Normal approximation (based on central limit theorem)</li>
 * <li>Agresti-Coull interval</li>
 * <li>Wilson score interval</li>
 * </ul>
 *
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Binomial_proportion_confidence_interval">Binomial
 *      proportion confidence interval (Wikipedia)</a>
 * @version $Id$
 * @since 3.3
 */
public class BinomialConfidenceInterval {

    /**
     * Create a Clopper-Pearson binomial confidence interval for the true
     * probability of success of an unknown binomial distribution with the given
     * observed number of trials, successes and confidence level.
     * <p>
     * Preconditions:
     * <ul>
     * <li>{@code numberOfTrials} must be positive</li>
     * <li>{@code numberOfSuccesses} may not exceed {@code numberOfTrials}</li>
     * <li>{@code confidenceLevel} must be strictly between 0 and 1 (exclusive)</li>
     * </ul>
     * </p>
     *
     * @param numberOfTrials number of trials
     * @param numberOfSuccesses number of successes
     * @param confidenceLevel desired probability that the true probability of
     *        success falls within the returned interval
     * @return Confidence interval containing the probability of success with
     *         probability {@code confidenceLevel}
     * @throws NotStrictlyPositiveException if {@code numberOfTrials <= 0}.
     * @throws NotPositiveException if {@code numberOfSuccesses < 0}.
     * @throws NumberIsTooLargeException if {@code numberOfSuccesses > numberOfTrials}.
     * @throws OutOfRangeException if {@code confidenceLevel} is not in the interval {@code [0, 1]}.
     */
    public ConfidenceInterval getClopperPearsonInterval(int numberOfTrials, int numberOfSuccesses,
                                                        double confidenceLevel) {
        checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        double lowerBound = 0;
        double upperBound = 0;
        final double alpha = (1.0 - confidenceLevel) / 2.0;

        final FDistribution distributionLowerBound = new FDistribution(2 * (numberOfTrials - numberOfSuccesses + 1),
                                                                       2 * numberOfSuccesses);
        final double fValueLowerBound = distributionLowerBound.inverseCumulativeProbability(1 - alpha);
        if (numberOfSuccesses > 0) {
            lowerBound = numberOfSuccesses /
                         (numberOfSuccesses + (numberOfTrials - numberOfSuccesses + 1) * fValueLowerBound);
        }

        final FDistribution distributionUpperBound = new FDistribution(2 * (numberOfSuccesses + 1),
                                                                       2 * (numberOfTrials - numberOfSuccesses));
        final double fValueUpperBound = distributionUpperBound.inverseCumulativeProbability(1 - alpha);
        if (numberOfSuccesses > 0) {
            upperBound = (numberOfSuccesses + 1) * fValueUpperBound /
                         (numberOfTrials - numberOfSuccesses + (numberOfSuccesses + 1) * fValueUpperBound);
        }

        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }

    /**
     * Create a binomial confidence interval for the true probability of success
     * of an unknown binomial distribution with the given observed number of
     * trials, successes and confidence level using the Normal approximation to
     * the binomial distribution.
     *
     * @param numberOfTrials number of trials
     * @param numberOfSuccesses number of successes
     * @param confidenceLevel desired probability that the true probability of
     *        success falls within the interval
     * @return Confidence interval containing the probability of success with
     *         probability {@code confidenceLevel}
     */
    public ConfidenceInterval getNormalApproximationInterval(int numberOfTrials, int numberOfSuccesses,
                                                             double confidenceLevel) {
        checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double mean = (double) numberOfSuccesses / (double) numberOfTrials;
        final double alpha = (1.0 - confidenceLevel) / 2;
        final NormalDistribution normalDistribution = new NormalDistribution();
        final double difference = normalDistribution.inverseCumulativeProbability(1 - alpha) *
                                  FastMath.sqrt(1.0 / numberOfTrials * mean * (1 - mean));
        return new ConfidenceInterval(mean - difference, mean + difference, confidenceLevel);
    }

    /**
     * Create an Agresti-Coull binomial confidence interval for the true
     * probability of success of an unknown binomial distribution with the given
     * observed number of trials, successes and confidence level.
     *
     * @param numberOfTrials number of trials
     * @param numberOfSuccesses number of successes
     * @param confidenceLevel desired probability that the true probability of
     *        success falls within the returned interval
     * @return Confidence interval containing the probability of success with
     *         probability {@code confidenceLevel}
     * @throws NotStrictlyPositiveException if {@code numberOfTrials <= 0}.
     * @throws NotPositiveException if {@code numberOfSuccesses < 0}.
     * @throws NumberIsTooLargeException if {@code numberOfSuccesses > numberOfTrials}.
     * @throws OutOfRangeException if {@code confidenceLevel} is not in the interval {@code [0, 1]}.
     */
    public ConfidenceInterval getAgrestiCoullInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
        checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double alpha = (1.0 - confidenceLevel) / 2;
        final NormalDistribution normalDistribution = new NormalDistribution();
        final double z = normalDistribution.inverseCumulativeProbability(1 - alpha);
        final double zSquared = FastMath.pow(z, 2);
        final double modifiedNumberOfTrials = numberOfTrials + zSquared;
        final double modifiedSuccessesRatio = (1.0 / modifiedNumberOfTrials) * (numberOfSuccesses + 0.5 * zSquared);
        final double difference = z *
                                  FastMath.sqrt(1.0 / modifiedNumberOfTrials * modifiedSuccessesRatio *
                                                (1 - modifiedSuccessesRatio));
        return new ConfidenceInterval(modifiedSuccessesRatio - difference, modifiedSuccessesRatio + difference,
                                      confidenceLevel);
    }

    /**
     * Create a Wilson score binomial confidence interval for the true
     * probability of success of an unknown binomial distribution with the given
     * observed number of trials, successes and confidence level.
     *
     * @param numberOfTrials number of trials
     * @param numberOfSuccesses number of successes
     * @param confidenceLevel desired probability that the true probability of
     *        success falls within the returned interval
     * @return Confidence interval containing the probability of success with
     *         probability {@code confidenceLevel}
     * @throws NotStrictlyPositiveException if {@code numberOfTrials <= 0}.
     * @throws NotPositiveException if {@code numberOfSuccesses < 0}.
     * @throws NumberIsTooLargeException if {@code numberOfSuccesses > numberOfTrials}.
     * @throws OutOfRangeException if {@code confidenceLevel} is not in the interval {@code [0, 1]}.
     */
    public ConfidenceInterval getWilsonScoreInterval(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
        checkParameters(numberOfTrials, numberOfSuccesses, confidenceLevel);
        final double alpha = (1.0 - confidenceLevel) / 2;
        final NormalDistribution normalDistribution = new NormalDistribution();
        final double z = normalDistribution.inverseCumulativeProbability(1 - alpha);
        final double zSquared = FastMath.pow(z, 2);
        final double mean = (double) numberOfSuccesses / (double) numberOfTrials;

        final double factor = 1.0 / (1 + (1.0 / numberOfTrials) * zSquared);
        final double modifiedSuccessRatio = mean + (1.0 / (2 * numberOfTrials)) * zSquared;
        final double difference = z *
                                  FastMath.sqrt(1.0 / numberOfTrials * mean * (1 - mean) +
                                                (1.0 / (4 * FastMath.pow(numberOfTrials, 2)) * zSquared));

        final double lowerBound = factor * (modifiedSuccessRatio - difference);
        final double upperBound = factor * (modifiedSuccessRatio + difference);
        return new ConfidenceInterval(lowerBound, upperBound, confidenceLevel);
    }

    /**
     * Verifies that parameters satisfy preconditions.
     *
     * @param numberOfTrials Number of trials (must be positive)
     * @param numberOfSuccesses Number of successes (must not exceed numberOfTrials)
     * @param confidenceLevel Confidence level (must be strictly between 0 and 1)
     * @throws NotStrictlyPositiveException if {@code numberOfTrials <= 0}.
     * @throws NotPositiveException if {@code numberOfSuccesses < 0}.
     * @throws NumberIsTooLargeException if {@code numberOfSuccesses > numberOfTrials}.
     * @throws OutOfRangeException if {@code confidenceLevel} is not in the interval {@code [0, 1]}.
     */
    private void checkParameters(int numberOfTrials, int numberOfSuccesses, double confidenceLevel) {
        if (numberOfTrials <= 0) {
            throw new NotStrictlyPositiveException(LocalizedFormats.NUMBER_OF_TRIALS, numberOfTrials);
        }
        if (numberOfSuccesses < 0) {
            throw new NotPositiveException(LocalizedFormats.NEGATIVE_NUMBER_OF_SUCCESSES, numberOfSuccesses);
        }
        if (numberOfSuccesses > numberOfTrials) {
            throw new NumberIsTooLargeException(LocalizedFormats.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE,
                                                numberOfSuccesses, numberOfTrials, true);
        }
        if (confidenceLevel <= 0 || confidenceLevel >= 1) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_CONFIDENCE_LEVEL,
                                          confidenceLevel, 0, 1);
        }
    }
}
