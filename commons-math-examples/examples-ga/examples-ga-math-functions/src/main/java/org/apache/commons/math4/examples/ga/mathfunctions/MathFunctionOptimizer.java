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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math4.ga.GeneticAlgorithm;
import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.convergence.UnchangedBestFitness;
import org.apache.commons.math4.ga.crossover.OnePointBinaryCrossover;
import org.apache.commons.math4.ga.listener.PopulationStatisticsLogger;
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
     * @return returns best chromosome
     */
    public Chromosome<Coordinate> optimize(int dimension,
            double crossoverRate,
            double mutationRate,
            double elitismRate,
            int tournamentSize,
            int generationCountWithUnchangedBestFitness,
            int populationSize) {

        // initialize a new genetic algorithm
        final GeneticAlgorithm<Coordinate> ga = new GeneticAlgorithm<Coordinate>(
                new OnePointBinaryCrossover<Coordinate>(), crossoverRate, new BinaryMutation<Coordinate>(),
                mutationRate, new TournamentSelection<Coordinate>(tournamentSize), elitismRate,
                new PopulationStatisticsLogger<Coordinate>());

        // stopping condition
        final StoppingCondition<Coordinate> stopCond = new UnchangedBestFitness<>(
                generationCountWithUnchangedBestFitness);

        // run the algorithm
        final Population<Coordinate> finalPopulation = ga.evolve(getInitialPopulation(dimension, populationSize),
                stopCond, Runtime.getRuntime().availableProcessors());

        // best chromosome from the final population
        final Chromosome<Coordinate> bestFinal = finalPopulation.getFittestChromosome();

        return bestFinal;
    }

    private static Population<Coordinate> getInitialPopulation(int dimension, int populationSize) {
        final Population<Coordinate> population = new ListPopulation<>(populationSize);

        for (int i = 0; i < populationSize; i++) {
            population.addChromosome(BinaryChromosome
                    .<Coordinate>randomChromosome(dimension * CHROMOSOME_LENGTH_PER_DIMENSION, coordinate -> {
                        double sumOfSquare = 0.0;
                        for (Double value : coordinate.getValues()) {
                            sumOfSquare += Math.pow(value, 2);
                        }
                        return -Math.pow(sumOfSquare, .25) *
                                (Math.pow(Math.sin(50 * Math.pow(sumOfSquare, .1)), 2) + 1);

                    }, chromosome -> {
                            final BinaryChromosome<Coordinate> binaryChromosome =
                                    (BinaryChromosome<Coordinate>) chromosome;
                            final long length = binaryChromosome.getLength();
                            final List<Double> dimValues = new ArrayList<>();

                            for (int j = 0; j < length; j += 12) {
                                final String dimensionStrValue = binaryChromosome.getStringRepresentation(j, j + 12);
                                dimValues.add(Integer.parseUnsignedInt(dimensionStrValue, 2) / 100d);
                            }

                            return new Coordinate(dimValues);
                        }));
        }
        return population;
    }
}
