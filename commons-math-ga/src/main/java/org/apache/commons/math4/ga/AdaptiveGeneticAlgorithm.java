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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.chromosome.ChromosomePair;
import org.apache.commons.math4.ga.crossover.CrossoverPolicy;
import org.apache.commons.math4.ga.crossover.rategenerator.ConstantCrossoverRateGenerator;
import org.apache.commons.math4.ga.crossover.rategenerator.CrossoverRateGenerator;
import org.apache.commons.math4.ga.internal.exception.GeneticIllegalArgumentException;
import org.apache.commons.math4.ga.internal.stats.PopulationStatisticalSummaryImpl;
import org.apache.commons.math4.ga.listener.ConvergenceListener;
import org.apache.commons.math4.ga.mutation.MutationPolicy;
import org.apache.commons.math4.ga.mutation.rategenerator.ConstantMutationRateGenerator;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(AdaptiveGeneticAlgorithm.class);
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
     * @param elitismRate                   elitism rate
     * @param convergenceListeners          An optional collection of
     *                                      {@link ConvergenceListener} with
     *                                      variable arity
     */
    @SafeVarargs
    public AdaptiveGeneticAlgorithm(CrossoverPolicy<P> crossoverPolicy,
            CrossoverRateGenerator<P> crossoverProbabilityGenerator,
            MutationPolicy<P> mutationPolicy,
            MutationRateGenerator<P> mutationProbabilityGenerator,
            SelectionPolicy<P> selectionPolicy,
            double elitismRate,
            ConvergenceListener<P>... convergenceListeners) {
        super(crossoverPolicy, mutationPolicy, selectionPolicy, elitismRate, convergenceListeners);
        this.crossoverRateGenerator = crossoverProbabilityGenerator;
        this.mutationRateGenerator = mutationProbabilityGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Population<P> nextGeneration(final Population<P> current, final ExecutorService executorService) {

        LOGGER.debug("Reproducing next generation.");

        // Initialize the next generation with elit chromosomes from previous
        // generation.
        final Population<P> nextGeneration = current.nextGeneration(getElitismRate());

        LOGGER.debug(
                "No of Elite chromosomes selected from previous generation: " + nextGeneration.getPopulationSize());

        final int maxOffspringCount = nextGeneration.getPopulationLimit() - nextGeneration.getPopulationSize();

        final Population<P> offsprings = reproduceOffsprings(current, executorService, maxOffspringCount);

        LOGGER.debug("Performing adaptive mutation of offsprings.");

        nextGeneration.addChromosomes(mutateChromosomes(executorService, offsprings));

        LOGGER.debug("New Generation: " + System.lineSeparator() + nextGeneration.toString());

        return nextGeneration;
    }

    private List<Chromosome<P>> mutateChromosomes(final ExecutorService executorService,
            final Population<P> offspringPopulation) {

        // recompute the statistics of the offspring population.
        final PopulationStatisticalSummary<P> offspringPopulationStats =
                mutationRateGenerator instanceof ConstantMutationRateGenerator ?
                null :
                new PopulationStatisticalSummaryImpl<>(offspringPopulation);

        List<Future<Chromosome<P>>> mutatedChromosomes = new ArrayList<>();

        // apply mutation policy to the offspring chromosomes and add the mutated
        // chromosomes to next generation.
        for (Chromosome<P> chromosome : offspringPopulation) {
            mutatedChromosomes.add(executorService.submit(new Callable<Chromosome<P>>() {

                @Override
                public Chromosome<P> call() throws Exception {
                    return getMutationPolicy().mutate(chromosome, mutationRateGenerator.generate(chromosome,
                            offspringPopulationStats, getGenerationsEvolved()));
                }
            }));
        }
        List<Chromosome<P>> mutatedOffsprings = new ArrayList<>();
        try {
            for (Future<Chromosome<P>> mutatedChromosome : mutatedChromosomes) {
                mutatedOffsprings.add(mutatedChromosome.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new GeneticIllegalArgumentException(e);
        }

        return mutatedOffsprings;
    }

    private Population<P> reproduceOffsprings(final Population<P> current,
            final ExecutorService executorService,
            final int maxOffspringCount) {
        // compute statistics of current generation chromosomes.
        final PopulationStatisticalSummary<P> currentGenPopulationStats = ConstantCrossoverRateGenerator.class
                .isAssignableFrom(this.crossoverRateGenerator.getClass()) ? null :
                        new PopulationStatisticalSummaryImpl<>(current);
        List<Future<ChromosomePair<P>>> chromosomePairs = new ArrayList<>();
        for (int i = maxOffspringCount / 2; i > 0; i--) {
            chromosomePairs.add(executorService.submit(() -> {
                // select parent chromosomes
                ChromosomePair<P> pair = getSelectionPolicy().select(current);
                LOGGER.debug("Selected Chromosomes: " + System.lineSeparator() + pair.toString());

                final double crossoverRate = crossoverRateGenerator.generate(pair.getFirst(), pair.getSecond(),
                        currentGenPopulationStats, getGenerationsEvolved());
                // apply crossover policy to create two offspring
                pair = getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond(), crossoverRate);
                LOGGER.debug("Offsprings after Crossover: " + System.lineSeparator() + pair.toString());

                return pair;
            }));
        }

        // Initialize an empty population for offsprings.
        final Population<P> offspringPopulation = current.nextGeneration(0);
        try {
            for (Future<ChromosomePair<P>> chromosomePair : chromosomePairs) {
                ChromosomePair<P> pair = chromosomePair.get();
                offspringPopulation.addChromosome(pair.getFirst());
                offspringPopulation.addChromosome(pair.getSecond());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new GeneticIllegalArgumentException(e);
        }
        return offspringPopulation;
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
