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
package org.apache.commons.math4.examples.ga.mathfunctions.dimension2.legacy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.math3.genetics.BinaryChromosome;
import org.apache.commons.math3.genetics.BinaryMutation;
import org.apache.commons.math3.genetics.Chromosome;
import org.apache.commons.math3.genetics.ElitisticListPopulation;
import org.apache.commons.math3.genetics.GeneticAlgorithm;
import org.apache.commons.math3.genetics.OnePointCrossover;
import org.apache.commons.math3.genetics.Population;
import org.apache.commons.math3.genetics.StoppingCondition;
import org.apache.commons.math3.genetics.TournamentSelection;
import org.apache.commons.math4.examples.ga.mathfunctions.utils.Constants;
import org.apache.commons.math4.ga.internal.exception.GeneticException;

/**
 * This class represents an optimizer for a 2-dimensional math function using
 * the legacy genetic algorithm.
 */
public class Dimension2FunctionOptimizerLegacy {

    /** number of dimension. **/
    private static final int DIMENSION = 2;

    /** size of tournament. **/
    private static final int TOURNAMENT_SIZE = 3;

    /**
     * Optimizes the 2-dimensional fitness function.
     * @param args arguments
     */
    public static void main(String[] args) {
        final Population initPopulation = getInitialPopulation();
        final Dimension2FunctionOptimizerLegacy simulation = new Dimension2FunctionOptimizerLegacy();

        simulation.optimize(initPopulation);
    }

    /**
     * Optimizes the initial population using legacy genetic algorithm.
     * @param initial initial {@link Population}
     */
    public void optimize(Population initial) {

        // initialize a new genetic algorithm
        final GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(new OnePointCrossover<>(),
                Constants.CROSSOVER_RATE, new BinaryMutation(), Constants.AVERAGE_MUTATION_RATE,
                new TournamentSelection(TOURNAMENT_SIZE));

        // stopping condition
        final StoppingCondition stopCond = new UnchangedBestFitness(
                Constants.GENERATION_COUNT_WITH_UNCHANGED_BEST_FUTNESS);

        // run the algorithm
        final Population finalPopulation = geneticAlgorithm.evolve(initial, stopCond);

        // best chromosome from the final population
        final Chromosome bestFinal = finalPopulation.getFittestChromosome();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, Constants.ENCODING))) {
            writer.write("*********************************************");
            writer.newLine();
            writer.write("***********Optimization Result***************");
            writer.write(bestFinal.toString());
        } catch (IOException e) {
            throw new GeneticException(e);
        }
    }

    /**
     * Generates the initial population.
     * @return initial population
     */
    private static Population getInitialPopulation() {
        final Population population = new ElitisticListPopulation(Constants.POPULATION_SIZE_PER_DIMENSION,
                Constants.ELITISM_RATE);
        for (int i = 0; i < Constants.POPULATION_SIZE_PER_DIMENSION; i++) {
            population.addChromosome(new LegacyBinaryChromosome(BinaryChromosome
                    .randomBinaryRepresentation(DIMENSION * Constants.CHROMOSOME_LENGTH_PER_DIMENSION)));
        }
        return population;
    }

}
