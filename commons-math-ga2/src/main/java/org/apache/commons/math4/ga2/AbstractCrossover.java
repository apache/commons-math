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
import org.apache.commons.rng.UniformRandomProvider;

/**
 * Base class for "crossover" operators.
 *
 * @param <G> Genotype.
 */
public abstract class AbstractCrossover<G> implements GeneticOperator<G> {
    /** Number of parents and number of offsprings. */
    private static final int NUM_CHROMOSOMES = 2;

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
            throw new IllegalArgumentException("Unexpected number of parents");
        }

        final List<G> offsprings = apply(parents.get(0),
                                         parents.get(1),
                                         rng);
        if (offsprings.size() != NUM_CHROMOSOMES) {
            throw new IllegalArgumentException("Unexpected number of offsprings");
        }

        return offsprings;
    }

    /**
     * Unconditionally apply operator.
     *
     * @param parent1 Parent.
     * @param parent2 Parent.
     * @param rng RNG.
     * @return a list containing two offpsrings.
     */
    protected abstract List<G> apply(G parent1,
                                     G parent2,
                                     UniformRandomProvider rng);
}
