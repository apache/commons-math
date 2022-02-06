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
package org.apache.commons.math4.examples.ga.mathfunctions.legacy;

import org.apache.commons.math3.genetics.BinaryChromosome;
import org.apache.commons.math3.genetics.BinaryMutation;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;

/**
 * This class represents an optimizer for a 2-dimensional math function using
 * the legacy genetic algorithm.
 */
public final class LegacyMathFunctionOptimizer {

    /** length of chromosome per dimension. **/
    private static final int CHROMOSOME_LENGTH_PER_DIMENSION = 12;

    /** encoding for console logger. **/
    private static final String ENCODING = "UTF-8";

    /**
     * Optimizes the initial population using legacy genetic algorithm.
     * @param dimension                               dimension of problem domain
     * @param crossoverRate                           rate of crossover
     * @param mutationRate                            rate of mutation
     * @param elitismRate                             rate of elitism
     * @param tournamentSize                          size of tournament
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
        final GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(new OnePointCrossover<>(), crossoverRate,
                new BinaryMutation(), mutationRate, new TournamentSelection(tournamentSize));

        // stopping condition
        final StoppingCondition stopCond = new UnchangedBestFitness(generationCountWithUnchangedBestFitness);

        // run the algorithm
        final Population finalPopulation = geneticAlgorithm
                .evolve(getInitialPopulation(dimension, populationSize, elitismRate), stopCond);

        // best chromosome from the final population
        final Chromosome bestFinal = finalPopulation.getFittestChromosome();

        System.out.println("best=" + bestFinal.toString());
    }

    private static Population getInitialPopulation(int dimension, int populationSize, double elitismRate) {
        final Population population = new ElitisticListPopulation(populationSize, elitismRate);
        for (int i = 0; i < populationSize; i++) {
            population.addChromosome(new LegacyBinaryChromosome(
                    BinaryChromosome.randomBinaryRepresentation(dimension * CHROMOSOME_LENGTH_PER_DIMENSION)));
        }
        return population;
    }

}
