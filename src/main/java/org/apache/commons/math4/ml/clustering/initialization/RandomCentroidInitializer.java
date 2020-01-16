package org.apache.commons.math4.ml.clustering.initialization;

import org.apache.commons.math4.ml.clustering.CentroidCluster;
import org.apache.commons.math4.ml.clustering.ClusterUtils;
import org.apache.commons.math4.ml.clustering.Clusterable;
import org.apache.commons.rng.UniformRandomProvider;

import java.util.*;

/**
 * Random choose the initial centers.
 */
public class RandomCentroidInitializer implements CentroidInitializer {
    private final UniformRandomProvider random;

    /**
     * Build a random RandomCentroidInitializer
     *
     * @param random the random to use.
     */
    public RandomCentroidInitializer(final UniformRandomProvider random) {
        this.random = random;
    }

    /**
     * Random choose the initial centers.
     *
     * @param points the points to choose the initial centers from
     * @param k      The number of clusters
     * @return the initial centers
     */
    @Override
    public <T extends Clusterable> List<CentroidCluster<T>> chooseCentroids(Collection<T> points, int k) {
        ArrayList<T> list = ClusterUtils.shuffle(points, random);
        List<CentroidCluster<T>> result = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            result.add(new CentroidCluster<>(list.get(i)));
        }
        return result;
    }
}
