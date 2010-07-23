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

package org.apache.commons.math.optimization.general;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealConvergenceChecker;
import org.apache.commons.math.optimization.DifferentiableMultivariateRealOptimizer;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;

/**
 * Base class for implementing optimizers for multivariate scalar
 * differentiable functions.
 * It contains boiler-plate code for dealing with gradient evaluation.
 *
 * @version $Revision$ $Date$
 * @since 2.0
 */
public abstract class AbstractScalarDifferentiableOptimizer
    extends BaseAbstractScalarOptimizer<DifferentiableMultivariateRealFunction>
    implements DifferentiableMultivariateRealOptimizer {
    /** Number of gradient evaluations. */
    private int gradientEvaluations;
    /** Objective function gradient. */
    private MultivariateVectorialFunction gradient;

    /** Convergence checker.
     * @deprecated in 2.2 (to be removed in 3.0). Please use the accessor
     * {@link BaseAbstractScalarOptimizer#getConvergenceChecker()} instead.
     */
    protected RealConvergenceChecker checker;
    /**
     * Type of optimization.
     * @since 2.1
     * @deprecated in 2.2 (to be removed in 3.0). Please use the accessor
     * {@link BaseAbstractScalarOptimizer#getGoalType()} instead.
     */
    protected GoalType goal;
    /** Current point set.
     * @deprecated in 2.2 (to be removed in 3.0).
     */
    protected double[] point;

    /**
     * Simple constructor with default settings.
     * The convergence check is set to a {@link SimpleScalarValueChecker},
     * the allowed number of iterations and evaluations are set to their
     * default values.
     */
    protected AbstractScalarDifferentiableOptimizer() {}
    /**
     * @param convergenceChecker Convergence checker.
     * @param maxIterations Maximum number of iterations.
     * @param maxEvaluations Maximum number of evaluations.
     */
    protected AbstractScalarDifferentiableOptimizer(RealConvergenceChecker checker,
                                                    int maxIterations,
                                                    int maxEvaluations) {
        super(checker, maxIterations, maxEvaluations);
        this.checker = checker; // Do not use (deprecated).
    }

    /** {@inheritDoc} */
    public int getGradientEvaluations() {
        return gradientEvaluations;
    }

    /**
     * Compute the gradient vector.
     * @param evaluationPoint point at which the gradient must be evaluated
     * @return gradient at the specified point
     * @exception FunctionEvaluationException if the function gradient
     */
    protected double[] computeObjectiveGradient(final double[] evaluationPoint)
        throws FunctionEvaluationException {
        ++gradientEvaluations;
        return gradient.value(evaluationPoint);
    }

    /** {@inheritDoc} */
    public RealPointValuePair optimize(final DifferentiableMultivariateRealFunction f,
                                       final GoalType goalType,
                                       final double[] startPoint)
        throws FunctionEvaluationException,
               OptimizationException {
        // reset counters
        gradientEvaluations = 0;

        // store optimization problem characteristics
        gradient = f.gradient();

        goal = goalType; // Do not use (deprecated).
        point = startPoint.clone(); // Do not use (deprecated).

        return super.optimize(f, goalType, startPoint);
    }
}
