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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.math4.exception.ConvergenceException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.ml.clustering.initialization.CentroidInitializer;
import org.apache.commons.math4.ml.clustering.initialization.KMeansPlusPlusCentroidInitializer;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.math4.stat.descriptive.moment.Variance;
import org.apache.commons.math4.util.MathUtils;
import org.apache.commons.rng.simple.RandomSource;
import org.apache.commons.rng.UniformRandomProvider;

/**
 * Clustering algorithm based on David Arthur and Sergei Vassilvitski k-means++ algorithm.
 * @param <T> type of the points to cluster
 * @see <a href="http://en.wikipedia.org/wiki/K-means%2B%2B">K-means++ (wikipedia)</a>
 * @since 3.2
 */
public class KMeansPlusPlusClusterer<T extends Clusterable> extends Clusterer<T> {

    /** Strategies to use for replacing an empty cluster. */
    public enum EmptyClusterStrategy {

        /** Split the cluster with largest distance variance. */
        LARGEST_VARIANCE,

        /** Split the cluster with largest number of points. */
        LARGEST_POINTS_NUMBER,

        /** Create a cluster around the point farthest from its centroid. */
        FARTHEST_POINT,

        /** Generate an error. */
        ERROR

    }

    /** The number of clusters. */
    private final int k;

    /** The maximum number of iterations. */
    private final int maxIterations;

    /** Random generator for choosing initial centers. */
    private final UniformRandomProvider random;

    /** Selected strategy for empty clusters. */
    private final EmptyClusterStrategy emptyStrategy;

    /** Clusters centroids initializer. */
    private final CentroidInitializer centroidInitializer;

    /** Build a clusterer.
     * <p>
     * The default strategy for handling empty clusters that may appear during
     * algorithm iterations is to split the cluster with largest distance variance.
     * <p>
     * The euclidean distance will be used as default distance measure.
     *
     * @param k the number of clusters to split the data into
     */
    public KMeansPlusPlusClusterer(final int k) {
        this(k, -1);
    }

    /** Build a clusterer.
     * <p>
     * The default strategy for handling empty clusters that may appear during
     * algorithm iterations is to split the cluster with largest distance variance.
     * <p>
     * The euclidean distance will be used as default distance measure.
     *
     * @param k the number of clusters to split the data into
     * @param maxIterations the maximum number of iterations to run the algorithm for.
     *   If negative, no maximum will be used.
     */
    public KMeansPlusPlusClusterer(final int k, final int maxIterations) {
        this(k, maxIterations, new EuclideanDistance());
    }

    /** Build a clusterer.
     * <p>
     * The default strategy for handling empty clusters that may appear during
     * algorithm iterations is to split the cluster with largest distance variance.
     *
     * @param k the number of clusters to split the data into
     * @param maxIterations the maximum number of iterations to run the algorithm for.
     *   If negative, no maximum will be used.
     * @param measure the distance measure to use
     */
    public KMeansPlusPlusClusterer(final int k, final int maxIterations, final DistanceMeasure measure) {
        this(k, maxIterations, measure, RandomSource.create(RandomSource.MT_64));
    }

    /** Build a clusterer.
     * <p>
     * The default strategy for handling empty clusters that may appear during
     * algorithm iterations is to split the cluster with largest distance variance.
     *
     * @param k the number of clusters to split the data into
     * @param maxIterations the maximum number of iterations to run the algorithm for.
     *   If negative, no maximum will be used.
     * @param measure the distance measure to use
     * @param random random generator to use for choosing initial centers
     */
    public KMeansPlusPlusClusterer(final int k, final int maxIterations,
                                   final DistanceMeasure measure,
                                   final UniformRandomProvider random) {
        this(k, maxIterations, measure, random, EmptyClusterStrategy.LARGEST_VARIANCE);
    }

    /** Build a clusterer.
     *
     * @param k the number of clusters to split the data into
     * @param maxIterations the maximum number of iterations to run the algorithm for.
     *   If negative, no maximum will be used.
     * @param measure the distance measure to use
     * @param random random generator to use for choosing initial centers
     * @param emptyStrategy strategy to use for handling empty clusters that
     * may appear during algorithm iterations
     */
    public KMeansPlusPlusClusterer(final int k, final int maxIterations,
                                   final DistanceMeasure measure,
                                   final UniformRandomProvider random,
                                   final EmptyClusterStrategy emptyStrategy) {
        super(measure);
        this.k             = k;
        this.maxIterations = maxIterations;
        this.random        = random;
        this.emptyStrategy = emptyStrategy;
        // Use K-means++ to choose the initial centers.
        this.centroidInitializer = new KMeansPlusPlusCentroidInitializer(measure, random);
    }

