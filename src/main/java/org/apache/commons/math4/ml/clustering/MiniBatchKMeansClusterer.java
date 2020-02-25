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

package org.apache.commons.math4.ml.clustering;

import org.apache.commons.math4.exception.ConvergenceException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.ml.clustering.initialization.CentroidInitializer;
import org.apache.commons.math4.ml.clustering.initialization.KMeansPlusPlusCentroidInitializer;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.math4.util.MathUtils;
import org.apache.commons.math4.util.Pair;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.sampling.ListSampler;
import org.apache.commons.rng.simple.RandomSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A very fast clustering algorithm base on KMeans(Refer to Python sklearn.cluster.MiniBatchKMeans)
 * Use a partial points in initialize cluster centers, and mini batch in iterations.
 * It finish in few seconds when clustering millions of data, and has few differences between KMeans.
 * See https://www.eecs.tufts.edu/~dsculley/papers/fastkmeans.pdf
 *
 * @param <T> Type of the points to cluster
 */
public class MiniBatchKMeansClusterer<T extends Clusterable> extends Clusterer<T> {
    private static final DistanceMeasure DEFAULT_DISTANCE_MEASURE = new EuclideanDistance();

    /**
     * The number of clusters.
     */
    private final int k;

    /**
     * The maximum number of iterations.
     */
    private final int maxIterations;

    /**
     * Batch data size in iteration.
     */
    private final int batchSize;
    /**
     * Iteration count of initialize the centers.
     */
    private final int initIterations;
    /**
     * Data size of batch to initialize the centers, default 3*k
     */
    private final int initBatchSize;
    /**
     * Max iterate times when no improvement on step iterations.
     */
    private final int maxNoImprovementTimes;
    /**
     * Random generator for choosing initial centers.
     */
    private final UniformRandomProvider random;

    /**
     * Centroid initial algorithm
     */
    private final CentroidInitializer centroidInitializer;


    /**
     * Build a clusterer.
     *
     * @param k                     the number of clusters to split the data into
     * @param maxIterations         the maximum number of iterations to run the algorithm for.
     *                              If negative, no maximum will be used.
     * @param batchSize             the mini batch size for training iterations.
     * @param initIterations        the iterations to find out the best clusters centers.
     * @param initBatchSize         the mini batch size to initial the clusters centers.
     * @param maxNoImprovementTimes the max iterations times when the square distance has no improvement.
     * @param measure               the distance measure to use
     * @param random                random generator to use for choosing initial centers
     *                              may appear during algorithm iterations
     * @param centroidInitializer   the centroid initializer algorithm
     */
    public MiniBatchKMeansClusterer(final int k, final int maxIterations, final int batchSize, final int initIterations,
                                    final int initBatchSize, final int maxNoImprovementTimes,
                                    final DistanceMeasure measure, final UniformRandomProvider random,
                                    final CentroidInitializer centroidInitializer) {
        super(measure);
        if (k < 1) throw new NumberIsTooSmallException(k, 1, true);
        else this.k = k;
        this.maxIterations = maxIterations;
        if (batchSize < 1) throw new NumberIsTooSmallException(batchSize, 1, true);
        else this.batchSize = batchSize;
        if (initIterations < 1) throw new NumberIsTooSmallException(initIterations, 1, true);
        else this.initIterations = initIterations;
        if (initBatchSize < 1) throw new NumberIsTooSmallException(initBatchSize, 1, true);
        else this.initBatchSize = initBatchSize;
        if (maxNoImprovementTimes < 1) throw new NumberIsTooSmallException(maxNoImprovementTimes, 1, true);
        else this.maxNoImprovementTimes = maxNoImprovementTimes;
        this.random = random;
        this.centroidInitializer = centroidInitializer;
    }

    /**
     * Build a clusterer with default distance measure and so on.
     * <p>The default initIterations will be 3 as a empiric value.</p>
     * <p>The default initBatchSize will be 3 times to the batchSize as a empiric value.</p>
     * <p>The default maxImprovementTimes will be 10 as a empiric value.</p>
     * <p>The euclidean distance will be used as default distance measure.</p>
     * <p>The KMeans++ will be used as default centroid initialize algorithm.</p>
     *
     * @param k             the number of clusters to split the data into
     * @param maxIterations the maximum number of iterations to run the algorithm for.
     *                      If negative, no maximum will be used.
     * @param batchSize     the mini batch size for training iterations.
     * @param random        random generator to use for choosing initial centers
     *                      may appear during algorithm iterations
     */
    public MiniBatchKMeansClusterer(final int k, final int maxIterations, final int batchSize, final UniformRandomProvider random) {
        this(k, maxIterations, batchSize, 3, batchSize * 3, 10,
                DEFAULT_DISTANCE_MEASURE, random, new KMeansPlusPlusCentroidInitializer(DEFAULT_DISTANCE_MEASURE, random));
    }


