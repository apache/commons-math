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

package org.apache.commons.math4.ga2;

import java.util.List;
import java.util.ArrayList;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * Base class for "mutation" operators.
 *
 * @param <G> Genotype.
 */
public abstract class AbstractMutation<G> implements GeneticOperator<G> {
    /** Number of parents and number of offsprings. */
    private static final int NUM_CHROMOSOMES = 1;
    /** Mutation probability, per gene. */
    private final double probability;

    /**
     * @param probability Probability that any gene should mutate.
     */
    protected AbstractMutation(double probability) {
        if (probability <= 0 ||
            probability > 1) {
            throw new IllegalArgumentException("Out of range");
        }

        this.probability = probability;
    }

    /** @return the probability of gene mutation. */
    protected double getProbability() {
        return probability;
    }

    /** {@inheritDoc} */
    @Override
    public final int numberOfParents() {
        return NUM_CHROMOSOMES;
    }

    /** {@inheritDoc} */
    @Override
    public final int numberOfOffsprings() {
        return NUM_CHROMOSOMES;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if there are less than two parents
     * or they are of different sizes.
     */
    @Override
    public List<G> apply(List<G> parents,
                         UniformRandomProvider rng) {
        if (parents.size() != NUM_CHROMOSOMES) {
            throw new IllegalArgumentException("Incompatible number of parents");
        }

        final G mutated = apply(parents.get(0),
                                rng);

        final List<G> offsprings = new ArrayList<>(1);
        offsprings.add(mutated);

        return offsprings;
    }

    /**
     * Apply operator.
     *
     * @param parent Parent.
     * @param rng RNG.
     * @return a list containing one offpsring.
     */
    protected abstract G apply(G parent,
                               UniformRandomProvider rng);
}
