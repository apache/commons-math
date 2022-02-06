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
package org.apache.commons.math4.examples.ga.tsp.legacy;

import java.util.List;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.RandomKey;
import org.apache.commons.math3.genetics.RandomKeyMutation;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;

/**
 * This class represents the tsp optimizer based on legacy implementation of
 * Genetic Algorithm.
 */
public class LegacyTSPOptimizer {
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
        final GeneticAlgorithm ga = new GeneticAlgorithm(new OnePointCrossover<Integer>(), crossoverRate,
                new RandomKeyMutation(), mutationRate, new TournamentSelection(tournamentSize));

        // stopping condition
        final StoppingCondition stopCond = new UnchangedBestFitness(generationCountWithUnchangedBestFitness);

        // run the algorithm
        final Population finalPopulation = ga.evolve(getInitialPopulation(cities, populationSize, elitismRate),
                stopCond);

        // best chromosome from the final population
        @SuppressWarnings("unchecked")
        final RandomKey<City> bestFinal = (RandomKey<City>) finalPopulation.getFittestChromosome();

        System.out.println("best=" + bestFinal.toString());
    }

    private static Population getInitialPopulation(List<City> cities, int populationSize, double elitismRate) {
        final Population simulationPopulation = new ElitisticListPopulation(populationSize, elitismRate);

        for (int i = 0; i < populationSize; i++) {
            simulationPopulation.addChromosome(new TSPChromosome(RandomKey.randomPermutation(cities.size()), cities));
        }

        return simulationPopulation;
    }
}
