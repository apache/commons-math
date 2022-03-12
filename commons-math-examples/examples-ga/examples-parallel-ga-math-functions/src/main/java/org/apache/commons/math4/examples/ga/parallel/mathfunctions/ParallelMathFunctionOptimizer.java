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

package org.apache.commons.math4.examples.ga.parallel.mathfunctions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math4.ga.AdaptiveGeneticAlgorithm;
import org.apache.commons.math4.ga.GeneticAlgorithm;
import org.apache.commons.math4.ga.ParallelGeneticAlgorithm;
import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.convergence.UnchangedBestFitness;
import org.apache.commons.math4.ga.crossover.OnePointBinaryCrossover;
import org.apache.commons.math4.ga.crossover.rategenerator.AdaptiveLinearMaximumRankBasedCrossoverRateGenerator;
import org.apache.commons.math4.ga.listener.PopulationStatisticsLogger;
import org.apache.commons.math4.ga.mutation.BinaryMutation;
import org.apache.commons.math4.ga.mutation.rategenerator.AdaptiveLinearMutationRateGenerator;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.selection.TournamentSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an optimizer for a 2-dimensional math function using
 * genetic algorithm.
 */

public final class ParallelMathFunctionOptimizer {
    /** length of chromosome. **/
    private static final int CHROMOSOME_LENGTH_PER_DIMENSION = 12;
    /** instance of logger. **/
    private final Logger logger = LoggerFactory.getLogger(ParallelMathFunctionOptimizer.class);

    /**
     * Optimizes the population.
     * @param dimension                               dimension
     * @param tournamentSize                          tournament size
     * @param generationCountWithUnchangedBestFitness no of generation evolved with
     *                                                unchanged best fitness
     * @param populationSize                          size of population
     */
    public void optimize(int dimension,
            int tournamentSize,
            int generationCountWithUnchangedBestFitness,
            int populationSize) {

        ParallelGeneticAlgorithm<Coordinate> parallelGeneticAlgorithm = new ParallelGeneticAlgorithm<>();

        parallelGeneticAlgorithm.addAlgorithmExecutionConfig(
                new GeneticAlgorithm<Coordinate>(new OnePointBinaryCrossover<Coordinate>(), .8,
                        new BinaryMutation<Coordinate>(), .02, new TournamentSelection<Coordinate>(tournamentSize), .2,
                        new PopulationStatisticsLogger<Coordinate>()),
                getInitialPopulation(dimension, populationSize),
                new UnchangedBestFitness<>(generationCountWithUnchangedBestFitness));
        parallelGeneticAlgorithm.addAlgorithmExecutionConfig(
                new GeneticAlgorithm<Coordinate>(new OnePointBinaryCrossover<Coordinate>(), .75,
                        new BinaryMutation<Coordinate>(), .03, new TournamentSelection<Coordinate>(tournamentSize), .3,
                        new PopulationStatisticsLogger<Coordinate>()),
                getInitialPopulation(dimension, populationSize),
                new UnchangedBestFitness<>(generationCountWithUnchangedBestFitness));
        parallelGeneticAlgorithm.addAlgorithmExecutionConfig(
                new AdaptiveGeneticAlgorithm<>(new OnePointBinaryCrossover<>(),
                        new AdaptiveLinearMaximumRankBasedCrossoverRateGenerator<>(.5, 1), new BinaryMutation<>(),
                        new AdaptiveLinearMutationRateGenerator<>(.01, .05), new TournamentSelection<>(tournamentSize),
                        .25, new PopulationStatisticsLogger<>()),
                getInitialPopulation(dimension, populationSize),
                new UnchangedBestFitness<>(generationCountWithUnchangedBestFitness));

        // run the algorithm
        final List<Population<Coordinate>> finalPopulations = parallelGeneticAlgorithm
                .evolve(Runtime.getRuntime().availableProcessors());

        // find best chromosomes from all populations.
        List<Chromosome<Coordinate>> bestChromosomes = new ArrayList<>();
        for (Population<Coordinate> population : finalPopulations) {
            bestChromosomes.add(population.getFittestChromosome());
        }

        // best chromosome after convergence.
        final Chromosome<Coordinate> bestFinal = Collections.max(bestChromosomes);

        logger.info(bestFinal.toString());
    }

    private static Population<Coordinate> getInitialPopulation(int dimension, int populationSize) {
        final Population<Coordinate> population = new ListPopulation<>(populationSize);
        final MathFunction fitnessFunction = new MathFunction();
        final CoordinateDecoder decoder = new CoordinateDecoder();
        for (int i = 0; i < populationSize; i++) {
            population.addChromosome(BinaryChromosome.<Coordinate>randomChromosome(
                    dimension * CHROMOSOME_LENGTH_PER_DIMENSION, fitnessFunction, decoder));
        }
        return population;
    }
}
