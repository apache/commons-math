package org.apache.commons.math4.ml.clustering;

import org.apache.commons.math4.exception.ConvergenceException;
import org.apache.commons.math4.exception.MathIllegalArgumentException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.ml.distance.EuclideanDistance;
import org.apache.commons.math4.rng.RandomSource;
import org.apache.commons.math4.rng.UniformRandomProvider;
import org.apache.commons.math4.stat.descriptive.moment.VectorialMean;
import org.apache.commons.math4.util.FastMath;
import org.apache.commons.math4.util.MathUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This is the boosted implementation of kmeans++ algorithm based on the following publication:
 * Elkan, Charles. "Using the triangle inequality to accelerate k-means." ICML. Vol. 3. 2003.
 *
 * <p>
 * Algorithm uses triangle inequality to speed kmeans computation, basically by reducing the amount
 * of distances calculation. Towards the last iteration of kmeans algorithm, points which already assigned to
 * some cluster are unlikely to move to new cluster, as well updates of cluster centers usually relatively small.
 * Therefore algorithm uses triangle inequality to determine the cases where distance computation could be skipped
 * since center move only a little without affecting points partitioning.
 * <p>
 * As for initial centers seeding, kmeans++ initilization used following algorithm in:
 * Arthur, David, and Sergei Vassilvitskii. "k-means++: The advantages of careful seeding."
 * Proceedings of the eighteenth annual ACM-SIAM symposium on Discrete algorithms.
 * Society for Industrial and Applied Mathematics, 2007.
 *
 * @param <T> type of the points to cluster
 */
public class ElkanKmeansPlusPlusClusterer<T extends Clusterable> extends Clusterer<T> {

    // Clustering parameter k
    private int k;

    // Maximum of available iterations to make before terminate clustering process.
    private int maxIter;

    private UniformRandomProvider random = RandomSource.create(RandomSource.MT_64);

    public ElkanKmeansPlusPlusClusterer(int k) {
        this(new EuclideanDistance(), k, Integer.MAX_VALUE);
    }

    public ElkanKmeansPlusPlusClusterer(DistanceMeasure measure, int k) {
        this(measure, k, Integer.MAX_VALUE);
    }

    public ElkanKmeansPlusPlusClusterer(DistanceMeasure measure, int k, int maxIter) {
        super(measure);
        if (k < 1) {
            throw new IllegalStateException("k should be positive integer number greater than zero.");
        }
        this.k = k;
        this.maxIter = maxIter;
    }

    public ElkanKmeansPlusPlusClusterer(int k, int maxIter) {
        this(new EuclideanDistance(), k, maxIter);
    }

