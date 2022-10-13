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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.math4.legacy.exception.NumberIsTooSmallException;
import org.apache.commons.math4.legacy.ml.distance.DistanceMeasure;
import org.apache.commons.math4.legacy.stat.descriptive.moment.VectorialMean;

/**
 * Implementation of k-means++ algorithm.
 * It is based on
 * <blockquote>
 *  Elkan, Charles.
 *  "Using the triangle inequality to accelerate k-means."
 *  ICML. Vol. 3. 2003.
 * </blockquote>
 *
 * <p>
 * Algorithm uses triangle inequality to speed up computation, by reducing
 * the amount of distances calculations.  Towards the last iterations of
 * the algorithm, points which already assigned to some cluster are unlikely
 * to move to a new cluster; updates of cluster centers are also usually
 * relatively small.
 * Triangle inequality is thus used to determine the cases where distance
 * computation could be skipped since center move only a little, without
 * affecting points partitioning.
 *
 * <p>
 * For initial centers seeding, we apply the algorithm described in
 * <blockquote>
 *  Arthur, David, and Sergei Vassilvitskii.
 *  "k-means++: The advantages of careful seeding."
 *  Proceedings of the eighteenth annual ACM-SIAM symposium on Discrete algorithms.
 *  Society for Industrial and Applied Mathematics, 2007.
 * </blockquote>
 *
 * @param <T> Type of the points to cluster.
 */
