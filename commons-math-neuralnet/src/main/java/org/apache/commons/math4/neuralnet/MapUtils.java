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

package org.apache.commons.math4.neuralnet;

import java.util.List;

import org.apache.commons.math4.neuralnet.internal.NeuralNetException;

/**
 * Utilities for network maps.
 *
 * @since 3.3
 */
public final class MapUtils {
    /**
     * Class contains only static methods.
     */
    private MapUtils() {}

    /**
     * Computes the quantization error.
     * The quantization error is the average distance between a feature vector
     * and its "best matching unit" (closest neuron).
     *
     * @param data Feature vectors.
     * @param neurons List of neurons to scan.
     * @param distance Distance function.
     * @return the error.
     * @throws IllegalArgumentException if {@code data} is empty.
     */
    public static double computeQuantizationError(Iterable<double[]> data,
                                                  Iterable<Neuron> neurons,
                                                  DistanceMeasure distance) {
        final MapRanking rank = new MapRanking(neurons, distance);

        double d = 0;
        int count = 0;
        for (final double[] f : data) {
            ++count;
            d += distance.applyAsDouble(f, rank.rank(f, 1).get(0).getFeatures());
        }

        if (count == 0) {
            throw new NeuralNetException(NeuralNetException.NO_DATA);
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
     * @throws IllegalArgumentException if {@code data} is empty.
     */
    public static double computeTopographicError(Iterable<double[]> data,
                                                 Network net,
                                                 DistanceMeasure distance) {
        final MapRanking rank = new MapRanking(net, distance);

        int notAdjacentCount = 0;
        int count = 0;
        for (final double[] f : data) {
            ++count;
            final List<Neuron> p = rank.rank(f, 2);
            if (!net.getNeighbours(p.get(0)).contains(p.get(1))) {
                // Increment count if first and second best matching units
                // are not neighbours.
                ++notAdjacentCount;
            }
        }

        if (count == 0) {
            throw new NeuralNetException(NeuralNetException.NO_DATA);
        }

        return ((double) notAdjacentCount) / count;
    }
}
