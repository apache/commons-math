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
package org.apache.commons.math4.examples.ga.parallel.mathfunctions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "mathfunction-optimizer", mixinStandardHelpOptions = true, version = "mathfunction-optimizer 1.0")
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
    /** number of generations with unchanged best fitness. **/
    @Option(names = "-g", paramLabel = "GENERATIONS_EVOLVED_WITH_UNCHANGED_BEST_FITNESS",
            description = "No of generations evolved with unchanged best fitness (default: ${DEFAULT-VALUE}).")
    private int generationsEvolvedWithUnchangedBestFitness = 50;
    /** indicates the absolute file path where output would be stored. **/
    @Option(names = {"-o", "--output"}, required = true, paramLabel = "OUTPUT_FILE_PATH")
    private String output;
    /** instance of logger. **/
    private final Logger logger = LoggerFactory.getLogger(StandAlone.class);

    public static void main(String[] args) {
        CommandLine.run(new StandAlone(), args);
    }

    /**
     * This method is responsible for validating input and then invoking math
     * function optimizer.
     */
    @Override
    public void run() {

        // validate all input options.
        validateInput();

        try (PrintWriter writer = new PrintWriter(new File(output), Charset.defaultCharset().name())) {
            writer.println("Optimization Result:");
            writer.println(new ParallelMathFunctionOptimizer().optimize(dimension, tournamentSize,
                    generationsEvolvedWithUnchangedBestFitness, populationSize));
        } catch (FileNotFoundException e) {
            logger.error("Error while writing to file", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("Encoding not supported for writing to output file", e);
        }
    }

    private void validateInput() {
        if (this.dimension < 1) {
            throw new IllegalArgumentException("Dimension should be > 0.");
        }
        if (this.tournamentSize < 1) {
            throw new IllegalArgumentException("Tournament size should be > 0.");
        }
        if (populationSize <= 1) {
            throw new IllegalArgumentException("Population size should be > 1.");
        }
        if (generationsEvolvedWithUnchangedBestFitness < 1) {
            throw new IllegalArgumentException(
                    "Number of generations evolved with unchanged best fitness should be >= 1.");
        }
        File outFile = new File(output);
        if (outFile.exists() && !outFile.delete()) {
            throw new IllegalArgumentException("Existing output file could not be deleted.");
        }
    }
}
