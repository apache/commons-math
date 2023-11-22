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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar;

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.analysis.UnivariateFunction;
import org.apache.commons.math4.legacy.optim.BaseMultivariateOptimizer;
import org.apache.commons.math4.legacy.optim.ConvergenceChecker;
import org.apache.commons.math4.legacy.optim.OptimizationData;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.MaxEval;
import org.apache.commons.math4.legacy.optim.univariate.BracketFinder;
import org.apache.commons.math4.legacy.optim.univariate.BrentOptimizer;
import org.apache.commons.math4.legacy.optim.univariate.SearchInterval;
import org.apache.commons.math4.legacy.optim.univariate.SimpleUnivariateValueChecker;
import org.apache.commons.math4.legacy.optim.univariate.UnivariateObjectiveFunction;
import org.apache.commons.math4.legacy.optim.univariate.UnivariateOptimizer;
import org.apache.commons.math4.legacy.optim.univariate.UnivariatePointValuePair;

/**
 * Base class for a multivariate scalar function optimizer.
 *
 * @since 3.1
 */
public abstract class MultivariateOptimizer
    extends BaseMultivariateOptimizer<PointValuePair> {
    /** Objective function. */
    private MultivariateFunction function;
    /** Type of optimization. */
    private GoalType goal;
    /** Line search relative tolerance. */
    private double lineSearchRelativeTolerance = 1e-8;
    /** Line search absolute tolerance. */
    private double lineSearchAbsoluteTolerance = 1e-8;
    /** Line serach initial bracketing range. */
    private double lineSearchInitialBracketingRange = 1d;
    /** Line search. */
    private LineSearch lineSearch;

    /**
     * @param checker Convergence checker.
     */
    protected MultivariateOptimizer(ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }

    /**
     * {@inheritDoc}
     *
     * @param optData Optimization data. In addition to those documented in
     * {@link BaseMultivariateOptimizer#parseOptimizationData(OptimizationData[])
     * BaseMultivariateOptimizer}, this method will register the following data:
     * <ul>
     *  <li>{@link ObjectiveFunction}</li>
     *  <li>{@link GoalType}</li>
     *  <li>{@link LineSearchTolerance}</li>
     * </ul>
     * @return {@inheritDoc}
     * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
     * if the maximal number of evaluations is exceeded.
     */
    @Override
    public PointValuePair optimize(OptimizationData... optData) {
        // Set up base class and perform computation.
        return super.optimize(optData);
    }

    /**
     * Scans the list of (required and optional) optimization data that
     * characterize the problem.
     *
     * @param optData Optimization data.
     * The following data will be looked for:
     * <ul>
     *  <li>{@link ObjectiveFunction}</li>
     *  <li>{@link GoalType}</li>
     *  <li>{@link LineSearchTolerance}</li>
     * </ul>
     */
    @Override
    protected void parseOptimizationData(OptimizationData... optData) {
        // Allow base class to register its own data.
        super.parseOptimizationData(optData);

        // The existing values (as set by the previous call) are reused if
        // not provided in the argument list.
        for (OptimizationData data : optData) {
            if (data instanceof GoalType) {
                goal = (GoalType) data;
                continue;
            }
            if (data instanceof ObjectiveFunction) {
                final MultivariateFunction delegate = ((ObjectiveFunction) data).getObjectiveFunction();
                function = new MultivariateFunction() {
                        @Override
                        public double value(double[] point) {
                            incrementEvaluationCount();
                            return delegate.value(point);
                        }
                    };
                continue;
            }
            if (data instanceof LineSearchTolerance) {
                final LineSearchTolerance tol = (LineSearchTolerance) data;
                lineSearchRelativeTolerance = tol.getRelativeTolerance();
                lineSearchAbsoluteTolerance = tol.getAbsoluteTolerance();
                lineSearchInitialBracketingRange = tol.getInitialBracketingRange();
                continue;
            }
        }
    }

    /**
     * Intantiate the line search implementation.
     */
    protected void createLineSearch() {
        lineSearch = new LineSearch(this,
                                    lineSearchRelativeTolerance,
                                    lineSearchAbsoluteTolerance,
                                    lineSearchInitialBracketingRange);
    }

    /**
     * Finds the number {@code alpha} that optimizes
     * {@code f(startPoint + alpha * direction)}.
     *
     * @param startPoint Starting point.
     * @param direction Search direction.
     * @return the optimum.
     * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
     * if the number of evaluations is exceeded.
     */
    protected UnivariatePointValuePair lineSearch(final double[] startPoint,
                                                  final double[] direction) {
        return lineSearch.search(startPoint, direction);
    }

    /**
     * @return the optimization type.
     */
    protected GoalType getGoalType() {
        return goal;
    }

    /**
     * @return a wrapper that delegates to the user-supplied function,
     * and counts the number of evaluations.
     */
    protected MultivariateFunction getObjectiveFunction() {
        return function;
    }

    /**
     * Computes the objective function value.
     * This method <em>must</em> be called by subclasses to enforce the
     * evaluation counter limit.
     *
     * @param params Point at which the objective function must be evaluated.
     * @return the objective function value at the specified point.
     * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
     * if the maximal number of evaluations is exceeded.
     *
     * @deprecated Use {@link #getObjectiveFunction()} instead.
     */
    @Deprecated
    public double computeObjectiveValue(double[] params) {
        return function.value(params);
    }

    /**
     * Find the minimum of the objective function along a given direction.
     *
     * @since 4.0
     */
    private static class LineSearch {
        /**
         * Value that will pass the precondition check for {@link BrentOptimizer}
         * but will not pass the convergence check, so that the custom checker
         * will always decide when to stop the line search.
         */
        private static final double REL_TOL_UNUSED = 1e-15;
        /**
         * Value that will pass the precondition check for {@link BrentOptimizer}
         * but will not pass the convergence check, so that the custom checker
         * will always decide when to stop the line search.
         */
        private static final double ABS_TOL_UNUSED = Double.MIN_VALUE;
        /**
         * Optimizer used for line search.
         */
        private final UnivariateOptimizer lineOptimizer;
        /**
         * Automatic bracketing.
         */
        private final BracketFinder bracket = new BracketFinder();
        /**
         * Extent of the initial interval used to find an interval that
         * brackets the optimum.
         */
        private final double initialBracketingRange;
        /**
         * Optimizer on behalf of which the line search must be performed.
         */
        private final MultivariateOptimizer mainOptimizer;

        /**
         * The {@code BrentOptimizer} default stopping criterion uses the
         * tolerances to check the domain (point) values, not the function
         * values.
         * The {@code relativeTolerance} and {@code absoluteTolerance}
         * arguments are thus passed to a {@link SimpleUnivariateValueChecker
         * custom checker} that will use the function values.
         *
         * @param optimizer Optimizer on behalf of which the line search
         * be performed.
         * Its {@link MultivariateOptimizer#getObjectiveFunction() objective
         * function} will be called by the {@link #search(double[],double[])
         * search} method.
         * @param relativeTolerance Search will stop when the function relative
         * difference between successive iterations is below this value.
         * @param absoluteTolerance Search will stop when the function absolute
         * difference between successive iterations is below this value.
         * @param initialBracketingRange Extent of the initial interval used to
         * find an interval that brackets the optimum.
         * If the optimized function varies a lot in the vicinity of the optimum,
         * it may be necessary to provide a value lower than the distance between
         * successive local minima.
         */
        /* package-private */ LineSearch(MultivariateOptimizer optimizer,
                                         double relativeTolerance,
                                         double absoluteTolerance,
                                         double initialBracketingRange) {
            mainOptimizer = optimizer;
            lineOptimizer = new BrentOptimizer(REL_TOL_UNUSED,
                                               ABS_TOL_UNUSED,
                                               new SimpleUnivariateValueChecker(relativeTolerance,
                                                                                absoluteTolerance));
            this.initialBracketingRange = initialBracketingRange;
        }

        /**
         * Finds the number {@code alpha} that optimizes
         * {@code f(startPoint + alpha * direction)}.
         *
         * @param startPoint Starting point.
         * @param direction Search direction.
         * @return the optimum.
         * @throws org.apache.commons.math4.legacy.exception.TooManyEvaluationsException
         * if the number of evaluations is exceeded.
         */
        /* package-private */ UnivariatePointValuePair search(final double[] startPoint,
                                                              final double[] direction) {
            final int n = startPoint.length;
            final MultivariateFunction func = mainOptimizer.getObjectiveFunction();
            final UnivariateFunction f = new UnivariateFunction() {
                    /** {@inheritDoc} */
                    @Override
                    public double value(double alpha) {
                        final double[] x = new double[n];
                        for (int i = 0; i < n; i++) {
                            x[i] = startPoint[i] + alpha * direction[i];
                        }
                        return func.value(x);
                    }
                };

            final GoalType goal = mainOptimizer.getGoalType();
            bracket.search(f, goal, 0, initialBracketingRange);
            // Passing "MAX_VALUE" as a dummy value because it is the enclosing
            // class that counts the number of evaluations (and will eventually
            // generate the exception).
            return lineOptimizer.optimize(new MaxEval(Integer.MAX_VALUE),
                                          new UnivariateObjectiveFunction(f),
                                          goal,
                                          new SearchInterval(bracket.getLo(),
                                                             bracket.getHi(),
                                                             bracket.getMid()));
        }
    }
}
