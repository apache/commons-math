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

package org.apache.commons.math4.examples.genetics.mathfunctions;

import org.apache.commons.math4.examples.genetics.mathfunctions.utils.Constants;

import org.apache.commons.math4.examples.genetics.mathfunctions.utils.GraphPlotter;
import org.apache.commons.math4.genetics.GeneticAlgorithm;
import org.apache.commons.math4.genetics.chromosome.BinaryChromosome;
import org.apache.commons.math4.genetics.chromosome.Chromosome;
import org.apache.commons.math4.genetics.convergencecond.StoppingCondition;
import org.apache.commons.math4.genetics.convergencecond.UnchangedBestFitness;
import org.apache.commons.math4.genetics.crossover.OnePointCrossover;
import org.apache.commons.math4.genetics.listener.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.listener.PopulationStatisticsLogger;
import org.apache.commons.math4.genetics.mutation.BinaryMutation;
import org.apache.commons.math4.genetics.population.ListPopulation;
import org.apache.commons.math4.genetics.population.Population;
import org.apache.commons.math4.genetics.selection.TournamentSelection;
import org.apache.commons.math4.genetics.utils.ConsoleLogger;

/**
 * This class represents an optimizer for a 2-dimensional math function using
 * genetic algorithm.
 */
public class Dimension2FunctionOptimizer {

    /**
     * Optimizes the 2-dimension fitness function.
     * @param args arguments
     */
    public static void main(String[] args) {
        final Population<Coordinate> initPopulation = getInitialPopulation();

        final Dimension2FunctionOptimizer optimizer = new Dimension2FunctionOptimizer();

        final ConvergenceListenerRegistry<Coordinate> convergenceListenerRegistry = ConvergenceListenerRegistry
                .getInstance();
        convergenceListenerRegistry
                .addConvergenceListener(new PopulationStatisticsLogger<Coordinate>(Constants.ENCODING));
        convergenceListenerRegistry
                .addConvergenceListener(new GraphPlotter("Convergence Stats", "generation", "fitness"));

        optimizer.optimize(initPopulation);
    }

    /**
     * Optimizes the population.
     * @param initial The {@link Population}
     */
    public void optimize(Population<Coordinate> initial) {

        // initialize a new genetic algorithm
        final GeneticAlgorithm<Coordinate> ga = new GeneticAlgorithm<>(new OnePointCrossover<Integer, Coordinate>(),
                Constants.CROSSOVER_RATE, new BinaryMutation<Coordinate>(), Constants.AVERAGE_MUTATION_RATE,
                new TournamentSelection<Coordinate>(Constants.TOURNAMENT_SIZE), Constants.ELITISM_RATE);

        // stopping condition
        final StoppingCondition<Coordinate> stopCond = new UnchangedBestFitness<>(
                Constants.GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS);

        // run the algorithm
        final Population<Coordinate> finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        final Chromosome<Coordinate> bestFinal = finalPopulation.getFittestChromosome();
        final ConsoleLogger consoleLogger = ConsoleLogger.getInstance(Constants.ENCODING);
        consoleLogger.log("*********************************************");
        consoleLogger.log("***********Optimization Result***************");

        consoleLogger.log(bestFinal.toString());

    }

    /**
     * Generates an initial population.
     * @return initial population
     */
    private static Population<Coordinate> getInitialPopulation() {
        final Population<Coordinate> population = new ListPopulation<>(Constants.POPULATION_SIZE);
        final Dimension2FitnessFunction fitnessFunction = new Dimension2FitnessFunction();
        final Dimension2Decoder decoder = new Dimension2Decoder();
        for (int i = 0; i < Constants.POPULATION_SIZE; i++) {
            population.addChromosome(BinaryChromosome.<Coordinate>randomChromosome(Constants.CHROMOSOME_LENGTH,
                    fitnessFunction, decoder));
        }
        return population;
    }

}
