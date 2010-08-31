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
import org.apache.commons.math.util.Incrementor;
import org.apache.commons.math.exception.MaxCountExceededException;
import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.optimization.BaseMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.VectorialPointValuePair;
import org.apache.commons.math.optimization.SimpleVectorialValueChecker;

/**
 * Base class for implementing optimizers for multivariate scalar functions.
 * This base class handles the boiler-plate methods associated to thresholds
 * settings, iterations and evaluations counting.
 *
 * @param <FUNC> the type of the objective function to be optimized
 *
 * @version $Revision$ $Date$
 * @since 3.0
 */
public abstract class BaseAbstractVectorialOptimizer<FUNC extends MultivariateVectorialFunction>
    implements BaseMultivariateVectorialOptimizer<FUNC> {
    /** Evaluations counter. */
    protected final Incrementor evaluations = new Incrementor();
    /** Convergence checker. */
    private ConvergenceChecker<VectorialPointValuePair> checker;
    /** Target value for the objective functions at optimum. */
    private double[] target;
    /** Weight for the least squares cost computation. */
    private double[] weight;
    /** Initial guess. */
    private double[] start;
    /** Objective function. */
    private MultivariateVectorialFunction function;

    /**
     * Simple constructor with default settings.
     * The convergence check is set to a {@link SimpleVectorialValueChecker} and
     * the allowed number of evaluations is set to {@link Integer#MAX_VALUE}.
     */
    protected BaseAbstractVectorialOptimizer() {
        this(new SimpleVectorialValueChecker(),  Integer.MAX_VALUE);
    }
    /**
     * @param checker Convergence checker.
     * @param maxEvaluations Maximum number of function evaluations.
     */
    protected BaseAbstractVectorialOptimizer(ConvergenceChecker<VectorialPointValuePair> checker,
                                             int maxEvaluations) {
        this.checker = checker;
        evaluations.setMaximalCount(maxEvaluations);
    }

    /** {@inheritDoc} */
    public void setMaxEvaluations(int maxEvaluations) {
        evaluations.setMaximalCount(maxEvaluations);
    }

    /** {@inheritDoc} */
    public int getMaxEvaluations() {
        return evaluations.getMaximalCount();
    }

    /** {@inheritDoc} */
    public int getEvaluations() {
        return evaluations.getCount();
    }

    /** {@inheritDoc} */
    public void setConvergenceChecker(ConvergenceChecker<VectorialPointValuePair> convergenceChecker) {
        this.checker = convergenceChecker;
    }

    /** {@inheritDoc} */
    public ConvergenceChecker<VectorialPointValuePair> getConvergenceChecker() {
        return checker;
    }

    /**
     * Compute the objective function value.
     *
     * @param point Point at which the objective function must be evaluated.
     * @return the objective function value at the specified point.
     * @throws FunctionEvaluationException if the function cannot be evaluated.
     * @throws TooManyEvaluationsException if the maximal number of evaluations is
     * exceeded.
     */
    protected double[] computeObjectiveValue(double[] point)
        throws FunctionEvaluationException {
        try {
            evaluations.incrementCount();
        } catch (MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
        return function.value(point);
    }

    /** {@inheritDoc} */
    public VectorialPointValuePair optimize(FUNC f,
                                            double[] target, double[] weight,
                                            double[] startPoint)
        throws FunctionEvaluationException {
        // Checks.
        if (f == null) {
            throw new NullArgumentException();
        }
        if (target == null) {
            throw new NullArgumentException();
        }
        if (weight == null) {
            throw new NullArgumentException();
        }
        if (startPoint == null) {
            throw new NullArgumentException();
        }
        if (target.length != weight.length) {
            throw new DimensionMismatchException(target.length, weight.length);
        }

        // Reset.
        evaluations.resetCount();

        // Store optimization problem characteristics.
        function = f;
        this.target = target.clone();
        this.weight = weight.clone();
        start = startPoint.clone();

        // Perform computation.
        return doOptimize();
    }

    /**
     * @return the initial guess.
     */
    public double[] getStartPoint() {
        return start.clone();
    }

    /**
     * Perform the bulk of the optimization algorithm.
     *
     * @return the point/value pair giving the optimal value for objective function
     * @throws FunctionEvaluationException if the objective function throws one during
     * the search
     */
    protected abstract VectorialPointValuePair doOptimize()
        throws FunctionEvaluationException;

    /**
     * @return a reference to the {@link #target array}.
     */
    protected double[] getTargetRef() {
        return target;
    }
    /**
     * @return a reference to the {@link #weight array}.
     */
    protected double[] getWeightRef() {
        return weight;
    }
}
