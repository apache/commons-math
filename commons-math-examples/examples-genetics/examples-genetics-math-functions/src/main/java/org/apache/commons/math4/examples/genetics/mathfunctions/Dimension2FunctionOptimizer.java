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
import org.apache.commons.math4.genetics.BinaryChromosome;
import org.apache.commons.math4.genetics.Chromosome;
import org.apache.commons.math4.genetics.GeneticAlgorithm;
import org.apache.commons.math4.genetics.ListPopulation;
import org.apache.commons.math4.genetics.Population;
import org.apache.commons.math4.genetics.convergencecond.StoppingCondition;
import org.apache.commons.math4.genetics.convergencecond.UnchangedBestFitness;
import org.apache.commons.math4.genetics.crossover.OnePointCrossover;
import org.apache.commons.math4.genetics.listener.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.listener.PopulationStatisticsLogger;
import org.apache.commons.math4.genetics.mutation.BinaryMutation;
import org.apache.commons.math4.genetics.selection.TournamentSelection;
import org.apache.commons.math4.genetics.utils.ConsoleLogger;

public class Dimension2FunctionOptimizer {

    public static void main(String[] args) {
        Population<Coordinate> initPopulation = getInitialPopulation();

        Dimension2FunctionOptimizer optimizer = new Dimension2FunctionOptimizer();

        ConvergenceListenerRegistry<Coordinate> convergenceListenerRegistry = ConvergenceListenerRegistry.getInstance();
        convergenceListenerRegistry.addConvergenceListener(new PopulationStatisticsLogger<Coordinate>());
        convergenceListenerRegistry
                .addConvergenceListener(new GraphPlotter("Convergence Stats", "generation", "fitness"));

        optimizer.optimize(initPopulation);
    }

    public void optimize(Population<Coordinate> initial) {

        // initialize a new genetic algorithm
        GeneticAlgorithm<Coordinate> ga = new GeneticAlgorithm<Coordinate>(new OnePointCrossover<Integer, Coordinate>(),
                Constants.CROSSOVER_RATE, new BinaryMutation<Coordinate>(), Constants.AVERAGE_MUTATION_RATE,
                new TournamentSelection<Coordinate>(Constants.TOURNAMENT_SIZE), Constants.ELITISM_RATE);

        // stopping condition
        StoppingCondition<Coordinate> stopCond = new UnchangedBestFitness<Coordinate>(
                Constants.GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS);

        // run the algorithm
        Population<Coordinate> finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        Chromosome<Coordinate> bestFinal = finalPopulation.getFittestChromosome();

        ConsoleLogger.log("*********************************************");
        ConsoleLogger.log("***********Optimization Result***************");
        ConsoleLogger.log("*********************************************");

        ConsoleLogger.log(bestFinal.toString());

    }

    private static Population<Coordinate> getInitialPopulation() {
        Population<Coordinate> population = new ListPopulation<>(Constants.POPULATION_SIZE);
        for (int i = 0; i < Constants.POPULATION_SIZE; i++) {
            population.addChromosome(BinaryChromosome.<Coordinate>randomChromosome(Constants.CHROMOSOME_LENGTH,
                    new Dimension2FitnessFunction(), new Dimension2Decoder()));
        }
        return population;
    }

}
