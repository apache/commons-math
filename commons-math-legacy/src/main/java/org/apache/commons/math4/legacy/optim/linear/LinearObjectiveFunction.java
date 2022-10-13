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
package org.apache.commons.math4.legacy.optim.linear;

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.linear.ArrayRealVector;
import org.apache.commons.math4.legacy.linear.RealVector;
import org.apache.commons.math4.legacy.optim.OptimizationData;

/**
 * An objective function for a linear optimization problem.
 * <p>
 * A linear objective function has one the form:
 * <div style="white-space: pre"><code>
 * c<sub>1</sub>x<sub>1</sub> + ... c<sub>n</sub>x<sub>n</sub> + d
 * </code></div>
 * The c<sub>i</sub> and d are the coefficients of the equation,
 * the x<sub>i</sub> are the coordinates of the current point.
 *
 * @since 2.0
 */
public class LinearObjectiveFunction
    implements MultivariateFunction,
               OptimizationData {
    /** Coefficients of the linear equation (c<sub>i</sub>). */
    private final RealVector coefficients;
    /** Constant term of the linear equation. */
    private final double constantTerm;

    /**
     * @param coefficients Coefficients for the linear equation being optimized.
     * @param constantTerm Constant term of the linear equation.
     */
    public LinearObjectiveFunction(double[] coefficients, double constantTerm) {
        this(new ArrayRealVector(coefficients), constantTerm);
    }

    /**
     * @param coefficients Coefficients for the linear equation being optimized.
     * @param constantTerm Constant term of the linear equation.
     */
    public LinearObjectiveFunction(RealVector coefficients, double constantTerm) {
        this.coefficients = coefficients;
        this.constantTerm = constantTerm;
    }

    /**
     * Gets the coefficients of the linear equation being optimized.
     *
     * @return coefficients of the linear equation being optimized.
     */
    public RealVector getCoefficients() {
        return coefficients;
    }

    /**
     * Gets the constant of the linear equation being optimized.
     *
     * @return constant of the linear equation being optimized.
     */
    public double getConstantTerm() {
        return constantTerm;
    }

    /**
     * Computes the value of the linear equation at the current point.
     *
     * @param point Point at which linear equation must be evaluated.
     * @return the value of the linear equation at the current point.
     */
    @Override
    public double value(final double[] point) {
        return value(new ArrayRealVector(point, false));
    }

    /**
     * Computes the value of the linear equation at the current point.
     *
     * @param point Point at which linear equation must be evaluated.
     * @return the value of the linear equation at the current point.
     */
    public double value(final RealVector point) {
        return coefficients.dotProduct(point) + constantTerm;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LinearObjectiveFunction) {
            LinearObjectiveFunction rhs = (LinearObjectiveFunction) other;
          return constantTerm == rhs.constantTerm && coefficients.equals(rhs.coefficients);
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Double.valueOf(constantTerm).hashCode() ^ coefficients.hashCode();
    }
}