    /**
     * Build a clusterer with default values on most of the parameters.
     * <p>The default maxIterations will be 100 as a empiric value.</p>
     * <p>The default batchSize will be 100 as a empiric value.</p>
     * <p>The default initIterations will be 3 as a empiric value.</p>
     * <p>The default initBatchSize will be 3 times to the batchSize.</p>
     * <p>The default maxImprovementTimes will be 10.</p>
     * <p>The euclidean distance will be used as default distance measure.</p>
     * <p>The KMeans++ will be used as default centroid initialize algorithm.</p>
     * <p>The MT_64 will be used as default Random Provider.</p>
     *
     * @param k the number of clusters to split the data into.
     */
    public MiniBatchKMeansClusterer(final int k) {
        this(k, 100, 100, RandomSource.create(RandomSource.MT_64));
    }

    /**
     * Runs the MiniBatch K-means clustering algorithm.
     *
     * @param points the points to cluster
     * @return a list of clusters containing the points
     * @throws MathIllegalArgumentException if the data points are null or the number
     *                                      of clusters is larger than the number of data points
     */
    @Override
    public List<CentroidCluster<T>> cluster(Collection<T> points) throws MathIllegalArgumentException, ConvergenceException {
        // sanity checks
        MathUtils.checkNotNull(points);

        // number of clusters has to be smaller or equal the number of data points
        if (points.size() < k) {
            throw new NumberIsTooSmallException(points.size(), k, false);
        }

        int pointSize = points.size();
        int batchCount = pointSize / batchSize + ((pointSize % batchSize > 0) ? 1 : 0);
        int max = this.maxIterations < 0 ? Integer.MAX_VALUE : (this.maxIterations * batchCount);
        MiniBatchImprovementEvaluator evaluator = new MiniBatchImprovementEvaluator();
        List<CentroidCluster<T>> clusters = initialCenters(points);
        for (int i = 0; i < max; i++) {
            //Clear points in clusters
            clearClustersPoints(clusters);
            //Random sampling a mini batch of points.
            List<T> batchPoints = randomMiniBatch(points, batchSize);
            // Processing the mini batch training step
            Pair<Double, List<CentroidCluster<T>>> pair = step(batchPoints, clusters);
            double squareDistance = pair.getFirst();
            clusters = pair.getSecond();
            // Evaluate the training can finished early.
            if (evaluator.convergence(squareDistance, pointSize)) break;
        }
        clearClustersPoints(clusters);
        //Add every mini batch points to their nearest cluster.
        for (T point : points) {
            addToNearestCentroidCluster(point, clusters);
        }
        return clusters;
    }

    /**
     * clear clustered points
     *
     * @param clusters The clusters to clear
     */
    private void clearClustersPoints(List<CentroidCluster<T>> clusters) {
        for (CentroidCluster<T> cluster : clusters) {
            cluster.getPoints().clear();
        }
    }

    /**
     * Mini batch iteration step
     *
     * @param batchPoints The mini batch points.
     * @param clusters    The cluster centers.
     * @return Square distance of all the batch points to the nearest center, and newly clusters.
     */
    private Pair<Double, List<CentroidCluster<T>>> step(
            List<T> batchPoints,
            List<CentroidCluster<T>> clusters) {
        //Add every mini batch points to their nearest cluster.
        for (T point : batchPoints) {
            addToNearestCentroidCluster(point, clusters);
        }
        List<CentroidCluster<T>> newClusters = new ArrayList<>(clusters.size());
        //Refresh then cluster centroid.
        for (CentroidCluster<T> cluster : clusters) {
            Clusterable newCenter;
            if (cluster.getPoints().isEmpty()) {
                newCenter = new DoublePoint(ClusterUtils.getPointFromLargestVarianceCluster(clusters, this.getDistanceMeasure(), random).getPoint());
            } else {
                newCenter = ClusterUtils.centroidOf(cluster.getPoints(), cluster.getCenter().getPoint().length);
            }
            newClusters.add(new CentroidCluster<>(newCenter));
        }
        // Add every mini batch points to their nearest cluster again.
        double squareDistance = 0.0;
        for (T point : batchPoints) {
            double d = addToNearestCentroidCluster(point, newClusters);
            squareDistance += d * d;
        }
        return new Pair<>(squareDistance, newClusters);
    }