public class ElkanKMeansPlusPlusClusterer<T extends Clusterable>
    extends KMeansPlusPlusClusterer<T> {

    /**
     * @param k Clustering parameter.
     */
    public ElkanKMeansPlusPlusClusterer(int k) {
        super(k);
    }

    /**
     * @param k Clustering parameter.
     * @param maxIterations Allowed number of iterations.
     * @param measure Distance measure.
     * @param random Random generator.
     */
    public ElkanKMeansPlusPlusClusterer(int k,
                                        int maxIterations,
                                        DistanceMeasure measure,
                                        UniformRandomProvider random) {
        super(k, maxIterations, measure, random);
    }

    /**
     * @param k Clustering parameter.
     * @param maxIterations Allowed number of iterations.
     * @param measure Distance measure.
     * @param random Random generator.
     * @param emptyStrategy Strategy for handling empty clusters that
     * may appear during algorithm progress.
     */
    public ElkanKMeansPlusPlusClusterer(int k,
                                        int maxIterations,
                                        DistanceMeasure measure,
                                        UniformRandomProvider random,
                                        EmptyClusterStrategy emptyStrategy) {
        super(k, maxIterations, measure, random, emptyStrategy);
    }

    /** {@inheritDoc} */
    @Override
    public List<CentroidCluster<T>> cluster(final Collection<T> points) {
        final int k = getNumberOfClusters();

        // Number of clusters has to be smaller or equal the number of data points.
        if (points.size() < k) {
            throw new NumberIsTooSmallException(points.size(), k, false);
        }

        final List<T> pointsList = new ArrayList<>(points);
        final int n = points.size();
        final int dim = pointsList.get(0).getPoint().length;

        // Keep minimum intra cluster distance, e.g. for given cluster c s[c] is
        // the distance to the closest cluster c' or s[c] = 1/2 * min_{c'!=c} dist(c', c)
        final double[] s = new double[k];
        Arrays.fill(s, Double.MAX_VALUE);
        // Store the matrix of distances between all cluster centers, e.g. dcc[c1][c2] = distance(c1, c2)
        final double[][] dcc = new double[k][k];

        // For each point keeps the upper bound distance to the cluster center.
        final double[] u = new double[n];
        Arrays.fill(u, Double.MAX_VALUE);

        // For each point and for each cluster keeps the lower bound for the distance between the point and cluster
        final double[][] l = new double[n][k];

        // Seed initial set of cluster centers.
        final double[][] centers = seed(pointsList);

        // Points partitioning induced by cluster centers, e.g. for point xi the value of partitions[xi] indicates
        // the cluster or index of the cluster center which is closest to xi. partitions[xi] = min_{c} distance(xi, c).
        final int[] partitions = partitionPoints(pointsList, centers, u, l);

        final double[] deltas = new double[k];
        VectorialMean[] means = new VectorialMean[k];
        for (int it = 0, max = getMaxIterations();
             it < max;
             it++) {
            int changes = 0;
            // Step I.
            // Compute inter-cluster distances.
            updateIntraCentersDistances(centers, dcc, s);

            for (int xi = 0; xi < n; xi++) {
                boolean r = true;

                // Step II.
                if (u[xi] <= s[partitions[xi]]) {
                    continue;
                }

                for (int c = 0; c < k; c++) {
                    // Check condition III.
                    if (isSkipNext(partitions, u, l, dcc, xi, c)) {
                        continue;
                    }

                    final double[] x = pointsList.get(xi).getPoint();

                    // III(a)
                    if (r) {
                        u[xi] = distance(x, centers[partitions[xi]]);
                        l[xi][partitions[xi]] = u[xi];
                        r = false;
                    }
                    // III(b)
                    if (u[xi] > l[xi][c] || u[xi] > dcc[partitions[xi]][c]) {
                        l[xi][c] = distance(x, centers[c]);
                        if (l[xi][c] < u[xi]) {
                            partitions[xi] = c;
                            u[xi] = l[xi][c];
                            ++changes;
                        }
                    }
                }
            }

            // Stopping criterion.
            if (changes == 0 &&
                it != 0) { // First iteration needed (to update bounds).
                break;
            }

            // Step IV.
            Arrays.fill(means, null);
            for (int i = 0; i < n; i++) {
                if (means[partitions[i]] == null) {
                    means[partitions[i]] = new VectorialMean(dim);
                }
                means[partitions[i]].increment(pointsList.get(i).getPoint());
            }

            for (int i = 0; i < k; i++) {
                deltas[i] = distance(centers[i], means[i].getResult());
                centers[i] = means[i].getResult();
            }

            updateBounds(partitions, u, l, deltas);
        }

        return buildResults(pointsList, partitions, centers);
    }

    /**
     * kmeans++ seeding which provides guarantee of resulting with log(k) approximation
     * for final clustering results
     * <p>
     * Arthur, David, and Sergei Vassilvitskii. "k-means++: The advantages of careful seeding."
     * Proceedings of the eighteenth annual ACM-SIAM symposium on Discrete algorithms.
     * Society for Industrial and Applied Mathematics, 2007.
     *
     * @param points input data points
     * @return an array of initial clusters centers
     *
     */
    private double[][] seed(final List<T> points) {
        final int k = getNumberOfClusters();
        final UniformRandomProvider random = getRandomGenerator();

        final double[][] result = new double[k][];
        final int n = points.size();
        final int pointIndex = random.nextInt(n);

        final double[] minDistances = new double[n];

        int idx = 0;
        result[idx] = points.get(pointIndex).getPoint();

        double sumSqDist = 0;

        for (int i = 0; i < n; i++) {
            final double d = distance(result[idx], points.get(i).getPoint());
            minDistances[i] = d * d;
            sumSqDist += minDistances[i];
        }

        while (++idx < k) {
            final double p = sumSqDist * random.nextDouble();
            int next = 0;
            for (double cdf = 0; cdf < p; next++) {
                cdf += minDistances[next];
            }

            result[idx] = points.get(next - 1).getPoint();
            for (int i = 0; i < n; i++) {
                final double d = distance(result[idx], points.get(i).getPoint());
                sumSqDist -= minDistances[i];
                minDistances[i] = Math.min(minDistances[i], d * d);
                sumSqDist += minDistances[i];
            }
        }

        return result;
    }


    /**
     * Once initial centers are chosen, we can actually go through data points and assign points to the
     * cluster based on the distance between initial centers and points.
     *
     * @param pointsList data points list
     * @param centers current clusters centers
     * @param u points upper bounds
     * @param l lower bounds for points to clusters centers
     *
     * @return initial assignment of points into clusters
     */
    private int[] partitionPoints(List<T> pointsList,
                                  double[][] centers,
                                  double[] u,
                                  double[][] l) {
        final int k = getNumberOfClusters();
        final int n = pointsList.size();
        // Points assignments vector.
        final int[] assignments = new int[n];
        Arrays.fill(assignments, -1);
        // Need to assign points to the clusters for the first time and intitialize the lower bound l(x, c)
        for (int i = 0; i < n; i++) {
            final double[] x = pointsList.get(i).getPoint();
            for (int j = 0; j < k; j++) {
                l[i][j] = distance(x, centers[j]); // l(x, c) = d(x, c)
                if (u[i] > l[i][j]) {
                    u[i] = l[i][j]; // u(x) = min_c d(x, c)
                    assignments[i] = j; // c(x) = argmin_c d(x, c)
                }
            }
        }
        return assignments;
    }

    /**
     * Updated distances between clusters centers and for each cluster
     * pick the closest neighbour and keep distance to it.
     *
     * @param centers cluster centers
     * @param dcc matrix of distance between clusters centers, e.g.
     * {@code dcc[i][j] = distance(centers[i], centers[j])}
     * @param s For a given cluster, {@code s[si]} holds distance value
     * to the closest cluster center.
     */
    private void updateIntraCentersDistances(double[][] centers,
                                             double[][] dcc,
                                             double[] s) {
        final int k = getNumberOfClusters();
        for (int i = 0; i < k; i++) {
            // Since distance(xi, xj) == distance(xj, xi), we need to update
            // only upper or lower triangle of the distances matrix and mirror
            // to the lower of upper triangle accordingly, trace has to be all
            // zeros, since distance(xi, xi) == 0.
            for (int j = i + 1; j < k; j++) {
                dcc[i][j] = 0.5 * distance(centers[i], centers[j]);
                dcc[j][i] = dcc[i][j];
                if (dcc[i][j] < s[i]) {
                    s[i] = dcc[i][j];
                }
                if (dcc[j][i] < s[j]) {
                    s[j] = dcc[j][i];
                }
            }
        }
    }

    /**
     * For given points and and cluster, check condition (3) of Elkan algorithm.
     *
     * <ul>
     *  <li>c is not the cluster xi assigned to</li>
     *  <li>{@code u[xi] > l[xi][x]} upper bound for point xi is greater than
     *   lower bound between xi and some cluster c</li>
     *  <li>{@code u[xi] > 1/2 * d(c(xi), c)} upper bound is greater than
     *   distance between center of xi's cluster and c</li>
     * </ul>
     *
     * @param partitions current partition of points into clusters
     * @param u upper bounds for points
     * @param l lower bounds for distance between cluster centers and points
     * @param dcc matrix of distance between clusters centers
     * @param xi index of the point
     * @param c index of the cluster
     * @return true if conditions above satisfied false otherwise
     */
    private static boolean isSkipNext(int[] partitions,
                                      double[] u,
                                      double[][] l,
                                      double[][] dcc,
                                      int xi,
                                      int c) {
        return c == partitions[xi] ||
               u[xi] <= l[xi][c] ||
               u[xi] <= dcc[partitions[xi]][c];
    }

    /**
     * Once kmeans iterations have been converged and no more movements, we can build up the final
     * resulted list of cluster centroids ({@link CentroidCluster}) and assign input points based
     * on the converged partitioning.
     *
     * @param pointsList list of data points
     * @param partitions current partition of points into clusters
     * @param centers cluster centers
     * @return cluster partitioning
     */
    private List<CentroidCluster<T>> buildResults(List<T> pointsList,
                                                  int[] partitions,
                                                  double[][] centers) {
        final int k = getNumberOfClusters();
        final List<CentroidCluster<T>> result = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            final CentroidCluster<T> cluster = new CentroidCluster<>(new DoublePoint(centers[i]));
            result.add(cluster);
        }
        for (int i = 0; i < pointsList.size(); i++) {
            result.get(partitions[i]).addPoint(pointsList.get(i));
        }
        return result;
    }

    /**
     * Based on the distance that cluster center has moved we need to update our upper and lower bound.
     * Worst case assumption, the center of the assigned to given cluster moves away from the point, while
     * centers of over clusters become closer.
     *
     * @param partitions current points assiments to the clusters
     * @param u points upper bounds
     * @param l lower bounds for distances between point and corresponding cluster
     * @param deltas the movement delta for each cluster center
     */
    private void updateBounds(int[] partitions,
                              double[] u,
                              double[][] l,
                              double[] deltas) {
        final int k = getNumberOfClusters();
        for (int i = 0; i < partitions.length; i++) {
            u[i] += deltas[partitions[i]];
            for (int j = 0; j < k; j++) {
                l[i][j] = Math.max(0, l[i][j] - deltas[j]);
            }
        }
    }

    /**
     * @param a Coordinates.
     * @param b Coordinates.
     * @return the distance between {@code a} and {@code b}.
     */
    private double distance(final double[] a,
                            final double[] b) {
        return getDistanceMeasure().compute(a, b);
    }
}
