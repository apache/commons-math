package org.apache.commons.math3.ml.distance;

import org.apache.commons.math3.util.MathArrays;

/**
 * Calculates the L<sub>&infin;</sub> (max of abs) distance between two points.
 *
 * @version $Id $
 * @since 3.2
 */
public class ChebyshevDistance implements DistanceMeasure {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -4694868171115238296L;

    /** {@inheritDoc} */
    public double compute(double[] a, double[] b) {
        return MathArrays.distanceInf(a, b);
    }

}
