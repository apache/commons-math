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

package org.apache.commons.math.optimization.direct;

import org.apache.commons.math.util.Incrementor;
import org.apache.commons.math.exception.MaxCountExceededException;
import org.apache.commons.math.exception.TooManyEvaluationsException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.optimization.BaseMultivariateRealOptimizer;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.ConvergenceChecker;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.SimpleScalarValueChecker;

/**
 * Base class for implementing optimizers for multivariate scalar functions.
 * This base class handles the boiler-plate methods associated to thresholds
 * settings, iterations and evaluations counting.
 *
 * @param <FUNC> Type of the objective function to be optimized
 *
 * @version $Id$
 * @since 2.2
 */
public abstract class BaseAbstractScalarOptimizer<FUNC extends MultivariateRealFunction>
    implements BaseMultivariateRealOptimizer<FUNC> {
    /** Evaluations counter. */
    protected final Incrementor evaluations = new Incrementor();
    /** Convergence checker. */
    private ConvergenceChecker<RealPointValuePair> checker;
    /** Type of optimization. */
    private GoalType goal;
    /** Initial guess. */
    private double[] start;
    /** Lower bounds. */
    private double[] lowerBound;
    /** Upper bounds. */
    private double[] upperBound;
    /** Objective function. */
    private MultivariateRealFunction function;

    /**
     * Simple constructor with default settings.
     * The convergence check is set to a {@link SimpleScalarValueChecker} and
     * the allowed number of evaluations is set to {@link Integer#MAX_VALUE}.
     */
    protected BaseAbstractScalarOptimizer() {
        this(new SimpleScalarValueChecker());
    }
    /**
     * @param checker Convergence checker.
     */
    protected BaseAbstractScalarOptimizer(ConvergenceChecker<RealPointValuePair> checker) {
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
    public ConvergenceChecker<RealPointValuePair> getConvergenceChecker() {
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
    public RealPointValuePair optimize(int maxEval, FUNC f, GoalType goalType,
                                       double[] startPoint) {
        return optimize(maxEval, f, goalType, startPoint, null, null);
    }

    /** {@inheritDoc} */
    public RealPointValuePair optimize(int maxEval, FUNC f, GoalType goalType,
                                       double[] startPoint,
                                       double[] lower, double[] upper) {
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
        final int dim = startPoint.length;
        if (lower != null &&
            lower.length != dim) {
            throw new DimensionMismatchException(lower.length, dim);
        }
        if (upper != null &&
            upper.length != dim) {
            throw new DimensionMismatchException(upper.length, dim);
        }

        // Reset.
        evaluations.setMaximalCount(maxEval);
        evaluations.resetCount();

        // Store optimization problem characteristics.
        function = f;
        goal = goalType;
        start = startPoint.clone();

        if (lower == null) {
            lowerBound = new double[dim];
            for (int i = 0; i < dim; i++) {
                lowerBound[i] = Double.NEGATIVE_INFINITY;
            }
        } else {
            lowerBound = lower.clone();
        }
        if (upper == null) {
            upperBound = new double[dim];
            for (int i = 0; i < dim; i++) {
                upperBound[i] = Double.POSITIVE_INFINITY;
            }
        } else {
            upperBound = upper.clone();
        }

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
     * @return the lower bounds.
     */
    public double[] getLowerBound() {
        return lowerBound.clone();
    }

    /**
     * @return the upper bounds.
     */
    public double[] getUpperBound() {
        return upperBound.clone();
    }

    /**
     * Perform the bulk of the optimization algorithm.
     *
     * @return the point/value pair giving the optimal value for the
     * objective function.
     */
    protected abstract RealPointValuePair doOptimize();
}
