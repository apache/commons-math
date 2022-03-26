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
package org.apache.commons.math4.examples.ga.mathfunctions.adaptive;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "adaptive-mathfunction-optimizer", mixinStandardHelpOptions = true,
        version = "adaptive-mathfunction-optimizer 1.0")
public class StandAlone implements Runnable {

    /** number of dimension. **/
    @Option(names = "-d", paramLabel = "DIMENSION", required = true, description = "Dimension of problem domain.")
    private int dimension;

    /** size of tournament. **/
    @Option(names = "-t", paramLabel = "TOURNAMENT_SIZE", required = true, description = "Tournament size.")
    private int tournamentSize;

    /** size of population. **/
    @Option(names = "-p", paramLabel = "POPULATION_SIZE", required = true, description = "Size of population.")
    private int populationSize;

    /** minimum rate of crossover. **/
    @Option(names = "-c", paramLabel = "MIN_CROSSOVER_RATE",
            description = "Crossover rate (default: ${DEFAULT-VALUE}).")
    private double minCrossoverRate = 0;

    /** maximum rate of crossover. **/
    @Option(names = "-C", paramLabel = "MAX_CROSSOVER_RATE",
            description = "Crossover rate (default: ${DEFAULT-VALUE}).")
    private double maxCrossoverRate = 1.0;

    /** minimum rate of mutation. **/
    @Option(names = "-m", paramLabel = "MIN_MUTATION_RATE",
            description = "Minimum Mutation rate (default: ${DEFAULT-VALUE}).")
    private double minMutationRate = 0;

    /** maximum rate of mutation. **/
    @Option(names = "-M", paramLabel = "MAX_MUTATION_RATE",
            description = "Maximum Mutation rate (default: ${DEFAULT-VALUE}).")
    private double maxMutationRate = 0.1;

    /** rate of elitism. **/
    @Option(names = "-e", paramLabel = "ELITISM_RATE", description = "Elitism rate (default: ${DEFAULT-VALUE}).")
    private double elitismRate = 0.25;

    /** number of generations with unchanged best fitness. **/
    @Option(names = "-g", paramLabel = "GENERATIONS_EVOLVED_WITH_UNCHANGED_BEST_FITNESS",
            description = "No of generations evolved with unchanged best fitness (default: ${DEFAULT-VALUE}).")
    private int generationsEvolvedWithUnchangedBestFitness = 50;

    public static void main(String[] args) {
        CommandLine.run(new StandAlone(), args);
    }

    /**
     * This method is responsible for validating input and invoking
     * {@link AdaptiveMathFunctionOptimizer}.
     */
    @Override
    public void run() {

        // validate all input options.
        validateInput();

        final AdaptiveMathFunctionOptimizer optimizer = new AdaptiveMathFunctionOptimizer();

        optimizer.optimize(dimension, minCrossoverRate, maxCrossoverRate, minMutationRate, maxMutationRate, elitismRate,
                tournamentSize, generationsEvolvedWithUnchangedBestFitness, populationSize);

    }

    private void validateInput() {
        if (this.dimension < 1) {
            throw new IllegalArgumentException("Dimension should be > 0.");
        }
        if (this.tournamentSize < 1) {
            throw new IllegalArgumentException("Tournament size should be > 0.");
        }
        if (populationSize < 2) {
            throw new IllegalArgumentException("Population size should be > 1.");
        }
        if (minCrossoverRate > 1) {
            throw new IllegalArgumentException("Minimum crossover rate should be <= 1.");
        }
        if (maxCrossoverRate > 1) {
            throw new IllegalArgumentException("Maximum crossover rate should be <= 1.");
        }
        if (maxCrossoverRate < minCrossoverRate) {
            throw new IllegalArgumentException("Minimum crossover rate should be lesser than maximum crossover rate.");
        }
        if (minMutationRate > 1) {
            throw new IllegalArgumentException("Minimum mutation rate should be <= 1.");
        }
        if (maxMutationRate > 1) {
            throw new IllegalArgumentException("Maximum mutation rate should be <= 1.");
        }
        if (minMutationRate > maxMutationRate) {
            throw new IllegalArgumentException("Minimum mutation rate should be lesser than maximum mutation rate.");
        }
        if (elitismRate >= 1) {
            throw new IllegalArgumentException("Elitism rate should be < 1.");
        }
        if (generationsEvolvedWithUnchangedBestFitness < 1) {
            throw new IllegalArgumentException(
                    "Number of generations evolved with unchanged best fitness should be >= 1.");
        }
    }
}
