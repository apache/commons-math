package org.apache.commons.math3.ml.distance;

import java.io.Serializable;

/**
 * Interface for distance measures of n-dimensional vectors.
 *
 * @version $Id $
 * @since 3.2
 */
public interface DistanceMeasure extends Serializable {

    /**
     * Compute the distance between two n-dimensional vectors.
     * <p>
     * The two vectors are required to have the same dimension.
     *
     * @param a the first vector
     * @param b the second vector
     * @return the distance between the two vectors
     */
    double compute(double[] a, double[] b);
}
