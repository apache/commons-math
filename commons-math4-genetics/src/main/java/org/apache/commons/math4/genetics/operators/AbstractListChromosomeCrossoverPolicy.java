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

package org.apache.commons.math4.genetics.operators;

import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.model.AbstractListChromosome;
import org.apache.commons.math4.genetics.model.Chromosome;
import org.apache.commons.math4.genetics.model.ChromosomePair;

/**
 * An abstraction of crossover policy for list chromosomes.
 * @param <T>
 */
public abstract class AbstractListChromosomeCrossoverPolicy<T> extends AbstractChromosomeCrossoverPolicy {

    /**
     * {@inheritDoc}
     *
     * @throws GeneticException if the chromosomes are not an instance of
     *                          {@link AbstractListChromosome}
     * @throws GeneticException if the length of the two chromosomes is different
     */
    @Override
    public ChromosomePair crossover(Chromosome first, Chromosome second) {

        if (!(first instanceof AbstractListChromosome<?> && second instanceof AbstractListChromosome<?>)) {
            throw new GeneticException(GeneticException.INVALID_FIXED_LENGTH_CHROMOSOME);
        }

        final AbstractListChromosome<T> firstListChromosome = (AbstractListChromosome<T>) first;
        final AbstractListChromosome<T> secondListChromosome = (AbstractListChromosome<T>) second;

        final int length = firstListChromosome.getLength();
        if (length != secondListChromosome.getLength()) {
            throw new GeneticException(GeneticException.SIZE_MISMATCH, secondListChromosome.getLength(), length);
        }

        return mate(firstListChromosome, secondListChromosome);
    }

    /**
     * Performs mating between two chromosomes and returns the offspring pair.
     * @param first
     * @param second
     * @return chromosome pair
     */
    protected abstract ChromosomePair mate(AbstractListChromosome<T> first, AbstractListChromosome<T> second);

}
