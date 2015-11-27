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

package org.apache.commons.math3.ml.neuralnet.sofm;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.math3.Retry;
import org.apache.commons.math3.RetryRunner;
import org.apache.commons.math3.ml.neuralnet.sofm.KohonenTrainingTask;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests for {@link KohonenTrainingTask}
 */
@RunWith(RetryRunner.class)
public class KohonenTrainingTaskTest {
    @Test
    public void testTravellerSalesmanSquareTourSequentialSolver() {
        // Cities (in optimal travel order).
        final City[] squareOfCities = new City[] {
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

        // Seed that allows the unit test to always succeed.
        final long seed = 1245632379L;

        final TravellingSalesmanSolver solver = new TravellingSalesmanSolver(squareOfCities, 2, seed);
        // printSummary("before.travel.seq.dat", solver);
        final Runnable task = solver.createSequentialTask(15000);
        task.run();

        // All update attempts must be successful in the absence of concurrency.
        Assert.assertEquals(solver.getUpdateRatio(), 1, 0d);

        // printSummary("after.travel.seq.dat", solver);
        final City[] result = solver.getCityList();
        Assert.assertEquals(squareOfCities.length,
                            uniqueCities(result).size());
        final double ratio = computeTravelDistance(squareOfCities) / computeTravelDistance(result);
        Assert.assertEquals(1, ratio, 1e-1); // We do not require the optimal travel.
    }

    // Test can sometimes fail: Run several times.
    @Test
    @Retry
    public void testTravellerSalesmanSquareTourParallelSolver() throws ExecutionException {
        // Cities (in optimal travel order).
        final City[] squareOfCities = new City[] {
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

        // Seed that allows the unit test to always succeed.
        final long seed = 534712311L;

        final TravellingSalesmanSolver solver = new TravellingSalesmanSolver(squareOfCities, 2, seed);
        // printSummary("before.travel.par.dat", solver);

        // Parallel execution.
        final ExecutorService service = Executors.newCachedThreadPool();
        final int numProcs = Runtime.getRuntime().availableProcessors();
        final Runnable[] tasks = solver.createParallelTasks(numProcs, 5000);
        final List<Future<?>> execOutput = new ArrayList<Future<?>>();
        // Run tasks.
        for (Runnable r : tasks) {
            execOutput.add(service.submit(r));
        }
        // Wait for completion (ignoring return value).
        try {
            for (Future<?> f : execOutput) {
                f.get();
            }
        } catch (InterruptedException ignored) {}
        // Terminate all threads.
        service.shutdown();

        if (numProcs > 1) {
            // We expect that some update attempts will be concurrent.
            Assert.assertTrue(solver.getUpdateRatio() < 1);
        }

        // printSummary("after.travel.par.dat", solver);
        final City[] result = solver.getCityList();
        Assert.assertEquals(squareOfCities.length,
                            uniqueCities(result).size());
        final double ratio = computeTravelDistance(squareOfCities) / computeTravelDistance(result);
        Assert.assertEquals(1, ratio, 1e-1); // We do not require the optimal travel.
    }

    /**
     * Creates a map of the travel suggested by the solver.
     *
     * @param solver Solver.
     * @return a 4-columns table: {@code <x (neuron)> <y (neuron)> <x (city)> <y (city)>}.
     */
    private String travelCoordinatesTable(TravellingSalesmanSolver solver) {
        final StringBuilder s = new StringBuilder();
        for (double[] c : solver.getCoordinatesList()) {
            s.append(c[0]).append(" ").append(c[1]).append(" ");
            final City city = solver.getClosestCity(c[0], c[1]);
            final double[] cityCoord = city.getCoordinates();
            s.append(cityCoord[0]).append(" ").append(cityCoord[1]).append(" ");
            s.append("   # ").append(city.getName()).append("\n");
        }
        return s.toString();
    }

    /**
     * Compute the distance covered by the salesman, including
     * the trip back (from the last to first city).
     *
     * @param cityList List of cities visited during the travel.
     * @return the total distance.
     */
    private Collection<City> uniqueCities(City[] cityList) {
        final Set<City> unique = new HashSet<City>();
        for (City c : cityList) {
            unique.add(c);
        }
        return unique;
    }

    /**
     * Compute the distance covered by the salesman, including
     * the trip back (from the last to first city).
     *
     * @param cityList List of cities visited during the travel.
     * @return the total distance.
     */
    private double computeTravelDistance(City[] cityList) {
        double dist = 0;
        for (int i = 0; i < cityList.length; i++) {
            final double[] currentCoord = cityList[i].getCoordinates();
            final double[] nextCoord = cityList[(i + 1) % cityList.length].getCoordinates();

            final double xDiff = currentCoord[0] - nextCoord[0];
            final double yDiff = currentCoord[1] - nextCoord[1];

            dist += FastMath.sqrt(xDiff * xDiff + yDiff * yDiff);
        }

        return dist;
    }

    /**
     * Prints a summary of the current state of the solver to the
     * given filename.
     *
     * @param filename File.
     * @param solver Solver.
     */
    @SuppressWarnings("unused")
    private void printSummary(String filename,
                              TravellingSalesmanSolver solver) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(filename);
            out.println(travelCoordinatesTable(solver));

            final City[] result = solver.getCityList();
            out.println("# Number of unique cities: " + uniqueCities(result).size());
            out.println("# Travel distance: " + computeTravelDistance(result));
        } catch (Exception e) {
            // Do nothing.
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}
