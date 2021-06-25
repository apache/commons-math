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
package org.apache.commons.math4.legacy.fitting.leastsquares;

import org.apache.commons.math4.legacy.analysis.MultivariateVectorFunction;
import org.apache.commons.math4.legacy.analysis.UnivariateVectorFunction;
import org.apache.commons.math4.legacy.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math4.legacy.analysis.differentiation.UnivariateVectorFunctionDifferentiator;
import org.apache.commons.math4.legacy.linear.Array2DRowRealMatrix;
import org.apache.commons.math4.legacy.linear.ArrayRealVector;
import org.apache.commons.math4.legacy.linear.RealMatrix;
import org.apache.commons.math4.legacy.linear.RealVector;
import org.apache.commons.math4.legacy.core.Pair;

import java.util.Arrays;

/**
 * A MultivariateJacobianFunction (a thing that requires a derivative)
 * combined with the thing that can find derivatives.
 *
 * Can be used with a LeastSquaresProblem, a LeastSquaresFactory, or a LeastSquaresBuilder.
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
     * Build the jacobian function using a differentiator.
     *
     * @param function the function to turn into a jacobian
     * @param differentiator the differentiator to find the derivative
     */
    public DifferentiatorVectorMultivariateJacobianFunction(MultivariateVectorFunction function, UnivariateVectorFunctionDifferentiator differentiator) {
        this.function = function;
        this.differentiator = differentiator;
    }

    /** {@inheritDoc} */
    @Override
    public Pair<RealVector, RealMatrix> value(RealVector point) {
        double[] testArray = point.toArray();
        RealVector value = new ArrayRealVector(function.value(testArray));
        RealMatrix jacobian = new Array2DRowRealMatrix(value.getDimension(), point.getDimension());

        for(int column = 0; column < point.getDimension(); column++) {
            final int columnFinal = column;
            double originalPoint = point.getEntry(column);
            double[] partialDerivatives = getPartialDerivative(testPoint -> {

                testArray[columnFinal] = testPoint;

                return function.value(testArray);
            }, originalPoint);

            testArray[column] = originalPoint; //set it back

            jacobian.setColumn(column, partialDerivatives);
        }

        return new Pair<>(value, jacobian);
    }

    /**
     * Returns first order derivative for the function passed in using a differentiator.
     * @param univariateVectorFunction the function to differentiate
     * @param atParameterValue the point at which to differentiate it at
     * @return the slopes at that point
     */
    private double[] getPartialDerivative(UnivariateVectorFunction univariateVectorFunction, double atParameterValue) {
        DerivativeStructure[] derivatives = differentiator
                .differentiate(univariateVectorFunction)
                .value(new DerivativeStructure(1, 1, 0, atParameterValue));
        return Arrays.stream(derivatives).mapToDouble(derivative -> derivative.getPartialDerivative(1)).toArray();
    }
}