    /**
     * Get a mini batch of points
     *
     * @param points    all the points
     * @param batchSize the mini batch size
     * @return mini batch of all the points
     */
    private List<T> randomMiniBatch(Collection<T> points, int batchSize) {
        ArrayList<T> list = new ArrayList<>(points);
        ListSampler.shuffle(random, list);
        return list.subList(0, batchSize);
    }

    /**
     * Initial cluster centers with multiply iterations, find out the best.
     *
     * @param points Points use to initial the cluster centers.
     * @return Clusters with center
     */
    private List<CentroidCluster<T>> initialCenters(Collection<T> points) {
        List<T> validPoints = initBatchSize < points.size() ? randomMiniBatch(points, initBatchSize) : new ArrayList<>(points);
        double nearestSquareDistance = Double.POSITIVE_INFINITY;
        List<CentroidCluster<T>> bestCenters = null;
        for (int i = 0; i < initIterations; i++) {
            List<T> initialPoints = (initBatchSize < points.size()) ? randomMiniBatch(points, initBatchSize) : new ArrayList<>(points);
            List<CentroidCluster<T>> clusters = centroidInitializer.selectCentroids(initialPoints, k);
            Pair<Double, List<CentroidCluster<T>>> pair = step(validPoints, clusters);
            double squareDistance = pair.getFirst();
            List<CentroidCluster<T>> newClusters = pair.getSecond();
            //Find out a best centers that has the nearest total square distance.
            if (squareDistance < nearestSquareDistance) {
                nearestSquareDistance = squareDistance;
                bestCenters = newClusters;
            }
        }
        return bestCenters;
    }

    /**
     * Add a point to the cluster which the nearest center belong to.
     *
     * @param point    The point to add.
     * @param clusters The clusters to add to.
     * @return The distance to nearest center.
     */
    private double addToNearestCentroidCluster(T point, List<CentroidCluster<T>> clusters) {
        double minDistance = Double.POSITIVE_INFINITY;
        CentroidCluster<T> nearestCentroidCluster = null;
        for (CentroidCluster<T> centroidCluster : clusters) {
            double distance = distance(point, centroidCluster.getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                nearestCentroidCluster = centroidCluster;
            }
        }
        assert nearestCentroidCluster != null;
        nearestCentroidCluster.addPoint(point);
        return minDistance;
    }

    /**
     * The Evaluator to evaluate whether the iteration should finish where square has no improvement for appointed times.
     */
    class MiniBatchImprovementEvaluator {
        private Double ewaInertia = null;
        private double ewaInertiaMin = Double.POSITIVE_INFINITY;
        private int noImprovementTimes = 0;

        /**
         * Evaluate whether the iteration should finish where square has no improvement for appointed times
         *
         * @param squareDistance the total square distance of the mini batch points to their nearest center.
         * @param pointSize      size of the the data points.
         * @return true if no improvement for appointed times, otherwise false
         */
        public boolean convergence(double squareDistance, int pointSize) {
            double batchInertia = squareDistance / batchSize;
            if (ewaInertia == null) {
                ewaInertia = batchInertia;
            } else {
                // Refer to sklearn, pointSize+1 maybe intent to avoid the div/0 error,
                // but java double does not have a div/0 error
                double alpha = batchSize * 2.0 / (pointSize + 1);
                alpha = Math.min(alpha, 1.0);
                ewaInertia = ewaInertia * (1 - alpha) + batchInertia * alpha;
            }

            // Improved
            if (ewaInertia < ewaInertiaMin) {
                noImprovementTimes = 0;
                ewaInertiaMin = ewaInertia;
            } else {
                // No improvement
                noImprovementTimes++;
            }
            // Has no improvement continuous for many times
            return noImprovementTimes >= maxNoImprovementTimes;
        }
    }
}
