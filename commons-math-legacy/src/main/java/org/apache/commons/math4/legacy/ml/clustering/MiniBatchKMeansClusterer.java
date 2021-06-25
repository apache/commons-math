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

package org.apache.commons.math4.legacy.ml.clustering;

import org.apache.commons.math4.legacy.exception.NullArgumentException;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ml.distance.DistanceMeasure;
import org.apache.commons.math4.legacy.core.Pair;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.ListSampler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Clustering algorithm <a href="https://www.eecs.tufts.edu/~dsculley/papers/fastkmeans.pdf">
 * based on KMeans</a>.
 *
 * @param <T> Type of the points to cluster.
 */
public class MiniBatchKMeansClusterer<T extends Clusterable>
    extends KMeansPlusPlusClusterer<T> {
    /** Batch data size in iteration. */
    private final int batchSize;
    /** Iteration count of initialize the centers. */
    private final int initIterations;
    /** Data size of batch to initialize the centers. */
    private final int initBatchSize;
    /** Maximum number of iterations during which no improvement is occuring. */
    private final int maxNoImprovementTimes;


    /**
     * Build a clusterer.
     *
     * @param k Number of clusters to split the data into.
     * @param maxIterations Maximum number of iterations to run the algorithm for all the points,
     * The actual number of iterationswill be smaller than {@code maxIterations * size / batchSize},
     * where {@code size} is the number of points to cluster.
     * Disabled if negative.
     * @param batchSize Batch size for training iterations.
     * @param initIterations Number of iterations allowed in order to find out the best initial centers.
     * @param initBatchSize Batch size for initializing the clusters centers.
     * A value of {@code 3 * batchSize} should be suitable in most cases.
     * @param maxNoImprovementTimes Maximum number of iterations during which no improvement is occuring.
     * A value of 10 is suitable in most cases.
     * @param measure Distance measure.
     * @param random Random generator.
     * @param emptyStrategy Strategy for handling empty clusters that may appear during algorithm iterations.
     */
    public MiniBatchKMeansClusterer(final int k,
                                    final int maxIterations,
                                    final int batchSize,
                                    final int initIterations,
                                    final int initBatchSize,
                                    final int maxNoImprovementTimes,
                                    final DistanceMeasure measure,
                                    final UniformRandomProvider random,
                                    final EmptyClusterStrategy emptyStrategy) {
        super(k, maxIterations, measure, random, emptyStrategy);

        if (batchSize < 1) {
            throw new NumberIsTooSmallException(batchSize, 1, true);
        }
        if (initIterations < 1) {
            throw new NumberIsTooSmallException(initIterations, 1, true);
        }
        if (initBatchSize < 1) {
            throw new NumberIsTooSmallException(initBatchSize, 1, true);
        }
        if (maxNoImprovementTimes < 1) {
            throw new NumberIsTooSmallException(maxNoImprovementTimes, 1, true);
        }

        this.batchSize = batchSize;
        this.initIterations = initIterations;
        this.initBatchSize = initBatchSize;
        this.maxNoImprovementTimes = maxNoImprovementTimes;
    }

    /**
     * Runs the MiniBatch K-means clustering algorithm.
     *
     * @param points Points to cluster (cannot be {@code null}).
     * @return the clusters.
     * @throws org.apache.commons.math4.legacy.exception.MathIllegalArgumentException
     * if the number of points is smaller than the number of clusters.
     */
    @Override
    public List<CentroidCluster<T>> cluster(final Collection<T> points) {
        // Sanity check.
        NullArgumentException.check(points);
        if (points.size() < getNumberOfClusters()) {
            throw new NumberIsTooSmallException(points.size(), getNumberOfClusters(), false);
        }

        final int pointSize = points.size();
        final int batchCount = pointSize / batchSize + (pointSize % batchSize > 0 ? 1 : 0);
        final int max = getMaxIterations() < 0 ?
            Integer.MAX_VALUE :
            getMaxIterations() * batchCount;

        final List<T> pointList = new ArrayList<>(points);
        List<CentroidCluster<T>> clusters = initialCenters(pointList);

        final ImprovementEvaluator evaluator = new ImprovementEvaluator(batchSize,
                                                                        maxNoImprovementTimes);
        for (int i = 0; i < max; i++) {
            clearClustersPoints(clusters);
            final List<T> batchPoints = ListSampler.sample(getRandomGenerator(), pointList, batchSize);
            // Training step.
            final Pair<Double, List<CentroidCluster<T>>> pair = step(batchPoints, clusters);
            final double squareDistance = pair.getFirst();
            clusters = pair.getSecond();
            // Check whether the training can finished early.
            if (evaluator.converge(squareDistance, pointSize)) {
                break;
            }
        }

        // Add every mini batch points to their nearest cluster.
        clearClustersPoints(clusters);
        for (final T point : points) {
            addToNearestCentroidCluster(point, clusters);
        }

        return clusters;
    }

    /**
     * Helper method.
     *
     * @param clusters Clusters to clear.
     */
    private void clearClustersPoints(final List<CentroidCluster<T>> clusters) {
        for (CentroidCluster<T> cluster : clusters) {
            cluster.getPoints().clear();
        }
    }

    /**
     * Mini batch iteration step.
     *
     * @param batchPoints Points selected for this batch.
     * @param clusters Centers of the clusters.
     * @return the squared distance of all the batch points to the nearest center.
     */
    private Pair<Double, List<CentroidCluster<T>>> step(final List<T> batchPoints,
                                                        final List<CentroidCluster<T>> clusters) {
        // Add every mini batch points to their nearest cluster.
        for (final T point : batchPoints) {
            addToNearestCentroidCluster(point, clusters);
        }
        final List<CentroidCluster<T>> newClusters = adjustClustersCenters(clusters);
        // Add every mini batch points to their nearest cluster again.
        double squareDistance = 0.0;
        for (T point : batchPoints) {
            final double d = addToNearestCentroidCluster(point, newClusters);
            squareDistance += d * d;
        }

        return new Pair<>(squareDistance, newClusters);
    }

    /**
     * Initializes the clusters centers.
     *
     * @param points Points used to initialize the centers.
     * @return clusters with their center initialized.
     */
    private List<CentroidCluster<T>> initialCenters(final List<T> points) {
        final List<T> validPoints = initBatchSize < points.size() ?
            ListSampler.sample(getRandomGenerator(), points, initBatchSize) :
            new ArrayList<>(points);
        double nearestSquareDistance = Double.POSITIVE_INFINITY;
        List<CentroidCluster<T>> bestCenters = null;

        for (int i = 0; i < initIterations; i++) {
            final List<T> initialPoints = (initBatchSize < points.size()) ?
                ListSampler.sample(getRandomGenerator(), points, initBatchSize) :
                new ArrayList<>(points);
            final List<CentroidCluster<T>> clusters = chooseInitialCenters(initialPoints);
            final Pair<Double, List<CentroidCluster<T>>> pair = step(validPoints, clusters);
            final double squareDistance = pair.getFirst();
            final List<CentroidCluster<T>> newClusters = pair.getSecond();
            //Find out a best centers that has the nearest total square distance.
            if (squareDistance < nearestSquareDistance) {
                nearestSquareDistance = squareDistance;
                bestCenters = newClusters;
            }
        }
        return bestCenters;
    }

    /**
     * Adds a point to the cluster whose center is closest.
     *
     * @param point Point to add.
     * @param clusters Clusters.
     * @return the distance between point and the closest center.
     */
    private double addToNearestCentroidCluster(final T point,
                                               final List<CentroidCluster<T>> clusters) {
        double minDistance = Double.POSITIVE_INFINITY;
        CentroidCluster<T> closestCentroidCluster = null;

        // Find cluster closest to the point.
        for (CentroidCluster<T> centroidCluster : clusters) {
            final double distance = distance(point, centroidCluster.getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                closestCentroidCluster = centroidCluster;
            }
        }
        NullArgumentException.check(closestCentroidCluster);
        closestCentroidCluster.addPoint(point);

        return minDistance;
    }

    /**
     * Stopping criterion.
     * The evaluator checks whether improvement occurred during the
     * {@link #maxNoImprovementTimes allowed number of successive iterations}.
     */
    private static final class ImprovementEvaluator {
        /** Batch size. */
        private final int batchSize;
        /** Maximum number of iterations during which no improvement is occuring. */
        private final int maxNoImprovementTimes;
        /**
         * <a href="https://en.wikipedia.org/wiki/Moving_average">
         * Exponentially Weighted Average</a> of the squared
         * diff to monitor the convergence while discarding
         * minibatch-local stochastic variability.
         */
        private double ewaInertia = Double.NaN;
        /** Minimum value of {@link #ewaInertia} during iteration. */
        private double ewaInertiaMin = Double.POSITIVE_INFINITY;
        /** Number of iteration during which {@link #ewaInertia} did not improve. */
        private int noImprovementTimes;

        /**
         * @param batchSize Number of elements for each batch iteration.
         * @param maxNoImprovementTimes Maximum number of iterations during
         * which no improvement is occuring.
         */
        private ImprovementEvaluator(int batchSize,
                                     int maxNoImprovementTimes) {
            this.batchSize = batchSize;
            this.maxNoImprovementTimes = maxNoImprovementTimes;
        }

        /**
         * Stopping criterion.
         *
         * @param squareDistance Total square distance from the batch points
         * to their nearest center.
         * @param pointSize Number of data points.
         * @return {@code true} if no improvement was made after the allowed
         * number of iterations, {@code false} otherwise.
         */
        public boolean converge(final double squareDistance,
                                final int pointSize) {
            final double batchInertia = squareDistance / batchSize;
            if (Double.isNaN(ewaInertia)) {
                ewaInertia = batchInertia;
            } else {
                final double alpha = Math.min(batchSize * 2 / (pointSize + 1), 1);
                ewaInertia = ewaInertia * (1 - alpha) + batchInertia * alpha;
            }

            if (ewaInertia < ewaInertiaMin) {
                // Improved.
                noImprovementTimes = 0;
                ewaInertiaMin = ewaInertia;
            } else {
                // No improvement.
                ++noImprovementTimes;
            }

            return noImprovementTimes >= maxNoImprovementTimes;
        }
    }
}
