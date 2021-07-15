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
package org.apache.commons.math4.legacy.optim.nonlinear.scalar.noderiv;

import java.util.Comparator;
import java.util.function.UnaryOperator;

import org.apache.commons.math4.legacy.analysis.MultivariateFunction;
import org.apache.commons.math4.legacy.exception.MathUnsupportedOperationException;
import org.apache.commons.math4.legacy.exception.util.LocalizedFormats;
import org.apache.commons.math4.legacy.optim.ConvergenceChecker;
import org.apache.commons.math4.legacy.optim.OptimizationData;
import org.apache.commons.math4.legacy.optim.PointValuePair;
import org.apache.commons.math4.legacy.optim.SimpleValueChecker;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math4.legacy.optim.nonlinear.scalar.MultivariateOptimizer;

/**
 * This class implements simplex-based direct search optimization.
 *
 * <p>
 * Direct search methods only use objective function values, they do
 * not need derivatives and don't either try to compute approximation
 * of the derivatives. According to a 1996 paper by Margaret H. Wright
 * (<a href="http://cm.bell-labs.com/cm/cs/doc/96/4-02.ps.gz">Direct
 * Search Methods: Once Scorned, Now Respectable</a>), they are used
 * when either the computation of the derivative is impossible (noisy
 * functions, unpredictable discontinuities) or difficult (complexity,
 * computation cost). In the first cases, rather than an optimum, a
 * <em>not too bad</em> point is desired. In the latter cases, an
 * optimum is desired but cannot be reasonably found. In all cases
 * direct search methods can be useful.
 *
 * <p>
 * Simplex-based direct search methods are based on comparison of
 * the objective function values at the vertices of a simplex (which is a
 * set of n+1 points in dimension n) that is updated by the algorithms
 * steps.
 *
 * <p>
 * In addition to those documented in
 * {@link MultivariateOptimizer#optimize(OptimizationData[]) MultivariateOptimizer},
 * an instance of this class will register the following data:
 * <ul>
 *  <li>{@link Simplex}</li>
 *  <li>{@link Simplex.TransformFactory} (either {@link NelderMeadTransform}
 *   or {@link MultiDirectionalTransform})</li>
 * </ul>
 *
 * <p>
 * Each call to {@code optimize} will re-use the start configuration of
 * the current simplex and move it such that its first vertex is at the
 * provided start point of the optimization.
 * If the {@code optimize} method is called to solve a different problem
 * and the number of parameters change, the simplex must be re-initialized
 * to one with the appropriate dimensions.
 *
 * <p>
 * Convergence is considered achieved when <em>all</em> the simplex points
 * have converged.
 *
 * <p>
 * This implementation does not directly support constrained optimization
 * with simple bounds.
 * The call to {@link #optimize(OptimizationData[]) optimize} will throw
 * {@link MathUnsupportedOperationException} if bounds are passed to it.
 */
public class SimplexOptimizer extends MultivariateOptimizer {
    /** Simplex update function factory. */
    private Simplex.TransformFactory updateRule;
    /** Current simplex. */
    private Simplex simplex;

    /**
     * @param checker Convergence checker.
     */
    public SimplexOptimizer(ConvergenceChecker<PointValuePair> checker) {
        super(checker);
    }

    /**
     * @param rel Relative threshold.
     * @param abs Absolute threshold.
     */
    public SimplexOptimizer(double rel,
                            double abs) {
        this(new SimpleValueChecker(rel, abs));
    }

    /** {@inheritDoc} */
    @Override
    protected PointValuePair doOptimize() {
        checkParameters();

        // Indirect call to "computeObjectiveValue" in order to update the
        // evaluations counter.
        final MultivariateFunction evalFunc = this::computeObjectiveValue;

        final boolean isMinim = getGoalType() == GoalType.MINIMIZE;
        final Comparator<PointValuePair> comparator = (o1, o2) -> {
            final double v1 = o1.getValue();
            final double v2 = o2.getValue();
            return isMinim ? Double.compare(v1, v2) : Double.compare(v2, v1);
        };

        final UnaryOperator<Simplex> update = updateRule.apply(evalFunc, comparator);

        // Initialize search.
        simplex = simplex.translate(getStartPoint()).evaluate(evalFunc, comparator);

        Simplex previous = null;
        final ConvergenceChecker<PointValuePair> checker = getConvergenceChecker();
        while (true) {
            if (previous != null) { // Skip check at first iteration.
                boolean converged = true;
                for (int i = 0; i < simplex.getSize(); i++) {
                    if (!checker.converged(getIterations(),
                                           previous.get(i),
                                           simplex.get(i))) {
                        converged = false;
                        break;
                    }
                }
                if (converged) {
                    // We have found an optimum.
                    return simplex.get(0);
                }
            }

            // We still need to search.
            previous = simplex;
            simplex = update.apply(simplex).evaluate(evalFunc, comparator);

            incrementIterationCount();
        }
    }

    /**
     * Scans the list of (required and optional) optimization data that
     * characterize the problem.
     *
     * @param optData Optimization data.
     * The following data will be looked for:
     * <ul>
     *  <li>{@link Simplex}</li>
     *  <li>{@link Simplex.TransformFactory}</li>
     * </ul>
     */
    @Override
    protected void parseOptimizationData(OptimizationData... optData) {
        // Allow base class to register its own data.
        super.parseOptimizationData(optData);

        // The existing values (as set by the previous call) are reused
        // if not provided in the argument list.
        for (OptimizationData data : optData) {
            if (data instanceof Simplex) {
                simplex = (Simplex) data;
            } else if (data instanceof Simplex.TransformFactory) {
                updateRule = (Simplex.TransformFactory) data;
            }
        }
    }

    /**
     * @throws MathUnsupportedOperationException if bounds were passed to the
     * {@link #optimize(OptimizationData[]) optimize} method.
     * @throws NullPointerException if no initial simplex or no transform rule
     * was passed to the {@link #optimize(OptimizationData[]) optimize} method.
     */
    private void checkParameters() {
        if (updateRule == null) {
            throw new NullPointerException("No update rule");
        }
        if (simplex == null) {
            throw new NullPointerException("No initial simplex");
        }
        if (getLowerBound() != null ||
            getUpperBound() != null) {
            throw new MathUnsupportedOperationException(LocalizedFormats.CONSTRAINT);
        }
    }
}
