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

import org.apache.commons.math4.ga.GeneticAlgorithm;
import org.apache.commons.math4.ga.chromosome.RealValuedChromosome;
import org.apache.commons.math4.ga.convergence.StoppingCondition;
import org.apache.commons.math4.ga.convergence.UnchangedBestFitness;
import org.apache.commons.math4.ga.crossover.OnePointCrossover;
import org.apache.commons.math4.ga.decoder.RandomKeyDecoder;
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
public final class TSPOptimizer {
    /** instance of logger. **/
    private final Logger logger = LoggerFactory.getLogger(TSPOptimizer.class);

    /**
     * Optimizes the TSP problem.
     * @param cities                                  list of cities
     * @param crossoverRate                           rate of crossover
     * @param mutationRate                            rate of mutation
     * @param elitismRate                             rate of elitism
     * @param tournamentSize                          size of tournament
     * @param generationCountWithUnchangedBestFitness no of generations evolved with
     *                                                unchanged best fitness
     * @param populationSize                          size of population
     */
    public void optimize(List<City> cities,
            double crossoverRate,
            double mutationRate,
            double elitismRate,
            int tournamentSize,
            int generationCountWithUnchangedBestFitness,
            int populationSize) {

        // initialize a new genetic algorithm
        final GeneticAlgorithm<List<City>> ga = new GeneticAlgorithm<>(new OnePointCrossover<Integer, List<City>>(),
                crossoverRate, new RealValuedMutation<>(), mutationRate, new TournamentSelection<>(tournamentSize),
                elitismRate, new PopulationStatisticsLogger<>());

        // stopping condition
        final StoppingCondition<List<City>> stopCond = new UnchangedBestFitness<>(
                generationCountWithUnchangedBestFitness);

        // run the algorithm
        final Population<List<City>> finalPopulation = ga.evolve(getInitialPopulation(cities, populationSize), stopCond,
                Runtime.getRuntime().availableProcessors());

        // best chromosome from the final population
        final RealValuedChromosome<List<City>> bestFinal = (RealValuedChromosome<List<City>>) finalPopulation
                .getFittestChromosome();

        logger.info(bestFinal.decode().toString());
    }

    private static Population<List<City>> getInitialPopulation(List<City> cities, int populationSize) {
        final Population<List<City>> simulationPopulation = new ListPopulation<>(populationSize);

        for (int i = 0; i < populationSize; i++) {
            simulationPopulation.addChromosome(
                    new RealValuedChromosome<>(ChromosomeRepresentationUtils.randomPermutation(cities.size()),
                            new TSPFitnessFunction(), new RandomKeyDecoder<City>(cities)));
        }

        return simulationPopulation;
    }

}
