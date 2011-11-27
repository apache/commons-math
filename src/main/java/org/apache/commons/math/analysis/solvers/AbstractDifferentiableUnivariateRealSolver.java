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

package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.analysis.DifferentiableUnivariateFunction;
import org.apache.commons.math.analysis.UnivariateFunction;

/**
 * Provide a default implementation for several functions useful to generic
 * solvers.
 *
 * @version $Id$
 * @since 3.0
 */
public abstract class AbstractDifferentiableUnivariateRealSolver
    extends BaseAbstractUnivariateRealSolver<DifferentiableUnivariateFunction>
    implements DifferentiableUnivariateRealSolver {
    /** Derivative of the function to solve. */
    private UnivariateFunction functionDerivative;

    /**
     * Construct a solver with given absolute accuracy.
     *
     * @param absoluteAccuracy Maximum absolute error.
     */
    protected AbstractDifferentiableUnivariateRealSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy);
    }

    /**
     * Construct a solver with given accuracies.
     *
     * @param relativeAccuracy Maximum relative error.
     * @param absoluteAccuracy Maximum absolute error.
     * @param functionValueAccuracy Maximum function value error.
     */
    protected AbstractDifferentiableUnivariateRealSolver(final double relativeAccuracy,
                                                         final double absoluteAccuracy,
                                                         final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy);
    }

    /**
     * Compute the objective function value.
     *
     * @param point Point at which the objective function must be evaluated.
     * @return the objective function value at specified point.
     * @throws org.apache.commons.math.exception.TooManyEvaluationsException
     * if the maximal number of evaluations is exceeded.
     */
    protected double computeDerivativeObjectiveValue(double point) {
        incrementEvaluationCount();
        return functionDerivative.value(point);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setup(int maxEval, DifferentiableUnivariateFunction f,
                         double min, double max, double startValue) {
        super.setup(maxEval, f, min, max, startValue);
        functionDerivative = f.derivative();
    }
}
