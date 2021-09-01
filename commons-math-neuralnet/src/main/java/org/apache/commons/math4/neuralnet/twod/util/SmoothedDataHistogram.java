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

package org.apache.commons.math4.neuralnet.twod.util;

import java.util.List;
import org.apache.commons.math4.neuralnet.internal.NeuralNetException;
import org.apache.commons.math4.neuralnet.DistanceMeasure;
import org.apache.commons.math4.neuralnet.MapRanking;
import org.apache.commons.math4.neuralnet.Neuron;
import org.apache.commons.math4.neuralnet.twod.NeuronSquareMesh2D;

/**
 * Visualization of high-dimensional data projection on a 2D-map.
 * The method is described in
 * <blockquote>
 *  <em>Using Smoothed Data Histograms for Cluster Visualization in Self-Organizing Maps</em>
 *  <br>
 *  by Elias Pampalk, Andreas Rauber and Dieter Merkl.
 * </blockquote>
 * @since 3.6
 */
public class SmoothedDataHistogram implements MapDataVisualization {
    /** Smoothing parameter. */
    private final int smoothingBins;
    /** Distance. */
    private final DistanceMeasure distance;
    /** Normalization factor. */
    private final double membershipNormalization;

    /**
     * @param smoothingBins Number of bins.
     * @param distance Distance.
     */
    public SmoothedDataHistogram(int smoothingBins,
                                 DistanceMeasure distance) {
        this.smoothingBins = smoothingBins;
        this.distance = distance;

        double sum = 0;
        for (int i = 0; i < smoothingBins; i++) {
            sum += smoothingBins - i;
        }

        this.membershipNormalization = 1d / sum;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the size of the {@code map} is
     * smaller than the number of {@link #SmoothedDataHistogram(int,DistanceMeasure)
     * smoothing bins}.
     */
    @Override
    public double[][] computeImage(NeuronSquareMesh2D map,
                                   Iterable<double[]> data) {
        final int nR = map.getNumberOfRows();
        final int nC = map.getNumberOfColumns();

        final int mapSize = nR * nC;
        if (mapSize < smoothingBins) {
            throw new NeuralNetException(NeuralNetException.TOO_SMALL,
                                         mapSize, smoothingBins);
        }

        final LocationFinder finder = new LocationFinder(map);
        final MapRanking rank = new MapRanking(map.getNetwork(), distance);

        // Histogram bins.
        final double[][] histo = new double[nR][nC];

        for (final double[] sample : data) {
            final List<Neuron> sorted = rank.rank(sample);
            for (int i = 0; i < smoothingBins; i++) {
                final LocationFinder.Location loc = finder.getLocation(sorted.get(i));
                final int row = loc.getRow();
                final int col = loc.getColumn();
                histo[row][col] += (smoothingBins - i) * membershipNormalization;
            }
        }

        return histo;
    }
}
