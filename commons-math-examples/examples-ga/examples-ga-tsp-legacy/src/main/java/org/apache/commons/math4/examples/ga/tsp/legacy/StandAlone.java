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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "legacy-tsp-optimizer", mixinStandardHelpOptions = true, version = "legacy-tsp-optimizer 1.0")
public class StandAlone implements Runnable {

    /** list of cities. **/
    public static final List<City> CITIES = Collections.unmodifiableList(
            Arrays.asList(new City[] {new City(1, 0, 0), new City(2, 1, 0), new City(3, 2, 0), new City(4, 3, 0),
                new City(5, 3, 1), new City(6, 3, 2), new City(7, 3, 3), new City(8, 2, 3), new City(9, 1, 3),
                new City(10, 0, 3), new City(11, 1, 2), new City(12, 2, 2), new City(13, 2, 1), new City(14, 1, 1)}));

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
     * {@link LegacyTSPOptimizer}.
     */
    @Override
    public void run() {

        // validate all input options.
        validateInput();

        final LegacyTSPOptimizer optimizer = new LegacyTSPOptimizer();

        optimizer.optimize(CITIES, crossoverRate, mutationRate, elitismRate, tournamentSize,
                generationsEvolvedWithUnchangedBestFitness, populationSize);

    }

    private void validateInput() {
        if (this.tournamentSize < 1) {
            throw new IllegalArgumentException("Tournament size should be > 0.");
        }
        if (populationSize < 2) {
            throw new IllegalArgumentException("Population size should be > 1.");
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
