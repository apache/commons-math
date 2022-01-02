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
package org.apache.commons.math4.examples.ga.tsp;

import java.util.List;

import org.apache.commons.math4.examples.ga.tsp.commons.City;
import org.apache.commons.math4.examples.ga.tsp.utils.Constants;
import org.apache.commons.math4.examples.ga.tsp.utils.GraphPlotter;
import org.apache.commons.math4.ga.GeneticAlgorithm;
import org.apache.commons.math4.ga.chromosome.RealValuedChromosome;
import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.convergence.UnchangedBestFitness;
import org.apache.commons.math4.ga.crossover.OnePointCrossover;
import org.apache.commons.math4.ga.decoder.RandomKeyDecoder;
import org.apache.commons.math4.ga.internal.exception.GeneticException;
import org.apache.commons.math4.ga.listener.ConvergenceListenerRegistry;
import org.apache.commons.math4.ga.listener.PopulationStatisticsLogger;
import org.apache.commons.math4.ga.mutation.RealValuedMutation;
import org.apache.commons.math4.ga.population.ListPopulation;
import org.apache.commons.math4.ga.population.Population;
import org.apache.commons.math4.ga.selection.TournamentSelection;
import org.apache.commons.math4.ga.utils.ChromosomeRepresentationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the optimizer for traveling salesman problem.
 */
public class TSPOptimizer {

    /** instance of logger. **/
    private Logger logger = LoggerFactory.getLogger(TSPOptimizer.class);

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
            convergenceListenerRegistry.addConvergenceListener(new PopulationStatisticsLogger<>());
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

        logger.info("*********************************************");
        logger.info("***********Optimization Result***************");

        logger.info(bestFinal.decode().toString());

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
