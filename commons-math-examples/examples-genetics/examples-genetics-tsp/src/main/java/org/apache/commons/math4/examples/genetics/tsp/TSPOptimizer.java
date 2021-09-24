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
package org.apache.commons.math4.examples.genetics.tsp;

import java.util.List;

import org.apache.commons.math4.examples.genetics.tsp.commons.City;
import org.apache.commons.math4.examples.genetics.tsp.utils.Constants;
import org.apache.commons.math4.examples.genetics.tsp.utils.GraphPlotter;
import org.apache.commons.math4.genetics.GeneticAlgorithm;
import org.apache.commons.math4.genetics.chromosome.RealValuedChromosome;
import org.apache.commons.math4.genetics.convergencecond.StoppingCondition;
import org.apache.commons.math4.genetics.convergencecond.UnchangedBestFitness;
import org.apache.commons.math4.genetics.crossover.OnePointCrossover;
import org.apache.commons.math4.genetics.decoder.RandomKeyDecoder;
import org.apache.commons.math4.genetics.exception.GeneticException;
import org.apache.commons.math4.genetics.listener.ConvergenceListenerRegistry;
import org.apache.commons.math4.genetics.listener.PopulationStatisticsLogger;
import org.apache.commons.math4.genetics.mutation.RealValuedMutation;
import org.apache.commons.math4.genetics.population.ListPopulation;
import org.apache.commons.math4.genetics.population.Population;
import org.apache.commons.math4.genetics.selection.TournamentSelection;
import org.apache.commons.math4.genetics.utils.ChromosomeRepresentationUtils;
import org.apache.commons.math4.genetics.utils.ConsoleLogger;

/**
 * This class represents the optimizer for traveling salesman problem.
 */
public class TSPOptimizer {

    /**
     * Main method to initiate the optimization process.
     * @param args arguments
     */
    public static void main(String[] args) {
        try {
            final Population<List<City>> initPopulation = getInitialPopulation(Constants.CITIES);

            final TSPOptimizer optimizer = new TSPOptimizer();

            final ConvergenceListenerRegistry<List<City>> convergenceListenerRegistry = ConvergenceListenerRegistry
                    .getInstance();
            convergenceListenerRegistry
                    .addConvergenceListener(new PopulationStatisticsLogger<List<City>>(Constants.ENCODING));
            convergenceListenerRegistry
                    .addConvergenceListener(new GraphPlotter("Convergence", "generation", "total-distance"));

            optimizer.optimizeSGA(initPopulation, Constants.CITIES);

            Thread.sleep(5000);

        } catch (InterruptedException e) {
            throw new GeneticException(e);
        }
    }

    /**
     * Optimizes the tsp problem.
     * @param initial initial population
     * @param cities  cities
     */
    public void optimizeSGA(Population<List<City>> initial, List<City> cities) {

        // initialize a new genetic algorithm
        final GeneticAlgorithm<List<City>> ga = new GeneticAlgorithm<>(new OnePointCrossover<Integer, List<City>>(),
                Constants.CROSSOVER_RATE, new RealValuedMutation<List<City>>(), Constants.AVERAGE_MUTATION_RATE,
                new TournamentSelection<List<City>>(Constants.TOURNAMENT_SIZE));

        // stopping condition
        final StoppingCondition<List<City>> stopCond = new UnchangedBestFitness<>(
                Constants.GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS);

        // run the algorithm
        final Population<List<City>> finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        final RealValuedChromosome<List<City>> bestFinal = (RealValuedChromosome<List<City>>) finalPopulation
                .getFittestChromosome();

        final ConsoleLogger consoleLogger = ConsoleLogger.getInstance(Constants.ENCODING);
        consoleLogger.log("*********************************************");
        consoleLogger.log("***********Optimization Result***************");

        consoleLogger.log(bestFinal.decode().toString());
        consoleLogger.log("Best Fitness: %.6f", bestFinal.evaluate());

    }

    private static Population<List<City>> getInitialPopulation(List<City> cities) {
        final Population<List<City>> simulationPopulation = new ListPopulation<>(Constants.POPULATION_SIZE);

        for (int i = 0; i < Constants.POPULATION_SIZE; i++) {
            simulationPopulation.addChromosome(new RealValuedChromosome<>(
                    ChromosomeRepresentationUtils.randomPermutation(Constants.CHROMOSOME_LENGTH),
                    new TSPFitnessFunction(), new RandomKeyDecoder<City>(cities)));
        }

        return simulationPopulation;
    }

}
