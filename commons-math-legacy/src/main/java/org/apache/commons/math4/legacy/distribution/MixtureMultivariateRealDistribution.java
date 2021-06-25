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
import java.util.List;

import org.apache.commons.math4.legacy.exception.DimensionMismatchException;
import org.apache.commons.math4.legacy.exception.MathArithmeticException;
import org.apache.commons.math4.legacy.exception.NotPositiveException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.legacy.core.Pair;

/**
 * Class for representing <a href="http://en.wikipedia.org/wiki/Mixture_model">
 * mixture model</a> distributions.
 *
 * @param <T> Type of the mixture components.
 *
 * @since 3.1
 */
public class MixtureMultivariateRealDistribution<T extends MultivariateRealDistribution>
    extends AbstractMultivariateRealDistribution {
    /** Normalized weight of each mixture component. */
    private final double[] weight;
    /** Mixture components. */
    private final List<T> distribution;

    /**
     * Creates a mixture model from a list of distributions and their
     * associated weights.
     *
     * @param components Distributions from which to sample.
     * @throws NotPositiveException if any of the weights is negative.
     * @throws DimensionMismatchException if not all components have the same
     * number of variables.
     */
    public MixtureMultivariateRealDistribution(List<Pair<Double, T>> components) {
        super(components.get(0).getSecond().getDimension());

        final int numComp = components.size();
        final int dim = getDimension();
        double weightSum = 0;
        for (int i = 0; i < numComp; i++) {
            final Pair<Double, T> comp = components.get(i);
            if (comp.getSecond().getDimension() != dim) {
                throw new DimensionMismatchException(comp.getSecond().getDimension(), dim);
            }
            if (comp.getFirst() < 0) {
                throw new NotPositiveException(comp.getFirst());
            }
            weightSum += comp.getFirst();
        }

        // Check for overflow.
        if (Double.isInfinite(weightSum)) {
            throw new MathArithmeticException(LocalizedFormats.OVERFLOW);
        }

        // Store each distribution and its normalized weight.
        distribution = new ArrayList<>();
        weight = new double[numComp];
        for (int i = 0; i < numComp; i++) {
            final Pair<Double, T> comp = components.get(i);
            weight[i] = comp.getFirst() / weightSum;
            distribution.add(comp.getSecond());
        }
    }

    /** {@inheritDoc} */
    @Override
    public double density(final double[] values) {
        double p = 0;
        for (int i = 0; i < weight.length; i++) {
            p += weight[i] * distribution.get(i).density(values);
        }
        return p;
    }

    /**
     * Gets the distributions that make up the mixture model.
     *
     * @return the component distributions and associated weights.
     */
    public List<Pair<Double, T>> getComponents() {
        final List<Pair<Double, T>> list = new ArrayList<>(weight.length);

        for (int i = 0; i < weight.length; i++) {
            list.add(new Pair<>(weight[i], distribution.get(i)));
        }

        return list;
    }

    /** {@inheritDoc} */
    @Override
    public MultivariateRealDistribution.Sampler createSampler(UniformRandomProvider rng) {
        return new MixtureSampler(rng);
    }

    /**
     * Sampler.
     */
    private class MixtureSampler implements MultivariateRealDistribution.Sampler {
        /** RNG. */
        private final UniformRandomProvider rng;
        /** Sampler for each of the distribution in the mixture. */
        private final MultivariateRealDistribution.Sampler[] samplers;

        /**
         * @param generator RNG.
         */
        MixtureSampler(UniformRandomProvider generator) {
            rng = generator;

            samplers = new MultivariateRealDistribution.Sampler[weight.length];
            for (int i = 0; i < weight.length; i++) {
                samplers[i] = distribution.get(i).createSampler(rng);
            }
        }

        /** {@inheritDoc} */
        @Override
        public double[] sample() {
            // Sampled values.
            double[] vals = null;

            // Determine which component to sample from.
            final double randomValue = rng.nextDouble();
            double sum = 0;

            for (int i = 0; i < weight.length; i++) {
                sum += weight[i];
                if (randomValue <= sum) {
                    // pick model i
                    vals = samplers[i].sample();
                    break;
                }
            }

            if (vals == null) {
                // This should never happen, but it ensures we won't return a null in
                // case the loop above has some floating point inequality problem on
                // the final iteration.
                vals = samplers[weight.length - 1].sample();
            }

            return vals;
        }
    }
}