    /**
     * Return the number of clusters this instance will use.
     * @return the number of clusters
     */
    public int getK() {
        return k;
    }

    /**
     * Returns the maximum number of iterations this instance will use.
     * @return the maximum number of iterations, or -1 if no maximum is set
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Runs the K-means++ clustering algorithm.
     *
     * @param points the points to cluster
     * @return a list of clusters containing the points
     * @throws org.apache.commons.math4.exception.MathIllegalArgumentException
     * if the data points are null or the number of clusters is larger than the
     * number of data points
     * @throws ConvergenceException if an empty cluster is encountered and the
     * empty cluster strategy is set to {@link EmptyClusterStrategy#ERROR}
     */
    @Override
    public List<CentroidCluster<T>> cluster(final Collection<T> points) {
        // sanity checks
        MathUtils.checkNotNull(points);

        // number of clusters has to be smaller or equal the number of data points
        if (points.size() < k) {
            throw new NumberIsTooSmallException(points.size(), k, false);
        }

        // create the initial clusters
        List<CentroidCluster<T>> clusters = centroidInitializer.selectCentroids(points, k);

        // create an array containing the latest assignment of a point to a cluster
        // no need to initialize the array, as it will be filled with the first assignment
        int[] assignments = new int[points.size()];
        assignPointsToClusters(clusters, points, assignments);

        // iterate through updating the centers until we're done
        final int max = (maxIterations < 0) ? Integer.MAX_VALUE : maxIterations;
        for (int count = 0; count < max; count++) {
            boolean hasEmptyCluster = clusters.stream().anyMatch(cluster->cluster.getPoints().isEmpty());
            List<CentroidCluster<T>> newClusters = adjustClustersCenters(clusters);
            int changes = assignPointsToClusters(newClusters, points, assignments);
            clusters = newClusters;

            // if there were no more changes in the point-to-cluster assignment
            // and there are no empty clusters left, return the current clusters
            if (changes == 0 && !hasEmptyCluster) {
                return clusters;
            }
        }
        return clusters;
    }

    /**
     * @return the random generator
     */
    UniformRandomProvider getRandomGenerator() {
        return random;
    }

    /**
     * @return the {@link EmptyClusterStrategy}
     */
    EmptyClusterStrategy getEmptyClusterStrategy() {
        return emptyStrategy;
    }

    /**
     * @return the CentroidInitializer
     */
    CentroidInitializer getCentroidInitializer() {
        return centroidInitializer;
    }

    /**
     * Adjust the clusters's centers with means of points
     * @param clusters the origin clusters
     * @return adjusted clusters with center points
     */
    List<CentroidCluster<T>> adjustClustersCenters(List<CentroidCluster<T>> clusters) {
        List<CentroidCluster<T>> newClusters = new ArrayList<>();
        for (final CentroidCluster<T> cluster : clusters) {
            final Clusterable newCenter;
            if (cluster.getPoints().isEmpty()) {
                switch (emptyStrategy) {
                    case LARGEST_VARIANCE :
                        newCenter = getPointFromLargestVarianceCluster(clusters);
                        break;
                    case LARGEST_POINTS_NUMBER :
                        newCenter = getPointFromLargestNumberCluster(clusters);
                        break;
                    case FARTHEST_POINT :
                        newCenter = getFarthestPoint(clusters);
                        break;
                    default :
                        throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
                }
            } else {
                newCenter = cluster.centroid();
            }
            newClusters.add(new CentroidCluster<>(newCenter));
        }
        return newClusters;
    }

    /**
     * Adds the given points to the closest {@link Cluster}.
     *
     * @param clusters the {@link Cluster}s to add the points to
     * @param points the points to add to the given {@link Cluster}s
     * @param assignments points assignments to clusters
     * @return the number of points assigned to different clusters as the iteration before
     */
    private int assignPointsToClusters(final List<CentroidCluster<T>> clusters,
                                       final Collection<T> points,
                                       final int[] assignments) {
        int assignedDifferently = 0;
        int pointIndex = 0;
        for (final T p : points) {
            int clusterIndex = getNearestCluster(clusters, p);
            if (clusterIndex != assignments[pointIndex]) {
                assignedDifferently++;
            }

            CentroidCluster<T> cluster = clusters.get(clusterIndex);
            cluster.addPoint(p);
            assignments[pointIndex++] = clusterIndex;
        }

        return assignedDifferently;
    }

