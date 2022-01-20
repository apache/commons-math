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

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "legacy-mathfunction-optimizer", mixinStandardHelpOptions = true,
        version = "legacy-mathfunction-optimizer 1.0")
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

    /** rate of crossover. **/
    @Option(names = "-c", paramLabel = "CROSSOVER_RATE", description = "Crossover rate (default: ${DEFAULT-VALUE}).")
    private double crossoverRate = 1.0;

    /** rate of elitism. **/
    @Option(names = "-e", paramLabel = "ELITISM_RATE", description = "Elitism rate (default: ${DEFAULT-VALUE}).")
    private double elitismRate = 0.25;

    /** rate of mutation. **/
    @Option(names = "-m", paramLabel = "MUTATION_RATE", description = "Mutation rate (default: ${DEFAULT-VALUE}).")
    private double mutationRate = 0.01;

    /** number of generations with unchanged best fitness. **/
    @Option(names = "-g", paramLabel = "GENERATIONS_EVOLVED_WITH_UNCHANGED_BEST_FITNESS",
            description = "No of generations evolved with unchanged best fitness (default: ${DEFAULT-VALUE}).")
    private int generationsEvolvedWithUnchangedBestFitness = 50;

    public static void main(String[] args) {
        CommandLine.run(new StandAlone(), args);
    }

    /**
     * This method is responsible for validating input and invoking
     * {@link LegacyMathFunctionOptimizer}.
     */
    @Override
    public void run() {

        // validate all input options.
        validateInput();

        final LegacyMathFunctionOptimizer optimizer = new LegacyMathFunctionOptimizer();

        optimizer.optimize(dimension, crossoverRate, mutationRate, elitismRate, tournamentSize,
                generationsEvolvedWithUnchangedBestFitness, populationSize);

    }

    private void validateInput() {
        if (this.dimension < 1) {
            throw new IllegalArgumentException("Dimension should be greater than 0.");
        }
        if (this.tournamentSize < 1) {
            throw new IllegalArgumentException("Tournament size should be greater than 1.");
        }
        if (populationSize < 2) {
            throw new IllegalArgumentException("Population size should be greater than 9.");
        }
        if (crossoverRate > 1) {
            throw new IllegalArgumentException("Crossover rate should be <= 1.");
        }
        if (elitismRate >= 1) {
            throw new IllegalArgumentException("Elitism rate should be < 1.");
        }
        if (mutationRate > 1) {
            throw new IllegalArgumentException("Mutation rate should be <= 1.");
        }
        if (generationsEvolvedWithUnchangedBestFitness < 1) {
            throw new IllegalArgumentException(
                    "Number of generations evolved with unchanged best fitness should be >= 1.");
        }
    }
}
