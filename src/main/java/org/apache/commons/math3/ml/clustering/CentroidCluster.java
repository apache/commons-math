package org.apache.commons.math3.ml.clustering;

/**
 * A Cluster used by centroid-based clustering algorithms.
 * <p>
 * Defines additionally a cluster center which may not necessarily be a member
 * of the original data set.
 *
 * @param <T> the type of points that can be clustered
 * @version $Id $
 * @since 3.2
 */
public class CentroidCluster<T extends Clusterable> extends Cluster<T> {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -3075288519071812288L;

    /** Center of the cluster. */
    private final Clusterable center;

    /**
     * Build a cluster centered at a specified point.
     * @param center the point which is to be the center of this cluster
     */
    public CentroidCluster(final Clusterable center) {
        super();
        this.center = center;
    }

    /**
     * Get the point chosen to be the center of this cluster.
     * @return chosen cluster center
     */
    public Clusterable getCenter() {
        return center;
    }

}
