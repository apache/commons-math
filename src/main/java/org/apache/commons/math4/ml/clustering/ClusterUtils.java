package org.apache.commons.math4.ml.clustering;

import org.apache.commons.math4.exception.ConvergenceException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.ml.distance.DistanceMeasure;
import org.apache.commons.math4.stat.descriptive.moment.Variance;
import org.apache.commons.rng.UniformRandomProvider;
import org.apache.commons.rng.simple.RandomSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClusterUtils {
    private ClusterUtils() {
    }

    public static <T> ArrayList<T> shuffle(Collection<T> c, UniformRandomProvider random) {
        ArrayList<T> list = new ArrayList<T>(c);
        int size = list.size();
        for (int i = size; i > 1; --i) {
            list.set(i - 1, list.set(random.nextInt(i), list.get(i - 1)));
        }
        return list;
    }

    public static <T> ArrayList<T> shuffle(Collection<T> points) {
        return shuffle(points, RandomSource.create(RandomSource.MT_64));
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
