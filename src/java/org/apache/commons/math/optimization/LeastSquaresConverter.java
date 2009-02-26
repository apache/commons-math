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

package org.apache.commons.math.optimization;

import org.apache.commons.math.linear.RealMatrix;

/** This class converts {@link MultiObjectiveFunction vectorial
 * objective functions} to {@link ObjectiveFunction scalar objective functions}
 * when the goal is to minimize them.
 * <p>
 * This class is mostly used when the vectorial objective function represents
 * residuals, i.e. differences between a theoretical result computed from a
 * variables set applied to a model and a reference. Residuals are intended to be
 * minimized in order to get the variables set that best fit the model to the
 * reference. The reference may be obtained for example from physical measurements
 * whether the model is built from theoretical considerations.
 * </p>
 * <p>
 * This class computes a possibly weighted squared sum of the residuals, which is
 * a scalar value. It implements the {@link ObjectiveFunction} interface and can
 * therefore be minimized by any optimizer supporting scalar objectives functions.
 * This correspond to a least square estimation.
 * </p>
 * <p>
 * This class support combination of residuals with or without weights and correlations.
 * </p>
  *
 * @see ObjectiveFunction
 * @see MultiObjectiveFunction
 * @version $Revision$ $Date$
 * @since 2.0
 */

public class LeastSquaresConverter implements ObjectiveFunction {

    /** Serializable version identifier. */
    private static final long serialVersionUID = -5174886571116126798L;

    /** Underlying vectorial function. */
    private final MultiObjectiveFunction function;

    /** Optional weights for the residuals. */
    private final double[] weights;

    /** Optional scaling matrix (weight and correlations) for the residuals. */
    private final RealMatrix scale;

    /** Build a simple converter for uncorrelated residuals with the same weight.
     * @param function vectorial residuals function to wrap
     */
    public LeastSquaresConverter (final MultiObjectiveFunction function) {
        this.function = function;
        this.weights  = null;
        this.scale    = null;
    }

    /** Build a simple converter for uncorrelated residuals with the specific weights.
     * <p>
     * The scalar objective function value is computed as:
     * <pre>
     * objective = &sum;(weight<sub>i</sub>residual<sub>i</sub>)<sup>2</sup>
     * </pre>
     * </p>
     * <p>
     * Weights can be used for example to combine residuals with different standard
     * deviations. As an example, consider a 2000 elements residuals array in which
     * even elements are angular measurements in degrees with a 0.01&deg; standard
     * deviation and off elements are distance measurements in meters with a 15m
     * standard deviation. In this case, the weights array should be initialized with
     * value 1.0/0.01 in the even elements and 1.0/15.0 in the odd elements. 
     * </p>
     * <p>
     * The residuals array computed by the function and the weights array must
     * have consistent sizes or a {@link ObjectiveException} will be triggered while
     * computing the scalar objective.
     * </p>
     * @param function vectorial residuals function to wrap
     * @param weights weights to apply to the residuals
     */
    public LeastSquaresConverter (final MultiObjectiveFunction function,
                                  final double[] weights) {
        this.function = function;
        this.weights  = weights.clone();
        this.scale    = null;
    }

    /** Build a simple convertor for correlated residuals with the specific weights.
     * <p>
     * The scalar objective function value is computed as:
     * <pre>
     * objective = &sum;(y<sub>i</sub>)<sup>2</sup> with y = scale&times;residual
     * </pre>
     * </p>
     * <p>
     * The residuals array computed by the function and the scaling matrix must
     * have consistent sizes or a {@link ObjectiveException} will be triggered while
     * computing the scalar objective.
     * </p>
     * @param function vectorial residuals function to wrap
     * @param scale scaling matrix (
     */
    public LeastSquaresConverter (final MultiObjectiveFunction function,
                                  final RealMatrix scale) {
        this.function = function;
        this.weights  = null;
        this.scale    = scale.copy();
    }

    /** {@inheritDoc} */
    public double objective(final double[] variables) throws ObjectiveException {

        final double[] residuals = function.objective(variables);
        double sumSquares = 0;

        if (weights != null) {
            if (weights.length != residuals.length) {
                throw new ObjectiveException("dimension mismatch {0} != {1}",
                                        weights.length, residuals.length);
            }
            for (int i = 0; i < weights.length; ++i) {
                final double ai = residuals[i] * weights[i];
                sumSquares += ai * ai;
            }
        } else if (scale != null) {
            if (scale.getColumnDimension() != residuals.length) {
                throw new ObjectiveException("dimension mismatch {0} != {1}",
                                        scale.getColumnDimension(), residuals.length);
            }
            for (final double yi : scale.operate(residuals)) {
                sumSquares += yi * yi;
            }
        } else {
            for (final double ri : residuals) {
                sumSquares += ri * ri;
            }
        }

        return sumSquares;

    }

}
