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
package org.apache.commons.math4.genetics;

import org.apache.commons.math4.genetics.chromosome.ChromosomePair;
import org.apache.commons.math4.genetics.crossover.CrossoverPolicy;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.mutation.MutationPolicy;
import org.apache.commons.math4.genetics.population.Population;
import org.apache.commons.math4.genetics.selection.SelectionPolicy;
import org.apache.commons.math4.genetics.utils.Constants;

/**
 * Implementation of a genetic algorithm. All factors that govern the operation
 * of the algorithm can be configured for a specific problem.
 *
 * @param <P> phenotype of chromosome
 * @since 4.0
 */
public class GeneticAlgorithm<P> extends AbstractGeneticAlgorithm<P> {

    /** the rate of crossover for the algorithm. */
    private final double crossoverRate;

    /** the rate of mutation for the algorithm. */
    private final double mutationRate;

    /**
     * Create a new genetic algorithm.
     * @param crossoverPolicy The {@link CrossoverPolicy}
     * @param crossoverRate   The crossover rate as a percentage (0-1 inclusive)
     * @param mutationPolicy  The {@link MutationPolicy}
     * @param mutationRate    The mutation rate as a percentage (0-1 inclusive)
     * @param selectionPolicy The {@link SelectionPolicy}
     */
    public GeneticAlgorithm(final CrossoverPolicy<P> crossoverPolicy, final double crossoverRate,
            final MutationPolicy<P> mutationPolicy, final double mutationRate,
            final SelectionPolicy<P> selectionPolicy) {
        super(crossoverPolicy, mutationPolicy, selectionPolicy);

        if (crossoverRate < 0 || crossoverRate > 1) {
            throw new GeneticException(GeneticException.OUT_OF_RANGE, crossoverRate, Constants.CROSSOVER_RATE, 0, 1);
        }
        if (mutationRate < 0 || mutationRate > 1) {
            throw new GeneticException(GeneticException.OUT_OF_RANGE, mutationRate, Constants.MUTATION_RATE, 0, 1);
        }
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
    }

    /**
     * Create a new genetic algorithm.
     * @param crossoverPolicy The {@link CrossoverPolicy}
     * @param crossoverRate   The crossover rate as a percentage (0-1 inclusive)
     * @param mutationPolicy  The {@link MutationPolicy}
     * @param mutationRate    The mutation rate as a percentage (0-1 inclusive)
     * @param selectionPolicy The {@link SelectionPolicy}
     * @param elitismRate     The rate of elitism
     */
    public GeneticAlgorithm(final CrossoverPolicy<P> crossoverPolicy, final double crossoverRate,
            final MutationPolicy<P> mutationPolicy, final double mutationRate, final SelectionPolicy<P> selectionPolicy,
            final double elitismRate) {
        super(crossoverPolicy, mutationPolicy, selectionPolicy, elitismRate);

        if (crossoverRate < 0 || crossoverRate > 1) {
            throw new GeneticException(GeneticException.OUT_OF_RANGE, crossoverRate, Constants.CROSSOVER_RATE, 0, 1);
        }
        if (mutationRate < 0 || mutationRate > 1) {
            throw new GeneticException(GeneticException.OUT_OF_RANGE, mutationRate, Constants.MUTATION_RATE, 0, 1);
        }
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
    }

    /**
     * Evolve the given population into the next generation.
     * <ol>
     *  <li>Get nextGeneration population to fill from <code>current</code>
     *      generation, using its nextGeneration method</li>
     *  <li>Loop until new generation is filled:
     *  <ul><li>Apply configured SelectionPolicy to select a pair of parents
     *          from <code>current</code></li>
     *      <li>With probability = {@link #getCrossoverRate()}, apply
     *          configured {@link CrossoverPolicy} to parents</li>
     *      <li>With probability = {@link #getMutationRate()}, apply
     *          configured {@link MutationPolicy} to each of the offspring</li>
     *      <li>Add offspring individually to nextGeneration,
     *          space permitting</li>
     *  </ul></li>
     *  <li>Return nextGeneration</li>
     * </ol>
     *
     * @param current the current population.
     * @return the population for the next generation.
     */
    @Override
    protected Population<P> nextGeneration(final Population<P> current) {
        final Population<P> nextGeneration = current.nextGeneration(getElitismRate());

        while (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit() - 1) {
            // select parent chromosomes
            ChromosomePair<P> pair = getSelectionPolicy().select(current);

            // apply crossover policy to create two offspring
            pair = getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond(), crossoverRate);

            // apply mutation policy to the chromosomes
            pair = new ChromosomePair<>(getMutationPolicy().mutate(pair.getFirst(), mutationRate),
                    getMutationPolicy().mutate(pair.getSecond(), mutationRate));

            // add the chromosomes to the population
            nextGeneration.addChromosome(pair.getFirst());
            nextGeneration.addChromosome(pair.getSecond());
        }

        return nextGeneration;
    }

    /**
     * Returns the crossover rate.
     * @return crossover rate
     */
    public double getCrossoverRate() {
        return crossoverRate;
    }

    /**
     * Returns the mutation rate.
     * @return mutation rate
     */
    public double getMutationRate() {
        return mutationRate;
    }

}
