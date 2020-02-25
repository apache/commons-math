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
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.math4.stat.descriptive.moment.Variance;
import org.apache.commons.rng.UniformRandomProvider;

import java.util.Collection;
import java.util.List;

/**
 * Common functions used in clustering
 */
public class ClusterUtils {
    /**
     * Use only for static
     */
    private ClusterUtils() {
    }

    public static final DistanceMeasure DEFAULT_MEASURE = new EuclideanDistance();

    /**
     * Predict which cluster is best for the point
     *
     * @param clusters cluster to predict into
     * @param point    point to predict
     * @param measure  distance measurer
     * @param <T>      type of cluster point
     * @return the cluster which has nearest center to the point
     */
    public static <T extends Clusterable> CentroidCluster<T> predict(List<CentroidCluster<T>> clusters, Clusterable point, DistanceMeasure measure) {
        double minDistance = Double.POSITIVE_INFINITY;
        CentroidCluster<T> nearestCluster = null;
        for (CentroidCluster<T> cluster : clusters) {
            double distance = measure.compute(point.getPoint(), cluster.getCenter().getPoint());
            if (distance < minDistance) {
                minDistance = distance;
                nearestCluster = cluster;
            }
        }
        return nearestCluster;
    }

    /**
     * Predict which cluster is best for the point
     *
     * @param clusters cluster to predict into
     * @param point    point to predict
     * @param <T>      type of cluster point
     * @return the cluster which has nearest center to the point
     */
    public static <T extends Clusterable> CentroidCluster<T> predict(List<CentroidCluster<T>> clusters, Clusterable point) {
        return predict(clusters, point, DEFAULT_MEASURE);
    }

    /**
     * Computes the centroid for a set of points.
     *
     * @param points    the set of points
     * @param dimension the point dimension
     * @return the computed centroid for the set of points
     */
    public static <T extends Clusterable> Clusterable centroidOf(final Collection<T> points, final int dimension) {
        final double[] centroid = new double[dimension];
        for (final T p : points) {
            final double[] point = p.getPoint();
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] += point[i];
            }
        }
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= points.size();
        }
        return new DoublePoint(centroid);
    }


    /**
     * Get a random point from the {@link Cluster} with the largest distance variance.
     *
     * @param clusters the {@link Cluster}s to search
     * @param measure  DistanceMeasure
     * @param random   Random generator
     * @return a random point from the selected cluster
     * @throws ConvergenceException if clusters are all empty
     */
    public static <T extends Clusterable> T getPointFromLargestVarianceCluster(final Collection<CentroidCluster<T>> clusters,
                                                                               final DistanceMeasure measure,
                                                                               final UniformRandomProvider random)
            throws ConvergenceException {
        double maxVariance = Double.NEGATIVE_INFINITY;
        Cluster<T> selected = null;
        for (final CentroidCluster<T> cluster : clusters) {
            if (!cluster.getPoints().isEmpty()) {
                // compute the distance variance of the current cluster
                final Clusterable center = cluster.getCenter();
                final Variance stat = new Variance();
                for (final T point : cluster.getPoints()) {
                    stat.increment(measure.compute(point.getPoint(), center.getPoint()));
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
}
