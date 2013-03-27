package org.apache.commons.math3.ml.distance;

import org.apache.commons.math3.util.MathArrays;

/**
 * Calculates the L<sub>1</sub> (sum of abs) distance between two points.
 *
 * @version $Id $
 * @since 3.2
 */
public class ManhattanDistance implements DistanceMeasure {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -9108154600539125566L;

    /** {@inheritDoc} */
    public double compute(double[] a, double[] b) {
        return MathArrays.distance1(a, b);
    }

}
