package org.apache.commons.math3.ml.distance;

import org.apache.commons.math3.util.MathArrays;

/**
 * Calculates the L<sub>2</sub> (Euclidean) distance between two points.
 *
 * @version $Id $
 * @since 3.2
 */
public class EuclideanDistance implements DistanceMeasure {

    /** Serializable version identifier. */
    private static final long serialVersionUID = 1717556319784040040L;

    /** {@inheritDoc} */
    public double compute(double[] a, double[] b) {
        return MathArrays.distance(a, b);
    }

}
