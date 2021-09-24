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
package org.apache.commons.math4.examples.genetics.tsp.legacy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.RandomKey;
import org.apache.commons.math3.genetics.RandomKeyMutation;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math4.examples.genetics.tsp.commons.City;
import org.apache.commons.math4.examples.genetics.tsp.utils.Constants;
import org.apache.commons.math4.genetics.exception.GeneticException;

/**
 * This class represents the tsp optimizer based on legacy implementation of
 * Genetic Algorithm.
 */
public class TSPOptimizerLegacy {

    /**
     * Main method to initiate optimization.
     * @param args arguments
     */
    public static void main(String[] args) {
        try {
            final Population initPopulation = getInitialPopulation(Constants.CITIES);

            final TSPOptimizerLegacy optimizer = new TSPOptimizerLegacy();

            optimizer.optimize(initPopulation, Constants.CITIES);

            Thread.sleep(5000);

        } catch (InterruptedException e) {
            throw new GeneticException(e);
        }
    }

    /**
     * Optimizes the tsp problem using legacy GA.
     * @param initial initial population
     * @param cities  cities
     */
    public void optimize(Population initial, List<City> cities) {

        // initialize a new genetic algorithm
        final GeneticAlgorithm ga = new GeneticAlgorithm(new OnePointCrossover<Integer>(), Constants.CROSSOVER_RATE,
                new RandomKeyMutation(), Constants.AVERAGE_MUTATION_RATE,
                new TournamentSelection(Constants.TOURNAMENT_SIZE));

        // stopping condition
        final StoppingCondition stopCond = new UnchangedBestFitness(
                Constants.GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS);

        // run the algorithm
        final Population finalPopulation = ga.evolve(initial, stopCond);

        // best chromosome from the final population
        @SuppressWarnings("unchecked")
        final RandomKey<City> bestFinal = (RandomKey<City>) finalPopulation.getFittestChromosome();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, Constants.ENCODING))) {
            writer.write("*********************************************");
            writer.newLine();
            writer.write("***********Optimization Result***************");
            writer.write(bestFinal.toString());
        } catch (IOException e) {
            throw new GeneticException(e);
        }
    }

    private static Population getInitialPopulation(List<City> cities) {
        final Population simulationPopulation = new ElitisticListPopulation(Constants.POPULATION_SIZE, .25);

        for (int i = 0; i < Constants.POPULATION_SIZE; i++) {
            simulationPopulation.addChromosome(new TSPChromosome(RandomKey.randomPermutation(cities.size()), cities));
        }

        return simulationPopulation;
    }

}
