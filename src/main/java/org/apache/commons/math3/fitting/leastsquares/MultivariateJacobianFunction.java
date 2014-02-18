package org.apache.commons.math3.fitting.leastsquares;

import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

/**
 * A interface for functions that compute a vector of values and can compute their
 * derivatives (Jacobian).
 *
 * @version $Id$
 */
public interface MultivariateJacobianFunction {

    /**
     * Compute the function value and its Jacobian.
     *
     * @param point the abscissae
     * @return the values and their Jacobian of this vector valued function.
     */
    Pair<RealVector, RealMatrix> value(double[] point);

}
