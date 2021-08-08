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

package org.apache.commons.math4.examples.ga.mathfunctions.dimension2;

import org.apache.commons.math4.examples.ga.mathfunctions.utils.Constants;
import org.apache.commons.math4.ga.AdaptiveGeneticAlgorithm;
import org.apache.commons.math4.ga.chromosome.BinaryChromosome;
import org.apache.commons.math4.ga.chromosome.Chromosome;
import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.convergence.UnchangedBestFitness;
import org.apache.commons.math4.ga.crossover.OnePointCrossover;
import org.apache.commons.math4.ga.crossover.rategenerator.ConstantCrossoverRateGenerator;
import org.apache.commons.math4.ga.listener.ConvergenceListenerRegistry;
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
public class Dimension2FunctionAdaptiveOptimizer {

    /** number of dimension. **/
    private static final int DIMENSION = 2;

    /** size of tournament. **/
    private static final int TOURNAMENT_SIZE = 3;

    /** instance of logger. **/
    private Logger logger = LoggerFactory.getLogger(Dimension2FunctionAdaptiveOptimizer.class);

    /**
     * Optimizes the 2-dimension fitness function.
     * @param args arguments
     */
    public static void main(String[] args) {
        final Population<Dimension2Coordinate> initPopulation = getInitialPopulation();

        final Dimension2FunctionAdaptiveOptimizer optimizer = new Dimension2FunctionAdaptiveOptimizer();

        final ConvergenceListenerRegistry<Dimension2Coordinate> convergenceListenerRegistry =
                ConvergenceListenerRegistry.getInstance();
        convergenceListenerRegistry.addConvergenceListener(new PopulationStatisticsLogger<Dimension2Coordinate>());
        convergenceListenerRegistry
                .addConvergenceListener(new Dim2GraphPlotter("Adaptive Convergence Stats", "generation", "fitness"));

        optimizer.optimize(initPopulation);
    }

    private void optimize(Population<Dimension2Coordinate> initial) {

        // initialize a new genetic algorithm
        final AdaptiveGeneticAlgorithm<Dimension2Coordinate> ga = new AdaptiveGeneticAlgorithm<>(
                new OnePointCrossover<Integer, Dimension2Coordinate>(), new ConstantCrossoverRateGenerator<>(1),
                new BinaryMutation<Dimension2Coordinate>(),
                new AdaptiveLinearMutationRateGenerator<>(Constants.AVERAGE_MUTATION_RATE / 2,
                        Constants.AVERAGE_MUTATION_RATE * 2),
                new TournamentSelection<>(TOURNAMENT_SIZE));

        // stopping condition
        final StoppingCondition<Dimension2Coordinate> stopCond = new UnchangedBestFitness<>(
                Constants.GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS);

        // run the algorithm
        final Population<Dimension2Coordinate> finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        final Chromosome<Dimension2Coordinate> bestFinal = finalPopulation.getFittestChromosome();

        logger.info("*********************************************");
        logger.info("***********Optimization Result***************");

        logger.info(bestFinal.toString());

    }

    /**
     * Generates an initial population.
     * @return initial population
     */
    private static Population<Dimension2Coordinate> getInitialPopulation() {
        final Population<Dimension2Coordinate> population = new ListPopulation<>(
                DIMENSION * Constants.POPULATION_SIZE_PER_DIMENSION);
        final Dimension2FitnessFunction fitnessFunction = new Dimension2FitnessFunction();
        final Dimension2Decoder decoder = new Dimension2Decoder();
        for (int i = 0; i < DIMENSION * Constants.POPULATION_SIZE_PER_DIMENSION; i++) {
            population.addChromosome(BinaryChromosome.<Dimension2Coordinate>randomChromosome(
                    DIMENSION * Constants.CHROMOSOME_LENGTH_PER_DIMENSION, fitnessFunction, decoder));
        }
        return population;
    }

}
