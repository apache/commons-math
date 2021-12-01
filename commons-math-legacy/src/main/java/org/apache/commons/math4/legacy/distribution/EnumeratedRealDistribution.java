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
package org.apache.commons.math4.legacy.distribution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.statistics.distribution.ContinuousDistribution;
import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathArithmeticException;
import org.apache.commons.math4.legacy.exception.NotANumberException;
import org.apache.commons.math4.legacy.exception.NotFiniteNumberException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.OutOfRangeException;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.legacy.core.Pair;

/**
 * <p>Implementation of a real-valued {@link EnumeratedDistribution}.
 *
 * <p>Values with zero-probability are allowed but they do not extend the
 * support.<br>
 * Duplicate values are allowed. Probabilities of duplicate values are combined
 * when computing cumulative probabilities and statistics.</p>
 *
 * @since 3.2
 */
public class EnumeratedRealDistribution
    implements ContinuousDistribution {
    /**
     * {@link EnumeratedDistribution} (using the {@link Double} wrapper)
     * used to generate the pmf.
     */
    protected final EnumeratedDistribution<Double> innerDistribution;

    /**
     * Create a discrete real-valued distribution using the given random number generator
     * and probability mass function enumeration.
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
    public EnumeratedRealDistribution(final double[] singletons,
                                      final double[] probabilities)
        throws DimensionMismatchException,
               NotPositiveException,
               MathArithmeticException,
               NotFiniteNumberException,
               NotANumberException {
        innerDistribution = new EnumeratedDistribution<>(createDistribution(singletons, probabilities));
    }

    /**
     * Creates a discrete real-valued distribution from the input data.
     * Values are assigned mass based on their frequency.
     *
     * @param data input dataset
     */
    public EnumeratedRealDistribution(final double[] data) {
        final Map<Double, Integer> dataMap = new HashMap<>();
        for (double value : data) {
            Integer count = dataMap.get(value);
            if (count == null) {
                count = 0;
            }
            dataMap.put(value, ++count);
        }
        final int massPoints = dataMap.size();
        final double denom = data.length;
        final double[] values = new double[massPoints];
        final double[] probabilities = new double[massPoints];
        int index = 0;
        for (Entry<Double, Integer> entry : dataMap.entrySet()) {
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
    private static List<Pair<Double, Double>>  createDistribution(double[] singletons, double[] probabilities) {
        if (singletons.length != probabilities.length) {
            throw new DimensionMismatchException(probabilities.length, singletons.length);
        }

        final List<Pair<Double, Double>> samples = new ArrayList<>(singletons.length);

        for (int i = 0; i < singletons.length; i++) {
            samples.add(new Pair<>(singletons[i], probabilities[i]));
        }
        return samples;

    }

    /**
     * For a random variable {@code X} whose values are distributed according to
     * this distribution, this method returns {@code P(X = x)}. In other words,
     * this method represents the probability mass function (PMF) for the
     * distribution.
     *
     * @param x the point at which the PMF is evaluated
     * @return the value of the probability mass function at point {@code x}
     */
    @Override
    public double density(final double x) {
        return innerDistribution.probability(x);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double cumulativeProbability(final double x) {
        double probability = 0;

        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
            if (sample.getKey() <= x) {
                probability += sample.getValue();
            }
        }

        return probability;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }

        double probability = 0;
        double x = getSupportLowerBound();
        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
            if (sample.getValue() == 0.0) {
                continue;
            }

            probability += sample.getValue();
            x = sample.getKey();

            if (probability >= p) {
                break;
            }
        }

        return x;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code sum(singletons[i] * probabilities[i])}
     */
    @Override
    public double getMean() {
        double mean = 0;

        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
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

        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
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
    public double getSupportLowerBound() {
        double min = Double.POSITIVE_INFINITY;
        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
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
    public double getSupportUpperBound() {
        double max = Double.NEGATIVE_INFINITY;
        for (final Pair<Double, Double> sample : innerDistribution.getPmf()) {
            if (sample.getKey() > max && sample.getValue() > 0) {
                max = sample.getKey();
            }
        }

        return max;
    }

    /** {@inheritDoc} */
    @Override
    public ContinuousDistribution.Sampler createSampler(final UniformRandomProvider rng) {
        return new ContinuousDistribution.Sampler() {
            /** Delegate. */
            private final EnumeratedDistribution<Double>.Sampler inner =
                innerDistribution.createSampler(rng);

            /** {@inheritDoc} */
            @Override
            public double sample() {
                return inner.sample();
            }
        };
    }
}
