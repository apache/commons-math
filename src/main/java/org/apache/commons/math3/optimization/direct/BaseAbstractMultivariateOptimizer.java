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

package org.apache.commons.math3.optimization.direct;

import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optimization.BaseMultivariateOptimizer;
import org.apache.commons.math3.optimization.GoalType;
import org.apache.commons.math3.optimization.ConvergenceChecker;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.SimpleValueChecker;

/**
 * Base class for implementing optimizers for multivariate scalar functions.
 * This base class handles the boiler-plate methods associated to thresholds
 * settings, iterations and evaluations counting.
 *
 * @param <FUNC> Type of the objective function to be optimized.
 *
 * @version $Id$
 * @since 2.2
 */
public abstract class BaseAbstractMultivariateOptimizer<FUNC extends MultivariateFunction>
    implements BaseMultivariateOptimizer<FUNC> {
    /** Evaluations counter. */
    protected final Incrementor evaluations = new Incrementor();
    /** Convergence checker. */
    private ConvergenceChecker<PointValuePair> checker;
    /** Type of optimization. */
    private GoalType goal;
    /** Initial guess. */
    private double[] start;
    /** Objective function. */
    private MultivariateFunction function;

    /**
     * Simple constructor with default settings.
     * The convergence check is set to a {@link SimpleValueChecker}.
     * @deprecated See {@link SimpleValueChecker#SimpleValueChecker()}
     */
    @Deprecated
    protected BaseAbstractMultivariateOptimizer() {
        this(new SimpleValueChecker());
    }
    /**
     * @param checker Convergence checker.
     */
    protected BaseAbstractMultivariateOptimizer(ConvergenceChecker<PointValuePair> checker) {
        this.checker = checker;
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
    public ConvergenceChecker<PointValuePair> getConvergenceChecker() {
        return checker;
    }

    /**
     * Compute the objective function value.
     *
     * @param point Point at which the objective function must be evaluated.
     * @return the objective function value at the specified point.
     * @throws TooManyEvaluationsException if the maximal number of
     * evaluations is exceeded.
     */
    protected double computeObjectiveValue(double[] point) {
        try {
            evaluations.incrementCount();
        } catch (MaxCountExceededException e) {
            throw new TooManyEvaluationsException(e.getMax());
        }
        return function.value(point);
    }

    /** {@inheritDoc} */
    public PointValuePair optimize(int maxEval, FUNC f, GoalType goalType,
                                       double[] startPoint) {
        return optimizeInternal(maxEval, f, goalType, startPoint);
    }

    /**
     * Optimize an objective function.
     *
     * @param f Objective function.
     * @param goalType Type of optimization goal: either
     * {@link GoalType#MAXIMIZE} or {@link GoalType#MINIMIZE}.
     * @param startPoint Start point for optimization.
     * @param maxEval Maximum number of function evaluations.
     * @return the point/value pair giving the optimal value for objective
     * function.
     * @throws org.apache.commons.math3.exception.DimensionMismatchException
     * if the start point dimension is wrong.
     * @throws org.apache.commons.math3.exception.TooManyEvaluationsException
     * if the maximal number of evaluations is exceeded.
     * @throws org.apache.commons.math3.exception.NullArgumentException if
     * any argument is {@code null}.
     */
    protected PointValuePair optimizeInternal(int maxEval, MultivariateFunction f, GoalType goalType,
                                              double[] startPoint) {
        // Checks.
        if (f == null) {
            throw new NullArgumentException();
        }
        if (goalType == null) {
            throw new NullArgumentException();
        }
        if (startPoint == null) {
            throw new NullArgumentException();
        }

        // Reset.
        evaluations.setMaximalCount(maxEval);
        evaluations.resetCount();

        // Store optimization problem characteristics.
        function = f;
        goal = goalType;
        start = startPoint.clone();

        // Perform computation.
        return doOptimize();
    }

    /**
     * @return the optimization type.
     */
    public GoalType getGoalType() {
        return goal;
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
     * @return the point/value pair giving the optimal value for the
     * objective function.
     */
    protected abstract PointValuePair doOptimize();
}
