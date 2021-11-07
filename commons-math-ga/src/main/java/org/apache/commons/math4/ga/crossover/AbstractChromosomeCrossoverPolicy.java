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

package org.apache.commons.math4.ga.crossover;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.utils.RandomNumberGenerator;

/**
 * An abstraction to represent the base crossover policy.
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public abstract class AbstractChromosomeCrossoverPolicy<P> implements CrossoverPolicy<P> {

    /**
     * {@inheritDoc}
     */
    @Override
    public ChromosomePair<P> crossover(final Chromosome<P> first,
            final Chromosome<P> second,
            final double crossoverRate) {
        if (RandomNumberGenerator.getRandomGenerator().nextDouble() < crossoverRate) {
            return crossover(first, second);
        } else {
            return new ChromosomePair<>(first, second);
        }
    }

    /**
     * Performs crossover of two chromosomes.
     * @param first  The first parent chromosome participating in crossover
     * @param second The second parent chromosome participating in crossover
     * @return chromosome pair
     */
    protected abstract ChromosomePair<P> crossover(Chromosome<P> first, Chromosome<P> second);

}