    /**
     * Get a random point from the {@link Cluster} with the largest distance variance.
     *
     * @param clusters the {@link Cluster}s to search
     * @return a random point from the selected cluster
     * @throws ConvergenceException if clusters are all empty
     */
    private T getPointFromLargestVarianceCluster(final Collection<CentroidCluster<T>> clusters) {
        double maxVariance = Double.NEGATIVE_INFINITY;
        Cluster<T> selected = null;
        for (final CentroidCluster<T> cluster : clusters) {
            if (!cluster.getPoints().isEmpty()) {

                // compute the distance variance of the current cluster
                final Clusterable center = cluster.getCenter();
                final Variance stat = new Variance();
                for (final T point : cluster.getPoints()) {
                    stat.increment(distance(point, center));
                }
                final double variance = stat.getResult();

                // select the cluster with the largest variance
                if (variance > maxVariance) {
                    maxVariance = variance;
                    selected = cluster;
                }

            }
        }

        // did we find at least one non-empty cluster ?
        if (selected == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
        }

        // extract a random point from the cluster
        final List<T> selectedPoints = selected.getPoints();
        return selectedPoints.remove(random.nextInt(selectedPoints.size()));

    }

    /**
     * Get a random point from the {@link Cluster} with the largest number of points
     *
     * @param clusters the {@link Cluster}s to search
     * @return a random point from the selected cluster
     * @throws ConvergenceException if clusters are all empty
     */
    private T getPointFromLargestNumberCluster(final Collection<? extends Cluster<T>> clusters) {
        int maxNumber = 0;
        Cluster<T> selected = null;
        for (final Cluster<T> cluster : clusters) {

            // get the number of points of the current cluster
            final int number = cluster.getPoints().size();

            // select the cluster with the largest number of points
            if (number > maxNumber) {
                maxNumber = number;
                selected = cluster;
            }

        }

        // did we find at least one non-empty cluster ?
        if (selected == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
        }

        // extract a random point from the cluster
        final List<T> selectedPoints = selected.getPoints();
        return selectedPoints.remove(random.nextInt(selectedPoints.size()));

    }

    /**
     * Get the point farthest to its cluster center
     *
     * @param clusters the {@link Cluster}s to search
     * @return point farthest to its cluster center
     * @throws ConvergenceException if clusters are all empty
     */
    private T getFarthestPoint(final Collection<CentroidCluster<T>> clusters) {
        double maxDistance = Double.NEGATIVE_INFINITY;
        Cluster<T> selectedCluster = null;
        int selectedPoint = -1;
        for (final CentroidCluster<T> cluster : clusters) {

            // get the farthest point
            final Clusterable center = cluster.getCenter();
            final List<T> points = cluster.getPoints();
            for (int i = 0; i < points.size(); ++i) {
                final double distance = distance(points.get(i), center);
                if (distance > maxDistance) {
                    maxDistance     = distance;
                    selectedCluster = cluster;
                    selectedPoint   = i;
                }
            }

        }

        // did we find at least one non-empty cluster ?
        if (selectedCluster == null) {
            throw new ConvergenceException(LocalizedFormats.EMPTY_CLUSTER_IN_K_MEANS);
        }

        return selectedCluster.getPoints().remove(selectedPoint);

    }

    /**
     * Returns the nearest {@link Cluster} to the given point
     *
     * @param clusters the {@link Cluster}s to search
     * @param point the point to find the nearest {@link Cluster} for
     * @return the index of the nearest {@link Cluster} to the given point
     */
    private int getNearestCluster(final Collection<CentroidCluster<T>> clusters, final T point) {
        double minDistance = Double.MAX_VALUE;
        int clusterIndex = 0;
        int minCluster = 0;
        for (final CentroidCluster<T> c : clusters) {
            final double distance = distance(point, c.getCenter());
            if (distance < minDistance) {
                minDistance = distance;
                minCluster = clusterIndex;
            }
            clusterIndex++;
        }
        return minCluster;
    }
}
