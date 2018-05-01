package org.apache.commons.math4.fitting.leastsquares;

import org.apache.commons.math4.analysis.MultivariateFunction;
import org.apache.commons.math4.analysis.UnivariateFunction;
import org.apache.commons.math4.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.linear.ArrayRealVector;
import org.apache.commons.math4.linear.RealMatrix;
import org.apache.commons.math4.linear.RealVector;
import org.apache.commons.math4.util.Pair;

/**
 * A MultivariateJacobianFunction (a thing that requires a derivative)
 * combined with the thing that can find derivatives
 *
 * This version that works with MultivariateFunction
 * @see DifferentiatorVectorMultivariateJacobianFunction for version that works with MultivariateVectorFunction
 */
public class DifferentiatorMultivariateJacobianFunction implements MultivariateJacobianFunction {
    private final MultivariateFunction function;
    private final FiniteDifferencesDifferentiator differentiator;

    /**
     * Build a differentiator with number of points and step size when independent variable is unbounded.
     * <p>
     * Beware that wrong settings for the finite differences differentiator
     * can lead to highly unstable and inaccurate results, especially for
     * high derivation orders. Using very small step sizes is often a
     * <em>bad</em> idea.
     * </p>
     * @param function the function to turn into a multivariate jacobian function
     * @param nbPoints number of points to use
     * @param stepSize step size (gap between each point)
     * @exception NotPositiveException if {@code stepsize <= 0} (note that
     * {@link NotPositiveException} extends {@link NumberIsTooSmallException})
     * @exception NumberIsTooSmallException {@code nbPoint <= 1}
     *
     * This version that works with MultivariateFunction
     * @see DifferentiatorVectorMultivariateJacobianFunction for version that works with MultivariateVectorFunction
     */
    public DifferentiatorMultivariateJacobianFunction(MultivariateFunction function, int nbPoints, double stepSize) {
        this.function = function;
        this.differentiator = new FiniteDifferencesDifferentiator(nbPoints, stepSize);
    }

    @Override
    public Pair<RealVector, RealMatrix> value(RealVector point) {
        ArrayRealVector value = new ArrayRealVector(1);
        value.setEntry(0, function.value(point.toArray()));
        RealMatrix jacobian = new Array2DRowRealMatrix(1, point.getDimension());

        for(int column = 0; column < point.getDimension(); column++) {
            final int columnFinal = column;
            double originalPoint = point.getEntry(column);
            double partialDerivative = getPartialDerivative(testPoint -> {

                point.setEntry(columnFinal, testPoint);

                double testPointOutput = function.value(point.toArray());

                point.setEntry(columnFinal, originalPoint);  //set it back

                return testPointOutput;
            }, originalPoint);

            jacobian.setEntry(0, column, partialDerivative);
        }

        return new Pair<>(value, jacobian);
    }

    private double getPartialDerivative(UnivariateFunction univariateFunction, double atParameterValue) {
        return differentiator
                .differentiate(univariateFunction)
                .value(new DerivativeStructure(1, 1, 0, atParameterValue))
                .getPartialDerivative(1);
    }
}
