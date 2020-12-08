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
package org.apache.commons.math4.distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.statistics.distribution.DiscreteDistribution;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.NotANumberException;
import org.apache.commons.math4.exception.NotFiniteNumberException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.util.Pair;

/**
 * <p>Implementation of an integer-valued {@link EnumeratedDistribution}.</p>
 *
 * <p>Values with zero-probability are allowed but they do not extend the
 * support.<br>
 * Duplicate values are allowed. Probabilities of duplicate values are combined
 * when computing cumulative probabilities and statistics.</p>
 *
 * @since 3.2
 */
public class EnumeratedIntegerDistribution extends AbstractIntegerDistribution {
    /** Serializable UID. */
    private static final long serialVersionUID = 20130308L;
    /**
     * {@link EnumeratedDistribution} instance (using the {@link Integer} wrapper)
     * used to generate the pmf.
     */
    protected final EnumeratedDistribution<Integer> innerDistribution;

    /**
     * Create a discrete distribution.
     *
     * @param singletons array of random variable values.
     * @param probabilities array of probabilities.
     * @throws DimensionMismatchException if
     * {@code singletons.length != probabilities.length}
     * @throws NotPositiveException if any of the probabilities are negative.
     * @throws NotFiniteNumberException if any of the probabilities are infinite.
     * @throws NotANumberException if any of the probabilities are NaN.
     * @throws MathArithmeticException all of the probabilities are 0.
     */
    public EnumeratedIntegerDistribution(final int[] singletons,
                                         final double[] probabilities)
        throws DimensionMismatchException,
               NotPositiveException,
               MathArithmeticException,
               NotFiniteNumberException,
               NotANumberException {
        innerDistribution = new EnumeratedDistribution<>(createDistribution(singletons,
                                                                                   probabilities));
    }

    /**
     * Create a discrete integer-valued distribution from the input data.
     * Values are assigned mass based on their frequency.
     *
     * @param data input dataset
     */
    public EnumeratedIntegerDistribution(final int[] data) {
        final Map<Integer, Integer> dataMap = new HashMap<>();
        for (int value : data) {
            Integer count = dataMap.get(value);
            if (count == null) {
                count = 0;
            }
            dataMap.put(value, ++count);
        }
        final int massPoints = dataMap.size();
        final double denom = data.length;
        final int[] values = new int[massPoints];
        final double[] probabilities = new double[massPoints];
        int index = 0;
        for (Entry<Integer, Integer> entry : dataMap.entrySet()) {
            values[index] = entry.getKey();
            probabilities[index] = entry.getValue().intValue() / denom;
            index++;
        }
        innerDistribution = new EnumeratedDistribution<>(createDistribution(values, probabilities));
    }

    /**
     * Create the list of Pairs representing the distribution from singletons and probabilities.
     *
     * @param singletons values
     * @param probabilities probabilities
     * @return list of value/probability pairs
     */
    private static List<Pair<Integer, Double>>  createDistribution(int[] singletons, double[] probabilities) {
        final int singletonsLength = singletons.length;
        final int probabilitiesLength = probabilities.length;

        if (singletonsLength != probabilitiesLength) {
            throw new DimensionMismatchException(probabilitiesLength, singletonsLength);
        }

        final List<Pair<Integer, Double>> samples = new ArrayList<>(singletonsLength);

        for (int i = 0; i < singletonsLength; i++) {
            samples.add(new Pair<>(singletons[i], probabilities[i]));
        }
        return samples;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double probability(final int x) {
        return innerDistribution.probability(x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(final int x) {
        double probability = 0;

        for (final Pair<Integer, Double> sample : innerDistribution.getPmf()) {
            if (sample.getKey() <= x) {
                probability += sample.getValue();
            }
        }

        return probability;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code sum(singletons[i] * probabilities[i])}
     */
    @Override
    public double getMean() {
        double mean = 0;

        for (final Pair<Integer, Double> sample : innerDistribution.getPmf()) {
            mean += sample.getValue() * sample.getKey();
        }

        return mean;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code sum((singletons[i] - mean) ^ 2 * probabilities[i])}
     */
    @Override
    public double getVariance() {
        double mean = 0;
        double meanOfSquares = 0;

        for (final Pair<Integer, Double> sample : innerDistribution.getPmf()) {
            mean += sample.getValue() * sample.getKey();
            meanOfSquares += sample.getValue() * sample.getKey() * sample.getKey();
        }

        return meanOfSquares - mean * mean;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the lowest value with non-zero probability.
     *
     * @return the lowest value with non-zero probability.
     */
    @Override
    public int getSupportLowerBound() {
        int min = Integer.MAX_VALUE;
        for (final Pair<Integer, Double> sample : innerDistribution.getPmf()) {
            if (sample.getKey() < min && sample.getValue() > 0) {
                min = sample.getKey();
            }
        }

        return min;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the highest value with non-zero probability.
     *
     * @return the highest value with non-zero probability.
     */
    @Override
    public int getSupportUpperBound() {
        int max = Integer.MIN_VALUE;
        for (final Pair<Integer, Double> sample : innerDistribution.getPmf()) {
            if (sample.getKey() > max && sample.getValue() > 0) {
                max = sample.getKey();
            }
        }

        return max;
    }

    /**
     * {@inheritDoc}
     *
     * The support of this distribution is connected.
     *
     * @return {@code true}
     */
    @Override
    public boolean isSupportConnected() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public DiscreteDistribution.Sampler createSampler(final UniformRandomProvider rng) {
        return new DiscreteDistribution.Sampler() {
            /** Delegate. */
            private final EnumeratedDistribution<Integer>.Sampler inner =
                innerDistribution.createSampler(rng);

            /** {@inheritDoc} */
            @Override
            public int sample() {
                return inner.sample();
            }
        };
    }
}
