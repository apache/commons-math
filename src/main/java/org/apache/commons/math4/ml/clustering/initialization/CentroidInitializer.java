package org.apache.commons.math4.ml.clustering.initialization;

import org.apache.commons.math4.ml.clustering.CentroidCluster;
import org.apache.commons.math4.ml.clustering.Clusterable;

import java.util.Collection;
import java.util.List;

/**
 * Interface abstract the algorithm for clusterer to choose the initial centers.
 */
public interface CentroidInitializer {

    /**
     * Choose the initial centers.
     *
     * @param points the points to choose the initial centers from
     * @param k      The number of clusters
     * @return the initial centers
     */
    <T extends Clusterable> List<CentroidCluster<T>> chooseCentroids(final Collection<T> points, final int k);
}
