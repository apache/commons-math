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

import org.apache.commons.math4.ga.chromosome.AbstractListChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.internal.exception.GeneticException;

/**
 * An abstraction of crossover policy for list chromosomes.
 * @param <T> genetype of chromosome
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public abstract class AbstractListChromosomeCrossoverPolicy<T, P> extends AbstractChromosomeCrossoverPolicy<P> {

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public ChromosomePair<P> crossover(final Chromosome<P> first, final Chromosome<P> second) {
        // check for validity.
        checkValidity(first, second);

        final AbstractListChromosome<T, P> firstListChromosome = (AbstractListChromosome<T, P>) first;
        final AbstractListChromosome<T, P> secondListChromosome = (AbstractListChromosome<T, P>) second;

        return mate(firstListChromosome, secondListChromosome);
    }

    /**
     * Validates the chromosome pair.
     * @param first  first chromosome
     * @param second second chromosome
     */
    @SuppressWarnings("unchecked")
    protected void checkValidity(final Chromosome<P> first, final Chromosome<P> second) {
        if (!(first instanceof AbstractListChromosome<?, ?> && second instanceof AbstractListChromosome<?, ?>)) {
            throw new GeneticException(GeneticException.INVALID_FIXED_LENGTH_CHROMOSOME);
        }
        final AbstractListChromosome<T, P> firstListChromosome = (AbstractListChromosome<T, P>) first;
        final AbstractListChromosome<T, P> secondListChromosome = (AbstractListChromosome<T, P>) second;

        final int length = firstListChromosome.getLength();
        if (length != secondListChromosome.getLength()) {
            throw new GeneticException(GeneticException.SIZE_MISMATCH, secondListChromosome.getLength(), length);
        }

    }

    /**
     * Performs mating between two chromosomes and returns the offspring pair.
     * @param first  The first parent chromosome participating in crossover
     * @param second The second parent chromosome participating in crossover
     * @return chromosome pair
     */
    protected abstract ChromosomePair<P> mate(AbstractListChromosome<T, P> first, AbstractListChromosome<T, P> second);

}
