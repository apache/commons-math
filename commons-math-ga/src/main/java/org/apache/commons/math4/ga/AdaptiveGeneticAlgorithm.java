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

package org.apache.commons.math4.ga;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.crossover.CrossoverPolicy;
import org.apache.commons.math4.ga.crossover.rategenerator.CrossoverRateGenerator;
import org.apache.commons.math4.ga.internal.stats.PopulationStatisticalSummaryImpl;
import org.apache.commons.math4.ga.mutation.MutationPolicy;
import org.apache.commons.math4.ga.mutation.rategenerator.MutationRateGenerator;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.selection.SelectionPolicy;
import org.apache.commons.math4.ga.stats.PopulationStatisticalSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of Genetic Algorithm. The probability of crossover and
 * mutation is generated in an adaptive way. This implementation allows
 * configuration of dynamic crossover and mutation rate generator along with
 * crossover policy, mutation policy, selection policy and optionally elitism
 * rate.
 * @param <P> phenotype of chromosome
 */
public class AdaptiveGeneticAlgorithm<P> extends AbstractGeneticAlgorithm<P> {

    /** instance of logger. **/
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGeneticAlgorithm.class);

    /** The crossover rate generator. **/
    private final CrossoverRateGenerator<P> crossoverRateGenerator;

    /** The mutation rate generator. **/
    private final MutationRateGenerator<P> mutationRateGenerator;

    /**
     * @param crossoverPolicy               crossover policy
     * @param crossoverProbabilityGenerator crossover probability generator
     * @param mutationPolicy                mutation policy
     * @param mutationProbabilityGenerator  mutation probability generator
     * @param selectionPolicy               selection policy
     */
    public AdaptiveGeneticAlgorithm(CrossoverPolicy<P> crossoverPolicy,
            CrossoverRateGenerator<P> crossoverProbabilityGenerator,
            MutationPolicy<P> mutationPolicy,
            MutationRateGenerator<P> mutationProbabilityGenerator,
            SelectionPolicy<P> selectionPolicy) {
        super(crossoverPolicy, mutationPolicy, selectionPolicy);
        this.crossoverRateGenerator = crossoverProbabilityGenerator;
        this.mutationRateGenerator = mutationProbabilityGenerator;
    }

    /**
     * @param crossoverPolicy               crossover policy
     * @param crossoverProbabilityGenerator crossover probability generator
     * @param mutationPolicy                mutation policy
     * @param mutationProbabilityGenerator  mutation probability generator
     * @param selectionPolicy               selection policy
     * @param elitismRate                   elitism rate
     */
    public AdaptiveGeneticAlgorithm(CrossoverPolicy<P> crossoverPolicy,
            CrossoverRateGenerator<P> crossoverProbabilityGenerator,
            MutationPolicy<P> mutationPolicy,
            MutationRateGenerator<P> mutationProbabilityGenerator,
            SelectionPolicy<P> selectionPolicy,
            double elitismRate) {
        super(crossoverPolicy, mutationPolicy, selectionPolicy, elitismRate);
        this.crossoverRateGenerator = crossoverProbabilityGenerator;
        this.mutationRateGenerator = mutationProbabilityGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Population<P> nextGeneration(Population<P> current) {

        LOGGER.debug("Reproducing next generation.");

        // compute statistics of current generation chromosomes.
        PopulationStatisticalSummary<P> populationStats = new PopulationStatisticalSummaryImpl<>(current);

        // Initialize the next generation with elit chromosomes from previous
        // generation.
        final Population<P> nextGeneration = current.nextGeneration(getElitismRate());

        LOGGER.debug(
                "No of Elite chromosomes selected from previous generation: " + nextGeneration.getPopulationSize());

        final int maxOffspringCount = nextGeneration.getPopulationLimit() - nextGeneration.getPopulationSize();

        // Initialize an empty population for offsprings.
        final Population<P> offspringPopulation = current.nextGeneration(0);

        // perform crossover and generate new offsprings
        while (offspringPopulation.getPopulationSize() < maxOffspringCount) {

            // select parent chromosomes
            ChromosomePair<P> pair = getSelectionPolicy().select(current);
            LOGGER.debug("Selected Chromosomes: \r\n" + pair.toString());

            final double crossoverRate = crossoverRateGenerator.generate(pair.getFirst(), pair.getSecond(),
                    populationStats, getGenerationsEvolved());
            // apply crossover policy to create two offspring
            pair = getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond(), crossoverRate);
            LOGGER.debug("Offsprings after Crossover: \r\n" + pair.toString());

            // add the first chromosome to the population
            offspringPopulation.addChromosome(pair.getFirst());
            // is there still a place for the second chromosome?
            if (offspringPopulation.getPopulationSize() < maxOffspringCount) {
                // add the second chromosome to the population
                offspringPopulation.addChromosome(pair.getSecond());
            }
        }
        LOGGER.debug("Performing adaptive mutation of offsprings.");

        // recompute the statistics of the offspring population.
        populationStats = new PopulationStatisticalSummaryImpl<>(offspringPopulation);

        // apply mutation policy to the offspring chromosomes and add the mutated
        // chromosomes to next generation.
        for (Chromosome<P> chromosome : offspringPopulation) {
            nextGeneration.addChromosome(getMutationPolicy().mutate(chromosome,
                    mutationRateGenerator.generate(chromosome, populationStats, getGenerationsEvolved())));
        }
        LOGGER.debug("New Generation: \r\n" + nextGeneration.toString());

        return nextGeneration;
    }

    /**
     * Returns crossover probability generator.
     * @return crossover probability generator
     */
    public CrossoverRateGenerator<P> getCrossoverProbabilityGenerator() {
        return crossoverRateGenerator;
    }

    /**
     * Returns mutation probability generator.
     * @return mutation probability generator
     */
    public MutationRateGenerator<P> getMutationProbabilityGenerator() {
        return mutationRateGenerator;
    }

}
