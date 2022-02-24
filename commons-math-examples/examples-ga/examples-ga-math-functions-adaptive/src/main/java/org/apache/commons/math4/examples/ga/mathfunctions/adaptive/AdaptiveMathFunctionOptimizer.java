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

package org.apache.commons.math4.examples.ga.mathfunctions.adaptive;

import org.apache.commons.math4.ga.AbstractGeneticAlgorithm;
import org.apache.commons.math4.ga.AdaptiveGeneticAlgorithm;
import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.convergence.StoppingCondition;
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
public final class AdaptiveMathFunctionOptimizer {
    /** length of chromosome. **/
    private static final int CHROMOSOME_LENGTH_PER_DIMENSION = 12;

    /** instance of logger. **/
    private final Logger logger = LoggerFactory.getLogger(AdaptiveMathFunctionOptimizer.class);

    public void optimize(int dimension,
            double minCrossoverRate,
            double maxCrossoverRate,
            double minMutationRate,
            double maxMutationRate,
            double elitismRate,
            int tournamentSize,
            int generationCountWithUnchangedBestFitness,
            int populationSize) {

        // initialize a new genetic algorithm
        final AbstractGeneticAlgorithm<Coordinate> ga = new AdaptiveGeneticAlgorithm<>(new OnePointBinaryCrossover<>(),
                new AdaptiveLinearMaximumRankBasedCrossoverRateGenerator<>(minCrossoverRate, maxCrossoverRate),
                new BinaryMutation<>(), new AdaptiveLinearMutationRateGenerator<>(minMutationRate, maxMutationRate),
                new TournamentSelection<>(tournamentSize), elitismRate, new PopulationStatisticsLogger<>());

        // stopping condition
        final StoppingCondition<Coordinate> stopCond = new UnchangedBestFitness<>(
                generationCountWithUnchangedBestFitness);

        // run the algorithm
        final Population<Coordinate> finalPopulation = ga.evolve(getInitialPopulation(dimension, populationSize),
                stopCond, Runtime.getRuntime().availableProcessors());

        // best chromosome from the final population
        final Chromosome<Coordinate> bestFinal = finalPopulation.getFittestChromosome();

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
