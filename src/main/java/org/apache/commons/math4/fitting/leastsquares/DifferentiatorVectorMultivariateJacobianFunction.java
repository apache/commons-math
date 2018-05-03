/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math4.fitting.leastsquares;

import org.apache.commons.math4.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.analysis.UnivariateVectorFunction;
import org.apache.commons.math4.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math4.analysis.differentiation.UnivariateVectorFunctionDifferentiator;
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
    /**
     * The input function to find a jacobian for.
     */
    private final MultivariateVectorFunction function;
    /**
     * The differentiator to use to find the jacobian.
     */
    private final UnivariateVectorFunctionDifferentiator differentiator;

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
     * @exception org.apache.commons.math4.exception.NotPositiveException if {@code stepsize <= 0} (note that
     * {@link org.apache.commons.math4.exception.NotPositiveException} extends {@link org.apache.commons.math4.exception.NumberIsTooSmallException})
     * @exception org.apache.commons.math4.exception.NumberIsTooSmallException {@code nbPoint <= 1}
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

    /**
     * Returns first order derivative for the function passed in using a differentiator
     * @param univariateVectorFunction the function to differentiate
     * @param atParameterValue the point at which to differentiate it at
     * @return the slopes at that point
     */
    private double[] getPartialDerivative(UnivariateVectorFunction univariateVectorFunction, double atParameterValue) {
        DerivativeStructure[] derivatives = differentiator
                .differentiate(univariateVectorFunction)
                .value(new DerivativeStructure(1, 1, 0, atParameterValue));
        double[] derivativesOut = new double[derivatives.length];
        for(int index=0;index<derivatives.length;index++) {
            derivativesOut[index] = derivatives[index].getPartialDerivative(1);
        }
        return derivativesOut;
    }
}