    @Override
    public List<CentroidCluster<T>> cluster(final Collection<T> points) throws
            MathIllegalArgumentException, ConvergenceException {
        // sanity checks
        MathUtils.checkNotNull(points);

        // number of clusters has to be smaller or equal the number of data points
        if (points.size() < k) {
            throw new NumberIsTooSmallException(points.size(), k, false);
        }

        List<T> pointsList = new ArrayList<>(points);
        final int N = points.size();
        final int dim = pointsList.get(0).getPoint().length;

        // Keep minimum intra cluster distance, e.g. for given cluster c s[c] is
        // the distance to the closest cluster c' or s[c] = 1/2 * min_{c'!=c} dist(c', c)
        double[] s = new double[k];
        Arrays.fill(s, Double.MAX_VALUE);
        // Store the matrix of distances between all cluster centers, e.g. d_cc[c1][c2] = distance(c1, c2)
        double[][] d_cc = new double[k][k];

        // For each point keeps the upper bound distance to the cluster center.
        double[] u = new double[N];
        Arrays.fill(u, Double.MAX_VALUE);

        // For each point and for each cluster keeps the lower bound for the distance between the point and cluster
        double[][] l = new double[N][k];

        // Cluster centers
        double[][] centers;

        // Points partitioning induced by cluster centers, e.g. for point x_i the value of partitions[x_i] indicates
        // the cluster or index of the cluster center which is closest to x_i. partitions[x_i] = min_{c} distance(x_i, c).
        int[] partitions;

        // Seed initial set of points
        centers = seed(pointsList);
        partitions = partitionPoints(pointsList, centers, u, l);

        VectorialMean[] means = new VectorialMean[k];
        double[] deltas = new double[k];

        for (int it = 0, changes = 0; it < FastMath.min(maxIter, Integer.MAX_VALUE); it++, changes = 0) {
            // Step I.
            /**
             * Compute inter-cluster distances.
             */
            updateIntraCentersDistances(centers, d_cc, s);

            for (int x_i = 0; x_i < N; x_i++) {
                boolean r = true;

                // Steps II.
                if (u[x_i] <= s[partitions[x_i]]) {
                    continue;
                }

                for (int c = 0; c < k; c++) {
                    // Check condition III of an algorithm.
                    if (isSkipNext(partitions, u, l, d_cc, x_i, c)) {
                        continue;
                    }

                    double[] x = pointsList.get(x_i).getPoint();

                    // III(a)
                    if (r) {
                        u[x_i] = distance(x, centers[partitions[x_i]]);
                        l[x_i][partitions[x_i]] = u[x_i];
                        r = false;
                    }
                    // III(b)
                    if (u[x_i] > l[x_i][c] || u[x_i] > d_cc[partitions[x_i]][c]) {
                        l[x_i][c] = distance(x, centers[c]);
                        if (l[x_i][c] < u[x_i]) {
                            partitions[x_i] = c;
                            u[x_i] = l[x_i][c];
                            changes++;
                        }
                    }
                }
            }

            // No reassignments? We can stop!
            // it != 0, needed since first iteration needed to update bounds
            // however it's possible that points won't move until centers are
            // updated.
            if (changes == 0 && it != 0)
                break;

            // Step IV.
            Arrays.fill(means, null);
            for (int i = 0; i < N; i++) {
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
     * @param points - input data points
     * @return - an array of initial clusters centers
     *
     */
    private double[][] seed(final List<T> points) {
        double[][] result = new double[k][];
        int N = points.size();
        int pointIndex = random.nextInt(N);

        double[] minDistances = new double[N];

        int idx = 0;
        result[idx] = points.get(pointIndex).getPoint();

        double sumSqDist = 0;

        for (int i = 0; i < N; i++) {
            final double d = distance(result[idx], points.get(i).getPoint());
            minDistances[i] = d * d;
            sumSqDist += minDistances[i];
        }

        while (++idx < k) {
            double p = sumSqDist * random.nextDouble();
            int next = 0;
            for (double cdf = 0; cdf < p; ++next) {
                cdf += minDistances[next];
            }

            result[idx] = points.get(next - 1).getPoint();
            for (int i = 0; i < N; i++) {
                final double d = distance(result[idx], points.get(i).getPoint());
                sumSqDist -= minDistances[i];
                minDistances[i] = FastMath.min(minDistances[i], d * d);
                sumSqDist += minDistances[i];
            }
        }

        return result;
    }


    /**
     * Once initial centers are chosen, we can actually go through data points and assign points to the
     * cluster based on the distance between initial centers and points.
     * @param pointsList data points list
     * @param centers current clusters centers
     * @param u points upper bounds
     * @param l lower bounds for points to clusters centers
     *
     * @return initial assignment of points into clusters
     */
    private int[] partitionPoints(List<T> pointsList, double[][] centers, double[] u, double[][] l) {
        int n = pointsList.size();
        // Points assignments vector.
        int[] assignments = new int[n];
        Arrays.fill(assignments, -1);
        // Need to assign points to the clusters for the first time and intitialize the lower bound l(x, c)
        for (int i = 0; i < n; i++) {
            final double[] x = pointsList.get(i).getPoint();
            for (int j = 0; j < k; j++) {
                l[i][j] = distance(x, centers[j]); // l(x, c) = d(x, c)
                if (u[i] > l[i][j]) {
                    u[i] = l[i][j];     // u(x) = min_c d(x, c).
                    assignments[i] = j; // c(x) = argmin_c d(x, c)
                }
            }
        }
        return assignments;
    }

    /**
     * Updated distances between clusters centers and for each cluster
     * pick the closest neighbour and keep distance to it.
     * @param centers - cluster centers
     * @param d_cc - matrix of distance between clusters centers, e.g. d_cc[i][j] = distance(centers[i], centers[j])
     * @param s - for given cluster c_i s[s_i] holds distance value to the closest cluster center
     */
    private void updateIntraCentersDistances(double[][] centers, double[][] d_cc, double[] s) {
        for (int i = 0; i < k; i++) {
            // Well, since distance(x_i, x_j) == distance(x_j, x_i), we need to update only upper or lower triangle of
            // the distances matrix and mirror to the lower of upper triangle accordingly, trace has to be all zeros, since
            // distance(x_i, x_i) == 0.
            for (int j = i + 1; j < k; j++) {
                d_cc[i][j] = 0.5 * distance(centers[i], centers[j]);
                d_cc[j][i] = d_cc[i][j];
                if (d_cc[i][j] < s[i]) {
                    s[i] = d_cc[i][j];
                }
                if (d_cc[j][i] < s[j]) {
                    s[j] = d_cc[j][i];
                }
            }
        }
    }

    /**
     * For given points and and cluster check condition (3) of Elkan algorithm:
     * <ul>c is not the cluster x_i assigned to</ul>
     * <ul>u[x_i] > l[x_i][x] upped bound for point x_i is greater than lower bound between x_i and some cluster c</ul>
     * <ul>u[x_i] > 1/2 * d(c(x_i), c) upper bound is greater than distance between center of x_i's cluster and c</ul>
     * @param partitions - current partition of points into clusters
     * @param u - upper bounds for points
     * @param l - lower bounds for distance between cluster centers and points
     * @param d_cc - matrix of distance between clusters centers
     * @param x_i index of the point
     * @param c index of the cluster
     * @return true if conditions above satisfied false otherwise
     */
    private boolean isSkipNext(int partitions[], double[]  u, double[][] l, double[][] d_cc, int x_i, int c) {
        return (c == partitions[x_i]) ||
                (u[x_i] <= l[x_i][c]) ||
                (u[x_i] <= d_cc[partitions[x_i]][c]);
    }

    /**
     * Once kmeans iterations have been converged and no more movements, we can build up the final
     * resulted list of cluster centroids ({@link CentroidCluster}) and assign input points based
     * on the converged partitioning.
     * @param pointsList list of data points
     * @param partitions - current partition of points into clusters
     * @param centers - cluster centers
     * @return cluster partitioning
     */
    private List<CentroidCluster<T>> buildResults(List<T> pointsList, int[] partitions, double centers[][]) {
        List<CentroidCluster<T>> result = new ArrayList<>();
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
    private void updateBounds(int[] partitions, double[] u, double[][] l, double[] deltas) {
        for (int i = 0; i < partitions.length; i++) {
            u[i] += deltas[partitions[i]];
            for (int j = 0; j < k; j++) {
                l[i][j] = FastMath.max(0, l[i][j] - deltas[j]);
            }
        }
    }

    protected double distance(final double[] a, final double[] b) {
        return getDistanceMeasure().compute(a, b);
    }
}