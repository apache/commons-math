package org.apache.commons.math4.fitting.leastsquares;

import org.apache.commons.math4.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.analysis.UnivariateVectorFunction;
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
 * This version that works with MultivariateVectorFunction
 * @see DifferentiatorMultivariateJacobianFunction for version that works with MultivariateFunction
 */
public class DifferentiatorVectorMultivariateJacobianFunction implements MultivariateJacobianFunction {
    private final MultivariateVectorFunction function;
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
     * This version that works with MultivariateVectorFunction
     * @see DifferentiatorMultivariateJacobianFunction for version that works with MultivariateFunction
     */
    public DifferentiatorVectorMultivariateJacobianFunction(MultivariateVectorFunction function, int nbPoints, double stepSize) {
        this.function = function;
        this.differentiator = new FiniteDifferencesDifferentiator(nbPoints, stepSize);
    }

    @Override
    public Pair<RealVector, RealMatrix> value(RealVector point) {
        RealVector value = new ArrayRealVector(function.value(point.toArray()));
        RealMatrix jacobian = new Array2DRowRealMatrix(value.getDimension(), point.getDimension());

        for(int column = 0; column < point.getDimension(); column++) {
            final int columnFinal = column;
            double originalPoint = point.getEntry(column);
            double[] partialDerivatives = getPartialDerivative(testPoint -> {

                point.setEntry(columnFinal, testPoint);

                double[] testPointValue = function.value(point.toArray());

                point.setEntry(columnFinal, originalPoint);  //set it back

                return testPointValue;
            }, originalPoint);

            jacobian.setColumn(column, partialDerivatives);
        }

        return new Pair<>(value, jacobian);
    }

    private double[] getPartialDerivative(UnivariateVectorFunction univariateFunction, double atParameterValue) {
        DerivativeStructure[] derivatives = differentiator
                .differentiate(univariateFunction)
                .value(new DerivativeStructure(1, 1, 0, atParameterValue));
        double[] derivativesOut = new double[derivatives.length];
        for(int index=0;index<derivatives.length;index++) {
            derivativesOut[index] = derivatives[index].getPartialDerivative(1);
        }
        return derivativesOut;
    }
}
