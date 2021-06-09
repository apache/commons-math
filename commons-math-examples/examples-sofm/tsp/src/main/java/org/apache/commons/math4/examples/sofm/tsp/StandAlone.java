/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math4.examples.sofm.tsp;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import org.apache.commons.math4.neuralnet.twod.NeuronSquareMesh2D;

/**
 * Application class.
 */
@Command(description = "Run the application",
         mixinStandardHelpOptions = true)
public class StandAlone implements Callable<Void> {
    @Option(names = { "-n" }, paramLabel = "neuronsPerCity",
            description = "Average number of neurons per city (default: ${DEFAULT-VALUE}).")
    private double _neuronsPerCity = 2.2;
    @Option(names = { "-s" }, paramLabel = "numSamples",
            description = "Number of samples for the training (default: ${DEFAULT-VALUE}).")
    private long _numSamples = 2000L;
    @Option(names = { "-j" }, paramLabel = "numJobs",
            description = "Number of concurrent tasks (default: ${DEFAULT-VALUE}).")
    private int _numJobs = Runtime.getRuntime().availableProcessors();
    @Option(names = { "-m" }, paramLabel = "maxTrials",
            description = "Maximal number of trials (default: ${DEFAULT-VALUE}).")
    private int _maxTrials = 10;
    @Option(names = { "-o" }, paramLabel = "outputFile", required = true,
            description = "Output file name.")
    private String _outputFile = null;

    /**
     * Program entry point.
     *
     * @param args Command line arguments and options.
     */
    public static void main(String[] args) {
        CommandLine.call(new StandAlone(), args);
    }

    @Override
    public Void call() throws Exception {
        // Cities (in optimal travel order).
        final City[] cities = new City[] {
            new City("o0", 0, 0),
            new City("o1", 1, 0),
            new City("o2", 2, 0),
            new City("o3", 3, 0),
            new City("o4", 3, 1),
            new City("o5", 3, 2),
            new City("o6", 3, 3),
            new City("o7", 2, 3),
            new City("o8", 1, 3),
            new City("o9", 0, 3),
            new City("i3", 1, 2),
            new City("i2", 2, 2),
            new City("i1", 2, 1),
            new City("i0", 1, 1),
        };

        final UniformRandomProvider rng = RandomSource.create(RandomSource.KISS);
        City[] best = null;
        int maxCities = 0;
        double minDist = Double.POSITIVE_INFINITY;

        int count = 0;
        while (count++ < _maxTrials) {
            final City[] travel = TravellingSalesmanSolver.solve(cities,
                                                                 _neuronsPerCity,
                                                                 _numSamples,
                                                                 _numJobs,
                                                                 rng);
            final int numCities = City.unique(travel).size();
            if (numCities > maxCities) {
                best = travel;
                maxCities = numCities;
            }

            if (numCities == cities.length) {
                final double dist = computeDistance(travel);
                if (dist < minDist) {
                    minDist = dist;
                    best = travel;
                }
            }
        }

        printSummary(_outputFile, best, computeDistance(cities));

        return null;
    }
    /**
     * Compute the distance covered by the salesman, including
     * the trip back (from the last to first city).
     *
     * @param cityList List of cities visited during the travel.
     * @return the total distance.
     */
    private static double computeDistance(City[] cityList) {
        double dist = 0;
        for (int i = 0; i < cityList.length; i++) {
            final double[] currentCoord = cityList[i].getCoordinates();
            final double[] nextCoord = cityList[(i + 1) % cityList.length].getCoordinates();

            final double xDiff = currentCoord[0] - nextCoord[0];
            final double yDiff = currentCoord[1] - nextCoord[1];

            dist += Math.sqrt(xDiff * xDiff + yDiff * yDiff);
        }

        return dist;
    }

    /**
     * Prints a summary of the current state of the solver to the
     * given file name.
     *
     * @param fileName File.
     * @param travel Solution.
     * @param optimalDistance Length of shortest path.
     * @throws UnsupportedEncodingException If UTF-8 encoding does not exist.
     * @throws FileNotFoundException If the file cannot be created.
     */
    private static void printSummary(String fileName,
                                     City[] travel,
                                     double optimalDistance) throws FileNotFoundException, UnsupportedEncodingException {
        try (final PrintWriter out = new PrintWriter(fileName, StandardCharsets.UTF_8.name())) {
            out.println("# Number of unique cities: " + City.unique(travel).size());
            out.println("# Travel distance: " + computeDistance(travel));
            out.println("# Optimal travel distance: " + optimalDistance);

            for (int i = 0; i < travel.length; i++) {
                final City c = travel[i];
                final double[] coord = c.getCoordinates();
                out.println(coord[0] + " " + coord[1] + " # " + c.getName());
            }
        }
    }
}
