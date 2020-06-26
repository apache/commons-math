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

package org.apache.commons.math4.ml.neuralnet;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math4.exception.NoDataException;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.neuralnet.twod.NeuronSquareMesh2D;

/**
 * Utilities for network maps.
 *
 * @since 3.3
 */
public class MapUtils {
    /**
     * Class contains only static methods.
     */
    private MapUtils() {}

    /**
     * Computes the <a href="http://en.wikipedia.org/wiki/U-Matrix">
     *  U-matrix</a> of a two-dimensional map.
     *
     * @param map Network.
     * @param distance Function to use for computing the average
     * distance from a neuron to its neighbours.
     * @return the matrix of average distances.
     */
    public static double[][] computeU(NeuronSquareMesh2D map,
                                      DistanceMeasure distance) {
        final int numRows = map.getNumberOfRows();
        final int numCols = map.getNumberOfColumns();
        final double[][] uMatrix = new double[numRows][numCols];

        final Network net = map.getNetwork();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                final Neuron neuron = map.getNeuron(i, j);
                final Collection<Neuron> neighbours = net.getNeighbours(neuron);
                final double[] features = neuron.getFeatures();

                double d = 0;
                int count = 0;
                for (Neuron n : neighbours) {
                    ++count;
                    d += distance.compute(features, n.getFeatures());
                }

                uMatrix[i][j] = d / count;
            }
        }

        return uMatrix;
    }

    /**
     * Computes the "hit" histogram of a two-dimensional map.
     *
     * @param data Feature vectors.
     * @param map Network.
     * @param distance Function to use for determining the best matching unit.
     * @return the number of hits for each neuron in the map.
     */
    public static int[][] computeHitHistogram(Iterable<double[]> data,
                                              NeuronSquareMesh2D map,
                                              DistanceMeasure distance) {
        final HashMap<Neuron, Integer> hit = new HashMap<>();
        final MapRanking rank = new MapRanking(map.getNetwork(), distance);

        for (double[] f : data) {
            final Neuron best = rank.rank(f, 1).get(0);
            final Integer count = hit.get(best);
            if (count == null) {
                hit.put(best, 1);
            } else {
                hit.put(best, count + 1);
            }
        }

        // Copy the histogram data into a 2D map.
        final int numRows = map.getNumberOfRows();
        final int numCols = map.getNumberOfColumns();
        final int[][] histo = new int[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                final Neuron neuron = map.getNeuron(i, j);
                final Integer count = hit.get(neuron);
                if (count == null) {
                    histo[i][j] = 0;
                } else {
                    histo[i][j] = count;
                }
            }
        }

        return histo;
    }

    /**
     * Computes the quantization error.
     * The quantization error is the average distance between a feature vector
     * and its "best matching unit" (closest neuron).
     *
     * @param data Feature vectors.
     * @param neurons List of neurons to scan.
     * @param distance Distance function.
     * @return the error.
     * @throws NoDataException if {@code data} is empty.
     */
    public static double computeQuantizationError(Iterable<double[]> data,
                                                  Iterable<Neuron> neurons,
                                                  DistanceMeasure distance) {
        final MapRanking rank = new MapRanking(neurons, distance);

        double d = 0;
        int count = 0;
        for (double[] f : data) {
            ++count;
            d += distance.compute(f, rank.rank(f, 1).get(0).getFeatures());
        }

        if (count == 0) {
            throw new NoDataException();
        }

        return d / count;
    }

    /**
     * Computes the topographic error.
     * The topographic error is the proportion of data for which first and
     * second best matching units are not adjacent in the map.
     *
     * @param data Feature vectors.
     * @param net Network.
     * @param distance Distance function.
     * @return the error.
     * @throws NoDataException if {@code data} is empty.
     */
    public static double computeTopographicError(Iterable<double[]> data,
                                                 Network net,
                                                 DistanceMeasure distance) {
        final MapRanking rank = new MapRanking(net, distance);

        int notAdjacentCount = 0;
        int count = 0;
        for (double[] f : data) {
            ++count;
            final List<Neuron> p = rank.rank(f, 2);
            if (!net.getNeighbours(p.get(0)).contains(p.get(1))) {
                // Increment count if first and second best matching units
                // are not neighbours.
                ++notAdjacentCount;
            }
        }

        if (count == 0) {
            throw new NoDataException();
        }

        return ((double) notAdjacentCount) / count;
    }
}
