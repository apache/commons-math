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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.ga.chromosome.AbstractListChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.utils.RandomNumberGenerator;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * Perform Uniform Crossover [UX] on the specified chromosomes. A fixed mixing
 * ratio is used to combine genes from the first and second parents, e.g. using
 * a ratio of 0.5 would result in approximately 50% of genes coming from each
 * parent. This is typically a poor method of crossover, but empirical evidence
 * suggests that it is more exploratory and results in a larger part of the
 * problem space being searched.
 * <p>
 * This crossover policy evaluates each gene of the parent chromosomes by
 * choosing a uniform random number {@code p} in the range [0, 1]. If {@code p}
 * &lt; {@code ratio}, the parent genes are swapped. This means with a ratio of
 * 0.7, 30% of the genes from the first parent and 70% from the second parent
 * will be selected for the first offspring (and vice versa for the second
 * offspring).
 * <p>
 * This policy works only on {@link AbstractListChromosome}, and therefore it is
 * parameterized by T. Moreover, the chromosomes must have same lengths.
 *
 * @see <a href=
 *      "http://en.wikipedia.org/wiki/Crossover_%28genetic_algorithm%29">Crossover
 *      techniques (Wikipedia)</a>
 * @see <a href=
 *      "http://www.obitko.com/tutorials/genetic-algorithms/crossover-mutation.php">Crossover
 *      (Obitko.com)</a>
 * @see <a href="http://www.tomaszgwiazda.com/uniformX.htm">Uniform
 *      crossover</a>
 * @param <T> generic type of the {@link AbstractListChromosome}s for crossover
 * @param <P> phenotype of chromosome
 * @since 3.1
 */
public class UniformCrossover<T, P> extends AbstractListChromosomeCrossoverPolicy<T, P> {

    /** crossover rate. **/
    public static final String CROSSOVER_RATE = "CROSSOVER_RATE";

    /** The mixing ratio. */
    private final double ratio;

    /**
     * Creates a new {@link UniformCrossover} policy using the given mixing ratio.
     *
     * @param ratio the mixing ratio
     */
    public UniformCrossover(final double ratio) {
        if (ratio < 0.0d || ratio > 1.0d) {
            throw new GeneticException(GeneticException.OUT_OF_RANGE, ratio, CROSSOVER_RATE, 0.0d, 1.0d);
        }
        this.ratio = ratio;
    }

    /**
     * Returns the mixing ratio used by this {@link CrossoverPolicy}.
     *
     * @return the mixing ratio
     */
    public double getRatio() {
        return ratio;
    }

    /**
     * Helper for {@link #crossover(Chromosome, Chromosome, double)}. Performs the
     * actual crossover.
     *
     * @param first  the first chromosome
     * @param second the second chromosome
     * @return the pair of new chromosomes that resulted from the crossover
     */
    @Override
    protected ChromosomePair<P> mate(final AbstractListChromosome<T, P> first,
            final AbstractListChromosome<T, P> second) {
        final int length = first.getLength();
        // array representations of the parents
        final List<T> parent1Rep = first.getRepresentation();
        final List<T> parent2Rep = second.getRepresentation();
        // and of the children
        final List<T> child1Rep = new ArrayList<>(length);
        final List<T> child2Rep = new ArrayList<>(length);

        final UniformRandomProvider random = RandomNumberGenerator.getRandomGenerator();

        for (int index = 0; index < length; index++) {

            if (random.nextDouble() < ratio) {
                // swap the bits -> take other parent
                child1Rep.add(parent2Rep.get(index));
                child2Rep.add(parent1Rep.get(index));
            } else {
                child1Rep.add(parent1Rep.get(index));
                child2Rep.add(parent2Rep.get(index));
            }
        }

        return new ChromosomePair<>(first.newChromosome(child1Rep), second.newChromosome(child2Rep));
    }
}
