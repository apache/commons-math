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
package org.apache.commons.math3.distribution;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.commons.math3.util.Pair;

/**
 * Implementation of the integer-valued discrete distribution.
 *
 * Note: values with zero-probability are allowed but they do not extend the
 * support.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Probability_distribution#Discrete_probability_distribution">Discrete probability distribution (Wikipedia)</a>
 * @see <a href="http://mathworld.wolfram.com/DiscreteDistribution.html">Discrete Distribution (MathWorld)</a>
 * @version $Id: DiscreteIntegerDistribution.java 169 2013-03-08 09:02:38Z wydrych $
 */
public class DiscreteIntegerDistribution extends AbstractIntegerDistribution {

    /** Serializable UID. */
    private static final long serialVersionUID = 20130308L;

    /**
     * {@link DiscreteDistribution} instance (using the {@link Integer} wrapper)
     * used to generate samples.
     */
    protected final DiscreteDistribution<Integer> innerDistribution;

    /**
     * Create a discrete distribution using the given probability mass function
     * definition.
     *
     * @param singletons array of random variable values.
     * @param probabilities array of probabilities.
     * @throws DimensionMismatchException if
     * {@code singletons.length != probabilities.length}
     * @throws NotPositiveException if probability of at least one value is
     * negative.
     * @throws MathArithmeticException if the probabilities sum to zero.
     * @throws MathIllegalArgumentException if probability of at least one value
     * is infinite.
     */
    public DiscreteIntegerDistribution(final int[] singletons, final double[] probabilities)
        throws DimensionMismatchException, NotPositiveException, MathArithmeticException, MathIllegalArgumentException {
        this(new Well19937c(), singletons, probabilities);
    }

    /**
     * Create a discrete distribution using the given random number generator
     * and probability mass function definition.
     *
     * @param rng random number generator.
     * @param singletons array of random variable values.
     * @param probabilities array of probabilities.
     * @throws DimensionMismatchException if
     * {@code singletons.length != probabilities.length}
     * @throws NotPositiveException if probability of at least one value is
     * negative.
     * @throws MathArithmeticException if the probabilities sum to zero.
     * @throws MathIllegalArgumentException if probability of at least one value
     * is infinite.
     */
    public DiscreteIntegerDistribution(final RandomGenerator rng,
                                       final int[] singletons, final double[] probabilities)
        throws DimensionMismatchException, NotPositiveException, MathArithmeticException, MathIllegalArgumentException {
        super(rng);
        if (singletons.length != probabilities.length) {
            throw new DimensionMismatchException(probabilities.length, singletons.length);
        }

        final List<Pair<Integer, Double>> samples = new ArrayList<Pair<Integer, Double>>(singletons.length);

        for (int i = 0; i < singletons.length; i++) {
            samples.add(new Pair<Integer, Double>(singletons[i], probabilities[i]));
        }

        innerDistribution = new DiscreteDistribution<Integer>(rng, samples);
    }

    /**
     * {@inheritDoc}
     */
    public double probability(final int x) {
        return innerDistribution.probability(x);
    }

    /**
     * {@inheritDoc}
     */
    public double cumulativeProbability(final int x) {
        double probability = 0;

        for (final Pair<Integer, Double> sample : innerDistribution.getSamples()) {
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
    public double getNumericalMean() {
        double mean = 0;

        for (final Pair<Integer, Double> sample : innerDistribution.getSamples()) {
            mean += sample.getValue() * sample.getKey();
        }

        return mean;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code sum((singletons[i] - mean) ^ 2 * probabilities[i])}
     */
    public double getNumericalVariance() {
        double mean = 0;
        double meanOfSquares = 0;

        for (final Pair<Integer, Double> sample : innerDistribution.getSamples()) {
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
    public int getSupportLowerBound() {
        int min = Integer.MAX_VALUE;
        for (final Pair<Integer, Double> sample : innerDistribution.getSamples()) {
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
    public int getSupportUpperBound() {
        int max = Integer.MIN_VALUE;
        for (final Pair<Integer, Double> sample : innerDistribution.getSamples()) {
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
    public boolean isSupportConnected() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int sample() {
        return innerDistribution.sample();
    }
}
