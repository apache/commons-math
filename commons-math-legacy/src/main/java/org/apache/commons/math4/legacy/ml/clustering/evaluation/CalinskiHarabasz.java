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

package org.apache.commons.math4.legacy.ml.clustering.evaluation;

import org.apache.commons.math4.legacy.exception.InsufficientDataException;
import org.apache.commons.math4.legacy.ml.clustering.Cluster;
import org.apache.commons.math4.legacy.ml.clustering.ClusterEvaluator;
import org.apache.commons.math4.legacy.ml.clustering.Clusterable;
import org.apache.commons.math4.legacy.core.MathArrays;

import java.util.Collection;
import java.util.List;

/**
 * Compute the Calinski and Harabasz score.
 * <p>
 * It is also known as the Variance Ratio Criterion.
 * <p>
 * The score is defined as ratio between the within-cluster dispersion and
 * the between-cluster dispersion.
 *
 * @see <a href="https://www.tandfonline.com/doi/abs/10.1080/03610927408827101">A dendrite method for cluster
 * analysis</a>
 */
public class CalinskiHarabasz implements ClusterEvaluator {
    /** {@inheritDoc} */
    @Override
    public double score(List<? extends Cluster<? extends Clusterable>> clusters) {
        final int dimension = dimensionOfClusters(clusters);
        final double[] centroid = meanOfClusters(clusters, dimension);

        double intraDistanceProduct = 0.0;
        double extraDistanceProduct = 0.0;
        for (Cluster<? extends Clusterable> cluster : clusters) {
            // Calculate the center of the cluster.
            double[] clusterCentroid = mean(cluster.getPoints(), dimension);
            for (Clusterable p : cluster.getPoints()) {
                // Increase the intra distance sum
                intraDistanceProduct += covariance(clusterCentroid, p.getPoint());
            }
            // Increase the extra distance sum
            extraDistanceProduct += cluster.getPoints().size() * covariance(centroid, clusterCentroid);
        }

        final int pointCount = countAllPoints(clusters);
        final int clusterCount = clusters.size();
        // Return the ratio of the intraDistranceProduct to extraDistanceProduct
        return intraDistanceProduct == 0.0 ? 1.0 :
                (extraDistanceProduct * (pointCount - clusterCount) /
                        (intraDistanceProduct * (clusterCount - 1)));
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBetterScore(double a,
                                 double b) {
        return a > b;
    }

    /**
     * Calculate covariance of two double array.
     * <pre>
     *   covariance = sum((p1[i]-p2[i])^2)
     * </pre>
     *
     * @param p1 Double array
     * @param p2 Double array
     * @return covariance of two double array
     */
    private double covariance(double[] p1, double[] p2) {
        MathArrays.checkEqualLength(p1, p2);
        double sum = 0;
        for (int i = 0; i < p1.length; i++) {
            final double dp = p1[i] - p2[i];
            sum += dp * dp;
        }
        return sum;
    }

    /**
     * Calculate the mean of all the points.
     *
     * @param points    A collection of points
     * @param dimension The dimension of each point
     * @return The mean value.
     */
    private double[] mean(final Collection<? extends Clusterable> points, final int dimension) {
        final double[] centroid = new double[dimension];
        for (final Clusterable p : points) {
            final double[] point = p.getPoint();
            for (int i = 0; i < centroid.length; i++) {
                centroid[i] += point[i];
            }
        }
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= points.size();
        }
        return centroid;
    }

    /**
     * Calculate the mean of all the points in the clusters.
     *
     * @param clusters  A collection of clusters.
     * @param dimension The dimension of each point.
     * @return The mean value.
     */
    private double[] meanOfClusters(final Collection<? extends Cluster<? extends Clusterable>> clusters, final int dimension) {
        final double[] centroid = new double[dimension];
        int allPointsCount = 0;
        for (Cluster<? extends Clusterable> cluster : clusters) {
            for (Clusterable p : cluster.getPoints()) {
                double[] point = p.getPoint();
                for (int i = 0; i < centroid.length; i++) {
                    centroid[i] += point[i];
                }
                allPointsCount++;
            }
        }
        for (int i = 0; i < centroid.length; i++) {
            centroid[i] /= allPointsCount;
        }
        return centroid;
    }

    /**
     * Count all the points in collection of cluster.
     *
     * @param clusters collection of cluster
     * @return points count
     */
    private int countAllPoints(final Collection<? extends Cluster<? extends Clusterable>> clusters) {
        int pointCount = 0;
        for (Cluster<? extends Clusterable> cluster : clusters) {
            pointCount += cluster.getPoints().size();
        }
        return pointCount;
    }

    /**
     * Detect the dimension of points in the clusters.
     *
     * @param clusters collection of cluster
     * @return The dimension of the first point in clusters
     */
    private int dimensionOfClusters(final Collection<? extends Cluster<? extends Clusterable>> clusters) {
        // Iteration and find out the first point.
        for (Cluster<? extends Clusterable> cluster : clusters) {
            for (Clusterable p : cluster.getPoints()) {
                return p.getPoint().length;
            }
        }
        // Throw exception if there is no point.
        throw new InsufficientDataException();
    }
}
