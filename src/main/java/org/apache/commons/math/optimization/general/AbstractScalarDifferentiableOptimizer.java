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
 * Base class for implementing optimizers for multivariate scalar functions.
 * <p>This base class handles the boilerplate methods associated to thresholds
 * settings, iterations and evaluations counting.</p>
 * @version $Revision$ $Date$
 * @since 2.0
 */
public abstract class AbstractScalarDifferentiableOptimizer
    implements DifferentiableMultivariateRealOptimizer {

    /** Default maximal number of iterations allowed. */
    public static final int DEFAULT_MAX_ITERATIONS = 100;

    /** Maximal number of iterations allowed. */
    private int maxIterations;

    /** Number of iterations already performed. */
    private int iterations;

    /** Maximal number of evaluations allowed. */
    private int maxEvaluations;

    /** Number of evaluations already performed. */
    private int evaluations;

    /** Number of gradient evaluations. */
    private int gradientEvaluations;

    /** Convergence checker. */
    protected RealConvergenceChecker checker;

    /** Objective function. */
    private DifferentiableMultivariateRealFunction f;

    /** Objective function gradient. */
    private MultivariateVectorialFunction gradient;

    /** Type of optimization. */
    protected GoalType goalType;

    /** Current point set. */
    protected double[] point;

    /** Simple constructor with default settings.
     * <p>The convergence check is set to a {@link SimpleScalarValueChecker}
     * and the maximal number of evaluation is set to its default value.</p>
     */
    protected AbstractScalarDifferentiableOptimizer() {
        setConvergenceChecker(new SimpleScalarValueChecker());
        setMaxIterations(DEFAULT_MAX_ITERATIONS);
        setMaxEvaluations(Integer.MAX_VALUE);
    }

    /** {@inheritDoc} */
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /** {@inheritDoc} */
    public int getMaxIterations() {
        return maxIterations;
    }

    /** {@inheritDoc} */
    public int getIterations() {
        return iterations;
    }

    /** {@inheritDoc} */
    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    /** {@inheritDoc} */
    public int getEvaluations() {
        return evaluations;
    }

    /** {@inheritDoc} */
    public int getGradientEvaluations() {
        return gradientEvaluations;
    }

    /** {@inheritDoc} */
    public void setConvergenceChecker(RealConvergenceChecker checker) {
        this.checker = checker;
    }

    /** {@inheritDoc} */
    public RealConvergenceChecker getConvergenceChecker() {
        return checker;
    }

    /** Increment the iterations counter by 1.
     * @exception OptimizationException if the maximal number
     * of iterations is exceeded
     */
    protected void incrementIterationsCounter()
        throws OptimizationException {
        if (++iterations > maxIterations) {
            throw new OptimizationException(new MaxIterationsExceededException(maxIterations));
        }
    }

    /** 
     * Compute the gradient vector.
     * @param point point at which the gradient must be evaluated
     * @return gradient at the specified point
     * @exception FunctionEvaluationException if the function gradient
     */
    protected double[] computeObjectiveGradient(final double[] point)
        throws FunctionEvaluationException {
        ++gradientEvaluations;
        return gradient.value(point);
    }

    /** 
     * Compute the objective function value.
     * @param point point at which the objective function must be evaluated
     * @return objective function value at specified point
     * @exception FunctionEvaluationException if the function cannot be evaluated
     * or its dimension doesn't match problem dimension or the maximal number
     * of iterations is exceeded
     */
    protected double computeObjectiveValue(final double[] point)
        throws FunctionEvaluationException {
        if (++evaluations > maxEvaluations) {
            throw new FunctionEvaluationException(new MaxEvaluationsExceededException(maxEvaluations),
                                                  point);
        }
        return f.value(point);
    }

    /** {@inheritDoc} */
    public RealPointValuePair optimize(final DifferentiableMultivariateRealFunction f,
                                         final GoalType goalType,
                                         final double[] startPoint)
        throws FunctionEvaluationException, OptimizationException, IllegalArgumentException {

        // reset counters
        iterations          = 0;
        evaluations         = 0;
        gradientEvaluations = 0;

        // store optimization problem characteristics
        this.f        = f;
        gradient      = f.gradient();
        this.goalType = goalType;
        point         = startPoint.clone();

        return doOptimize();

    }

    /** Perform the bulk of optimization algorithm.
     * @return the point/value pair giving the optimal value for objective function
     * @exception FunctionEvaluationException if the objective function throws one during
     * the search
     * @exception OptimizationException if the algorithm failed to converge
     * @exception IllegalArgumentException if the start point dimension is wrong
     */
    abstract protected RealPointValuePair doOptimize()
        throws FunctionEvaluationException, OptimizationException, IllegalArgumentException;

}