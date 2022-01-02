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

package org.apache.commons.math4.examples.ga.mathfunctions;

import org.apache.commons.math4.ga.GeneticAlgorithm;
import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.convergence.UnchangedBestFitness;
import org.apache.commons.math4.ga.crossover.OnePointBinaryCrossover;
import org.apache.commons.math4.ga.mutation.BinaryMutation;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.selection.TournamentSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an optimizer for a 2-dimensional math function using
 * genetic algorithm.
 */

public final class MathFunctionOptimizer {

    /** length of chromosome. **/
    private static final int CHROMOSOME_LENGTH_PER_DIMENSION = 12;

    /** encoding for console logger. **/
    private static final String ENCODING = "UTF-8";

    /** instance of logger. **/
    private final Logger logger = LoggerFactory.getLogger(MathFunctionOptimizer.class);

    /**
     * Optimizes the population.
     * @param dimension                               dimension
     * @param crossoverRate                           crossover rate
     * @param mutationRate                            mutation rate
     * @param elitismRate                             elitism rate
     * @param tournamentSize                          tournament size
     * @param generationCountWithUnchangedBestFitness no of generation evolved with
     *                                                unchanged best fitness
     * @param populationSize                          size of population
     */
    public void optimize(int dimension,
            double crossoverRate,
            double mutationRate,
            double elitismRate,
            int tournamentSize,
            int generationCountWithUnchangedBestFitness,
            int populationSize) {

        // initialize a new genetic algorithm
        final GeneticAlgorithm<Coordinate> ga = new GeneticAlgorithm<>(new OnePointBinaryCrossover<Coordinate>(),
                crossoverRate, new BinaryMutation<Coordinate>(), mutationRate,
                new TournamentSelection<>(tournamentSize), elitismRate);

        // stopping condition
        final StoppingCondition<Coordinate> stopCond = new UnchangedBestFitness<>(
                generationCountWithUnchangedBestFitness);

        // run the algorithm
        final Population<Coordinate> finalPopulation = ga.evolve(getInitialPopulation(dimension, populationSize),
                stopCond);

        // best chromosome from the final population
        final Chromosome<Coordinate> bestFinal = finalPopulation.getFittestChromosome();

        logger.info("*********************************************");
        logger.info("***********Optimization Result***************");
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
