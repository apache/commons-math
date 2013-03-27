package org.apache.commons.math3.ml.distance;

import org.apache.commons.math3.util.FastMath;

/**
 * Calculates the Canberra distance between two points.
 *
 * @version $Id $
 * @since 3.2
 */
public class CanberraDistance implements DistanceMeasure {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -6972277381587032228L;

    /** {@inheritDoc} */
    public double compute(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            final double num = FastMath.abs(a[i] - b[i]);
            final double denom = FastMath.abs(a[i]) + FastMath.abs(b[i]);
            sum += num == 0.0 && denom == 0.0 ? 0.0 : num / denom;
        }
        return sum;
    }

}
